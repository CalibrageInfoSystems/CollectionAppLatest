package com.oilpalm3f.mainapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.oilpalm3f.mainapp.Oil3FPalmMainApplication;


//Managing Shared Preferences
public class SharedPrefManager {

    private static SharedPrefManager instance;
   // private static Context context;



    public static void initialize() {
        if (instance == null)
            instance =  new SharedPrefManager();
    }

    private SharedPrefManager() {

    }

    public static SharedPreferences getPref() {
        return Oil3FPalmMainApplication.getInstance().getSharedPreferences("UserPrefsFile", Context.MODE_PRIVATE);
    }
    public static boolean getAlarm() {
        SharedPreferences prefs = SharedPrefManager.getPref();
        return prefs.getBoolean("is_first_time", false);
    }

    public static void setAlarm(boolean bvalue) {
        SharedPreferences.Editor editor = SharedPrefManager.getPref().edit();
        editor.putBoolean("is_first_time", bvalue);
        editor.apply();
    }
    public static boolean get_isDataSyncRunning() {
        SharedPreferences prefs = SharedPrefManager.getPref();
        return prefs.getBoolean("isDataSyncRunning", false);
    }

    public static void set_isDataSyncRunning(boolean bvalue) {
        SharedPreferences.Editor editor = SharedPrefManager.getPref().edit();
        editor.putBoolean("isDataSyncRunning", bvalue);
        editor.apply();
    }

}


