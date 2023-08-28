package com.oilpalm3f.mainapp.database;

import android.text.TextUtils;

import com.oilpalm3f.mainapp.common.CommonConstants;


//Here we write Queries we use
public class Queries {

    private static Queries instance;

    public static Queries getInstance() {
        if (instance == null) {
            instance = new Queries();
        }
        return instance;
    }

    public String getStatesMasterQuery() {
        return "select Id, Code, Name from State";
    }

    public String countOfSync(String UserId){
        return "Select * from UserSync where DATE(CreatedDate)= DATE('now') AND App = '3fCropCollection' AND UserId = '"+UserId+"' ";
    }


    public String getTokenNo(String CollectionCenterCode){
        return "select TokenNo from Collection where CollectionCenterCode ='"+CollectionCenterCode+"' " +
                "and CreatedDate=DATE('now')\n" +
                "order by TokenNo desc LIMIT 1";
    }
    public String getTokenNoTa(String CollectionCenterCode){
        return "select TokenNo from TokenTable where  CreatedDate>=date('now')\n" +
                "    order by TokenNo desc LIMIT 1";
    }
    public String getGrossWeightDetails(Integer TokenNo){
        return "select * from TokenTable where TokenNo='"+TokenNo+"'";
    }

    public String getStatesQuery() {
        return "SELECT s.Id," +
                "  s.Code," +
                "  s.Name," +
                "  s.CountryId," +
                "  c.Code AS CountryCode," +
                "  c.Name AS CountryName" +
                "FROM State  s" +
                "INNER JOIN Country c ON c.Id = s.CountryId";
    }

    public String getDistrictQuery(final String stateId) {
        return "select Id, Code, Name from District where StateId IN (Select Id from State where Id = '" + stateId + "'" + ")";
    }

    public String getMandalsQuery(final String DistrictId) {
        return "select Id, Code, Name from Mandal where DistrictId IN (Select Id from District where Id = '" + DistrictId + "'" + ")";
    }

    public String getVillagesQuery(final String mandalId) {
        return "select Id, Code, Name from Village where MandalId IN (Select Id from Mandal where Id = '" + mandalId + "'" + ")";
    }

    public String getMandalsQueryBasedOnMandalCode(final String MandalCode) {
        return "select MandalCode, MandalId, MandalName from MandalMaster where MandalCode = '" + MandalCode + "'";
    }

    public String getVillagesQueryBasedOnVillageCode(final String VillageCode) {
        return "select VillageCode from VillageMaster where VillageCode = '" + VillageCode + "'";
    }

    public String getCityQuery(final String DistrictId) {
        return "select CityId,CityName from CityMaster where DistrictCode IN (Select DistrictCode from DistrictMaster where DistrictId ='" + DistrictId + "'" + ")";
    }

    public String getBankNameQuery() {
        return "select BankCode,BankName from BankMaster";
    }

    public String getBankBranchNameQuery() {
        return "select BankCode,BankName from BankMaster";
    }

    public String getSwapReasonNameQuery() {
        return "select SwapReasonCode,SwapReasonName from SwapReasonsMaster";
    }

    public String getVarietyTypeQuery() {
        return "select CropVarietyCode,CropVarietyType from CropVarietyMaster";
    }

    public String getSourceofSaplings() {
        return "";
    }

    public String getBindPalmDetailsQuery() {
        return "select OtherCrop,CropVarietyType,CropVarietyName, CropId from CropInfo where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'";
//        return "select CropName,CropVarietyType,CropVarietyName from CropInfo  where FarmerCode ='" + CommonConstants.FARMER_CODE + "' PlotCode ='"+CommonConstants.PLOT_CODE+"'";
    }

    public String getBindIntercropQuery() {
        return "select InterCropInYear,OtherCrop,InterCropId from InterCropDetails where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'";
    }

    public String getBindFertilizerQuery() {
        return "select FertilizerSource,FertilizerType,FertilizerProductName,CropMaintenanceId from CropMaintenance where CropMaintenanceTypeId = '1' and FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'";
    }

    public String getUserSyncDetails(){
        return "Select * from UserSync where ServerUpdatedStatus = 0 ";
    }

    public String getStockTransferrecieveRefresh(){
        return "Select * from StockTransferReceive where ServerUpdatedStatus = 0 ";
    }
    public String countOfMasterSync(){
        return "select * from UserSync where MasterSync=1 and TransactionSync=0 and ResetData=0 and DATE(CreatedDate)= DATE('now') ";
    }
    public String countOfTraSync(){
        return "select * from UserSync where MasterSync=0 and TransactionSync=1 and ResetData=0 and DATE(CreatedDate)= DATE('now')";
    }

    public String countOfResetdata(){
        return "select * from UserSync where MasterSync=0 and TransactionSync=0 and ResetData=1 and DATE(CreatedDate)= DATE('now')";
    }



    public String getBindWeedingQuery() {
        return "select WeedMaster.WeedName,C.WeedMethod,C.OtherChemical,C.CropMaintenanceId from CropMaintenance C , WeedMaster wm inner join WeedMaster on WeedMaster.WeedCode = C.WeedCode" +
                "where CropMaintenanceTypeId = '2' and FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'" + " group by C.WeedCode";
    }

    public String getPruning() {
        return "select Pruning from CropMaintenance where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'";
    }


    public String getBindDiseaseQuery() {
        return "select DiseaseName,OtherChemical,UOM, PlantProtectionId from PlantProtectionDetails where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "' and PlantProtecionTypeId = '1'";
    }

    public String getBindPestQuery() {
        return "select OtherPestName,OtherChemical,UOM,PlantProtectionId from PlantProtectionDetails where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "' and PlantProtecionTypeId = '2'";
    }

    public String getBindneighborQuery() {
        return "select NeighbourPlot,OtherCrop,NeighboringPlotId from NeighboringPlot where FarmerCode ='" + CommonConstants.FARMER_CODE + "' and PlotCode ='" + CommonConstants.PLOT_CODE + "'";
    }


    public String getVarietyFromVarietyTypeQuery(final String varietyName) {
        return "select CropVarietyCode,CropVarietyName from CropVarietyMaster where CropVarietyType ='" + varietyName + "'";
    }

    public String getCropNameQuery() {
        return "select CropCode,CropName from CropMaster";
    }

    public String getWeedNameQuery() {
        return "select WeedCode,WeedName from WeedMaster";
    }

    public String getWeedNameQuery(String WeedCode) {
        return "select WeedName from WeedMaster where WeedCode = '" + WeedCode + "'";
    }

    public String getChemicalNameNameQuery() {
        return "select ChemicalCode,ChemicalName from ChemicalMaster";
    }

    public String getUserNameQuery() {
        return "select EmployeeId,EmployeeName from UserDetails";
    }

    public String getfertiliQzerNameQuery() {
        return "select FertilizerCode,FertilizerName from FertilizerMaster";
    }

    public String getDiseaseCodeQuery() {
        return "select DiseaseName,DiseaseCode from DiseaseMaster";
    }

    public String getDiseaseNameQuery() {
        return "select DiseaseCode,DiseaseName from DiseaseMaster";
    }

    public String getPestCodeQuery() {
        return "select PestName,PestCode from PestMaster";
    }

    public String getPestNameQuery() {
        return "select PestCode,PestName from PestMaster";
    }

    public String getfertiliQzerTypeQuery() {
        return "select FertilizerCode,FertilizerType from FertilizerMaster";
    }

    public String getPreferredCollectionQuery(String DistrictCode) {
        return "select CollectionCentreCode,CollectionCentreName from CollectionCentreMaster where DistrictCode ='" + DistrictCode + "'";
    }

    public String getPreferredCollectionAllQuery() {
        return "select CollectionCentreCode,CollectionCentreName from CollectionCentreMaster";
    }

    public String getUprootmentReasonQuery() {
        return "select UprootReasonCode,UprootReasonName from UprootReasonsMaster";
    }


    public String getMaxNumberQuery(final String villageId, final String villageCode) {
        return "SELECT max(substr(FarmerCode, length(FarmerCode) - 3,length(FarmerCode))) as Maxnumber FROM FarmerDetails  WHERE FarmerCode like '%" + villageId + "%'" + " AND VillageCode='" + villageCode + "'";
    }


    public String getMaxNumberForPlotQuery(final String villageId, final String villageCode) {
        return "SELECT max(substr(PlotCode, length(PlotCode) - 3,length(PlotCode))) as Maxnumber" +
                "FROM LandDetails WHERE PlotCode like '%" + villageId + "%'" + " AND VillageCode='" + villageCode + "'";
    }

    public String getCastesQuery() {
        return "select CastId,CastName from CastMaster";
    }

 public String getsource_of_contactQuery() {
        return "SELECT Id,NAme FROM LookUp where LookUpTypeId = '13' and isActive ='true'";
    }

    public String getvehicleTypeName(Integer ID) {
        return "Select Name From LookUp where Id = '"+ID+"'";
    }

public String gettitleQuery() {
        return "SELECT Typecdid,desc FROM typecddmt where classtypeid= '6' and isActive ='true'";
    }

