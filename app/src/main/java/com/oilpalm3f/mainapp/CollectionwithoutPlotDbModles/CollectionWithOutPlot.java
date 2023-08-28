package com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class CollectionWithOutPlot implements Parcelable {
    private String Code;
    private String CollectionCenterCode;
    private String FarmerCode;
    private Integer WeighbridgeCenterId;
    private String VehicleNumber;
    private String DriverName;
    private double GrossWeight;
    private double TareWeight;
    private double NetWeight;
    private String OperatorName;
    private String Comments;
    private int TotalBunches;
    private int RejectedBunches;
    private int AcceptedBunches;
    private String Remarks;
    private String GraderName;
    private String ReceiptCode;
    private String WBReceiptName;
    private String WBReceiptLocation;
    private String WBReceiptExtension;
    private int CreatedByUserId;
    private int UpdatedByUserId;
    private String WeighingDate;
    private String ReceiptGeneratedDate;
    private String CreatedDate;
    private String UpdatedDate;
    private int IsActive;
    int TokenNo;
    Float Trpt;

    private boolean ServerUpdatedStatus;


    private int VehicleTypeId;
    private String Name;
    private String MobileNumber;
    private String Village;
    private String Mandal;
    private double UnRipen;
    private double UnderRipe;
    private double Ripen;
    private double OverRipe;
    private double Diseased;
    private double EmptyBunches;
    private double FFBQualityLong;
    private double FFBQualityMedium;
    private double FFBQualityShort;
    private double FFBQualityOptimum;

    private int LooseFruit;
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



    public CollectionWithOutPlot(){

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

    public Float getTrpt() {
        return Trpt;
    }

    public void setTrpt(Float trpt) {
        Trpt = trpt;
    }

    public int getTokenNo() {
        return TokenNo;
    }

    public void setTokenNo(int tokenNo) {
        TokenNo = tokenNo;
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
        dest.writeString(Code);
        dest.writeString(CollectionCenterCode);
        dest.writeString(FarmerCode);
        if (WeighbridgeCenterId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(WeighbridgeCenterId);
        }
        dest.writeString(VehicleNumber);
        dest.writeString(DriverName);
        dest.writeDouble(GrossWeight);
        dest.writeDouble(TareWeight);
        dest.writeDouble(NetWeight);
        dest.writeString(OperatorName);
        dest.writeString(Comments);
        dest.writeInt(TotalBunches);
        dest.writeInt(RejectedBunches);
        dest.writeInt(AcceptedBunches);
        dest.writeString(Remarks);
        dest.writeString(GraderName);
        dest.writeString(ReceiptCode);
        dest.writeString(WBReceiptName);
        dest.writeString(WBReceiptLocation);
        dest.writeString(WBReceiptExtension);
        dest.writeInt(CreatedByUserId);
        dest.writeInt(UpdatedByUserId);
        dest.writeString(WeighingDate);
        dest.writeString(ReceiptGeneratedDate);
        dest.writeString(CreatedDate);
        dest.writeString(UpdatedDate);
        dest.writeInt(IsActive);
        dest.writeByte((byte) (ServerUpdatedStatus ? 1 : 0));
        dest.writeInt(TokenNo);

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

    protected CollectionWithOutPlot(Parcel in) {
        Code = in.readString();
        CollectionCenterCode = in.readString();
        FarmerCode = in.readString();
        if (in.readByte() == 0) {
            WeighbridgeCenterId = null;
        } else {
            WeighbridgeCenterId = in.readInt();
        }
        VehicleNumber = in.readString();
        DriverName = in.readString();
        GrossWeight = in.readDouble();
        TareWeight = in.readDouble();
        NetWeight = in.readDouble();
        OperatorName = in.readString();
        Comments = in.readString();
        TotalBunches = in.readInt();
        RejectedBunches = in.readInt();
        AcceptedBunches = in.readInt();
        Remarks = in.readString();
        GraderName = in.readString();
        ReceiptCode = in.readString();
        WBReceiptName = in.readString();
        WBReceiptLocation = in.readString();
        WBReceiptExtension = in.readString();
        CreatedByUserId = in.readInt();
        UpdatedByUserId = in.readInt();
        WeighingDate = in.readString();
        ReceiptGeneratedDate = in.readString();
        CreatedDate = in.readString();
        UpdatedDate = in.readString();
        TokenNo=in.readInt();
        IsActive = in.readInt();
        ServerUpdatedStatus = in.readByte() != 0;


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

    public static final Creator<CollectionWithOutPlot> CREATOR = new Creator<CollectionWithOutPlot>() {
        @Override
        public CollectionWithOutPlot createFromParcel(Parcel in) {
            return new CollectionWithOutPlot(in);
        }

        @Override
        public CollectionWithOutPlot[] newArray(int size) {
            return new CollectionWithOutPlot[size];
        }
    };
}

