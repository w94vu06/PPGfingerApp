package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        fragmentContainer = findViewById(R.id.fragmentContainer);
        setMain();//設定主畫面
        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(NaviSelectedListener);
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


//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
//    /**
//     * 接收profile
//     **/
//    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
//    public void onMessageEvent(SignUpActivity.MessageEvent event) {
//        // 收到MessageEvent時要做的事寫在這裡
//        String profileMsg = event.getMessage();
//        Log.d("rrrr", "onProfileEvent: " + profileMsg);
//    }

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