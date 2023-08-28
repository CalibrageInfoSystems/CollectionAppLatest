package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;


public class Consignment implements Parcelable {
    @Expose
    private String Code;
    @Expose
    private String CollectionCenterCode;
    @Expose
    private String VehicleNumber;
    @Expose
    private String DriverName;
    @Expose
    private String MillCode;
    @Expose
    private double TotalWeight;
    @Expose
    private double GrossWeight;
    @Expose
    private double TareWeight;
    @Expose
    private double NetWeight;
    @Expose
    private double WeightDifference;
    @Expose
    private String ReceiptGeneratedDate;
    @Expose
    private String ReceiptCode;

    @Expose(serialize = false, deserialize = false)
    private String createdBy;

    @Expose(serialize = false, deserialize = false)
    private String comments;
    @Expose
    private boolean ServerUpdatedStatus;
    @Expose
    private String CreatedDate;
    @Expose
    private String UpdatedDate;
    @Expose
    private int IsActive;
    @Expose
    private int CreatedByUserId;
    @Expose
    private int UpdatedByUserId;
    @Expose
    private String DriverMobileNumber;
    @Expose
    private String VehcileType;

    @Expose
    private int TotalBunches;
    @Expose
    private int RejectedBunches;
    @Expose
    private int AcceptedBunches;
    @Expose
    private String Remarks;
    @Expose
    private String GraderName;
    @Expose
    private int SizeOfTruckId;
    @Expose
    private boolean IsSharing;
    @Expose
    private float TransportCost;
    @Expose
    private float SharingCost;
    @Expose
    private float OverWeightCost;
    @Expose
    private float ExpectedCost;
    @Expose
    private float ActualCost;



    public String getDriverMobileNumber() {
        return DriverMobileNumber;
    }

    public void setDriverMobileNumber(String driverMobileNumber) {
        DriverMobileNumber = driverMobileNumber;
    }

    public String getVehcileType() {
        return VehcileType;
    }

    public void setVehcileType(String vehcileType) {
        VehcileType = vehcileType;
    }

    public int getTotalBunches() {
        return TotalBunches;
    }

    public void setTotalBunches(int totalBunches) {
        TotalBunches = totalBunches;
    }

    public int getRejectedBunches() {
        return RejectedBunches;
    }

    public void setRejectedBunches(int rejectedBunches) {
        RejectedBunches = rejectedBunches;
    }

    public int getAcceptedBunches() {
        return AcceptedBunches;
    }

