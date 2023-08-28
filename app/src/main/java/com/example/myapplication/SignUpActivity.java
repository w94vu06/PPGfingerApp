package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.SignUpFragment.Habit;
import com.example.myapplication.SignUpFragment.Health;
import com.example.myapplication.SignUpFragment.Info;
import com.example.myapplication.Util.TextUtil;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements MariaDBCallback {

    Button btn_nextPage, btn_upPage;
    ImageView img_signup, img_signUpback;
    Info info = new Info();
    Health health = new Health();
    Habit habit = new Habit();
    private Fragment currentFragment;
    private FrameLayout container_signup;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    Boolean isValid = true;

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
        } catch (Exception e) {
            Log.e("tessttt", e.toString());
        }
    }

    private void setFragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container_signup, info);
        transaction.commit();
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment nowFragment = checkFragment();
            switch (view.getId()) {
                case R.id.btn_nextPage:
                    if (nowFragment == info) {
                        checkInfoEmpty();
                        btn_upPage.setClickable(true);
                        currentFragment = info;
                        if (isValid) {
                            btn_upPage.setVisibility(View.VISIBLE);
                            try {
                                FragmentHideShow(health);
                                info.sendValue(new Info.DataReturn() {
                                    @Override
                                    public void getResult(HashMap<String, String> hashMap) {
                                        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                                            String key = entry.getKey();
                                            String value = entry.getValue();
                                            Log.d("getInfovalue", "Key: " + key + ", Value: " + value);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.e("change", e.toString());
                            }
                        }
                    } else if (nowFragment == health) {
                        btn_upPage.setClickable(true);
                        btn_upPage.setVisibility(View.VISIBLE);
                        btn_nextPage.setText("註冊");
                        health.sendValue(new Health.DataReturn() {
                            @Override
                            public void getResult(String value) {
                                Log.d("getHealthValue", ""+value);
                            }
                        });

                        if (isValid) {

                        }

                        FragmentHideShow(habit);
                    } else if (nowFragment == habit) {
                        try {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("errrr", e.toString());
                        }
                    }
                    break;
                case R.id.btn_upPage:
                    if (nowFragment == habit) {
                        btn_nextPage.setText("下一頁");
                        FragmentHideShow(health);
                    } else if (nowFragment == health) {
                        btn_upPage.setClickable(false);
                        btn_upPage.setVisibility(View.INVISIBLE);
                        FragmentHideShow(info);
                    }
                    break;
            }
        }
    };

    private Fragment checkFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private void FragmentHideShow(Fragment fg) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if (!fg.isAdded()) {
            transaction.hide(currentFragment);
            transaction.add(R.id.container_signup, fg);
        } else {
            transaction.hide(currentFragment);
            transaction.show(fg);
        }
        currentFragment = fg;
        transaction.commit();
    }
    public void checkInfoEmpty() {
        isValid = true;
        if (info != null) {
            Map<Integer, String> fieldMap = new HashMap<>();
            fieldMap.put(R.id.edit_userName, "用戶名稱欄不得空白");
            fieldMap.put(R.id.edit_height, "身高欄不得空白");
            fieldMap.put(R.id.edit_weight, "體重欄不得空白");

            Pattern pattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/~`]+");

            for (Map.Entry<Integer, String> entry : fieldMap.entrySet()) {
                EditText editText = info.requireView().findViewById(entry.getKey());
                String fieldValue = editText.getText().toString().trim();

                if (fieldValue.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, entry.getValue(), Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    Matcher matcher = pattern.matcher(fieldValue);
                    if (matcher.find()) {
                        Toast.makeText(SignUpActivity.this, "字段不能包含特殊字符", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                }
            }

            EditText edit_birth = info.requireView().findViewById(R.id.edit_birth);
            String birthValue = edit_birth.getText().toString().trim();
            if (birthValue.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "出生日期欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            EditText edit_phone = info.requireView().findViewById(R.id.edit_phone);
            String phoneValue = edit_phone.getText().toString().trim();
            if (!TextUtil.isPhoneLegal(phoneValue)) {
                Toast.makeText(SignUpActivity.this, "手機號碼格式錯誤", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }
    }


    public void checkHealthEmpty() {
        isValid = true;

    }

    public void checkHabitEmpty() {

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