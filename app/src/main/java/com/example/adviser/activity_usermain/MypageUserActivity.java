package com.example.adviser.activity_usermain;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_medicmain.MainMedicActivity;
import com.example.adviser.databinding.ActivityMypageUserBinding;
import com.example.adviser.guide.AppGuideUserActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MypageUserActivity extends AppCompatActivity {


    public static final String PREF_NAME = "MyAppSession";
    private static final String KEY_USERNAME = "username";
    public static final String KEY_USER_INFO = "user_info";
    ActivityMypageUserBinding binding;
    RequestQueue requestQueue;
    StringRequest request;
    private DrawerLayout drawerLayout;
    final String ipUrl = "http://13.209.4.93:8092/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMypageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 상단 우측 메뉴버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                    binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if (item.getItemId() == R.id.home) {
                                Intent intent = new Intent(MypageUserActivity.this, MainUserActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.map) {
                                Intent intent = new Intent(MypageUserActivity.this, MapUserActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.guides) {
                                Intent intent = new Intent(MypageUserActivity.this, AppGuideUserActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(MypageUserActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;

                        }
                    });
                }
            }
        });


        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        binding.userEmail.setText(user.getUserEmail());
        binding.userName.setText(user.getUserName());
        binding.userPhone.setHint(user.getUserPhone());
        binding.userJoinMethod.setText(user.getUserloginType());
        binding.nick.setText(user.getUserNick());
        binding.userNick.setHint(user.getUserNick());


        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isButtonClickAllowed = true;

            @Override
            public void onClick(View v) {
                if (!isButtonClickAllowed) {
                    // 버튼 클릭이 허용되지 않은 경우 리턴
                    return;
                }

                // 버튼 클릭을 허용하지 않음
                isButtonClickAllowed = false;

                // 0.5초 딜레이 후 버튼 클릭을 다시 허용
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isButtonClickAllowed = true;
                    }
                }, 1000);
                String userNick = binding.userNick.getText().toString();
                String userPhone = binding.userPhone.getText().toString();
                String userEmail = user.getUserEmail();
                // 서버로 보낼 데이터를 JSON 형태로 생성
                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("userEmail", userEmail);  // userEmail을 서버로 보내야 합니다.
                    jsonRequest.put("userNick", userNick);
                    jsonRequest.put("userPhone", userPhone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 서버 URL을 본인의 서버에 맞게 수정
                String url = ipUrl + "updateUser";

                // JsonObjectRequest 생성
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonRequest,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("result")) {
                                        String result = response.getString("result");

                                        if ("success".equals(result)) {
                                            // 성공 시 세션 업데이트
                                            updateSession(userEmail, userNick, userPhone);
                                            Toast.makeText(getApplicationContext(), "정보 수정 성공", Toast.LENGTH_SHORT).show();

                                            // TODO: 필요한 경우 다음 액티비티로 이동
                                            Intent intent = new Intent(MypageUserActivity.this, MainUserActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "정보 수정 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // 에러 처리
                            }
                        }
                );

                // RequestQueue에 요청 추가
                requestQueue.add(jsonObjectRequest);
            }
        });

    }


    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainUserActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateSession(String userEmail, String userNick, String userPhone) {
        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        // 유저 정보 업데이트
        user.setUserNick(userNick);
        user.setUserPhone(userPhone);

        // 업데이트된 유저 정보를 세션에 저장
        saveUserInfoToSession(user.createJSONObject(user));
    }

    // 사용자 정보를 세션에 저장하는 메서드
    private void saveUserInfoToSession(JSONObject userInfo) {
        Gson gson = new Gson();
        String userInfoJson = userInfo.toString();

        Log.d("email", "여기까지 왔니?");

        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_INFO, userInfoJson)
                .apply();
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때의 동작 정의
        // 예시로 MainActivity로 이동하도록 설정
        super.onBackPressed();
        Intent intent = new Intent(MypageUserActivity.this, MainUserActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }


}