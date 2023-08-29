package com.oilpalm3f.mainapp.collectioncenter;

import static com.oilpalm3f.mainapp.common.CommonConstants.selectedPlotCode;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;

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
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcorlink.alcamsdk.SecuGenDevice;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.GraderDetails;
import com.oilpalm3f.mainapp.viewfarmers.FarmerDetailsRecyclerAdapter;
import com.oilpalm3f.mainapp.viewfarmers.FarmersListScreenForCC;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGImpressionType;

public class VerifyFingerPrint extends AppCompatActivity implements Runnable, SGFingerPresentEvent {
    private static final String LOG_TAG = VerifyFingerPrint.class.getName();

    private DataAccessHandler dataAccessHandler;
    private Toolbar toolbar;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage;
    private ImageView imgverify_fingerprint;
    TextView verifyfingerprint,notRequired,nograders;
    private CollectionCenter selectedCollectionCenter;
    Button btn_verifyfingerprint;
    private SGAutoOnEventNotifier autoOn;
    private JSGFPLib sgfplib;
    private SecuGenDevice secuGenDevice;

    private IntentFilter filter;
    private PendingIntent mPermissionIntent;
    private boolean usbPermissionRequested;
    private boolean bSecuGenDeviceOpened;
    private int mImageWidth;
    private int mImageHeight;
    private int mImageDPI;
    private boolean[] mFakeEngineReady;
    private int[] mNumFakeThresholds;
    private int[] mDefaultFakeThreshold;
    private int mFakeDetectionLevel = 1;
    private int[] mMaxTemplateSize;
    private byte[] mVerifyTemplate;
    private byte[] mVerifyImage;
    private boolean mAutoOnEnabled = false;
    ArrayList<GraderDetails> graderDetails = new ArrayList<>();

    public String matchedGraderName = "";
    public String matchedGraderCode = "";

    boolean hasNullString = false;
    TextView name_of_grader;
    LinearLayout gradernamelinear;
    private static final int IMAGE_CAPTURE_TIMEOUT_MS = 10000;
    private static final int IMAGE_CAPTURE_QUALITY = 50;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"; //Added by Arun dated 21st June

    private String IsFingerprintsAvailable = "";
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
                            /*debugMessage("USB BroadcastReceiver VID : " + device.getVendorId() + "\n");
                            debugMessage("USB BroadcastReceiver PID: " + device.getProductId() + "\n");*/
                        }
                        else
                            android.util.Log.e("TAG", "mUsbReceiver.onReceive() Device is null");
                    }
                    else
                        android.util.Log.e("TAG", "mUsbReceiver.onReceive() permission denied for device " + device);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_finger_print);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.verify));
        setSupportActionBar(toolbar);

        initUI();
        setviews();

        CommonUtils.currentActivity = this;

    }

    private void setviews() {
        dataAccessHandler = new DataAccessHandler(this);
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);

        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
        graderDetails = dataAccessHandler.getGraderdetails(Queries.getInstance().getGraderDetails(selectedCollectionCenter.getCode()));

        Log.d("graderDetails", graderDetails.size() +"");
        Log.d("WeighbridgeCC.IsFingerPrintReq", CommonConstants.IsFingerPrintReq + "");
        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
            verifyfingerprint.setVisibility(View.VISIBLE);
            imgverify_fingerprint.setVisibility(View.VISIBLE);

            btn_verifyfingerprint.setVisibility(View.VISIBLE);
            notRequired.setVisibility(View.GONE);
            nograders.setVisibility(View.GONE);

        }
        else if(CommonConstants.IsFingerPrintReq == false){
            notRequired.setVisibility(View.VISIBLE);
            nograders.setVisibility(View.GONE);
            verifyfingerprint.setVisibility(View.GONE);
            imgverify_fingerprint.setVisibility(View.GONE);

            btn_verifyfingerprint.setVisibility(View.GONE);

        }
        else{
            notRequired.setVisibility(View.GONE);
            nograders.setVisibility(View.VISIBLE);
            verifyfingerprint.setVisibility(View.GONE);
            imgverify_fingerprint.setVisibility(View.GONE);

            btn_verifyfingerprint.setVisibility(View.GONE);
        }

        imgverify_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // autoOn.start();
                Log.d("img_verifyfingerprint", "Clicked");
            }
        });//Added by Arun dated 21st June

        btn_verifyfingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("btn_takefingerprint", "Clicked");

                //sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));
                long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
