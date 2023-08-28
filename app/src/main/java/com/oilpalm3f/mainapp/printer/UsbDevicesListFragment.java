package com.oilpalm3f.mainapp.printer;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
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
import com.android.print.sdk.usb.USBPort;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// To Display the USB Devices List

public class UsbDevicesListFragment extends DialogFragment {

    public static final String LOG_TAG = UsbDevicesListFragment.class.getName();

    private ArrayAdapter<String> deviceArrayAdapter;
    private ListView usbDevicesListView;
    private List<UsbDevice> deviceList;
    private LinearLayout parentPanel;
    private TextView noDevicesFoundTxt;
    private Button reScan;
    private PrinterInstance dataPrinterInstance;
    private UsbOperation usbOperation;
    private UsbDevice selectedUsbDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View promptView = inflater.inflate(R.layout.bluetooth_device_screen, null);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        promptView.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

        parentPanel = (LinearLayout) promptView.findViewById(R.id.parentPanel);

        usbDevicesListView = (ListView) promptView.findViewById(R.id.btDevicesList);
        deviceArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        usbDevicesListView.setAdapter(deviceArrayAdapter);

        noDevicesFoundTxt = (TextView) promptView.findViewById(R.id.rescanTxt);
        reScan = (Button) promptView.findViewById(R.id.reScan);

        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });

        usbDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUsbDevice = deviceList.get(position);
                establishBtConnection();
            }
        });
        doDiscovery();
        return promptView;
    }

    public void establishBtConnection() {
        usbOperation = new UsbOperation(getActivity(), printerHandler);
        usbOperation.open(selectedUsbDevice);
    }
    private void doDiscovery() {
        deviceArrayAdapter.clear();
        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        if (null != devices && devices.size() > 0) {
            noDevicesFoundTxt.setVisibility(View.GONE);
            usbDevicesListView.setVisibility(View.VISIBLE);
            parentPanel.setVisibility(View.VISIBLE);
            deviceList = new ArrayList<UsbDevice>();
            for (UsbDevice device : devices.values()) {
                if (USBPort.isUsbPrinter(device)) {
                    deviceArrayAdapter.add(device.getDeviceName() + "\nvid: " + device.getVendorId() + " pid: " + device.getProductId());
                    deviceList.add(device);
                }
            }
        } else {
            noDevicesFoundTxt.setVisibility(View.VISIBLE);
            usbDevicesListView.setVisibility(View.GONE);
            parentPanel.setVisibility(View.GONE);
        }
    }

    public interface onUsbDeviceSelected {
        void selectedDevice(PrinterInstance printerInstance);
    }

    public void setOnUsbDeviceSelected(onUsbDeviceSelected onUsbDeviceSelected) {
        this.onUsbDeviceSelected = onUsbDeviceSelected;
    }

    public onUsbDeviceSelected onUsbDeviceSelected;

    private Handler printerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    dataPrinterInstance = usbOperation.getPrinter();
                    onUsbDeviceSelected.selectedDevice(dataPrinterInstance);
                    break;
                case PrinterConstants.Connect.FAILED:
                    ProgressBar.hideProgressBar();
                    UiUtils.showCustomToastMessage("connect failed...", getActivity(), 1);
                    break;
                case PrinterConstants.Connect.CLOSED:
                    ProgressBar.hideProgressBar();
                    UiUtils.showCustomToastMessage("connection closed...", getActivity(), 1);
                    break;
                default:
                    break;
            }
        }
    };
}
