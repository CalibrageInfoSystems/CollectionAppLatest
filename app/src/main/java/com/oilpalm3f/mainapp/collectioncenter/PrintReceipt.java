package com.oilpalm3f.mainapp.collectioncenter;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionWithOutPlot;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.printer.PrinterChooserFragment;
import com.oilpalm3f.mainapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.EXTRA_PLOTS;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.PRIVATE_WEIGHBRIDGE_INFO;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_PLOT_DATA;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

//To Print the Collections
public class PrintReceipt extends BaseFragment implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected {
    private static final String LOG_TAG = PrintReceipt.class.getName();
    String receiptCode = "";
    private TextView vehicle_number, gross_weight, net_weight, number_of_bunches_rejected, weighBridge_centre,
            weighBridge_centre_value, private_weighbridge_center;
    private CollectionCenter selectedCollectionCenter;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, dateTimeTxt, receptNumTxt, farmerCodeTxt, farmerNameTxt,
            farmerAddressTxt, farmerBankAccNumTxt;
    private BasicFarmerDetails basicFarmerDetails;
    private View rootView;
    private Collection enteredData;
    private CollectionWithOutPlot enteredCOLFarmerWOPData;
    private DataAccessHandler dataAccessHandler;
    private CCDataAccessHandler ccDataAccessHandler;
    private Button confirmBtn;
    private ImageView scrollBottomIndicator;
    private InteractiveScrollView interactiveScrollView;
    private String privateWeighBridgeName;
    private LinkedHashMap<String, String> weighbridgeCenterDataMap;
    private PrinterInstance mPrinter;
    private String ColFarmerWithOutPlotcode;
    String date, currentDate_am_pm;
    String vehicleType;
    private String days = "";
    public int financialYear;
    LinearLayout privateWBLinear;
    LinearLayout vehiclenumberlyt, harvesternamelyt, harvestermobilenumberlyt, unripenlyt, underipenlyt, ripenlyt, overripelyt,diseasedlyt,emptybuncheslyt,
    longstocklyt,mediumstocklyt,shortstocklyt,optimumstocklyt;
    TextView harvesternametv, harvestermobilenumbertv, unripentv, underipentv, ripentv, overripetv,diseasedtv,emptybunchestv,
            longstocktv,mediumstocktv,shortstocktv,optimumstocktv;

    private int isCollectionexist = 0;
    private int isCollectionwithoutplotexist = 0;

    private String collectionsameday = "";

    public PrintReceipt() {
    }

