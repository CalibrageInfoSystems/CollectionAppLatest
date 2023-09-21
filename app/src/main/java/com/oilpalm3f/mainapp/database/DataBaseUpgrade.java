package com.oilpalm3f.mainapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.ContextCompat;

import com.oilpalm3f.mainapp.cloudhelper.Log;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.helper.PrefUtil;
import com.oilpalm3f.mainapp.utils.UiUtils;

//To Update the DB

public class DataBaseUpgrade {

    private static final String LOG_TAG = DataBaseUpgrade.class.getName();

    private static final String alterUserInfoTable1 = "ALTER TABLE UserInfo ADD COLUMN UserCode VARCHAR (50)";
    private static final String alterUserInfoTable2 = "ALTER TABLE UserInfo ADD COLUMN EmployeeCode VARCHAR (50)";


    /* Altering Consignment */
    private static final String consignment1 = "ALTER TABLE Consignment ADD COLUMN TotalBunches INT";
    private static final String consignment2 = "ALTER TABLE Consignment ADD COLUMN RejectedBunches INT";
    private static final String consignment3 = "ALTER TABLE Consignment ADD COLUMN AcceptedBunches INT";
    private static final String consignment4 = "ALTER TABLE Consignment ADD COLUMN Remarks VARCHAR (200)";
    private static final String consignment5 = "ALTER TABLE Consignment ADD COLUMN GraderName VARCHAR (200)";
    private static final String consignment6 = "ALTER TABLE Consignment ADD COLUMN DriverMobileNumber VARCHAR (200)";
    private static final String consignment7 = "ALTER TABLE Consignment ADD COLUMN vehicleTypeId INT";

    private static final String alterplot1 = "ALTER TABLE Plot ADD COLUMN DateOfAdvanceReceived VARCHAR";
    private static final String alterplot2 = "ALTER TABLE Plot ADD COLUMN AdvanceAmountReceived DECIMAL";
    private static final String alterplot3 = "ALTER TABLE Plot ADD COLUMN ExpectedMonthOfPlanting VARCHAR";
    private static final String alterplot4 = "ALTER TABLE Plot ADD COLUMN Comments VARCHAR(500)";
    private static final String alterplot5 = "ALTER TABLE Plot ADD COLUMN IsPLotSubsidySubmission BOOLEAN";
    private static final String alterplot6 = "ALTER TABLE Plot ADD COLUMN IsPLotHavingIdCard BOOLEAN";
    private static final String alterplot7 = "ALTER TABLE Plot ADD COLUMN IsGeoBoundariesVerification BOOLEAN";
    private static final String alterplot8 = "ALTER TABLE Plot ADD COLUMN SaplingPickUpDate VARCHAR";
    private static final String alterplot9 = "ALTER TABLE PLot ADD COLUMN SuitablePalmOilArea DECIMAL";
    private static final String alterplot10 = "ALTER TABLE PLot ADD COLUMN DateofPlanting VARCHAR";
    private static final String alterplot11 = "ALTER TABLE PLot ADD COLUMN SwapingReasonId INT";

    private static final String alertPlot12 = "ALTER TABLE Plot ADD COLUMN IsRetakeGeoTagRequired INTEGER DEFAULT 0";


    private static final String alterfarmerhistory = "ALTER TABLE FarmerHistory ADD COLUMN Comments VARCHAR(500)";

    private static final String alterfilerepository = "ALTER TABLE FileRepository ADD COLUMN CropMaintenanceCode VARCHAR";


    private static final String deleteTable_1 = "Drop table Region";
    private static final String deleteTable_2 = "Drop table RegionStateDistrictXref";
    private static final String deleteTable_3 = "Drop table UOM";
    private static final String deleteTable_4 = "Drop table FertilizerProvider";
    private static final String deleteTable_5 = "Drop table Nursery";
    private static final String deleteTable_6 = "Drop table CookingOilBrand";
    private static final String deleteTable_7 = "Drop table DigitalContract";

    private static final String CollectionWithOutPlot = " CREATE TABLE CollectionWithOutPlot (    \n" +
            "Id                         INTEGER       PRIMARY KEY      NOT NULL ,  \n" +
            "Code                      VARCHAR(50)    UNIQUE           NOT NULL ,   \n" +
            "CollectionCenterCode      VARCHAR(10)                     NOT NULL,    \n" +
            "FarmerCode                VARCHAR(50)                     NOT NULL,    \n" +
            "WeighbridgeCenterId         INT         ,\n" +
            "WeighingDate              DATETIME      ,\n" +
            "VehicleNumber            VARCHAR(20)                      NOT NULL,    \n" +
            "DriverName               VARCHAR(100)                     NOT NULL,    \n" +
            "GrossWeight               Float,\n" +
            "TareWeight                Float,\n" +
            "NetWeight                 Float,\n" +
            "OperatorName            VARCHAR(100)                      NOT NULL,   \n" +
            "Comments                VARCHAR(150)    ,\n" +
            "TotalBunches               INT          ,\n" +
            "RejectedBunches             INT         ,\n" +
            "AcceptedBunches             INT         ,\n" +
            "Remarks                 VARCHAR(150)    ,\n" +
            "GraderName              VARCHAR(100)    ,\n" +
            "ReceiptGeneratedDate    DATETIME                           NOT NULL,  \n" +
            "ReceiptCode             VARCHAR(50)      UNIQUE            NOT NULL,  \n" +
            "WBReceiptName           VARCHAR(100)     ,\n" +
            "WBReceiptLocation       VARCHAR(250)     ,\n" +
            "WBReceiptExtension      VARCHAR(25)      ,\n" +
            "IsActive                    BIT                            NOT NULL      DEFAULT 1,\n" +
            "CreatedByUserId            INT                             NOT NULL,  \n" +
            "CreatedDate             DATETIME                           NOT NULL,  \n" +
            "UpdatedByUserId             INT                            NOT NULL,  \n" +
            "UpdatedDate              DATETIME                          NOT NULL,  \n" +
            "ServerUpdatedStatus         BIT                            NOT NULL      DEFAULT 0 \n" +
            ");";
    private static final String CollectionFarmerBank = "CREATE TABLE CollectionFarmerBank(  \n" +
            "Id                    INTEGER       PRIMARY KEY              NOT NULL,\n" +
            "FarmerCode           VARCHAR(50)                             NOT NULL,\n" +
            "BankId                INT                                    NOT NULL,\n" +
            "AccountHolderName    VARCHAR(100)                            NOT NULL,\n" +
            "AccountNumber        VARCHAR(50)                             NOT NULL,\n" +
            "FileName             VARCHAR(100),\n" +
            "FileLocation         VARCHAR(250),\n" +
            "FileExtension        VARCHAR(25),\n" +
            "IsActive               BIT                                   NOT NULL,\n" +
            "CreatedByUserId        INT                                   NOT NULL,\n" +
            "CreatedDate          DATETIME                                NOT NULL,\n" +
            "UpdatedByUserId        INT                                   NOT NULL,\n" +
            "UpdatedDate          DATETIME                                NOT NULL,\n" +
            "ServerUpdatedStatus    BIT           DEFAULT 0               NOT NULL\n" +
            " );";

