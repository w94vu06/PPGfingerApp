package com.example.myapplication.SignUpFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Util.CommonUtil;


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
    String checkedHeart,checkedHbp,checkedSbp,checkedDbp,checkedDia,checkedunit,checkedEmpty,checkedTwohrs,
            checkedMedicine,checkedLow,checkedCovid,checkedVaccine = "";
    private List<String> checkedFamily;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface DataReturn {
        public void getResult(String value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    /** 心血管疾病單選 **/
    @OnClick({R.id.check_heartY, R.id.check_heartN})
    void changeHeart(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHeart);
        checkBox.setChecked(true);
        checkedHeart = CommonUtil.getOne(radiosHeart);
    }

    /** 高血壓單選 **/
    @OnClick({R.id.check_hbpY, R.id.check_hbpN})
    void changeHbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosHbp);
        checkBox.setChecked(true);
        checkedHbp = CommonUtil.getOne(radiosHbp);
    }

    /** 收縮壓單選 **/
    @OnClick({R.id.check_sbp1, R.id.check_sbp2, R.id.check_sbp3})
    void changeSbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSbp);
        checkBox.setChecked(true);
        checkedSbp = CommonUtil.getOne(radiosSbp);
    }

    /** 舒張壓單選 **/
    @OnClick({R.id.check_dbp1, R.id.check_dbp2, R.id.check_dbp3})
    void changeDbp(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDbp);
        checkBox.setChecked(true);
        checkedDbp = CommonUtil.getOne(radiosDbp);
    }

    /** 糖尿病單選 **/
    @OnClick({R.id.check_diaN, R.id.check_dia0, R.id.check_dia1, R.id.check_dia2})
    void changeDia(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDia);
        checkBox.setChecked(true);
        checkedDia = CommonUtil.getOne(radiosDia);
    }

    /** 血糖單位單選 **/
    @OnClick({R.id.check_unit1, R.id.check_unit2})
    void changeUnit(CheckBox checkBox) {
        CommonUtil.unCheck(radiosUnit);
        checkBox.setChecked(true);
        checkedunit = CommonUtil.getOne(radiosUnit);
    }

    /** 早晨空副單選 **/
    @OnClick({R.id.check_empty1, R.id.check_empty2, R.id.check_empty3})
    void changeEmpty(CheckBox checkBox) {
        CommonUtil.unCheck(radiosEmpty);
        checkBox.setChecked(true);
        checkedEmpty = CommonUtil.getOne(radiosEmpty);
    }

    /** 餐後單選 **/
    @OnClick({R.id.check_twohrs1, R.id.check_twohrs2, R.id.check_twohrs3, R.id.check_twohrs4})
    void changeTwohrs(CheckBox checkBox) {
        CommonUtil.unCheck(radiosTwohrs);
        checkBox.setChecked(true);
        checkedTwohrs = CommonUtil.getOne(radiosTwohrs);
    }

    /** 用藥單選 **/
    @OnClick({R.id.check_medicine1, R.id.check_medicine2, R.id.check_medicine3})
    void changeMedicine(CheckBox checkBox) {
        CommonUtil.unCheck(radiosMedicine);
        checkBox.setChecked(true);
        checkedMedicine = CommonUtil.getOne(radiosMedicine);
    }

    /** 低血糖單選 **/
    @OnClick({R.id.check_low1, R.id.check_low2})
    void changeLow(CheckBox checkBox) {
        CommonUtil.unCheck(radiosLow);
        checkBox.setChecked(true);
        checkedLow = CommonUtil.getOne(radiosLow);
    }

    /** 家族病史複選 **/
    @OnClick({R.id.check_familyN, R.id.check_familyHbp, R.id.check_familyDia, R.id.check_familyHlp, R.id.check_familyDepression})
    void changeFamily(CheckBox checkBox) {
        checkedFamily = CommonUtil.getMany(radiosFamily);
    }

    /** Covid單選 **/
    @OnClick({R.id.check_covidY, R.id.check_covidN})
    void changeCovid(CheckBox checkBox) {
        CommonUtil.unCheck(radiosCovid);
        checkBox.setChecked(true);
        checkedCovid = CommonUtil.getOne(radiosCovid);
    }

    /** 疫苗單選 **/
    @OnClick({R.id.check_vaccineY, R.id.check_vaccineN})
    void changeVaccine(CheckBox checkBox) {
        CommonUtil.unCheck(radiosVaccine);
        checkBox.setChecked(true);
        checkedVaccine = CommonUtil.getOne(radiosVaccine);
    }

//    public void sendValue(Information.DataReturn dataReturn){
//        checkedCovid = CommonUtil.getOne(radiosCovid);
//        String value = checkedCovid;
//        dataReturn.getResult(value);
//    }
}