package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

import com.google.gson.annotations.Expose;

public class ConsignmentRepository {

    @Expose
    private String ConsignmentCode;
    @Expose
    private int ModuleTypeId;
    @Expose
    private  String FileName;
    @Expose
    private  String FileLocation;
    @Expose
    private  String FileExtension;

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
    private boolean ServerUpdatedStatus;

    public ConsignmentRepository(){

    }


    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public int getModuleTypeId() {
        return ModuleTypeId;
    }

    public void setModuleTypeId(int moduleTypeId) {
        ModuleTypeId = moduleTypeId;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileLocation() {
        return FileLocation;
    }

    public void setFileLocation(String fileLocation) {
        FileLocation = fileLocation;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public boolean isServerUpdatedStatus() {
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
}
