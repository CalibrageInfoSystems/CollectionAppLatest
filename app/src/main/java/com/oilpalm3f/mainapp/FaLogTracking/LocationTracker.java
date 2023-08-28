package com.oilpalm3f.mainapp.FaLogTracking;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationTracker implements Parcelable {


    private int UserId;
    private double Latitude;
    private double Longitude;
    private String Address;
    private String LogDate;
    private int ServerUpdatedStatus;

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public LocationTracker() {

    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String logDate) {
        LogDate = logDate;
    }



    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }


    protected LocationTracker(Parcel in) {
        UserId = in.readInt();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        Address = in.readString();
        LogDate = in.readString();

    }

    public static final Creator<LocationTracker> CREATOR = new Creator<LocationTracker>() {
        @Override
        public LocationTracker createFromParcel(Parcel in) {
            return new LocationTracker(in);
        }

        @Override
        public LocationTracker[] newArray(int size) {
            return new LocationTracker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(UserId);
        parcel.writeDouble(Latitude);
        parcel.writeDouble(Longitude);
        parcel.writeString(Address);
        parcel.writeString(LogDate);
    }
}
