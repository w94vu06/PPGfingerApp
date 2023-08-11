package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Util.ChartUtil;
import com.example.myapplication.Util.GetPixelUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;


public class CameraActivity extends AppCompatActivity implements MariaDBCallback {
    // 相機和使用者介面相關變數
    private ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextureView CameraView; // 相機預覽的TextureView
    private CameraDevice cameraDevice; // 相機設備
    private CameraCaptureSession cameraCaptureSessions; // 相機捕獲會話
    private CaptureRequest.Builder captureRequestBuilder; // 捕獲請求的Builder
    private Size imageDimension; // 相片尺寸
    private static final int REQUEST_CAMERA_PERMISSION = 420; // 請求相機權限的請求碼

    // 執行緒處理相關的變數
    private Handler mBackgroundHandler; // 背景執行緒的Handler
    private HandlerThread mBackgroundThread; // 背景執行緒

    // 心跳檢測相關變數
    private int totalCaptureRate = 35; // 總共捕獲的心跳數量
    private final int setHeartDetectTime = 30; // 設定心跳檢測時間
    private final int rollAvgStandard = setHeartDetectTime; // 心跳平均值標準
    private int mCurrentRollingAverage; // 當前心跳平均值
    private int mLastRollingAverage; // 上一個心跳平均值
    private int mLastLastRollingAverage; // 上上個心跳平均值
    private long[] mTimeArray; // 記錄捕獲心跳的時間陣列
    private int numRateCaptured = 0; // 目前已捕獲的心跳數量
    private int mNumBeats = 0; // 當前捕獲的心跳數量
    private int prevNumBeats = 0; // 上一個捕獲的心跳數量

    // 心跳變異性計算相關變數
    private CalculateHRV calculateHRV; // 心跳變異性計算工具
    private long[] outlierRRI;
    private static long[] getBPMOutlier;

    // 色素檢測相關變數
    GetPixelUtil getPixelUtil = new GetPixelUtil();
    private int fullAvgRed, fullAvgGreen, fullAvgBlue; // 整個畫面的紅綠藍色素值
    private int fixDarkRed; // 調整後的暗紅色素值
    private int fixAvgRedThreshold; // 調整後的平均紅色素值閾值

    // 使用者介面元件
    private TextView txt_phoneMarquee, txt_serverInfo;
    private TextView txt_phoneCal, txt_aiCal;
    private Button btn_restart, btn_detailed;
    private Dialog dialog;
    private ProgressBar progressBar;
    private TextView progressBar_text;
    private Spinner spinner_beats; // 心跳選擇的下拉選單
    private int lastSelectedItem = 0; // 初始化為預設的索引值，例如 0
    private long elapsedSecond; // 量測的秒數

    // 圖表相關變數
    private boolean chartIsRunning = false; // 圖表是否正在運行
    private ChartUtil chartUtil; // 圖表工具
    private LineChart chart; // 折線圖
    private Thread chartThread; // 圖表的執行緒
    private List<Float> fullAvgRedList = new ArrayList<>();
    private float fixedUpperBound = 0;
    private float fixedLowerBound = 0;
    private final float smoothFactor = 0.2f; // 調整平滑程度的因子
    private List<Float> upperBoundDefault = new ArrayList<>();
    private List<Float> lowerBoundDefault = new ArrayList<>();
    private float upperBound;
    private float lowerBound;


