package com.example.yanjiang.stockchart.bean;

import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MinuteHelper {
    private ArrayList<MinutesBean> datas=new ArrayList<>();
    private ArrayList<KLineBean> kDatas=new ArrayList<>();
    private float baseValue;
    private float maxmin;
    private float volmax;
    private SparseArray<String> dayLabels;
    private String code = "sz002081";
    private int decreasingColor;
    private int increasingColor;
    private String stockExchange;

    public void parseMinutes(JSONObject object) {
        JSONArray jsonArray = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optJSONArray("data");
        String date = object.optJSONObject("data").optJSONObject(code).optJSONObject("data").optString("date");
        if (date.length() == 0) {
            return;
        }
/*数据解析依照自己需求来定，如果服务器直接返回百分比数据，则不需要客户端进行计算*/
        baseValue = (float) object.optJSONObject("data").optJSONObject(code).optJSONObject("qt").optJSONArray(code).optDouble(4);
        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            String[] t = jsonArray.optString(i).split(" ");
            MinutesBean minutesData = new MinutesBean();
            minutesData.time = t[0].substring(0, 2) + ":" + t[0].substring(2);
            minutesData.cjprice = Float.parseFloat(t[1]);
            if (i != 0) {
                String[] pre_t = jsonArray.optString(i - 1).split(" ");
                minutesData.cjnum = Integer.parseInt(t[2]) - Integer.parseInt(pre_t[2]);
                minutesData.color = minutesData.cjprice - datas.get(i - 1).cjprice >= 0 ? this.increasingColor : decreasingColor;
                minutesData.total = minutesData.cjnum * minutesData.cjprice + datas.get(i - 1).total;
                minutesData.avprice = (minutesData.total) / Integer.parseInt(t[2]);
            } else {
                minutesData.cjnum = Integer.parseInt(t[2]);
                minutesData.avprice = minutesData.cjprice;
                minutesData.color = this.increasingColor;
                minutesData.total = minutesData.cjnum * minutesData.cjprice;
            }
            minutesData.exchange = minutesData.cjprice - baseValue;
            minutesData.per = (minutesData.exchange / baseValue);
            double cha = minutesData.cjprice - baseValue;
            if (Math.abs(cha) > maxmin) {
                maxmin = (float) Math.abs(cha);
            }
            volmax = Math.max(minutesData.cjnum, volmax);
            datas.add(minutesData);
        }

        if (maxmin == 0) {
            maxmin = baseValue * 0.02f;
        }
    }

    public void parseKLine(JSONObject obj) {
        ArrayList<KLineBean> kLineBeans = new ArrayList<>();
        JSONObject data = obj.optJSONObject("data").optJSONObject(code);
        JSONArray list = data.optJSONArray("day");
        if (list != null) {
            int count = list.length();
            for (int i = 0; i < count; i++) {
                JSONArray dayData = list.optJSONArray(i);
                KLineBean kLineData = new KLineBean();
                kLineBeans.add(kLineData);
                kLineData.date = dayData.optString(0);
                kLineData.open = (float) dayData.optDouble(1);
                kLineData.close = (float) dayData.optDouble(2);
                kLineData.high = (float) dayData.optDouble(3);
                kLineData.low = (float) dayData.optDouble(4);
                kLineData.vol = (float) dayData.optDouble(5);
            }
        }
        kDatas.addAll(kLineBeans);


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
    public ArrayList<KLineBean> getKLineDatas() {
        return kDatas;
    }

}
