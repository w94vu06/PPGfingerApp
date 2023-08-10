package com.example.myapplication.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.Data.DataFeature;
import com.example.myapplication.Data.DataImage;
import com.example.myapplication.Adapter.FeatureAdapter;
import com.example.myapplication.Adapter.ImageNetAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.WrapContentLinearLayoutManager;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomePage extends Fragment{

    /** UI **/
    private View view;
    private Banner banner;
    private TextView txt_filename;
    private Button btn_choose,btn_count,btn_addSelect,btn_addCancel,btn_addDone;
//    private ImageButton imgbtn_add,imgbtn_close;
    private RecyclerView recycler_feature;
    private RecyclerView.Adapter adapter_feature;
    private ChooserDialog chooserDialog;
    private Dialog dialog;
    private String filePath,xName;
    private List<String> checkFeature = new ArrayList<>();

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

//    ArrayList<DataFeature> featureList = new ArrayList<>();
    Unbinder unbinder;
    @BindViews({R.id.check_dbp, R.id.check_sbp, R.id.check_bs, R.id.check_hr, R.id.check_sdnn, R.id.check_rmssd})
    List<CheckBox> dialogCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_page,container,false);
        initParameter();
        initChooser();
        return view;
    }

    /** Feature資料添加 **/
    private void RecyclerViewFeature() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recycler_feature.setLayoutManager(linearLayoutManager);

        ArrayList<DataFeature> featureList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.#");//設定輸出格式

        String fatigue = preferences.getString("fatigue", "null");
        float mood_state =  preferences.getFloat("mood_state", 6);
        float BPc_dia = Float.parseFloat(df.format(preferences.getFloat("BPc_dia", 0)));
        float BPc_sys = Float.parseFloat(df.format(preferences.getFloat("BPc_sys", 0)));
        float BSc = Float.parseFloat(df.format(preferences.getFloat("BSc", 0)));
        float ecg_hr_mean = Float.parseFloat(df.format(preferences.getFloat("ecg_hr_mean", 0)));
        float RMSSD = Float.parseFloat(df.format(preferences.getFloat("RMSSD", 0)));
        float sdNN = Float.parseFloat(df.format(preferences.getFloat("sdNN", 0)));

        String mood_state_transfer;
        if ((int) mood_state == 0) {
            mood_state_transfer = "精神耗弱";
        } else if ((int) mood_state == 1) {
            mood_state_transfer = "輕度焦慮";
        }else if ((int) mood_state == 2) {
            mood_state_transfer = "中度焦慮";
        }else if ((int) mood_state == 3) {
            mood_state_transfer = "重度焦慮";
        } else if ((int) mood_state == 4) {
            mood_state_transfer = "憂鬱";
        } else if ((int) mood_state == 5) {
            mood_state_transfer = "平常心";
        } else {
            mood_state_transfer = "未測量";
        }

        featureList.add(new DataFeature("疲勞",String.valueOf(fatigue)));
        featureList.add(new DataFeature("心情",String.valueOf(mood_state_transfer)));
        featureList.add(new DataFeature("脈搏訊號",String.valueOf(BPc_dia)+"/\n"+String.valueOf(BPc_sys)));
        featureList.add(new DataFeature("飲食指標", String.valueOf(BSc)));
        featureList.add(new DataFeature("心率", String.valueOf(ecg_hr_mean)));
        featureList.add(new DataFeature("RMSSD", String.valueOf(RMSSD)));
        featureList.add(new DataFeature("SDNN", String.valueOf(sdNN)));

        adapter_feature = new FeatureAdapter(featureList);
        recycler_feature.setAdapter(adapter_feature);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            RecyclerViewFeature();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        banner.start();
        RecyclerViewFeature();
    }

    @Override
    public void onPause(){
        super.onPause();
        banner.stop();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        try {
            unbinder.unbind();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /** 宣告參數 **/
    public void initParameter(){
        banner = view.findViewById(R.id.banner);
        recycler_feature = view.findViewById(R.id.recycler_feature);
        txt_filename = view.findViewById(R.id.txt_filename);
        btn_choose = view.findViewById(R.id.btn_choose);
        btn_count = view.findViewById(R.id.btn_count);
        btn_count.setOnClickListener(lis);
        useBanner();
        RecyclerViewFeature();
    }

    public void initChooser(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                chooserDialog = new ChooserDialog(getActivity())
                        .withStartFile("/storage/emulated/0/")
                        .withOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                dialogInterface.cancel();
                            }
                        })
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String dir, File dirFile) {
                                filePath = dir;
                                File file = new File(dir);
                                xName = file.getName();
                                Log.d("xName",xName);
                                txt_filename.setText(xName);
                                txt_filename.setTextSize(16);
                            }
                        })
                        .withOnBackPressedListener(dialog -> chooserDialog.goBack())
                        .withOnLastBackPressedListener(dialog -> dialog.cancel());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_choose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chooserDialog.build();
                                chooserDialog.show();
                            }
                        });
                    }
                });
            }
        });
        thread.start();
    }

    /** 新增Feature Dialog **/
    public void buildDialog(){
        dialog = new Dialog(getActivity(),R.style.custom_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add,null);
        dialog.setContentView(dialogView);

        /** 宣告UI參數 **/
        btn_addSelect = dialogView.findViewById(R.id.btn_addSelect);
        btn_addCancel = dialogView.findViewById(R.id.btn_addCancel);
        btn_addDone = dialogView.findViewById(R.id.btn_addDone);
        unbinder = ButterKnife.bind(this,dialogView);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        btn_addSelect.setOnClickListener(lis);
        btn_addDone.setOnClickListener(lis);
        btn_addCancel.setOnClickListener(lis);
    }

    @OnClick({R.id.check_dbp, R.id.check_sbp, R.id.check_bs, R.id.check_hr, R.id.check_sdnn, R.id.check_rmssd})
    public void changFeature(CheckBox checkBox){
        checkFeature = CommonUtil.getMany(dialogCheck);
    }

    /** 圖片輪播 **/
    public void useBanner(){
        banner.addBannerLifecycleObserver(this)
                .setAdapter(new ImageNetAdapter(DataImage.getTestData3()))
                .setIndicator(new CircleIndicator(getContext()))
                .setBannerRound(60);
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_choose:
                    break;
                case R.id.btn_count:
                    break;
                case R.id.btn_addCancel:
                    dialog.dismiss();
                    break;
//                case R.id.btn_dialogCancel:
//                    break;
                case R.id.btn_addDone:
                    if (checkFeature.size() == 0) {
                        dialog.dismiss();
                    }
//                    notifyDataChanged();
                    dialog.dismiss();
                    break;
            }
        }
    };

//    private void notifyDataChanged(){
//        if (featureList != null){
//            featureList.clear();
//            adapter_feature.notifyItemRangeRemoved(0,featureList.size());
//            //舒張壓:0、收縮壓:1、血糖:2、心率:3、SDNN:4、RMSSD:5
//            Random x = new Random();
//            for (int i=0; i<checkFeature.size(); i++){
//                Double y = (double)Math.round((x.nextDouble()*100.0)/100.0);
//                featureList.add(new DataFeature(checkFeature.get(i),y));
//            }
//            adapter_feature.notifyItemRangeInserted(0,checkFeature.size());
//        }
//    }
}