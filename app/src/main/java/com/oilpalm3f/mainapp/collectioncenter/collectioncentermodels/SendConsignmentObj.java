package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skasam on 2/4/2017.
 */
public class SendConsignmentObj implements Parcelable{


    private String consignmentNumber;
    private String createdBy;
    private String vehicleNumber;
    private String driverName;
    private String millName;
    private String consignmentweight;
    private String comments;
    private int CreatedByUserId;
    private int UpdatedByUserId;

    public String getConsignmentNumber() {
        return consignmentNumber;
    }

    public void setConsignmentNumber(String consignmentNumber) {
        this.consignmentNumber = consignmentNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getMillName() {
        return millName;
    }

    public void setMillName(String millName) {
        this.millName = millName;
    }

    public String getConsignmentweight() {
        return consignmentweight;
    }

    public void setConsignmentweight(String consignmentweight) {
        this.consignmentweight = consignmentweight;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public int getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.consignmentNumber);
        dest.writeString(this.createdBy);
        dest.writeString(this.vehicleNumber);
        dest.writeString(this.driverName);
        dest.writeString(this.millName);
        dest.writeString(this.consignmentweight);
        dest.writeString(this.comments);
        dest.writeInt(this.CreatedByUserId);
        dest.writeInt(this.UpdatedByUserId);
    }

    public SendConsignmentObj() {
    }

    protected SendConsignmentObj(Parcel in) {
        this.consignmentNumber = in.readString();
        this.createdBy = in.readString();
        this.vehicleNumber = in.readString();
        this.driverName = in.readString();
        this.millName = in.readString();
        this.consignmentweight = in.readString();
        this.comments = in.readString();
        this.CreatedByUserId = in.readInt();
        this.UpdatedByUserId = in.readInt();
    }

    public static final Parcelable.Creator<SendConsignmentObj> CREATOR = new Parcelable.Creator<SendConsignmentObj>() {
        @Override
        public SendConsignmentObj createFromParcel(Parcel source) {
            return new SendConsignmentObj(source);
        }

        @Override
        public SendConsignmentObj[] newArray(int size) {
            return new SendConsignmentObj[size];
        }
    };
}
