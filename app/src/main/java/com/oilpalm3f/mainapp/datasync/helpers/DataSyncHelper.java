package com.oilpalm3f.mainapp.datasync.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionFarmer;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionFarmerBank;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionFarmerIdentityProof;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionFileRepository;
import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionWithOutPlot;
import com.oilpalm3f.mainapp.FaLogTracking.LocationTracker;
import com.oilpalm3f.mainapp.StockTransfer.StockReceiveRefresh;
import com.oilpalm3f.mainapp.StockTransfer.StockTransfer;
import com.oilpalm3f.mainapp.StockTransfer.StockTransferReceive;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.CloudDataHandler;
import com.oilpalm3f.mainapp.cloudhelper.Config;
import com.oilpalm3f.mainapp.cloudhelper.HttpClient;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionPlotXref;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignment;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentRepositoryRefresh;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignmentstatushistory;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.dbmodels.CollectionClass;
import com.oilpalm3f.mainapp.dbmodels.ImageDetails;
import com.oilpalm3f.mainapp.dbmodels.Address;
import com.oilpalm3f.mainapp.dbmodels.Farmer;
import com.oilpalm3f.mainapp.dbmodels.FarmerBank;
import com.oilpalm3f.mainapp.dbmodels.FarmerHistory;
import com.oilpalm3f.mainapp.dbmodels.FileRepository;
import com.oilpalm3f.mainapp.dbmodels.IdentityProof;
import com.oilpalm3f.mainapp.dbmodels.Plot;
import com.oilpalm3f.mainapp.dbmodels.UserSync;
import com.oilpalm3f.mainapp.dbmodels.VisitLog;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.dbmodels.DataCountModel;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.uihelper.ProgressDialogFragment;
import com.oilpalm3f.mainapp.utils.UiUtils;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.oilpalm3f.mainapp.cloudhelper.HttpClient.getOkHttpClient;
import static com.oilpalm3f.mainapp.common.CommonConstants.selectedPlotCode;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

@SuppressWarnings("unchecked")

//Master/Transaction/Send Data will be done from here
public class DataSyncHelper {
    public static LinkedHashMap<String, List> dataToUpdate = new LinkedHashMap<>();
    private static final String LOG_TAG = DataSyncHelper.class.getName();
    private static int countCheck, transactionsCheck = 0, imagesCount = 0, runningIndex = 0, reverseSyncTransCount = 0, innerCountCheck = 0;
    private static List<String> tableNamesList = new ArrayList<>();
    private static List<String> CollectiontableNamesList = new ArrayList<>();
    private static LinkedHashMap<String, List> CollectionCentertransactionsDataMap = new LinkedHashMap<>();
    private static int totalCount = 0;
    private static int MAX_COUNT = 5000;
    private static boolean isFailedForAnyTable = false;
    public static boolean isSyncFinished = false;
    //public static String PREVIOUS_SYNC_DATE = "previous_sync_date";
    public static String PREVIOUS_SYNC_DATE = null;
    private static List<FileRepository> totalFileRepositoryList = new ArrayList<>();
    private static ProgressDialogFragment progressDialogFragment;
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final MediaType TEXT_PLAIN = MediaType.parse("application/x-www-form-urlencoded");
    private static String IMEINUMBER;
    private static  int stReceiveCount = 0;


