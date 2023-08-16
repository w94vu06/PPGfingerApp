package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Data.DataRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SplashActivity extends AppCompatActivity implements MariaDBCallback {

    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String userId, userName, email, phone, birth;
    private int old, height, weight, sex, smokes, diabetes, hbp;
    private String recordBundleData;
    Intent intent_IN;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        intent_IN = new Intent();
//        openCamera();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoggedIn();
            }
        }, 2000); // 延遲時間，單位毫秒（此處設置為 2000 毫秒，即 2 秒）
    }
    private void isLoggedIn() {
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            preloadProfile();
            preloadRecord();
            intent_IN.setClass(SplashActivity.this, MainActivity.class);
        } else {
            intent_IN.setClass(SplashActivity.this, BeginActivity.class);
        }
        startActivity(intent_IN);
        finish();
    }

    private void preloadRecord() {
        Calendar calendar = Calendar.getInstance();
        JSONObject jsonObject = new JSONObject();
        String userId = preferences.getString("ProfileId", "888889");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("selectYear", year);
            jsonObject.put("selectMonth", month);
            jsonObject.put("selectDay", day);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            String json = jsonObject.toString();
            controlMariaDB.IdAndDateReadData(json);
        }).start();
    }

    private void preloadProfile() {
        String name = preferences.getString("userName", null);
        if (name == null) {
            readProfile();
        }
    }
    public void readProfile() {
        String loginName = preferences.getString("LoginName", null);
        String loginPhone = preferences.getString("LoginPhone", null);
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userName", loginName);
            jsonData.put("phone", loginPhone);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonString = jsonData.toString();
        controlMariaDB.userRead(jsonString);
    }



    @Override
    public void onResult(String result) {
        //userRead
        if (result.equals("0")) {
            editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            finish();
        } else {
            unpackJson(result);
        }
    }

    @Override
    public void onSave(String result) {
        Log.d("dddd", "onSave: "+result);
        if (result.equals("noData")) {
        } else {
            editor = preferences.edit();
            editor.putString("recordData", result);
            editor.apply();
        }
    }

    @Override
    public void onTest(String result) {

    }
    private void unpackJson(String json) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                userId = jsonObject.getString("userId");
                userName = jsonObject.getString("name");
                email = jsonObject.getString("email");
                phone = jsonObject.getString("phone");
                birth = jsonObject.getString("birth");
                old = jsonObject.getInt("old");
                height = jsonObject.getInt("height");
                weight = jsonObject.getInt("weight");
                sex = jsonObject.getInt("sex");
                smokes = jsonObject.getInt("smokes");
                diabetes = jsonObject.getInt("diabetes");
                hbp = jsonObject.getInt("hbp");
                setProfile();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void setProfile() {
        editor = preferences.edit();
        editor.putString("ProfileId", userId);
        editor.putString("ProfileName", userName);
        editor.putString("ProfileEmail", email);
        editor.putString("ProfilePhone", phone);
        editor.putString("ProfileBirth", birth);
        editor.putInt("ProfileOld", old);
        editor.putInt("ProfileSex", sex);
        editor.putInt("ProfileHeight", height);
        editor.putInt("ProfileWeight", weight);
        editor.putInt("ProfileDiabetes", diabetes);
        editor.putInt("ProfileSmokes", smokes);
        editor.putInt("ProfileHbp", hbp);
        editor.apply();
    }
}
