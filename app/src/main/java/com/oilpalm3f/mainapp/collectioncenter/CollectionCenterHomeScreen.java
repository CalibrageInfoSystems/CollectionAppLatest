package com.oilpalm3f.mainapp.collectioncenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;

import java.text.SimpleDateFormat;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.mainapp.BroadCastReciver.myBackgroundProcess;
import com.oilpalm3f.mainapp.GraderFingerprint.GraderFingerprint;
import com.oilpalm3f.mainapp.GraderFingerprint.GraderList;
import com.oilpalm3f.mainapp.R;
import com.oilpalm3f.mainapp.StockTransfer.ReciveStockTransfer;
import com.oilpalm3f.mainapp.StockTransfer.SendStockTransfer;
import com.oilpalm3f.mainapp.StockTransfer.StockTransferReport;
import com.oilpalm3f.mainapp.StockTransfer.StockTransferReportModel;
import com.oilpalm3f.mainapp.activitylogdetails.LocationProvider;
import com.oilpalm3f.mainapp.activitylogdetails.LogBookScreenActivity;
import com.oilpalm3f.mainapp.areacalculator.LocationService;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.RefreshSyncActivity;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.CollectionClass;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.ui.OilPalmBaseActivity;
import com.oilpalm3f.mainapp.utils.SharedPrefManager;
import com.oilpalm3f.mainapp.utils.UiUtils;
import com.oilpalm3f.mainapp.viewfarmers.FarmersListScreenForCC;
import com.oilpalm3f.mainapp.weighbridge.New_WeighbridgeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.oilpalm3f.mainapp.common.CommonConstants.Current_Latitude;
import static com.oilpalm3f.mainapp.common.CommonConstants.Current_Longitude;
import static com.oilpalm3f.mainapp.common.CommonConstants.changecollectionType;
import static com.oilpalm3f.mainapp.common.CommonConstants.collectionType;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.COLLECTION_CENTER_DATA;
import static com.oilpalm3f.mainapp.datasync.helpers.DataManager.EXTRA_PLOTS;
import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

//Home Screen
public class CollectionCenterHomeScreen extends OilPalmBaseActivity {
    RelativeLayout newCollectionRel11;
    public static final String NEW_COLLECTION = "new_collection";
    public static final String CHANGE_COLLECTION_CENTER = "chenge_collection_center";
    public static final String CONSIGNMENT = "consignment";
    public static final String STOCK_TRANSFER = "stock_transfer";
    public static final String Verify_Fingerprint = "Verify_FingerPrint";
    private static final String LOG_TAG = CollectionCenterHomeScreen.class.getName();
    public boolean receivedAddr = false;
    private ImageView newCollectionRel, sendConsignmentRel, syncRel, reportsRel, receiveStockRel, sendStockTransferRL, log_icon,fingerprint_icon,verifyfingerprint_icon;
    private boolean doubleback = false;
    private CCDataAccessHandler ccDataAccessHandler = null;
    private List<CollectionCenter> collectionCenterList = null;
    private String gpsVillageName = "";
    private String deleteDate, currentdate;
    LocationManager lm;
    RelativeLayout log_updateFingerprint;
    LocationListener ll;
    private String days;
    public static String PREVIOUS_DELETE_DATE = "";
    private DataAccessHandler dataAccessHandler;



