package com.oilpalm3f.mainapp.collectioncenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.oilpalm3f.mainapp.CollectionwithoutPlotDbModles.CollectionWithOutPlot;
import com.oilpalm3f.mainapp.StockTransfer.StockReceiveRefresh;
import com.oilpalm3f.mainapp.StockTransfer.StockTransfer;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Collection;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionPlotXref;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignment;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentRepositoryRefresh;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.Consignmentstatushistory;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.database.DatabaseKeys;
import com.oilpalm3f.mainapp.database.Palm3FoilDatabase;
import com.oilpalm3f.mainapp.database.Queries;
import com.oilpalm3f.mainapp.dbmodels.CollectionClass;
import com.oilpalm3f.mainapp.dbmodels.ImageDetails;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.dbmodels.UserSync;
import com.oilpalm3f.mainapp.dbmodels.VisitLog;
import com.oilpalm3f.mainapp.utils.ImageUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@SuppressLint("SimpleDateFormat")
public class CCDataAccessHandler {

    private static final String LOG_TAG = CCDataAccessHandler.class.getName();
    public String COLLECTION_CENTER_CODE_INITIAL = "COL";
    public String COLLECTION_CENTER_CODE_WithOutPlot_INITIAL = "COLF";
    public String RECEIPT_CODE_INITIAL = "REC";
    public String ST_RECEIPT_CODE_INITIAL = "STR";
    public String RECEIPT_CODE_WithOutPlot_INITIAL = "RECF";
    public String CONSIGNMENT_CODE_INITIAL = "CON";
    public String STOCKTRANSFER_CODE_INITIAL = "CST";
    public String TABLE_COLLECTION = "Collection";
    public String TABLE_FORMER = "Farmer";
    public String TABLE_COLLECTION_WithOutPlot = "CollectionWithOutPlot";
    public String TABLE_COLLECTION_FORMER = "CollectionFarmer";
    public String TABLE_ADDRESS = "Address";
    public String TABLE_COLLECTION_PLOT_XREF = "CollectionPlotXref";
    public String TABLE_CONSIGNMENT = "Consignment";
    private SQLiteDatabase mDatabase;

