package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Adapter.ProfileAdapter;
import com.example.myapplication.Data.DataProfile;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Profile extends Fragment {

    private RecyclerView recycler_profile;
    private RecyclerView.Adapter adapter_profile;

    private String name, email, phone, birth;
    private String old, height, weight, sex, smokes, diabetes, hbp;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private TextView titleName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        name = preferences.getString("ProfileName", null);
        phone = preferences.getString("ProfilePhone", "無資料");
        email = preferences.getString("ProfileEmail", "無資料");
        height = String.valueOf(preferences.getInt("ProfileHeight", 0));
        weight = String.valueOf(preferences.getInt("ProfileWeight", 0));
        old = String.valueOf(preferences.getInt("ProfileOld", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recycler_profile = view.findViewById(R.id.recycler_profile);
        titleName = view.findViewById(R.id.txt_name);
        recyclerViewProfile();
        return view;
    }

    public void recyclerViewProfile() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_profile.setLayoutManager(linearLayoutManager);
        recycler_profile.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        ArrayList<DataProfile> profileList = new ArrayList<>();
        try {
            titleName.setText(name);
            profileList.add(new DataProfile("手機 Phone", phone));
            profileList.add(new DataProfile("信箱 Email", email));
            profileList.add(new DataProfile("身高 Height", height));
            profileList.add(new DataProfile("體重 Weight", weight));
            profileList.add(new DataProfile("年齡 Age", old));
        } catch (Exception e) {
            System.out.println(e);
        }
        adapter_profile = new ProfileAdapter(profileList);
        recycler_profile.setAdapter(adapter_profile);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (name == null) {
            recyclerViewProfile();
        }
    }
}