package com.oilpalm3f.mainapp.uihelper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.ProgressBar;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;

/**
 * Created by siva on 06/03/17.
 */

//Sync Progress Dialog
public class ProgressDialogFragment extends DialogFragment {
    private static final String LOG_TAG = ProgressDialogFragment.class.getName();
    private ProgressBar mProgressBar;
    private TextView downLoadStatusTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View promptView = inflater.inflate(R.layout.latest_progressbar, null);
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
        promptView.setMinimumWidth((int) (displayRectangle.width() * 0.7f));
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color
                .TRANSPARENT));
        mProgressBar =  (android.widget.ProgressBar)promptView.findViewById(R.id.google_progress);
        downLoadStatusTxt = (TextView) promptView.findViewById(R.id.downLoadStatusTxt);

        Drawable progressDrawable = new ChromeFloatingCirclesDrawable.Builder(getActivity())
                .colors(getProgressDrawableColors(getActivity()))
                .build();

        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
        mProgressBar.setIndeterminateDrawable(progressDrawable);
        mProgressBar.getIndeterminateDrawable().setBounds(bounds);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(null);
        return promptView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,
                                 android.view.KeyEvent event) {

                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    //This is the filter
                    if (event.getAction()!= KeyEvent.ACTION_DOWN)
                        return true;
                    else
                    {
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                }
                else
                    return false; // pass on to be processed as normal
            }
        });
    }

    private static int[] getProgressDrawableColors(Context context) {
        int[] colors = new int[4];
        colors[0] = ContextCompat.getColor(context, R.color.red);
        colors[1] = ContextCompat.getColor(context, R.color.blue);
        colors[2] = ContextCompat.getColor(context, R.color.yellow);
        colors[3] = ContextCompat.getColor(context, R.color.green);
        return colors;
    }

    public void updateText(final String statusMessage) {
        ApplicationThread.uiPost(LOG_TAG, "data down load status", new Runnable() {
            @Override
            public void run() {
                downLoadStatusTxt.setText(""+statusMessage+"...");
                downLoadStatusTxt.invalidate();
            }
        });
    }
}
