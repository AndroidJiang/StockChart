package com.example.yanjiang.stockchart.bean;

import android.text.TextUtils;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MinuteHelper {
    private ArrayList<MinutesBean> datas;
    private float baseValue;
    private float maxmin;
    private float volmax;
    private SparseArray<String> dayLabels;
    private String code="sz002081";
    private int decreasingColor;
    private int increasingColor;
    private String stockExchange;

    public void parseData(JSONObject object) {
        datas = new ArrayList<>();
        JSONArray temp = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optJSONArray("data");
        String date = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optString("date");
        if (date.length() == 0) {
            return;
        }
        baseValue = (float) object.optJSONObject("data").optJSONObject(code).optJSONObject("qt").optJSONArray(code).optDouble(4);
        int count = temp.length();
        for (int i = 0; i < count; i++) {
            String[] t = temp.optString(i).split(" ");
            MinutesBean minutesData = new MinutesBean();
            minutesData.time = t[0].substring(0, 2) + ":" + t[0].substring(2);
            minutesData.chengjiaojia = Float.parseFloat(t[1]);
            if (i != 0) {
                String[] pre_t = temp.optString(i - 1).split(" ");
                minutesData.chengjiaoliang = Integer.parseInt(t[2]) - Integer.parseInt(pre_t[2]);
                minutesData.color = minutesData.chengjiaojia - datas.get(i - 1).chengjiaojia >= 0 ? this.increasingColor : decreasingColor;
                minutesData.total = minutesData.chengjiaoliang * minutesData.chengjiaojia + datas.get(i - 1).total;
                minutesData.junjia = (minutesData.total) / Integer.parseInt(t[2]);
            } else {
                minutesData.chengjiaoliang = Integer.parseInt(t[2]);
                minutesData.junjia = minutesData.chengjiaojia;
                minutesData.color = this.increasingColor;
                minutesData.total = minutesData.chengjiaoliang * minutesData.chengjiaojia;
            }
            minutesData.change = minutesData.chengjiaojia - baseValue;
            minutesData.percentage = (minutesData.change / baseValue);
            double cha = minutesData.chengjiaojia - baseValue;
            if (Math.abs(cha) > maxmin) {
                maxmin = (float) Math.abs(cha);
            }
            volmax = Math.max(minutesData.chengjiaoliang, volmax);
            datas.add(minutesData);
        }

        if (maxmin == 0) {
            maxmin = baseValue * 0.02f;
        }
    }


    public float getMin() {
        return baseValue - maxmin;
    }
    public float getMax() {
        return baseValue + maxmin;
    }

    public float getPercentMax() {
        return maxmin / baseValue;
    }

    public float getPercentMin() {
        return -getPercentMax();
    }


    public float getVolmax() {
        return volmax;
    }


    public ArrayList<MinutesBean> getDatas() {
        return datas;
    }


    private String doDate(String date) {
        if (TextUtils.isEmpty(date) || date.length() < 8) {
            return "";
        }

        return date.substring(4, 6) + "-" + date.substring(6);
    }
}
