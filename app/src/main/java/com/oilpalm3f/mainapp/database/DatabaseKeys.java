package com.oilpalm3f.mainapp.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siva on 24/09/16.
 */

//To define commonly used Database Strings

public class DatabaseKeys {

    public static List<String> transactionTables = new ArrayList<>();

    public static String TABLE_FORMER = "Farmer";
    public static String TABLE_FARMERHISTORY = "FarmerHistory";
    public static String TABLE_PLOT = "Plot";
    public static String TABLE_ADDRESS = "Address";
    public static String TABLE_FARMERBANK = "FarmerBank";
    public static String TABLE_FILEREPOSITORY = "FileRepository";
    public static String TABLE_IDENTITYPROOF = "IdentityProof";
    public static final String TABLE_VisitLog = "VisitLog";
    public static final String TABLE_Userync = "UserSync";

    public static final String LocationTracker = "LocationTracker";

    // Collection with out plot database tables
    public final static String TABLE_CollectionFarmer= "CollectionFarmer";
    public final static String TABLE_CollectionWithOutPlot = "CollectionWithOutPlot";
    public final static String TABLE_CollectionFarmerBank = "CollectionFarmerBank";
    public final static String TABLE_CollectionFileRepository = "CollectionFileRepository";
    public final static String TABLE_CollectionFarmerIdentityProof = "CollectionFarmerIdentityProof";

    // MasterVersionTrackingSystem
    public final static String TABLE_MASTERVERSIONTRACKINGSYSTEM = "MasterVersionTrackingSystem";
    public final static String COLUMN_USERID = "UserId";
    public final static String COLUMN_UPDATEDON = "UpdatedOn";


    // PlotBoundaries database table info
    public final static String TABLE_PLOTBOUNDARIES = "PlotBoundaries";
    public final static String COLUMN_PLOTCODE = "PlotCode";
    public final static String COLUMN_LATITUDE = "Latitude";
    public final static String COLUMN_LONGITUDE = "Longitude";
    public final static String COLUMN_CREATEDBY = "CreatedBy";
    public final static String COLUMN_CCREATEDDATE = "CreatedDate";
    public final static String COLUMN_UPDATEDBY = "UpdatedBy";
    public final static String COLUMN_CUPDATEDDATE = "UpdatedDate";
    //  public final static String COLUMN_SERVERUPDATEDDATE = "ServerUpdatedDate";

    //UprootMentDetails database table info
    public final static String TABLE_UPROOTMENTDETAILS = "UprootmentDetails";
    public final static String COLUMN_FARMERCODE = "FarmerCode";
    //    public final static String COLUMN_UPROOTMENTPLOTCODE = "PlotCode";
    public final static String COLUMN_SEEDINGSPLANTED = "SeedingsPlanted";
    public final static String COLUMN_TREESCURRENTLY = "TreesCurrently";
    public final static String COLUMN_UPROOTMENT = "Uprootment";
    //    public final static String COLUMN_UPROOTMENTID = "UprootmentId";
    public final static String COLUMN_UPROOTMENTREASONCODE = "UprootmentReasonCode";
    public final static String COLUMN_UPROOTMENTREASONNAME = "OtherReason";
    //FFB Harvest Details database info
    public final static String TABLE_FFBHARVESTDETAILS = "FFB_HarvestDetails";
    public final static String COLUMN_FFBHARVESTEID = "FFBHarvestId";
    //    public final static String COLUMN_HARVESTFARMERCODE = "FarmerCode";
//    public final static String COLUMN_HARVESTPLOTCODE = "PlotCode";
    public final static String COLUMN_COLLECTIONCENTERID = "CollectionCentreId";
    public final static String COLUMN_MODEOFTRANSPORT = "ModeOfTransport";
    public final static String COLUMN_HARVESTINGMETHOD = "HarvestingMethod";
    public final static String COLUMN_WAGESPERDAY = "WagesPerDay";
    public final static String COLUMN_CONTRACTRSPERMONTH = "ContractRsPerMonth";
    public final static String COLUMN_CONTRACTRSPERANNUM = "ContractRsPerAnum";
    public final static String COLUMN_TYPEOFHARVESTING = "TypeOfHarvesting";
    public final static String COLUMN_CONTRACTORPITCH = "ContractorPitch";
    public final static String COLUMN_FARMERCONSENT = "FarmerConsent";
    public final static String COLUMN_COMMENTSHARVESTING = "Comments";


