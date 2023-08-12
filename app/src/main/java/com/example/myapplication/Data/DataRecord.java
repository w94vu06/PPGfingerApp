package com.example.myapplication.Data;

import androidx.lifecycle.ViewModel;

public class DataRecord extends ViewModel {
    private String date,time,preloadData;

    public DataRecord() {
    }

    public DataRecord(String date,String time){
        this.date = date;
        this.time = time;
    }
    public void setData(String newData) {
        preloadData = newData;
    }

    public String getData() {
        return preloadData;
    }
//    public String getDate(){
//        return date;
//    }
//
//    public String getTime(){
//        return time;
//    }

}
