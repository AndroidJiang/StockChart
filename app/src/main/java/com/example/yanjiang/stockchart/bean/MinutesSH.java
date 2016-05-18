package com.example.yanjiang.stockchart.bean;

import android.util.SparseArray;

/**
 * Created by ljs on 15/11/19.
 */
public class MinutesSH implements MinutesType {

    @Override
    public SparseArray<String> getShowTimeLabels() {
        SparseArray<String> times = new SparseArray<>();
        times.put(0, "09:30");
//        times.put(30, "10:00");
        times.put(60, "10:30");
//        times.put(90, "11:00");
        times.put(121, "11:30/13:00");
//        times.put(152, "13:30");
        times.put(182, "14:00");
//        times.put(212, "14:30");
        times.put(242, "15:00");

        return times;
    }

    @Override
    public String getType() {
        return "sh";
    }

    @Override
    public String[] getMinutesCount() {
        return new String[getOneDayCounts() + 1];
    }

    @Override
    public SparseArray<String> getShowDayLabels(String[] days) {
        SparseArray<String> temp = new SparseArray<>();

        for (int i = 0, s = getOneDayCounts(); i < days.length; i++) {
            temp.put(s * (i + 1), days[i]);
        }

        return temp;
    }

    @Override
    public String[] getDayCount(int day) {
        return new String[(getOneDayCounts()) * day + day];
    }

    @Override
    public int getOneDayCounts() {
        return 60 * 4 + 2;
    }

}
