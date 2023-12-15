package com.example.adviser.guide;

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
import android.widget.ImageButton;

import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_medicmain.ExchangeMedicActivity;
import com.example.adviser.activity_medicmain.MainMedicActivity;
import com.example.adviser.activity_medicmain.MypageMedicActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.activity_usermain.MypageUserActivity;
import com.example.adviser.databinding.ActivityAppGuideBinding;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class AppGuideActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActivityAppGuideBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        guideText();

        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.name.setText(user.getUserName() + "님");

        // 상단 우측 메뉴 버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigation Drawer를 열거나 닫는 메소드
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                    setupDrawerMenuListener();  // NavigationView의 아이템 클릭 이벤트 설정
                }
            }
        });
    }

    // NavigationView의 아이템 클릭 이벤트 설정
    private void setupDrawerMenuListener() {
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 아이템에 따라 다른 액티비티로 이동
                if (item.getItemId() == R.id.home) {
                    navigateTo(MainMedicActivity.class);
                } else if (item.getItemId() == R.id.mypage) {
                    navigateTo(MypageMedicActivity.class);
                } else if (item.getItemId() == R.id.exchange) {
                    navigateTo(ExchangeMedicActivity.class);
                } else if (item.getItemId() == R.id.logout) {
                    logoutTo(LoginActivity.class);
                }
                return true;
            }
        });
    }

    // 다른 액티비티로 이동하는 메소드
    private void navigateTo(Class<?> destinationActivity) {
        Intent intent = new Intent(AppGuideActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때의 동작 정의
        // 예시로 MainActivity로 이동하도록 설정
        super.onBackPressed();
        Intent intent = new Intent(AppGuideActivity.this, MainMedicActivity.class);
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

    private void logoutTo(Class<?> destinationActivity) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(AppGuideActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }

    private void guideText(){
        String text = "목적\n" +
                "@PROJECT BY ADVICE(이하 ADVISER)는 딥러닝 모델의\n" +
                "사용자의 피부 상처를 분석 판별하고, 의과대학생의 조언을 제공하는 서비스입니다.\n" +
                "\n" +
                "정의\n" +
                "본 서비스는 의하적 소견 및 처방이 아닌 단순 조언 서비스로 \n" +
                "필요시 병원 방문을 하여 진단을 받으시길 바랍니다.\n" +
                "\n" +
                "약관\n" +
                "ADVISER는 보건법의 의거 아래 내용을 준수합니다.\n" +
                "보건법\n" +
                "제 1장 총칙\n" +
                "제 2조\n" +
                "제2조(의료인)연혁판례문헌\n" +
                "① 이 법에서 \"의료인\"이란 보건복지부장관의 면허를 받은 의사ㆍ치과의사ㆍ한의사ㆍ조산사 및 간호사를 말한다. <개정 2008.2.29, 2010.1.18>\n" +
                "② 의료인은 종별에 따라 다음 각 호의 임무를 수행하여 국민보건 향상을 이루고 국민의 건강한 생활 확보에 이바지할 사명을 가진다. <개정 2015.12.29, 2019.4.23>\n" +
                "\n" +
                "1. 의사는 의료와 보건지도를 임무로 한다.\n" +
                "2. 치과의사는 치과 의료와 구강 보건지도를 임무로 한다.\n" +
                "3. 한의사는 한방 의료와 한방 보건지도를 임무로 한다.\n" +
                "4. 조산사는 조산(조산)과 임산부 및 신생아에 대한 보건과 양호지도를 임무로 한다.\n" +
                "5. 간호사는 다음 각 목의 업무를 임무로 한다.\n" +
                "\n" +
                "가. 환자의 간호요구에 대한 관찰, 자료수집, 간호판단 및 요양을 위한 간호\n" +
                "나. 의사, 치과의사, 한의사의 지도하에 시행하는 진료의 보조\n" +
                "다. 간호 요구자에 대한 교육ㆍ상담 및 건강증진을 위한 활동의 기획과 수행, 그 밖의 대통령령으로 정하는 보건활동\n" +
                "라. 제80조에 따른 간호조무사가 수행하는 가목부터 다목까지의 업무보조에 대한 지도\n" +
                "(출처: 의료법 일부개정 2021. 9. 24. [법률 제18468호, 시행 2023. 9. 25.] 보건복지부 > 종합법률정보 법령)\n" +
                "\n" +
                "제 3절 27절\n" +
                "제27조(무면허 의료행위 등 금지)연혁판례문헌\n" +
                "\n" +
                "① 의료인이 아니면 누구든지 의료행위를 할 수 없으며 의료인도 면허된 것 이외의 의료행위를 할 수 없다. 다만, 다음 각 호의 어느 하나에 해당하는 자는 보건복지부령으로 정하는 범위에서 의료행위를 할 수 있다. <개정 2008.2.29, 2009.1.30, 2010.1.18>\n" +
                "\n" +
                "1. 외국의 의료인 면허를 가진 자로서 일정 기간 국내에 체류하는 자\n" +
                "2. 의과대학, 치과대학, 한의과대학, 의학전문대학원, 치의학전문대학원, 한의학전문대학원, 종합병원 또는 외국 의료원조기관의 의료봉사 또는 연구 및 시범사업을 위하여 의료행위를 하는 자\n" +
                "3. 의학ㆍ치과의학ㆍ한방의학 또는 간호학을 전공하는 학교의 학생\n" +
                "\n" +
                "② 의료인이 아니면 의사ㆍ치과의사ㆍ한의사ㆍ조산사 또는 간호사 명칭이나 이와 비슷한 명칭을 사용하지 못한다.\n" +
                "③ 누구든지 「국민건강보험법」이나 「의료급여법」에 따른 본인부담금을 면제하거나 할인하는 행위, 금품 등을 제공하거나 불특정 다수인에게 교통편의를 제공하는 행위 등 영리를 목적으로 환자를 의료기관이나 의료인에게 소개ㆍ알선ㆍ유인하는 행위 및 이를 사주하는 행위를 하여서는 아니 된다. 다만, 다음 각 호의 어느 하나에 해당하는 행위는 할 수 있다. <개정 2009.1.30, 2010.1.18, 2011.12.31>\n" +
                "\n" +
                "1. 환자의 경제적 사정 등을 이유로 개별적으로 관할 시장ㆍ군수ㆍ구청장의 사전승인을 받아 환자를 유치하는 행위\n" +
                "2. 「국민건강보험법」 제109조에 따른 가입자나 피부양자가 아닌 외국인(보건복지부령으로 정하는 바에 따라 국내에 거주하는 외국인은 제외한다)환자를 유치하기 위한 행위\n" +
                "\n" +
                "④ 제3항제2호에도 불구하고 「보험업법」 제2조에 따른 보험회사, 상호회사, 보험설계사, 보험대리점 또는 보험중개사는 외국인환자를 유치하기 위한 행위를 하여서는 아니 된다. <신설 2009.1.30>\n" +
                "⑤ 누구든지 의료인이 아닌 자에게 의료행위를 하게 하거나 의료인에게 면허 사항 외의 의료행위를 하게 하여서는 아니 된다. <신설 2019.4.23, 2020.12.29>\n" +
                "(출처: 의료법 일부개정 2021. 9. 24. [법률 제18468호, 시행 2023. 9. 25.] 보건복지부 > 종합법률정보 법령)\n" +
                "\n" +
                "\n" +
                "회원에 대한 통지\n" +
                "1. ADVISER는. 의과대학생 가입 등록한 E-Mail 주소를 통해 가입 승인 여부를 전송한다.\n" +
                "\n" +
                "2. 회원의 E-Mail 주소를 통해 결제 정보를 전송한다.\n";


        binding.guiedText.setText(text);
    }
}
