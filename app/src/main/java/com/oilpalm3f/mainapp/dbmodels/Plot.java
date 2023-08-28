package com.oilpalm3f.mainapp.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siva on 19/02/17.
 */

public class Plot implements Parcelable {

    private int IsActive;
    private String Code;
    private String FarmerCode;
    private double TotalPlotArea;
    private double TotalPalmArea;
    private double GPSPlotArea;
    private Integer CropIncomeTypeId;
    private String AddressCode;
    private String SurveyNumber;
    private String AdangalNumber;
    private double LeftOutArea;
    private Integer LeftOutAreaCropId;
    private Integer PlotOwnerShipTypeId;
    private Integer IsPlotHandledByCareTaker;
    private String CareTakerName;
    private String CareTakerContactNumber;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;
    private String DateOfAdvanceReceived;
    private double AdvanceAmountReceived;
    private String ExpectedMonthOfPlanting;
    private String Comments ;
    private Integer  IsPLotSubsidySubmission;
    private Integer IsPLotHavingIdCard;
    private Integer IsGeoBoundariesVerification;
    private String SaplingPickUpDate;
    private Double SuitablePalmOilArea;
    private String DateofPlanting;
    private Integer SwapingReasonId;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }


    public String getDateOfAdvanceReceived() {
        return DateOfAdvanceReceived;
    }

    public void setDateOfAdvanceReceived(String dateOfAdvanceReceived) {
        DateOfAdvanceReceived = dateOfAdvanceReceived;
    }

    public double getAdvanceAmountReceived() {
        return AdvanceAmountReceived;
    }

    public void setAdvanceAmountReceived(double advanceAmountReceived) {
        AdvanceAmountReceived = advanceAmountReceived;
    }

    public String getExpectedMonthOfPlanting() {
        return ExpectedMonthOfPlanting;
    }

    public void setExpectedMonthOfPlanting(String expectedMonthOfPlanting) {
        ExpectedMonthOfPlanting = expectedMonthOfPlanting;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public Integer getIsPLotSubsidySubmission() {
        return IsPLotSubsidySubmission;
    }

    public void setIsPLotSubsidySubmission(Integer isPLotSubsidySubmission) {
        IsPLotSubsidySubmission = isPLotSubsidySubmission;
    }

    public Integer getIsPLotHavingIdCard() {
        return IsPLotHavingIdCard;
    }

    public void setIsPLotHavingIdCard(Integer isPLotHavingIdCard) {
        IsPLotHavingIdCard = isPLotHavingIdCard;
    }

    public Integer getIsGeoBoundariesVerification() {
        return IsGeoBoundariesVerification;
    }

    public void setIsGeoBoundariesVerification(Integer isGeoBoundariesVerification) {
        IsGeoBoundariesVerification = isGeoBoundariesVerification;
    }

    public String getSaplingPickUpDate() {
        return SaplingPickUpDate;
    }

    public void setSaplingPickUpDate(String saplingPickUpDate) {
        SaplingPickUpDate = saplingPickUpDate;
    }

    public Integer getIsBoundryFencing() {
        return IsBoundryFencing;
    }

    public void setIsBoundryFencing(Integer isBoundryFencing) {
        IsBoundryFencing = isBoundryFencing;
    }

    private Integer IsBoundryFencing;

    public String getFarmercode() {
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode) {
        this.FarmerCode = FarmerCode;
    }

    public double getTotalplotarea() {
        return TotalPlotArea;
    }

    public void setTotalplotarea(double TotalPlotArea) {
        this.TotalPlotArea = TotalPlotArea;
    }

    public double getTotalpalmarea() {
        return TotalPalmArea;
    }

    public void setTotalpalmarea(double TotalPalmArea) {
        this.TotalPalmArea = TotalPalmArea;
    }

    public double getGpsplotarea() {
        return GPSPlotArea;
    }

    public void setGpsplotarea(double GPSPlotArea) {
        this.GPSPlotArea = GPSPlotArea;
    }

    public Integer getCropincometypeid() {
        return CropIncomeTypeId;
    }

    public void setCropincometypeid(Integer CropIncomeTypeId) {
        this.CropIncomeTypeId = CropIncomeTypeId;
    }

    public String getAddesscode() {
        return AddressCode;
    }

    public void setAddesscode(String AddessCode) {
        this.AddressCode = AddessCode;
    }

    public String getSurveynumber() {
        return SurveyNumber;
    }

    public void setSurveynumber(String SurveyNumber) {
        this.SurveyNumber = SurveyNumber;
    }

    public String getAdangalnumber() {
        return AdangalNumber;
    }

    public void setAdangalnumber(String AdangalNumber) {
        this.AdangalNumber = AdangalNumber;
    }

    public double getLeftoutarea() {
        return LeftOutArea;
    }

    public void setLeftoutarea(double LeftOutArea) {
        this.LeftOutArea = LeftOutArea;
    }

    public Integer getLeftoutareacropid() {
        return LeftOutAreaCropId;
    }

    public void setLeftoutareacropid(Integer LeftOutAreaCropId) {
        this.LeftOutAreaCropId = LeftOutAreaCropId;
    }

    public Integer getPlotownershiptypeid() {
        return PlotOwnerShipTypeId;
    }

    public void setPlotownershiptypeid(Integer PlotOwnerShipTypeId) {
        this.PlotOwnerShipTypeId = PlotOwnerShipTypeId;
    }

    public Integer getIsplothandledbycaretaker() {
        return IsPlotHandledByCareTaker;
    }

    public void setIsplothandledbycaretaker(Integer IsPlotHandledByCareTaker) {
        this.IsPlotHandledByCareTaker = IsPlotHandledByCareTaker;
    }

    public String getCaretakername() {
        return CareTakerName;
    }

    public void setCaretakername(String CareTakerName) {
        this.CareTakerName = CareTakerName;
    }

    public String getCaretakercontactnumber() {
        return CareTakerContactNumber;
    }

    public void setCaretakercontactnumber(String CareTakerContactNumber) {
        this.CareTakerContactNumber = CareTakerContactNumber;
    }

    public int getCreatedbyuserid() {
        return CreatedByUserId;
    }

    public void setCreatedbyuserid(int CreatedByUserId) {
        this.CreatedByUserId = CreatedByUserId;
    }

    public String getCreateddate() {
        return CreatedDate;
    }

    public void setCreateddate(String CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public int getUpdatedbyuserid() {
        return UpdatedByUserId;
    }

    public void setUpdatedbyuserid(int UpdatedByUserId) {
        this.UpdatedByUserId = UpdatedByUserId;
    }

    public String getUpdateddate() {
        return UpdatedDate;
    }

    public void setUpdateddate(String UpdatedDate) {
        this.UpdatedDate = UpdatedDate;
    }

    public int getServerupdatedstatus() {
        return ServerUpdatedStatus;
    }

    public void setServerupdatedstatus(int ServerUpdatedStatus) {
        this.ServerUpdatedStatus = ServerUpdatedStatus;
    }

    public Plot() {

    }

    protected Plot(Parcel in) {
        FarmerCode = in.readString();
        TotalPlotArea = in.readDouble();
        TotalPalmArea = in.readDouble();
        GPSPlotArea = in.readDouble();
        CropIncomeTypeId = in.readInt();
        AddressCode = in.readString();
        SurveyNumber = in.readString();
        AdangalNumber = in.readString();
        LeftOutArea = in.readDouble();
        LeftOutAreaCropId = in.readInt();
        PlotOwnerShipTypeId = in.readInt();
        IsPlotHandledByCareTaker = in.readInt();
        CareTakerName = in.readString();
        CareTakerContactNumber = in.readString();
        CreatedByUserId = in.readInt();
        CreatedDate = in.readString();
        UpdatedByUserId = in.readInt();
        UpdatedDate = in.readString();
        ServerUpdatedStatus = in.readInt();
        IsActive = in.readInt();
        IsBoundryFencing = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FarmerCode);
        dest.writeDouble(TotalPlotArea);
        dest.writeDouble(TotalPalmArea);
        dest.writeDouble(GPSPlotArea);
        dest.writeInt(CropIncomeTypeId);
        dest.writeString(AddressCode);
        dest.writeString(SurveyNumber);
        dest.writeString(AdangalNumber);
        dest.writeDouble(LeftOutArea);
        dest.writeInt(LeftOutAreaCropId);
        dest.writeInt(PlotOwnerShipTypeId);
        dest.writeInt(IsPlotHandledByCareTaker);
        dest.writeString(CareTakerName);
        dest.writeString(CareTakerContactNumber);
        dest.writeInt(CreatedByUserId);
        dest.writeString(CreatedDate);
        dest.writeInt(UpdatedByUserId);
        dest.writeString(UpdatedDate);
        dest.writeInt(ServerUpdatedStatus);
        dest.writeInt(IsActive);
        dest.writeInt(IsBoundryFencing);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Plot> CREATOR = new Parcelable.Creator<Plot>() {
        @Override
        public Plot createFromParcel(Parcel in) {
            return new Plot(in);
        }

        @Override
        public Plot[] newArray(int size) {
            return new Plot[size];
        }
    };

    public Double getSuitablePalmOilArea() {
        return SuitablePalmOilArea;
    }

    public void setSuitablePalmOilArea(Double suitablePalmOilArea) {
        SuitablePalmOilArea = suitablePalmOilArea;
    }

    public String getDateofPlanting() {
        return DateofPlanting;
    }

    public void setDateofPlanting(String dateofPlanting) {
        DateofPlanting = dateofPlanting;
    }

    public Integer getSwapingReasonId() {
        return SwapingReasonId;
    }

    public void setSwapingReasonId(Integer swapingReasonId) {
        SwapingReasonId = swapingReasonId;
    }
}