    public String getgenderQuery() {
        return "SELECT Typecdid,desc FROM typecddmt where classtypeid= '7' and isActive ='true'";
    }
    public String getcastQuery() {
        return "SELECT Typecdid,desc FROM typecddmt where classtypeid= '8' and isActive ='true'";
    }

    public String getTruckSize(String code)
    {
        return "select tsm.Id,tsm.Capacity,tsm.TruckSize from TruckSizeMst tsm" +
                " left join CcRate c on tsm.Id = c.SizeOfTruck" +
                " where c.CollectionCenterCode = '"+code+"'";
    }

    public String getCcRate(String collectionCode,int sizeId)
    {
        return "SELECT TransportCost,CombinedCharge,OverweightCharge FROM CcRate where CollectionCenterCode= '" + collectionCode + "' and SizeOfTruck = '"+sizeId+"'";
    }

    public String isCenterExists(String code)
    {
      return " SELECT COUNT(*) FROM CcRate WHERE IsActive = 'true' and CollectionCenterCode = '"+code+"'";
    }

    public String isCollectionExists(String code)
    {
        return "SELECT COUNT(*) FROM Collection WHERE Code = '"+code+"'";
    }

    public String isCollectionwithoutPlotExists(String code)
    {
        return "SELECT COUNT(*) FROM CollectionWithOutPlot WHERE Code = '"+code+"'";
    }

    public String isConsignmentExists(String code)
    {
        return "SELECT COUNT(*) FROM Consignment WHERE Code = '"+code+"'";
    }

    public String isStockTransferExists(String code)
    {
        return "SELECT COUNT(*) FROM StockTransfer WHERE Code = '"+code+"'";
    }



    public String deleteTableData() {
        return "delete from %s";
    }

    public String getSyncDate() {
        return "select UpdatedOn from MasterVersionTrackingSystem";
    }

    public String getUserDetails(final String imeiNumer) {
        return "select U.EmployeeId, U.EmployeeName, U.Password, T.TabletId from UserDetails U, TabletAllocation T " +
                "inner join TabletAllocation on U.EmployeeId = T.EmployeeId where U.Employeeid IN (select Employeeid from TabletAllocation where TabletIMEINo = '" + imeiNumer + "'" + ") GROUP BY U.EmployeeId";
    }

    public String getFarmerLandDetailsQuery() {
        return "select  FarmerDetails.FarmerCode,FarmerDetails.FarmerFirstName,FarmerDetails.FarmerMiddleName, FarmerDetails.FarmerLastName,FarmerDetails.DOB," +
                "FarmerDetails.Gender,FarmerDetails.FatherName, FarmerDetails.MotherName,StateMaster.StateName,DistrictMaster.DistrictName,PictureReporting.Photo, " +
                "FarmerDetails.Active, FarmerDetails.PrimaryContactNumber,FarmerDetails.SecondaryContactNumber,  FarmerDetails.Address1,FarmerDetails.Address2,FarmerDetails.Landmark," +
                "VillageMaster.VillageName, VillageMaster.VillageCode, VillageMaster.VillageId, StateMaster.StateCode, StateMaster.StateId, MandalMaster.MandalCode, " +
                "DistrictMaster.DistrictCode  from FarmerDetails inner join VillageMaster on FarmerDetails.VillageCode = VillageMaster.VillageCode LEFT JOIN PictureReporting on " +
                "PictureReporting.FarmerCode = FarmerDetails.FarmerCode and PictureReporting.ModuleId = '1' inner join StateMaster on StateMaster.StateCode = FarmerDetails.StateCode " +
                "inner join DistrictMaster on DistrictMaster.DistrictCode = FarmerDetails.DistrictCode inner join MandalMaster on MandalMaster.MandalCode = FarmerDetails.MandalCode " +
                "where FarmerDetails.Active =1  group by FarmerDetails.FarmerCode;";
    }

    public String getBankDetailsQuery(String farmercode) {
        return "select BankDetailId,OtherBank,AccountHolderName,AccountNumber,BranchName,IFSCCode from BankDetails where FarmerCode='" + farmercode + "'";
    }

    public String marketSurveyDataCheck(String farmerCode) {
        return "select * from MarketSurveyAndReferrals where FarmerCode = '" + farmerCode + "'";
    }

    public String getPlotDetailsQuery(String farmerCode) {
        return "select LandDetails.PlotCode,LandDetails.Landmark,LandDetails.TotalArea from LandDetails " +
                " inner join FarmerDetails on FarmerDetails.FarmerCode=LandDetails.FarmerCode where LandDetails.FarmerCode='" + farmerCode + "' group by LandDetails.PlotCode order by LandDetails.rowid";
    }

    public String getRefreshCountQuery(String tablename) {
        return "select count(0) from " + tablename + " where ServerUpdatedStatus='false'";
    }

    public String getContVisitLog() {
        return "select count(0) from VisitLog where ServerUpdatedStatus = 0 ";
    }

    public String getRefreshCountQueryForPictures() {
        return "select count(0) from PictureReporting where ServerUpdatedStatus='false' and LENGTH(PicturePath) > 0";
    }


    //********************* REFRESH QUERIES****************************************************************************************

    public String getFarmerDetailsRefreshQuery() {
        return "select FarmerCode,FarmerFirstName,FarmerMiddleName,FarmerLastName,DOB,Gender,FatherName,MotherName,EmailAddress," +
                "Age,CastId,PrimaryContactNumber,SecondaryContactNumber,POCContactInfo,CareTaker,Address1,Address2,Landmark," +
                "StateCode,DistrictCode,MandalCode,VillageCode,CityCode,Pincode,Active,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus,FarmerPhoto from FarmerDetails where ServerUpdatedStatus=0";
    }

    public String getIDProofDetailsRefreshQuery() {
        return "select ProofID,ProofType,ProofNo,FarmerCode,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from IDProofs where ServerUpdatedStatus=0";
    }

    public String getBankDetailsRefreshQuery() {
        return "select BankDetailId,FarmerCode,BankCode,OtherBank,AccountHolderName,AccountNumber,BranchName,IFSCCode,Active," +
                "CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from BankDetails where ServerUpdatedStatus=0";
    }

    public String getLandDetailsRefreshQuery() {
        return "select PlotCode,FarmerCode,DistrictCode,MandalCode,VillageCode,SurveyNumber,AdangalOrFileNo,CareTaker,POCContactInfo,POCContactNumber,OwnerShip,LandlordName,LandlordContactNumber,LeaseDateFrom,LeaseDateTo,Comments,TotalArea,TotalPalm,AreaLeftOut," +
                "MOUNo,MOUDate,Landmark,PlotAddress,SourceOfWater,NumberOfBoreWells,GPSPlotArea,PlotDifference,Representative,Active,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from LandDetails where ServerUpdatedStatus=0 ";
    }

    public String getPlotBoundaryRefreshQuery() {
        return "select BoundaryId,FarmerCode,PlotCode,Latitude,Longitude,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from PlotBoundaries where ServerUpdatedStatus=0 ";
    }

    public String getCropInfoRefreshQuery() {
        return "select CropId,FarmerCode,PlotCode,CropCode,OtherCrop,CropVarietyType,CropVarietyName,AreaAllocated," +
                "CountOfTrees,YearOfPlanting,CropCodeBeforeOilPalm,OtherCropBeforeOilPalm,SwapReasonCode,OtherSwapReason," +
                "CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from CropInfo where ServerUpdatedStatus=0";
    }

    public String getCropInfoEditQuery(final int cropId) {
        return getCropInfoRefreshQuery() + " and CropId ='" + cropId + "'";
    }

    public String getInterCropDetailsRefreshQuery() {
        return "select InterCropId,FarmerCode,PlotCode,InterCropInYear,CropCode,OtherCrop,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from InterCropDetails where ServerUpdatedStatus=0";
    }

    public String getNeighbourPlotRefreshQuery() {
        return "select NeighboringPlotId,FarmerCode,PlotCode,NeighbourPlot,CropCode,OtherCrop,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from NeighboringPlot where ServerUpdatedStatus=0";
    }

    public String getFFBHarvestRefreshQuery() {
        return "select FFBHarvestId,FarmerCode,PlotCode,CollectionCentreId,ModeOfTransport,HarvestingMethod," +
                "WagesPerDay,ContractRsPerMonth,ContractRsPerAnum,TypeOfHarvesting,ContractorPitch,FarmerConsent,Comments,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from FFB_HarvestDetails where ServerUpdatedStatus=0";
    }

    public String getFFBHarvestEditQuery(final String plotCode) {
        return getFFBHarvestRefreshQuery() + " and PlotCode ='" + plotCode + "'";
    }

    public String getUprootmentRefreshQuery() {
        return "select UprootmentId,FarmerCode,PlotCode,SeedingsPlanted,TreesCurrently,Uprootment,UprootmentReasonCode," +
                "OtherReason,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from UprootmentDetails where ServerUpdatedStatus=0";
    }

