package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.myapplication.Data.DataRecord;
import com.example.myapplication.Fragment.Record;
import com.example.myapplication.Fragment.HomePage;
import com.example.myapplication.Fragment.Profile;
import com.example.myapplication.Fragment.Category;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MariaDBCallback {

    private FrameLayout fragmentContainer;
    private BottomAppBar bar;
    private BottomNavigationView navigationView;
    private FloatingActionButton fab_measure;
    private String loginName, loginPhone;
    private String userId, userName, email, phone, birth;
    private int old, height, weight, sex, smokes, diabetes, hbp;

    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private DataRecord dataRecordViewModel;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        fragmentContainer = findViewById(R.id.fragmentContainer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(NaviSelectedListener);
        switchFragment(new HomePage()).commit();//設定主畫面
        bar = findViewById(R.id.bar);
        fab_measure = findViewById(R.id.fab_measure);
        CheckInternetDialog checkInternetDialog = new CheckInternetDialog(MainActivity.this);
        checkInternetDialog.checkInternet();
        enterPPG();
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        editor = preferences.edit();
        dataRecordViewModel = new ViewModelProvider(this).get(DataRecord.class);
        preloadRecord();
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
            if (!isExit) {
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

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_VIDEO}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String name = preferences.getString("userName", null);
        if (name == null) {
            readProfile();
        }
        EventBus.getDefault().register(this);

    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收profile
     **/
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(SignUpActivity.MessageEvent event) {
        // 收到MessageEvent時要做的事寫在這裡
        String profileMsg = event.getMessage();
        unpackJson(profileMsg);
    }

    /**
     * 透過preference裡的資料到資料庫查詢profile
     **/
    public void readProfile() {
        loginName = preferences.getString("LoginName", null);
        loginPhone = preferences.getString("LoginPhone", null);
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("userName", loginName);
            jsonData.put("phone", loginPhone);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonString = jsonData.toString();
        controlMariaDB.userRead(jsonString);
//        if (loginName == null) {
//
//        }
    }

    private void preloadRecord() {
        Calendar calendar = Calendar.getInstance();
        JSONObject jsonObject = new JSONObject();
        String userId = preferences.getString("ProfileId", "888889");
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        Log.d("dddd", "preloadRecord: "+month);
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("selectYear", year);
            jsonObject.put("selectMonth", month);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            String json = jsonObject.toString();
            controlMariaDB.IdAndDateReadData(json);
        }).start();
    }

    @Override
    public void onResult(String result) {
        //userRead
        Log.d("resultLONG", "onResult: " + result);
        if (result.equals("0")) {
            Toast.makeText(MainActivity.this, "請重新登入", Toast.LENGTH_SHORT).show();
            Intent it = new Intent(MainActivity.this, BeginActivity.class);
            startActivity(it);
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            finish();
        } else {
            unpackJson(result);
        }
    }

    @Override
    public void onSave(String result) {
        if (!result.equals("noData")) {
            dataRecordViewModel.setData(result);
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

    public void enterPPG() {
        fab_measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPPG = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intentPPG);
            }
        });
    }

    private final NavigationBarView.OnItemSelectedListener NaviSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.homepage:
                    switchFragment(new HomePage()).commit();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomePage()).commit();
                    return true;
                case R.id.record:
                    switchFragment(new Record()).commit();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Record()).commit();
                    return true;
                case R.id.category:
                    switchFragment(new Category()).commit();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Category()).commit();
                    return true;
                case R.id.profile:
                    switchFragment(new Profile()).commit();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Profile()).commit();
                    return true;
            }
            return false;
        }
    };

    private Fragment currentFragment = new Fragment();

    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragmentContainer, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }


    private void setMain() {
//        this.getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new HomePage()).commit();
    }

    private String decodeUnicode(String unicodeString) {
        StringBuilder sb = new StringBuilder();
        int length = unicodeString.length();
        for (int i = 0; i < length; i++) {
            char c = unicodeString.charAt(i);
            if (c == '\\' && i + 1 < length && unicodeString.charAt(i + 1) == 'u') {
                String hex = unicodeString.substring(i + 2, i + 6);
                try {
                    int code = Integer.parseInt(hex, 16);
                    sb.append((char) code);
                    i += 5; // 跳過已處理的字符
                } catch (NumberFormatException e) {
                    // 忽略解析失敗的情況
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}