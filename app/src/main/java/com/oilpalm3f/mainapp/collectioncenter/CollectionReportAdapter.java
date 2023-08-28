package com.oilpalm3f.mainapp.collectioncenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionReportModel;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skasam on 2/7/2017.
 */
public class   CollectionReportAdapter extends RecyclerView.Adapter<CollectionReportAdapter.CollectionReportViewHolder> {

    private static final String LOG_TAG = CollectionReportAdapter.class.getName();
    private List<CollectionReportModel> mList;
    private Context context;
    private CollectionReportModel item;
    private DataAccessHandler dataAccessHandler = null;
    private onPrintOptionSelected onPrintSelected;

    public CollectionReportAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        dataAccessHandler = new DataAccessHandler(context);
    }

    @Override
    public CollectionReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.collectionreport_item, null);
        CollectionReportViewHolder myHolder = new CollectionReportViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(CollectionReportViewHolder holder, final int position) {
        item = mList.get(position);
        String lastName = "", middleName = "";
        if (!TextUtils.isEmpty(item.getLastName())) {
            lastName = item.getLastName();
        }
        if (!TextUtils.isEmpty(item.getMiddleName()) && !
                item.getMiddleName().equalsIgnoreCase("null")) {
            middleName = item.getMiddleName();
        }
        if (item == null)
            return;

        holder.tvcollectionid.setText(item.getCode().trim());
        holder.tvdatetimestamp.setText(item.getWeighingDate().trim());
        holder.tvfarmercode.setText(item.getFarmerCode().trim());
        holder.tvfarmername.setText(item.getFirstName().trim() + " " + middleName.trim() + " " + lastName.trim());
        String plotCodes = TextUtils.join(", ",dataAccessHandler.getListOfCodes(Queries.getInstance().getPlotCodes(item.getCode())).toArray());
        holder.tvplotcodes.setText(""+plotCodes);
        holder.tvgrossweight.setText(item.getGrossWeight().trim());
        holder.tvtareweight.setText(item.getTareWeight().trim());
        holder.tvnetweight.setText(item.getNetWeight().trim());

        if (item.getVehicleNumber().equalsIgnoreCase("null")){
            holder.vehiclenumber_layout.setVisibility(View.GONE);
        }else{
            holder.vehiclenumber_layout.setVisibility(View.VISIBLE);
            holder.tvveniclenumber.setText(item.getVehicleNumber().trim());
        }
        holder.tvveniclenumber.setText(item.getVehicleNumber().trim());
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

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<CollectionReportModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class CollectionReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvcollectionid;
        private TextView tvdatetimestamp;
        private TextView tvfarmercode;
        private TextView tvfarmername;
        private TextView tvplotcodes;
        private TextView tvgrossweight;
        private TextView tvtareweight;
        private TextView tvnetweight;
        private TextView tvveniclenumber;
        private TextView tvccagentname;
        private ImageView printBtn;

        LinearLayout vehiclenumber_layout;

        public CollectionReportViewHolder(View view) {
            super(view);
            tvcollectionid = (TextView) view.findViewById(R.id.tvcollectionid);
            tvdatetimestamp = (TextView) view.findViewById(R.id.tv_dateandtimestamp);
            tvfarmercode = (TextView) view.findViewById(R.id.tvfarmercode);
            tvfarmername = (TextView) view.findViewById(R.id.tvfarmername);
            tvplotcodes = (TextView) view.findViewById(R.id.tvplotcodes);
            tvgrossweight = (TextView) view.findViewById(R.id.tvgrossweight);
            tvtareweight = (TextView) view.findViewById(R.id.tvtareweight);
            tvnetweight = (TextView) view.findViewById(R.id.tvnetweight);
            tvveniclenumber = (TextView) view.findViewById(R.id.tvvehiclenumber);
            tvccagentname = (TextView) view.findViewById(R.id.tvccagentname);
            printBtn = (ImageView) view.findViewById(R.id.printBtn);
            vehiclenumber_layout = view.findViewById(R.id.vehiclenumber_layout);
        }
    }

    public void setonPrintSelected(final onPrintOptionSelected onPrintSelected) {
        this.onPrintSelected = onPrintSelected;
    }
}
