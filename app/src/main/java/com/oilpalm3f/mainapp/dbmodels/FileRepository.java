package com.oilpalm3f.mainapp.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siva on 25/02/17.
 */

public class FileRepository implements Parcelable {

    private String FarmerCode;
    private String PlotCode;
    private int ModuleTypeId;
    private String FileName;
    private String FileLocation;
    private String FileExtension;
    private int CreatedByUserId;
    private int UpdatedByUserId;
    private boolean ServerUpdatedStatus;

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

    public int getModuletypeid(){
        return ModuleTypeId;
    }

    public void setModuletypeid(int ModuleTypeId){
        this.ModuleTypeId=ModuleTypeId;
    }

    public String getFilename(){
        return FileName;
    }

    public void setFilename(String FileName){
        this.FileName=FileName;
    }

    public String getPicturelocation(){
        return FileLocation;
    }

    public void setPicturelocation(String PictureLocation){
        this.FileLocation =PictureLocation;
    }

    public String getFileextension(){
        return FileExtension;
    }

    public void setFileextension(String FileExtension){
        this.FileExtension=FileExtension;
    }

    public int getCreatedbyuserid(){
        return CreatedByUserId;
    }

    public void setCreatedbyuserid(int CreatedByUserId){
        this.CreatedByUserId=CreatedByUserId;
    }

    public int getUpdatedbyuserid(){
        return UpdatedByUserId;
    }

    public void setUpdatedbyuserid(int UpdatedByUserId){
        this.UpdatedByUserId=UpdatedByUserId;
    }

    public boolean getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(boolean serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    protected FileRepository(Parcel in) {
        FarmerCode = in.readString();
        PlotCode = in.readString();
        ModuleTypeId = in.readInt();
        FileName = in.readString();
        FileLocation = in.readString();
        FileExtension = in.readString();
        CreatedByUserId = in.readInt();
        UpdatedByUserId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FarmerCode);
        dest.writeString(PlotCode);
        dest.writeInt(ModuleTypeId);
        dest.writeString(FileName);
        dest.writeString(FileLocation);
        dest.writeString(FileExtension);
        dest.writeInt(CreatedByUserId);
        dest.writeInt(UpdatedByUserId);
    }

    public FileRepository() {

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FileRepository> CREATOR = new Parcelable.Creator<FileRepository>() {
        @Override
        public FileRepository createFromParcel(Parcel in) {
            return new FileRepository(in);
        }

        @Override
        public FileRepository[] newArray(int size) {
            return new FileRepository[size];
        }
    };
}
