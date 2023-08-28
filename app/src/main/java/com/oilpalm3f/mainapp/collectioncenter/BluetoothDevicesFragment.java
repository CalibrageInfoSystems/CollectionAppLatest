package com.oilpalm3f.mainapp.collectioncenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.printer.BluetoothOperation;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.util.Set;

//To Display BluetoothDevices
public class BluetoothDevicesFragment extends DialogFragment {

    public static final String LOG_TAG = BluetoothDevicesFragment.class.getName();

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> btArrayAdapter;
    private TextView noDevicesFoundTxt;
    private Button reScan;
    private ListView btDevicesList;
    private String address;
    private LinearLayout parentPanel;
    private BluetoothOperation bluetoothOperation;
    private PrinterInstance dataPrinterInstance;
    private boolean isConnected;
    private static int connectionCount;

    public void setOnDeviceSelected(BluetoothDevicesFragment.onDeviceSelected onDeviceSelected) {
        this.onDeviceSelected = onDeviceSelected;
    }

    public onDeviceSelected onDeviceSelected;

    public BluetoothDevicesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View promptView = inflater.inflate(R.layout.bluetooth_device_screen, null);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        promptView.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        parentPanel = (LinearLayout) promptView.findViewById(R.id.parentPanel);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevicesList = (ListView) promptView.findViewById(R.id.btDevicesList);
        btArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        btDevicesList.setAdapter(btArrayAdapter);

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(bluetoothStateReceiver, filter1);

        noDevicesFoundTxt = (TextView) promptView.findViewById(R.id.rescanTxt);
        reScan = (Button) promptView.findViewById(R.id.reScan);

        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.disable();
                ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        connectionCount = 0;
                        bluetoothAdapter.enable();
                        startDiscovery();
                    }
                }, 2000);
            }
        });

        getActivity().registerReceiver(btDevicesFound, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));

        btDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selection = (String) (btDevicesList.getItemAtPosition(position));
                address = selection.substring(0, 17);
                Log.v(LOG_TAG, "@@@ bt device address selected "+address);
                Log.v(LOG_TAG, "@@@ bt device address selected "+address);
                CommonConstants.PrinterName =  btArrayAdapter.getItem(position);
                establishBtConnection(false);
                dismiss();
            }
        });
        connectionCount = 0;
        return promptView;
    }

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
                        + device.getName() + " ( " + getResources().getText(device.getBondState() == BluetoothDevice.BOND_BONDED ? R.string.has_paired : R.string.not_paired) +" )");
                btArrayAdapter.notifyDataSetChanged();
            } else {
                noDevicesFoundTxt.setVisibility(View.VISIBLE);
                parentPanel.setVisibility(View.GONE);
            }
        }
    };

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
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        btArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            noDevicesFoundTxt.setVisibility(View.GONE);
            parentPanel.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                btArrayAdapter.add(device.getAddress() + "\n"
                        + device.getName() + " ( " + getResources().getText(device.getBondState() == BluetoothDevice.BOND_BONDED ? R.string.has_paired : R.string.not_paired) +" )");
                btArrayAdapter.notifyDataSetChanged();
            }
        } else {
//            bluetoothAdapter.enable();
            startDiscovery();
//            bluetoothAdapter.disable();
//            ApplicationThread.nuiPost(LOG_TAG, "", new Runnable() {
//                @Override
//                public void run() {
//                    bluetoothAdapter.enable();
//                    startDiscovery();
//                }
//            }, 2000);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothStateReceiver);
        getActivity().unregisterReceiver(btDevicesFound);
    }

    public interface onDeviceSelected {
        void selectedDevice(PrinterInstance printerInstance);
        void enablingPrintButton(boolean rePrint);
    }

    public void establishBtConnection(boolean rePair) {
        bluetoothOperation = new BluetoothOperation(getActivity(), printerHandler);
        ProgressBar.showProgressBar(getActivity(), "Connecting...");
        try {
            bluetoothOperation.open(address, rePair);
        } catch (Exception e) {
            ProgressBar.hideProgressBar();
            Log.e(LOG_TAG, "failed due to connection problem "+e.getMessage());
            UiUtils.showCustomToastMessage("connect failed...please try again", getActivity(), 1);
        }

    }

    @Override
    public void onStop() {
        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        super.onStop();
    }

    @SuppressLint("HandlerLeak")
    private Handler printerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    dataPrinterInstance = bluetoothOperation.getPrinter();
                    ProgressBar.hideProgressBar();
                    onDeviceSelected.selectedDevice(dataPrinterInstance);
//                    UiUtils.showCustomToastMessage("Printer connection success...", getActivity(), 0);
                    break;
                case PrinterConstants.Connect.FAILED:
                    connectionCount++;
                    isConnected = false;
                    ProgressBar.hideProgressBar();
                    if (connectionCount <= 1) {
                        UiUtils.showCustomToastMessage("connect failed...please try again", (null != getActivity()) ? getActivity() : CommonUtils.currentActivity, 1);
                        establishBtConnection(true);
                        onDeviceSelected.enablingPrintButton(true);
                    } else {
                        onDeviceSelected.enablingPrintButton(true);
                    }
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    ProgressBar.hideProgressBar();
                    UiUtils.showCustomToastMessage("connection closed...", getActivity(), 1);
                    break;
                default:
                    break;
            }
        }
    };
}
