package com.example.myapplication.SignUpFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.Util.TextUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Info extends Fragment {

    private View view;
    private EditText edit_userName, edit_phone, edit_birth, edit_height, edit_weight, edit_waist;
    @BindViews({R.id.check_sexMale, R.id.check_sexFemale})
    List<CheckBox> radiosSex;

    private String userName, phone, birth, old, height, weight, waist, checkedSex = "";

    public interface DataReturn {
        public void getResult(HashMap<String, String> hashMap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        edit_userName = view.findViewById(R.id.edit_userName);
        edit_phone = view.findViewById(R.id.edit_phone);
        edit_birth = view.findViewById(R.id.edit_birth);
        edit_height = view.findViewById(R.id.edit_height);
        edit_weight = view.findViewById(R.id.edit_weight);
        edit_waist = view.findViewById(R.id.edit_waist);
        createMonthDialog(edit_birth);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.check_sexMale, R.id.check_sexFemale})
    void SexClick(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSex);
        checkBox.setChecked(true);
        checkedSex = CommonUtil.getOne(radiosSex);
    }

    /**
     * 出生日期欄點擊事件
     **/
    public void createMonthDialog(final EditText edt) {
        BirthPickDialog dialog = new BirthPickDialog(getActivity());

        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.showDialog();
            }
        });


        dialog.onDialogRespond = new BirthPickDialog.OnDialogRespond() {
            @Override
            public void onRespond(String selected) {
                edt.setText(selected);
            }
        };
    }

    private void getValue() {
        userName = edit_userName.getText().toString().trim();
        phone = edit_phone.getText().toString().trim();
        birth = edit_birth.getText().toString().trim();
        height = edit_height.getText().toString().trim();
        weight = edit_weight.getText().toString().trim();
        waist = edit_waist.getText().toString().trim();
        calAge();
    }

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

    public void sendValue(DataReturn dataReturn) {
        HashMap<String, String> infoHashMap = new HashMap<>();
        getValue();
        checkedSex = CommonUtil.getOne(radiosSex);

        infoHashMap.put("userName", userName);
        infoHashMap.put("phone", phone);
        infoHashMap.put("birth", birth);
        infoHashMap.put("old", old);
        infoHashMap.put("height", height);
        infoHashMap.put("weight", weight);
        if (waist == null) {
            infoHashMap.put("waist", "-1");
        } else {
            infoHashMap.put("waist", waist);
        }
        infoHashMap.put("checkedSex", checkedSex);

        dataReturn.getResult(infoHashMap);
    }
}

/**
 * 日期選擇Dialog
 **/
class BirthPickDialog {
    private Activity activity;
    OnDialogRespond onDialogRespond;
    private NumberPicker np_birthY, np_birthM, np_birthD;
    private Button btn_birthCancel, btn_birthDone;
    private DatePickerDialog datePickerDialog;

    public BirthPickDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        Dialog monthDialog = new Dialog(this.activity, R.style.MonthDialog);
        View contentView = LayoutInflater.from(this.activity).inflate(R.layout.dialog_birth, null);
        monthDialog.setContentView(contentView);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        params.width = activity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(params);
        monthDialog.getWindow().setGravity(Gravity.BOTTOM);
        monthDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        monthDialog.show();

        np_birthY = contentView.findViewById(R.id.np_birthY);
        np_birthM = contentView.findViewById(R.id.np_birthM);
        np_birthD = contentView.findViewById(R.id.np_birthD);
        btn_birthCancel = contentView.findViewById(R.id.btn_birthCancel);
        btn_birthDone = contentView.findViewById(R.id.btn_birthDone);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        np_birthY.setMaxValue(year);
        np_birthY.setMinValue(year - 150);
        np_birthY.setValue(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)));
        np_birthM.setMaxValue(12);
        np_birthM.setMinValue(1);
        np_birthM.setValue(Integer.parseInt(new SimpleDateFormat("MM").format(date)));

        np_birthY.setOnValueChangedListener((picker, oldVal, newVal) -> setDaysInDayPicker(newVal, np_birthM.getValue()));
        np_birthM.setOnValueChangedListener((picker, oldVal, newVal) -> setDaysInDayPicker(np_birthY.getValue(), newVal));

        np_birthY.setValue(year);
        np_birthM.setValue(month);
        np_birthD.setValue(day);

        // 設置當前月份的天數
        setDaysInDayPicker(year, month);

        btn_birthCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthDialog.dismiss();
            }
        });
        btn_birthDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.format("%04d-%02d-%02d", np_birthY.getValue(), np_birthM.getValue(), np_birthD.getValue());
                onDialogRespond.onRespond(s);
                monthDialog.dismiss();
            }
        });
    }

    private void setDaysInDayPicker(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        np_birthD.setMaxValue(maxDay);
        np_birthD.setMinValue(1);
    }

    interface OnDialogRespond {
        void onRespond(String selected);
    }
}