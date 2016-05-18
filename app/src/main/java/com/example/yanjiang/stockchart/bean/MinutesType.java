package com.example.yanjiang.stockchart.bean;

import android.util.SparseArray;

/**
 * Created by ljs on 15/11/19.
 */
public interface MinutesType {

    String SH = "SH";
    String SZ = "SZ";
    String HK = "HK";
    String US = "US";

    SparseArray<String> getShowTimeLabels();

    String getType();

    String[] getMinutesCount();

    SparseArray<String> getShowDayLabels(String[] days);

    String[] getDayCount(int day);

    int getOneDayCounts();

}