    public String getUprootmentEditQuery(final String plotCode) {
        return getUprootmentRefreshQuery() + " and PlotCode ='" + plotCode + "'";
    }

    public String getCropMaintainRefreshQuery() {
        return "select CropMaintenanceId,CropMaintenanceTypeId,FarmerCode,PlotCode,StafflastVisit,RateOurService,Comments," +
                "Pruning,FertilizerSource,FertilizerType,FertilizerProductName,WeedCode,WeedMethod,ChemicalCode,OtherChemical," +
                "UnitOfMeasure,DosageGiven,LastAppliedDate,FrequencyApplicationPerYear,RateOnScale,CreatedBy,CreatedDate," +
                "UpdatedBy,UpdatedDate,ServerUpdatedStatus from CropMaintenance where ServerUpdatedStatus=0";
    }

    public String getCropMaintainEditQuery(final String plotCode, final String cropMaintenanceId) {
        return getCropMaintainRefreshQuery() + " and PlotCode ='" + plotCode + "'" + " and CropMaintenanceId = '" + cropMaintenanceId + "'";
    }

    public String getPlantProtectionRefreshQuery() {
        return "select PlantProtectionId,PlantProtecionTypeId,FarmerCode,PlotCode,DiseaseCode,DiseaseName,ChemicalCode,OtherChemical," +
                "PestCode,OtherPestName,UOM,DosageGiven,LastAppliedDate,Observations,Weavils,Mulching,CreatedBy,CreatedDate,UpdatedBy," +
                "UpdatedDate,ServerUpdatedStatus from PlantProtectionDetails where ServerUpdatedStatus=0";
    }

    public String getPlantProtectionEditQuery(final String plotCode, final String PlantProtectionId) {
        return getPlantProtectionRefreshQuery() + " and PlotCode ='" + plotCode + "'" + " and PlantProtectionId = '" + PlantProtectionId + "'";
    }


    public String getHealthPlantRefreshQuery() {
        return "select PlantationDetailsId,FarmerCode,PlotCode,AppearanceOfTrees,GrowthOfTree,HeightOfTree,ColorOfFruit,SizeOfFruit," +
                "PalmHygiene,TypeOfPlantation,Comments,Photo,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from HealthofPlantationDetails where ServerUpdatedStatus=0";
    }

    public String getHealthPlantEditQuery(String plotCode) {
        return getHealthPlantRefreshQuery() + " and PlotCode ='" + plotCode + "'";
    }

    public String getHealthPlantImageQuery(String plotCode) {
        return "select Photo from PictureReporting where PlotCode ='" + plotCode + "'" + " and ModuleId = '2'";
    }

    public String getComplaintRefreshQueries() {
        return "select ComplaintId,FarmerCode,PlotCode,NatureofComplaint,DegreeOfComplaint,Comments,Status,Resolution,ResolvedBy," +
                "FollowupRequired,NextFollowupDate,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from ComplaintDetails where ServerUpdatedStatus=0";
    }

    public String getComplaintEditQueries(String plotCode) {
        return getComplaintRefreshQueries() + " and PlotCode ='" + plotCode + "'";
    }

    public String getMarketSurveyRefreshQuery() {
        return "select SurveyId,VillageCode,VillageName,MarketSurveyNo,MarketSurveyDate,Farmer,FarmerCode,PersonName,FamilyCount," +
                "CookingOilType,Brand,QuantityConsumedperMonth,AmountPaidForOilPerMonth,Total,ReasonForParticularOil,SwapToPalmOil," +
                "BrandAmount,SmartPhone,Cattle,CattleQuantity,CattleDetails,OwnVehicles,VehiclesDetails,CollectionCentreIssues,IssueDetails," +
                "Referrals,ReferralName,MobileNo,Complaint,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from MarketSurveyAndReferrals where ServerUpdatedStatus=0";
    }

    public String updateServerUpdatedStatus() {
        return "update %s set ServerUpdatedStatus = 'true' where ServerUpdatedStatus = 'false'";
    }
    public String updateVisitServerUpdatedStatus() {
        return "update %s set ServerUpdatedStatus = 1 where ServerUpdatedStatus = 0 ";
    }

    public String updateUserSyncServerUpdatedStatus() {
        return "update %s set ServerUpdatedStatus = 1 where ServerUpdatedStatus = 0 ";
    }

    public String updateServerUpdatedStatus(String number) {
        return "update %s set ServerUpdatedStatus = 'true' where ServerUpdatedStatus = 'false' and id <= '" + number + "'";
    }

    public String updateStockReceiveRecord(final String plotCode) {
        return "update %s set ServerUpdatedStatus = 'true' where Code ='" + plotCode + "' and ServerUpdatedStatus = 'false'";
    }

    //*****************************************************END OF REFRESH QUERIES************************************************************


    public String getPlotExistanceInAnyPalmDetailsQuery(final String plotCode) {
        return "select COUNT(*) " +
                "from (" +
                "    select PlotCode from CropInfo" +
                "    union all" +
                "    select PlotCode from CropMaintenance" +
                "    union all" +
                "    select PlotCode from FFB_HarvestDetails" +
                "    union all" +
                "    select PlotCode from HealthofPlantationDetails" +
                "    union all" +
                "    select PlotCode from InterCropDetails" +
                "    union all" +
                "    select PlotCode from NeighboringPlot" +
                "    union all" +
                "    select PlotCode from PlantProtectionDetails" +
                "     union all" +
                "    select PlotCode from UprootmentDetails" +
                "     union all" +
                "    select PlotCode from ComplaintDetails" +
                ") a" +
                "where PlotCode = '" + plotCode + "'";
    }

    public String getTableDataExist(final String plotCode, String table) {
        return "select * from " + table + " where PlotCode = '" + plotCode + "'";
    }

    public String getTableData(final String CropMaintenanceTypeId, String table, String plotCode) {
        return "select * from " + table + " where CropMaintenanceTypeId ='" + CropMaintenanceTypeId + "' and PlotCode ='" + plotCode + "'";
    }

    public String getData(final String PlantProtecionTypeId, String table, String plotCode) {
        return "select * from " + table + " where PlantProtecionTypeId ='" + PlantProtecionTypeId + "' and PlotCode ='" + plotCode + "'";
    }

    public String getMarketSurveyNumber() {
        return "SELECT COALESCE(MAX(MarketSurveyNo),0)+1 FROM MarketSurveyAndReferrals";
    }

    public String getEmployeeDistrict(final String EmployeeId, String StateCode) {
        return "select Distinct D.DistrictCode, D.DistrictId,D.DistrictName  from DistrictMaster  D" + " inner join EmployeeVillageAllocation E  on D.DistrictCode = E.DistrictCode where E.EmployeeId ='" + EmployeeId + "'and E.StateCode ='" + StateCode + "'";
    }

    public String getEmployeeMandal(final String EmployeeId, String DistrictCode) {
        return "select Distinct D.MandalCode, D.MandalId,D.MandalName  from MandalMaster  D" + " inner join EmployeeVillageAllocation E  on D.MandalCode = E.MandalCode where E.EmployeeId ='" + EmployeeId + "'and E.DistrictCode ='" + DistrictCode + "'";
    }

    public String getEmployeeVillage(final String EmployeeId, String MandalCode) {
        return "select Distinct D.VillageCode, D.VillageId,D.VillageName  from VillageMaster  D" + " inner join EmployeeVillageAllocation E  on D.VillageCode = E.VillageCode where E.EmployeeId ='" + EmployeeId + "'and E.MandalCode ='" + MandalCode + "'";
    }

    public String getImageDetails() {
        return "select Code,FarmerCode, ModuleId, PicturePath from PictureReporting where ServerUpdatedStatus='false'";
    }

    public String updatedImageDetailsStatus(String code,final String farmerCode, int moduleId) {
        return "update PictureReporting set ServerUpdatedStatus = 'true' where Code = '"+code+"' and FarmerCode ='" + farmerCode + "'" + " and ModuleId = " + moduleId;
    }

    public String deleteTableData(final String farmerCode, String table, String plotCode) {
        return "delete from " + table + " where FarmerCode ='" + farmerCode + "' and PlotCode ='" + plotCode + "'";
    }

    public String deleteTableData(final String farmerCode, String table) {
        return "delete from " + table + " where FarmerCode ='" + farmerCode + "'";
    }

    public String updatedConsignmentDetailsStatus(final String consignmentCode) {
        return "update Consignment set ServerUpdatedStatus = 'true' where Code = " + consignmentCode;
    }

    public String updatedConsignmentDetailsStatusFalse(final String consignmentCode) {
        return "update Consignment set ServerUpdatedStatus = 'false' where Code = " + consignmentCode;
    }

    public String updatedCollectionPlotXRefDetailsStatus(final String collectionCode, String plotCode) {
        return "update CollectionPlotXref set ServerUpdatedStatus = 'true' where CollectionCode ='" + collectionCode + "' and PlotCode ='" + plotCode + "'";
    }

    public String updatedCollectionPlotXRefDetailsStatusFalse(final String collectionCode, String plotCode) {
        return "update CollectionPlotXref set ServerUpdatedStatus = 'false' where CollectionCode ='" + collectionCode + "' and PlotCode ='" + plotCode + "'";
    }

