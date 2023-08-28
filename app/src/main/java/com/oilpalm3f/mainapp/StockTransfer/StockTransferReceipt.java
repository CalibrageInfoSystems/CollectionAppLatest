package com.oilpalm3f.mainapp.StockTransfer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.BluetoothDevicesFragment;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentRepository;
import com.oilpalm3f.mainapp.collectioncenter.onPrinterType;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.common.FiscalDate;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.printer.PrinterChooserFragment;
import com.oilpalm3f.mainapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.mainapp.ui.BaseFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.oilpalm3f.mainapp.database.DatabaseKeys.TABLE_CONSIGNMENT_FileRepo;
import static com.oilpalm3f.mainapp.database.DatabaseKeys.TABLE_STOCK_TRANSFER;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_ID;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.USER_DETAILS;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

/**
 * Created by Bali Reddy on 25/02/19.
 *
 */

//Stock Transfer Receipt Screen
public class StockTransferReceipt extends BaseFragment implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected  {
    private static final String LOG_TAG = StockTransferReceipt.class.getName();
    public StockTransfer selectedStockTransfer;
    private View rootView;
    private CCDataAccessHandler ccDataAccessHandler;
    private CollectionCenter selectedCollectionCenter,selectedToCC, selectedCollectionCenterId;
    private TextView stockTransferCode,fromCC,toCC,operatorName,netWeight,grossWeight,tareWeight,dateAndTimeStamp,driverName,vehicleNumber;
    private android.widget.Button generateReceipt;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt;
    private String receiptCode = "";
    private String toCcStr = "",createdByName;
    private TextView tv_vehicleType,tv_mobile_no,tv_vehicleNumber;
    ConsignmentRepository stockTransferImage;

    public static DecimalFormat twoDForm = new DecimalFormat("#.##");

    String date,currentDate_am_pm;
    private String days = "";
    public int financialYear;
    public String dayyys;

    private int isstocktransferexist = 0;
    private DataAccessHandler dataAccessHandler;

    String convertedNum = "";

    //Intializing the Class and Getting selected Collection Center Details
    @SuppressLint("InflateParams")
    @Override
    public void Initialize() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        rootView = layoutInflater.inflate(R.layout.fragment_stocktransfer_receipt,null);
        baseLayout.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile("Stock Transfer Receipt");
        ccDataAccessHandler = new CCDataAccessHandler(getActivity());
        dataAccessHandler = new DataAccessHandler(getActivity());
        Calendar c = Calendar.getInstance();

        SimpleDateFormat objdateformat = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
        date = objdateformat.format(c.getTime());


        SimpleDateFormat dateFormat_am_pm = new SimpleDateFormat(CommonConstants.DATE_FORMAT_1, Locale.US);
        currentDate_am_pm = dateFormat_am_pm.format(c.getTime());


        selectedCollectionCenterId = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_ID);
        //Log.d("selectedCollectionCenterId", selectedCollectionCenterId.getCCId() + "");
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);
        //Log.d("selectedCollectionCenterIdd", selectedCollectionCenter.getCCId() + "");
        selectedToCC = (CollectionCenter) DataManager.getInstance().getDataFromManager(DataManager.To_COLLECTION_CENTER_DATA);
        stockTransferImage = (ConsignmentRepository) DataManager.getInstance().getDataFromManager(DataManager.stockTransferImage);

        Bundle dataBundle = getArguments();
        selectedStockTransfer = dataBundle.getParcelable("stockTransfer");
        convertedNum = ccDataAccessHandler.getGeneratedCollectionConvertedNum(selectedCollectionCenter.getCode(), "STR",TABLE_STOCK_TRANSFER );

        dayyys = dataBundle.getString("days");
        UserDetails userDetails = (UserDetails) DataManager.getInstance().getDataFromManager(USER_DETAILS);

        if (null != userDetails) {
            createdByName = userDetails.getFirstName();
        }

        initView();
        bindData();
        enablePrintBtn(true);
    }

