package com.oilpalm3f.mainapp.GraderFingerprint;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.APiInterface;
import com.oilpalm3f.mainapp.cloudhelper.RetrofitClient;
import com.oilpalm3f.mainapp.collectioncenter.CollectionCenterHomeScreen;
import com.oilpalm3f.mainapp.collectioncenter.VerifyFingerPrint;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.dbmodels.FingerprintModel;
import com.oilpalm3f.mainapp.ui.LoginScreen;
import com.oilpalm3f.mainapp.ui.SplashScreen;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxConstant;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGImpressionType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraderFingerprint extends AppCompatActivity implements Runnable, SGFingerPresentEvent {

    private ActionBar actionBar;
    private Toolbar toolbar;

    private static final String TAG = "GraderFingerprint";

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private IntentFilter filter;
    private JSGFPLib sgfplib;
    private boolean usbPermissionRequested;
    private PendingIntent mPermissionIntent;
    private boolean bSecuGenDeviceOpened;
    private int mImageWidth;
    private int mImageHeight;
    private int mImageDPI;
    private boolean[] mFakeEngineReady;
    private int[] mNumFakeThresholds;
    private int[] mDefaultFakeThreshold;
    private int mFakeDetectionLevel = 1;

    private byte[] mRegisterTemplate;
    private byte[] mRegisterTemplate2;
    private byte[] mRegisterTemplate3;
    private int[] mMaxTemplateSize;
    private boolean mAutoOnEnabled;
    private SGAutoOnEventNotifier autoOn;

    Button btn_takefingerprint1, btn_takefingerprint2, btn_takefingerprint3;
    ImageView img_takefingerprint1, img_takefingerprint2, img_takefingerprint3;

    private byte[] mRegisterImage1;
    private byte[] mRegisterImage2;
    private byte[] mRegisterImage3;

    private boolean bFingerprintRegistered;
    private static final int IMAGE_CAPTURE_TIMEOUT_MS = 10000;
    private static final int IMAGE_CAPTURE_QUALITY = 50;

    String isfrom = "";

    String fingerprintTemplateString1 = "";
    String fingerprintTemplateString2 = "";
    String fingerprintTemplateString3 = "";
    Button btn_submitfinger;

    byte[] lastRegisteredTemplate1 = null; // Variable to store the last registered template
    byte[] lastRegisteredTemplate2 = null;
    byte[] lastRegisteredTemplate3 = null;
    String GraderCode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grader_fingerprint);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Register Fingerprints");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            GraderCode = extras.getString("GraderCode");

        }

        btn_takefingerprint1 = findViewById(R.id.btn_takefingerprint1);
        btn_takefingerprint2 = findViewById(R.id.btn_takefingerprint2);
        btn_takefingerprint3 = findViewById(R.id.btn_takefingerprint3);
        img_takefingerprint1 = findViewById(R.id.img_takefingerprint1);
        img_takefingerprint2 = findViewById(R.id.img_takefingerprint2);
        img_takefingerprint3 = findViewById(R.id.img_takefingerprint3);
        btn_submitfinger =  findViewById(R.id.btn_submitfinger);

        filter = new IntentFilter(ACTION_USB_PERMISSION);
        sgfplib = new JSGFPLib(this, (UsbManager)getSystemService(Context.USB_SERVICE));
        usbPermissionRequested = false;
        bSecuGenDeviceOpened = false;
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mNumFakeThresholds = new int[1];
        mFakeEngineReady =new boolean[1];
        mDefaultFakeThreshold = new int[1];
        mMaxTemplateSize = new int[1];
        mAutoOnEnabled = true;
        autoOn = new SGAutoOnEventNotifier(sgfplib, this);


        btn_takefingerprint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoOn.start();
                UiUtils.showCustomToastMessage("Scan your Finger 1", GraderFingerprint.this, 0);
                isfrom = "btn_takefingerprint1";