    public String updatedCollectionDetailsStatus(final String collectionCode) {
        return "update Collection set ServerUpdatedStatus = 'true' where Code = " + collectionCode;
    }
    public String updatedCollectionDetailsStatusFalse(final String collectionCode) {
        return "update Collection set ServerUpdatedStatus = 'false' where Code = " + collectionCode;
    }
    public String updatedCollectionWithOutPlotDetailsStatus(final String collectionCode) {
        return "update CollectionWithOutPlot set ServerUpdatedStatus = 'true' where Code = " + collectionCode;
    }

    public String updatedCollectionWithOutPlotDetailsStatusFalse(final String collectionCode) {
        return "update CollectionWithOutPlot set ServerUpdatedStatus = 'false' where Code = " + collectionCode;
    }

    public String updatedImagesStatus(final String farmerCode, String plotCode) {

        String query = "update PictureReporting set ServerUpdatedStatus = 1 where FarmerCode = '" + farmerCode + "'";
        if (TextUtils.isEmpty(plotCode)) {
            query = query + " and PlotCode IS NULL OR PlotCode = ''";
        } else {
            query = query + " and PlotCode ='" + plotCode + "'";
        }
        return query;
    }

    public String deleteOldRecord() {
        return "delete from %s where %s = '" + "%s" + "' and PlotCode = '" + "%s" + "'";
    }

    public String deleteOldData() {
        return "delete from %s where %s = '" + "%s" + "'";
    }

    public String getMarketSurveyDetailsQuery(String farmercode) {
        return "select SurveyId,VillageCode,VillageName,MarketSurveyNo,MarketSurveyDate,Farmer,FarmerCode,PersonName,FamilyCount," +
                "CookingOilType,Brand,QuantityConsumedperMonth,AmountPaidForOilPerMonth,Total,ReasonForParticularOil,SwapToPalmOil," +
                "BrandAmount,SmartPhone,Cattle,CattleQuantity,CattleDetails,OwnVehicles,VehiclesDetails,CollectionCentreIssues,IssueDetails," +
                "Referrals,ReferralName,MobileNo,Complaint,CreatedBy,CreatedDate,UpdatedBy,UpdatedDate,ServerUpdatedStatus from MarketSurveyAndReferrals where FarmerCode='" + farmercode + "'";
    }

    public String queryToFindJunkData() {
        return "SELECT DISTINCT(src.PlotCode) as PlotCode, src.FarmerCode from %s src " +
                "LEFT JOIN LandDetails l" +
                "on l.PlotCode = src.PlotCode " +
                "where l.PlotCode IS NULL";
    }

    public String deleteInCompleteData() {
        return "delete from %s where PlotCode IN (" + "%s" + ")";
    }

    public String deleteHOPimagesData() {
        return "delete from PictureReporting where ServerUpdatedStatus = '1' and ModuleId = '2'";
    }

    public String queryToGetIncompleteMarketSurveyData() {
        return "SELECT src.FarmerCode from MarketSurveyAndReferrals src " +
                "LEFT JOIN FarmerDetails l" +
                "on src.FarmerCode = l.FarmerCode" +
                "where l.FarmerCode IS NULL";
    }

    public String deleteInCompleteMarketSurveyData() {
        return "delete from %s where FarmerCode IN (" + "%s" + ")";
    }

    //*****water,power,soil Queries************//
    public String getTypeofIrrigationQuery() {
        return "";
    }

    public String getplotPrioritizationQuery() {
        return "";
    }

    public String getSoilTypeQuery() {
        return "";
    }

    public String getPowerAvailabilityQuery() {
        return "";
    }

    public String getTypeOfPowerQuery() {
        return "";
    }

    //*****conversion potential queries *********//

    public String getFarmerReadytoConvertQuery() {
        return "";
    }

    public String getConversionPotentialScore() {
        return "";
    }


    /* Query for getting farmers data for CC data */
    public String getFarmersDataForCC() {
        return "select f.Code, f.FirstName, f.MiddleName, f.LastName, f.DOB, f.GuardianName,  f.MotherName,\n" +
                " s.Name as StateName, D.Name as DistrictName,\n" +
                " f.ContactNumber, f.MobileNumber, v.Name VillageName, v.Code AS VillageCode, v.Id as VillageId, s.Code as StateCode, s.Id as StateId, m.Code as MandalCode,  d.Code as DistrictCode,\n" +
                "   addr.AddressLine1, addr.AddressLine2, addr.AddressLine3, addr.Landmark, B.AccountNumber, B.AccountHolderName, fileRep.FileLocation, fileRep.FileName, fileRep.FileExtension, (select BranchName from Bank where Id = B.BankId) as BranchName,(select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName from Farmer f \n" +
                "   left join Village v on f.VillageId = v.Id\n" +
                "   left join Mandal m on f.MandalId = m.Id\n" +
                "   left join District d on f.DistrictId = d.Id\n" +
                "   left join Address addr on f.AddressCode = addr.Code\n" +
                "   left join FarmerBank B on B.FarmerCode = f.Code\n" +
                "   left join State s on f.StateId = s.Id\n" +
                "   left join FileRepository fileRep on f.Code = fileRep.FarmerCode\n" +
                " and fileRep.ModuleTypeId = 193  where f.StateId IN(select distinct stateid from \n" +
                "UserVillageXref uv\n" +
                "join village v on uv.villageid=v.id\n" +
                "join mandal m on m.id=v.mandalid\n" +
                "join district d on d.id=m.districtid\n" +
                "where uv.userid = '"+CommonConstants.USER_ID+"')OR f.Code ='FADUMMY01';";
    }

    public String getFarmersDataForCCFarmers() {
        return "select Cf.Code, Cf.FirstName, Cf.MiddleName, Cf.LastName, Cf.DOB, Cf.GuardianName,  Cf.MotherName,\n" +
                " s.Name as StateName, D.Name as DistrictName,\n" +
                " Cf.ContactNumber, Cf.MobileNumber, v.Name VillageName, v.Code AS VillageCode, v.Id as VillageId, s.Code as StateCode,\n" +
                " s.Id as StateId, m.Code as MandalCode,  d.Code as DistrictCode,\n" +
                "  addr.AddressLine1, addr.AddressLine2, addr.AddressLine3, addr.Landmark, CB.AccountNumber, CB.AccountHolderName,\n" +
                "  CfileRep.FileLocation, CfileRep.FileName, CfileRep.FileExtension, (select BranchName from Bank where Id = CB.BankId) as BranchName,\n" +
                " (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = CB.BankId)) as bankName from CollectionFarmer Cf \n" +
                "   left join Village v on Cf.VillageId = v.Id\n" +
                "   left join Mandal m on Cf.MandalId = m.Id\n" +
                "   left join District d on Cf.DistrictId = d.Id\n" +
                "   left join Address addr on Cf.AddressCode = addr.Code\n" +
                "   left join CollectionFarmerBank CB on CB.FarmerCode = Cf.Code\n" +
                "   left join State s on Cf.StateId = s.Id\n" +
                "   left join CollectionFileRepository CfileRep on Cf.Code = CfileRep.FarmerCode\n" +
                " and CfileRep.ModuleTypeId = 313 where Cf.StateId IN(select distinct stateid from  \n" +
                "                UserVillageXref uv \n" +
                "                join village v on uv.villageid=v.id  \n" +
                "                join mandal m on m.id=v.mandalid  \n" +
                "                join district d on d.id=m.districtid  \n" +
                "                where uv.userid = '"+CommonConstants.USER_ID+"')OR Cf.Code ='FADUMMY01';";
    }

    public String getPlotDetailsForCC(final String farmercode) {
        return "select p.Code, p.TotalPalmArea, p.TotalPlotArea, p.GPSPlotArea, p.SurveyNumber, addr.Landmark,\n" +
                "v.Code AS VillageCode, v.Name as VillageName, v.Id as VillageId,\n" +
                "m.Code as MandalCode, m.Name as MandalName, m.Id as MandalId,\n" +
                "d.Code as DistrictCode, d.Name as DistrictName, d.Id as DistrictId,\n" +
                "s.Code as StateCode, s.Name as StateName, s.Id as StateId from Plot p\n" +
                "inner join Address addr on p.AddressCode = addr.Code\n" +
                "inner join Village v on addr.VillageId = v.Id\n" +
                "inner join Mandal m on addr.MandalId = m.Id\n" +
                "inner join District d on addr.DistrictId = d.Id\n" +
                "inner join State s on addr.StateId = s.Id\n" +
                "inner join FarmerHistory fh on fh.PlotCode = p.Code\n" +
                "where p.FarmerCode='" + farmercode + "' and p.isActive = '1' " + " and fh.StatusTypeId in ('89','88','258','259') group by p.Code";
    }


