package com.oilpalm3f.mainapp.collectioncenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignment;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.SendConsignmentObj;
import com.oilpalm3f.mainapp.uihelper.InteractiveScrollView;

/**
 * Created by skasam on 2/3/2017.
 */

//Not Using
public class ViewSendConsignmentDetails extends Fragment {

    private View rootView;
    private ActionBar actionBar;
    private ImageView scrollBottomIndicator;
    private InteractiveScrollView interactiveScrollView;
    private static final String LOG_TAG = ViewSendConsignmentDetails.class.getName();
    private TextView tvconsignName,tvconsignId,tvvehNum,tvdriverName,tvdatetimestamp,tvconsignweight,tvOperatorName,tvMillName;
    private Button btnBack,btnSavePrint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_viewsendconsignment, container, false);
        ;
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(activity.getResources().getString(R.string.sendconsign_details));

        Bundle dataBundle = getArguments();
        Consignment enteredData = dataBundle.getParcelable("sendconsignment_data");


        tvconsignName = (TextView)rootView.findViewById(R.id.collection_center_name);
        tvconsignId = (TextView)rootView.findViewById(R.id.collection_id);
        tvvehNum = (TextView)rootView.findViewById(R.id.veh_num);
        tvdriverName = (TextView)rootView.findViewById(R.id.driverName);
        tvdatetimestamp = (TextView)rootView.findViewById(R.id.date_and_time_stamp);
        tvconsignweight = (TextView)rootView.findViewById(R.id.consign_weight);
        tvOperatorName = (TextView)rootView.findViewById(R.id.opetr_name);
        tvMillName = (TextView)rootView.findViewById(R.id.millname);

        bindData(enteredData);

        /*scrollBottomIndicator = (ImageView) rootView.findViewById(R.id.bottomScroll);
        interactiveScrollView = (InteractiveScrollView) rootView.findViewById(R.id.scrollView);
        scrollBottomIndicator.setVisibility(View.VISIBLE);
        scrollBottomIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactiveScrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        interactiveScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        interactiveScrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {
                scrollBottomIndicator.setVisibility(View.GONE);
            }
        });

        interactiveScrollView.setOnTopReachedListener(new InteractiveScrollView.OnTopReachedListener() {
            @Override
            public void onTopReached() {
            }
        });

        interactiveScrollView.setOnScrollingListener(new InteractiveScrollView.OnScrollingListener() {
            @Override
            public void onScrolling() {
                android.util.Log.d(LOG_TAG, "onScrolling: ");
                scrollBottomIndicator.setVisibility(View.VISIBLE);
            }
        });*/

        return rootView;
    }

    private void bindData(Consignment enteredData)
    {
        tvconsignName.setText("");
        tvconsignId.setText(enteredData.getCode());
        tvvehNum.setText(enteredData.getVehiclenumber());
        tvdriverName.setText(enteredData.getDrivername());
        tvdatetimestamp.setText("");
        tvconsignweight.setText(""+enteredData.getTotalweight());
        tvOperatorName.setText("");
        tvMillName.setText(enteredData.getMillcode());
    }


    }
