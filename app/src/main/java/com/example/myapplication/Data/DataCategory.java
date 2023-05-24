package com.example.myapplication.Data;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class DataCategory {
    private String img;
    private String cate;

    public DataCategory(String img, String cate){
        this.img = img;
        this.cate = cate;
    }

    public String getImg(){
        return img;
    }

    public String getCate(){
        return cate;
    }
}
