package com.oilpalm3f.mainapp.viewfarmers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.FarmersDetailsScreen;
import com.oilpalm3f.mainapp.collectioncenter.OperateAdditionalPlots;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.ui.RecyclerItemClickListener;
import com.oilpalm3f.mainapp.uihelper.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.oilpalm3f.mainapp.common.CommonConstants.selectedPlotCode;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.EXTRA_PLOTS;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.SELECTED_FARMER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.TOTAL_FARMERS_DATA;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

/**
 * Created by skasam on 9/27/2016.
 */
public class FarmersListScreenForCC extends AppCompatActivity implements RecyclerItemClickListener, OperateAdditionalPlots.addedPlotCodesListener , ZBarScannerView.ResultHandler {
    private static final String LOG_TAG = FarmersListScreenForCC.class.getName();
    private FarmerDetailsRecyclerAdapter farmerDetailsRecyclerAdapter;
    private RecyclerView farmlandLVMembers;
    private List<BasicFarmerDetails> mFarmersList = new ArrayList<>();
    private List<BasicFarmerDetails> mFinalFarmersList = new ArrayList<>();
    private ArrayList<BasicFarmerDetails> mList_sorted;
    private TextView mEtSearch;
    private TextView tvNorecords;
    private ImageView mIVClear;
    private DataAccessHandler dataAccessHandler;
    private Toolbar toolbar;
    private TextView collectionCenterName, collectionCenterCode, collectionCenterVillage, collectionIDTxt, selected_records;
    private RelativeLayout morePlotsLl;
    private CollectionCenter selectedCollectionCenter;
    private Button addDoneBtn;
    private ImageView qrByFarmer;
    private BasicFarmerDetails mainFarmer;
    private ZBarScannerView mScannerView;
    private Dialog dialog;
    private LinearLayout layoutScan;

    //On Text Changed Method

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            doSearch(s.toString());
            if (s.toString().length() > 0)
                mIVClear.setVisibility(View.VISIBLE);
            else
                mIVClear.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private boolean isAddAdditionalPlots;



    //Initializing the Class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (getIntent().getAction().equalsIgnoreCase("Add Farmers")) {
//            setTheme(R.style.AppTheme_Dialog);
//            this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.farmer_list));
        setSupportActionBar(toolbar);

        initUI();

