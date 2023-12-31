package com.example.myapplication.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.RecordAdapter;
import com.example.myapplication.CameraActivity;
import com.example.myapplication.ControlMariaDB;
import com.example.myapplication.Data.DataRecord;
import com.example.myapplication.MariaDBCallback;
import com.example.myapplication.R;
import com.example.myapplication.SignInActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class Record extends Fragment implements RecordAdapter.OnItemListener, MariaDBCallback {

    //    private RecyclerView recycler_category, recycler_detail;
//    private RecyclerView.Adapter adapter_category, adapter_detail;
    private EditText edit_month;
    private RadioGroup sortRadioGroup;
    private RadioButton radioBtn_o2n, radioBtn_n2o;
    private RecyclerView recycler_record;
    private RecyclerView.Adapter adapter_record;
    private View view;
    private BottomAppBar bottomAppBar;
    private ProgressDialog progressDialog;
    ControlMariaDB controlMariaDB = new ControlMariaDB(this);
    private DataRecord dataRecordViewModel;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String selectYear;
    private String selectMonth;
    private String selectDay;
    private String dateStr_date;
    private String dateStr_time;
    Category category = new Category();
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;



    ArrayList<HashMap<String, String>> recordArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_record, container, false);
        dataRecordViewModel = new ViewModelProvider(requireActivity()).get(DataRecord.class);
        recycler_record = view.findViewById(R.id.recycler_record);
        edit_month = view.findViewById(R.id.edit_month);

        bottomAppBar = getActivity().findViewById(R.id.bar);

        sortRadioGroup = view.findViewById(R.id.sortRadioGroup);
        radioBtn_o2n = view.findViewById(R.id.radiobtn_o2n);
        radioBtn_n2o = view.findViewById(R.id.radiobtn_n2o);

        setSortRadioGroup();

        progressDialog = new ProgressDialog(getActivity());
        createMonthDialog(edit_month);
        Log.d("eeee", "onCreateView: ");

        getPreloadData();
        return view;
    }

    private void getPreloadData() {
        String preloadData = preferences.getString("recordData",null);
        Log.d("hhhh", "getPreloadData: "+preloadData);
        if (preloadData != null) {
            catchData(preloadData);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("123456789", "onResume: ");
        boolean hasNewData = preferences.getBoolean("hasNewData", false);
        if (hasNewData) {
            readDateData();
        }
    }

    @Override
    public void onResult(String result) {
    }

    @Override
    public void onTest(String result) {

    }

    /**
     * 選擇月份
     **/
    @SuppressLint("ClickableViewAccessibility")
    public void createMonthDialog(final EditText edt) {
        MonthPickDialog monthPickDialog = new MonthPickDialog(getActivity());
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    monthPickDialog.showDialog();
                    return true;
                }
                return false;
            }
        });

        monthPickDialog.onDialogRespond = new MonthPickDialog.OnDialogRespond() {
            @Override
            public void onRespond(String selected) {
                edt.setText(selected);
                selectYear = selected.substring(0, 4);
                selectMonth = selected.substring(5, 7);
                selectDay = selected.substring(8, 10);
                Log.d("vvvv", "selectYear: "+selectYear+"\nselectMonth: "+selectMonth+"\nselectDay: "+selectDay);
                readDateData();
                progressDialog.show();
            }
        };
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void readDateData() {
        JSONObject jsonObject = new JSONObject();
        String userId = preferences.getString("ProfileId", "888889");
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("selectYear", selectYear);
            jsonObject.put("selectMonth", selectMonth);
            jsonObject.put("selectDay", selectDay);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            String json = jsonObject.toString();
            controlMariaDB.IdAndDateReadData(json);
        }).start();
    }

    @Override
    public void onSave(String result) {
        if (result.equals("noData")) {
            hideProgressDialog();
            Toast.makeText(getActivity(), "查無量測資料", Toast.LENGTH_SHORT).show();
        } else {
            editor = preferences.edit();
            editor.putString("recordData", result);
            editor.apply();

            catchData(result);
        }
    }

    private void catchData(String json) {
        if (json != null) {
            json = json.replaceAll("NaN", "null");
            json = json.replaceAll("null", "0.0");
        } else {
            return;
        }
        String finalJson = json;
        new Thread(() -> {
            try {
                recordArrayList.clear();
                JSONArray jsonArray = new JSONArray(finalJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double AF_Similarity = jsonObject.getDouble("AF_Similarity");
                    double AI_bshl = jsonObject.getDouble("AI_bshl");
                    double AI_bshl_pa = jsonObject.getDouble("AI_bshl_pa");
                    double AI_dis = jsonObject.getDouble("AI_dis");
                    double AI_dis_pa = jsonObject.getDouble("AI_dis_pa");
                    double AI_medic = jsonObject.getDouble("AI_medic");
                    double BMI = jsonObject.getDouble("BMI");
                    double BPc_dia = jsonObject.getDouble("BPc_dia");
                    double BPc_sys = jsonObject.getDouble("BPc_sys");
                    double BSc = jsonObject.getDouble("BSc");
                    double Lf_Hf = jsonObject.getDouble("Lf_Hf");
                    double RMSSD = jsonObject.getDouble("RMSSD");
                    double Shannon_h = jsonObject.getDouble("Shannon_h");
                    double Total_Power = jsonObject.getDouble("Total_Power");
                    double ULF = jsonObject.getDouble("ULF");
                    double VHF = jsonObject.getDouble("VHF");
                    double VLF = jsonObject.getDouble("VLF");
                    double dis0bs1_0 = jsonObject.getDouble("dis0bs1_0");
                    double dis0bs1_1 = jsonObject.getDouble("dis0bs1_1");
                    double dis1bs1_0 = jsonObject.getDouble("dis1bs1_0");
                    double dis1bs1_1 = jsonObject.getDouble("dis1bs1_1");
                    double ecg_hr_max = jsonObject.getDouble("ecg_hr_max");
                    double ecg_hr_mean = jsonObject.getDouble("ecg_hr_mean");
                    double ecg_hr_min = jsonObject.getDouble("ecg_hr_min");
                    double ecg_rsp = jsonObject.getDouble("ecg_rsp");
                    String fatigue = jsonObject.getString("fatigue");
                    double hbp = jsonObject.getDouble("hbp");
                    double hr_rsp_rate = jsonObject.getDouble("hr_rsp_rate");
                    double meanNN = jsonObject.getDouble("meanNN");
                    double mood_state = jsonObject.getDouble("mood_state");
                    double pNN50 = jsonObject.getDouble("pNN50");
                    double sdNN = jsonObject.getDouble("sdNN");
                    double total_scores = jsonObject.getDouble("total_scores");
                    double way_eat = jsonObject.getDouble("way_eat");
                    double way_eat_pa = jsonObject.getDouble("way_eat_pa");
                    double year10scores = jsonObject.getDouble("year10scores");

                    String time = jsonObject.getString("time");
                    formatDateTime(time);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("originalData", finalJson);
                    hashMap.put("recordDate", String.valueOf(dateStr_date));

                    hashMap.put("ecg_hr_mean", String.valueOf(ecg_hr_mean));

                    if (BSc == 6666.0) {
                        BSc = 0.0;
                        hashMap.put("BSc", String.valueOf(BSc));
                    } else {
                        hashMap.put("BSc", String.valueOf(BSc));
                    }
                    if (BPc_dia == 6666.0) {
                        BPc_dia = 0.0;
                        hashMap.put("BPc_dia", String.valueOf(BPc_dia));
                    } else {
                        hashMap.put("BPc_dia", String.valueOf(BPc_dia));
                    }
                    if (BPc_sys == 6666.0) {
                        BPc_sys = 0.0;
                        hashMap.put("BPc_sys", String.valueOf(BPc_sys));
                    } else {
                        hashMap.put("BPc_sys", String.valueOf(BPc_sys));
                    }
                    recordArrayList.add(hashMap);
                    getActivity().runOnUiThread(() -> {
                        recycler_record.setLayoutManager(linearLayoutManager);
                        linearLayoutManager.setStackFromEnd(true);
                        linearLayoutManager.setReverseLayout(true);
                        if (adapter_record != null) {
                            int lastItemPosition = adapter_record.getItemCount() - 1;
                            linearLayoutManager.scrollToPosition(lastItemPosition);
                        }
                        adapter_record = new RecordAdapter(recordArrayList, this);
                        recycler_record.setAdapter(adapter_record);
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
        hideProgressDialog();
    }

    private void formatDateTime(String time) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            Date date = inputDateFormat.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            String str_date = dateFormat.format(date);
            dateStr_date = str_date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
        }
    }

    public void setSortRadioGroup() {
        sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (adapter_record != null) {
                    int lastItemPosition = adapter_record.getItemCount() - 1;
                    if (radioBtn_o2n.isChecked()) {
                        linearLayoutManager.setStackFromEnd(false);
                        linearLayoutManager.setReverseLayout(false);
                        linearLayoutManager.scrollToPosition(0);
                    } else if (radioBtn_n2o.isChecked() && lastItemPosition > 0) {
                        linearLayoutManager.setStackFromEnd(true);
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.scrollToPosition(lastItemPosition);
                    }
                }
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        Record record = new Record();
        currentFragment = record;
        BottomNavigationView navigationView = getActivity().findViewById(R.id.navigationView);
        try {
            FragmentHideShow(category);
            navigationView.setSelectedItemId(R.id.category);
        }catch (Exception e){
            Log.e("RecordItemClick",e.toString());
        }
//        Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
    }

    private void FragmentHideShow(Fragment fg){
        fragmentManager = getActivity().getSupportFragmentManager();
        transaction= fragmentManager.beginTransaction();
        if(!fg.isAdded()){
            transaction.hide(currentFragment);
            transaction.add(R.id.container_all,fg);
        }else{
            transaction.hide(currentFragment);
            transaction.show(fg);
        }
        currentFragment=fg;
        transaction.commit();
    }
}

class MonthPickDialog {
    private Activity activity;
    OnDialogRespond onDialogRespond;
    NumberPicker np_year, np_month, np_day;

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


        Button btn_monthCancel, btn_monthDone;
        np_year = contentView.findViewById(R.id.np_year);
        np_month = contentView.findViewById(R.id.np_month);
        np_day = contentView.findViewById(R.id.np_day);

        btn_monthCancel = contentView.findViewById(R.id.btn_monthCancel);
        btn_monthDone = contentView.findViewById(R.id.btn_monthDone);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        np_year.setMaxValue(year);
        np_year.setMinValue(year - 20);
        np_year.setValue(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)));

        np_month.setMaxValue(12);
        np_month.setMinValue(1);
        np_month.setValue(Integer.parseInt(new SimpleDateFormat("MM").format(date)));

        np_day.setMaxValue(31);
        np_day.setMinValue(1);
        np_day.setValue(Integer.parseInt(new SimpleDateFormat("dd").format(date)));

        btn_monthCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthDialog.dismiss();

            }
        });
        btn_monthDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = String.format("%04d-%02d-%02d", np_year.getValue(), np_month.getValue(), np_day.getValue());
                onDialogRespond.onRespond(s);
                monthDialog.dismiss();
            }
        });
    }

    interface OnDialogRespond {
        void onRespond(String selected);
    }
}