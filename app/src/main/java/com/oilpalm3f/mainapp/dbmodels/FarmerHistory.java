package com.oilpalm3f.mainapp.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

public class FarmerHistory {
    private String FarmerCode;
    private String PlotCode;
    private Integer StatusTypeId;
    private int IsActive;
    private int CreatedByUserId;
    private String CreatedDate;
    private String UpdatedDate;
    private int UpdatedByUserId;
    private int ServerUpdatedStatus;


    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }



    public String getFarmercode(){
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode){
        this.FarmerCode=FarmerCode;
    }

    public String getPlotcode(){
        return PlotCode;
    }

    public void setPlotcode(String PlotCode){
        this.PlotCode=PlotCode;
    }

    public Integer getStatustypeid(){
        return StatusTypeId;
    }

    public void setStatustypeid(Integer StatusTypeId){
        this.StatusTypeId=StatusTypeId;
    }

    public int getIsactive(){
        return IsActive;
    }

    public void setIsactive(int IsActive){
        this.IsActive=IsActive;
    }

    public int getCreatedbyuserid(){
        return CreatedByUserId;
    }

    public void setCreatedbyuserid(int CreatedByUserId){
        this.CreatedByUserId=CreatedByUserId;
    }

    public String getCreateddate(){
        return CreatedDate;
    }

    public void setCreateddate(String CreatedDate){
        this.CreatedDate=CreatedDate;
    }

    public int getUpdatedbyuserid(){
        return UpdatedByUserId;
    }

    public void setUpdatedbyuserid(int UpdatedByUserId){
        this.UpdatedByUserId=UpdatedByUserId;
    }

    public String getUpdateddate(){
        return UpdatedDate;
    }

    public void setUpdateddate(String UpdatedDate){
        this.UpdatedDate=UpdatedDate;
    }
}

