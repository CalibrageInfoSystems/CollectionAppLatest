package com.oilpalm3f.mainapp.datasync;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.CloudDataHandler;
import com.oilpalm3f.mainapp.cloudhelper.Config;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.UserSync;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.uihelper.ProgressDialogFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper.PREVIOUS_SYNC_DATE;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

/**
 * Created by skasam on 9/30/2016.
 */

//Sync Activities can be done from this screen
public class RefreshSyncActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = RefreshSyncActivity.class.getName();
    private static int consignmentCount = 0, collectionsCount = 0, collectionPlotsCountInt = 0,collectionsWithOutPlotCount = 0;
    private TextView tvcollectiondetails,stCount, tvcollectionplot, tvconsignment,
            tvconsignmenthistory, imagesCount,msg,stReceiveCount,logCount;
    private Button btnsend, btnmastersync, btnDBcopy, transSyncBtn, btresetdatabase, yesDialogButton, noDialogButton;
    private DataAccessHandler dataAccessHandler;
    private List<String> collectionCodes, consignmentCodes,CollectionWithoutPlotCodes;
    private List<Pair> collectionPlots = null;
    private ArrayList<String> allRefreshDataMap;
    public  Dialog dialog;
    UserSync userSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.refresh);
        setSupportActionBar(toolbar);
         dialog = new Dialog(RefreshSyncActivity.this);
        dialog.setContentView(R.layout.custom_alert_dailog);

        dataAccessHandler = new DataAccessHandler(this);


        //Tables Data to get deleted when Reset Data is done
        allRefreshDataMap = new ArrayList<>();
       //allRefreshDataMap.add(DatabaseKeys.TABLE_COLLECTION);
        allRefreshDataMap.add(DatabaseKeys.TABLE_ADDRESS);
        allRefreshDataMap.add(DatabaseKeys.TABLE_FORMER);
        allRefreshDataMap.add(DatabaseKeys.TABLE_PLOT);
        allRefreshDataMap.add(DatabaseKeys.TABLE_FARMERHISTORY);
        allRefreshDataMap.add(DatabaseKeys.TABLE_IDENTITYPROOF);
        allRefreshDataMap.add(DatabaseKeys.TABLE_FARMERBANK);
        allRefreshDataMap.add(DatabaseKeys.TABLE_FILEREPOSITORY);
        allRefreshDataMap.add(DatabaseKeys.TABLE_CollectionFarmer);
        allRefreshDataMap.add(DatabaseKeys.TABLE_CollectionFarmerIdentityProof);
        allRefreshDataMap.add(DatabaseKeys.TABLE_CollectionFarmerBank);
        allRefreshDataMap.add(DatabaseKeys.TABLE_CollectionFileRepository);


        CommonUtils.currentActivity = this;

        initUI();
        consignmentCount = 0;
        collectionsCount = 0;
        collectionPlotsCountInt = 0;
        collectionsWithOutPlotCount = 0;

        if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
            ProgressBar.showProgressBar(this, "updating local data...");
            collectionCodes = dataAccessHandler.getListOfCodes(Queries.getInstance().getCollectionCodes());
            CollectionWithoutPlotCodes = dataAccessHandler.getListOfCodes(Queries.getInstance().getCollectionWithOutPlotCodes());
            consignmentCodes = dataAccessHandler.getListOfCodes(Queries.getInstance().getConsignmentCodes());
            collectionPlots = dataAccessHandler.getOnlyPairData(Queries.getInstance().getCollectionPlotXrefData());
            updateCollectionsData();
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        }

    }

    //Initializing the UI
    private void initUI() {
         yesDialogButton = (Button) dialog.findViewById(R.id.Yes);
         noDialogButton = (Button) dialog.findViewById(R.id.No);
         msg = (TextView) dialog.findViewById(R.id.test);
        stCount = findViewById(R.id.stCount);
        stReceiveCount = findViewById(R.id.stReceiveCount);
        tvcollectiondetails = (TextView) findViewById(R.id.tvcollectiondetails);
        tvcollectionplot = (TextView) findViewById(R.id.tvcollectionplot);
        tvconsignment = (TextView) findViewById(R.id.tvconsignment);
        logCount = findViewById(R.id.logCount);
        btnsend = (Button) findViewById(R.id.btsynctoserver);
        btnmastersync = (Button) findViewById(R.id.btnmastersync);
        btnDBcopy = (Button) findViewById(R.id.btcopydatabase);
        tvconsignmenthistory = (TextView) findViewById(R.id.tvconsignmenthistory);
        imagesCount = (TextView) findViewById(R.id.imagesCount);
        transSyncBtn = (Button) findViewById(R.id.transSyncBtn);
        btresetdatabase = (Button) findViewById(R.id.btresetdatabase);

        TextView titleTxt = (TextView) findViewById(R.id.titleTxt);

        titleTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (btresetdatabase.getVisibility() == View.VISIBLE) {
                    btresetdatabase.setVisibility(View.GONE);
                } else {
                    btresetdatabase.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

        //Reset Data On Click Listener

        btresetdatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionsAlertDialog(true);
            }
        });

        //Send Data to Server on Long Click Listener
        btnsend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.v(LOG_TAG, "long pressed");
                CommonUtils.copyFile(RefreshSyncActivity.this);
                return true;
            }
        });

        //Transaction Sync On Click Listener
        transSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransactionsAlertDialog();
            }
        });

        btnsend.setOnClickListener(this);
        btnmastersync.setOnClickListener(this);
        btnDBcopy.setOnClickListener(this);


        bindData();

    }

    //Transaction Sync Method
    public void showTransactionsAlertDialog(final boolean fromReset) {

        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText(R.string.you_want_to_perform_transactions_sync);
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)){
                    dialog.dismiss();
                    if (fromReset) {
                        PrefUtil.putString(RefreshSyncActivity.this, PREVIOUS_SYNC_DATE, null);

                        for (String s : allRefreshDataMap) {
                            dataAccessHandler.executeRawQuery("DELETE FROM " + s);
                        }

                    }
                    FragmentManager fm = getSupportFragmentManager();
                    ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                    progressDialogFragment.show(fm, "progress dialog fragment");
                    DataSyncHelper.startTransactionSync(RefreshSyncActivity.this, progressDialogFragment);
                    btresetdatabase.setVisibility(View.GONE);
             //       List<UserSync> resetList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfResetdata());
                    List<UserSync> resetList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync(CommonConstants.USER_ID));

                    if(resetList.size()==0){
                        Log.v("@@@MM","mas");
                        if(Integer.parseInt(CommonConstants.USER_ID) != 12345)
                        {
                            addUserResetSyncDetails();
                        }

                    }else {
                        dataAccessHandler.updateResetDataSync();
                    }

                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Binding Data to the Fields
    private void bindData() {
        try {
            String Collection = dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("Collection"));
            String CollectionWithOuPlot = dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("CollectionWithOutPlot"));
            int  CollectionCount = Integer.parseInt(Collection ) + Integer.parseInt(CollectionWithOuPlot);
            tvcollectiondetails.setText(""+CollectionCount);
            stReceiveCount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("StockTransferReceive")));
            stCount.setText( dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("StockTransfer")));
            tvcollectionplot.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("CollectionPlotXref")));
            tvconsignment.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("Consignment")));
            tvconsignmenthistory.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQuery("ConsignmentStatusHistory")));
            imagesCount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getRefreshCountQueryForPictures()));
            logCount.setText(dataAccessHandler.getCountValue(Queries.getInstance().getContVisitLog()));
        } catch (Exception e) {
            e.printStackTrace();
            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"bindData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }
    }


    //On Click Listeners for SendData, Master Sync and Upload Database
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btsynctoserver:
//                List<UserSync> traList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfTraSync());
//                if(traList.size()==0){
//                    Log.v("@@@MM","mas");
//                    addUserTraSyncDetails();
//                }

                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                CommonUtils.isNotSyncScreen = true;
                    DataSyncHelper.performCollectionCenterTransactionsSync(RefreshSyncActivity.this, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, final Object result, String msg) {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"btsynctoserver", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.isNotSyncScreen = false;
                                        UiUtils.showCustomToastMessage("Successfully data sent to server", RefreshSyncActivity.this, 0);
                                        bindData();
                                       // dataAccessHandler.updateUserSync();

                                    }
                                });
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"btsynctoserver", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.isNotSyncScreen = false;
                                        UiUtils.showCustomToastMessage("Sync Failed due to"+result, RefreshSyncActivity.this, 1);
                                        bindData();
                                    }
                                });
                            }
                        }

                    });

                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }


                break;

            case R.id.btnmastersync:


                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)) {
                    DataSyncHelper.performMasterSync(this, false, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            ProgressBar.hideProgressBar();
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"btnmastersync", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                if (!msg.equalsIgnoreCase("Sync is up-to-date")) {
                                    Toast.makeText(RefreshSyncActivity.this, "Data synced successfully", Toast.LENGTH_SHORT).show();

                                   // List<UserSync> userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfMasterSync());
                                    List<UserSync> userSyncList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync(CommonConstants.USER_ID));


                                    if(userSyncList.size()==0){
                                        Log.v("@@@MM","mas");

                                        if(Integer.parseInt(CommonConstants.USER_ID) != 12345)
                                        {
                                            addUserMasSyncDetails();
                                        }

                                    }else {
                                        dataAccessHandler.updateMasterSync();
                                    }

                                    // DataAccessHandler dataAccessHandler = new DataAccessHandler(RefreshSyncActivity.this);
                                   // dataAccessHandler.updateMasterSyncDate(false, CommonConstants.USER_ID);
                                } else {
                                    ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RefreshSyncActivity.this, "You have updated data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"btnmastersync", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                Log.v(LOG_TAG, "@@@ Master sync failed " + msg);
                                ApplicationThread.uiPost(LOG_TAG, "master sync message", new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RefreshSyncActivity.this, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
                break;
            case R.id.btcopydatabase:
                showAlertDialog();
                break;
            default:
                break;
        }

    }

    //Inserting Data into Usersync when Transaction sync is clicked
    public void addUserTraSyncDetails(){

        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp("3fCropCollection");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(0);
        userSync.setTransactionSync(1);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);
        dataAccessHandler.addUserSync(userSync);

    }

    //Inserting Data into Usersync when Master sync is clicked


    public void addUserMasSyncDetails(){



        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp(""+"3fCropCollection");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(1);
        userSync.setTransactionSync(0);
        userSync.setResetData(0);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);

        long resul=  dataAccessHandler.addUserSync(userSync);
        if(resul>-1){
            Log.v("@@@MM","Success");
        }

    }

    //Inserting Data into Usersync when Reset Data is clicked

    public void addUserResetSyncDetails(){



        userSync = new UserSync();
        userSync.setUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setApp("3fCropCollection");
        userSync.setDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setMasterSync(0);
        userSync.setTransactionSync(0);
        userSync.setResetData(1);
        userSync.setIsActive(1);
        userSync.setCreatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setCreatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setUpdatedByUserId(Integer.parseInt(CommonConstants.USER_ID));
        userSync.setUpdatedDate(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        userSync.setServerUpdatedStatus(0);
        dataAccessHandler.addUserSync(userSync);

    }


//Upload Database to Server Method
    public void uploadDataBase(final File uploadDbFile, final ApplicationThread.OnComplete<String> onComplete) {
        if (null != uploadDbFile) {
            final long nanoTime = System.nanoTime();
            final String filePathToSave = "/sdcard/3f_" + CommonConstants.TAB_ID + "_" + nanoTime +"_CCv_"+CommonUtils.getAppVersion(RefreshSyncActivity.this)+ ".gzip";
            final File toZipFile = getDbFileToUpload();
            CommonUtils.gzipFile(toZipFile, filePathToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"uploadDataBase", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        File dir = Environment.getExternalStorageDirectory();
                        File uploadFile = new File(dir, "3f_" + CommonConstants.TAB_ID + "_" + nanoTime+"_CCv_"+CommonUtils.getAppVersion(RefreshSyncActivity.this) + ".gzip");
                        Log.v(LOG_TAG, "@@@ file size " + uploadFile.length());
                        if (uploadFile != null) {
                            CloudDataHandler.uploadFileToServer(uploadFile, Config.live_url + Config.updatedbFile, new ApplicationThread.OnComplete<String>() {
                                @Override
                                public void execute(boolean success, String result, String msg) {
                                    onComplete.execute(success, result, msg);
                                }
                            });
                        } else {
                            onComplete.execute(false, "failed", "data base is empty");
                        }

                    } else {
                        onComplete.execute(success, result, msg);
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"uploadDataBase", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    }
                }
            });
        } else {
            onComplete.execute(false, "file upload failed", "null database");
        }

    }

    //Copying the DB
    public boolean copy(File src, File dst) throws IOException {

        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
            return false;
        }
    }

    //Getting DB File from the Path
    public File getDbFileToUpload() {
        try {
//            File dir = Environment.getExternalStorageDirectory();
            File dbFileToUpload = new File("/sdcard/3FDatabase/3foilpalm.sqlite");
            return dbFileToUpload;
        } catch (Exception e) {
            android.util.Log.w("Settings Backup", e);
        }
        return null;
    }

    //Upload Database to Server Method
    public void uploadDatabaseFile() {
        ApplicationThread.bgndPost(LOG_TAG, "upload database..", new Runnable() {
            @Override
            public void run() {
                uploadDataBase(getDbFileToUpload(), new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            Log.v(LOG_TAG, "@@@ 3f db file upload success");
                            CommonUtils.showToast("3f db file uploaded successfully", RefreshSyncActivity.this);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"uploadDatabaseFile", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        } else {
                            Log.v(LOG_TAG, "@@@ 3f db file upload failed due to " + msg);
                            CommonUtils.showToast("3f db file upload failed due to \n"+ msg, RefreshSyncActivity.this);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"uploadDatabaseFile", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        }
                    }
                });
            }
        });
    }

    //Alert Dialog when Upload Database is Clicked
    public void showAlertDialog() {
        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText("Do you want to upload data base to server ?");
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)){
                    dialog.dismiss();
                    ProgressBar.showProgressBar(RefreshSyncActivity.this, "uploading database...");
                    CommonUtils.copyFile(RefreshSyncActivity.this);
                    uploadDatabaseFile();
                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Alert Dialog when Transaction Sync is Clicked
    public void showTransactionsAlertDialog() {

        yesDialogButton.setTextColor(getResources().getColor(R.color.green));
        noDialogButton.setTextColor(getResources().getColor(R.color.btnPressedColor));
        msg.setText(R.string.you_want_to_perform_transactions_sync);
        // if button is clicked, close the custom dialog
        yesDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (CommonUtils.isNetworkAvailable(RefreshSyncActivity.this)){
                    dialog.dismiss();
                    FragmentManager fm = getSupportFragmentManager();
                    ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                    progressDialogFragment.show(fm, "progress dialog fragment");
                    DataSyncHelper.startTransactionSync(RefreshSyncActivity.this, progressDialogFragment);
                 //   List<UserSync> traList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfTraSync());
                    List<UserSync> traList = (List<UserSync>)dataAccessHandler.getUserSyncData(Queries.getInstance().countOfSync(CommonConstants.USER_ID));

                    if(traList.size()==0){
                        Log.v("@@@MM","mas");
                        if(Integer.parseInt(CommonConstants.USER_ID) != 12345)
                        {
                            addUserTraSyncDetails();
                        }
                    }else {
                        dataAccessHandler.updateTransactionSync();
                    }

                } else {
                    dialog.dismiss();
                    UiUtils.showCustomToastMessage("Please check network connection", RefreshSyncActivity.this, 1);
                }
            }
        });
        dialog.show();
        noDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
            }
        });



    }

    //Update Collections Without Plot Data
    public void updateCollectionsWithOutPlotData() {
        if (null != CollectionWithoutPlotCodes && !CollectionWithoutPlotCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectionWithOutPlot, CollectionWithoutPlotCodes.get(collectionsWithOutPlotCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionWithOutPlotDetailsStatus("'" + CollectionWithoutPlotCodes.get(collectionsWithOutPlotCount)) + "'");
                    }
                    collectionsWithOutPlotCount++;
                    if (collectionsWithOutPlotCount == CollectionWithoutPlotCodes.size()) {
                        Log.v(LOG_TAG, "CollectionWithOutPlot update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                Log.v(LOG_TAG, "CollectionWithOutPlot update finished");
                               updateConsignmentData();
                            }
                        });
                    } else {
                        updateCollectionsWithOutPlotData();
                    }
                }
            });
        }else {
            updateConsignmentData();
        }
    }
    //Update Collections Data
    public void updateCollectionsData() {
        if (null != collectionCodes && !collectionCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectioncode, collectionCodes.get(collectionsCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionDetailsStatus("'" + collectionCodes.get(collectionsCount)) + "'");
                    }
                    collectionsCount++;
                    if (collectionsCount == collectionCodes.size()) {
                        Log.v(LOG_TAG, "CollectionsData update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                updateCollectionsWithOutPlotData();
                            }
                        });
                    } else {
                        updateCollectionsData();
                    }
                }
            });
        } else {
            updateCollectionsWithOutPlotData();
        }
    }

    // Update Consignment Data
    public void updateConsignmentData() {
        if (null != consignmentCodes && !consignmentCodes.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findconsignmentcode, consignmentCodes.get(consignmentCount)), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        Log.v(LOG_TAG, "@@@@"+result);
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedConsignmentDetailsStatus("'" + consignmentCodes.get(consignmentCount)) + "'");
                    }
                    consignmentCount++;
                    if (consignmentCount == consignmentCodes.size()) {
                        Log.v(LOG_TAG, "ConsignmentData update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                updateCollectionPlotsData();
                            }
                        });
                    } else {
                        updateConsignmentData();
                    }
                }
            });
        } else {
            updateCollectionPlotsData();
        }
    }

    // Update Collection Plots Data
    public void updateCollectionPlotsData() {
        if (null != collectionPlots && !collectionPlots.isEmpty()) {
            CloudDataHandler.getRecordStatus(String.format(Config.live_url + Config.findcollectionplotcode, collectionPlots.get(collectionPlotsCountInt).first,collectionPlots.get(collectionPlotsCountInt).second), new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    Pair collectionPlotPair = collectionPlots.get(collectionPlotsCountInt);
                    if (success && !TextUtils.isEmpty(result) && result.equalsIgnoreCase("true")) {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedCollectionPlotXRefDetailsStatus(collectionPlotPair.first.toString(), collectionPlotPair.second.toString()));
                    }
                    collectionPlotsCountInt++;
                    if (collectionPlotsCountInt == collectionPlots.size()) {
                        Log.v(LOG_TAG, "CollectionPlotsData update finished");
                        ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar.hideProgressBar();
                                bindData();
                            }
                        });
                    } else {
                        updateCollectionPlotsData();
                    }
                }
            });
        } else {
            ProgressBar.hideProgressBar();
            bindData();
        }
    }

}
