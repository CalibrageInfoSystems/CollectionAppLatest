package com.oilpalm3f.mainapp.dbmodels;



public class GraderAttendance {
   // private int id;
    private String graderCode;
    private String validDate;
    private int createdByUserId;
    private String createdDate;
    private boolean serverUpdatedStatus;

private String CCCode;
    public GraderAttendance () {
    }

    public GraderAttendance (String graderCode, String validDate, int createdByUserId, String createdDate, boolean serverUpdatedStatus) {
       // this.id = id;
        this.graderCode = graderCode;
        this.validDate = validDate;
        this.createdByUserId = createdByUserId;
        this.createdDate = createdDate;
        this.serverUpdatedStatus = serverUpdatedStatus;
    }

    // Getters and setters

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getGraderCode() {
        return graderCode;
    }

    public void setGraderCode(String graderCode) {
        this.graderCode = graderCode;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }

    public String getCCCode() {
        return CCCode;
    }

    public void setCCCode(String CCCode) {
        this.CCCode = CCCode;
    }
}