package com.example.adviser.vo;

import android.graphics.Bitmap;

public class QnAVO {
    private transient Bitmap questionImg; // transient 키워드로 직렬화에서 제외
    private int req_idx;
    private String user_email;
    private String req_user_nick;
    private String user_birthyear;
    private String user_gender;
    private String deep_result;
    private String deep_img;
    private transient Bitmap deep_img_url;
    private String req_title;
    private String req_content;
    private String created_at;
    private int ans_idx;
    private String ans_user_email;
    private String ans_user_name;
    private String ans_content;
    private String ans_file;
    private String answered_at;

    public QnAVO(Bitmap questionImg, int req_idx, String user_email, String req_user_nick, String user_birthyear, String user_gender, String deep_result, String deep_img, Bitmap deep_img_url, String req_title, String req_content, String created_at, int ans_idx, String ans_user_email, String ans_user_name, String ans_content, String ans_file, String answered_at) {
        this.questionImg = questionImg;
        this.req_idx = req_idx;
        this.user_email = user_email;
        this.req_user_nick = req_user_nick;
        this.user_birthyear = user_birthyear;
        this.user_gender = user_gender;
        this.deep_result = deep_result;
        this.deep_img = deep_img;
        this.deep_img_url = deep_img_url;
        this.req_title = req_title;
        this.req_content = req_content;
        this.created_at = created_at;
        this.ans_idx = ans_idx;
        this.ans_user_email = ans_user_email;
        this.ans_user_name = ans_user_name;
        this.ans_content = ans_content;
        this.ans_file = ans_file;
        this.answered_at = answered_at;
    }

    public Bitmap getQuestionImg() {
        return questionImg;
    }

    public void setQuestionImg(Bitmap questionImg) {
        this.questionImg = questionImg;
    }

    public int getReq_idx() {
        return req_idx;
    }

    public void setReq_idx(int req_idx) {
        this.req_idx = req_idx;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getReq_user_nick() {
        return req_user_nick;
    }

    public void setReq_user_nick(String req_user_nick) {
        this.req_user_nick = req_user_nick;
    }

    public String getUser_birthyear() {
        return user_birthyear;
    }

    public void setUser_birthyear(String user_birthyear) {
        this.user_birthyear = user_birthyear;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getDeep_result() {
        return deep_result;
    }

    public void setDeep_result(String deep_result) {
        this.deep_result = deep_result;
    }

    public String getDeep_img() {
        return deep_img;
    }

    public void setDeep_img(String deep_img) {
        this.deep_img = deep_img;
    }

    public Bitmap getDeep_img_url() {
        return deep_img_url;
    }

    public void setDeep_img_url(Bitmap deep_img_url) {
        this.deep_img_url = deep_img_url;
    }

    public String getReq_title() {
        return req_title;
    }

    public void setReq_title(String req_title) {
        this.req_title = req_title;
    }

    public String getReq_content() {
        return req_content;
    }

    public void setReq_content(String req_content) {
        this.req_content = req_content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getAns_idx() {
        return ans_idx;
    }

    public void setAns_idx(int ans_idx) {
        this.ans_idx = ans_idx;
    }

    public String getAns_user_email() {
        return ans_user_email;
    }

    public void setAns_user_email(String ans_user_email) {
        this.ans_user_email = ans_user_email;
    }

    public String getAns_user_name() {
        return ans_user_name;
    }

    public void setAns_user_name(String ans_user_name) {
        this.ans_user_name = ans_user_name;
    }

    public String getAns_content() {
        return ans_content;
    }

    public void setAns_content(String ans_content) {
        this.ans_content = ans_content;
    }

    public String getAns_file() {
        return ans_file;
    }

    public void setAns_file(String ans_file) {
        this.ans_file = ans_file;
    }

    public String getAnswered_at() {
        return answered_at;
    }

    public void setAnswered_at(String answered_at) {
        this.answered_at = answered_at;
    }
}