package com.oilpalm3f.mainapp.dbmodels;

public class FileRepository1 {
    private String ConsignmentCode;

    private int ModuleTypeId;
    private int CreatedByUserId;
    private int UpdatedByUserId;
    private boolean ServerUpdatedStatus,IsActive;
    String CreatedDate,UpdatedDate,FileExtension,FileLocation,FileName;

    public String getFileName() {
        return FileName;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
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


    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }


    public int getModuleId() {
        return ModuleTypeId;
    }

    public void setModuleId(int moduleId) {
        ModuleTypeId = moduleId;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public String getFileLocation() {
        return FileLocation;
    }

    public void setFileLocation(String fileLocation) {
        FileLocation = fileLocation;
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

    public boolean isServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }
}
