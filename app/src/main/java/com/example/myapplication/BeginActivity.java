package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BeginActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    Button btn_signin,btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        isLoggedIn();
    }

    /**
     * 返回鍵退出程式
     **/
    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer timerExit = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            isExit = false;
            hasTask = true;
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判斷是否按下Back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if (isExit == false) {
                isExit = true; //記錄下一次要退出
                Toast.makeText(this, "再按一次退出APP"
                        , Toast.LENGTH_SHORT).show();

                // 如果超過兩秒則恢復預設值
                if (!hasTask) {
                    timerExit.schedule(task, 2000);
                }
            } else {
                finish(); // 離開程式
                System.exit(0);
            }
        }
        return false;
    }

    public void initParameter(){
        btn_signin = findViewById(R.id.btn_signin);
        btn_signup = findViewById(R.id.btn_signup);
        btn_signin.setOnClickListener(lis);
        btn_signup.setOnClickListener(lis);
    }

    private void isLoggedIn() {
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent intent_IN = new Intent();
            intent_IN.setClass(BeginActivity.this, MainActivity.class);
            startActivity(intent_IN);
            finish();
        } else {
            Window window = getWindow();
            if(window != null){
                window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
                window.setEnterTransition(new android.transition.Slide(Gravity.BOTTOM));
                window.setExitTransition(new android.transition.Slide(Gravity.TOP));
                window.setReenterTransition(new android.transition.Slide(Gravity.BOTTOM));
                window.setReturnTransition(new android.transition.Slide(Gravity.TOP));
            }
            setContentView(R.layout.activity_begin);
            initParameter();
        }
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_signin:
                    Intent intent_IN = new Intent();
                    intent_IN.setClass(BeginActivity.this, SignInActivity.class);
                    startActivity(intent_IN);
                    break;
                case R.id.btn_signup:
                    Intent intent_UP = new Intent();
                    intent_UP.setClass(BeginActivity.this, SignUpActivity.class);
                    startActivity(intent_UP);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}