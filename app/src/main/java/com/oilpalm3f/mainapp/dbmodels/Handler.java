package com.oilpalm3f.mainapp.dbmodels;

/**
 * Created by Lenovo on 3/5/2018.
 */

public class Handler {
    public  String Tab_Id;
    public  String Version;

    public Handler() {
    }

    public Handler(String Tab_Id, String Version) {
        this.Tab_Id = Tab_Id;
        this.Version = Version;

    }
    public String getTab_Id() {
        return Tab_Id;
    }

    public void setTab_Id(String tab_Id) {
        Tab_Id = tab_Id;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }





}
