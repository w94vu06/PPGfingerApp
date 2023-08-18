package com.example.myapplication.Data;

import androidx.lifecycle.ViewModel;

public class DataRecord extends ViewModel {
    private String date,heartbeat,index,brain,preloadData;

    public DataRecord() {
    }

    public DataRecord(String date,String heartbeat,String index,String brain){
        this.date = date;
        this.heartbeat = heartbeat;
        this.index = index;
        this.brain = brain;
    }
    public void setData(String newData) {
        preloadData = newData;
    }

    public String getData() {
        return preloadData;
    }

}
