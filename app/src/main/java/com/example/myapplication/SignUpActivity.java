package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.Util.TextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.Cleaner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    EditText edit_userName,edit_email,edit_phone,edit_birth,edit_height,edit_weight;
    ImageButton imgbtn_signUp;
    ImageView img_signup,img_signUpback;
    @BindViews({R.id.check_sexMale, R.id.check_sexFemale})
    List<CheckBox> radiosSex;
    @BindViews({R.id.check_smokeY, R.id.check_smokeN})
    List<CheckBox> radiosSmoke;
    @BindViews({R.id.check_diaY, R.id.check_diaN})
    List<CheckBox> radiosDia;
    @BindViews({R.id.check_hbpY,R.id.check_hbpN})
    List<CheckBox> radiosHbp;
    Boolean isValid = true;
    JsonUpload jsonUpload = new JsonUpload();

    String profileJson;

    String old;

    String userName,email,phone,birth,height,weight,checkedSex,checkedSmoke,checkedDia,checkedHbp = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initWidget();
        ButterKnife.bind(SignUpActivity.this);
    }

    private void initWidget(){
        edit_userName = findViewById(R.id.edit_userName);
        edit_email = findViewById(R.id.edit_email);
        edit_phone = findViewById(R.id.edit_phone);
        edit_birth = findViewById(R.id.edit_birth);
        edit_height = findViewById(R.id.edit_height);
        edit_weight = findViewById(R.id.edit_weight);
        img_signup = findViewById(R.id.img_signup);
        img_signUpback = findViewById(R.id.img_signUpback);
        imgbtn_signUp = findViewById(R.id.imgbtn_signUp);
        img_signup.setImageResource(R.drawable.sign_up2);
        img_signUpback.setImageResource(R.drawable.sign_background2);
        imgbtn_signUp.setImageResource(R.drawable.btn_signup);
        imgbtn_signUp.setOnClickListener(lis);
        showDateOnClick(edit_birth);
    }

    /** 性別單選 **/
    @OnClick({R.id.check_sexMale, R.id.check_sexFemale})
    void changeSex(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSex);
        checkBox.setChecked(true);
        checkedSex = CommonUtil.getOne(radiosSex);
    }

    /** 抽菸單選 **/
    @OnClick({R.id.check_smokeY, R.id.check_smokeN})
    void changeSmoke(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSmoke);
        checkBox.setChecked(true);
        checkedSmoke = CommonUtil.getOne(radiosSmoke);
    }

    /** 糖尿病單選 **/
    @OnClick({R.id.check_diaY, R.id.check_diaN})
    void changeDia(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDia);
        checkBox.setChecked(true);
        checkedDia = CommonUtil.getOne(radiosDia);
    }

    /** 高血壓單選 **/
    @OnClick({R.id.check_hbpY,R.id.check_hbpN})
    void changeHbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHbp);
        checkBox.setChecked(true);
        checkedHbp = CommonUtil.getOne(radiosHbp);
    }

    /** 出生日期欄點擊事件 **/
    private void showDateOnClick(final EditText edt){
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePick(edt);
                    return true;
                }

                return false;
            }
        });

        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    showDatePick(edt);
                }
            }
        });
    }

    /** 日期選擇Dialog **/
    private void showDatePick(final EditText edt){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month+=1;
                edt.setText(year + "-" + month + "-" +day);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private String getValue(){
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
        calAge();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", userName);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("birth", birth);
            jsonObject.put("old", old);
            jsonObject.put("height", height);
            jsonObject.put("weight", weight);
            jsonObject.put("checkedSex", checkedSex);
            jsonObject.put("checkedSmoke", checkedSmoke);
            jsonObject.put("checkedDia", checkedDia);
            jsonObject.put("checkedHbp", checkedHbp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        profileJson = String.valueOf(jsonObject);

        return profileJson;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                getValue();
                checkEmpty();
                if (isValid) {
                    Toast.makeText(SignUpActivity.this, "success", Toast.LENGTH_SHORT).show();
                    jsonUpload.controlMariaDB(profileJson);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    };

    private void calAge() {
        //get current time
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String formattedDate = dtf.format(date);
        //get birth's year
        String years = birth.substring(4);
        int pastYear = Integer.parseInt(years);
        int currentYear = Integer.parseInt(formattedDate);
        int howOldAreYou = currentYear - pastYear;
        old = String.valueOf(howOldAreYou);
    }

    /** 判斷輸入欄是否空白 **/
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

}