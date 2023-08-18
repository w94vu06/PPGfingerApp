package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Data.DataRecord;
import com.example.myapplication.Fragment.Record;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>{

    private final RecordAdapter.OnItemListener onItemListener;
    //    ArrayList<DataRecord> recordList;
    ArrayList<HashMap<String, String>> recordList;
    public RecordAdapter(ArrayList<HashMap<String, String>> recordList, RecordAdapter.OnItemListener onItemListener){
        this.recordList = recordList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_record,parent,false);
        return new RecordAdapter.RecordViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.txt_recordDate.setText(recordList.get(position).get("recordDate"));
        holder.txt_bpm.setText((recordList.get(position).get("ecg_hr_mean")));
        holder.txt_eatIndex.setText((recordList.get(position).get("BSc")));
        holder.txt_BPC.setText((recordList.get(position).get("BPc_sys")+"/"+recordList.get(position).get("BPc_dia")));

//        holder.txt_recordDate.setText(recordList.get(position).getDate());
//        holder.txt_recordTime.setText(recordList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txt_recordDate,txt_bpm,txt_eatIndex,txt_BPC;
        private final RecordAdapter.OnItemListener onItemListener;

        public RecordViewHolder(@NonNull View itemView, RecordAdapter.OnItemListener onItemListener) {
            super(itemView);
            txt_recordDate = itemView.findViewById(R.id.txt_recordDate);

            txt_bpm = itemView.findViewById(R.id.txt_bpm);
            txt_eatIndex = itemView.findViewById(R.id.txt_eatIndex);
            txt_BPC = itemView.findViewById(R.id.txt_BPC);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
