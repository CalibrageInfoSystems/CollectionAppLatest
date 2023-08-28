package com.oilpalm3f.mainapp.collectioncenter;

import static android.support.v4.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alcorlink.alcamsdk.SecuGenDevice;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.FarmerPlotDetailsAdapter;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.NewWeighbridgeCC;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.GraderDetails;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.ui.SplashScreen;
import com.oilpalm3f.mainapp.uihelper.CircleImageView;
import com.oilpalm3f.mainapp.utils.UiUtils;
import com.oilpalm3f.mainapp.viewfarmers.FarmersListScreenForCC;
import com.oilpalm3f.mainapp.weighbridge.AutoWeighFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.oilpalm3f.mainapp.common.CommonConstants.collectionType;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.EXTRA_PLOTS;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.EXTRA_SELECTED_FARMER_PLOT_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_PLOT_DATA;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;

public class FarmersDetailsScreen extends OilPalmBaseActivity implements FarmerPlotDetailsAdapter.ClickListener, SGFingerPresentEvent {



    private static final String LOG_TAG = FarmersDetailsScreen.class.getName();

    private BasicFarmerDetails basicFarmerDetails;
    private android.widget.TextView farmerNameTxt;
    private TextView tvfathername, tvvillagename, tvcontactnum, tvaddress, selectedPlotsTxt;
    private RecyclerView rvplotlist;
    private List<PlotDetailsObj> plotdetailslistObj = new ArrayList<>();
    private FarmerPlotDetailsAdapter farmerplotDetailsLVAdapter;
    private CCDataAccessHandler ccDataAccessHandler = null;
    private DataAccessHandler dataAccessHandler;
    private Button nextBtn, addFarmersBtn;
    private CircleImageView userImage;
    private LinearLayout selectedPlotsLayout;
    private String CollectionFarmerCodeWithOutPlot;
    public static String firstthree = "";
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

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
    private CollectionCenter selectedCollectionCenter;
    ArrayList<GraderDetails> graderDetails = new ArrayList<>();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
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
//    }; //Added by Arun dated 21st June

    //Initializing the Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.content_farmers_details_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.farmer_details));

       //if (CommonConstants.isComingfrom == "Home"){
            basicFarmerDetails = (BasicFarmerDetails) DataManager.getInstance().getDataFromManager(SELECTED_FARMER_DATA);
            ccDataAccessHandler = new CCDataAccessHandler(FarmersDetailsScreen.this);
            dataAccessHandler = new DataAccessHandler(FarmersDetailsScreen.this);

//        sgfplib = new JSGFPLib(FarmersDetailsScreen.this, (UsbManager) this.getSystemService(Context.USB_SERVICE));
//
//        //USB Permissions
//        mPermissionIntent = PendingIntent.getBroadcast(FarmersDetailsScreen.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        filter = new IntentFilter(ACTION_USB_PERMISSION);
//        usbPermissionRequested = false;
//        bSecuGenDeviceOpened = false;
//        mMaxTemplateSize = new int[1];
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);

        graderDetails = dataAccessHandler.getGraderdetails(Queries.getInstance().getGraderDetails(selectedCollectionCenter.getCode()));




        initView();
            bindDateToUi();
            bindPlotData();
       // }

