package com.oilpalm3f.mainapp.weighbridge;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
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

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.collectioncenter.FarmersDetailsScreen;
import com.oilpalm3f.mainapp.collectioncenter.WeighbridgeCC;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;
import com.oilpalm3f.mainapp.utils.DateFormats;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.PRIVATE_WEIGHBRIDGE_INFO;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;

//Not Using
public class AutoWeighFragment extends Fragment {
    View view;
//    ArrayList<CollectionCenter1> collectionCenter1ArrayList = new ArrayList<>();
//    private FragmentManager mFragmentManager;
//    private FragmentTransaction mFragmentTransaction;
//
//
//    public static final int OWN_WEIGHBRIDGE = 24;
//    public static final int PRIVATE_WEIGHBRIDGE = 25;
//    public static final int NO_WEIGHBRIDGE = 26;
//    private static final String LOG_TAG = WeighbridgeCC.class.getName();
//
//    private android.widget.Spinner nameOfPrivateWbSpin;
//    private ImageView scrollBottomIndicator;
//    private InteractiveScrollView interactiveScrollView;
//
//    String TokenNo;
//
//    CollectionCenter1 collectionCenter1;
//    CollectionCenter1 cc1;
//    TokenTable tokenTable;
//    private Button takeReading, takeWeight;
//    private EditText commentsEdit, dateTimeEdit, name_of_operator, vehicle_number, vehicle_driver, gross_weight,
//
//    postDateTimeEdit;
//
//
//    private int typeSelected;
//    private LinearLayout privateWBrel, tareWeightLL, grossWeightLL;
//
//
//    private LinkedHashMap<String, String> weighbridgeCenterDataMap = null;
//    private DataAccessHandler dataAccessHandler;
//    private CCDataAccessHandler ccDataAccessHandler;
//    private String dateAndTimeStr = "";
//    public static String postedDateAndTimeStr = "";
//    private CollectionCenter selectedCollectionCenter;
//    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt, netWeightTxt, tokenNoText;
//    private String collectionId = "";
//    private Point mSize;
//
//    private String ColFarmerWithOutplot;
//    String wbId = "";
//    public int financialYear;
//
//    final DatePickerDialog.OnDateSetListener date_TimeofWeighing = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            weighingCalendar.set(Calendar.YEAR, year);
//            weighingCalendar.set(Calendar.MONTH, monthOfYear);
//            weighingCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            String myFormat = CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS;
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            dateAndTimeStr = sdf.format(weighingCalendar.getTime());
//            dateTimeEdit.setText(sdf.format(weighingCalendar.getTime()));
//
//        }
//    };
//
//
//    final DatePickerDialog.OnDateSetListener postingDate = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            myCalendar1.set(Calendar.YEAR, year);
//            myCalendar1.set(Calendar.MONTH, monthOfYear);
//            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            String myFormat = CommonConstants.PostingDate_FORMAT_DDMMYYYY_HHMMSS;
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//            SimpleDateFormat sdf2 = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
//            postedDateAndTimeStr = sdf2.format(myCalendar1.getTime());
//            postDateTimeEdit.setText(sdf.format(myCalendar1.getTime()));
//
//        }
//    };
//
//    private String createdByName;
//
//    Calendar myCalendar = Calendar.getInstance();
//    Calendar myCalendar1 = Calendar.getInstance();
//    Calendar weighingCalendar = Calendar.getInstance();
//    private String days = "";
//
//
//    public AutoWeighFragment() {
//
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.fragment_auto_weigh, container, false);
//        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
//        typeSelected = selectedCollectionCenter.getWeighBridgeTypeId();
//
//
//        initViews();
//
//
//        return view;
//
//
//
//    }
//
//    public void initViews() {
//
//        dataAccessHandler = new DataAccessHandler(getActivity());
//        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
//        ColFarmerWithOutplot = FarmersDetailsScreen.firstthree;
//
//
//        vehicle_driver = view.findViewById(R.id.vehicle_driver);
//        gross_weight = view.findViewById(R.id.gross_weight);
//        name_of_operator = (EditText) view.findViewById(R.id.operator_name);
//        UserDetails userDetails = (UserDetails) DataManager.getInstance().getDataFromManager(USER_DETAILS);
//
//        if (null != userDetails) {
//            createdByName = userDetails.getFirstName();
//            name_of_operator.setText(createdByName);
//        }
//
//
//        dateTimeEdit = (EditText) view.findViewById(R.id.date_and_time);
//        postDateTimeEdit = (EditText) view.findViewById(R.id.post_date_and_time);
//
//        commentsEdit = (EditText) view.findViewById(R.id.commentsEdit);
//
//        nameOfPrivateWbSpin = (Spinner) view.findViewById(R.id.millSpin);
//
//        takeReading = (Button) view.findViewById(R.id.takeReading);
//        takeWeight = (Button) view.findViewById(R.id.takeWeight);
//        vehicle_number = view.findViewById(R.id.vehicle_number);
//        collectionCenterName = (TextView) view.findViewById(R.id.collection_center_name);
//        collectionCenterCode = (TextView) view.findViewById(R.id.collection_center_code);
//        collectionCenterVillage = (TextView) view.findViewById(R.id.collection_center_village);
//        collectionCenterName.setText(selectedCollectionCenter.getName());
//        collectionCenterCode.setText(selectedCollectionCenter.getCode());
//        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
//
//        scrollBottomIndicator = (ImageView) view.findViewById(R.id.bottomScroll);
//        interactiveScrollView = (InteractiveScrollView) view.findViewById(R.id.scrollView);
//        scrollBottomIndicator.setVisibility(View.VISIBLE);
//
//        final Calendar calendar = Calendar.getInstance();
//        final FiscalDate fiscalDate = new FiscalDate(calendar);
//        financialYear = fiscalDate.getFiscalYear();
//
//
//        takeReading.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
//                    UiUtils.showCustomToastMessage("Please select weighbridge center", getActivity(), 1);
//                } else if (TextUtils.isEmpty(dateAndTimeStr)) {
//                    UiUtils.showCustomToastMessage("Please select date and time", getActivity(), 1);
//                } else if (TextUtils.isEmpty(vehicle_number.getText().toString())) {
//                    UiUtils.showCustomToastMessage("Enter Vehicle Number", getActivity(), 1);
//                } else if (TextUtils.isEmpty(vehicle_driver.getText().toString())) {
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
////                    collectionCenter1ArrayList.clear();
////                    DataManager.getInstance().deleteData(DataManager.COLLECTION_CENTRE11);
////                    collectionCenter1 = new CollectionCenter1();
////                    collectionCenter1.setTokenNo(Integer.parseInt(TokenNo));
////                    collectionCenter1.setCollId(collectionIDTxt.getText().toString());
////                    collectionCenter1.setWeighbridgeCentre(nameOfPrivateWbSpin.getSelectedItem().toString());
////                    collectionCenter1.setWbId(wbId);
////                    collectionCenter1.setDateAndTime(dateAndTimeStr);
////                    collectionCenter1.setVechileNo(vehicleNumber);
////                    collectionCenter1.setDriverName(vehicleDriverName);
////                    collectionCenter1.setNameOfOperator(name_of_operator.getText().toString());
////                    collectionCenter1.setComments(commentsEdit.getText().toString());
////                    collectionCenter1.setPostingDate(postDateTimeEdit.getText().toString());
////                    collectionCenter1ArrayList.add(collectionCenter1);
////                    DataManager.getInstance().addData(DataManager.COLLECTION_CENTRE11, collectionCenter1ArrayList);
////                    if (collectionCenter1ArrayList.size() > 0) {
////                        //  replaceFragment(new WeightCaliculationFragment());
////                        startActivity(new Intent(getContext(), WeighbridgeActivity.class));
////                    }
//                    tokenTable = new TokenTable();
//                    tokenTable.setTokenNo(Integer.parseInt(TokenNo));
//                    tokenTable.setCollId(collectionIDTxt.getText().toString());
//                    tokenTable.setWeighbridgeName(nameOfPrivateWbSpin.getSelectedItem().toString());
//                    tokenTable.setWeighbridgeId(Integer.parseInt(wbId));
//                    tokenTable.setWeighingDate(dateAndTimeStr);
//                    tokenTable.setVehicleNumber(vehicle_number.getText().toString());
//                    tokenTable.setDriverName(vehicle_driver.getText().toString());
//                    tokenTable.setOperatorName(name_of_operator.getText().toString());
//                    tokenTable.setComments(commentsEdit.getText().toString());
//                    tokenTable.setPostingDate(postDateTimeEdit.getText().toString());
//                    long result = dataAccessHandler.addTokenTable(tokenTable);
//                    if (result > -1) {
//                        startActivity(new Intent(getContext(), WeighbridgeActivity.class));
//
//                    }
//
//                }
//            }
//        });
//
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            String currentdate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_3);
//            String financalDate = "01/04/" + String.valueOf(financialYear);
//            Date date1 = dateFormat.parse(currentdate);
//            Date date2 = dateFormat.parse(financalDate);
//            long diff = date1.getTime() - date2.getTime();
//            String noOfDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
//            days = StringUtils.leftPad(noOfDays, 3, "0");
//            Log.v(LOG_TAG, "days -->" + days);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        scrollBottomIndicator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                interactiveScrollView.post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        interactiveScrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//        interactiveScrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
//            @Override
//            public void onBottomReached() {
//                scrollBottomIndicator.setVisibility(View.GONE);
//            }
//        });
//
//        interactiveScrollView.setOnTopReachedListener(new InteractiveScrollView.OnTopReachedListener() {
//            @Override
//            public void onTopReached() {
//            }
//        });
//
//        interactiveScrollView.setOnScrollingListener(new InteractiveScrollView.OnScrollingListener() {
//            @Override
//            public void onScrolling() {
//                Log.d(LOG_TAG, "onScrolling: ");
//                scrollBottomIndicator.setVisibility(View.VISIBLE);
//            }
//        });
//
//        weighbridgeCenterDataMap = dataAccessHandler.getGenericData(Queries.getInstance().getPrivateWeighbridgeDetails());
//        DataManager.getInstance().addData(PRIVATE_WEIGHBRIDGE_INFO, weighbridgeCenterDataMap);
//        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
//                CommonUtils.fromMap(weighbridgeCenterDataMap, "WeighbridgeCenter"));
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        nameOfPrivateWbSpin.setAdapter(spinnerArrayAdapter);
//
//        nameOfPrivateWbSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (weighbridgeCenterDataMap != null && weighbridgeCenterDataMap.size() > 0 && nameOfPrivateWbSpin.getSelectedItemPosition() != 0) {
//                    if (!CommonUtils.isEmptySpinner(nameOfPrivateWbSpin)) {
//                        int selectedPos = nameOfPrivateWbSpin.getSelectedItemPosition();
//                        wbId = weighbridgeCenterDataMap.keySet().toArray(new String[weighbridgeCenterDataMap.size()])[selectedPos - 1];
//                        Log.v(LOG_TAG, "@@@ selected wb center id " + wbId);
//                        String wbName = weighbridgeCenterDataMap.get(wbId);
//                        Log.v(LOG_TAG, "@@@ selected wb center name " + wbName);
//
//
//                    } else {
//                        if (typeSelected == NO_WEIGHBRIDGE) {
//                            dateTimeEdit.setText("");
//                        }
//                    }
//                } else {
//                    if (typeSelected == NO_WEIGHBRIDGE) {
//                        dateTimeEdit.setText("");
//
//
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//
//        dateTimeEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date_TimeofWeighing, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                datePickerDialog.show();
//
//
//              /*  new SlideDateTimePicker.Builder(getChildFragmentManager())
//                        .setListener(listener)
//                        .setInitialDate(new Date())
//                        .setMaxDate(new Date(System.currentTimeMillis()))
//                        .build()
//                        .show();*/
//            }
//        });
//
//        postDateTimeEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(System.currentTimeMillis());
//                cal.add(Calendar.DATE, -5);
//                Date dateBefore5Days = cal.getTime();
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), postingDate, myCalendar1
//                        .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
//                        myCalendar1.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
//                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                datePickerDialog.getDatePicker().setMinDate(dateBefore5Days.getTime());
//                datePickerDialog.show();
//
//
//            }
//        });
//
//
//        collectionIDTxt = (TextView) view.findViewById(R.id.collection_ID);
//        tokenNoText = (TextView) view.findViewById(R.id.tokenNoText);
//        if (ColFarmerWithOutplot.equalsIgnoreCase("CCFARMERW") || ColFarmerWithOutplot.equalsIgnoreCase("FFBFARMER")) {
//            collectionId = ccDataAccessHandler.getGeneratedCollectionCodeWithOutPlotFarmers(selectedCollectionCenter.getCode(),
//                    ccDataAccessHandler.COLLECTION_CENTER_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days);
//        } else {
//            collectionId = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.COLLECTION_CENTER_CODE_INITIAL,
//                    ccDataAccessHandler.TABLE_COLLECTION, days);
//        }
//        collectionIDTxt.setText("" + collectionId);
//        Log.v(LOG_TAG, "@@@ collection code " + collectionId);
//
//        tokenTable = dataAccessHandler.getLastTokenNoTa(Queries.getInstance().getTokenNoTa(selectedCollectionCenter.getCode()));
//
//
//        if (tokenTable != null) {
//            TokenNo = String.valueOf(tokenTable.getTokenNo() + 1);
//
//        } else {
//            TokenNo = days + "01";
//
//        }
//
//        tokenNoText.setText("" + TokenNo);
//
//        postDateTimeEdit.setText(DateFormats.getDateTime(new Date()));
//
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
//        postedDateAndTimeStr = objdateformat.format(c.getTime());
//    }
//
//





}