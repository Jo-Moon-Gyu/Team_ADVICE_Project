package com.example.adviser.qna;

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
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.example.adviser.activity_medicmain.ExchangeMedicActivity;
import com.example.adviser.activity_medicmain.MainMedicActivity;
import com.example.adviser.activity_medicmain.MypageMedicActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.databinding.ActivityAnswerMedicBinding;
import com.example.adviser.guide.AppGuideActivity;
import com.example.adviser.vo.QuestionMedicVO;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AnswerMedicActivity extends AppCompatActivity {

    private ActivityAnswerMedicBinding binding;
    private DrawerLayout drawerLayout;
    private RequestQueue requestQueue;
    Bitmap decodedByte2;
    private byte[] decodedBytes2;
    private StringRequest request;
    private int req_idx = 0;
    private final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnswerMedicBinding.inflate(getLayoutInflater());
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
                                Intent intent = new Intent(AnswerMedicActivity.this, MainMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.mypage) {
                                Intent intent = new Intent(AnswerMedicActivity.this, MypageMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.exchange) {
                                Intent intent = new Intent(AnswerMedicActivity.this, ExchangeMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.guide) {
                                Intent intent = new Intent(AnswerMedicActivity.this, AppGuideActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.logout) {
                                SharedPreferences sharedPreferences = getSharedPreferences(UserLoginActivity.PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(AnswerMedicActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            return true;
                        }
                    });
                }
            }
        });

        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        // 세션에서 유저 정보를 가져온다
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.name.setText(user.getUserName() + "님");

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        if (intent != null) {
            QuestionMedicVO question = (QuestionMedicVO) intent.getSerializableExtra("question");
            byte[] byteArray = intent.getByteArrayExtra("image");

            if (byteArray != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // 이미지 처리 로직 추가
                binding.userNick.setText(question.getReq_user_nick());
                binding.userYear.setText(question.getUser_birthyear());
                binding.userGender.setText(question.getUser_gender());
                binding.questionTitle.setText(question.getReq_title());
                binding.quesitonCont.setText(question.getReq_content());
                binding.photo.setImageBitmap(bitmap);
                req_idx = question.getReq_idx();
                Log.d("시벌것", question.getDeep_result());
                if (question.getDeep_result().equals("null") || question.getDeep_result() == null) {
                    binding.Analysis.setText("");
                    binding.result.setText("");
                } else {
                    binding.result.setText(question.getDeep_result());
                }
            }
        }

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarningDialog(req_idx);
            }
        });

        binding.closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAnswer();
            }
        });
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
        closeAnswer();
    }







    // 다른 액티비티로 이동
    private void navigateToActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(AnswerMedicActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }


    // 답변 등록
    private void answerWrite(int req_idx) {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail().toString();
        String userName = user.getUserName().toString();
        String ansCont = binding.answerRegister.getText().toString();

        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "answer",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleAnswerWriteResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ansUserEmail", userEmail);
                params.put("ansUserName", userName);
                params.put("ansContent", ansCont);
                params.put("reqIdx", String.valueOf(req_idx));
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void showWarningDialog(int req_idx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("답변을 등록하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                answerWrite(req_idx);
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

    // 답변 작성 응답 처리
    private void handleAnswerWriteResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            String data = json.getString("result");
            Toast.makeText(this, "답변이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            navigateToActivity(MainMedicActivity.class);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeAnswer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("답변 작성을 취소하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                navigateToActivity(MainMedicActivity.class);
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

    private void goHome() {
        Intent intent = new Intent(this, MainUserActivity.class);
        startActivity(intent);
        finish();
    }
}
