package com.example.myapplication.Util;

import android.graphics.Color;

import com.example.myapplication.CalculateHRV;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;

import java.util.List;
import java.util.Locale;

public class ChartUtil {

    public float defaultUpperBound;
    public float defaultLowerBound;
    /**
     * 設定圖表格式
     */
    public void initChart(LineChart chart) {
        chart.getDescription().setEnabled(false);//設置不要圖表標籤
        chart.setBackgroundColor(Color.parseColor("#FFFFFFFF"));//畫布顏色
//        chart.setTouchEnabled(false);//設置不可觸碰
        chart.setDragEnabled(true);//設置不可互動
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawBorders(true);  // 啟用畫布的外框線
        chart.setBorderWidth(1.5f);   // 設置外框線的寬度
        chart.setBorderColor(Color.BLACK);  // 設置外框線的顏色
        //設置單一線數據
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);
        //設置左下角標籤
        Legend l = chart.getLegend();
        l.setEnabled(false);

        //設置X軸
        XAxis x = chart.getXAxis();
        x.setTextColor(Color.BLACK);
        x.setDrawLabels(true);//X軸標籤
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(true);//畫X軸線
        x.setGridColor(Color.parseColor("#F2E5CC"));
        x.setGranularity(0.1f);//設置 X 軸的刻度間隔
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0f", value/10);
            }
        });

        YAxis y = chart.getAxisLeft();
//        y.setTextColor(Color.parseColor("#F2E5CC"));
        y.setTextColor(Color.parseColor("#0a0a0a"));
        y.setDrawLabels(true);//去掉Y軸標籤
        y.setDrawGridLines(true);//畫Y軸線
        y.setGridColor(Color.parseColor("#F2E5CC"));
        y.setGranularity(0.1f);

        y.setAxisMaximum(255);//最高255
        y.setAxisMinimum(0);//最低0

        chart.getAxisRight().setEnabled(false);//右邊Y軸不可視
//        chart.setVisibleXRange(0,50);//設置顯示範圍
        y.setInverted(true);
        float scaleX = chart.getScaleX();
        if (scaleX == 1)
            chart.zoomToCenter(5, 1f);
        else {
            BarLineChartTouchListener barLineChartTouchListener = (BarLineChartTouchListener) chart.getOnTouchListener();
            barLineChartTouchListener.stopDeceleration();
            chart.fitScreen();
        }
        chart.invalidate();
    }

    /**
     * 設置數據線的樣式
     */
    public LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.parseColor("#6D8B75"));
        set.setLineWidth(2);
        set.setDrawCircles(false);
        set.setDrawFilled(false);
        set.setValueTextColor(Color.BLACK);
        set.setDrawValues(false);
        return set;
    }
    public void calculateAndSetDefaultBounds(List<Float> upperBoundDefault, List<Float> lowerBoundDefault) {
        float totalUpperBound = 0;
        float totalLowerBound = 0;

        for (Float upperBound : upperBoundDefault) {
            totalUpperBound += upperBound;
        }

        for (Float lowerBound : lowerBoundDefault) {
            totalLowerBound += lowerBound;
        }

        if (!upperBoundDefault.isEmpty() && !lowerBoundDefault.isEmpty()) {
            float avgUpperBound = totalUpperBound / upperBoundDefault.size();
            float avgLowerBound = totalLowerBound / lowerBoundDefault.size();

            // 設定為下次啟動量測時的預設上下界值
            defaultUpperBound = avgUpperBound;
            defaultLowerBound = avgLowerBound;
        }

        // 清空上下界值的列表
        upperBoundDefault.clear();
        lowerBoundDefault.clear();
    }

}
