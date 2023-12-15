package com.example.adviser.alarm;

import static com.example.adviser.activity_login_join.UserLoginActivity.PREF_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

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
import com.example.adviser.adapter.QuestionMedicWaitAdapter;
import com.example.adviser.databinding.ActivityAlarmBinding;
import com.example.adviser.guide.AppGuideActivity;
import com.example.adviser.qna.AnswerMedicActivity;
import com.example.adviser.vo.QuestionMedicVO;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class AlarmActivity extends AppCompatActivity implements QuestionMedicWaitAdapter.OnQuestionClickListener {

    Bitmap decodedByte;
    Bitmap decodedByte2;
    byte[] decodedBytes2;
    ActivityAlarmBinding binding;
    private DrawerLayout drawerLayout;
    RequestQueue requestQueue;
    StringRequest request;
    final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user = getUserInfoFromSession();

        binding.name.setText(user.getUserName()+"님");
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
                                Intent intent = new Intent(AlarmActivity.this, MainMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.mypage) {
                                Intent intent = new Intent(AlarmActivity.this, MypageMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.exchange) {
                                Intent intent = new Intent(AlarmActivity.this, ExchangeMedicActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.guide) {
                                Intent intent = new Intent(AlarmActivity.this, AppGuideActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (item.getItemId() == R.id.logout) {
                                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(AlarmActivity.this, LoginActivity.class);
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

        // API 호출 함수 호출
        new GetMedicQnaContentTask().execute();
    }


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
            // UI 업데이트
            updateUIWithQuestions(dataset);
        }
    }

    private void goHome() {
        Intent intent = new Intent(this, MainMedicActivity.class);
        startActivity(intent);
        finish();
    }

    // UI 업데이트 함수
    private void updateUIWithQuestions(ArrayList<QuestionMedicVO> dataset) {
        QuestionMedicWaitAdapter adapter = new QuestionMedicWaitAdapter(this,R.layout.alarm, dataset,this);
        binding.listAlarm.setLayoutManager(new LinearLayoutManager(AlarmActivity.this));
        binding.listAlarm.setAdapter(adapter);
    }

    // API 호출 함수
    private String getMedicQnaContent() throws InterruptedException {
        String url = ipUrl + "MedicQnaContent";
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

    // RecyclerView 아이템 클릭 이벤트 처리
    @Override
    public void onQuestionClick(int position) {

        // 선택된 아이템의 데이터 가져오기
        QuestionMedicVO selectedQuestion = ((QuestionMedicWaitAdapter) binding.listAlarm.getAdapter()).getItem(position);

        // 이미지를 Bitmap 형태로 변환하여 Intent에 추가
        Intent detailIntent = new Intent(AlarmActivity.this, AnswerMedicActivity.class);
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
        startActivity(detailIntent);
    }



    // 세션값 가져오기 함수
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        Log.d("MainUserActivity", "User Info JSON: " + userInfoJson);
        return gson.fromJson(userInfoJson, User.class);
    }

}