    // 時間相關變數
    private String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis()); // 現在的時間字串

    // 手機計算的特徵
    private String phoneRMSSD, phoneSDNN, phoneMedianNN, phonePNN50, phoneMinNN, phoneMaxNN, phoneBPM;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg);

        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        CameraView = findViewById(R.id.texture);
        CameraView.setSurfaceTextureListener(textureListener);

        btn_restart = findViewById(R.id.btn_restart);
        btn_detailed = findViewById(R.id.btn_detailed);
        txt_phoneMarquee = findViewById(R.id.txt_phoneMarquee);
        txt_serverInfo = findViewById(R.id.txt_serverInfo);

        chart = findViewById(R.id.lineChart);
        chartUtil = new ChartUtil();
        chartUtil.initChart(chart);//確保畫布初始化
        setPreferChartLimit();
        spinner_beats = findViewById(R.id.spinner_beats);
        progressBar = findViewById(R.id.progressBar_Circle);
        progressBar_text = findViewById(R.id.progress_text);

        mTimeArray = new long[totalCaptureRate];
        calculateHRV = new CalculateHRV();

        closeTopBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setScreenOn();
        controlMariaDB.testServer("test");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScreenOn();
        startBackgroundThread();
        chartUtil.initChart(chart);//確保畫布初始化
        setPreferChartLimit();
        idleHandler.removeCallbacks(idleRunnable);
        if (CameraView.isAvailable()) {
            openCamera();
        } else {
            CameraView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        setScreenOff();
        stopBackgroundThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
        setScreenOff();
        removeRunnable();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 關閉標題列
     */
    public void closeTopBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setScreenOff() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 初始化dialog
     */
    public void initDialog() {
        dialog = new Dialog(CameraActivity.this);
        dialog.setContentView(R.layout.detail_sheet);

        txt_phoneCal = dialog.findViewById(R.id.txt_phoneCal);
        txt_aiCal = dialog.findViewById(R.id.txt_aiCal);
    }

    public void detailDialog() {
        Window window = dialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // 設置對話框的寬度和高度
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(layoutParams);
        }
        Button btn_closeDialog = dialog.findViewById(R.id.btn_closeDialog);
        btn_closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void initProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar_text.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNumBeats <= totalCaptureRate) {
                    int progress = (int) (mNumBeats / (float) totalCaptureRate * 100);
                    progressBar_text.setText(progress + "%");
                    progressBar.setProgress(progress);
                }
            }
        }, 200);
    }

    /**
     * 重新量測-按鈕
     * 詳細數據-按鈕
     */
    public void initBtn() {
        btn_restart.setOnClickListener(v -> {
            initProgressBar();
            resetChartAndProgressBar();
            onResume();
        });
        btn_detailed.setOnClickListener(v -> {
            detailDialog();
        });
    }

    /**
     * 初始化量測用數值
     */
    public void initValue() {
        mTimeArray = new long[totalCaptureRate];
        numRateCaptured = 0;
        prevNumBeats = 0;
        mNumBeats = 0;
        fullAvgRedList.clear();
    }

    private void resetChartAndProgressBar() {
        chart.clear(); // 畫布清除
        chartUtil.initChart(chart);//確保畫布初始化
        setPreferChartLimit();
        initValue(); // 初始化量測用數值
        initProgressBar();// 重置progressBar
        txt_phoneMarquee.setText("把手指靠近相機鏡頭，調整直到畫面\n充滿紅色，然後保持靜止。");
        txt_phoneCal.setText("");
    }

    /**
     * 設定抓到的畫面
     */
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
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
            bmp.getPixels(pixels, 0, width, width / 2, height / 2,
                    width / 10, height / 10);//get small screen

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
            Log.d("nnnn", "RED: " + fullAvgRed);

            //表示鏡頭太暗
            if (fullAvgRed < 150) {
                fixDarkRed = fullScreenRed * 2;
                fixAvgRedThreshold = averageRedThreshold * 2;
            } else {
                fixDarkRed = fullScreenRed;
                fixAvgRedThreshold = averageRedThreshold;
            }
            //如果色素閥值是正確的才進行量測
            if (fixAvgRedThreshold == 2 && averageGreenThreshold == 0 && averageBlueThreshold == 0) { //改
                fullAvgRedList.add((float) fullAvgRed);
                Log.d("zzzz", "run: " + fullAvgRedList.size());
                setChartLimit();
                // Waits 20 captures, to remove startup artifacts.  First average is the sum.
                //等待前幾個取樣，以去除啟動過程中的初始偏差
                if (numRateCaptured == setHeartDetectTime) {
                    mCurrentRollingAverage = fixDarkRed;//改
                }
                // Next averages needs to incorporate the sum with the correct N multiplier
                // in rolling average.
                //在接下來取樣之間，會使用前面的取樣和當前取樣的加權平均值來計算移動平均值
                else if (numRateCaptured > setHeartDetectTime && numRateCaptured < rollAvgStandard) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * (numRateCaptured - setHeartDetectTime) + fixDarkRed) / (numRateCaptured - (setHeartDetectTime - 1));//改
                }
                // From 49 on, the rolling average incorporates the last 30 rolling averages.
                else if (numRateCaptured >= rollAvgStandard) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * 29 + fixDarkRed) / 30;//改
                    if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < totalCaptureRate) {
                        mTimeArray[mNumBeats] = System.currentTimeMillis();
                        mNumBeats++;
                        startChartRun();//開始跑圖表
                        initProgressBar();

                        if (mNumBeats == totalCaptureRate) { //量測完成時
                            //計算量測秒數
                            long elapsedTime = (mTimeArray[mNumBeats - 1] - mTimeArray[0]);
                            elapsedSecond = elapsedTime / 1000;

                            closeCamera();
                            Toast.makeText(CameraActivity.this, "伺服端計算中...", Toast.LENGTH_SHORT).show();
                            calcBPM_RMMSD_SDNN();
                            removeRunnable();
                            chartIsRunning = false;
                            calLastTimeChartLimit();

                        }
                    }
                }
                // Another capture
                numRateCaptured++;
                // Save previous two values
                mLastLastRollingAverage = mLastRollingAverage;
                mLastRollingAverage = mCurrentRollingAverage;
                removeRunnable();
            } else {
                qualityHandler.postDelayed(qualityRunnable, 8000);
                idleHandler.postDelayed(idleRunnable, 15000);
            }

        }
    };

    private void calLastTimeChartLimit() {
        chartUtil.calculateAndSetDefaultBounds(upperBoundDefault, lowerBoundDefault);
        float upper = chartUtil.defaultUpperBound;
        float lower = chartUtil.defaultLowerBound;

        editor = preferences.edit();
        editor.putFloat("lastUpper", upper);
        editor.putFloat("lastLower", lower);
        editor.apply();
    }

    private void setPreferChartLimit() {
        float upper = preferences.getFloat("lastUpper", 255);
        float lower = preferences.getFloat("lastLower", 0);
        YAxis y = chart.getAxisLeft();
        y.setAxisMaximum(upper);
        y.setAxisMinimum(lower);
    }

    /**
     * 閒置太久強制中止
     */
    private final Handler idleHandler = new Handler();
    private final Runnable idleRunnable = new Runnable() {
        @Override
        public void run() {
            // 在此暫時關閉某些功能
            onPause();
            resetChartAndProgressBar();
            qualityHandler.removeCallbacks(qualityRunnable);
//            updateChartLimitHandler.removeCallbacks(updateChartLimitRunnable);
            chartIsRunning = false;//關閉畫圖
            txt_phoneMarquee.setText("訊號過差，量測失敗");
        }
    };

    /**
     * 量測提醒
     */
    private final Handler qualityHandler = new Handler();
    private final Runnable qualityRunnable = new Runnable() {
        @Override
        public void run() {
            Toast toast = Toast.makeText(CameraActivity.this, "請勿用力按緊，並避免移動", Toast.LENGTH_SHORT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (qualityHandler.hasCallbacks(qualityRunnable)) {
                    qualityHandler.removeCallbacks(qualityRunnable);
                } else {
                    toast.show();
                }
            }
            qualityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 1000);
        }
    };

    /**
     * 取中位數設定圖表上下界
     */
    private void setFixedBounds(float upperBound, float lowerBound) {
        fixedUpperBound = upperBound;
        fixedLowerBound = lowerBound;
    }

    private void smoothBounds(float newUpperBound, float newLowerBound) {
        fixedUpperBound = fixedUpperBound + (newUpperBound - fixedUpperBound) * smoothFactor;
        fixedLowerBound = fixedLowerBound + (newLowerBound - fixedLowerBound) * smoothFactor;
    }

    private void setChartLimit() {
        YAxis y = chart.getAxisLeft();

        if (fullAvgRedList.size() >= 75) {
            // 計算四分位數
            float[] quartiles = calculateHRV.calculateQuartiles(fullAvgRedList);
            float Q1 = quartiles[0];
            float Q2 = quartiles[1];
            float Q3 = quartiles[2];

            // 設定上下界
            float interQuartileRange = Q3 - Q1;
            upperBound = Q1 + 4f * interQuartileRange;
            lowerBound = Q3 - 4f * interQuartileRange;

            // 設定固定的上下界並執行平滑移動
            setFixedBounds(upperBound, lowerBound);
            smoothBounds(upperBound, lowerBound);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fixedUpperBound != 0 && fixedLowerBound != 0 && fixedUpperBound > fixedLowerBound) {
                        y.setAxisMaximum(fixedUpperBound * 1.01f);
                        y.setAxisMinimum(fixedLowerBound * 0.99f);

                        upperBoundDefault.add(fixedUpperBound);
                        lowerBoundDefault.add(fixedLowerBound);

                        Log.d("asdasd", "fixedUpperBound: " + fixedUpperBound + "\nfixedLowerBound: " + fixedLowerBound);
                    }
                }
            });

            fullAvgRedList.clear();
        }
    }


    private void removeRunnable() {
        qualityHandler.removeCallbacks(qualityRunnable);
        idleHandler.removeCallbacks(idleRunnable);
    }

    /**
     * 開啟相機服務
     */
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
            setCameraShape();

            //UI初始化
            chartUtil.initChart(chart);
            setPreferChartLimit();
            setSpinner_beats();
            spinner_beats.setSelection(lastSelectedItem);
            initDialog();
            initBtn();
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

    /**
     * 設定相機形狀
     */
    public void setCameraShape() {
        View overlay = findViewById(R.id.overlay);
        Drawable circleDrawable = ContextCompat.getDrawable(this, R.drawable.shape_oval);
        if (circleDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) circleDrawable;
            overlay.setBackground(gradientDrawable);
        }
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

    public void setSpinner_beats() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,    //對應的Context
                        R.array.beats_array,                    //資料選項內容
                        android.R.layout.simple_spinner_item); //預設Spinner未展開時的View(預設及選取後樣式)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_beats.setAdapter(adapter);
        spinner_beats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lastSelectedItem = i;
                switch (i) {
                    case 0:
                        totalCaptureRate = 35;
                        break;
                    case 1:
                        totalCaptureRate = 45;
                        break;
                    case 2:
                        totalCaptureRate = 55;
                        break;
                }
                resetChartAndProgressBar();

                if (cameraDevice == null) {
                    openCamera();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//        updateChartLimitHandler.post(updateChartLimitRunnable);
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
//        updateChartLimitHandler.removeCallbacks(updateChartLimitRunnable);
    }

    private void calcBPM_RMMSD_SDNN() {
        long[] time_dist = new long[mTimeArray.length - 1];
        //calcRRI
        for (int i = 0; i < time_dist.length - 1; i++) {
            time_dist[i] = mTimeArray[i + 1] - mTimeArray[i];
        }
        outlierRRI = calculateHRV.IQR(time_dist);//去掉離群值
        //calcBPM
        getBPMOutlier = outlierRRI;

        //calcRMSSD_SDNN
        uploadResult(getBPMOutlier);

        DecimalFormat df = new DecimalFormat("#.##");//設定輸出格式
        phoneRMSSD = df.format(calculateHRV.calculateRMSSD(getBPMOutlier));
        phoneSDNN = df.format(calculateHRV.calculateSDNN(getBPMOutlier));
        phonePNN50 = df.format(calculateHRV.calculatePNN50(getBPMOutlier));
        phoneMinNN = df.format(calculateHRV.calMinNN(getBPMOutlier));
        phoneMaxNN = df.format(calculateHRV.calMaxNN(getBPMOutlier));
        //會排序到RRI
        phoneBPM = df.format(calculateHRV.calBPM(getBPMOutlier));
        phoneMedianNN = df.format(calculateHRV.calculateMedianNN(getBPMOutlier));
        showMeasureResult();
    }

    private void showMeasureResult() {
        try {
            txt_phoneMarquee.setText("");
            txt_phoneMarquee.setText("本次量測使用" + elapsedSecond + "秒\n");
            txt_phoneMarquee.append("手機端：RMSSD：" + phoneRMSSD + "\nSDNN：" + phoneSDNN + "\nBPM：" + phoneBPM);
            txt_phoneCal.setText("");
            String showHRV = "RMSSD：" + phoneRMSSD + ",SDNN：" + phoneSDNN + ",ecg_hr_mean：" + phoneBPM +
                    ",MedianNN：" + phoneMedianNN + ",pNN50：" + phonePNN50 + ",min_hr：" + phoneMinNN + ",max_hr：" + phoneMaxNN;
            String[] showHRVOnPhone = showHRV.split(",");
            for (String s : showHRVOnPhone) {
                txt_phoneCal.append(s + "\n");
            }

            onPause();
        } catch (Exception e) {
            System.out.println(e);
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
            String morningdiabetes = String.valueOf(preferences.getInt("ProfileOld", -1));
            String aftermealdiabetes = String.valueOf(preferences.getInt("ProfileOld", -1));

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

                jsonData.put("morningdiabetes", morningdiabetes);
                jsonData.put("aftermealdiabetes", aftermealdiabetes);
                //not yet
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
        if (result.equals("fail")) {
            txt_phoneMarquee.append("\n伺服器發生錯誤");
        } else {
            unpackJsonAndSave(result);
        }
    }

    /**
     * 儲存量測資料到資料庫
     */
    @Override
    public void onSave(String result) {
        int errorCode = Integer.parseInt(result);
        //如果儲存失敗會顯示伺服器發生錯誤
        if (errorCode == 0) {
            txt_phoneMarquee.append("\n伺服器發生錯誤");
        }
    }

    /**
     * 判斷AI伺服器狀態
     */
    @Override
    public void onTest(String result) {
        Log.d("gggg", "onTest: " + result);
        if (Objects.equals(result, "200")) {
            txt_serverInfo.setText("ai伺服器已開啟");
        } else if (Objects.equals(result, "400")) {
            txt_serverInfo.setText("伺服器發生錯誤");
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

    /**
     * 開始跑圖表
     */
    private void startChartRun() {
        if (chartIsRunning) return;
        if (chartThread != null) chartThread.interrupt();

        chartIsRunning = true;
        Runnable runnable = () -> {
            addData(fullAvgRed);
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
        chart.setVisibleXRange(0, 50);//設置可見範圍 預設0-80
        chart.moveViewToX(data.getEntryCount());//將可視焦點放在最新一個數據，使圖表可移動
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
                double AI_bshl = jsonObject.getDouble("AI_bshl");
                double AI_bshl_pa = jsonObject.getDouble("AI_bshl_pa");
                double AI_dis = jsonObject.getDouble("AI_dis");
                double AI_dis_pa = jsonObject.getDouble("AI_dis_pa");
                double AI_medic = jsonObject.getDouble("AI_medic");
                double BMI = jsonObject.getDouble("BMI");
                double BPc_dia = jsonObject.getDouble("BPc_dia");
                double BPc_sys = jsonObject.getDouble("BPc_sys");
                double BSc = jsonObject.getDouble("BSc");
                double Lf_Hf = jsonObject.getDouble("Lf/Hf");
                double RMSSD = jsonObject.getDouble("RMSSD");
                double Shannon_h = jsonObject.getDouble("Shannon_h");
                double Total_Power = jsonObject.getDouble("Total_Power");
                double ULF = jsonObject.getDouble("ULF");
                double VHF = jsonObject.getDouble("VHF");
                double VLF = jsonObject.getDouble("VLF");
                double dis0bs1_0 = jsonObject.getDouble("dis0bs1_0");
                double dis0bs1_1 = jsonObject.getDouble("dis0bs1_1");
                double dis1bs1_0 = jsonObject.getDouble("dis1bs1_0");
                double dis1bs1_1 = jsonObject.getDouble("dis1bs1_1");
                double ecg_hr_max = jsonObject.getDouble("ecg_hr_max");
                double ecg_hr_mean = jsonObject.getDouble("ecg_hr_mean");
                double ecg_hr_min = jsonObject.getDouble("ecg_hr_min");
                double ecg_rsp = jsonObject.getDouble("ecg_rsp");
                String fatigue = jsonObject.getString("fatigue");
                double hbp = jsonObject.getDouble("hbp");
                double hr_rsp_rate = jsonObject.getDouble("hr_rsp_rate");
                double meanNN = jsonObject.getDouble("meanNN");
                double mood_state = jsonObject.getDouble("mood_state");
                double pNN50 = jsonObject.getDouble("pNN50");
                double sdNN = jsonObject.getDouble("sdNN");
                double total_scores = jsonObject.getDouble("total_scores");
                double way_eat = jsonObject.getDouble("way_eat");
                double way_eat_pa = jsonObject.getDouble("way_eat_pa");
                double year10scores = jsonObject.getDouble("year10scores");

                editor = preferences.edit();
                editor.putFloat("AF_Similarity", (float) AF_Similarity);
                editor.putFloat("AI_bshl", (float) AI_bshl);
                editor.putFloat("AI_bshl_pa", (float) AI_bshl_pa);
                editor.putFloat("AI_dis", (float) AI_dis);
                editor.putFloat("AI_dis_pa", (float) AI_dis_pa);
                editor.putFloat("AI_medic", (float) AI_medic);
                editor.putFloat("BMI", (float) BMI);
                editor.putFloat("BPc_dia", (float) BPc_dia);
                editor.putFloat("BPc_sys", (float) BPc_sys);
                editor.putFloat("BSc", (float) BSc);
                editor.putFloat("Lf/Hf", (float) Lf_Hf);
                editor.putFloat("RMSSD", (float) RMSSD);
                editor.putFloat("Shannon_h", (float) Shannon_h);
                editor.putFloat("Total_Power", (float) Total_Power);
                editor.putFloat("ULF", (float) ULF);
                editor.putFloat("VHF", (float) VHF);
                editor.putFloat("VLF", (float) VLF);
                editor.putFloat("dis0bs1_0", (float) dis0bs1_0);
                editor.putFloat("dis0bs1_1", (float) dis0bs1_1);
                editor.putFloat("dis1bs1_0", (float) dis1bs1_0);
                editor.putFloat("dis1bs1_1", (float) dis1bs1_1);
                editor.putFloat("ecg_hr_max", (float) ecg_hr_max);
                editor.putFloat("ecg_hr_mean", (float) ecg_hr_mean);
                editor.putFloat("ecg_hr_min", (float) ecg_hr_min);
                editor.putFloat("ecg_rsp", (float) ecg_rsp);
                editor.putString("fatigue", fatigue);
                editor.putFloat("hbp", (float) hbp);
                editor.putFloat("hr_rsp_rate", (float) hr_rsp_rate);
                editor.putFloat("meanNN", (float) meanNN);
                editor.putFloat("mood_state", (float) mood_state);
                editor.putFloat("pNN50", (float) pNN50);
                editor.putFloat("sdNN", (float) sdNN);
                editor.putFloat("total_scores", (float) total_scores);
                editor.putFloat("way_eat", (float) way_eat);
                editor.putFloat("way_eat_pa", (float) way_eat_pa);
                editor.putFloat("year10scores", (float) year10scores);
                editor.apply();

                String jsonString = jsonObject.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DecimalFormat df = new DecimalFormat("#.##");//設定輸出格式
                        txt_phoneMarquee.append("\n伺服端：RMSSD：" + df.format(RMSSD) + "\nSDNN：" + df.format(sdNN) + "\nBPM：" + df.format(ecg_hr_mean));
                        txt_aiCal.setText("");
                        String trimmedJsonString = jsonString.substring(1, jsonString.length() - 1);
                        String[] lines = trimmedJsonString.split(",");
                        Pattern pattern = Pattern.compile("\"|\\\\");
                        for (String line : lines) {
                            String cleanedLine = pattern.matcher(line).replaceAll("");
                            txt_aiCal.append(cleanedLine + "\n");
                        }
                    }
                });

                String time = new SimpleDateFormat("yyyyMMddHHmmss",
                        Locale.getDefault()).format(System.currentTimeMillis());

                //做成JSON送去儲存(包含ID與時間)
                jsonObject.put("userId", userId);
                jsonObject.put("time", time);

                String jsonString2 = jsonObject.toString();
                controlMariaDB.userIdSave(jsonString2);

                editor = preferences.edit();
                editor.putBoolean("hasNewData", true);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}