    //HealthofPlantationDetails database info
    public final static String TABLE_HEALTHOFPLANTATIONDETAILS = "HealthofPlantationDetails";
    public final static String COLUMN_PLANTATIONDETAILSID = "PlantationDetailsId";
    public final static String COLUMN_APPEARANCEOFTREES = "AppearanceOfTrees";
    public final static String COLUMN_GROWTHOFTREE = "GrowthOfTree";
    public final static String COLUMN_HEIGHTOFTREE = "HeightOfTree";
    public final static String COLUMN_COLOROFFRUIT = "ColorOfFruit";
    public final static String COLUMN_SIZEOFFRUIT = "SizeOfFruit";
    public final static String COLUMN_PLAMHYGIENE = "PalmHygiene";
    public final static String COLUMN_TYPEOFPLANTATION = "TypeOfPlantation";
    public final static String COLUMN_COMMENTS = "Comments";
    public final static String COLUMN_PHOTO = "PicturePath";
    //    public final static String COLUMN_CREATEDBY = "CreatedBy";
//    public final static String COLUMN_CREATEDDATE = "CreatedDate";
//    public final static String COLUMN_UPDATEDBY = "UpdatedBy";
//    public final static String COLUMN_UPDATEDDATE = "UpdatedDate";
    //   public final static String COLUMN_HEALTHSERVERUPDATEDSTATUS = "ServerUpdatedStatus";

    //FarmerPersonalDetailsModel database table info

    public final static String TABLE_FARMERPERSONALDETAILS = "FarmerDetails";
    //   public final static String COLUMN_FARMERCODE = "FarmerCode";
    public final static String COLUMN_FARMERFIRSTNAME = "FarmerFirstName";
    public final static String COLUMN_FARMERMIDDLENAME = "FarmerMiddleName";
    public final static String COLUMN_FARMERLASTNAME = "FarmerLastName";
    public final static String COLUMN_FARMERDOB = "DOB";
    public final static String COLUMN_FARMERGENDER = "Gender";
    public final static String COLUMN_FARMERFATHERNAME = "FatherName";
    public final static String COLUMN_FARMERMOTHERNAME = "MotherName";
    public final static String COLUMN_FARMEREMAILADDRESS = "EmailAddress";
    public final static String COLUMN_FARMERAGE = "Age";
    public final static String COLUMN_CASTID = "CastId";
    public final static String COLUMN_FARMERPRIMARYCONTACTNUMBER = "PrimaryContactNumber";
    public final static String COLUMN_FARMERSECONDARYCONTACTNUMBER = "SecondaryContactNumber";
    public final static String COLUMN_POCCONTACTINFO = "POCContactInfo";
    public final static String COLUMN_CARETAKER = "CareTaker";
    public final static String COLUMN_FARMERADDRESS1 = "Address1";
    public final static String COLUMN_FARMERADDRESS2 = "Address2";
    public final static String COLUMN_FARMERLANDMARK = "Landmark";
    public final static String COLUMN_FARMERSTATECODE = "StateCode";
    public final static String COLUMN_FARMERDISTRICTCODE = "DistrictCode";
    public final static String COLUMN_FARMERMANDALCODE = "MandalCode";
    public final static String COLUMN_FARMERVILLAGECODE = "VillageCode";
    public final static String COLUMN_FARMERCITYCODE = "CityCode";
    public final static String COLUMN_FARMERPINCODE = "Pincode";
    public final static String COLUMN_FARMERPHOTO = "FarmerPhoto";

    //InterCropDetails database table info
    public final static String TABLE_INTERCROPDETAILSDETAILS = "InterCropDetails";
    public final static String COLUMN_INTERCROPID = "InterCropId";
    public final static String TABLE_CROPINFO = "CropInfo";
    //    public  final static String COLUMN_PLOTCODE = "PlotCode";
    public final static String COLUMN_INTERCROPINYEAR = "InterCropInYear";
    public final static String COLUMN_CROPCODE = "CropCode";
    public final static String COLUMN_CROPID = "CropId";
    public final static String COLUMN_CROPNAME = "OtherCrop";
    //    public  final static String COLUMN_CREATEDBY = "CreatedBy";
//    public  final static String COLUMN_CREATEDDATE = "CreatedDate";
//    public  final static String COLUMN_UPDATEBY = "UpdatedBy";
//    public  final static String COLUMN_UPDATEDATE = "UpdatedDate";
    //  public final static String COLUMN_SERVERUPDATESTATUS = "ServerUpdateStatus";

