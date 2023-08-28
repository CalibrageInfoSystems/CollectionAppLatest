package com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles;

public class CollectionFarmerBank {
    private String FarmerCode;
    private int BankId;
    private String AccountHolderName;
    private String AccountNumber;
    private String FileName;
    private String FileLocation;
    private String FileExtension;
    private int IsActive;
    private int CreatedByUserId;
    private String  CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;

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

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public String getFarmercode() {
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode) {
        this.FarmerCode = FarmerCode;
    }

    public int getBankid() {
        return BankId;
    }

    public void setBankid(int BankId) {
        this.BankId = BankId;
    }

    public String getAccountholdername() {
        return AccountHolderName;
    }

    public void setAccountholdername(String AccountHolderName) {
        this.AccountHolderName = AccountHolderName;
    }

    public String getAccountnumber() {
        return AccountNumber;
    }

    public void setAccountnumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public String getFilename() {
        return FileName;
    }

    public void setFilename(String FileName) {
        this.FileName = FileName;
    }

    public String getFilelocation() {
        return FileLocation;
    }

    public void setFilelocation(String FileLocation) {
        this.FileLocation = FileLocation;
    }

    public String getFileextension() {
        return FileExtension;
    }

    public void setFileextension(String FileExtension) {
        this.FileExtension = FileExtension;
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
}