    final String OLD_FORMAT = "dd/MM/yyyy hh:mm";
    final String NEW_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public CCDataAccessHandler(final Context context) {
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void executeRawQuery(String query) {
        try {
            if (mDatabase != null) {
                mDatabase.execSQL(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    //Set CollectionCenters Data
    public List<CollectionCenter> getCollectionCenters(final String query) {
        List<CollectionCenter> collectionCenterList = new ArrayList<>();
        Cursor dataCursor = null;
        try {
            Log.v(LOG_TAG, "@@@ query while getting collection centers " + query);
            dataCursor = mDatabase.rawQuery(query, null);
            if (dataCursor != null && dataCursor.moveToFirst()) {
                do {
                    CollectionCenter collectionCenter = new CollectionCenter();
                    collectionCenter.setCode(dataCursor.getString(0));
                    collectionCenter.setName(dataCursor.getString(1));
                    collectionCenter.setWeighBridgeTypeId(dataCursor.getInt(2));
                    collectionCenter.setWeignBridgeType(dataCursor.getString(3));
                    collectionCenter.setVillageId(dataCursor.getInt(4));
                    collectionCenter.setVillageCode(dataCursor.getString(5));
                    collectionCenter.setVillageName(dataCursor.getString(6));
                    collectionCenter.setStateId(dataCursor.getInt(7));
                    //collectionCenter.setDistance(dataCursor.getDouble(8));
                    collectionCenterList.add(collectionCenter);
                } while (dataCursor.moveToNext());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error while getting data from database " + ex.getMessage());
        } finally {
            if (dataCursor != null) {
                dataCursor.close();
            }
        }
        return collectionCenterList;
    }

    //Set Stock Transfer to CollectionCenters
    public List<CollectionCenter> getSTToCC(final String query) {
        List<CollectionCenter> collectionCenterList = new ArrayList<>();
        Cursor dataCursor = null;
        try {
            Log.v(LOG_TAG, "@@@ query while getting collection centers " + query);
            dataCursor = mDatabase.rawQuery(query, null);
            if (dataCursor != null && dataCursor.moveToFirst()) {
                do {
                    CollectionCenter collectionCenter = new CollectionCenter();
                    collectionCenter.setCode(dataCursor.getString(0));
                    collectionCenter.setCCId(dataCursor.getString(1));
                    collectionCenter.setName(dataCursor.getString(2));
                    collectionCenter.setWeighBridgeTypeId(dataCursor.getInt(3));
                    collectionCenter.setWeignBridgeType(dataCursor.getString(4));
                    collectionCenter.setVillageId(dataCursor.getInt(5));
                    collectionCenter.setVillageCode(dataCursor.getString(6));
                    collectionCenter.setVillageName(dataCursor.getString(7));
                    collectionCenterList.add(collectionCenter);
                } while (dataCursor.moveToNext());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error while getting data from database " + ex.getMessage());
        } finally {
            if (dataCursor != null) {
                dataCursor.close();
            }
        }
        return collectionCenterList;
    }
    //Collection CenterId--

    public List<CollectionCenter>  getGeneratedCollectionCenterId(final String query){
        List<CollectionCenter> collectionCenterId = new ArrayList<>();
        Cursor cursor = null;
        try {
            Log.v(LOG_TAG, "@@@ query while getting collection centers " + query);
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()){
                do {
                    CollectionCenter collectionId = new CollectionCenter();
                    collectionId.setCCId(cursor.getString(0));
                    collectionId.setName(cursor.getString(1));
                    collectionId.setWeighBridgeTypeId(cursor.getInt(2));
                    collectionId.setWeignBridgeType(cursor.getString(3));
                    collectionId.setVillageId(cursor.getInt(4));
                    collectionId.setVillageCode(cursor.getString(5));
                    collectionId.setVillageName(cursor.getString(6));
                    //collectionId.setDistance(cursor.getDouble(7));
                    collectionCenterId.add(collectionId);
                }while (cursor.moveToNext());
            }
        }catch (Exception e) {
            Log.e(LOG_TAG, "Error while getting data from database " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return collectionCenterId;
    }


//Get Plot Details for selected Farmer
    public List<PlotDetailsObj> getPlotDetails(String farmerCode) {
        List<PlotDetailsObj> plotDetailslistObj = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getPlotDetailsForCC(farmerCode.trim());
        Log.v(LOG_TAG, "Query for getting plots related to farmer "+query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    PlotDetailsObj bankobj = new PlotDetailsObj();
                    bankobj.setPlotID(cursor.getString(0));
                    bankobj.setTotalPalm(cursor.getString(1));
                    bankobj.setPlotArea(cursor.getString(2));
                    bankobj.setGpsPlotArea(cursor.getString(3));
                    bankobj.setSurveyNumber(cursor.getString(4));
                    bankobj.setPlotLandMark(cursor.getString(5));
                    bankobj.setVillageCode(cursor.getString(6));
                    bankobj.setVillageName(cursor.getString(7));
                    bankobj.setVillageId(cursor.getString(8));
                    bankobj.setMandalCode(cursor.getString(9));
                    bankobj.setFarmerMandalName(cursor.getString(10));
                    bankobj.setMandalId(cursor.getString(11));
                    bankobj.setDistrictCode(cursor.getString(12));
                    bankobj.setFarmerDistrictName(cursor.getString(13));
                    bankobj.setDistrictId(cursor.getString(14));
                    bankobj.setStateCode(cursor.getString(15));
                    bankobj.setFarmerStateName(cursor.getString(16));
                    bankobj.setStateId(cursor.getString(17));
                    plotDetailslistObj.add(bankobj);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return plotDetailslistObj;
    }

    //Get Stock Transfer Number

    public String getGenerateSTConvertedNum(String collectionCenterCode, final String initialCode, final String tableName) {
        String ccQuery = null;
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);

        String maxNum = getOnlyOneValueFromDb(ccQuery);
        int convertedNum = 0;
        if (!TextUtils.isEmpty(maxNum)) {
//            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) + 1, 6);
            convertedNum = Integer.parseInt(maxNum) + 1;
        } else {
            convertedNum = 1;
        }
        StringBuilder stConvertedNum = new StringBuilder();
        stConvertedNum.append(String.valueOf(convertedNum));
        Log.v(LOG_TAG, "@@@ ConvertedNum " + stConvertedNum.toString());
        return stConvertedNum.toString();
    }

    public String getGeneratedSTNumber(String collectionCenterCode, final String initialCode, final String tableName) {
        String ccQuery = null;

            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);

        String maxNum = getOnlyOneValueFromDb(ccQuery);
        int convertedNum = 0;
        if (!TextUtils.isEmpty(maxNum)) {
//            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) + 1, 6);
            convertedNum = Integer.parseInt(maxNum) + 1;
        } else {
            convertedNum = 1;
        }
        StringBuffer farmerCoder = new StringBuffer();
        farmerCoder.append(initialCode).append(getCurrentYear()).append(CommonConstants.TAB_ID.toUpperCase()).append(collectionCenterCode).append("-").append(String.valueOf(convertedNum));
        Log.v(LOG_TAG, "@@@ StockTransfer code " + farmerCoder.toString());
        return farmerCoder.toString();
    }


    //Generated ConvertedNumber for Consignment & Stock
    public String getGeneratedCollectionConvertedNum(String collectionCenterCode, final String initialCode, final String tableName) {
        String ccQuery = null;
        if (Objects.equals(initialCode, COLLECTION_CENTER_CODE_INITIAL)) {
            ccQuery = Queries.getInstance().getMaxCollectionCenterCode(getCurrentYear(), collectionCenterCode, tableName);

        } else if (Objects.equals(initialCode, CONSIGNMENT_CODE_INITIAL))  {
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);
        }else {
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);

        }
        String maxNum = getOnlyOneValueFromDb(ccQuery);
        int convertedNum = 0;
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = Integer.parseInt(maxNum) + 1;
        } else {
            convertedNum = 1;
        }
        StringBuilder stConvertedNum = new StringBuilder();
        stConvertedNum.append(String.valueOf(convertedNum));
        Log.v(LOG_TAG, "@@@ ConvertedNum " + stConvertedNum.toString());
        return stConvertedNum.toString();
    }

    //Generated ConvertedNumber for Collection
    public String getGeneratedCollectionCode(String collectionCenterCode, final String initialCode, final String tableName,String days) {
        String ccQuery = null;
        if (Objects.equals(initialCode, COLLECTION_CENTER_CODE_INITIAL)) {
            ccQuery = Queries.getInstance().getMaxCollectionCenterCode(getCurrentYear(), collectionCenterCode, tableName);
        } else if (Objects.equals(initialCode, CONSIGNMENT_CODE_INITIAL)){
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);
        }else {
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);

        }
        String maxNum = getOnlyOneValueFromDb(ccQuery);
        int convertedNum = 0;
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = Integer.parseInt(maxNum) + 1;
        } else {
            convertedNum = 1;
        }
        StringBuffer farmerCoder = new StringBuffer();
        farmerCoder.append(initialCode).append(getCurrentYear()).append(CommonConstants.TAB_ID.toUpperCase()).append(collectionCenterCode).append(days).append("-").append(String.valueOf(convertedNum));
        Log.v(LOG_TAG, "@@@ collection code " + farmerCoder.toString());
        return farmerCoder.toString();
    }



    // generate CollectionId for WithOutPlot

    public String getGeneratedCollectionCodeWithOutPlotFarmers(String collectionCenterCode, final String initialCode, final String tableName,String days) {
        String ccQuery = null;
        if (initialCode == COLLECTION_CENTER_CODE_WithOutPlot_INITIAL) {
            ccQuery = Queries.getInstance().getMaxCollectionCenterCode(getCurrentYear(), collectionCenterCode, tableName);
        } else {
            ccQuery = Queries.getInstance().getMaxReceiptCode(getCurrentYear(), collectionCenterCode, tableName);
        }
        String maxNum = getOnlyOneValueFromDb(ccQuery);
        int convertedNum = 0;
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = Integer.parseInt(maxNum) + 1;
        } else {
            convertedNum = 1;
        }
        StringBuffer farmerCoder = new StringBuffer();
        farmerCoder.append(initialCode).append(getCurrentYear()).append(CommonConstants.TAB_ID.toUpperCase()).append(collectionCenterCode).append(days).append("-").append(String.valueOf(convertedNum));
        Log.v(LOG_TAG, "@@@ collection code " + farmerCoder.toString());
        return farmerCoder.toString();
    }


//Use when we need only one Value from DB
    public String getOnlyOneValueFromDb(String query) {
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();
        }
        return "";
    }

    //To Get Current Year
    public String getCurrentYear() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        String yearInString = String.valueOf(year);
        return yearInString;
    }


    //To Get Image Details
    public List<ImageDetails> getImageDetails() {
        List<ImageDetails> imageDetailsList = new ArrayList<>();
        Cursor cursor = null;
        try {
           // mDatabase = palm3FoilDatabase.getReadableDatabase();
            cursor = mDatabase.rawQuery(Queries.getInstance().getImageDetails(), null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        if (cursor.getString(3) != null) {
                            ImageDetails rec = new ImageDetails();
                            rec.setCollectionCode(cursor.getString(0));
                            rec.setFarmerCode(cursor.getString(1));
                            rec.setPlotCode("");
                            if (cursor.getInt(2) == 100) {
                                rec.setTableName(DatabaseKeys.TABLE_COLLECTION);
                            } else if (cursor.getInt(2) == 200) {
                                rec.setTableName(DatabaseKeys.TABLE_CollectionWithOutPlot);
                            }
                            else {
                                rec.setTableName("");
                            }
                            if (cursor.getString(3) != null && cursor.getString(3).length()>0) {
                                String filepath = cursor.getString(3);
                                File imagefile = new File(filepath);
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(imagefile);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }


                                Bitmap bm = BitmapFactory.decodeStream(fis);
                                bm = ImageUtility.rotatePicture(90, bm);
                                String base64string = ImageUtility.convertBitmapToString(bm);
                                rec.setImageString(base64string);
                                imageDetailsList.add(rec);

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

        }
        return imageDetailsList;
    }

    //Get CollectionWithoutPlot Local Data
     public List<CollectionWithOutPlot> getCollectionWithoutPlotListRefresh(){
        List<CollectionWithOutPlot> collectionWithOutPlotListObj = new ArrayList<>();
        Cursor cursor = null;
        String Query = Queries.getInstance().getCollectionFarmerWithoutPlotRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(Query,null);
            if (cursor !=null && cursor.moveToFirst()){
                do {
                    CollectionWithOutPlot collectionWithOutPlotobj = new CollectionWithOutPlot();
                    collectionWithOutPlotobj.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    collectionWithOutPlotobj.setCollectioncentercode(cursor.getString(cursor.getColumnIndex("CollectionCenterCode")));
                    collectionWithOutPlotobj.setFarmercode(cursor.getString(cursor.getColumnIndex("FarmerCode")));
                    String weighBridgecenterid = cursor.getString(cursor.getColumnIndex("WeighbridgeCenterId"));
                    if (!TextUtils.isEmpty(weighBridgecenterid) && !weighBridgecenterid.equalsIgnoreCase("null")) {
                        collectionWithOutPlotobj.setWeighbridgecenterid(cursor.getInt(3));
                    } else {
                        collectionWithOutPlotobj.setWeighbridgecenterid(null);
                    }
//                    collectionobj.setWeighbridgecenterid(cursor.getColumnIndex("WeighbridgeCenterId"));
                    collectionWithOutPlotobj.setWeighingDate(cursor.getString(cursor.getColumnIndex("WeighingDate")));
                    collectionWithOutPlotobj.setVehiclenumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    collectionWithOutPlotobj.setDrivername(cursor.getString(cursor.getColumnIndex("DriverName")));
                    collectionWithOutPlotobj.setGrossweight(cursor.getDouble(cursor.getColumnIndex("GrossWeight")));
                    collectionWithOutPlotobj.setTareweight(cursor.getDouble(cursor.getColumnIndex("TareWeight")));
                    collectionWithOutPlotobj.setNetweight(cursor.getDouble(cursor.getColumnIndex("NetWeight")));
                    collectionWithOutPlotobj.setOperatorname(cursor.getString(cursor.getColumnIndex("OperatorName")));
                    collectionWithOutPlotobj.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    collectionWithOutPlotobj.setTotalbunches(cursor.getInt(cursor.getColumnIndex("TotalBunches")));
                    collectionWithOutPlotobj.setRejectedbunches(cursor.getInt(cursor.getColumnIndex("RejectedBunches")));
                    collectionWithOutPlotobj.setAcceptedbunches(cursor.getInt(cursor.getColumnIndex("AcceptedBunches")));
                    collectionWithOutPlotobj.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                    collectionWithOutPlotobj.setGradername(cursor.getString(cursor.getColumnIndex("GraderName")));
                    collectionWithOutPlotobj.setTokenNo(cursor.getInt(cursor.getColumnIndex("TokenNo")));

                    String receiptGeneratedDate = cursor.getString(cursor.getColumnIndex("ReceiptGeneratedDate"));
                    if (receiptGeneratedDate.contains("p.m.") || receiptGeneratedDate.contains("p.m")) {
                        receiptGeneratedDate = receiptGeneratedDate.replace("p.m.", "PM");
                    } else if (receiptGeneratedDate.contains("a.m.") || receiptGeneratedDate.contains("a.m")) {
                        receiptGeneratedDate = receiptGeneratedDate.replace("a.m.", "AM");
                    }



                    String newDateString = null;
                    SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                    Date d = null;
                    try {
                        d = sdf.parse(receiptGeneratedDate);
                        sdf.applyPattern(NEW_FORMAT);
                        newDateString = sdf.format(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    collectionWithOutPlotobj.setRecieptGeneratedDate((!TextUtils.isEmpty(newDateString) ? newDateString : receiptGeneratedDate));
                    collectionWithOutPlotobj.setRecieptcode(cursor.getString(cursor.getColumnIndex("ReceiptCode")));
                    collectionWithOutPlotobj.setRecieptname(cursor.getString(cursor.getColumnIndex("WBReceiptName")));
                    collectionWithOutPlotobj.setRecieptlocation(cursor.getString(cursor.getColumnIndex("WBReceiptLocation")));
                    collectionWithOutPlotobj.setRecieptextension(cursor.getString(cursor.getColumnIndex("WBReceiptExtension")));
                    collectionWithOutPlotobj.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    collectionWithOutPlotobj.setCreatedbyuserid(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    collectionWithOutPlotobj.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    collectionWithOutPlotobj.setUpdatedbyuserid(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    boolean value = (Objects.equals(cursor.getString(cursor.getColumnIndex("ServerUpdatedStatus")), "false"));
                    collectionWithOutPlotobj.setServerUpdatedStatus(value);

                    collectionWithOutPlotobj.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));

                    collectionWithOutPlotobj.setVehicleTypeId(cursor.getInt(cursor.getColumnIndex("VehicleTypeId")));
                    collectionWithOutPlotobj.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    collectionWithOutPlotobj.setMobileNumber(cursor.getString(cursor.getColumnIndex("MobileNumber")));
                    collectionWithOutPlotobj.setVillage(cursor.getString(cursor.getColumnIndex("Village")));
                    collectionWithOutPlotobj.setMandal(cursor.getString(cursor.getColumnIndex("Mandal")));
                    collectionWithOutPlotobj.setUnRipen(cursor.getDouble(cursor.getColumnIndex("UnRipen")));
                    collectionWithOutPlotobj.setUnderRipe(cursor.getDouble(cursor.getColumnIndex("UnderRipe")));
                    collectionWithOutPlotobj.setRipen(cursor.getDouble(cursor.getColumnIndex("Ripen")));
                    collectionWithOutPlotobj.setOverRipe(cursor.getDouble(cursor.getColumnIndex("OverRipe")));
                    collectionWithOutPlotobj.setDiseased(cursor.getDouble(cursor.getColumnIndex("Diseased")));
                    collectionWithOutPlotobj.setEmptyBunches(cursor.getDouble(cursor.getColumnIndex("EmptyBunches")));
                    collectionWithOutPlotobj.setFFBQualityLong(cursor.getDouble(cursor.getColumnIndex("FFBQualityLong")));
                    collectionWithOutPlotobj.setFFBQualityMedium(cursor.getDouble(cursor.getColumnIndex("FFBQualityMedium")));
                    collectionWithOutPlotobj.setFFBQualityShort(cursor.getDouble(cursor.getColumnIndex("FFBQualityShort")));
                    collectionWithOutPlotobj.setFFBQualityOptimum(cursor.getDouble(cursor.getColumnIndex("FFBQualityOptimum")));
                    collectionWithOutPlotobj.setLooseFruit(cursor.getInt(cursor.getColumnIndex("LooseFruit")));

                    if(cursor.getInt(cursor.getColumnIndex("LooseFruit")) == 1)
                        collectionWithOutPlotobj.setLooseFruitWeight(cursor.getInt(cursor.getColumnIndex("LooseFruitWeight")));
                    else
                        collectionWithOutPlotobj.setLooseFruitWeight(null);

                    collectionWithOutPlotobj.setGraderCode(cursor.getString(cursor.getColumnIndex("GraderCode")));

                    collectionWithOutPlotListObj.add(collectionWithOutPlotobj);

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor !=null){
                cursor.close();
            }
        }

        return collectionWithOutPlotListObj;
}

//Get Collection Local Data
    public CollectionClass getCollectionDetailsRefresh() {
        List<Collection> collectionDetailslistObj = new ArrayList<>();
        CollectionClass collectionClass = new CollectionClass();
        Cursor cursor = null;
        String data = "";
        String query = Queries.getInstance().getCollectionDetailsRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Collection collectionobj = new Collection();
                    collectionobj.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    collectionobj.setCollectioncentercode(cursor.getString(cursor.getColumnIndex("CollectionCenterCode")));
                    collectionobj.setFarmercode(cursor.getString(cursor.getColumnIndex("FarmerCode")));
                    String weighBridgecenterid = cursor.getString(cursor.getColumnIndex("WeighbridgeCenterId"));
                    if (!TextUtils.isEmpty(weighBridgecenterid) && !weighBridgecenterid.equalsIgnoreCase("null")) {
                        collectionobj.setWeighbridgecenterid(cursor.getInt(cursor.getColumnIndex("WeighbridgeCenterId")));
                    } else {
                        collectionobj.setWeighbridgecenterid(null);
                    }
                    collectionobj.setWeighingDate(cursor.getString(cursor.getColumnIndex("WeighingDate")));
                    collectionobj.setVehiclenumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    collectionobj.setDrivername(cursor.getString(cursor.getColumnIndex("DriverName")));
                    collectionobj.setGrossweight(cursor.getDouble(cursor.getColumnIndex("GrossWeight")));
                    collectionobj.setTareweight(cursor.getDouble(cursor.getColumnIndex("TareWeight")));
                    collectionobj.setNetweight(cursor.getDouble(cursor.getColumnIndex("NetWeight")));
                    collectionobj.setOperatorname(cursor.getString(cursor.getColumnIndex("OperatorName")));
                    collectionobj.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    collectionobj.setTotalbunches(cursor.getInt(cursor.getColumnIndex("TotalBunches")));
                    collectionobj.setRejectedbunches(cursor.getInt(cursor.getColumnIndex("RejectedBunches")));
                    collectionobj.setAcceptedbunches(cursor.getInt(cursor.getColumnIndex("AcceptedBunches")));
                    collectionobj.setRemarks(cursor.getString(cursor.getColumnIndex("Remarks")));
                    collectionobj.setGradername(cursor.getString(cursor.getColumnIndex("GraderName")));


                    collectionobj.setRecieptGeneratedDate(cursor.getString(cursor.getColumnIndex("ReceiptGeneratedDate")));
                    collectionobj.setRecieptcode(cursor.getString(cursor.getColumnIndex("ReceiptCode")));
                    collectionobj.setRecieptname(cursor.getString(cursor.getColumnIndex("WBReceiptName")));
                    collectionobj.setRecieptlocation(cursor.getString(cursor.getColumnIndex("WBReceiptLocation")));
                    collectionobj.setRecieptextension(cursor.getString(cursor.getColumnIndex("WBReceiptExtension")));
                    collectionobj.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    collectionobj.setCreatedbyuserid(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    collectionobj.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    collectionobj.setUpdatedbyuserid(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    boolean value = (cursor.getString(26) == "false");
                    collectionobj.setServerUpdatedStatus(false);
                    collectionobj.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    collectionobj.setCollectionWithOutFarmer(cursor.getString(cursor.getColumnIndex("IsCollectionWithOutFarmer")) == "true");
                    collectionobj.setTokenNo(cursor.getInt(cursor.getColumnIndex("TokenNo")));

                    if(cursor.isFirst())
                    {
                        data = cursor.getString(29);
                    }

                    collectionobj.setVehicleTypeId(cursor.getInt(cursor.getColumnIndex("VehicleTypeId")));
                    collectionobj.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    collectionobj.setMobileNumber(cursor.getString(cursor.getColumnIndex("MobileNumber")));
                    collectionobj.setVillage(cursor.getString(cursor.getColumnIndex("Village")));
                    collectionobj.setMandal(cursor.getString(cursor.getColumnIndex("Mandal")));
                    collectionobj.setUnRipen(cursor.getDouble(cursor.getColumnIndex("UnRipen")));
                    collectionobj.setUnderRipe(cursor.getDouble(cursor.getColumnIndex("UnderRipe")));
                    collectionobj.setRipen(cursor.getDouble(cursor.getColumnIndex("Ripen")));
                    collectionobj.setOverRipe(cursor.getDouble(cursor.getColumnIndex("OverRipe")));
                    collectionobj.setDiseased(cursor.getDouble(cursor.getColumnIndex("Diseased")));
                    collectionobj.setEmptyBunches(cursor.getDouble(cursor.getColumnIndex("EmptyBunches")));
                    collectionobj.setFFBQualityLong(cursor.getDouble(cursor.getColumnIndex("FFBQualityLong")));
                    collectionobj.setFFBQualityMedium(cursor.getDouble(cursor.getColumnIndex("FFBQualityMedium")));
                    collectionobj.setFFBQualityShort(cursor.getDouble(cursor.getColumnIndex("FFBQualityShort")));
                    collectionobj.setFFBQualityOptimum(cursor.getDouble(cursor.getColumnIndex("FFBQualityOptimum")));
                    collectionobj.setLooseFruit(cursor.getInt(cursor.getColumnIndex("LooseFruit")));

                    if(cursor.getInt(cursor.getColumnIndex("LooseFruit")) == 1)
                        collectionobj.setLooseFruitWeight(cursor.getInt(cursor.getColumnIndex("LooseFruitWeight")));
                    else
                        collectionobj.setLooseFruitWeight(null);

                    collectionobj.setGraderCode(cursor.getString(cursor.getColumnIndex("GraderCode")));

                    collectionDetailslistObj.add(collectionobj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        collectionClass.setCollectionList(collectionDetailslistObj);

        collectionClass.setData(data);
        return collectionClass;
    }

//Get User Sync Data
    public List<UserSync> getUserSyncData(final String query) {
        Cursor cursor = null;
        UserSync userSync = null;
        List<UserSync> userSyncList = new ArrayList<>();
        Log.v(LOG_TAG, "@@@ UserSync data " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userSync = new UserSync();
                    userSync.setUserId(cursor.getInt(cursor.getColumnIndex("UserId")));
                    userSync.setApp(cursor.getString(cursor.getColumnIndex("App")));
                    userSync.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                    userSync.setMasterSync(cursor.getInt(cursor.getColumnIndex("MasterSync")));
                    userSync.setTransactionSync(cursor.getInt(cursor.getColumnIndex("TransactionSync")));
                    userSync.setResetData(cursor.getInt(cursor.getColumnIndex("ResetData")));
                    userSync.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    userSync.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    userSync.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    userSync.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    userSync.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    userSync.setServerUpdatedStatus(cursor.getInt(cursor.getColumnIndex("ServerUpdatedStatus")));
                    userSyncList.add(userSync);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@  UserSyncdetails " + e.getMessage());
            e.printStackTrace();
        }
        return  userSyncList;

    }

    // Get Consignment File Repo Local Data
    public List<ConsignmentRepositoryRefresh> getConsignementFileRefresh() {
        List<ConsignmentRepositoryRefresh> ConsignmentDetailslistObj = new ArrayList<>();
        ConsignmentRepositoryRefresh consignmentobj = null;
        Cursor cursor = null;
        String query = Queries.getInstance().getConsignmentFileRepoRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                     consignmentobj = new ConsignmentRepositoryRefresh();
                    consignmentobj.setConsignmentCode(cursor.getString(cursor.getColumnIndex("ConsignmentCode")));
                    consignmentobj.setModuleTypeId(cursor.getInt(cursor.getColumnIndex("ModuleTypeId")));
                    consignmentobj.setFileName(cursor.getString(cursor.getColumnIndex("FileName")));
                    consignmentobj.setFileLocation(cursor.getString(cursor.getColumnIndex("FileLocation")));
                    consignmentobj.setFileExtension(cursor.getString(cursor.getColumnIndex("FileExtension")));
                    consignmentobj.setIsActive(cursor.getInt(cursor.getColumnIndex("IsActive")));
                    consignmentobj.setCreatedByUserId(cursor.getInt(cursor.getColumnIndex("CreatedByUserId")));
                    consignmentobj.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    consignmentobj.setUpdatedByUserId(cursor.getInt(cursor.getColumnIndex("UpdatedByUserId")));
                    consignmentobj.setUpdatedDate(cursor.getString(cursor.getColumnIndex("UpdatedDate")));
                    boolean value = (cursor.getString(10) == "false");
                    consignmentobj.setServerUpdatedStatus(value);
                    String filepath = consignmentobj.getFileLocation();
                    File imagefile = new File(filepath);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(imagefile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    bm = ImageUtility.rotatePicture(90, bm);
                    String base64string = ImageUtility.convertBitmapToString(bm);
                    consignmentobj.setByteImage(base64string);


                    ConsignmentDetailslistObj.add(consignmentobj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ConsignmentDetailslistObj;
    }

    //Get Stock Transfer Receive Data

    public List<StockReceiveRefresh> getStockTransferReceive(){
        List<StockReceiveRefresh> stockTransferReceiveList = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getStockReceiveRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query,null);
            if (cursor != null && cursor.moveToFirst()){
                do {
                    StockReceiveRefresh stockTransferReceive = new StockReceiveRefresh();
                    stockTransferReceive.setCode(cursor.getString(0));
                    stockTransferReceive.setStockUpdate(cursor.getInt(1));
                    stockTransferReceive.setUpdatedByUserId(cursor.getInt(2));
                    stockTransferReceive.setUpdatedDate(cursor.getString(3));
                    stockTransferReceiveList.add(stockTransferReceive);

                }while (cursor.moveToNext());
            }
        }catch (SQLiteException se){
            se.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return stockTransferReceiveList;
    }

    //Get Stock Transfer Data
    public List<StockTransfer> getStockTransferDetailsData(){
        List<StockTransfer> stockTransferList = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getStockTransferRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query,null);
            if (cursor != null && cursor.moveToFirst()){
                do {
                    StockTransfer stockTransfer = new StockTransfer();
                    stockTransfer.setCode(cursor.getString(1));
                    stockTransfer.setFromCC(cursor.getString(2));
                    stockTransfer.setVehicleNumber(cursor.getString(3));
                    stockTransfer.setDriverName(cursor.getString(4));
                    stockTransfer.setDriverMobileNumber(cursor.getString(5));
                    stockTransfer.setToCC(cursor.getString(6));
                    stockTransfer.setGrossWeight(cursor.getDouble(7));
                    stockTransfer.setTareWeight(cursor.getDouble(8));
                    stockTransfer.setNetWeight(cursor.getDouble(9));
                    stockTransfer.setWeightDifference(cursor.getDouble(10));
                    stockTransfer.setReceiptGeneratedDate(cursor.getString(11));
                    stockTransfer.setReceiptCode(cursor.getString(12));
                    stockTransfer.setTotalBunches(cursor.getInt(13));
                    stockTransfer.setRejectedBunches(cursor.getInt(14));
                    stockTransfer.setAcceptedBunches(cursor.getInt(15));
                    stockTransfer.setRemarks(cursor.getString(16));
                    stockTransfer.setGraderName(null);
                    stockTransfer.setStockUpdate(cursor.getInt(18));
                    stockTransfer.setActive(cursor.getInt(19));
                    stockTransfer.setCreatedByUserId(cursor.getInt(20));
                    stockTransfer.setCreatedDate(cursor.getString(21));
                    stockTransfer.setUpdatedByUserId(cursor.getInt(22));
                    stockTransfer.setUpdatedDate(cursor.getString(23));
                    boolean value = (Objects.equals(cursor.getString(24), "false"));
                    stockTransfer.setServerUpdatedStatus(value);
                    stockTransferList.add(stockTransfer);

                }while (cursor.moveToNext());
            }
        }catch (SQLiteException se){
            se.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return stockTransferList;
    }

    // Get Consignment Local Data
    public List<Consignment> getConsignementRefresh() {
        List<Consignment> ConsignmentDetailslistObj = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getConsignmentRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Consignment consignmentobj = new Consignment();
                    consignmentobj.setCode(cursor.getString(0));
                    consignmentobj.setCollectioncentercode(cursor.getString(1));
                    consignmentobj.setVehiclenumber(cursor.getString(2));
                    consignmentobj.setDrivername(cursor.getString(3));
                    consignmentobj.setMillcode(cursor.getString(4));
                    consignmentobj.setTotalweight(cursor.getDouble(5));
                    consignmentobj.setGrossweight(cursor.getDouble(6));
                    consignmentobj.setTareweight(cursor.getDouble(7));
                    consignmentobj.setNetweight(cursor.getDouble(8));
                    consignmentobj.setWeightdifference(cursor.getDouble(9));
                    consignmentobj.setRecieptGeneratedDate(cursor.getString(10));
                    consignmentobj.setReceiptCode(cursor.getString(11));
                    consignmentobj.setIsActive(cursor.getInt(12));
                    consignmentobj.setCreatedbyuserid(cursor.getInt(13));
                    consignmentobj.setCreatedDate(cursor.getString(14));
                    consignmentobj.setUpdatedbyuserid(cursor.getInt(15));
                    consignmentobj.setUpdatedDate(cursor.getString(16));
                    boolean value = (Objects.equals(cursor.getString(17), "false"));
                    consignmentobj.setServerUpdatedStatus(value);
                    consignmentobj.setTotalBunches(cursor.getInt(18));
                    consignmentobj.setRejectedBunches(cursor.getInt(19));
                    consignmentobj.setAcceptedBunches(cursor.getInt(20));
                    consignmentobj.setRemarks(cursor.getString(21));
                    consignmentobj.setGraderName(cursor.getString(22));
                    consignmentobj.setVehcileType(cursor.getString(23));
                    consignmentobj.setDriverMobileNumber(cursor.getString(24));
                    consignmentobj.setSizeOfTruckId(cursor.getInt(25));
                    consignmentobj.setSharing(Boolean.parseBoolean(cursor.getString(26)));
                    consignmentobj.setTransportCost(cursor.getFloat(27));
                    consignmentobj.setSharingCost(cursor.getFloat(28));
                    consignmentobj.setOverWeightCost(cursor.getFloat(29));
                    consignmentobj.setExpectedCost(cursor.getFloat(30));
                    consignmentobj.setActualCost(cursor.getFloat(31));

                    ConsignmentDetailslistObj.add(consignmentobj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ConsignmentDetailslistObj;
    }

    // Get Consignment History Local Data
    public List<Consignmentstatushistory> getConsignementHistoryRefresh() {
        List<Consignmentstatushistory> ConsignmentDetailslistObj = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getConsignementStatusHistoryRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    Consignmentstatushistory consignmentobj = new Consignmentstatushistory();
                    consignmentobj.setConsignmentcode(cursor.getString(0));
                    consignmentobj.setStatustypeid(cursor.getInt(1));
                    consignmentobj.setOperatorname(cursor.getString(2));
                    consignmentobj.setComments(cursor.getString(3));
                    consignmentobj.setIsActive(cursor.getInt(4));
                    consignmentobj.setCreatedbyuserid(cursor.getInt(5));
                    consignmentobj.setCreatedDate(cursor.getString(6));
                    consignmentobj.setUpdatedbyuserid(cursor.getInt(7));
                    consignmentobj.setUpdatedDate(cursor.getString(8));
                    boolean value = (cursor.getString(9) == "false");
                    consignmentobj.setServerUpdatedStatus(value);
                    ConsignmentDetailslistObj.add(consignmentobj);

                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ConsignmentDetailslistObj;
    }

    //Get Collection Plot XRef  Local Data
    public List<CollectionPlotXref> getCollectionPlotXRefRefresh() {
        List<CollectionPlotXref> CollectionPlotXreflistObj = new ArrayList<>();
        Cursor cursor = null;
        String query = Queries.getInstance().getCollectionPlotXRefRefreshQuery();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CollectionPlotXref consignmentobj = new CollectionPlotXref();
                    consignmentobj.setCollectionCode(cursor.getString(0));
                    consignmentobj.setPlotcode(cursor.getString(1));
                    boolean value = (cursor.getString(2) == "false");
                    consignmentobj.setServerUpdatedStatus(value);
                    consignmentobj.setYieldPerHectar(cursor.getFloat(3));
                    consignmentobj.setNetWeightPerPlot(cursor.getFloat(4));
                    boolean mainFarmerValue;
                    if (cursor.getString(5).equalsIgnoreCase("false")) {
                        mainFarmerValue = false;
                    } else {
                        mainFarmerValue = true;
                    }
                    consignmentobj.setMainFarmerPlot(mainFarmerValue);
                    CollectionPlotXreflistObj.add(consignmentobj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return CollectionPlotXreflistObj;
    }



//Get Visit Log Local Data
    public List<VisitLog> getVisitLogData(final String query){
        Cursor cursor = null;
        VisitLog visitLog = null;
        List<VisitLog> visitLogList = new ArrayList<>();
        Log.v(LOG_TAG,"@@@ VisitLog data "+ query);
        try {
            cursor = mDatabase.rawQuery(query,null);
            if (cursor != null && cursor.moveToFirst()){
                do {
                    visitLog = new VisitLog();
                    visitLog.setClientName(cursor.getString(1));
                    visitLog.setMobileNumber(cursor.getString(2));
                    visitLog.setLocation(cursor.getString(3));
                    visitLog.setDetails(cursor.getString(4));
                    visitLog.setLatitude(cursor.getFloat(5));
                    visitLog.setLongitude(cursor.getFloat(6));
                    visitLog.setCreatedByUserId(cursor.getInt(7));
                    visitLog.setCreatedDate(cursor.getString(8));
                    visitLog.setServerUpdatedStatus(cursor.getInt(9));

                    visitLogList.add(visitLog);
                }while (cursor.moveToNext());
            }
        }catch (Exception se){
            Log.e(LOG_TAG, "@@@ getting VisitLog details " + se.getMessage());
            se.printStackTrace();
        }

        return visitLogList;
    }

    public  Void getVechildType() {
        Cursor cursor = null;
        String query = Queries.getInstance().getVehicleType();
        Log.v(LOG_TAG, "Query for getting Vehcile " + query);
        cursor = mDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                String s = cursor.getString(0);
                String s1 = cursor.getString(1);
                String s2 = cursor.getString(2);
                String s3 = cursor.getString(3);

            } while (cursor.moveToNext());
        }
        Log.i(LOG_TAG, "" + cursor.getCount());
        return null;
    }

    }
