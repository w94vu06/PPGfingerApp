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

public class Habit extends Fragment {

    private View view;
    @BindViews({R.id.check_smokeY, R.id.check_smokeN})
    List<CheckBox> radiosSmoke;
    @BindViews({R.id.check_drinkY, R.id.check_drinkN})
    List<CheckBox> radiosDrink;
    @BindViews({R.id.check_sportY, R.id.check_sportN})
    List<CheckBox> radiosSport;
    @BindViews({R.id.check_sleep1, R.id.check_sleep2, R.id.check_sleep3})
    List<CheckBox> radiosSleep;
    String checkedSmoke,checkedDrink,checkedSport,checkedSleep = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_habit, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    /** 吸菸單選 **/
    @OnClick({R.id.check_smokeY, R.id.check_smokeN})
    void changeSmoke(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSmoke);
        checkBox.setChecked(true);
        checkedSmoke = CommonUtil.getOne(radiosSmoke);
    }

    /** 喝酒單選 **/
    @OnClick({R.id.check_drinkY, R.id.check_drinkN})
    void changeDrink(CheckBox checkBox) {
        CommonUtil.unCheck(radiosDrink);
        checkBox.setChecked(true);
        checkedDrink = CommonUtil.getOne(radiosDrink);
    }

    /** 運動單選 **/
    @OnClick({R.id.check_sportY, R.id.check_sportN})
    void changeSport(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSport);
        checkBox.setChecked(true);
        checkedSport = CommonUtil.getOne(radiosSport);
    }

    /** 睡眠單選 **/
    @OnClick({R.id.check_sleep1, R.id.check_sleep2, R.id.check_sleep3})
    void changeSleep(CheckBox checkBox) {
        CommonUtil.unCheck(radiosSleep);
        checkBox.setChecked(true);
        checkedSleep = CommonUtil.getOne(radiosSleep);
    }
}