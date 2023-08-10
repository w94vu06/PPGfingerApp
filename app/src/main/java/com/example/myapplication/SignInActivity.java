package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Util.TextUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements MariaDBCallback {

    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private EditText edit_userName;
    private EditText edit_phone;
    private Button btn_login;
    private CheckBox remember;
    String profileJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        closeTopBar();
        loginObject();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //禁止自動彈出虛擬鍵盤
    }

    public void loginObject() {
//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        edit_userName = findViewById(R.id.edit_userName);
        edit_phone = findViewById(R.id.edit_phone);
        btn_login = findViewById(R.id.btn_login);
        remember = findViewById(R.id.rememberMe);
        setRememberAndLogin();
        inputEditTextListener();
    }

    private void inputEditTextListener() {
        edit_phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // 虛擬鍵盤的回車鍵被按下
                    btn_login.performClick(); // 觸發按鈕的點擊事件
                    return true;
                }
                return false;
            }
        });
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
                    if (!TextUtil.isPhoneLegal(phone)) {
                        Toast.makeText(SignInActivity.this, "手機號碼格式錯誤", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "驗證中...", Toast.LENGTH_SHORT).show();
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
                packedJson();

                controlMariaDB.userLogin(profileJson);
            }
        });
    }

    public void packedJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", edit_userName.getText().toString());
            jsonObject.put("phone", edit_phone.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profileJson = String.valueOf(jsonObject);
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

    @Override
    public void onSave(String result) {

    }

    @Override
    public void onTest(String result) {

    }

    public void judgeEventCode(int res) {
        if (res == 1) {
            Toast.makeText(SignInActivity.this, "登入成功，頁面跳轉中...", Toast.LENGTH_SHORT).show();
            recordAccount();
            Intent goHomePage = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(goHomePage);
            finish();
        }else if (res == 0) {
            Toast.makeText(SignInActivity.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    public void recordAccount() {
        String name = edit_userName.getText().toString();
        String phone = edit_phone.getText().toString();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("LoginName", name);
        editor.putString("LoginPhone", phone);
        editor.apply();
    }
}