package com.example.myapplication;

import static com.example.myapplication.FilterAndIQR.findMedian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.Util.ChartUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;


public class CameraActivity extends AppCompatActivity implements MariaDBCallback {
    ControlMariaDB controlMariaDB = new ControlMariaDB(this);

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextureView CameraView;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private static final int REQUEST_CAMERA_PERMISSION = 420;

    // Thread handler member variables
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    //Heart rate detector member variables
    public static int heart_rate_bpm;
    /**
     * 總共要抓的心跳數
     */
    private final int captureRate = 35;
    // detectTime
    private final int setHeartDetectTime = 40;
    private final int rollAvgStandard = setHeartDetectTime + 29;
    private int mCurrentRollingAverage;
    private int mLastRollingAverage;
    private int mLastLastRollingAverage;
    private long[] mTimeArray;
    //Threshold
    private int numCaptures = 0;
    private int mNumBeats = 0;
    private int prevNumBeats = 0;
    private TextView heartBeatCount;
    private Button btn_restart;
    //chart
    private boolean chartIsRunning = false;
    private ChartUtil chartUtil;
    private LineChart chart;
    private Thread chartThread;
    //IQR
    private FilterAndIQR filterAndIQR;
    long[] nonZeroValuesAPI23;
    long[] outlierRRI;
    int fullAvgRed, fullAvgGreen, fullAvgBlue;
    int fixDarkRed;
    int fixAvgRedThreshold;
    ProgressBar progressBar;
    TextView progressBar_text;
    String phoneRMSSD;
    String phoneSDNN;

    String time = new SimpleDateFormat("yyyyMMddHHmmss",
            Locale.getDefault()).format(System.currentTimeMillis());


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg);

        preferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        CameraView = findViewById(R.id.texture);
        CameraView.setSurfaceTextureListener(textureListener);

        mTimeArray = new long[captureRate];

        btn_restart = findViewById(R.id.btn_restart);
        heartBeatCount = findViewById(R.id.heartBeatCount);

        filterAndIQR = new FilterAndIQR();

        progressBar = findViewById(R.id.progressBar_Circle);
        progressBar_text = findViewById(R.id.progress_text);
        //初始畫圖表
        chart = findViewById(R.id.lineChart);
        chartUtil = new ChartUtil();
        chartUtil.initChart(chart);
        closeTopBar();
        restartBtn();
        setCameraShape();
    }

    /**
     * //關閉標題列
     */
    public void closeTopBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * //初始化量測用數值
     */
    public void initValue() {
        numCaptures = 0;
        prevNumBeats = 0;
        mNumBeats = 0;
    }

    /**
     * 設定相機形狀
     */
    public void setCameraShape() {
        View overlay = findViewById(R.id.overlay);
        GradientDrawable circleDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.shape_oval);
        overlay.setBackground(circleDrawable);
        CameraView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth();
                int height = view.getHeight();
                int radius = Math.min(width, height) / 2;
                int centerX = width / 2;
                int centerY = height / 2;
                outline.setOval(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            }
        });
        CameraView.setClipToOutline(true);
    }

    /**
     * 重新量測-按鈕
     */
    public void restartBtn() {
        btn_restart.setOnClickListener(v -> {
            heartBeatCount.setText("量測準備中...");
            initProgressBar();
            shoutDownDetect();
            onResume();
        });
    }

    public void shoutDownDetect() {
        closeCamera();
        chart.clear();//畫布清除
        initValue();//初始化量測用數值
        chartUtil.initChart(chart);//確保畫布初始化
    }

    /**
     * 設定抓到的畫面
     */
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
            setScreenOn();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Bitmap bmp = CameraView.getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();

            int[] pixels = new int[height * width];
            int[] pixelsFullScreen = new int[height * width];
            bmp.getPixels(pixelsFullScreen, 0, width, 0, 0, width, height);//get full screen and main to detect
            bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 10, height / 10);//get small screen

            int redThreshold = 0;
            int greenThreshold = 0;
            int blueThreshold = 0;//小畫面的紅綠藍
            int fullScreenRed = 0;
            int fullScreenGreen = 0;
            int fullScreenBlue = 0;//整個畫面的紅綠藍
            for (int i = 0; i < height * width; i++) {
                //RED
                int red = (pixels[i] >> 16) & 0xFF;
                int redFull = (pixelsFullScreen[i] >> 16) & 0xFF;
                fullScreenRed += redFull;
                redThreshold += red;
                //GREEN
                int green = (pixels[i] >> 8) & 0xFF;
                int greenFull = (pixelsFullScreen[i] >> 8) & 0xFF;
                fullScreenGreen += greenFull;
                greenThreshold += green;
                //BLUE
                int blue = pixels[i] & 0xFF;
                int blueFull = pixelsFullScreen[i] & 0xFF;
                fullScreenBlue += blueFull;
                blueThreshold += blue;
            }
            //小畫面平均值
            int averageRedThreshold = redThreshold / (height * width);// 0 1 2
            int averageGreenThreshold = greenThreshold / (height * width);
            int averageBlueThreshold = blueThreshold / (height * width);
            //整個畫面平均值
            fullAvgRed = fullScreenRed / (height * width);// < 150
            fullAvgGreen = fullScreenGreen / (height * width);
            fullAvgBlue = fullScreenBlue / (height * width);

