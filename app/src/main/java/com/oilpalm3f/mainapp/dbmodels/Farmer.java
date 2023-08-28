package com.oilpalm3f.mainapp.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Farmer implements Parcelable {
    private String Email;
    private String DOB;
    private String Code;
    private int Age;
    @Expose(serialize = true, deserialize = false)
    private int Id;
    private int CountryId;
    private int RegionId;
    private int StateId;
    private int DistrictId;
    private int MandalId;
    private int VillageId;
    private int SourceOfContactTypeId;
    private int TitleTypeId;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String GuardianName;
    private String MotherName;
    private int GenderTypeId;
    private String ContactNumber;
    private String MobileNumber;
    private int CategoryTypeId;
    private int AnnualIncomeTypeId;
    private String AddressCode;
    private int EducationTypeId;
    private int IsActive;
    private int CreatedByUserId;
    private String CreatedDate;

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    private String UpdatedDate;
    private int UpdatedByUserId;
    private int ServerUpdatedStatus;

    public String getEmail(){
        return Email;
    }

    public void setEmail(String Email){
        this.Email=Email;
    }

    public String getDOB(){
        return DOB;
    }

    public void setDOB(String DOB){
        this.DOB=DOB;
    }

    public String getCode(){
        return Code;
    }

    public void setCode(String Code){
        this.Code=Code;
    }

    public int getAge(){
        return Age;
    }

    public void setAge(int Age){
        this.Age=Age;
    }

    public int getCountryid(){
        return CountryId;
    }

    public void setCountryid(int CountryId){
        this.CountryId=CountryId;
    }

    public int getRegionid(){
        return RegionId;
    }

    public void setRegionid(int RegionId){
        this.RegionId=RegionId;
    }

    public int getStateid(){
        return StateId;
    }

    public void setStateid(int StateId){
        this.StateId=StateId;
    }

    public int getDistictid(){
        return DistrictId;
    }

    public void setDistictid(int DistictId){
        this.DistrictId =DistictId;
    }

    public int getMandalid(){
        return MandalId;
    }

    public void setMandalid(int MandalId){
        this.MandalId=MandalId;
    }

    public int getVillageid(){
        return VillageId;
    }

    public void setVillageid(int VillageId){
        this.VillageId=VillageId;
    }

    public int getSourceofcontacttypeid(){
        return SourceOfContactTypeId;
    }

    public void setSourceofcontacttypeid(int SourceOfContactTypeId){
        this.SourceOfContactTypeId=SourceOfContactTypeId;
    }

    public int getTitletypeid(){
        return TitleTypeId;
    }

    public void setTitletypeid(int TitleTypeId){
        this.TitleTypeId=TitleTypeId;
    }

    public String getFirstname(){
        return FirstName;
    }

    public void setFirstname(String FirstName){
        this.FirstName=FirstName;
    }

    public String getMiddlename(){
        return MiddleName;
    }

    public void setMiddlename(String MiddleName){
        this.MiddleName=MiddleName;
    }

    public String getLastname(){
        return LastName;
    }

    public void setLastname(String LastName){
        this.LastName=LastName;
    }

    public String getGuardianname(){
        return GuardianName;
    }

    public void setGuardianname(String GuardianName){
        this.GuardianName=GuardianName;
    }

    public String getMothername(){
        return MotherName;
    }

    public void setMothername(String MotherName){
        this.MotherName=MotherName;
    }

    public int getGendertypeid(){
        return GenderTypeId;
    }

    public void setGendertypeid(int GenderTypeId){
        this.GenderTypeId=GenderTypeId;
    }

    public String getContactnumber(){
        return ContactNumber;
    }

    public void setContactnumber(String ContactNumber){
        this.ContactNumber=ContactNumber;
    }

    public String getMobilenumber(){
        return MobileNumber;
    }

    public void setMobilenumber(String MobileNumber){
        this.MobileNumber=MobileNumber;
    }

    public int getCategorytypeid(){
        return CategoryTypeId;
    }

    public void setCategorytypeid(int CategoryTypeId){
        this.CategoryTypeId=CategoryTypeId;
    }

    public int getAnnualincometypeid(){
        return AnnualIncomeTypeId;
    }

    public void setAnnualincometypeid(int AnnualIncomeTypeId){
        this.AnnualIncomeTypeId=AnnualIncomeTypeId;
    }

    public String getAddresscode(){
        return AddressCode;
    }

    public void setAddresscode(String AddressCode){
        this.AddressCode=AddressCode;
    }

    public int getEducationtypeid(){
        return EducationTypeId;
    }

    public void setEducationtypeid(int EducationTypeId){
        this.EducationTypeId=EducationTypeId;
    }

    public int getIsactive() {
        return IsActive;
    }

    public void setIsactive(int IsActive) {
        this.IsActive = IsActive;
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

    public int getServerupdatedstatus(){
        return ServerUpdatedStatus;
    }

    public void setServerupdatedstatus(int ServerUpdatedStatus){
        this.ServerUpdatedStatus=ServerUpdatedStatus;
    }

    protected Farmer(Parcel in) {
        Email = in.readString();
        DOB = in.readString();
        Code = in.readString();
        Age = in.readInt();
        CountryId = in.readInt();
        RegionId = in.readInt();
        StateId = in.readInt();
        DistrictId = in.readInt();
        MandalId = in.readInt();
        VillageId = in.readInt();
        SourceOfContactTypeId = in.readInt();
        TitleTypeId = in.readInt();
        FirstName = in.readString();
        MiddleName = in.readString();
        LastName = in.readString();
        GuardianName = in.readString();
        MotherName = in.readString();
        GenderTypeId = in.readInt();
        ContactNumber = in.readString();
        MobileNumber = in.readString();
        CategoryTypeId = in.readInt();
        AnnualIncomeTypeId = in.readInt();
        AddressCode = in.readString();
        EducationTypeId = in.readInt();
        IsActive = in.readInt();
        CreatedByUserId = in.readInt();
        CreatedDate = in.readString();
        UpdatedByUserId = in.readInt();
        ServerUpdatedStatus = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Email);
        dest.writeString(DOB);
        dest.writeString(Code);
        dest.writeInt(Age);
        dest.writeInt(CountryId);
        dest.writeInt(RegionId);
        dest.writeInt(StateId);
        dest.writeInt(DistrictId);
        dest.writeInt(MandalId);
        dest.writeInt(VillageId);
        dest.writeInt(SourceOfContactTypeId);
        dest.writeInt(TitleTypeId);
        dest.writeString(FirstName);
        dest.writeString(MiddleName);
        dest.writeString(LastName);
        dest.writeString(GuardianName);
        dest.writeString(MotherName);
        dest.writeInt(GenderTypeId);
        dest.writeString(ContactNumber);
        dest.writeString(MobileNumber);
        dest.writeInt(CategoryTypeId);
        dest.writeInt(AnnualIncomeTypeId);
        dest.writeString(AddressCode);
        dest.writeInt(EducationTypeId);
        dest.writeInt(IsActive);
        dest.writeInt(CreatedByUserId);
        dest.writeString(CreatedDate);
        dest.writeInt(UpdatedByUserId);
        dest.writeInt(ServerUpdatedStatus);
    }

    public Farmer() {

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Farmer> CREATOR = new Parcelable.Creator<Farmer>() {
        @Override
        public Farmer createFromParcel(Parcel in) {
            return new Farmer(in);
        }

        @Override
        public Farmer[] newArray(int size) {
            return new Farmer[size];
        }
    };
}
