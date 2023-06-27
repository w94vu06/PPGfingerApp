package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity implements MariaDBCallback {

    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText edit_userName;
    private EditText edit_phone;
    private Button btn_login;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        closeTopBar();
        loginObject();

    }

    public void loginObject() {
//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        edit_userName = findViewById(R.id.edit_userName);
        edit_phone = findViewById(R.id.edit_phone);
        btn_login = findViewById(R.id.btn_login);
        remember = findViewById(R.id.rememberMe);
        setRememberAndLogin();
    }

    public void setRememberAndLogin() {
        boolean isRemember = preferences.getBoolean("remember_phone", false);
        if (isRemember) {
            String name = preferences.getString("LoginName","");
            String phone = preferences.getString("LoginPhone","");
            edit_userName.setText(name);
            edit_phone.setText(phone);
            remember.setChecked(true);
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //確認網路連線
                CheckInternetDialog checkInternetDialog = new CheckInternetDialog(SignInActivity.this);
                checkInternetDialog.checkInternet();

                String name = edit_userName.getText().toString();
                String phone = edit_phone.getText().toString();
                if (TextUtils.isEmpty(edit_userName.getText().toString()) || TextUtils.isEmpty(edit_phone.getText().toString())) {
                    Toast.makeText(SignInActivity.this, "欄位不得為空", Toast.LENGTH_SHORT).show();
                } else {
                    editor = preferences.edit();
                    if (remember.isChecked()) {
                        editor.putBoolean("remember_phone", true);
                        editor.putString("LoginName", name);
                        editor.putString("LoginPhone", phone);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                }

            }
        });
    }

    /**
     * //關閉標題列
     */
    public void closeTopBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onResult(String result) {
        int eventCode = Integer.parseInt(result);
        judgeEventCode(eventCode);
    }

    public void judgeEventCode(int res) {
        if (res == 1) {
            Toast.makeText(SignInActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
            Intent goHomePage = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(goHomePage);
        }else if (res == 0) {
            Toast.makeText(SignInActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
        }
    }
}