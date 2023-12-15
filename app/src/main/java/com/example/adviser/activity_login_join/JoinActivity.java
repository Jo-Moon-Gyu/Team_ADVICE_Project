package com.example.adviser.activity_login_join;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;

import com.android.volley.toolbox.StringRequest;
import com.example.adviser.databinding.ActivityJoinBinding;
import com.example.adviser.vo.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity {

    private EditText mEtAddress;
    ActivityJoinBinding binding;
    RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedImageBitmap;
    private String fileName;
    private final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            // 추가된 부분: 첨부 파일을 선택하는 리스너 설정
            binding.certificate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 파일 선택을 위한 액티비티를 띄우는 코드
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");  // 모든 파일 형식 선택 가능
                    startActivityForResult(Intent.createChooser(intent, "이미지"), PICK_IMAGE_REQUEST);
                }
            });


        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    join();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private String generateImageFileName(String userEmail) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return "Join_" + userEmail + "_" + timeStamp + ".jpg";
    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Search Activity 로부터의 결과 값이 이곳으로 전달 된다.. (setResult에 의해..)
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        String data = result.getData().getStringExtra("data");
                        mEtAddress.setText(data);
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 갤러리에서 이미지를 선택한 경우
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                fileName = generateImageFileName(binding.joinEmail.getText().toString());
                binding.regist.setText(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // getFileNameFromUri 메서드는 그대로 유지
    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath(); // 파일 이름을 얻을 수 없으면 Uri에서 경로를 사용
        }
        return result;
    }

    private void join() throws JSONException {
        String reqImg = encodeImage(selectedImageBitmap);
        String userEmail = binding.joinEmail.getText().toString();
        String userPw = binding.joinPw.getText().toString();
        String userPw2 = binding.joinPw2.getText().toString();
        String userName = binding.name.getText().toString();
        String userPhone = binding.tel.getText().toString();

        Log.d("djjjjj",userName);
        User user = new User();
        user.setUserEmail(userEmail);
        user.setUserPw(userPw);
        user.setUserName(userName);
        user.setUserPhone(userPhone);
        user.setUserAuth(reqImg);
        user.setUserloginType("Normal");
        user.setUserRole("의대생");

        Log.d("check", reqImg);
        Log.d("file", fileName);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(user);


        if (!userPw.equals(userPw2)) {
            Log.d("teetett", "여기를 타나?");
            Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        }else if(userEmail.equals("")){
            Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
        }else if(userName.equals("")){
            Toast.makeText(JoinActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        }else if(userPhone.equals("")){
            Toast.makeText(JoinActivity.this, "핸드폰번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    ipUrl + "joinMedic?imageFileName=" + fileName,
                    new JSONObject(jsonRequest),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // 서버 응답 처리
                            Toast.makeText(JoinActivity.this, "가입 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            waitJoin();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 서버 응답 에러 처리
                            Log.d("error가 왜 나와", error.toString());
                            Toast.makeText(getApplicationContext(), "가입 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            waitJoin();
                        }
                    });

            requestQueue.add(request);
        }
    }

    public void onEmail(View view) {

        String email = binding.joinEmail.getText().toString().trim();

        // 이메일 유효성 검사
        if (!isValidEmail(email)) {
            // 이메일이 유효하지 않은 경우
            showValidationMessage("이메일이 유효하지 않습니다.");
            return;
        }

        // 이메일 중복 확인 등 필요한 로직 수행
        boolean isEmailAvailable = checkEmailAvailability(email);


        // 패스워드 등 다른 로직 수행
        // ...

        String password = binding.joinPw.getText().toString().trim();
        String confirmPassword = binding.joinPw2.getText().toString().trim();


        // 예를 들어, 가입 성공 시 메시지 출력
        showValidationMessage("가입이 완료되었습니다.");
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean checkEmailAvailability(String email) {
        // 여기에 이메일 중복 확인 로직을 추가
        // 예를 들어, 서버 API를 호출하여 중복 여부를 확인할 수 있습니다.
        // 이 예제에서는 임의로 true를 반환하도록 설정합니다.
        return true;
    }

    private void showValidationMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void waitJoin() {
        Intent intent = new Intent(JoinActivity.this, MedicalSingWaitingActivity.class);
        startActivity(intent);
        finish();
    }


    private String encodeImage(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d("hiiiiiiiiiiii", "hi");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        } else {
            Log.d("hiiiiiiiiiii", "bye");

            return ""; // 이미지가 선택되지 않았을 때 빈 문자열 반환 또는 다른 처리를 수행할 수 있습니다.
        }
    }


}