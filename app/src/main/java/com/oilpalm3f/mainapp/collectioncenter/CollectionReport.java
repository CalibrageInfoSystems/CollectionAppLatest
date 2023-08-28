package com.oilpalm3f.mainapp.collectioncenter;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionReportModel;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.printer.PrinterChooserFragment;
import com.oilpalm3f.mainapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CollectionReport extends OilPalmBaseActivity implements onPrintOptionSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected, BluetoothDevicesFragment.onDeviceSelected {

    private static final String LOG_TAG = CollectionReport.class.getName();
    private CollectionReportAdapter collectionReportRecyclerAdapter;
    private RecyclerView ccReportsList;
    private List<CollectionReportModel> mReportsList = new ArrayList<>();
    private TextView tvNorecords, totalNetWeightSum;
    private DataAccessHandler dataAccessHandler;
    private EditText fromDateEdt, toDateEdt;
    private Calendar myCalendar = Calendar.getInstance();
    private Button searchBtn;
    private String searchQuery = "";
    public static String SearchCollectionwithoutPlotQuery = "";
    private String fromDateStr = "";
    private String toDateStr = "";
    private CollectionReportModel selectedReport;
    private String title = " 3F OILPALM PVT LTD ";
    private String subTitle = "   Oil Palm F.F.B Receipt";
    private BluetoothDevicesFragment bluetoothDevicesFragment = null;
    private UsbDevicesListFragment usbDevicesListFragment = null;

    String vehicleType;
    String CCStateId;

    //Initializing the Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.cc_reports_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.collection_report));

        dataAccessHandler = new DataAccessHandler(this);
        initUI();

        String currentDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        fromDateEdt.setText(sdf.format(new Date()));
        toDateEdt.setText(sdf.format(new Date()));

        searchQuery = Queries.getInstance().getCollectionCenterReports(currentDate, currentDate);
        SearchCollectionwithoutPlotQuery = Queries.getInstance().getCollectionCenterReportsWithOutPlot(currentDate, currentDate);
        updateLabel(0);
        updateLabel(1);

        getCollectionCenterReports(searchQuery);
        CommonUtils.currentActivity = this;
        collectionReportRecyclerAdapter = new CollectionReportAdapter(CollectionReport.this);
        collectionReportRecyclerAdapter.setonPrintSelected(this);
        ccReportsList.setLayoutManager(new LinearLayoutManager(CollectionReport.this, LinearLayoutManager.VERTICAL, false));
        ccReportsList.setAdapter(collectionReportRecyclerAdapter);

        String CollectionNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(currentDate, currentDate));
        String CollectionWithOutPlotNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionWithOutPlotNetSum(currentDate, currentDate));

        if (CollectionNetWeight == null){
            CollectionNetWeight = "0.0";
        }
        if (CollectionWithOutPlotNetWeight == null){
            CollectionWithOutPlotNetWeight = "0.0";
        }
        Float totalNetWeight = Float.valueOf(CollectionNetWeight) +Float.valueOf(CollectionWithOutPlotNetWeight);
        totalNetWeightSum.setText(" "+totalNetWeight + " Kgs");
    }

    //Intializing the UI
    private void initUI() {
        ccReportsList = (RecyclerView) findViewById(R.id.cc_reports_list);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        totalNetWeightSum = (TextView) findViewById(R.id.totalNetWeightSum);


        //Search Button On Click Listener
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fromDateStr) && TextUtils.isEmpty(toDateStr)) {
                    UiUtils.showCustomToastMessage("Please select from or to dates", CollectionReport.this, 0);
                } else {
                    String CollectionNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(fromDateStr, toDateStr));
                    String CollectionWithOutPlotNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionWithOutPlotNetSum(fromDateStr, toDateStr));
                    if (CollectionNetWeight == null){
                        CollectionNetWeight = "0.0";
                    }
                    if (CollectionWithOutPlotNetWeight == null){
                        CollectionWithOutPlotNetWeight = "0.0";
                    }
                    Float totalNetWeight = Float.valueOf(CollectionNetWeight) + Float.valueOf(CollectionWithOutPlotNetWeight);
