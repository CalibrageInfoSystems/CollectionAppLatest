package com.oilpalm3f.mainapp.dbmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Siva on 26/10/16.
 */
public class ImageDetails {

    @SerializedName("TableName")
    @Expose
    private String TableName;
    @SerializedName("FarmerCode")
    @Expose
    private String FarmerCode;
    @SerializedName("ImageString")
    @Expose
    private String ImageString;
    @SerializedName("CollectionCode")
    @Expose
    private String CollectionCode;
    @SerializedName("PlotCode")
    @Expose
    private String PlotCode;

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public String getFarmerCode() {
        return FarmerCode;
    }

    public void setFarmerCode(String farmerCode) {
        FarmerCode = farmerCode;
    }

    public String getImageString() {
        return ImageString;
    }

    public void setImageString(String imageString) {
        ImageString = imageString;
    }

    public String getCollectionCode() {
        return CollectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        CollectionCode = collectionCode;
    }

    public String getPlotCode() {
        return PlotCode;
    }

    public void setPlotCode(String plotCode) {
        PlotCode = plotCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
