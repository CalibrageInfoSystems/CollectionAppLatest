package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

public class CollectionPlotXref {
    private String PlotCode;
    private String CollectionCode;
    private boolean ServerUpdatedStatus;
    private boolean IsMainFarmerPlot;

    public boolean isMainFarmerPlot() {
        return IsMainFarmerPlot;
    }

    public void setMainFarmerPlot(boolean mainFarmerPlot) {
        IsMainFarmerPlot = mainFarmerPlot;
    }

    public Float getYieldPerHectar() {
        return YieldPerHectar;
    }

    public void setYieldPerHectar(Float yieldPerHectar) {
        YieldPerHectar = yieldPerHectar;
    }

    private Float YieldPerHectar;
    private Float NetWeightPerPlot;

    public Float getNetWeightPerPlot() {
        return NetWeightPerPlot;
    }

    public void setNetWeightPerPlot(Float netWeightPerPlot) {
        NetWeightPerPlot = netWeightPerPlot;
    }

    public boolean getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public String getCollectionCode() {
        return CollectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        CollectionCode = collectionCode;
    }

    public String getPlotcode(){
        return PlotCode;
    }

    public void setPlotcode(String PlotCode){
        this.PlotCode=PlotCode;
    }
}
