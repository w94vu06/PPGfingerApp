package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ControlMariaDB {
    private static final OkHttpClient client = new OkHttpClient();
    Handler mHandler = new MHandler();
    Handler resHandler = new ResHandler();
    String serverUrl = "http://192.168.2.104:5000/"; //公司
//    String serverUrl = "http://192.168.0.102:5000/"; //家裡
    String calServerUrl = "http://192.168.2.97:8090";//計算用server
    private MariaDBCallback mCallback;
    private static final int MSG_REGISTER = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_READ = 3;
    private static final int MSG_DELETE = 4;
    private static final int MSG_ID_SAVE = 5;
    private static final int MSG_ID_READ = 6;
    public ControlMariaDB(MariaDBCallback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * 登入
     **/
    public void userLogin(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 登入
                Request loginRequest = new Request.Builder()
                        .url(serverUrl + "login")
                        .post(requestBody)
                        .build();
                try {
                    // 發送登入請求
                    Response loginResponse = client.newCall(loginRequest).execute();
                    if (loginResponse.isSuccessful()) {
                        // 登入回應
                        String loginRes = Objects.requireNonNull(loginResponse.body()).string();
                        // 0:登入失敗、1:登入成功
                        Log.d("loginRes", "getLoginRes: " + loginRes);

                        Message resMsg = Message.obtain();
                        resMsg.what = MSG_LOGIN;
                        resMsg.obj = loginRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        throw new IOException("Unexpected code " + loginResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 註冊
     **/
    public void userRegister(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 註冊
                Request registerRequest = new Request.Builder()
                        .url(serverUrl + "register")
                        .post(requestBody)
                        .build();
                try {
                    // 發送註冊請求
                    Response registerResponse = client.newCall(registerRequest).execute();
                    if (registerResponse.isSuccessful()) {
                        // 註冊回應
                        String registerRes = Objects.requireNonNull(registerResponse.body()).string();
                        // 0:註冊失敗、1:註冊成功、2:使用者已存在
                        Message resMsg = Message.obtain();
                        resMsg.what = MSG_REGISTER;
                        resMsg.obj = registerRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        throw new IOException("Unexpected code " + registerResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 讀取
     **/
    public void userRead(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 註冊
                Request readRequest = new Request.Builder()
                        .url(serverUrl + "read")
                        .post(requestBody)
                        .build();
                try {
                    // 發送讀取請求
                    Response readResponse = client.newCall(readRequest).execute();
                    if (readResponse.isSuccessful()) {
                        // 讀取回應
                        String readRes = Objects.requireNonNull(readResponse.body()).string();
                        // 0:讀取失敗
                        Message resMsg = Message.obtain();
                        resMsg.what = MSG_READ;
                        resMsg.obj = readRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        throw new IOException("Unexpected code " + readResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 用userId存
     **/
    public void userIdSave(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 註冊
                Request readRequest = new Request.Builder()
                        .url(serverUrl + "save")
                        .post(requestBody)
                        .build();
                try {
                    // 發送讀取請求
                    Response readResponse = client.newCall(readRequest).execute();
                    if (readResponse.isSuccessful()) {
                        // 讀取回應
                        String readRes = Objects.requireNonNull(readResponse.body()).string();
                        // 0:讀取失敗
                        Message resMsg = Message.obtain();
                        resMsg.what = MSG_ID_SAVE;
                        resMsg.obj = readRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        throw new IOException("Unexpected code " + readResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 用userId讀
     **/
    public void userIdRead(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 註冊
                Request readRequest = new Request.Builder()
                        .url(serverUrl + "readData")
                        .post(requestBody)
                        .build();
                try {
                    // 發送讀取請求
                    Response readResponse = client.newCall(readRequest).execute();
                    if (readResponse.isSuccessful()) {
                        // 讀取回應
                        String readRes = Objects.requireNonNull(readResponse.body()).string();
                        // 0:讀取失敗
                        Message resMsg = Message.obtain();
                        resMsg.what = MSG_ID_READ;
                        resMsg.obj = readRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        throw new IOException("Unexpected code " + readResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * CRUD訊息回傳
     **/
    class ResHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message resMsg) {
            super.handleMessage(resMsg);

            switch (resMsg.what) {
                case MSG_LOGIN:
                case MSG_REGISTER:
                case MSG_READ:
                case MSG_DELETE:
                case MSG_ID_READ:
                    mCallback.onResult(resMsg.obj.toString());
                    break;
                case MSG_ID_SAVE:
                    mCallback.onSave(resMsg.obj.toString());
                    break;
            }
        }
    }

    public void jsonUploadToServer(String jsonString) {
        /**
         * 連接伺服器運算
         **/
        new Thread() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonString);
                Request request = new Request.Builder()
                        .url(calServerUrl)//伺服器
//                        .url("http://192.168.2.110:5000")//測試服
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Message msg = Message.obtain();
                    String res;
                    if (!response.isSuccessful()) {
                        res = "fail";
                    } else {
                        res = Objects.requireNonNull(response.body()).string();
                    }
                    msg.obj = res;
                    Log.d("serverRes", "getServerRes: " + res);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    String res = "fail";
                    Message msg = Message.obtain();
                    msg.obj = res;
                    Log.d("serverRes", "getServerRes: " + res);
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    public void testServer(String jsonString) {
        /**
         * 連接伺服器運算
         **/
        new Thread() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonString);
                Request request = new Request.Builder()
                        .url(calServerUrl)//伺服器
//                        .url("http://192.168.2.110:5000")//測試服
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Message msg = Message.obtain();
                    if (!response.isSuccessful()) {
                        String res;
                        res = "open";
                        msg.obj = res;
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    String res = "fail";
                    Message msg = Message.obtain();
                    msg.obj = res;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 運算結果回傳
     **/
    class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mCallback.onResult(msg.obj.toString());
        }
    }
}

