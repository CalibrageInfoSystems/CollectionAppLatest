package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Point;


import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.collectioncenter.FarmersDetailsScreen;
import com.oilpalm3f.mainapp.collectioncenter.PrintReceipt;
import com.oilpalm3f.mainapp.collectioncenter.WeighbridgeCC;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;

import com.oilpalm3f.mainapp.utils.DateFormats;

import com.oilpalm3f.mainapp.utils.UiUtils;
import com.oilpalm3f.mainapp.weighbridge.CollectionCenter1;
import com.oilpalm3f.mainapp.weighbridge.New_WeighbridgeActivity;
import com.oilpalm3f.mainapp.weighbridge.SettingsActivity;
import com.oilpalm3f.mainapp.weighbridge.TerminalFragment;
import com.oilpalm3f.mainapp.weighbridge.TokenTable;
import com.oilpalm3f.mainapp.weighbridge.WeighbridgeActivity;
import com.oilpalm3f.mainapp.weighbridge.tareWeighCaliculation;


import org.apache.commons.lang3.StringUtils;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.getSystemService;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.PRIVATE_WEIGHBRIDGE_INFO;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;


public class NewWeighbridgeCC extends BaseFragment{
    //  private ArrayList<CollectionCenter> collectionCenterArrayList = new ArrayList<>();


    ArrayList<CollectionCenter1> collectionCenter1ArrayList = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    SharedPreferences sharedPreferences;


    public static final int OWN_WEIGHBRIDGE = 24;
    public static final int PRIVATE_WEIGHBRIDGE = 25;
    public static final int NO_WEIGHBRIDGE = 26;
    private static final String LOG_TAG = WeighbridgeCC.class.getName();

    private android.widget.Spinner nameOfPrivateWbSpin;
    private ImageView scrollBottomIndicator;
    private InteractiveScrollView interactiveScrollView;

    String TokenNo;

    CollectionCenter1 collectionCenter1;
    CollectionCenter1 cc1;
    TokenTable tokenTable;
  public static EditText gross_weight;
    private Button takeReading, takeWeight;
    private EditText commentsEdit, dateTimeEdit, name_of_operator, vehicle_number, vehicle_driver,

    postDateTimeEdit;


    private int typeSelected;
    private LinearLayout privateWBrel, tareWeightLL, grossWeightLL, linearLayout1, parent;
    private View rootView;
    Button gross_weight_Button, total_Weight_Button;

    Spinner vehicleTypespin, vehicleCategoryspin, isloosefruitavailable_spinner;
    private LinkedHashMap<String, String> VehicleTypeMap,VehicleCategoryTypeMap;
    private String vehicleCategoryCode, vehicleCategoryType;
    private String vehicleTypeCode, vehicleTypeName;
    private TextView avehiclenumber_tv;

    private LinkedHashMap<String, String> weighbridgeCenterDataMap = null;
    private DataAccessHandler dataAccessHandler;
    private CCDataAccessHandler ccDataAccessHandler;
    private String dateAndTimeStr = "";
    public static String postedDateAndTimeStr = "";
    private CollectionCenter selectedCollectionCenter;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt, netWeightTxt, tokenNoText;
    private String collectionId = "";
    private Point mSize;
    Button grossWeightBtn;
    private String ColFarmerWithOutplot;
    String wbId = "";
    public int financialYear;


    private SGAutoOnEventNotifier autoOn;
    private JSGFPLib sgfplib;

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

    private String createdByName;

    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar1 = Calendar.getInstance();
    Calendar weighingCalendar = Calendar.getInstance();
    private String days = "";


    public NewWeighbridgeCC() {
    }


    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        rootView = inflater.inflate(R.layout.fragment_new_weighbridge_c_c, null);
        baseLayout.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        grossWeightBtn = rootView.findViewById(R.id.grossWeightBtn);
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        typeSelected = selectedCollectionCenter.getWeighBridgeTypeId();

        //CommonConstants.IsFingerPrintReq = true;

        Log.d("InwhichscreenamI", "NewWeighbridgeCC" + "");

        if (typeSelected == OWN_WEIGHBRIDGE) {
            setTile(getString(R.string.new_collection_own));
        } else if (typeSelected == PRIVATE_WEIGHBRIDGE) {
            setTile(getString(R.string.new_collection_priv));
        } else {
            setTile(getString(R.string.new_collection_no_weighbridge));
        }

