package com.oilpalm3f.mainapp.dbmodels;

public class IdentityProof {
    private String FarmerCode;
    private int IDProofTypeId;
    private String IdProofNumber;
    private int CreatedByUserId;
    private int UpdatedByUserId;

    public String getFarmercode() {
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode) {
        this.FarmerCode = FarmerCode;
    }

    public int getIdprooftypeid() {
        return IDProofTypeId;
    }

    public void setIdprooftypeid(int IDProofTypeId) {
        this.IDProofTypeId = IDProofTypeId;
    }

    public String getIdproofnumber() {
        return IdProofNumber;
    }

    public void setIdproofnumber(String IdProofNumber) {
        this.IdProofNumber = IdProofNumber;
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