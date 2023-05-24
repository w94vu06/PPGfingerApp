package com.example.myapplication.Data;

import java.util.ArrayList;
import java.util.List;

public class DataImage {

    public String url;
    public Integer imageRes;
    public int viewType;
    public String title;

    public DataImage(String url, String title, int viewType) {
        this.url = url;
        this.title = title;
        this.viewType = viewType;
    }

    public static List<DataImage> getTestData3() {
        List<DataImage> list = new ArrayList<>();
        list.add(new DataImage("https://i.ytimg.com/vi/HuhkUIYRryA/maxresdefault.jpg", null, 1));
        list.add(new DataImage("https://mrmad.com.tw/wp-content/uploads/2021/03/apple-iphone-12-cook-and-fumble.jpg", null, 1));
        list.add(new DataImage("https://img.onl/sensKg",null,1));
        return list;
    }
}
