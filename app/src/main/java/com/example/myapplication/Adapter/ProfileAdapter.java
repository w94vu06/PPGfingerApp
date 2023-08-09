package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.DataProfile;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    ArrayList<DataProfile> profileList;

    public ProfileAdapter(ArrayList<DataProfile> profileList){
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate  = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile,parent,false);
        return new ProfileViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.txt_profileTitle.setText(profileList.get(position).getProfile_title());
        holder.txt_profileData.setText(profileList.get(position).getProfile_data());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_profileTitle;
        private final TextView txt_profileData;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_profileTitle = itemView.findViewById(R.id.txt_profileTitle);
            txt_profileData = itemView.findViewById(R.id.txt_profileData);
            txt_profileData.setTextSize(15);
        }
    }
}