        initViews();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

//        sgfplib = new JSGFPLib(getActivity(), (UsbManager) getActivity().getSystemService(Context.USB_SERVICE));
//
//        autoOn = new SGAutoOnEventNotifier(sgfplib, this);

    }

    public void initViews() {

        dataAccessHandler = new DataAccessHandler(getActivity());
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());

        ColFarmerWithOutplot = FarmersDetailsScreen.firstthree;

        CommonConstants.flowFrom="Gross_Weight";
        privateWBrel = rootView.findViewById(R.id.privateWBrel);
        grossWeightLL = rootView.findViewById(R.id.gross_weight_LL);
        tareWeightLL = rootView.findViewById(R.id.tare_weight_LL);

//        if (typeSelected == PRIVATE_WEIGHBRIDGE) {
//            privateWBrel.setVisibility(View.VISIBLE);
//        } else {
//            privateWBrel.setVisibility(View.GONE);
//        }
//
//        if (typeSelected == NO_WEIGHBRIDGE) {
//            tareWeightLL.setVisibility(View.GONE);
//            grossWeightLL.setVisibility(View.GONE);
//
//        }


        netWeightTxt = (TextView) rootView.findViewById(R.id.netWeightTxt);

        vehicle_driver = rootView.findViewById(R.id.vehicle_driver);
        gross_weight = rootView.findViewById(R.id.gross_weight);
        name_of_operator = (EditText) rootView.findViewById(R.id.operator_name);
        UserDetails userDetails = (UserDetails) DataManager.getInstance().getDataFromManager(USER_DETAILS);

        if (null != userDetails) {
            createdByName = userDetails.getFirstName();
            name_of_operator.setText(createdByName);
        }


        dateTimeEdit = (EditText) rootView.findViewById(R.id.date_and_time);
        postDateTimeEdit = (EditText) rootView.findViewById(R.id.post_date_and_time);

        commentsEdit = (EditText) rootView.findViewById(R.id.commentsEdit);

        nameOfPrivateWbSpin = (Spinner) rootView.findViewById(R.id.millSpin);
        gross_weight_Button = rootView.findViewById(R.id.gross_weight_Button);
        total_Weight_Button = rootView.findViewById(R.id.total_Weight_Button);
        linearLayout1=rootView.findViewById(R.id.linearLayout1);
        parent=rootView.findViewById(R.id.parent);
        takeReading = (Button) rootView.findViewById(R.id.takeReading);
        takeWeight = (Button) rootView.findViewById(R.id.takeWeight);
        vehicle_number = rootView.findViewById(R.id.vehicle_number);
        collectionCenterName = (TextView) rootView.findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) rootView.findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) rootView.findViewById(R.id.collection_center_village);
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());


        vehicleCategoryspin = (Spinner) rootView.findViewById(R.id.vehicleCategorySpin);
        vehicleTypespin = (Spinner) rootView.findViewById(R.id.vehicleTypeSpin);
        avehiclenumber_tv = rootView.findViewById(R.id.avehiclenumber_tv);

        scrollBottomIndicator = (ImageView) rootView.findViewById(R.id.bottomScroll);
        interactiveScrollView = (InteractiveScrollView) rootView.findViewById(R.id.scrollView);
        scrollBottomIndicator.setVisibility(View.VISIBLE);


        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();
        sharedPreferences = getActivity().getSharedPreferences("collection", MODE_PRIVATE);

        gross_weight_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setVisibility(View.VISIBLE);
                linearLayout1.setVisibility(View.GONE);
            }
        });
        total_Weight_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout1.setVisibility(View.GONE);

//                tareWeighCaliculation fragment = new tareWeighCaliculation ();
//                Bundle args = new Bundle();
//                args.putString("sameday", "days");
//                fragment.setArguments(args);
//
//                // getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
//
//                replaceFragment(fragment);
                replaceFragment(new tareWeighCaliculation());
            }
        });

        takeReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
