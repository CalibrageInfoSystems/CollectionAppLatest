package com.oilpalm3f.mainapp.BroadCastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.datasync.RefreshSyncActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class myBackgroundProcess extends BroadcastReceiver {
    private DataAccessHandler dataAccessHandler;
    @Override
    public void onReceive(Context context, Intent intent) {
        dataAccessHandler = new DataAccessHandler(context);

        Ringtone ringtone = RingtoneManager.getRingtone(context,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
       //   ringtone.play();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -15);
        Date FdaysBeforeDate = cal.getTime();
    //     Toast.makeText(context, "This is my background process: \n" + Calendar.getInstance().getTime().toString(), Toast.LENGTH_LONG).show();
        Log.e("===========>","This is my background process: \n" + FdaysBeforeDate);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datef = formatter.format(Date.parse(String.valueOf(FdaysBeforeDate)));
        Log.e("===========>","This is my background process: \n" + datef);
        dataAccessHandler.executeRawQuery("DELETE FROM ErrorLogs WHERE Date(CreatedDate) <= Date('"+datef+"')");
        Log.v("myBackgroundProcess", "delete table ErrorLogs");
       // CommonUtils.showToast("3f db file uploaded successfully", RefreshSyncActivity.this);

        SystemClock.sleep(2000);
        ringtone.stop();
    }
}