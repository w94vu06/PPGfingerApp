package com.example.myapplication;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CalculateHRV {
    double rmssd;
    double sdnn;
    long[] nonZeroValues;

    public long[] IQR(long[] RRI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            double[] arr = Arrays.stream(RRI).asDoubleStream().toArray();

            double[] arr = new double[RRI.length];
            for (int i = 0; i < RRI.length; i++) {
                arr[i] = (double) RRI[i];
            }

            Arrays.sort(arr);

            double q1 = findMedian(arr, 0, arr.length / 2 - 1);
            double q3 = findMedian(arr, arr.length / 2 + arr.length % 2, arr.length - 1);
            // 計算 IQR
            double iqr = q3 - q1;

            // 計算上下界
            double upperBound = q3 + 1 * iqr;
            double lowerBound = q1 - 1 * iqr;

            // 將超過上下界的值設為0
            for (int i = 0; i < RRI.length; i++) {
                if (RRI[i] > upperBound || RRI[i] < lowerBound) {
                    RRI[i] = 0;
                }
            }
            ArrayList<Long> clearArrayList = new ArrayList<>();
            // 找出所有不為0的值的平均數
            for (int i = 0; i < RRI.length; i++) {
                if (RRI[i] != 0 && RRI[i] > 300 && RRI[i] < 1100) {
                    clearArrayList.add(RRI[i]);
                }
            }
            nonZeroValues = new long[clearArrayList.size()];
            for (int i = 0; i < clearArrayList.size(); i++) {
                nonZeroValues[i] = clearArrayList.get(i);
            }
        }

        return nonZeroValues;
    }

    public static double findMedian(double[] arr, int start, int end) {
        int len = end - start + 1;
        int mid = start + len / 2;
        if (len % 2 == 0) {
            return (arr[mid - 1] + arr[mid]) / 2.0;
        } else {
            return arr[mid];
        }
    }

    public float[] calculateQuartiles(List<Float> values) {
        // 先排序
        Collections.sort(values);
        // 計算中位數
        float median;
        int size = values.size();
        if (size % 2 == 0) {
            median = (values.get(size / 2 - 1) + values.get(size / 2)) / 2;
        } else {
            median = values.get(size / 2);
        }

        // 計算 Q1 和 Q3
        float Q1, Q3;
        int mid = size / 2;
        if (size % 2 == 0) {
            Q1 = (values.get(mid / 2 - 1) + values.get(mid / 2)) / 2;
            Q3 = (values.get(mid + mid / 2 - 1) + values.get(mid + mid / 2)) / 2;
        } else {
            Q1 = values.get(mid / 2);
            Q3 = values.get(mid + mid / 2);
        }

        return new float[]{Q1, median, Q3};
    }

    // 計算SDNN
    public double calculateSDNN(long[] rrIntervals) {
        double mean = calculateMean(rrIntervals);
        double sumOfSquares = 0.0;
        for (int i = 0; i < rrIntervals.length; i++) {
            sumOfSquares += Math.pow(rrIntervals[i] - mean, 2);
        }
        double variance = sumOfSquares / (rrIntervals.length - 1);
        sdnn = Math.sqrt(variance);
        return sdnn;
    }

    // 計算RMSSD
    public double calculateRMSSD(long[] rrIntervals) {
        double sumOfDifferencesSquared = 0.0;

        for (int i = 0; i < rrIntervals.length - 1; i++) {
            double difference = rrIntervals[i + 1] - rrIntervals[i];
            sumOfDifferencesSquared += difference * difference;
        }
        double rmssd = sumOfDifferencesSquared / (rrIntervals.length - 1);

        return Math.sqrt(rmssd);
    }

    // 計算MedianNN
    public double calculateMedianNN(long[] rrIntervals) {
        long[] extendRRI = new long[rrIntervals.length];
        System.arraycopy(rrIntervals, 0, extendRRI, 0, rrIntervals.length);
        Arrays.sort(extendRRI);
        int length = extendRRI.length;
        double medianNN;
        if (length % 2 == 0) {
            medianNN = (extendRRI[length / 2 - 1] + extendRRI[length / 2]) / 2.0;
        } else {
            medianNN = extendRRI[length / 2];
        }
        return 60000/medianNN;
    }

    // 計算pNN50
    public double calculatePNN50(long[] rrIntervals) {
        int nn50 = 0;
        long[] diff_rri = new long[rrIntervals.length - 1];
        for (int i = 0; i < rrIntervals.length - 1; i++) {
            diff_rri[i] = rrIntervals[i + 1] - rrIntervals[i];
        }
        for (long diff : diff_rri) {
            if (Math.abs(diff) > 50) {
                nn50++;
            }
        }
        int totalIntervals = diff_rri.length;

        return nn50 / (double) totalIntervals * 100.0;
    }


    // 計算MaxNN
    public double calMaxNN(long[] rrIntervals) {
        double minNN = Double.POSITIVE_INFINITY;
        for (double value : rrIntervals) {
            if (!Double.isNaN(value) && value < minNN) {
                minNN = value;
            }
        }
        return 60000/minNN;
    }

    // 計算MinNN
    public double calMinNN(long[] rrIntervals) {
        double maxNN = Double.NEGATIVE_INFINITY;
        for (double value : rrIntervals) {
            if (!Double.isNaN(value) && value > maxNN) {
                maxNN = value;
            }
        }
        return 60000/maxNN;
    }

    // 計算平均值
    public double calculateMean(long[] values) {
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) {
            sum += (double) values[i];
        }
        double mean = sum / values.length;
        return mean;
    }

    public double calBPM(long[] rri) {
        int med;
        Arrays.sort(rri);
        med = (int) rri[rri.length / 2];
        return 60000 / med;
    }
}
