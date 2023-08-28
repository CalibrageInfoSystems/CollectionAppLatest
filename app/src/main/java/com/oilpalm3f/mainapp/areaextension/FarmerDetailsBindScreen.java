package com.oilpalm3f.mainapp.areaextension;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CCDataAccessHandler;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skasam on 9/28/2016.
 */

//Not Using
public class FarmerDetailsBindScreen extends OilPalmBaseActivity {

    private TextView tvfarmername, tvfathername, tvvillagename, tvcontactnum, tvaddress,
            tvholdername, tvbankname, tvaccountnum, tvbranchname;
    private ImageView imgvfarmer;
    private RecyclerView rvplotlist;
    private BasicFarmerDetails userdetailsObj;
    private List<PlotDetailsObj> plotdetailslistObj = new ArrayList<>();
    private CCDataAccessHandler ccDataAccessHandler = null;
    private View parentView = null;
    private FarmerPlotDetailsAdapter farmerPlotDetailsAdapter;

    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        parentView = inflater.inflate(R.layout.activity_farmer_bind_details2, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.farmer_details));

        ccDataAccessHandler = new CCDataAccessHandler(FarmerDetailsBindScreen.this);
        CommonUtils.currentActivity = this;

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        userdetailsObj = (BasicFarmerDetails) bundle.getSerializable("farmerdetails");
        initUI();
        bindData();
        bindPlotData();


        CommonConstants.prevMandalPos = 0;
        CommonConstants.prevVillagePos = 0;
        CommonConstants.preDistrictPos = 0;

    }


    private void initUI() {

        tvfarmername = (TextView) parentView.findViewById(R.id.tvfarmername);
        tvfathername = (TextView) parentView.findViewById(R.id.tvfathername);
        tvvillagename = (TextView) parentView.findViewById(R.id.tvvillagename);
        tvcontactnum = (TextView) parentView.findViewById(R.id.tvcontactnumber);
        tvaddress = (TextView) parentView.findViewById(R.id.tvaddress);
        tvbankname = (TextView) parentView.findViewById(R.id.tvbankname);
        tvholdername = (TextView) parentView.findViewById(R.id.tvholdername);
        tvaccountnum = (TextView) parentView.findViewById(R.id.tvaccno);
        tvbranchname = (TextView) parentView.findViewById(R.id.tvbranch);
        imgvfarmer = (ImageView) parentView.findViewById(R.id.profile_pic);
        rvplotlist = (RecyclerView) parentView.findViewById(R.id.lv_farmerplotdetails);

    }

    private void bindData() {
        try {
            if (userdetailsObj.getFarmerPhoto() != null) {

                byte[] arr = userdetailsObj.getFarmerPhoto();
                Log.v("Image::::::::::::", userdetailsObj.toString());

                Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                imgvfarmer.setImageBitmap(bmp);
                imgvfarmer.invalidate();
            }

            tvfarmername.setText(userdetailsObj.getFarmerFirstName() + " " + userdetailsObj.getFarmerMiddleName() + " " + userdetailsObj.getFarmerLastName());
            tvfathername.setText(userdetailsObj.getFarmerFatherName());
            tvvillagename.setText(userdetailsObj.getFarmerVillageName());
            tvcontactnum.setText(userdetailsObj.getPrimaryContactNum());
            tvaddress.setText(userdetailsObj.getAddress1() + " , " + userdetailsObj.getAddress2() + " , " + userdetailsObj.getLandmark());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindPlotData() {
        plotdetailslistObj = ccDataAccessHandler.getPlotDetails(userdetailsObj.getFarmerCode());
        if (plotdetailslistObj != null && plotdetailslistObj.size() > 0) {
//            farmerplotDetailsLVAdapter = new FarmerPlotDetailsAdapter(this, plotdetailslistObj);
            rvplotlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvplotlist.setAdapter(farmerPlotDetailsAdapter);
        }
    }

}
