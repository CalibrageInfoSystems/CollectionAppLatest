package com.oilpalm3f.mainapp.uihelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;


//Show/hide Progress Bar
public class ProgressBar {

    private static final String LOG_TAG = ProgressBar.class.getName();
    private static ProgressDialog mProgressDialog;
    private static ProgressDialog mNewProgressDialog;
    private static android.widget.ProgressBar mProgressBar;

    public static ProgressDialog showProgressBar(final Context context, final String msg) {
        hideProgressBar();


        ApplicationThread.uiPost(LOG_TAG, "hiding progress bar", new Runnable() {
            @Override
            public void run() {
                try {
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(context);
                        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                        mProgressDialog.setMessage(msg);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        return mProgressDialog;
    }

    public static void hideProgressBar() {
        ApplicationThread.uiPost(LOG_TAG, "hiding progress bar", new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    try {
                        mProgressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mProgressDialog = null;
            }
        });
    }

}