    public String getCollectionCenterMaster(final String userId,String latitude, String longitude) {
        return "select c.Code collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId," +
                " v.Code as VillageCode, v.Name as VillageName, c.StateId,(ABS('"+latitude+"' - Latitude) + ABS('"+longitude+"' - Longitude)) AS DISTANCE from CollectionCenter  c      \n" +
                "inner join Village v on c.VillageId = v.Id\n" +
                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' " +
                "and c.VillageId IN "+"(select villageId from UserVillageXref where userId IN ('"+userId+"')) and c.IsActive = 'true' " +
                " group by collectionCenterCode ORDER BY distance,c.Name Asc";
//        return "select c.Code collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId," +
//                " v.Code as VillageCode, v.Name as VillageName, c.StateId from CollectionCenter  c      \n" +
//                "inner join Village v on c.VillageId = v.Id\n" +
//                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' " +
//                "and c.VillageId IN "+"(select villageId from UserVillageXref where userId IN ('"+userId+"')) and c.IsActive = 'true' " +
//                " group by collectionCenterCode ORDER BY c.Name Asc";
    }
    public String getStToCC(final String userId) {
        return "select c.Code collectionCenterCode,c.Id as CCId, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId," +
                " v.Code as VillageCode, v.Name as VillageName from CollectionCenter  c      \n" +
                "inner join Village v on c.VillageId = v.Id\n" +
                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' and c.IsActive='true'" +
                "and c.StateId IN "+"(select distinct stateid from \n" +
                "                         UserVillageXref uv \n" +
                "                              join village v on uv.villageid=v.id  \n" +
                "                              join mandal m on m.id=v.mandalid  \n" +
                "                              join district d on d.id=m.districtid  \n" +
                "                              where uv.userid = '"+userId+"')" + "" +
                " group by collectionCenterCode ORDER BY c.Name Asc";
    }

    public String getToCCId(final String userId){
        return "select c.Id  as CCId from CollectionCenter  c   \n" +
                " where c.IsMill = 'false' and c.VillageId IN (select villageId from UserVillageXref where userId IN ('" + userId + "')) \n" +
                " ORDER BY c.Name Asc";
    }
    public  String getCollectionCenterId(final String userId){

        return  " select c.Id as CCId, c.Name from CollectionCenter  c   \n" +
                " inner join Village v on c.VillageId = v.Id\n" +
                " where c.IsMill = 'false' and" +
                " c.VillageId IN " + "(select villageId from UserVillageXref where userId IN ('" + userId + "'))" +
                " order by c.Id";
    }

    public String getVistLogs(){
        return "Select * from VisitLog where ServerUpdatedStatus = 0";
    }

    //getCollectionCenter ID----
    public  String getCOLCNId(final String userId,String latitude, String longitude){

        return  "select c.Id collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId, v.Code as VillageCode, v.Name as VillageName,(ABS('"+latitude+"' - Latitude) + ABS('"+longitude+"' - Longitude)) AS DISTANCE from CollectionCenter  c   \n" +
                "inner join Village v on c.VillageId = v.Id\n" +
                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' and c.IsActive = 'true'  and c.VillageId IN " + "(select villageId from UserVillageXref where userId IN ('" + userId + "'))" + " group by collectionCenterCode ORDER BY distance,c.Name Asc";
//        return  "select c.Id collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId, v.Code as VillageCode, v.Name as VillageName from CollectionCenter  c   \n" +
//                "inner join Village v on c.VillageId = v.Id\n" +
//                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' and c.IsActive = 'true'  and c.VillageId IN " + "(select villageId from UserVillageXref where userId IN ('" + userId + "'))" + " group by collectionCenterCode ORDER BY c.Name Asc";
    }

    public  String getStockTransferCOLCNId(final String userId){

        return  "select c.Id collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId, v.Code as VillageCode, v.Name as VillageName from CollectionCenter  c   \n" +
                "inner join Village v on c.VillageId = v.Id\n" +
                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where c.IsMill = 'false' and c.IsActive = 'true'  and c.VillageId IN " + "(select villageId from UserVillageXref where userId IN ('" + userId + "'))" + " group by collectionCenterCode ORDER BY c.Name Asc";
    }

    public String getMaxCollectionCenterCode(final String year, final String collectionCenterCode, final String tableName) {
        return "select MAX(cast(substr(Code, INSTR(Code,'-') + 1, length(Code)) as INTEGER)) as Maxnumber from " + tableName +
                " where Code like '%" + year + "%' and Code like '%" + collectionCenterCode + "%'";
    }


    public String getCollectionType(String Name){
        return "select CollectionType,ReadMethod,NoOfChars,UpToCharacter,IsFingerPrintReq from CollectionCenter where Name='"+Name+"'";
    }
    public String getMaxReceiptCode(final String year, final String collectionCenterCode, final String tableName) {
        return "select MAX(cast(substr(Code, INSTR(Code,'-') + 1, length(Code)) as INTEGER)) as Maxnumber from " + tableName +
                " where ReceiptCode like '%" + year + "%' and ReceiptCode like '%" + collectionCenterCode + "%'";
    }

    public String getCollectionCenterWithVillage(final String gpsVillage) {
        String[] villageNames = new String[2];
        String filteredVillageName = "";
        String filteredVillageName2 = "";
        if (!TextUtils.isEmpty(gpsVillage)) {
            villageNames = gpsVillage.split("-");
        }
        if (null != villageNames && villageNames.length > 1) {
            filteredVillageName = villageNames[0];
            filteredVillageName2 = villageNames[1];
        }
        return "select  c.CollectionType,c.Code collectionCenterCode, c.Name, c.WeighBridgeTypeId, t.Desc, v.Id as VillageId, v.Code as VillageCode, " +
                "v.Name as VillageName from CollectionCenter c\n" +
                "inner join Village v on c.VillageId = c.VillageId \n" +
                "inner join TypeCdDmt t on c.WeighBridgeTypeId = t.TypeCdId where v.Name = '" + filteredVillageName + "'" + " OR v.Name = '" + filteredVillageName2 + "'" + " and c.IsMill = 'false' group by collectionCenterCode order by c.Id";
    }

    public String getCollectionDetailsRefreshQuery() {
//        return "select Code,CollectionCenterCode,FarmerCode,WeighbridgeCenterId,WeighingDate,VehicleNumber,DriverName," +
//                "GrossWeight,TareWeight ,NetWeight,OperatorName,Comments,TotalBunches,RejectedBunches,AcceptedBunches," +
//                "Remarks ,GraderName ,ReceiptGeneratedDate,ReceiptCode,WBReceiptName," +
//                "WBReceiptLocation,WBReceiptExtension,IsActive,CreatedByUserId ,Trpt,TokenNo,CreatedDate ,UpdatedByUserId,ServerUpdatedStatus,UpdatedDate,IsCollectionWithOutFarmer,id from Collection where ServerUpdatedStatus='false' order by id desc ";
        return "select Code,CollectionCenterCode,FarmerCode,WeighbridgeCenterId,WeighingDate,VehicleNumber,DriverName,GrossWeight,TareWeight ,NetWeight,OperatorName,Comments,TotalBunches,RejectedBunches,AcceptedBunches,Remarks ,GraderName ,ReceiptGeneratedDate,ReceiptCode,WBReceiptName,WBReceiptLocation,WBReceiptExtension,IsActive,CreatedByUserId ,Trpt,TokenNo,CreatedDate ,UpdatedByUserId,ServerUpdatedStatus,UpdatedDate,IsCollectionWithOutFarmer,id,VehicleTypeId,Name,MobileNumber,Village,Mandal,UnRipen,UnderRipe,Ripen,OverRipe,Diseased,EmptyBunches,FFBQualityLong,FFBQualityMedium,FFBQualityShort,FFBQualityOptimum,LooseFruit,LooseFruitWeight,GraderCode from Collection where ServerUpdatedStatus='false' order by id desc";
    }

    public String getCollectionFarmerWithoutPlotRefreshQuery() {
        return "select Code,CollectionCenterCode,FarmerCode,WeighbridgeCenterId,WeighingDate,VehicleNumber,DriverName," +
                "GrossWeight,TareWeight ,NetWeight,OperatorName,Comments,TotalBunches,RejectedBunches,AcceptedBunches," +
                "Remarks ,GraderName ,ReceiptGeneratedDate,ReceiptCode,WBReceiptName," +
                "WBReceiptLocation,WBReceiptExtension,IsActive,CreatedByUserId ,CreatedDate ,TokenNo," +
                "UpdatedByUserId,ServerUpdatedStatus,UpdatedDate,VehicleTypeId,Name,MobileNumber,Village,Mandal,UnRipen,UnderRipe,Ripen,OverRipe,Diseased,EmptyBunches,FFBQualityLong,FFBQualityMedium,FFBQualityShort,FFBQualityOptimum,LooseFruit,LooseFruitWeight,GraderCode  from CollectionWithOutPlot where ServerUpdatedStatus='false'";
    }
    public String getCollectionPlotXRefRefreshQuery() {

        return "select CollectionCode,PlotCode,ServerUpdatedStatus,YieldPerHectar,NetWeightPerPlot,IsMainFarmerPlot  from CollectionPlotXref where ServerUpdatedStatus='false'";
    }

