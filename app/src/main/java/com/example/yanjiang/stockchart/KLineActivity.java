package com.example.yanjiang.stockchart;

import android.os.Bundle;
import android.util.Log;

import com.example.yanjiang.stockchart.api.ConstantTest;
import com.example.yanjiang.stockchart.bean.KLineBean;
import com.example.yanjiang.stockchart.bean.MinuteHelper;
import com.example.yanjiang.stockchart.mychart.MyBarChart;
import com.github.mikephil.charting.charts.CombinedChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KLineActivity extends BaseActivity {

    @Bind(R.id.combinedchart)
    CombinedChart combinedchart;
    @Bind(R.id.barchart)
    MyBarChart barchart;
    private MinuteHelper mData;
    private ArrayList<KLineBean> kLineDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        ButterKnife.bind(this);

        getOffLineData();
    }


    private void getOffLineData() {
           /*方便测试，加入假数据*/
        mData = new MinuteHelper();
        JSONObject object = null;
        try {
            object = new JSONObject(ConstantTest.KLINEURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData.parseKLine(object);

        mData.getKLineDatas();


       setData(mData);
    }

    private void setData(MinuteHelper mData) {
        kLineDatas = mData.getKLineDatas();
        Log.e("~~~","count"+kLineDatas.size());
    }

}
