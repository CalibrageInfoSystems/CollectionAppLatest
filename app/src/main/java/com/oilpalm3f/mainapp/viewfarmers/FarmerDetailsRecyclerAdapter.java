package com.oilpalm3f.mainapp.viewfarmers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.ui.RecyclerItemClickListener;
import com.oilpalm3f.mainapp.uihelper.CircleImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FarmerDetailsRecyclerAdapter extends RecyclerView.Adapter<FarmerDetailsRecyclerAdapter.FarmerDetailsViewHolder> {

    private List<BasicFarmerDetails> mList;
    private Context context;
    private BasicFarmerDetails item;
    private RecyclerItemClickListener recyclerItemClickListener;
    private static final String LOG_TAG = FarmerDetailsRecyclerAdapter.class.getName();

    public FarmerDetailsRecyclerAdapter(Context context, List<BasicFarmerDetails> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public FarmerDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.member_item, null);
        FarmerDetailsViewHolder myHolder = new FarmerDetailsViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final FarmerDetailsViewHolder holder, final int position) {
        item = mList.get(position);
        String lastName = "", middleName = "";
        if (!TextUtils.isEmpty(item.getFarmerLastName())) {
            lastName = item.getFarmerLastName();
        }
        if (!TextUtils.isEmpty(item.getFarmerMiddleName()) && !
                item.getFarmerMiddleName().equalsIgnoreCase("null")) {
            middleName = item.getFarmerMiddleName();
        }
        holder.tvuserName.setText(item.getFarmerFirstName().trim() + " " + middleName+" "+ lastName.trim() + " " + "(" + item.getFarmerCode().trim() + ")");
        holder.fatherName.setText(item.getFarmerFatherName().trim());
        holder.mobileNumber.setText(item.getPrimaryContactNum().trim());
        holder.villageName.setText(item.getFarmerVillageName().trim());
        holder.tvuserStateName.setText(item.getFarmerStateName().trim());

        holder.userImage.setBorderColor(context.getResources().getColor(R.color.colorPrimary));

//        if (null != item.getPhotoLocation() && !item.getPhotoLocation().equalsIgnoreCase("null")) {
//            Picasso.with(context).load(new File(item.getPhotoLocation())).into(holder.userImage);
//        } else {
//            holder.userImage.setImageResource(R.mipmap.app_logo);
//            holder.userImage.invalidate();
//        }

        final String imageUrl = CommonUtils.getImageUrl(item);
//        final String imageUrl = "https://api.learn2crack.com/android/images/donut.png";
        Picasso.with(context)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUrl)
                                .error(R.mipmap.ic_launcher)
                                .into(holder.userImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso","Could not fetch image");
                                    }
                                });
                    }
                });

        holder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "@@@ on item clicked");
                recyclerItemClickListener.onItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<BasicFarmerDetails> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class FarmerDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvuserName;
        private TextView mobileNumber;
        private TextView fatherName;
        private TextView villageName;
        private CircleImageView userImage;
        private TextView tvuserStateName;
        private View convertView;

        public FarmerDetailsViewHolder(View view) {
            super(view);
            convertView = view;
            tvuserName = (TextView) view.findViewById(R.id.notif_title);
            userImage = (CircleImageView) view.findViewById(R.id.circularImageView);
            mobileNumber = (TextView) view.findViewById(R.id.mobile_number);
            fatherName = (TextView) view.findViewById(R.id.fatherName);
            villageName = (TextView) view.findViewById(R.id.villageName);
            tvuserStateName = (TextView) view.findViewById(R.id.stateName);
        }
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }
}
