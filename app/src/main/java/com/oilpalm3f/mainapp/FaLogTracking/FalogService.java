package com.oilpalm3f.mainapp.FaLogTracking;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.oilpalm3f.mainapp.activitylogdetails.LatLongListener;
import com.oilpalm3f.mainapp.activitylogdetails.LocationProvider;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.database.DataAccessHandler;
import com.oilpalm3f.mainapp.database.Palm3FoilDatabase;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;


//Location Logs of the Login User
public class FalogService extends Service implements LocationListener {

    private static final String LOG_TAG = "MyService";

    private static LocationProvider mLocationProvider;
    private static String latLong="";

    PowerManager.WakeLock wakeLock;
    public Context context;
    double latitude, longitude;
    private Palm3FoilDatabase palm3FoilDatabase;
    private static final int MIN_UPDATE_TIME = 0;
    private static final int MIN_UPDATE_DISTANCE = 250;
    private Location location;
    public LocationManager locationManager;
    public String CreatedDate, UpdatedDate, ServerUpdatedStatus, CreatedByUserId;
    public String USER_ID_TRACKING;
     UserDetails userDetails;
    private DataAccessHandler dataAccessHandler = null;


    @Override
    public void onCreate() {
        super.onCreate();
//        palm3FoilDatabase = new Palm3FoilDatabase(this);
        Log.v(LOG_TAG, "Congrats! MyService Created");
//        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "onCreate");


    }

    public static LocationProvider getLocationProvider(Context context, boolean showDialog){
        if(mLocationProvider == null){
            mLocationProvider = new LocationProvider(context, new LatLongListener() {

                public void getLatLong(String mLatLong) {
                    latLong = mLatLong;
                }
            });

        }
        if(mLocationProvider.getLocation(showDialog)){
            return mLocationProvider;
        } else{
            return null;
        }

    }

    //Getting Lat Longs from the Location Provider
    public  String getLatLong(Context context,boolean showDialog) {

        mLocationProvider = getLocationProvider(context,showDialog);

        if(mLocationProvider != null){
            latLong = mLocationProvider.getLatitudeLongitude();


        }
        return latLong;
    }



//Starting Location Service
    public void startLocationService(ApplicationThread.OnComplete onComplete) {
        Log.d(LOG_TAG, "start location service");
        String providerType = null;
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gpsProviderEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkProviderEnabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsProviderEnabled) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    providerType = "gps";
                    com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG, "gps lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));
                    //updateLocation(location);
                }
            }
            if (networkProviderEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    providerType = "network";
                    com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG, "network lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) +"," + String.valueOf(location.getLongitude())));
                   // updateLocation(location);
                }
            }




        } catch (Exception e) {
            Log.e(LOG_TAG, "Cannot get location", e);
        }

        if (onComplete != null) {
            onComplete.execute(location != null, location, providerType);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        com.oilpalm3f.mainapp.cloudhelper.Log.d(LOG_TAG, "start location service & location listener");
        ApplicationThread.nuiPost(LOG_TAG, "start lococation service", new Runnable() {
            @Override
            public void run() {
                startLocationService(null);

            }
        });

        try {
            palm3FoilDatabase = Palm3FoilDatabase.getPalm3FoilDatabase(this);
            palm3FoilDatabase.createDataBase();
            dataAccessHandler = new DataAccessHandler(context);
        } catch (Exception e) {
            e.getMessage();
        }

        String query = Queries.getInstance().getUserDetailsNewQuery(CommonUtils.getIMEInumberID(this));

        DataAccessHandler dataAccessHandler = new DataAccessHandler(this);
         userDetails = (UserDetails) dataAccessHandler.getUserDetails(query);

        if (null != userDetails) {
            USER_ID_TRACKING = userDetails.getId();
            CommonConstants.USER_ID = userDetails.UserId;
            Log.v(LOG_TAG, "Start Service userId" + USER_ID_TRACKING);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }



    //On Location Changed Method, new Lat longs gets inserted
    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("appprefs", MODE_PRIVATE);
        boolean isFreshInstall = sharedPreferences.getBoolean(CommonConstants.IS_FRESH_INSTALL, true);

        if (location != null) {


            String latlong[]= getLatLong(FalogService.this,false).split("@");

            Log.d(LOG_TAG, "updateTracking location:" + String.valueOf(location.getLatitude()) + "/" + String.valueOf(location.getLongitude()));
            latitude = Double.parseDouble(latlong[0]);
            longitude = Double.parseDouble(latlong[1]);

            CreatedDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS);


            String selectedLatLong = dataAccessHandler.getFalogLatLongs(Queries.getInstance().queryVerifyFalogDistance());
            if (!TextUtils.isEmpty(selectedLatLong)){
                Log.v(LOG_TAG, "@@@@ data " + selectedLatLong);
                double actualDistance = 0;
                String[] yieldDataArr = selectedLatLong.split("-");

                if (yieldDataArr.length > 0 && !TextUtils.isEmpty(yieldDataArr[0]) && !TextUtils.isEmpty(yieldDataArr[1])) {

                    actualDistance = CommonUtils.distance(latitude, longitude,
                            Double.parseDouble(yieldDataArr[0]),
                            Double.parseDouble(yieldDataArr[1]), 'm');

                }

                     Log.v(LOG_TAG, "@@@@ actual distance " + actualDistance);

                if (actualDistance >= 250) {


                    if(Integer.parseInt(CommonConstants.USER_ID) != 12345) {
                        palm3FoilDatabase.insertLatLong(latitude, longitude, CommonConstants.USER_ID, CreatedDate);
                    }

                    if(CommonUtils.isNetworkAvailable(this)){
                        DataSyncHelper.sendTrackingData(context, new ApplicationThread.OnComplete() {
                            @Override
                            public void execute(boolean success, Object result, String msg) {
                                if (success) {
                                    Log.v(LOG_TAG, "sent success");
                                } else {
                                    Log.e(LOG_TAG, "sent failed");
                                }
                            }
                        });
                    }

                }
                else {

                }
            }else {
                if(Integer.parseInt(CommonConstants.USER_ID) != 12345) {
                palm3FoilDatabase.insertLatLong(latitude, longitude, CommonConstants.USER_ID, CreatedDate);
                }

                if(CommonUtils.isNetworkAvailable(this)){
                    DataSyncHelper.sendTrackingData(context, new ApplicationThread.OnComplete() {
                        @Override
                        public void execute(boolean success, Object result, String msg) {
                            if (success) {
                                Log.v(LOG_TAG, "sent success");
                            } else {
                                Log.e(LOG_TAG, "sent failed");
                            }
                        }
                    });
                }
            }
        }


    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
