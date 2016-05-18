package com.example.yanjiang.stockchart.bean;

import android.text.TextUtils;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ljs on 15/11/26.
 */
public class MData {

    private ArrayList<MinutesData> datas;


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

            MinutesData minutesData = new MinutesData();
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



    public float getBaseValue() {
        return baseValue;
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


    public ArrayList<MinutesData> getDatas() {
        return datas;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIncreasingColor(int color) {
        this.increasingColor = color;
    }

    public void setDecreasingColor(int color) {
        this.decreasingColor = color;
    }

    public void parseFiveData(JSONObject obj) {

        MinutesType minutesType = MinutesTypeFactory.getType(stockExchange);

        dayLabels = new SparseArray<>();
        datas = new ArrayList<>();
        JSONArray temp = obj.optJSONObject("data").optJSONObject(code).optJSONArray("data");

        baseValue = (float) obj.optJSONObject("data").optJSONObject(code).optJSONObject("qt").optJSONArray(code).optDouble(4);


        int count = temp.length();

        for (int i = count - 1; i >= 0; i--) {

            JSONObject one = temp.optJSONObject(i);
            final JSONArray data = one.optJSONArray("data");
            int tempCount = data.length();
            String date = one.optString("date");

            dayLabels.put((count - i) * (minutesType.getOneDayCounts()), doDate(date));
            ArrayList<MinutesData> list = new ArrayList<>();
            for (int x = 0; x < tempCount; x++) {

                if (data.optString(x).equals("")) {
                    list.add(null);

                    continue;
                }

                String[] t = data.optString(x).split(" ");


                MinutesData minutesData = new MinutesData();
                minutesData.time = t[0].substring(0, 2) + ":" + t[0].substring(2);
                minutesData.chengjiaojia = Float.parseFloat(t[1]);

                minutesData.percentage = ((int) ((minutesData.chengjiaojia - baseValue) / baseValue * 100 * 100)) / 100.0f;

                double cha = minutesData.chengjiaojia - baseValue;

                if (Math.abs(cha) > maxmin) {
                    maxmin = (float) Math.abs(cha);
                }

                if (x != 0) {
                    String[] pre_t = data.optString(x - 1).split(" ");
                    minutesData.chengjiaoliang = Integer.parseInt(t[2]) - Integer.parseInt(pre_t[2]);
                    minutesData.color = minutesData.chengjiaojia - list.get(x - 1).chengjiaojia >= 0 ? this.increasingColor : decreasingColor;

                    minutesData.total = minutesData.chengjiaoliang * minutesData.chengjiaojia + list.get(x - 1).total;

                    minutesData.junjia = (minutesData.total) / Integer.parseInt(t[2]);

                } else {
                    minutesData.chengjiaoliang = Integer.parseInt(t[2]);
                    minutesData.junjia = minutesData.chengjiaojia;
                    minutesData.color = this.increasingColor;
                    minutesData.total = minutesData.chengjiaoliang * minutesData.chengjiaojia;
                }

                volmax = Math.max(minutesData.chengjiaoliang, volmax);
                list.add(minutesData);

            }

            datas.addAll(list);
        }
        if (maxmin == 0) {
            maxmin = baseValue * 0.02f;
        }
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public SparseArray<String> getDayLabels() {
        return dayLabels;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    private String doDate(String date) {
        if (TextUtils.isEmpty(date) || date.length() < 8) {
            return "";
        }

        return date.substring(4, 6) + "-" + date.substring(6);
    }
}
