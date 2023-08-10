package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
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
    String serverUrl = "https://7689-59-126-42-176.ngrok-free.app/"; //公司
//    String serverUrl = "http://192.168.0.102:5000/"; //家裡

    //    String calServerUrl = "http://192.168.2.97:8090";//計算用server
    String calServerUrl = "https://7689-59-126-42-176.ngrok-free.app/";//計算用server
    private final MariaDBCallback mCallback;
    private static final int MSG_REGISTER = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_READ = 3;
    private static final int MSG_DELETE = 4;
    private static final int MSG_ID_SAVE = 5;
    private static final int MSG_ID_DATE_READ_DATA = 6;
    private static final int MSG_AI_CAL = 7;
    private static final int MSG_TEST = 8;

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
    public void IdAndDateReadData(String jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonObject);

                // 註冊
                Request readRequest = new Request.Builder()
                        .url(serverUrl + "idAndDate")
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
                        resMsg.what = MSG_ID_DATE_READ_DATA;
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
                    mCallback.onResult(resMsg.obj.toString());
                    break;
                case MSG_DELETE:
                case MSG_ID_DATE_READ_DATA: //讀取資料庫裡符合ID的資料
                case MSG_ID_SAVE: //儲存量測資料到資料庫
                    mCallback.onSave(resMsg.obj.toString());
                    break;
            }
        }
    }

    /**
     * AI伺服器
     */
    public void jsonUploadToServer(String jsonString) {
        new Thread(() -> {
            try {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonString);
                Request request = new Request.Builder()
//                        .url(calServerUrl + "calculate")
                        .url("http://59.126.42.176:8090")
//                        .url("http://192.168.2.97:8090")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String res = response.isSuccessful() ? Objects.requireNonNull(response.body()).string() : "fail";
                    Message msg = Message.obtain();
                    msg.what = MSG_AI_CAL;
                    msg.obj = res;
                    Log.d("serverRes", "getServerRes: " + res);
                    mHandler.sendMessage(msg);
                    assert response.body() != null;
                    response.body().close();
                }
            } catch (IOException e) {
                String res = "fail";
                Message msg = Message.obtain();
                msg.what = MSG_AI_CAL;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void testServer(String jsonString) {
        new Thread() {
            @Override
            public void run() {
                try {
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    RequestBody requestBody = RequestBody.create(mediaType, jsonString);
                    Request request = new Request.Builder()
                            .url(calServerUrl + "testServer")
//                        .url(calServerUrl)
                            .post(requestBody)
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        String res = Objects.requireNonNull(response.body()).string();

                        Message msg = Message.obtain();
                        msg.what = MSG_TEST;
                        msg.obj = res;
                        mHandler.sendMessage(msg);
                        assert response.body() != null;
                        response.body().close();
                    }
                } catch (IOException e) {
                    String res = "fail";
                    Message msg = Message.obtain();
                    msg.what = MSG_TEST;
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
            switch (msg.what) {
                case MSG_AI_CAL:
                    mCallback.onResult(msg.obj.toString());
                    break;
                case MSG_TEST:
                    mCallback.onTest(msg.obj.toString());
                    break;
            }
        }
    }
}

