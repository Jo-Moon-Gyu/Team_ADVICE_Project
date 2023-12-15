package com.example.adviser.activity_login_join;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.adviser.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button userLogin = findViewById(R.id.userLogin);
        Button medicalLogin = findViewById(R.id.medicalLogin);

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserLoginActivity();
            }
        });

        medicalLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMedicalLoginActivity();
            }
        });
    }

    private void startUserLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMedicalLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, MedicalLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
