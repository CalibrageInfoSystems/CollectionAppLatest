package com.oilpalm3f.mainapp.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by  BAliReddy on 26/3/2018.
 */

public class TestJobService extends JobService {
    private static final String TAG = "SyncService";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onStartJob(JobParameters params) {
//        Intent service = new Intent(getApplicationContext(), LocalWordService.class);
//        getApplicationContext().startService(service);
        AutoSync.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}