    //Inserting Data into Master Sync
    public static synchronized void performMasterSync(final Context context, final boolean firstTimeInsertFinished, final ApplicationThread.OnComplete onComplete) {

        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        if (!firstTimeInsertFinished) {
            IMEINUMBER = CommonUtils.getIMEInumberID(context);
            syncDataMap.put("LastUpdatedDate", "");
            syncDataMap.put("IMEINumber", IMEINUMBER);
        } else {
            syncDataMap.put("LastUpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY));
            syncDataMap.put("IMEINumber",IMEINUMBER);
        }

        countCheck = 0;
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Making data ready for you...");
        CloudDataHandler.getMasterData(Config.live_url + Config.masterSyncUrl, syncDataMap, new ApplicationThread.OnComplete<HashMap<String, List>>() {
            @Override
            public void execute(boolean success, final HashMap<String, List> masterData, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performMasterSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    if (masterData != null && masterData.size() > 0) {
                        Log.v(LOG_TAG, "@@@ Master sync is success and data size is " + masterData.size());
                        final Set<String> tableNames = masterData.keySet();
                        for (final String tableName : tableNames) {
                            Log.v(LOG_TAG, "@@@ Delete Query " + String.format(Queries.getInstance().deleteTableData(), tableName));
                            ApplicationThread.dbPost("Master Data Sync..", "master data", new Runnable() {
                                @Override
                                public void run() {
                                    countCheck++;
                                    if (!firstTimeInsertFinished) {
                                        dataAccessHandler.deleteRow(tableName, null, null, false, new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performMasterSync -- Delete Row",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                    dataAccessHandler.insertData(true, tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                                        @Override
                                                        public void execute(boolean success, String result, String msg) {
                                                            if (success) {
                                                                Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                            } else {
                                                                Log.v(LOG_TAG, "@@@ check 1 " + masterData.size() + "...pos " + countCheck);
                                                                Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                            }
                                                            if (countCheck == masterData.size()) {
                                                                Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                                ProgressBar.hideProgressBar();
                                                                onComplete.execute(true, null, "Sync is success");
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performMasterSync -- Delete Row",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                    Log.v(LOG_TAG, "@@@ Master table deletion failed for " + tableName);
                                                }
                                            }
                                        });
                                    } else {
                                        dataAccessHandler.insertData(true, tableName, masterData.get(tableName), new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                } else {
                                                    Log.v(LOG_TAG, "@@@ check 1 " + masterData.size() + "...pos " + countCheck);
                                                    Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                }
                                                if (countCheck == masterData.size()) {
                                                    Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck);
                                                    ProgressBar.hideProgressBar();
                                                    onComplete.execute(true, null, "Sync is success");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        ProgressBar.hideProgressBar();
                        Log.v(LOG_TAG, "@@@ Sync is up-to-date");
                        onComplete.execute(true, null, "Sync is up-to-date");
                    }
                } else {
                    ProgressBar.hideProgressBar();
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performMasterSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    onComplete.execute(false, null, "Master sync failed. Please try again");
                }
            }
        });
    }


    //Send Data to Server Method
    public static synchronized void performCollectionCenterTransactionsSync(final Context context, final ApplicationThread.OnComplete onComplete) {
        countCheck = 0;
        transactionsCheck = 0;
        imagesCount = 0;

        CollectiontableNamesList.clear();
        CollectionCentertransactionsDataMap.clear();
        final CCDataAccessHandler CCdataAccessHandler = new CCDataAccessHandler(context);
        ProgressBar.showProgressBar(context, "Sending data to server...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", new Runnable() {
            @Override
            public void run() {
                getCollectionCenterTransDataMap(context, new ApplicationThread.OnComplete<LinkedHashMap<String, List>>() {
                    @Override
                    public void execute(boolean success, final LinkedHashMap<String, List> transDataMap, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performCollectionCenterTransactionsSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            if (transDataMap != null && transDataMap.size() > 0) {
                                Log.v(LOG_TAG, "number " +msg);
                                Log.v(LOG_TAG, "transactions data size " + transDataMap.size());
                                Set<String> transDataTableNames = transDataMap.keySet();
                                CollectiontableNamesList.addAll(transDataTableNames);
                                CollectionCentertransactionsDataMap = transDataMap;
                                sendTrackingData(context, onComplete);
                                postCollectionCenterTransactionsData(context, CollectiontableNamesList.get(transactionsCheck), CCdataAccessHandler,msg, onComplete);
                            }
                        } else {
                            ProgressBar.hideProgressBar();
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"performCollectionCenterTransactionsSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            Log.v(LOG_TAG, "@@@ Transactions sync failed due to data retrieval error");
                            onComplete.execute(false, result, "Transactions sync failed due to data retrieval error");
                        }
                    }
                });
            }
        });

    }

    //Hitting Send data to server Api
    private static void postCollectionCenterTransactionsData(final Context context, final String tableName, final CCDataAccessHandler ccdataAccessHandler,final String number, final ApplicationThread.OnComplete onComplete) {

        List cctransDataList = CollectionCentertransactionsDataMap.get(tableName);

        final List<StockReceiveRefresh> stockTransferReceiveListt = ccdataAccessHandler.getStockTransferReceive();
//
        syncStockTransferReceiveData(context, stockTransferReceiveListt, ccdataAccessHandler, onComplete);

//        final List<ImageDetails> imagesDataa = ccdataAccessHandler.getImageDetails();
//        sendImageDetails(context, imagesDataa, ccdataAccessHandler, onComplete);

//
//        syncStockTransferReceiveData(context, stockTransferReceiveListt, ccdataAccessHandler, onComplete);

        if (null != cctransDataList && cctransDataList.size() > 0) {

            Type listType = new TypeToken<List>() {
            }.getType();
            Gson gson = null;
            if (tableName.equalsIgnoreCase("Consignment")) {
                gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
            } else {
                gson = new GsonBuilder().serializeNulls().create();
            }

            String dat = gson.toJson(cctransDataList, listType);

            JSONObject transObj = new JSONObject();
            try {
                transObj.put(tableName, new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"postCollectionCenterTransactionsData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }
            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
            Log.v(LOG_TAG, "@@@@ checknumber.." + number);
            CloudDataHandler.placeDataInCloud(transObj, Config.live_url + Config.collectiontransactionSyncURL, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"postCollectionCenterTransactionsData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        if(tableName.equals("VisitLog"))
                        {
                            ccdataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateVisitServerUpdatedStatus(), tableName));

                        }else if(tableName.equals("UserSync")){
                            ccdataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateUserSyncServerUpdatedStatus(), tableName));

                        }
                       else if(tableName.equals("Collection"))
                        {
                            ccdataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateServerUpdatedStatus(number), tableName));

                            Log.v(LOG_TAG, "@@@ update collction qurry " + String.format(Queries.getInstance().updateServerUpdatedStatus(number), tableName));
                        }else {
                            ccdataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateServerUpdatedStatus(), tableName));
                        }
                        Log.v(LOG_TAG, "@@@ Transactions sync success for " + tableName);
                        transactionsCheck++;
                        if (transactionsCheck == CollectionCentertransactionsDataMap.size()) {
                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);

                            final List<ImageDetails> imagesData = ccdataAccessHandler.getImageDetails();
                            final List<StockReceiveRefresh> stockTransferReceiveList = ccdataAccessHandler.getStockTransferReceive();

