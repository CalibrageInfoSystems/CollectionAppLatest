package com.oilpalm3f.mainapp.helper;

import android.content.Context;
import android.preference.PreferenceManager;

//Helps to Put/Get Data Types
public class PrefUtil {

    public static void putString(Context context, String key, String value, String pref) {
        if (context==null || key == null) { return; }
        if (pref==null || pref.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
        } else {
            context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit().putString(key, value).apply();
        }
    }

    public static String getString(Context context, String key, String pref) {
        if (context==null || key == null) { return null; }
        if (pref==null || pref.isEmpty()) {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
        } else {
            return context.getSharedPreferences(pref, Context.MODE_PRIVATE).getString(key, null);
        }
    }

    public static void
    putInt(Context context, String key, int value, String pref) {
        if (context==null || key == null) { return; }
        if (pref==null || pref.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
        } else {
            context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
        }
    }

    public static int getInt(Context context, String key, String pref) {
        if (context==null || key == null) { return 0; }
        if (pref==null || pref.isEmpty()) {
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
        } else {
            return context.getSharedPreferences(pref, Context.MODE_PRIVATE).getInt(key, 0);
        }
    }

    public static void putLong(Context context, String key, long value, String pref) {
        if (context == null || key == null) {
            return;
        }
        if (pref == null || pref.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
        } else {
            context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit().putLong(key, value).apply();
        }
    }

    public static long getLong(Context context, String key, String pref) {
        if (context == null || key == null) {
            return 0;
        }
        if (pref == null || pref.isEmpty()) {
            return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
        } else {
            return context.getSharedPreferences(pref, Context.MODE_PRIVATE).getLong(key, 0);
        }
    }

    public static void putBool(Context context, String key, boolean value, String pref) {
        if (context==null || key == null) { return; }
        if (pref==null || pref.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
        } else {
            context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
        }
    }

    public static boolean getBool(Context context, String key, String pref) {
        if (context==null || key == null) { return false; }
        if (pref==null || pref.isEmpty()) {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
        } else {
            return context.getSharedPreferences(pref, Context.MODE_PRIVATE).getBoolean(key, false);
        }
    }

    public static void putInt(Context context, String key, int value) {
        putInt(context, key, value, null);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, null);
    }

    public static int getIntvalue(Context context, String key) {
        return getInt(context, key, null);
    }

    public static void putLong(Context context, String key, long value) {
        putLong(context, key, value, null);
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, null);
    }

    public static void putBool(Context context, String key, boolean value) {
        putBool(context, key, value, null);
    }

    public static boolean getBool(Context context, String key) {
        return getBool(context, key, null);
    }

    public static void putString(Context context, String key, String value) {
        putString(context, key, value, null);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static void removeKey(Context context, String key, String pref) {
        if (context==null || key == null) { return; }
        if (pref==null || pref.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit();
        } else {
        }
    }
}
