package com.example.myapplication.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.CapabilityParams;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Data.DataFeature;
import com.example.myapplication.Data.DataImage;
import com.example.myapplication.Adapter.FeatureAdapter;
import com.example.myapplication.Adapter.ImageNetAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Util.CommonUtil;
import com.example.myapplication.WrapContentLinearLayoutManager;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomePage extends Fragment {

    /** UI **/
    private View view;
    private Banner banner;
    private TextView txt_filename;
    private Button btn_choose,btn_count,btn_dialogCancel,btn_dialogSure;
    private ImageButton imgbtn_add,imgbtn_close;
    private RecyclerView recycler_feature;
    private RecyclerView.Adapter adapter_feature;
    private ChooserDialog chooserDialog;
    private Dialog dialog;
    private String filePath,xName;
    private List<String> checkFeature = new ArrayList<>();

    ArrayList<DataFeature> featureList = new ArrayList<>();
    Unbinder unbinder;
    @BindViews({R.id.check_dbp, R.id.check_sbp, R.id.check_bs, R.id.check_hr, R.id.check_sdnn, R.id.check_rmssd})
    List<CheckBox> dialogCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_page,container,false);
        initParameter();
        initChooser();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        banner.start();
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
        imgbtn_add = view.findViewById(R.id.imgbtn_add);
        btn_count.setOnClickListener(lis);
        imgbtn_add.setOnClickListener(lis);
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
        imgbtn_close = dialogView.findViewById(R.id.imgbtn_close);
        btn_dialogCancel = dialogView.findViewById(R.id.btn_dialogCancel);
        btn_dialogSure = dialogView.findViewById(R.id.btn_dialogSure);
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
        imgbtn_close.setOnClickListener(lis);
        btn_dialogSure.setOnClickListener(lis);
        btn_dialogCancel.setOnClickListener(lis);
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
                case R.id.imgbtn_add:
                    buildDialog();
                    break;
                case R.id.imgbtn_close:
                    dialog.dismiss();
                    break;
                case R.id.btn_dialogCancel:
                    break;
                case R.id.btn_dialogSure:
                    Toast.makeText(getContext(),checkFeature.toString(),Toast.LENGTH_SHORT).show();
                    notifyDataChanged();
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void RecyclerViewFeature(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recycler_feature.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        featureList = new ArrayList<>();
        if (checkFeature.size()==0) {
            featureList.add(new DataFeature("無資料", 0.0));
        }
        adapter_feature = new FeatureAdapter(featureList);
        recycler_feature.setAdapter(adapter_feature);
    }

    private void notifyDataChanged(){
        if (featureList != null){
            featureList.clear();
            adapter_feature.notifyItemRangeRemoved(0,featureList.size());
            Random x = new Random();
            for (int i=0; i<checkFeature.size(); i++){
                Double y = (double)Math.round((x.nextDouble()*100.0)/100.0);
                featureList.add(new DataFeature(checkFeature.get(i),y));
            }
            adapter_feature.notifyItemRangeInserted(0,checkFeature.size());
        }
    }
    
}