    private static final String CollectionFarmer = " CREATE TABLE CollectionFarmer (   \n  " +

            "Id                     INTEGER       PRIMARY KEY    NOT NULL,\n" +
            "Code                   VARCHAR(50)                  NOT NULL      UNIQUE,\n" +
            "CountryId                 INT                       NOT NULL ,\n" +
            "RegionId                   INT ,\n" +
            "StateId                    INT                      NOT NULL,\n" +
            "DistrictId                 INT                      NOT NULL,\n" +
            "MandalId                   INT                      NOT NULL,\n" +
            "VillageId                  INT                      NOT NULL,\n" +
            "SourceOfContactTypeId      INT                      NOT NULL,\n" +
            "TitleTypeId                INT                      NOT NULL,\n" +
            "FirstName              VARCHAR(255) NOT NULL,\n" +
            "MiddleName             VARCHAR(255), \n" +
            "LastName               VARCHAR(255)                NOT NULL,\n" +
            "GuardianName           VARCHAR(255)                NOT NULL,\n" +
            "MotherName             VARCHAR(255)                NOT NULL,\n" +
            "GenderTypeId               INT                     NOT NULL,\n" +
            "ContactNumber          VARCHAR(15)                 NOT NULL,\n" +
            "MobileNumber           VARCHAR(15), \n" +
            "DOB                    DATETIME, \n" +
            "Age                        INT                     NOT NULL,\n" +
            "Email                  VARCHAR(50),\n" +
            "CategoryTypeId             INT ,\n" +
            "AnnualIncomeTypeId         INT  ,\n" +
            "AddressCode            VARCHAR(60) ,\n" +
            "EducationTypeId            INT  ,\n" +
            "IsActive                   BIT                     NOT NULL,\n" +
            "CreatedByUserId            INT                     NOT NULL,\n" +
            "CreatedDate            DATETIME                    NOT NULL,\n" +
            "UpdatedByUserId            INT                     NOT NULL,\n" +
            "UpdatedDate            DATETIME                    NOT NULL,\n" +
            "ServerUpdatedStatus        BIT                     NOT NULL        DEFAULT 0,\n" +
            "InActivatedByUserId        int                     NULL ,\n" +
            "InActivatedDate        Datetime                    NULL,\n" +
            "InactivatedReasonTypeId    INT                     NULL \n" +
            ");";

    private static final String CollectionFarmerIdentityProof = "CREATE TABLE  CollectionFarmerIdentityProof (  \n" +
            "Id                     INTEGER       PRIMARY KEY       NOT NULL,\n" +
            "FarmerCode             VARCHAR(50) ,\n" +
            "IDProofTypeId            INT                           NOT NULL,\n" +
            "IdProofNumber          VARCHAR(100)                    NOT NULL,\n" +
            "IsActive                  BIT                          NOT NULL,\n" +
            "CreatedByUserId           INT                          NOT NULL,\n" +
            "CreatedDate            DATETIME                        NOT NULL,\n" +
            "UpdatedByUserId           INT                          NOT NULL,\n" +
            "UpdatedDate            DATETIME                        NOT NULL,\n" +
            "ServerUpdatedStatus       BIT        DEFAULT 0         NOT NULL\n" +

            "  );";
    private static final String CollectionFileRepository = "CREATE TABLE  CollectionFileRepository(  \n" +
            "Id                     INTEGER       PRIMARY KEY       NOT NULL,\n" +
            "FarmerCode             VARCHAR(50)                     NOT NULL,\n" +
            "ModuleTypeId               INT                         NOT NULL ,\n" +
            "FileName               VARCHAR(100)                    NOT NULL,\n" +
            "FileLocation           VARCHAR(250)                    NOT NULL,\n" +
            "FileExtension          VARCHAR(25)                     NOT NULL,\n" +
            "IsActive                   BIT                         NOT NULL,\n" +
            "CreatedByUserId            INT                         NOT NULL ,\n" +
            "CreatedDate            DATETIME                        NOT NULL,\n" +
            "UpdatedByUserId            INT                         NOT NULL,\n" +
            "UpdatedDate            DATETIME                        NOT NULL,\n" +
            "ServerUpdatedStatus        BIT       DEFAULT 0         NOT NULL\n" +

            ");";