                            if (null != imagesData && !imagesData.isEmpty()) {
                                sendImageDetails(context, imagesData, ccdataAccessHandler, onComplete);
                            } else if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty() && CommonUtils.isNotSyncScreen) {
                                syncStockTransferReceiveData(context, stockTransferReceiveList, ccdataAccessHandler, onComplete);
                            } else {
                                ProgressBar.hideProgressBar();
                                onComplete.execute(true, null, "Sync is success");
                            }
                        } else {
                            postCollectionCenterTransactionsData(context, CollectiontableNamesList.get(transactionsCheck), ccdataAccessHandler,number, onComplete);
                        }
                    } else {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"postCollectionCenterTransactionsData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        ApplicationThread.uiPost(LOG_TAG, "Sync is failed", new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.showCustomToastMessage("Sync failed for "+tableName, context, 1);
                            }
                        });
                        transactionsCheck++;
                        if (transactionsCheck == CollectionCentertransactionsDataMap.size()) {
                            Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                            final List<ImageDetails> imagesData = ccdataAccessHandler.getImageDetails();
                            final List<StockReceiveRefresh> stockTransferReceiveList = ccdataAccessHandler.getStockTransferReceive();
                            if (null != imagesData && !imagesData.isEmpty()) {
                                sendImageDetails(context, imagesData, ccdataAccessHandler, onComplete);
                            }else if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty() && CommonUtils.isNotSyncScreen) {
                                syncStockTransferReceiveData(context, stockTransferReceiveList, ccdataAccessHandler, onComplete);
                            } else {
                                ProgressBar.hideProgressBar();
                                onComplete.execute(true, null, "Sync is success");
                            }
                        } else {
                            postCollectionCenterTransactionsData(context, CollectiontableNamesList.get(transactionsCheck), ccdataAccessHandler,number, onComplete);
                        }
                        Log.v(LOG_TAG, "@@@ Transactions sync failed for " + tableName);
                    }
                }
            });
        }else if(null != stockTransferReceiveListt && !stockTransferReceiveListt.isEmpty() && CommonUtils.isNotSyncScreen){
            syncStockTransferReceiveData(context, stockTransferReceiveListt, ccdataAccessHandler, onComplete);
        }
        else {
            transactionsCheck++;
            if (transactionsCheck == CollectionCentertransactionsDataMap.size()) {
                Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                final List<ImageDetails> imagesData = ccdataAccessHandler.getImageDetails();
                final List<StockReceiveRefresh> stockTransferReceiveList = ccdataAccessHandler.getStockTransferReceive();
                if (null != imagesData && !imagesData.isEmpty()) {
                    sendImageDetails(context, imagesData, ccdataAccessHandler, onComplete);
                } else if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty() && CommonUtils.isNotSyncScreen) {
                    syncStockTransferReceiveData(context, stockTransferReceiveList, ccdataAccessHandler, onComplete);
                }else  {
                    ProgressBar.hideProgressBar();
                    onComplete.execute(true, null, "Sync is success");
                    Log.v(LOG_TAG, "@@@ Done with transactions sync " + transactionsCheck);
                }

            } else {
                postCollectionCenterTransactionsData(context, CollectiontableNamesList.get(transactionsCheck), ccdataAccessHandler,number, onComplete);
            }
        }
    }

    //Send Image Details
    public static void sendImageDetails(final Context context, final List<ImageDetails> imagesData, final CCDataAccessHandler dataAccessHandler, final ApplicationThread.OnComplete onComplete) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String dat = gson.toJson(imagesData.get(imagesCount));
        JSONObject transObj = null;
        try {
            transObj = new JSONObject(dat);
        } catch (JSONException e) {
            e.printStackTrace();
            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendImageDetails", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        }

        Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());

        CloudDataHandler.placeDataInCloud(transObj, Config.live_url + Config.imageUploadURL, new ApplicationThread.OnComplete<String>() {
            @Override
            public void execute(boolean success, String result, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendImageDetails",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    String COlFarmerId = (imagesData.get(imagesCount).getFarmerCode()).substring(0,9);
                    if (COlFarmerId.equalsIgnoreCase("CCFARMERW") || COlFarmerId.equalsIgnoreCase("FFBFARMER")){
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedImageDetailsStatus(imagesData.get(imagesCount).getCollectionCode(),imagesData.get(imagesCount).getFarmerCode(),
                                200));
                    }else {
                        dataAccessHandler.executeRawQuery(Queries.getInstance().updatedImageDetailsStatus(imagesData.get(imagesCount).getCollectionCode(),imagesData.get(imagesCount).getFarmerCode(),
                                100));
                    }

                    imagesCount++;
                    if (imagesCount == imagesData.size()) {
                        final List<StockReceiveRefresh> stockTransferReceiveList = dataAccessHandler.getStockTransferReceive();
                        if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty()  && CommonUtils.isNotSyncScreen) {
                            syncStockTransferReceiveData(context, stockTransferReceiveList, dataAccessHandler, onComplete);
                        }else {
                        ProgressBar.hideProgressBar();
                        onComplete.execute(true, "", "sync success");
                        }
                    } else {
                        sendImageDetails(context, imagesData, dataAccessHandler, onComplete);
                    }
                } else {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendImageDetails",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    imagesCount++;
                    if (imagesCount == imagesData.size()) {
                        selectedPlotCode.clear();
                        final List<StockReceiveRefresh> stockTransferReceiveList = dataAccessHandler.getStockTransferReceive();
                        if (null != stockTransferReceiveList && !stockTransferReceiveList.isEmpty() && CommonUtils.isNotSyncScreen) {
                            syncStockTransferReceiveData(context, stockTransferReceiveList, dataAccessHandler, onComplete);
                        }else {
                            ProgressBar.hideProgressBar();
                            onComplete.execute(true, "", "sync success");
                        }
                    } else {
                        sendImageDetails(context, imagesData, dataAccessHandler, onComplete);
                    }
                    ProgressBar.hideProgressBar();
                    onComplete.execute(false, result, "sync failed due to " + msg);
                }
            }
        });
    }

    //Send Stock Transfer Receive Data
    public static void syncStockTransferReceiveData(final Context context, List<StockReceiveRefresh> stockTransferReceiveList, final CCDataAccessHandler ccDataAccessHandler, final ApplicationThread.OnComplete onComplete){
         final int count = stockTransferReceiveList.size();
         stReceiveCount = 0;

        for(final StockReceiveRefresh stockReceiveRefresh : stockTransferReceiveList){
            final List singleStockReceive = new ArrayList<>();
            singleStockReceive.add(stockReceiveRefresh);

            Gson gson = new GsonBuilder().serializeNulls().create();
            String dat = gson.toJson(singleStockReceive);

            JSONObject transObj = new JSONObject();
            try {
                transObj.put("UpdateStockTransfer", new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncStockTransferReceiveData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }

            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());

            CloudDataHandler.placeDataInCloud(transObj, Config.live_url + Config.updateStockReceive, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncStockTransferReceiveData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        ccDataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateStockReceiveRecord(stockReceiveRefresh.getCode()), "StockTransferReceive"));
                          stReceiveCount++;

                          if (stReceiveCount == count){
                              CommonUtils.isNotSyncScreen = false;
                              ProgressBar.hideProgressBar();
                              onComplete.execute(true, result, "Sync Success");
                          }
                    } else {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncStockTransferReceiveData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        stReceiveCount++;
                        if (stReceiveCount == count){
                            CommonUtils.isNotSyncScreen = false;
                            ProgressBar.hideProgressBar();
                            onComplete.execute(false, "Sync Failed For Stock Receive due to " +result,  msg);
                        }
                        UiUtils.showCustomToastMessage("Stock Receive Sync Failed due to"+CommonUtils.isNotSyncScreen, context, 1);
//                        ProgressBar.hideProgressBar();
//                        onComplete.execute(false, "Sync Failed For Stock Receive due to " +result,  msg);
                    }
                }
            });
        }
        ProgressBar.hideProgressBar();

    }



