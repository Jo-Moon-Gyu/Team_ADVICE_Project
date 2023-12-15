package com.example.adviser.vo;

public class PayVO {

    private String payName;
    private String payPrice;
    private int payImg;

    public PayVO(String payName, String payPrice, int payImg) {
        this.payName = payName;
        this.payPrice = payPrice;
        this.payImg = payImg;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public int getPayImg() {
        return payImg;
    }

    public void setPayImg(int payImg) {
        this.payImg = payImg;
    }
}
