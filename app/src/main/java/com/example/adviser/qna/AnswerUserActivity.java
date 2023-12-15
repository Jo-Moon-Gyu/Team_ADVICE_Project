package com.example.adviser.qna;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.databinding.ActivityAnswerUserBinding;
import com.example.adviser.guide.AppGuideUserActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.activity_usermain.MypageUserActivity;
import com.example.adviser.vo.QuestionMedicVO;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AnswerUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    ActivityAnswerUserBinding binding;
    private final String ipUrl = "http://13.209.4.93:8092/";
    private RequestQueue requestQueue;
    private StringRequest request;
    private int ans_idx;
    private String ans_user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnswerUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user = getUserInfoFromSession();

        binding.nick.setText(user.getUserNick());

        requestQueue = Volley.newRequestQueue(this);

        // 상단 우측 메뉴버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            QuestionMedicVO question = (QuestionMedicVO) intent.getSerializableExtra("question");
            byte[] byteArray = intent.getByteArrayExtra("image");

            if (byteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // 이미지 처리 로직 추가
                binding.answerName.setText(question.getAns_user_name()+"님 답변");
                binding.questionTitle.setText(question.getReq_title());
                binding.answerCont.setText(question.getAns_content());
                binding.deepPhoto.setImageBitmap(bitmap);
                ans_idx = question.getAns_idx();
                ans_user_email = question.getAns_user_email();
                ratingSel(ans_idx);
            }
        }

        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerUserActivity.this, MainUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingPopup();
            }
        });

    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            drawerLayout.openDrawer(GravityCompat.END);
            setupNavigationClickListener();
        }
    }

    private void setupNavigationClickListener() {
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemClick(item);
                return true;
            }
        });
    }

    private void handleNavigationItemClick(MenuItem item) {
        int itemId = item.getItemId();
        Class<?> destinationActivity = null;

        if (itemId == R.id.home) {
            destinationActivity = MainUserActivity.class;
        } else if (itemId == R.id.map) {
            destinationActivity = MapUserActivity.class;
        } else if (itemId == R.id.mypage) {
            destinationActivity = MypageUserActivity.class;
        } else if (itemId == R.id.guides) {
            destinationActivity = AppGuideUserActivity.class;
        } else if (item.getItemId() == R.id.logout) {
            // 로그아웃
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(AnswerUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (destinationActivity != null) {
            navigateToActivity(destinationActivity);
        }
    }

    private void navigateToActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(AnswerUserActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }

    // 유저 이름 세션값 가져오기
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE)
                .getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("AnswerMedicActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

    public void onBackPressed() {
        finish();
    }

    // 평점 입력 팝업
    private void showRatingPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_rating, null);
        dialogBuilder.setView(view);

        RatingBar ratingBarPopup = view.findViewById(R.id.ratingBarPopup);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        AlertDialog alertDialog = dialogBuilder.create();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBarPopup.getRating();
                // 여기에서 평점을 활용하여 원하는 작업 수행
                    Log.d("rating", String.valueOf(rating));
                showWarningDialog(rating, ans_idx, ans_user_email);


                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showWarningDialog(float rating, int ans_idx, String ans_user_email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("평점을 등록하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                insertRating(rating, ans_idx, ans_user_email);
                Class<?> destinationActivity = null;
                destinationActivity = MainUserActivity.class;
                navigateToActivity(destinationActivity);
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

    private void insertRating(float rating, int ans_idx, String ans_user_email) {

        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "rating",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                params.put("ansIdx", String.valueOf(ans_idx));
                params.put("userEmail", ans_user_email);
                params.put("rating", String.valueOf(rating));
                return params;
            }
        };

        requestQueue.add(request);
    }
    private void ratingSel(int ans_idx) {
        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "ratingSel",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleRatingResponse2(response);
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
                params.put("ansIdx", String.valueOf(ans_idx));
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void handleRatingResponse2(String response) {
        // 서버 응답에 따른 처리
        if ("0".equals(response)) {
            // 결과 값이 1이면 saveBtn 활성화하고 텍스트 설정
            binding.saveBtn.setEnabled(true);
            binding.saveBtn.setText("사용자 평가"); // 적절한 텍스트로 변경
        } else {
            // 결과 값이 0이면 saveBtn 비활성화하고 텍스트 설정
            binding.saveBtn.setEnabled(false);
            binding.saveBtn.setBackgroundResource(R.drawable.clear_background);
            binding.saveBtn.setText("평가 완료"); // 적절한 텍스트로 변경
        }
    }

}