//Data Map for each table sending to server
    private static void getCollectionCenterTransDataMap(final Context context, final ApplicationThread.OnComplete<LinkedHashMap<String, List>> onComplete) {

        final CCDataAccessHandler ccdataAccessHandler = new CCDataAccessHandler(context);
//        final DataAccessHandler dataAccessHandler=new DataAccessHandler(context);

        List<StockTransfer> stockTransferList = ccdataAccessHandler.getStockTransferDetailsData();

        CollectionClass collectionDetailsRefresh = ccdataAccessHandler.getCollectionDetailsRefresh();
        List<CollectionPlotXref> collectionPlotXRefRefresh = ccdataAccessHandler.getCollectionPlotXRefRefresh();
        List<Consignment> ConsignmentDetails = ccdataAccessHandler.getConsignementRefresh();
        List<Consignmentstatushistory> consignementHistoryRefresh = ccdataAccessHandler.getConsignementHistoryRefresh();
        List<ConsignmentRepositoryRefresh> consignmentFileRepo =  ccdataAccessHandler.getConsignementFileRefresh();
        List<CollectionWithOutPlot> collectionWithOutPlotListRefresh = ccdataAccessHandler.getCollectionWithoutPlotListRefresh();
        List<VisitLog> visitLogList = (List<VisitLog>) ccdataAccessHandler.getVisitLogData(Queries.getInstance().getVistLogs());
        List<UserSync> userSylist=(List<UserSync>)ccdataAccessHandler.getUserSyncData(Queries.getInstance().getUserSyncDetails());
        List<StockReceiveRefresh> stocktransferreceive=(List<StockReceiveRefresh>)ccdataAccessHandler.getStockTransferReceive();


        LinkedHashMap<String, List> allCCDataMap = new LinkedHashMap<>();

        allCCDataMap.put(DatabaseKeys.TABLE_STOCK_TRANSFER,stockTransferList);
        allCCDataMap.put(DatabaseKeys.TABLE_COLLECTION,collectionDetailsRefresh.getCollectionList());
        allCCDataMap.put(DatabaseKeys.TABLE_COLLECTIONXPLOTREF, collectionPlotXRefRefresh);
        allCCDataMap.put(DatabaseKeys.TABLE_CONSIGNMENT, ConsignmentDetails);
        allCCDataMap.put(DatabaseKeys.TABLE_CONSIGNMENTSTATUSHISTORY, consignementHistoryRefresh);
        allCCDataMap.put(DatabaseKeys.TABLE_CONSIGNMENT_FileRepo,consignmentFileRepo);
        allCCDataMap.put(DatabaseKeys.TABLE_CollectionWithOutPlot, collectionWithOutPlotListRefresh);
        allCCDataMap.put(DatabaseKeys.TABLE_VisitLog,visitLogList);
        allCCDataMap.put(DatabaseKeys.TABLE_Userync,userSylist);
        allCCDataMap.put(DatabaseKeys.TABLE_STOCK_RECEIVE,stocktransferreceive);


        onComplete.execute(true, allCCDataMap,collectionDetailsRefresh.getData());


    }

//Send Tracking Data
    public static void sendTrackingData(final Context context, final ApplicationThread.OnComplete onComplete) {
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
        List<LocationTracker> gpsTrackingList = (List<LocationTracker>) dataAccessHandler.getGpsTrackingData(Queries.getInstance().getGpsTrackingRefresh());
        if (null != gpsTrackingList && !gpsTrackingList.isEmpty()) {
            Type listType = new TypeToken<List>() {
            }.getType();
            Gson gson = null;
            gson = new GsonBuilder().serializeNulls().create();
            String dat = gson.toJson(gpsTrackingList, listType);
            JSONObject transObj = new JSONObject();
            try {
                transObj.put("LocationTracker", new JSONArray(dat));
            } catch (JSONException e) {
                e.printStackTrace();
                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendTrackingData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            }
            Log.v(LOG_TAG, "@@@@ check.." + transObj.toString());
            CloudDataHandler.placeDataInCloud(transObj, Config.live_url + Config.locationTrackingURL, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendTrackingData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                dataAccessHandler.executeRawQuery(String.format(Queries.getInstance().updateVisitServerUpdatedStatus(), DatabaseKeys.LocationTracker));
                                Log.v(LOG_TAG, "@@@ Transactions sync success for LocationTracker");
                                onComplete.execute(true, null, "Sync is success");
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"sendTrackingData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(false, null, "Sync is failed");
                            }
                        }
                    }
            );

        }
    }



