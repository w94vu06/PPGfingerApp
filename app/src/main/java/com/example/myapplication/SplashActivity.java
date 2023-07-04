package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoggedIn();
            }
        }, 2000); // 延遲時間，單位毫秒（此處設置為 2000 毫秒，即 2 秒）
    }

    private void isLoggedIn() {
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        Intent intent_IN = new Intent();
        if (isLoggedIn) {
            intent_IN.setClass(SplashActivity.this, MainActivity.class);
        } else {
            intent_IN.setClass(SplashActivity.this, BeginActivity.class);
        }
        startActivity(intent_IN);
        finish();
    }
}
