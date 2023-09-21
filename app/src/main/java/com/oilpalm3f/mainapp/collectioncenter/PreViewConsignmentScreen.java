package com.oilpalm3f.mainapp.collectioncenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignment;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.printer.PrinterChooserFragment;
import com.oilpalm3f.mainapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.oilpalm3f.mainapp.database.DatabaseKeys.TABLE_CONSIGNMENT;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_ID;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.MILL_INFORMATION;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

/**
 * Created by BaliReddy on 09/02/17.
 */

//Displaying Consignment Receipt

@SuppressWarnings("unchecked")
public class PreViewConsignmentScreen extends BaseFragment implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected {
    private static final String LOG_TAG = PreViewConsignmentScreen.class.getName();
    public Consignment selectedConsignment;
    private View rootView;

    private CCDataAccessHandler ccDataAccessHandler;
    private CollectionCenter selectedCollectionCenter, selectedCollectionCenterId;
    private android.widget.TextView collectionId;
    private android.widget.TextView vehNum;
    private android.widget.TextView driverName;
    private android.widget.TextView dateAndTimeStamp;
    private android.widget.TextView consignWeight;
    private android.widget.TextView opetrName;
    private android.widget.TextView millname;
    private android.widget.Button generateReceipt;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt;
    private String receiptCode = "";
    private String millName = "", isThere = "", sizeOfTruck = "";
    private TextView tv_vehicleType, tv_mobile_no;
    private LinkedHashMap<String, String> vehcile_typeDataMap;
    private String vehcile_typeName;
    String date, currentDate_am_pm;
    public int financialYear;
    private String days = "";
    private String sameday = "";

    private DataAccessHandler dataAccessHandler;

    private int isConsignmentexist = 0;

    String convertedNum = "";

    //Intializing the Class
    @SuppressLint("InflateParams")
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        rootView = inflater.inflate(R.layout.preview_consignment, null);
        baseLayout.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile("Consignment");
        Calendar c = Calendar.getInstance();

        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        date = objdateformat.format(c.getTime());


        SimpleDateFormat dateFormat_am_pm = new SimpleDateFormat(CommonConstants.DATE_FORMAT_1, Locale.US);
        currentDate_am_pm = dateFormat_am_pm.format(c.getTime());


        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
        dataAccessHandler = new DataAccessHandler(getActivity());

