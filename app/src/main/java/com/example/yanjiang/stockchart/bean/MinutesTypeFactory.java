package com.example.yanjiang.stockchart.bean;

/**
 * Created by ljs on 15/11/19.
 */
public class MinutesTypeFactory {

    public static final MinutesType getType(String type) {
        type = type == null ? "" : type;
        switch (type) {
            case MinutesType.SH:
                return new MinutesSH();
            default:
                return new MinutesSH();
        }

    }

}
