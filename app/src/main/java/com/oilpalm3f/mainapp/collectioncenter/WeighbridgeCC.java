package com.oilpalm3f.mainapp.collectioncenter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alcorlink.alcamsdk.SecuGenDevice;
import com.oilpalm3f.mainapp.BuildConfig;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionWithOutPlot;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.common.InputFilterMinMax;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DataSavingHelper;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.FileRepository1;
import com.oilpalm3f.mainapp.dbmodels.GraderDetails;
import com.oilpalm3f.mainapp.dbmodels.ImageUtility1;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;
import com.oilpalm3f.mainapp.utils.AlbumStorageDirFactory;
import com.oilpalm3f.mainapp.utils.BaseAlbumDirFactory;
import com.oilpalm3f.mainapp.utils.DateFormats;
import com.oilpalm3f.mainapp.utils.ImageUtility;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.PRIVATE_WEIGHBRIDGE_INFO;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

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

/**
 * A simple {@link Fragment} subclass.
 */

//Fill Collection Details Screen
public class WeighbridgeCC extends BaseFragment implements Runnable, SGFingerPresentEvent {
    private long twoDaysTime = 15 * 24 * 3600 * 1000;
    public static final int REQUEST_CAM_PERMISSIONS = 1;
    public static final int OWN_WEIGHBRIDGE = 24;
    public static final int PRIVATE_WEIGHBRIDGE = 25;
    public static final int NO_WEIGHBRIDGE = 26;
    public static final int Manual_Weigh = 597;
    private boolean firstTime = true;
    private String cropImagePath1, cropImagePath2;
    List<String> cropImagesList = new ArrayList<>();
    List<FileRepository1> oftObservationTakenImagesList;
    private List<String> stateIds;

    private static final String LOG_TAG = WeighbridgeCC.class.getName();
    private static final int CAMERA_REQUEST = 1888;
    private android.widget.Spinner nameOfPrivateWbSpin;
    Spinner vehicleTypespin, vehicleCategoryspin, isloosefruitavailable_spinner;
    private LinkedHashMap<String, String> VehicleTypeMap, VehicleCategoryTypeMap;
    private ImageView scrollBottomIndicator;
    private InteractiveScrollView interactiveScrollView;
    private Bitmap userImageData;
    private File fileToUpload;
    LinearLayout img2Layout, img1Layout;
    private ImageView slipImage, slipIcon, img_takefingerprint;
    private Button generateReceipt;
    private EditText commentsEdit, dateTimeEdit, vehicle_no,
            vehicleDriver, grossWt, tareWt, netWt, name_of_operator, no_of_bunches, no_of_bunches_rejected, rmrks, name_of_grader, number_of_bunches_accepted, postDateTimeEdit;
    private String vehicleNumber, vehicleDriverName, grossWeight, tareWeight, netWeight, operatorName, bunchesNumber, rejectedBunches, remarks, graderName;

    private EditText harvestername, harvestermobilenumber, harvestervillage, harvestermandal, unripen, underripe, ripen, overripe, diseased,
            emptybunches, longstalk, mediumstalk, shortstalk, optimum, loosefruitweight;
    private LinearLayout loosefruitweightLL;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ImageView imageView1, imageView2;
    private int typeSelected;
    private LinearLayout privateWBrel, tareWeightLL, grossWeightLL;
    private View rootView;
    private double GrossWeightD = 0.0, TareWeightD = 0.0;
    private LinkedHashMap<String, String> weighbridgeCenterDataMap = null;
    private DataAccessHandler dataAccessHandler;
    private CCDataAccessHandler ccDataAccessHandler;
    private String dateAndTimeStr = "";
    public static String postedDateAndTimeStr = "";
    private CollectionCenter selectedCollectionCenter;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt, netWeightTxt, takefingerprint;
    private String collectionId = "", totalBunches;
    private Point mSize;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String ColFarmerWithOutplot;

