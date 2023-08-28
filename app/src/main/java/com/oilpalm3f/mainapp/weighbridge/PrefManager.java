package com.oilpalm3f.mainapp.weighbridge;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class PrefManager {
    private static final String BaudRate = "BaudRate";
    private static final String Button_CR = "Button_CR";
    private static final String Button_LF = "Button_LF";
    private static final String Button_Row = "ButtonRow";
    private static final String Button_Send_ASCII = "Button_Send_ASCII";
    private static final String Button_command = "Button_command";
    private static final String Button_name = "Button_name";
    private static final String DataBits = "DataBits";
    private static final String Keep = "Keep";
    private static final String Parity = "Parity";
    private static final String StopBits = "StopBits";
    private static final String ad_chat_room_Load_Counter = "ad_chat_room_Load_Counter";
    private static final String ad_mainlaunch_page_Counter = "ad_mainlaunch_page_Counter";
    private static final String ad_setting_page_Counter = "ad_setting_page_Counter";
    private static final String font_size = "FontSize";
    private static final String isRemove_ads = "isRemove_ads";
    private Context _context;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    PrefManager(Context context) {
        this._context = context;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.pref = defaultSharedPreferences;
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        this.editor = edit;
        edit.apply();
    }

    /* access modifiers changed from: package-private */
    public int getBaudRate() {
        return Integer.parseInt(this.pref.getString(BaudRate, "9600"));
    }

    /* access modifiers changed from: package-private */
    public int getDataBits() {
        return Integer.parseInt(this.pref.getString(DataBits, "8"));
    }

    /* access modifiers changed from: package-private */
    public int getParity() {
        return Integer.parseInt(this.pref.getString(Parity, "0"));
    }

    /* access modifiers changed from: package-private */
    public int getStopBits() {
        return Integer.parseInt(this.pref.getString(StopBits, "1"));
    }

    /* access modifiers changed from: package-private */
    public void setIsRemove_ads(Boolean bool) {
        this.editor.putBoolean(isRemove_ads, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getIsRemove_ads() {
        if (this.pref.contains(isRemove_ads)) {
            return Boolean.valueOf(this.pref.getBoolean(isRemove_ads, true));
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean getKeepscreen() {
        return this.pref.getBoolean(Keep, false);
    }

    /* access modifiers changed from: package-private */
    public int getButtonRow() {
        return Integer.parseInt(this.pref.getString(Button_Row, "1"));
    }

    /* access modifiers changed from: package-private */
    public int getFont_Size() {
        return Integer.parseInt(this.pref.getString(font_size, "14"));
    }

    /* access modifiers changed from: package-private */
    public void setButton_name(int i, String str) {
        SharedPreferences.Editor editor2 = this.editor;
        editor2.putString(Button_name + i, str);
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public String getButton_name(int i) {
        SharedPreferences sharedPreferences = this.pref;
        return sharedPreferences.getString(Button_name + i, (String) null);
    }

    /* access modifiers changed from: package-private */
    public void setButton_command(int i, String str) {
        SharedPreferences.Editor editor2 = this.editor;
        editor2.putString(Button_command + i, str);
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public String getButton_command(int i) {
        SharedPreferences sharedPreferences = this.pref;
        return sharedPreferences.getString(Button_command + i, (String) null);
    }

    /* access modifiers changed from: package-private */
    public void setButton_CR(int i, Boolean bool) {
        SharedPreferences.Editor editor2 = this.editor;
        editor2.putBoolean(Button_CR + i, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getButton_CR(int i) {
        SharedPreferences sharedPreferences = this.pref;
        return Boolean.valueOf(sharedPreferences.getBoolean(Button_CR + i, true));
    }

    /* access modifiers changed from: package-private */
    public void setButton_LF(int i, Boolean bool) {
        SharedPreferences.Editor editor2 = this.editor;
        editor2.putBoolean(Button_LF + i, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getButton_LF(int i) {
        SharedPreferences sharedPreferences = this.pref;
        return Boolean.valueOf(sharedPreferences.getBoolean(Button_LF + i, true));
    }

    /* access modifiers changed from: package-private */
    public void setSend_ASCII(int i, Boolean bool) {
        SharedPreferences.Editor editor2 = this.editor;
        editor2.putBoolean(Button_Send_ASCII + i, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getSend_ASCII(int i) {
        SharedPreferences sharedPreferences = this.pref;
        return Boolean.valueOf(sharedPreferences.getBoolean(Button_Send_ASCII + i, true));
    }

    /* access modifiers changed from: package-private */
    public void setButton_CR(Boolean bool) {
        this.editor.putBoolean(Button_CR, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getButton_CR() {
        return Boolean.valueOf(this.pref.getBoolean(Button_CR, true));
    }

    /* access modifiers changed from: package-private */
    public void setButton_LF(Boolean bool) {
        this.editor.putBoolean(Button_LF, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getButton_LF() {
        return Boolean.valueOf(this.pref.getBoolean(Button_LF, true));
    }

    /* access modifiers changed from: package-private */
    public void setSend_ASCII(Boolean bool) {
        this.editor.putBoolean(Button_Send_ASCII, bool.booleanValue());
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public Boolean getSend_ASCII() {
        return Boolean.valueOf(this.pref.getBoolean(Button_Send_ASCII, true));
    }

    /* access modifiers changed from: package-private */
    public void setChatRoomAds_Counter(int i) {
        this.editor.putInt(ad_chat_room_Load_Counter, i + 1);
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public int getChatRoomAds_Counter() {
        return this.pref.getInt(ad_chat_room_Load_Counter, 0);
    }

    /* access modifiers changed from: package-private */
    public void setAd_setting_page_Counter(int i) {
        this.editor.putInt(ad_setting_page_Counter, i + 1);
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public int getAd_setting_page_Counter() {
        return this.pref.getInt(ad_setting_page_Counter, 0);
    }

    /* access modifiers changed from: package-private */
    public void setAd_mainlaunch_page_Counter(int i) {
        this.editor.putInt(ad_mainlaunch_page_Counter, i + 1);
        this.editor.commit();
    }

    /* access modifiers changed from: package-private */
    public int getAd_mainlaunch_page_Counter() {
        return this.pref.getInt(ad_mainlaunch_page_Counter, 0);
    }
}
