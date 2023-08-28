package com.oilpalm3f.mainapp.weighbridge;

import android.os.Parcel;
import android.os.Parcelable;

//Collection Center Model
public class CollectionCenter1 implements Parcelable {
    public static final Creator<CollectionCenter1> CREATOR = new Creator<CollectionCenter1>() {
        @Override
        public CollectionCenter1 createFromParcel(Parcel in) {
            return new CollectionCenter1(in);
        }

        @Override
        public CollectionCenter1[] newArray(int size) {
            return new CollectionCenter1[size];
        }
    };

    String CollId,WeighbridgeCentre,dateAndTime,vechileNo,driverName,nameOfOperator,comments,postingDate,noOfBunches,bunchesRejected,bunchesAccepted,
            remarks,nameOfGarder;
    String wbId;
    Integer TokenNo;
    Float Trpt;
    String ReadMethod;

    public String getReadMethod() {
        return ReadMethod;
    }

    public void setReadMethod(String readMethod) {
        ReadMethod = readMethod;
    }

    public CollectionCenter1() {

    }

    public CollectionCenter1(Parcel in) {
        CollId = in.readString();
        WeighbridgeCentre = in.readString();
        dateAndTime = in.readString();
        vechileNo = in.readString();
        driverName = in.readString();
        nameOfOperator = in.readString();
        comments = in.readString();
        postingDate = in.readString();
        noOfBunches = in.readString();
        bunchesRejected = in.readString();
        bunchesAccepted = in.readString();
        remarks = in.readString();
        nameOfGarder = in.readString();
        wbId = in.readString();
        TokenNo=in.readInt();
        Trpt=in.readFloat();
        ReadMethod=in.readString();
    }


    public Integer getTokenNo() {
        return TokenNo;
    }

    public void setTokenNo(Integer tokenNo) {
        TokenNo = tokenNo;
    }

    public Float getTrpt() {
        return Trpt;
    }

    public void setTrpt(Float trpt) {
        Trpt = trpt;
    }

    public String getWbId() {
        return wbId;
    }

    public void setWbId(String wbId) {
        this.wbId = wbId;
    }

    public String getCollId() {
        return CollId;
    }

    public void setCollId(String collId) {
        CollId = collId;
    }

    public String getWeighbridgeCentre() {
        return WeighbridgeCentre;
    }

    public void setWeighbridgeCentre(String weighbridgeCentre) {
        WeighbridgeCentre = weighbridgeCentre;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getVechileNo() {
        return vechileNo;
    }

    public void setVechileNo(String vechileNo) {
        this.vechileNo = vechileNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getNameOfOperator() {
        return nameOfOperator;
    }

    public void setNameOfOperator(String nameOfOperator) {
        this.nameOfOperator = nameOfOperator;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getNoOfBunches() {
        return noOfBunches;
    }

    public void setNoOfBunches(String noOfBunches) {
        this.noOfBunches = noOfBunches;
    }

    public String getBunchesRejected() {
        return bunchesRejected;
    }

    public void setBunchesRejected(String bunchesRejected) {
        this.bunchesRejected = bunchesRejected;
    }

    public String getBunchesAccepted() {
        return bunchesAccepted;
    }

    public void setBunchesAccepted(String bunchesAccepted) {
        this.bunchesAccepted = bunchesAccepted;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNameOfGarder() {
        return nameOfGarder;
    }

    public void setNameOfGarder(String nameOfGarder) {
        this.nameOfGarder = nameOfGarder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CollId);
        dest.writeString(WeighbridgeCentre);
        dest.writeString(dateAndTime);
        dest.writeString(vechileNo);
        dest.writeString(driverName);
        dest.writeString(nameOfOperator);
        dest.writeString(comments);
        dest.writeString(postingDate);
        dest.writeString(noOfBunches);
        dest.writeString(bunchesRejected);
        dest.writeString(bunchesAccepted);
        dest.writeString(remarks);
        dest.writeString(nameOfGarder);
        dest.writeString(wbId);
        dest.writeInt(TokenNo);
        dest.writeFloat(Trpt);
        dest.writeString(ReadMethod);
    }
}
