package com.example.yanjiang.stockchart.rxutils;


import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class ChengJiaoLiangFormatter implements YAxisValueFormatter {

    private final int mJishu;
    private DecimalFormat mFormat;

    public ChengJiaoLiangFormatter(int jishu) {
        if (jishu == 1) {
            mFormat = new DecimalFormat("#0");
        } else {
            mFormat = new DecimalFormat("#0.00");
        }
        mJishu = jishu;
    }


    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        value = value / mJishu;
        return mFormat.format(value);
    }
}
