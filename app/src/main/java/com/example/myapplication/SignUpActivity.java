package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements MariaDBCallback {

    EditText edit_userName, edit_email, edit_phone, edit_birth, edit_height, edit_weight;
    ImageButton imgbtn_signUp;
    ImageView img_signup, img_signUpback;
    @BindViews({R.id.check_sexMale, R.id.check_sexFemale})
    List<CheckBox> radiosSex;
    @BindViews({R.id.check_smokeY, R.id.check_smokeN})
    List<CheckBox> radiosSmoke;
    @BindViews({R.id.check_diaY, R.id.check_diaN})
    List<CheckBox> radiosDia;
    @BindViews({R.id.check_hbpY, R.id.check_hbpN})
    List<CheckBox> radiosHbp;

    Boolean isValid = true; //判斷欄位是否為空
    ControlMariaDB controlMariaDB = new ControlMariaDB(this);

    String profileJson; //profile的JSON

    String userName, email, phone, birth, old, height, weight,
            checkedSex, checkedSmoke, checkedDia, checkedHbp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initWidget();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //禁止自動彈出虛擬鍵盤
        ButterKnife.bind(SignUpActivity.this);
    }

    private void initWidget() {
        edit_userName = findViewById(R.id.edit_userName);
        edit_email = findViewById(R.id.edit_email);
        edit_phone = findViewById(R.id.edit_phone);
        edit_birth = findViewById(R.id.edit_birth);
        edit_height = findViewById(R.id.edit_height);
        edit_weight = findViewById(R.id.edit_weight);
        img_signup = findViewById(R.id.img_signup);
        img_signUpback = findViewById(R.id.img_signUpBack);
        imgbtn_signUp = findViewById(R.id.imgbtn_signUp);
        img_signup.setImageResource(R.drawable.sign_up2);
        img_signUpback.setImageResource(R.drawable.sign_background2);
        imgbtn_signUp.setImageResource(R.drawable.btn_signup);
        imgbtn_signUp.setOnClickListener(lis);
        showDateOnClick(edit_birth);
    }

    /**
     * 性別單選
     **/
    @OnClick({R.id.check_sexMale, R.id.check_sexFemale})
    void changeSex(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSex);
        checkBox.setChecked(true);
        checkedSex = CommonUtil.getOne(radiosSex);
    }

    /**
     * 抽菸單選
     **/
    @OnClick({R.id.check_smokeY, R.id.check_smokeN})
    void changeSmoke(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSmoke);
        checkBox.setChecked(true);
        checkedSmoke = CommonUtil.getOne(radiosSmoke);
    }

    /**
     * 糖尿病單選
     **/
    @OnClick({R.id.check_diaY, R.id.check_diaN})
    void changeDia(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDia);
        checkBox.setChecked(true);
        checkedDia = CommonUtil.getOne(radiosDia);
    }

    /**
     * 高血壓單選
     **/
    @OnClick({R.id.check_hbpY, R.id.check_hbpN})
    void changeHbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHbp);
        checkBox.setChecked(true);
        checkedHbp = CommonUtil.getOne(radiosHbp);
    }

    /**
     * 出生日期欄點擊事件
     **/
    private void showDateOnClick(final EditText edt) {
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePick(edt);
            }
        });

        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && !edt.isClickable()) {
                    showDatePick(edt);
                }
            }
        });
    }

    /**
     * 日期選擇Dialog
     **/
    private void showDatePick(final EditText edt) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                edt.setText(year + "-" + month + "-" + day);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * 取欄位值
     **/
    private void getValue() {
        userName = edit_userName.getText().toString();
        email = edit_email.getText().toString();
        phone = edit_phone.getText().toString();
        birth = edit_birth.getText().toString();
        height = edit_height.getText().toString();
        weight = edit_weight.getText().toString();
        checkedSex = CommonUtil.getOne(radiosSex);
        checkedSmoke = CommonUtil.getOne(radiosSmoke);
        checkedDia = CommonUtil.getOne(radiosDia);
        checkedHbp = CommonUtil.getOne(radiosHbp);
        checkEmpty();
        calAge();
        packedJson();
    }

    /**
     * 打包JSON
     **/
    public String packedJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", userName);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("birth", birth);
            jsonObject.put("old", old);
            jsonObject.put("height", height);
            jsonObject.put("weight", weight);
            jsonObject.put("sex", checkedSex);
            jsonObject.put("smokes", checkedSmoke);
            jsonObject.put("diabetes", checkedDia);
            jsonObject.put("hbp", checkedHbp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profileJson = String.valueOf(jsonObject);

        return profileJson;
    }

    /**
     * 註冊
     **/
    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (FastClickUtil.isFastDoubleClick()) {
                Toast.makeText(SignUpActivity.this, "請勿快速點擊", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    //確認網路連線
                    CheckInternetDialog checkInternetDialog = new CheckInternetDialog(SignUpActivity.this);
                    checkInternetDialog.checkInternet();
                    getValue();
                    if (isValid) {
                        /**註冊按鈕按下後，用UserRegister，會回傳事件代碼 */
                        controlMariaDB.userRegister(profileJson);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    };

    /**
     * 註冊按鈕按下後，從MariaDBCallback，回傳事件代碼
     */
    @Override
    public void onResult(String result) {
        int eventCode = Integer.parseInt(result);
        judgeEventCode(eventCode);
    }

    @Override
    public void onSave(String result) {

    }

    /**
     * 判斷事件代碼
     */
    public void judgeEventCode(int res) {
        if (res == 1) {
            Toast.makeText(SignUpActivity.this, "註冊成功，頁面跳轉中...", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().postSticky(new MessageEvent(profileJson));//把profile資料送去MainActivity
            Intent goHomePage = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(goHomePage);
            finish();
        }
        if (res == 2) {
            Toast.makeText(SignUpActivity.this, "帳戶已經存在", Toast.LENGTH_SHORT).show();
        } else if (res == 0) {
            Toast.makeText(SignUpActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 註冊成功後傳profile到MainActivity
     **/
    public class MessageEvent {
        private String message;

        public MessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 算年齡
     **/
    private void calAge() {
        //get current time
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String formattedDate = dtf.format(date);

        //get birth's year
        String years = birth.substring(0, 4);
        int pastYear = Integer.parseInt(years);
        int currentYear = Integer.parseInt(formattedDate);
        int howOldAreYou = currentYear - pastYear;
        old = String.valueOf(howOldAreYou);
    }

    /**
     * 判斷輸入欄是否空白
     **/
    private boolean checkEmpty() {
        isValid = true;
        try {
            if (userName.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "用戶名稱欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (birth.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "出生日期欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (height.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "身高欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (weight.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "體重欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (checkedSex.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "性別欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (checkedSmoke.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "抽菸欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (checkedDia.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "糖尿病欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (checkedHbp.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "高血壓欄不得空白", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (!TextUtil.isValidEmailFormat(email)) {
                Toast.makeText(SignUpActivity.this, "Email格式錯誤", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            if (!TextUtil.isPhoneLegal(phone)) {
                Toast.makeText(SignUpActivity.this, "手機號碼格式錯誤", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        } catch (Exception e) {
            Toast.makeText(SignUpActivity.this, "請正確填寫資料", Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }
        return isValid;
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