package com.example.yanjiang.stockchart.mychart;

import com.github.mikephil.charting.components.YAxis;

/**
 * 作者：ajiang
 * 邮箱：1025065158
 * 博客：http://blog.csdn.net/qqyanjiang
 */
public class MyYAxis extends YAxis {
    private float baseValue=Float.NaN;
    private String minValue;
    public MyYAxis() {
        super();
    }
    public MyYAxis(AxisDependency axis) {
        super(axis);
    }
    public void setShowMaxAndUnit(String minValue) {
        setShowOnlyMinMax(true);
        this.minValue = minValue;
    }
    public float getBaseValue() {
        return baseValue;
    }

    public String getMinValue(){
        return minValue;
    }
    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
    }
}
