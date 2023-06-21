package com.example.myapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.CategoryAdapter;
import com.example.myapplication.Adapter.ProfileAdapter;
import com.example.myapplication.Data.DataProfile;
import com.example.myapplication.R;
import com.example.myapplication.SignUpActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Profile extends Fragment {

    private RecyclerView recycler_profile;
    private RecyclerView.Adapter adapter_profile;

    private String userName;
    private String email;
    private String phone;
    private String birth;
    private int old;
    private int height;
    private int weight;
    private int sex;
    private int smokes;
    private int diabetes;
    private int hbp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recycler_profile = view.findViewById(R.id.recycler_profile);

        return view;
    }

    public void RecyclerViewProfile(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recycler_profile.setLayoutManager(linearLayoutManager);
        recycler_profile.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        ArrayList<DataProfile> profileList = new ArrayList<>();
        profileList.add(new DataProfile("姓名 Name",userName));
        profileList.add(new DataProfile("手機 Phone",phone));
        profileList.add(new DataProfile("信箱 Email",email));
        profileList.add(new DataProfile("身高 Height",String.valueOf(height)));
        profileList.add(new DataProfile("體重 Weight",String.valueOf(weight)));
        profileList.add(new DataProfile("年齡 Age",String.valueOf(old)));

        adapter_profile = new ProfileAdapter(profileList);
        recycler_profile.setAdapter(adapter_profile);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收profile
     **/
    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(SignUpActivity.MessageEvent event) {
        // 收到MessageEvent時要做的事寫在這裡
        String profileMsg = event.getMessage();
        Log.d("rrrr", "onProfilePage: " + profileMsg);
        unpackJson(profileMsg);
    }

    private void unpackJson(String json) {
        new Thread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                userName = jsonObject.getString("userName");
                email = jsonObject.getString("email");
                phone = jsonObject.getString("phone");
                birth = jsonObject.getString("birth");
                old = jsonObject.getInt("old");
                height = jsonObject.getInt("height");
                weight = jsonObject.getInt("weight");
                sex = jsonObject.getInt("sex");
                smokes = jsonObject.getInt("smokes");
                diabetes = jsonObject.getInt("diabetes");
                hbp = jsonObject.getInt("hbp");
                Log.d("rrrr", "onProfilePage: " + userName+email+phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
        RecyclerViewProfile();
    }
}