//                    String totalNetWeight = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionNetSum(fromDateStr, toDateStr));
                    if (!TextUtils.isEmpty(String.valueOf(totalNetWeight))) {
                        totalNetWeightSum.setText(" "+totalNetWeight + " Kgs");
                    }
                    searchQuery = Queries.getInstance().getCollectionCenterReports(fromDateStr, toDateStr);
                    SearchCollectionwithoutPlotQuery = Queries.getInstance().getCollectionCenterReportsWithOutPlot(fromDateStr, toDateStr);
                    if (null != collectionReportRecyclerAdapter) {
                        mReportsList.clear();
                        collectionReportRecyclerAdapter.notifyDataSetChanged();
                    }
                    getCollectionCenterReports(searchQuery);
                }
            }
        });
        tvNorecords = (TextView) findViewById(R.id.no_records);
        tvNorecords.setVisibility(View.GONE);

        fromDateEdt = (EditText) findViewById(R.id.fromDate);
        toDateEdt = (EditText) findViewById(R.id.toDate);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(0);
            }
        };

        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(1);
            }
        };

        //To Date on Click Listener
        toDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CollectionReport.this, toDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });
        //From Date on Click Listener
        fromDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CollectionReport.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

    }

    // Setting Date Format to Update Lables
    private void updateLabel(int type) {
        String myFormat = "dd-MM-yyyy";
        String dateFormatter = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);

        if (type == 0) {
            fromDateStr = sdf2.format(myCalendar.getTime());
            fromDateEdt.setText(sdf.format(myCalendar.getTime()));
        } else {
            toDateStr = sdf2.format(myCalendar.getTime());
            toDateEdt.setText(sdf.format(myCalendar.getTime()));
        }

    }

    //To get the Collection Center Reports on Selected Dates
    public void getCollectionCenterReports(final String searchQuery) {
        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "getting reports data", new Runnable() {
            @Override
            public void run() {
                dataAccessHandler.getCollectionReportDetails(searchQuery, new ApplicationThread.OnComplete<List<CollectionReportModel>>() {
                    @Override
                    public void execute(boolean success, final List<CollectionReportModel> reports, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getCollectionCenterReports", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            if (reports != null && reports.size() > 0) {
                                mReportsList.clear();
                                mReportsList = reports;
                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        int recordsSize = reports.size();
                                        Log.v(LOG_TAG, "data size " + recordsSize);
                                        if (recordsSize > 0) {
                                            collectionReportRecyclerAdapter.updateAdapter(reports);
                                            tvNorecords.setVisibility(View.GONE);
                                            ccReportsList.setVisibility(View.VISIBLE);
                                            setTile(getString(R.string.collection_report) + " ("+recordsSize+")");
                                        } else {
                                            tvNorecords.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            } else {
                                ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNorecords.setVisibility(View.VISIBLE);
                                        Log.v(LOG_TAG, "@@@ No records found");
                                        ccReportsList.setVisibility(View.GONE);
                                    }
                                });
                            }
                        } else {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getCollectionCenterReports", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
                                @Override
                                public void run() {
                                    tvNorecords.setVisibility(View.VISIBLE);
                                    Log.v(LOG_TAG, "@@@ No records found");
                                    ccReportsList.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    //Navigates to the Printer
    @Override
    public void printOptionSelected(int position) {
        selectedReport = mReportsList.get(position);

//        Log.d("HNAME",selectedReport.getName() + "");
//        Log.d("HMOBILE",selectedReport.getMobileNumber() + "");
//
//        Log.d("Unripen",selectedReport.getUnRipen() + "");
//        Log.d("Underipe",selectedReport.getUnderRipe() + "");
//        Log.d("Ripen",selectedReport.getRipen() + "");
//        Log.d("Overripe",selectedReport.getOverRipe() + "");
//        Log.d("Diseased",selectedReport.getDiseased() + "");
//        Log.d("EmptyBunches",selectedReport.getEmptyBunches() + "");
//
//        Log.d("FFBLong",selectedReport.getFFBQualityLong() + "");
//        Log.d("FFBMedium",selectedReport.getFFBQualityMedium() + "");
//        Log.d("FFBShort",selectedReport.getFFBQualityShort() + "");
//        Log.d("FFBOptimum",selectedReport.getFFBQualityOptimum() + "");


       CCStateId = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getCollectionCenterStateId(selectedReport.getCCCode()));
        vehicleType = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getvehicleTypeName(selectedReport.getVehicleTypeId()));

//        Log.d("CollectionStateId",CCStateId);
//        Log.d("vehicleType",vehicleType);

        FragmentManager fm = getSupportFragmentManager();
        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
        printerChooserFragment.setPrinterType(this);
        printerChooserFragment.show(fm, "bluetooth fragment");
    }


    //Calls when Printer is Selected
    @Override
    public void onPrinterTypeSelected(int printerType) {
        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getSupportFragmentManager();
            usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setOnUsbDeviceSelected(this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setOnDeviceSelected(this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }

    @Override
    public void enablingPrintButton(boolean rePrint) {

    }

    //Letting know how many times data should Print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        for (int i = 0; i < 2; i++) {
            printCollectionData(printerInstance, i);
        }
    }

    //Print Page Data

    public void printCollectionData(PrinterInstance mPrinter, int count) {

        String middleName = "";
        if (!TextUtils.isEmpty(selectedReport.getMiddleName()) && !
                selectedReport.getMiddleName().equalsIgnoreCase("null")) {
            middleName = selectedReport.getMiddleName();
        }
        String farmerName = selectedReport.getFirstName()+" "+middleName+" "+selectedReport.getLastName();
        String bankName = selectedReport.getBankName();
        mPrinter.init();
        StringBuffer sb = new StringBuffer();
//        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
//        mPrinter.setCharacterMultiple(0, 1);
//        mPrinter.printText(title + "\n");
//        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
//        mPrinter.setCharacterMultiple(0, 1);
//        mPrinter.printText(subTitle + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText("Duplicate Copy" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        sb.append("--------------------------------------------"+"\n");
        sb.append("  DateTime: ");
        sb.append(" " + selectedReport.getCreatedDate() + "\n");
        sb.append(" ");
        sb.append(" Receipt Number: ");
        sb.append(" " + selectedReport.getReceiptCode() + "\n");
        sb.append(" ");
        sb.append(" CC name: ");
        sb.append(" " + selectedReport.getCollectionCenterName() + "\n");

        sb.append(" ");
        sb.append(" Vehicle Type: ").append(vehicleType + "").append("\n");
        sb.append(" ");
        if (!selectedReport.getVehicleNumber().equalsIgnoreCase("null")) {
            sb.append(" Vehicle Number: ").append(selectedReport.getVehicleNumber()).append("\n");
        }
//        sb.append(" ");
//        sb.append(" Vehicle Number: " + selectedReport.getVehicleNumber() + "\n");
        sb.append(" ");
        sb.append(" Farmer ID: ");
        sb.append(" " + selectedReport.getFarmerCode() + "\n");
        sb.append(" ");
        sb.append(" Farmer Name: ");
        sb.append(" " + farmerName + "\n");
        sb.append(" ");
        sb.append(" A/c No: "+ selectedReport.getBankAccountNumber() + "\n");
        sb.append(" ");
        sb.append(" Bank name: "+ bankName + "\n");
        sb.append(" ");
        sb.append(" Branch name: "+ selectedReport.getBranchName() + "\n");
        sb.append(" ");
        sb.append("-----------------------------------------------\n");
        sb.append("  Commodity: FFB"+ "\n");
        sb.append("-----------------------------------------------\n");
        if (!TextUtils.isEmpty(selectedReport.getPrivateWeighBridgeName())) {
            sb.append(" ");
            sb.append(" Private Weighbridge Centre: "+ "\n");
            sb.append("  "+ selectedReport.getPrivateWeighBridgeName() + "\n");
            sb.append(" ");
        }
        sb.append(" ");
        sb.append(" Gross Weight(Kgs) : "+ selectedReport.getGrossWeight() + "\n");
        sb.append(" ");
        sb.append(" Tare weight(Kgs) : "+ selectedReport.getTareWeight() + "\n");
        sb.append(" ");
        sb.append(" Net weight(Kgs) : "+ selectedReport.getNetWeight() + "\n");
        sb.append(" ");
        sb.append(" No of bunches rejected : "+ selectedReport.getRejectedBunches() + "\n");


        if (!TextUtils.isEmpty(selectedReport.getName()) || !TextUtils.isEmpty(selectedReport.getMobileNumber())) {

            sb.append(" ");
            sb.append("------------------------------------ -----------\n");
            sb.append("  Harvester Details" + "\n");
            sb.append("-----------------------------------------------\n");
            if (!TextUtils.isEmpty(selectedReport.getName())) {
                sb.append(" ");
                sb.append(" Harvester Name: ").append(selectedReport.getName()).append("\n");
            }
            if (!TextUtils.isEmpty(selectedReport.getMobileNumber())) {
                sb.append(" ");
                sb.append(" Harvester Mobile Number: ").append(selectedReport.getMobileNumber()).append("\n");
            }
        }

        if (CCStateId.equalsIgnoreCase("1")) {

            sb.append(" ");
            sb.append("-----------------------------------------------\n");
            sb.append("  FFB Quality Details" + "\n");
            sb.append("-----------------------------------------------\n");

            if (!selectedReport.getUnRipen().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Unripen : ").append(selectedReport.getUnRipen() + "%").append("\n");
            }
            if (!selectedReport.getUnderRipe().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Under Ripe : ").append(selectedReport.getUnderRipe() + "%").append("\n");
            }
            if (!selectedReport.getRipen().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Ripen : ").append(selectedReport.getRipen() + "%").append("\n");
            }
            if (!selectedReport.getOverRipe().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Over Ripe : ").append(selectedReport.getOverRipe() + "%").append("\n");
            }
            if (!selectedReport.getDiseased().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Diseased : ").append(selectedReport.getDiseased() + "%").append("\n");
            }
            if (!selectedReport.getEmptyBunches().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Empty Bunch's : ").append(selectedReport.getEmptyBunches() + "%").append("\n");
            }

            sb.append(" ");
            sb.append("-----------------------------------------------\n");
            sb.append("  Stalk Quality Details" + "\n");
            sb.append("-----------------------------------------------\n");

            if (!selectedReport.getFFBQualityLong().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Long : ").append(selectedReport.getFFBQualityLong() + "%").append("\n");
            }
            if (!selectedReport.getFFBQualityMedium().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Medium : ").append(selectedReport.getFFBQualityMedium() + "%").append("\n");
            }
            if (!selectedReport.getFFBQualityShort().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Short : ").append(selectedReport.getFFBQualityShort() + "%").append("\n");
            }
            if (!selectedReport.getFFBQualityOptimum().equalsIgnoreCase("0")) {
                sb.append(" ");
                sb.append(" Optimum : ").append(selectedReport.getFFBQualityOptimum() + "%").append("\n");
            }
            if (!selectedReport.getLoosefruitweight().equalsIgnoreCase("null")){

                sb.append(" ");
                sb.append(" Loose Fruit Approx.Quantity : ").append(selectedReport.getLoosefruitweight() + "Kg").append("\n");

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
            printSuccess = true;
        } catch (Exception e) {
            android.util.Log.v(LOG_TAG, "@@@ printing failed "+e.getMessage());
            UiUtils.showCustomToastMessage("Printing failed due to "+e.getMessage(), this, 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {

            }
        }
    }
}