package com.example.adviser.activity_intro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.adviser.R;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.databinding.ActivityResultExperienceBinding;

public class ResultExperienceActivity extends AppCompatActivity {

    ActivityResultExperienceBinding binding;
    Bitmap resultImageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultExperienceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();

        // 앨범에서 이미지를 제대로 받아오는지 확인
        String resultImagePath = getIntent().getStringExtra("resultImagePath");
        if (resultImagePath != null) {
            resultImageBitmap = decodeFile(resultImagePath);
            // 나머지 코드...
        } else {
            // 파일 경로가 null인 경우에 대한 처리
            Log.e("ResultExperienceActivity", "Received null file path");
        }

        // 텍스트 데이터 가져오기
        String resultText = getIntent().getStringExtra("resultText");
        int resultValue = getIntent().getIntExtra("resultValue", 0);

        // 이미지 및 텍스트를 사용하여 UI 업데이트
        binding.ivCamera.setImageBitmap(resultImageBitmap);
        binding.resultText.setText("피부 상처는 " + resultText + " " + resultValue + "% 입니다.");
    }

    // 리스너 설정
    private void setupListeners() {
        binding.adviserRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    // 기능 함수 ----------------------------------------
    private void goToLogin() {
        Intent intent = new Intent(ResultExperienceActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private Bitmap decodeFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(filePath, options);
    }

    private String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return filePath;
    }
}