    public String getConsignmentFileRepoRefreshQuery() {
        return "select ConsignmentCode,ModuleTypeId,FileName,FileLocation,FileExtension,IsActive,CreatedByUserId,CreatedDate,UpdatedByUserId,UpdatedDate,ServerUpdatedStatus  from  ConsignmentRepository where ServerUpdatedStatus = 'false'";
        //return "select ConsignmentCode,ModuleTypeId,FileName,FileLocation,FileExtension,IsActive,CreatedByUserId,CreatedDate,UpdatedByUserId,UpdatedDate,ServerUpdatedStatus  from  ConsignmentRepository where date(CreatedDate) = Date('2021-11-22') AND ServerUpdatedStatus = 'false'";
       // return "select ConsignmentCode,ModuleTypeId,FileName,FileLocation,FileExtension,IsActive,CreatedByUserId,CreatedDate,UpdatedByUserId,UpdatedDate,ServerUpdatedStatus  from  ConsignmentRepository where date(CreatedDate) BETWEEN Date('2021-10-05') AND  Date('2021-11-22') AND ServerUpdatedStatus = 'false'";
    }
    public String getConsignmentRefreshQuery() {
        return "select Code,CollectionCenterCode,VehicleNumber,DriverName,MillCode,TotalWeight,GrossWeight," +
                "TareWeight ,NetWeight,WeightDifference,ReceiptGeneratedDate,ReceiptCode ," +
                " IsActive,CreatedByUserId,CreatedDate ,UpdatedByUserId ,UpdatedDate,ServerUpdatedStatus, TotalBunches, RejectedBunches, AcceptedBunches, Remarks, GraderName, vehicleTypeId, DriverMobileNumber,SizeOfTruckId,IsSharing,TransportCost,SharingCost,OverWeightCost,ExpectedCost,ActualCost from Consignment where ServerUpdatedStatus='false'";
    }

    public String getStockTransferRefreshQuery(){
        return "Select * from StockTransfer where ServerUpdatedStatus = 'false' ";
    }
    public String getStockReceiveRefreshQuery(){
        return "Select Code,IsStockUpdate,UpdatedByUserId,UpdatedDate from StockTransferReceive where ServerUpdatedStatus = 'false' ";
    }

    public String getConsignementStatusHistoryRefreshQuery() {
        return "select ConsignmentCode ,StatusTypeId,OperatorName,Comments,IsActive,CreatedByUserId,CreatedDate,UpdatedByUserId," +
                "UpdatedDate,ServerUpdatedStatus from ConsignmentStatusHistory where ServerUpdatedStatus='false'";
    }
    public String getDateRangeCollectionReportDetails(String fromdate,String todate) {
        return "select Collection.Code ,WeighingDate,FarmerCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName," +
                " GrossWeight,TareWeight,NetWeight,VehicleNumber,CollectionCenter.InchargeName \n" +
                " from Collection\n" +
                " inner join Farmer on Farmer.Code=Collection.FarmerCode\n" +
                " inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode" +
                " WHERE date(WeighingDate) BETWEEN date('"+fromdate+"')" +
                " AND date('"+todate+"')";
    }
    public String getCollectionReportDetails() {
        return "select Collection.Code ,WeighingDate,FarmerCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName," +
                " GrossWeight,TareWeight,NetWeight,VehicleNumber,CollectionCenter.InchargeName, cXref.PlotCode \n" +
                " from Collection\n" +
                "inner join CollectionPlotXref cXref on cXref.CollectionCode = Collection.Code \n"+
                " inner join Farmer on Farmer.Code=Collection.FarmerCode\n" +
                " inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode";
    }
    public String getCollectionPlotCodes(String farmercode) {
        return "select Code  from Plot where FarmerCode='"+farmercode+"'";
    }

    public String getNewUserDetails(final String imeiNumber) {
        return "select u.UserId, u.UserName, u.Password, u.RoleId, u.ManagerId, u.TabletId from UserInfo u\n" +
                "inner join Tablet t on u.TabletId = t.Id where t.IMEINumber = '" + imeiNumber + "'";
    }

    public String getNewUserDetailsWithUserName(final String userName, final String password) {
//        return "select u.UserId, u.UserName, u.Password, u.RoleId, u.ManagerId, u.TabletId, uv.VillageId from UserInfo u\n" +
//                "inner join Tablet t on u.TabletId = t.Id " +
//                "inner join UserVillageXref uv on u.Id = uv.UserId where u.UserName = '" + userName + "'" + " and u.Password = '" + password + "'";

        return "select u.UserId, u.UserName, u.Password, u.RoleId, u.ManagerId, u.Id, u.FirstName from UserInfo u where u.UserName = '" + userName + "'" + " and u.Password = '" + password + "'";
    }

    public String getUserVillages(final String userId) {
        return "select villageId from UserVillageXref where userId IN ("+userId+")";
    }

    public String getPrivateWeighbridgeDetails() {
        return "select Id, Name from WeighBridgeCenter";
    }

    public String getMillInformation() {
        return "select Code, Name from CollectionCenter where IsMill = 'true' and IsActive='true'";
    }

    public String getCollectionCenterReports(final String fromDate, final String toDate) {
        String query = "select Collection.Code ,WeighingDate, Collection.FarmerCode as fCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName, GrossWeight,TareWeight,NetWeight,VehicleNumber,vehicleTypeId,Collection.Name as Harvestername,Collection.MobileNumber,Collection.UnRipen,Collection.UnderRipe,Collection.Ripen,Collection.OverRipe,Collection.Diseased,Collection.EmptyBunches,Collection.FFBQualityLong,Collection.FFBQualityMedium,Collection.FFBQualityShort,Collection.FFBQualityOptimum,Collection.LooseFruitWeight,CollectionCenter.InchargeName, cXref.PlotCode, DATE(substr(Collection.CreatedDate, 0, INSTR(Collection.CreatedDate, ' ') + 1)) date, Collection.ReceiptCode, cc.Name, (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName,\n" +
                "                (select BranchName from Bank where Id = B.BankId) as BranchName,\n" +
                "                B.AccountNumber, RejectedBunches,\n" +
                "                wc.Name as wcName, DATETIME(Collection.CreatedDate) as CreatedDate, Collection.CollectionCenterCode \n" +
                "                from Collection\n" +
                "                inner join CollectionPlotXref cXref on cXref.CollectionCode = Collection.Code\n" +
                "                inner join CollectionCenter cc on cc.Code = Collection.CollectionCenterCode\n" +
                "                inner join Farmer on Farmer.Code = Collection.FarmerCode\n" +
                "                inner join FarmerBank B on B.FarmerCode = Collection.FarmerCode\n" +
                "                left join WeighBridgeCenter wc on wc.Id = Collection.WeighbridgeCenterId\n" +
                "                inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode\n" +
                "                \n" +
                "union all\n" +
                "\n" +
                "select Collection.Code ,WeighingDate, Collection.FarmerCode as fCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName, GrossWeight,TareWeight,NetWeight,VehicleNumber, vehicleTypeId,Collection.Name as Harvestername,Collection.MobileNumber,Collection.UnRipen,Collection.UnderRipe,Collection.Ripen,Collection.OverRipe,Collection.Diseased,Collection.EmptyBunches,Collection.FFBQualityLong,Collection.FFBQualityMedium,Collection.FFBQualityShort,Collection.FFBQualityOptimum,Collection.LooseFruitWeight,CollectionCenter.InchargeName,'' PlotCode, DATE(substr(Collection.CreatedDate, 0, INSTR(Collection.CreatedDate, ' ') + 1)) date, Collection.ReceiptCode, cc.Name, (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName,\n" +
                "                (select BranchName from Bank where Id = B.BankId) as BranchName,\n" +
                "                B.AccountNumber, RejectedBunches,\n" +
                "                wc.Name as wcName, DATETIME(Collection.CreatedDate) as CreatedDate, Collection.CollectionCenterCode \n" +
                "                from CollectionWithOutPlot Collection\n" +
                "          \n" +
                "                inner join CollectionCenter cc on cc.Code = Collection.CollectionCenterCode\n" +
                "                inner join CollectionFarmer Farmer on Farmer.Code = Collection.FarmerCode\n" +
                "                inner join CollectionFarmerBank B on B.FarmerCode = Collection.FarmerCode\n" +
                "                left join WeighBridgeCenter wc on wc.Id = Collection.WeighbridgeCenterId\n" +
                "                inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode\n";

        if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
            query ="select Collection.Code ,WeighingDate, Collection.FarmerCode as fCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName, GrossWeight,TareWeight,NetWeight,VehicleNumber,vehicleTypeId,Collection.Name as Harvestername,Collection.MobileNumber,Collection.UnRipen,Collection.UnderRipe,Collection.Ripen,Collection.OverRipe,Collection.Diseased,Collection.EmptyBunches,Collection.FFBQualityLong,Collection.FFBQualityMedium,Collection.FFBQualityShort,Collection.FFBQualityOptimum,Collection.LooseFruitWeight,CollectionCenter.InchargeName, cXref.PlotCode, DATE(substr(Collection.CreatedDate, 0, INSTR(Collection.CreatedDate, ' ') + 1)) date, Collection.ReceiptCode, cc.Name, (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName,\n" +
                    "                                                        (select BranchName from Bank where Id = B.BankId) as BranchName,\n" +
                    "                                                        B.AccountNumber, RejectedBunches,\n" +
                    "                                                        wc.Name as wcName, DATETIME(Collection.CreatedDate) as CreatedDate,Collection.CollectionCenterCode\n" +
                    "                                                        from Collection\n" +
                    "                                                        inner join  (SELECT CollectionCode, GROUP_CONCAT(PlotCode,',') plotCode FROM CollectionPlotXref group by CollectionCode) cXref\n" +
                    "                                                         on cXref.CollectionCode =   Collection.Code\n" +
                    "                                                        inner join CollectionCenter cc on cc.Code = Collection.CollectionCenterCode\n" +
                    "                                                        inner join Farmer on Farmer.Code = Collection.FarmerCode\n" +
                    "                                                        inner join FarmerBank B on B.FarmerCode = Collection.FarmerCode\n" +
                    "                                                        left join WeighBridgeCenter wc on wc.Id = Collection.WeighbridgeCenterId\n" +
                    "                                                        inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode\n" +
                    "                                                        where date between '"+fromDate+"' and '"+toDate+"'\n" +
                    "                                                        \n" +
                    "                                        union all\n" +
                    "                                        \n" +
                    "                                       select Collection.Code ,WeighingDate, Collection.FarmerCode as fCode,Farmer. FirstName,Farmer.MiddleName,Farmer.LastName, GrossWeight,TareWeight,NetWeight,VehicleNumber,vehicleTypeId,Collection.Name as Harvestername,Collection.MobileNumber,Collection.UnRipen,Collection.UnderRipe,Collection.Ripen,Collection.OverRipe,Collection.Diseased,Collection.EmptyBunches,Collection.FFBQualityLong,Collection.FFBQualityMedium,Collection.FFBQualityShort,Collection.FFBQualityOptimum,Collection.LooseFruitWeight,CollectionCenter.InchargeName,'' PlotCode, DATE(substr(Collection.CreatedDate, 0, INSTR(Collection.CreatedDate, ' ') + 1)) date, Collection.ReceiptCode, cc.Name, (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName,\n" +
                    "                                                       (select BranchName from Bank where Id = B.BankId) as BranchName,\n" +
                    "                                                       B.AccountNumber, RejectedBunches,\n" +
                    "                                                    wc.Name as wcName, DATETIME(Collection.CreatedDate) as CreatedDate,Collection.CollectionCenterCode\n" +
                    "                                                        from CollectionWithOutPlot Collection\n" +
                    "                                \n" +
                    "                                                       \n" +
                    "                                                        inner join CollectionCenter cc on cc.Code = Collection.CollectionCenterCode\n" +
                    "                                                        inner join CollectionFarmer Farmer on Farmer.Code = Collection.FarmerCode\n" +
                    "                                                        inner join CollectionFarmerBank B on B.FarmerCode = Collection.FarmerCode\n" +
                    "                                                        left join WeighBridgeCenter wc on wc.Id = Collection.WeighbridgeCenterId\n" +
                    "                                                        inner join CollectionCenter on CollectionCenter.Code=Collection.CollectionCenterCode\n" +
                    "                                                        where date between '"+fromDate+"' and '"+toDate+"'\n" +
                    "       ";
        }