//Transaction Sync Starts here
    public static void startTransactionSync(final Context context, final ProgressDialogFragment progressDialogFragment) {
        final String date = PrefUtil.getString(context, PREVIOUS_SYNC_DATE);
        Log.v(LOG_TAG, "@@@ Date "+date);
        progressDialogFragment.updateText("Getting total records count");
        getCountOfHits(date, new ApplicationThread.OnComplete() {
            @Override
            public void execute(boolean success, Object result, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"startTransactionSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    try {
                        Log.v(LOG_TAG, "@@@@ count here " + result.toString());
                       // Map countMap = CommonUtils.toMap(new JSONObject(result.toString()));
                        List<DataCountModel> dataCountModelList = (List<DataCountModel>) result;
                        if (dataCountModelList.isEmpty()) {
                            ProgressBar.hideProgressBar();
                            if (null != progressDialogFragment) {
                                progressDialogFragment.dismiss();
                            }
                            CommonUtils.showToast("There is no transactions data to sync", context);
                        } else {
                            prepareIndexes(date, dataCountModelList, context, progressDialogFragment);
                        }
                        //prepareIndexes(finalDate, countMap, context, finalProgressDialogFragment);
                    } catch (Exception e) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"startTransactionSync",CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        e.printStackTrace();
                        ProgressBar.hideProgressBar();
                        if (null != progressDialogFragment) {
                            progressDialogFragment.dismiss();
                        }
                        CommonUtils.showToast("Transaction sync failed due to ", context);
                    }
                } else {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"startTransactionSync",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    if (null != progressDialogFragment) {
                        progressDialogFragment.dismiss();
                    }
                    CommonUtils.showToast("Transaction sync failed due to ", context);
                }
            }
        });
    }

    //Preparing Transaction Sync Tables to sync
    public static void prepareIndexes(final String date, List<DataCountModel> countData, final Context context, ProgressDialogFragment progressDialogFragment) {
        dataToUpdate.clear();
        final List<DataCountModel> countDataList = new ArrayList<>();
        for (int i = 0; i < countData.size(); i++) {
            if (countData.get(i).getTableName().equalsIgnoreCase("Address")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncAddresses", "Address"));
            } else if (countData.get(i).getTableName().equalsIgnoreCase("Farmer")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncFarmers", "Farmer"));

            } else if (countData.get(i).getTableName().equalsIgnoreCase("Plot")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncPlots", "Plot"));

            } else if (countData.get(i).getTableName().equalsIgnoreCase("FarmerBank")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncFarmerBanks", "FarmerBank"));

            } else if (countData.get(i).getTableName().equalsIgnoreCase("FarmerHistory")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncFarmerHistory", "FarmerHistory"));

            } else if (countData.get(i).getTableName().equalsIgnoreCase("CollectionFileRepository")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncCollectionFileRepositoryDetails", "CollectionFileRepository"));
            }
            else if (countData.get(i).getTableName().equalsIgnoreCase("CollectionFarmerIdentityProof")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncCollectionFarmerIdentityProofDetails", "CollectionFarmerIdentityProof"));
            }
            else if (countData.get(i).getTableName().equalsIgnoreCase("CollectionFarmerBank")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncCollectionFarmerBankDetails", "CollectionFarmerBank"));
            }
            else if (countData.get(i).getTableName().equalsIgnoreCase("CollectionFarmer")) {
                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncCollectionFarmerDetails", "CollectionFarmer"));
            }
//            else if (countData.get(i).getTableName().equalsIgnoreCase("Collection")) {
//                countDataList.add(new DataCountModel(countData.get(i).getCount(), "SyncCollectionDetails", "Collection"));
//            }
//            else if(countData.get(i).getTableName().equalsIgnoreCase("UserSync")){
//                countDataList.add(new DataCountModel(countData.get(i).getCount(),"SyncUserSync","UserSync"));
//            }

        }


        if (!countDataList.isEmpty()) {
            runningIndex = 0;
            totalCount = 0;
            totalFileRepositoryList.clear();
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);

            if (!countDataList.isEmpty()) {
                reverseSyncTransCount = 0;
                transactionsCheck = 0;
                dataToUpdate.clear();

                //final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
                new DownLoadData(context, date, countDataList, 0, 0, dataAccessHandler, progressDialogFragment).execute();
            } else {
                ProgressBar.hideProgressBar();
                if (null != progressDialogFragment) {
                    progressDialogFragment.dismiss();
                }
                CommonUtils.showToast("There is no transactions data to sync", context);
            }
        } else {
            ProgressBar.hideProgressBar();
            if (null != progressDialogFragment) {
                progressDialogFragment.dismiss();
            }
            CommonUtils.showToast("There is no transactions data to sync", context);
        }
    }

    //Reject Object for Get Count and hitting it
    public static void getCountOfHits(String date, final ApplicationThread.OnComplete onComplete) {
        String countUrl = "";
        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        syncDataMap.put("Date", TextUtils.isEmpty(date) ? "null" : date);
        //syncDataMap.put("Date", "2022-12-14 14:25:05");
        syncDataMap.put("UserId", CommonConstants.USER_ID);
        syncDataMap.put("IsUserDataAccess", "true");
        countUrl = Config.live_url + String.format(Config.getTransCount);

        CloudDataHandler.getGenericData(countUrl, syncDataMap, new ApplicationThread.OnComplete<List<DataCountModel>>() {
            @Override
            public void execute(boolean success, List<DataCountModel> result, String msg) {
                onComplete.execute(success, result, msg);
            }
        });
    }

    //Updating Sync Performed last date
    public static void updateSyncDate(Context context) {
        Log.v(LOG_TAG, "@@@ saving date into");
        PrefUtil.putString(context, PREVIOUS_SYNC_DATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        Log.v("currentdate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS + ""));
        Log.v("newdate", PREVIOUS_SYNC_DATE + "");
    }



    //Downloading the Tables Data
    public static class DownLoadData extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private Context context;
        private String date;
        private List<DataCountModel> totalData;
        private int totalDataCount;
        private int currentIndex;
        private DataAccessHandler dataAccessHandler;
        private ProgressDialogFragment progressDialogFragment;

        DownLoadData(final Context context, final String date, final List<DataCountModel> totalData, int totalDataCount, int currentIndex, DataAccessHandler dataAccessHandler, ProgressDialogFragment progressDialogFragment) {
            this.context = context;
            this.totalData = totalData;
            this.date = date;
            this.totalDataCount = totalDataCount;
            this.currentIndex = currentIndex;
            this.dataAccessHandler = dataAccessHandler;
            this.progressDialogFragment = progressDialogFragment;
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            String resultMessage = null;
            //date = null;

            String countUrl = Config.live_url + String.format(Config.getTransData, totalData.get(totalDataCount).getMethodName());
            Log.v(LOG_TAG, "@@@ data sync url "+countUrl);
            final String tableName = totalData.get(totalDataCount).getTableName();
            progressDialogFragment.updateText("Downloading "+tableName.toString()+" ("+currentIndex+"/"+totalData.get(totalDataCount).getCount()+")"+" data");
            if (tableName.equalsIgnoreCase("FileRepository")) {
                Log.v(LOG_TAG, "@@@@ FileRepository table data "+totalFileRepositoryList.size());
            }
            try {
                Response response = null;
                URL obj = new URL(countUrl);
                Map<String, String> syncDataMap = new LinkedHashMap<>();
                syncDataMap.put("Date", TextUtils.isEmpty(date) ? "null" : date);
                //syncDataMap.put("Date", "2022-12-14 14:25:05");
                syncDataMap.put("UserId", CommonConstants.USER_ID);
                syncDataMap.put("IsUserDataAccess", "true");
                syncDataMap.put("Index", String.valueOf(currentIndex));
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("User-Agent", USER_AGENT);

                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                RequestBody requestBody = null;
                for (Map.Entry<String, String> entry : syncDataMap.entrySet()) {
                    if (first) first = false;
                    else sb.append("&");
                    sb.append(URLEncoder.encode(entry.getKey(), HTTP.UTF_8)).append("=")
                            .append(URLEncoder.encode(entry.getValue().toString(), HTTP.UTF_8));

                    Log.d(LOG_TAG, "\nposting key: " + entry.getKey() + " -- value: " + entry.getValue());
                }
                requestBody = RequestBody.create(TEXT_PLAIN, sb.toString());

                Request request = HttpClient.buildRequest(countUrl, "POST", (requestBody != null) ? requestBody : RequestBody.create(TEXT_PLAIN, "")).build();
                OkHttpClient client = getOkHttpClient();
                response = client.newCall(request).execute();
                int statusCode = response.code();

                final String strResponse = response.body() != null ? response.body().string() : null;
                Log.d(LOG_TAG, " ############# POST RESPONSE ################ (" + statusCode + ")\n\n" + strResponse + "\n\n");
                JSONArray parentTransactionSyncObject = new JSONArray(strResponse);
                if (statusCode == HttpURLConnection.HTTP_OK) {

                    if (TextUtils.isEmpty(date)) {
                        List<LinkedHashMap> dataToInsert = new ArrayList<LinkedHashMap>();
                        for (int i = 0; i < parentTransactionSyncObject.length(); i++) {
                            JSONObject eachDataObject = parentTransactionSyncObject.getJSONObject(i);
                            dataToInsert.add(CommonUtils.toMap(eachDataObject));
                        }
                        dataAccessHandler.insertData(tableName, dataToInsert, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                Log.v(LOG_TAG, "@@@@ Data insertion status "+result);
                            }
                        });
                    /*List dataToInsert = new ArrayList();
                    JSONArray parentTransactionSyncObject = new JSONArray(response.toString());
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject eachDataObject = dataArray.getJSONObject(i);
                        dataToInsert.add(CommonUtils.toMap(eachDataObject));
                    }*/

                      /*  dataAccessHandler.insertData(tableName, dataToInsert, context, new ApplicationThread.OnComplete<String>() {
                            @Override
                            public void execute(boolean success, String result, String msg) {
                                Log.v(LOG_TAG, "@@@@ Data insertion status "+result);
                            }
                        });*/
                    } else {
                        if (tableName.equalsIgnoreCase("FileRepository")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FileRepository>>() {
                            }.getType();
                            List<FileRepository> fileRepositoryInnerList = gson.fromJson(response.toString(), type);
                            totalFileRepositoryList.addAll(fileRepositoryInnerList);
                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FORMER)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Farmer>>() {
                            }.getType();
                            List<Farmer> farmerDataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != farmerDataList && farmerDataList.size() > 0)
                                dataToUpdate.put(tableName, farmerDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_PLOT)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Plot>>() {
                            }.getType();
                            List<Plot> plotDataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != plotDataList && plotDataList.size() > 0)
                                dataToUpdate.put(tableName, plotDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FARMERHISTORY)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FarmerHistory>>() {
                            }.getType();
                            List<FarmerHistory> farmerHistoryDataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != farmerHistoryDataList && farmerHistoryDataList.size() > 0)
                                dataToUpdate.put(tableName, farmerHistoryDataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FARMERBANK)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FarmerBank>>() {
                            }.getType();
                            List<FarmerBank> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_IDENTITYPROOF)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<IdentityProof>>() {
                            }.getType();
                            List<IdentityProof> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        }
