package com.oilpalm3f.mainapp.cloudhelper;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.oilpalm3f.mainapp.BuildConfig;


//Urls Listing

public class Config {

    public static final boolean DEVELOPER_MODE = false;
//    public static final String url = "http://192.168.1.145/local3fservice/api/SyncTransactions/SyncTransactions";

    public static final String login_url = "/Login";

    //http://localhost/3FSmartPalm_Performance/API/api/SyncTransactions/GetCollectionAppCount

 //  public static String live_url = "http://120.138.8.8:9020/API/api";
//      public static  String live_url = "http://183.82.103.171:8085/API";
     public static  String live_url = "http://182.18.139.166/3FOilPalm/API/api";
      //public static  String live_url = "http://183.82.111.111/3FOilPalm_Live/API/api";
       //public static String live_url = "http://183.82.111.111/3FOilPalm/API/api"; //test
    //   public static String live_url = "http://182.18.157.215/3FSmartPalm/API/api";//new Test
  //  public static String live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api"; //UAT
 //   public static String live_url = "http://182.18.157.215/3FSmartPalm_Performance/API/api"; //UAT

    //local URl
    public static void initialize() {

        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
           //live_url = "http://183.82.111.111/3FOilPalm_Live/API/api" ;
          //live_url = "http://183.82.111.111/3FOilPalm/API/api" ;//test
        //  live_url = "http://182.18.157.215/3FSmartPalm/API/api"; //new Test
    //   live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";
           // live_url = "http://182.18.157.215/3FSmartPalm_Performance/API/api";
//      //      live_url = "http://183.82.111.111/3FOilPalm/API/api" ;
         live_url = "http://182.18.139.166/3FOilPalm/API/api" ;
     //             live_url = "http://120.138.8.8:9020/API/api";

        } else {
       //   live_url = "http://120.138.8.8:9020/API/api";
            live_url = "http://182.18.139.166/3FOilPalm/API/api" ;
            //live_url = "http://183.82.111.111/3FOilPalm/API/api" ; //test
          //  live_url = "http://182.18.157.215/3FSmartPalm/API/api"; //new Test
          //  live_url = "http://182.18.157.215/3FSmartPalm_Nursery/API/api";
          //  live_url = "http://182.18.157.215/3FSmartPalm_Performance/API/api";
//            live_url = "http://183.82.111.111/3FOilPalm/API/api" ;
            //live_url = "http://183.82.111.111/3FOilPalm_Live/API/api" ;
        }

    }


    public static final String masterSyncUrl = "/SyncMasters/SyncCollection";
//    public static final String masterSyncUrl = "http://192.168.1.145/OilPalmService/api/MastersSync/SyncMasters";
//    public static final String live_url = "http://192.168.1.145/API/api";

    public static final String updateStockReceive = "/Consignment/UpdateStockTransfer";
    public static final String transactionSyncURL = "/TranSync/SyncTransactions";
    public static final String collectiontransactionSyncURL = "/SyncTransactions/SyncTransactions";
    public static final String imageUploadURL = "/SyncTransactions/UploadImage";
    public static final String locationTrackingURL = "/LocationTracker/SaveOfflineLocations";
//  public static final String transactionSyncURL = "http://192.168.1.145/OilPalmService/api/TranSync/SyncTransactions";

    public static final String findcollectioncode = "/SyncTransactions/FindCollectionCode/%s";
    public static final String findcollectionWithOutPlot = "/SyncTransactions/FindCollectionWithOutPlot/%s";
    public static final String findconsignmentcode = "/SyncTransactions/FindConsignmentCode/%s";
    public static final String findcollectionplotcode = "/SyncTransactions/FindCollectionPlotXref/%s/%s";
    public static final String FindImage = "/TranSync/FindImage/%s/%s";
    public static final String updatedbFile = "/TabDatabase/UploadDatabase";
    public static final String getTransactionSyncURL = "/TranSync/TabSync";

    public static final String getTransCount = "/SyncTransactions/GetCount";//{Date}/{UserId}
   // public static final String getTransCount = "/SyncTransactions/GetCollectionAppCount";//{Date}/{UserId}


    public static final String getTransCountByVillageCode = "/TranSync/GetCountByVillage/%s/%s/%s";//{Date}/{UserId}{Village}
    public static final String getCountByVillage = "/TranSync/GetCountByVillage/%s/%s";//{Date}/{UserId}
    public static final String getTransData = "/SyncTransactions/%s";//api/TranSync/SyncFarmers/{Date}/{UserId}/{Index}
    public static final String getTransDataByVillageCode = "/TranSync/%s/%s/%s/%s/%s";//api/TranSync/SyncFarmers/{Date}/{UserId}/{Village}/{Index}
    //    public static final String getTransCount = "TranSync/GetCount/{%s}/{%s}";
    public static final String validateTranSync = "/TranSync/ValidateTranSync/%s";

  //  public static final String image_url = "http://182.18.139.166/3FOilPalm/3FOilPalmRepository/FileRepository/";
    public static final String image_url = "http://182.18.139.166/3FOilPalm/FileRepository/";  //using this on for live
   // public static final String image_url = "http://183.82.111.111/3FOilPalm/3FFileRepositery/FileRepository/";//test
  //  public static final String image_url = "http://182.18.157.215/3FSmartPalm_Nursery/3FSmartPalm_Nursery_Repo/FileRepository/";
}
