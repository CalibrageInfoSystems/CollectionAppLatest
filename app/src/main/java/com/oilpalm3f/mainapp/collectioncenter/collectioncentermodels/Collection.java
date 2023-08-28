package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Collection implements Parcelable {
    @Expose
    private String Code;
    @Expose
    private String CollectionCenterCode;
    @Expose
    private String FarmerCode;
    @Expose
    private Integer WeighbridgeCenterId;
    @Expose
    private String VehicleNumber;
    @Expose
    private String DriverName;
    @Expose
    private double GrossWeight;
    @Expose
    private double TareWeight;
    @Expose
    private double NetWeight;
    @Expose
    private String OperatorName;
    @Expose
    private String Comments;
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
    private String ReceiptCode;
    @Expose
    private String WBReceiptName;
    @Expose
    private String WBReceiptLocation;
    @Expose
    private String WBReceiptExtension;
    @Expose
    private int CreatedByUserId;
    @Expose
    private int UpdatedByUserId;
    @Expose
    private String WeighingDate;
    @Expose
    private String ReceiptGeneratedDate;
    @Expose
    private String CreatedDate;
    @Expose
    private String UpdatedDate;
    @Expose
    private int IsActive;
    @Expose
    private int TokenNo;

    @Expose
    private boolean IsCollectionWithOutFarmer;



    @Expose
    private int VehicleTypeId;
    @Expose
    private String Name;
    @Expose
    private String MobileNumber;
    @Expose
    private String Village;
    @Expose
    private String Mandal;
    @Expose
    private double UnRipen;
    @Expose
    private double UnderRipe;
    @Expose
    private double Ripen;
    @Expose
    private double OverRipe;
    @Expose
    private double Diseased;
    @Expose
    private double EmptyBunches;
    @Expose
    private double FFBQualityLong;
    @Expose
    private double FFBQualityMedium;
    @Expose
    private double FFBQualityShort;
    @Expose
    private double FFBQualityOptimum;
    @Expose
    private int LooseFruit;
    @Expose
    private Integer LooseFruitWeight;

    private String GraderCode;

    public String getGraderCode() {
        return GraderCode;
    }

    public void setGraderCode(String graderCode) {
        GraderCode = graderCode;
    }

    public int getLooseFruit() {
        return LooseFruit;
    }

    public void setLooseFruit(int looseFruit) {
        LooseFruit = looseFruit;
    }

    public Integer getLooseFruitWeight() {
        return LooseFruitWeight;
    }

    public void setLooseFruitWeight(Integer looseFruitWeight) {
        LooseFruitWeight = looseFruitWeight;
    }

    public int getVehicleTypeId() {
        return VehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        VehicleTypeId = vehicleTypeId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getMandal() {
        return Mandal;
    }

    public void setMandal(String mandal) {
        Mandal = mandal;
    }

    public double getUnRipen() {
        return UnRipen;
    }

    public void setUnRipen(double unRipen) {
        UnRipen = unRipen;
    }

    public double getUnderRipe() {
        return UnderRipe;
    }

    public void setUnderRipe(double underRipe) {
        UnderRipe = underRipe;
    }

    public double getRipen() {
        return Ripen;
    }

    public void setRipen(double ripen) {
        Ripen = ripen;
    }

    public double getOverRipe() {
        return OverRipe;
    }

    public void setOverRipe(double overRipe) {
        OverRipe = overRipe;
    }

    public double getDiseased() {
        return Diseased;
    }

    public void setDiseased(double diseased) {
        Diseased = diseased;
    }

    public double getEmptyBunches() {
        return EmptyBunches;
    }

    public void setEmptyBunches(double emptyBunches) {
        EmptyBunches = emptyBunches;
    }

    public double getFFBQualityLong() {
        return FFBQualityLong;
    }

    public void setFFBQualityLong(double FFBQualityLong) {
        this.FFBQualityLong = FFBQualityLong;
    }

    public double getFFBQualityMedium() {
        return FFBQualityMedium;
    }

    public void setFFBQualityMedium(double FFBQualityMedium) {
        this.FFBQualityMedium = FFBQualityMedium;
    }

    public double getFFBQualityShort() {
        return FFBQualityShort;
    }

    public void setFFBQualityShort(double FFBQualityShort) {
        this.FFBQualityShort = FFBQualityShort;
    }

    public double getFFBQualityOptimum() {
        return FFBQualityOptimum;
    }

    public void setFFBQualityOptimum(double FFBQualityOptimum) {
        this.FFBQualityOptimum = FFBQualityOptimum;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
       UpdatedDate = updatedDate;
   }

    public boolean getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }


    public int getTokenNo() {
        return TokenNo;
    }

    public void setTokenNo(int tokenNo) {
        TokenNo = tokenNo;
    }

    private boolean ServerUpdatedStatus;

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getRecieptGeneratedDate() {
        return ReceiptGeneratedDate;
    }

    public void setRecieptGeneratedDate(String recieptGeneratedDate) {
        ReceiptGeneratedDate = recieptGeneratedDate;
    }

    public String getWeighingDate() {
        return WeighingDate;
    }

    public void setWeighingDate(String weighingDate) {
        WeighingDate = weighingDate;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCollectioncentercode() {
        return CollectionCenterCode;
    }

    public void setCollectioncentercode(String CollectionCenterCode) {
        this.CollectionCenterCode = CollectionCenterCode;
    }

    public String getFarmercode() {
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode) {
        this.FarmerCode = FarmerCode;
    }

    public Integer getWeighbridgecenterid() {
        return WeighbridgeCenterId;
    }

    public void setWeighbridgecenterid(Integer WeighbridgeCenterId) {
        this.WeighbridgeCenterId = WeighbridgeCenterId;
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

    public String getOperatorname() {
        return OperatorName;
    }

    public void setOperatorname(String OperatorName) {
        this.OperatorName = OperatorName;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public int getTotalbunches() {
        return TotalBunches;
    }

    public void setTotalbunches(int TotalBunches) {
        this.TotalBunches = TotalBunches;
    }

    public int getRejectedbunches() {
        return RejectedBunches;
    }

    public void setRejectedbunches(int RejectedBunches) {
        this.RejectedBunches = RejectedBunches;
    }

    public int getAcceptedbunches() {
        return AcceptedBunches;
    }

    public void setAcceptedbunches(int AcceptedBunches) {
        this.AcceptedBunches = AcceptedBunches;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    public String getGradername() {
        return GraderName;
    }

    public void setGradername(String GraderName) {
        this.GraderName = GraderName;
    }

    public String getRecieptcode() {
        return ReceiptCode;
    }

    public void setRecieptcode(String RecieptCode) {
        this.ReceiptCode = RecieptCode;
    }

    public String getRecieptname() {
        return WBReceiptName;
    }

    public void setRecieptname(String RecieptName) {
        this.WBReceiptName = RecieptName;
    }

    public String getRecieptlocation() {
        return WBReceiptLocation;
    }

    public void setRecieptlocation(String RecieptLocation) {
        this.WBReceiptLocation = RecieptLocation;
    }

    public String getRecieptextension() {
        return WBReceiptExtension;
    }

    public void setRecieptextension(String RecieptExtension) {
        this.WBReceiptExtension = RecieptExtension;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Code);
        dest.writeString(this.CollectionCenterCode);
        dest.writeString(this.FarmerCode);
        dest.writeValue(this.WeighbridgeCenterId);
        dest.writeString(this.VehicleNumber);
        dest.writeString(this.DriverName);
        dest.writeDouble(this.GrossWeight);
        dest.writeDouble(this.TareWeight);
        dest.writeDouble(this.NetWeight);
        dest.writeString(this.OperatorName);
        dest.writeString(this.Comments);
        dest.writeInt(this.TotalBunches);
        dest.writeInt(this.RejectedBunches);
        dest.writeInt(this.AcceptedBunches);
        dest.writeString(this.Remarks);
        dest.writeString(this.GraderName);
        dest.writeString(this.ReceiptCode);
        dest.writeString(this.WBReceiptName);
        dest.writeString(this.WBReceiptLocation);
        dest.writeString(this.WBReceiptExtension);
        dest.writeInt(this.CreatedByUserId);
        dest.writeInt(this.UpdatedByUserId);
        dest.writeString(this.WeighingDate);
        dest.writeString(this.CreatedDate);
        dest.writeString(this.ReceiptGeneratedDate);
        dest.writeInt(this.IsActive);
        dest.writeInt(this.TokenNo);

        dest.writeInt(this.VehicleTypeId);
        dest.writeString(this.Name);
        dest.writeString(this.MobileNumber);
        dest.writeValue(this.Village);
        dest.writeString(this.Mandal);
        dest.writeDouble(this.UnRipen);
        dest.writeDouble(this.UnderRipe);
        dest.writeDouble(this.Ripen);
        dest.writeDouble(this.OverRipe);
        dest.writeDouble(this.Diseased);
        dest.writeDouble(this.EmptyBunches);
        dest.writeDouble(this.FFBQualityLong);
        dest.writeDouble(this.FFBQualityMedium);
        dest.writeDouble(this.FFBQualityShort);
        dest.writeDouble(this.FFBQualityOptimum);

        dest.writeInt(this.LooseFruit);
        dest.writeInt(this.LooseFruitWeight);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.Code = in.readString();
        this.CollectionCenterCode = in.readString();
        this.FarmerCode = in.readString();
        this.WeighbridgeCenterId = in.readInt();
        this.VehicleNumber = in.readString();
        this.DriverName = in.readString();
        this.GrossWeight = in.readDouble();
        this.TareWeight = in.readDouble();
        this.NetWeight = in.readDouble();
        this.OperatorName = in.readString();
        this.Comments = in.readString();
        this.TotalBunches = in.readInt();
        this.RejectedBunches = in.readInt();
        this.AcceptedBunches = in.readInt();
        this.Remarks = in.readString();
        this.GraderName = in.readString();
        this.ReceiptCode = in.readString();
        this.WBReceiptName = in.readString();
        this.WBReceiptLocation = in.readString();
        this.WBReceiptLocation = in.readString();
        this.CreatedByUserId = in.readInt();
        this.UpdatedByUserId = in.readInt();
        this.ReceiptGeneratedDate = in.readString();
        this.CreatedDate = in.readString();
        this.WeighingDate = in.readString();
        this.IsActive = in.readInt();
        this.TokenNo=in.readInt();

        this.VehicleTypeId = in.readInt();
        this.Name = in.readString();
        this.MobileNumber = in.readString();
        this.Village = in.readString();
        this.Mandal = in.readString();
        this.UnRipen = in.readDouble();
        this.UnderRipe = in.readDouble();
        this.Ripen = in.readDouble();
        this.OverRipe = in.readDouble();
        this.Diseased = in.readDouble();
        this.EmptyBunches = in.readDouble();
        this.FFBQualityLong = in.readDouble();
        this.FFBQualityMedium = in.readDouble();
        this.FFBQualityShort = in.readDouble();
        this.FFBQualityOptimum = in.readDouble();

        this.LooseFruit = in.readInt();
        this.LooseFruitWeight = in.readInt();

    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    public boolean isCollectionWithOutFarmer() {
        return IsCollectionWithOutFarmer;
    }

    public void setCollectionWithOutFarmer(boolean collectionWithOutFarmer) {
        IsCollectionWithOutFarmer = collectionWithOutFarmer;
    }
}