//Initializing the UI
    private void initView(){
        stockTransferCode = rootView.findViewById(R.id.st_Number);
        driverName =  rootView.findViewById(R.id.driverName);
        dateAndTimeStamp =  rootView.findViewById(R.id.date_and_time_stamp);
        netWeight =  rootView.findViewById(R.id.st_NetWeight);
        fromCC = rootView.findViewById(R.id.st_fromCC);
        grossWeight =  rootView.findViewById(R.id.st_GrossWeight);
        tareWeight =  rootView.findViewById(R.id.st_TareWeight);
        operatorName =   rootView.findViewById(R.id.userName);
        toCC =   rootView.findViewById(R.id.st_toCC);
        generateReceipt = (Button) rootView.findViewById(R.id.generateReceipt);
        tv_mobile_no= rootView.findViewById(R.id.tv_mobile_no);
        tv_vehicleNumber = rootView.findViewById(R.id.tv_vehicleNumber);

        collectionCenterName =   rootView.findViewById(R.id.collection_center_name);
        collectionCenterCode =   rootView.findViewById(R.id.collection_center_code);
        collectionCenterVillage =   rootView.findViewById(R.id.collection_center_village);

        final Calendar calendar = Calendar.getInstance();
        final FiscalDate fiscalDate = new FiscalDate(calendar);
        financialYear = fiscalDate.getFiscalYear();

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

//Generating Receipt Code
        receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.ST_RECEIPT_CODE_INITIAL, TABLE_STOCK_TRANSFER,dayyys);

