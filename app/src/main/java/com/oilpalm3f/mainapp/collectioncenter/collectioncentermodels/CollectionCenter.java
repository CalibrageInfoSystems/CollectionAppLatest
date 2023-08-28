package com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CollectionCenter implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CollectionCenter> CREATOR = new Parcelable.Creator<CollectionCenter>() {
        @Override
        public CollectionCenter createFromParcel(Parcel in) {
            return new CollectionCenter(in);
        }

        @Override
        public CollectionCenter[] newArray(int size) {
            return new CollectionCenter[size];
        }
    };
    private String Code;
    private  String CCId;
    private String Name;
    private int VillageId;
    private int MandalId;
    private int MandalName;
    private int DistrictId;
    private int DistrictName;
    private int StateId;
    private int CountryId;
    private int WeighBridgeTypeId;
    private String InchargeName;
    private String villageName;
    private String villageCode;
    private String weignBridgeType;
    private String CollectionType;
    private  String ReadMethod;
    private String NoOfChars;
    private String UpToCharacter;
    private List collectionCenterNames;
    private Double Distance;

    private Boolean IsFingerPrintReq;

    public Boolean getFingerPrintReq() {
        return IsFingerPrintReq;
    }

    public void setFingerPrintReq(Boolean fingerPrintReq) {
        IsFingerPrintReq = fingerPrintReq;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public CollectionCenter() {

    }

    public String getReadMethod() {
        return ReadMethod;
    }

    public void setReadMethod(String readMethod) {
        ReadMethod = readMethod;
    }

    public String getNoOfChars() {
        return NoOfChars;
    }

    public void setNoOfChars(String noOfChars) {
        NoOfChars = noOfChars;
    }

    public String getUpToCharacter() {
        return UpToCharacter;
    }

    public void setUpToCharacter(String upToCharacter) {
        UpToCharacter = upToCharacter;
    }

    protected CollectionCenter(Parcel in) {
        CCId = in.readString();
        Code = in.readString();
        Name = in.readString();
        VillageId = in.readInt();
        MandalId = in.readInt();
        DistrictId = in.readInt();
        StateId = in.readInt();
        CountryId = in.readInt();
        WeighBridgeTypeId = in.readInt();
        InchargeName = in.readString();
        villageName = in.readString();
        villageCode = in.readString();
        weignBridgeType = in.readString();
        if (in.readByte() == 0x01) {
            collectionCenterNames = new ArrayList<>();
            in.readList(collectionCenterNames, null);
        } else {
            collectionCenterNames = null;
        }
        CollectionType=in.readString();
        NoOfChars=in.readString();
        UpToCharacter=in.readString();
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCollectionType() {
        return CollectionType;
    }

    public void setCollectionType(String collectionType) {
        CollectionType = collectionType;
    }

    public String getName() {
        return Name;
    }

    public String getCCId() {
        return CCId;
    }

    public void setCCId(String CCId) {
        this.CCId = CCId;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getVillageId() {
        return VillageId;
    }

    public void setVillageId(int villageId) {
        VillageId = villageId;
    }

    public int getMandalId() {
        return MandalId;
    }

    public void setMandalId(int mandalId) {
        MandalId = mandalId;
    }

    public int getDistrictId() {
        return DistrictId;
    }

    public void setDistrictId(int districtId) {
        DistrictId = districtId;
    }

    public int getStateId() {
        return StateId;
    }

    public void setStateId(int stateId) {
        StateId = stateId;
    }

    public int getCountryId() {
        return CountryId;
    }

    public void setCountryId(int countryId) {
        CountryId = countryId;
    }

    public int getWeighBridgeTypeId() {
        return WeighBridgeTypeId;
    }

    public void setWeighBridgeTypeId(int weighBridgeTypeId) {
        WeighBridgeTypeId = weighBridgeTypeId;
    }

    public String getInchargeName() {
        return InchargeName;
    }

    public void setInchargeName(String inchargeName) {
        InchargeName = inchargeName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getWeignBridgeType() {
        return weignBridgeType;
    }

    public void setWeignBridgeType(String weignBridgeType) {
        this.weignBridgeType = weignBridgeType;
    }

    public List getCollectionCenterNames() {
        return collectionCenterNames;
    }

    public void setCollectionCenterNames(List collectionCenterNames) {
        this.collectionCenterNames = collectionCenterNames;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CCId);
        dest.writeString(Code);
        dest.writeString(Name);
        dest.writeInt(VillageId);
        dest.writeInt(MandalId);
        dest.writeInt(DistrictId);
        dest.writeInt(StateId);
        dest.writeInt(CountryId);
        dest.writeInt(WeighBridgeTypeId);
        dest.writeString(InchargeName);
        dest.writeString(villageName);
        dest.writeString(villageCode);
        dest.writeString(weignBridgeType);
        if (collectionCenterNames == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(collectionCenterNames);
        }
        dest.writeString(NoOfChars);
        dest.writeString(UpToCharacter);
    }
}
