package com.oilpalm3f.mainapp.cloudhelper;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


//Common Log Class
public class Log {
    public static final boolean DEBUG_FLAG = true;
    private static final boolean DEBUG = DEBUG_FLAG;
    private static final boolean ERROR = DEBUG_FLAG;
    private static final boolean WARN  = DEBUG_FLAG;
    private static final boolean INFO  = DEBUG_FLAG;
    private static final boolean VERB  = DEBUG_FLAG;

    public static String getStackTraceString(Throwable e) {
            return android.util.Log.getStackTraceString(e);
    }

    public static void e(final String clazz, final Throwable ex) {
        if (ERROR) {
            if (null != ex.getMessage()) android.util.Log.e(clazz, ex.getMessage());
            android.util.Log.e(clazz, ex.getClass().getName());
            for (final StackTraceElement element : ex.getStackTrace()) {
                android.util.Log.e(clazz, "\t"+element.toString());
            }
        }
    }

    public static void v(final String clazz, final String msg) {
        if (VERB) android.util.Log.v(clazz, ""+msg);
    }

    public static void i(final String clazz, final String msg) {
        if (INFO) android.util.Log.i(clazz, ""+msg);
    }

    public static void w(final String clazz, final String msg) {
        if (WARN) android.util.Log.w(clazz, ""+msg);
    }

    public static void e(final String clazz, final String msg) {
        if (ERROR) android.util.Log.e(clazz, ""+msg);
    }

    public static void e(final String clazz, final String msg, final Throwable ex) {
        if (ERROR) {
            e(clazz, msg);
            e(clazz, ex);
        }
    }

    public static void d(final String clazz, final String msg) {
        if (DEBUG) android.util.Log.d(clazz, ""+msg);
    }



    /**
     * Crashlytics Exception
     */
    public static void pushExceptionToCrashlytics(Exception e) {
        if (Fabric.isInitialized())
            Crashlytics.logException(e);

    }

    public static void pushExceptionToCrashlytics(String tag, Exception e, String message) {
        e(tag, e.getMessage());
        if (Fabric.isInitialized())
            pushExceptionToCrashlytics(e);
        Crashlytics.log(message);
    }


    public static void pushExceptionToCrashlytics(String tag, String msg, Exception e) {
        e(tag, msg, e);
        pushExceptionToCrashlytics(e);
    }

    public static void pushLogToCrashlytics(String msg) {
        if (Fabric.isInitialized())
            Crashlytics.log(msg);
    }
}