//       else{
//            Intent intent = new Intent(this, SplashScreen.class);
//            startActivity(intent);
//        }
    }

    //Naviagation Method based on Collection Center Type

    public void letsGoToCollectionCenter() {
        if (CommonConstants.CollectionType.equalsIgnoreCase("Auto")) {
            replaceFragment1(new NewWeighbridgeCC());

        }   else {
//            replaceFragment(new NewWeighbridgeCC());
            replaceFragment(new WeighbridgeCC());

        }


    }

    //Initializing Fragment
    public void replaceFragment1(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
        );
        mFragmentTransaction.replace(android.R.id.content, fragment);
        mFragmentTransaction.addToBackStack(backStateName);
        mFragmentTransaction.commit();
    }


    //Initializing UI
    private void initView() {
        farmerNameTxt = (TextView) findViewById(R.id.farmerNameTxt);
        tvfathername = (TextView) findViewById(R.id.tvfathername);
        tvvillagename = (TextView) findViewById(R.id.tvvillagename);
        tvcontactnum = (TextView) findViewById(R.id.tvcontactnumber);
        tvaddress = (TextView) findViewById(R.id.tvaddress);
        rvplotlist = (RecyclerView) findViewById(R.id.lv_farmerplotdetails);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        userImage = (CircleImageView) findViewById(R.id.profile_pic);
        addFarmersBtn = (Button) findViewById(R.id.addFarmersBtn);
        selectedPlotsLayout = (LinearLayout) findViewById(R.id.selectedPlotsLayout);
        selectedPlotsTxt = (TextView) findViewById(R.id.selectedPlotsTxt);
        CollectionFarmerCodeWithOutPlot = basicFarmerDetails.getFarmerCode().trim();
        firstthree = CollectionFarmerCodeWithOutPlot.substring(0, 9);
//        if ((basicFarmerDetails.getFarmerCode().contains("DUMMY")) || (firstthree.equalsIgnoreCase("CCFARMERW"))
//                || (firstthree.equalsIgnoreCase("FFBFARMER"))) {
//            addFarmersBtn.setVisibility(View.GONE);
//        } else {
//            addFarmersBtn.setVisibility(View.VISIBLE);
//        }

//Next Button On Click Listener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.d("ProceedIsFingerPrintReq", CommonConstants.IsFingerPrintReq + "");
//                Log.d("ProceedGraderSize", graderDetails.size() + "");
//
//                if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0){
//                    sgfplib = new JSGFPLib(FarmersDetailsScreen.this, (UsbManager) getSystemService(Context.USB_SERVICE));
//                    registerReceiver(mUsbReceiver, filter);
//                    long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
//                    if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
//                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(FarmersDetailsScreen.this);
//                        if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
//                            dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
//                        else
//                            dlgAlert.setMessage("Fingerprint device initialization failed!");
//                        dlgAlert.setTitle("SecuGen Fingerprint SDK");
//                        dlgAlert.setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,int whichButton){
//                                        finish();
//                                        return;
//                                    }
//                                }
//                        );
//                        dlgAlert.setCancelable(false);
//                        dlgAlert.create().show();
//                    }
//                    else {
//                        UsbDevice usbDevice = sgfplib.GetUsbDevice();
//                        if (usbDevice == null) {
//                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(FarmersDetailsScreen.this);
//                            dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
//                            dlgAlert.setTitle("SecuGen Fingerprint SDK");
//                            dlgAlert.setPositiveButton("OK",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int whichButton) {
//                                            finish();
//                                            return;
//                                        }
//                                    }
//                            );
//                            dlgAlert.setCancelable(false);
//                            dlgAlert.create().show();
//                        } else {
//                            boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                            if (!hasPermission) {
//                                if (!usbPermissionRequested) {
//                                    //Log.d(TAG, "Call GetUsbManager().requestPermission()");
//                                    usbPermissionRequested = true;
//                                    sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
//                                } else {
//                                    //wait up to 20 seconds for the system to grant USB permission
//                                    hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                                    int i = 0;
//                                    while ((hasPermission == false) && (i <= 40)) {
//                                        ++i;
//                                        hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
//                                        try {
//                                            Thread.sleep(500);
//                                            sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
//                                    }
//                                }
//                            }
//                            if (hasPermission) {
//                                // usbDevice = sgfplib.GetUsbDevice();
//                                sgfplib = new JSGFPLib(FarmersDetailsScreen.this, (UsbManager) getSystemService(Context.USB_SERVICE));
//                                error = sgfplib.Init(0);
//                                error = sgfplib.OpenDevice(0);
//                                if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
//                                    bSecuGenDeviceOpened = true;
//                                    SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
//                                    error = sgfplib.GetDeviceInfo(deviceInfo);
//                                    mImageWidth = deviceInfo.imageWidth;
//                                    mImageHeight = deviceInfo.imageHeight;
//                                    mImageDPI = deviceInfo.imageDPI;
//                                    sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
//                                    sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
//
//                                    if (firstthree.equalsIgnoreCase("CCFARMERW") || (firstthree.equalsIgnoreCase("FFBFARMER"))) {
//                                        Log.v("@@@HH", "HHH");
//
//                                        if (collectionType.equalsIgnoreCase(CollectionCenterHomeScreen.NEW_COLLECTION)) {
//                                            letsGoToCollectionCenter();
//                                        } else {
////                        letsGoToConsignmentScreen();
//                                        }
//                                    } else {
//
//                                        if (farmerplotDetailsLVAdapter != null && farmerplotDetailsLVAdapter.getSelectedItemCount() > 0) {
//                                            Log.v(LOG_TAG, "@@@ let's go next " + farmerplotDetailsLVAdapter.getSelectedItemCount());
//                                            List plotCodes = new ArrayList();
//                                            List<Integer> selectedPos = farmerplotDetailsLVAdapter.getSelectedItems();
//                                            for (int i = 0; i < selectedPos.size(); i++) {
//                                                Log.v(LOG_TAG, "@@@ let's go next " + selectedPos.get(i));
//                                                plotCodes.add(plotdetailslistObj.get(selectedPos.get(i)).getPlotID());
//                                            }
//                                            if (getIntent().getAction().equalsIgnoreCase("Main Farmer")) {
//                                                DataManager.getInstance().addData(SELECTED_FARMER_PLOT_DATA, plotCodes);
//                                            } else {
//                                                DataManager.getInstance().addData(EXTRA_SELECTED_FARMER_PLOT_DATA, plotCodes);
//                                            }
//
//                                            if (collectionType.equalsIgnoreCase(CollectionCenterHomeScreen.NEW_COLLECTION)) {
//                                                letsGoToCollectionCenter();
//                                            } else {
////                        letsGoToConsignmentScreen();
//                                            }
//
//                                        } else {
//                                            UiUtils.showCustomToastMessage("Please select plot to proceed", FarmersDetailsScreen.this, 1);
//                                        }
//
//                                    }
//
//                                } else {
//                                    Toast.makeText(FarmersDetailsScreen.this, "Please Re-connect the Fingerprint Device", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//                    }
//
//                }
//                else{


                    if (firstthree.equalsIgnoreCase("CCFARMERW") || (firstthree.equalsIgnoreCase("FFBFARMER"))) {
                        Log.v("@@@HH", "HHH");

                        if (collectionType.equalsIgnoreCase(CollectionCenterHomeScreen.NEW_COLLECTION)) {
                            letsGoToCollectionCenter();
                        } else {
//                        letsGoToConsignmentScreen();
                        }
                    } else {

                        if (farmerplotDetailsLVAdapter != null && farmerplotDetailsLVAdapter.getSelectedItemCount() > 0) {
                            Log.v(LOG_TAG, "@@@ let's go next " + farmerplotDetailsLVAdapter.getSelectedItemCount());
                            List plotCodes = new ArrayList();
                            List<Integer> selectedPos = farmerplotDetailsLVAdapter.getSelectedItems();
                            for (int i = 0; i < selectedPos.size(); i++) {
                                Log.v(LOG_TAG, "@@@ let's go next " + selectedPos.get(i));
                                plotCodes.add(plotdetailslistObj.get(selectedPos.get(i)).getPlotID());
                            }
                            if (getIntent().getAction().equalsIgnoreCase("Main Farmer")) {
                                DataManager.getInstance().addData(SELECTED_FARMER_PLOT_DATA, plotCodes);
                            } else {
                                DataManager.getInstance().addData(EXTRA_SELECTED_FARMER_PLOT_DATA, plotCodes);
                            }

                            if (collectionType.equalsIgnoreCase(CollectionCenterHomeScreen.NEW_COLLECTION)) {
                                letsGoToCollectionCenter();
                            } else {
//                        letsGoToConsignmentScreen();
                            }

                        } else {
                            UiUtils.showCustomToastMessage("Please select plot to proceed", FarmersDetailsScreen.this, 1);
                        }

                    }

               // }


            }
        });

        //Add Farmer Button On Click Listener

        addFarmersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(FarmersDetailsScreen.this, FarmersListScreenForCC.class).setAction("Add Farmers"), 100);
            }
        });
    }

       // @Override
