package com.example.yanjiang.stockchart.rxutils;


import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class VolFormatter implements YAxisValueFormatter {

    private final int unit;
    private DecimalFormat mFormat;

    public VolFormatter(int unit) {
        if (unit == 1) {
            mFormat = new DecimalFormat("#0");
        } else {
            mFormat = new DecimalFormat("#0.00");
        }
        this.unit = unit;
    }


    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        value = value / unit;
        return mFormat.format(value);
    }
}
