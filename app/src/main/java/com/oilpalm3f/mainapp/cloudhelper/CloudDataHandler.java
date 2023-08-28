package com.oilpalm3f.mainapp.cloudhelper;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.dbmodels.FileRepository;
import com.oilpalm3f.mainapp.dbmodels.DataCountModel;
import com.oilpalm3f.mainapp.dbmodels.FarmerPhotos;
import com.oilpalm3f.mainapp.dbmodels.MasterDataQuery;
import com.oilpalm3f.mainapp.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class CloudDataHandler {

    private static final String LOG_TAG = CloudDataHandler.class.getName();

    //To Place Data In Cloud
    public static synchronized void placeDataInCloud(final JSONObject values, final String url, final ApplicationThread.OnComplete<String> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "placeDataInCloud..", new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient.postDataToServerjson(url, values, new ApplicationThread.OnComplete<String>() {
                        @Override
                        public void execute(boolean success, String result, String msg) {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"placeDataInCloud", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                try {
                                  Log.v(LOG_TAG, "@@@ Transactions sync success for " + result);
                                    onComplete.execute(true, result, msg);
                                } catch (Exception e) {
                                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"placeDataInCloud", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                    e.printStackTrace();
                                   Log.v(LOG_TAG, "@@@ Transactions sync success for " + result);

                                    onComplete.execute(true, result, msg);
                                }
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"placeDataInCloud", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                Log.v(LOG_TAG, "@@@ Transactions sync failed for " + result);
                                onComplete.execute(false, result, msg);
                            }
                            Log.v(LOG_TAG, "@Error while saving in server "+result);
                        }
                    });
                } catch (Exception e) {
                    Log.v(LOG_TAG, "@Error while getting "+e.getMessage());
                }
            }
        });

    }


    public static void getRecordStatus(final String url, final ApplicationThread.OnComplete<String> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getRecordStatus...", new Runnable() {
            @Override
            public void run() {
                HttpClient.get(url, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getRecordStatus", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            try {
                                onComplete.execute(success, result, msg);
                            } catch (Exception e) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getRecordStatus", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                e.printStackTrace();
                                onComplete.execute(false, result, msg);
                            }
                        } else{
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getRecordStatus", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            onComplete.execute(success, result, msg);
                        }

                    }
                });
            }
        });

    }

    //Pairing Data to the Tables
    public static void getGenericData(final String url, final LinkedHashMap dataMap, final ApplicationThread.OnComplete<List<DataCountModel>> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getGenericData...", new Runnable() {
            @Override
            public void run() {
                HttpClient.post(url, dataMap, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getGenericData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            try {
                                JSONObject parentData = new JSONObject(result);
                                List<DataCountModel> dataCountModelList = new ArrayList<DataCountModel>();
                                Iterator keysToCopyIterator = parentData.keys();
                                List<String> keysList = new ArrayList<>();
                                while (keysToCopyIterator.hasNext()) {
                                    String key = (String) keysToCopyIterator.next();
                                    keysList.add(key);
                                }
                                for (String tableName : keysList) {
                                    Gson gson = new Gson();
                                    DataCountModel dataCountModel = gson.fromJson(parentData.getJSONObject(tableName).toString(), DataCountModel.class);

                                    if (dataCountModel.getCount() > 0) {
                                        dataCountModelList.add(dataCountModel);
                                    }

                                }
                                onComplete.execute(success, dataCountModelList, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getGenericData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(success, null, msg);
                                SharedPrefManager.set_isDataSyncRunning(false);
                            }

                            //  EventBus.getDefault().post(false);
                            SharedPrefManager.set_isDataSyncRunning(false);

                        } else {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getGenericData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            onComplete.execute(success, null, msg);
                            SharedPrefManager.set_isDataSyncRunning(false);
                        }
                    }
                });
            }
        });
    }