        //query = query + " group by Collection.Code  order by Collection.Code";

        return query;
    }

    public String getCollectionCenterReportsWithOutPlot(final String fromDate, final String toDate) {
        String query = "select cwf.Code ,WeighingDate, cwf.FarmerCode as fCode,CFarmer. FirstName,CFarmer.MiddleName,CFarmer.LastName, " +
                "GrossWeight,TareWeight,NetWeight,VehicleNumber,vehicleTypeId,cwf.Name,cwf.MobileNumber,cwf.UnRipen,cwf.UnderRipe,cwf.Ripen,cwf.OverRipe,cwf.Diseased,cwf.EmptyBunches,cwf.FFBQualityLong,cwf.FFBQualityMedium,cwf.FFBQualityShort,cwf.FFBQualityOptimum,cwf.LooseFruitWeight,CollectionCenter.InchargeName,\n" +
                " DATE(substr(cwf.CreatedDate, 0, INSTR(cwf.CreatedDate, ' ') + 1)) date, cwf.ReceiptCode, cc.Name,\n" +
                " (select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = CB.BankId)) as bankName,\n" +
                "(select BranchName from Bank where Id = CB.BankId) as BranchName,\n" +
                " CB.AccountNumber, RejectedBunches,\n" +
                " wc.Name as wcName, DATETIME(Collection.CreatedDate) as CreatedDate \n" +
                "from CollectionWithOutPlot cwf\n" +
                "inner join CollectionCenter cc on cc.Code = cwf.CollectionCenterCode\n" +
                "inner join CollectionFarmer CFarmer on CFarmer.Code = cwf.FarmerCode\n" +
                "inner join CollectionFarmerBank CB on CB.FarmerCode = cwf.FarmerCode\n" +
                "left join WeighBridgeCenter wc on wc.Id = cwf.WeighbridgeCenterId\n" +
                "inner join CollectionCenter on CollectionCenter.Code= cwf.CollectionCenterCode";

        if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
            query = query + " where date between '"+fromDate+"' and '"+toDate+"'";
        }

        query = query + " group by cwf.Code  order by cwf.Code";

        return query;
    }

    public String getConsignmentReportDetails(final String fromDate, final String toDate) {
        String  query="select CollectionCenter.Name,Consignment.Code,ReceiptGeneratedDate,MillCode ,TotalWeight,VehicleNumber,\n" +
                "                 ConsignmentStatusHistory.OperatorName,CollectionCenter.InchargeName, DATE(substr(Consignment.CreatedDate, 0, INSTR(Consignment.CreatedDate, ' ') + 1)) date, Consignment.ReceiptCode, Consignment.DriverName, cc.Name as CCNAME,cc.id,(CASE WHEN cd.Desc IS NULL THEN '0' ELSE cd.Desc END) as Desc,TransportCost,SharingCost,OverWeightCost,ExpectedCost,ActualCost,Consignment.CreatedDate \n" +
                "             from Consignment\n" +
                "                 inner join ConsignmentStatusHistory on ConsignmentStatusHistory.ConsignmentCode =Consignment.Code\n" +
                "                 inner join CollectionCenter cc on cc.Code = Consignment.CollectionCenterCode \n" +
                "                 inner join CollectionCenter on CollectionCenter.Code=Consignment.MillCode\n" +
                "                 left join TypecdDmt cd on cd.TypeCdId  = Consignment.SizeOfTruckId";

        if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
            query = query + " where date between '"+fromDate+"' and '"+toDate+"'";
        }
        return query;
    }

    public String queryVerifyFalogDistance() {

        //return "select Latitude, Longitude from LocationTracker where strftime('%Y-%m-%d',logDate) = (select strftime('%Y-%m-%d','now'))  order by id  desc limit 1";
        return "select Latitude, Longitude from LocationTracker ORDER BY Id DESC LIMIT 1";
    }

    public String getGpsTrackingRefresh() {
        return "select * from LocationTracker where ServerUpdatedStatus = '0' ";
    }

    public String getStockTransferReportDetails(final String fromDate, final String toDate) {
        String  stquery=" select st.Code,st.ReceiptCode,ReceiptGeneratedDate, fcc.Name as FromCC,tCC.Name as ToCC ,fcc.id as fromCCId,tCC.Id as toCCId, \n" +
                "  GrossWeight,TareWeight ,NetWeight,VehicleNumber,  DriverMobileNumber,UInfo.UserName,\n" +
                "  DATE(substr(st.CreatedDate, 0, INSTR(st.CreatedDate, ' ') + 1)) date, \n" +
                "  st.DriverName,fcc.InchargeName,st.CreatedDate\n" +
                "  from StockTransfer st\n" +
                "  inner JOIN UserInfo UInfo on UInfo.Id = st.CreatedByUserId\n" +
                "  inner join CollectionCenter fcc on fcc.Code = st.FromCC \n" +
                "  inner join CollectionCenter  tCC on tCC.Code = st.ToCC";

        if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
            stquery = stquery + " where date between '"+fromDate+"' and '"+toDate+"'";
        }
        return stquery;
    }

    public String getCollectionNetSum(final String fromDate, final String toDate) {
        return "select sum(NetWeight) from Collection  where DATE(substr(Collection.CreatedDate, 0, INSTR(Collection.CreatedDate, ' ') + 1)) between '"+fromDate+"' and '"+toDate+"'";
    }
    public String getCollectionWithOutPlotNetSum(final String fromDate, final String toDate) {
        return "select sum(NetWeight) from CollectionWithOutPlot  where DATE(substr(CollectionWithOutPlot.CreatedDate, 0, INSTR(CollectionWithOutPlot.CreatedDate, ' ') + 1)) between '"+fromDate+"' and '"+toDate+"'";
    }
    public String updateFileRepositoryTable(final String fileLocation, final String farmerCode) {
        return "update FileRepository set FileLocation = '"+fileLocation+"' where FarmerCode = '"+farmerCode+"'";
    }

    public String getStatusTypeId()
    {
        return "select TypeCdId from TypeCdDmt where Desc = 'Transit'";
    }

    public String getCollectionCenterStateId(String CCCode)
    {
        return "select StateId from CollectionCenter where Code = '"+CCCode+"'";
    }