//                btn_takefingerprint2.setClickable(false);
//                btn_takefingerprint3.setClickable(false);
            }
        });

        btn_takefingerprint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoOn.start();
                UiUtils.showCustomToastMessage("Scan your Finger 2", GraderFingerprint.this, 0);
                isfrom = "btn_takefingerprint2";
//                btn_takefingerprint1.setClickable(false);
//                btn_takefingerprint3.setClickable(false);
            }
        });

        btn_takefingerprint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoOn.start();
                UiUtils.showCustomToastMessage("Scan your Finger 3", GraderFingerprint.this, 0);
                isfrom = "btn_takefingerprint3";
//                btn_takefingerprint2.setClickable(false);
//                btn_takefingerprint1.setClickable(false);
            }
        });

        btn_submitfinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (Validations()) {

                  Log.d("finalfingerString1", fingerprintTemplateString1.trim());
                  Log.d("finalfingerString2", fingerprintTemplateString2.trim());
                  Log.d("finalfingerString3", fingerprintTemplateString3.trim());

                  if (!CommonUtils.isNetworkAvailable(GraderFingerprint.this)) {
                      UiUtils.showCustomToastMessage("Please check your network connection", GraderFingerprint.this, 1);
                  }else{
                  try {
                      btnSendpostRequestclicked();
                  } catch (JSONException e) {
                      throw new RuntimeException(e);
                  }
              }
              }
            }
        });
    }

    public boolean Validations(){

        if (TextUtils.isEmpty(fingerprintTemplateString1)){
            UiUtils.showCustomToastMessage("Please Take Fingerprint1", GraderFingerprint.this, 0);
            return false;
        }
        if (TextUtils.isEmpty(fingerprintTemplateString2)){
            UiUtils.showCustomToastMessage("Please Take Fingerprint2", GraderFingerprint.this, 0);
            return false;
        }
        if (TextUtils.isEmpty(fingerprintTemplateString3)){
            UiUtils.showCustomToastMessage("Please Take Fingerprint3", GraderFingerprint.this, 0);
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void SGFingerPresentCallback() {
        autoOn.stop();
        RegisterFingerPrint();
    }

    @Override
    public void run() {

    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
                        }
                        else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null");
                    }
                    else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device " + device);
                }
            }
        }
    };

    @Override
    public void onResume(){
        Log.d(TAG, "Enter onResume()");
        super.onResume();
        registerReceiver(mUsbReceiver, filter);
        long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
            else
                dlgAlert.setMessage("Fingerprint device initialization failed!");
            dlgAlert.setTitle("SecuGen Fingerprint SDK");
            dlgAlert.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int whichButton){
                            finish();
                            return;
                        }
                    }
            );
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        }
        else {
            UsbDevice usbDevice = sgfplib.GetUsbDevice();
            if (usbDevice == null){
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
                dlgAlert.setTitle("SecuGen Fingerprint SDK");
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int whichButton){
                                finish();
                                return;
                            }
                        }
                );
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
            }
            else {
                boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                if (!hasPermission) {
                    if (!usbPermissionRequested)
                    {
                        //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                        usbPermissionRequested = true;
                        sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                    }
                    else
                    {
                        //wait up to 20 seconds for the system to grant USB permission
                        hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                        int i=0;
                        while ((hasPermission == false) && (i <= 40))
                        {
                            ++i;
                            hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
                        }
                    }
                }
                if (hasPermission) {



                    if (sgfplib != null) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();

                        try {
                            error = executor.submit(() -> sgfplib.OpenDevice(0)).get(10, TimeUnit.SECONDS);

                            if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {

                                bSecuGenDeviceOpened = true;
                                SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
//
                                if (deviceInfo != null) {

                                    Future<Long> future = executor.submit(() -> sgfplib.GetDeviceInfo(deviceInfo));

                                    try {
                                        error = future.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                        // Handle the error value or success scenario
                                    } catch (TimeoutException e) {
                                        // Handle the timeout situation
                                        // You can log a message, cancel the operation, or perform other actions
                                    } catch (InterruptedException | ExecutionException e) {
                                        // Handle exceptions that might occur during the operation
                                    } finally {
                                        future.cancel(true); // Cancel the operation if it's still running
                                        executor.shutdown(); // Shut down the executor service
                                    }
                                    //   android.util.Log.e("==========>431", error + "");
                                    //   Toast.makeText(VerifyFingerPrint.this, String.valueOf(error)+"Roja", Toast.LENGTH_SHORT).show();
                                    mImageWidth = deviceInfo.imageWidth;
                                    mImageHeight = deviceInfo.imageHeight;
                                    mImageDPI = deviceInfo.imageDPI;
                                    sgfplib.SetLedOn(true);
                                    autoOn.start();
                                }
                                ExecutorService executor2 = Executors.newSingleThreadExecutor();
                                Future<Long> future2 = executor2.submit(() -> {
                                    sgfplib.FakeDetectionCheckEngineStatus(mFakeEngineReady);
                                    return 0L; // Return a value since Future requires a return type
                                });

                                try {
                                    error = future2.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                    // Handle the error value or success scenario
                                } catch (TimeoutException e) {
                                    // Handle the timeout situation
                                    // You can log a message, cancel the operation, or perform other actions
                                    error = SGFDxErrorCode.SGFDX_ERROR_TIME_OUT; // Set a timeout error code
                                } catch (InterruptedException | ExecutionException e) {
                                    // Handle exceptions that might occur during the operation
                                    error = SGFDxErrorCode.SGFDX_ERROR_FUNCTION_FAILED; // Set an appropriate error code
                                } finally {
                                    future2.cancel(true); // Cancel the operation if it's still running
                                    //  executor2.shutdown(); // Shut down the executor service
                                }

                                // error = sgfplib.FakeDetectionCheckEngineStatus(mFakeEngineReady);

                                if (mFakeEngineReady[0]) {
                                    ExecutorService executor3 = Executors.newSingleThreadExecutor();
                                    Future<Long> future3 = executor3.submit(() -> sgfplib.FakeDetectionGetNumberOfThresholds(mNumFakeThresholds));

                                    try {
                                        error = future3.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                        // Handle the error value or success scenario
                                    } catch (TimeoutException e) {
                                        // Handle the timeout situation
                                        // You can log a message, cancel the operation, or perform other actions
                                        error = SGFDxErrorCode.SGFDX_ERROR_TIME_OUT; // Set a timeout error code
                                    } catch (InterruptedException | ExecutionException e) {
                                        // Handle exceptions that might occur during the operation
                                        error = SGFDxErrorCode.SGFDX_ERROR_FUNCTION_FAILED; // Set an appropriate error code
                                    } finally {
                                        future3.cancel(true); // Cancel the operation if it's still running
                                    }

                                    if (error != SGFDxErrorCode.SGFDX_ERROR_NONE)
                                        mNumFakeThresholds[0] = 1; //0=Off, 1=TouchChip

                                    ExecutorService executor4 = Executors.newSingleThreadExecutor();
                                    Future<Long> future4 = executor4.submit(() -> sgfplib.FakeDetectionGetDefaultThreshold(mDefaultFakeThreshold));

                                    try {
                                        error = future4.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                        // Handle the error value or success scenario
                                    } catch (TimeoutException e) {
                                        // Handle the timeout situation
                                        // You can log a message, cancel the operation, or perform other actions
                                        error = SGFDxErrorCode.SGFDX_ERROR_TIME_OUT; // Set a timeout error code
                                    } catch (InterruptedException | ExecutionException e) {
                                        // Handle exceptions that might occur during the operation
                                        error = SGFDxErrorCode.SGFDX_ERROR_FUNCTION_FAILED; // Set an appropriate error code
                                    } finally {
                                        future4.cancel(true); // Cancel the operation if it's still running
                                    }

                                    //	error = sgfplib.OpenDevice(0);
                                    android.util.Log.e("==========>408", error + "");

                                    // error = sgfplib.FakeDetectionGetDefaultThreshold(mDefaultFakeThreshold);
                                    mFakeDetectionLevel = mDefaultFakeThreshold[0];

                                    //error = this.sgfplib.SetFakeDetectionLevel(mFakeDetectionLevel);
                                    //debugMessage("Ret[" + error + "] Set Fake Threshold: " + mFakeDetectionLevel + "\n");


                                    double[] thresholdValue = new double[1];
                                    ExecutorService executor5 = Executors.newSingleThreadExecutor();
                                    Future<Long> future5 = executor5.submit(() -> sgfplib.FakeDetectionGetThresholdValue(thresholdValue));

                                    try {
                                        error = future5.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                        // Handle the error value or success scenario
                                    } catch (TimeoutException e) {
                                        // Handle the timeout situation
                                        // You can log a message, cancel the operation, or perform other actions
                                    } catch (InterruptedException | ExecutionException e) {
                                        // Handle exceptions that might occur during the operation
                                    } finally {
                                        future5.cancel(true); // Cancel the operation if it's still running
                                        // executor5.shutdown(); // Shut down the executor service
                                    }


                                    // error = sgfplib.FakeDetectionGetThresholdValue(thresholdValue);
                                }
                                  else {
                                        mNumFakeThresholds[0] = 1;        //0=Off, 1=Touch Chip
                                        mDefaultFakeThreshold[0] = 1;    //Touch Chip Enabled
                                    }

                                    sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
                                    sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
                                    mRegisterTemplate = new byte[(int) mMaxTemplateSize[0]];
                                    mRegisterTemplate2 = new byte[(int) mMaxTemplateSize[0]];
                                    mRegisterTemplate3 = new byte[(int) mMaxTemplateSize[0]];


                                    if (mAutoOnEnabled) {
                                        autoOn.start();
                                    }
                            }
                            else {
                                Toast.makeText(GraderFingerprint.this, "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
                            }
                        } catch (TimeoutException e) {
                            // Handle the timeout situation for OpenDevice operation
                            // You can log a message, close the device, or perform other necessary actions
                            error = SGFDxErrorCode.SGFDX_ERROR_TIME_OUT; // Set a timeout error code
                        } catch (InterruptedException | ExecutionException e) {
                            // Handle exceptions that might occur during the operation
                            error = SGFDxErrorCode.SGFDX_ERROR_FUNCTION_FAILED; // Set an appropriate error code
                        } finally {
                            executor.shutdown();
                        }

                        //   android.util.Log.e("==========>408", error + "");
                        //   Toast.makeText(VerifyFingerPrint.this, String.valueOf(error)+"Roja", Toast.LENGTH_SHORT).show();
                        if( error == 0L){
                            Toast.makeText(GraderFingerprint.this, "Proceed", Toast.LENGTH_SHORT).show();
                        }
                        if( error == 2L) {
                            Toast.makeText(GraderFingerprint.this, "Fingerprint operation failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        //    Toast.makeText(VerifyFingerPrint.this, String.valueOf(error), Toast.LENGTH_SHORT).show();



                    }
                }
                //Thread thread = new Thread(this);
                //thread.start();
            }
        }
        Log.d(TAG, "Exit onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bSecuGenDeviceOpened)
        {
            autoOn.stop();
            sgfplib.CloseDevice();
            bSecuGenDeviceOpened = false;
        }
        unregisterReceiver(mUsbReceiver);
    }

    //    public void RegisterFingerPrint(){
//        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
//
//        if (mRegisterImage1 != null) {
//            mRegisterImage1 = null;
//        }
//
//        mRegisterImage1 = new byte[mImageWidth*mImageHeight];
//        bFingerprintRegistered = false;
//        dwTimeStart = System.currentTimeMillis();
//        long result = sgfplib.GetImageEx(mRegisterImage1, IMAGE_CAPTURE_TIMEOUT_MS,IMAGE_CAPTURE_QUALITY);
//        dwTimeEnd = System.currentTimeMillis();
//        dwTimeElapsed = dwTimeEnd-dwTimeStart;
//        dwTimeStart = System.currentTimeMillis();
//        result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
//        dwTimeEnd = System.currentTimeMillis();
//        dwTimeElapsed = dwTimeEnd-dwTimeStart;
//
//        int quality1[] = new int[1];
//        result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mRegisterImage1, quality1);
//
//        SGFingerInfo fpInfo = new SGFingerInfo();
//        fpInfo.FingerNumber = 1;
//        fpInfo.ImageQuality = quality1[0];
//        fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
//        fpInfo.ViewNumber = 1;
//
//        for (int i=0; i< mRegisterTemplate.length; ++i)
//            mRegisterTemplate[i] = 0;
//        dwTimeStart = System.currentTimeMillis();
//        result = sgfplib.CreateTemplate(fpInfo, mRegisterImage1, mRegisterTemplate);
//        dwTimeEnd = System.currentTimeMillis();
//        dwTimeElapsed = dwTimeEnd-dwTimeStart;
//
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                img_takefingerprint1.setImageBitmap(toGrayscale(mRegisterImage1));
//            }
//        });
//
//        //img_takefingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage1));
//        if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
//            bFingerprintRegistered = true;
//
//            int[] size = new int[1];
//            result = sgfplib.GetTemplateSize(mRegisterTemplate, size);
//            Log.d("mRegisterTemplate", (Arrays.toString(mRegisterTemplate)));
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                String mRegisterTemplateString = Base64.getEncoder().encodeToString(mRegisterTemplate);
//                Log.d("mRegisterTemplateString", mRegisterTemplateString);
//            }
//
//        }
//        else{
//        }
//        fpInfo = null;
//    }

    public void RegisterFingerPrint() {

        if (isfrom == "btn_takefingerprint1") {

            long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
//            if (mRegisterImage1 != null)
//                mRegisterImage1 = null;

            mRegisterImage1 = new byte[mImageWidth * mImageHeight];
            bFingerprintRegistered = false;
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.GetImageEx(mRegisterImage1, IMAGE_CAPTURE_TIMEOUT_MS, IMAGE_CAPTURE_QUALITY);
            Log.d("result", result + "");
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            if (mRegisterImage1 != null) {

                int quality1[] = new int[1];
                result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mRegisterImage1, quality1);

                SGFingerInfo fpInfo = new SGFingerInfo();
                fpInfo.FingerNumber = 1;
                fpInfo.ImageQuality = quality1[0];
                fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
                fpInfo.ViewNumber = 1;

                for (int i = 0; i < mRegisterTemplate.length; ++i)
                    mRegisterTemplate[i] = 0;
                dwTimeStart = System.currentTimeMillis();
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage1, mRegisterTemplate);
                dwTimeEnd = System.currentTimeMillis();
                dwTimeElapsed = dwTimeEnd - dwTimeStart;

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_takefingerprint1.setImageBitmap(toGrayscale(mRegisterImage1));
                    }
                });

                //img_takefingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage1));
                if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                    bFingerprintRegistered = true;
                    int[] size = new int[1];
                    result = sgfplib.GetTemplateSize(mRegisterTemplate, size);
                    Log.d("mRegisterTemplate", (Arrays.toString(mRegisterTemplate)));

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                         fingerprintTemplateString1 = Base64.getEncoder().encodeToString(mRegisterTemplate);
                        Log.d("fingerprintTemplate1", fingerprintTemplateString1);
                    }
                } else {
                }

           // mRegisterImage1 = null;
            fpInfo = null;
        }
                // Use the last registered template as needed
                // For example, if you want to convert it to a Base64 string:


    }
        if (isfrom == "btn_takefingerprint2") {

            long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
//            if (mRegisterImage2 != null)
//                mRegisterImage2 = null;

            mRegisterImage2 = new byte[mImageWidth * mImageHeight];
            bFingerprintRegistered = false;
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.GetImageEx(mRegisterImage2, IMAGE_CAPTURE_TIMEOUT_MS, IMAGE_CAPTURE_QUALITY);
            Log.d("result", result + "");
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            if (mRegisterImage2 != null) {

                int quality1[] = new int[1];
                result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mRegisterImage2, quality1);

                SGFingerInfo fpInfo = new SGFingerInfo();
                fpInfo.FingerNumber = 1;
                fpInfo.ImageQuality = quality1[0];
                fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
                fpInfo.ViewNumber = 1;

                for (int i = 0; i < mRegisterTemplate2.length; ++i)
                    mRegisterTemplate2[i] = 0;
                dwTimeStart = System.currentTimeMillis();
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage2, mRegisterTemplate2);
                dwTimeEnd = System.currentTimeMillis();
                dwTimeElapsed = dwTimeEnd - dwTimeStart;

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_takefingerprint2.setImageBitmap(toGrayscale(mRegisterImage2));
                    }
                });

                //img_takefingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage1));
                if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                    bFingerprintRegistered = true;
                    int[] size = new int[1];
                    result = sgfplib.GetTemplateSize(mRegisterTemplate2, size);
                    Log.d("mRegisterTemplate2", (Arrays.toString(mRegisterTemplate2)));

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                         fingerprintTemplateString2 = Base64.getEncoder().encodeToString(mRegisterTemplate2);
                        Log.d("fingerprintTemplate2", fingerprintTemplateString2);
                    }

                } else {
                }
                //mRegisterImage2 = null;
                fpInfo = null;
            }
        }
        if (isfrom == "btn_takefingerprint3") {

            long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
//            if (mRegisterImage3 != null)
//                mRegisterImage3 = null;

            mRegisterImage3 = new byte[mImageWidth * mImageHeight];
            bFingerprintRegistered = false;
            dwTimeStart = System.currentTimeMillis();
            long result = sgfplib.GetImageEx(mRegisterImage3, IMAGE_CAPTURE_TIMEOUT_MS, IMAGE_CAPTURE_QUALITY);
            Log.d("result", result + "");
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            if (mRegisterImage3 != null) {

                int quality1[] = new int[1];
                result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mRegisterImage3, quality1);

                SGFingerInfo fpInfo = new SGFingerInfo();
                fpInfo.FingerNumber = 1;
                fpInfo.ImageQuality = quality1[0];
                fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
                fpInfo.ViewNumber = 1;

                for (int i = 0; i < mRegisterTemplate3.length; ++i)
                    mRegisterTemplate3[i] = 0;
                dwTimeStart = System.currentTimeMillis();
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage3, mRegisterTemplate3);
                dwTimeEnd = System.currentTimeMillis();
                dwTimeElapsed = dwTimeEnd - dwTimeStart;

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_takefingerprint3.setImageBitmap(toGrayscale(mRegisterImage3));
                    }
                });

                //img_takefingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage1));
                if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                    bFingerprintRegistered = true;

                    int[] size = new int[1];
                    result = sgfplib.GetTemplateSize(mRegisterTemplate3, size);
                    Log.d("mRegisterTemplate3", (Arrays.toString(mRegisterTemplate3)));

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        fingerprintTemplateString3 = Base64.getEncoder().encodeToString(mRegisterTemplate3);
                        Log.d("fingerprintTemplate3", fingerprintTemplateString3);
                    }

                } else {
                }
                //mRegisterImage3 = null;
                fpInfo = null;
            }


        }


    }




    public Bitmap toGrayscale(byte[] mImageBuffer)
    {
        byte[] Bits = new byte[mImageBuffer.length * 4];
        for (int i = 0; i < mImageBuffer.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = mImageBuffer[i]; // Invert the source bits
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
        }

        Bitmap bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bmpGrayscale;
    }