//                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_COLLECTION)) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<List<Collection>>() {
//                            }.getType();
//                            List<Collection> collectionDataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
//                            if (null != collectionDataList && collectionDataList.size() > 0)
//                                dataToUpdate.put(tableName, collectionDataList);
//                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmer)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollectionFarmer>>() {
                            }.getType();
                            List<CollectionFarmer> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmerIdentityProof)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollectionFarmerIdentityProof>>() {
                            }.getType();
                            List<CollectionFarmerIdentityProof> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFileRepository)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollectionFileRepository>>() {
                            }.getType();
                            List<CollectionFileRepository> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        }
                        else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmerBank)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollectionFarmerBank>>() {
                            }.getType();
                            List<CollectionFarmerBank> dataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != dataList && dataList.size() > 0)
                                dataToUpdate.put(tableName, dataList);
                        }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ADDRESS)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Address>>() {
                            }.getType();
                            List<Address> AddressDataList = gson.fromJson(parentTransactionSyncObject.toString(), type);
                            if (null != AddressDataList && AddressDataList.size() > 0)
                                dataToUpdate.put(tableName, AddressDataList);
                        }


//                        else if(tableName.equalsIgnoreCase("UserSync")){
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<List<UserSync>>() {
//                            }.getType();
//                            List<UserSync> userSyncList = gson.fromJson(parentTransactionSyncObject.toString(), type);
//                            if (null != userSyncList && userSyncList.size() > 0)
//                                dataToUpdate.put(tableName, userSyncList);
//                        }

                    }

                    resultMessage = "success";
                } else {
                    resultMessage = "failed";
                }
            } catch (Exception e) {
                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"doInBackground",CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                resultMessage = e.getMessage();
                Log.e(LOG_TAG, "@@@ data sync failed for "+tableName);
            }
            return resultMessage;
        }