//    public static void getGenericData(final String url, final LinkedHashMap objectHashMap, final ApplicationThread.OnComplete<String> onComplete) {
//        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getGenericData...", new Runnable() {
//            @Override
//            public void run() {
//                HttpClient.post(url, objectHashMap, new ApplicationThread.OnComplete<String>() {
//                    @Override
//                    public void execute(boolean success, String result, String msg) {
//                        if (success) {
//                            try {
//                                onComplete.execute(success, result.toString(), msg);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                onComplete.execute(success, result, msg);
//                            }
//                        } else {
//                            onComplete.execute(success, result, msg);
//                        }
//                    }
//                });
//            }
//        });
//
//    }

    //Pairing and getting Master Data
    public static void getMasterData(final String url, final LinkedHashMap dataMap, final ApplicationThread.OnComplete<HashMap<String, List>> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getMasterData...", new Runnable() {
            @Override
            public void run() {
                HttpClient.post(url, dataMap, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getMasterData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            try {

                                JSONObject parentMasterDataObject = new JSONObject(result);
                                Iterator keysToCopyIterator = parentMasterDataObject.keys();
                                List<String> keysList = new ArrayList<>();
                                while (keysToCopyIterator.hasNext()) {
                                    String key = (String) keysToCopyIterator.next();
                                    keysList.add(key);
                                }

                                Log.v(LOG_TAG, "@@@@ Tables Size " + keysList.size());
                                LinkedHashMap<String, List> masterDataMap = new LinkedHashMap<>();
                                for (String tableName : keysList) {
                                    if (!tableName.equalsIgnoreCase("KnowledgeZone")&& !tableName.equalsIgnoreCase("KRA") && !tableName.equalsIgnoreCase("DigitalContract")) {
                                        masterDataMap.put(tableName, CommonUtils.toList(parentMasterDataObject.getJSONArray(tableName)));
                                    }
                                }

                                Log.v(LOG_TAG, "@@@@ Tables Data " + masterDataMap.size());
                                
                                onComplete.execute(success, masterDataMap, msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getMasterData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(success, null, msg);
                            }
                        } else{
                            onComplete.execute(success, null, msg);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getMasterData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        }

                    }
                });
            }
        });

    }

    //Pairing and getting Transaction Data
    public static void getTransactionsData(final String url, final HashMap dataMap, final ApplicationThread.OnComplete<LinkedHashMap<String, List<MasterDataQuery>>> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getMasterData...", new Runnable() {
            @Override
            public void run() {
                HttpClient.post(url, dataMap, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTransactionsData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            try {

                                JSONObject parentMasterDataObject = new JSONObject(result);

                                Iterator keysToCopyIterator = parentMasterDataObject.keys();
                                List<String> keysList = new ArrayList<String>();
                                while (keysToCopyIterator.hasNext()) {
                                    String key = (String) keysToCopyIterator.next();
                                    keysList.add(key);
                                }

                                Log.v(LOG_TAG, "@@@@ Tables Size " + keysList.size());

                                LinkedHashMap<String, List<MasterDataQuery>> masterDataMap = new LinkedHashMap<>();
                                for (String arrName : keysList) {
                                    JSONArray childJsonArr = parentMasterDataObject.getJSONArray(arrName);
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<MasterDataQuery>>() {
                                    }.getType();
                                    List<MasterDataQuery> masterDataQueriesList = gson.fromJson(childJsonArr.toString(), type);

                                    Log.v(LOG_TAG, "@@@@ Table name " + arrName + " data size " + masterDataQueriesList.size());
                                    if (masterDataQueriesList.size() > 0) {
                                        masterDataMap.put(arrName, masterDataQueriesList);
                                    }
                                }

                                onComplete.execute(success, masterDataMap, msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTransactionsData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(success, null, msg);
                            }
                        } else{
                            onComplete.execute(success, null, msg);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTransactionsData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        }

                    }
                });
            }
        });

    }


    /**
     * This function upload the large file to server with other POST values.
     *
     * @param file
     * @param targetUrl
     * @return
     */
    public static String uploadFileToServer(File file, String targetUrl, final ApplicationThread.OnComplete<String> onComplete) {
        String response = "error";
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String urlServer = targetUrl;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String token = CommonConstants.USER_ID;
            outputStream.writeBytes("Content-Disposition: form-data; name=\"userId\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
            outputStream.writeBytes("Content-Length: " + token.length() + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(token + lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String taskId = CommonConstants.TAB_ID;
            outputStream.writeBytes("Content-Disposition: form-data; name=\"tabId\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
            outputStream.writeBytes("Content-Length: " + taskId.length() + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(taskId + lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String connstr = null;
            connstr = "Content-Disposition: form-data; name=\"UploadDatabase\";filename=\""
                    + file.getAbsolutePath() + "\"" + lineEnd;

            outputStream.writeBytes(connstr);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            System.out.println("Image length " + bytesAvailable + "");
            try {
                while (bytesRead > 0) {
                    try {
                        outputStream.write(buffer, 0, bufferSize);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        response = "outofmemoryerror";
                        onComplete.execute(false, response, response);
                        return response;
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
                onComplete.execute(false, response, e.getMessage());
                return response;
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            System.out.println("Server Response Code " + " " + serverResponseCode);
            System.out.println("Server Response Message " + serverResponseMessage);

            if (serverResponseCode == 200) {
                response = "true";
                onComplete.execute(true, response, response);
            } else {
                response = "false";
                onComplete.execute(false, response, response);
            }

            fileInputStream.close();
            outputStream.flush();

            connection.getInputStream();
            //for android InputStream is = connection.getInputStream();
            java.io.InputStream is = connection.getInputStream();

            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }

            String responseString = b.toString();
            System.out.println("response string is" + responseString); //Here is the actual output

            outputStream.close();
            outputStream = null;

        } catch (Exception ex) {
            // Exception handling
            response = "error";
            System.out.println("Send file Exception" + ex.getMessage() + "");
            onComplete.execute(false, response, "Send file Exception" + ex.getMessage() + "");
            ex.printStackTrace();
        }
        return response;
    }

    public static void getTabTransData(final List<FileRepository> fileRepositoryList, final String url, final ApplicationThread.OnComplete<List> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getTabTransData...", new Runnable() {
            @Override
            public void run() {
                HttpClient.get(url, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        try {
                            if (success) {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTabTransData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                List dataToInsert = new ArrayList();
                                JSONArray parentTransactionSyncObject = new JSONArray(result);
                                int length = parentTransactionSyncObject.length();
                                for (int i = 0; i < parentTransactionSyncObject.length(); i++) {
                                    JSONObject eachDataObject = parentTransactionSyncObject.getJSONObject(i);
                                    dataToInsert.add(CommonUtils.toMap(eachDataObject));
                                }

                                if (null != fileRepositoryList) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<FileRepository>>() {
                                    }.getType();
                                    List<FileRepository> fileRepositoryInnerList = gson.fromJson(result, type);
                                    fileRepositoryList.addAll(fileRepositoryInnerList);
                                }
                                onComplete.execute(success, dataToInsert, msg);
                            } else {
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTabTransData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(success, null, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getTabTransData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            onComplete.execute(success, null, msg);
                        }
                    }
                });
            }
        });

    }


    public static void getFarmerPhotosData(final String url, final ApplicationThread.OnComplete<List<FarmerPhotos>> onComplete) {
        ApplicationThread.bgndPost(CloudDataHandler.class.getName(), "getFarmerPhotosData...", new Runnable() {
            @Override
            public void run() {
                HttpClient.get(url, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getFarmerPhotosData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                            try {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<FarmerPhotos>>() {
                                }.getType();
                                List<FarmerPhotos> farmerPhotosList = gson.fromJson(result, type);
                                onComplete.execute(success, farmerPhotosList, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                                palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getFarmerPhotosData", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                                onComplete.execute(success, null, msg);
                            }
                        } else{
                            onComplete.execute(success, null, msg);
                            palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getFarmerPhotosData", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        }

                    }
                });
            }
        });

    }
}