    //FarmerLandDetailsModel database table info
    public final static String TABLE_LANDDETAILS = "LandDetails";
    //    public  final static String COLUMN_PLOTCODE = "PlotCode";
//    public  final static String COLUMN_FARMERCODE = "FarmerCode";
    public final static String COLUMN_VILLAGECODE = "VillageCode";
    public final static String COLUMN_MANDALCODE = "MandalCode";
    public final static String COLUMN_DISTRICTCODE = "DistrictCode";
    public final static String COLUMN_SURVEYNUMBER = "SurveyNumber";
    public final static String COLUMN_ADANGALNO = "AdangalOrFileNo";
    public final static String COLUMN_TOTALAREA = "TotalArea";
    public final static String COLUMN_TOTALPALM = "TotalPalm";
    public final static String COLUMN_AREALEFTOUT = "AreaLeftOut";
    public final static String COLUMN_MOUNO = "MOUNo";
    public final static String COLUMN_MOUDATE = "MOUDate";
    public final static String COLUMN_LEASE = "OwnerShip";
    public final static String COLUMN_LANDLORDNAME = "LandlordName";
    public final static String COLUMN_LANDLORDCONTACTNUMBER = "LandlordContactNumber";
    public final static String COLUMN_LEASEDATETO = "LeaseDateTo";
    public final static String COLUMN_LEASEDATEFROM = "LeaseDateFrom";
    public final static String COLUMN_GPSPLOTAREA = "GPSPlotArea";
    public final static String COLUMN_PLOTDIFFERENCE = "PlotDifference";
    public final static String COLUMN_LANDMARK = "Landmark";
    public final static String COLUMN_PLOTADRESS = "PlotAddress";
    public final static String COLUMN_SOURCEOFWATER = "SourceOfWater";
    public final static String COLUMN_NUMBEROFBOREWELLS = "NumberOfBoreWells";
    public final static String COLUMN_CONTACTPERSON = "POCContactInfo";
    public final static String COLUMN_CONTACTPERSONNUMBER = "POCContactNumber";
    public final static String COLUMN_LEASECOMMENTS = "Comments";


//    public  final static String COLUMN_CREATEDBY = "CreatedBy";
//    public  final static String COLUMN_CREATEDDATE = "CreatedDate";
//    public  final static String COLUMN_UPDATEDBY = "UpdatedBy";
//    public  final static String COLUMN_UPDATEDATE = "UpdatedDate";
//    public  final static String COLUMN_SERVERUPDATEDSTATUS = "ServerUpdatedStatus";

    //MarketSurveyAndReferrals database table info
    public final static String TABLE_MARKETSURVEYDETAILS = "MarketSurveyAndReferrals";
    public final static String COLUMN_SURVEYID = "SurveyId";
    //    public  final static String COLUMN_VILLAGECODE = "VillageCode";
    public final static String COLUMN_VILLAGENAME = "VillageName";
    public final static String COLUMN_MARKETSURVEYNO = "MarketSurveyNo";
    public final static String COLUMN_MARKETSURVEYDATE = "MarketSurveyDate";
    public final static String COLUMN_FARMER = "Farmer";
    //    public  final static String COLUMN_FARMERCODE = "FarmerCode";
    public final static String COLUMN_PERSONNAME = "PersonName";
    public final static String COLUMN_FAMILYCOUNT = "FamilyCount";
    public final static String COLUMN_COOKINGOILTYPE = "CookingOilType";
    public final static String COLUMN_BRAND = "Brand";
    public final static String COLUMN_QUANTITYCONSUMEDPERMONTH = "QuantityConsumedperMonth";
    public final static String COLUMN_AMOUNTPAIDFOROILMONTH = "AmountPaidForOilPerMonth";
    public final static String COLUMN_TOTAL = "Total";
    public final static String COLUMN_REASONFORPARTICULAROIL = "ReasonForParticularOil";
    public final static String COLUMN_SWAPTOPALMOIL = "SwapToPalmOil";
    public final static String COLUMN_BRANDAMOUNT = "BrandAmount";
    public final static String COLUMN_SMARTPHONE = "SmartPhone";
    public final static String COLUMN_CATTLE = "Cattle";
    public final static String COLUMN_CATTLEQUANTITY = "CattleQuantity";
    public final static String COLUMN_CATTLEDETAILS = "CattleDetails";
    public final static String COLUMN_OWNVECHICLES = "OwnVehicles";
    public final static String COLUMN_VECHICLEDETAILS = "VehiclesDetails";
    public final static String COLUMN_COLLECTIONCENTERISSUES = "CollectionCentreIssues";
    public final static String COLUMN_ISSUESDETAILS = "IssueDetails";
    public final static String COLUMN_REFERRALS = "Referrals";
    public final static String COLUMN_REFERRALNAME = "ReferralName";
    public final static String COLUMN_MOBILENO = "MobileNo";
    public final static String COLUMN_COMMENTS_FARMER = "Complaint";

