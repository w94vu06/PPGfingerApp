package com.example.myapplication.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;

import com.example.myapplication.Adapter.RecordAdapter;
import com.example.myapplication.ControlMariaDB;
import com.example.myapplication.Data.DataRecord;
import com.example.myapplication.MariaDBCallback;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Record extends Fragment implements RecordAdapter.OnItemListener, MariaDBCallback {

    //    private RecyclerView recycler_category, recycler_detail;
//    private RecyclerView.Adapter adapter_category, adapter_detail;
    private EditText edit_month;
    private RadioButton radiobtn_o2n, radiobtn_n2o;
    private RecyclerView recycler_record;
    private RecyclerView.Adapter adapter_record;
    private View view;

    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);
        edit_month = view.findViewById(R.id.edit_month);
        radiobtn_o2n = view.findViewById(R.id.radiobtn_o2n);
        radiobtn_n2o = view.findViewById(R.id.radiobtn_n2o);
        recycler_record = view.findViewById(R.id.recycler_record);
        createMonthDialog(edit_month);
        RecyclerViewRecord();

        try {
            boolean hasNewData = preferences.getBoolean("hasNewData", false);
            if (hasNewData) {

            }
            setCategoryData();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    public void setCategoryData() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        String userId = preferences.getString("ProfileId", null);
        jsonObject.put("userId", userId);

        String json = jsonObject.toString();
        controlMariaDB.userIdRead(json);
    }

    @Override
    public void onResult(String result) {
//        RecyclerViewCategory();
//        RecyclerViewDetail();
    }

    @Override
    public void onSave(String result) {

    }

    @Override
    public void onTest(String result) {

    }

    /**
     * 選擇月份
     **/
    public void createMonthDialog(final EditText edt) {
        MonthPickDialog dialog = new MonthPickDialog(getActivity());
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    dialog.showDialog();
                    return true;
                }
                return false;
            }
        });

        dialog.onDialogRespond = new MonthPickDialog.OnDialogRespond() {
            @Override
            public void onRespond(String selected) {
                edt.setText(selected);
            }
        };
    }

    /**
     * 紀錄顯示
     **/
    private void RecyclerViewRecord() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_record.setLayoutManager(linearLayoutManager);

        ArrayList<DataRecord> recordList = new ArrayList<>();
        recordList.add(new DataRecord("2023-07-19", "17:00"));
        recordList.add(new DataRecord("2023-07-19", "17:05"));
        recordList.add(new DataRecord("2023-07-19", "17:10"));
        recordList.add(new DataRecord("2023-07-19", "17:15"));
        recordList.add(new DataRecord("2023-07-19", "17:20"));

        adapter_record = new RecordAdapter(recordList, this);
        recycler_record.setAdapter(adapter_record);
    }

    @Override
    public void onItemClick(int position) {

    }

    /** Category資料添加 **/
//    private void RecyclerViewCategory() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recycler_category.setLayoutManager(linearLayoutManager);
//
//        ArrayList<DataCategory> categoryList = new ArrayList<>();
//        categoryList.add(new DataCategory("ai", "AI"));
//        categoryList.add(new DataCategory("emotional", "情緒"));
//        categoryList.add(new DataCategory("body_ai", "生理AI"));
//        categoryList.add(new DataCategory("rsp", "呼吸"));
//        categoryList.add(new DataCategory("disease", "疾病"));
//        categoryList.add(new DataCategory("body_index", "生理指數"));
//        categoryList.add(new DataCategory("hrv", "HRV"));
//        categoryList.add(new DataCategory("heartbeat", "心率"));
//        categoryList.add(new DataCategory("signal", "訊號"));
//
//        adapter_category = new CategoryAdapter(categoryList, this);
//        recycler_category.setAdapter(adapter_category);
//    }

    /** Detail資料添加 **/
