package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

public class Consignmentstatushistory {
    private String ConsignmentCode;
    private int StatusTypeId;
    private String OperatorName;
    private String Comments;
    private int CreatedByUserId;
    private int UpdatedByUserId;
    private String CreatedDate;
    private String UpdatedDate;
    private int IsActive;
    private boolean ServerUpdatedStatus;

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

    public String getConsignmentcode() {
        return ConsignmentCode;
    }

    public void setConsignmentcode(String ConsignmentCode) {
        this.ConsignmentCode = ConsignmentCode;
    }

    public int getStatustypeid() {
        return StatusTypeId;
    }

    public void setStatustypeid(int StatusTypeId) {
        this.StatusTypeId = StatusTypeId;
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