    private String vehicleCategoryCode, vehicleCategoryType;
    private String vehicleTypeCode, vehicleTypeName;
    private TextView vehiclenumber_tv;
    private LinearLayout newlyt;
    Boolean whichimage = false;

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
    }; //Added by Arun dated 21st June

    final DatePickerDialog.OnDateSetListener date_TimeofWeighing = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            weighingCalendar.set(Calendar.YEAR, year);
            weighingCalendar.set(Calendar.MONTH, monthOfYear);
            weighingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS;
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateAndTimeStr = sdf.format(weighingCalendar.getTime());
            dateTimeEdit.setText(sdf.format(weighingCalendar.getTime()));
        }
    };


    final DatePickerDialog.OnDateSetListener postingDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar1.set(Calendar.YEAR, year);
            myCalendar1.set(Calendar.MONTH, monthOfYear);
            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = CommonConstants.PostingDate_FORMAT_DDMMYYYY_HHMMSS;
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            SimpleDateFormat sdf2 = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
            postedDateAndTimeStr = sdf2.format(myCalendar1.getTime());
            postDateTimeEdit.setText(sdf.format(myCalendar1.getTime()));

        }
    };
    private int rejectedBunches_converted, totalBunches_converted;
    private String createdByName;
    private String mCurrentPhotoPath;
    private String mCurrentPhotoPath1;

    private Bitmap currentBitmap = null;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar1 = Calendar.getInstance();
    Calendar weighingCalendar = Calendar.getInstance();
    private String days = "";
    public int financialYear;

    Button btn_takefingerprint;

    public WeighbridgeCC() {
    }


    //Initializing the Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        rootView = inflater.inflate(R.layout.fragment_weighbridge_cc, null);
        baseLayout.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        Log.d("selectedCollectionCenterState", selectedCollectionCenter.getStateId() + "");
        Log.d("InwhichscreenamI", "WeighbridgeCC" + "");

        Log.d("WeighbridgeCC.IsFingerPrintReq", CommonConstants.IsFingerPrintReq + "");

        typeSelected = selectedCollectionCenter.getWeighBridgeTypeId();

        if (typeSelected == OWN_WEIGHBRIDGE) {
            setTile(getString(R.string.new_collection_own));
        } else if (typeSelected == PRIVATE_WEIGHBRIDGE) {
            setTile(getString(R.string.new_collection_priv));
        } else if (typeSelected == Manual_Weigh) {
            setTile(getString(R.string.new_collection_manual));

        } else {
            setTile(getString(R.string.new_collection_no_weighbridge));
        }

        initViews();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();

        sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));

        autoOn = new SGAutoOnEventNotifier(sgfplib, this);
        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        mAutoOnEnabled = false;
        usbPermissionRequested = false;
        bSecuGenDeviceOpened = false;
        mNumFakeThresholds = new int[1];
        mDefaultFakeThreshold = new int[1];
        mFakeEngineReady = new boolean[1];
        mMaxTemplateSize = new int[1];
    }


    //Initializing the UI
    public void initViews() {

        dataAccessHandler = new DataAccessHandler(getActivity());
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
        DataManager.getInstance().deleteData(DataManager.Manual_Images);


        graderDetails = dataAccessHandler.getGraderdetails(Queries.getInstance().getGraderDetails(selectedCollectionCenter.getCode()));

        Log.d("graderDetails", graderDetails.size() +"");

//        for (int i = 0; i< graderDetails.size(); i++) {
//            Log.d("gradersName", graderDetails.get(i).getName());
//            fingerprint1list.add(graderDetails.get(i).getFingerPrintData1());
//            fingerprint2list.add(graderDetails.get(i).getFingerPrintData2());
//            fingerprint3list.add(graderDetails.get(i).getFingerPrintData3());
//        }
//        mainArray.add(fingerprint1list);
//        mainArray.add(fingerprint2list);
//        mainArray.add(fingerprint2list);
//
//        for (List<String> fingerprintList : mainArray) {
//            for (String fingerprint : fingerprintList) {
//                if (fingerprint == null || fingerprint.isEmpty()) {
//                    hasNullString = true;
//                    break;
//                }
//            }
//            if (hasNullString) {
//                break;
//            }
//        }
//
//        if (hasNullString) {
//            Log.d("MainArray", "Main array contains null or empty strings.");
//        } else {
//            Log.d("MainArray", "Main array does not contain null or empty strings.");
//        }


        ColFarmerWithOutplot = FarmersDetailsScreen.firstthree;
//        Toast.makeText(getActivity(),"Collection module  "+ColFarmerWithOutplot,Toast.LENGTH_LONG).show();

        privateWBrel = rootView.findViewById(R.id.privateWBrel);
        grossWeightLL = rootView.findViewById(R.id.gross_weight_LL);
        tareWeightLL = rootView.findViewById(R.id.tare_weight_LL);

        if (typeSelected == PRIVATE_WEIGHBRIDGE) {
            privateWBrel.setVisibility(View.VISIBLE);
        } else {
            privateWBrel.setVisibility(View.GONE);
        }

        if (typeSelected == NO_WEIGHBRIDGE) {
            tareWeightLL.setVisibility(View.GONE);
            grossWeightLL.setVisibility(View.GONE);
            removeMandatoryFields();
        }


        vehicle_no = (EditText) rootView.findViewById(R.id.vehicle_number);
        vehiclenumber_tv = (TextView) rootView.findViewById(R.id.vehiclenumber_tv);
        vehicleDriver = (EditText) rootView.findViewById(R.id.vehicle_driver);
        grossWt = (EditText) rootView.findViewById(R.id.gross_weight);
        tareWt = (EditText) rootView.findViewById(R.id.tare_weight);
        netWt = (EditText) rootView.findViewById(R.id.net_weight);
        netWeightTxt = (TextView) rootView.findViewById(R.id.netWeightTxt);
        img1Layout = rootView.findViewById(R.id.img1Layout);
        img2Layout = rootView.findViewById(R.id.img2Layout);
        imageView1 = rootView.findViewById(R.id.imageView1);
        imageView2 = rootView.findViewById(R.id.imageView2);
        if (typeSelected == Manual_Weigh) {
            tareWeightLL.setVisibility(View.GONE);
            grossWeightLL.setVisibility(View.GONE);
            removeMandatoryFields();
            img1Layout.setVisibility(View.VISIBLE);
            img2Layout.setVisibility(View.VISIBLE);

        }

        if (typeSelected == NO_WEIGHBRIDGE) {
            netWeightTxt.setVisibility(View.GONE);
            netWt.setVisibility(View.VISIBLE);
        } else if (typeSelected == Manual_Weigh) {
            netWeightTxt.setVisibility(View.GONE);
            netWt.setVisibility(View.VISIBLE);
            img1Layout.setVisibility(View.VISIBLE);
            img2Layout.setVisibility(View.VISIBLE);

        } else {
            netWeightTxt.setVisibility(View.VISIBLE);
            netWt.setVisibility(View.GONE);
        }

        name_of_operator = (EditText) rootView.findViewById(R.id.operator_name);
        UserDetails userDetails = (UserDetails) DataManager.getInstance().getDataFromManager(USER_DETAILS);

        if (null != userDetails) {
            createdByName = userDetails.getFirstName();
            name_of_operator.setText(createdByName);
        }
        no_of_bunches = (EditText) rootView.findViewById(R.id.number_of_bunches);
        no_of_bunches_rejected = (EditText) rootView.findViewById(R.id.number_of_bunches_rejected);
        rmrks = (EditText) rootView.findViewById(R.id.remarks);
        name_of_grader = (EditText) rootView.findViewById(R.id.grader_name);
        dateTimeEdit = (EditText) rootView.findViewById(R.id.date_and_time);
        postDateTimeEdit = (EditText) rootView.findViewById(R.id.post_date_and_time);
        slipImage = (ImageView) rootView.findViewById(R.id.slip_image);
        slipIcon = (ImageView) rootView.findViewById(R.id.slip_icon);
        commentsEdit = (EditText) rootView.findViewById(R.id.commentsEdit);
        generateReceipt = (Button) rootView.findViewById(R.id.generateReceipt);
        nameOfPrivateWbSpin = (Spinner) rootView.findViewById(R.id.millSpin);
        vehicleCategoryspin = (Spinner) rootView.findViewById(R.id.vehicleCategorySpin);
        vehicleTypespin = (Spinner) rootView.findViewById(R.id.vehicleTypeSpin);
        number_of_bunches_accepted = (EditText) rootView.findViewById(R.id.number_of_bunches_accepted);

        newlyt = rootView.findViewById(R.id.newlyt);
        harvestername = rootView.findViewById(R.id.harvestername_et);
        harvestermobilenumber = rootView.findViewById(R.id.harvestermobileNumber_et);
        harvestervillage = rootView.findViewById(R.id.harvestervillage_et);
        harvestermandal = rootView.findViewById(R.id.harvestermandal_et);
        unripen = rootView.findViewById(R.id.unripen);
        underripe = rootView.findViewById(R.id.underripe);
        ripen = rootView.findViewById(R.id.ripen);
        overripe = rootView.findViewById(R.id.overipe);
        diseased = rootView.findViewById(R.id.diseased);
        emptybunches = rootView.findViewById(R.id.emptybunch);
        longstalk = rootView.findViewById(R.id.longstake);
        mediumstalk = rootView.findViewById(R.id.mediumstake);
        shortstalk = rootView.findViewById(R.id.shortstake);
        optimum = rootView.findViewById(R.id.optimum);
        loosefruitweight = rootView.findViewById(R.id.loosefruitweight);
        loosefruitweightLL = rootView.findViewById(R.id.loosefruitweightLL);
        isloosefruitavailable_spinner = rootView.findViewById(R.id.isloosefruitavailable_spinner);

        img_takefingerprint = rootView.findViewById(R.id.img_takefingerprint);
        takefingerprint = rootView.findViewById(R.id.takefingerprint);
        btn_takefingerprint = rootView.findViewById(R.id.btn_takefingerprint);

        if (selectedCollectionCenter.getStateId() != 1) {
            newlyt.setVisibility(View.GONE);
        } else {
            newlyt.setVisibility(View.VISIBLE);
        }

        unripen.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        underripe.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        ripen.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        overripe.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        diseased.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        emptybunches.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        longstalk.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        mediumstalk.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        shortstalk.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});
        optimum.setFilters(new InputFilter[]{new InputFilterMinMax("0", "100")});

        collectionCenterName = (TextView) rootView.findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) rootView.findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) rootView.findViewById(R.id.collection_center_village);
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());


        scrollBottomIndicator = (ImageView) rootView.findViewById(R.id.bottomScroll);
        interactiveScrollView = (InteractiveScrollView) rootView.findViewById(R.id.scrollView);
        scrollBottomIndicator.setVisibility(View.VISIBLE);

        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
            String financalDate = "01/04/" + String.valueOf(financialYear);
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(financalDate);
            long diff = date1.getTime() - date2.getTime();
            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
            days = StringUtils.leftPad(noOfDays, 3, "0");
            Log.v(LOG_TAG, "days -->" + days);

        } catch (Exception e) {
            e.printStackTrace();
        }

        scrollBottomIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactiveScrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        interactiveScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        interactiveScrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                scrollBottomIndicator.setVisibility(View.GONE);
            }
        });

        interactiveScrollView.setOnTopReachedListener(new InteractiveScrollView.OnTopReachedListener() {
            @Override
            public void onTopReached() {
            }
        });

        interactiveScrollView.setOnScrollingListener(new InteractiveScrollView.OnScrollingListener() {
            @Override
            public void onScrolling() {
                //Log.d(LOG_TAG, "onScrolling: ");
                scrollBottomIndicator.setVisibility(View.VISIBLE);
            }
        });

        //Binding Data to Loose Fruit Available & On Item Selected Listener
        String[] isloosefruitavailableArray = getResources().getStringArray(R.array.yesOrNo_values);
        List<String> isloosefruitavailableList = Arrays.asList(isloosefruitavailableArray);
        ArrayAdapter<String> isloosefruitavailableAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, isloosefruitavailableList);
        isloosefruitavailableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isloosefruitavailable_spinner.setAdapter(isloosefruitavailableAdapter);

        isloosefruitavailable_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {

                    loosefruitweightLL.setVisibility(View.VISIBLE);
                    loosefruitweight.setText("");
                } else {
                    loosefruitweightLL.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Binding Data to Vehicle Category & On Item Selected Listener
        VehicleCategoryTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleCategoryType());
        ArrayAdapter spinnerArrayAdaptervechilecategory = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(VehicleCategoryTypeMap, "Vehicle Category"));
        spinnerArrayAdaptervechilecategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleCategoryspin.setAdapter(spinnerArrayAdaptervechilecategory);

        vehicleCategoryspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (VehicleCategoryTypeMap != null && VehicleCategoryTypeMap.size() > 0 && vehicleCategoryspin.getSelectedItemPosition() != 0) {
                    vehicleCategoryCode = VehicleCategoryTypeMap.keySet().toArray(new String[VehicleCategoryTypeMap.size()])[i - 1];
                    vehicleCategoryType = vehicleCategoryspin.getSelectedItem().toString();
                    android.util.Log.v(LOG_TAG, "@@@ vehicle category code " + vehicleCategoryCode + " category name " + vehicleCategoryType);

                    //Binding Data to Vehicle Type
                    VehicleTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleTypeonCategory(vehicleCategoryCode));
                    ArrayAdapter spinnerArrayAdaptervechileType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(VehicleTypeMap, "Vehicle"));
                    spinnerArrayAdaptervechileType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicleTypespin.setAdapter(spinnerArrayAdaptervechileType);

                }

                if (vehicleCategoryspin.getSelectedItemPosition() == 1) {

                    vehiclenumber_tv.setText("Vehicle Number");
                } else {

                    vehiclenumber_tv.setText("Vehicle Number *");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Vehicle Type On Item Selected
        vehicleTypespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (VehicleTypeMap != null && VehicleTypeMap.size() > 0 && vehicleTypespin.getSelectedItemPosition() != 0) {
                    vehicleTypeCode = VehicleTypeMap.keySet().toArray(new String[VehicleTypeMap.size()])[i - 1];
                    vehicleTypeName = vehicleTypespin.getSelectedItem().toString();
                    android.util.Log.v(LOG_TAG, "@@@ vehicle Type code " + vehicleTypeCode + " vehicle Type name " + vehicleTypeName);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Binding Data to WeighBridge Spin based on State Id
        stateIds = dataAccessHandler.getListOfCodes(Queries.getInstance().getstateIds(CommonConstants.USER_ID));
        Log.d("stateIds", stateIds.size() + "");
        Log.d("stateIdscontent", stateIds.get(0));

        if (stateIds.size() > 1) {

            Log.d("StateWise1", "Weighbridgecenter State wise");
            weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getweighbridgecenteragainststate(CommonConstants.USER_ID));
            DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);
        }

        if (stateIds.size() == 1 && stateIds.get(0).equalsIgnoreCase("1")) {

            Log.d("StateWise2", "Weighbridgecenter District wise AP");
            weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getweighbridgecenteragainstdistricts(CommonConstants.USER_ID));
            DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);
        }

        if (stateIds.size() == 1 && !stateIds.get(0).equalsIgnoreCase("1")) {

            Log.d("StateWise3", "Weighbridgecenter State wise not AP");
            weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getweighbridgecenteragainststate(CommonConstants.USER_ID));
            DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                    CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);
        }


