package com.oilpalm3f.mainapp.collectioncenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentReportModel;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skasam on 2/7/2017.
 */
public class ConsignmentReportAdapter extends RecyclerView.Adapter<ConsignmentReportAdapter.ConsignmentReportViewHolder> {

    private static final String LOG_TAG = CollectionReportAdapter.class.getName();

    private List<ConsignmentReportModel> mList;
    private Context context;
    private ConsignmentReportModel item;
    private DataAccessHandler dbHandler;
    private onPrintOptionSelected onPrintSelected;

    public ConsignmentReportAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        dbHandler = new DataAccessHandler(context);
    }

    @Override
    public ConsignmentReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.consignmentreport_item, null);
        return new ConsignmentReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConsignmentReportViewHolder holder, final int position) {
        item = mList.get(position);
        if (item != null) {
            holder.tvconsignmentid.setText(item.getCode().trim());
            holder.tvdatetimestamp.setText(item.getRecieptGeneratedDate().trim());
            holder.tvmillname.setText(item.getMillCode().trim());
            holder.tvconsignmentweight.setText(item.getTotalWeight().trim());
            holder.tvvehiclenumber.setText(item.getVehicleNumber().trim());
            holder.tvcreatedby.setText(item.getOperatorName().trim());
            holder.tvccagentname.setText(item.getInchargeName().trim());

            holder.printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onPrintSelected) {
                        onPrintSelected.printOptionSelected(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<ConsignmentReportModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class ConsignmentReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvconsignmentid;
        private TextView tvdatetimestamp;
        private TextView tvmillname;
        private TextView tvconsignmentweight;
        private TextView tvvehiclenumber;
        private TextView tvcreatedby;
        private TextView tvccagentname;
        private ImageView printBtn;
        public ConsignmentReportViewHolder(View view) {
            super(view);
            tvconsignmentid = (TextView) view.findViewById(R.id.tvconsignID);
            tvdatetimestamp = (TextView) view.findViewById(R.id.tv_dateandtimestamp);
            tvmillname = (TextView) view.findViewById(R.id.tvmillname);
            tvconsignmentweight = (TextView) view.findViewById(R.id.tvconsignmentweight);
            tvvehiclenumber = (TextView) view.findViewById(R.id.tvvehiclenumber);
            tvcreatedby = (TextView) view.findViewById(R.id.tvcreatedby);
            tvccagentname = (TextView) view.findViewById(R.id.tvccagentname);
            printBtn = (ImageView) view.findViewById(R.id.printBtn);
        }
    }

    public void setonPrintSelected(final onPrintOptionSelected onPrintSelected) {
        this.onPrintSelected = onPrintSelected;
    }
}
