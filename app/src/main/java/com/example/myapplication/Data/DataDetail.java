package com.example.myapplication.Data;

import java.util.ArrayList;

public class DataDetail {

    public static final int PARENT_ITEM = 0;
    public static final int CHILD_ITEM = 1;
    private String title;
    private String value;
    private String meaning;

    public DataDetail(String title, String value){
        this.title = title;
        this.value = value;
    }

    public String getTitle(){
        return title;
    }

    public String getValue(){
        return value;
    }

    public static ArrayList<DataDetail> getList_AI(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("心臟年紀 Heart Age","35"));
        list.add(new DataDetail("情緒AI Emotion AI","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_Emotional(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("活力指數","35"));
        list.add(new DataDetail("壓力","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_Body_AI(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("舒張壓","35"));
        list.add(new DataDetail("收縮壓","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_RSP(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("RSP","35"));
        list.add(new DataDetail("PRQ","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_Disease(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("AF","35"));
        list.add(new DataDetail("PVC","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_Body_index(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("健康分數","35"));
        return list;
    }

    public static ArrayList<DataDetail> getList_HRV(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("SDNN","35"));
        list.add(new DataDetail("RMSSD","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_Heartbeat(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("MaxValue","35"));
        list.add(new DataDetail("MinValue","155"));
        return list;
    }

    public static ArrayList<DataDetail> getList_signal(){
        ArrayList<DataDetail> list = new ArrayList<>();
        list.add(new DataDetail("紅三燈","35"));
        list.add(new DataDetail("訊號分數","155"));
        return list;
    }
}
