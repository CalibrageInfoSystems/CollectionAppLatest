package com.oilpalm3f.mainapp.BroadCastReciver;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import  android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.CloudDataHandler;
import com.oilpalm3f.mainapp.cloudhelper.Config;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CollectionCenterHomeScreen;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;
import com.oilpalm3f.mainapp.utils.SharedPrefManager;
import com.oilpalm3f.mainapp.utils.SyncHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by BAliReddy on 26/3/2018.
 */

public class AlarmReceiver  extends BroadcastReceiver {
    private static final String LOG_TAG =AlarmReceiver.class.getName() ;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;

        // For our recurring task, we'll just display a message
        Intent intentToRepeat = new Intent(context, CollectionCenterHomeScreen.class);
        //set flag to restart/relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, SyncHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        //Send local notification
        SyncHelper.getNotificationManager(context).notify(SyncHelper.ALARM_TYPE_RTC, repeatedNotification);

        Log.i("AlarmReceiver","AlarmReceiver is wake up");
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        if(CommonUtils.isNetworkAvailable(mContext))
        {
            syncMaster();
        }

    }
    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Morning Notification")
                        .setAutoCancel(true);

        return builder;
    }
    public void syncMaster() {
        if(CommonUtils.isNetworkAvailable(mContext))
        {
        SharedPrefManager.set_isDataSyncRunning(true);
        }
     //   EventBus.getDefault().post(true);

        LinkedHashMap<String, String> syncDataMap = new LinkedHashMap<>();
        final boolean firstTimeInsertFinished=false;
        if (!firstTimeInsertFinished) {
            syncDataMap.put("LastUpdatedDate", "");
        } else {
            syncDataMap.put("LastUpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY));
        }

        final int[] countCheck = {0};
        final DataAccessHandler dataAccessHandler = new DataAccessHandler(mContext);
        ProgressBar.showProgressBar(mContext, "Making data ready for you...");
        CloudDataHandler.getMasterData(Config.live_url + Config.masterSyncUrl, syncDataMap, new ApplicationThread.OnComplete<HashMap<String, List>>() {
            @Override
            public void execute(boolean success, final HashMap<String, List> masterData, String msg) {
                if (success) {
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncMaster", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    if (masterData != null && masterData.size() > 0) {
                      Log.v(LOG_TAG, "@@@ Master sync is success and data size is " + masterData.size());
                        final Set<String> tableNames = masterData.keySet();
                        for (final String tableName : tableNames) {
                            Log.v(LOG_TAG, "@@@ Delete Query " + String.format(Queries.getInstance().deleteTableData(), tableName));
                            ApplicationThread.dbPost("Master Data Sync..", "master data", new Runnable() {
                                @Override
                                public void run() {
                                    countCheck[0]++;
                                    if (!firstTimeInsertFinished) {
                                        dataAccessHandler.deleteRow(tableName, null, null, false, new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncMaster", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                    dataAccessHandler.insertData(tableName, masterData.get(tableName), mContext, new ApplicationThread.OnComplete<String>() {
                                                        @Override
                                                        public void execute(boolean success, String result, String msg) {
                                                            if (success) {
                                                                Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                            } else {
                                                                    Log.v(LOG_TAG, "@@@ check 1 " + masterData.size() + "...pos " + countCheck[0]);
                                                               Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                            }

                                                            if (countCheck[0] == masterData.size()) {
                                                                Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck[0]);
                                                                ProgressBar.hideProgressBar();
                                                                Toast.makeText(mContext, "Sync is success for Master data", Toast.LENGTH_SHORT).show();
                                                              //  DataSyncHelper.startTransactionSync(RefreshSyncActivity.this, progressDialogFragment);
                                                                /*android.app.FragmentManager fm =   ((AppCompatActivity) mContext).getFragmentManager();
                                                                ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                                                                progressDialogFragment.show(fm, "progress dialog fragment");*/
                                                                DataSyncHelper.startTransactionSync(mContext, null);
                                                               // onComplete.execute(true, null, "Sync is success");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncMaster", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                                        Log.v(LOG_TAG, "@@@ Master table deletion failed for " + tableName);
                                                }
                                            }
                                        });
                                    } else {
                                        dataAccessHandler.insertData(tableName, masterData.get(tableName), mContext, new ApplicationThread.OnComplete<String>() {
                                            @Override
                                            public void execute(boolean success, String result, String msg) {
                                                if (success) {
                                                    Log.v(LOG_TAG, "@@@ sync success for " + tableName);
                                                } else {

                                                    Log.v(LOG_TAG, "@@@ check 2 " + masterData.size() + "...pos " + countCheck[0]);
                                                 Log.v(LOG_TAG, "@@@ sync failed for " + tableName + " message " + msg);
                                                }
                                                if (countCheck[0] == masterData.size()) {
                                                   Log.v(LOG_TAG, "@@@ Done with master sync " + countCheck[0]);
                                                    ProgressBar.hideProgressBar();

                                                    Toast.makeText(mContext, "Sync is success", Toast.LENGTH_SHORT).show();
                                                    //DataSyncHelper.startTransactionSync(mContext, null);
/*
                                                    EventBus.getDefault().post(false);
*/



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

                        Toast.makeText(mContext, "Sync is success", Toast.LENGTH_SHORT).show();
                    }
/*
                    EventBus.getDefault().post(false);
*/

                } else {
                    ProgressBar.hideProgressBar();
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"syncMaster", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                    Toast.makeText(mContext, "Master sync failed. Please try again", Toast.LENGTH_SHORT).show();
             //       EventBus.getDefault().post(false);

                }

                //SharedPrefManager.set_isDataSyncRunning(false);
            }
        });


    }


}