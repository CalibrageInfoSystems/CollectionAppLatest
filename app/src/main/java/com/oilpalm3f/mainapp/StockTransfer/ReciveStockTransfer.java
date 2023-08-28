package com.oilpalm3f.mainapp.StockTransfer;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.oilpalm3f.mainapp.database.DatabaseKeys.TABLE_STOCK_RECEIVE;

//Receive Stock Transfer Receive

public class ReciveStockTransfer extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = ReciveStockTransfer.class.getName();
    Button scanner,confirmReceiveBtn ;
     TextView resultTv,textView;
    private ActionBar actionBar;
    private Toolbar toolbar;
    TextView stNumberTv,fromCCTv,toCCTv,netWeightTv;
    String stockTransferNumber;
    DataAccessHandler dataAccessHandler;
    private IntentIntegrator qrScan;
    List<String> assignedCCtoUser ;
    EditText vehicleNumber;
    private boolean isLoopDone = false;
    private String stNumber = "",date;
    Calendar c;

    private boolean isThere = false;


    //Initialize the UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recivestockscreen);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Stock Receive");
        actionBar.setDisplayHomeAsUpEnabled(true);
         c = Calendar.getInstance();


        dataAccessHandler = new DataAccessHandler(this);
        assignedCCtoUser = new ArrayList<>();
        scanner = findViewById(R.id.scanner);
        resultTv = findViewById(R.id.result);
        stNumberTv = findViewById(R.id.stNumber);
        fromCCTv = findViewById(R.id.fromCC);
        toCCTv = findViewById(R.id.toCC);
        netWeightTv = findViewById(R.id.netweight);
        textView = findViewById(R.id.errorTv);
        vehicleNumber = findViewById(R.id.stRecVehicleNumber);
        confirmReceiveBtn = findViewById(R.id.confirmReceive);
        confirmReceiveBtn.setVisibility(View.GONE);

//        vehicleNumber.setVisibility(View.GONE);

        assignedCCtoUser = dataAccessHandler.getSingleListData(Queries.getInstance().getToCCId(CommonConstants.USER_ID));

        qrScan = new IntentIntegrator(this);
        //attaching onclick listener
        scanner.setOnClickListener(this);
        confirmReceiveBtn.setOnClickListener(this);


    }

    //Decoding the Scanned QR Code
    private void decodeStockTransferNumber() {
        isThere = false;

        if (!TextUtils.isEmpty(stockTransferNumber)){


            String year = stockTransferNumber.substring(0,4);
            int tabId = Integer.parseInt(stockTransferNumber.substring(4,7));
            int fromCCId = Integer.parseInt(stockTransferNumber.substring(7,10));
            //Log.d("fromCCID", fromCCId + "");
            int toCCId = Integer.parseInt(stockTransferNumber.substring(10,13));
            int maxCount = Integer.parseInt(stockTransferNumber.substring(13,18));
            int netWeight = 0;
            String  days = "";

            if(stockTransferNumber.length() == 24)
            {
                netWeight  = Integer.parseInt(stockTransferNumber.substring(18,24));
            }

            if(stockTransferNumber.length() == 27)
            {
                netWeight  = Integer.parseInt(stockTransferNumber.substring(21,27));
                days  = stockTransferNumber.substring(18,21);
            }



            String tabName =  dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getTabName(tabId));
            String[] fromCCNameCode = dataAccessHandler.getTwoValueFromDb(Queries.getInstance().getCCName(fromCCId)).split("@");
            //Log.d("fromCCNameCode", fromCCNameCode[1] + "");
            String[] toCCNameCode = dataAccessHandler.getTwoValueFromDb(Queries.getInstance().getCCName(toCCId)).split("@");
            stNumber = "CST"+year+tabName+fromCCNameCode[0]+days+"-"+String.valueOf(maxCount);
            stNumberTv.setText(stNumber);
            fromCCTv.setText(fromCCNameCode[1]);
            toCCTv.setText(toCCNameCode[1]);
            netWeightTv.setText(String.valueOf(netWeight)+"kg");

            for (int  i = 0 ; i < assignedCCtoUser.size();i++){

                if (assignedCCtoUser.get(i).equalsIgnoreCase(String.valueOf(toCCId))){

                    isThere = true;
                }

            }

            if(isThere)
            {
                confirmReceiveBtn.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }
            else {
                confirmReceiveBtn.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    //On Click Events for Scan and Confirm Buttons
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanner:
                qrScan.initiateScan();
                break;
            case R.id.confirmReceive:
                SimpleDateFormat stReceiveDate = new SimpleDateFormat(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS, Locale.US);
                    date = stReceiveDate.format(c.getTime());
                    updateStockTransferReceive();
                break;
        }

    }

    //Saving Data to Table
    private void updateStockTransferReceive() {
        StockTransferReceive stockTransferReceive = new StockTransferReceive();
        stockTransferReceive.setCode(stNumber);
//        stockTransferReceive.setVehicleNumber("");
        stockTransferReceive.setStockUpdate(1);
        stockTransferReceive.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        stockTransferReceive.setUpdatedDate(date);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JSONObject stData = null;
        List sTReceiveList = null;
        try {

            stData = new JSONObject(gson.toJson(stockTransferReceive));
            sTReceiveList = new ArrayList();
            sTReceiveList.add(CommonUtils.toMap(stData));
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "@@ Stock Receive data" + stData.toString());
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(this);
        final CCDataAccessHandler ccDataAccessHandler = new CCDataAccessHandler(this);

        dataAccessHandler.insertData(TABLE_STOCK_RECEIVE, sTReceiveList, this, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success){
                    if (CommonUtils.isNetworkAvailable(getApplicationContext())){
                        final List<StockReceiveRefresh> stockTransferReceiveList = ccDataAccessHandler.getStockTransferReceive();
                        if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty()) {
                            DataSyncHelper.syncStockTransferReceiveData(getApplicationContext(), stockTransferReceiveList, ccDataAccessHandler, new ApplicationThread.OnComplete<String>(){
                                @Override
                                public void execute(boolean success, final String result, String msg) {

                                    if (success){

                                        ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                            @Override
                                            public void run() {
                                                UiUtils.showCustomToastMessage("Stock Receive Updated Successfully", getApplicationContext(), 0);
                                                finish();

                                            }
                                        });
                                    }else {

                                        ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                            @Override
                                            public void run() {
                                                UiUtils.showCustomToastMessage("Stock Receive Sync Failed due to"+result, ReciveStockTransfer.this, 1);                                                finish();
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }else {
                        UiUtils.showCustomToastMessage("Data saved Success", getApplicationContext(), 0);
                        finish();
                    }
                }else {

                    UiUtils.showCustomToastMessage("Data Saving Failed Due To STNumber Already Available in DB", getApplicationContext(), 1);
                    finish();
                }
            }
        });

    }

    //Handling the result after scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!=null){
            if (result.getContents() == null){
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }else {
                //if qr contains data
                try {
                    stockTransferNumber = result.getContents();
                    resultTv.setText(result.getContents());


                    decodeStockTransferNumber();
                } catch (Exception e) {
                    e.printStackTrace();

//                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
           onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }



}
