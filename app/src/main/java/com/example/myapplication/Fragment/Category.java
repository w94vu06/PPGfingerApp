package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.CategoryAdapter;
import com.example.myapplication.Adapter.DetailAdapter;
import com.example.myapplication.Adapter.FeatureAdapter;
import com.example.myapplication.ControlMariaDB;
import com.example.myapplication.Data.DataCategory;
import com.example.myapplication.Data.DataDetail;
import com.example.myapplication.Data.DataFeature;
import com.example.myapplication.MariaDBCallback;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Category extends Fragment implements CategoryAdapter.OnItemListener, MariaDBCallback {

    private RecyclerView recycler_category, recycler_detail;
    private RecyclerView.Adapter adapter_category, adapter_detail;
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
        view = inflater.inflate(R.layout.fragment_category, container, false);
        recycler_category = view.findViewById(R.id.recycler_category);
        recycler_detail = view.findViewById(R.id.recycler_detail);

        try {
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
        if (userId == null) {
            controlMariaDB.userIdRead(json);
        }
    }

    @Override
    public void onResult(String result) {
        Log.d("rrrr", "IdDataBack: " + result);
        RecyclerViewCategory();
        RecyclerViewDetail();
    }

    @Override
    public void onSave(String result) {

    }

    private void RecyclerViewCategory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_category.setLayoutManager(linearLayoutManager);

        ArrayList<DataCategory> categoryList = new ArrayList<>();
        categoryList.add(new DataCategory("ai", "AI"));
        categoryList.add(new DataCategory("emotional", "情緒"));
        categoryList.add(new DataCategory("body_ai", "生理AI"));
        categoryList.add(new DataCategory("rsp", "呼吸"));
        categoryList.add(new DataCategory("disease", "疾病"));
        categoryList.add(new DataCategory("body_index", "生理指數"));
        categoryList.add(new DataCategory("hrv", "HRV"));
        categoryList.add(new DataCategory("heartbeat", "心率"));
        categoryList.add(new DataCategory("signal", "訊號"));

        adapter_category = new CategoryAdapter(categoryList, this);
        recycler_category.setAdapter(adapter_category);
    }

    private void RecyclerViewDetail() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_detail.setLayoutManager(linearLayoutManager);

        ArrayList<DataDetail> dataDetails = DataDetail.getList_AI();
        dataDetails.add(new DataDetail("心臟年紀 Heart Age", "35"));
        dataDetails.add(new DataDetail("情緒AI Emotion AI", "155"));

        adapter_detail = new DetailAdapter(dataDetails);
        recycler_detail.setAdapter(adapter_detail);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                ArrayList<DataDetail> dataDetails_AI = DataDetail.getList_AI();
                dataDetails_AI.add(new DataDetail("心臟年紀 Heart Age", "35"));
                dataDetails_AI.add(new DataDetail("情緒AI Emotion AI", "155"));

                adapter_detail = new DetailAdapter(dataDetails_AI);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 1:
                ArrayList<DataDetail> dataDetails_Emotional = DataDetail.getList_Emotional();

                dataDetails_Emotional.add(new DataDetail("活力指數", "35"));
                dataDetails_Emotional.add(new DataDetail("壓力", "155"));

                adapter_detail = new DetailAdapter(dataDetails_Emotional);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 2:
                ArrayList<DataDetail> dataDetails_Body_AI = DataDetail.getList_Body_AI();

                dataDetails_Body_AI.add(new DataDetail("舒張壓", "35"));
                dataDetails_Body_AI.add(new DataDetail("收縮壓", "155"));

                adapter_detail = new DetailAdapter(dataDetails_Body_AI);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 3:
                ArrayList<DataDetail> dataDetails_RSP = DataDetail.getList_RSP();

                dataDetails_RSP.add(new DataDetail("RSP", "35"));
                dataDetails_RSP.add(new DataDetail("PRQ", "155"));

                adapter_detail = new DetailAdapter(dataDetails_RSP);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 4:
                ArrayList<DataDetail> dataDetails_Disease = DataDetail.getList_Disease();

                dataDetails_Disease.add(new DataDetail("AF", "35"));
                dataDetails_Disease.add(new DataDetail("PVC", "155"));

                adapter_detail = new DetailAdapter(dataDetails_Disease);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 5:
                ArrayList<DataDetail> dataDetails_Body_index = DataDetail.getList_Body_index();

                dataDetails_Body_index.add(new DataDetail("健康分數", "35"));

                adapter_detail = new DetailAdapter(dataDetails_Body_index);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 6:
                ArrayList<DataDetail> dataDetails_HRV = DataDetail.getList_HRV();

                dataDetails_HRV.add(new DataDetail("SDNN", "35"));
                dataDetails_HRV.add(new DataDetail("RMSSD", "155"));

                adapter_detail = new DetailAdapter(dataDetails_HRV);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 7:
                ArrayList<DataDetail> dataDetails_Heartbeat = DataDetail.getList_Heartbeat();

                dataDetails_Heartbeat.add(new DataDetail("MaxValue", "35"));
                dataDetails_Heartbeat.add(new DataDetail("MinValue", "155"));

                adapter_detail = new DetailAdapter(dataDetails_Heartbeat);
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 8:
                ArrayList<DataDetail> dataDetails_signal = DataDetail.getList_signal();

                dataDetails_signal.add(new DataDetail("紅三燈", "35"));
                dataDetails_signal.add(new DataDetail("訊號分數", "155"));

                adapter_detail = new DetailAdapter(dataDetails_signal);
                recycler_detail.setAdapter(adapter_detail);
                break;
        }
    }


}