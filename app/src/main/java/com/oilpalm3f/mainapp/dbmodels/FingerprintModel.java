package com.oilpalm3f.mainapp.dbmodels;

public class FingerprintModel {

    private int SuccessCode;
    private String SuccessMessage;

    public int getSuccessCode() {
        return SuccessCode;
    }

    public void setSuccessCode(int successCode) {
        SuccessCode = successCode;
    }

    public String getSuccessMessage() {
        return SuccessMessage;
    }

    public void setSuccessMessage(String successMessage) {
        SuccessMessage = successMessage;
    }
}