//        UsbDevice usbDevice = sgfplib.GetUsbDevice();
//        long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);

                if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){

                    registerReceiver(mUsbReceiver, filter);
                    error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
                    if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(VerifyFingerPrint.this);
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
                        if (usbDevice == null) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(VerifyFingerPrint.this);
                            dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
                            dlgAlert.setTitle("SecuGen Fingerprint SDK");
                            dlgAlert.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                          finish();
                                            return;
                                        }
                                    }
                            );
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();
                        } else {
                            boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                            if (!hasPermission) {
                                if (!usbPermissionRequested) {
                                    //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                                    usbPermissionRequested = true;
                                    sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                                } else {
                                    //wait up to 20 seconds for the system to grant USB permission
                                    hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                                    int i = 0;
                                    while ((hasPermission == false) && (i <= 40)) {
                                        ++i;
                                        hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                                        try {
                                            Thread.sleep(500);
                                            sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
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
                                    Future<Long> future = executor.submit(() -> sgfplib.OpenDevice(0));

                                    try {
                                        error = future.get(10, TimeUnit.SECONDS); // Set the timeout duration
                                        // Handle the error value or success scenario
                                    } catch (TimeoutException e) {
                                        // Handle the timeout situation
                                        // You can close the device or perform other necessary actions
                                    } catch (InterruptedException | ExecutionException e) {
                                        // Handle exceptions that might occur during the operation
                                    } finally {
                                        future.cancel(true); // Cancel the operation if it's still running
                                        executor.shutdown(); // Shut down the executor service
                                    }
                                    //	error = sgfplib.OpenDevice(0);
                                    android.util.Log.e("==========>408", error + "");


                                    Toast.makeText(VerifyFingerPrint.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
                                    if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                                        bSecuGenDeviceOpened = true;
                                        SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
//                                        error = sgfplib.GetDeviceInfo(deviceInfo);
//                                        mImageWidth = deviceInfo.imageWidth;
//                                        mImageHeight = deviceInfo.imageHeight;
//                                        mImageDPI = deviceInfo.imageDPI;
//                                        sgfplib.SetLedOn(true);
//                                        autoOn.start();

                                        if(deviceInfo!=null) {
                                            //	error = sgfplib.GetDeviceInfo(deviceInfo);
                                            ExecutorService executorr = Executors.newSingleThreadExecutor();
                                            Future<Long> futurer = executorr.submit(() -> sgfplib.GetDeviceInfo(deviceInfo));

                                            try {
                                                error = futurer.get(10, TimeUnit.SECONDS); // Set the timeout duration
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
                                            android.util.Log.e("==========>431",error+"");
                                            mImageWidth = deviceInfo.imageWidth;
                                            mImageHeight = deviceInfo.imageHeight;
                                            mImageDPI = deviceInfo.imageDPI;
                                            sgfplib.SetLedOn(true);
                                            autoOn.start();
                                        }

                                        error = sgfplib.FakeDetectionCheckEngineStatus(mFakeEngineReady);
                                        if (mFakeEngineReady[0]) {
                                            error = sgfplib.FakeDetectionGetNumberOfThresholds(mNumFakeThresholds);
                                            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE)
                                                mNumFakeThresholds[0] = 1; //0=Off, 1=TouchChip

                                            error = sgfplib.FakeDetectionGetDefaultThreshold(mDefaultFakeThreshold);
                                            mFakeDetectionLevel = mDefaultFakeThreshold[0];

                                            //error = this.sgfplib.SetFakeDetectionLevel(mFakeDetectionLevel);
                                            //debugMessage("Ret[" + error + "] Set Fake Threshold: " + mFakeDetectionLevel + "\n");


                                            double[] thresholdValue = new double[1];
                                            error = sgfplib.FakeDetectionGetThresholdValue(thresholdValue);
                                        } else {
                                            mNumFakeThresholds[0] = 1;        //0=Off, 1=Touch Chip
                                            mDefaultFakeThreshold[0] = 1;    //Touch Chip Enabled
                                        }

                                        sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
                                        sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
                                        mVerifyTemplate = new byte[(int) mMaxTemplateSize[0]];
                                        boolean smartCaptureEnabled = true;
                                        if (smartCaptureEnabled)
                                            sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 1);
                                        else
                                            sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 0);
                                        if (mAutoOnEnabled) {
                                            //sgfplib.SetLedOn(true);
                                            autoOn.start();
                                        }
                                    } else {
                                        Toast.makeText(VerifyFingerPrint.this, "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(VerifyFingerPrint.this, "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();

                                  //  debugMessage("sgfplib is null. Unable to open SecuGen Device\n");
                                }

                            }
                        }
                    }

                }
                //Thread thread = new Thread(this);
                //thread.start();
                //android.util.Log.d("TAG", "Exit onResume()");

            }
        });
    }

    private void initUI() {

        collectionCenterName = (TextView) findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) findViewById(R.id.collection_center_village);
        verifyfingerprint = (TextView) findViewById(R.id.verifyfingerprint);
        notRequired = (TextView)findViewById(R.id.notRequired);
        nograders= (TextView)findViewById(R.id.nograders);
        name_of_grader = (TextView) findViewById(R.id.grader_name) ;
        imgverify_fingerprint  = (ImageView) findViewById(R.id.imgverify_fingerprint);
        btn_verifyfingerprint = (Button) findViewById(R.id.btn_verifyfingerprint);
        gradernamelinear = (LinearLayout)findViewById(R.id.gradernamelinear);
        sgfplib = new JSGFPLib(this, (UsbManager)this.getSystemService(Context.USB_SERVICE));

        autoOn = new SGAutoOnEventNotifier(sgfplib, this);
        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        mAutoOnEnabled = false;
        usbPermissionRequested = false;
        bSecuGenDeviceOpened = false;
        mNumFakeThresholds = new int[1];
        mDefaultFakeThreshold = new int[1];
        mFakeEngineReady = new boolean[1];
        mMaxTemplateSize = new int[1];

    }

    @Override
    public void SGFingerPresentCallback() {
        autoOn.stop();
        //sgfplib.SetLedOn(true);
        Log.d("Fingerprint", "taken");
         Verify_FingerPrint();
    }

    @SuppressLint("SuspiciousIndentation")
    public void Verify_FingerPrint() {
        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
        sgfplib.SetLedOn(false);
        if (mVerifyImage != null)
            mVerifyImage = null;
        mVerifyImage = new byte[mImageWidth*mImageHeight];
        dwTimeStart = System.currentTimeMillis();
        long result = sgfplib.GetImageEx(mVerifyImage, IMAGE_CAPTURE_TIMEOUT_MS,IMAGE_CAPTURE_QUALITY);


        dwTimeEnd = System.currentTimeMillis();
        dwTimeElapsed = dwTimeEnd-dwTimeStart;
        //mImageViewFingerprint.setImageBitmap(this.toGrayscale(mVerifyImage));

   runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgverify_fingerprint.setImageBitmap(toGrayscale(mVerifyImage));
            }
        });

        dwTimeStart = System.currentTimeMillis();
        result = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        dwTimeEnd = System.currentTimeMillis();
        dwTimeElapsed = dwTimeEnd-dwTimeStart;

        int quality[] = new int[1];
        result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mVerifyImage, quality);

        SGFingerInfo fpInfo = new SGFingerInfo();
        fpInfo.FingerNumber = 1;
        fpInfo.ImageQuality = quality[0];
        fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fpInfo.ViewNumber = 1;


        for (int i=0; i< mVerifyTemplate.length; ++i)
            mVerifyTemplate[i] = 0;
        dwTimeStart = System.currentTimeMillis();
        result = sgfplib.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate);
        dwTimeEnd = System.currentTimeMillis();
        dwTimeElapsed = dwTimeEnd-dwTimeStart;

        if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {

            int[] size = new int[1];
            result = sgfplib.GetTemplateSize(mVerifyTemplate, size);

            ArrayList<byte[]> registeredTemplates = new ArrayList<>();

            ArrayList<String> fingerprintdata = new ArrayList<>();

            for (int i = 0; i < graderDetails.size(); i++){
                fingerprintdata.add(graderDetails.get(i).getFingerPrintData1());
            }

            Log.d("fingerprintdatasize", fingerprintdata.size() +"");

            //fingerprintdata.remove(0);

//            for (String fingerprinttemplates : fingerprintdata) {
//                byte[] fingerprintBytes = new byte[0];
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    fingerprintBytes = Base64.getDecoder().decode(fingerprinttemplates.trim());
//                }
//                registeredTemplates.add(fingerprintBytes);
//            }
//
//            Log.d("registeredTemplatessize", registeredTemplates.size() +"");
//
//            int matchedCount = 0; // Counter for matched fingerprints
//            boolean fingerprintMatched = false;
//            String matchedGraderName = "";
//            String matchedGraderCode = "";
//
//            for (byte[] registeredTemplate : registeredTemplates) {
//                boolean[] matched = new boolean[1];
//                dwTimeStart = System.currentTimeMillis();
//                result = sgfplib.MatchTemplate(registeredTemplate, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
//                dwTimeEnd = System.currentTimeMillis();
//                dwTimeElapsed = dwTimeEnd - dwTimeStart;
//
//                if (matched[0]) {
//                    // Fingerprint matched with a registered template
//                    fingerprintMatched = true;
//                    int matchedGraderIndex = matchedCount;
//                    GraderDetails matchedGrader = graderDetails.get(matchedGraderIndex);
//                    matchedGraderName = matchedGrader.getName();
//                    matchedGraderCode = matchedGrader.getCode();
//
//                }
//            }

//            boolean fingerprintMatched = false;
//
//            for (GraderDetails matchedGrader : graderDetails) {
//                byte[] fingerprintBytes = new byte[0];
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    fingerprintBytes = Base64.getDecoder().decode(matchedGrader.getFingerPrintData1().trim());
//                }
//                registeredTemplates.add(fingerprintBytes);
//
//                boolean[] matched = new boolean[1];
//                dwTimeStart = System.currentTimeMillis();
//                result = sgfplib.MatchTemplate(fingerprintBytes, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
//                dwTimeEnd = System.currentTimeMillis();
//                dwTimeElapsed = dwTimeEnd - dwTimeStart;
//
//                if (matched[0]) {
//                    // Fingerprint matched with a registered template
//                    fingerprintMatched = true;
//                    matchedGraderName = matchedGrader.getName();
//                    matchedGraderCode = matchedGrader.getCode();
//                    break; // Exit the loop since a match is found
//                }
//            }
//
//            if (fingerprintMatched == true) {
//                android.util.Log.d("Fingerprints matched!", result + "");
////                Log.d("matchedGraderName", matchedGraderName);
////                Log.d("matchedGraderCode", matchedGraderCode);
//                //Toast.makeText(getContext(), "Matched", Toast.LENGTH_SHORT).show();
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        name_of_grader.setText(matchedGraderName);
//                        Toast.makeText(getContext(), "Matched", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }else{
//                android.util.Log.d("Fingerprint not matched", result + "");
//                autoOn.start();
//                //Toast.makeText(getContext(), "Not Matched", Toast.LENGTH_SHORT).show();
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getContext(), "Not Matched", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }


            boolean fingerprintMatched = false;
            List<byte[]> fingerprintDataList = new ArrayList<>();

            for (GraderDetails matchedGrader : graderDetails) {
                //List<byte[]> fingerprintDataList = new ArrayList<>();

                // Decode and add the first fingerprint data to the list
                byte[] fingerprintData = new byte[0];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fingerprintData = Base64.getDecoder().decode(matchedGrader.getFingerPrintData1().trim());
                }
                fingerprintDataList.add(fingerprintData);

                // Add the second fingerprint data to the list if available
                String fingerprintData2 = matchedGrader.getFingerPrintData2();
                if (fingerprintData2 != null && !fingerprintData2.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        fingerprintData = Base64.getDecoder().decode(fingerprintData2.trim());
                    }
                    fingerprintDataList.add(fingerprintData);
                }

                // Add the third fingerprint data to the list if available
                String fingerprintData3 = matchedGrader.getFingerPrintData3();
                if (fingerprintData3 != null && !fingerprintData3.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        fingerprintData = Base64.getDecoder().decode(fingerprintData3.trim());
                    }
                    fingerprintDataList.add(fingerprintData);
                }

                // Iterate over all fingerprint data in the list
                for (byte[] fingerprintBytes : fingerprintDataList) {
                    boolean[] matched = new boolean[1];
                    dwTimeStart = System.currentTimeMillis();
                    result = sgfplib.MatchTemplate(fingerprintBytes, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
                    dwTimeEnd = System.currentTimeMillis();
                    dwTimeElapsed = dwTimeEnd - dwTimeStart;

                    if (matched[0]) {
                        // Fingerprint matched with a registered template
                        fingerprintMatched = true;
                        matchedGraderName = matchedGrader.getName();
                        matchedGraderCode = matchedGrader.getCode();
                   //     name_of_grader.setText(matchedGraderName);
                        break; // Exit the loop since a match is found
                    }

                }

                if (fingerprintMatched) {
                    break; // Exit the outer loop since a match is found
                }
            }

            if (fingerprintMatched) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    name_of_grader.setText(matchedGraderName);
                        gradernamelinear.setVisibility(View.VISIBLE);
                        btn_verifyfingerprint.setFocusable(false);
                        btn_verifyfingerprint.setClickable(false);
                        Toast.makeText(VerifyFingerPrint.this, "Matched", Toast.LENGTH_SHORT).show();
                        Toast.makeText(VerifyFingerPrint.this, matchedGraderName, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!fingerprintDataList.isEmpty()) {
                   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            autoOn.start();
                            gradernamelinear.setVisibility(View.GONE);
                            Toast.makeText(VerifyFingerPrint.this, "Not Matched", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

//            if (fingerprintMatched) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        name_of_grader.setText(matchedGraderName);
//                        Toast.makeText(getContext(), "Matched", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//            else{
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        autoOn.start();
//                        Toast.makeText(getContext(), "Not Matched", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }

        }

        mVerifyImage = null;
        fpInfo = null;
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


    @Override
    public void run() {

    }

    @Override
    public void onPause() {
        super.onPause();
        android.util.Log.d("TAG", "Enter onPause()");

        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0) {

            if (bSecuGenDeviceOpened) {
                autoOn.stop();
                sgfplib.CloseDevice();
                //sgfplib.Close();
                bSecuGenDeviceOpened = false;
            }
      unregisterReceiver(mUsbReceiver);
            mVerifyImage = null;
            mVerifyTemplate = null;

            android.util.Log.d("TAG", "Exit onPause()");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Play Store Log.d(TAG, "Enter onDestroy()");
        sgfplib.CloseDevice();
        mVerifyImage = null;
        mVerifyTemplate = null;
        sgfplib.Close();

        //Play Store Log.d(TAG, "Exit onDestroy()");
    }

}