    public final static String COLUMN_REFARAL_SERVERUPDATEDSTATUS = "ServerUpdatedStatus";


    //Bank Account Details table info
    public final static String TABLE_BANKACCOUNTDETAILS = "BankDetails";
    public final static String COLUMN_BANKDETAILSID = "BankDetailId";
    public final static String COLUMN_BANKCODE = "BankCode";
    public final static String COLUMN_BANKNAME = "OtherBank";
    public final static String COLUMN_ACCOUNTHOLDERNAME = "AccountHolderName";
    public final static String COLUMN_ACCOUNTNUMBER = "AccountNumber";
    public final static String COLUMN_BRANCHNAME = "BranchName";
    public final static String COLUMN_IFSCCODE = "IFSCCode";
    public final static String COLUMN_ACTIVE = "Active";
    public final static String COLUMN_REPRESENTATIVE = "Representative";
    //  public final static String COLUMN_SERVERUPDATEDSTATUS = "ServerUpdatedStatus";

    //NeighboringPlot details table info
    public final static String TABLE_NEIGHBORINGPLOT = "NeighboringPlot";
    public final static String COLUMN_NEIGHBORINGPLOTID = "NeighboringPlotId";
    //    public final static String COLUMN_FARMERCODE = "FarmerCode";
//    public final static String COLUMN_PLOTCODE = "PlotCode";
    public final static String COLUMN_NEIGHBOURPLOT = "NeighbourPlot";
    //    public final static String COLUMN_CROPCODE = "CropCode";
//    public final static String COLUMN_CROPNAME = "CropName";
//    public final static String COLUMN_CREATEDBY = "CreatedBy";
//    public final static String COLUMN_CREATEDDATE = "CreatedDate";
//    public final static String COLUMN_UPDATEDBY = "UpdatedBy";
//    public final static String COLUMN_UPDATEDATE = "UpdateDate";
    public final static String COLUMN_CSERVERUPDATEDSTATUS = "ServerUpdatedStatus";

    //CropMaintenance details table info
    public final static String TABLE_CROPMAINTENANCE = "CropMaintenance";
    public final static String COLUMN_CROPMAINTENANCEID = "CropMaintenanceId";
    public final static String COLUMN_CROPMAINTENANCETYPEID = "CropMaintenanceTypeId";
    //    public final static String COLUMN_FARMERCODE = "FarmerCode";
//    public final static String COLUMN_PLOTCODE = "PlotCode";
    public final static String COLUMN_STAFFLASTVISIT = "StafflastVisit";
    public final static String COLUMN_RATEOURSERVICE = "RateOurService";
    //    public final static String COLUMN_COMMENTS = "Comments";
    public final static String COLUMN_PRUNING = "Pruning";
    public final static String COLUMN_FERTILIZERSOURCE = "FertilizerSource";
    public final static String COLUMN_FERTILIZERTYPE = "FertilizerType";
    public final static String COLUMN_FERTILIZERPRODUCTNAME = "FertilizerProductName";
    public final static String COLUMN_WEEDCODE = "WeedCode";
    public final static String COLUMN_WEEDMETHOD = "WeedMethod";
    public final static String COLUMN_CHEMICALCODE = "ChemicalCode";
    public final static String COLUMN_CHEMICALNAME = "OtherChemical";
    public final static String COLUMN_UNITOFMEASURE = "UnitOfMeasure";
    public final static String COLUMN_DOSAGEGIVEN = "DosageGiven";
    public final static String COLUMN_LASTAPPLICATEDDATE = "LastAppliedDate";
    public final static String COLUMN_FREQUENCYAPPLICATIONPERYEAR = "FrequencyApplicationPerYear";
    public final static String COLUMN_RATEONSCALE = "RateOnScale";
    //    public final static String COLUMN_CREATEDBY = "CreatedBy";
//    public final static String COLUMN_CREATEDDATE = "CreatedDate";
//    public final static String COLUMN_UPDATEDBY = "UpdatedBy";
//    public final static String COLUMN_UPDATEDDATE = "UpdatedDate";
    //   public final static String COLUMN_SERVERUPDATEDSATUS = "ServerUpdatedStatus";

