package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.DataFeature;
import com.example.myapplication.R;

import java.util.ArrayList;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
    ArrayList<DataFeature> featureList;

    public FeatureAdapter(ArrayList<DataFeature> featureList) {
        this.featureList = featureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_feature, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_feature.setText(featureList.get(position).getName());
        holder.txt_val.setText(featureList.get(position).getVal());
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_feature;
        TextView txt_val;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_feature = itemView.findViewById(R.id.txt_feature);
            txt_val = itemView.findViewById(R.id.txt_val);
        }
    }
}