//        weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getweighbridgecenteragainstdistricts(CommonConstants.USER_ID));
//        DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
//        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
//                CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);

        //WeighBridge Spin On Item Selected

        nameOfPrivateWbSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (weighbridgeCenterDataMap != null && weighbridgeCenterDataMap.size() > 0 && nameOfPrivateWbSpin.getSelectedItemPosition() != 0) {
                    if (!CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
                        int selectedPos = nameOfPrivateWbSpin.getSelectedItemPosition();
                        String wbId = weighbridgeCenterDataMap.keySet().toArray(new String[weighbridgeCenterDataMap.size()])[selectedPos - 1];
                        Log.v(LOG_TAG, "@@@ selected wb center id " + wbId);
                        String wbName = weighbridgeCenterDataMap.get(wbId);
                        Log.v(LOG_TAG, "@@@ selected wb center name " + wbName);
                        netWt.setVisibility(View.GONE);
                        netWeightTxt.setVisibility(View.VISIBLE);
                    } else {
                        if (typeSelected == NO_WEIGHBRIDGE) {
                            dateTimeEdit.setText("");
                            netWt.setVisibility(View.VISIBLE);
                            netWeightTxt.setVisibility(View.GONE);
                        }
                        if (typeSelected == Manual_Weigh) {
                            dateTimeEdit.setText("");
                            netWt.setVisibility(View.VISIBLE);
                            netWeightTxt.setVisibility(View.GONE);
                        }

                    }
                } else {
                    if (typeSelected == NO_WEIGHBRIDGE) {
                        dateTimeEdit.setText("");
                        netWt.setVisibility(View.VISIBLE);
                        netWeightTxt.setVisibility(View.GONE);
                    }
                    if (typeSelected == Manual_Weigh) {
                        dateTimeEdit.setText("");
                        netWt.setVisibility(View.VISIBLE);
                        netWeightTxt.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Image Views On Click Listener
        slipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstTime = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                    Log.v(LOG_TAG, "Location Permissions Not Granted");
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {
//                    Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(Camera, CAMERA_REQUEST);
                    dispatchTakePictureIntent(CAMERA_REQUEST);
                }
            }
        });

        imageView1.setOnClickListener(v1 -> {
            firstTime = true;
            whichimage = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                android.util.Log.v(LOG_TAG, "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                dispatchTakePictureIntent1(CAMERA_REQUEST);
            }
        });

        imageView2.setOnClickListener(v1 -> {
            whichimage = false;
            firstTime = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                android.util.Log.v(LOG_TAG, "Camera Permissions Not Granted");
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_STORAGE,
                        REQUEST_CAM_PERMISSIONS
                );
            } else {
                dispatchTakePictureIntent1(CAMERA_REQUEST);
            }
        });

        //Generate Receipt On Click Listener

        generateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard();
                injectData();
                if (typeSelected == Manual_Weigh && isValidation() && validationsInNoWbCase()) {

                    saveObservationImages();
                }

                if (typeSelected == OWN_WEIGHBRIDGE && !validationsInOwWbCase(true)) {
                    return;
                } else if (typeSelected == PRIVATE_WEIGHBRIDGE && !validationsInPrivateWbCase()) {
                    return;
                } else if (!validationsInNoWbCase()) {
                    return;
                }

                //sgfplib.Close();
                gotoPreviewScreen();
            }
        });

        //Weighing Date On Click Listener

        dateTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date_TimeofWeighing, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();


              /*  new SlideDateTimePicker.Builder(getChildFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setMaxDate(new Date(System.currentTimeMillis()))
                        .build()
                        .show();*/
            }
        });

        //Posting Date On Click Listener

        postDateTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.DATE, -5);
                Date dateBefore5Days = cal.getTime();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), postingDate, myCalendar1
                        .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(dateBefore5Days.getTime());
                datePickerDialog.show();


            }
        });

        //Gross Weight On Text Changed Listener

        grossWt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                grossWeight = grossWt.getText().toString();
                if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE || typeSelected == Manual_Weigh) {
                    validateDoubles();
                }

                if (TextUtils.isEmpty(grossWeight) || !validateDoubles()) {
                    tareWt.setText("");
                    netWeightTxt.setText("");
                }
                enableNetWeight();
            }
        });

        //Tare Weight On Text Changed Listener

        tareWt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tareWeight = tareWt.getText().toString();
                if (!validateDoubles()) {
                    if (!TextUtils.isEmpty(grossWeight)) {
                        tareWt.setText(tareWeight.substring(0, tareWeight.length() - 1));
                        tareWt.setSelection(tareWeight.length());
                    }
                }
                enableNetWeight();
            }
        });

        //Net Weight On Text Changed Listener

        netWt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE) {
//                    String netWeight = netWt.getText().toString();
//                    if (!TextUtils.isEmpty(netWeight) && TextUtils.isEmpty(tareWeight) && TextUtils.isEmpty(grossWeight)) {
//                        tareWt.setEnabled(false);
//                        grossWt.setEnabled(false);
//                    } else {
//                        tareWt.setEnabled(true);
//                        grossWt.setEnabled(true);
//                    }
//                }
            }
        });

        //No of Bunches On Text Changed Listener
        no_of_bunches.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                totalBunches = no_of_bunches.getText().toString();
                if (TextUtils.isEmpty(totalBunches) || !validateBunchValues()) {
                    no_of_bunches_rejected.setText("");
                    number_of_bunches_accepted.setText("");
                }
            }
        });

        //No of Bunches Rejected On Text Changed Listener
        no_of_bunches_rejected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rejectedBunches = no_of_bunches_rejected.getText().toString();
                if (!validateBunchValues()) {
                    if (!TextUtils.isEmpty(totalBunches)) {
                        no_of_bunches_rejected.setText(rejectedBunches.substring(0, rejectedBunches.length() - 1));
                        no_of_bunches_rejected.setSelection(rejectedBunches.length());
                    }
                }
            }
        });

        //Set Collection Id based on With or Without Plot Farmers
        collectionIDTxt = (TextView) rootView.findViewById(R.id.collection_ID);
        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days);
        } else {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days);
        }
        collectionIDTxt.setText("" + collectionId);
        Log.v(LOG_TAG, "@@@ collection code " + collectionId);
        postDateTimeEdit.setText(DateFormats.getDateTime(new Date()));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        postedDateAndTimeStr = objdateformat.format(c.getTime());


        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
            takefingerprint.setVisibility(View.VISIBLE);
            img_takefingerprint.setVisibility(View.VISIBLE);
            name_of_grader.setFocusable(false);
            btn_takefingerprint.setVisibility(View.VISIBLE);
        }else{
            takefingerprint.setVisibility(View.GONE);
            img_takefingerprint.setVisibility(View.GONE);
            name_of_grader.setFocusable(true);
            btn_takefingerprint.setVisibility(View.GONE);
        }