    //ComplaintDetails table info
    public final static String TABLE_COMPLAINTDID = "ComplaintId";
    public final static String TABLE_COMPLAINTDETAILS = "ComplaintDetails";
    //    public final static String COLUMN_FARMERCODE = "FarmerCode";
//    public final static String COLUMN_PLOTCODE = "PlotCode";
    public final static String COLUMN_COMPLAINTDESCRIPITION = "NatureofComplaint";
    public final static String COLUMN_DEGREEOFCOMPLAINT = "DegreeOfComplaint";
    public final static String COLUMN_STATUS = "Status";
    public final static String COLUMN_RESOLUTION = "Resolution";
    public final static String COLUMN_RESOLVEDBY = "ResolvedBy";
    public final static String COLUMN_FOLLOWUPREQURIED = "FollowupRequired";
    public final static String COLUMN_NEXTFOLLOWUPDATE = "NextFollowupDate";
    public final static String COLUMN_COMMENTSCOMPLAINT = "Comments";

    //    public final static String COLUMN_CREATEDBY = "CreatedBy";
//    public final static String COLUMN_CREATEDDATE = "CreatedDate";
    public final static String COLUMN_UPDATEBY = "UpdatedBy";
//    public final static String COLUMN_UPDATEDATE = "UpdatedDate";
//    public final static String COLUMN_SERVERUPDATEDSTATUS = "ServerUpdatedStatus";


    //IDProofs table info
    public final static String TABLE_IDPROOFS = "IDProofs";
    public final static String COLUMN_PROOFID = "ProofID";
    public final static String COLUMN_PROOFTYPE = "ProofType";
    public final static String COLUMN_PROOFNO = "ProofNo";
    public final static String COLUMN_IDPRROF_FARMERCODE = "FramerCode";

    //PlantProtectionDetails table info

    public final static String COLUMN_PLANTPROTECTIONID = "PlantProtectionId";
    public final static String TABLE_PLANTPROTECTIONID = "PlantProtectionId";
    public final static String TABLE_PLANTPROTECTIONDETAILS = "PlantProtectionDetails";
    public final static String COLUMN_PLANTPROTECTIONTYPEID = "PlantProtecionTypeId";
    //    public  final static String COLUMN_FARMERCODE = "FarmerCode";
//    public  final static String COLUMN_PLOTCODE= "PlotCode";
    public final static String COLUMN_DISEASECODE = "DiseaseCode";
    public final static String COLUMN_DISEASENAME = "DiseaseName";
    public final static String COLUMN_OTHERPESTNAME = "OtherPestName";
    public final static String COLUMN_OTHERCHEMICALNAME = "OtherChemical";
    //    public  final static String COLUMN_CHEMICALCODE= "ChemicalCode";
//    public  final static String COLUMN_CHEMICALNAME= "ChemicalName";
    public final static String COLUMN_PESTCODE = "PestCode";
    public final static String COLUMN_PESTNAME = "PestName";
    public final static String COLUMN_UOM = "UOM";
    //    public  final static String COLUMN_DOSAGEGIVEN= "DosageGiven";
    public final static String COLUMN_LASTAPPLIEDDATE = "LastAppliedDate";
    public final static String TABLE_OBSERVATIONS = "Observations";
    public final static String COLUMN_WEAVILS = "Weavils";
    public final static String TABLE_MULCHING = "Mulching";
    public final static String COLUMN_MODULEID = "ModuleId";
    public final static String TABLE_PICTURE_REPORTING = "PictureReporting";