    //Initializing the Class
    @SuppressLint("SetTextI18n")
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        rootView = inflater.inflate(R.layout.fragment_print_receipt, null);
        baseLayout.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getActivity().getResources().getString(R.string.cc_receipt));

        dataAccessHandler = new DataAccessHandler(getActivity());
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        Log.d("PselectedCollectionCenterStateId", selectedCollectionCenter.getStateId() + "");
        basicFarmerDetails = (BasicFarmerDetails) DataManager.getInstance().getDataFromManager(SELECTED_FARMER_DATA);
        ColFarmerWithOutPlotcode = FarmersDetailsScreen.firstthree;
        initViews();

        Calendar c = Calendar.getInstance();

        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        date = objdateformat.format(c.getTime());


        SimpleDateFormat dateFormat_am_pm = new SimpleDateFormat(CommonConstants.DATE_FORMAT_1, Locale.US);
        currentDate_am_pm = dateFormat_am_pm.format(c.getTime());

        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
            Bundle dataBundle = getArguments();
            if (dataBundle != null) {
                enteredCOLFarmerWOPData = dataBundle.getParcelable("collection_data");
                collectionsameday = dataBundle.getString("collectionsamedaywithoutfarmer");
            }
        } else {
            Bundle dataBundle = getArguments();
            if (dataBundle != null) {
                enteredData = dataBundle.getParcelable("collection_data");
                collectionsameday = dataBundle.getString("collectionsamedaywithfarmer");
            }
        }

        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
            if (null != enteredCOLFarmerWOPData.getWeighbridgecenterid() && enteredCOLFarmerWOPData.getWeighbridgecenterid() > 0) {
                privateWBLinear.setVisibility(View.VISIBLE);
                weighbridgeCenterDataMap = (LinkedHashMap) DataManager.getInstance().getDataFromManager(PRIVATE_WEIGHBRIDGE_INFO);
                privateWeighBridgeName = weighbridgeCenterDataMap.get(String.valueOf(enteredCOLFarmerWOPData.getWeighbridgecenterid()));
                if (TextUtils.isEmpty(privateWeighBridgeName)) {
                    privateWeighBridgeName = "";
                }

                private_weighbridge_center.setText("" + privateWeighBridgeName);
            }else{

                privateWBLinear.setVisibility(View.GONE);
            }
        } else {
            if (null != enteredData.getWeighbridgecenterid() && enteredData.getWeighbridgecenterid() > 0) {
                privateWBLinear.setVisibility(View.VISIBLE);
                weighbridgeCenterDataMap = (LinkedHashMap) DataManager.getInstance().getDataFromManager(PRIVATE_WEIGHBRIDGE_INFO);
                privateWeighBridgeName = weighbridgeCenterDataMap.get(String.valueOf(enteredData.getWeighbridgecenterid()));

                Log.d("privateWeighBridgeName", privateWeighBridgeName + "");

                if (TextUtils.isEmpty(privateWeighBridgeName)) {
                    privateWeighBridgeName = "";
                }
                private_weighbridge_center.setText("" + privateWeighBridgeName);
            }
            else{
                privateWBLinear.setVisibility(View.GONE);

            }
        }

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


        bindData();

        enablePrintBtn(true);
    }

    //Binding Data to the Fields
    @SuppressLint("SetTextI18n")
    private void bindData() {

//        Log.d("HarvesterName", enteredData.getName() + "");
//        Log.d("HarvesterNumber", enteredData.getMobileNumber() + "");
//        Log.d("UnRipenCount", enteredData.getUnRipen() + "");
        //Log.d("UnRipenCount", Integer.parseInt(enteredData.getUnRipen() + "") + "");




        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {

            vehicleType = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getvehicleTypeName(enteredCOLFarmerWOPData.getVehicleTypeId()));
            Log.d("vehicleType is", vehicleType + "");

            //Log.d("getVehiclenumber", enteredCOLFarmerWOPData.getVehiclenumber() + "");

            if (enteredCOLFarmerWOPData.getVehiclenumber().equalsIgnoreCase("null")){

            vehiclenumberlyt.setVisibility(View.GONE);
        }else{
            vehiclenumberlyt.setVisibility(View.VISIBLE);

        }

            if (enteredCOLFarmerWOPData.getName() ==  ""){
                harvesternamelyt.setVisibility(View.GONE);
            }else{

                harvesternamelyt.setVisibility(View.VISIBLE);
            }

            if (enteredCOLFarmerWOPData.getMobileNumber() ==  ""){
                harvestermobilenumberlyt.setVisibility(View.GONE);
            }else{

                harvestermobilenumberlyt.setVisibility(View.VISIBLE);
            }

        }

        else {
            vehicleType = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getvehicleTypeName(enteredData.getVehicleTypeId()));

            Log.d("vehicleType is", vehicleType + "");

            if (enteredData.getVehiclenumber().equalsIgnoreCase("null")){

                vehiclenumberlyt.setVisibility(View.GONE);
            }else{
                vehiclenumberlyt.setVisibility(View.VISIBLE);

            }

            Log.d("PHarvesterName", enteredData.getName() + "");

            if (enteredData.getName() ==  ""){
                harvesternamelyt.setVisibility(View.GONE);
            }else{

                harvesternamelyt.setVisibility(View.VISIBLE);
            }

            if (enteredData.getMobileNumber() ==  ""){
                harvestermobilenumberlyt.setVisibility(View.GONE);
            }else{

                harvestermobilenumberlyt.setVisibility(View.VISIBLE);
            }

        }

        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))){
            vehicle_number.setText(enteredCOLFarmerWOPData.getVehiclenumber());
            gross_weight.setText("" + enteredCOLFarmerWOPData.getGrossweight());
            net_weight.setText("" + enteredCOLFarmerWOPData.getNetweight());
            number_of_bunches_rejected.setText("" + enteredCOLFarmerWOPData.getRejectedbunches());
            harvesternametv.setText(enteredCOLFarmerWOPData.getName() + "");
            harvestermobilenumbertv.setText(enteredCOLFarmerWOPData.getMobileNumber() +"");
            receptNumTxt.setText("" + ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, collectionsameday));

            if (enteredCOLFarmerWOPData.getUnRipen() == 0){
                unripenlyt.setVisibility(View.GONE);
            }else{
                unripenlyt.setVisibility(View.VISIBLE);
                unripentv.setText(Math.round(enteredCOLFarmerWOPData.getUnRipen()) + "");
            }
            if (enteredCOLFarmerWOPData.getUnderRipe() == 0){
                underipenlyt.setVisibility(View.GONE);
            }else{
                underipenlyt.setVisibility(View.VISIBLE);
                underipentv.setText(Math.round(enteredCOLFarmerWOPData.getUnderRipe()) + "");
            }
            if (enteredCOLFarmerWOPData.getRipen() == 0){
                ripenlyt.setVisibility(View.GONE);
            }else{
                ripenlyt.setVisibility(View.VISIBLE);
                ripentv.setText(Math.round(enteredCOLFarmerWOPData.getRipen()) + "");
            }
            if (enteredCOLFarmerWOPData.getOverRipe() == 0){
                overripelyt.setVisibility(View.GONE);
            }else{
                overripelyt.setVisibility(View.VISIBLE);
                overripetv.setText(Math.round(enteredCOLFarmerWOPData.getOverRipe()) + "");
            }
            if (enteredCOLFarmerWOPData.getDiseased() == 0){
                diseasedlyt.setVisibility(View.GONE);
            }else{
                diseasedlyt.setVisibility(View.VISIBLE);
                diseasedtv.setText(Math.round(enteredCOLFarmerWOPData.getDiseased()) + "");
            }
            if (enteredCOLFarmerWOPData.getEmptyBunches() == 0){
                emptybuncheslyt.setVisibility(View.GONE);
            }else{
                emptybuncheslyt.setVisibility(View.VISIBLE);
                emptybunchestv.setText(Math.round(enteredCOLFarmerWOPData.getEmptyBunches()) + "");
            }


            if (enteredCOLFarmerWOPData.getFFBQualityLong() == 0){
                longstocklyt.setVisibility(View.GONE);
            }else{
                longstocklyt.setVisibility(View.VISIBLE);
                longstocktv.setText(Math.round(enteredCOLFarmerWOPData.getFFBQualityLong()) + "");
            }

            if (enteredCOLFarmerWOPData.getFFBQualityMedium() == 0){
                mediumstocklyt.setVisibility(View.GONE);
            }else{
                mediumstocklyt.setVisibility(View.VISIBLE);
                mediumstocktv.setText(Math.round(enteredCOLFarmerWOPData.getFFBQualityMedium()) + "");
            }

            if (enteredCOLFarmerWOPData.getFFBQualityShort() == 0){
                shortstocklyt.setVisibility(View.GONE);
            }else{
                shortstocklyt.setVisibility(View.VISIBLE);
                shortstocktv.setText(Math.round(enteredCOLFarmerWOPData.getFFBQualityShort()) + "");
            }

            if (enteredCOLFarmerWOPData.getFFBQualityOptimum() == 0){
                optimumstocklyt.setVisibility(View.GONE);
            }else{
                optimumstocklyt.setVisibility(View.VISIBLE);
                optimumstocktv.setText(Math.round(enteredCOLFarmerWOPData.getFFBQualityOptimum()) + "");
            }

        } else {
            vehicle_number.setText(enteredData.getVehiclenumber());
            gross_weight.setText("" + enteredData.getGrossweight());
            net_weight.setText("" + enteredData.getNetweight());
            number_of_bunches_rejected.setText("" + enteredData.getRejectedbunches());
            harvesternametv.setText(enteredData.getName() + "");
            harvestermobilenumbertv.setText(enteredData.getMobileNumber() +"");
            receptNumTxt.setText("" + ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, collectionsameday));

            if (enteredData.getUnRipen() == 0){
                unripenlyt.setVisibility(View.GONE);
            }else{
                unripenlyt.setVisibility(View.VISIBLE);
                unripentv.setText(Math.round(enteredData.getUnRipen()) + "");
            }
            if (enteredData.getUnderRipe() == 0){
                underipenlyt.setVisibility(View.GONE);
            }else{
                underipenlyt.setVisibility(View.VISIBLE);
                underipentv.setText(Math.round(enteredData.getUnderRipe()) + "");
            }
            if (enteredData.getRipen() == 0){
                ripenlyt.setVisibility(View.GONE);
            }else{
                ripenlyt.setVisibility(View.VISIBLE);
                ripentv.setText(Math.round(enteredData.getRipen()) + "");
            }
            if (enteredData.getOverRipe() == 0){
                overripelyt.setVisibility(View.GONE);
            }else{
                overripelyt.setVisibility(View.VISIBLE);
                overripetv.setText(Math.round(enteredData.getOverRipe()) + "");
            }
            if (enteredData.getDiseased() == 0){
                diseasedlyt.setVisibility(View.GONE);
            }else{
                diseasedlyt.setVisibility(View.VISIBLE);
                diseasedtv.setText(Math.round(enteredData.getDiseased()) + "");
            }
            if (enteredData.getEmptyBunches() == 0){
                emptybuncheslyt.setVisibility(View.GONE);
            }else{
                emptybuncheslyt.setVisibility(View.VISIBLE);
                emptybunchestv.setText(Math.round(enteredData.getEmptyBunches()) + "");
            }


            if (enteredData.getFFBQualityLong() == 0){
                longstocklyt.setVisibility(View.GONE);
            }else{
                longstocklyt.setVisibility(View.VISIBLE);
                longstocktv.setText(Math.round(enteredData.getFFBQualityLong()) + "");
            }

            if (enteredData.getFFBQualityMedium() == 0){
                mediumstocklyt.setVisibility(View.GONE);
            }else{
                mediumstocklyt.setVisibility(View.VISIBLE);
                mediumstocktv.setText(Math.round(enteredData.getFFBQualityMedium()) + "");
            }

            if (enteredData.getFFBQualityShort() == 0){
                shortstocklyt.setVisibility(View.GONE);
            }else{
                shortstocklyt.setVisibility(View.VISIBLE);
                shortstocktv.setText(Math.round(enteredData.getFFBQualityShort()) + "");
            }

            if (enteredData.getFFBQualityOptimum() == 0){
                optimumstocklyt.setVisibility(View.GONE);
            }else{
                optimumstocklyt.setVisibility(View.VISIBLE);
                optimumstocktv.setText(Math.round(enteredData.getFFBQualityOptimum()) + "");
            }



        }
        weighBridge_centre.setText("W. c");
        weighBridge_centre_value.setText("w.c name");

        dateTimeTxt.setText(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS1));
        farmerCodeTxt.setText("" + basicFarmerDetails.getFarmerCode().trim());
        farmerNameTxt.setText("" + basicFarmerDetails.getFarmerFirstName().trim());

        if (!TextUtils.isEmpty(basicFarmerDetails.getAddress1()) && !
                basicFarmerDetails.getAddress1().equalsIgnoreCase("null")) {
            farmerAddressTxt.setText("" + basicFarmerDetails.getAddress1().trim());
        }
        farmerBankAccNumTxt.setText("" + basicFarmerDetails.getBankAccountNumber().split("@")[0]);
    }

    //Initializing the UI
    public void initViews() {
        collectionCenterName = rootView.findViewById(R.id.collection_center_name);
        collectionCenterCode = rootView.findViewById(R.id.collection_center_code);
        collectionCenterVillage = rootView.findViewById(R.id.collection_center_village);
        vehicle_number = rootView.findViewById(R.id.vehicle_number);
        vehiclenumberlyt = rootView.findViewById(R.id.vehiclenumberlyt);
        gross_weight = rootView.findViewById(R.id.gross_weight);
        net_weight = rootView.findViewById(R.id.net_weight);
        number_of_bunches_rejected = rootView.findViewById(R.id.number_of_bunches_rejected);
        weighBridge_centre = rootView.findViewById(R.id.weighbridge_centre);
        weighBridge_centre_value = rootView.findViewById(R.id.weighbridge_centre_value);
        private_weighbridge_center = rootView.findViewById(R.id.private_weighbridge_center);
        privateWBLinear = rootView.findViewById(R.id.privateWBLinear);

        harvesternametv = rootView.findViewById(R.id.harvestorname_tv);
        harvestermobilenumbertv = rootView.findViewById(R.id.harvestornumber_tv);

        harvesternamelyt = rootView.findViewById(R.id.harvestornameLL);
        harvestermobilenumberlyt = rootView.findViewById(R.id.harvestornumberLL);


        unripenlyt = rootView.findViewById(R.id.unripenLL);
        underipenlyt = rootView.findViewById(R.id.underripeLL);
        ripenlyt = rootView.findViewById(R.id.ripenLL);
        overripelyt = rootView.findViewById(R.id.overriperipeLL);
        diseasedlyt = rootView.findViewById(R.id.diseasedLL);
        emptybuncheslyt = rootView.findViewById(R.id.emptybunchesLL);
        longstocklyt = rootView.findViewById(R.id.longLL);
        mediumstocklyt = rootView.findViewById(R.id.mediumLL);
        shortstocklyt = rootView.findViewById(R.id.shortLL);
        optimumstocklyt = rootView.findViewById(R.id.optimumLL);

        unripentv = rootView.findViewById(R.id.unripen_tv);
        underipentv = rootView.findViewById(R.id.underripe_tv);
        ripentv = rootView.findViewById(R.id.ripen_tv);
        overripetv = rootView.findViewById(R.id.overripe_tv);
        diseasedtv = rootView.findViewById(R.id.diseased_tv);
        emptybunchestv = rootView.findViewById(R.id.emptybunches_tv);
        longstocktv = rootView.findViewById(R.id.longstock_tv);
        mediumstocktv = rootView.findViewById(R.id.mediumstock_tv);
        shortstocktv = rootView.findViewById(R.id.shortstock_tv);
        optimumstocktv = rootView.findViewById(R.id.optimumstock_tv);


        dateTimeTxt = rootView.findViewById(R.id.date);
        receptNumTxt = rootView.findViewById(R.id.receipt_number);
        farmerCodeTxt = rootView.findViewById(R.id.farmer_id);
        farmerNameTxt = rootView.findViewById(R.id.farmer_name);
        farmerAddressTxt = rootView.findViewById(R.id.address);
        farmerBankAccNumTxt = rootView.findViewById(R.id.bank_ac_number);

        confirmBtn = rootView.findViewById(R.id.confirm_print);
        scrollBottomIndicator = rootView.findViewById(R.id.bottomScroll);
        interactiveScrollView = rootView.findViewById(R.id.scrollView);
        scrollBottomIndicator.setVisibility(View.VISIBLE);
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

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              enablePrintBtn(false);

              //isCollectionexist = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isCollectionExists(enteredData.getCode()));
              //isCollectionwithoutplotexist=dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isCollectionwithoutPlotExists(enteredCOLFarmerWOPData.getCode()));
             // Log.d("isCollectionexist", isCollectionexist + "");

                  if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))){

                      isCollectionwithoutplotexist=dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isCollectionwithoutPlotExists(enteredCOLFarmerWOPData.getCode()));
                      if (isCollectionwithoutplotexist == 0){
                          saveCollectionFarmerWithOutPlotData();
                          FragmentManager fm = getChildFragmentManager();
                          PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                          printerChooserFragment.setCancelable(false);
                          printerChooserFragment.setPrinterType (PrintReceipt.this);
                          printerChooserFragment.show(fm, "bluetooth fragment");
                      }else{
                          FragmentManager fm = getChildFragmentManager();
                          PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                          printerChooserFragment.setCancelable(false);
                          printerChooserFragment.setPrinterType (PrintReceipt.this);
                          printerChooserFragment.show(fm, "bluetooth fragment");
                      }
                  }else {
                      isCollectionexist = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isCollectionExists(enteredData.getCode()));
                      if (isCollectionexist == 0) {

                          saveCollectionData();
                          FragmentManager fm = getChildFragmentManager();
                          PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                          printerChooserFragment.setCancelable(false);
                          printerChooserFragment.setPrinterType(PrintReceipt.this);
                          printerChooserFragment.show(fm, "bluetooth fragment");
                      }else{
                          FragmentManager fm = getChildFragmentManager();
                          PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                          printerChooserFragment.setCancelable(false);
                          printerChooserFragment.setPrinterType (PrintReceipt.this);
                          printerChooserFragment.show(fm, "bluetooth fragment");
                      }
                  }


