package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.Util.TextUtil;

import java.util.Calendar;
import java.util.List;

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

    private void getValue(){
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
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
//            getValue();
//            checkEmpty();
//            if (TextUtil.isValidEmailFormat(email) == false){
//                Toast.makeText(SignUpActivity.this, "Email格式錯誤",Toast.LENGTH_SHORT).show();
//            }
//            if (TextUtil.isPhoneLegal(phone) == false){
//                Toast.makeText(SignUpActivity.this, "手機號碼格式錯誤",Toast.LENGTH_SHORT).show();
//            }
        }
    };

    /** 判斷輸入欄是否空白 **/
    private void checkEmpty(){
        if (userName.isEmpty()){
            Toast.makeText(SignUpActivity.this, "用戶名稱欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (birth.isEmpty()){
            Toast.makeText(SignUpActivity.this, "出生日期欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (height.isEmpty()){
            Toast.makeText(SignUpActivity.this, "身高欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (weight.isEmpty()){
            Toast.makeText(SignUpActivity.this, "體重欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (checkedSex.isEmpty()){
            Toast.makeText(SignUpActivity.this, "性別欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (checkedSmoke.isEmpty()){
            Toast.makeText(SignUpActivity.this, "抽菸欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (checkedDia.isEmpty()){
            Toast.makeText(SignUpActivity.this, "糖尿病欄不得空白",Toast.LENGTH_SHORT).show();
        }
        if (checkedHbp.isEmpty()){
            Toast.makeText(SignUpActivity.this, "高血壓欄不得空白",Toast.LENGTH_SHORT).show();
        }
    }

}