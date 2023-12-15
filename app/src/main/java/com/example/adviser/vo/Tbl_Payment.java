package com.example.adviser.vo;

public class Tbl_Payment {

    private String paidAmount;

    private String shareAmount;

    private String userEmail;

    private String ansUserEmail;

    public Tbl_Payment() {
        this.paidAmount = paidAmount;
        this.shareAmount = shareAmount;
        this.userEmail = userEmail;
        this.ansUserEmail = ansUserEmail;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(String shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAnsUserEmail() {
        return ansUserEmail;
    }

    public void setAnsUserEmail(String ansUserEmail) {
        this.ansUserEmail = ansUserEmail;
    }
}
