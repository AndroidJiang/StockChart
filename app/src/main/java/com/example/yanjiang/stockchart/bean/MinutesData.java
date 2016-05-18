package com.example.yanjiang.stockchart.bean;

import android.graphics.Paint;

/**
 * Created by ljs on 15/11/2.
 */
public class MinutesData {


    public String time;
    public float chengjiaojia;
    public float chengjiaoliang;
    public float junjia = Float.NaN;
    public float percentage;
    public float change;
    public float total;

    public int color = 0xff000000;
    public Paint.Style barStyle = Paint.Style.STROKE;


}
