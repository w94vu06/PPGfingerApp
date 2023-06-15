package com.example.myapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.CategoryAdapter;
import com.example.myapplication.Adapter.ProfileAdapter;
import com.example.myapplication.Data.DataProfile;
import com.example.myapplication.R;

import java.util.ArrayList;

public class Profile extends Fragment {

    private RecyclerView recycler_profile;
    private RecyclerView.Adapter adapter_profile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recycler_profile = view.findViewById(R.id.recycler_profile);
        RecyclerViewProfile();
        return view;
    }

    public void RecyclerViewProfile(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recycler_profile.setLayoutManager(linearLayoutManager);
        recycler_profile.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        ArrayList<DataProfile> profileList = new ArrayList<>();
        profileList.add(new DataProfile("姓名 Name","已驗證"));
        profileList.add(new DataProfile("手機 Phone","已驗證"));
        profileList.add(new DataProfile("信箱 Email","已驗證"));
        profileList.add(new DataProfile("身高 Height",null));
        profileList.add(new DataProfile("體重 Weight",null));
        profileList.add(new DataProfile("年齡 Age",null));

        adapter_profile = new ProfileAdapter(profileList);
        recycler_profile.setAdapter(adapter_profile);
    }
}