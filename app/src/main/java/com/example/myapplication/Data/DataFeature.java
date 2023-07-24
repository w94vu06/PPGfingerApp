package com.example.myapplication.Data;

public class DataFeature {

    private final String name_feature;
    private final Double val_feature;

    public DataFeature(String name, Double val){
        this.name_feature = name;
        this.val_feature = val;
    }

    public String getName(){
        return name_feature;
    }

    public String getVal(){
        return val_feature.toString();
    }
}