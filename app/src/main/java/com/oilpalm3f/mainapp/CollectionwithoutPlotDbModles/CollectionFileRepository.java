package com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BaliReddy on 16/03/2018.
 */

public class CollectionFileRepository implements Parcelable {

    private String FarmerCode;
    private int ModuleTypeId;
    private String FileName;
    private String FileLocation;
    private String FileExtension;
    private int IsActive;
    private int CreatedByUserId;
    private String  CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
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

    public String getFarmercode(){
        return FarmerCode;
    }

    public void setFarmercode(String FarmerCode){
        this.FarmerCode=FarmerCode;
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

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    protected CollectionFileRepository(Parcel in) {
        FarmerCode = in.readString();
        ModuleTypeId = in.readInt();
        FileName = in.readString();
        FileLocation = in.readString();
        FileExtension = in.readString();
        IsActive = in.readInt();
        CreatedByUserId = in.readInt();
        CreatedDate = in.readString();
        UpdatedByUserId = in.readInt();
        UpdatedDate = in.readString();
        ServerUpdatedStatus = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FarmerCode);
        dest.writeInt(ModuleTypeId);
        dest.writeString(FileName);
        dest.writeString(FileLocation);
        dest.writeString(FileExtension);
        dest.writeInt(IsActive);
        dest.writeInt(CreatedByUserId);
        dest.writeString(CreatedDate);
        dest.writeInt(UpdatedByUserId);
        dest.writeString(UpdatedDate);
        dest.writeInt(ServerUpdatedStatus);
    }

    public CollectionFileRepository() {

    }

    @SuppressWarnings("unused")
    public static final Creator<CollectionFileRepository> CREATOR = new Creator<CollectionFileRepository>() {
        @Override
        public CollectionFileRepository createFromParcel(Parcel in) {
            return new CollectionFileRepository(in);
        }

        @Override
        public CollectionFileRepository[] newArray(int size) {
            return new CollectionFileRepository[size];
        }
    };
}