    private BroadcastReceiver mLbsMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(LocationService.ACTION_LOCATION_UPDATED)) {
                    final double latitude = intent.getDoubleExtra(LocationService.KEY_LATITUDE, -1);
                    final double longitude = intent.getDoubleExtra(LocationService.KEY_LONGITUDE, -1);
                    Log.d(LOG_TAG, "### in locationReceiver latitude: " + latitude + " longitude: " + longitude + " receivedAddr " + receivedAddr);
                    if (receivedAddr)
                        return;
                    CommonUtils.getAddressByLocation(CollectionCenterHomeScreen.this,
                            latitude, longitude, true, new ApplicationThread.OnComplete<String>() {
                                @Override
                                public void execute(boolean success, final String address, final String geoCountry) {
                                    Log.d(LOG_TAG, "### in getAddressByLocation from :" + LOG_TAG);
                                    if (success) {
                                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"mLbsMessageReceiver", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                        Log.v(LOG_TAG, "### address received " + address);
                                        if (!TextUtils.isEmpty(address)) {
                                            receivedAddr = true;
                                            gpsVillageName = address;
                                            Log.v("@@@", "address      " + gpsVillageName);
                                            collectionCenterList = ccDataAccessHandler.getCollectionCenters(Queries.getInstance().getCollectionCenterWithVillage(address));
                                            Log.v("@@@", "collectioncenterlistsize" + collectionCenterList.size());
                                            ApplicationThread.nuiPost(LOG_TAG, "", new Runnable() {
                                                @Override
                                                public void run() {
                                                    CommonUtils.stopLocationService(CollectionCenterHomeScreen.this, mLbsMessageReceiver);
                                                }
                                            }, 2000);
                                        }

                                        Log.v(LOG_TAG, "@@@ List of collection centers " + collectionCenterList);
                                    } else {
                                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"mLbsMessageReceiver", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                        ApplicationThread.nuiPost(LOG_TAG, "", new Runnable() {
                                            @Override
                                            public void run() {
                                                CommonUtils.stopLocationService(CollectionCenterHomeScreen.this, mLbsMessageReceiver);
                                            }
                                        }, 2000);
                                        Log.e(LOG_TAG, "@@@ failed to get address");
                                    }
                                }
                            });
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "EventBus.getDefault().unregister(this)");
    }

    //Intializing the Class
    @Override
    public void Initialize() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.content_collection_center_home_screen, null);
        baseLayout.addView(parentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setTile(getString(R.string.collection_center));
        ccDataAccessHandler = new CCDataAccessHandler(this);
        this.newCollectionRel = findViewById(R.id.newCollection_logo);
        newCollectionRel11=findViewById(R.id.newCollectionRel11);
        this.sendConsignmentRel = findViewById(R.id.sendConsignment_logo);
        log_icon = findViewById(R.id.log_icon);
        syncRel = findViewById(R.id.syncLogo);
        reportsRel = findViewById(R.id.reports_logo);
        receiveStockRel = findViewById(R.id.receiveStock_icon);
        sendStockTransferRL = findViewById(R.id.stockTransfer_icon);
        dataAccessHandler = new DataAccessHandler(this);
        log_updateFingerprint = findViewById(R.id.log_updateFingerprint);

        fingerprint_icon = findViewById(R.id.fingerprint_icon);
        verifyfingerprint_icon = findViewById(R.id.verifyfingerprint_icon);

        currentdate = CommonUtils.getCollectionCurrentDateTime(CommonConstants.DATE_FORMAT_2);
        deleteDate = PrefUtil.getString(this, PREVIOUS_DELETE_DATE);

        String result= dataAccessHandler.getCountValue(Queries.getInstance().getfingerprintActivityright(CommonConstants.USER_RoleID));
        Log.e("==============>result",result+"");
        if (result.equalsIgnoreCase("1")) {
            log_updateFingerprint.setVisibility(View.VISIBLE); // Set visibility to visible
        } else {
            log_updateFingerprint.setVisibility(View.GONE);
        }// Set visibility to gone

//Getting Days(Difference between financial year day to current day)
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        try {
            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(deleteDate);
            long diff = date1.getTime() - date2.getTime();
            days = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            Log.v(LOG_TAG, "" + PREVIOUS_DELETE_DATE);
            //  Toast.makeText(getApplicationContext(),"you can proceed"+deleteDate,Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

//

        if (days == null) {
            PrefUtil.putString(this, PREVIOUS_DELETE_DATE, CommonUtils.getCollectionCurrentDateTime(CommonConstants.DATE_FORMAT_2));
            Toast.makeText(getApplicationContext(), "you can", Toast.LENGTH_LONG).show();
        } else {

            if (Integer.parseInt(days) >= 31) {

                String root = Environment.getExternalStorageDirectory().toString();

                File pictureDirectory = new File(root + "/3F_Pictures/" + "CollectionPhotos");
                if (pictureDirectory.isDirectory()) {
                    String[] children = pictureDirectory.list();
                    for (String aChildren : children) {

                        String name = aChildren.replace(".jpg", "");
                        String data = ccDataAccessHandler.getOnlyOneValueFromDb("select ServerUpdatedStatus from PictureReporting where Code = '" + name + "'");
                        try {
                            if (data.equals("true")) {
                                Log.v(LOG_TAG, "@@@ name_img" + name + data);
                                new File(pictureDirectory, aChildren).delete();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                    PrefUtil.putString(this, PREVIOUS_DELETE_DATE, CommonUtils.getCollectionCurrentDateTime(CommonConstants.DATE_FORMAT_2));

                }
            } else {


            }
        }

        Intent intentt = new Intent(CollectionCenterHomeScreen.this, myBackgroundProcess.class);
        intentt.setAction("BackgroundProcess");
        Log.v(LOG_TAG, "BackgroundProcess");
        //Set the repeated Task
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CollectionCenterHomeScreen.this, 0, intentt, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //86400000 -- 24 hr
        //600000 -- 1 min
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 86400000, pendingIntent);


//Reports Button On Click Listener
        reportsRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {
                    FragmentManager fm = getSupportFragmentManager();
                    CollectionCenterHomeScreen.RegistrationTypeChooser registrationTypeChooser = new CollectionCenterHomeScreen.RegistrationTypeChooser();
                    registrationTypeChooser.show(fm, "fragment_edit_name");

                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }


            }
        });
        newCollectionRel11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CollectionCenterHomeScreen.this, New_WeighbridgeActivity.class));
            }
        });

        //Collection On Click Listener
        newCollectionRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {

                    FragmentManager fm = getSupportFragmentManager();
                    CollectionCenterHomeScreen.ChooseCollectionCenter registrationTypeChooser = ChooseCollectionCenter.newInstance(collectionCenterList, gpsVillageName, 1);
                    registrationTypeChooser.show(fm, "fragment_edit_name");
                    collectionType = NEW_COLLECTION;

                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }

            }
        });

        //Consignment On Click Listener
        sendConsignmentRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {

                    FragmentManager fm = getSupportFragmentManager();
                    CollectionCenterHomeScreen.ChooseCollectionCenter registrationTypeChooser = ChooseCollectionCenter.newInstance(collectionCenterList, gpsVillageName, 2);
                    registrationTypeChooser.show(fm, "fragment_edit_name");
                    collectionType = CONSIGNMENT;

                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }

            }
        });

        //Stock Transfer On Click Listener
        sendStockTransferRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {

                    FragmentManager fm = getSupportFragmentManager();
                    CollectionCenterHomeScreen.ChooseCollectionCenter registrationTypeChooser = ChooseCollectionCenter.newInstance(collectionCenterList, gpsVillageName, 2);
                    registrationTypeChooser.show(fm, "fragment_edit_name");
                    collectionType = STOCK_TRANSFER;

                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }
            }
        });
