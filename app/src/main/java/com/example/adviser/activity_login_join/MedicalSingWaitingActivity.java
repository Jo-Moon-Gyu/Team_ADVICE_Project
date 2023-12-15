package com.example.adviser.activity_login_join;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.adviser.R;

public class MedicalSingWaitingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_sing_waiting);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(MedicalSingWaitingActivity.this, MedicalLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
