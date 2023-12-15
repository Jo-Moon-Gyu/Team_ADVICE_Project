package com.example.adviser.vo;

import android.graphics.Bitmap;

public class TokenVO {
    private String tokenName;
    private int img;

    public TokenVO(String tokenName, int img) {
        this.tokenName = tokenName;
        this.img = img;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
