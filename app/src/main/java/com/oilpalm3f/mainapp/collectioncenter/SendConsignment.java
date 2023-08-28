package com.oilpalm3f.mainapp.collectioncenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.BuildConfig;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignment;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.CcRate;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.ImageUtility;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.oilpalm3f.mainapp.database.DatabaseKeys.TABLE_CONSIGNMENT;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.MILL_INFORMATION;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;

//Fill Consignment Details
public class SendConsignment extends BaseFragment {

    private static final String LOG_TAG = SendConsignment.class.getName();

    public CCDataAccessHandler ccDataAccessHandler;
    public static final int REQUEST_CAM_PERMISSIONS = 1;
    private TextView consignmentNumber,expectedCost;
    private EditText createdBy;
    private EditText vehicleNumber;
    private EditText driverName, commentsEdt;
    private Spinner millSpin;
    private EditText consignmentWeight;
    private static final int CAMERA_REQUEST = 1888;
    private Button generateReceipt;
    private View parentView;
    private  File finalFile;
    private LinkedHashMap<String, String> millNameDataMap = null;
    private LinkedHashMap<String, Pair> sizeOfTruckDataMap = null;
    private LinkedHashMap<String, String> isSharingDataMap = null;
    private DataAccessHandler dataAccessHandler;
    private String millCode, millName;
    private CollectionCenter selectedCollectionCenter;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt;
    private String createdByName;
    private LinkedHashMap<String, String> VehicleTypeMap;
    private Spinner sp_vehicle_type,sizeOfTruck,sharingConsignment;
    private String vehicleCode, vehcileType;
    private EditText et_DriverMobileNumber,actualCost;
    private ImageView slipImage, slipIcon;
    public static  String mCurrentPhotoPath;
    private String consinmentid = "";
    private String days = "";
    public int financialYear;
    private DecimalFormat dec,dec1;
    private boolean isSharingCon = false;
    private String sizeOfTruckId,truckSize = "";
    private CcRate ccRate;
    private int  isSharingId,isThere = 0;
    private float overWeight = 0.0f,result = 0.0f;
    private LinearLayout sizeOfTruckLL,sharingLL,expectedLL,actualLL;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Bitmap currentBitmap = null;

    public SendConsignment() {

    }

