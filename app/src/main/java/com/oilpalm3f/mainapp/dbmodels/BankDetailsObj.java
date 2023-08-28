package com.oilpalm3f.mainapp.dbmodels;

/**
 * Created by skasam on 9/28/2016.
 */
public class BankDetailsObj {


    private String BankDetailId;
    private String BankName;
    private String AccountHolderName;
    private String AccountNumber;
    private String BranchName;
    private String IFSCCode;

    public String getBankDetailId() {
        return BankDetailId;
    }

    public void setBankDetailId(String bankDetailId) {
        BankDetailId = bankDetailId;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAccountHolderName() {
        return AccountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        AccountHolderName = accountHolderName;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }
}
