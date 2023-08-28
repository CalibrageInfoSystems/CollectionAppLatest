package com.oilpalm3f.mainapp.areaextension;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.uihelper.SelectableAdapter;

import java.util.List;

/**
 * Created by skasam on 9/28/2016.
 */

//To Display the Farmer Plots
public class FarmerPlotDetailsAdapter extends SelectableAdapter<FarmerPlotDetailsAdapter.PlotDetailsViewHolder> {


    private Context context;
    private List<PlotDetailsObj> plotlist;
    private PlotDetailsObj plotdetailsObj;
    private ClickListener clickListener;
    private int layoutResourceId;

    public FarmerPlotDetailsAdapter(Context context, List<PlotDetailsObj> plotlist, int layoutResourceId) {
        this.context = context;
        this.plotlist = plotlist;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public PlotDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutResourceId, null);
        PlotDetailsViewHolder myHolder = new PlotDetailsViewHolder(view);
        return myHolder;
    }


    //Binding the Plots Data
    @Override
    public void onBindViewHolder(PlotDetailsViewHolder holder, final int position) {
        plotdetailsObj = plotlist.get(position);

        holder.tvplotId.setText(plotdetailsObj.getPlotID());
        if (!TextUtils.isEmpty(plotdetailsObj.getPlotLandMark())) {
            holder.tvlandmark.setText(plotdetailsObj.getPlotLandMark());
            holder.tvlandmark.setVisibility(View.VISIBLE);
        } else {
            holder.tvlandmark.setVisibility(View.GONE);
        }

        holder.tvplotarea.setText("Plot size: "+plotdetailsObj.getPlotArea() + " Ha");
        holder.tvplotvillage.setText(plotdetailsObj.getVillageName());
        holder.tvplotsurveynumber.setText(plotdetailsObj.getSurveyNumber());
        holder.selectionChk.setChecked(isSelected(position));

        holder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return plotlist.size();
    }

    public static class PlotDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvplotId;
        private TextView tvlandmark;
        private TextView tvplotarea;
        private TextView tvplotvillage;
        private TextView tvplotsurveynumber;
        private ClickListener listener;
        private CheckBox selectionChk;
        private View convertView;

        public PlotDetailsViewHolder(View view) {
            super(view);
            this.convertView = view;
            tvplotId = (TextView) view.findViewById(R.id.tvplotidvalue);
            tvlandmark = (TextView) view.findViewById(R.id.tvplotlandmarkvalue);
            tvplotarea = (TextView) view.findViewById(R.id.tvplotareavalue);
            tvplotvillage = (TextView) view.findViewById(R.id.tvplotvillagevalue);
            tvplotsurveynumber = (TextView) view.findViewById(R.id.tvplotsurveyvalue);
            selectionChk = (CheckBox) view.findViewById(R.id.chkSelected);
        }
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClicked(int position);
    }
}
