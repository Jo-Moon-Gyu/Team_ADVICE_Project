package com.example.adviser.activity_usermain;

import static com.example.adviser.activity_login_join.MedicalLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.example.adviser.databinding.ActivityMainUserBinding;
import com.example.adviser.guide.AppGuideUserActivity;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.kakaomap.MapUserPhActivity;
import com.example.adviser.qna.AnswerUserActivity;
import com.example.adviser.qna.QuesitonUserActivity;
import com.example.adviser.vo.QuestionMedicVO;
import com.example.adviser.vo.QuestionVO;
import com.example.adviser.adapter.*;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MainUserActivity extends AppCompatActivity implements QuestionMedicWaitAdapter.OnQuestionClickListener {

    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    private boolean doubleBackToExitPressedOnce = false;
    ActivityMainUserBinding binding;
    private ArrayList<QuestionVO> dataset;
    Bitmap decodedByte;
    Bitmap decodedByte2;
    EditText edit;
    byte[] decodedBytes2;
    StringRequest request;
    private ArrayList<QuestionVO> dataset2;
    private ArrayList<QuestionMedicVO> dataset3;
    private ArrayList<QuestionMedicVO> dataset4;
    RequestQueue requestQueue;
    private DrawerLayout drawerLayout;
    QuestionWaitAdapter adapter = new QuestionWaitAdapter(this, R.layout.answer_wait_view, dataset);
    QuestionMedicWaitAdapter adapter2 = new QuestionMedicWaitAdapter(this, R.layout.answer_wait_view, dataset3, this);
    private final String ipUrl = "http://13.209.4.93:8092/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
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
                                return true;
                            } else if (item.getItemId() == R.id.map) {
                                Intent intent = new Intent(MainUserActivity.this, MapUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.mypage) {
                                Intent intent = new Intent(MainUserActivity.this, MypageUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.guides) {
                                // 이용 안내 이동
                                Intent intent = new Intent(MainUserActivity.this, AppGuideUserActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.logout) {
                                // 로그아웃
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(MainUserActivity.this, LoginActivity.class);
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

        // 유저 정보를 레이아웃에 표시한다.
        binding.nick.setText(user.getUserNick() + "님");

        // 질문 버튼 클릭
        binding.questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goQuestion();
            }
        });

        // RecyclerView에 표시될 데이터 리스트
        dataset = new ArrayList<>();
        // 필터링 결과 저장 리스트
        dataset2 = new ArrayList<>();

        // Volley RequestQueue 초기화
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        fetchQuestionList(user.getUserEmail());


        // 맵 이동
        binding.hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapUserActivity.class);


                startActivity(intent);

                finish();
            }
        });

        binding.medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapUserPhActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // API 호출 함수 호출
        new GetMedicQnaContentTask().execute();



    }