    static {
        transactionTables.add(TABLE_MARKETSURVEYDETAILS);
        transactionTables.add(TABLE_CROPINFO);
        transactionTables.add(TABLE_INTERCROPDETAILSDETAILS);
        transactionTables.add(TABLE_NEIGHBORINGPLOT);
        transactionTables.add(TABLE_FFBHARVESTDETAILS);
        transactionTables.add(TABLE_UPROOTMENTDETAILS);
        transactionTables.add(TABLE_CROPMAINTENANCE);
        transactionTables.add(TABLE_PLANTPROTECTIONDETAILS);
        transactionTables.add(TABLE_HEALTHOFPLANTATIONDETAILS);
        transactionTables.add(TABLE_COMPLAINTDETAILS);

    }
    //consignment file Repo tables keys
    public final static String TABLE_CONSIGNMENT_FileRepo = "ConsignmentRepository";
    public final static String COLUMN_ConsigmentCODE = "ConsignmentCode";
    public final static String COLUMN_ModuleTypeId = "ModuleTypeId";
    public final static String COLUMN_FileName = "FileName";
    public final static String COLUMN_FileLocation = "FileLocation";
    public final static String COLUMN_FileExtension = "FileExtension";

    //consignment tables keys

    public final static String TABLE_CONSIGNMENT = "Consignment";
    public final static String TABLE_STOCK_TRANSFER = "StockTransfer";
    public final static String TABLE_STOCK_RECEIVE = "StockTransferReceive";
    public final static String COLUMN_CODE = "Code";
    public final static String COLUMN_CCCODE = "CollectionCenterCode";
    public final static String COLUMN_VEHICLENUMBER = "VehicleNumber";
    public final static String COLUMN_DRIVERNAME = "DriverName";
    public final static String COLUMN_MILLCODE = "MillCode";
    public final static String COLUMN_TOTALWEIGHT = "TotalWeight";
    public final static String COLUMN_GROSSWEIGHT = "GrossWeight";
    public final static String COLUMN_TAREWEIGHT = "TareWeight";
    public final static String COLUMN_NETWEIGHT = "NetWeight";
    public final static String COLUMN_WEIGHTDIFF = "WeightDifference";
    public final static String COLUMN_RECEIPTGENERATEDDATE = "ReceiptGeneratedDate";
    public final static String COLUMN_RECEIPTCODE = "ReceiptCode";
    public final static String COLUMN_RECEIPTNAME = "RecieptName";
    public final static String COLUMN_RECEIPTLOCATION = "RecieptLocation";
    public final static String COLUMN_RECEIPTEXTENSION = "RecieptExtension";
    public final static String COLUMN_CCREATEDBY = "createdBy";
    public final static String COLUMN_CONSIGNMENTWEIGHT = "consignmentWeight";
    public final static String COLUMN_CCOMMENTS = "comments";
    public final static String COLUMN_CREATEDBYUSERID = "CreatedByUserId";
    public final static String COLUMN_UPDATEDBYUSERID = "UpdatedByUserId";
    public final static String COLUMN_ISACTIVE = "IsActive";
    public final static String COLUMN_CREATEDDATE = "CreatedDate";
    public final static String COLUMN_UPDATEDDATE = "UpdatedDate";
    public final static String COLUMN_SERVERUPDATEDSTATUS = "ServerUpdatedStatus";

    public final static String TABLE_CONSIGNMENTSTATUSHISTORY = "ConsignmentStatusHistory";
    public final static String COLUMN_CONSIGNMENTCODE = "ConsignmentCode";
    public final static String COLUMN_STATUSTYPEID = "StatusTypeId";
    public final static String COLUMN_OPERATORNAME = "OperatorName";

    //collection tables keys

    public final static String TABLE_COLLECTION = "Collection";
    public final static String COLUMN_CFARMERCODE = "FarmerCode";
    public final static String COLUMN_WEIGHBRIDGECENTERID = "WeighbridgeCenterId";
    public final static String COLUMN_WEIGHDATE = "WeighingDate";
    public final static String COLUMN_TOTALBUNCHES = "TotalBunches";
    public final static String COLUMN_REJECTEDBUNCHES = "RejectedBunches";
    public final static String COLUMN_ACCEPTEDBUNCHES = "AcceptedBunches";
    public final static String COLUMN_REMARKS = "Remarks";
    public final static String COLUMN_GRADERNAME = "GraderName";
    public final static String COLUMN_VECHILE_ID = "vehicleTypeId";
    public final static String COLUMN_DRIVER_MOBILENUMBER = "DriverMobileNumber";


    public final static String TABLE_COLLECTIONXPLOTREF = "CollectionPlotXref";

    public final static String COLUMN_COLLECTIONCODE = "CollectionCode";

}