//Generate Receipt On Click Listener
        generateReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 enablePrintBtn(false);


                isstocktransferexist = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().isStockTransferExists(selectedStockTransfer.getCode()));

                if (isstocktransferexist == 0){

                    saveStockTransferData(getActivity());
                    FragmentManager fm = getChildFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setCancelable(false);
                    printerChooserFragment.setPrinterType(StockTransferReceipt.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                }else{
                    FragmentManager fm = getChildFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setCancelable(false);
                    printerChooserFragment.setPrinterType(StockTransferReceipt.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                }

            }
        });
    }

    //Binding Data to the Fields
    @SuppressLint("SetTextI18n")
    private void bindData() {
        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
        stockTransferCode.setText(selectedStockTransfer.getCode());
        fromCC.setText(selectedCollectionCenter.getName());
        toCC.setText(selectedToCC.getName());
        grossWeight.setText(""+selectedStockTransfer.getGrossWeight());
        tareWeight.setText(""+selectedStockTransfer.getTareWeight());
        netWeight.setText(""+selectedStockTransfer.getNetWeight());
        operatorName.setText(createdByName);
        tv_mobile_no.setText(selectedStockTransfer.getDriverMobileNumber());
        tv_vehicleNumber.setText((selectedStockTransfer.getVehicleNumber()));

        dateAndTimeStamp.setText(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS1));
    }

    //Inserting Stock Transfer Data
    private void saveStockTransferData(final Context context) {
        selectedStockTransfer.setReceiptCode(receiptCode);
        selectedStockTransfer.setToCC(selectedToCC.getCode());
        Gson gson = new GsonBuilder().serializeNulls().create();
        JSONObject stData = null;
        List sTdataToInsert = null;
        try {

            stData = new JSONObject(gson.toJson(selectedStockTransfer));
            sTdataToInsert = new ArrayList();
            sTdataToInsert.add(CommonUtils.toMap(stData));
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "@@ Stock Transfer data " + stData.toString());
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());

        dataAccessHandler.insertData(TABLE_STOCK_TRANSFER, sTdataToInsert, getActivity(), new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success){
                    addStockTransferImage();
                }else {
                    enablePrintBtn(true);
                    UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);

                }
            }
        });
    }

    //Inserting Stock Transfer Image Data
    private void addStockTransferImage() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JSONObject stDataImage = null;
        List stDataImageToInsert = null;
        try {

            stDataImage = new JSONObject(gson.toJson(stockTransferImage));
            stDataImageToInsert = new ArrayList();
            stDataImageToInsert.add(CommonUtils.toMap(stDataImage));
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "@@ Stock Trnafer data " + stDataImage.toString());
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(getActivity());
        dataAccessHandler.insertData(TABLE_CONSIGNMENT_FileRepo, stDataImageToInsert, getActivity(), new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success){
                    Log.v(LOG_TAG, "@@@ data saved successfully for " + ccDataAccessHandler.TABLE_COLLECTION_PLOT_XREF);
                    UiUtils.showCustomToastMessage("Data saved", getActivity(), 0);
                    if (CommonUtils.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
                        CommonUtils.isNotSyncScreen = false;
                        DataSyncHelper.performCollectionCenterTransactionsSync(getActivity(), new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"addStockTransferImage", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    enablePrintBtn(true);
                                    ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                          DataManager.getInstance().deleteData(DataManager.stockTransferImage);
                                          DataManager.getInstance().deleteData(DataManager.To_COLLECTION_CENTER_DATA);
                                            UiUtils.showCustomToastMessage("Successfully data sent to server", getActivity(), 0);
                                            //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                            getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                                        getActivity().finish();
                                        }
                                    });
                                } else {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"addStockTransferImage", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
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
//                        getActivity().startActivity(new Intent(getActivity(), CollectionCenterHomeScreen.class));
//                                    getActivity().finish();
                    }
                }else {
                    enablePrintBtn(true);
                    UiUtils.showCustomToastMessage("Data saving failed", getActivity(), 1);

                }
            }
        });

    }

    //Print Stock Transfer Data
    public void printStockTransferData(PrinterInstance mPrinter, int printCount, FragmentActivity context) {

        //CommonConstants.TABLET_ID = ccDataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumberID(getContext())));
       // receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.ST_RECEIPT_CODE_INITIAL, TABLE_STOCK_TRANSFER,"");

        //String convertedNum = ccDataAccessHandler.getGeneratedCollectionConvertedNum(selectedCollectionCenter.getCode(), "STR",TABLE_STOCK_TRANSFER );
//        receiptCode = ccDataAccessHandler.getGeneratedCollectionCode(selectedCollectionCenter.getCode(), ccDataAccessHandler.RECEIPT_CODE_INITIAL, ccDataAccessHandler.TABLE_CONSIGNMENT);
        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText("  Stock Transfer Dispatch Receipt " + "\n");
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
        sb.append("  Stock Transfer ID: ");
        sb.append(" ").append(selectedStockTransfer.getCode()).append("\n");
        sb.append(" ");
        sb.append(" From CC Name: ");
        sb.append(" ").append(selectedCollectionCenter.getName()).append("\n");
        sb.append(" ");
//        sb.append(" Vehicle Number: ").append(selectedStockTransfer.getVehiclenumber()).append("\n");
        sb.append(" To CC Name: ");
        sb.append(" ").append(selectedToCC.getName()).append("\n");
        sb.append(" ");
        sb.append(" Driver Mobile Number  : ");
        sb.append(" ").append(selectedStockTransfer.getDriverMobileNumber()).append("\n");
        sb.append(" ");
        sb.append(" Vehicle Number : ");
        sb.append(" ").append(selectedStockTransfer.getVehicleNumber()).append("\n");
        sb.append(" ");
        sb.append(" Operator Name: ");
        sb.append(" ").append(createdByName).append("\n");
        sb.append(" ");
        sb.append(" Gross Weight: ").append(Double.parseDouble(twoDForm.format(selectedStockTransfer.getGrossWeight()))).append(" Kg").append("\n");
        sb.append(" ");
        sb.append(" Tare Weight : ").append(Double.parseDouble(twoDForm.format(selectedStockTransfer.getTareWeight()))).append(" Kg").append("\n");
        sb.append(" ");
        sb.append(" Net Weight  : ").append(Double.parseDouble(twoDForm.format(selectedStockTransfer.getNetWeight()))).append(" Kg").append("\n");
        sb.append(" ");
        sb.append("==============================================\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" FromCC Officer signature");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");

        mPrinter.printText(sb.toString());


        String tab_Id = StringUtils.leftPad(CommonConstants.TABLET_ID, 3, "0");
        String convertedNumber = StringUtils.leftPad(convertedNum, 5, "0");
        String stFromCCId = StringUtils.leftPad(selectedCollectionCenterId.getCCId(), 3, "0");
        String stToCCId = StringUtils.leftPad(selectedToCC.getCCId(),3,"0");
        Integer totalWeight = (int) (selectedStockTransfer.getNetWeight());
        String netWeight_str = StringUtils.leftPad(String.valueOf(totalWeight), 6, "0");

        String hashString = ccDataAccessHandler.getCurrentYear() + tab_Id + stFromCCId + stToCCId + convertedNumber +dayyys+ netWeight_str;
        String qrCodeValue = hashString.substring(0,27);
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

//       Log.v(LOG_TAG,"Barcode"+hashString);
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);