//Executing the Tables Data
        @Override
        protected void onPostExecute(String result) {
            currentIndex++;
            if (currentIndex == totalData.get(totalDataCount).getCount()) {
                currentIndex = 0;
                totalDataCount++;
                if (totalDataCount == totalData.size()) {
                    if (TextUtils.isEmpty(date)) {
                        ProgressBar.hideProgressBar();
                        if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                            progressDialogFragment.dismiss();
                        }
                        UiUtils.showCustomToastMessage("Data synced successfully", context, 0);
                        Log.d("Sync", "Success");
                        updateSyncDate(context);
                    } else {
                        reverseSyncTransCount = 0;
                        Set<String> tableNames = dataToUpdate.keySet();
                        List<String> tableNamesList = new ArrayList<>();
                        tableNamesList.addAll(tableNames);
                        updateTransactionData(dataToUpdate, dataAccessHandler, tableNamesList, progressDialogFragment, new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"onPostExecute",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    updateSyncDate(context);
                                    UiUtils.showCustomToastMessage("Data synced successfully", context, 0);
                                } else {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"onPostExecute",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    UiUtils.showCustomToastMessage(msg, context, 1);
                                }
                                if (null != progressDialogFragment && !CommonUtils.currentActivity.isFinishing()) {
                                    progressDialogFragment.dismiss();
                                }
                            }
                        });
                    }
                } else {
                    Log.v(LOG_TAG, "@@@ data downloading next count "+currentIndex+" out of "+totalData.size());
                    new DownLoadData(context, date, totalData, totalDataCount, currentIndex, dataAccessHandler, progressDialogFragment).execute();
                }
            } else {
                Log.v(LOG_TAG, "@@@ data downloading next count "+currentIndex+" out of "+totalData.size());
                new DownLoadData(context, date, totalData, totalDataCount, currentIndex, dataAccessHandler, progressDialogFragment).execute();
            }
        }
    }

    //Update Transaction Sync
    public static synchronized void updateTransactionData(final LinkedHashMap<String, List> transactionsData, final DataAccessHandler dataAccessHandler, final List<String> tableNames, final ProgressDialogFragment progressDialogFragment , final ApplicationThread.OnComplete onComplete) {
        progressDialogFragment.updateText("Updating data...");
        if (transactionsData != null && transactionsData.size() > 0) {
            Log.v(LOG_TAG, "@@@ Transactions sync is success and data size is " + transactionsData.size());
            final String tableName = tableNames.get(reverseSyncTransCount);
            innerCountCheck = 0;
            updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, new ApplicationThread.OnComplete() {
                @Override
                public void execute(boolean success, Object result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateTransactionData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        reverseSyncTransCount++;
                        if (reverseSyncTransCount == transactionsData.size()) {
                            onComplete.execute(success, "data updated successfully", "");
                            progressDialogFragment.dismiss();
                        } else {
                            updateTransactionData(transactionsData, dataAccessHandler, tableNames, progressDialogFragment, onComplete);
                        }
                    } else {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateTransactionData",CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        reverseSyncTransCount++;
                        if (reverseSyncTransCount == transactionsData.size()) {
                            onComplete.execute(success, "data updated successfully", "");
                            progressDialogFragment.dismiss();
                        } else {
                            updateTransactionData(transactionsData, dataAccessHandler, tableNames, progressDialogFragment, onComplete);
                        }
                    }
                }
            });
        } else {
            onComplete.execute(false, "data not found to save", "");
        }
    }

    //To Update the data into the Tables
    public static synchronized void updateDataIntoDataBase(final LinkedHashMap<String, List> transactionsData, final DataAccessHandler dataAccessHandler, final String tableName, final ApplicationThread.OnComplete onComplete) {
        final List dataList = transactionsData.get(tableName);
        List<LinkedHashMap> dataToInsert = new ArrayList<>();
        JSONObject ccData = null;
        Gson gson = new GsonBuilder().serializeNulls().create();

        boolean recordExisted = false;
        String whereCondition = null;

        if (dataList.size() > 0) {
           if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FORMER)) {
                Farmer farmerData = (Farmer) dataList.get(innerCountCheck);
                farmerData.setServerupdatedstatus(0);
                whereCondition = " where  Code = '" + farmerData.getCode() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(farmerData));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                    recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Code", farmerData.getCode()));
                } catch (JSONException e) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
            }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_ADDRESS)) {
               Address plotData = (Address) dataList.get(innerCountCheck);
               plotData.setServerupdatedstatus(1);
               whereCondition = " where  Code= '" + plotData.getCode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(plotData));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Code", plotData.getCode()));
           }
            else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_PLOT)) {
                Plot plotData = (Plot) dataList.get(innerCountCheck);
                plotData.setServerupdatedstatus(1);
                whereCondition = " where  Code= '" + plotData.getCode() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(plotData));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Code", plotData.getCode()));
            } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FARMERHISTORY)) {
                FarmerHistory farmerHistoryData = (FarmerHistory) dataList.get(innerCountCheck);
                farmerHistoryData.setServerUpdatedStatus(1);
                whereCondition = " where  PlotCode = '" + farmerHistoryData.getPlotcode() + "'";
                try {
                    ccData = new JSONObject(gson.toJson(farmerHistoryData));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
                }
                recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "PlotCode", farmerHistoryData.getPlotcode()));
            } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_FARMERBANK)) {
               FarmerBank data = (FarmerBank) dataList.get(innerCountCheck);
               whereCondition = " where FarmerCode = '" + data.getFarmercode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(data));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FarmerCode", data.getFarmercode()));
           } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_IDENTITYPROOF)) {
               IdentityProof data = (IdentityProof) dataList.get(innerCountCheck);
               whereCondition = " where FarmerCode = '" + data.getFarmercode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(data));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FarmerCode", data.getFarmercode()));
           }else  if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmer)) {
               CollectionFarmer collectionfarmerData = (CollectionFarmer) dataList.get(innerCountCheck);
               collectionfarmerData.setServerupdatedstatus(0);
               whereCondition = " where  Code = '" + collectionfarmerData.getCode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(collectionfarmerData));
                   dataToInsert.add(CommonUtils.toMap(ccData));
                   recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Code", collectionfarmerData.getCode()));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
           }
