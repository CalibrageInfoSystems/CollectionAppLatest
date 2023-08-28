package com.oilpalm3f.mainapp.datasync.helpers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager<T> {

    private static final String LOG_TAG = DataManager.class.getName();
    private static final DataManager instance = new DataManager();
    public static final String COLLECTION_CENTER_DATA = "collection_center_data";
    public static final String stockTransferImage = "stockTransferImage";
    public static final String To_COLLECTION_CENTER_DATA = "to_collection_center_data";
    public static final String COLLECTION_CENTER_ID = "collection_center_ID";
    public static final String SELECTED_FARMER_DATA = "selected_farmer_data";
    public static final String SELECTED_FARMER_PLOT_DATA = "selected_farmer_plot_data";
    public static final String EXTRA_SELECTED_FARMER_PLOT_DATA = "extra_selected_farmer_plot_data";
    public static final String USER_DETAILS = "user_details";
    public static final String USER_VILLAGES = "user_villages";
    public static final String MILL_INFORMATION = "mill_information";
    public static final String PRIVATE_WEIGHBRIDGE_INFO = "private_weighbridge_info";
    public static final String TOTAL_FARMERS_DATA = "total_farmers_data";
    public static final String EXTRA_PLOTS = "extra_plots";
    public static final String Manual_Images = "ManualImages";
    public static final String COLLECTION_CENTRE11 = "collectioncenter1";

    private final Map<String, T> dataMap = new ConcurrentHashMap<>();

    public static DataManager getInstance() {
        return instance;
    }

    public void addData(final String type, final T data) {
        deleteData(type);
        dataMap.put(type, data);
    }

    public T getDataFromManager(final String type) {
        return dataMap.get(type);
    }

    public void deleteData(final String type) {
        if (dataMap.get(type) != null) {
            dataMap.remove(type);
        }
    }
}
