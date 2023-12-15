package com.example.adviser.vo;

import android.graphics.Bitmap;

public class QuestionVO {

    private Bitmap questionImg;
    private String dateText;
    private String questionText;

    public QuestionVO(Bitmap questionImg, String dateText, String questionText) {
        this.questionImg = questionImg;
        this.dateText = dateText;
        this.questionText = questionText;
    }

    public Bitmap getQuestionImg() {
        return questionImg;
    }

    public void setQuestionImg(Bitmap questionImg) {
        this.questionImg = questionImg;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
