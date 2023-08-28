package com.oilpalm3f.mainapp.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.BluetoothDevicesFragment;


//To find the Bluetooth devices List
public class BlueToothDevicesList extends AppCompatActivity {

    public static final String LOG_TAG = BlueToothDevicesList.class.getName();

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> btArrayAdapter;
    private TextView noDevicesFoundTxt, reScan;
    private ListView btDevicesList;
    private String address;
    private LinearLayout parentPanel;

    public void setOnDeviceSelected(BluetoothDevicesFragment.onDeviceSelected onDeviceSelected) {
        this.onDeviceSelected = onDeviceSelected;
    }

    public BluetoothDevicesFragment.onDeviceSelected onDeviceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_device_screen);

        parentPanel = (LinearLayout) findViewById(R.id.parentPanel);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevicesList = (ListView) findViewById(R.id.btDevicesList);
        btArrayAdapter = new ArrayAdapter<String>(BlueToothDevicesList.this, android.R.layout.simple_list_item_1);
        btDevicesList.setAdapter(btArrayAdapter);

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter1);
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.v(LOG_TAG, "@@@ bluetooth is STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.v(LOG_TAG, "@@@ bluetooth is STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.v(LOG_TAG, "@@@ bluetooth is STATE_ON");
                        startDiscovery();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.v(LOG_TAG, "@@@ bluetooth is STATE_TURNING_ON");
                        break;
                }

            }
        }
    };

    public void startDiscovery() {
        btArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetoothAdapter.disable();
        ApplicationThread.nuiPost(LOG_TAG, "", new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.enable();
                startDiscovery();
            }
        }, 2000);

    }

    //To display the devices found
    private final BroadcastReceiver btDevicesFound = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                noDevicesFoundTxt.setVisibility(View.GONE);
                parentPanel.setVisibility(View.VISIBLE);
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getAddress() + "\n"
                        + device.getName());
                btArrayAdapter.notifyDataSetChanged();
            } else {
                noDevicesFoundTxt.setVisibility(View.VISIBLE);
                parentPanel.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(btDevicesFound);
    }

    public interface onDeviceSelected {
        void selectedDevice(String address);
    }

}