//                    UiUtils.showCustomToastMessage("Please select weighbridge center", getActivity(), 1);
//                } else if (TextUtils.isEmpty(dateAndTimeStr)) {
//                    UiUtils.showCustomToastMessage("Please select date and time", getActivity(), 1);
//                }else if (CommonUtils.isEmptySpinner(vehicleCategoryspin)){
//                    UiUtils.showCustomToastMessage("Please select Vehicle Category", getActivity(), 1);
//                }
//                else if (CommonUtils.isEmptySpinner(vehicleTypespin)){
//                    UiUtils.showCustomToastMessage("Please select Vehicle Type", getActivity(), 1);
//                }
//                else if (vehicleCategoryspin.getSelectedItemPosition() != 1) {
//                    if (TextUtils.isEmpty(vehicle_number.getText().toString())) {
//                        UiUtils.showCustomToastMessage("Enter Vehicle Number", getActivity(), 1);
//                    }
//                }
//                else if (TextUtils.isEmpty(vehicle_driver.getText().toString())) {
//                    UiUtils.showCustomToastMessage("Enter Vehicle Driver Name", getActivity(), 1);
//                } else if (TextUtils.isEmpty(name_of_operator.getText().toString())) {
//                    UiUtils.showCustomToastMessage("Enter Operator Name", getActivity(), 1);
//                } else if (TextUtils.isEmpty(gross_weight.getText().toString())) {
//                    UiUtils.showCustomToastMessage("Enter Gross Weight", getActivity(), 1);
//                } else if (TextUtils.isEmpty(postedDateAndTimeStr)) {
//                    Calendar c = Calendar.getInstance();
//                    SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
//                    postedDateAndTimeStr = objdateformat.format(c.getTime());
//
//                } else {
                if (validations()){
                    tokenTable = new TokenTable();
                    tokenTable.setTokenNo(Integer.parseInt(TokenNo));
                    tokenTable.setCollId(collectionIDTxt.getText().toString());
                    tokenTable.setWeighbridgeName(nameOfPrivateWbSpin.getSelectedItem().toString());
                    tokenTable.setWeighbridgeId(Integer.parseInt(wbId));
                    tokenTable.setCollectionCenterCode(wbId);
                    tokenTable.setWeighingDate(dateAndTimeStr);
                    if (TextUtils.isEmpty(vehicle_number.getText().toString())){
                        tokenTable.setVehicleNumber("null");
                    }else{

                        tokenTable.setVehicleNumber(vehicle_number.getText().toString());
                    }
                    tokenTable.setDriverName(vehicle_driver.getText().toString());
                    tokenTable.setOperatorName(name_of_operator.getText().toString());
                    tokenTable.setComments(commentsEdit.getText().toString());
                    tokenTable.setPostingDate(postDateTimeEdit.getText().toString());
                    tokenTable.setGrossWeight(Float.parseFloat(gross_weight.getText().toString()));
                    tokenTable.setVehicleTypeId(Integer.parseInt(vehicleTypeCode));

                    tokenTable.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    long result = dataAccessHandler.addTokenTable(tokenTable);
                    if (result > -1) {
                        UiUtils.showCustomToastMessage("Success,Token No Is"+TokenNo,getContext(),0);
                        clearFields();
                        //  startActivity(new Intent(getContext(), WeighbridgeActivity.class));

                    }

                }
            }
        });
        takeWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new tareWeighCaliculation());
            }
        });
        grossWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("collection", MODE_PRIVATE);
               if(preferences.contains("baudrate")) {
                   CommonConstants.baudrate=preferences.getString("baudrate","null");
                   startActivity(new Intent(getContext(), New_WeighbridgeActivity.class));
               }else {
                   startActivity(new Intent(getContext(), SettingsActivity.class));

               }

            }
        });

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
                Log.d(LOG_TAG, "onScrolling: ");
                scrollBottomIndicator.setVisibility(View.VISIBLE);
            }
        });



        weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getPrivateWeighbridgeDetails());
        DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);

        nameOfPrivateWbSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (weighbridgeCenterDataMap != null && weighbridgeCenterDataMap.size() > 0 &&
                        nameOfPrivateWbSpin.getSelectedItemPosition() != 0) {
                    if (!CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
                        int selectedPos = nameOfPrivateWbSpin.getSelectedItemPosition();
                        wbId = weighbridgeCenterDataMap.keySet().toArray(new String[weighbridgeCenterDataMap.size()])[selectedPos - 1];
                        Log.v(LOG_TAG, "@@@ selected wb center id " + wbId);
                        String wbName = weighbridgeCenterDataMap.get(wbId);
                        Log.v(LOG_TAG, "@@@ selected wb center name " + wbName);

                        netWeightTxt.setVisibility(View.VISIBLE);
                    } else {
                        if (typeSelected == NO_WEIGHBRIDGE) {
                            dateTimeEdit.setText("");
                        }
                    }
                } else {
                    if (typeSelected == NO_WEIGHBRIDGE) {
                        dateTimeEdit.setText("");


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                    VehicleTypeMap = dataAccessHandler.getvechileData(Queries.getInstance().getVehicleTypeonCategory(vehicleCategoryCode));
                    ArrayAdapter spinnerArrayAdaptervechileType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                            CommonUtils.fromMap(VehicleTypeMap, "Vehicle"));
                    spinnerArrayAdaptervechileType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicleTypespin.setAdapter(spinnerArrayAdaptervechileType);

                }

                if (vehicleCategoryspin.getSelectedItemPosition() == 1){

                    avehiclenumber_tv.setText("Vehicle Number");
                }else{

                    avehiclenumber_tv.setText("Vehicle Number *");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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


        collectionIDTxt = (TextView) rootView.findViewById(R.id.collection_ID);
        tokenNoText = (TextView) rootView.findViewById(R.id.tokenNoText);
        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(),
                    ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days);
        } else {
            collectionId = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL,
                    ccDataAccessHandler.TABLE_COLLECTION, days);
        }
        collectionIDTxt.setText("" + collectionId);
        Log.v(LOG_TAG, "@@@ collection code " + collectionId);

        tokenTable = dataAccessHandler.getLastTokenNoTa(Queries.getInstance().getTokenNoTa(selectedCollectionCenter.getCode()));


        if (tokenTable != null) {
            TokenNo = String.valueOf(tokenTable.getTokenNo() + 1);

        } else {
            TokenNo = days + "01";
        }

        //8886585656
        tokenNoText.setText("" + TokenNo);
        CommonConstants.TokenNumber = TokenNo;

        postDateTimeEdit.setText(DateFormats.getDateTime(new Date()));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        postedDateAndTimeStr = objdateformat.format(c.getTime());


    }

    public void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        mFragmentManager = getActivity().getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
        );
        mFragmentTransaction.replace(android.R.id.content, fragment);
        mFragmentTransaction.addToBackStack(backStateName);
        mFragmentTransaction.commit();
    }

    public boolean validations(){

        if (CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
            UiUtils.showCustomToastMessage("Please select weighbridge center", getActivity(), 1);
            return false;
        }
        if (TextUtils.isEmpty(dateAndTimeStr)) {
            UiUtils.showCustomToastMessage("Please select date and time", getActivity(), 1);
            return false;
        }
        if (CommonUtils.isEmptySpinner(vehicleCategoryspin)){
            UiUtils.showCustomToastMessage("Please select Vehicle Category", getActivity(), 1);
            return false;
        }

        if (CommonUtils.isEmptySpinner(vehicleTypespin)){
            UiUtils.showCustomToastMessage("Please select Vehicle Type", getActivity(), 1);
            return false;
        }

         if (vehicleCategoryspin.getSelectedItemPosition() != 1) {
            if (TextUtils.isEmpty(vehicle_number.getText().toString())) {
                UiUtils.showCustomToastMessage("Enter Vehicle Number", getActivity(), 1);
                return false;
            }
        }
         if (TextUtils.isEmpty(vehicle_driver.getText().toString())) {
            UiUtils.showCustomToastMessage("Enter Vehicle Driver Name", getActivity(), 1);
             return false;
        }
         if (TextUtils.isEmpty(name_of_operator.getText().toString())) {
            UiUtils.showCustomToastMessage("Enter Operator Name", getActivity(), 1);
             return false;
        }
         if (TextUtils.isEmpty(gross_weight.getText().toString())) {
            UiUtils.showCustomToastMessage("Enter Gross Weight", getActivity(), 1);
             return false;
        }
         if (TextUtils.isEmpty(postedDateAndTimeStr)) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
            postedDateAndTimeStr = objdateformat.format(c.getTime());

        }

        return true;
    }

    public  void clearFields(){
        nameOfPrivateWbSpin.setSelection(0);
        dateTimeEdit.setText("");
        vehicle_number.setText("");
        vehicle_driver.setText("");
        name_of_operator.setText("");
        gross_weight.setText("");
        postDateTimeEdit.setText("");
        parent.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.VISIBLE);
    }


//    @Override
//    public void SGFingerPresentCallback() {
//        autoOn.stop();
//        //fingerDetectedHandler.sendMessage(new Message());
//    }
}
