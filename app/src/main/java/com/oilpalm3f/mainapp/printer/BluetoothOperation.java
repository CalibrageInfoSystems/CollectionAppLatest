package com.oilpalm3f.mainapp.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.util.Log;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.mainapp.common.CommonUtils;

import java.lang.reflect.Method;

//To operate the Bluetooth Printer and Pair the device
public class BluetoothOperation implements IPrinterOpertion {
	private static final String TAG = "BluetoothOpertion";
	private BluetoothAdapter adapter;
	private Context mContext;
	private boolean hasRegBoundReceiver;
	private boolean rePair;

	private BluetoothDevice mDevice;
	private String deviceAddress;
	private Handler mHandler;
	private PrinterInstance mPrinter;
	private boolean hasRegDisconnectReceiver;
	private IntentFilter filter;
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	public static String EXTRA_RE_PAIR = "re_pair";

	public BluetoothOperation(Context context, Handler handler) {
		adapter = BluetoothAdapter.getDefaultAdapter();
		mContext = context;
		mHandler = handler;
		hasRegDisconnectReceiver = false;

		filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	}

	@Override
	public void open(Intent data) {

	}

	@Override
	public void open(UsbDevice data) {

	}

	@Override
	public void open(String address, boolean pair) {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		deviceAddress = address;
		mDevice = adapter.getRemoteDevice(deviceAddress);

		if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
			Log.i(TAG, "device.getBondState() is BluetoothDevice.BOND_NONE");
			PairOrRePairDevice(false, mDevice);
		} else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
			rePair = pair;
			if (rePair) {
				PairOrRePairDevice(true, mDevice);
			} else {
				openPrinter();
			}
		}
	}

	// use device to init printer.
	private void openPrinter() {
		mPrinter = new PrinterInstance(mContext, mDevice, mHandler);
		// default is gbk...
		// mPrinter.setEncoding("gbk");
		mPrinter.openConnection();
	}

	private boolean PairOrRePairDevice(boolean re_pair, BluetoothDevice device) {
		boolean success = false;
		try {
			if (!hasRegBoundReceiver) {
				mDevice = device;
				IntentFilter boundFilter = new IntentFilter(
						BluetoothDevice.ACTION_BOND_STATE_CHANGED);
				if (mContext != null) {
					mContext.registerReceiver(boundDeviceReceiver, boundFilter);
				} else {
					mContext = CommonUtils.currentActivity;
					CommonUtils.currentActivity.registerReceiver(boundDeviceReceiver, boundFilter);
				}
				hasRegBoundReceiver = true;
			}

			if (re_pair) {
				// cancel bond
				Method removeBondMethod = BluetoothDevice.class
						.getMethod("removeBond");
				success = (Boolean) removeBondMethod.invoke(device);
				Log.i(TAG, "removeBond is success? : " + success);
			} else {
				// Input password
				// Method setPinMethod =
				// BluetoothDevice.class.getMethod("setPin");
				// setPinMethod.invoke(device, 1234);
				// create bond
				Method createBondMethod = BluetoothDevice.class
						.getMethod("createBond");
				success = (Boolean) createBondMethod.invoke(device);
				Log.i(TAG, "createBond is success? : " + success);
			}
		} catch (Exception e) {
			Log.i(TAG, "removeBond or createBond failed.");
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	// receive bound broadcast to open connect.
	private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!mDevice.equals(device)) {
					return;
				}
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					Log.i(TAG, "bounding......");
					break;
				case BluetoothDevice.BOND_BONDED:
					Log.i(TAG, "bound success");
					// if bound success, auto init BluetoothPrinter. open
					// connect.
					if (hasRegBoundReceiver) {
						mContext.unregisterReceiver(boundDeviceReceiver);
						hasRegBoundReceiver = false;
					}
					openPrinter();
					break;
				case BluetoothDevice.BOND_NONE:
					if (rePair) {
						rePair = false;
						Log.i(TAG, "removeBond success, wait create bound.");
						PairOrRePairDevice(false, device);
					} else if (hasRegBoundReceiver) {
						mContext.unregisterReceiver(boundDeviceReceiver);
						hasRegBoundReceiver = false;
						// bond failed
						mHandler.obtainMessage(Connect.FAILED).sendToTarget();
						Log.i(TAG, "bound cancel");
					}
				default:
					break;
				}
			}
		}
	};

	public void close() {
		if (mPrinter != null) {
			mPrinter.closeConnection();
			mPrinter = null;
		}
		if(hasRegDisconnectReceiver){
			mContext.unregisterReceiver(myReceiver);
			hasRegDisconnectReceiver = false;
		}
	}

	public PrinterInstance getPrinter() {
		if (mPrinter != null && mPrinter.isConnected()) {
			if(!hasRegDisconnectReceiver){
				mContext.registerReceiver(myReceiver, filter);
				hasRegDisconnectReceiver = true;
			}
		}
		return mPrinter;
	}

	// receive the state change of the bluetooth.
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Log.i(TAG, "receiver is: " + action);
			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				if (device != null && mPrinter != null && mPrinter.isConnected() && device.equals(mDevice)) {
					close();
				}
			}
		}
	};

	@Override
	public void chooseDevice() {

	}


}
