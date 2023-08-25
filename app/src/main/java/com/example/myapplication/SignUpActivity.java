package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.myapplication.SignUpFragment.Habit;
import com.example.myapplication.SignUpFragment.Health;
import com.example.myapplication.SignUpFragment.Info;
import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.Util.FastClickUtil;
import com.example.myapplication.Util.TextUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements MariaDBCallback {

    Button btn_nextPage,btn_upPage;
    ImageView img_signup,img_signUpback;
    Info info = new Info();
    Health health = new Health();
    Habit habit = new Habit();
    private Fragment currentFragment;
    private FrameLayout container_signup;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initWidget();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //禁止自動彈出虛擬鍵盤
        ButterKnife.bind(SignUpActivity.this);


    }

    private void initWidget() {
        img_signup = findViewById(R.id.img_signup);
        img_signUpback = findViewById(R.id.img_signUpback);
        btn_nextPage = findViewById(R.id.btn_nextPage);
        btn_upPage = findViewById(R.id.btn_upPage);
        container_signup = findViewById(R.id.container_signup);
        fragmentManager = getSupportFragmentManager();
        img_signup.setImageResource(R.drawable.sign_up2);
        img_signUpback.setImageResource(R.drawable.art);
        btn_upPage.setVisibility(View.INVISIBLE);
        btn_upPage.setClickable(false);
        btn_nextPage.setOnClickListener(lis);
        btn_upPage.setOnClickListener(lis);
        try {
            setFragment();
        }catch (Exception e){
            Log.e("tessttt",e.toString());
        }
    }

    private void setFragment(){
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container_signup,info);
        transaction.commit();
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment nowFragment = checkFragment();
            switch (view.getId()){
                case R.id.btn_nextPage:
                    if (nowFragment == info){
                        checkInfoData();
                        btn_upPage.setClickable(true);
                        btn_upPage.setVisibility(View.VISIBLE);
                        currentFragment = info;
                        try {
                            FragmentHideShow(health);
                            info.sendValue(new Info.DataReturn() {
                                @Override
                                public void getResult(String value) {
                                    Log.d("getvalue",value);
                                }
                            });
                        }catch (Exception e){
                            Log.e("change",e.toString());
                        }
                    }else if(nowFragment == health){
                        btn_upPage.setClickable(true);
                        btn_upPage.setVisibility(View.VISIBLE);
                        btn_nextPage.setText("註冊");
                        FragmentHideShow(habit);
                    }else if(nowFragment == habit){
                        try {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }catch (Exception e){
                            Log.e("errrr",e.toString());
                        }
                    }
                    break;
                case R.id.btn_upPage:
                    if (nowFragment == habit){
                        btn_nextPage.setText("下一頁");
                        FragmentHideShow(health);
                    }else if(nowFragment == health){
                        btn_upPage.setClickable(false);
                        btn_upPage.setVisibility(View.INVISIBLE);
                        FragmentHideShow(info);
                    }
                    break;
            }
        }
    };

    private Fragment checkFragment(){
        FragmentManager fm = this.getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment:fragments){
            if (fragment != null && fragment.isVisible()){
                return fragment;
            }
        }
        return null;
    }

    private void FragmentHideShow(Fragment fg){
        fragmentManager = getSupportFragmentManager();
        transaction= fragmentManager.beginTransaction();
        if(!fg.isAdded()){
            transaction.hide(currentFragment);
            transaction.add(R.id.container_signup,fg);
        }else{
            transaction.hide(currentFragment);
            transaction.show(fg);
        }
        currentFragment=fg;
        transaction.commit();
    }

    public void checkInfoData() {
        if (info != null) {
            EditText editText = info.requireView().findViewById(R.id.edit_phone);
            String terr = editText.getText().toString();
            Toast.makeText(SignUpActivity.this, ""+terr, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 註冊按鈕按下後，從MariaDBCallback，回傳事件代碼
     */
    @Override
    public void onResult(String result) {
    }

    @Override
    public void onSave(String result) {

    }

    @Override
    public void onTest(String result) {

    }
    /**
     * 判斷事件代碼
     */

    /**
     * 註冊成功後傳profile到MainActivity
     **/
    public class MessageEvent {
        private final String message;

        public MessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}