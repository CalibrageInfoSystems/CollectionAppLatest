package com.oilpalm3f.mainapp.dbmodels;

public class UserSync {
    private Integer Id;
    private Integer UserId;
    private Integer MasterSync;
    private Integer TransactionSync;
    private Integer ResetData;
    private Integer IsActive;
    private Integer CreatedByUserId;
    private Integer UpdatedByUserId;
    private Integer ServerUpdatedStatus;
    private String App,Date,CreatedDate,UpdatedDate;


    public Integer getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(Integer serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }


    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer userId) {
        UserId = userId;
    }

    public Integer getMasterSync() {
        return MasterSync;
    }

    public void setMasterSync(Integer masterSync) {
        MasterSync = masterSync;
    }

    public Integer getTransactionSync() {
        return TransactionSync;
    }

    public void setTransactionSync(Integer transactionSync) {
        TransactionSync = transactionSync;
    }

    public Integer getResetData() {
        return ResetData;
    }

    public void setResetData(Integer resetData) {
        ResetData = resetData;
    }

    public Integer getIsActive() {
        return IsActive;
    }

    public void setIsActive(Integer isActive) {
        IsActive = isActive;
    }

    public Integer getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public Integer getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(Integer updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getApp() {
        return App;
    }

    public void setApp(String app) {
        App = app;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
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
}
