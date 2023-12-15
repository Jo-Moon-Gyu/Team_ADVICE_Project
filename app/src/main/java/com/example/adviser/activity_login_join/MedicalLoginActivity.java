package com.example.adviser.activity_login_join;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_medicmain.MainMedicActivity;
import com.example.adviser.databinding.ActivityMedicalLoginBinding;
import com.example.adviser.vo.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MedicalLoginActivity extends AppCompatActivity {

    public static final String PREF_NAME = "MyAppSession";
    public static final String KEY_USER_INFO = "user_info";
    ActivityMedicalLoginBinding binding;

    RequestQueue requestQueue;
    StringRequest request;
    private final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.inputEmail.getText().toString();
                String password = binding.inputPw.getText().toString();

                // REST API 호출
                request = new StringRequest(
                        Request.Method.POST,
                        ipUrl + "userInfo",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                handleApiResponse(response);
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
                        params.put("dataid", email);
                        params.put("datapw", password);
                        return params;
                    }
                };

                // 요청을 요청 큐에 추가
                requestQueue.add(request);
            }
        });
    }

    private void handleApiResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("result")) {
                String result = jsonResponse.getString("result");

                if (!"loginFail".equals(result) && !"NotApprove".equals(result)) {
                    User user = new Gson().fromJson(result, User.class);
                    saveSession(user);
                    navigateToMainPage();
                } else if ("loginFail".equals(result)) {
                    Toast.makeText(getApplicationContext(), "로그인 정보를 확인해주세요", Toast.LENGTH_SHORT).show();
                } else if ("NotApprove".equals(result)){
                    Toast.makeText(getApplicationContext(), "승인되지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveSession(User user) {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_INFO, new Gson().toJson(user))
                .apply();
    }

    private void navigateToMainPage() {
        Intent intent = new Intent(MedicalLoginActivity.this, MainMedicActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
    }

    public void joinMembership(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}
