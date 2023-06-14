package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class CheckInternetDialog {
    private Context context;

    public  CheckInternetDialog(Context context) {
        this.context = context;
    }

    public void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isAvailable()) {
            showInternetDialog();
        }
    }

    private void showInternetDialog() {
        AlertDialog.Builder internetDialog = new AlertDialog.Builder(context);
        internetDialog.setMessage("您的設備未連接網路，請確認網路狀態，重新連線後再試一次");
        internetDialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkInternet();
            }
        });
        internetDialog.setCancelable(false);
        internetDialog.show();
    }
}
