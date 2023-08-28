package com.oilpalm3f.mainapp.collectioncenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.areaextension.FarmerPlotDetailsAdapter;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.uihelper.expandablecheckbox.viewholders.CheckableChildViewHolder;

/**
 * Created by siva on 06/04/17.
 */

public class AddFarmersPlotsInfoViewHolder extends CheckableChildViewHolder {

    private CheckedTextView childCheckedTextView;
    private Context context;

    private PlotDetailsObj plotdetailsObj;

    private TextView tvplotId;
    private TextView tvlandmark;
    private TextView tvplotarea;
    private TextView tvplotvillage;
    private TextView tvplotsurveynumber;
    private FarmerPlotDetailsAdapter.ClickListener listener;
    private CheckBox selectionChk;
    private View convertView;

    public AddFarmersPlotsInfoViewHolder(View itemView) {
        super(itemView);
        this.convertView = itemView;
        tvplotId = (TextView) itemView.findViewById(R.id.tvplotidvalue);
        tvlandmark = (TextView) itemView.findViewById(R.id.tvplotlandmarkvalue);
        tvplotarea = (TextView) itemView.findViewById(R.id.tvplotareavalue);
        tvplotvillage = (TextView) itemView.findViewById(R.id.tvplotvillagevalue);
        tvplotsurveynumber = (TextView) itemView.findViewById(R.id.tvplotsurveyvalue);
        selectionChk = (CheckBox) itemView.findViewById(R.id.chkSelected);
    }

    @Override
    public Checkable getCheckable() {
        return childCheckedTextView;
    }

    public void setPlotsData(PlotDetailsObj plotDetailsObj) {
        tvplotId.setText(plotdetailsObj.getPlotID());
        if (!TextUtils.isEmpty(plotdetailsObj.getPlotLandMark())) {
            tvlandmark.setText(plotdetailsObj.getPlotLandMark());
            tvlandmark.setVisibility(View.VISIBLE);
        } else {
            tvlandmark.setVisibility(View.GONE);
        }

        tvplotarea.setText("Plot size: "+plotdetailsObj.getPlotArea() + " Ha");
        tvplotvillage.setText(plotdetailsObj.getVillageName());
        tvplotsurveynumber.setText(plotdetailsObj.getSurveyNumber());
    }
}
