package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

/**
 * Created by skasam on 2/7/2017.
 */
public class ConsignmentReportModel {

    private String Code;
    private String RecieptGeneratedDate;
    private String MillCode;
    private String TotalWeight;
    private String VehicleNumber;
    private String OperatorName;
    private String InchargeName;
    private String receiptCode;
    private String selectedCollectionCenterName;
    private String driverName;
    private String millName;
    private String CollectionCenterCode;
    private int sizeOfTruck;
    private String TransportCost;
    private String SharingCost;
    private String  OverWeightCost;
    private String ExpectedCost;
    private String ActualCost;
    private String createdDate;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getRecieptGeneratedDate() {
        return RecieptGeneratedDate;
    }

    public void setRecieptGeneratedDate(String recieptGeneratedDate) {
        RecieptGeneratedDate = recieptGeneratedDate;
    }

    public String getMillCode() {
        return MillCode;
    }

    public void setMillCode(String millCode) {
        MillCode = millCode;
    }

    public String getTotalWeight() {
        return TotalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        TotalWeight = totalWeight;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getInchargeName() {
        return InchargeName;
    }

    public void setInchargeName(String inchargeName) {
        InchargeName = inchargeName;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getSelectedCollectionCenterName() {
        return selectedCollectionCenterName;
    }

    public void setSelectedCollectionCenterName(String selectedCollectionCenterName) {
        this.selectedCollectionCenterName = selectedCollectionCenterName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getMillName() {
        return millName;
    }

    public void setMillName(String millName) {
        this.millName = millName;
    }

    public String getCollectionCenterCode() {
        return CollectionCenterCode;
    }

    public void setCollectionCenterCode(String collectionCenterCode) {
        CollectionCenterCode = collectionCenterCode;
    }

    public int getSizeOfTruck() {
        return sizeOfTruck;
    }

    public void setSizeOfTruck(int sizeOfTruck) {
        this.sizeOfTruck = sizeOfTruck;
    }

    public String getTransportCost() {
        return TransportCost;
    }

    public void setTransportCost(String transportCost) {
        TransportCost = transportCost;
    }

    public String getSharingCost() {
        return SharingCost;
    }

    public void setSharingCost(String sharingCost) {
        SharingCost = sharingCost;
    }

    public String getOverWeightCost() {
        return OverWeightCost;
    }

    public void setOverWeightCost(String overWeightCost) {
        OverWeightCost = overWeightCost;
    }

    public String getExpectedCost() {
        return ExpectedCost;
    }

    public void setExpectedCost(String expectedCost) {
        ExpectedCost = expectedCost;
    }

    public String getActualCost() {
        return ActualCost;
    }

    public void setActualCost(String actualCost) {
        ActualCost = actualCost;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
