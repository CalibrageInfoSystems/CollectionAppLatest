package com.oilpalm3f.mainapp.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Palm3FoilDatabase;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.UiUtils;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;


//Splash Screen
public class SplashScreen extends AppCompatActivity{

    public static final String LOG_TAG = SplashScreen.class.getName();

    public static Palm3FoilDatabase palm3FoilDatabase;

    private IntentFilter filter;
    private PendingIntent mPermissionIntent;
    private JSGFPLib sgfplib;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"; //Added by Arun dated 21st June

    private String[] PERMISSIONS_REQUIRED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
    };

    //Creating DB and Master Sync
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        sgfplib = new JSGFPLib(this, (UsbManager) this.getSystemService(Context.USB_SERVICE));


        if (!CommonUtils.isNetworkAvailable(this)) {
            UiUtils.showCustomToastMessage("Please check your network connection", SplashScreen.this, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !CommonUtils.areAllPermissionsAllowed(this, PERMISSIONS_REQUIRED)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, CommonUtils.PERMISSION_CODE);
        } else {
            try {
                palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                palm3FoilDatabase.createDataBase();
                dbUpgradeCall();
            } catch (Exception e) {
                e.getMessage();
            }
            startMasterSync();
        }

    }

    //Request Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CommonUtils.PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOG_TAG, "permission granted");
                    try {
                        palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
                        palm3FoilDatabase.createDataBase();
                        dbUpgradeCall();
                    } catch (Exception e) {
                       Log.e(LOG_TAG, "@@@ Error while getting master data "+e.getMessage());
                    }
                    dbUpgradeCall();
                    startMasterSync();
                }
                break;
        }
    }

    //Perform Master Sync
    public void startMasterSync() {
        if (CommonUtils.isNetworkAvailable(this) && !PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS)) {
            DataSyncHelper.performMasterSync(this, PrefUtil.getBool(this, CommonConstants.IS_MASTER_SYNC_SUCCESS), new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    ProgressBar.hideProgressBar();
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"startMasterSync",CommonConstants.TAB_ID,"result.toString()",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        PrefUtil.putBool(SplashScreen.this, CommonConstants.IS_MASTER_SYNC_SUCCESS, true);
                        startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                        finish();
                    } else {
                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"startMasterSync",CommonConstants.TAB_ID,"result.toString()",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
                                startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                                finish();
                            }
                        });
                    }
                }
            });
        } else {
            startActivity(new Intent(SplashScreen.this, LoginScreen.class));
            finish();
        }
    }

//    public void startMasterSync(){
//
//        if (CommonUtils.isNetworkAvailable(this)) {
//            DataSyncHelper.performMasterSync(this, false, new ApplicationThread.OnComplete() {
//                @Override
//                public void execute(boolean success, Object result, String msg) {
//                    ProgressBar.hideProgressBar();
//                    if (success) {
//                        if (!msg.equalsIgnoreCase("Sync is up-to-date")) {
//                            Toast.makeText(SplashScreen.this, "Data synced successfully", Toast.LENGTH_SHORT).show();
//                            PrefUtil.putBool(SplashScreen.this, CommonConstants.IS_MASTER_SYNC_SUCCESS, true);
//                            startActivity(new Intent(SplashScreen.this, LoginScreen.class));
//                            finish();
//                        } else {
//                            ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
//                                @Override
//                                public void run() {
//                                    UiUtils.showCustomToastMessage("Data syncing failed", SplashScreen.this, 1);
//                                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
//                                    finish();
//
//                                   // Toast.makeText(SplashScreen.this, "You have updated data", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    } else {
//                        Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
//                        ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(SplashScreen.this, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//                    }
//                }
//            });
//        } else {
//            UiUtils.showCustomToastMessage("Please check network connection", SplashScreen.this, 1);
//        }
//
//
//    }


    //Db Upgrade Method
    public void dbUpgradeCall() {
        DataAccessHandler dataAccessHandler = new DataAccessHandler(SplashScreen.this, false);
        String count = dataAccessHandler.getCountValue(Queries.getInstance().UpgradeCount());
        if (TextUtils.isEmpty(count) || Integer.parseInt(count) == 0) {
            PrefUtil.putBool(SplashScreen.this, CommonConstants.IS_FRESH_INSTALL, true);
        }
    }

//    public static final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
//            if (ACTION_USB_PERMISSION.equals(action)) {
//                synchronized (this) {
//                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if(device != null){
//                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
//                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
//                            /*debugMessage("USB BroadcastReceiver VID : " + device.getVendorId() + "\n");
//                            debugMessage("USB BroadcastReceiver PID: " + device.getProductId() + "\n");*/
//                        }
//                        else
//                            android.util.Log.e("TAG", "mUsbReceiver.onReceive() Device is null");
//                    }
//                    else
//                        android.util.Log.e("TAG", "mUsbReceiver.onReceive() permission denied for device " + device);
//                }
//            }
//        }
//    };

//    public void onResume() {
//        super.onResume();
//
//        registerReceiver(SplashScreen.mUsbReceiver, filter);
//        long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
//        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
//            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
//            if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
//                dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
//            else
//                dlgAlert.setMessage("Fingerprint device initialization failed!");
//            dlgAlert.setTitle("SecuGen Fingerprint SDK");
//            dlgAlert.setPositiveButton("OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            finish();
//                            return;
//                        }
//                    }
//            );
//            dlgAlert.setCancelable(false);
//            dlgAlert.create().show();
//        } else {
//            UsbDevice usbDevice = sgfplib.GetUsbDevice();
//            if (usbDevice == null) {
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
//                dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
//                dlgAlert.setTitle("SecuGen Fingerprint SDK");
//                dlgAlert.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                finish();
//                                return;
//                            }
//                        }
//                );
//                dlgAlert.setCancelable(false);
//                dlgAlert.create().show();
//            }
//        }
//    }
}
