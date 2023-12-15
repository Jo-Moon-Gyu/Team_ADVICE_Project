package com.example.adviser.activity_medicmain;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.databinding.ActivityMypageMedicBinding;
import com.example.adviser.guide.AppGuideActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MypageMedicActivity extends AppCompatActivity {

    ActivityMypageMedicBinding binding;
    private DrawerLayout drawerLayout;
    final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMypageMedicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.name.setText(user.getUserName() + "님");

        // 상단 우측 메뉴버튼
        drawerLayout = findViewById(R.id.drawerLayout);

        binding.sideBar.setOnClickListener(new View.OnClickListener() {
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
                                // 메인화면 이동
                                Intent intent = new Intent(MypageMedicActivity.this, MainMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.mypage) {
                                // 메딕 마이페이지 이동
                            } else if (item.getItemId() == R.id.exchange) {
                                // 메딕 환전요청 이동
                                Intent intent = new Intent(MypageMedicActivity.this, ExchangeMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.guide) {
                                // 이용 안내 이동
                                Intent intent = new Intent(MypageMedicActivity.this, AppGuideActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(MypageMedicActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                }
            }
        });

        binding.changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '정보 수정' 버튼 클릭 시 실행되는 코드

                // 변경 비밀번호 레이아웃과 변경 비밀번호 확인 레이아웃을 토글
                if (binding.change1.getVisibility() == View.VISIBLE) {
                    // 비밀번호 확인 로직은 이제 TextWatcher에서 처리되므로 이곳에서는 생략
                    binding.change1.setVisibility(View.GONE);
                    binding.change2.setVisibility(View.GONE);
                } else {
                    // 변경 비밀번호 레이아웃과 변경 비밀번호 확인 레이아웃을 보이도록 설정
                    binding.change1.setVisibility(View.VISIBLE);
                    binding.change2.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.userEmail.setText(user.getUserEmail());
        binding.userName.setText(user.getUserName());
        binding.userPw.setText(user.getUserPw());
        binding.userPhone.setText(user.getUserPhone());
        binding.userJoinMethod.setText(user.getApprovedAt().substring(0, 10));

        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userPw = binding.changePw.getText().toString();
                String userPwCheck = binding.changePwCheck.getText().toString();
                String userPhone = binding.userPhone.getText().toString();
                String userEmail = user.getUserEmail();

                // 변경된 비밀번호가 일치하는지 확인
                if (!userPw.equals(userPwCheck)) {
                    // 비밀번호가 일치하지 않을 경우 사용자에게 메시지 표시
                    binding.changePwCheck.setError("비밀번호가 일치하지않습니다.");
                    return;
                }
                // 비밀번호가 일치하면 pwCheck TextView를 보이도록 설정
                binding.pwCheck.setVisibility(View.VISIBLE);

                // 서버로 보낼 데이터를 JSON 형태로 생성
                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("userEmail", userEmail);
                    jsonRequest.put("userPw", userPw);
                    jsonRequest.put("userPhone", userPhone);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 서버 URL을 본인의 서버에 맞게 수정
                String url = ipUrl + "updateMedic";

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
                                            Toast.makeText(getApplicationContext(), "정보 수정 성공", Toast.LENGTH_SHORT).show();

                                            // TODO: 필요한 경우 다음 액티비티로 이동
                                            Intent intent = new Intent(MypageMedicActivity.this, MainMedicActivity.class);
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

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때의 동작 정의
        // 예시로 MainActivity로 이동하도록 설정
        super.onBackPressed();
        Intent intent = new Intent(MypageMedicActivity.this, MainMedicActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }


    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainMedicActivity.class);
        startActivity(intent);
        finish();
    }
}