//        mPrinter.printBarCode(barcode);
//        print_qr_code(mPrinter,qrCodeValue);

        /*if(CommonConstants.PrinterName.contains("AMIGOS")){
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG,"########### NEW ##############");
            print_qr_code(mPrinter,qrCodeValue);
        }else{
            com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG,"########### OLD ##############");
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
            android.util.Log.v(LOG_TAG, "@@@ Printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failed due to " + e.getMessage(), getActivity(), 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                if (printCount == 2) {
                    //saveStockTransferData(context);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    UiUtils.showCustomToastMessage("Print Success", getContext(), 0);
                }
            }
        }
    }

//    public void print_qr_code(PrinterInstance mPrinter,String qrdata)
//    {
//        int store_len = qrdata.length() + 3;
//        byte store_pL = (byte) (store_len % 256);
//        byte store_pH = (byte) (store_len / 256);
//
//
//        // QR Code: Select the model
//        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
//        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
//        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
//        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};
//
//        // QR Code: Set the size of module
//        // Hex      1D      28      6B      03      00      31      43      n
//        // n depends on the printer
//        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141
//        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x03};
//
//
//        //          Hex     1D      28      6B      03      00      31      45      n
//        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
//        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
//        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};
//
//
//        // QR Code: Store the data in the symbol storage area
//        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
//        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
//        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
//        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};
//
//
//        // QR Code: Print the symbol data in the symbol storage area
//        // Hex      1D      28      6B      03      00      31      51      m
//        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
//        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};
//
//        // flush() runs the print job and clears out the print buffer
////        flush();
//
//        // write() simply appends the data to the buffer
//        mPrinter.sendByteData(modelQR);
//
//        mPrinter.sendByteData(sizeQR);
//        mPrinter.sendByteData(errorQR);
//        mPrinter.sendByteData(storeQR);
//        mPrinter.sendByteData(qrdata.getBytes());
//        mPrinter.sendByteData(printQR);
//
//    }

    //Print QRCode Method
    public void print_qr_code(PrinterInstance mPrinter,String qrdata)
    {
        int store_len = qrdata.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);


        // QR Code: Select the modelc
        //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
        // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
        byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

        // QR Code: Set the size of module
        // Hex      1D      28      6B      03      00      31      43      n
        // n depends on the printer
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x10};


        //          Hex     1D      28      6B      03      00      31      45      n
        // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
        byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};


        // QR Code: Store the data in the symbol storage area
        // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
        //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
        byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};


        // QR Code: Print the symbol data in the symbol storage area
        // Hex      1D      28      6B      03      00      31      51      m
        // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
        byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

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

//Enabling/Disabling Print Button
    public void enablePrintBtn(final boolean enable) {
        ApplicationThread.uiPost(LOG_TAG, "Updating UI", new Runnable() {
            @Override
            public void run() {
                generateReceipt.setEnabled(enable);
                generateReceipt.setClickable(enable);
                generateReceipt.setFocusable(enable);
            }
        });
    }

    //Printer Type Selection
    @Override
    public void onPrinterTypeSelected(int printerType) {
        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getChildFragmentManager();
            UsbDevicesListFragment usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setCancelable(false);
            usbDevicesListFragment.setOnUsbDeviceSelected(StockTransferReceipt.this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getChildFragmentManager();
            BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setCancelable(false);
            bluetoothDevicesFragment.setOnDeviceSelected(StockTransferReceipt.this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }

    //Letting know how many times data should print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        if (null != printerInstance) {
            enablePrintBtn(false);
            int PRINT_COUNT = 3;
            for (int i = 0; i < PRINT_COUNT; i++) {
                printStockTransferData(printerInstance, i, getActivity());
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
