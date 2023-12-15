package com.example.adviser.activity_login_join;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    private static KakaoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this,"0cd4e8952c64ec8738ad11695dcadac5");
    }
}