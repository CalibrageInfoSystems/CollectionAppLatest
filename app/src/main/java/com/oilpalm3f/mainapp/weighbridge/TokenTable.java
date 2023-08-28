package com.oilpalm3f.mainapp.weighbridge;

//Token Table Model
public class TokenTable {
    Integer Id,TokenNo,CreatedByUserId,UpdatedByUserId,WeighbridgeId;
    String CollId,WeighbridgeName,WeighingDate,VehicleNumber,DriverName,OperatorName,Comments,PostingDate,CreatedDate,UpdatedDate,
            CollectionCenterCode;
    Float GrossWeight;
    private int VehicleTypeId;

    public int getVehicleTypeId() {
        return VehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        VehicleTypeId = vehicleTypeId;
    }

    public String getCollectionCenterCode() {
        return CollectionCenterCode;
    }

    public Integer getWeighbridgeId() {
        return WeighbridgeId;
    }

    public void setWeighbridgeId(Integer weighbridgeId) {
        WeighbridgeId = weighbridgeId;
    }

    public void setCollectionCenterCode(String collectionCenterCode) {
        CollectionCenterCode = collectionCenterCode;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getTokenNo() {
        return TokenNo;
    }

    public void setTokenNo(Integer tokenNo) {
        TokenNo = tokenNo;
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

    public String getCollId() {
        return CollId;
    }

    public void setCollId(String collId) {
        CollId = collId;
    }

    public String getWeighbridgeName() {
        return WeighbridgeName;
    }

    public void setWeighbridgeName(String weighbridgeName) {
        WeighbridgeName = weighbridgeName;
    }

    public String getWeighingDate() {
        return WeighingDate;
    }

    public void setWeighingDate(String weighingDate) {
        WeighingDate = weighingDate;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String postingDate) {
        PostingDate = postingDate;
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

    public Float getGrossWeight() {
        return GrossWeight;
    }

    public void setGrossWeight(Float grossWeight) {
        GrossWeight = grossWeight;
    }
}
