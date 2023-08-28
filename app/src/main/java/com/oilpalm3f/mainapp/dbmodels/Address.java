package com.oilpalm3f.mainapp.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siva on 12/02/17.
 */

public class Address implements Parcelable {
    private String Code;
    private String AddressLine1;
    private String AddressLine2;
    private String AddressLine3;
    private String Landmark;
    private int VillageId;
    private int MandalId;
    private int DistrictId;
    private int StateId;
    private int CountryId;
    private int PinCode;
    private int IsActive;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;


    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getAddressline1() {
        return AddressLine1;
    }

    public void setAddressline1(String AddressLine1) {
        this.AddressLine1 = AddressLine1;
    }

    public String getAddressline2() {
        return AddressLine2;
    }

    public void setAddressline2(String AddressLine2) {
        this.AddressLine2 = AddressLine2;
    }

    public String getAddressline3() {
        return AddressLine3;
    }

    public void setAddressline3(String AddressLine3) {
        this.AddressLine3 = AddressLine3;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String Landmark) {
        this.Landmark = Landmark;
    }

    public int getVillageid() {
        return VillageId;
    }

    public void setVillageid(int VillageId) {
        this.VillageId = VillageId;
    }

    public int getMandalid() {
        return MandalId;
    }

    public void setMandalid(int MandalId) {
        this.MandalId = MandalId;
    }

    public int getDistictid() {
        return DistrictId;
    }

    public void setDistictid(int DistictId) {
        this.DistrictId = DistictId;
    }

    public int getStateid() {
        return StateId;
    }

    public void setStateid(int StateId) {
        this.StateId = StateId;
    }

    public int getCountryid() {
        return CountryId;
    }

    public void setCountryid(int CountryId) {
        this.CountryId = CountryId;
    }

    public int getPincode() {
        return PinCode;
    }

    public void setPincode(int PinCode) {
        this.PinCode = PinCode;
    }

    public int getIsactive() {
        return IsActive;
    }

    public void setIsactive(int IsActive) {
        this.IsActive = IsActive;
    }

    public int getCreatedbyuserid() {
        return CreatedByUserId;
    }

    public void setCreatedbyuserid(int CreatedByUserId) {
        this.CreatedByUserId = CreatedByUserId;
    }

    public String getCreateddate() {
        return CreatedDate;
    }

    public void setCreateddate(String CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public int getUpdatedbyuserid() {
        return UpdatedByUserId;
    }

    public void setUpdatedbyuserid(int UpdatedByUserId) {
        this.UpdatedByUserId = UpdatedByUserId;
    }

    public String getUpdateddate() {
        return UpdatedDate;
    }

    public void setUpdateddate(String UpdatedDate) {
        this.UpdatedDate = UpdatedDate;
    }

    public int getServerupdatedstatus() {
        return ServerUpdatedStatus;
    }

    public void setServerupdatedstatus(int ServerUpdatedStatus) {
        this.ServerUpdatedStatus = ServerUpdatedStatus;
    }

    protected Address(Parcel in) {
        Code = in.readString();
        AddressLine1 = in.readString();
        AddressLine2 = in.readString();
        AddressLine3 = in.readString();
        Landmark = in.readString();
        VillageId = in.readInt();
        MandalId = in.readInt();
        DistrictId = in.readInt();
        StateId = in.readInt();
        CountryId = in.readInt();
        PinCode = in.readInt();
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
        dest.writeString(Code);
        dest.writeString(AddressLine1);
        dest.writeString(AddressLine2);
        dest.writeString(AddressLine3);
        dest.writeString(Landmark);
        dest.writeInt(VillageId);
        dest.writeInt(MandalId);
        dest.writeInt(DistrictId);
        dest.writeInt(StateId);
        dest.writeInt(CountryId);
        dest.writeInt(PinCode);
        dest.writeInt(IsActive);
        dest.writeInt(CreatedByUserId);
        dest.writeString(CreatedDate);
        dest.writeInt(UpdatedByUserId);
        dest.writeString(UpdatedDate);
        dest.writeInt(ServerUpdatedStatus);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public Address() {

    }
}