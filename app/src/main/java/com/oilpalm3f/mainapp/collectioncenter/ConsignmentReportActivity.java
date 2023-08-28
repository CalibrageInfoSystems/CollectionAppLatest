package com.oilpalm3f.mainapp.collectioncenter;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentActivity;
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

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentReportModel;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.printer.BluetoothOperation;
import com.oilpalm3f.mainapp.printer.PrinterChooserFragment;
import com.oilpalm3f.mainapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//Displaying Consignment Report
public class ConsignmentReportActivity extends OilPalmBaseActivity implements onPrintOptionSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected, BluetoothDevicesFragment.onDeviceSelected{
    private static final String LOG_TAG = ConsignmentReportActivity.class.getName();
    private ConsignmentReportAdapter consignemtnDetailsRecyclerAdapter;
    private RecyclerView consignmentReportsList;
    ;
    private List<ConsignmentReportModel> mConsignmentReportsList = new ArrayList<>();
    private TextView tvNorecords;
    private DataAccessHandler dataAccessHandler;
    private EditText fromDateEdt, toDateEdt;
    private Calendar myCalendar = Calendar.getInstance();
    private Button searchBtn;
    private String searchQuery = "";
    private String fromDateStr = "";
    private String toDateStr = "";
    private ConsignmentReportModel selectedReport;
    private String title = " 3F OILPALM PVT LTD ";
    private String subTitle = "   Consignment Dispatch Receipt";
    private BluetoothOperation bluetoothOperation;
    private CCDataAccessHandler ccDataAccessHandler;


    //Initializing the Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.consignment_reports_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.consignment_report));

        dataAccessHandler = new DataAccessHandler(this);
        ccDataAccessHandler = new CCDataAccessHandler(this);
        initUI();
        searchQuery = Queries.getInstance().getConsignmentReportDetails("", "");
        getConsignmentReports(searchQuery);
        CommonUtils.currentActivity = this;

