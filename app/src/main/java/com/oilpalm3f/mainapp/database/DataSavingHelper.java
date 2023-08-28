package com.oilpalm3f.mainapp.database;

import static com.oilpalm3f.mainapp.ui.SplashScreen.palm3FoilDatabase;

import android.content.Context;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oilpalm3f.mainapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.mainapp.common.CommonConstants;
import com.oilpalm3f.mainapp.common.CommonUtils;
import com.oilpalm3f.mainapp.datasync.helpers.DataManager;
import com.oilpalm3f.mainapp.dbmodels.FileRepository1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataSavingHelper {
    private static final String LOG_TAG = DataSavingHelper.class.getName();


//TO Save the Images Taken while doing a collection
    public static void saveOFTObservationTakenImages(final Context context, final ApplicationThread.OnComplete<String> oncomplete){
        List dataToSave = getOFTObservationImages();
        if (null != dataToSave && !dataToSave.isEmpty()) {
            final DataAccessHandler dataAccessHandler = new DataAccessHandler(context);
            dataAccessHandler.insertDataOld("ConsignmentRepository", dataToSave, new ApplicationThread.OnComplete<String>() {
                @Override
                public void execute(boolean success, String result, String msg) {
                    if (success) {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveOFTObservationTakenImages", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        Log.v(LOG_TAG, "@@@ Manual Images  data saved successfully");
                        DataManager.getInstance().deleteData(DataManager.Manual_Images);
                        oncomplete.execute(true, "Manual Images   data saved successfully", "");
                        //   saveEconomicData(context, oncomplete);
                    } else {
                        palm3FoilDatabase.insertErrorLogs(LOG_TAG,"saveOFTObservationTakenImages", CommonConstants.TAB_ID,"",msg,CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                        Log.e(LOG_TAG, "@@@ Manual Images  data saving failed due to " + msg);
                        oncomplete.execute(false, "data saving failed for Manual Images", "");
                        //  saveEconomicData(context, oncomplete);
                    }
                }
            });
        }

    }

    private static List getOFTObservationImages() {
        List<FileRepository1> oftObservationTakenImages =
                (List<FileRepository1>) DataManager.getInstance().getDataFromManager(DataManager.Manual_Images);
        List dataToInsert = new ArrayList();
        if (null != oftObservationTakenImages && !oftObservationTakenImages.isEmpty()) {
            for (FileRepository1 mPictureOfCrop : oftObservationTakenImages) {
                JSONObject ccData = null;
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    ccData = new JSONObject(gson.toJson(mPictureOfCrop));
                    dataToInsert.add(CommonUtils.toMap(ccData));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "@@@ error while converting Manual Images Details data");
                    palm3FoilDatabase.insertErrorLogs(LOG_TAG,"getOFTObservationImages", CommonConstants.TAB_ID,"",e.getMessage(),CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                }
            }
        }
        return dataToInsert;
    }
}