    private static final String CollectionCenter_Table1 = "ALTER TABLE CollectionCenter ADD COLUMN  Latitude  FLOAT";
    private static final String CollectionCenter_Table2 = "ALTER TABLE CollectionCenter ADD COLUMN  Longitude FLOAT";

    private static final String CollectionFarmer_alter = "ALTER TABLE CollectionFarmer ADD COLUMN  PlotState       VARCHAR(50)    NULL";
    private static final String CollectionFarmer_alter1 = "ALTER TABLE CollectionFarmer ADD COLUMN  PlotDistrict    VARCHAR(50)    NULL";
    private static final String CollectionFarmer_alter2 = "ALTER TABLE CollectionFarmer ADD COLUMN  PlotMandal      VARCHAR(50)    NULL";
    private static final String CollectionFarmer_alter3 = "ALTER TABLE CollectionFarmer ADD COLUMN  PlotVillage     VARCHAR(50)    NULL";
    private static final String CollectionFarmer_alter4 = "ALTER TABLE CollectionFarmer ADD COLUMN  DOP             DATETIME       NULL";
    private static final String CollectionFarmer_alter5 = "ALTER TABLE CollectionFarmer ADD COLUMN  AreaofPlot      FLOAT          NULL";
    private static final String CollectionFarmer_alter6 = "ALTER TABLE CollectionFarmer ADD COLUMN  Comments        VARCHAR(1000)  NULL";

    private static final String ClusterTable = "CREATE TABLE  Cluster ( \n" +
            "Id             INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
            "Code           VARCHAR(5)      NOT NULL,\n" +
            "Name           VARCHAR(50)     NOT NULL,\n" +
            "IsActive       BIT             NOT NULL DEFAULT 1,\n" +
            "CreatedByUserId INT            NOT NULL  ,\n" +
            "CreatedDate    DATETIME        NOT NULL,\n" +
            "UpdatedByUserId INT            NOT NULL ,\n" +
            "UpdatedDate    DATETIME        NOT NULL \n" +
            ");";

    private static final String ClusterVillageXref = "  CREATE TABLE     VillageClusterxref     (\n" +
            " VillageId    INT     NOT NULL ,\n" +
            " ClusterId    INT     NOT NULL \n" +
            ");";


    private static final String ConsigmentFileRepoTable = " CREATE TABLE  ConsignmentRepository(    \n" +
            "        Id                  INTEGER    PRIMARY KEY AUTOINCREMENT    NOT NULL,   \n" +
            "        ConsignmentCode     VARCHAR(50)                             NOT NULL,   \n" +
            "        ModuleTypeId          INT                                   NOT NULL,   \n" +
            "        FileName            VARCHAR(100)                            NOT NULL,   \n" +
            "        FileLocation        VARCHAR(250)                            NOT NULL,   \n" +
            "        FileExtension       VARCHAR(25)                             NOT NULL,   \n" +
            "        IsActive              BIT                                   NOT NULL,   \n" +
            "        CreatedByUserId       INT                                   NOT NULL,   \n" +
            "        CreatedDate         DATETIME                                NOT NULL,   \n" +
            "        UpdatedByUserId       INT                                   NOT NULL,   \n" +
            "        UpdatedDate         DATETIME                                NOT NULL,   \n" +
            "        ServerUpdatedStatus    BIT          DEFAULT 0               NOT NULL    \n" +
            "    );";

    private static final String StockTransferTable = " CREATE TABLE StockTransfer (   \n" +
            "    Id                   INTEGER      PRIMARY KEY AUTOINCREMENT   \n" +
            "                                       NOT NULL,   \n" +
            "    Code                 VARCHAR (50)  NOT NULL,   \n" +
            "    FromCC               VARCHAR (10)  NOT NULL,   \n" +
            "    VehicleNumber        VARCHAR (20)  ,           \n" +
            "    DriverName           VARCHAR (100) ,           \n " +
            "    DriverMobileNumber   VARCHAR (15)  ,           \n" +
            "    ToCC                 VARCHAR (10)  NOT NULL,   \n" +
            "    GrossWeight          DECIMAL       NOT NULL,   \n" +
            "    TareWeight           DECIMAL       NOT NULL,   \n" +
            "    NetWeight            DECIMAL       NOT NULL,   \n" +
            "    WeightDifference     DECIMAL,                  \n" +
            "    ReceiptGeneratedDate DATETIME      NOT NULL    \n" +
            "                                       DEFAULT (NULL),  \n" +
            "    ReceiptCode          VARCHAR (50)  NOT NULL         \n" +
            "                                       DEFAULT (NULL),  \n" +
            "    TotalBunches         INTEGER,                       \n" +
            "    RejectedBunches      INTEGER,                       \n" +
            "    AcceptedBunches      INTEGER,                       \n" +
            "    Remarks              VARCHAR (200),                 \n" +
            "    GraderName           VARCHAR (200),                 \n" +
            "    IsStockUpdate        BIT         NOT NULL           \n" +
            "                                     DEFAULT(0),        \n" +
            "    IsActive             BIT           NOT NULL         \n" +
            "                                       DEFAULT (1),     \n" +
            "    CreatedByUserId      INT           NOT NULL,        \n" +
            "    CreatedDate          VARCHAR       NOT NULL         \n" +
            "                                       DEFAULT (NULL),  \n" +
            "    UpdatedByUserId      INT           NOT NULL,        \n" +
            "    UpdatedDate          VARCHAR       NOT NULL         \n" +
            "                                       DEFAULT (NULL),  \n" +
            "    ServerUpdatedStatus  BOOL          NOT NULL         \n" +
            "                                       DEFAULT (0)     \n" +
            ");";

