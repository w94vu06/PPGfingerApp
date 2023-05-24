package com.example.myapplication.Util;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    /** 取消checkBox選取 **/
    public static void unCheck(List<CheckBox> checkBoxList){
        for (CheckBox checkBox : checkBoxList){
            checkBox.setChecked(false);
        }
    }

    public static String getOne(List<CheckBox> checkBoxList){
        String tag = "";
        for (CheckBox checkBox : checkBoxList){
            if (checkBox.getTag() == null){
                continue;
            }
            if (checkBox.isChecked()){
                tag = checkBox.getTag().toString();
                break;
            }
        }
        return tag;
    }

    public static List<String> getMany(List<CheckBox> checkBoxList){
        List<String> sb = new ArrayList<>();
        for (CheckBox checkBox : checkBoxList){
            if (checkBox.getTag()==null){
                continue;
            }
            if (checkBox.isChecked()){
                sb.add(checkBox.getTag().toString());
            }
        }
        return sb;
    }

}