//            Log.d("yyyy", "RED: " + averageRedThreshold + "\nGREEN: " + averageGreenThreshold + "\nBLUE: " + averageBlueThreshold);
            if (fullAvgRed < 150) {
                fixDarkRed = fullScreenRed * 2;
                fixAvgRedThreshold = averageRedThreshold * 2;
            } else {
                fixDarkRed = fullScreenRed;
                fixAvgRedThreshold = averageRedThreshold;
            }
            //如果色素閥值是正確的才進行量測
            if (fixAvgRedThreshold == 2 && averageGreenThreshold == 0 && averageBlueThreshold == 0) { //改
                idleHandler.removeCallbacks(idleRunnable);

                // Waits 20 captures, to remove startup artifacts.  First average is the sum.
                //等待前幾個取樣，以去除啟動過程中的初始偏差
                if (numCaptures == setHeartDetectTime) {
                    mCurrentRollingAverage = fixDarkRed;//改
                }
                // Next 18 averages needs to incorporate the sum with the correct N multiplier
                // in rolling average.
                //在接下來18個取樣之間，程式會使用前面的取樣和當前取樣的加權平均值來計算移動平均值
                else if (numCaptures > setHeartDetectTime && numCaptures < rollAvgStandard) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * (numCaptures - setHeartDetectTime) + fixDarkRed) / (numCaptures - (setHeartDetectTime - 1));//改
                }

                // From 49 on, the rolling average incorporates the last 30 rolling averages.
                else if (numCaptures >= rollAvgStandard) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * 29 + fixDarkRed) / 30;//改
                    if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < captureRate) {
                        mTimeArray[mNumBeats] = System.currentTimeMillis();
                        initProgressBar();
                        mNumBeats++;
                        if (mNumBeats > prevNumBeats) {
                            triggerHandler();
                        }

                        prevNumBeats = mNumBeats;
                        startChartRun();//開始跑圖表
//                        heartBeatCount.setText("檢測到的心跳次數：" + mNumBeats);
                        if (mNumBeats == captureRate) {
                            chartIsRunning = false;
                            closeCamera();
                            setScreenOff();
                            calcBPM_RMMSD_SDNN();
                            showInfoOnScrollView();
                        }
                    }
                }
                // Another capture
                numCaptures++;
                // Save previous two values
                mLastLastRollingAverage = mLastRollingAverage;
                mLastRollingAverage = mCurrentRollingAverage;
            } else {
                qualityHandler.postDelayed(qualityRunnable, 5000);
                idleHandler.postDelayed(idleRunnable, 15000);
            }
        }
    };

    /**
     * 閒置太久強制中止
     */
    private Handler idleHandler = new Handler();
    private Runnable idleRunnable = new Runnable() {
        @Override
        public void run() {
            // 在此暫時關閉某些功能
            disableSomeFunction();
            qualityHandler.removeCallbacks(qualityRunnable);
            chartIsRunning = false;//關閉畫圖
            heartBeatCount.setText("訊號過差，量測失敗");
        }
    };

    /**
     * 關閉功能
     */
    private void disableSomeFunction() {
        // 關閉功能，停止影像
        shoutDownDetect();
    }

    /**
     * 量測提醒
     */
    private Handler qualityHandler = new Handler();
    private Runnable qualityRunnable = new Runnable() {
        @Override
        public void run() {
            heartBeatCount.setText("把手指靠近相機鏡頭，調整直到畫面充滿紅色，然後保持靜止。");
        }
    };


    /**
     * 開啟相機服務
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            if (cameraDevice != null)
                cameraDevice.close();
            cameraDevice = null;
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void calcBPM_RMMSD_SDNN() {
        int med;
        long[] time_dist = new long[mTimeArray.length - 1];
        Log.d("rrrr", "calcBPM_RMMSD_SDNN: " + mTimeArray.length);
        //calcRRI
        for (int i = 5; i < time_dist.length - 1; i++) {
            time_dist[i] = mTimeArray[i + 1] - mTimeArray[i];
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            outlierRRI = filterAndIQR.IQR(time_dist);//去掉離群值
        } else {
            outlierRRI = IQRForAPI23(time_dist);//去掉離群值
        }
        //calcBPM
        long[] getBPMOutlier = outlierRRI;
        uploadResult(getBPMOutlier);
        try {
            Arrays.sort(getBPMOutlier);
            med = (int) outlierRRI[outlierRRI.length / 2];
            heart_rate_bpm = 60000 / med;

            //calcRMSSD_SDNN
            DecimalFormat df = new DecimalFormat("#.##");//設定輸出格式
            phoneRMSSD = df.format(filterAndIQR.calculateRMSSD(outlierRRI));
            phoneSDNN = df.format(filterAndIQR.calculateSDNN(outlierRRI));


            onPause();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 顯示結果在ScrollView
     */
    private void showInfoOnScrollView() {
        for (int i = 0; i < outlierRRI.length; i++) {
//            scv_text.append("RRi：" + outlierRRI[i] + " / " + "紅色色素：" + fullAvgRed + "\n");
        }
    }

    /**
     * 上傳量測結果至伺服器
     */
    private void uploadResult(long[] time_dist) {
        if (outlierRRI != null) {
            JSONArray jsonTimeDist = new JSONArray();
            for (int i = 0; i < time_dist.length; i++) {
                jsonTimeDist.put(time_dist[i]);
            }
            JSONObject jsonData = new JSONObject();


            String userId = preferences.getString("ProfileId", "888889");
            String old = String.valueOf(preferences.getInt("ProfileOld", -1));
            String sex = String.valueOf(preferences.getInt("ProfileSex", -1));
            String height = String.valueOf(preferences.getInt("ProfileHeight", -1));
            String weight = String.valueOf(preferences.getInt("ProfileWeight", -1));
            String diabetes = String.valueOf(preferences.getInt("ProfileDiabetes", -1));
            String smokes = String.valueOf(preferences.getInt("ProfileSmokes", -1));
            String hbp = String.valueOf(preferences.getInt("ProfileHbp", -1));

//            String sys = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String morningdiabetes = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String aftermealdiabetes = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String userstatus = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String mealstatus = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String medicationstatus = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String hbpSBp = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String hbpDBp = String.valueOf(preferences.getInt("ProfileOld", 0));
//            String md_num = String.valueOf(preferences.getInt("ProfileOld", 0));

            try {
                //proFile
                jsonData.put("filename", time + "_" + userId);
                jsonData.put("time", time);
                jsonData.put("id_num", userId);

                jsonData.put("chaurl", "-1");

                jsonData.put("old", old);
                jsonData.put("sex", sex);
                jsonData.put("height", height);
                jsonData.put("weight", weight);
                jsonData.put("diabetes", diabetes);
                jsonData.put("smokes", smokes);
                jsonData.put("hbp", hbp);

                //not yet
                jsonData.put("sys", "-1");
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
            controlMariaDB.jsonUploadToServer(jsonString);
        }
    }

    /**
     * 量測結果
     */
    @Override
    public void onResult(String result) {
        unpackJsonAndSave(result);
    }

    @Override
    public void onSave(String result) {
        Log.d("eeee", "onSave: "+result);
    }

    public void judgeEventCode(int res) {
        if (res == 1) {
            Toast.makeText(CameraActivity.this, "登入成功，頁面跳轉中...", Toast.LENGTH_SHORT).show();
        } else if (res == 0) {
            Toast.makeText(CameraActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = CameraView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice) {
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        System.out.println("is camera open");
        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //check Permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        System.out.println("openCamera X");
    }

    //check camera and flash can be use
    protected void updatePreview() {
        if (null == cameraDevice) {
            System.out.println("updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    //check Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(CameraActivity.this, "Please Grant Permissions", Toast.LENGTH_LONG).show();
                recreate();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        setScreenOn();
        idleHandler.removeCallbacks(idleRunnable);
        if (CameraView.isAvailable()) {
            openCamera();
        } else {
            CameraView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        setScreenOff();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * keep screen open
     */
    public void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setScreenOff() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 開始跑圖表
     */
    private void startChartRun() {
        if (chartIsRunning) return;
        if (chartThread != null) chartThread.interrupt();

        //簡略寫法
        chartIsRunning = true;
        Runnable runnable = () -> {
            addData(50);
        };

        //簡略寫法
        chartThread = new Thread(() -> {
            while (chartIsRunning) {
                runOnUiThread(runnable);
                if (!chartIsRunning) break;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        chartThread.start();
    }

    /**
     * 新增資料
     */
    private void addData(float inputData) {
        LineData data = chart.getData();//取得原數據
        ILineDataSet set = data.getDataSetByIndex(0);//取得曲線(因為只有一條，故為0，若有多條則需指定)
        if (set == null) {
            set = chartUtil.createSet();
            data.addDataSet(set);//如果是第一次跑則需要載入數據
        }
        data.addEntry(new Entry(set.getEntryCount(), inputData), 0);//新增數據點
        //
        data.notifyDataChanged();
        data.setDrawValues(false);//是否繪製線條上的文字
        chart.notifyDataSetChanged();
        chart.setVisibleXRange(0, 60);//設置可見範圍
        chart.moveViewToX(data.getEntryCount() + 3);//將可視焦點放在最新一個數據，使圖表可移動
    }

    /**
     * 觸發心跳
     */
    public void triggerHandler() {
        float inputData = 60;
        float downData = 40;
        addData(inputData);
        addData(downData);
    }

    public void initProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar_text.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNumBeats <= 35) {
                    int progress = (int) (mNumBeats / 35.0 * 100);
                    progressBar_text.setText(progress + "%");
                    progressBar.setProgress(progress);
                }
            }
        }, 200);
    }

    public long[] IQRForAPI23(long[] RRI) {

        double[] arr = new double[RRI.length];
        for (int i = 0; i < RRI.length; i++) {
            arr[i] = (double) RRI[i];
        }

        Arrays.sort(arr);

        double q1 = findMedian(arr, 0, arr.length / 2 - 1);
        double q3 = findMedian(arr, arr.length / 2 + arr.length % 2, arr.length - 1);
        // 計算 IQR
        double iqr = q3 - q1;

        // 計算上下界
        double upperBound = q3 + 1 * iqr;
        double lowerBound = q1 - 1 * iqr;

        // 將超過上下界的值設為0
        for (int i = 0; i < RRI.length; i++) {
            if (RRI[i] > upperBound || RRI[i] < lowerBound) {
                RRI[i] = 0;
            }
        }
        ArrayList<Long> clearArrayList = new ArrayList<>();
        // 找出所有不為0的值的平均數
        for (int i = 0; i < RRI.length; i++) {
            if (RRI[i] != 0 && RRI[i] > 300 && RRI[i] < 1100) {
                clearArrayList.add(RRI[i]);
            }
        }
        nonZeroValuesAPI23 = new long[clearArrayList.size()];
        for (int i = 0; i < clearArrayList.size(); i++) {
            nonZeroValuesAPI23[i] = clearArrayList.get(i);
        }
        return nonZeroValuesAPI23;
    }

    private void unpackJsonAndSave(String json) {
        json = json.replaceAll("NaN", "null");
        json = json.replaceAll("null", "0.0");
        String finalJson = json;
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(finalJson);
                String userId = preferences.getString("ProfileId", "888889");

                double AF_Similarity = jsonObject.getDouble("AF_Similarity");
                double AI_Depression = jsonObject.getDouble("AI_Depression");
                double AI_Heart_age = jsonObject.getDouble("AI_Heart_age");
                double AI_bshl = jsonObject.getDouble("AI_bshl");
                double AI_dis = jsonObject.getDouble("AI_dis");
                double AI_medic = jsonObject.getDouble("AI_medic");
                double BMI = jsonObject.getDouble("BMI");
                double BPc_dia = jsonObject.getDouble("BPc_dia");
                double BPc_sys = jsonObject.getDouble("BPc_sys");
                double BSc = jsonObject.getDouble("BSc");
                double Excellent_no = jsonObject.getDouble("Excellent_no");
                double Lf_Hf = jsonObject.getDouble("Lf/Hf");
                double RMSSD = jsonObject.getDouble("RMSSD");
                double Shannon_h = jsonObject.getDouble("Shannon_h");
                double Total_Power = jsonObject.getDouble("Total_Power");
                double ULF = jsonObject.getDouble("ULF");
                double Unacceptable_no = jsonObject.getDouble("Unacceptable_no");
                double VHF = jsonObject.getDouble("VHF");
                double VLF = jsonObject.getDouble("VLF");
                double dis0bs1_0 = jsonObject.getDouble("dis0bs1_0");
                double dis0bs1_1 = jsonObject.getDouble("dis0bs1_1");
                double dis1bs1_0 = jsonObject.getDouble("dis1bs1_0");
                double dis1bs1_1 = jsonObject.getDouble("dis1bs1_1");
                double ecg_Arr = jsonObject.getDouble("ecg_Arr");
                double ecg_PVC = jsonObject.getDouble("ecg_PVC");
                double ecg_QTc = jsonObject.getDouble("ecg_QTc");
                double ecg_hr_max = jsonObject.getDouble("ecg_hr_max");
                double ecg_hr_mean = jsonObject.getDouble("ecg_hr_mean");
                double ecg_hr_min = jsonObject.getDouble("ecg_hr_min");
                double ecg_rsp = jsonObject.getDouble("ecg_rsp");
                double hbp = jsonObject.getDouble("hbp");
                double hr_rsp_rate = jsonObject.getDouble("hr_rsp_rate");
                double meanNN = jsonObject.getDouble("meanNN");
                double miny = jsonObject.getDouble("miny");
                double miny_local_total = jsonObject.getDouble("miny_local_total");
                double mood_state = jsonObject.getDouble("mood_state");
                double pNN50 = jsonObject.getDouble("pNN50");
                double sdNN = jsonObject.getDouble("sdNN");
                double sym_score_shift066 = jsonObject.getDouble("sym_score_shift066");
                double t_error = jsonObject.getDouble("t_error");
                double total_scores = jsonObject.getDouble("total_scores");
                double unhrv = jsonObject.getDouble("unhrv");
                double way_eat = jsonObject.getDouble("way_eat");
                double way_eat_pa = jsonObject.getDouble("way_eat_pa");
                double waybp1_0_dia = jsonObject.getDouble("waybp1_0_dia");
                double waybp1_0_sys = jsonObject.getDouble("waybp1_0_sys");
                double waybp1_1_dia = jsonObject.getDouble("waybp1_1_dia");
                double year10scores = jsonObject.getDouble("year10scores");

                String time = new SimpleDateFormat("yyyyMMddHHmmss",
                        Locale.getDefault()).format(System.currentTimeMillis());

                //做成JSON送去儲存(包含ID與時間)
                jsonObject.put("userId", userId);

                jsonObject.put("time", time);

                String jsonString = jsonObject.toString();
                Log.d("eeee", "JsonAndSave: " + jsonString);
                controlMariaDB.userIdSave(jsonString);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DecimalFormat df = new DecimalFormat("#.##");//設定輸出格式
                        heartBeatCount.setText("手機端：RMSSD：" + phoneRMSSD + "\nSDNN：" + phoneSDNN + "\nBPM：" + heart_rate_bpm + "\n伺服端-RMSSD：" + df.format(RMSSD) + "\nSDNN：" + df.format(sdNN) + "\nBPM：" + df.format(ecg_hr_mean));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}










