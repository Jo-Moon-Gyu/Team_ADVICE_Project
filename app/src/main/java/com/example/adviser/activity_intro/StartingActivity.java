package com.example.adviser.activity_intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adviser.R;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(StartingActivity.this, SetPermissionsActivity.class);
            startActivity(intent);
            finish();
        }, 1300);
    }
}
