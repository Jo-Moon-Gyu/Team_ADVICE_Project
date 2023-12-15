package com.example.adviser.activity_intro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.adviser.R;
import com.example.adviser.activity_login_join.JoinActivity;
import com.example.adviser.activity_login_join.LoginActivity;
import com.example.adviser.activity_login_join.MedicalLoginActivity;
import com.example.adviser.activity_login_join.UserLoginActivity;

public class Example extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Button btnEx = findViewById(R.id.btnEx);
        btnEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCameraActivity();
            }
        });
    }

    private void moveToCameraActivity() {
        Intent intent = new Intent(Example.this, ExperienceActivity.class);
        intent.putExtra("startPreview", true);
        startActivity(intent);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void joinMembership(View view) {
        Intent intent = new Intent(this, MedicalLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
