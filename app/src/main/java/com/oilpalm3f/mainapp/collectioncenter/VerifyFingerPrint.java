package com.oilpalm3f.mainapp.collectioncenter;

import static com.oilpalm3f.mainapp.common.CommonConstants.selectedPlotCode;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alcorlink.alcamsdk.SecuGenDevice;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.GraderDetails;
import com.oilpalm3f.mainapp.utils.UiUtils;
import com.oilpalm3f.mainapp.viewfarmers.FarmerDetailsRecyclerAdapter;
import com.oilpalm3f.mainapp.viewfarmers.FarmersListScreenForCC;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    TextView verifyfingerprint, notRequired, nograders;
    private CollectionCenter selectedCollectionCenter;
    Button btn_verifyfingerprint,btn_Submitverifyfingerprint;
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
    String date;
    Spinner spinnergradername;
    public String matchedGraderName = "";
    public String matchedGraderCode = "";

    boolean hasNullString = false;
    private int[] grayBuffer;
    TextView name_of_grader;
    LinearLayout gradernamelinear,gradernamedroplinear;
    private static final int IMAGE_CAPTURE_TIMEOUT_MS = 10000;
    private static final int IMAGE_CAPTURE_QUALITY = 50;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"; //Added by Arun dated 21st June
    private String selectedGrader = "";
    private String IsFingerprintsAvailable = "";
    private Bitmap grayBitmap;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
                            /*debugMessage("USB BroadcastReceiver VID : " + device.getVendorId() + "\n");
                            debugMessage("USB BroadcastReceiver PID: " + device.getProductId() + "\n");*/
                        } else
                            android.util.Log.e("TAG", "mUsbReceiver.onReceive() Device is null");
                    } else
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

        Log.d("graderDetails", graderDetails.size() + "");
         if (graderDetails.size() != 0) {
             String[] grader_name = new String[graderDetails.size()];
             if (graderDetails.size() == 1) {
                 name_of_grader.setText(graderDetails.get(0).getName());
                 selectedGrader = graderDetails.get(0).getName();
                 gradernamelinear.setVisibility(View.VISIBLE);
             } else {
                 gradernamedroplinear.setVisibility(View.VISIBLE);


                 for (int i = 0; i < graderDetails.size(); i++) {
                     grader_name[i] = graderDetails.get(i).getName();
                 }

                 String[] filteredData = new String[1];
                 filteredData[0] = "-- Select Grader Name --";
                 List list = new ArrayList(Arrays.asList(filteredData));
                 list.addAll(Arrays.asList(grader_name));
                 Object[] c = list.toArray();
                 String[] finalgradername = Arrays.copyOf(c, c.length, String[].class);


                 ArrayAdapter<String> collectionSpinnerArrayAdapter = new ArrayAdapter<>(VerifyFingerPrint.this, android.R.layout.simple_spinner_item, finalgradername);
                 collectionSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                 spinnergradername.setAdapter(collectionSpinnerArrayAdapter);

                 spinnergradername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                     @Override
                     public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                         // Get the selected Grader name
                         selectedGrader = finalgradername[position];
                         Log.e("=========>",selectedGrader+"");
                         imgverify_fingerprint.setImageResource(R.drawable.fingerprintdefaulttwo);
                         btn_verifyfingerprint.setFocusable(true);
                         btn_verifyfingerprint.setClickable(true);
                         btn_Submitverifyfingerprint.setVisibility(View.GONE);
                     }

                     @Override
                     public void onNothingSelected(AdapterView<?> parentView) {
                         // Do nothing if nothing is selected
                     }
                 });

             }
         }
         else{


             nograders.setVisibility(View.VISIBLE);
         }


        Log.d("WeighbridgeCC.IsFingerPrintReq", CommonConstants.IsFingerPrintReq + "");
        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0) {
            verifyfingerprint.setVisibility(View.VISIBLE);
            imgverify_fingerprint.setVisibility(View.VISIBLE);

            btn_verifyfingerprint.setVisibility(View.VISIBLE);
            notRequired.setVisibility(View.GONE);
            nograders.setVisibility(View.GONE);


        } else if (CommonConstants.IsFingerPrintReq == false) {
            notRequired.setVisibility(View.VISIBLE);
            nograders.setVisibility(View.GONE);
            verifyfingerprint.setVisibility(View.GONE);
            imgverify_fingerprint.setVisibility(View.GONE);

            btn_verifyfingerprint.setVisibility(View.GONE);

        } else {
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
              //  String selectedGrader = spinnergradername.getSelectedItem().toString();

                // Check if a valid Grader is selected (replace "Select Grader" with your default selection)
                if (selectedGrader.equals("-- Select Grader Name --")) {
                    // Show an error message or take appropriate action
                    Toast.makeText(VerifyFingerPrint.this, "Please select a valid Grader", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Proceed with your action using the selectedGrader
                    // Your code here...
                }
                //sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));
                long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);

                if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0) {

                    registerReceiver(mUsbReceiver, filter);
                    error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
                    if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(VerifyFingerPrint.this);
                        if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                            dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
                        else
                            dlgAlert.setMessage("Fingerprint device initialization failed!");
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

                                    try {
                                        error = executor.submit(() -> sgfplib.OpenDevice(0)).get(10, TimeUnit.SECONDS);
                                        btn_verifyfingerprint.setFocusable(false);
                                        btn_verifyfingerprint.setClickable(false);
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
                                            }
                                        else {
                                            Toast.makeText(VerifyFingerPrint.this, "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(VerifyFingerPrint.this, "Proceed", Toast.LENGTH_SHORT).show();
                                    }
                                    if( error == 2L) {
                                        Toast.makeText(VerifyFingerPrint.this, "Fingerprint operation failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                //    Toast.makeText(VerifyFingerPrint.this, String.valueOf(error), Toast.LENGTH_SHORT).show();



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
        btn_Submitverifyfingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGraderAttendance();
            }
        });//Added by Arun dated 21st

    }

    private void saveGraderAttendance() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        date = objdateformat.format(c.getTime());
        Log.e("Date===",date);
        try {

            LinkedHashMap dataMap = new LinkedHashMap();
            final List<LinkedHashMap> dataList = new ArrayList<>();
            dataMap.put("GraderCode", matchedGraderCode);
            dataMap.put("ValidDate",date);
            dataMap.put("CreatedByUserId", CommonConstants.USER_ID);
            dataMap.put("CreatedDate", date);
            dataMap.put("CCCode", selectedCollectionCenter.getCode());
            dataMap.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, false);

            dataList.add(dataMap);

            ApplicationThread.dbPost(" GraderAttendance Saving..", "insert", new Runnable() {
                @Override
                public void run() {
                    dataAccessHandler.insertData(DatabaseKeys.GraderAttendance, dataList, VerifyFingerPrint.this, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void run() {

                            if (success) {
                            //    palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveGraderAttendance", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                UiUtils.showCustomToastMessage("Data saved", VerifyFingerPrint.this, 0);
                                if (CommonUtils.isNetworkAvailable(VerifyFingerPrint.this)) {
                                    CommonUtils.isNotSyncScreen = false;
                                    DataSyncHelper.performCollectionCenterTransactionsSync(VerifyFingerPrint.this, new ApplicationThread.OnComplete() {
                                        @Override
                                        public void execute(boolean success, Object result, String msg) {
                                            if (success) {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "GraderAttendance", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                    UiUtils.showCustomToastMessage("Successfully data sent to server", VerifyFingerPrint.this, 0);
                                                    finish();
                                                    }
                                                });
                                            } else {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "GraderAttendance", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        UiUtils.showCustomToastMessage("Data sync failed", VerifyFingerPrint.this, 1);
                                                        finish();

                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {

                                    finish();
                                }
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "GraderAttendance", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                android.util.Log.e(LOG_TAG, "@@@@ Error while saving GraderAttendance");
                                UiUtils.showCustomToastMessage("Data saving failed", VerifyFingerPrint.this, 1);
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            palm3FoilDatabase.insertErrorLogs(LOG_TAG, "GraderAttendance", CommonConstants.TAB_ID, "", e.getMessage(), CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            android.util.Log.e(LOG_TAG, "@@@@ Error while saving GraderAttendance due to " + e.getMessage());
        }
    }

    private void initUI() {

        collectionCenterName = (TextView) findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) findViewById(R.id.collection_center_village);
        verifyfingerprint = (TextView) findViewById(R.id.verifyfingerprint);
        notRequired = (TextView) findViewById(R.id.notRequired);
        nograders = (TextView) findViewById(R.id.nograders);
        name_of_grader = (TextView) findViewById(R.id.grader_name);
        imgverify_fingerprint = (ImageView) findViewById(R.id.imgverify_fingerprint);
        btn_verifyfingerprint = (Button) findViewById(R.id.btn_verifyfingerprint);
        btn_Submitverifyfingerprint = (Button)findViewById(R.id.btn_Submitverifyfingerprint);
        gradernamelinear = (LinearLayout) findViewById(R.id.gradernamelinear);
        gradernamedroplinear = (LinearLayout) findViewById(R.id.gradernamedroplinear);

        sgfplib = new JSGFPLib(this, (UsbManager) this.getSystemService(Context.USB_SERVICE));

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

        spinnergradername = (Spinner) findViewById(R.id.spinnergradername);
        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES* JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i=0; i<grayBuffer.length; ++i)
            grayBuffer[i] = Color.GRAY;
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);


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
        long result;
        if (sgfplib != null) {
            if (mVerifyImage != null)
                mVerifyImage = null;

            mVerifyImage = new byte[mImageWidth * mImageHeight];
            dwTimeStart = System.currentTimeMillis();
            try {
            if (sgfplib != null) {
                result = sgfplib.GetImageEx(mVerifyImage, IMAGE_CAPTURE_TIMEOUT_MS, IMAGE_CAPTURE_QUALITY);
            }
            else{
                Toast.makeText(VerifyFingerPrint.this, "result is null", Toast.LENGTH_SHORT).show();

            }
            } catch (NullPointerException e) {
                // Handle the NullPointerException here, e.g., log the error and display a message.
                e.printStackTrace(); // Log the exception stack trace for debugging.
            //    Toast.makeText(VerifyFingerPrint.this,  e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgverify_fingerprint.setImageBitmap(toGrayscale(mVerifyImage));
                }
            });

            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            int quality[] = new int[1];
          //  result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mVerifyImage, quality);
            try {
                if (sgfplib != null) {
                    result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mVerifyImage, quality);                }
                else{
                    Toast.makeText(VerifyFingerPrint.this, "result is null", Toast.LENGTH_SHORT).show();

                }
            } catch (NullPointerException e) {
                // Handle the NullPointerException here, e.g., log the error and display a message.
                e.printStackTrace(); // Log the exception stack trace for debugging.
                //    Toast.makeText(VerifyFingerPrint.this,  e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
            SGFingerInfo fpInfo = new SGFingerInfo();
            fpInfo.FingerNumber = 1;
            fpInfo.ImageQuality = quality[0];
            fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
            fpInfo.ViewNumber = 1;

            for (int i = 0; i < mVerifyTemplate.length; ++i)
                mVerifyTemplate[i] = 0;
            dwTimeStart = System.currentTimeMillis();
            result = sgfplib.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate);
            dwTimeEnd = System.currentTimeMillis();
            dwTimeElapsed = dwTimeEnd - dwTimeStart;

            if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                int[] size = new int[1];
                result = sgfplib.GetTemplateSize(mVerifyTemplate, size);

                ArrayList<byte[]> registeredTemplates = new ArrayList<>();
                ArrayList<String> fingerprintdata = new ArrayList<>();

                for (int i = 0; i < graderDetails.size(); i++) {
                    fingerprintdata.add(graderDetails.get(i).getFingerPrintData1());
                }

                boolean fingerprintMatched = false;
                List<byte[]> fingerprintDataList = new ArrayList<>();

                if (graderDetails.size() != 0) {
                    for (GraderDetails matchedGrader : graderDetails) {
                        if (matchedGrader == null) {
                            continue; // Skip null entries
                        }

                        if (matchedGrader.getName().equals(selectedGrader)) {
                            byte[] fingerprintData = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                fingerprintData = Base64.getDecoder().decode(matchedGrader.getFingerPrintData1().trim());
                            }

                            if (fingerprintData != null) {
                                fingerprintDataList.add(fingerprintData);
                            }

                            // Add the second fingerprint data to the list if available
                            String fingerprintData2 = matchedGrader.getFingerPrintData2();
                            if (fingerprintData2 != null && !fingerprintData2.isEmpty()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    fingerprintData = Base64.getDecoder().decode(fingerprintData2.trim());
                                }

                                if (fingerprintData != null) {
                                    fingerprintDataList.add(fingerprintData);
                                }
                            }

                            // Add the third fingerprint data to the list if available
                            String fingerprintData3 = matchedGrader.getFingerPrintData3();
                            if (fingerprintData3 != null && !fingerprintData3.isEmpty()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    fingerprintData = Base64.getDecoder().decode(fingerprintData3.trim());
                                }

                                if (fingerprintData != null) {
                                    fingerprintDataList.add(fingerprintData);
                                }
                            }

                            for (byte[] fingerprintBytes : fingerprintDataList) {
                                if (fingerprintBytes != null) {
                                    boolean[] matched = new boolean[1];
                                    dwTimeStart = System.currentTimeMillis();
                                    result = sgfplib.MatchTemplate(fingerprintBytes, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
                                    dwTimeEnd = System.currentTimeMillis();
                                    dwTimeElapsed = dwTimeEnd - dwTimeStart;

                                    if (matched[0]) {
                                        fingerprintMatched = true;
                                        matchedGraderName = matchedGrader.getName();
                                        matchedGraderCode = matchedGrader.getCode();
                                        break; // Exit the loop since a match is found
                                    }
                                }
                            }

                            if (fingerprintMatched) {
                                break; // Exit the outer loop since a match is found
                            }
                        }
                    }
                }

                if (fingerprintMatched) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_verifyfingerprint.setFocusable(false);
                            btn_verifyfingerprint.setClickable(false);
                            btn_Submitverifyfingerprint.setVisibility(View.VISIBLE);
                            Toast.makeText(VerifyFingerPrint.this, "Matched", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (!fingerprintDataList.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                autoOn.start();
                                btn_Submitverifyfingerprint.setVisibility(View.GONE);
                                Toast.makeText(VerifyFingerPrint.this, "Not Matched", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            mVerifyImage = null;
            fpInfo = null;
        }
    }

    //    public void Verify_FingerPrint() {
//        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
//        sgfplib.SetLedOn(false);
//        if (sgfplib != null) {
//        if (mVerifyImage != null)
//            mVerifyImage = null;
//        mVerifyImage = new byte[mImageWidth * mImageHeight];
//        dwTimeStart = System.currentTimeMillis();
//
//            long result = sgfplib.GetImageEx(mVerifyImage, IMAGE_CAPTURE_TIMEOUT_MS, IMAGE_CAPTURE_QUALITY);
//
//
//            dwTimeEnd = System.currentTimeMillis();
//            dwTimeElapsed = dwTimeEnd - dwTimeStart;
//            //mImageViewFingerprint.setImageBitmap(this.toGrayscale(mVerifyImage));
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    imgverify_fingerprint.setImageBitmap(toGrayscale(mVerifyImage));
//                }
//            });
//
//            dwTimeStart = System.currentTimeMillis();
//            result = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
//            dwTimeEnd = System.currentTimeMillis();
//            dwTimeElapsed = dwTimeEnd - dwTimeStart;
//
//            int quality[] = new int[1];
//            result = sgfplib.GetImageQuality(mImageWidth, mImageHeight, mVerifyImage, quality);
//
//            SGFingerInfo fpInfo = new SGFingerInfo();
//            fpInfo.FingerNumber = 1;
//            fpInfo.ImageQuality = quality[0];
//            fpInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
//            fpInfo.ViewNumber = 1;
//
//
//            for (int i = 0; i < mVerifyTemplate.length; ++i)
//                mVerifyTemplate[i] = 0;
//            dwTimeStart = System.currentTimeMillis();
//            result = sgfplib.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate);
//            dwTimeEnd = System.currentTimeMillis();
//            dwTimeElapsed = dwTimeEnd - dwTimeStart;
//
//            if (result == SGFDxErrorCode.SGFDX_ERROR_NONE) {
//
//                int[] size = new int[1];
//                result = sgfplib.GetTemplateSize(mVerifyTemplate, size);
//
//                ArrayList<byte[]> registeredTemplates = new ArrayList<>();
//
//                ArrayList<String> fingerprintdata = new ArrayList<>();
//
//                for (int i = 0; i < graderDetails.size(); i++) {
//                    fingerprintdata.add(graderDetails.get(i).getFingerPrintData1());
//                }
//
//                Log.d("fingerprintdatasize", fingerprintdata.size() + "");
//
//                boolean fingerprintMatched = false;
//                List<byte[]> fingerprintDataList = new ArrayList<>();
//
//
//                if (graderDetails.size() != 0) {
//
//                    for (GraderDetails matchedGrader : graderDetails) {
//
//                        Log.e("=====>629", graderDetails.size() + "");
//                        if (graderDetails.size() == 1) {
//                            selectedGrader = graderDetails.get(0).getName();
//                        } else {
//                            selectedGrader = selectedGrader;
//                        }
//                        if (matchedGrader.getName().equals(selectedGrader)) {
//                            // List<byte[]> fingerprintDataList = new ArrayList<>();
//
//                            // Decode and add the first fingerprint data to the list
//                            byte[] fingerprintData = new byte[0];
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                fingerprintData = Base64.getDecoder().decode(matchedGrader.getFingerPrintData1().trim());
//                            }
//                            fingerprintDataList.add(fingerprintData);
//
//                            // Add the second fingerprint data to the list if available
//                            String fingerprintData2 = matchedGrader.getFingerPrintData2();
//                            if (fingerprintData2 != null && !fingerprintData2.isEmpty()) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    fingerprintData = Base64.getDecoder().decode(fingerprintData2.trim());
//                                }
//                                fingerprintDataList.add(fingerprintData);
//                            }
//
//                            // Add the third fingerprint data to the list if available
//                            String fingerprintData3 = matchedGrader.getFingerPrintData3();
//                            if (fingerprintData3 != null && !fingerprintData3.isEmpty()) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    fingerprintData = Base64.getDecoder().decode(fingerprintData3.trim());
//                                }
//                                fingerprintDataList.add(fingerprintData);
//                            }
//
//                            // Iterate over all fingerprint data in the list
//                            for (byte[] fingerprintBytes : fingerprintDataList) {
//                                boolean[] matched = new boolean[1];
//                                dwTimeStart = System.currentTimeMillis();
//                                result = sgfplib.MatchTemplate(fingerprintBytes, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
//                                dwTimeEnd = System.currentTimeMillis();
//                                dwTimeElapsed = dwTimeEnd - dwTimeStart;
//
//                                if (matched[0]) {
//                                    // Fingerprint matched with a registered template
//                                    fingerprintMatched = true;
//                                    matchedGraderName = matchedGrader.getName();
//                                    matchedGraderCode = matchedGrader.getCode();
//                                    // name_of_grader.setText(matchedGraderName);
//                                    break; // Exit the loop since a match is found
//                                }
//                            }
//
//                            if (fingerprintMatched) {
//                                break; // Exit the outer loop since a match is found
//                            }
//                        }
//
//
////                //List<byte[]> fingerprintDataList = new ArrayList<>();
////
////                // Decode and add the first fingerprint data to the list
////                byte[] fingerprintData = new byte[0];
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    fingerprintData = Base64.getDecoder().decode(matchedGrader.getFingerPrintData1().trim());
////                }
////                fingerprintDataList.add(fingerprintData);
////
////                // Add the second fingerprint data to the list if available
////                String fingerprintData2 = matchedGrader.getFingerPrintData2();
////                if (fingerprintData2 != null && !fingerprintData2.isEmpty()) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                        fingerprintData = Base64.getDecoder().decode(fingerprintData2.trim());
////                    }
////                    fingerprintDataList.add(fingerprintData);
////                }
////
////                // Add the third fingerprint data to the list if available
////                String fingerprintData3 = matchedGrader.getFingerPrintData3();
////                if (fingerprintData3 != null && !fingerprintData3.isEmpty()) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                        fingerprintData = Base64.getDecoder().decode(fingerprintData3.trim());
////                    }
////                    fingerprintDataList.add(fingerprintData);
////                }
////
////                // Iterate over all fingerprint data in the list
////                for (byte[] fingerprintBytes : fingerprintDataList) {
////                    boolean[] matched = new boolean[1];
////                    dwTimeStart = System.currentTimeMillis();
////                    result = sgfplib.MatchTemplate(fingerprintBytes, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched);
////                    dwTimeEnd = System.currentTimeMillis();
////                    dwTimeElapsed = dwTimeEnd - dwTimeStart;
////
////                    if (matched[0]) {
////                        // Fingerprint matched with a registered template
////                        fingerprintMatched = true;
////                        matchedGraderName = matchedGrader.getName();
////                        matchedGraderCode = matchedGrader.getCode();
////                        //     name_of_grader.setText(matchedGraderName);
////                        break; // Exit the loop since a match is found
////                    }
////
////                }
////
////                if (fingerprintMatched) {
////                    break; // Exit the outer loop since a match is found
////                }
//                    }
//                }
//
//                if (fingerprintMatched) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            btn_verifyfingerprint.setFocusable(false);
//                            btn_verifyfingerprint.setClickable(false);
//                           btn_Submitverifyfingerprint.setVisibility(View.VISIBLE);
//                            Toast.makeText(VerifyFingerPrint.this, "Matched", Toast.LENGTH_SHORT).show();
//                         //   Toast.makeText(VerifyFingerPrint.this, matchedGraderName, Toast.LENGTH_SHORT).show();
//                        //    Toast.makeText(VerifyFingerPrint.this, matchedGraderCode, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    if (!fingerprintDataList.isEmpty()) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                autoOn.start();
//                             btn_Submitverifyfingerprint.setVisibility(View.GONE);
//                                //   gradernamelinear.setVisibility(View.GONE);
//                                Toast.makeText(VerifyFingerPrint.this, "Not Matched", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//
////            if (fingerprintMatched) {
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        name_of_grader.setText(matchedGraderName);
////                        Toast.makeText(getContext(), "Matched", Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
////            else{
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        autoOn.start();
////                        Toast.makeText(getContext(), "Not Matched", Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
//
//            }
//
//            mVerifyImage = null;
//            fpInfo = null;
//        }
//    }
    public Bitmap toGrayscale(byte[] mImageBuffer) {
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
    // Register the BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    // Unregister the BroadcastReceiver

    @Override
    public void onPause() {
     //   super.onPause();
        android.util.Log.d("TAG", "Enter onPause()");

        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0) {

            if (bSecuGenDeviceOpened) {
                autoOn.stop();
                sgfplib.CloseDevice();
                //sgfplib.Close();
                bSecuGenDeviceOpened = false;
            }
            if (mUsbReceiver != null) {
                try {
                    unregisterReceiver(mUsbReceiver);
                } catch (IllegalArgumentException e) {
                    // Handle the exception (e.g., log or ignore)
                }
                mVerifyImage = null;
                mVerifyTemplate = null;
                imgverify_fingerprint.setImageBitmap(grayBitmap);
                super.onPause();
                android.util.Log.d("TAG", "Exit onPause()");
            }

        }
        else{
            super.onPause();
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