        selectedCollectionCenterId = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_ID);
        //Log.d("selectedCollectionCen", selectedCollectionCenterId.getCCId());
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        Bundle dataBundle = getArguments();
        selectedConsignment = dataBundle.getParcelable("sendconsignment_data");
        isThere = dataBundle.getString("isThere");
        sizeOfTruck = dataBundle.getString("sizeOfTruck");
        sameday = dataBundle.getString("sameday");
        Log.d("sameday", sameday + "");

        convertedNum = ccDataAccessHandler.getGeneratedCollectionConvertedNum(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_CONSIGNMENT);


        LinkedHashMap<String, String> millNameDataMap = (LinkedHashMap<String, String>) DataManager.getInstance().getDataFromManager(MILL_INFORMATION);
        millName = millNameDataMap.get(selectedConsignment.getMillcode());

        initView();
        bindData();
        enablePrintBtn(true);

        Log.d("tab_Id", CommonConstants.TABLET_ID + "");
    }

    //Initializing the UI

    private void initView() {
        collectionId = rootView.findViewById(R.id.collection_id);
        vehNum = rootView.findViewById(R.id.veh_num);
        driverName = rootView.findViewById(R.id.driverName);
        dateAndTimeStamp = rootView.findViewById(R.id.date_and_time_stamp);
        consignWeight = rootView.findViewById(R.id.consign_weight);
        opetrName = rootView.findViewById(R.id.opetr_name);
        millname = rootView.findViewById(R.id.millname);
        generateReceipt = (Button) rootView.findViewById(R.id.generateReceipt);
        tv_vehicleType = rootView.findViewById(R.id.tv_vehicleType);
        tv_mobile_no = rootView.findViewById(R.id.tv_mobile_no);

        collectionCenterName = rootView.findViewById(R.id.collection_center_name);
        collectionCenterCode = rootView.findViewById(R.id.collection_center_code);
        collectionCenterVillage = rootView.findViewById(R.id.collection_center_village);

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
            com.oilpalm3f.mainapp.cloudhelper.Log.v(LOG_TAG, "days -->" + days);

        } catch (Exception e) {
            e.printStackTrace();
        }


        receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_CONSIGNMENT, sameday);
        Log.d("receiptCode", receiptCode + "");


        //Generate Receipt on Click Listener
        generateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                isConsignmentexist = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isConsignmentExists(selectedConsignment.getCode()));
                Log.d("isConsignmentexist", isConsignmentexist + "");

                if (isConsignmentexist == 0){
                    saveConsignmentData(getContext());
                    FragmentManager fm = getChildFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setCancelable(false);
                    printerChooserFragment.setPrinterType(PreViewConsignmentScreen.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                }else{
                    FragmentManager fm = getChildFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setCancelable(false);
                    printerChooserFragment.setPrinterType(PreViewConsignmentScreen.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                }
            }
        });
    }


    //Binding Data to Fields
    @SuppressLint("SetTextI18n")
    public void bindData() {
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
        collectionId.setText(selectedConsignment.getCode());

        vehNum.setText(selectedConsignment.getVehiclenumber());
        driverName.setText(selectedConsignment.getDrivername());
        dateAndTimeStamp.setText(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS1));
        consignWeight.setText("" + selectedConsignment.getTotalweight());
        millname.setText(millName);
        opetrName.setText(selectedConsignment.getCreatedBy());
        tv_mobile_no.setText(selectedConsignment.getDriverMobileNumber());
        tv_vehicleType.setText(selectedConsignment.getVehcileType().split("@")[0]);
    }

    //Saving Consignment Data

    public void saveConsignmentData(final Context context) {

        try {

            LinkedHashMap dataMap = new LinkedHashMap();
            final List<LinkedHashMap> dataList = new ArrayList<>();

            selectedConsignment.setTotalBunches(0);
            selectedConsignment.setRejectedBunches(0);
            selectedConsignment.setAcceptedBunches(0);
            selectedConsignment.setGraderName(null);

            dataMap.put(DatabaseKeys.COLUMN_CODE, selectedConsignment.getCode());
            dataMap.put(DatabaseKeys.COLUMN_CCCODE, selectedConsignment.getCollectioncentercode());
            dataMap.put(DatabaseKeys.COLUMN_VEHICLENUMBER, selectedConsignment.getVehiclenumber());
            dataMap.put(DatabaseKeys.COLUMN_DRIVERNAME, selectedConsignment.getDrivername());
            dataMap.put(DatabaseKeys.COLUMN_MILLCODE, selectedConsignment.getMillcode());
            dataMap.put(DatabaseKeys.COLUMN_TOTALWEIGHT, selectedConsignment.getTotalweight());
            dataMap.put(DatabaseKeys.COLUMN_GROSSWEIGHT, selectedConsignment.getGrossweight());
            dataMap.put(DatabaseKeys.COLUMN_TAREWEIGHT, selectedConsignment.getTareweight());
            dataMap.put(DatabaseKeys.COLUMN_NETWEIGHT, selectedConsignment.getNetweight());
            dataMap.put(DatabaseKeys.COLUMN_WEIGHTDIFF, selectedConsignment.getWeightdifference());
            dataMap.put(DatabaseKeys.COLUMN_RECEIPTGENERATEDDATE, date);
            dataMap.put(DatabaseKeys.COLUMN_RECEIPTCODE, receiptCode);
            dataMap.put(DatabaseKeys.COLUMN_ISACTIVE, selectedConsignment.getIsActive());
            dataMap.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            dataMap.put(DatabaseKeys.COLUMN_CREATEDDATE, date);
            dataMap.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            dataMap.put(DatabaseKeys.COLUMN_UPDATEDDATE, date);
            dataMap.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, false);
            dataMap.put(DatabaseKeys.COLUMN_TOTALBUNCHES, selectedConsignment.getTotalBunches());
            dataMap.put(DatabaseKeys.COLUMN_REJECTEDBUNCHES, selectedConsignment.getRejectedBunches());
            dataMap.put(DatabaseKeys.COLUMN_ACCEPTEDBUNCHES, selectedConsignment.getAcceptedBunches());
            dataMap.put(DatabaseKeys.COLUMN_REMARKS, selectedConsignment.getRemarks());
            Log.v("@@@remarkspre", "" + selectedConsignment.getRemarks());

            dataMap.put(DatabaseKeys.COLUMN_GRADERNAME, selectedConsignment.getGraderName());
            String[] vT = selectedConsignment.getVehcileType().split("@");
            dataMap.put(DatabaseKeys.COLUMN_VECHILE_ID, vT[1]);
            dataMap.put(DatabaseKeys.COLUMN_DRIVER_MOBILENUMBER, selectedConsignment.getDriverMobileNumber());
            dataMap.put("SizeOfTruckId", selectedConsignment.getSizeOfTruckId());
            dataMap.put("IsSharing", selectedConsignment.isSharing());
            dataMap.put("TransportCost", selectedConsignment.getTransportCost());
            dataMap.put("SharingCost", selectedConsignment.getSharingCost());
            dataMap.put("OverWeightCost", selectedConsignment.getOverWeightCost());
            dataMap.put("ExpectedCost", selectedConsignment.getExpectedCost());
            dataMap.put("ActualCost", selectedConsignment.getActualCost());


            dataList.add(dataMap);
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            ApplicationThread.dbPost("Consignment Saving..", "insert", new Runnable() {
                @Override
                public void run() {
                    dataAccessHandler.insertData(TABLE_CONSIGNMENT, dataList, getActivity(), new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void run() {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                saveConsignmentStatusHistoryData(dataAccessHandler);
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                UiUtils.showCustomToastMessage("Data saving failed for Consignment", getActivity(), 1);

                                Log.e(LOG_TAG, "@@@@ Error while saving ConsignmentStatusHistoryData");
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@@ Error while saving ConsignmentStatusHistoryData due to " + e.getMessage());
        }
    }

//Saving Consignment Status History Data

    public void saveConsignmentStatusHistoryData(final DataAccessHandler dataAccessHandler) {

        try {

            LinkedHashMap dataMap = new LinkedHashMap();
            final List<LinkedHashMap> dataList = new ArrayList<>();
            String typeId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getStatusTypeId());
            dataMap.put(DatabaseKeys.COLUMN_CONSIGNMENTCODE, selectedConsignment.getCode());
            dataMap.put(DatabaseKeys.COLUMN_STATUSTYPEID, typeId);
            dataMap.put(DatabaseKeys.COLUMN_OPERATORNAME, selectedConsignment.getCreatedBy());
            dataMap.put(DatabaseKeys.COLUMN_COMMENTS, selectedConsignment.getComments());
            dataMap.put(DatabaseKeys.COLUMN_ISACTIVE, 1);
            dataMap.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            dataMap.put(DatabaseKeys.COLUMN_CREATEDDATE, date);
            dataMap.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            dataMap.put(DatabaseKeys.COLUMN_UPDATEDDATE, date);
            dataMap.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, false);

            dataList.add(dataMap);

            ApplicationThread.dbPost("ConsignmentStatusHistoryData Saving..", "insert", new Runnable() {
                @Override
                public void run() {
                    dataAccessHandler.insertData(DatabaseKeys.TABLE_CONSIGNMENTSTATUSHISTORY, dataList, getActivity(), new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void run() {

                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentStatusHistoryData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                UiUtils.showCustomToastMessage("Data saved", getActivity(), 0);

                                if (CommonUtils.isNetworkAvailable(getActivity())) {
                                    CommonUtils.isNotSyncScreen = false;
                                    DataSyncHelper.performCollectionCenterTransactionsSync(getActivity(), new ApplicationThread.OnComplete() {
                                        @Override
                                        public void execute(boolean success, Object result, String msg) {
                                            if (success) {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentStatusHistoryData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        enablePrintBtn(true);
                                                        SendConsignment.mCurrentPhotoPath = "";
                                                        UiUtils.showCustomToastMessage("Successfully data sent to server", getActivity(), 0);
                                                        //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                                        getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                                        getActivity().finish();
                                                    }
                                                });
                                            } else {
                                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentStatusHistoryData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        enablePrintBtn(true);
                                                        UiUtils.showCustomToastMessage("Data sync failed", getActivity(), 1);
                                                        //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    enablePrintBtn(true);
                                    //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                    getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                    getActivity().finish();
                                }
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentStatusHistoryData", CommonConstants.TAB_ID, "", msg, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                Log.e(LOG_TAG, "@@@@ Error while saving ConsignmentStatusHistoryData");
                                UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            palm3FoilDatabase.insertErrorLogs(LOG_TAG, "saveConsignmentStatusHistoryData", CommonConstants.TAB_ID, "", e.getMessage(), CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            Log.e(LOG_TAG, "@@@@ Error while saving ConsignmentStatusHistoryData due to " + e.getMessage());
        }
    }

    //Print Consignment Data
    public void printConsignmentData(PrinterInstance mPrinter, int printCount, FragmentActivity context) {

        //CommonConstants.TABLET_ID = ccDataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumberID(getContext())));

       // String convertedNum = ccDataAccessHandler.getGeneratedCollectionConvertedNum(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_CONSIGNMENT);
        // receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_CONSIGNMENT,days);
        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.setLeftMargin(0, 0);
        mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText("   Consignment Dispatch Receipt" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        sb.append("==============================================" + "\n");
        sb.append("  DateTime: ");
        sb.append(" ").append(currentDate_am_pm).append("\n");
        sb.append(" ");
        sb.append(" Receipt Number: ");
        sb.append(" ").append(receiptCode).append("\n");
        sb.append("  Consignment ID: ");
        sb.append(" ").append(selectedConsignment.getCode()).append("\n");
        sb.append(" ");
        sb.append(" CC name: ");
        sb.append(" ").append(selectedCollectionCenter.getName()).append("\n");
        sb.append(" ");
        sb.append(" Vehicle Number: ").append(selectedConsignment.getVehiclenumber()).append("\n");
        sb.append("  Driver Name: ");
        sb.append(" ").append(selectedConsignment.getDrivername()).append("\n");
        sb.append(" ");
        sb.append(" Mill Name: ");
        sb.append(" ").append(millName).append("\n");
        sb.append(" ");
        sb.append(" Operator name: ");
        sb.append(" ").append(selectedConsignment.getCreatedBy()).append("\n");
        sb.append(" ");
        sb.append(" Consignment weight: ").append(selectedConsignment.getTotalweight()).append(" Kg").append("\n");
        sb.append(" ");
        sb.append("==============================================\n");
        if (isThere.equalsIgnoreCase("Yes")) {
            sb.append("Truck Size:  " + sizeOfTruck).append("\n");
            sb.append("Basic Transport Cost: " + selectedConsignment.getTransportCost()).append("\n");
            sb.append("Sharing Cost: " + selectedConsignment.getSharingCost()).append("\n");
            sb.append("OverWeight Cost: " + selectedConsignment.getOverWeightCost()).append("\n");
            sb.append("Expected Transport Cost: " + selectedConsignment.getExpectedCost()).append("\n");
            sb.append("Actual Transport Cost: " + selectedConsignment.getActualCost()).append("\n");
            sb.append("==============================================\n");
        }
        sb.append(" ");
        sb.append("\n");
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

        mPrinter.printText(sb.toString());


        String tab_Id = StringUtils.leftPad(CommonConstants.TABLET_ID, 3, "0");
        Log.d("tab_Id", tab_Id + "");
        String convertednum = StringUtils.leftPad(convertedNum, 6, "0");
        String collectionCenterId = StringUtils.leftPad(selectedCollectionCenterId.getCCId(), 3, "0");

        Integer totalweight = (int) (selectedConsignment.getTotalweight());
        String TotalWeight = StringUtils.leftPad(String.valueOf(totalweight), 5, "0");
        Log.d("TotalWeightis", TotalWeight + "");

        String hashString = ccDataAccessHandler.getCurrentYear() + tab_Id + collectionCenterId + convertednum + sameday + TotalWeight;
        String qrCodeValue = hashString.substring(0, 24);
        Log.d("qrCodeValueis", qrCodeValue + "");
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);

        /*if (CommonConstants.PrinterName.contains("AMIGOS")) {
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG, "########### NEW ##############");
            print_qr_code(mPrinter, qrCodeValue);
        } else {
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG, "########### OLD ##############");
            mPrinter.printBarCode(barcode);
        }*/

        if((CommonConstants.PrinterName.contains("AMIGOS")) && !(CommonConstants.PrinterName.contains("G-8BT3"))){
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG,"########### NEW ##############");
            print_qr_code(mPrinter,qrCodeValue);
        }else if (CommonConstants.PrinterName.contains("G-8BT3 AMIGOS")){
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG,"########### NEWEST ##############");
            print_qr_codee(mPrinter,qrCodeValue);
        }else{
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG,"########### OLD ##############");
            mPrinter.printBarCode(barcode);
        }

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(qrCodeValue);


        String spaceBuilder = "\n" +
                " " +
                "\n" +
                " " +
                "\n" +
                "\n" +
                " " +
                "\n" +
                "\n" +
                "\n" +
                "\n";
        mPrinter.printText(spaceBuilder);

        boolean printSuccess = false;
        try {
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            printSuccess = true;
        } catch (Exception e) {
            Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), getActivity(), 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                if (printCount == 2) {
                    //saveConsignmentData(context);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        }
    }

    //Generates QR Code
    public void print_qr_code(PrinterInstance mPrinter, String qrdata) {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the modelc
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x04, (byte) 0x00, (byte) 0x31, (byte) 0x41, (byte) 0x32, (byte) 0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141


        byte[] sizeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x43, (byte) 0x10};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x45, (byte) 0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, store_pL, store_pH, (byte) 0x31, (byte) 0x50, (byte) 0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte) 0x1d, (byte) 0x28, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x31, (byte) 0x51, (byte) 0x30};

        // flush() runs the print job and clears out the print buffer
//        flush();

        // write() simply appends the data to the buffer
        mPrinter.sendByteData(modelQR);

        mPrinter.sendByteData(sizeQR);
        mPrinter.sendByteData(errorQR);
        mPrinter.sendByteData(storeQR);
        mPrinter.sendByteData(qrdata.getBytes());
        mPrinter.sendByteData(printQR);

    }

    //Letting know how many times data should Print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        if (null != printerInstance) {
            enablePrintBtn(true);
            int PRINT_COUNT = 3;
            for (int i = 0; i < PRINT_COUNT; i++) {
                printConsignmentData(printerInstance, i, getActivity());
            }
        } else {
            enablePrintBtn(true);
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
            usbDevicesListFragment.setOnUsbDeviceSelected(PreViewConsignmentScreen.this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getChildFragmentManager();
            BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setCancelable(false);
            bluetoothDevicesFragment.setOnDeviceSelected(PreViewConsignmentScreen.this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }

    //Show/hide Print Button
    public void enablePrintBtn(final boolean enable) {
        ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
            @Override
            public void run() {
                generateReceipt.setEnabled(enable);
                generateReceipt.setClickable(enable);
                generateReceipt.setFocusable(enable);
            }
        });
    }

    public void print_qr_codee(PrinterInstance mPrinter,String qrdata)
    {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the modelc
        //Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        //byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};//original
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x49, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141

        //byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x10};//original
        byte[] sizeQR = {(byte)0x1d, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x00, (byte)0x0, (byte)0x0, (byte)0x10};


        //Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};//original



        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};//original


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};//original

        // flush() runs the print job and clears out the print buffer
//        flush();

        // write() simply appends the data to the buffer
        mPrinter.sendByteData(modelQR);

        mPrinter.sendByteData(sizeQR);
        mPrinter.sendByteData(errorQR);
        mPrinter.sendByteData(storeQR);
        mPrinter.sendByteData(qrdata.getBytes());
        mPrinter.sendByteData(printQR);

    }
}
