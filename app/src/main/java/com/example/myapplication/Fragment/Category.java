package com.example.myapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.CategoryAdapter;
import com.example.myapplication.Adapter.DetailAdapter;
import com.example.myapplication.Adapter.FeatureAdapter;
import com.example.myapplication.Data.DataCategory;
import com.example.myapplication.Data.DataDetail;
import com.example.myapplication.Data.DataFeature;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class Category extends Fragment implements CategoryAdapter.OnItemListener {

    private RecyclerView recycler_category,recycler_detail;
    private RecyclerView.Adapter adapter_category,adapter_detail;
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);
        recycler_category = view.findViewById(R.id.recycler_category);
        recycler_detail = view.findViewById(R.id.recycler_detail);
        RecyclerViewCategory();
        RecyclerViewDetail();
        return view;
    }

    private void RecyclerViewCategory(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recycler_category.setLayoutManager(linearLayoutManager);

        ArrayList<DataCategory> categoryList = new ArrayList<>();
        categoryList.add(new DataCategory("ai","AI"));
        categoryList.add(new DataCategory("emotional","情緒"));
        categoryList.add(new DataCategory("body_ai","生理AI"));
        categoryList.add(new DataCategory("rsp","呼吸"));
        categoryList.add(new DataCategory("disease","疾病"));
        categoryList.add(new DataCategory("body_index","生理指數"));
        categoryList.add(new DataCategory("hrv","HRV"));
        categoryList.add(new DataCategory("heartbeat","心率"));
        categoryList.add(new DataCategory("signal","訊號"));

        adapter_category = new CategoryAdapter(categoryList,this);
        recycler_category.setAdapter(adapter_category);
    }

    private void RecyclerViewDetail(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recycler_detail.setLayoutManager(linearLayoutManager);

        adapter_detail = new DetailAdapter(DataDetail.getList_AI());
        recycler_detail.setAdapter(adapter_detail);
    }

    @Override
    public void onItemClick(int position) {
        switch (position){
            case 0:
                adapter_detail = new DetailAdapter(DataDetail.getList_AI());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 1:
                adapter_detail = new DetailAdapter(DataDetail.getList_Emotional());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 2:
                adapter_detail = new DetailAdapter(DataDetail.getList_Body_AI());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 3:
                adapter_detail = new DetailAdapter(DataDetail.getList_RSP());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 4:
                adapter_detail = new DetailAdapter(DataDetail.getList_Disease());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 5:
                adapter_detail = new DetailAdapter(DataDetail.getList_Body_index());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 6:
                adapter_detail = new DetailAdapter(DataDetail.getList_HRV());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 7:
                adapter_detail = new DetailAdapter(DataDetail.getList_Heartbeat());
                recycler_detail.setAdapter(adapter_detail);
                break;
            case 8:
                adapter_detail = new DetailAdapter(DataDetail.getList_signal());
                recycler_detail.setAdapter(adapter_detail);
                break;
        }
    }
}