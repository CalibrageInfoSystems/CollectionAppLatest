package com.oilpalm3f.mainapp.dbmodels;

import com.google.gson.annotations.Expose;

/**
 * Created by Siva on 28/09/16.
 */
public class MasterDataQuery {

    @Expose
    private String query;
    private String tableName;


    public String getQuery() {
        return query;
    }
}