//Logbook On Click Listener
        log_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(new Intent(CollectionCenterHomeScreen.this, LogBookScreenActivity.class));

                } else {
                    UiUtils.showCustomToastMessage("Please Trun On GPS......", getApplicationContext(), 1);

                }


//                if (CommonUtils.isLocationPermissionGranted(getApplicationContext())) {
//                    startActivity(new Intent(CollectionCenterHomeScreen.this, LogBookScreenActivity.class));
//                }else {
//                    if (ContextCompat.checkSelfPermission(CollectionCenterHomeScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                            || ContextCompat.checkSelfPermission(CollectionCenterHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        android.util.Log.v("@@@LLL", "Hello1");
//                        ActivityCompat.requestPermissions(CollectionCenterHomeScreen.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//                                        Manifest.permission.ACCESS_FINE_LOCATION},
//                                100);
//
//                        if (CommonUtils.isLocationPermissionGranted(getApplicationContext())) {
//                            startActivity(new Intent(CollectionCenterHomeScreen.this, LogBookScreenActivity.class));
//                        }else {
//                            UiUtils.showCustomToastMessage("Please Trun On GPS", getApplicationContext(), 1);
//
//                        }
//
//                    }
//                }

            }
        });



        //Receive Stock On Click Listener
        receiveStockRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ReciveStockTransfer.class));
            }
        });

        fingerprint_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GraderList.class));
            }
        });


        //Sync On Click Listener
        syncRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {

                    startActivity(new Intent(CollectionCenterHomeScreen.this, RefreshSyncActivity.class));
                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }

            }
        });
        verifyfingerprint_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.get_isDataSyncRunning()) {

                    FragmentManager fm = getSupportFragmentManager();
                    CollectionCenterHomeScreen.ChooseCollectionCenter registrationTypeChooser = ChooseCollectionCenter.newInstance(collectionCenterList, gpsVillageName, 1);
                    registrationTypeChooser.show(fm, "fragment_edit_name");
                    collectionType = Verify_Fingerprint;

                } else {
                    UiUtils.backGroundSyncDilogue(CollectionCenterHomeScreen.this);
                }
            }
        });


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CommonUtils.startLocationService(this, mLbsMessageReceiver);

        }


       /* CollectionClass cc = ccDataAccessHandler.getCollectionDetailsRefresh();

        List<Collection> collectionDetailsRefresh = cc.getCollectionList();
        Log.v(LOG_TAG, "@@@ sample "+collectionDetailsRefresh.size());*/
        CommonUtils.currentActivity = this;

    }



    //onBack Pressed Method
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            fm.popBackStack();
        } else {
            if (doubleback) {
                finishAffinity();
            } else {
                doubleback = true;
                UiUtils.showCustomToastMessage("Press the back key again to close the app", this, 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleback = false;
                    }
                }, 2000);
            }
        }
    }

    //Choose Collection Center Pop-up
    public static class ChooseCollectionCenter extends DialogFragment {

        List<CollectionCenter> collectionCenterList = null;
        List<CollectionCenter> collectionCenterId = null;
        private Spinner collectionCenterSpinner;
        private CCDataAccessHandler ccDataAccessHandler = null;
        DataAccessHandler dataAccessHandler;
        private Button okBtn;
        private TextView gpsVillageNameTxt;
        private Button changeBtn;
        private FragmentManager mFragmentManager;
        private FragmentTransaction mFragmentTransaction;
        private CollectionCenter selectedCollectionCenter;
        private static LocationProvider mLocationProvider;
        private static String latLong = "";
        private double Latitude, Longitude;
        LocationManager lm;

        public ChooseCollectionCenter() {

        }

        public static ChooseCollectionCenter newInstance(List<CollectionCenter> collectionCenterList, String villageName, int type) {
            CollectionCenterHomeScreen.ChooseCollectionCenter fragment = new CollectionCenterHomeScreen.ChooseCollectionCenter();
            Bundle args = new Bundle();
            args.putParcelableArrayList("collection_data", (ArrayList<? extends Parcelable>) collectionCenterList);
            args.putInt("screen_type", type);
            args.putString("village_name", villageName);
            fragment.setArguments(args);
            return fragment;
        }

        public static LocationProvider getLocationProvider(Context context, boolean showDialog) {
            if (mLocationProvider == null) {
                mLocationProvider = new LocationProvider(context, mLatLong -> latLong = mLatLong);

            }
            if (mLocationProvider.getLocation(showDialog)) {
                return mLocationProvider;
            } else {
                return null;
            }

        }

        public String getLatLong(Context context, boolean showDialog) {

            mLocationProvider = getLocationProvider(context, showDialog);

            if (mLocationProvider != null) {
                latLong = mLocationProvider.getLatitudeLongitude();

            }

            return latLong;
        }

        private void getLocationDetails() {
            String latlong[] = getLatLong(getContext(), false).split("@");
            Log.d("latlong",latlong.length + "");
            //if (latlong == null) {
            if (isLocationEnabled(getContext()) == false){
                Toast.makeText(getContext(), "Please turn on Location", Toast.LENGTH_SHORT).show();
            } else {
                Latitude = Double.parseDouble(latlong[0]);
                Longitude = Double.parseDouble(latlong[1]);
                Log.d("CurrentLatitude", Latitude + "");
                Log.d("CurrentLongitude", Longitude + "");
            }


        }

        private boolean isLocationEnabled(Context context){
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            final boolean enabled = (mode != android.provider.Settings.Secure.LOCATION_MODE_OFF);
            return enabled;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.collection_center_chooser, container);
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

            if (android.os.Build.VERSION.SDK_INT >= 29) {
                lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocationDetails();

                }
            } else {
                if (CommonUtils.isLocationPermissionGranted(getContext())) {
                    getLocationDetails();
                } else {
                    UiUtils.showCustomToastMessage("Please Trun On GPS", getContext(), 1);
                }
            }

            ccDataAccessHandler = new CCDataAccessHandler(getActivity());
            collectionCenterSpinner = (Spinner) view.findViewById(R.id.collectionSpinner);
            okBtn = (Button) view.findViewById(R.id.okBtn);
            changeBtn = (Button) view.findViewById(R.id.chooseTxt);
            gpsVillageNameTxt = (TextView) view.findViewById(R.id.gpsVillageName);
            String gpsVillageName = getArguments().getString("village_name");
            gpsVillageNameTxt.setText(gpsVillageName);
            dataAccessHandler = new DataAccessHandler(getContext());

            selectedCollectionCenter = (CollectionCenter) DataManager.getInstance().getDataFromManager(COLLECTION_CENTER_DATA);

