package com.oilpalm3f.mainapp.GraderFingerprint;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.ui.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GraderList extends AppCompatActivity implements RecyclerItemClickListener {
    private static final String LOG_TAG = GraderList.class.getSimpleName();
    String searchKey = "";
    int offset;
    public static final int LIMIT = 30;
    private DataAccessHandler dataAccessHandler;
    private RecyclerView Graderslist;
    private EditText mEtSearch;
    private ImageView mIVClear;
    private TextView tvNorecords,no_text;
    private ProgressBar progress;
    private List<GraderBasicDetails> graderdataList = new ArrayList<>();
    private GraderDetailsRecyclerAdapter graderDetailsRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private boolean hasMoreItems = true;
    private boolean isSearch = false;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.d("WhatisinSearch", "is :"+ s);
            //
            offset = 0;
            ApplicationThread.uiPost(LOG_TAG, "search", new Runnable() {
                @Override
                public void run() {
                    doSearch(s.toString().trim());
                    if (s.toString().length() > 0) {
                        mIVClear.setVisibility(View.VISIBLE);
                    } else {
                        mIVClear.setVisibility(View.GONE);
                    }
                }
            }, 100);
        }

        @Override
        public void afterTextChanged(final Editable s) {

        }
    };

    public void doSearch(String searchQuery) {
        Log.d("DoSearchQuery", "is :" +  searchQuery);
        offset = 0;
        hasMoreItems = true;
        if (searchQuery !=null &  !TextUtils.isEmpty(searchQuery)  & searchQuery.length()  > 0) {

            offset = 0;
            isSearch = true;
            searchKey = searchQuery.trim();
            getAllgraders();
        } else {
            searchKey = "";
            getAllgraders();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_list);
        intviews();
        setviews();

    }

    private void intviews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Graders");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Graderslist =  findViewById(R.id.graderlist);
        no_text = findViewById(R.id.no_text);
        dataAccessHandler = new DataAccessHandler(this);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIVClear = (ImageView) findViewById(R.id.iv_clear);
        tvNorecords = (TextView) findViewById(R.id.no_records);

        mIVClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = false;
                graderdataList.clear();
                mEtSearch.setText("");
            }
        });
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Graderslist.setLayoutManager(layoutManager);
      //  Graderslist.setAdapter(graderDetailsRecyclerAdapter);

        mEtSearch.addTextChangedListener(mTextWatcher);
       // Graderslist.addOnScrollListener(recyclerViewOnScrollListener);
    }
    private void setviews() {
//        String farmerfCount = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().queryFarmersCount());
//        setTile(getString(R.string.farmer_list) + "("+farmerfCount+")");

        progress = (ProgressBar) findViewById(R.id.progress);


        getAllgraders();
        graderDetailsRecyclerAdapter = new GraderDetailsRecyclerAdapter(GraderList.this, graderdataList,GraderList.this);
        Graderslist.setAdapter(graderDetailsRecyclerAdapter);
       // graderDetailsRecyclerAdapter.setRecyclerItemClickListener(this);
    }

    private void getAllgraders() {

        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
//        ProgressBar.showProgressBar(this, "Please wait...");
        ApplicationThread.bgndPost(LOG_TAG, "getting transactions data", () ->
                dataAccessHandler.getGraderbasicdetails(searchKey, offset, LIMIT,
                        new ApplicationThread.OnComplete<List<GraderBasicDetails>>() {
                            @Override
                            public void execute(boolean success, final List<GraderBasicDetails>graderDetails, String msg) {
//                        ProgressBar.hideProgressBar();
                                isLoading = false;
                                if (graderDetails.isEmpty()) {
                                    hasMoreItems = false;
                                }

                                if (offset == 0 && isSearch) {
                                    graderdataList.clear();
                                    graderdataList.addAll(graderDetails);

                                } else {

                                    if(graderDetails != null  & graderDetails.size()  > 0)
                                        graderdataList.clear();

                                    graderdataList.addAll(graderDetails);


                                }
                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);
                                        int farmersSize = graderDetails.size();
                                        Log.v(LOG_TAG, "data size " + farmersSize);
                                        graderDetailsRecyclerAdapter.addItems(graderdataList);
                                        if (graderDetailsRecyclerAdapter.getItemCount() == 0) {
                                            tvNorecords.setVisibility(View.VISIBLE);
                                            actionBar.setTitle("Grader List");
                                        } else {
                                          //  graderDetailsRecyclerAdapter = new GraderDetailsRecyclerAdapter(GraderList.this, graderdataList);
                                            actionBar.setTitle(getString(R.string.Grader_list) + "("+graderdataList.size()+")");
                                            tvNorecords.setVisibility(View.GONE);
                                            Graderslist.getLayoutManager().scrollToPosition(0);

                                        }
                                    }
                                });
                            }

                        }));
    }

    @Override
    public void onItemSelected(int position) {
       String GraderCode =  graderdataList.get(position).getGraderCode();
       Log.e("=======>GraderCode",GraderCode);
        Intent intent = new Intent(GraderList.this, GraderFingerprint.class);
        intent.putExtra("GraderCode", GraderCode);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}