//                if(entereddata != null){
//                    if(collectionexists == 0){
//                        save() & print();
//                    }else{
//                        print();
//                    }
//                }else{
//                    if(collectionwithoutplotexists == 0){
//                        save() & print();
//                    }else {
//                        print();
//                    }
//                }

            }
        });
    }

    //To Save Collection Without Plot Data
    public void saveCollectionFarmerWithOutPlotData() {
        receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, collectionsameday);
        enteredCOLFarmerWOPData.setFarmercode(basicFarmerDetails.getFarmerCode());
        enteredCOLFarmerWOPData.setRecieptcode(receiptCode);
        enteredCOLFarmerWOPData.setCreatedDate(date);
        enteredCOLFarmerWOPData.setIsActive(1);
        enteredCOLFarmerWOPData.setRecieptname(receiptCode);
        enteredCOLFarmerWOPData.setCreatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
        enteredCOLFarmerWOPData.setUpdatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
        enteredCOLFarmerWOPData.setServerUpdatedStatus(false);
        enteredCOLFarmerWOPData.setUpdatedDate(date);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JSONObject ccData = null;
        List dataToInsert = null;
        try {
            ccData = new JSONObject(gson.toJson(enteredCOLFarmerWOPData));
            dataToInsert = new ArrayList();
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionFarmerWithOutPlotData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            Log.e(LOG_TAG, "@@@ error while converting CollectionFarmerWithOutPlot data");
        }
        Log.v(LOG_TAG, "@@ enteredCollectionFarmerWithOutPlot data " + ccData.toString());
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
        dataAccessHandler.insertData(ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, dataToInsert, getActivity(), new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionFarmerWithOutPlotData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    if (!TextUtils.isEmpty(enteredCOLFarmerWOPData.getRecieptlocation()) && enteredCOLFarmerWOPData.getRecieptlocation().length() > 0) {
                        dataAccessHandler.insertImageDataColectionFarmer(enteredCOLFarmerWOPData.getCode(), enteredCOLFarmerWOPData.getFarmercode(), enteredCOLFarmerWOPData.getRecieptlocation(), "false");
                    }
                    Log.v(LOG_TAG, "@@@ collection center withoutPlot data saved successfully");
                    Log.v(LOG_TAG, "@@@ data saved successfully for " + ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot);
                    UiUtils.showCustomToastMessage("Data saved", getActivity(), 0);

                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        CommonUtils.isNotSyncScreen = false;
                        DataSyncHelper.performCollectionCenterTransactionsSync(getActivity(), new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionFarmerWithOutPlotData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    enablePrintBtn(true);
                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                            UiUtils.showCustomToastMessage("Successfully data sent to server", getActivity(), 0);
                                            //getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                                        getActivity().finish();
                                        }
                                    });
                                } else {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionFarmerWithOutPlotData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                        @Override
                                        public void run() {
                                            UiUtils.showCustomToastMessage("Data sync failed", getActivity(), 1);

                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        enablePrintBtn(true);
                        //getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                    getActivity().finish();
                    }

                } else {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionFarmerWithOutPlotData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.e(LOG_TAG, "@@@ collection center without plot data saving failed due to " + msg);
                }
            }
        });
    }

    //To Save Collection Data
    public void saveCollectionData() {
        receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, collectionsameday);
        enteredData.setFarmercode(basicFarmerDetails.getFarmerCode());
        enteredData.setRecieptcode(receiptCode);
        enteredData.setCreatedDate(date);
