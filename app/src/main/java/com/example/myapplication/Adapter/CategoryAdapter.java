package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Data.DataCategory;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final OnItemListener onItemListener;
    ArrayList<DataCategory> categoryList;

    public CategoryAdapter(ArrayList<DataCategory> categoryList, OnItemListener onItemListener){
        this.categoryList = categoryList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_category,parent,false);
        return new CategoryViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.txt_category.setText(categoryList.get(position).getCate());
        int drawID = holder.itemView.getContext().getResources().getIdentifier(categoryList.get(position).getImg(),"drawable",holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawID)
                .into(holder.img_category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView img_category;
        private final TextView txt_category;
        private final CategoryAdapter.OnItemListener onItemListener;
        public CategoryViewHolder(@NonNull View itemView, CategoryAdapter.OnItemListener onItemListener) {
            super(itemView);
            img_category = itemView.findViewById(R.id.img_category);
            txt_category = itemView.findViewById(R.id.txt_category);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
