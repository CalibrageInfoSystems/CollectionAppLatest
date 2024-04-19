package com.oilpalm3f.mainapp.common;

import android.os.Environment;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siva on 28/09/16.
 */

//Defining Commonly Used String & Integers
public class CommonConstants {
    public static String baudrate="";
    public static int databits=0;
    public static int stopbits=0;
    public static int parity=0;

    public static String collectionType = "";
    public static String changecollectionType = "";
    public static String migrationSync = "true";

    public static String stateCode = "";
    public static String districtCode = "";
    public static String mandalCode = "";
    public static String stateName = "";
    public static String villageCode = "";
    public static String districtName = "";
    public static String mandalName = "";
    public static String bankCode = "";
    public static String castCode = "";
    public static String villageName = "";


    public static String districtCodePlot = "";
    public static String mandalCodePlot = "";
    public static String villageCodePlot = "";

    public static int prevMandalPos = 0;
    public static int prevVillagePos = 0;
    public static int preDistrictPos = 0;
    public static double Current_Latitude = 0.0;
    public static double Current_Longitude = 0.0;


    public static String farmerFirstName = "";
    public static String farmerMiddleName = "";
    public static String farmerLastName = "";


    public static String DATE_FORMAT_DDMMYYYY = "yyyy-MM-dd";
    public static String DATE_FORMAT_DDMMYYYY_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static String PostingDate_FORMAT_DDMMYYYY_HHMMSS = "dd/MM/yyyy HH:mm:ss";
    public static String DATE_FORMAT_DDMMYYYY_HHMMSS1 = "dd-MM-yyyy HH:mm:ss";
    public static String DATE_FORMAT_1 = "dd-MM-yyyy HH:mm a";
    public static String DATE_FORMAT_2 = "dd MM yyyy";
    public static String DATE_FORMAT_3 = "dd/MM/yyyy";

    public static String IS_MASTER_SYNC_SUCCESS = "is_master_sync_success";
    //public static String IS_MASTER_SYNC_SUCCESS = null;
    public static String IS_FRESH_INSTALL = "is_fresh_install";
    public static String PREVIOUS_PRINTER_ADDRESS = "previous_printer_address";
    public static String IS_ANY_PALM_DETAIL_Fill = "is_any_palm_detail_fill";
    public static String USER_ID = "12345";
    public static String TAB_ID = "0000";
    public static String TABLET_ID = "0000";
    public static String PASSWORD = "";
    public static String USER_NAME = "";
    public static String FARMER_CODE = "";
    public static String PLOT_CODE = "";
    public static String ServerUpdatedStatus = "0";
    public static String TokenNumber = "";

    public static String countryID = "1";
    public static String stateId = "";
    public static String districtId = "";
    public static String mandalId = "";
    public static String villageId = "";

    public static String villageIdPlot = "";
    public static String mandalIdPlot = "";
    public static String districtIdPlot = "";

    public static String landMark = "";
    public static String palmArea = "";
    public static String plotVillage = "";
    public static String gpsArea = "";


    public static String CropMaint_LastVisit = "CropMaint_LastVisit";
    public static String CropMaint_Comments = "CropMaint_Comments";
    public static String CropMaint_Pruning = "CropMaint_Pruning";
    public static String CropMaint_Rate = "CropMaint_Rate";
    public static String CropMaint_type = "CropMaint_Type";
    public static String CropMaint_type1 = "1";
    public static String CropMaint_type2 = "2";
    public static int Crop_pruning = 0;

    public static double AREA_ALLOCATED = 0;
    public static String COUNT_OF_TREES = "";
    public static String screenFrom = "";
    public static String REPRESENTATIVE = "";
    public static String PlantprotectionType1 = "1";
    public static String PlantprotectionType2 = "2";

    public static String EmployeeDistrict = "";

    public static String FarmersCount = "FarmerCount";
    public static String FarmerBankCount = "FarmerBankCount";
    public static String PlotCount = "PlotCount";
    public static String FarmerHistoryCount = "FarmerHistoryCount";
    public static String PlantationCount = "PlantationCount";
    public static String AddressCount = "AddressCount";
    public static String FileRepositoryCount = "FileRepositoryCount";

    public static String FarmersImageCount = "FarmersImageCount";
    public static String BankDetailsCount = "BankDetailsCount";
    public static String LandDetailsCount = "LandDetailsCount";

    public static String CollectionType="";
    public static String ReadMethod="";
    public static String NoOfChars="";
    public static String UpToCharacter="";
    public static Boolean IsFingerPrintReq;


    public static String isComingfrom="";


    public static final String JPEG_FILE_SUFFIX = ".jpg";
    public static String farmerPhotosFilePath = Environment.getExternalStorageDirectory().toString()+"/3F_Pictures/" + "FarmerPhotos/";
    public static List<String> selectedPlotCode = new ArrayList<>();

    public static Float GrossWeight=0.0f;

    public static String flowFrom="";
    public static String PrinterName="";

    public static Integer USER_RoleID = 12;

}


