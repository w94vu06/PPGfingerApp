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


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements MariaDBCallback {
    private ControlMariaDB controlMariaDB = new ControlMariaDB(this);
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

    private HashMap<String, String> infoHashMap = new HashMap<>();
    private HashMap<String, String> healthHashMap = new HashMap<>();
    private HashMap<String, String> habitHashMap = new HashMap<>();

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
                            try {
                                FragmentHideShow(health);
                                btn_upPage.setVisibility(View.VISIBLE);
                                info.sendValue(new Info.DataReturn() {
                                    @Override
                                    public void getResult(HashMap<String, String> hashMap) {
                                        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                                            String key = entry.getKey();
                                            String value = entry.getValue();
                                            Log.d("getInfovalue", "Key: " + key + ", Value: " + value);
                                        }
                                        infoHashMap = hashMap;
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
                            public void getResult(HashMap<String, String> hashMap) {
                                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    Log.d("getHealthValue", "Key: " + key + ", Value: " + value);
                                }
                                healthHashMap = hashMap;
                            }

                        });
                        FragmentHideShow(habit);
                    } else if (nowFragment == habit) {
                        try {
                            habit.sendValue(new Habit.DataReturn() {
                                @Override
                                public void getResult(HashMap<String, String> hashMap) {
                                    for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        Log.d("getHabitValue", "Key: " + key + ", Value: " + value);
                                    }
                                    habitHashMap = hashMap;
                                }
                            });
                            putHashMapToJson();
//                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                            startActivity(intent);
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

    private void putHashMapToJson() {
        JSONObject jsonObject = new JSONObject();

        String userName = infoHashMap.get("userName");
        String phone = infoHashMap.get("phone");
        String birth = infoHashMap.get("birth");
        String old = infoHashMap.get("old");
        String height = infoHashMap.get("height");
        String weight = infoHashMap.get("weight");
        String waist = infoHashMap.get("waist");
        String checkedSex = infoHashMap.get("checkedSex");

        String checkedHeart = healthHashMap.get("checkedHeart");
        String checkedHbp = healthHashMap.get("checkedHbp");
        String checkedSbp = healthHashMap.get("checkedSbp");
        String checkedDbp = healthHashMap.get("checkedDbp");
        String checkedDia = healthHashMap.get("checkedDia");
        String checkedunit = healthHashMap.get("checkedunit");
        String checkedEmpty = healthHashMap.get("checkedEmpty");
        String checkedTwohrs = healthHashMap.get("checkedTwohrs");
        String checkedMedicine = healthHashMap.get("checkedMedicine");
        String checkedFamily = healthHashMap.get("checkedFamily");
        String checkedLow = healthHashMap.get("checkedLow");
        String checkedCovid = healthHashMap.get("checkedCovid");
        String checkedVaccine = healthHashMap.get("checkedVaccine");

        String checkedSmoke = habitHashMap.get("checkedSmoke");
        String checkedDrink = habitHashMap.get("checkedDrink");
        String checkedSport = habitHashMap.get("checkedSport");
        String checkedSleep = habitHashMap.get("checkedSleep");
        try {
            //Info
            jsonObject.put("userName", userName);
            jsonObject.put("phone", phone);
            jsonObject.put("birth", birth);
            jsonObject.put("old", old);
            jsonObject.put("height", height);
            jsonObject.put("weight", weight);
            jsonObject.put("waist", waist);
            jsonObject.put("sex", checkedSex);
            //Health
            jsonObject.put("cvd", checkedHeart);
            jsonObject.put("hbp", checkedHbp);
            jsonObject.put("hbpSBp", checkedSbp);
            jsonObject.put("hbpDBp", checkedDbp);
            jsonObject.put("diabetes", checkedDia);
            jsonObject.put("checkedunit", checkedunit);
            jsonObject.put("morningdiabetes", checkedEmpty);
            jsonObject.put("aftermealdiabetes", checkedTwohrs);
            jsonObject.put("userstatus", checkedMedicine);
            jsonObject.put("family", checkedFamily);
            jsonObject.put("low", checkedLow);
            jsonObject.put("covid", checkedCovid);
            jsonObject.put("vaccine", checkedVaccine);
            //Habit
            jsonObject.put("smokes", checkedSmoke);
            jsonObject.put("drink", checkedDrink);
            jsonObject.put("sport", checkedSport);
            jsonObject.put("sleep", checkedSleep);

            new Thread(() -> {
                String json = jsonObject.toString();
                controlMariaDB.userRegister(json);
            }).start();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
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

    /**
     * 註冊按鈕按下後，從MariaDBCallback，回傳事件代碼
     */
    @Override
    public void onResult(String result) {
        Log.d("xxxx", "onResult: "+result);
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