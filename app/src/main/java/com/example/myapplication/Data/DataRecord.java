package com.example.myapplication.Data;

public class DataRecord {
    private String date,time;

    public DataRecord(String date,String time){
        this.date = date;
        this.time = time;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }
}
