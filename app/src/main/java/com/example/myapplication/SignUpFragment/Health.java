package com.example.myapplication.SignUpFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SignUpActivity;
import com.example.myapplication.Util.CommonUtil;


import java.util.HashMap;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Health extends Fragment {

    private View view;
    @BindViews({R.id.check_heartY, R.id.check_heartN})
    List<CheckBox> radiosHeart;
    @BindViews({R.id.check_hbpY, R.id.check_hbpN})
    List<CheckBox> radiosHbp;
    @BindViews({R.id.check_sbp1, R.id.check_sbp2, R.id.check_sbp3})
    List<CheckBox> radiosSbp;
    @BindViews({R.id.check_dbp1, R.id.check_dbp2, R.id.check_dbp3})
    List<CheckBox> radiosDbp;
    @BindViews({R.id.check_diaN, R.id.check_dia0, R.id.check_dia1, R.id.check_dia2})
    List<CheckBox> radiosDia;
    @BindViews({R.id.check_unit1, R.id.check_unit2})
    List<CheckBox> radiosUnit;
    @BindViews({R.id.check_empty1, R.id.check_empty2, R.id.check_empty3})
    List<CheckBox> radiosEmpty;
    @BindViews({R.id.check_twohrs1, R.id.check_twohrs2, R.id.check_twohrs3, R.id.check_twohrs4})
    List<CheckBox> radiosTwohrs;
    @BindViews({R.id.check_medicine1, R.id.check_medicine2, R.id.check_medicine3})
    List<CheckBox> radiosMedicine;
    @BindViews({R.id.check_low1, R.id.check_low2})
    List<CheckBox> radiosLow;
    @BindViews({R.id.check_familyN, R.id.check_familyHbp, R.id.check_familyDia, R.id.check_familyHlp, R.id.check_familyDepression})
    List<CheckBox> radiosFamily;
    @BindViews({R.id.check_covidY, R.id.check_covidN})
    List<CheckBox> radiosCovid;
    @BindViews({R.id.check_vaccineY, R.id.check_vaccineN})
    List<CheckBox> radiosVaccine;
    String checkedHeart, checkedHbp, checkedSbp, checkedDbp, checkedDia, checkedunit, checkedEmpty, checkedTwohrs,
            checkedMedicine, checkedLow, checkedCovid, checkedVaccine = "";
    private List<String> checkedFamily;
    private String checkedFamilyString;
    //收縮壓
    TextView txt_sbp;
    CheckBox check_sbp1, check_sbp2, check_sbp3;
    //舒張壓
    TextView txt_dbp;
    CheckBox check_dbp1, check_dbp2, check_dbp3;
    //早晨空腹、餐後兩小、用藥
    TextView txt_empty,txt_twohrs, txt_medicine;
    CheckBox check_empty1,check_empty2,check_empty3, check_empty4;
    CheckBox check_twohrs1,check_twohrs2,check_twohrs3, check_twohrs4;
    CheckBox check_medicine1,check_medicine2, check_medicine3;
    CheckBox check_familyN,check_familyHbp, check_familyDia, check_familyHlp,check_familyDepression;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface DataReturn {
        public void getResult(HashMap<String, String> hashMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health, container, false);
        ButterKnife.bind(this, view);

        checkedHeart = CommonUtil.getOne(radiosHeart);
        setRadiosHbp();
        setRadiosDia();
        setUnit();
//        setRadiosFamily();
        return view;
    }
    //高血壓為否，就無法選擇收縮壓及舒張壓
    public void setRadiosHbp() {
        checkedHbp = CommonUtil.getOne(radiosHbp);

        txt_sbp = view.findViewById(R.id.txt_sbp);
        check_sbp1 = view.findViewById(R.id.check_sbp1);
        check_sbp2 = view.findViewById(R.id.check_sbp2);
        check_sbp3 = view.findViewById(R.id.check_sbp3);

        txt_dbp = view.findViewById(R.id.txt_dbp);
        check_dbp1 = view.findViewById(R.id.check_dbp1);
        check_dbp2 = view.findViewById(R.id.check_dbp2);
        check_dbp3 = view.findViewById(R.id.check_dbp3);
        // 收縮壓
        if (Integer.parseInt(checkedHbp) == 0) {
            check_sbp1.setEnabled(false);
            check_sbp2.setEnabled(false);
            check_sbp3.setEnabled(false);
            txt_sbp.setAlpha(0.5f);
            check_sbp1.setAlpha(0.5f);
            check_sbp2.setAlpha(0.5f);
            check_sbp3.setAlpha(0.5f);
            checkedSbp = "-1";
        } else if (Integer.parseInt(checkedHbp) == 1) {
            check_sbp1.setEnabled(true);
            check_sbp2.setEnabled(true);
            check_sbp3.setEnabled(true);
            txt_sbp.setAlpha(1f);
            check_sbp1.setAlpha(1f);
            check_sbp2.setAlpha(1f);
            check_sbp3.setAlpha(1f);
        }
        // 舒張壓
        if (Integer.parseInt(checkedHbp) == 0) {
            check_dbp1.setEnabled(false);
            check_dbp2.setEnabled(false);
            check_dbp3.setEnabled(false);
            txt_dbp.setAlpha(0.5f);
            check_dbp1.setAlpha(0.5f);
            check_dbp2.setAlpha(0.5f);
            check_dbp3.setAlpha(0.5f);
            checkedDbp = "-1";
        } else if (Integer.parseInt(checkedHbp) == 1) {
            check_dbp1.setEnabled(true);
            check_dbp2.setEnabled(true);
            check_dbp3.setEnabled(true);
            txt_dbp.setAlpha(1f);
            check_dbp1.setAlpha(1f);
            check_dbp2.setAlpha(1f);
            check_dbp3.setAlpha(1f);
        }
    }

    //沒糖尿病，就無法選擇早晨空腹、餐後兩小、用藥
    public void setRadiosDia() {
        checkedDia = CommonUtil.getOne(radiosDia);

        txt_empty = view.findViewById(R.id.txt_empty);
        check_empty1 = view.findViewById(R.id.check_empty1);
        check_empty2 = view.findViewById(R.id.check_empty2);
        check_empty3 = view.findViewById(R.id.check_empty3);
        check_empty4 = view.findViewById(R.id.check_empty4);

        txt_twohrs = view.findViewById(R.id.txt_twohrs);
        check_twohrs1 = view.findViewById(R.id.check_twohrs1);
        check_twohrs2 = view.findViewById(R.id.check_twohrs2);
        check_twohrs3 = view.findViewById(R.id.check_twohrs3);
        check_twohrs4 = view.findViewById(R.id.check_twohrs4);

        txt_medicine = view.findViewById(R.id.txt_medicine);
        check_medicine1 = view.findViewById(R.id.check_medicine1);
        check_medicine2 = view.findViewById(R.id.check_medicine2);
        check_medicine3 = view.findViewById(R.id.check_medicine3);

        if (Integer.parseInt(checkedDia) == 0) {
            //早晨空腹
            check_empty1.setEnabled(false);
            check_empty2.setEnabled(false);
            check_empty3.setEnabled(false);
            check_empty4.setEnabled(false);
            txt_empty.setAlpha(0.5f);
            check_empty1.setAlpha(0.5f);
            check_empty2.setAlpha(0.5f);
            check_empty3.setAlpha(0.5f);
            check_empty4.setAlpha(0.5f);
            checkedEmpty = "-1";
            //餐後兩小
            check_twohrs1.setEnabled(false);
            check_twohrs2.setEnabled(false);
            check_twohrs3.setEnabled(false);
            check_twohrs4.setEnabled(false);
            txt_twohrs.setAlpha(0.5f);
            check_twohrs1.setAlpha(0.5f);
            check_twohrs2.setAlpha(0.5f);
            check_twohrs3.setAlpha(0.5f);
            check_twohrs4.setAlpha(0.5f);
            checkedTwohrs = "-1";
            //用藥
            check_medicine1.setEnabled(false);
            check_medicine2.setEnabled(false);
            check_medicine3.setEnabled(false);
            txt_medicine.setAlpha(0.5f);
            check_medicine1.setAlpha(0.5f);
            check_medicine2.setAlpha(0.5f);
            check_medicine3.setAlpha(0.5f);
            checkedMedicine = "-1";
        } else {
            //早晨空腹
            check_empty1.setEnabled(true);
            check_empty2.setEnabled(true);
            check_empty3.setEnabled(true);
            check_empty4.setEnabled(true);
            txt_empty.setAlpha(1f);
            check_empty1.setAlpha(1f);
            check_empty2.setAlpha(1f);
            check_empty3.setAlpha(1f);
            check_empty4.setAlpha(1f);
            //餐後兩小
            check_twohrs1.setEnabled(true);
            check_twohrs2.setEnabled(true);
            check_twohrs3.setEnabled(true);
            check_twohrs4.setEnabled(true);
            txt_twohrs.setAlpha(1f);
            check_twohrs1.setAlpha(1f);
            check_twohrs2.setAlpha(1f);
            check_twohrs3.setAlpha(1f);
            check_twohrs4.setAlpha(1f);
            //用藥
            check_medicine1.setEnabled(true);
            check_medicine2.setEnabled(true);
            check_medicine3.setEnabled(true);
            txt_medicine.setAlpha(1f);
            check_medicine1.setAlpha(1f);
            check_medicine2.setAlpha(1f);
            check_medicine3.setAlpha(1f);
        }
    }

    public void setUnit() {
        checkedunit = CommonUtil.getOne(radiosUnit);

        txt_empty = view.findViewById(R.id.txt_empty);
        check_empty1 = view.findViewById(R.id.check_empty1);
        check_empty2 = view.findViewById(R.id.check_empty2);
        check_empty3 = view.findViewById(R.id.check_empty3);
        check_empty4 = view.findViewById(R.id.check_empty4);

        txt_twohrs = view.findViewById(R.id.txt_twohrs);
        check_twohrs1 = view.findViewById(R.id.check_twohrs1);
        check_twohrs2 = view.findViewById(R.id.check_twohrs2);
        check_twohrs3 = view.findViewById(R.id.check_twohrs3);
        check_twohrs4 = view.findViewById(R.id.check_twohrs4);

        if (Integer.parseInt(checkedunit) == 1) {
            check_empty1.setText("70-110");
            check_empty2.setText("111-130");
            check_empty3.setText("131-150");
            check_empty4.setText(">150");

            check_twohrs1.setText("70-130");
            check_twohrs2.setText("131-158");
            check_twohrs3.setText("159-200");
            check_twohrs4.setText(">200");
        } else {
            check_empty1.setText("3.9-6.1");
            check_empty2.setText("6.2-7.2");
            check_empty3.setText("7.3-8.2");
            check_empty4.setText(">8.2");

            check_twohrs1.setText("3.9-7.2");
            check_twohrs2.setText("7.3-8.8");
            check_twohrs3.setText("8.9-11.1");
            check_twohrs4.setText(">11.1");
        }
    }

    public void setRadiosFamily() {
        checkedFamily = CommonUtil.getMany(radiosFamily);

        check_familyN = view.findViewById(R.id.check_familyN);
        check_familyHbp = view.findViewById(R.id.check_familyHbp);
        check_familyDia = view.findViewById(R.id.check_familyDia);
        check_familyHlp = view.findViewById(R.id.check_familyHlp);
        check_familyDepression = view.findViewById(R.id.check_familyDepression);

        check_familyN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_familyHbp.setChecked(false);
                check_familyDia.setChecked(false);
                check_familyHlp.setChecked(false);
                check_familyDepression.setChecked(false);
            }
        });
        check_familyHbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_familyN.setChecked(false);
            }
        });
        check_familyDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_familyN.setChecked(false);
            }
        });
        check_familyHlp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_familyN.setChecked(false);
            }
        });
        check_familyDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_familyN.setChecked(false);
            }
        });
        Log.d("whyFam", "setRadiosFamily: "+checkedFamily);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 心血管疾病單選
     **/
    @OnClick({R.id.check_heartY, R.id.check_heartN})
    void changeHeart(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHeart);
        checkBox.setChecked(true);
        checkedHeart = CommonUtil.getOne(radiosHeart);
    }

    /**
     * 高血壓單選
     **/
    @OnClick({R.id.check_hbpY, R.id.check_hbpN})
    void changeHbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHbp);
        checkBox.setChecked(true);
        checkedHbp = CommonUtil.getOne(radiosHbp);
        setRadiosHbp();
    }

    /**
     * 收縮壓單選
     **/
    @OnClick({R.id.check_sbp1, R.id.check_sbp2, R.id.check_sbp3})
    void changeSbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSbp);
        checkBox.setChecked(true);
        checkedSbp = CommonUtil.getOne(radiosSbp);
    }

    /**
     * 舒張壓單選
     **/
    @OnClick({R.id.check_dbp1, R.id.check_dbp2, R.id.check_dbp3})
    void changeDbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDbp);
        checkBox.setChecked(true);
        checkedDbp = CommonUtil.getOne(radiosDbp);
    }

    /**
     * 糖尿病單選
     **/
    @OnClick({R.id.check_diaN, R.id.check_dia0, R.id.check_dia1, R.id.check_dia2})
    void changeDia(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDia);
        checkBox.setChecked(true);
        checkedDia = CommonUtil.getOne(radiosDia);
        setRadiosDia();
    }

    /**
     * 血糖單位單選
     **/
    @OnClick({R.id.check_unit1, R.id.check_unit2})
    void changeUnit(CheckBox checkBox) {
        CommonUtil.unCheck(radiosUnit);
        checkBox.setChecked(true);
        checkedunit = CommonUtil.getOne(radiosUnit);
        setUnit();
    }

    /**
     * 早晨空副單選
     **/
    @OnClick({R.id.check_empty1, R.id.check_empty2, R.id.check_empty3})
    void changeEmpty(CheckBox checkBox) {
        CommonUtil.unCheck(radiosEmpty);
        checkBox.setChecked(true);
        checkedEmpty = CommonUtil.getOne(radiosEmpty);
    }

    /**
     * 餐後單選
     **/
    @OnClick({R.id.check_twohrs1, R.id.check_twohrs2, R.id.check_twohrs3, R.id.check_twohrs4})
    void changeTwohrs(CheckBox checkBox) {
        CommonUtil.unCheck(radiosTwohrs);
        checkBox.setChecked(true);
        checkedTwohrs = CommonUtil.getOne(radiosTwohrs);
    }

    /**
     * 用藥單選
     **/
    @OnClick({R.id.check_medicine1, R.id.check_medicine2, R.id.check_medicine3})
    void changeMedicine(CheckBox checkBox) {
        CommonUtil.unCheck(radiosMedicine);
        checkBox.setChecked(true);
        checkedMedicine = CommonUtil.getOne(radiosMedicine);
    }

    /**
     * 低血糖單選
     **/
    @OnClick({R.id.check_low1, R.id.check_low2})
    void changeLow(CheckBox checkBox) {
        CommonUtil.unCheck(radiosLow);
        checkBox.setChecked(true);
        checkedLow = CommonUtil.getOne(radiosLow);
    }

    /**
     * 家族病史複選
     **/
    @OnClick({R.id.check_familyN, R.id.check_familyHbp, R.id.check_familyDia, R.id.check_familyHlp, R.id.check_familyDepression})
    void changeFamily(CheckBox checkBox) {
        checkedFamily = CommonUtil.getMany(radiosFamily);

        for (String family : checkedFamily) {
            checkedFamilyString += family + ",";
        }
//        setRadiosFamily();
    }

    /**
     * Covid單選
     **/
    @OnClick({R.id.check_covidY, R.id.check_covidN})
    void changeCovid(CheckBox checkBox) {
        CommonUtil.unCheck(radiosCovid);
        checkBox.setChecked(true);
        checkedCovid = CommonUtil.getOne(radiosCovid);
    }

    /**
     * 疫苗單選
     **/
    @OnClick({R.id.check_vaccineY, R.id.check_vaccineN})
    void changeVaccine(CheckBox checkBox) {
        CommonUtil.unCheck(radiosVaccine);
        checkBox.setChecked(true);
        checkedVaccine = CommonUtil.getOne(radiosVaccine);
    }

    public void sendValue(DataReturn dataReturn) {

        HashMap<String, String> healthHashMap = new HashMap<>();
        healthHashMap.put("checkedHeart", checkedHeart != null ? checkedHeart : "-1");
        healthHashMap.put("checkedHbp", checkedHbp != null ? checkedHbp : "-1");
        healthHashMap.put("checkedSbp", checkedSbp != null ? checkedSbp : "-1");
        healthHashMap.put("checkedDbp", checkedDbp != null ? checkedDbp : "-1");
        healthHashMap.put("checkedDia", checkedDia != null ? checkedDia : "-1");
        healthHashMap.put("checkedunit", checkedunit != null ? checkedunit : "-1");
        healthHashMap.put("checkedEmpty", checkedEmpty != null ? checkedEmpty : "-1");
        healthHashMap.put("checkedTwohrs", checkedTwohrs != null ? checkedTwohrs : "-1");
        healthHashMap.put("checkedMedicine", checkedMedicine != null ? checkedMedicine : "-1");
        healthHashMap.put("checkedFamily", checkedFamilyString != null ? checkedFamilyString : "-1");
        healthHashMap.put("checkedLow", checkedLow != null ? checkedLow : "-1");
        healthHashMap.put("checkedCovid", checkedCovid != null ? checkedCovid : "-1");
        healthHashMap.put("checkedVaccine", checkedVaccine != null ? checkedVaccine : "-1");

        dataReturn.getResult(healthHashMap);

    }
}