package com.example.adviser.qna;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.UserLoginActivity;
import com.example.adviser.activity_usermain.MainUserActivity;
import com.example.adviser.activity_usermain.MypageUserActivity;
import com.example.adviser.databinding.ActivityQuesitonUserBinding;
import com.example.adviser.kakaomap.MapUserActivity;
import com.example.adviser.payment.PaymentActivity;
import com.example.adviser.vo.Tbl_Request;
import com.example.adviser.vo.User;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class QuesitonUserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RequestQueue requestQueue;
    private ActivityQuesitonUserBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Bitmap selectedImageBitmap;
    private final String ipUrl = "http://13.209.4.93:8092/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuesitonUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 상단 우측 메뉴버튼
        ImageButton menuBtn = findViewById(R.id.sideBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        // 세션에서 사용자 정보 가져오기
        User user = getUserInfoFromSession();

        // 유저 정보를 레이아웃에 표시한다.
        binding.nick.setText(user.getUserNick() + "님");

        // 카메라 버튼 클릭 이벤트
        binding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCaAndGal();
            }
        });

        // 질문 등록 버튼 클릭 이벤트
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarningDialog();
            }
        });

        // 종료 버튼 클릭 이벤트
        Button closeQuestion = findViewById(R.id.closeQuestion);
        closeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeQuestionMes();
            }
        });
    }

    // 함수 로직-------------------------------------------------------------------

    // 뒤로가기 버튼이 눌렸을 때 실행될 코드
    @Override
    public void onBackPressed() {
        closeQuestionMes();
    }

    // 사용자 세션 정보
    private User getUserInfoFromSession() {
        Gson gson = new Gson();
        String userInfoJson = getSharedPreferences(UserLoginActivity.PREF_NAME, MODE_PRIVATE).getString(UserLoginActivity.KEY_USER_INFO, "");
        return gson.fromJson(userInfoJson, User.class);
    }

    private String encodeImage(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        } else {
            return "";
        }
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("질문을 등록하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    uploadDataToServer();

                    Intent intent = new Intent(QuesitonUserActivity.this, PaymentActivity.class);
                    if (selectedImageBitmap != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra("insertImg", byteArray);
                    }
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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

    private void uploadDataToServer() throws JSONException {
        User user = getUserInfoFromSession();
        String userEmail = user.getUserEmail();
        String reqUserNick = user.getUserNick();
        String reqTitle = binding.questionTitle.getText().toString();
        String reqContent = binding.quesitonCont.getText().toString();
        String reqImg = encodeImage(selectedImageBitmap);

        String url;
        if (!reqImg.equals("")) {
            String imageFileName = generateImageFileName(userEmail);
            url = ipUrl + "uploadImage2?imageFileName=" + imageFileName;
        } else {
            Log.d("이미지 없음", "없음");
            String imageFileName = "";
            url = ipUrl + "uploadImage2?imageFileName=" + imageFileName;
        }

        Tbl_Request tblRequest = new Tbl_Request();
        tblRequest.setUserEmail(userEmail);
        tblRequest.setReqUserNick(reqUserNick);
        tblRequest.setReqTitle(reqTitle);
        tblRequest.setReqContent(reqContent);
        tblRequest.setReqImg(reqImg);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(tblRequest);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonRequest),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Error: " + error.toString());
                    }
                });

        requestQueue.add(request);
    }

    private void closeQuestionMes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("질문을 등록을 취소 하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuesitonUserActivity.this, MainUserActivity.class);
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

    private void openCaAndGal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("피부 상처 사진을 가져올 방식을 선택하세요.");

        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openGallery();
            }
        });

        builder.setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openCamera();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요"), PICK_IMAGE_REQUEST);
    }

    private String generateImageFileName(String userEmail) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return "Image_" + userEmail + "_" + timeStamp + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                binding.photo.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            try {
                Bundle extras = data.getExtras();
                selectedImageBitmap = (Bitmap) extras.get("data");
                binding.photo.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        }

        if (destinationActivity != null) {
            navigateToActivity(destinationActivity);
        }
    }

    private void navigateToActivity(Class<?> destinationActivity) {
        Intent intent = new Intent(QuesitonUserActivity.this, destinationActivity);
        startActivity(intent);
        finish();
    }
}
