package com.oilpalm3f.mainapp.database;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;

import com.oilpalm3f.mainapp.FaLogTracking.LocationTracker;
import com.oilpalm3f.mainapp.GraderFingerprint.GraderBasicDetails;
import com.oilpalm3f.mainapp.StockTransfer.StockTransferReportModel;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.collectioncenter.CollectionReport;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionCenter;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.CollectionReportModel;
import com.oilpalm3f.mainapp.collectioncenter.collectioncentermodels.ConsignmentReportModel;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.dbmodels.CcRate;
import com.oilpalm3f.mainapp.dbmodels.GraderDetails;
import com.oilpalm3f.mainapp.dbmodels.PlotDetailsObj;
import com.oilpalm3f.mainapp.dbmodels.UserDetails;
import com.oilpalm3f.mainapp.dbmodels.UserSync;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.dbmodels.BasicFarmerDetails;
import com.oilpalm3f.mainapp.dbmodels.MasterDataQuery;
import com.oilpalm3f.mainapp.weighbridge.CollectionCenter1;
import com.oilpalm3f.mainapp.weighbridge.TokenTable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataAccessHandler {

    private static final String LOG_TAG = DataAccessHandler.class.getName();

    private Context context;
    private Palm3FoilDatabase palm3FoilDatabase;
    private SQLiteDatabase mDatabase;

    public DataAccessHandler() {

    }

    SimpleDateFormat simpledatefrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    String currentTime = simpledatefrmt.format(new Date());


    public DataAccessHandler(final Context context) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
            DataBaseUpgrade.upgradeDataBase(context, mDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataAccessHandler(final Context context, boolean firstTime) {
        this.context = context;
        try {
            mDatabase = Palm3FoilDatabase.openDataBaseNew();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


//Get Last Token Number from TokenTable
    public TokenTable getLastTokenNoTa(String query) {
        TokenTable collectionCenter1 = null;
        Cursor cursor = null;
        android.util.Log.v(LOG_TAG, "Token query" + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    collectionCenter1 = new TokenTable();
                    collectionCenter1.setTokenNo(cursor.getInt(cursor.getColumnIndex("TokenNo")));


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collectionCenter1;
    }

    //get Token Details From Token Table
    public TokenTable getgrossWeightDetails(String query) {
        TokenTable tokenTable = null;
        Cursor cursor = null;
        android.util.Log.v(LOG_TAG, "Token query" + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    tokenTable = new TokenTable();
                    tokenTable.setCollId(cursor.getString(cursor.getColumnIndex("CollId")));
                    tokenTable.setWeighbridgeName(cursor.getString(cursor.getColumnIndex("WeighbridgeName")));
                    tokenTable.setWeighingDate(cursor.getString(cursor.getColumnIndex("WeighingDate")));
                    tokenTable.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    tokenTable.setDriverName(cursor.getString(cursor.getColumnIndex("DriverName")));
                    tokenTable.setOperatorName(cursor.getString(cursor.getColumnIndex("OperatorName")));
                    tokenTable.setComments(cursor.getString(cursor.getColumnIndex("Comments")));
                    tokenTable.setPostingDate(cursor.getString(cursor.getColumnIndex("PostingDate")));
                    tokenTable.setCollectionCenterCode(cursor.getString(cursor.getColumnIndex("CollectionCenterCode")));
                    tokenTable.setGrossWeight(cursor.getFloat(cursor.getColumnIndex("GrossWeight")));
                    tokenTable.setVehicleTypeId(cursor.getInt(cursor.getColumnIndex("VehicleTypeId")));


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenTable;
    }

    //To Get CollectionCenter Type
    public CollectionCenter getCOllectionType(String query) {
        CollectionCenter collectionCenter1 = null;
        Cursor cursor = null;
        android.util.Log.v(LOG_TAG, "Token query" + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    collectionCenter1 = new CollectionCenter();
                    collectionCenter1.setCollectionType(cursor.getString(cursor.getColumnIndex("CollectionType")));
                    collectionCenter1.setReadMethod(cursor.getString(cursor.getColumnIndex("ReadMethod")));
                    collectionCenter1.setNoOfChars(cursor.getString(cursor.getColumnIndex("NoOfChars")));
                    collectionCenter1.setUpToCharacter(cursor.getString(cursor.getColumnIndex("UpToCharacter")));

//                    int columnIndex = cursor.getColumnIndexOrThrow("IsFingerPrintReq");
//                    Log.d("columnIndex", columnIndex + "");
//                    int value = cursor.getInt(columnIndex);
//                    Log.d("value", value + "");
//                    boolean booleanValue = (value == 1); // Check for value == 1 instead of value != 0
//                    Log.d("booleanValue", booleanValue + "");

                    collectionCenter1.setFingerPrintReq(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("IsFingerPrintReq"))));

                    Log.d("setFingerPrintReq", collectionCenter1.getFingerPrintReq()+ "");


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collectionCenter1;
    }

//To get Paired Data
    public LinkedHashMap<String, String> getGenericData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(1));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
//            palm3FoilDatabase.closeDataBase();
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }

    //To get Paired Data
    public LinkedHashMap<String, String> getvechileData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(2));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
//            palm3FoilDatabase.closeDataBase();
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    //To Insert Usersync Table record
    public long addUserSync(UserSync userSync) {


        ContentValues contentValues = new ContentValues();
        contentValues.put("UserId", userSync.getUserId());
        contentValues.put("App", userSync.getApp());
        contentValues.put("Date", userSync.getDate());
        contentValues.put("MasterSync", userSync.getMasterSync());
        contentValues.put("TransactionSync", userSync.getTransactionSync());
        contentValues.put("ResetData", userSync.getResetData());
        contentValues.put("IsActive", userSync.getIsActive());
        contentValues.put("CreatedByUserId", userSync.getCreatedByUserId());
        contentValues.put("CreatedDate", userSync.getCreatedDate());
        contentValues.put("UpdatedByUserId", userSync.getUpdatedByUserId());
        contentValues.put("UpdatedDate", userSync.getUpdatedDate());
        contentValues.put("ServerUpdatedStatus", userSync.getServerUpdatedStatus());
        return mDatabase.insert("UserSync", null, contentValues);


    }
    //To Insert record in TokenTable
    public long addTokenTable(TokenTable tokenTable) {


        ContentValues contentValues = new ContentValues();
        contentValues.put("TokenNo", tokenTable.getTokenNo());
        contentValues.put("CollId", tokenTable.getCollId());
        contentValues.put("WeighbridgeName", tokenTable.getWeighbridgeName());
        contentValues.put("WeighingDate", tokenTable.getWeighingDate());
        contentValues.put("VehicleNumber", tokenTable.getVehicleNumber());
        contentValues.put("DriverName", tokenTable.getDriverName());
        contentValues.put("OperatorName", tokenTable.getOperatorName());
        contentValues.put("Comments", tokenTable.getComments());
        contentValues.put("PostingDate", tokenTable.getPostingDate());
        contentValues.put("GrossWeight", tokenTable.getGrossWeight());
        contentValues.put("CreatedDate",tokenTable.getCreatedDate());
        contentValues.put("CollectionCenterCode",tokenTable.getCollectionCenterCode());
        contentValues.put("VehicleTypeId",tokenTable.getVehicleTypeId());


        return mDatabase.insert("TokenTable", null, contentValues);


    }


    public void updateUserSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ServerUpdatedStatus", 1);
        mDatabase.update("UserSync", contentValues, "ServerUpdatedStatus='0'", null);
        Log.v("@@@MM", "Updating");

    }

    //Update Usersync table when Master Sync is done
    public void updateMasterSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("MasterSync", 1);
        contentValues.put("ServerUpdatedStatus", 0);
        contentValues.put("UpdatedByUserId",  CommonConstants.USER_ID);
        //contentValues.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        contentValues.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        //contentValues.put("Date", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        //mDatabase.update("UserSync", contentValues, null, null);
        mDatabase.update("UserSync",contentValues,"DATE(CreatedDate) = DATE('now') AND UserId = '" + CommonConstants.USER_ID +"' AND App = '3fCropCollection'",null);

        Log.v("@@@MMMaster", "Updating");
    }

    //Update Usersync table when Transaction Sync is done
    public void updateTransactionSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TransactionSync", 1);
        contentValues.put("ServerUpdatedStatus", 0);
        contentValues.put("UpdatedByUserId",  CommonConstants.USER_ID);
        //contentValues.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        contentValues.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
       // contentValues.put("Date", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        // mDatabase.update("UserSync",contentValues,"ServerUpdatedStatus='0'",null);
        mDatabase.update("UserSync",contentValues,"DATE(CreatedDate) = DATE('now') AND UserId = '" + CommonConstants.USER_ID +"' AND App = '3fCropCollection'",null);
        //mDatabase.update("UserSync", contentValues, null, null);
        Log.v("@@@MMTransaction", "Updating");
    }

    //Update Usersync table when Reset Data is done
    public void updateResetDataSync() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ResetData", 1);
        contentValues.put("ServerUpdatedStatus", 0);
        contentValues.put("UpdatedByUserId",  CommonConstants.USER_ID);
        //contentValues.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
        contentValues.put("UpdatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
       // contentValues.put("Date", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));

        mDatabase.update("UserSync",contentValues,"DATE(CreatedDate) = DATE('now') AND UserId = '" + CommonConstants.USER_ID +"' AND App = '3fCropCollection'",null);

      //  mDatabase.update("UserSync", contentValues, null, null);
        Log.v("@@@MMReset", "Updating");
    }

    public String countOfSync() {
        return "Select * from UserSync where DATE(CreatedDate)= DATE('now') ";
    }


    //To get Usersync data
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
        return userSyncList;

    }


    public LinkedHashMap<String, String> getMoreGenericData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, String> mGenericData = new LinkedHashMap<>();
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor genericDataQuery = mDatabase.rawQuery(query, null);
        try {
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), genericDataQuery.getString(1) + "-" + genericDataQuery.getString(2) + "-" + genericDataQuery.getString(3));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
//            palm3FoilDatabase.closeDataBase();
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }

//To get Pair Data
    public LinkedHashMap<String, Pair> getPairData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        LinkedHashMap<String, Pair> mGenericData = new LinkedHashMap<>();
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.put(genericDataQuery.getString(0), Pair.create(genericDataQuery.getString(1), genericDataQuery.getString(2)));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
//            palm3FoilDatabase.closeDataBase();
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    //ToCheck the Value exists in DB or not
    public boolean checkValueExistedInDatabase(final String query) {
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor mOprQuery = mDatabase.rawQuery(query, null);
        try {
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return (mOprQuery.getInt(0) > 0);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return false;
    }

//To Get Only one value from DB
    public String getOnlyOneValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
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

            closeDataBase();
        }
        return "";
    }

    //To get two values from DB
    public String getTwoValueFromDb(String query) {
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0) + "@" + mOprQuery.getString(1);

            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return "";
    }

    public byte[] getImageFromDb(String query) {
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor mOprQuery = mDatabase.rawQuery(query, null);
        try {
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getBlob(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }

    public String getGeneratedFarmerCode(final String query) {
        String maxNum = getOnlyOneValueFromDb(query);
        String convertedNum = "";
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) + 1, 4);
        } else {
            convertedNum = CommonUtils.serialNumber(1, 4);
        }
        StringBuffer farmerCoder = new StringBuffer();

        farmerCoder.append(CommonConstants.stateId)
                .append(CommonConstants.districtId)
                .append(CommonConstants.mandalId)
                .append(CommonConstants.villageId)
                .append(CommonConstants.TAB_ID)
                .append(convertedNum);
        Log.v(LOG_TAG, "@@@ farmer code " + farmerCoder.toString());
        return farmerCoder.toString();
    }


    public String getGeneratedPlotId(final String query, final String villageId, final String mandalID) {
        String maxNum = getOnlyOneValueFromDb(query);
        String convertedNum = "";
        if (!TextUtils.isEmpty(maxNum)) {
            convertedNum = CommonUtils.serialNumber(Integer.parseInt(maxNum) + 1, 6);
        } else {
            convertedNum = CommonUtils.serialNumber(1, 6);
        }
        StringBuffer farmerCoder = new StringBuffer();
        farmerCoder.append(CommonConstants.stateId).append(mandalID).append(villageId).append(CommonConstants.TAB_ID).append(convertedNum);
        Log.v(LOG_TAG, "@@@ farmer code " + farmerCoder.toString());
        return farmerCoder.toString();
    }


    public void updateMasterSyncDate(final boolean isNotFirstTime, String userId) {
        final List<LinkedHashMap> listMap = new ArrayList<>();
        final LinkedHashMap dataMap = new LinkedHashMap();
        dataMap.put(DatabaseKeys.COLUMN_USERID, (null == userId) ? "1" : userId);
        final String finalUserId = userId;
        dataMap.put(DatabaseKeys.COLUMN_UPDATEDON, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY));
        listMap.add(dataMap);
        ApplicationThread.dbPost("MasterVersionTrackingSystem Saving..", "insert", new Runnable() {
            @Override
            public void run() {
                if (isNotFirstTime) {
                    insertData(DatabaseKeys.TABLE_MASTERVERSIONTRACKINGSYSTEM, listMap, context, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void run() {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateMasterSyncDate", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                PrefUtil.putBool(context, CommonConstants.IS_MASTER_SYNC_SUCCESS, true);
                                Log.v(LOG_TAG, "@@@ MasterVersionTrackingSystem inserted ");
                            }else{
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"updateMasterSyncDate", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            }
                        }
                    });
                } else {
                    String whereCondition = "  where " + DatabaseKeys.COLUMN_USERID + "='" + finalUserId + "'";
                    updateData(DatabaseKeys.TABLE_MASTERVERSIONTRACKINGSYSTEM, listMap, false, "", whereCondition, context);
                    Log.v(LOG_TAG, "@@@ MasterVersionTrackingSystem updated ");
                }
            }
        });
    }

    /**
     * Inserting data into database
     *
     * @param tableName ---> Table name to insert data
     * @param mapList   ---> map list which contains data
     * @param context   ---> Current class context
     */
    public synchronized void insertData(String tableName, List<LinkedHashMap> mapList, Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        boolean errorMessageSent = false;
        try {
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());
                String query = "insert into " + tableName;
                String namestring, valuestring;
                StringBuffer values = new StringBuffer();
                StringBuffer columns = new StringBuffer();
                for (LinkedHashMap.Entry temp : entryList) {

                    columns.append(temp.getKey());
                    columns.append(",");
                    values.append("'");
                    values.append(temp.getValue());
                    values.append("'");
                    values.append(",");
                }
                namestring = "(" + columns.deleteCharAt(columns.length() - 1).toString() + ")";
                valuestring = "(" + values.deleteCharAt(values.length() - 1).toString() + ")";
                query = query + namestring + "values" + valuestring;
                Log.v(getClass().getSimpleName(), "query.." + query);
                Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + mapList.size());
                try {
                    mDatabase.execSQL(query);
                } catch (Exception e) {
                    Log.v(LOG_TAG, "@@@ Error while inserting data " + e.getMessage());
                    if (checkCount == mapList.size()) {
                        errorMessageSent = true;
                        if (null != oncomplete)
                            oncomplete.execute(false, "failed to insert data", "");
                    }
                }
                if (checkCount == mapList.size() && !errorMessageSent) {
                    if (null != oncomplete)
                        oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    //To Insert MastersData Into Database
    public synchronized void insertData(boolean fromMaster, String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        int checkCount = 0;
        try {
            List<ContentValues> values1 = new ArrayList<>();
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());

                ContentValues contentValues = new ContentValues();
                for (LinkedHashMap.Entry temp : entryList) {
                    String keyToInsert = temp.getKey().toString();
                    if (!fromMaster) {
                        if (keyToInsert.equalsIgnoreCase("Id"))
                            continue;
                    }
                    if (keyToInsert.equalsIgnoreCase("ServerUpdatedStatus")) {
                        contentValues.put(keyToInsert, "1");
                    } else {
                        contentValues.put(temp.getKey().toString(), temp.getValue().toString());
                    }
                }
                values1.add(contentValues);
            }
            Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + values1.size());
            boolean hasError = bulkinserttoTable(values1, tableName);
            if (hasError) {
                Log.v(LOG_TAG, "@@@ Error while inserting data ");
                if (null != oncomplete) {
                    oncomplete.execute(false, "failed to insert data", "");
                }
            } else {
                Log.v(LOG_TAG, "@@@ data inserted successfully for table :" + tableName);
                if (null != oncomplete) {
                    oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    //To Insert Bulk data into tables
    public boolean bulkinserttoTable(List<ContentValues> cv, final String tableName) {
        boolean isError = false;
        mDatabase.beginTransaction();
        try {
            for (int i = 0; i < cv.size(); i++) {
                ContentValues stockResponse = cv.get(i);
                long id = mDatabase.insert(tableName, null, stockResponse);
                if (id < 0) {
                    isError = true;
                }
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
        return isError;
    }

    /**
     * Inserting data into database
     *
     * @param tableName ---> Table name to insert data
     * @param mapList   ---> map list which contains data
     * @param context   ---> Current class context
     */
    public synchronized void insertDataTrans(String tableName, List<LinkedHashMap> mapList, Context context, final ApplicationThread.OnComplete<String> oncomplete) {
        if (!ApplicationThread.dbThreadCheck())
            Log.e(LOG_TAG, "called on non-db thread", new RuntimeException());
        int checkCount = 0;
        try {
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());
                String query = "insert into " + tableName;
                String namestring, valuestring;
                StringBuffer values = new StringBuffer();
                StringBuffer columns = new StringBuffer();
                for (LinkedHashMap.Entry temp : entryList) {
                    if (temp.getKey().equals("Id"))
                        continue;
                    columns.append(temp.getKey());
                    columns.append(",");
                    values.append("'");
                    values.append(temp.getValue());
                    values.append("'");
                    values.append(",");
                }
                namestring = "(" + columns.deleteCharAt(columns.length() - 1).toString() + ")";
                valuestring = "(" + values.deleteCharAt(values.length() - 1).toString() + ")";
                query = query + namestring + "values" + valuestring;
                Log.v(getClass().getSimpleName(), "query.." + query);
                Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + mapList.size());
                try {
                    mDatabase.execSQL(query);
                } catch (Exception e) {
                    Log.v(LOG_TAG, "@@@ Error while inserting data " + e.getMessage());
                }
                if (checkCount == mapList.size()) {
                    if (null != oncomplete)
                        oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    /**
     * Updating database records
     *
     * @param tableName      ---> Table name to update
     * @param list           ---> List which contains data values
     * @param isClaues       ---> Checking where condition availability
     * @param clauesValue    ---> Where condition values
     * @param whereCondition ---> condition
     * @param context        ---> Current class
     */
    public void updateData(String tableName, List<LinkedHashMap> list, Boolean isClaues, String clauesValue, String whereCondition, Context context) {

        if (!ApplicationThread.dbThreadCheck())
            Log.e(LOG_TAG, "called on non-db thread", new RuntimeException());

//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        try {
            for (int i = 0; i < list.size(); i++) {
                List<Map.Entry> entryList = new ArrayList<>((list.get(i)).entrySet());
                String query = "update " + tableName + " set ";
                String namestring = "";
                System.out.println("\n==> Size of Entry list: " + entryList.size());
                StringBuffer values = new StringBuffer();
                StringBuffer columns = new StringBuffer();
                for (Map.Entry temp : entryList) {
                    columns.append(temp.getKey());
                    columns.append("='");
                    columns.append(temp.getValue());
                    columns.append("',");
                }
                namestring = columns.deleteCharAt(columns.length() - 1).toString();
                query = query + namestring + "" + whereCondition;
                mDatabase.execSQL(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }

    }


    /**
     * Updating database records
     *
     * @param tableName      ---> Table name to update
     * @param list           ---> List which contains data values
     * @param isClaues       ---> Checking where condition availability
     * @param whereCondition ---> condition
     */
    public synchronized void updateData(String tableName, List<LinkedHashMap> list, Boolean isClaues, String whereCondition, final ApplicationThread.OnComplete<String> oncomplete) {
        boolean isUpdateSuccess = false;
        int checkCount = 0;
        try {
            for (int i = 0; i < list.size(); i++) {
                checkCount++;
                List<Map.Entry> entryList = new ArrayList<Map.Entry>((list.get(i)).entrySet());
                String query = "update " + tableName + " set ";
                String namestring = "";

                System.out.println("\n==> Size of Entry list: " + entryList.size());
                StringBuffer columns = new StringBuffer();
                for (Map.Entry temp : entryList) {
                    columns.append(temp.getKey());
                    columns.append("='");
                    columns.append(temp.getValue());
                    columns.append("',");
                }

                namestring = columns.deleteCharAt(columns.length() - 1).toString();
                query = query + namestring + "" + whereCondition;
                mDatabase.execSQL(query);
                isUpdateSuccess = true;
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            isUpdateSuccess = false;
        } finally {
            closeDataBase();
            if (checkCount == list.size()) {
                if (isUpdateSuccess) {
                    Log.v(LOG_TAG, "@@@ data updated successfully for " + tableName);
                    oncomplete.execute(true, null, "data updated successfully for " + tableName);
                } else {
                    oncomplete.execute(false, null, "data updation failed for " + tableName);
                }
            }
        }
    }

    /**
     * Deleting records from database table
     *
     * @param tableName  ---> Table name
     * @param columnName ---> Column name to deleting
     * @param value      ---> Value for where condition
     * @param isWhere    ---> Checking where condition is required or not
     */
    public synchronized void deleteRow(String tableName, String columnName, String value, boolean isWhere, final ApplicationThread.OnComplete<String> onComplete) {
        boolean isDataDeleted = true;
//        if (!ApplicationThread.dbThreadCheck())
//            Log.e(LOG_TAG, "called on non-db thread", new RuntimeException());

        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            String query = "delete from " + tableName;
            if (isWhere) {
                query = query + " where " + columnName + " = '" + value + "'";
            }
            mDatabase.execSQL(query);
        } catch (Exception e) {
            isDataDeleted = false;
            Log.e(LOG_TAG, "@@@ master data deletion failed for " + tableName + " error is " + e.getMessage());
            onComplete.execute(false, null, "master data deletion failed for " + tableName + " error is " + e.getMessage());
        } finally {
            closeDataBase();

            if (isDataDeleted) {
                Log.v(LOG_TAG, "@@@ master data deleted successfully for " + tableName);
                onComplete.execute(true, null, "master data deleted successfully for " + tableName);
            }

        }
    }

    //To get List of Particular codes
    public ArrayList<String> getListOfCodes(final String query) {
        ArrayList<String> plotCodes = new ArrayList<>();
        Cursor paCursor = null;
        try {
            paCursor = mDatabase.rawQuery(query, null);
            if (paCursor.moveToFirst()) {
                do {
                    plotCodes.add(paCursor.getString(0).trim());
                } while (paCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != paCursor)
                paCursor.close();

            closeDataBase();
        }
        return plotCodes;
    }

    //To get GPS Tracking table data
    public List<LocationTracker> getGpsTrackingData(final String query) {
        LocationTracker mGpsBoundaries = null;
        List<LocationTracker> mGpsBoundariesList = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "@@@ location tracker query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    mGpsBoundaries = new LocationTracker();

                    mGpsBoundaries.setUserId(cursor.getInt(1));
                    mGpsBoundaries.setLatitude(cursor.getDouble(2));
                    mGpsBoundaries.setLongitude(cursor.getDouble(3));
                    mGpsBoundaries.setAddress(cursor.getString(4));
                    mGpsBoundaries.setLogDate(cursor.getString(5));
                    mGpsBoundaries.setServerUpdatedStatus(cursor.getInt(6));

                    mGpsBoundariesList.add(mGpsBoundaries);
                    Log.v(LOG_TAG, "Lat@Log" + mGpsBoundariesList.size());


                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, "@@@ getting user details " + e.getMessage());
        }
        return mGpsBoundariesList;
    }

    public void checkForDBTblUpdate(String[] tbls) {
        boolean tableexistsornot = false;
        try {
            if (tableexistsornot == false) {
                int size = tbls.length;
                //database = dbInstance.openDatabase();
                for (int i = 0; i < size; i++) {
                    if (mDatabase != null)
                        mDatabase.execSQL(tbls[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();

        }
    }

    public String getPuring(final String query) {
        String pruning = "0";
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor paCursor = null;
        try {
            paCursor = mDatabase.rawQuery(query, null);
            if (paCursor.moveToFirst()) {
                do {
                    pruning = paCursor.getString(0);
                } while (paCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != paCursor)
                paCursor.close();

            closeDataBase();
        }
        return pruning;
    }

    public synchronized void insertMasterData(final String tableName, final List<MasterDataQuery> masterDataQueryList, final ApplicationThread.OnComplete<String> onComplete) {
        boolean isDataInserted = true;

//        mDatabase = palm3FoilDatabase.getWritableDatabase();

//        mDatabase.beginTransaction();
        try {
            for (MasterDataQuery masterDataQuery : masterDataQueryList) {
                mDatabase.execSQL(masterDataQuery.getQuery());
            }
//            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            isDataInserted = false;
            Log.e(LOG_TAG, "@@@ master data insertion failed for " + tableName + " error is " + e.getMessage());
            onComplete.execute(false, null, "master data insertion failed for " + tableName + " error is " + e.getMessage());
        } finally {
//            mDatabase.endTransaction();
            if (isDataInserted) {
                Log.v(LOG_TAG, "@@@ master data inserted successfully for " + tableName);
                onComplete.execute(true, null, "master data inserted successfully for " + tableName);
            }
        }
    }

    //To get lat long of Field Assistant
    public String getFalogLatLongs(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        String latlongData = "";
        Cursor genericDataQuery = mDatabase.rawQuery(query, null);
        try {
            if (genericDataQuery.getCount() > 0 && genericDataQuery.moveToFirst()) {
                do {
                    latlongData = (genericDataQuery.getDouble(0) + "-" + genericDataQuery.getDouble(1));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return latlongData;
    }

    //Get Count of records
    public String getCountValue(String query) {
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            Log.d("query",query);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getString(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mOprQuery.close();
            closeDataBase();
        }
        return "";
    }

    //To check the recrod exists or not
    public int getIsThere(String query) {
        Cursor mOprQuery = null;
        Log.v("@@QueryIsThere", query);
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                Log.v("@@@isthere", "" + mOprQuery.getInt(0));
                return mOprQuery.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mOprQuery.close();
            closeDataBase();
        }
        return 0;
    }

    //get only one Integer value from Db
    public Integer getOnlyOneIntValueFromDb(String query) {
        Log.v(LOG_TAG, "@@@ query " + query);
        Cursor mOprQuery = null;
        try {
            mOprQuery = mDatabase.rawQuery(query, null);
            if (mOprQuery != null && mOprQuery.moveToFirst()) {
                return mOprQuery.getInt(0);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mOprQuery)
                mOprQuery.close();

            closeDataBase();
        }
        return null;
    }


    public void updateImageDate(byte[] imageDate, final String farmerCode, final String tableName, final String columnName) {
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            ContentValues update_values = new ContentValues();
            update_values.put(columnName, imageDate);
            String where = " FarmerCode ='" + farmerCode + "'";
            mDatabase.update(tableName, update_values, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }
    }

    //to insert collection images
    public synchronized void insertImageData(String code, String farmercode, String imagepath, String serverUpdatedStatus) {
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            ContentValues update_values = new ContentValues();
            update_values.put(DatabaseKeys.COLUMN_CODE, code);
            update_values.put(DatabaseKeys.COLUMN_FARMERCODE, farmercode);
            update_values.put(DatabaseKeys.COLUMN_MODULEID, 100);
//            update_values.put(DatabaseKeys.COLUMN_PLOTCODE, "");
            update_values.put(DatabaseKeys.COLUMN_PHOTO, imagepath);
            update_values.put(DatabaseKeys.COLUMN_CREATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, serverUpdatedStatus);
            mDatabase.insert(DatabaseKeys.TABLE_PICTURE_REPORTING, null, update_values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }
    }

    //to insert consignment images
    public synchronized void insertConsignmentImageData(String code, String imagepath, String serverUpdatedStatus) {
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            ContentValues update_values = new ContentValues();
            update_values.put(DatabaseKeys.COLUMN_ConsigmentCODE, code);
            update_values.put(DatabaseKeys.COLUMN_ModuleTypeId, 326);
            update_values.put(DatabaseKeys.COLUMN_FileName, code);
            update_values.put(DatabaseKeys.COLUMN_FileLocation, imagepath);
            update_values.put(DatabaseKeys.COLUMN_FileExtension, ".jpg");
            update_values.put(DatabaseKeys.COLUMN_ISACTIVE, 1);
            update_values.put(DatabaseKeys.COLUMN_CREATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, serverUpdatedStatus);
            mDatabase.insert(DatabaseKeys.TABLE_CONSIGNMENT_FileRepo, null, update_values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }
    }
    //to insert collection without plot images
    public synchronized void insertImageDataColectionFarmer(String code, String farmercode, String imagepath, String serverUpdatedStatus) {
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            ContentValues update_values = new ContentValues();
            update_values.put(DatabaseKeys.COLUMN_CODE, code);
            update_values.put(DatabaseKeys.COLUMN_FARMERCODE, farmercode);
            update_values.put(DatabaseKeys.COLUMN_MODULEID, 200);
//            update_values.put(DatabaseKeys.COLUMN_PLOTCODE, "");
            update_values.put(DatabaseKeys.COLUMN_PHOTO, imagepath);
            update_values.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_CREATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, serverUpdatedStatus);
            mDatabase.insert(DatabaseKeys.TABLE_PICTURE_REPORTING, null, update_values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }
    }

    public synchronized void insertImageDataConsignmentFarmer(String code, String imagepath, String serverUpdatedStatus) {
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            ContentValues update_values = new ContentValues();
            update_values.put(DatabaseKeys.COLUMN_CODE, code);
            update_values.put(DatabaseKeys.COLUMN_MODULEID, 300);
//            update_values.put(DatabaseKeys.COLUMN_PLOTCODE, "");
            update_values.put(DatabaseKeys.COLUMN_PHOTO, imagepath);
            update_values.put(DatabaseKeys.COLUMN_CREATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_CREATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_UPDATEDBYUSERID, CommonConstants.USER_ID);
            update_values.put(DatabaseKeys.COLUMN_UPDATEDDATE, CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            update_values.put(DatabaseKeys.COLUMN_SERVERUPDATEDSTATUS, serverUpdatedStatus);
            mDatabase.insert(DatabaseKeys.TABLE_PICTURE_REPORTING, null, update_values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDataBase();
        }
    }

    public void closeDataBase() {
//        if (mDatabase != null)
//            mDatabase.close();
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

    public List<Pair> getOnlyPairData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        List<Pair> mGenericData = new ArrayList<>();
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    mGenericData.add(Pair.create(genericDataQuery.getString(0), genericDataQuery.getString(1)));
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }

    //To get Farmer Details in Collections
    public void getFarmerDetailsForCC(final ApplicationThread.OnComplete onComplete) {
        List<BasicFarmerDetails> farmerDetails = new ArrayList<>();

        Cursor cursor = null;
        String query = Queries.getInstance().getFarmersDataForCC();
        Log.v(LOG_TAG, "Query for getting farmers " + query);
        try {
//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            cursor = mDatabase.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    BasicFarmerDetails farmlanddetails = new BasicFarmerDetails();

                    farmlanddetails.setFarmerCode(cursor.getString(0));
                    farmlanddetails.setFarmerFirstName(cursor.getString(1));
                    farmlanddetails.setFarmerMiddleName((null != cursor.getString(2) && !cursor.getString(2).equalsIgnoreCase("null")) ? cursor.getString(2) : "");
                    farmlanddetails.setFarmerLastName((null != cursor.getString(3) && !cursor.getString(3).equalsIgnoreCase("null")) ? cursor.getString(3) : "");
                    farmlanddetails.setFarmerDOB(cursor.getString(4));
                    farmlanddetails.setFarmerFatherName(cursor.getString(5));
                    farmlanddetails.setFarmerMotherName(cursor.getString(6));
                    farmlanddetails.setFarmerStateName(cursor.getString(7));
                    farmlanddetails.setFarmerDistrictName(cursor.getString(8));
                    farmlanddetails.setPrimaryContactNum(cursor.getString(9));
                    farmlanddetails.setSecondaryContactNum(cursor.getString(10));
                    farmlanddetails.setFarmerVillageName(cursor.getString(11));
                    farmlanddetails.setVillageCode(cursor.getString(12));
                    farmlanddetails.setVillageId(cursor.getString(13));
                    farmlanddetails.setStateCode(cursor.getString(14));
                    farmlanddetails.setStateId(cursor.getString(15));
                    farmlanddetails.setMandalCode(cursor.getString(16));
                    farmlanddetails.setDistrictCode(cursor.getString(17));
                    farmlanddetails.setAddress1(cursor.getString(18));
                    farmlanddetails.setAddress2(cursor.getString(19));
                    farmlanddetails.setLandmark(cursor.getString(21));
                    farmlanddetails.setBankAccountNumber(cursor.getString(22) + "@" + cursor.getString(23));
//                    farmlanddetails.setPhotoLocation((null != cursor.getString(23) && !cursor.getString(23).equalsIgnoreCase("null")) ? farmerPhotosFilePath+cursor.getString(23) : null);
                    farmlanddetails.setPhotoLocation(cursor.getString(24));
                    farmlanddetails.setPhotoName(cursor.getString(25));
                    farmlanddetails.setFileExtension(cursor.getString(26));
                    farmlanddetails.setBranchName(cursor.getString(27));
                    farmlanddetails.setBankName(cursor.getString(28));
                    farmerDetails.add(farmlanddetails);
                } while (cursor.moveToNext());
            }
            //collection farmer data
            String ccFquery = Queries.getInstance().getFarmersDataForCCFarmers();
            Log.v(LOG_TAG, "Query for getting farmers " + ccFquery);

//            mDatabase = palm3FoilDatabase.getWritableDatabase();
            cursor = mDatabase.rawQuery(ccFquery, null);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    BasicFarmerDetails farmlanddetails = new BasicFarmerDetails();

                    farmlanddetails.setFarmerCode(cursor.getString(0));
                    farmlanddetails.setFarmerFirstName(cursor.getString(1));
                    farmlanddetails.setFarmerMiddleName((null != cursor.getString(2) && !cursor.getString(2).equalsIgnoreCase("null")) ? cursor.getString(2) : "");
                    farmlanddetails.setFarmerLastName((null != cursor.getString(3) && !cursor.getString(3).equalsIgnoreCase("null")) ? cursor.getString(3) : "");
                    farmlanddetails.setFarmerDOB(cursor.getString(4));
                    farmlanddetails.setFarmerFatherName(cursor.getString(5));
                    farmlanddetails.setFarmerMotherName(cursor.getString(6));
                    farmlanddetails.setFarmerStateName(cursor.getString(7));
                    farmlanddetails.setFarmerDistrictName(cursor.getString(8));
                    farmlanddetails.setPrimaryContactNum(cursor.getString(9));
                    farmlanddetails.setSecondaryContactNum(cursor.getString(10));
                    farmlanddetails.setFarmerVillageName(cursor.getString(11));
                    farmlanddetails.setVillageCode(cursor.getString(12));
                    farmlanddetails.setVillageId(cursor.getString(13));
                    farmlanddetails.setStateCode(cursor.getString(14));
                    farmlanddetails.setStateId(cursor.getString(15));
                    farmlanddetails.setMandalCode(cursor.getString(16));
                    farmlanddetails.setDistrictCode(cursor.getString(17));
                    farmlanddetails.setAddress1(cursor.getString(18));
                    farmlanddetails.setAddress2(cursor.getString(19));
                    farmlanddetails.setLandmark(cursor.getString(21));
                    farmlanddetails.setBankAccountNumber(cursor.getString(22) + "@" + cursor.getString(23));
//                    farmlanddetails.setPhotoLocation((null != cursor.getString(23) && !cursor.getString(23).equalsIgnoreCase("null")) ? farmerPhotosFilePath+cursor.getString(23) : null);
                    farmlanddetails.setPhotoLocation(cursor.getString(24));
                    farmlanddetails.setPhotoName(cursor.getString(25));
                    farmlanddetails.setFileExtension(cursor.getString(26));
                    farmlanddetails.setBranchName(cursor.getString(27));
                    farmlanddetails.setBankName(cursor.getString(28));
                    farmerDetails.add(farmlanddetails);
                } while (cursor.moveToNext());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, farmerDetails, "getting data");
        }
    }

    //To Get Collection Reports
    public void getCollectionReportDetails(final String query, final ApplicationThread.OnComplete onComplete) {
        List<CollectionReportModel> collectionReportDetails = new ArrayList<>();
        Cursor cursor = null;
//        String query = Queries.getInstance().getCollectionReportDetails();
        Log.v(LOG_TAG, "@@@@ collection reports query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    CollectionReportModel farmlanddetails = new CollectionReportModel();

                    farmlanddetails.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    farmlanddetails.setWeighingDate(cursor.getString(cursor.getColumnIndex("WeighingDate")));
                    farmlanddetails.setFarmerCode(cursor.getString(cursor.getColumnIndex("fCode")));
                    farmlanddetails.setFirstName(cursor.getString(cursor.getColumnIndex("FirstName")));
                    farmlanddetails.setMiddleName(cursor.getString(cursor.getColumnIndex("MiddleName")));
                    farmlanddetails.setLastName(cursor.getString(cursor.getColumnIndex("LastName")));
                    farmlanddetails.setGrossWeight(cursor.getString(cursor.getColumnIndex("GrossWeight")));
                    farmlanddetails.setTareWeight(cursor.getString(cursor.getColumnIndex("TareWeight")));
                    farmlanddetails.setNetWeight(cursor.getString(cursor.getColumnIndex("NetWeight")));
                    farmlanddetails.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    farmlanddetails.setVehicleTypeId(cursor.getInt(cursor.getColumnIndex("VehicleTypeId")));
                    farmlanddetails.setName(cursor.getString(cursor.getColumnIndex("Harvestername")));
                    farmlanddetails.setMobileNumber(cursor.getString(cursor.getColumnIndex("MobileNumber")));
                    farmlanddetails.setUnRipen(cursor.getString(cursor.getColumnIndex("UnRipen")));
                    farmlanddetails.setUnderRipe(cursor.getString(cursor.getColumnIndex("UnderRipe")));
                    farmlanddetails.setRipen(cursor.getString(cursor.getColumnIndex("Ripen")));
                    farmlanddetails.setOverRipe(cursor.getString(cursor.getColumnIndex("OverRipe")));
                    farmlanddetails.setDiseased(cursor.getString(cursor.getColumnIndex("Diseased")));
                    farmlanddetails.setEmptyBunches(cursor.getString(cursor.getColumnIndex("EmptyBunches")));
                    farmlanddetails.setFFBQualityLong(cursor.getString(cursor.getColumnIndex("FFBQualityLong")));
                    farmlanddetails.setFFBQualityMedium(cursor.getString(cursor.getColumnIndex("FFBQualityMedium")));
                    farmlanddetails.setFFBQualityShort(cursor.getString(cursor.getColumnIndex("FFBQualityShort")));
                    farmlanddetails.setFFBQualityOptimum(cursor.getString(cursor.getColumnIndex("FFBQualityOptimum")));
                   // farmlanddetails.setInchargeName(cursor.getString(23));
                    farmlanddetails.setInchargeName(cursor.getString(cursor.getColumnIndex("InchargeName")));
                    farmlanddetails.setPlotCodes(cursor.getString(26));
                    farmlanddetails.setReceiptCode(cursor.getString(cursor.getColumnIndex("ReceiptCode")));
                    farmlanddetails.setCollectionCenterName(cursor.getString(cursor.getColumnIndex("Name")));
                    farmlanddetails.setBankName(cursor.getString(cursor.getColumnIndex("bankName")));
                    farmlanddetails.setBranchName(cursor.getString(cursor.getColumnIndex("BranchName")));
                    farmlanddetails.setBankAccountNumber(cursor.getString(cursor.getColumnIndex("AccountNumber")));
                    farmlanddetails.setRejectedBunches(cursor.getString(cursor.getColumnIndex("RejectedBunches")));
                    farmlanddetails.setPrivateWeighBridgeName(cursor.getString(cursor.getColumnIndex("wcName")));
                    farmlanddetails.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    farmlanddetails.setCCCode(cursor.getString(cursor.getColumnIndex("CollectionCenterCode")));
                   // farmlanddetails.setCCCode(cursor.getString(36));
                    farmlanddetails.setLoosefruitweight(cursor.getString(cursor.getColumnIndex("LooseFruitWeight")));
                    collectionReportDetails.add(farmlanddetails);
                } while (cursor.moveToNext());
            }
            //Collection Reports for CollectionWithoutPlot data

            String ColFarmerQuery = CollectionReport.SearchCollectionwithoutPlotQuery;
            Log.v(LOG_TAG, "@@@@ collection reports withOutPlot query " + ColFarmerQuery);

            cursor = mDatabase.rawQuery(ColFarmerQuery, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    CollectionReportModel farmlanddetails = new CollectionReportModel();

                    farmlanddetails.setCode(cursor.getString(0));
                    farmlanddetails.setWeighingDate(cursor.getString(1));
                    farmlanddetails.setFarmerCode(cursor.getString(2));
                    farmlanddetails.setFirstName(cursor.getString(3));
                    farmlanddetails.setMiddleName((null != cursor.getString(4)) ? cursor.getString(4) : "");
                    farmlanddetails.setLastName(cursor.getString(5));
                    farmlanddetails.setGrossWeight(cursor.getString(6));
                    farmlanddetails.setTareWeight(cursor.getString(7));
                    farmlanddetails.setNetWeight(cursor.getString(8));
                    farmlanddetails.setVehicleNumber(cursor.getString(9));
                    farmlanddetails.setVehicleTypeId(cursor.getInt(10));
                    farmlanddetails.setName(cursor.getString(11));
                    farmlanddetails.setMobileNumber(cursor.getString(12));
                    farmlanddetails.setUnRipen(cursor.getString(13));
                    farmlanddetails.setUnderRipe(cursor.getString(14));
                    farmlanddetails.setRipen(cursor.getString(15));
                    farmlanddetails.setOverRipe(cursor.getString(16));
                    farmlanddetails.setDiseased(cursor.getString(17));
                    farmlanddetails.setEmptyBunches(cursor.getString(18));
                    farmlanddetails.setFFBQualityLong(cursor.getString(19));
                    farmlanddetails.setFFBQualityMedium(cursor.getString(20));
                    farmlanddetails.setFFBQualityShort(cursor.getString(21));
                    farmlanddetails.setFFBQualityOptimum(cursor.getString(22));
                    farmlanddetails.setInchargeName(cursor.getString(23));
//                        farmlanddetails.setPlotCodes(cursor.getString(11));
                    farmlanddetails.setReceiptCode(cursor.getString(24));
                    farmlanddetails.setCollectionCenterName(cursor.getString(25));
                    farmlanddetails.setBankName(cursor.getString(26));
                    farmlanddetails.setBranchName(cursor.getString(27));
                    farmlanddetails.setBankAccountNumber(cursor.getString(28));
                    farmlanddetails.setRejectedBunches(cursor.getString(29));
                    farmlanddetails.setPrivateWeighBridgeName(cursor.getString(30));
                    farmlanddetails.setCCCode(cursor.getString(31));
                    farmlanddetails.setLoosefruitweight(cursor.getString(32));
                    collectionReportDetails.add(farmlanddetails);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, collectionReportDetails, "getting data");
        }
    }

    //To get Single List of Data
    public List<String> getSingleListData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        List<String> mGenericData = new ArrayList<>();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    String plotCode = genericDataQuery.getString(0);
                    mGenericData.add(plotCode);
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();
            closeDataBase();
        }
        return mGenericData;
    }

    public List<String> getZombiData(final String query) {
        Log.v(LOG_TAG, "@@@ Generic Query " + query);
        List<String> mGenericData = new ArrayList<>();
//        mDatabase = palm3FoilDatabase.getWritableDatabase();
        Cursor genericDataQuery = null;
        try {
            genericDataQuery = mDatabase.rawQuery(query, null);
            if (genericDataQuery.moveToFirst()) {
                do {
                    String plotCode = genericDataQuery.getString(0);
                    if (!TextUtils.isEmpty(plotCode)) {
                        mGenericData.add("'" + plotCode + "'");
                    }
                } while (genericDataQuery.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (null != genericDataQuery)
                genericDataQuery.close();

            closeDataBase();
        }
        return mGenericData;
    }


    /*public List<ImageDetails> getImageDetails() {
        List<ImageDetails> imageDetailsList = new ArrayList<>();
        Cursor cursor = null;
        try {
            mDatabase = palm3FoilDatabase.getReadableDatabase();
            cursor = mDatabase.rawQuery(Queries.getInstance().getImageDetails(), null);
            if (cursor.moveToFirst()) {
                do {
                    try {
                        if (cursor.getBlob(3) != null) {
                            ImageDetails rec = new ImageDetails();
                            rec.setFarmerCode(cursor.getString(0));
                            if (cursor.getInt(1) == 1) {
                                rec.setTableName(DatabaseKeys.TABLE_FARMERPERSONALDETAILS);
                            } else {
                                rec.setTableName(DatabaseKeys.TABLE_HEALTHOFPLANTATIONDETAILS);
                            }
                            if (cursor.getBlob(3) != null && cursor.getBlob(3).length > 0) {
                                String base64string = Base64.encodeToString(cursor.getBlob(3), 0);
                                rec.setPhoto(base64string);
                            } else {
                                rec.setPhoto(null);
                            }
                            imageDetailsList.add(rec);
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
    }*/


    //Tot get Collection Center Rates
    public CcRate checkListOfTables(String query) {
        CcRate ccRate = new CcRate();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ccRate.setCombinedCharge(cursor.getFloat(cursor.getColumnIndex("CombinedCharge")));
                    ccRate.setOverweightCharge(cursor.getFloat(cursor.getColumnIndex("OverweightCharge")));
                    ccRate.setTransportCost(cursor.getFloat(cursor.getColumnIndex("TransportCost")));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            return ccRate;
        }

    }

//To Get Consignment Report Details
    public void getConsignmentReportDetails(String query, final ApplicationThread.OnComplete onComplete) {
        List<ConsignmentReportModel> collectionReportDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ConsignmentReportModel consignmentReportModel = new ConsignmentReportModel();

                    consignmentReportModel.setMillName(cursor.getString(cursor.getColumnIndex("Name")));
                    consignmentReportModel.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    consignmentReportModel.setRecieptGeneratedDate(cursor.getString(cursor.getColumnIndex("ReceiptGeneratedDate")));
                    consignmentReportModel.setMillCode(cursor.getString(cursor.getColumnIndex("MillCode")));
                    consignmentReportModel.setTotalWeight(cursor.getString(cursor.getColumnIndex("TotalWeight")));
                    consignmentReportModel.setVehicleNumber(cursor.getString(cursor.getColumnIndex("VehicleNumber")));
                    consignmentReportModel.setOperatorName(cursor.getString(cursor.getColumnIndex("OperatorName")));
                    consignmentReportModel.setInchargeName(cursor.getString(cursor.getColumnIndex("InchargeName")));
                    consignmentReportModel.setReceiptCode(cursor.getString(cursor.getColumnIndex("ReceiptCode")));
                    consignmentReportModel.setDriverName(cursor.getString(cursor.getColumnIndex("DriverName")));
                    consignmentReportModel.setSelectedCollectionCenterName(cursor.getString(cursor.getColumnIndex("CCNAME")));
                    consignmentReportModel.setCollectionCenterCode(cursor.getString(cursor.getColumnIndex("Id")));
                    consignmentReportModel.setSizeOfTruck(cursor.getInt(cursor.getColumnIndex("Desc")));
                    consignmentReportModel.setTransportCost(cursor.getString(cursor.getColumnIndex("TransportCost")));
                    consignmentReportModel.setSharingCost(cursor.getString(cursor.getColumnIndex("SharingCost")));
                    consignmentReportModel.setOverWeightCost(cursor.getString(cursor.getColumnIndex("OverWeightCost")));
                    consignmentReportModel.setExpectedCost(cursor.getString(cursor.getColumnIndex("ExpectedCost")));
                    consignmentReportModel.setActualCost(cursor.getString(cursor.getColumnIndex("ActualCost")));
                    consignmentReportModel.setCreatedDate(cursor.getString(cursor.getColumnIndex("CreatedDate")));
                    collectionReportDetails.add(consignmentReportModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, collectionReportDetails, "getting data");
        }
    }

    //To get Stock Transfer Report Details
    public void getStockTransferReportDetails(String query, final ApplicationThread.OnComplete onComplete) {
        List<StockTransferReportModel> stockTransferReportModelsList = new ArrayList<>();
        Cursor cursor = null;
        Log.v(LOG_TAG, "stocktransfer Report" + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StockTransferReportModel stockTransferReportModel = new StockTransferReportModel();
                    stockTransferReportModel.setCode(cursor.getString(0));
                    stockTransferReportModel.setReceiptCode(cursor.getString(1));
                    stockTransferReportModel.setReceiptGeneratedDate(cursor.getString(2));
                    stockTransferReportModel.setFromCC(cursor.getString(3));
                    stockTransferReportModel.setToCC(cursor.getString(4));
                    stockTransferReportModel.setFromCCId(cursor.getString(5));
                    stockTransferReportModel.setToCCId(cursor.getString(6));
                    stockTransferReportModel.setGrossWeight(cursor.getDouble(7));
                    stockTransferReportModel.setTareWeight(cursor.getDouble(8));
                    stockTransferReportModel.setNetWeight(cursor.getDouble(9));
                    stockTransferReportModel.setVehicleNumber(cursor.getString(10));
                    stockTransferReportModel.setDriverMobileNumber(cursor.getString(11));
                    stockTransferReportModel.setUserName(cursor.getString(12));
                    stockTransferReportModel.setDriverName(cursor.getString(14));
                    stockTransferReportModel.setInchargeName(cursor.getString(15));
                    stockTransferReportModel.setCreatedDate(cursor.getString(16));
                    stockTransferReportModelsList.add(stockTransferReportModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, stockTransferReportModelsList, "getting data");
        }
    }

    //To get Login User Details
    public UserDetails getUserDetails(final String imeiNumber) {
        UserDetails userDetails = null;
        Cursor cursor = null;
        String query = Queries.getInstance().getUserDetailsNewQuery(imeiNumber);
        Log.v(LOG_TAG, "@@@ user details query " + query);
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                userDetails = new UserDetails();
                do {
                    userDetails.setUserId(cursor.getString(0));
                    userDetails.setUserName(cursor.getString(1));
                    userDetails.setPassword(cursor.getString(2));
                    userDetails.setRoleId(cursor.getInt(3));
                    userDetails.setManagerId(cursor.getInt(4));
                    userDetails.setId(cursor.getString(5));
                    userDetails.setFirstName(cursor.getString(6));
                    userDetails.setTabName(cursor.getString(7));
//                    userDetails.setTabletId(cursor.getInt(5));
//                    userDetails.setUserVillageId(cursor.getString(6));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@ getting user details " + e.getMessage());
        }
        return userDetails;
    }

    //To insert transaction Data
    public synchronized void insertData(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {
        insertData(false, tableName, mapList, oncomplete);
    }


    //To Insert Image data
    public synchronized void insertDataOld(String tableName, List<LinkedHashMap> mapList, final ApplicationThread.OnComplete<String> oncomplete) {

        int checkCount = 0;
        boolean errorMessageSent = false;
        try {
            for (int i = 0; i < mapList.size(); i++) {
                checkCount++;
                List<LinkedHashMap.Entry> entryList = new ArrayList<>((mapList.get(i)).entrySet());
                String query = "insert into " + tableName;
                String namestring, valuestring;
                StringBuffer values = new StringBuffer();
                StringBuffer columns = new StringBuffer();
                for (LinkedHashMap.Entry temp : entryList) {

                    columns.append(temp.getKey());
                    columns.append(",");
                    values.append("'");
                    values.append(temp.getValue());
                    values.append("'");
                    values.append(",");
                }
                namestring = "(" + columns.deleteCharAt(columns.length() - 1).toString() + ")";
                valuestring = "(" + values.deleteCharAt(values.length() - 1).toString() + ")";
                query = query + namestring + "values" + valuestring;
                android.util.Log.v(getClass().getSimpleName(), "query.." + query);
                android.util.Log.v(LOG_TAG, "@@@@ log check " + checkCount + " here " + mapList.size());
                try {
                    mDatabase.execSQL(query);
                } catch (Exception e) {
                    android.util.Log.v(LOG_TAG, "@@@ Error while inserting data " + e.getMessage());
                    if (checkCount == mapList.size()) {
                        errorMessageSent = true;
                        if (null != oncomplete)
                            oncomplete.execute(false, "failed to insert data", "");
                    }
                }
                if (checkCount == mapList.size() && !errorMessageSent) {
                    if (null != oncomplete)
                        oncomplete.execute(true, "data inserted successfully", "");
                }
            }
        } catch (Exception e) {
            checkCount++;
            e.printStackTrace();
            android.util.Log.v(LOG_TAG, "@@@@ exception log check " + checkCount + " here " + mapList.size());
            if (checkCount == mapList.size()) {
                if (null != oncomplete)
                    oncomplete.execute(false, "data insertion failed", "" + e.getMessage());
            }
        } finally {
            closeDataBase();
        }
    }

    public ArrayList<GraderDetails> getGraderdetails(final String query) {
        Cursor cursor = null;
        GraderDetails graderdetails = null;
        Log.v(LOG_TAG, "getGraderdetails" + query);
        ArrayList<GraderDetails> graderdetailsList = new ArrayList<>();
        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    graderdetails = new GraderDetails();
                    graderdetails.setCode(cursor.getString(cursor.getColumnIndex("Code")));
                    graderdetails.setName(cursor.getString(cursor.getColumnIndex("Name")));
                    graderdetails.setFingerPrintData1(cursor.getString(cursor.getColumnIndex("FingerPrintData1")));
                    graderdetails.setFingerPrintData2(cursor.getString(cursor.getColumnIndex("FingerPrintData2")));
                    graderdetails.setFingerPrintData3(cursor.getString(cursor.getColumnIndex("FingerPrintData3")));

                    graderdetailsList.add(graderdetails);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "@@@  UserSyncdetails " + e.getMessage());
            e.printStackTrace();
        }
        return graderdetailsList;

    }

    public void getGraderbasicdetails(String key, int offset, int limit, final ApplicationThread.OnComplete onComplete) {
        List<GraderBasicDetails> gradetlistDetails = new ArrayList<>();
        Cursor cursor = null;
        String query = null;
        query = Queries.getInstance().getActiveGraders(key,offset,limit);
        Log.v(LOG_TAG, "Query for getting farmers " + query);

        try {
            cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    GraderBasicDetails graderbasicdetails = new GraderBasicDetails();
                    graderbasicdetails.setGraderCode(cursor.getString(cursor.getColumnIndex("GraderCode")));
                    graderbasicdetails.setGraderName(cursor.getString(cursor.getColumnIndex("GraderName")));
                    graderbasicdetails.setMobileNumber(cursor.getString(cursor.getColumnIndex("MobileNumber")));
                    graderbasicdetails.setVillageName(cursor.getString(cursor.getColumnIndex("VillageName")));


                    gradetlistDetails.add(graderbasicdetails);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(LOG_TAG, "getting failed fromLocalDb" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDataBase();

            onComplete.execute(true, gradetlistDetails, "getting data");
        }
    }

}