//    private void btnSendpostRequestclicked() {
//
//        APiInterface aPiInterface = RetrofitClient.getRetrofitInstance().create(APiInterface.class);
//        //Call<User> call = aPiInterface.getUserInformation("arun", "Android developer");
//        Call<FingerprintModel> call = aPiInterface.postGraderFingerprints("GR000002",
//                fingerprintTemplateString1.trim() + "",fingerprintTemplateString2.trim()+"", fingerprintTemplateString3.trim()+"");
//
//        Log.d("request", call.request().toString());
//
//        call.enqueue(new Callback<FingerprintModel>() {
//            @Override
//            public void onResponse(Call<FingerprintModel> call, Response<FingerprintModel> response) {
//
//                Log.d("request1", call.request().toString());
//                Log.d("issucess",response.isSuccessful() + "");
//               Log.d("response", response.toString());
//                Log.d("successcode", response.body().getSuccessCode() + "");
//                Log.d("successmessage", response.body().getSuccessMessage() + "");
//                Toast.makeText(GraderFingerprint.this, "API SUCCESS", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onFailure(Call<FingerprintModel> call, Throwable t) {
//
//                Log.e(TAG, "onFailure" + t.getMessage());
//                Toast.makeText(GraderFingerprint.this, "API FAILED", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void btnSendpostRequestclicked() throws JSONException {

        String value1 = "GR000002";

        LinkedHashMap<String, String> requestData = new LinkedHashMap<>();
        requestData.put("GraderCode", GraderCode);
        requestData.put("FingerPrintData1", fingerprintTemplateString1.trim());
        requestData.put("FingerPrintData2", fingerprintTemplateString2.trim());
        requestData.put("FingerPrintData3", fingerprintTemplateString3.trim());

        String value2 = fingerprintTemplateString1.trim();
        String value3 = fingerprintTemplateString2.trim();
        String value4 = fingerprintTemplateString3.trim();

        String apiUrl = "http://182.18.157.215/3FSmartPalm_Nursery/API/api/Grader/UpdateGraderFingerPrint";
        //String apiUrl = "http://182.18.157.215/3FOilPalmNurseryTest/API/api/Grader/UpdateGraderFingerPrint";// UAT
          //String apiUrl = "http://182.18.139.166/3FOilPalm/API/api/Grader/UpdateGraderFingerPrint";

        PostRequestTask task = new PostRequestTask();
        String response = null;
        try {
            response = task.execute(apiUrl, PostRequestTask.convertToJsonString(requestData)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null) {
            JSONObject jsonResponse = null;
            jsonResponse = new JSONObject(response);

            int successcode = jsonResponse.getInt("SuccessCode");
            String successmessage = jsonResponse.getString("SuccessMessage");

            Log.d("successcode", String.valueOf(successcode));
            Log.d("successmessage", successmessage + "");

            if (successcode == 1) {
                Log.d("Fingerprints", "Submitted Successfully");
                UiUtils.showCustomToastMessage("Fingerprints Submitted Successfully", GraderFingerprint.this, 0);
                this.startActivity(new Intent(this, CollectionCenterHomeScreen.class));
            } else {
                Log.d("Fingerprints", "Submit Failed");
                UiUtils.showCustomToastMessage("Fingerprints Submit Failed", GraderFingerprint.this, 1);
            }

        } else {
            Log.d("Fingerprints",  " Submit Failed due to");
            UiUtils.showCustomToastMessage("Fingerprints Submit Failed due to Network Issue", GraderFingerprint.this, 1);
        }
    }
}