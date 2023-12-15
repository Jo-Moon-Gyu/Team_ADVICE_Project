package com.example.adviser.activity_medicmain;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.databinding.ActivityExchangeMedicBinding;
import com.example.adviser.guide.AppGuideActivity;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class ExchangeMedicActivity extends AppCompatActivity {

    ActivityExchangeMedicBinding binding;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExchangeMedicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.name.setText(user.getUserName() + "님");

        binding.myMoney.setText(" : " + user.getUserProfit() + "원");

        binding.money.setHint("5000");

        binding.userName.setText(user.getUserName());

        binding.userBank.setHint("입금 은행 입력");

        binding.userBankNumber.setHint("계좌번호 입력");

        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        int minMoney = 5000;

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moneyString = binding.money.getText().toString().trim(); // trim을 사용하여 공백 제거
                if (!moneyString.isEmpty()) {
                    int usermoney_int = Integer.parseInt(moneyString);
                    if (usermoney_int > minMoney && usermoney_int <= Integer.parseInt(user.getUserProfit()) && usermoney_int != 0) {
                        String username = binding.userName.getText().toString();
                        String usermoney = moneyString; // 수정된 부분
                        String userbank = binding.userBank.getText().toString();
                        String userbanknumber = binding.userBankNumber.getText().toString();
                        clear(username, usermoney, userbank, userbanknumber);
                    } else {
                        Toast.makeText(ExchangeMedicActivity.this, "환전 요구 신청 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // moneyString이 비어있을 경우 처리
                    Toast.makeText(ExchangeMedicActivity.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                                Intent intent = new Intent(ExchangeMedicActivity.this, MainMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.mypage) {
                                // 메딕 마이페이지 이동
                                Intent intent = new Intent(ExchangeMedicActivity.this, MypageMedicActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.exchange) {
                                // 메딕 환전요청 이동
                            } else if (item.getItemId() == R.id.guide) {
                                // 이용 안내 이동
                                Intent intent = new Intent(ExchangeMedicActivity.this, AppGuideActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(ExchangeMedicActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }
                    });
                }
            }
        });
    }

    // 기능 함수 -------------------------------------------------

    // 뒤로가기 버튼이 눌렸을 때 실행될 코드
    @Override
    public void onBackPressed() {
        close();
    }

    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

    // 전송 알림
    private void clear(String username, String usermoney, String userbank, String userbanknumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("환전을 신정하시겠습니까?\n 이름 : " + username +
                "\n 환전금액 : " + usermoney +
                "\n 환전은행 : " + userbank +
                "\n 계좌번호 : " + userbanknumber);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ExchangeMedicActivity.this, MainMedicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void close() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("환전 신청을 취소하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(ExchangeMedicActivity.this, MainMedicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
