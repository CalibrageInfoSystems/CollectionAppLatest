package com.oilpalm3f.mainapp.GraderFingerprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.ui.RecyclerItemClickListener;
import com.oilpalm3f.mainapp.ui.SplashScreen;
import com.oilpalm3f.mainapp.utils.UiUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class GraderDetailsRecyclerAdapter extends RecyclerView.Adapter<GraderDetailsRecyclerAdapter.ViewHolder>{

    private List<GraderBasicDetails> mList;
    private Context context;
    private GraderBasicDetails item;
    private RecyclerItemClickListener recyclerItemClickListener;

    private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public GraderDetailsRecyclerAdapter(Context context, List<GraderBasicDetails> mList,RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        this.mList = mList;
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public void addItems(List<GraderBasicDetails> list) {
        this.mList = new ArrayList<>(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.grader_item, viewGroup, false);
        ViewHolder myHolder = new ViewHolder(view);
        return myHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        item = mList.get(position);

        holder.gradercode.setText(item.getGraderCode());
//
        holder.gradervillage.setText(item.getVillageName() != null ? item.getVillageName().trim() : "");
        holder.gradername.setText(item.getGraderName() != null ? item.getGraderName().trim() : "");
        holder.mobile_number.setText(item.getMobileNumber() != null ? item.getMobileNumber().trim() : "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!CommonUtils.isNetworkAvailable(context)) {
                    UiUtils.showCustomToastMessage("Please check your network connection", context, 1);
                }else{
                    notifyItemChanged(position);
                    recyclerItemClickListener.onItemSelected(position);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

 
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView gradercode;
        private TextView gradername;
        private TextView mobile_number;
        private TextView gradervillage;

        private View convertView;

        public ViewHolder(View view) {
            super(view);
            convertView = view;

            gradercode = (TextView) view.findViewById(R.id.gradercode);
            gradername = (TextView) view.findViewById(R.id.gradername);
            mobile_number = (TextView) view.findViewById(R.id.mobile_number);
            // tvPlotcluster = (TextView) view.findViewById(R.id.tvContactNumber);
            gradervillage = (TextView) view.findViewById(R.id.gradervillage);

        }
    }
}

