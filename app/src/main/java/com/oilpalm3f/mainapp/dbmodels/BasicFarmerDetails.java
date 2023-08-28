package com.oilpalm3f.mainapp.dbmodels;

import java.io.Serializable;

/**
 * Created by skasam on 9/27/2016.
 */
public class BasicFarmerDetails implements Serializable {

    private String farmerCode;
    private String farmerFirstName;
    private String farmerLastName;
    private String farmerMiddleName = "";
    private String farmerDOB;
    private String farmerGender;
    private String farmerMotherName;
    private String farmerFatherName;
    private String farmerStateName;
    private String farmerDistrictName;
    private String farmerMandalName;
    private byte[] farmerPhoto;
    private String farmerActive;
    private String farmerVillageName;
    private String plotCode;
    private String villageName;
    private String surveyNumber;
    private String adangalorFiledNo;
    private String totalArea;
    private String totalPalm;
    private String areaLeftOut;
    private String mouNo;
    private String mouDate;
    private String gpsPlotArea;
    private String plotDifference;
    private String primaryContactNum;
    private String secondaryContactNum;
    private String address1;
    private String address2;
    private String landmark;
    private String villageCode;
    private String villageId;
    private String stateCode;
    private String stateId;
    private String mandalCode;
    private String bankAccountNumber;
    private String photoLocation;
    private String photoName;
    private String FileExtension;
    private String branchName;
    private String bankName;

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    private String districtCode;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getFarmerVillageName() {
        return farmerVillageName;
    }

    public void setFarmerVillageName(String farmerVillageName) {
        this.farmerVillageName = farmerVillageName;
    }

    public String getPrimaryContactNum() {
        return primaryContactNum;
    }

    public void setPrimaryContactNum(String primaryContactNum) {
        this.primaryContactNum = primaryContactNum;
    }

    public String getSecondaryContactNum() {
        return secondaryContactNum;
    }

    public void setSecondaryContactNum(String secondaryContactNum) {
        this.secondaryContactNum = secondaryContactNum;
    }

    public String getFarmerCode() {
        return farmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        this.farmerCode = farmerCode;
    }

    public String getFarmerFirstName() {
        return farmerFirstName;
    }

    public void setFarmerFirstName(String farmerFirstName) {
        this.farmerFirstName = farmerFirstName;
    }

    public String getFarmerLastName() {
        return farmerLastName;
    }

    public void setFarmerLastName(String farmerLastName) {
        this.farmerLastName = farmerLastName;
    }

    public String getFarmerMiddleName() {
        return farmerMiddleName;
    }

    public void setFarmerMiddleName(String farmerMiddleName) {
        this.farmerMiddleName = farmerMiddleName;
    }

    public String getFarmerDOB() {
        return farmerDOB;
    }

    public void setFarmerDOB(String farmerDOB) {
        this.farmerDOB = farmerDOB;
    }

    public String getFarmerGender() {
        return farmerGender;
    }

    public void setFarmerGender(String farmerGender) {
        this.farmerGender = farmerGender;
    }

    public String getFarmerMotherName() {
        return farmerMotherName;
    }

    public void setFarmerMotherName(String farmerMotherName) {
        this.farmerMotherName = farmerMotherName;
    }

    public String getFarmerFatherName() {
        return farmerFatherName;
    }

    public void setFarmerFatherName(String farmerFatherName) {
        this.farmerFatherName = farmerFatherName;
    }

    public String getFarmerStateName() {
        return farmerStateName;
    }

    public void setFarmerStateName(String farmerStateName) {
        this.farmerStateName = farmerStateName;
    }

    public String getFarmerDistrictName() {
        return farmerDistrictName;
    }

    public void setFarmerDistrictName(String farmerDistrictName) {
        this.farmerDistrictName = farmerDistrictName;
    }

    public String getFarmerMandalName() {
        return farmerMandalName;
    }

    public void setFarmerMandalName(String farmerMandalName) {
        this.farmerMandalName = farmerMandalName;
    }

    public byte[] getFarmerPhoto() {
        return farmerPhoto;
    }

    public void setFarmerPhoto(byte[] farmerPhoto) {
        this.farmerPhoto = farmerPhoto;
    }

    public String getFarmerActive() {
        return farmerActive;
    }

    public void setFarmerActive(String farmerActive) {
        this.farmerActive = farmerActive;
    }

    public String getPlotCode() {
        return plotCode;
    }

    public void setPlotCode(String plotCode) {
        this.plotCode = plotCode;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getSurveyNumber() {
        return surveyNumber;
    }

    public void setSurveyNumber(String surveyNumber) {
        this.surveyNumber = surveyNumber;
    }

    public String getAdangalorFiledNo() {
        return adangalorFiledNo;
    }

    public void setAdangalorFiledNo(String adangalorFiledNo) {
        this.adangalorFiledNo = adangalorFiledNo;
    }

    public String getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(String totalArea) {
        this.totalArea = totalArea;
    }

    public String getTotalPalm() {
        return totalPalm;
    }

    public void setTotalPalm(String totalPalm) {
        this.totalPalm = totalPalm;
    }

    public String getAreaLeftOut() {
        return areaLeftOut;
    }

    public void setAreaLeftOut(String areaLeftOut) {
        this.areaLeftOut = areaLeftOut;
    }

    public String getMouNo() {
        return mouNo;
    }

    public void setMouNo(String mouNo) {
        this.mouNo = mouNo;
    }

    public String getMouDate() {
        return mouDate;
    }

    public void setMouDate(String mouDate) {
        this.mouDate = mouDate;
    }

    public String getGpsPlotArea() {
        return gpsPlotArea;
    }

    public void setGpsPlotArea(String gpsPlotArea) {
        this.gpsPlotArea = gpsPlotArea;
    }

    public String getPlotDifference() {
        return plotDifference;
    }

    public void setPlotDifference(String plotDifference) {
        this.plotDifference = plotDifference;
    }

    public String getVillageId() {
        return villageId;
    }

    public void setVillageId(String villageId) {
        this.villageId = villageId;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getMandalCode() {
        return mandalCode;
    }

    public void setMandalCode(String mandalCode) {
        this.mandalCode = mandalCode;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
