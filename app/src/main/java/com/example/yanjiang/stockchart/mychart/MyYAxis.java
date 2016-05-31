package com.example.yanjiang.stockchart.mychart;

import com.github.mikephil.charting.components.YAxis;

/**
 * 作者：ajiang
 * 邮箱：1025065158
 * 博客：http://blog.csdn.net/qqyanjiang
 */
public class MyYAxis extends YAxis {
    private String minValue;
    public MyYAxis() {
        super();
    }
    public MyYAxis(AxisDependency axis) {
        super(axis);
    }
    public void setShowOnlyMax(String minValue) {
        setShowOnlyMinMax(true);
        this.minValue = minValue;
    }
    public String getMinValue(){
        return minValue;
    }
}
