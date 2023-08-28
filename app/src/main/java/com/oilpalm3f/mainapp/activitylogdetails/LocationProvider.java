package com.oilpalm3f.mainapp.activitylogdetails;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

public class LocationProvider implements LocationListener {

    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    Location location;
    Context context;

    double mLatitude;
    double mLongitude;


    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    Dialog alertDialog;
    LatLongListener mLatLongListener;

    public LocationProvider(Context context, LatLongListener mLatLongListener) {
        this.context = context;
        this.mLatLongListener = mLatLongListener;
    }

    //Get Location
    public boolean getLocation(boolean showDialog) {


        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        try {
            Location gpsLocation = null;
            Location networkLocation = null;
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }

            gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER);
            networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER);
            // If both providers return last known locations, compare the two and use the better
            // one to update the UI.  If only one provider returns a location, use it.

            if (gpsLocation != null && networkLocation != null) {
                location = gpsLocation;
                updateUILocation(getBetterLocation(gpsLocation, networkLocation));
            } else if (gpsLocation != null) {
                location = gpsLocation;
                updateUILocation(gpsLocation);
            } else if (networkLocation != null) {
                location = networkLocation;
                updateUILocation(networkLocation);
            } else {
                //Toast.makeText(getApplicationContext(), getString(R.string.str_unable_to_get_location_from_service), Toast.LENGTH_LONG).show();
            }


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(),getString(R.string.str_unable_to_get_location_from_service), Toast.LENGTH_LONG).show();
        }

        return !((!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }


    //Update Location
    private void updateUILocation(Location location) {
        // We're sending the update to a handler which then updates the UI with the new
        // location.
        try {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            //Toast.makeText(context, ""+mLatitude+" , "+mLongitude, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            mLatitude = 0;
            mLongitude = 0;
        }

        if (location != null)
            onLocationChanged(location);
    }

    //Update Location from the Location Provider
    private Location requestUpdatesFromProvider(final String provider) {
        Location location = null;
        try {
            if (locationManager.isProviderEnabled(provider)) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                locationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, this);
                location = locationManager.getLastKnownLocation(provider);
            } else {
                // Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            // TODO: handle exception
        }
        return location;
    }

//Get Accurate Location
    private Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return newLocation;
        }

        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }

    /** Checks whether two providers are the same */

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

//On Location Changed
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        if(mLatLongListener != null)
            mLatLongListener.getLatLong(mLatitude+"@"+mLongitude);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }


    public String getLatitudeLongitude(){
        return mLatitude+"@"+mLongitude;
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

}