//    private void RecyclerViewDetail() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recycler_detail.setLayoutManager(linearLayoutManager);
//
//        ArrayList<DataDetail> dataDetails = DataDetail.getList_AI();
//        dataDetails.add(new DataDetail("心臟年紀 Heart Age", "35"));
//        dataDetails.add(new DataDetail("情緒AI Emotion AI", "155"));
//
//        adapter_detail = new DetailAdapter(dataDetails);
//        recycler_detail.setAdapter(adapter_detail);
//    }
//
//    @Override
//    public void onItemClick(int position) {
//        switch (position) {
//            case 0:
//                ArrayList<DataDetail> dataDetails_AI = DataDetail.getList_AI();
//                dataDetails_AI.add(new DataDetail("心臟年紀 Heart Age", "35"));
//                dataDetails_AI.add(new DataDetail("情緒AI Emotion AI", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_AI);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 1:
//                ArrayList<DataDetail> dataDetails_Emotional = DataDetail.getList_Emotional();
//
//                dataDetails_Emotional.add(new DataDetail("活力指數", "35"));
//                dataDetails_Emotional.add(new DataDetail("壓力", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_Emotional);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 2:
//                ArrayList<DataDetail> dataDetails_Body_AI = DataDetail.getList_Body_AI();
//
//                dataDetails_Body_AI.add(new DataDetail("舒張壓", "35"));
//                dataDetails_Body_AI.add(new DataDetail("收縮壓", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_Body_AI);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 3:
//                ArrayList<DataDetail> dataDetails_RSP = DataDetail.getList_RSP();
//
//                dataDetails_RSP.add(new DataDetail("RSP", "35"));
//                dataDetails_RSP.add(new DataDetail("PRQ", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_RSP);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 4:
//                ArrayList<DataDetail> dataDetails_Disease = DataDetail.getList_Disease();
//
//                dataDetails_Disease.add(new DataDetail("AF", "35"));
//                dataDetails_Disease.add(new DataDetail("PVC", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_Disease);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 5:
//                ArrayList<DataDetail> dataDetails_Body_index = DataDetail.getList_Body_index();
//
//                dataDetails_Body_index.add(new DataDetail("健康分數", "35"));
//
//                adapter_detail = new DetailAdapter(dataDetails_Body_index);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 6:
//                ArrayList<DataDetail> dataDetails_HRV = DataDetail.getList_HRV();
//
//                dataDetails_HRV.add(new DataDetail("SDNN", "35"));
//                dataDetails_HRV.add(new DataDetail("RMSSD", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_HRV);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 7:
//                ArrayList<DataDetail> dataDetails_Heartbeat = DataDetail.getList_Heartbeat();
//
//                dataDetails_Heartbeat.add(new DataDetail("MaxValue", "35"));
//                dataDetails_Heartbeat.add(new DataDetail("MinValue", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_Heartbeat);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//            case 8:
//                ArrayList<DataDetail> dataDetails_signal = DataDetail.getList_signal();
//
//                dataDetails_signal.add(new DataDetail("紅三燈", "35"));
//                dataDetails_signal.add(new DataDetail("訊號分數", "155"));
//
//                adapter_detail = new DetailAdapter(dataDetails_signal);
//                recycler_detail.setAdapter(adapter_detail);
//                break;
//        }
//    }


}

class MonthPickDialog {
    private Activity activity;
    OnDialogRespond onDialogRespond;

    public MonthPickDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        Dialog monthDialog = new Dialog(this.activity, R.style.MonthDialog);
        View contentView = LayoutInflater.from(this.activity).inflate(R.layout.dialog_month, null);
        monthDialog.setContentView(contentView);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        params.width = activity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(params);
        monthDialog.getWindow().setGravity(Gravity.BOTTOM);
        monthDialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        monthDialog.show();

        NumberPicker np_year, np_month;
        Button btn_monthCancel, btn_monthDone;
        np_year = contentView.findViewById(R.id.np_year);
        np_month = contentView.findViewById(R.id.np_month);
        btn_monthCancel = contentView.findViewById(R.id.btn_monthCancel);
        btn_monthDone = contentView.findViewById(R.id.btn_monthDone);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        int year = calendar.get(Calendar.YEAR);

        np_year.setMaxValue(year + 20);
        np_year.setMinValue(year - 20);
        np_year.setMaxValue(year + 20);
        np_year.setMinValue(year - 20);
        np_year.setValue(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)));
        np_month.setMaxValue(12);
        np_month.setMinValue(1);
        np_month.setValue(Integer.parseInt(new SimpleDateFormat("MM").format(date)));

        btn_monthCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthDialog.dismiss();
            }
        });
        btn_monthDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.format("%04d-%02d", np_year.getValue(), np_month.getValue());
                onDialogRespond.onRespond(s);
                monthDialog.dismiss();
            }
        });
    }

    interface OnDialogRespond {
        void onRespond(String selected);
    }
}