//        enteredData.setRecieptGeneratedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        enteredData.setIsActive(1);
        enteredData.setRecieptname(receiptCode);
        enteredData.setCreatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
        enteredData.setUpdatedbyuserid(!TextUtils.isEmpty(CommonConstants.USER_ID) ? Integer.parseInt(CommonConstants.USER_ID) : 0);
        enteredData.setServerUpdatedStatus(false);
        enteredData.setUpdatedDate(date);

        if (basicFarmerDetails.getFarmerCode().contains("DUMMY")) {
            enteredData.setCollectionWithOutFarmer(true);
        } else {
            enteredData.setCollectionWithOutFarmer(false);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        JSONObject ccData = null;
        List dataToInsert = null;
        try {
            ccData = new JSONObject(gson.toJson(enteredData));
            dataToInsert = new ArrayList();
            dataToInsert.add(CommonUtils.toMap(ccData));
        } catch (JSONException e) {
            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            Log.e(LOG_TAG, "@@@ error while converting collection center data");
        }
        Log.v(LOG_TAG, "@@ entered data " + ccData.toString());
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
        dataAccessHandler.insertData(ccDataAccessHandler.TABLE_COLLECTION, dataToInsert, getActivity(), new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.v(LOG_TAG, "@@@ collection center data saved successfully");
                    dataAccessHandler.insertData(ccDataAccessHandler.TABLE_COLLECTION_PLOT_XREF, getCollectionXrefData(), getActivity(), new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                if (!TextUtils.isEmpty(enteredData.getRecieptlocation()) && enteredData.getRecieptlocation().length() > 0) {
                                    dataAccessHandler.insertImageData(enteredData.getCode(), enteredData.getFarmercode(), enteredData.getRecieptlocation(), "false");
                                }
                                Log.v(LOG_TAG, "@@@ data saved successfully for " + ccDataAccessHandler.TABLE_COLLECTION_PLOT_XREF);
                                UiUtils.showCustomToastMessage("Data saved", getActivity(), 0);
                                if (CommonUtils.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                                    DataSyncHelper.performCollectionCenterTransactionsSync(getActivity(), new ApplicationThread.OnComplete() {
                                        @Override
                                        public void execute(boolean success, Object result, String msg) {
                                            if (success) {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                enablePrintBtn(true);
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        UiUtils.showCustomToastMessage("Successfully data sent to server", getActivity(), 0);
                                                        //getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                                        getActivity().finish();
                                                    }
                                                });
                                            } else {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        UiUtils.showCustomToastMessage("Data sync failed", getActivity(), 1);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    enablePrintBtn(true);
                                    //getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                    getActivity().finish();
                                }
                            } else {
                                enablePrintBtn(true);
                                UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);
                                Log.e(LOG_TAG, "@@@ collection center x ref data saving failed due to " + msg);
                            }
                        }
                    });
                } else {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveCollectionData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.e(LOG_TAG, "@@@ collection center data saving failed due to " + msg);
                }
            }
        });
    }

    //Saving Collection Xref Data
    public List getCollectionXrefData() {
        List<String> selectedPlotCodes = (List) DataManager.getInstance().getDataFromManager(SELECTED_FARMER_PLOT_DATA);
        List<String> extrasPlotCodes = (List) DataManager.getInstance().getDataFromManager(EXTRA_PLOTS);
        selectedPlotCodes = CommonUtils.ignoreDuplicatedInArrayList(selectedPlotCodes);
        extrasPlotCodes = CommonUtils.ignoreDuplicatedInArrayList(extrasPlotCodes);
        List dataList = new ArrayList();
        if (null != selectedPlotCodes && !selectedPlotCodes.isEmpty()) {
            for (String plotId : selectedPlotCodes) {
                Log.v(LOG_TAG, "@@@ selected plot id " + plotId);
                LinkedHashMap dataMap = new LinkedHashMap();
                dataMap.put("CollectionCode", enteredData.getCode());
                dataMap.put("PlotCode", plotId);
                dataMap.put("YieldPerHectar", 0);
                dataMap.put("ServerUpdatedStatus", false);
                dataMap.put("NetWeightPerPlot", 0);
                dataMap.put("IsMainFarmerPlot", true);
                dataList.add(dataMap);
            }
        }

        if (null != extrasPlotCodes && !extrasPlotCodes.isEmpty()) {
            for (String plotId : extrasPlotCodes) {
                Log.v(LOG_TAG, "@@@ extra selected plot id " + plotId);

                if ((selectedPlotCodes != null && !selectedPlotCodes.contains(plotId))) {
                    LinkedHashMap dataMap = new LinkedHashMap();
                    dataMap.put("CollectionCode", enteredData.getCode());
                    dataMap.put("PlotCode", plotId);
                    dataMap.put("YieldPerHectar", 0);
                    dataMap.put("ServerUpdatedStatus", false);
                    dataMap.put("NetWeightPerPlot", 0);
                    dataMap.put("IsMainFarmerPlot", false);
                    dataList.add(dataMap);
                } else {
                    Log.v(LOG_TAG, "@@@ extra selected plot id " + plotId);
                }

            }
        }
        return dataList;
    }

    //Letting know how many times data should Print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        Log.v(LOG_TAG, "selected address is ");
        if (null != printerInstance) {
            enablePrintBtn(false);
            for (int i = 0; i < 3; i++) {
                printCollectionData(printerInstance, false, i);
            }
        } else {
            UiUtils.showCustomToastMessage("Printing failed", getActivity(), 1);
        }
    }

    @Override
    public void enablingPrintButton(boolean rePrint) {
        enablePrintBtn(rePrint);
    }

    //Navigates to the Printer
    @Override
    public void onPrinterTypeSelected(int printerType) {
        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getChildFragmentManager();
            UsbDevicesListFragment usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setCancelable(false);
            usbDevicesListFragment.setOnUsbDeviceSelected(PrintReceipt.this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getChildFragmentManager();
            BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setCancelable(false);
            bluetoothDevicesFragment.setOnDeviceSelected(PrintReceipt.this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }


    //Print Collection Data
    public void printCollectionData(PrinterInstance mPrinter, boolean isReprint, int printCount) {
//        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
//            receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_WithOutPlot_INITIAL, ccDataAccessHandler.TABLE_COLLECTION_WithOutPlot, days);
//
//        } else {
//            receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_COLLECTION, days);
//        }
        String middleName = "";
        if (!TextUtils.isEmpty(basicFarmerDetails.getFarmerMiddleName()) && !
                basicFarmerDetails.getFarmerMiddleName().equalsIgnoreCase("null")) {
            middleName = basicFarmerDetails.getFarmerMiddleName();
        }
        this.mPrinter = mPrinter;
        String farmerName = basicFarmerDetails.getFarmerFirstName() + " " + middleName + " " + basicFarmerDetails.getFarmerLastName();
        String bankName = basicFarmerDetails.getBankName();
        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        //mPrinter.printText(" 3F Oil Palm AgroTech PVT LTD " + "\n");
//        if (!basicFarmerDetails.getFarmerCode().startsWith("FFBFARMER")) {
//            mPrinter.printText(" 3F Oil Palm AgroTech PVT LTD " + "\n");
//        }

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
     //   mPrinter.printText("   Oil Palm F.F.B Receipt " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        sb.append("--------------------------------------------" + "\n");
        sb.append("  DateTime: ");
        sb.append(" ").append(currentDate_am_pm).append("\n");
        sb.append(" ");
        if (!basicFarmerDetails.getFarmerCode().startsWith("FFBFARMER")) {

            sb.append(" Receipt Number: ");
            sb.append(" ").append(receiptCode).append("\n");
        }
        sb.append(" ");

        if (!basicFarmerDetails.getFarmerCode().startsWith("FFBFARMER")) {

            sb.append(" CC name: ");
            sb.append(" ").append(selectedCollectionCenter.getName()).append("\n");
        }
        //sb.append(" CC name: ");
        //sb.append(" ").append(selectedCollectionCenter.getName()).append("\n");
        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
            sb.append(" ");
            sb.append(" Vehicle Type: ").append(vehicleType + "").append("\n");
            sb.append(" ");
            if (!enteredCOLFarmerWOPData.getVehiclenumber().equalsIgnoreCase("null")) {
                sb.append(" Vehicle Number: ").append(enteredCOLFarmerWOPData.getVehiclenumber()).append("\n");
            }

        } else {
            sb.append(" ");
            sb.append(" Vehicle Type: ").append(vehicleType + "").append("\n");
            sb.append(" ");
            if (!enteredData.getVehiclenumber().equalsIgnoreCase("null")) {
                sb.append(" Vehicle Number: ").append(enteredData.getVehiclenumber()).append("\n");
            }


        }
        sb.append(" ");
        if (!basicFarmerDetails.getFarmerCode().startsWith("FFBFARMER")) {

            sb.append(" Farmer ID: ");
            sb.append(" ").append(basicFarmerDetails.getFarmerCode()).append("\n");

            sb.append(" ");
            sb.append(" Farmer Name: ");
            sb.append(" ").append(farmerName).append("\n");

            sb.append(" ");
            String account[] = basicFarmerDetails.getBankAccountNumber().split("@");
            if (!account[1].isEmpty()) {
                sb.append(" A/c holder: ").append(account[1]).append("\n");
                sb.append(" ");
            } else {
                sb.append(" A/c holder:" + "--NA--" + "\n");
                sb.append(" ");
            }
            if (!account[0].isEmpty()) {
                sb.append(" A/c No: ").append(account[0]).append("\n");
                sb.append(" ");
            } else {
                sb.append("  A/c No:" + "--NA--" + "\n");
                sb.append(" ");
            }
            sb.append(" Bank name: ").append(bankName).append("\n");
            sb.append(" ");
            sb.append(" Branch name: ").append(basicFarmerDetails.getBranchName()).append("\n");
        }
        sb.append(" ");
        sb.append("-----------------------------------------------\n");
        sb.append("  Commodity: FFB" + "\n");
        sb.append("-----------------------------------------------\n");
        if (!basicFarmerDetails.getFarmerCode().startsWith("FFBFARMER")) {

            if (!TextUtils.isEmpty(privateWeighBridgeName)) {
                sb.append(" ");
                sb.append(" Private Weighbridge Centre: " + "\n");
                sb.append("  ").append(privateWeighBridgeName).append("\n");
                sb.append(" ");
            }
        }
        if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW")||(ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
            sb.append(" ");
            sb.append(" Gross Weight(Kgs) : ").append(enteredCOLFarmerWOPData.getGrossweight()).append("\n");
            sb.append(" ");
            sb.append(" Tare weight(Kgs) : ").append(enteredCOLFarmerWOPData.getTareweight()).append("\n");
            sb.append(" ");
            sb.append(" Net weight(Kgs) : ").append(enteredCOLFarmerWOPData.getNetweight()).append("\n");
            sb.append(" ");
            sb.append(" No of bunches rejected : ").append(enteredCOLFarmerWOPData.getRejectedbunches()).append("\n");



            if (enteredCOLFarmerWOPData.getName() !=  "" || enteredCOLFarmerWOPData.getMobileNumber() !=  "" ) {

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  Harvester Details" + "\n");
                sb.append("-----------------------------------------------\n");
                if (enteredCOLFarmerWOPData.getName() != "") {
                    sb.append(" ");
                    sb.append(" Harvester Name: ").append(enteredCOLFarmerWOPData.getName()).append("\n");
                }
                if (enteredCOLFarmerWOPData.getMobileNumber() != "") {
                    sb.append(" ");
                    sb.append(" Harvester Mobile Number: ").append(enteredCOLFarmerWOPData.getMobileNumber()).append("\n");
                }
            }

            if (selectedCollectionCenter.getStateId() == 1) {

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  FFB Quality Details" + "\n");
                sb.append("-----------------------------------------------\n");

                if (enteredCOLFarmerWOPData.getUnRipen() != 0) {
                    sb.append(" ");
                    sb.append(" Unripen : ").append(Math.round(enteredCOLFarmerWOPData.getUnRipen()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getUnderRipe() != 0) {
                    sb.append(" ");
                    sb.append(" Under Ripe : ").append(Math.round(enteredCOLFarmerWOPData.getUnderRipe()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getRipen() != 0) {
                    sb.append(" ");
                    sb.append(" Ripen : ").append(Math.round(enteredCOLFarmerWOPData.getRipen()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getOverRipe() != 0) {
                    sb.append(" ");
                    sb.append(" Over Ripe : ").append(Math.round(enteredCOLFarmerWOPData.getOverRipe()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getDiseased() != 0) {
                    sb.append(" ");
                    sb.append(" Diseased : ").append(Math.round(enteredCOLFarmerWOPData.getDiseased()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getEmptyBunches() != 0) {
                    sb.append(" ");
                    sb.append(" Empty Bunch's : ").append(Math.round(enteredCOLFarmerWOPData.getEmptyBunches()) + "%").append("\n");
                }

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  Stalk Quality Details" + "\n");
                sb.append("-----------------------------------------------\n");

                if (enteredCOLFarmerWOPData.getFFBQualityLong() != 0) {
                    sb.append(" ");
                    sb.append(" Long : ").append(Math.round(enteredCOLFarmerWOPData.getFFBQualityLong()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getFFBQualityMedium() != 0) {
                    sb.append(" ");
                    sb.append(" Medium : ").append(Math.round(enteredCOLFarmerWOPData.getFFBQualityMedium()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getFFBQualityShort() != 0) {
                    sb.append(" ");
                    sb.append(" Short : ").append(Math.round(enteredCOLFarmerWOPData.getFFBQualityShort()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getFFBQualityOptimum() != 0) {
                    sb.append(" ");
                    sb.append(" Optimum : ").append(Math.round(enteredCOLFarmerWOPData.getFFBQualityOptimum()) + "%").append("\n");
                }
                if (enteredCOLFarmerWOPData.getLooseFruitWeight() != null){

                    sb.append(" ");
                    sb.append(" Loose Fruit Approx.Quantity : ").append(enteredCOLFarmerWOPData.getLooseFruitWeight() + "Kg").append("\n");

                }



            }


        } else {
            sb.append(" ");
            sb.append(" Gross Weight(Kgs) : ").append(enteredData.getGrossweight()).append("\n");
            sb.append(" ");
            sb.append(" Tare weight(Kgs) : ").append(enteredData.getTareweight()).append("\n");
            sb.append(" ");
            sb.append(" Net weight(Kgs) : ").append(enteredData.getNetweight()).append("\n");
            sb.append(" ");
            sb.append(" No of bunches rejected : ").append(enteredData.getRejectedbunches()).append("\n");

            if (enteredData.getName() !=  "" || enteredData.getMobileNumber() !=  "" ) {

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  Harvester Details" + "\n");
                sb.append("-----------------------------------------------\n");

                if (enteredData.getName() != "") {
                    sb.append(" ");
                    sb.append(" Harvester Name: ").append(enteredData.getName()).append("\n");
                }
                if (enteredData.getMobileNumber() != "") {
                    sb.append(" ");
                    sb.append(" Harvester Mobile Number: ").append(enteredData.getMobileNumber()).append("\n");
                }
            }

            if (selectedCollectionCenter.getStateId() == 1) {

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  FFB Quality Details" + "\n");
                sb.append("-----------------------------------------------\n");

                if (enteredData.getUnRipen() != 0) {
                    sb.append(" ");
                    sb.append(" Unripen : ").append(Math.round(enteredData.getUnRipen()) + "%").append("\n");
                }
                if (enteredData.getUnderRipe() != 0) {
                    sb.append(" ");
                    sb.append(" Under Ripe : ").append(Math.round(enteredData.getUnderRipe()) + "%").append("\n");
                }
                if (enteredData.getRipen() != 0) {
                    sb.append(" ");
                    sb.append(" Ripen : ").append(Math.round(enteredData.getRipen()) + "%").append("\n");
                }
                if (enteredData.getOverRipe() != 0) {
                    sb.append(" ");
                    sb.append(" Over Ripe : ").append(Math.round(enteredData.getOverRipe()) + "%").append("\n");
                }
                if (enteredData.getDiseased() != 0) {
                    sb.append(" ");
                    sb.append(" Diseased : ").append(Math.round(enteredData.getDiseased()) + "%").append("\n");
                }
                if (enteredData.getEmptyBunches() != 0) {
                    sb.append(" ");
                    sb.append(" Empty Bunch's : ").append(Math.round(enteredData.getEmptyBunches()) + "%").append("\n");
                }

                sb.append(" ");
                sb.append("-----------------------------------------------\n");
                sb.append("  Stalk Quality Details" + "\n");
                sb.append("-----------------------------------------------\n");

                if (enteredData.getFFBQualityLong() != 0) {
                    sb.append(" ");
                    sb.append(" Long : ").append(Math.round(enteredData.getFFBQualityLong()) + "%").append("\n");
                }
                if (enteredData.getFFBQualityMedium() != 0) {
                    sb.append(" ");
                    sb.append(" Medium : ").append(Math.round(enteredData.getFFBQualityMedium()) + "%").append("\n");
                }
                if (enteredData.getFFBQualityShort() != 0) {
                    sb.append(" ");
                    sb.append(" Short : ").append(Math.round(enteredData.getFFBQualityShort()) + "%").append("\n");
                }
                if (enteredData.getFFBQualityOptimum() != 0) {
                    sb.append(" ");
                    sb.append(" Optimum : ").append(Math.round(enteredData.getFFBQualityOptimum()) + "%").append("\n");
                }

                if (enteredData.getLooseFruitWeight() != null) {
                    sb.append(" ");
                    sb.append(" Loose Fruit Approx.Quantity : ").append(enteredData.getLooseFruitWeight() + " Kg").append("\n");
                }
            }

        }
        sb.append("-----------------------------------------------\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" CC Officer signature");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" Farmer signature");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");

        mPrinter.printText(sb.toString());

        boolean printSuccess = false;
        try {
            if (farmerName.length() > 30 || bankName.length() > 30) {
                mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_RETURN_STANDARD, 2);
            } else {
                mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            }
//            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

            printSuccess = true;
        } catch (Exception e) {
            android.util.Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failed due to " + e.getMessage(), getActivity(), 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                if (printCount == 2) {
                    /*if (ColFarmerWithOutPlotcode.equalsIgnoreCase("CCFARMERW") || (ColFarmerWithOutPlotcode.equalsIgnoreCase("FFBFARMER"))) {
                        saveCollectionFarmerWithOutPlotData();
                    } else {
                        saveCollectionData();
                    }*/

                    UiUtils.showCustomToastMessage("Print Success", getActivity(), 0);
                    getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));

                }
            }
        }
    }


    public void showRepPrintDialog(final FragmentActivity appCompatActivity) {
        new AlertDialog.Builder(appCompatActivity)
                .setTitle("Re Print")
                .setMessage("Do you want to take re print ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        printCollectionData(mPrinter, true, getActivity());
                    }
                })

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveCollectionData();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    //Show/hide Print Button
    public void enablePrintBtn(final boolean enable) {
        ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
            @Override
            public void run() {
                confirmBtn.setEnabled(enable);
                confirmBtn.setClickable(enable);
                confirmBtn.setFocusable(enable);
            }
        });

    }

}