// ------------

    // AsyncTask를 사용하여 백그라운드에서 API 호출
    private class GetMedicQnaContentTask extends AsyncTask<Void, Void, ArrayList<QuestionMedicVO>> {

        @Override
        protected ArrayList<QuestionMedicVO> doInBackground(Void... voids) {

            ArrayList<QuestionMedicVO> dataset = new ArrayList<>();
            try {
                // API 호출
                String response = getMedicQnaContent();
                JSONObject json = new JSONObject(response);

                JSONArray qnaArray = json.getJSONArray("qnaContent");
                Log.d("qnaArray", qnaArray.toString());

                if (qnaArray.length() > 0) {
                    for (int i = 0; i < qnaArray.length(); i++) {
                        JSONObject qna = (JSONObject) qnaArray.get(i);
                        String dateText = qna.getString("created_at");
                        String req_content = qna.getString("req_content");
                        String req_url = qna.getString("req_img_url");
                        String user_gender = qna.getString("user_gender");
                        String user_birthyear = qna.getString("user_birthyear");
                        String req_user_nick = qna.getString("req_user_nick");
                        String ans_user_name = qna.getString("ans_user_name");
                        int req_idx = qna.getInt("req_idx");
                        String user_email = qna.getString("user_email");
                        String req_title = qna.getString("req_title");
                        int ans_idx = qna.getInt("ans_idx");
                        String ans_user_email = qna.getString("ans_user_email");
                        String ans_content = qna.getString("ans_content");
                        String ans_file = qna.getString("ans_file");
                        String answered_at = qna.getString("answered_at");
                        String deep_img_url = qna.getString("deep_img_url");
                        String deep_result = qna.getString("deep_result");

                        if (deep_img_url != null && req_url != null) {
                            byte[] decodedBytes = Base64.getDecoder().decode(req_url);
                            decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                            decodedBytes2 = Base64.getDecoder().decode(deep_img_url);
                            decodedByte2 = BitmapFactory.decodeByteArray(decodedBytes2, 0, decodedBytes2.length);

                            dataset.add(new QuestionMedicVO(decodedByte, req_idx, user_email, req_user_nick, user_birthyear, user_gender, deep_result, decodedByte2, req_title, req_content, dateText, ans_idx, ans_user_email, ans_user_name, ans_content, ans_file, answered_at));

                        } else {
                            decodedByte = null;
                            decodedByte2 = null;
                            dataset.add(new QuestionMedicVO(decodedByte, req_idx, user_email, req_user_nick, user_birthyear, user_gender, deep_result, decodedByte2, req_title, req_content, dateText, ans_idx, ans_user_email, ans_user_name, ans_content, ans_file, answered_at));
                        }

                    }

                } else {
                    binding.messageTextView2.setText("상담 완료 목록이 없습니다.");
                    // 토큰이 없는 경우 어댑터를 null로 설정하거나 다른 작업을 수행하고 싶다면 이 부분에 추가 코드를 작성할 수 있습니다.
                    binding.answerArr.setBackgroundResource(R.color.white);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return dataset;
        }

        @Override
        protected void onPostExecute(ArrayList<QuestionMedicVO> dataset) {
            // 세션 갱신
            updateSessionWithNewData();

            // UI 업데이트
            updateUIWithQuestions(dataset);

            // 알림 생성 코드 추가
            for (QuestionMedicVO question : dataset) {
                // 사용자가 답변을 받았을 때만 알림 표시
                if (question.getAns_idx() > 0) {
                    showNoti(question.getReq_title(), question.getAns_content(), question.getAns_idx());
                }
            }
        }
    }

    // API 호출 함수
    private String getMedicQnaContent() throws InterruptedException {
        User user = getUserInfoFromSession();
        String url = ipUrl + "userQnAnswerContent?user_email=" + user.getUserEmail();
        final String[] response = {""};
        final CountDownLatch latch = new CountDownLatch(1);

        // API 호출
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        response[0] = res;
                        latch.countDown();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error occurred", error);
                        latch.countDown();
                    }
                }
        );

        // Volley로 API 호출
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue.add(request);

        // 응답이 올 때까지 대기
        latch.await();

        return response[0];
    }


    // UI 업데이트 함수
    private void updateUIWithQuestions(ArrayList<QuestionMedicVO> dataset) {
        QuestionMedicWaitAdapter adapter = new QuestionMedicWaitAdapter(this, R.layout.answer_wait_view, dataset, this);
        adapter.notifyDataSetChanged();
        binding.answerList.setLayoutManager(new LinearLayoutManager(MainUserActivity.this));
        binding.answerList.setAdapter(adapter);
    }

    // RecyclerView 아이템 클릭 이벤트 처리
    @Override
    public void onQuestionClick(int position) {

        // 선택된 아이템의 데이터 가져오기
        QuestionMedicVO selectedQuestion = ((QuestionMedicWaitAdapter) binding.answerList.getAdapter()).getItem(position);

        // 이미지를 Bitmap 형태로 변환하여 Intent에 추가
        Intent detailIntent = new Intent(MainUserActivity.this, AnswerUserActivity.class);
        detailIntent.putExtra("question", selectedQuestion);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap questionImg = selectedQuestion.getQuestionImg();
        if (questionImg != null) {
            questionImg.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        } else {
            questionImg = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            questionImg.eraseColor(android.graphics.Color.TRANSPARENT);
        }
        byte[] byteArray = stream.toByteArray();
        detailIntent.putExtra("image", byteArray);

        Drawable currentDrawable = binding.alarm.getDrawable();

// 현재의 Drawable이 R.drawable.baseline_notifications_34의 ConstantState와 같다면 R.drawable.alarm으로 변경하고, 아니면 R.drawable.baseline_notifications_34으로 변경합니다.
        if((currentDrawable.getConstantState().equals(getResources().getDrawable(R.drawable.alarm).getConstantState())))
        {
            Drawable newDrawable = getResources().getDrawable(R.drawable.baseline_notifications_34);
            binding.alarm.setImageDrawable(newDrawable);
        }


// 이미지를 변경합니다.



        startActivity(detailIntent);

    }

    // 세션 업데이트 메서드
    private void updateSessionWithNewData() {
        User user = getUserInfoFromSession();
        String email = user.getUserEmail();
        // REST API 호출
        request = new StringRequest(
                Request.Method.POST,
                ipUrl + "userKaNaInfo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleApiResponseForUpdateSession(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userEmail", email);
                return params;
            }
        };

        // 요청을 요청 큐에 추가
        requestQueue.add(request);
    }

    // API 응답 처리 메서드
    private void handleApiResponseForUpdateSession(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("result")) {
                String result = jsonResponse.getString("result");

                if (!"loginFail".equals(result) && !"NotApprove".equals(result)) {
                    User newUser = new Gson().fromJson(result, User.class);
                    saveSession(newUser);

                    // 업데이트된 세션 정보로 UI 갱신
                    binding.nick.setText(newUser.getUserNick() + "님");
                }
            } else {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 사용자 정보를 저장하는 메서드
    private void saveSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserLoginActivity.KEY_USER_INFO, new Gson().toJson(user));
        editor.apply();
    }


    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

    // 질문 목록 가져오기
    private void fetchQuestionList(String userEmail) {
        adapter = new QuestionWaitAdapter(this, R.layout.answer_wait_view, dataset2);
        adapter2 = new QuestionMedicWaitAdapter(this, R.layout.answer_wait_view, dataset3, this);


        EditText edit = binding.questionTitle;


        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // 텍스트 변경 후
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                search(text, adapter);
                adapter.notifyDataSetChanged();
            }
        });


        StringRequest request = new StringRequest(
                Request.Method.GET,
                ipUrl + "qnaContent?user_email=" + userEmail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray qnaArray = json.getJSONArray("qnaContent");

                            if (qnaArray.length() > 0) {
                                for (int i = 0; i < qnaArray.length(); i++) {
                                    JSONObject qna = (JSONObject) qnaArray.get(i);
                                    String dateText = qna.getString("created_at");
                                    String req_content = qna.getString("req_title");
                                    String req_url = qna.getString("req_img_url");


                                    if (req_url != null) {
                                        byte[] decodedBytes = Base64.getDecoder().decode(req_url);
                                        decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                        dataset.add(new QuestionVO(decodedByte, dateText, req_content));
                                    } else {
                                        decodedByte = null;

                                        dataset.add(new QuestionVO(decodedByte, dateText, req_content));
                                    }

                                    adapter.notifyDataSetChanged();
                                    binding.questionList.setLayoutManager(new LinearLayoutManager(MainUserActivity.this));
                                    binding.questionList.setAdapter(MainUserActivity.this.adapter);
                                }
                                search(edit.getText().toString(), adapter);
                            } else {
                                binding.messageTextView.setText("답변 대기 목록이 없습니다.");
                                // 토큰이 없는 경우 어댑터를 null로 설정하거나 다른 작업을 수행하고 싶다면 이 부분에 추가 코드를 작성할 수 있습니다.
                                MainUserActivity.this.adapter = null;
                                binding.questionArr.setBackgroundResource(R.color.white);
                                binding.questionList.setLayoutManager(new LinearLayoutManager(MainUserActivity.this));
                                binding.questionList.setAdapter(MainUserActivity.this.adapter);
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error occurred", error);
                    }
                }
        );

        // Request를 RequestQueue에 추가
        requestQueue.add(request);


    }


    // 질문하기 이동 함수
    private void goQuestion() {
        Intent intent = new Intent(MainUserActivity.this, QuesitonUserActivity.class);
        startActivity(intent);
        finish();
    }

    private void search(String text, QuestionWaitAdapter adapter) {
        // 일단 비우고 시작
        dataset2.clear();
        // 검색어 없으면 전체 다 담아서 출력
        if (text.isEmpty()) {
            dataset2.addAll(dataset);
        } else {
            for (QuestionVO question : dataset) {
                if (question.getQuestionText().toLowerCase().contains(text.toLowerCase())) {
                    dataset2.add(question);
                }
            }
        }
        // 필터링된 리스트가 변경된 후에 화면을 갱신
        adapter.notifyDataSetChanged();
    }

    // 뒤로가기 두번 누를때
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // 2초 동안 두 번 누를 수 있도록 설정
    }

    // 알람 코드
    public void showNoti(String req_title, String ans_content, int ans_idx) {
        // 사용자 별로 알람 상태를 관리하기 위해 사용자 이메일과 req_idx를 이용하여 알람 ID를 동적으로 생성
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        int notificationId = (userEmail + "_" + ans_idx).hashCode(); // 이메일과 ans_idx를 기반으로 한 해시코드를 알람 ID로 사용

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean notificationShown = sharedPreferences.getBoolean("notificationShown_" + notificationId, false);

        if (!notificationShown) {
            binding.alarm.setImageResource(R.drawable.alarm);
            builder = null;
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // 버전 오레오 이상일 경우
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(
                        new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                );

                builder = new NotificationCompat.Builder(this, CHANNEL_ID);

                // 하위 버전일 경우
            } else {
                builder = new NotificationCompat.Builder(this);
            }

            // 알림창 제목
            builder.setContentTitle("답변 완료");
            Log.d("에러", "알림" + req_title);
            // 알림창 메시지
            builder.setContentText(req_title);

            // 알림창 아이콘
            builder.setSmallIcon(R.drawable.logo);

            Notification notification = builder.build();

            // 알림창 실행
            manager.notify(notificationId, notification);

            // Mark the notification as shown in the shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationShown_" + notificationId, true);
            editor.apply();
        }
    }


}