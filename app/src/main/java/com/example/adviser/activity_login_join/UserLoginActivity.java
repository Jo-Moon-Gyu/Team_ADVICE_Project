package com.example.adviser.activity_login_join;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.adviser.R;
import com.example.adviser.activity_intro.SetPermissionsActivity;
import com.example.adviser.activity_intro.StartingActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.databinding.ActivityMainUserBinding;
import com.google.gson.Gson;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class UserLoginActivity extends AppCompatActivity {

    public static final String PREF_NAME = "MyAppSession";
    private static final String KEY_USERNAME = "username";
    public static final String KEY_USER_INFO = "user_info";
    ActivityMainUserBinding userBinding;
    OAuthLogin mOAuthLoginModule;
    Context mContext;
    RequestQueue requestQueue;
    StringRequest request;
    private final String ipUrl = "http://13.209.4.93:8092/";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userBinding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_user_login);

        Button btnKakao = findViewById(R.id.btnKakao);
        Button btnNaver = findViewById(R.id.btnNaver);
        mContext = getApplicationContext();


        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 기기에 카카오톡이 설치되어 있는 확인
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(UserLoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(UserLoginActivity.this, callback);
                } else {
                    // 카카오톡이 설치되어 있지 않다면
                    UserApiClient.getInstance().loginWithKakaoAccount(UserLoginActivity.this, callback);
                } // 클릭 시 goMain 메소드 호출

            }
        });

        btnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule = OAuthLogin.getInstance();
                mOAuthLoginModule.init(
                        mContext,
                        getString(R.string.naver_client_id),
                        getString(R.string.naver_client_secret),
                        getString(R.string.naver_client_name)
                );

                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                            new GetNaverUserInfoTask().execute(accessToken);
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                            String tokenType = mOAuthLoginModule.getTokenType(mContext);

                            Log.i("LoginData","accessToken : "+ accessToken);
                            Log.i("LoginData","refreshToken : "+ refreshToken);
                            Log.i("LoginData","expiresAt : "+ expiresAt);
                            Log.i("LoginData","tokenType : "+ tokenType);

                            // 여기에 액세스 토큰 저장
                            saveAccessToken(accessToken);

                            // 여기에 사용자에게 표시할 메시지 등 추가
                            Toast.makeText(mContext, "네이버 로그인 성공!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UserLoginActivity.this, MainUserActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Toast.makeText(mContext, "네이버 로그인 실패: errorCode=" + errorCode
                                    + ", errorDesc=" + errorDesc, Toast.LENGTH_SHORT).show();
                        }
                    }

                };
                mOAuthLoginModule.startOauthLoginActivity(UserLoginActivity.this, mOAuthLoginHandler);

            }
        });


    }

    private void updateKakaoLoginUi() {

        // 로그인 여부에 따른 UI 설정
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                if (user != null) {

                    // 유저의 닉네임
                    Log.d(TAG, "invoke: nickname =" + user.getKakaoAccount().getProfile().getNickname());
                    // 유저의 아이디
                    Log.d(TAG, "invoke: id =" + user.getId());
                    // 유저의 이메일
                    Log.d(TAG, "invoke: email =" + user.getKakaoAccount().getEmail());
                    // 유저의 성별
                    Log.d(TAG, "invoke: gender =" + user.getKakaoAccount().getGender());
                    // 유저의 출생년도
                    Log.d(TAG, "invoke: age=" + user.getKakaoAccount().getBirthyear());
                    // 유저의 전화번호
                    Log.d(TAG, "invoke: age=" + user.getKakaoAccount().getPhoneNumber());

                    Log.d(TAG, "invoke: profile = " + user.getKakaoAccount().getProfile().getThumbnailImageUrl());

                    joinCheck(user);

                } else {
                    // 로그인 되어있지 않으면
                }
                return null;
            }
        });
    }


    // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
    Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            Log.e(TAG, "CallBack Method");

            if (oAuthToken != null) {
                // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                updateKakaoLoginUi();
            } else {
                // 로그인 실패
                Log.e(TAG, "invoke: login fail");

                // 실패 이유를 로그로 출력
                if (throwable != null) {
                    Log.e(TAG, "Login failed due to: " + throwable.getMessage());
                }
            }

            return null;
        }
    };


    // 회원가입 확인 함수
    private void joinCheck(User user) {
        Log.d("email", user.toString());
        String userEmail = user.getKakaoAccount().getEmail().toString();
        Log.d("email", userEmail);
        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "joinCheck",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        // response 결과값을 JSON 타입으로 파싱을 하면
                        // 원하는 키 값에 대한 value 값을 꺼내 올 수 있다
                        try {
                            JSONObject json = new JSONObject(response);
                            String data = json.getString("result");

                            if (data.equals("0")) {
                                join(user);
                            } else {
                                loginSuccess(user);

                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }


        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("email ch", userEmail);
                params.put("userEmail", userEmail);
                return params;
            }
        };


        requestQueue.add(request);

    }

    // 회원가입 함수
    private void join(User user) {

        Date currentDate = new Date();
        String userEmail = user.getKakaoAccount().getEmail().toString();
        String userName = user.getKakaoAccount().getName().toString();
        String userNick = user.getKakaoAccount().getProfile().getNickname().toString();
        String userGender = user.getKakaoAccount().getGender().toString();
        String userBirthyear = user.getKakaoAccount().getBirthyear().toString();
        String userPhone = user.getKakaoAccount().getPhoneNumber().toString();
        String userloginType = "카카오 로그인";

        if (userGender.equals("MALE")) {
            userGender = "M";
        } else {
            userGender = "F";
        }
        String finalUserGender = userGender;

        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "join",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        // response 결과값을 JSON 타입으로 파싱을 하면
                        // 원하는 키 값에 대한 value 값을 꺼내 올 수 있다
                        try {
                            JSONObject json = new JSONObject(response);
                            String data = json.getString("result");
                            loginSuccess(user);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }


        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();

                params.put("userEmail", userEmail);
                params.put("userName", userName);
                params.put("userNick", userNick);
                params.put("userPhone", userPhone);
                params.put("userBirthyear", userBirthyear);
                params.put("userGender", finalUserGender);
                params.put("userLoginType", userloginType);
                params.put("userRole", "사용자");

                return params;
            }
        };


        requestQueue.add(request);

    }

    // 로그인 성공 함수
    private void loginSuccess(User user) {

        // 로그인 성공 시 세션에 사용자 정보 저장
        String userEmail = user.getKakaoAccount().getEmail().toString();
        userinfo(userEmail);
        Toast.makeText(UserLoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(UserLoginActivity.this, MainUserActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);


    }

    private void userinfo(String userEmail) {
        Log.d("email", userEmail);
        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "userKaNaInfo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);

                            // "result" 키에 해당하는 값을 가져옴
                            if (json.has("result") && !json.isNull("result")) {
                                JSONObject data = json.getJSONObject("result");
                                Log.d("email = = = = =", data.toString());
                                // 사용자 정보가 있는 경우 세션에 저장
                                saveUserInfoToSession(data);
                            } else {
                                // "result" 키가 없거나 값이 null인 경우
                                Log.d("email", "No user information found");
                            }

                        } catch (JSONException e) {
                            // JSON 파싱 예외 처리
                            Log.e("email", "JSON parsing error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 네트워크 오류 등에 대한 예외 처리
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userEmail", userEmail);
                return params;
            }
        };

        requestQueue.add(request);
    }

    // 사용자 정보를 세션에 저장하는 메서드
    private void saveUserInfoToSession(JSONObject userInfo) {
        Gson gson = new Gson();
        String userInfoJson = userInfo.toString();

        Log.d("email", "여기까지 왔니?");

        // 세션에 저장할 때도 동일한 모드를 사용
        getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(UserLoginActivity.KEY_USER_INFO, userInfoJson)
                .apply();
    }

    // 세션에서 사용자 정보를 가져오는 메서드
    private User getUserInfoFromSession() {
        Gson gson = new Gson();

        // 세션에서 읽을 때도 동일한 모드를 사용
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE)
                .getString(UserLoginActivity.KEY_USER_INFO, "");

        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);

        return gson.fromJson(userInfoJson, User.class);
    }


    //naverlogin 코드
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            Log.d("NewIntentData", "URI: " + uri.toString());
            if ("https".equals(uri.getScheme()) && "adviser.com".equals(uri.getHost()) && "/callback".equals(uri.getPath())) {
                // 여기에 콜백 URI 처리 로직을 추가하세요.
                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                            String tokenType = mOAuthLoginModule.getTokenType(mContext);

                            Log.i("LoginData","accessToken : "+ accessToken);
                            Log.i("LoginData","refreshToken : "+ refreshToken);
                            Log.i("LoginData","expiresAt : "+ expiresAt);
                            Log.i("LoginData","tokenType : "+ tokenType);
                            // 여기에 액세스 토큰 저장
                            saveAccessToken(accessToken);

                            // 여기에 사용자에게 표시할 메시지 등 추가
                            Toast.makeText(mContext, "네이버 로그인 성공!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UserLoginActivity.this, MainUserActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Toast.makeText(mContext, "네이버 로그인 실패: errorCode=" + errorCode
                                    + ", errorDesc=" + errorDesc, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                // OAuthLoginHandler를 사용하여 로그인 처리
                mOAuthLoginModule.startOauthLoginActivity(UserLoginActivity.this, mOAuthLoginHandler);
            }
        }
    }


    private class GetNaverUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String accessToken = params[0];
            String url = "https://openapi.naver.com/v1/nid/me";
            return OAuthLogin.getInstance().requestApi(UserLoginActivity.this, accessToken, url);
        }

        @Override
        protected void onPostExecute(String response) {
            parseNaverUserInfo(response);
        }
    }

    private void parseNaverUserInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseObj = jsonObject.getJSONObject("response");

            // 사용자 정보 추출
            String nickname = responseObj.getString("nickname");
            String mobile = responseObj.getString("mobile");
            String email = responseObj.getString("email");

            Log.d("naverUser", "nickname : " + nickname);
            Log.d("naverUser", "mobile : " + mobile);
            Log.d("naverUser", "email : " + email);

            // TODO: 여기서 얻은 정보를 DB에 저장하거나 필요한 처리를 수행

            // 여기에 액세스 토큰 저장
            saveAccessToken(getAccessToken());

            // 여기에 사용자에게 표시할 메시지 등 추가
            Toast.makeText(mContext, "네이버 로그인 성공!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainUserActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SharedPreferences를 사용하여 액세스 토큰 저장
    private void saveAccessToken(String accessToken) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("access_token", accessToken);
        editor.apply();
    }

    // SharedPreferences에서 액세스 토큰 불러오기
    private String getAccessToken() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getString("access_token", null);
    }

}