//    public void onPause() {
//        super.onPause();
//        android.util.Log.d("TAG", "Enter onPause()");
//
//        if (CommonConstants.IsFingerPrintReq == true && graderDetails.size() > 0) {
//
//            if (bSecuGenDeviceOpened) {
//                autoOn.stop();
//                sgfplib.CloseDevice();
//                //sgfplib.Close();
//                bSecuGenDeviceOpened = false;
//                unregisterReceiver(mUsbReceiver);
//            }
//
//            mVerifyImage = null;
//            mVerifyTemplate = null;
//
//            android.util.Log.d("TAG", "Exit onPause()");
//        }
//    }

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

    //Binding Data to UI Method
    public void bindDateToUi() {
        farmerNameTxt.setText(basicFarmerDetails.getFarmerFirstName() + " " + basicFarmerDetails.getFarmerLastName() + " " + basicFarmerDetails.getFarmerMiddleName() + "(" + basicFarmerDetails.getFarmerCode().trim() + ")");
        tvfathername.setText(basicFarmerDetails.getFarmerFatherName());
        tvvillagename.setText(basicFarmerDetails.getFarmerVillageName());
        tvcontactnum.setText(basicFarmerDetails.getPrimaryContactNum());
        String addressLine1 = basicFarmerDetails.getAddress1();
        String addressLine2 = basicFarmerDetails.getAddress2();
        String LandMark = basicFarmerDetails.getLandmark();
        Log.d("AddressLine1", addressLine1 + "");

        if (addressLine1 == null || addressLine1.equalsIgnoreCase("null") || TextUtils.isEmpty(addressLine1)) {
            addressLine1 = " ";
        }
        if (addressLine2 == null || addressLine2.equalsIgnoreCase("null") || TextUtils.isEmpty(addressLine2)) {
            addressLine2 = " ";
        }
        if (LandMark == null || LandMark.equalsIgnoreCase("null") || TextUtils.isEmpty(LandMark)) {
            LandMark = " ";
        }
        tvaddress.setText(addressLine1 + " , " + addressLine2 + " , " + LandMark);
        CollectionFarmerCodeWithOutPlot = basicFarmerDetails.getFarmerCode().trim();
        firstthree = CollectionFarmerCodeWithOutPlot.substring(0, 9);
//        Toast.makeText(getApplicationContext(),"First 3 characters"+firstthree,Toast.LENGTH_LONG).show();

        if (null != basicFarmerDetails.getPhotoLocation()) {
            Picasso.with(this).load(new File(basicFarmerDetails.getPhotoLocation())).into(userImage);
        } else {
            userImage.setImageResource(R.mipmap.app_logo);
            userImage.invalidate();
        }

        Picasso.with(this)
                .load(CommonUtils.getImageUrl(basicFarmerDetails))
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(FarmersDetailsScreen.this)
                                .load(CommonUtils.getImageUrl(basicFarmerDetails))
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .into(userImage);
                    }
                });
    }

    //Binding Plot Data

    private void bindPlotData() {
        plotdetailslistObj = ccDataAccessHandler.getPlotDetails(basicFarmerDetails.getFarmerCode().trim());
        if (plotdetailslistObj != null && plotdetailslistObj.size() > 0) {
            farmerplotDetailsLVAdapter = new FarmerPlotDetailsAdapter(this, plotdetailslistObj, R.layout.adapter_plotdetails);
            farmerplotDetailsLVAdapter.setOnClickListener(this);
            rvplotlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(farmerplotDetailsLVAdapter);
            farmerplotDetailsLVAdapter.clearSelection();
            farmerplotDetailsLVAdapter.selectedItems.clear();
        }
    }

    //On Plot Selected
    @Override
    public void onItemClicked(int position) {
        Log.v(LOG_TAG, "@@@ item clicked and the position is " + position);
        toggleSelection(position);
        List<String> extraPlots = (List<String>) DataManager.getInstance().getDataFromManager(EXTRA_PLOTS);
        String plotCodes = getPlotCodesToDisplay();
        if (!TextUtils.isEmpty(plotCodes) && null != extraPlots && !extraPlots.isEmpty()) {
            selectedPlotsLayout.setVisibility(View.VISIBLE);
            selectedPlotsTxt.setText(getPlotCodesToDisplay() + ", " + TextUtils.join(", ", extraPlots));
        } else {
            if (!TextUtils.isEmpty(plotCodes)) {
                selectedPlotsLayout.setVisibility(View.VISIBLE);
                selectedPlotsTxt.setText(plotCodes);
            } else {
                selectedPlotsLayout.setVisibility(View.GONE);
            }
        }
    }

    //On Plots Select & UnSelect

    private void toggleSelection(int position) {
        Log.v(LOG_TAG, "@@@ item clicked and the position is 111 check " + position);
        farmerplotDetailsLVAdapter.toggleSelection(position);
        List<String> extraPlots = (List<String>) DataManager.getInstance().getDataFromManager(EXTRA_PLOTS);
        String plotCodes = getPlotCodesToDisplay();
        if (!TextUtils.isEmpty(plotCodes) && null != extraPlots && !extraPlots.isEmpty()) {
            selectedPlotsLayout.setVisibility(View.VISIBLE);
            extraPlots = CommonUtils.ignoreDuplicatedInArrayList(extraPlots);
            selectedPlotsTxt.setText(getPlotCodesToDisplay() + ", " + TextUtils.join(", ", extraPlots));
        } else {
            if (!TextUtils.isEmpty(plotCodes)) {
                selectedPlotsLayout.setVisibility(View.VISIBLE);
                selectedPlotsTxt.setText(plotCodes);
            } else {
                selectedPlotsLayout.setVisibility(View.GONE);
            }
        }
    }

    //Displaying Plot Codes

    public String getPlotCodesToDisplay() {
        List plotCodesToDisplay = new ArrayList();
        if (null != farmerplotDetailsLVAdapter.getSelectedItems() && !farmerplotDetailsLVAdapter.getSelectedItems().isEmpty()) {
            List<Integer> selectedPos = farmerplotDetailsLVAdapter.getSelectedItems();
            for (int i = 0; i < selectedPos.size(); i++) {
                Log.v(LOG_TAG, "@@@ let's go next " + selectedPos.get(i) + "..." + plotdetailslistObj.get(i).getPlotID());
                plotCodesToDisplay.add(plotdetailslistObj.get(selectedPos.get(i)).getPlotID());
            }
        }
        plotCodesToDisplay = CommonUtils.ignoreDuplicatedInArrayList(plotCodesToDisplay);
        return TextUtils.join(", ", plotCodesToDisplay);
    }

    //OnActivity Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            List<String> extraPlots = (List<String>) DataManager.getInstance().getDataFromManager(EXTRA_PLOTS);
            if (null != extraPlots && !extraPlots.isEmpty()) {
                if (!extraPlots.isEmpty()) {
                    extraPlots = CommonUtils.ignoreDuplicatedInArrayList(extraPlots);
                    selectedPlotsLayout.setVisibility(View.VISIBLE);
                    String plotCodes = getPlotCodesToDisplay();
                    if (!TextUtils.isEmpty(plotCodes)) {
                        selectedPlotsTxt.setText(getPlotCodesToDisplay() + ", " + TextUtils.join(", ", extraPlots));
                    } else {
                        selectedPlotsTxt.setText(TextUtils.join(", ", extraPlots));
                    }
                } else {
                    selectedPlotsLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void SGFingerPresentCallback() {

    }
}
