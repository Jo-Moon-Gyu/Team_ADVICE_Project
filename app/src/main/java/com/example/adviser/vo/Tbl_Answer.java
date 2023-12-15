package com.example.adviser.vo;

public class Tbl_Answer {

    private String ansUserEmail;
    private String ansContent;
    private int req_idx;

    public Tbl_Answer() {
        this.ansUserEmail = ansUserEmail;
        this.ansContent = ansContent;
        this.req_idx = req_idx;
    }

    public String getAnsUserEmail() {
        return ansUserEmail;
    }

    public void setAnsUserEmail(String ansUserEmail) {
        this.ansUserEmail = ansUserEmail;
    }

    public String getAnsContent() {
        return ansContent;
    }

    public void setAnsContent(String ansContent) {
        this.ansContent = ansContent;
    }

    public int getReq_idx() {
        return req_idx;
    }

    public void setReq_idx(int req_idx) {
        this.req_idx = req_idx;
    }
}