        consignemtnDetailsRecyclerAdapter = new ConsignmentReportAdapter(ConsignmentReportActivity.this);
        consignmentReportsList.setLayoutManager(new LinearLayoutManager(ConsignmentReportActivity.this, LinearLayoutManager.VERTICAL, false));
        consignmentReportsList.setAdapter(consignemtnDetailsRecyclerAdapter);
        consignemtnDetailsRecyclerAdapter.setonPrintSelected(this);
    }

    //Initializing the UI
    private void initUI() {
        consignmentReportsList = (RecyclerView) findViewById(R.id.consignment_reports_list);
        tvNorecords = (TextView) findViewById(R.id.no_records);
        tvNorecords.setVisibility(View.GONE);
        searchBtn = (Button) findViewById(R.id.searchBtn);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ConsignmentReportActivity.this, toDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });
        //From Date on Click Listener
        fromDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ConsignmentReportActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

        //Search Button On Click Listener
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fromDateStr) && TextUtils.isEmpty(toDateStr)) {
                    UiUtils.showCustomToastMessage("Please select from or to dates", ConsignmentReportActivity.this, 0);
                } else {
                    searchQuery = Queries.getInstance().getConsignmentReportDetails(fromDateStr, toDateStr);
                    if (null != consignemtnDetailsRecyclerAdapter) {
                        mConsignmentReportsList.clear();
                        consignemtnDetailsRecyclerAdapter.notifyDataSetChanged();
                    }
                    getConsignmentReports(searchQuery);
                }
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
    public void getConsignmentReports(final String searchQuery) {
        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "getting consignment reports data", new Runnable() {
            @Override
            public void run() {
                dataAccessHandler.getConsignmentReportDetails(searchQuery, new ApplicationThread.OnComplete<List<ConsignmentReportModel>>() {
                    @Override
                    public void execute(boolean success, final List<ConsignmentReportModel> reports, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getConsignmentReports", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            if (reports != null && reports.size() > 0) {
                                mConsignmentReportsList.clear();
                                mConsignmentReportsList = reports;
                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        int recordsSize = reports.size();
                                        Log.v(LOG_TAG, "data size " + recordsSize);
                                        if (recordsSize > 0) {
                                            consignemtnDetailsRecyclerAdapter.updateAdapter(reports);
                                            tvNorecords.setVisibility(View.GONE);
                                            consignmentReportsList.setVisibility(View.VISIBLE);
                                            setTile(getString(R.string.consignment_report) + " ("+recordsSize+")");
                                        } else {
                                            ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
                                                @Override
                                                public void run() {
                                                    tvNorecords.setVisibility(View.VISIBLE);
                                                    Log.v(LOG_TAG, "@@@ No records found");
                                                    consignmentReportsList.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNorecords.setVisibility(View.VISIBLE);
                                        Log.v(LOG_TAG, "@@@ No records found");
                                        consignmentReportsList.setVisibility(View.GONE);
                                    }
                                });
                            }
                        } else {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getConsignmentReports", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
                                @Override
                                public void run() {
                                    tvNorecords.setVisibility(View.VISIBLE);
                                    Log.v(LOG_TAG, "@@@ Error while getting data from data base");
                                    consignmentReportsList.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void enablingPrintButton(boolean rePrint) {
        this.bluetoothOperation = bluetoothOperation;
    }

    //Calls when Printer is Selected
    @Override
    public void printOptionSelected(int position) {
        selectedReport = mConsignmentReportsList.get(position);
        FragmentManager fm = getSupportFragmentManager();
        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
        printerChooserFragment.setPrinterType(this);
        printerChooserFragment.show(fm, "bluetooth fragment");
    }

    //Navigates to the Printer
    @Override
    public void onPrinterTypeSelected(int printerType) {
        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getSupportFragmentManager();
            UsbDevicesListFragment usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setOnUsbDeviceSelected(this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setOnDeviceSelected(this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }

    //Letting know how many times data should Print
    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        for (int i = 0; i < 2; i++) {
            printConsignmentData(printerInstance, i);
        }
    }

    //Print Page Data
    public void printConsignmentData(PrinterInstance mPrinter, int count) {

        //CommonConstants.TABLET_ID = ccDataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabId(CommonUtils.getIMEInumberID(this)));
        mPrinter.init();
        StringBuffer sb = new StringBuffer();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(title + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(subTitle + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        mPrinter.printText("Duplicate Copy" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        /*mPrinter.setLeftMargin(15, 15);*/
        mPrinter.setLeftMargin(0, 0);
        sb.append("=============================================="+"\n");
        sb.append("  DateTime: ");
        sb.append(" " + selectedReport.getCreatedDate() + "\n");
        sb.append(" ");
        sb.append(" Receipt Number: ");
        sb.append(" " + selectedReport.getReceiptCode() + "\n");
        sb.append("  Consignment ID: ");
        sb.append(" " + selectedReport.getCode() + "\n");
        sb.append(" ");
        sb.append(" CC name: ");
        sb.append(" " + selectedReport.getSelectedCollectionCenterName() + "\n");
        sb.append(" ");
        sb.append(" Vehicle Number: " + selectedReport.getVehicleNumber() + "\n");
        sb.append("  Diver Name: ");
        sb.append(" " + selectedReport.getDriverName() + "\n");
        sb.append(" ");
        sb.append(" Mill Name: ");
        sb.append(" " + selectedReport.getMillName() + "\n");
        sb.append(" ");
        sb.append(" Operator name: ");
        sb.append(" " + selectedReport.getOperatorName() + "\n");
        sb.append(" ");
        sb.append(" Consignment weight: " + selectedReport.getTotalWeight() + " Kg" + "\n");
        sb.append(" ");
        sb.append("==============================================\n");
        if(selectedReport.getSizeOfTruck() >0) {
            sb.append("Truck Size:  "+selectedReport.getSizeOfTruck()+" Tons").append("\n");
            sb.append("Basic Transport Cost: "+selectedReport.getTransportCost()).append("\n");
            sb.append("Sharing Cost: "+selectedReport.getSharingCost()).append("\n");
            sb.append("OverWeight Cost: "+selectedReport.getOverWeightCost()).append("\n");
            sb.append("Expected Transport Cost: "+selectedReport.getExpectedCost()).append("\n");
            sb.append("Actual Transport Cost: "+selectedReport.getActualCost()).append("\n");
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
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append(" ");
        sb.append("\n");
        sb.append("\n");
        mPrinter.printText(sb.toString());
        String tab_Id = StringUtils.leftPad(CommonConstants.TABLET_ID, 3, "0");
        String convertednum =   selectedReport.getCode().substring(selectedReport.getCode().lastIndexOf("-") + 1);
        convertednum = StringUtils.leftPad(convertednum, 6, "0");
        String collectionCenterId = StringUtils.leftPad(selectedReport.getCollectionCenterCode(), 3, "0");
        Integer totalweight = 0;
        if(selectedReport.getTotalWeight()!=null){
            totalweight = Integer.parseInt(selectedReport.getTotalWeight());
        }
        String TotalWeight = StringUtils.leftPad(String.valueOf(totalweight), 5, "0");

        String days = (selectedReport.getCode().substring(selectedReport.getCode().lastIndexOf("-")-3)).split("-")[0];
     //   Toast.makeText(getApplicationContext(),"QR-Code Value-->"+days,Toast.LENGTH_LONG).show();
        Log.v("@@@days",""+days);


        String hashString = ccDataAccessHandler.getCurrentYear() + tab_Id + collectionCenterId + convertednum +days+ TotalWeight;

        String qrCodeValue = hashString.substring(0,24);
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

        Toast.makeText(getApplicationContext(),"QR-Code Value-->"+qrCodeValue,Toast.LENGTH_LONG).show();

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);

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

//        mPrinter.printBarCode(barcode);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(qrCodeValue);// need to comment the line and test
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

        try {
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        } catch (Exception e) {
            android.util.Log.v(LOG_TAG, "@@@ printing failed "+e.getMessage());
            UiUtils.showCustomToastMessage("Printing failes due to "+e.getMessage(), this, 1);
        } finally {
            if (null != bluetoothOperation && count == 2) {
                bluetoothOperation.close();
            }
        }
    }

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
