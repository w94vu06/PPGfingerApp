package com.example.myapplication.Data;

import java.util.ArrayList;

public class DataFeature {

    private final String name_feature;
    private final String val_feature;

    public DataFeature(String name, String val){
        this.name_feature = name;
        this.val_feature = val;
    }

    public String getName(){
        return name_feature;
    }

    public String getVal(){
        return val_feature.toString();
    }
    public static ArrayList<DataFeature> getList_Feature(){
        ArrayList<DataFeature> list = new ArrayList<>();
//        list.add(new DataFeature("", 1.2));

        return list;
    }
}