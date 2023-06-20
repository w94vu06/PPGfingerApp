package com.example.myapplication;

import android.content.Context;
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
    private Double AF_Similarity, AI_Depression, AI_Heart_age, AI_bshl, AI_dis,
            AI_medic, BMI, BPc_dia, BPc_sys, BSc, Excellent_no, Lf_Hf, RMSSD, Shannon_h,
            Total_Power, ULF, Unacceptable_no, VHF, VLF, dis0bs1_0, dis0bs1_1, dis1bs1_0,
            dis1bs1_1, ecg_Arr, ecg_PVC, ecg_QTc, ecg_hr_max, ecg_hr_mean, ecg_hr_min, ecg_rsp,
            hbp, hr_rsp_rate, meanNN, miny, miny_local_total, mood_state, pNN50, sdNN,
            sym_score_shift066, t_error, total_scores, unhrv, way_eat, way_eat_pa, waybp1_0_dia,
            waybp1_0_sys, waybp1_1_dia, year10scores;

    //    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final OkHttpClient client = new OkHttpClient();
    private static String json;
    Handler mHandler = new MHandler();

    boolean uploadSuccess = true;
//    String serverUrl = "http://192.168.2.5:5000/"; //公司
    String serverUrl = "http://192.168.0.102:5000/"; //家裡

    String registerRes;

    Handler resHandler = new ResHandler();

    private Context context;



    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }


    /**
     * 登入
     **/
    public void UserLogin(String jsonObject) {
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
                        Log.d("loginRes", "getLoginRes: " + loginRes);
                        Message resMsg = Message.obtain();
                        resMsg.obj = loginRes;
                        resHandler.sendMessage(resMsg);
                    } else {
                        uploadSuccess = false;
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
    public void UserRegister(String jsonObject) {
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
                        resMsg.obj = registerRes;
                        resHandler.sendMessage(resMsg);

                    } else {
                        uploadSuccess = false;
                        throw new IOException("Unexpected code " + registerResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class ResHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message resMsg) {
            super.handleMessage(resMsg);
            registerRes = resMsg.obj.toString();
            int intRegisterRes = Integer.parseInt(registerRes);
            Log.d("hhhh", "before: "+registerRes);
            Log.d("hhhh", "before Int: "+intRegisterRes);
            EventBus.getDefault().postSticky(new CodeEvent("Hi"));
        }
    }

    public class CodeEvent {
        private String CodeMsg;

        public CodeEvent(String message) {
            this.CodeMsg = CodeMsg;
        }
        public String getCodeEvent() {
            return CodeMsg;
        }
    }

    public void jsonUploadToServer(long[] time_dist) {

        JSONArray jsonTimeDist = new JSONArray();
        for (int i = 0; i < time_dist.length; i++) {
            jsonTimeDist.put(time_dist[i]);
        }
        JSONObject jsonData = new JSONObject();
        String date = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());

        try {
            jsonData.put("filename", date);
            jsonData.put("id_num", "888889");
            jsonData.put("chaurl", "-1");
            //proFile
            jsonData.put("old", "-1");
            jsonData.put("sex", "-1");
            jsonData.put("height", "-1");
            jsonData.put("weight", "-1");
            jsonData.put("sys", "-1");
            jsonData.put("diabetes", "-1");
            jsonData.put("smokes", "-1");
            jsonData.put("hbp", "-1");
            jsonData.put("morningdiabetes", "-1");
            jsonData.put("aftermealdiabetes", "-1");
            jsonData.put("userstatus", "-1");
            jsonData.put("mealstatus", "-1");
            jsonData.put("medicationstatus", "-1");
            jsonData.put("hbpSBp", "-1");
            jsonData.put("hbpDBp", "-1");
            jsonData.put("md_num", "-1");

            jsonData.put("rri", jsonTimeDist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonData.toString();

        new Thread() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonString);
                Request request = new Request.Builder()
                        .url("http://192.168.2.11:8090")//伺服器
//                        .url("http://192.168.2.110:5000")//測試服
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        uploadSuccess = false;
                        throw new IOException("Unexpected code " + response);
                    }
                    Message msg = Message.obtain();
                    String res = Objects.requireNonNull(response.body()).string();
                    msg.obj = res;
                    Log.d("serverRes", "getServerRes: " + res);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    class MHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            json = msg.obj.toString();
            unpackJson();
        }
    }

    private void unpackJson() {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                AF_Similarity = jsonObject.getDouble("AF_Similarity");
                AI_Depression = jsonObject.getDouble("AI_Depression");
                AI_Heart_age = jsonObject.getDouble("AI_Heart_age");
                AI_bshl = jsonObject.getDouble("AI_bshl");
                AI_dis = jsonObject.getDouble("AI_dis");
                AI_medic = jsonObject.getDouble("AI_medic");
                BMI = jsonObject.getDouble("BMI");
                BPc_dia = jsonObject.getDouble("BPc_dia");
                BPc_sys = jsonObject.getDouble("BPc_sys");
                BSc = jsonObject.getDouble("BSc");
                Excellent_no = jsonObject.getDouble("Excellent_no");
                Lf_Hf = jsonObject.getDouble("Lf_Hf");
                RMSSD = jsonObject.getDouble("RMSSD");
                Shannon_h = jsonObject.getDouble("Shannon_h");
                Total_Power = jsonObject.getDouble("Total_Power");
                ULF = jsonObject.getDouble("ULF");
                Unacceptable_no = jsonObject.getDouble("Unacceptable_no");
                VHF = jsonObject.getDouble("VHF");
                VLF = jsonObject.getDouble("VLF");
                dis0bs1_0 = jsonObject.getDouble("dis0bs1_0");
                dis0bs1_1 = jsonObject.getDouble("dis0bs1_1");
                dis1bs1_0 = jsonObject.getDouble("dis1bs1_0");
                dis1bs1_1 = jsonObject.getDouble("dis1bs1_1");
                ecg_Arr = jsonObject.getDouble("ecg_Arr");
                ecg_PVC = jsonObject.getDouble("ecg_PVC");
                ecg_QTc = jsonObject.getDouble("ecg_QTc");
                ecg_hr_max = jsonObject.getDouble("ecg_hr_max");
                ecg_hr_mean = jsonObject.getDouble("ecg_hr_mean");
                ecg_hr_min = jsonObject.getDouble("ecg_hr_min");
                ecg_rsp = jsonObject.getDouble("ecg_rsp");
                hbp = jsonObject.getDouble("hbp");
                hr_rsp_rate = jsonObject.getDouble("hr_rsp_rate");
                meanNN = jsonObject.getDouble("meanNN");
                miny = jsonObject.getDouble("miny");
                miny_local_total = jsonObject.getDouble("miny_local_total");
                mood_state = jsonObject.getDouble("mood_state");
                pNN50 = jsonObject.getDouble("pNN50");
                sdNN = jsonObject.getDouble("sdNN");
                sym_score_shift066 = jsonObject.getDouble("sym_score_shift066");
                t_error = jsonObject.getDouble("t_error");
                total_scores = jsonObject.getDouble("total_scores");
                unhrv = jsonObject.getDouble("unhrv");
                way_eat = jsonObject.getDouble("way_eat");
                way_eat_pa = jsonObject.getDouble("way_eat_pa");
                waybp1_0_dia = jsonObject.getDouble("waybp1_0_dia");
                waybp1_0_sys = jsonObject.getDouble("waybp1_0_sys");
                waybp1_1_dia = jsonObject.getDouble("waybp1_1_dia");
                year10scores = jsonObject.getDouble("year10scores");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }



}