        CommonUtils.currentActivity = this;

    }


    //Get Farmers List on Resume Activity
    @Override
    public void onResume() {
        super.onResume();

        dataAccessHandler = new DataAccessHandler(this);
        selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);

        mFarmersList = mFinalFarmersList = (List<BasicFarmerDetails>) DataManager.getInstance().getDataFromManager(DataManager.TOTAL_FARMERS_DATA);

        if (getIntent().getAction().equalsIgnoreCase("Add Farmers")) {
            isAddAdditionalPlots = true;
            mainFarmer = (BasicFarmerDetails) DataManager.getInstance().getDataFromManager(SELECTED_FARMER_DATA);
            mFarmersList.remove(mainFarmer);
            mFinalFarmersList.remove(mainFarmer);
            if (null != selectedPlotCode && !selectedPlotCode.isEmpty()) {
                selectedPlotCode = CommonUtils.ignoreDuplicatedInArrayList(selectedPlotCode);
                morePlotsLl.setVisibility(View.VISIBLE);
                selected_records.setText("No. of Plots Added: "+selectedPlotCode.size());
            } else {
                morePlotsLl.setVisibility(View.GONE);
            }
        } else {
            isAddAdditionalPlots = false;
        }

        if (null != mFarmersList && !mFarmersList.isEmpty()) {
            if (getIntent().getAction().equalsIgnoreCase("Add Farmers")) {
                farmerDetailsRecyclerAdapter = new FarmerDetailsRecyclerAdapter(FarmersListScreenForCC.this, mFarmersList);
                farmlandLVMembers.setLayoutManager(new LinearLayoutManager(FarmersListScreenForCC.this, LinearLayoutManager.VERTICAL, false));
                farmlandLVMembers.setAdapter(farmerDetailsRecyclerAdapter);
                farmerDetailsRecyclerAdapter.setRecyclerItemClickListener(FarmersListScreenForCC.this);
                tvNorecords.setVisibility(View.GONE);
                toolbar.setTitle(getResources().getString(R.string.farmer_list) + " (" + ((null != mFarmersList) ? mFarmersList.size() : 0) + ")");
                toolbar.invalidate();
            }else{
                getAllUsers();
            }
        } else {
            mFarmersList = new ArrayList<>();
            mFinalFarmersList = new ArrayList<>();
            getAllUsers();
        }

        collectionCenterName.setText(selectedCollectionCenter.getName());
        collectionCenterCode.setText(selectedCollectionCenter.getCode());
        collectionCenterVillage.setText(selectedCollectionCenter.getVillageName());
    }


    //Initializing the UI
    private void initUI() {
        farmlandLVMembers = (RecyclerView) findViewById(R.id.lv_farmerlanddetails);
        morePlotsLl = (RelativeLayout) findViewById(R.id.morePlotsLl);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIVClear = (ImageView) findViewById(R.id.iv_clear);
        tvNorecords = (TextView) findViewById(R.id.no_records);
        qrByFarmer = findViewById(R.id.qrByFarmer);
        selected_records = (TextView) findViewById(R.id.selected_records);
        addDoneBtn = (Button) findViewById(R.id.nextBtn);
        layoutScan = findViewById(R.id.layoutScan);
        layoutScan.setVisibility(View.VISIBLE);

        mEtSearch.addTextChangedListener(mTextWatcher);

        mIVClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.setText("");
            }
        });

        collectionCenterName = (TextView) findViewById(R.id.collection_center_name);
        collectionCenterCode = (TextView) findViewById(R.id.collection_center_code);
        collectionCenterVillage = (TextView) findViewById(R.id.collection_center_village);

        addDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
                selectedPlotCode = CommonUtils.ignoreDuplicatedInArrayList(selectedPlotCode);
                DataManager.getInstance().addData(EXTRA_PLOTS, selectedPlotCode);
                finish();
            }
        });


        qrByFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanByQrCode();
            }
        });
    }

    //Get All Farmers and setting them to Adapter
    public void getAllUsers() {
        dataAccessHandler = new DataAccessHandler(this);
        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", new Runnable() {
            @Override
            public void run() {
                dataAccessHandler.getFarmerDetailsForCC(new ApplicationThread.OnComplete<List<BasicFarmerDetails>>() {
                    @Override
                    public void execute(boolean success, final List<BasicFarmerDetails> farmerDetails, String msg) {
                        ProgressBar.hideProgressBar();
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getAllUsers", CommonConstants.TAB_ID,"result.toString()",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            if (farmerDetails != null && farmerDetails.size() > 0) {
                                mFarmersList.clear();
                                mFinalFarmersList.clear();
                                mFarmersList = farmerDetails;
                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        int farmersSize = farmerDetails.size();
                                        Log.v(LOG_TAG, "data size " + farmersSize);
                                        if (isAddAdditionalPlots) {
                                            mFarmersList.remove(mainFarmer);
                                        }
                                        mFinalFarmersList.addAll(mFarmersList);
                                        DataManager.getInstance().addData(TOTAL_FARMERS_DATA, mFinalFarmersList);
                                        farmerDetailsRecyclerAdapter = new FarmerDetailsRecyclerAdapter(FarmersListScreenForCC.this, mFarmersList);
                                        farmlandLVMembers.setLayoutManager(new LinearLayoutManager(FarmersListScreenForCC.this, LinearLayoutManager.VERTICAL, false));
                                        farmlandLVMembers.setAdapter(farmerDetailsRecyclerAdapter);
                                        farmerDetailsRecyclerAdapter.setRecyclerItemClickListener(FarmersListScreenForCC.this);
                                        tvNorecords.setVisibility(View.GONE);
                                        toolbar.setTitle(getResources().getString(R.string.farmer_list) + " (" + farmersSize + ")");
                                        toolbar.invalidate();
                                    }
                                });

                            } else {
                                tvNorecords.setVisibility(View.VISIBLE);
                                Log.v(LOG_TAG, "@@@ Error while getting data from data base");
                            }
                        } else {
                            tvNorecords.setVisibility(View.VISIBLE);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getAllUsers",CommonConstants.TAB_ID,"result.toString()",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            Log.v(LOG_TAG, "@@@ Error while getting data from data base");
                        }
                    }
                });
            }
        });

    }

    //Search Functionality What should happen when searched for Farmer
    public void doSearch(String searchQuery) {
        int etSearchLength = searchQuery.length();
        if (!TextUtils.isEmpty(searchQuery)) {
            mList_sorted = new ArrayList<>();
            mFinalFarmersList = new ArrayList<>();
            String farmerName = "";
            for (int i = 0; i < mFarmersList.size(); i++) {
                String lastName = "", fatherName = "";
                if (!TextUtils.isEmpty(mFarmersList.get(i).getFarmerLastName())) {
                    lastName = mFarmersList.get(i).getFarmerLastName();
                }
                if (!TextUtils.isEmpty(mFarmersList.get(i).getFarmerFatherName())) {
                    fatherName = mFarmersList.get(i).getFarmerFatherName();
                }

                farmerName = mFarmersList.get(i).getFarmerFirstName();

                if (!TextUtils.isEmpty(mFarmersList.get(i).getFarmerLastName()) && searchQuery.contains(" ")) {
                    farmerName = farmerName + mFarmersList.get(i).getFarmerLastName();
                }
                if (farmerName.toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase(Locale.getDefault()))
                        || mFarmersList.get(i).getPrimaryContactNum().toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase(Locale.getDefault()))
                        || lastName.toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase(Locale.getDefault()))
                        || mFarmersList.get(i).getFarmerCode().toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase(Locale.getDefault()))
                        || (!TextUtils.isEmpty(fatherName) && fatherName.toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase(Locale.getDefault())))) {
                    mList_sorted.add(mFarmersList.get(i));
                }
            }

            mFinalFarmersList.addAll(mList_sorted);

            if (farmerDetailsRecyclerAdapter == null) {
                farmerDetailsRecyclerAdapter = new FarmerDetailsRecyclerAdapter(this, mList_sorted);
            } else {
                farmerDetailsRecyclerAdapter.updateAdapter(mList_sorted);
            }
            if (mList_sorted != null && mList_sorted.size() == 0) {
                tvNorecords.setVisibility(View.VISIBLE);
            } else {
                tvNorecords.setVisibility(View.GONE);
                toolbar.setTitle(getResources().getString(R.string.farmer_list) + " (" + mList_sorted.size() + ")");
                toolbar.invalidate();
            }
        } else {

            if (mFarmersList != null) {
                mFinalFarmersList.addAll(mFarmersList);
            }

            if (farmerDetailsRecyclerAdapter == null) {
                if (isAddAdditionalPlots) {
                    mFarmersList.remove(mainFarmer);
                }
                farmerDetailsRecyclerAdapter = new FarmerDetailsRecyclerAdapter(this, mFarmersList);
            } else {
                if (isAddAdditionalPlots) {
                    mFarmersList.remove(mainFarmer);
                }
                farmerDetailsRecyclerAdapter.updateAdapter(mFarmersList);
            }

            if (mFarmersList != null && mFarmersList.size() == 0) {
                tvNorecords.setVisibility(View.VISIBLE);
            } else {
                tvNorecords.setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(searchQuery)) {
            getAllUsers();
        }
    }

    //On Farmer Selected
    @Override
    public void onItemSelected(int position) {
        if (getIntent().getAction().equalsIgnoreCase("Main Farmer")) {
            CommonConstants.isComingfrom = "Home";
            selectedPlotCode.clear();
            DataManager.getInstance().deleteData(EXTRA_PLOTS);
            DataManager.getInstance().addData(SELECTED_FARMER_DATA, mFinalFarmersList.get(position));
            startActivity(new Intent(this, FarmersDetailsScreen.class).setAction(getIntent().getAction()));
        } else {
            FragmentManager fm = getSupportFragmentManager();
            OperateAdditionalPlots additionalPlotsFragment = OperateAdditionalPlots.newInstance(mFinalFarmersList.get(position), selectedPlotCode);
            additionalPlotsFragment.setAddedPlotCodesListener(this);
            additionalPlotsFragment.show(fm, "additional plots fragment");
        }
    }

    //Add Plot Codes for Collections
    @Override
    public void addedPlotCodes(List<String> plotCodes) {
        if (null != plotCodes && !plotCodes.isEmpty()) {
            selectedPlotCode.addAll(plotCodes);
            selectedPlotCode = CommonUtils.ignoreDuplicatedInArrayList(selectedPlotCode);
            morePlotsLl.setVisibility(View.VISIBLE);
            if (null != selectedPlotCode && !selectedPlotCode.isEmpty()) {
                selected_records.setText("No. of Plots Added: "+selectedPlotCode.size());
            }
        }
    }

    //Handling Search Result
    @Override
    public void handleResult(Result result) {

        mEtSearch.setText(result.getContents());
        doSearch(result.getContents());

        dialog.dismiss();

    }

    //Search Farmer by Scanning QR Code
    private void scanByQrCode()
    {
        dialog = new Dialog(this);
        mScannerView = new ZBarScannerView(this);
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
       // mScannerView.setMinimumWidth((int) (displayRectangle.width() * 0.4f));
      //  mScannerView.setMinimumHeight((int) (displayRectangle.width() * 0.5f));
       // mScannerView.getFramingRectInPreview(400,400);

        mScannerView = new ZBarScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };

        View v = LayoutInflater.from(this).inflate(R.layout.scan_view,null);
        v.setMinimumHeight((int) (displayRectangle.width() * 0.5f));
        dialog.setContentView(v);
        LinearLayout layoutt = dialog.findViewById(R.id.layoutView);
        layoutt.addView(mScannerView);
        dialog.setTitle("Title...");

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        // set the custom dialog components - text, image and button
       //// TextView text = (TextView) dialog.findViewById(R.id.text);
      //  text.setText("Android custom dialog example!");
       // ImageView image = (ImageView) dialog.findViewById(R.id.image);
       /// image.setImageResource(R.drawable.ic_launcher);

      /*  Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        dialog.show();
    }

    private static class CustomViewFinderView extends ViewFinderView {

        private CustomViewFinderView(Context context)
        {
            super(context);
        }

        @Override
        public Rect getFramingRect() {
            Rect originalRect = super.getFramingRect();
            return new Rect(originalRect.left + 20, originalRect.top - 50, originalRect.right - 20, originalRect.bottom - 100);
        }
    }
}