//Change Button On Click Listener
            changeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changecollectionType = CHANGE_COLLECTION_CENTER;
                    Log.d("CurrentLatitude1", Latitude + "");
                    Log.d("CurrentLongitude1", Longitude + "");
                    collectionCenterList = ccDataAccessHandler.getCollectionCenters(Queries.getInstance().getCollectionCenterMaster(CommonConstants.USER_ID,String.valueOf(Latitude),String.valueOf(Longitude)));
                    //collectionCenterList = ccDataAccessHandler.getCollectionCenters(Queries.getInstance().getCollectionCenterMaster(CommonConstants.USER_ID));
                    //collectionCenterList = ccDataAccessHandler.getCollectionCenters(Queries.getInstance().getCollectionCenterMaster(CommonConstants.USER_ID,"18.3691","0"));
                    collectionCenterId = ccDataAccessHandler.getGeneratedCollectionCenterId(Queries.getInstance().getCOLCNId(CommonConstants.USER_ID,String.valueOf(Latitude),String.valueOf(Longitude)));
                    //collectionCenterId = ccDataAccessHandler.getGeneratedCollectionCenterId(Queries.getInstance().getCOLCNId(CommonConstants.USER_ID));
                    changebindCollectionCenterData();
                }
            });

            collectionCenterList = getArguments().getParcelableArrayList("collection_data");
            bindCollectionCenterData();
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (collectionCenterSpinner.getSelectedItemPosition() != 0) {
                        dismiss();
                        CollectionCenter collectionCenter11;
                        collectionCenter11 = dataAccessHandler.getCOllectionType(Queries.getInstance().getCollectionType(collectionCenterSpinner.getSelectedItem().toString()));

                        Log.v("@@@","ccName"+collectionCenterSpinner.getSelectedItem().toString());
                        if (collectionCenter11 != null) {
                            CommonConstants.CollectionType = collectionCenter11.getCollectionType();
                            CommonConstants.ReadMethod=collectionCenter11.getReadMethod();
                            CommonConstants.NoOfChars=collectionCenter11.getNoOfChars();
                            CommonConstants.UpToCharacter=collectionCenter11.getUpToCharacter();
                            CommonConstants.UpToCharacter=collectionCenter11.getUpToCharacter();
                            CommonConstants.IsFingerPrintReq = collectionCenter11.getFingerPrintReq();
                            Log.v("@@@","Type:"+CommonConstants.CollectionType);
                            Log.d("IsFingerPrintReq", collectionCenter11.getFingerPrintReq() + "");
                            Log.d("CommonConstants.IsFingerPrintReq", CommonConstants.IsFingerPrintReq + "");
                        }

                        DataManager.getInstance().addData(DataManager.COLLECTION_CENTER_ID, collectionCenterId.get(collectionCenterSpinner.getSelectedItemPosition() - 1));
                        DataManager.getInstance().addData(COLLECTION_CENTER_DATA, collectionCenterList.get(collectionCenterSpinner.getSelectedItemPosition() - 1));

                        Log.d("CCCData", COLLECTION_CENTER_DATA);
                        Log.d("CCCID", DataManager.COLLECTION_CENTER_ID);

//Navigations
                        if (Objects.equals(collectionType, NEW_COLLECTION)) {
                            CommonConstants.selectedPlotCode.clear();
                            DataManager.getInstance().deleteData(EXTRA_PLOTS);
                            startActivity(new Intent(getActivity(), FarmersListScreenForCC.class).setAction("Main Farmer"));
                        } else if (collectionType.equals(STOCK_TRANSFER)) {
                            mFragmentManager = getActivity().getSupportFragmentManager();
                            mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
                            );
                            mFragmentTransaction.replace(android.R.id.content, new SendStockTransfer());
                            mFragmentTransaction.addToBackStack("stock_transfer");
                            mFragmentTransaction.commit();
                        }
                        else if(collectionType.equals(Verify_Fingerprint)){

                            startActivity(new Intent(getActivity(), VerifyFingerPrint.class).setAction("Main Farmer"));
                        }
                        else {
                            mFragmentManager = getActivity().getSupportFragmentManager();
                            mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
                            );
                            mFragmentTransaction.replace(android.R.id.content, new SendConsignment());
                            mFragmentTransaction.addToBackStack("consignment");
                            mFragmentTransaction.commit();
                        }

                    } else if (selectedCollectionCenter != null) {
                        dismiss();
                        if (Objects.equals(collectionType, NEW_COLLECTION)) {
                            CommonConstants.selectedPlotCode.clear();
                            DataManager.getInstance().deleteData(EXTRA_PLOTS);
                            startActivity(new Intent(getActivity(), FarmersListScreenForCC.class).setAction("Main Farmer"));
                        } else if (collectionType.equals(STOCK_TRANSFER)) {
                            mFragmentManager = getActivity().getSupportFragmentManager();
                            mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
                            );
                            mFragmentTransaction.replace(android.R.id.content, new SendStockTransfer());
                            mFragmentTransaction.addToBackStack("stock_transfer");
                            mFragmentTransaction.commit();
                        }

                        else if(collectionType.equals(Verify_Fingerprint)){

                            startActivity(new Intent(getActivity(), VerifyFingerPrint.class).setAction("Main Farmer"));
                        }else {
                            mFragmentManager = getActivity().getSupportFragmentManager();
                            mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.enter_from_right, 0, 0, R.anim.exit_to_left
                            );
                            mFragmentTransaction.replace(android.R.id.content, new SendConsignment());
                            mFragmentTransaction.addToBackStack("consignment");
                            mFragmentTransaction.commit();
                        }
                    } else {
                        UiUtils.showCustomToastMessage("Please Select Proper Collection Center", getActivity(), 1);

                    }
                }
            });



            return view;
        }

        //Data Binds on Click on Change Button Click Event
        private void changebindCollectionCenterData() {
//                UiUtils.showCustomToastMessage("Selected collection center is empty", getActivity(), 1);

            if (null != collectionCenterList && !collectionCenterList.isEmpty()) {
                String[] collectionCenterNames = new String[collectionCenterList.size()];
                for (int i = 0; i < collectionCenterList.size(); i++) {
                    collectionCenterNames[i] = collectionCenterList.get(i).getName();
                }

                String[] filteredData = new String[1];
                filteredData[0] = "-- Select Collection Center--";
                List list = new ArrayList(Arrays.asList(filteredData));
                list.addAll(Arrays.asList(collectionCenterNames));
                Object[] c = list.toArray();
                String[] finalCollectionCenterNamnes = Arrays.copyOf(c, c.length, String[].class);

                ArrayAdapter<String> collectionSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, finalCollectionCenterNamnes);
                collectionSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collectionCenterSpinner.setAdapter(collectionSpinnerArrayAdapter);
                collectionCenterSpinner.setSelection(1);
            } else {
                String[] noData = new String[1];
                noData[0] = "---- No Collection Centers Found ---";
                ArrayAdapter<String> collectionSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, noData);
                collectionSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collectionCenterSpinner.setAdapter(collectionSpinnerArrayAdapter);
                //collectionCenterSpinner.setSelection(1);
            }

        }

        //Binding Collection Center Data
        public void bindCollectionCenterData() {
            if (selectedCollectionCenter == null) {
//                UiUtils.showCustomToastMessage("Selected collection center is first time", getActivity(), 1);

                String[] noData = new String[1];
                noData[0] = "---- No Collection Centers Found ---";
                ArrayAdapter<String> collectionSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, noData);
                collectionSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collectionCenterSpinner.setAdapter(collectionSpinnerArrayAdapter);

            } else {
//                UiUtils.showCustomToastMessage("Selected collection center -- "+selectedCollectionCenter.getName(), getActivity(), 0);

                String[] noData = new String[1];
                noData[0] = selectedCollectionCenter.getName();
                ArrayAdapter<String> collectionSpinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, noData);
                collectionSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collectionCenterSpinner.setAdapter(collectionSpinnerArrayAdapter);

            }

        }
    }

    //Registration Type Chooser for Reports
    public static class RegistrationTypeChooser extends DialogFragment {

        public RegistrationTypeChooser() {

        }

        public static CollectionCenterHomeScreen.RegistrationTypeChooser newInstance(String type) {
            CollectionCenterHomeScreen.RegistrationTypeChooser fragment = new CollectionCenterHomeScreen.RegistrationTypeChooser();
            Bundle args = new Bundle();
            args.putString("type", "" + type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.weigh_bridge_choose_dialog, container);
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            view.setMinimumWidth((int) (displayRectangle.width() * 0.7f));

            //Navigations

            RelativeLayout type1 = (RelativeLayout) view.findViewById(R.id.collectionreport_cc_rel);
            type1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                    startActivity(new Intent(getActivity(), CollectionReport.class));
                }
            });

            RelativeLayout type2 = (RelativeLayout) view.findViewById(R.id.consignmentteport_private_rel);
            type2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                    startActivity(new Intent(getActivity(), ConsignmentReportActivity.class));
                }
            });

            RelativeLayout stockTransferRl = view.findViewById(R.id.stockTransferRL);

            stockTransferRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                    startActivity(new Intent(getActivity(), StockTransferReport.class));
                }
            });
            /*type3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle typeBundle = new Bundle();
                    typeBundle.putInt("type", 3);
                    Fragment fragment = new WeighbridgeCC();
                    fragment.setArguments(typeBundle);
                    String backStateName = fragment.getClass().getName();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ftransaction = fm.beginTransaction();
                    ftransaction.replace(android.R.id.content, fragment).commit();
                    ftransaction.addToBackStack(backStateName);
                    getDialog().dismiss();
                }
            });*/
            return view;
        }
    }

}