    public void setAcceptedBunches(int acceptedBunches) {
        AcceptedBunches = acceptedBunches;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getGraderName() {
        return GraderName;
    }

    public void setGraderName(String graderName) {
        GraderName = graderName;
    }

    public static Creator<Consignment> getCREATOR() {
        return CREATOR;
    }


    public boolean getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public String getRecieptGeneratedDate() {
        return ReceiptGeneratedDate;
    }

    public void setRecieptGeneratedDate(String recieptGeneratedDate) {
        ReceiptGeneratedDate = recieptGeneratedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getCollectioncentercode() {
        return CollectionCenterCode;
    }

    public void setCollectioncentercode(String CollectionCenterCode) {
        this.CollectionCenterCode = CollectionCenterCode;
    }

    public String getVehiclenumber() {
        return VehicleNumber;
    }

    public void setVehiclenumber(String VehicleNumber) {
        this.VehicleNumber = VehicleNumber;
    }

    public String getDrivername() {
        return DriverName;
    }

    public void setDrivername(String DriverName) {
        this.DriverName = DriverName;
    }

    public String getMillcode() {
        return MillCode;
    }

    public void setMillcode(String MillCode) {
        this.MillCode = MillCode;
    }

    public double getTotalweight() {
        return TotalWeight;
    }

    public void setTotalweight(double TotalWeight) {
        this.TotalWeight = TotalWeight;
    }

    public double getGrossweight() {
        return GrossWeight;
    }

    public void setGrossweight(double GrossWeight) {
        this.GrossWeight = GrossWeight;
    }

    public double getTareweight() {
        return TareWeight;
    }

    public void setTareweight(double TareWeight) {
        this.TareWeight = TareWeight;
    }

    public double getNetweight() {
        return NetWeight;
    }

    public void setNetweight(double NetWeight) {
        this.NetWeight = NetWeight;
    }

    public double getWeightdifference() {
        return WeightDifference;
    }

    public void setWeightdifference(double WeightDifference) {
        this.WeightDifference = WeightDifference;
    }

    public String getReceiptCode() {
        return ReceiptCode;
    }

    public void setReceiptCode(String ReceiptCode) {
        this.ReceiptCode = ReceiptCode;
    }

    public int getCreatedbyuserid() {
        return CreatedByUserId;
    }

    public void setCreatedbyuserid(int CreatedByUserId) {
        this.CreatedByUserId = CreatedByUserId;
    }

    public int getUpdatedbyuserid() {
        return UpdatedByUserId;
    }

    public void setUpdatedbyuserid(int UpdatedByUserId) {
        this.UpdatedByUserId = UpdatedByUserId;
    }

    public int getSizeOfTruckId() {
        return SizeOfTruckId;
    }

    public void setSizeOfTruckId(int sizeOfTruckId) {
        SizeOfTruckId = sizeOfTruckId;
    }

    public boolean isSharing() {
        return IsSharing;
    }

    public void setSharing(boolean sharing) {
        IsSharing = sharing;
    }

    public float getTransportCost() {
        return TransportCost;
    }

    public void setTransportCost(float transportCost) {
        TransportCost = transportCost;
    }

    public float getSharingCost() {
        return SharingCost;
    }

    public void setSharingCost(float sharingCost) {
        SharingCost = sharingCost;
    }

    public float getOverWeightCost() {
        return OverWeightCost;
    }

    public void setOverWeightCost(float overWeightCost) {
        OverWeightCost = overWeightCost;
    }

    public float getExpectedCost() {
        return ExpectedCost;
    }

    public void setExpectedCost(float expectedCost) {
        ExpectedCost = expectedCost;
    }

    public float getActualCost() {
        return ActualCost;
    }

    public void setActualCost(float actualCost) {
        ActualCost = actualCost;
    }

    protected Consignment(Parcel in) {
        Code = in.readString();
        CollectionCenterCode = in.readString();
        VehicleNumber = in.readString();
        DriverName = in.readString();
        MillCode = in.readString();
        TotalWeight = in.readDouble();
        GrossWeight = in.readDouble();
        TareWeight = in.readDouble();
        NetWeight = in.readDouble();
        WeightDifference = in.readDouble();
        ReceiptCode = in.readString();
        createdBy = in.readString();
        comments = in.readString();
        CreatedByUserId = in.readInt();
        UpdatedByUserId = in.readInt();
        IsActive = in.readInt();
        CreatedDate = in.readString();
        UpdatedDate = in.readString();
        TotalBunches = in.readInt();
        RejectedBunches = in.readInt();
        AcceptedBunches=in.readInt();
        Remarks = in.readString();
        GraderName = in.readString();
        SizeOfTruckId = in.readInt();
        TransportCost = in.readFloat();
        SharingCost = in.readFloat();
        OverWeightCost = in.readFloat();
        ExpectedCost = in.readFloat();
        ActualCost = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Code);
        dest.writeString(CollectionCenterCode);
        dest.writeString(VehicleNumber);
        dest.writeString(DriverName);
        dest.writeString(MillCode);
        dest.writeDouble(TotalWeight);
        dest.writeDouble(GrossWeight);
        dest.writeDouble(TareWeight);
        dest.writeDouble(NetWeight);
        dest.writeDouble(WeightDifference);
        dest.writeString(ReceiptCode);
        dest.writeString(createdBy);
        dest.writeString(comments);
        dest.writeInt(CreatedByUserId);
        dest.writeInt(UpdatedByUserId);
        dest.writeInt(IsActive);
        dest.writeString(CreatedDate);
        dest.writeString(UpdatedDate);
        dest.writeInt(TotalBunches);
        dest.writeInt(RejectedBunches);
        dest.writeInt(AcceptedBunches);
        dest.writeString(Remarks);
        dest.writeString(GraderName);
        dest.writeInt(SizeOfTruckId);
        dest.writeFloat(TransportCost);
        dest.writeFloat(SharingCost);
        dest.writeFloat(OverWeightCost);
        dest.writeFloat(ExpectedCost);
        dest.writeFloat(ActualCost);
    }

    public Consignment() {
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Consignment> CREATOR = new Parcelable.Creator<Consignment>() {
        @Override
        public Consignment createFromParcel(Parcel in) {
            return new Consignment(in);
        }

        @Override
        public Consignment[] newArray(int size) {
            return new Consignment[size];
        }
    };
}