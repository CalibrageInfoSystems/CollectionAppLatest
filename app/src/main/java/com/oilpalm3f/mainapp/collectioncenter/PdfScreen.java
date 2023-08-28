package com.oilpalm3f.mainapp.collectioncenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.utils.UiUtils;

//Not using
public class PdfScreen extends AppCompatActivity {

//    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_layout);
//        mProgressBar = (ProgressBar) findViewById(R.id.google_progress);
//        View parentRel = findViewById(R.id.parentPanel);
//
//        Drawable progressDrawable = new ChromeFloatingCirclesDrawable.Builder(this)
//                .colors(getProgressDrawableColors())
//                .build();
//
//        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
//        mProgressBar.setIndeterminateDrawable(progressDrawable);
//        mProgressBar.getIndeterminateDrawable().setBounds(bounds);


        UiUtils.showCustomToastMessage("Sample", this, 0);
    }

    private int[] getProgressDrawableColors() {
        int[] colors = new int[4];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        colors[0] = ContextCompat.getColor(this, R.color.red);
        colors[1] = ContextCompat.getColor(this, R.color.blue);
        colors[2] = ContextCompat.getColor(this, R.color.yellow);
        colors[3] = ContextCompat.getColor(this, R.color.green);
        return colors;
    }
}