//        name_of_grader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Clicked", "Yes");
//                autoOn.start();
//                takefingerprint.setVisibility(View.VISIBLE);
//                img_takefingerprint.setVisibility(View.VISIBLE);
//            }
//        }); //Added by Arun dated 21st June

        img_takefingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // autoOn.start();
                Log.d("img_takefingerprint", "Clicked");
            }
        });//Added by Arun dated 21st June

        btn_takefingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("btn_takefingerprint", "Clicked");

                //sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));
                long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
//        UsbDevice usbDevice = sgfplib.GetUsbDevice();
//        long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);

                if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){

                    getContext().registerReceiver(mUsbReceiver, filter);
                    error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
                    if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
                        if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                            dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
                        else
                            dlgAlert.setMessage("Fingerprint device initialization failed!");
                        dlgAlert.setTitle("SecuGen Fingerprint SDK");
                        dlgAlert.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int whichButton){
                                        getActivity().finish();
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
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
                            dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
                            dlgAlert.setTitle("SecuGen Fingerprint SDK");
                            dlgAlert.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            getActivity().finish();
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
                                error = sgfplib.OpenDevice(0);
                                Toast.makeText(getContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();
                                if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                                    bSecuGenDeviceOpened = true;
                                    SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                                    error = sgfplib.GetDeviceInfo(deviceInfo);
                                    mImageWidth = deviceInfo.imageWidth;
                                    mImageHeight = deviceInfo.imageHeight;
                                    mImageDPI = deviceInfo.imageDPI;
                                    sgfplib.SetLedOn(true);
                                    autoOn.start();

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
                                    Toast.makeText(getContext(), "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
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

    //Enabling/Disabling NetWeight Field
    public void enableNetWeight() {
        if (CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
            if (typeSelected == NO_WEIGHBRIDGE && typeSelected == Manual_Weigh && (!TextUtils.isEmpty(tareWeight) || !TextUtils.isEmpty(grossWeight))) {
                netWt.setText("");
                netWt.setEnabled(false);
                netWt.setInputType(InputType.TYPE_NULL);
            } else {
                netWt.setEnabled(true);
                netWt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        }
    }

    //navigation to next screen with data
    public void gotoPreviewScreen() {
        PrintReceipt printReceipt = new PrintReceipt();
        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
            bindCollectionFarmerWOPData();
            Bundle dataBundle = new Bundle();
            dataBundle.putParcelable("collection_data", bindCollectionFarmerWOPData());
            dataBundle.putString("collectionsamedaywithoutfarmer", days);
            printReceipt.setArguments(dataBundle);

        } else {
            bindData();
            Bundle dataBundle = new Bundle();
            dataBundle.putParcelable("collection_data", bindData());
            dataBundle.putString("collectionsamedaywithfarmer", days);
            printReceipt.setArguments(dataBundle);
        }

        String backStateName = printReceipt.getClass().getName();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ftransaction = fm.beginTransaction();
        ftransaction.replace(android.R.id.content, printReceipt).commit();
        ftransaction.addToBackStack(backStateName);
    }


    // Bind Data for Farmer without Plot
    public CollectionWithOutPlot bindCollectionFarmerWOPData() {
        CollectionWithOutPlot wbWOPCollection = new CollectionWithOutPlot();

        wbWOPCollection.setWeighingDate(dateAndTimeStr);
        wbWOPCollection.setTokenNo(0);
        wbWOPCollection.setCollectioncentercode(selectedCollectionCenter.getCode());
        wbWOPCollection.setCode(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));

        if (TextUtils.isEmpty(vehicleNumber)) {

            wbWOPCollection.setVehiclenumber("null");
        } else {

            wbWOPCollection.setVehiclenumber(vehicleNumber.toUpperCase());
        }

        wbWOPCollection.setDrivername(vehicleDriverName);
        wbWOPCollection.setGrossweight(CommonUtils.isValidDouble(grossWeight) ? Double.parseDouble(grossWeight) : 0);
        wbWOPCollection.setNetweight(CommonUtils.isValidDouble(netWeight) ? Double.parseDouble(netWeight) : 0);
        wbWOPCollection.setTareweight(CommonUtils.isValidDouble(tareWeight) ? Double.parseDouble(tareWeight) : 0);
        wbWOPCollection.setOperatorname(operatorName);
        wbWOPCollection.setRecieptGeneratedDate(postedDateAndTimeStr);
        if (!TextUtils.isEmpty(bunchesNumber) && bunchesNumber.length() > 0) {
            wbWOPCollection.setTotalbunches(Integer.parseInt(bunchesNumber));
        }
        if (!TextUtils.isEmpty(rejectedBunches) && rejectedBunches.length() > 0) {
            wbWOPCollection.setRejectedbunches(Integer.parseInt(rejectedBunches));
        }
        if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE || typeSelected == Manual_Weigh) {
            if (!CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
                int selectedPos = nameOfPrivateWbSpin.getSelectedItemPosition();
                String wbId = weighbridgeCenterDataMap.keySet().toArray(new String[weighbridgeCenterDataMap.size()])[selectedPos - 1];
                Log.v(LOG_TAG, "@@@ selected wb center id " + wbId);
                String wbName = weighbridgeCenterDataMap.get(wbId);
                Log.v(LOG_TAG, "@@@ selected wb center name " + wbName);
                wbWOPCollection.setWeighbridgecenterid(Integer.parseInt(wbId));
            } else {
                wbWOPCollection.setWeighbridgecenterid(null);
            }
        } else {
            wbWOPCollection.setWeighbridgecenterid(null);
        }
        wbWOPCollection.setRemarks(remarks);

//        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
//            wbWOPCollection.setGradername(matchedGraderName);
//        }else{
//            wbWOPCollection.setGradername(graderName);
//        }

        wbWOPCollection.setGradername(graderName);

        wbWOPCollection.setGraderCode(matchedGraderCode);

        wbWOPCollection.setComments(commentsEdit.getText().toString());
        wbWOPCollection.setRecieptlocation(mCurrentPhotoPath);
        wbWOPCollection.setRecieptextension(".jpg");

        wbWOPCollection.setVehicleTypeId(Integer.parseInt(vehicleTypeCode));

//        if (TextUtils.isEmpty(harvestername.getText().toString())) {
//            wbWOPCollection.setName("null");
//        }else{
//            wbWOPCollection.setName(harvestername.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
//            wbWOPCollection.setMobileNumber("null");
//        }else{
//            wbWOPCollection.setMobileNumber(harvestermobilenumber.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestervillage.getText().toString())) {
//            wbWOPCollection.setVillage("null");
//        }else{
//            wbWOPCollection.setVillage(harvestervillage.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestermandal.getText().toString())) {
//            wbWOPCollection.setMandal("null");
//        }else{
//            wbWOPCollection.setMandal(harvestermandal.getText().toString());
//        }


        if (!TextUtils.isEmpty(harvestername.getText().toString())) {
            wbWOPCollection.setName(harvestername.getText().toString());
        } else {
            wbWOPCollection.setName("");
        }

        if (!TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
            wbWOPCollection.setMobileNumber(harvestermobilenumber.getText().toString());
        } else {
            wbWOPCollection.setMobileNumber("");
        }

        if (!TextUtils.isEmpty(harvestervillage.getText().toString())) {
            wbWOPCollection.setVillage(harvestervillage.getText().toString());
        } else {
            wbWOPCollection.setVillage("");
        }

        if (!TextUtils.isEmpty(harvestermandal.getText().toString())) {
            wbWOPCollection.setMandal(harvestermandal.getText().toString());
        } else {
            wbWOPCollection.setMandal("");
        }

        if (selectedCollectionCenter.getStateId() == 1) {

            wbWOPCollection.setUnRipen(Integer.parseInt(unripen.getText().toString()));
            wbWOPCollection.setUnderRipe(Integer.parseInt(underripe.getText().toString()));
            wbWOPCollection.setRipen(Integer.parseInt(ripen.getText().toString()));
            wbWOPCollection.setOverRipe(Integer.parseInt(overripe.getText().toString()));
            wbWOPCollection.setDiseased(Integer.parseInt(diseased.getText().toString()));
            wbWOPCollection.setEmptyBunches(Integer.parseInt(emptybunches.getText().toString()));
            wbWOPCollection.setFFBQualityLong(Integer.parseInt(longstalk.getText().toString()));
            wbWOPCollection.setFFBQualityMedium(Integer.parseInt(mediumstalk.getText().toString()));
            wbWOPCollection.setFFBQualityShort(Integer.parseInt(shortstalk.getText().toString()));
            wbWOPCollection.setFFBQualityOptimum(Integer.parseInt(optimum.getText().toString()));

            int isfruitavailablee = 0;

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {

                isfruitavailablee = 1;
            } else if (isloosefruitavailable_spinner.getSelectedItemPosition() == 2) {
                isfruitavailablee = 0;
            }

            wbWOPCollection.setLooseFruit(isfruitavailablee);

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {

                wbWOPCollection.setLooseFruitWeight(Integer.parseInt(loosefruitweight.getText().toString()));

            }
        }

        return wbWOPCollection;
    }

    // Bind Data for Farmer with Plot
    public Collection bindData() {
        Collection wbCollection = new Collection();
        wbCollection.setWeighingDate(dateAndTimeStr);
        wbCollection.setTokenNo(0);

        wbCollection.setCollectioncentercode(selectedCollectionCenter.getCode());
        wbCollection.setCode(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));

        if (TextUtils.isEmpty(vehicleNumber)) {

            wbCollection.setVehiclenumber("null");
        } else {

            wbCollection.setVehiclenumber(vehicleNumber.toUpperCase());
        }

        wbCollection.setDrivername(vehicleDriverName);
        wbCollection.setGrossweight(CommonUtils.isValidDouble(grossWeight) ? Double.parseDouble(grossWeight) : 0);
        wbCollection.setNetweight(CommonUtils.isValidDouble(netWeight) ? Double.parseDouble(netWeight) : 0);
        wbCollection.setTareweight(CommonUtils.isValidDouble(tareWeight) ? Double.parseDouble(tareWeight) : 0);
        wbCollection.setOperatorname(operatorName);
        wbCollection.setRecieptGeneratedDate(postedDateAndTimeStr);
        if (!TextUtils.isEmpty(bunchesNumber) && bunchesNumber.length() > 0) {
            wbCollection.setTotalbunches(Integer.parseInt(bunchesNumber));
        }
        if (!TextUtils.isEmpty(rejectedBunches) && rejectedBunches.length() > 0) {
            wbCollection.setRejectedbunches(Integer.parseInt(rejectedBunches));
        }
        if (typeSelected == PRIVATE_WEIGHBRIDGE || typeSelected == NO_WEIGHBRIDGE || typeSelected == Manual_Weigh) {
            if (!CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
                int selectedPos = nameOfPrivateWbSpin.getSelectedItemPosition();
                String wbId = weighbridgeCenterDataMap.keySet().toArray(new String[weighbridgeCenterDataMap.size()])[selectedPos - 1];
                Log.v(LOG_TAG, "@@@ selected wb center id " + wbId);
                String wbName = weighbridgeCenterDataMap.get(wbId);
                Log.v(LOG_TAG, "@@@ selected wb center name " + wbName);
                wbCollection.setWeighbridgecenterid(Integer.parseInt(wbId));
            } else {
                wbCollection.setWeighbridgecenterid(null);
            }
        } else {
            wbCollection.setWeighbridgecenterid(null);
        }
        wbCollection.setRemarks(remarks);

//        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
//            wbCollection.setGradername(matchedGraderName);
//        }else{
//            wbCollection.setGradername(graderName);
//        }

        wbCollection.setGradername(graderName);
        wbCollection.setGraderCode(matchedGraderCode);

        //wbCollection.setGraderCode(matchedGraderCode);

        wbCollection.setComments(commentsEdit.getText().toString());
        wbCollection.setRecieptlocation(mCurrentPhotoPath);
        wbCollection.setRecieptextension(".jpg");

        wbCollection.setVehicleTypeId(Integer.parseInt(vehicleTypeCode));

        if (!TextUtils.isEmpty(harvestername.getText().toString())) {
            wbCollection.setName(harvestername.getText().toString());
        } else {
            wbCollection.setName("");
        }

        if (!TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
            wbCollection.setMobileNumber(harvestermobilenumber.getText().toString());
        } else {
            wbCollection.setMobileNumber("");
        }

        if (!TextUtils.isEmpty(harvestervillage.getText().toString())) {
            wbCollection.setVillage(harvestervillage.getText().toString());
        } else {
            wbCollection.setVillage("");
        }

        if (!TextUtils.isEmpty(harvestermandal.getText().toString())) {
            wbCollection.setMandal(harvestermandal.getText().toString());
        } else {
            wbCollection.setMandal("");
        }

//        if (TextUtils.isEmpty(harvestername.getText().toString())) {
//            wbCollection.setName("null");
//        }else{
//            wbCollection.setName(harvestername.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
//            wbCollection.setMobileNumber("null");
//        }else{
//            wbCollection.setMobileNumber(harvestermobilenumber.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestervillage.getText().toString())) {
//            wbCollection.setVillage("null");
//        }else{
//            wbCollection.setVillage(harvestervillage.getText().toString());
//        }
//
//        if (TextUtils.isEmpty(harvestermandal.getText().toString())) {
//            wbCollection.setMandal("null");
//        }else{
//            wbCollection.setMandal(harvestermandal.getText().toString());
//        }

        if (selectedCollectionCenter.getStateId() == 1) {

            wbCollection.setUnRipen(Integer.parseInt(unripen.getText().toString()));
            wbCollection.setUnderRipe(Integer.parseInt(underripe.getText().toString()));
            wbCollection.setRipen(Integer.parseInt(ripen.getText().toString()));
            wbCollection.setOverRipe(Integer.parseInt(overripe.getText().toString()));
            wbCollection.setDiseased(Integer.parseInt(diseased.getText().toString()));
            wbCollection.setEmptyBunches(Integer.parseInt(emptybunches.getText().toString()));
            wbCollection.setFFBQualityLong(Integer.parseInt(longstalk.getText().toString()));
            wbCollection.setFFBQualityMedium(Integer.parseInt(mediumstalk.getText().toString()));
            wbCollection.setFFBQualityShort(Integer.parseInt(shortstalk.getText().toString()));
            wbCollection.setFFBQualityOptimum(Integer.parseInt(optimum.getText().toString()));


            int isfruitavailable = 0;

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {

                isfruitavailable = 1;
            } else if (isloosefruitavailable_spinner.getSelectedItemPosition() == 2) {
                isfruitavailable = 0;
            }

            wbCollection.setLooseFruit(isfruitavailable);

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {

                wbCollection.setLooseFruitWeight(Integer.parseInt(loosefruitweight.getText().toString()));

            }
        }


        return wbCollection;
    }

    //On Activity Result for Handling Image

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                if (resultCode == RESULT_OK && typeSelected == Manual_Weigh) {
                    handleBigCameraPhoto1();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

        } // switch
    }

    //Handling Images Method

    private void handleBigCameraPhoto() {
        firstTime = false;
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();

        }

    }

    private void handleBigCameraPhoto1() {

        if (whichimage == true) {
            firstTime = true;
        } else {
            firstTime = false;
        }

        if (mCurrentPhotoPath1 != null) {
            setPic1();
            galleryAddPic1();
        }


    }

    private void setPic() {
        firstTime = false;
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = slipImage.getWidth();
        int targetH = slipImage.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        bitmap = ImageUtility.rotatePicture(90, bitmap);

        currentBitmap = bitmap;
        slipImage.setImageBitmap(bitmap);

//		/* Associate the Bitmap to the ImageView */
//        if (null != rotatedBitmap) {
//            String convertedImage = CommonUtils.getBase64String(rotatedBitmap);
//            Log.v(LOG_TAG, "@@@ converted image "+convertedImage.length());
//            slipImage.setImageBitmap(rotatedBitmap);
//            currentBitmap = rotatedBitmap;
//        } else {
//            currentBitmap = bitmap;
//            slipImage.setImageBitmap(bitmap);
//        }

        slipImage.setVisibility(View.VISIBLE);
        slipIcon.setVisibility(View.GONE);
        slipImage.invalidate();
    }

    private void setPic1() {
        if (whichimage == true) {
            firstTime = true;
        } else {
            firstTime = false;
        }
        int targetW = imageView1.getWidth();
        int targetH = imageView1.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath1, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath1, bmOptions);

        bitmap = ImageUtility1.rotatePicture(90, bitmap);
        if (firstTime) {
            cropImagePath1 = mCurrentPhotoPath1;
            android.util.Log.v(LOG_TAG, "image path 1 -> " + cropImagePath1);
            cropImagesList.add(mCurrentPhotoPath1);
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setImageBitmap(bitmap);
            imageView1.invalidate();
            firstTime = false;
        } else {
            cropImagePath2 = mCurrentPhotoPath1;
            android.util.Log.v(LOG_TAG, "image path 2 -> " + cropImagePath2);
            cropImagesList.add(mCurrentPhotoPath1);
            imageView2.setVisibility(View.VISIBLE);
            imageView2.setImageBitmap(bitmap);
            imageView2.invalidate();


        }
    }


    private void galleryAddPic() {
        firstTime = false;
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void galleryAddPic1() {
        if (whichimage == true) {
            firstTime = true;
        } else {
            firstTime = false;
        }
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath1);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    //Saving Images
    private void saveObservationImages() {
        android.util.Log.v(LOG_TAG, "cropImagePath1 -> " + cropImagePath1);
        android.util.Log.v(LOG_TAG, "cropImagePath2 -> " + cropImagePath2);

        DataManager.getInstance().deleteData(DataManager.Manual_Images);
        BasicFarmerDetails basicFarmerDetails;
        basicFarmerDetails = (BasicFarmerDetails) DataManager.getInstance().getDataFromManager(SELECTED_FARMER_DATA);
        if (isValidation()) {
            oftObservationTakenImagesList = new ArrayList<>();
            FileRepository1 oftObservationTakenImages = new FileRepository1();
            FileRepository1 oftObservationTakenImages1 = new FileRepository1();


            if (cropImagePath1 != null) {
                if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
                    oftObservationTakenImages.setFileName(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));
                    oftObservationTakenImages.setConsignmentCode(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));
                } else {
                    oftObservationTakenImages.setFileName(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));
                    oftObservationTakenImages.setConsignmentCode(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));

                }
                //  oftObservationTakenImages.setFarmerCode(basicFarmerDetails.getFarmerCode().trim());
                oftObservationTakenImages.setModuleId(195);
                oftObservationTakenImages.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                oftObservationTakenImages.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                oftObservationTakenImages.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                oftObservationTakenImages.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));

                oftObservationTakenImages.setServerUpdatedStatus(false);
                oftObservationTakenImages.setFileLocation(cropImagePath1);
                oftObservationTakenImages.setFileExtension(".jpg");
                oftObservationTakenImages.setActive(true);
                oftObservationTakenImagesList.add(oftObservationTakenImages);
            }
            if (cropImagePath2 != null) {
                if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
                    oftObservationTakenImages1.setFileName(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));

                    oftObservationTakenImages1.setConsignmentCode(ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days));
                } else {
                    oftObservationTakenImages1.setFileName(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));

                    oftObservationTakenImages1.setConsignmentCode(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days));

                }
                //  oftObservationTakenImages.setFarmerCode(basicFarmerDetails.getFarmerCode().trim());
                oftObservationTakenImages1.setModuleId(195);
                oftObservationTakenImages1.setActive(true);

                oftObservationTakenImages1.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
                oftObservationTakenImages1.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                oftObservationTakenImages1.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                oftObservationTakenImages1.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));

                oftObservationTakenImages1.setServerUpdatedStatus(false);
                oftObservationTakenImages1.setFileLocation(cropImagePath2);
                oftObservationTakenImages1.setFileExtension(".jpg");

                oftObservationTakenImagesList.add(oftObservationTakenImages1);
            }

            DataManager.getInstance().addData(DataManager.Manual_Images, oftObservationTakenImagesList);
            DataSavingHelper.saveOFTObservationTakenImages(getActivity(), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveObservationImages", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        DataManager.getInstance().addData(DataManager.Manual_Images, "DataSaved");
                        Toast.makeText(getActivity(), "Data saving ", Toast.LENGTH_LONG).show();

                        getActivity().onBackPressed();
                    } else {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveObservationImages", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        Toast.makeText(getActivity(), "Data saving failed", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    //Validating Double Fields
    public boolean validateDoubles() {
        if (!TextUtils.isEmpty(grossWeight) && !grossWeight.equalsIgnoreCase(".") && !TextUtils.isEmpty(tareWeight) && !tareWeight.equalsIgnoreCase(".")) {
            try {
                GrossWeightD = Double.parseDouble(grossWeight);
                TareWeightD = Double.parseDouble(tareWeight);
                if (GrossWeightD >= TareWeightD) {
                    getNetWeight(GrossWeightD, TareWeightD);
                } else {
                    UiUtils.showCustomToastMessage("Tare weight  should not exceed gross weight", getActivity(), 1);
                    return false;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(grossWeight) && !grossWeight.equalsIgnoreCase(".")) {
            try {
                GrossWeightD = Double.parseDouble(grossWeight);
                getNetWeight(GrossWeightD, 0.0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(tareWeight) && !tareWeight.equalsIgnoreCase(".")) {
            try {
                TareWeightD = Double.parseDouble(tareWeight);
                getNetWeight(TareWeightD, 0.0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        }
        return true;
    }

    public double getNetWeight(final Double plotAreaDouble, final Double totalAreaDouble) {
        double diff = plotAreaDouble - totalAreaDouble;
        netWeightTxt.setVisibility(View.VISIBLE);
        netWeightTxt.setText("" + diff);
        return diff;
    }

    //Binding Fields Data to Strings
    public void injectData() {
        vehicleNumber = vehicle_no.getText().toString();
        vehicleDriverName = vehicleDriver.getText().toString();
        grossWeight = grossWt.getText().toString();
        tareWeight = tareWt.getText().toString();
        if (netWeightTxt.getVisibility() == View.VISIBLE) {
            netWeight = netWeightTxt.getText().toString();
        } else {
            netWeight = netWt.getText().toString();
        }
        operatorName = name_of_operator.getText().toString();
        bunchesNumber = no_of_bunches.getText().toString();
        rejectedBunches = no_of_bunches_rejected.getText().toString();
        remarks = rmrks.getText().toString();
        graderName = name_of_grader.getText().toString();
//        dateAndTimeStr = dateTimeEdit.getText().toString();
//        postedDateAndTimeStr = postDateTimeEdit.getText().toString();
    }

    //Validation Based on Weighbridge Type

    //Validation for Private WB Case
    public boolean validationsInPrivateWbCase() {

        if (CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
            UiUtils.showCustomToastMessage("Please select weighbridge center", getActivity(), 1);
            return false;
        }

        if (TextUtils.isEmpty(dateAndTimeStr)) {
            UiUtils.showCustomToastMessage("Please select date and time", getActivity(), 1);
            return false;
        }

        return validationsInOwWbCase(true);
    }

    //Validation for No WB Case
    public boolean validationsInNoWbCase() {


        if (TextUtils.isEmpty(dateAndTimeStr)) {
            UiUtils.showCustomToastMessage("Please select date and time", getActivity(), 1);
            return false;
        }
        if (CommonUtils.isEmptySpinner(vehicleCategoryspin)) {

            UiUtils.showCustomToastMessage("Please select Vehicle Category", getActivity(), 1);
            return false;
        }
        if (CommonUtils.isEmptySpinner(vehicleTypespin)) {

            UiUtils.showCustomToastMessage("Please select Vehicle Type", getActivity(), 1);
            return false;
        }

        if (vehicleCategoryspin.getSelectedItemPosition() != 1) {
            if (TextUtils.isEmpty(vehicleNumber)) {
                UiUtils.showCustomToastMessage("Enter Vehicle Number", getActivity(), 1);
                return false;
            }
        }

        if (TextUtils.isEmpty(vehicleDriverName)) {
            UiUtils.showCustomToastMessage("Enter Vehicle Driver Name", getActivity(), 1);
            return false;
        }

        if (TextUtils.isEmpty(netWeight)) {
            UiUtils.showCustomToastMessage("Enter Net Weight", getActivity(), 1);
            return false;
        }
        if (TextUtils.isEmpty(operatorName)) {
            UiUtils.showCustomToastMessage("Enter Operator Name", getActivity(), 1);
            return false;
        }
        if (currentBitmap == null) {
            UiUtils.showCustomToastMessage("Please Capture Photo", getActivity(), 1);
            return false;
        }

        if (TextUtils.isEmpty(postedDateAndTimeStr)) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
            postedDateAndTimeStr = objdateformat.format(c.getTime());

        }

//        if (TextUtils.isEmpty(harvestername.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Name", getActivity(), 1);
//            return false;
//        }
//
//        if (TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Mobile Number", getActivity(), 1);
//            return false;
//        }
//
//        if (harvestermobilenumber.getText().toString().length() < 10) {
//            UiUtils.showCustomToastMessage("Please Enter Proper Mobile Number", getActivity(), 0);
//            return false;
//        }
//
//        if (TextUtils.isEmpty(harvestervillage.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Village", getActivity(), 1);
//            return false;
//        }
//        if (TextUtils.isEmpty(harvestermandal.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Mandal", getActivity(), 1);
//            return false;
//        }

        if (!TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
            if (harvestermobilenumber.getText().toString().length() < 10) {
                UiUtils.showCustomToastMessage("Please Enter Proper Mobile Number", getActivity(), 0);
                return false;
            }
        }

        if (selectedCollectionCenter.getStateId() == 1) {

            if (TextUtils.isEmpty(unripen.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Unripen", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(underripe.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Underripe", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(ripen.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Ripen", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(overripe.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Overripe", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(diseased.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Diseased", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(emptybunches.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Empty Bunches", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(longstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Long Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(mediumstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Medium Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(shortstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Short Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(optimum.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Optimum Stock Quality", getActivity(), 0);
                return false;
            }

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 0) {
                UiUtils.showCustomToastMessage("Please Select Is Loose Fruit Available", getActivity(), 0);
                return false;
            }

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {
                if (TextUtils.isEmpty(loosefruitweight.getText().toString())) {
                    UiUtils.showCustomToastMessage("Please Enter Loose Fruit Weight", getActivity(), 0);
                    return false;
                }
            }

            if ((Double.parseDouble(unripen.getText().toString()) + Double.parseDouble(underripe.getText().toString()) + Double.parseDouble(ripen.getText().toString()) + Double.parseDouble(overripe.getText().toString()) + Double.parseDouble(diseased.getText().toString()) + Double.parseDouble(emptybunches.getText().toString())) != 100) {
                UiUtils.showCustomToastMessage("FFB Bunch Quality should be equal to 100%", getActivity(), 0);
                return false;
            }

            if ((Double.parseDouble(longstalk.getText().toString()) + Double.parseDouble(mediumstalk.getText().toString()) + Double.parseDouble(shortstalk.getText().toString()) + Double.parseDouble(optimum.getText().toString())) != 100) {
                UiUtils.showCustomToastMessage("FFB Stalk Quality should be equal to 100%", getActivity(), 0);
                return false;
            }
        }

        if (TextUtils.isEmpty(name_of_grader.getText().toString())) {
            if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
                UiUtils.showCustomToastMessage("Please Take Fingerprint", getActivity(), 0);
                return false;
            }else{
                UiUtils.showCustomToastMessage("Please Enter Grader Name", getActivity(), 0);
                return false;
            }
        }

        return true;
    }

    //Validation for Own WB Case
    public boolean validationsInOwWbCase(boolean applyValidation) {
//

        if (CommonUtils.isEmptySpinner(vehicleCategoryspin)) {

            UiUtils.showCustomToastMessage("Please select Vehicle Category", getActivity(), 1);
            return false;
        }
        if (CommonUtils.isEmptySpinner(vehicleTypespin)) {

            UiUtils.showCustomToastMessage("Please select Vehicle Type", getActivity(), 1);
            return false;
        }

        if (vehicleCategoryspin.getSelectedItemPosition() != 1) {
            if (TextUtils.isEmpty(vehicleNumber)) {
                UiUtils.showCustomToastMessage("Enter Vehicle Number", getActivity(), 1);
                return false;
            }
        }

        if (TextUtils.isEmpty(vehicleDriverName)) {
            UiUtils.showCustomToastMessage("Enter Vehicle Driver Name", getActivity(), 1);
            return false;
        }
        if (applyValidation && TextUtils.isEmpty(grossWeight)) {
            UiUtils.showCustomToastMessage("Enter Gross Weight", getActivity(), 1);
            return false;
        }
        if (applyValidation && TextUtils.isEmpty(tareWeight)) {
            UiUtils.showCustomToastMessage("Enter Tare Weight", getActivity(), 1);
            return false;
        }
        if (TextUtils.isEmpty(netWeight)) {
            UiUtils.showCustomToastMessage("Enter Net Weight", getActivity(), 1);
            return false;
        }
        if (TextUtils.isEmpty(operatorName)) {
            UiUtils.showCustomToastMessage("Enter Operator Name", getActivity(), 1);
            return false;
        }
        if (applyValidation && currentBitmap == null) {
            UiUtils.showCustomToastMessage("Please Capture Photo", getActivity(), 1);
            return false;
        }

        if (TextUtils.isEmpty(postedDateAndTimeStr)) {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
            postedDateAndTimeStr = objdateformat.format(c.getTime());

        }
//
//        if (TextUtils.isEmpty(harvestername.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Name", getActivity(), 1);
//            return false;
//        }
//
//        if (TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Mobile Number", getActivity(), 1);
//            return false;
//        }
//
//        if (harvestermobilenumber.getText().toString().length() < 10) {
//            UiUtils.showCustomToastMessage("Please Enter Proper Mobile Number", getActivity(), 0);
//            return false;
//        }
//
//        if (TextUtils.isEmpty(harvestervillage.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Village", getActivity(), 1);
//            return false;
//        }
//        if (TextUtils.isEmpty(harvestermandal.getText().toString())) {
//            UiUtils.showCustomToastMessage("Enter Harvester Mandal", getActivity(), 1);
//            return false;
//        }

        if (!TextUtils.isEmpty(harvestermobilenumber.getText().toString())) {
            if (harvestermobilenumber.getText().toString().length() < 10) {
                UiUtils.showCustomToastMessage("Please Enter Proper Mobile Number", getActivity(), 0);
                return false;
            }
        }

        ///////////////////

        if (selectedCollectionCenter.getStateId() == 1) {
            if (TextUtils.isEmpty(unripen.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Unripen", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(underripe.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Underripe", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(ripen.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Ripen", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(overripe.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Overripe", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(diseased.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Diseased", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(emptybunches.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Empty Bunches", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(longstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Long Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(mediumstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Medium Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(shortstalk.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Short Stock Quality", getActivity(), 0);
                return false;
            }
            if (TextUtils.isEmpty(optimum.getText().toString())) {
                UiUtils.showCustomToastMessage("Please Enter Optimum Stock Quality", getActivity(), 0);
                return false;
            }

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 0) {
                UiUtils.showCustomToastMessage("Please Select Is Loose Fruit Available", getActivity(), 0);
                return false;
            }

            if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1) {
                if (TextUtils.isEmpty(loosefruitweight.getText().toString())) {
                    UiUtils.showCustomToastMessage("Please Enter Loose Fruit Weight", getActivity(), 0);
                    return false;
                }
            }

            if ((Double.parseDouble(unripen.getText().toString()) + Double.parseDouble(underripe.getText().toString()) + Double.parseDouble(ripen.getText().toString()) + Double.parseDouble(overripe.getText().toString()) + Double.parseDouble(diseased.getText().toString()) + Double.parseDouble(emptybunches.getText().toString())) != 100) {
                UiUtils.showCustomToastMessage("FFB Bunch Quality should be equal to 100%", getActivity(), 0);
                return false;
            }

            if ((Double.parseDouble(longstalk.getText().toString()) + Double.parseDouble(mediumstalk.getText().toString()) + Double.parseDouble(shortstalk.getText().toString()) + Double.parseDouble(optimum.getText().toString())) != 100) {
                UiUtils.showCustomToastMessage("FFB Stalk Quality should be equal to 100%", getActivity(), 0);
                return false;
            }

        }

        if (TextUtils.isEmpty(name_of_grader.getText().toString())) {
            if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
                UiUtils.showCustomToastMessage("Please Take Fingerprint", getActivity(), 0);
                return false;
            }else{
                UiUtils.showCustomToastMessage("Please Enter Grader Name", getActivity(), 0);
                return false;
            }
        }

        return true;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    //Loads Image on Resume
    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "@@@ check on resume called");
        if (!TextUtils.isEmpty(mCurrentPhotoPath) && !TextUtils.isEmpty(collectionId) && null != slipImage && typeSelected != Manual_Weigh) {
            loadImageFromStorage(mCurrentPhotoPath);
            slipImage.invalidate();
        }

//        //sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));
//        long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
////        UsbDevice usbDevice = sgfplib.GetUsbDevice();
////        long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
//
//        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
//
//            getContext().registerReceiver(mUsbReceiver, filter);
//            error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
//            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
//                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
//                if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
//                    dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
//                else
//                    dlgAlert.setMessage("Fingerprint device initialization failed!");
//                dlgAlert.setTitle("SecuGen Fingerprint SDK");
//                dlgAlert.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int whichButton){
//                                 getActivity().finish();
//                                return;
//                            }
//                        }
//                );
//                dlgAlert.setCancelable(false);
//                dlgAlert.create().show();
//            }
//            else {
//                UsbDevice usbDevice = sgfplib.GetUsbDevice();
//                if (usbDevice == null) {
//                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
//                    dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
//                    dlgAlert.setTitle("SecuGen Fingerprint SDK");
//                    dlgAlert.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    getActivity().finish();
//                                    return;
//                                }
//                            }
//                    );
//                    dlgAlert.setCancelable(false);
//                    dlgAlert.create().show();
//                } else {
//                    boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                    if (!hasPermission) {
//                        if (!usbPermissionRequested) {
//                            //Log.d(TAG, "Call GetUsbManager().requestPermission()");
//                            usbPermissionRequested = true;
//                            sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
//                        } else {
//                            //wait up to 20 seconds for the system to grant USB permission
//                            hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                            int i = 0;
//                            while ((hasPermission == false) && (i <= 40)) {
//                                ++i;
//                                hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                                try {
//                                    Thread.sleep(500);
//                                    sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
//                            }
//                        }
//                    }
//                    if (hasPermission) {
//                        error = sgfplib.OpenDevice(0);
//                        Toast.makeText(getContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();
//                        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
//                            bSecuGenDeviceOpened = true;
//                            SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
//                            error = sgfplib.GetDeviceInfo(deviceInfo);
//                            mImageWidth = deviceInfo.imageWidth;
//                            mImageHeight = deviceInfo.imageHeight;
//                            mImageDPI = deviceInfo.imageDPI;
//                            sgfplib.SetLedOn(true);
//                            autoOn.start();
//
//                            error = sgfplib.FakeDetectionCheckEngineStatus(mFakeEngineReady);
//                            if (mFakeEngineReady[0]) {
//                                error = sgfplib.FakeDetectionGetNumberOfThresholds(mNumFakeThresholds);
//                                if (error != SGFDxErrorCode.SGFDX_ERROR_NONE)
//                                    mNumFakeThresholds[0] = 1; //0=Off, 1=TouchChip
//
//                                error = sgfplib.FakeDetectionGetDefaultThreshold(mDefaultFakeThreshold);
//                                mFakeDetectionLevel = mDefaultFakeThreshold[0];
//
//                                //error = this.sgfplib.SetFakeDetectionLevel(mFakeDetectionLevel);
//                                //debugMessage("Ret[" + error + "] Set Fake Threshold: " + mFakeDetectionLevel + "\n");
//
//
//                                double[] thresholdValue = new double[1];
//                                error = sgfplib.FakeDetectionGetThresholdValue(thresholdValue);
//                            } else {
//                                mNumFakeThresholds[0] = 1;        //0=Off, 1=Touch Chip
//                                mDefaultFakeThreshold[0] = 1;    //Touch Chip Enabled
//                            }
//
//                            sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
//                            sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
//                            mVerifyTemplate = new byte[(int) mMaxTemplateSize[0]];
//                            boolean smartCaptureEnabled = true;
//                            if (smartCaptureEnabled)
//                                sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 1);
//                            else
//                                sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 0);
//                            if (mAutoOnEnabled) {
//                                //sgfplib.SetLedOn(true);
//                                autoOn.start();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//
//        }
//        //Thread thread = new Thread(this);
//        //thread.start();
//        //android.util.Log.d("TAG", "Exit onResume()");
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
            getContext().unregisterReceiver(mUsbReceiver);
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

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            b = ImageUtility.rotatePicture(90, b);
            slipImage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Validation for Bunches Fields
    public boolean validateBunchValues() {
        if (!TextUtils.isEmpty(rejectedBunches) && !rejectedBunches.equalsIgnoreCase(".") && !TextUtils.isEmpty(totalBunches) && !totalBunches.equalsIgnoreCase(".")) {
            try {
                rejectedBunches_converted = Integer.parseInt(rejectedBunches);
                totalBunches_converted = Integer.parseInt(totalBunches);
                if (totalBunches_converted >= rejectedBunches_converted) {
                    getAreaLeft(totalBunches_converted, rejectedBunches_converted);
                } else {
                    UiUtils.showCustomToastMessage("Rejected bunches should not exceed total bunches", getActivity(), 1);
                    return false;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        } else if (!TextUtils.isEmpty(rejectedBunches) && !TextUtils.isEmpty(totalBunches) && !totalBunches.equalsIgnoreCase(".")) {
            Log.v(LOG_TAG, "@@@ check 1 " + totalBunches);
            totalBunches_converted = Integer.parseInt(totalBunches);
            getAreaLeft(totalBunches_converted, 0);
        }
        return true;
    }

    public double getAreaLeft(final int plotAreaDouble, final int totalAreaDouble) {
        double diff = plotAreaDouble - totalAreaDouble;
        number_of_bunches_accepted.setText("" + diff);
        return diff;
    }

    //Removing Mandatory mark based on Other Fields
    public void removeMandatoryFields() {
        TextView nop_wb_txt = (TextView) rootView.findViewById(R.id.nop_wb_txt);
        TextView dop_wb_txt = (TextView) rootView.findViewById(R.id.dop_wb_txt);
        TextView gross_weight_title = (TextView) rootView.findViewById(R.id.gross_weight_title);
        TextView tare_weight_title = (TextView) rootView.findViewById(R.id.tare_weight_title);
        TextView net_weight_title = (TextView) rootView.findViewById(R.id.net_weight_title);
        TextView weighbridge_slip_txt = (TextView) rootView.findViewById(R.id.weighbridge_slip_txt);

        String nop_wb_Str = nop_wb_txt.getText().toString().replace("*", "");
        String dop_wb_Str = dop_wb_txt.getText().toString().replace("*", "");
        String gross_weight_title_Str = gross_weight_title.getText().toString().replace("*", "");
        String tare_weight_title_Str = tare_weight_title.getText().toString().replace("*", "");
        String net_weight_title_Str = net_weight_title.getText().toString().replace("*", "");
        String weighbridge_slip_Str = weighbridge_slip_txt.getText().toString().replace("*", "");
        nop_wb_txt.setText(nop_wb_Str);
        dop_wb_txt.setText(dop_wb_Str);
        gross_weight_title.setText(gross_weight_title_Str);
        tare_weight_title.setText(tare_weight_title_Str);
        net_weight_title.setText(net_weight_title_Str);
        weighbridge_slip_txt.setText(weighbridge_slip_Str);
    }

//Imageview Methods
    private File createImageFile() throws IOException {
        firstTime = false;
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "CollectionPhotos");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, collectionId + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    private File createImageFile1() throws IOException {
        firstTime = true;
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "consignmentPhotos");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, collectionId + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    private File createImageFile2() throws IOException {
        firstTime = false;
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "consignmentPhotoss");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }

        File finalFile = new File(pictureDirectory, collectionId + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    private File setUpPhotoFile() throws IOException {
        firstTime = false;
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File setUpPhotoFile1() throws IOException {

        if (whichimage == true){
            firstTime = true;
        }else{
            firstTime = false;
        }

        if (firstTime == true) {
            firstTime = true;
            File f = createImageFile1();
            mCurrentPhotoPath1 = f.getAbsolutePath();
            return f;
        }else{
            firstTime = false;
            File f = createImageFile2();
            mCurrentPhotoPath1 = f.getAbsolutePath();
            return f;
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        firstTime = false;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }

    private void dispatchTakePictureIntent1(int actionCode) {
        if (whichimage == true){
            firstTime = true;
        }else{
            firstTime = false;
        }

        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case CAMERA_REQUEST:
                File f = null;
                mCurrentPhotoPath1 = null;
                try {
                    f = setUpPhotoFile1();
                    mCurrentPhotoPath1 = f.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                            BuildConfig.APPLICATION_ID + ".provider", f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                } catch (IOException e) {
                    android.util.Log.v(LOG_TAG, "IOException " + e.getMessage());
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath1 = null;
                }

                break;

            default:
                break;
        } // switch
        android.util.Log.v(LOG_TAG, "dispatchTakePictureIntent ");
        startActivityForResult(takePictureIntent, actionCode);
    }

//Validation for ImageView
    private boolean isValidation() {
        if (TextUtils.isEmpty(cropImagePath1)) {
            Toast.makeText(getActivity(), "Please take FFB Weighment Register Picture", Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(cropImagePath2)) {
            Toast.makeText(getActivity(), "Please take FFB collected quantity picture", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void SGFingerPresentCallback() {
        autoOn.stop();
        //sgfplib.SetLedOn(true);
        Log.d("Fingerprint", "taken");
        VerifyFingerPrint();
    }//Added by Arun dated 21st June

//    public void onResume(){
//        Log.d("TAG", "Enter onResume()");
//        super.onResume();
//
//    }

    @SuppressLint("SuspiciousIndentation")
    public void VerifyFingerPrint() {
        long dwTimeStart = 0, dwTimeEnd = 0, dwTimeElapsed = 0;
        sgfplib.SetLedOn(false);
        mVerifyImage = new byte[mImageWidth*mImageHeight];
        dwTimeStart = System.currentTimeMillis();
        long result = sgfplib.GetImageEx(mVerifyImage, IMAGE_CAPTURE_TIMEOUT_MS,IMAGE_CAPTURE_QUALITY);
        dwTimeEnd = System.currentTimeMillis();
        dwTimeElapsed = dwTimeEnd-dwTimeStart;
        //mImageViewFingerprint.setImageBitmap(this.toGrayscale(mVerifyImage));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_takefingerprint.setImageBitmap(toGrayscale(mVerifyImage));
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
                        //name_of_grader.setText(matchedGraderName);
                        break; // Exit the loop since a match is found
                    }

                }

                if (fingerprintMatched) {
                    break; // Exit the outer loop since a match is found
                }
            }

            if (fingerprintMatched) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name_of_grader.setText(matchedGraderName);
                        btn_takefingerprint.setFocusable(false);
                        btn_takefingerprint.setClickable(false);
                        Toast.makeText(getContext(), "Matched", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!fingerprintDataList.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            autoOn.start();
                            Toast.makeText(getContext(), "Not Matched", Toast.LENGTH_SHORT).show();
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
}