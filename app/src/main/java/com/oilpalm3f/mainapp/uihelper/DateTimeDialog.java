package com.oilpalm3f.mainapp.uihelper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.oilpalm3f.mainapp.R;

/**
 * Created by Siva on 31/01/17.
 */

//Not Using
public class DateTimeDialog extends Dialog implements android.view.View.OnClickListener{

    private TimePicker mTime;
    private DatePicker mDate;

    public DateTimeDialog(Context context) {
        super(context);
        setContentView(R.layout.date_time);

        mTime = (TimePicker)findViewById(R.id.timePicker);
        mDate = (DatePicker)findViewById(R.id.datePicker);

        Button done = (Button)findViewById(R.id.done);
        done.setOnClickListener(this);

        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.done:
                dismiss();
            case R.id.cancel:
                dismiss();
        }

    }

    public void setTimeListener(TimePicker.OnTimeChangedListener time){
        mTime.setOnTimeChangedListener(time);
    }

    public void setDateListener(int year, int monthOfYear, int dayOfMonth, DatePicker.OnDateChangedListener date){
        mDate.init(year, monthOfYear, dayOfMonth, date);
    }
}