    private static final String stockTransferReceiveTable = "CREATE TABLE StockTransferReceive ( \n" +
            "    Id                   INTEGER       PRIMARY KEY AUTOINCREMENT\n" +
            "                                       NOT NULL,    \n" +
            "    Code                 VARCHAR (50)  NOT NULL UNIQUE,    \n" +
            "    IsStockUpdate        BIT           NOT NULL     \n" +
            "                                       DEFAULT (1), \n" +
            "    UpdatedByUserId      INT           NOT NULL,    \n" +
            "    UpdatedDate          VARCHAR       NOT NULL     \n" +
            "                                       DEFAULT (NULL),\n" +
            "    ServerUpdatedStatus  BOOL          NOT NULL     \n" +
            "                                       DEFAULT (0)  \n" +
            ");";


    private static final String createVisitLogTable = "CREATE TABLE VisitLog (" +
            "    Id                  INTEGER       PRIMARY KEY AUTOINCREMENT," +
            "    ClientName          VARCHAR (255)," +
            "    MobileNumber        VARCHAR (255)," +
            "    Location            VARCHAR (255)," +
            "    Details             VARCHAR (255)," +
            "    Latitude            FLOAT," +
            "    Longitude           FLOAT," +
            "    CreatedByUserId     INT," +
            "    CreatedDate         VARCHAR," +
            "    ServerUpdatedStatus INT" +
            ");";


    private static final String addServerUpdatedStatus = "ALTER TABLE LocationTracker ADD COLUMN ServerUpdatedStatus BOOLEAN DEFAULT 0";

    private static final String createLocationTracker = "CREATE TABLE LocationTracker ( " +
            "    Id        INTEGER  PRIMARY KEY AUTOINCREMENT " +
            "                       NOT NULL, " +
            "    UserId    INT      NOT NULL, " +
            "    Latitude  FLOAT," +
            "    Longitude FLOAT," +
            "    Address   VARCHAR (200)," +
            "    ServerUpdatedStatus BOOLEAN DEFAULT 0," +
            "    LogDate   DATETIME NOT NULL\n" +
            ");";