//           else  if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_COLLECTION)) {
//               Collection collectionData = (Collection) dataList.get(innerCountCheck);
//               collectionData.setServerUpdatedStatus(false);
//               whereCondition = " where  Code = '" + collectionData.getCode() + "'";
//               try {
//                   ccData = new JSONObject(gson.toJson(collectionData));
//                   dataToInsert.add(CommonUtils.toMap(ccData));
//                   recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "Code", collectionData.getCode()));
//               } catch (JSONException e) {
//            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
//                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
//               }
//           }
           else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmerBank)) {
               CollectionFarmerBank data = (CollectionFarmerBank) dataList.get(innerCountCheck);
               whereCondition = " where FarmerCode = '" + data.getFarmercode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(data));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FarmerCode", data.getFarmercode()));
           } else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFarmerIdentityProof)) {
               CollectionFarmerIdentityProof data = (CollectionFarmerIdentityProof) dataList.get(innerCountCheck);
               whereCondition = " where FarmerCode = '" + data.getFarmercode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(data));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FarmerCode", data.getFarmercode()));
           }else if (tableName.equalsIgnoreCase(DatabaseKeys.TABLE_CollectionFileRepository)) {
               CollectionFileRepository data = (CollectionFileRepository) dataList.get(innerCountCheck);
               whereCondition = " where FarmerCode = '" + data.getFarmercode() + "'";
               try {
                   ccData = new JSONObject(gson.toJson(data));
                   dataToInsert.add(CommonUtils.toMap(ccData));
               } catch (JSONException e) {
                   palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateDataIntoDataBase", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                   Log.e(LOG_TAG, "####" + e.getLocalizedMessage());
               }
               recordExisted = dataAccessHandler.checkValueExistedInDatabase(Queries.getInstance().checkRecordStatusInTable(tableName, "FarmerCode", data.getFarmercode()));
           }

            if (dataList.size() != innerCountCheck) {
                updateOrInsertData(tableName, dataToInsert, whereCondition, recordExisted, dataAccessHandler, new ApplicationThread.OnComplete() {
                    @Override
                    public void execute(boolean success, Object result, String msg) {
                        innerCountCheck++;
                        if (innerCountCheck == dataList.size()) {
                            innerCountCheck = 0;
                            onComplete.execute(true, "", "");
                        } else {
                            updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, onComplete);
                        }
                    }
                });
            } else {
                onComplete.execute(true, "", "");
            }
        } else {
            innerCountCheck++;
            if (innerCountCheck == dataList.size()) {
                innerCountCheck = 0;
                onComplete.execute(true, "", "");
            } else {
                updateDataIntoDataBase(transactionsData, dataAccessHandler, tableName, onComplete);
            }
        }
    }

    //Checks whether to Update or Insert
    public static void updateOrInsertData(final String tableName, List<LinkedHashMap> dataToInsert, String whereCondition, boolean recordExisted, DataAccessHandler dataAccessHandler, final ApplicationThread.OnComplete onComplete) {
        if (recordExisted) {
            dataAccessHandler.updateData(tableName, dataToInsert, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    onComplete.execute(success, null, "Sync is " + success + " for " + tableName);
                }
            });
        } else {
            dataAccessHandler.insertData(tableName, dataToInsert, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    onComplete.execute(true, null, "Sync is " + success + " for " + tableName);
                }
            });
        }
    }
}