//    //getTABLet ID---
//    public String getTabID(final String imieiNumber) {
//        return "select Id from Tablet where IMEINumber = '" + imieiNumber + "'";
//    }

    //getTablet Name--
    public String getTabName(final int id) {
        return "select Name from Tablet where Id = '" + id + "'";
    }
    public String getCCName(final int id) {
        return "select Code, Name from CollectionCenter where  Id = '" + id + "'";
    }

    public String getTabIDNew(final String imieiNumber) {
//        return "select Name from Tablet where IMEINumber = '"+imieiNumber+"'";

        return "select Id from Tablet where ((IMEINumber = '" + imieiNumber + "') OR (IMEINumber2 = '" + imieiNumber + "'))";
    }


    public String getTabId(final String imieiNumber) {
//        return "select Name from Tablet where IMEINumber = '"+imieiNumber+"'";

        return "select Name from Tablet where ((IMEINumber = '" + imieiNumber + "') OR (IMEINumber2 = '" + imieiNumber + "'))";
    }

    public String getUserDetailsNewQuery(String imeiNumber) {
//        return "SELECT u.UserId, u.UserName, u.Password, u.RoleId, u.ManagerId, u.Id, u.FirstName, t.Name \n" +
//                " from Tablet t\n" +
//                " inner join UserInfo u on u.TabletId = t.Id\n" +
//                " where t.IMEINumber = '"+ imeiNumber +"' and u.IsActive = 'true'";

        return  "SELECT u.UserId, u.UserName, u.Password, u.RoleId, u.ManagerId, u.Id, u.FirstName, t.Name, u.UserCode \n" +
                " from Tablet t\n" +
                " inner join UserInfo u on u.TabletId = t.Id\n" +
                " where (t.IMEINumber = '"+ imeiNumber +"'  OR  t.IMEINumber2 = '"+ imeiNumber +"') and u.IsActive = 'true' ";
    }

    public String getstateIds(String Userid) {
        return "select state.Id from State state\n" +
                "        where\n" +
                "       state.Id  IN\n" +
                "        (select distinct s.Id from UserVillageXref x \n" +
                "        inner join Village v on x.VillageId = v.Id \n" +
                "        inner join Mandal m on m.Id = v.MandalId \n" +
                "        inner join District d on d.Id = m.DistrictId \n" +
                "        inner join State s on s.Id = d.StateId\n" +
                "        where x.userId IN ('"+ Userid +"'))\n" +
                "       ORDER BY state.Id Asc";
    }

    public String getweighbridgecenteragainstdistricts(String Userid) {
        return "select wbc.Id, wbc.Name from WeighBridgeCenter wbc\n" +
                "        where\n" +
                "        wbc.DistrictId IN\n" +
                "        (select distinct d.Id from UserVillageXref x \n" +
                "        inner join Village v on x.VillageId = v.Id \n" +
                "        inner join Mandal m on m.Id = v.MandalId \n" +
                "        inner join District d on d.Id = m.DistrictId \n" +
                "        where x.userId IN ('"+ Userid +"')) and wbc.IsActive = 'true'\n" +
                "       ORDER BY wbc.Name Asc";
    }

    public String getweighbridgecenteragainststate(String Userid) {
        return " select wbc.Id, wbc.Name from WeighBridgeCenter wbc\n" +
                "        where\n" +
                "        wbc.StateId IN\n" +
                "        (select distinct s.Id from UserVillageXref x \n" +
                "        inner join Village v on x.VillageId = v.Id \n" +
                "        inner join Mandal m on m.Id = v.MandalId \n" +
                "        inner join District d on d.Id = m.DistrictId \n" +
                "        inner join State s on s.Id = d.StateId\n" +
                "        where x.userId IN ('"+ Userid +"')) and wbc.IsActive = 'true'\n" +
                "       ORDER BY wbc.Name Asc";
    }

    public String getPlotCodes(final String collectionCode) {
        return "SELECT PlotCode from CollectionPlotXref where CollectionCode = '"+collectionCode+"'";
    }

    public String getCollectionCodes() {
        return "select Code from Collection where ServerUpdatedStatus = 'false'";
    }

    public String getCollectionWithOutPlotCodes() {
        return "select Code from CollectionWithOutPlot where ServerUpdatedStatus = 'false'";
    }

    public String getConsignmentCodes() {
        return "select Code from Consignment where ServerUpdatedStatus = 'false'";
    }

    public String getCollectionPlotXrefData() {
        return "select DISTINCT(CollectionCode), PlotCode from CollectionPlotXRef where ServerUpdatedStatus = 'false'";
    }

    public String getPlotsCount(final String farmerCode) {
        return "select count(*) from FarmerHistory where FarmerCode = '"+farmerCode+"' and StatusTypeId = '89'";
    }

    /* Query for getting farmers data for CC data */
    public String getFarmersDataForCCWithLimit(int position) {
        return "select f.Code, f.FirstName, f.MiddleName, f.LastName, f.DOB, f.GuardianName,  f.MotherName,\n" +
                " s.Name as StateName, D.Name as DistrictName,\n" +
                " f.ContactNumber, f.MobileNumber, v.Name VillageName, v.Code AS VillageCode, v.Id as VillageId, s.Code as StateCode, s.Id as StateId, m.Code as MandalCode,  d.Code as DistrictCode,\n" +
                "   addr.AddressLine1, addr.AddressLine2, addr.AddressLine3, addr.Landmark, B.AccountNumber, fileRep.FileLocation, fileRep.FileName, fileRep.FileExtension, (select BranchName from Bank where Id = B.BankId) as BranchName,(select Desc from TypeCdDmt where TypeCdId IN(select BankTypeId from Bank where Id = B.BankId)) as bankName from Farmer f \n" +
                "   left join Village v on f.VillageId = v.Id\n" +
                "   left join Mandal m on f.MandalId = m.Id\n" +
                "   left join District d on f.DistrictId = d.Id\n" +
                "   left join Address addr on f.AddressCode = addr.Code\n" +
                "   left join FarmerBank B on B.FarmerCode = f.Code\n" +
                "   left join State s on f.StateId = s.Id\n" +
                "   left join FileRepository fileRep on f.Code = fileRep.FarmerCode\n" +
                " and fileRep.ModuleTypeId = 193 limit "+position+" , 20;";
    }

    public String checkRecordStatusInTable(String tableName, String columnName, String columnValue) {
        return "SELECT EXISTS(SELECT 1 FROM " + tableName + " where " + columnName + "= '" + columnValue + "'" + " LIMIT 1)";
    }

    public String getVehicleType() {
        return "SELECT * from TypeCddmt Where Classtypeid = 19";
    }

    public String getVehicleCategoryType() {
        return "SELECT * from TypeCddmt Where Classtypeid = 68";
    }

    public String getVehicleTypeonCategory(String TypeCdId) {
        return "SELECT * from LookUp Where LookUpTypeId = '"+TypeCdId+"'";
    }

    public String UpgradeCount() {
        return "select count(*) from UserInfo";
    }

    public String getGraderDetails(String CCCode) {
        return "select g.Code, g.Name, g.FingerPrintData1,g.FingerPrintData2, g.FingerPrintData3 from Grader g \n" +
        "INNER Join GraderCollectionCenterXref gx on gx.GraderCode = g.Code \n" +
        "Inner Join CollectionCenter cc on cc.Code = gx.CollectionCenterCode where cc.Code = '"+CCCode+"' AND g.IsActive = 'true' AND g.FingerPrintData1 != 'null' AND g.FingerPrintData2 != 'null' AND g.FingerPrintData3 != 'null' ";
    }

    public String getfingerprintActivityright(Integer user_roleID) {
        return "select count(0) from  RoleActivityRightXref rarx\n" +
                "    INNER JOIN ActivityRight ar ON ar.Id = rarx.ActivityRightId\n" +
                "WHERE \n" +
                "    RoleId ="+user_roleID+ " AND ar.Id = 114";
    }

    public String getActiveGraders(String seachKey, int offset, int limit) {
        return "Select Gr.Code as GraderCode ,Gr.Name as GraderName,Gr.MobileNumber,v.Name VillageName from Grader Gr\n" +
                "INNER JOIN Village v on Gr.VillageId = v.Id where Gr.IsActive = 'true' AND (Gr.Name like '%" + seachKey + "%'or Gr.MobileNumber like'%" + seachKey + "%' )";
    }
}