    //Initializing Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        parentView = inflater.inflate(R.layout.send_consignment_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getActivity().getResources().getString(R.string.send_consignment));

        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());

        UserDetails userDetails = (UserDetails) DataManager.getInstance().getDataFromManager(USER_DETAILS);

        if (null != userDetails) {
            createdByName = userDetails.getFirstName();
        }

        initView();
    }

    //Initializing UI
    public void initView() {
        consignmentNumber = (TextView) parentView.findViewById(R.id.consignmentNumber);
        createdBy = (EditText) parentView.findViewById(R.id.createdBy);
        vehicleNumber = (EditText) parentView.findViewById(R.id.vehicle_number);
        driverName = (EditText) parentView.findViewById(R.id.driverName);
        millSpin = (Spinner) parentView.findViewById(R.id.millSpin);
        consignmentWeight = (EditText) parentView.findViewById(R.id.consignment_weight);
        consignmentWeight.setKeyListener(DigitsKeyListener.getInstance(true, true));
        generateReceipt = (Button) parentView.findViewById(R.id.generateReceipt);
        commentsEdt = (EditText) parentView.findViewById(R.id.commentsEdit);
        et_DriverMobileNumber = (EditText) parentView.findViewById(R.id.edt_mobile_no);
        sp_vehicle_type = (Spinner) parentView.findViewById(R.id.sp_vehicle_type);
        dataAccessHandler = new DataAccessHandler(getActivity());
        slipImage = (ImageView) parentView.findViewById(R.id.slip_image);
        slipIcon = (ImageView) parentView.findViewById(R.id.slip_icon);
        sizeOfTruck = parentView.findViewById(R.id.sizeOfTruck);
        sharingConsignment = parentView.findViewById(R.id.sharingConsignment);
        actualCost = parentView.findViewById(R.id.actualCost);
        expectedCost = parentView.findViewById(R.id.expectedCost);
        sizeOfTruckLL = parentView.findViewById(R.id.sizeOfTruckLL);
        sharingLL = parentView.findViewById(R.id.sharingLL);
        expectedLL = parentView.findViewById(R.id.expectedLL);
        actualLL = parentView.findViewById(R.id.actualLL);


        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();

        dec = new  DecimalFormat("0.00");
        dec1 = new DecimalFormat("0.000");

        collectionCenterName = (TextView) parentView.findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) parentView.findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) parentView.findViewById(R.id.collection_center_village);
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
        createdBy.setText(!TextUtils.isEmpty(createdByName) ? createdByName : "");

        isThere =  dataAccessHandler.getIsThere(Queries.getInstance().isCenterExists(selectedCollectionCenter.getCode()));
        Log.v("@@isther",""+isThere);

        if(isThere == 0)
        {
            sizeOfTruckLL.setVisibility(View.GONE);
            sharingLL.setVisibility(View.GONE);
            expectedLL.setVisibility(View.GONE);
            actualLL.setVisibility(View.GONE);
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
            String financalDate = "01/04/"+String.valueOf(financialYear);
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(financalDate);
            long diff = date1.getTime() - date2.getTime();
            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS)+1);
            days = StringUtils.leftPad(noOfDays,3,"0");
            Log.v(LOG_TAG,"days -->"+days);

        }catch (Exception e){
            e.printStackTrace();
        }

        consignmentNumber.setText(ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.CONSIGNMENT_CODE_INITIAL, TABLE_CONSIGNMENT,days));
        collectionIDTxt =(TextView) parentView.findViewById(R.id.consignmentNumber);
        consinmentid = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.CONSIGNMENT_CODE_INITIAL, TABLE_CONSIGNMENT,days);

        ccDataAccessHandler.getVechildType();
        millNameDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getMillInformation());

        //Binding Vehicle Type to Vehicle Type Spinner
        VehicleTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleType());
        ArrayAdapter spinnerArrayAdaptervechile = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(VehicleTypeMap, "Vehicle Type"));
        spinnerArrayAdaptervechile.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_vehicle_type.setAdapter(spinnerArrayAdaptervechile);
        sp_vehicle_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (VehicleTypeMap != null && VehicleTypeMap.size() > 0 && sp_vehicle_type.getSelectedItemPosition() != 0) {
                    vehicleCode = VehicleTypeMap.keySet().toArray(new String[VehicleTypeMap.size()])[i - 1];
                    vehcileType = sp_vehicle_type.getSelectedItem().toString();
                    Log.v(LOG_TAG, "@@@ vehicle code " + vehicleCode + " name " + vehcileType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Binding Mill Type to Mill Spinner

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(millNameDataMap, "Mill"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        millSpin.setAdapter(spinnerArrayAdapter);
        DataManager.getInstance().addData(MILL_INFORMATION, millNameDataMap);
        millSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (millNameDataMap != null && millNameDataMap.size() > 0 && millSpin.getSelectedItemPosition() != 0) {
                    millCode = millNameDataMap.keySet().toArray(new String[millNameDataMap.size()])[i - 1];
                    millName = millSpin.getSelectedItem().toString();
                    Log.v(LOG_TAG, "@@@ mill code " + millCode + " name " + millName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Image On Click Listener
        slipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (!CommonUtils.isPermissionAllowed(getActivity(), Manifest.permission.CAMERA))) {
                    com.oilpalm3f.mainapp.cloudhelper.Log.v(LOG_TAG, "Location Permissions Not Granted");
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            PERMISSIONS_STORAGE,
                            REQUEST_CAM_PERMISSIONS
                    );
                } else {

                    dispatchTakePictureIntent(CAMERA_REQUEST);
                }
            }
        });

        //Generate Receipt On Click Listener & saving Consignment Data
        generateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allValidFields()) {
                    try {
                        PreViewConsignmentScreen previewConsignment = new PreViewConsignmentScreen();
                        Consignment sendconsignObj = new Consignment();
                        sendconsignObj.setCreatedBy(createdBy.getText().toString());
                        sendconsignObj.setCode(consignmentNumber.getText().toString());
                        sendconsignObj.setCollectioncentercode(selectedCollectionCenter.getCode());
                        sendconsignObj.setVehiclenumber(vehicleNumber.getText().toString().toUpperCase());
                        sendconsignObj.setDrivername(driverName.getText().toString());
                        sendconsignObj.setMillcode(millCode);
                        sendconsignObj.setGrossweight(0);
                        sendconsignObj.setTareweight(0);
                        sendconsignObj.setNetweight(0);
                        sendconsignObj.setTotalweight(Double.parseDouble(consignmentWeight.getText().toString()));
                        sendconsignObj.setWeightdifference(0);
                        sendconsignObj.setRecieptGeneratedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        sendconsignObj.setIsActive(1);
                        sendconsignObj.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        sendconsignObj.setCreatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
                        sendconsignObj.setUpdatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
                        sendconsignObj.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        sendconsignObj.setServerUpdatedStatus(false);
                        sendconsignObj.setVehcileType(vehcileType + "@" + vehicleCode);
                        sendconsignObj.setDriverMobileNumber(et_DriverMobileNumber.getText().toString());

                        sendconsignObj.setRemarks(commentsEdt.getText().toString());
                        Log.v("@@@remarks",commentsEdt.getText().toString());

                        Bundle dataBundle = new Bundle();


                        if(isThere>0) {

                            if (isSharingId == 1) {
                                sendconsignObj.setSharingCost(ccRate.getCombinedCharge());
                                sendconsignObj.setTransportCost(ccRate.getTransportCost());
                                sendconsignObj.setOverWeightCost(overWeight);
                            } else if (isSharingId == 2) {
                                sendconsignObj.setSharingCost(0.0f);
                                sendconsignObj.setTransportCost(ccRate.getTransportCost());
                                sendconsignObj.setOverWeightCost(overWeight);
                            }

                            sendconsignObj.setSizeOfTruckId(Integer.parseInt(sizeOfTruckId));
                            sendconsignObj.setSharing(isSharingCon);
                            sendconsignObj.setExpectedCost(result);
                            sendconsignObj.setActualCost(Float.parseFloat(actualCost.getText().toString()));

                            dataBundle.putString("isThere","Yes");
                            dataBundle.putString("sizeOfTruck",truckSize);
                        }
                        else {
                            sendconsignObj.setSharingCost(0);
                            sendconsignObj.setTransportCost(0);
                            sendconsignObj.setOverWeightCost(0);
                            sendconsignObj.setSizeOfTruckId(0);
                            sendconsignObj.setSharing(false);
                            sendconsignObj.setExpectedCost(0);
                            sendconsignObj.setActualCost(0);

                            dataBundle.putString("isThere","No");

                        }
                        dataBundle.putString("sameday", days);
                        Log.d("samedaysend", days);
                        dataBundle.putParcelable("sendconsignment_data", sendconsignObj);
                        previewConsignment.setArguments(dataBundle);

                        String backStateName = previewConsignment.getClass().getName();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ftransaction = fm.beginTransaction();
                        ftransaction.replace(android.R.id.content, previewConsignment).commit();
                        ftransaction.addToBackStack(backStateName);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "@@@ Error while setting data " + e.getMessage());
                    }
                }
            }
        });


        //Binding Data to Size of Truck Spinner
        sizeOfTruckDataMap = dataAccessHandler.getPairData(Queries.getInstance().getTruckSize(selectedCollectionCenter.getCode()));

        Log.v("@@@count",""+sizeOfTruckDataMap.size());

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.arrayFromPair(sizeOfTruckDataMap, "Size Of Truck"));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeOfTruck.setAdapter(spinnerAdapter);


        isSharingDataMap = new LinkedHashMap<>();
        isSharingDataMap.put("1","Yes");
        isSharingDataMap.put("2","No");

        //Binding Data to Sharing Consignment Spinner & its On Click Listener
        ArrayAdapter isShareAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(isSharingDataMap, "Is Sharing"));
        isShareAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sharingConsignment.setAdapter(isShareAdapter);


        sharingConsignment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(sharingConsignment.getSelectedItemPosition()>0)
                {
                    calculateExpectedCost();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Consignment Weight on Text Changed Listener
        consignmentWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sharingConsignment.setSelection(0);
                expectedCost.setText("");
            }
        });
        //Size of Truck On Click Listener
        sizeOfTruck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(sizeOfTruck.getSelectedItemPosition()>0)
                {
                    truckSize = sizeOfTruck.getSelectedItem().toString();
                    sharingConsignment.setSelection(0);
                    expectedCost.setText("");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

//Validations & Calculating the Weight
    private void calculateExpectedCost()
    {
        if (CommonUtils.isEmptySpinner(millSpin)) {
            UiUtils.showCustomToastMessage("Please select mill", getActivity(), 1);
            sharingConsignment.setSelection(0);

        }
        else if (TextUtils.isEmpty(consignmentWeight.getText().toString())) {
            UiUtils.showCustomToastMessage(getActivity().getResources().getString(R.string.error_consignment_weight), getActivity(), 1);
            consignmentWeight.requestFocus();
            sharingConsignment.setSelection(0);

        }
        else  if (CommonUtils.isEmptySpinner(sizeOfTruck)) {
            UiUtils.showCustomToastMessage("Please select size of truck", getActivity(), 1);
            sharingConsignment.setSelection(0);
        }
        else if (CommonUtils.isEmptySpinner(sharingConsignment)) {
            sharingConsignment.setSelection(0);
            UiUtils.showCustomToastMessage("Please select sharing by other collection center", getActivity(), 1);
        }else
        {
            float conWeight = Float.parseFloat(consignmentWeight.getText().toString());
            float  convertTons = conWeight/1000;

             sizeOfTruckId =  sizeOfTruckDataMap.keySet().toArray(new String[sizeOfTruckDataMap.size()])[sizeOfTruck.getSelectedItemPosition() - 1];
             Log.v("@@@sizeID",""+sizeOfTruckId);
            Pair statePair = sizeOfTruckDataMap.get(sizeOfTruckId);
            Log.v("@@@sizeID",""+statePair.first.toString());
            float selectedTons  = Float.parseFloat(statePair.first.toString());
             ccRate = dataAccessHandler.checkListOfTables(Queries.getInstance().getCcRate(selectedCollectionCenter.getCode(),Integer.parseInt(sizeOfTruckId)));

            String isSharing =  isSharingDataMap.keySet().toArray(new String[isSharingDataMap.size()])[sharingConsignment.getSelectedItemPosition() - 1];


             isSharingId = Integer.parseInt(isSharing);

            if(isSharingId == 1)
            {
                isSharingCon = true;

                float extraTons = convertTons - selectedTons ;
                extraTons = Float.parseFloat(dec1.format(extraTons));
                Log.v("@@@extraTons",""+extraTons);

                if(extraTons>0)
                {
                   overWeight = extraTons*ccRate.getOverweightCharge();
                }

                Log.v("@@@overWeight",""+overWeight+"enterd"+convertTons+"selected"+selectedTons);
                Log.v("@@@ccRate",""+ccRate.getTransportCost()+" "+ccRate.getCombinedCharge());

                 result = ccRate.getTransportCost()+ccRate.getCombinedCharge()+overWeight;
                 result = Float.parseFloat(dec.format(result));
                expectedCost.setText(""+dec.format(result));


            }else if(isSharingId == 2)
            {
                isSharingCon = false;
                float extraTons = convertTons - selectedTons ;
                extraTons = Float.parseFloat(dec1.format(extraTons));

                Log.v("@@@extraTons",""+extraTons);
                if(extraTons>0)
                {
                    overWeight = extraTons*ccRate.getOverweightCharge();
                }
                Log.v("@@@overWeight",""+overWeight);
                Log.v("@@@ccRate",""+ccRate.getTransportCost());


                result = ccRate.getTransportCost()+overWeight;

                expectedCost.setText(""+dec.format(result));
            }
        }
    }

    //Load the Image OnResume
    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(mCurrentPhotoPath)&& !TextUtils.isEmpty(consinmentid) && null != slipImage)
        {
            loadImageFromStorage(mCurrentPhotoPath);
            slipImage.invalidate();
        }
    }

    //Validations
    public boolean allValidFields() {

        if (TextUtils.isEmpty(createdBy.getText().toString())) {
            UiUtils.showCustomToastMessage(getActivity().getResources().getString(R.string.error_createdby), getActivity(), 1);
            createdBy.requestFocus();
            return false;
        }
        if (CommonUtils.isEmptySpinner(millSpin)) {
            UiUtils.showCustomToastMessage("Please select mill", getActivity(), 1);
            return false;
        }

        if (TextUtils.isEmpty(consignmentWeight.getText().toString())) {
            UiUtils.showCustomToastMessage(getActivity().getResources().getString(R.string.error_consignment_weight), getActivity(), 1);
            consignmentWeight.requestFocus();
            return false;
        }

        if(isThere>0) {

            if (CommonUtils.isEmptySpinner(sizeOfTruck)) {
                UiUtils.showCustomToastMessage("Please select size of truck", getActivity(), 1);
                return false;
            }

            if (CommonUtils.isEmptySpinner(sharingConsignment)) {
                UiUtils.showCustomToastMessage("Please select sharing by other collection center", getActivity(), 1);
                return false;
            }

            if (TextUtils.isEmpty(actualCost.getText().toString())) {
                UiUtils.showCustomToastMessage("Please enter actual cost", getActivity(), 1);
                actualCost.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(commentsEdt.getText().toString())) {
                UiUtils.showCustomToastMessage("Please enter comments", getActivity(), 1);
                commentsEdt.requestFocus();
                return false;
            }
        }

        if (CommonUtils.isEmptySpinner(sp_vehicle_type)) {
            UiUtils.showCustomToastMessage("Please select vehicle Type", getActivity(), 1);
            Log.i(LOG_TAG, "Please select vehcile Type");

            return false;
        }


        if (TextUtils.isEmpty(vehicleNumber.getText().toString())) {
            UiUtils.showCustomToastMessage(getActivity().getResources().getString(R.string.error_vehicle_number), getActivity(), 1);
            vehicleNumber.requestFocus();
            return false;
        }


        if (TextUtils.isEmpty(driverName.getText().toString())) {
            UiUtils.showCustomToastMessage("Please enter Driver Name", getActivity(), 1);
            driverName.requestFocus();
            return false;
        }



        if (TextUtils.isEmpty(mCurrentPhotoPath)) {
            UiUtils.showCustomToastMessage("Please Take The Consigment Photo",getActivity(),1);
//            Toast.makeText(getActivity(), "Please take the photo", Toast.LENGTH_LONG).show();
            return false;
        }


        double weightValue = 0;
        try {
            weightValue = Double.parseDouble(consignmentWeight.getText().toString());
        } catch (NumberFormatException nme) {
            UiUtils.showCustomToastMessage("Invalid consignment weight", getActivity(), 1);
            return false;
        }

        if (weightValue <= 0) {
            UiUtils.showCustomToastMessage("Invalid consignment weight", getActivity(), 1);
            return false;
        }

        return true;
    }

    //What Happens on Acitivty Result
    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
//                        UiUtils.decodeFile(mCurrentPhotoPath,finalFile);
                        handleBigCameraPhoto();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }
                break;
            }
        }
    }

    //Setting the Picture
    private void handleBigCameraPhoto () {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
//            mCurrentPhotoPath = null;
        }

    }

    private void setPic()
    {

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
        slipImage.setVisibility(View.VISIBLE);
        slipIcon.setVisibility(View.GONE);
        slipImage.invalidate();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDirectory = new File(root + "/3F_Pictures");
        File pictureDirectory = new File(root + "/3F_Pictures/" + "consignmentPhotos");

        if (!rootDirectory.exists()) {
            rootDirectory.mkdirs();
        }

        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }
        finalFile = new File(pictureDirectory, consinmentid + CommonConstants.JPEG_FILE_SUFFIX);
        return finalFile;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch(actionCode) {
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
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    //Loading Image from the Storage
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


}