    public static boolean upgradeDataBase(final Context context, final SQLiteDatabase db) {
        boolean result = true;
        DataAccessHandler dataAccessHandler;
        try {
            boolean isFreshInstall = PrefUtil.getBool(context, CommonConstants.IS_FRESH_INSTALL, null);
            Log.v(LOG_TAG, "@@@@ isFreshInstall" + isFreshInstall);

            if (isFreshInstall) {
                upgradeDb1(context, db);
                upgradeDb2(context, db);
                upgradeDb4(context, db);
                upgradeDb7(context, db);
                upgradeDb9(context, db);
                UpgradeDb10(context, db);
                UpgradeDb11(context, db);
                UpgradeDb12(context, db);
                UpgradeDb13(context, db);
                UpgradeDb14(context, db);
                UpgradeDb15(context, db);
                UpgradeDb16(context, db);
                UpgradeDb17(context, db);
                UpgradeDb18(context, db);
                upgradeDb19(db);
                upgradeDb20(context, db);
                upgradeDb21(context, db);
                upgradeDb22(context,db);
                upgradeDb23(context,db);
                upgradeDb24(context,db);//Added on 16/09/2021
                upgradeDb25(context,db);//Added on 15/06/2023


            } else {
                boolean isDbUpgradeFinished = PrefUtil.getBool(context, String.valueOf(Palm3FoilDatabase.DATA_VERSION), null);
                Log.v(LOG_TAG, "@@@@ isDbUpgradeFinished" + isDbUpgradeFinished);
                if (!isDbUpgradeFinished) {
                    Log.v(LOG_TAG, "@@@@ DATA_VERSION" + Palm3FoilDatabase.DATA_VERSION);
                    switch (Palm3FoilDatabase.DATA_VERSION) {
                        case 1:
                            upgradeDb1(context, db);
                            break;
                        case 2:
                            upgradeDb2(context, db);
                            break;
                        case 6:
                            UiUtils.showCustomToastMessage("Updating database", context, 0);
                            upgradeDb4(context, db);
                            break;
                        case 8:
                            UiUtils.showCustomToastMessage("Updating database 6&8", context, 0);
                            upgradeDb4(context, db);
                            upgradeDb7(context, db);
                            break;
                        case 9:
                            upgradeDb4(context, db);
//                            upgradeDb7(context, db);
                            upgradeDb9(context, db);
                            UiUtils.showCustomToastMessage("Updating database 6,8&9", context, 0);
                            break;
                        case 10:
                            UpgradeDb10(context, db);
                            UiUtils.showCustomToastMessage("Updating database 10-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            break;
                        case 11:
                            UpgradeDb11(context, db);
                            UiUtils.showCustomToastMessage("Updating database 11-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            break;

                        case 12:
                            UpgradeDb12(context, db);
                            UiUtils.showCustomToastMessage("Updating database 12-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            break;
                        case 13:
                            UpgradeDb13(context, db);
                            UiUtils.showCustomToastMessage("Updating database 13-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            break;
                        case 14:
                            UpgradeDb14(context, db);
                            UiUtils.showCustomToastMessage("Updating database 13-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                            break;
                        case 15:
                            UpgradeDb15(context, db);
                            UiUtils.showCustomToastMessage("Updating database 14-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 16:
                            UpgradeDb16(context, db);
                            UiUtils.showCustomToastMessage("Updating database 15-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 17:
                            UpgradeDb17(context, db);
                            UiUtils.showCustomToastMessage("Updating database 16-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);

                        case 18:
                            UpgradeDb18(context, db);
                            UiUtils.showCustomToastMessage("Updating database 17-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 19:
                            upgradeDb19(db);
                            UiUtils.showCustomToastMessage("Updating database 18-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 20:
                            upgradeDb20(context, db);
                            UiUtils.showCustomToastMessage("Updating database 19-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 21:
                            upgradeDb21(context, db);
                            UiUtils.showCustomToastMessage("Updating database 20-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 22:
                            upgradeDb22(context, db);
                            UiUtils.showCustomToastMessage("Updating database 22-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 23:
                            upgradeDb23(context, db);
                            UiUtils.showCustomToastMessage("Updating database 23-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 24:
                            upgradeDb24(context, db);
                            UiUtils.showCustomToastMessage("Updating database 24-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                        case 25:
                            upgradeDb25(context, db);
                            UiUtils.showCustomToastMessage("Updating database 25-->" + Palm3FoilDatabase.DATA_VERSION, context, 0);
                    }
                } else {
                    Log.v(LOG_TAG, "@@@@ database is already upgraded");
                    return false;
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e);
            result = false;
        } finally {
            if (result) {
                Log.v(LOG_TAG, "@@@@ database is upgraded");
                PrefUtil.putBool(context, CommonConstants.IS_FRESH_INSTALL, false);
                PrefUtil.putBool(context, String.valueOf(Palm3FoilDatabase.DATA_VERSION), true);
            } else {
                PrefUtil.putBool(context, String.valueOf(Palm3FoilDatabase.DATA_VERSION), true);
                Log.e(LOG_TAG, "@@@@ database is upgrade failed or already upgraded");
            }
        }
        return result;
    }

    public static void upgradeDb1(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase " + Palm3FoilDatabase.DATA_VERSION);

        try {

            db.execSQL(alterUserInfoTable1);
            db.execSQL(alterUserInfoTable2);
            db.execSQL(consignment1);
            db.execSQL(consignment2);
            db.execSQL(consignment3);
            db.execSQL(consignment4);
            db.execSQL(consignment5);
            db.execSQL(alterplot1);
            db.execSQL(alterplot2);
            db.execSQL(alterplot3);
            db.execSQL(alterplot4);
            db.execSQL(alterplot5);
            db.execSQL(alterplot6);
            db.execSQL(alterplot7);
            db.execSQL(alterplot8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upgradeDb2(final Context context, final SQLiteDatabase db) {

        Log.d(LOG_TAG, "******* upgradeDataBase 2 " + Palm3FoilDatabase.DATA_VERSION);
        try {

            db.execSQL(alterplot9);
            db.execSQL(alterplot10);
            db.execSQL(alterplot11);
            db.execSQL(alterfilerepository);
            db.execSQL(alterfarmerhistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void upgradeDb4(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 4 " + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(consignment6);
            db.execSQL(consignment7);
        } catch (Exception e) {
            e.printStackTrace();
//            UiUtils.showCustomToastMessage("Db upgrade failed", context, 1);
        }
    }

    public static void upgradeDb7(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 8 " + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(alertPlot12);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db upgrade failed", context, 1);
        }
    }

    public static void upgradeDb9(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 9 " + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(deleteTable_1);
            db.execSQL(deleteTable_2);
            db.execSQL(deleteTable_3);
            db.execSQL(deleteTable_4);
            db.execSQL(deleteTable_5);
            db.execSQL(deleteTable_6);
            db.execSQL(deleteTable_7);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db upgrade failed", context, 1);
        }
    }

    public static void UpgradeDb10(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 10 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {

            db.execSQL(CollectionFarmer);
            db.execSQL(CollectionFarmerBank);
            db.execSQL(CollectionFarmerIdentityProof);
            db.execSQL(CollectionFileRepository);
            db.execSQL(CollectionWithOutPlot);
            db.execSQL(CollectionCenter_Table1);
            db.execSQL(CollectionCenter_Table2);
            db.execSQL(CollectionFarmer_alter);
            db.execSQL(CollectionFarmer_alter1);
            db.execSQL(CollectionFarmer_alter2);
            db.execSQL(CollectionFarmer_alter3);
            db.execSQL(CollectionFarmer_alter4);
            db.execSQL(CollectionFarmer_alter5);
            db.execSQL(CollectionFarmer_alter6);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version10", context, 1);
        }
    }

    public static void UpgradeDb11(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 11 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(ClusterTable);
            db.execSQL(ClusterVillageXref);

        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version11", context, 1);
        }
    }

    public static void UpgradeDb12(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 12 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {

            db.execSQL(ConsigmentFileRepoTable);

        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version12", context, 1);
        }
    }

    private static void UpgradeDb13(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(StockTransferTable);
            db.execSQL(stockTransferReceiveTable);
        } catch (Exception e) {
            e.printStackTrace();
           UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }

    private static void UpgradeDb14(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(createVisitLogTable);
            db.execSQL(addServerUpdatedStatus);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }


    private static void UpgradeDb15(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {

            db.execSQL(createLocationTracker);
            db.execSQL(createVisitLogTable);
            db.execSQL(addServerUpdatedStatus);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }


    private static void UpgradeDb16(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(createVisitLogTable);
            db.execSQL(addServerUpdatedStatus);
            db.execSQL(createLocationTracker);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }


    private static final String createCcRate = "CREATE TABLE CcRate (\n" +
            "    Id                   INTEGER      PRIMARY KEY AUTOINCREMENT" +
            "                                      NOT NULL," +
            "    CollectionCenterCode VARCHAR (10) NOT NULL," +
            "    SizeOfTruck          INT        NOT NULL," +
            "    TransportCost        FLOAT        NOT NULL," +
            "    CombinedCharge       FLOAT        NOT NULL," +
            "    OverweightCharge     FLOAT        NOT NULL," +
            "    IsActive             BIT          NOT NULL," +
            "    CreatedByUserId      INT          NOT NULL," +
            "    CreatedDate          DATETIME     NOT NULL," +
            "    UpdatedByUserId      INT          NOT NULL," +
            "    UpdatedDate          DATETIME     NOT NULL);";


    private static final String addSizeOfTruck = "ALTER TABLE Consignment ADD COLUMN  SizeOfTruckId INT";
    private static final String addIsSharing = "ALTER TABLE Consignment ADD COLUMN  IsSharing BOOLEAN";
    private static final String addTransportCost = "ALTER TABLE Consignment ADD COLUMN  TransportCost FLOAT";
    private static final String addSharingCost = "ALTER TABLE Consignment ADD COLUMN  SharingCost FLOAT";
    private static final String addOverWeightCost = "ALTER TABLE Consignment ADD COLUMN  OverWeightCost FLOAT";
    private static final String addExpectedCost = "ALTER TABLE Consignment ADD COLUMN  ExpectedCost FLOAT";
    private static final String addActualCost = "ALTER TABLE Consignment ADD COLUMN  ActualCost FLOAT";

    private static final String createView = "CREATE VIEW TruckSizeMst as\n" +
            "SELECT T.TypeCdId Id, T.Desc||' Tons' As TruckSize,CAST(t.Desc AS INT ) Capacity,T.IsActive \n" +
            "FROM TypeCdDmt T  JOIN ClassType C ON T.ClassTypeId=C.ClassTypeId\n" +
            "WHERE C.NAME = 'Truck Size'";


    private static void UpgradeDb17(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);
        try {
            db.execSQL(createCcRate);
            db.execSQL(addSizeOfTruck);
            db.execSQL(addIsSharing);
            db.execSQL(addTransportCost);
            db.execSQL(addSharingCost);
            db.execSQL(addOverWeightCost);
            db.execSQL(addExpectedCost);
            db.execSQL(addActualCost);
            db.execSQL(createView);

        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }

    private static void UpgradeDb18(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 13 ******" + Palm3FoilDatabase.DATA_VERSION);

        String plot1 = "ALTER TABLE Plot ADD COLUMN TotalAreaUnderHorticulture  FLOAT";
        String plot2 = "ALTER TABLE Plot ADD COLUMN LandTypeId  INT";
        try {
            db.execSQL(plot1);
            db.execSQL(plot2);


        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 13", context, 1);
        }
    }

    public static void upgradeDb19(final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 18 ******" + Palm3FoilDatabase.DATA_VERSION);

        String createUserSync = "CREATE TABLE UserSync(\n" +
                "Id Integer primary key Autoincrement,\n" +
                "UserId int NOT NULL,\n" +
                "App varchar(50) NOT NULL,\n" +
                "Date datetime NOT NULL,\n" +
                "MasterSync bit NOT NULL,\n" +
                "TransactionSync bit NOT NULL,\n" +
                "ResetData bit NOT NULL,\n" +
                "IsActive bit NOT NULL,\n" +
                "CreatedByUserId int NOT NULL,\n" +
                "CreatedDate datetime NOT NULL,\n" +
                "UpdatedByUserId int NOT NULL,\n" +
                "ServerUpdatedStatus bit NOT NULL,\n" +
                "UpdatedDate datetime NOT NULL)\n";

        try {
            db.execSQL(createUserSync);

        } catch (SQLiteException se) {
            se.printStackTrace();
            Log.v("@@@response", "failed" + se.getMessage());

        }
    }

    private static void upgradeDb20(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 20 ******" + Palm3FoilDatabase.DATA_VERSION);

        String column1 = "ALTER TABLE Collection ADD COLUMN Trpt Float";
        String column2 = "ALTER TABLE Collection ADD COLUMN TokenNo  INT";

        String column4 = "ALTER TABLE CollectionCenter ADD COLUMN ReadMethod  Varchar(50)";
        String column3 = "ALTER TABLE CollectionCenter ADD COLUMN CollectionType Varchar(50)";
        String column5 = "Alter Table CollectionCenter Add NoOfChars Varchar(10) Not Null Default(0)";
        String column6 = "Alter Table CollectionCenter Add UpToCharacter Varchar(50) Not Null Default(0)";

        String TokenNoTable = "CREATE TABLE TokenTable(\n" +
                "Id Integer primary key Autoincrement,\n" +
                "TokenNo Integer NOT NULL, " +
                "CollId Varchar(100)," +
                "WeighbridgeName Varchar(200)," +
                "WeighingDate DATETIME," +
                "VehicleNumber VARCHAR(20)," +
                "DriverName VARCHAR(100)," +
                "    OperatorName              VARCHAR (100) NOT NULL," +

                "Comments VARCHAR(150),  " +
                "  PostingDate      DATETIME      NOT NULL," +
                "CreatedDate DATETIME      NOT NULL," +
                "GrossWeight DECIMAL,CollectionCenterCode Varchar(100))\n";
        String creaateDevicetable = "CREATE TABLE DeviceValues (\n" +
                "    Id       [INTEGER PRIMARIKEY AUTO INCREMENT],\n" +
                "    PortName TEXT,\n" +
                "    IntValue TEXT\n" +
                ");\n";

        try {

            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(column5);
            db.execSQL(column6);
            db.execSQL(TokenNoTable);
            db.execSQL(creaateDevicetable);


        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 20", context, 1);
        }
    }

    private static void upgradeDb21(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 21 ******" + Palm3FoilDatabase.DATA_VERSION);

        String column1 = "ALTER TABLE CollectionWithOutPlot ADD COLUMN Trpt Float";
        String column2 = "ALTER TABLE CollectionWithOutPlot ADD COLUMN TokenNo  INT";



        try {

            db.execSQL(column1);
            db.execSQL(column2);



        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 21", context, 1);
        }
    }
    private static void upgradeDb22(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 21 ******" + Palm3FoilDatabase.DATA_VERSION);
        String column1 = "Alter Table Tablet Add IMEINumber2 Varchar(50)"; // Added by CIS DATE : 20/05/21
        try {
            db.execSQL(column1);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 21", context, 1);
        }
    }

    //Added by CIS 03/08/21
    private static void upgradeDb23(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 23 ******" + Palm3FoilDatabase.DATA_VERSION);
        String column1 = "ALTER TABLE Collection Add VehicleTypeId int";
        String column2 = "ALTER TABLE Collection Add Name Varchar(150)";
        String column3 = "ALTER TABLE Collection Add MobileNumber Varchar(15)";
        String column4 = "ALTER TABLE Collection Add Village Varchar(50)";
        String column5 = "ALTER TABLE Collection Add Mandal Varchar(50)";
        String column6 = "ALTER TABLE Collection Add UnRipen Float";
        String column7 = "ALTER TABLE Collection Add UnderRipe Float";
        String column8 = "ALTER TABLE Collection Add Ripen Float";
        String column9 = "ALTER TABLE Collection Add OverRipe Float";
        String column10 = "ALTER TABLE Collection Add Diseased Float";
        String column11 = "ALTER TABLE Collection Add EmptyBunches Float";
        String column12 = "ALTER TABLE Collection Add FFBQualityLong Float";
        String column13 = "ALTER TABLE Collection Add FFBQualityMedium Float";
        String column14 = "ALTER TABLE Collection Add FFBQualityShort Float";
        String column15 = "ALTER TABLE Collection Add FFBQualityOptimum Float";
        String column16 = "ALTER TABLE Collection Add LooseFruit BIT";
        String column17 = "ALTER TABLE Collection Add LooseFruitWeight int";

        String column18 = "ALTER TABLE CollectionWithOutPlot Add VehicleTypeId int";
        String column19 = "ALTER TABLE CollectionWithOutPlot Add Name Varchar(150)";
        String column20 = "ALTER TABLE CollectionWithOutPlot Add MobileNumber Varchar(15)";
        String column21 = "ALTER TABLE CollectionWithOutPlot Add Village Varchar(50)";
        String column22 = "ALTER TABLE CollectionWithOutPlot Add Mandal Varchar(50)";
        String column23 = "ALTER TABLE CollectionWithOutPlot Add UnRipen Float";
        String column24 = "ALTER TABLE CollectionWithOutPlot Add UnderRipe Float";
        String column25 = "ALTER TABLE CollectionWithOutPlot Add Ripen Float";
        String column26 = "ALTER TABLE CollectionWithOutPlot Add OverRipe Float";
        String column27 = "ALTER TABLE CollectionWithOutPlot Add Diseased Float";
        String column28 = "ALTER TABLE CollectionWithOutPlot Add EmptyBunches Float";
        String column29 = "ALTER TABLE CollectionWithOutPlot Add FFBQualityLong Float";
        String column30 = "ALTER TABLE CollectionWithOutPlot Add FFBQualityMedium Float";
        String column31 = "ALTER TABLE CollectionWithOutPlot Add FFBQualityShort Float";
        String column32 = "ALTER TABLE CollectionWithOutPlot Add FFBQualityOptimum Float";
        String column33 = "ALTER TABLE CollectionWithOutPlot Add LooseFruit BIT";
        String column34 = "ALTER TABLE CollectionWithOutPlot Add LooseFruitWeight int";

        String column35 = "ALTER TABLE TokenTable Add VehicleTypeId int";

        try {
            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(column5);
            db.execSQL(column6);
            db.execSQL(column7);
            db.execSQL(column8);
            db.execSQL(column9);
            db.execSQL(column10);
            db.execSQL(column11);
            db.execSQL(column12);
            db.execSQL(column13);
            db.execSQL(column14);
            db.execSQL(column15);
            db.execSQL(column16);
            db.execSQL(column17);

            db.execSQL(column18);
            db.execSQL(column19);
            db.execSQL(column20);
            db.execSQL(column21);
            db.execSQL(column22);
            db.execSQL(column23);
            db.execSQL(column24);
            db.execSQL(column25);
            db.execSQL(column26);
            db.execSQL(column27);
            db.execSQL(column28);
            db.execSQL(column29);
            db.execSQL(column30);
            db.execSQL(column31);
            db.execSQL(column32);
            db.execSQL(column33);
            db.execSQL(column34);

            db.execSQL(column35);


        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 23", context, 1);
        }
    }

    //Added on 16/09/2021
    private static void upgradeDb24(final Context context, final SQLiteDatabase db) {
        Log.d(LOG_TAG, "******* upgradeDataBase 24 ******" + Palm3FoilDatabase.DATA_VERSION);

        String column1 = "ALTER TABLE Consignment Add UnRipen Float";
        String column2 = "ALTER TABLE Consignment Add UnderRipe Float";
        String column3 = "ALTER TABLE Consignment Add Ripen Float";
        String column4 = "ALTER TABLE Consignment Add OverRipe Float";
        String column5 = "ALTER TABLE Consignment Add Diseased Float"; 
        String column6 = "ALTER TABLE Consignment Add EmptyBunches Float";
        String column7 = "ALTER TABLE Consignment Add FFBQualityLong Float";
        String column8 = "ALTER TABLE Consignment Add FFBQualityMedium Float";
        String column9 = "ALTER TABLE Consignment Add FFBQualityShort Float";
        String column10 = "ALTER TABLE Consignment Add FFBQualityOptimum Float";
        String column11 = "ALTER TABLE Consignment Add LooseFruit BIT";
        String column12 = "ALTER TABLE Consignment Add LooseFruitWeight int";
        String column13 = "ALTER TABLE Consignment Add TareWeight2 Float";

//        //for error logs
//        String ErrorLogTable = "CREATE TABLE ErrorLogs (    " +
//                "    Id          INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
//                "    ClassName   VARCHAR ,\n" +
//                "    MethodName   VARCHAR ,\n" +
//                "    TabId   VARCHAR ,\n" +
//                "    errormessage       VARCHAR(180000),\n" +
//                "    errordetails      VARCHAR ,\n" +
//                "    CreatedDate DATETIME\n" +
//                ");";

        try {

            db.execSQL(column1);
            db.execSQL(column2);
            db.execSQL(column3);
            db.execSQL(column4);
            db.execSQL(column5);
            db.execSQL(column6);
            db.execSQL(column7);
            db.execSQL(column8);
            db.execSQL(column9);
            db.execSQL(column10);
            db.execSQL(column11);
            db.execSQL(column12);
            db.execSQL(column13);
            //for error logs
            //db.execSQL(ErrorLogTable);

        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 24", context, 1);
        }
    }

//    private static void upgradeDb25(final Context context, final SQLiteDatabase db) {
//        Log.d(LOG_TAG, "******* upgradeDataBase 25 ******" + Palm3FoilDatabase.DATA_VERSION);
//
//        //for error logs
//        String ErrorLogTable = "CREATE TABLE ErrorLogs (    " +
//                "    Id          INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
//                "    ClassName   VARCHAR ,\n" +
//                "    MethodName   VARCHAR ,\n" +
//                "    TabId   VARCHAR ,\n" +
//                "    errormessage       VARCHAR(180000),\n" +
//                "    errordetails      VARCHAR ,\n" +
//                "    CreatedDate DATETIME\n" +
//                ");";
//
//        try {
//            //for error logs
//            db.execSQL(ErrorLogTable);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            UiUtils.showCustomToastMessage("Db Upgrade failed for Version 25", context, 1);
//        }
//    }
private static void upgradeDb25(final Context context, final SQLiteDatabase db) {
    Log.d(LOG_TAG, "****** upgradeDataBase 25 *****" + Palm3FoilDatabase.DATA_VERSION);

    //for error logs
    String ErrorLogTable = "CREATE TABLE ErrorLogs (    " +
            "    Id          INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
            "    ClassName   VARCHAR ,\n" +
            "    MethodName   VARCHAR ,\n" +
            "    TabId   VARCHAR ,\n" +
            "    errormessage       VARCHAR(180000),\n" +
            "    errordetails      VARCHAR ,\n" +
            "    CreatedDate DATETIME\n" +
            ");";

    String GraderTable = "CREATE TABLE Grader (    " +
            "    Id          INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
            "    Code   VARCHAR(50) NOT NULL ,\n" +
            "    Name   VARCHAR(150)  NOT NULL,\n" +
            "    VillageId Integer NOT NULL ,\n" +
            "    MobileNumber   VARCHAR (15) NOT NULL,\n" +
            "    FingerPrintData1   NVARCHAR ,\n" +
            "    FingerPrintData2   NVARCHAR ,\n" +
            "    FingerPrintData3   NVARCHAR ,\n" +
            "    IsActive bit DEFAULT 1,\n" +
            "    CreatedByUserId int NOT NULL,\n" +
            "    CreatedDate datetime NOT NULL,\n" +
            "    UpdatedByUserId int NOT NULL,\n" +
            "    UpdatedDate datetime NOT NULL \n"+
            ");";

    String GraderCollectionCenterXrefTable = "CREATE TABLE GraderCollectionCenterXref (    " +
            "    GraderCode   VARCHAR(50) ,\n" +
            "    CollectionCenterCode   VARCHAR (10)\n" +

            ");";

    String fingerprintcolumn = "ALTER TABLE CollectionCenter Add IsFingerPrintReq BIT";
    String gradercodeCollection = "ALTER TABLE Collection Add GraderCode VARCHAR";
    String gradercodecollectionwithoutplot = "ALTER TABLE CollectionWithOutPlot Add GraderCode VARCHAR";

    String column7 = "Alter Table Plot Add IsHOApproved INT";
    String column8 = "Alter Table Plot Add PreProspectiveReasonTypeId INT";
    String column9 = "Alter Table Plot Add PlotUprootmentStatusTypeId INT";

    String GraderAttendance ="CREATE TABLE GraderAttendance("+
            "Id    INTEGER    PRIMARY KEY AUTOINCREMENT,\n"+
            "GraderCode VARCHAR(50) NOT NULL,\n"+
            "CCCode VARCHAR(10) NOT NULL,\n"+
            "ValidDate DateTime NOT NULL,\n"+
            "CreatedByUserId INT NOT NULl,\n"+
            "CreatedDate DateTime NOT NULL,\n"+
            "ServerUpdatedStatus bit NOT NULL \n"+
            ");";
  //  String column10 = "Alter Table GraderAttendance Add CCCode VARCHAR(10) NOT NULL";
   // String column10 = "ALTER TABLE GraderAttendance Add CCCode VARCHAR(10)";
    try {
        //for error logs
        db.execSQL(ErrorLogTable);
        db.execSQL(GraderTable);
        db.execSQL(GraderCollectionCenterXrefTable);
        db.execSQL(fingerprintcolumn);
        db.execSQL(gradercodeCollection);
        db.execSQL(gradercodecollectionwithoutplot);
        db.execSQL(column7);
        db.execSQL(column8);
        db.execSQL(column9);
        db.execSQL(GraderAttendance);
     //   db.execSQL(column10);

    } catch (Exception e) {
        e.printStackTrace();
        UiUtils.showCustomToastMessage("Db Upgrade failed for Version 25", context, 1);
    }
}
}
