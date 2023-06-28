package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Fragment.Category;
import com.example.myapplication.Fragment.HomePage;
import com.example.myapplication.Fragment.Profile;
import com.example.myapplication.Fragment.Setting;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.util.BannerUtils;
import com.youth.banner.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private BottomNavigationView navigationView;

    private static Boolean isExit = false;
    private static Boolean hasTask = false;

    private String userName, email, phone, birth;
    private int old, height, weight, sex, smokes, diabetes, hbp;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        fragmentContainer = findViewById(R.id.fragmentContainer);
        setMain();//設定主畫面
        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(NaviSelectedListener);
        preferences = getSharedPreferences( "my_preferences", MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 返回鍵退出程式
     **/
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
        Log.d("rrrr", "onProfilePage: " + profileMsg);
        unpackJson(profileMsg);

    }

    private void unpackJson(String json) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                userName = jsonObject.getString("userName");
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
                Log.d("rrrr", "onMain: " + userName + email + phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setProfile() {
        editor.putString("ProfileName", userName);
        editor.putString("ProfileEmail", email);
        editor.putString("ProfilePhone", phone);
        editor.putString("ProfileBirth", birth);
        editor.putInt("ProfileOld", old);
        editor.putInt("ProfileHeight", height);
        editor.putInt("ProfileWeight", weight);
        editor.putInt("ProfileSex", sex);
        editor.putInt("ProfileSmokes", smokes);
        editor.putInt("ProfileDiabetes", diabetes);
        editor.putInt("ProfileHbp", hbp);
        editor.apply();

        String name = preferences.getString("ProfileName", "無資料");
        Log.d("rrrr", "checkEditor: "+name);
    }


    private NavigationBarView.OnItemSelectedListener NaviSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.homepage:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomePage()).commit();
                    return true;
                case R.id.category:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Category()).commit();
                    return true;
                case R.id.setting:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Setting()).commit();
                    return true;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new Profile()).commit();
                    return true;
            }
            return false;
        }
    };

    private void setMain() {
        this.getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new HomePage()).commit();
    }
}