package com.example.adviser.vo;

public class Tbl_Request {
    private String userEmail;
    private String reqUserNick;
    private String reqTitle;
    private String reqContent;
    private String reqImg;

    public Tbl_Request() {
        this.userEmail = userEmail;
        this.reqUserNick = reqUserNick;
        this.reqTitle = reqTitle;
        this.reqContent = reqContent;
        this.reqImg = reqImg;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNick() {
        return reqUserNick;
    }

    public void setReqUserNick(String reqUserNick) {
        this.reqUserNick = reqUserNick;
    }

    public String getReqTitle() {
        return reqTitle;
    }

    public void setReqTitle(String reqTitle) {
        this.reqTitle = reqTitle;
    }

    public String getReqContent() {
        return reqContent;
    }

    public void setReqContent(String reqContent) {
        this.reqContent = reqContent;
    }

    public String getReqImg() {
        return reqImg;
    }

    public void setReqImg(String reqImg) {
        this.reqImg = reqImg;
    }
}
