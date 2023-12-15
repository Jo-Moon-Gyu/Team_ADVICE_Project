package com.example.adviser.activity_intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.TextView;

import com.example.adviser.R;

public class SetPermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_permissions);

        checkAndRequestPermission();

    }

    private void checkAndRequestPermission() {
        // 필요한 권한이 이미 부여되었는지 확인
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 부여되었으면 원하는 작업 수행
            moveToNextPage();
        } else {

            // 권한 체크 및 요청을 2초 딜레이 후에 수행
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPermissions(new String[]
                            {Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, 1);;
                }
            }, 1500);
            // 권한이 부여되지 않았으면 권한 요청

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            // 권한 요청 결과 처리
            boolean allPermissionsGranted = true;

            // 권한 부여 여부 확인
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // 모든 권한이 부여되었으면 원하는 작업 수행
                moveToNextPage();
            } else {
                // 권한이 거부되었으면 권한 요청 다이얼로그 표시
                showPermissionDeniedDialog();
            }
        }
    }


    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("권한 거부");
        builder.setMessage("앱을 사용하기 위해서는 권한이 필요합니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                finish();
            }
        }); // 앱 종료 또는 다른 조치를 수행할 수 있음
        builder.setCancelable(false);
        builder.show();
    }

    private void moveToNextPage() {
        Log.d("SetPermissionsActivity", "moveToNextPage() caslled");
        Intent intent = new Intent(SetPermissionsActivity.this, Example.class);
        startActivity(intent);

        finish();
    }

}
