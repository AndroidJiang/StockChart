package com.example.yanjiang.stockchart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.example.yanjiang.stockchart.api.ConstantTest;
import com.example.yanjiang.stockchart.bean.KLineBean;
import com.example.yanjiang.stockchart.bean.DataParse;
import com.example.yanjiang.stockchart.mychart.CoupleChartGestureListener;
import com.example.yanjiang.stockchart.rxutils.MyUtils;
import com.example.yanjiang.stockchart.rxutils.VolFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class KLineActivity extends BaseActivity {

    @Bind(R.id.combinedchart)
    CombinedChart combinedchart;
    @Bind(R.id.barchart)
    BarChart barChart;
    private DataParse mData;
    private ArrayList<KLineBean> kLineDatas;
    XAxis xAxisBar, xAxisK;
    YAxis axisLeftBar, axisLeftK;
    YAxis axisRightBar, axisRightK;
    BarDataSet barDataSet;
    private BarLineChartTouchListener mChartTouchListener;
    private CoupleChartGestureListener coupleChartGestureListener;
    float sum=0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            combinedchart.invalidate();
            barChart.invalidate();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        ButterKnife.bind(this);
        initChart();
        getOffLineData();
    }


    private void getOffLineData() {
           /*方便测试，加入假数据*/
        mData = new DataParse();
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

    private void initChart() {
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(1);
        barChart.setBorderColor(getResources().getColor(R.color.minute_grayLine));
        barChart.setDescription("");
        barChart.setDragEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setAutoScaleMinMaxEnabled(true);
        Legend barChartLegend = barChart.getLegend();
        barChartLegend.setEnabled(false);
        //bar x y轴
        xAxisBar = barChart.getXAxis();
        xAxisBar.setDrawLabels(true);
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setDrawAxisLine(false);
        xAxisBar.setTextColor(getResources().getColor(R.color.minute_zhoutv));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setGridColor(getResources().getColor(R.color.minute_grayLine));

        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setAxisMinValue(0);
        axisLeftBar.setDrawGridLines(false);
        axisLeftBar.setDrawAxisLine(false);
        axisLeftBar.setTextColor(getResources().getColor(R.color.minute_zhoutv));
        axisLeftBar.setDrawLabels(true);
        axisLeftBar.setShowOnlyMinMax(true);
        axisRightBar = barChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);
        axisRightBar.setDrawAxisLine(false);

        /****************************************************************/
        combinedchart.setDrawBorders(true);
        combinedchart.setBorderWidth(1);
        combinedchart.setBorderColor(getResources().getColor(R.color.minute_grayLine));
        combinedchart.setDescription("");
        combinedchart.setDragEnabled(true);
        combinedchart.setScaleYEnabled(false);
        combinedchart.setAutoScaleMinMaxEnabled(true);
        Legend combinedchartLegend = combinedchart.getLegend();
        combinedchartLegend.setEnabled(false);
        //bar x y轴
        xAxisK = combinedchart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setTextColor(getResources().getColor(R.color.minute_zhoutv));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setGridColor(getResources().getColor(R.color.minute_grayLine));

        axisLeftK = combinedchart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setTextColor(getResources().getColor(R.color.minute_zhoutv));
        axisLeftK.setGridColor(getResources().getColor(R.color.minute_grayLine));
        axisLeftK.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisRightK = combinedchart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(true);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setGridColor(getResources().getColor(R.color.minute_grayLine));
        combinedchart.setDragDecelerationEnabled(false);
        barChart.setDragDecelerationEnabled(false);
        // 将K线控的滑动事件传递给交易量控件
        combinedchart.setOnChartGestureListener(new CoupleChartGestureListener(combinedchart, new Chart[]{barChart}));
        // 将交易量控件的滑动事件传递给K线控件
        barChart.setOnChartGestureListener(new CoupleChartGestureListener(barChart, new Chart[]{combinedchart}));
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                combinedchart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
            }
        });
        combinedchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                barChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
            }
        });


    }
    private float getSum(Integer a, Integer b) {

        for (int i = a; i <= b; i++) {
            sum += mData.getKLineDatas().get(i).close;
        }
        return sum;
    }


    private void setData(DataParse mData) {

        kLineDatas = mData.getKLineDatas();
        axisLeftBar.setAxisMaxValue(mData.getVolmax());
        String unit = MyUtils.getVolUnit(mData.getVolmax());
        int u = 1;
        if ("万手".equals(unit)) {
            u = 4;
        } else if ("亿手".equals(unit)) {
            u = 8;
        }
        axisLeftBar.setValueFormatter(new VolFormatter((int) Math.pow(10, u)));
        axisRightBar.setAxisMaxValue(mData.getVolmax());


        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        ArrayList<Entry> line5Entries=new ArrayList<>();
        ArrayList<Entry> line10Entries=new ArrayList<>();
        ArrayList<Entry> line30Entries=new ArrayList<>();
        for (int i = 0, j = 0; i < mData.getKLineDatas().size(); i++, j++) {
            xVals.add(mData.getKLineDatas().get(i).date + "");
            barEntries.add(new BarEntry(mData.getKLineDatas().get(i).vol, i));
            candleEntries.add(new CandleEntry(i, mData.getKLineDatas().get(i).high, mData.getKLineDatas().get(i).low, mData.getKLineDatas().get(i).open, mData.getKLineDatas().get(i).close));
            if(i>=4){
                sum=0;
                line5Entries.add(new Entry(getSum(i - 4, i)/5,i));
            }
            if(i>=9){
                sum=0;
                line10Entries.add(new Entry(getSum(i - 9, i)/10,i));
            }
            if(i>=29){
                sum=0;
                line30Entries.add(new Entry(getSum(i - 29, i)/30,i));
            }

        }
        barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setBarSpacePercent(50); //bar空隙
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setHighLightColor(Color.WHITE);
        barDataSet.setDrawValues(false);
        barDataSet.setColor(Color.RED);
        BarData barData = new BarData(xVals, barDataSet);
        barChart.setData(barData);
        barChart.setVisibleXRange(30, 100);


        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(Color.WHITE);
        candleDataSet.setValueTextSize(10f);
        candleDataSet.setDrawValues(false);
        candleDataSet.setColor(Color.RED);
        candleDataSet.setShadowWidth(1f);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        CandleData candleData = new CandleData(xVals, candleDataSet);


        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(setMaLine(5, xVals, line5Entries));
        sets.add(setMaLine(10, xVals, line10Entries));
        sets.add(setMaLine(30, xVals, line30Entries));


        CombinedData combinedData=new CombinedData(xVals);
        LineData lineData=new LineData(xVals,sets);
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        combinedchart.setData(combinedData);
        combinedchart.setVisibleXRange(30, 100);

     /*   combinedchart.moveViewToX(mData.getKLineDatas().size()-1);
        barChart.moveViewToX(mData.getDatas().size()-1);*/

        setOffset();

        /*此处解决方法来源于CombinedChartDemo，k线图y轴显示问题，图表滑动后才能对齐的bug，希望有人给出解决方法*/
        handler.sendEmptyMessageDelayed(0, 300);

   /*     barChart.invalidate();
        combinedchart.invalidate();*/
    }

    @NonNull
    private LineDataSet setMaLine(int ma,ArrayList<String> xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma"+ma);
        lineDataSetMa.setDrawValues(false);
        if(ma==5) {
            lineDataSetMa.setColor(Color.GREEN);
        }else if(ma==10){
            lineDataSetMa.setColor(Color.GRAY);
        }else{
            lineDataSetMa.setColor(Color.YELLOW);
        }
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setHighlightEnabled(false);
        return lineDataSetMa;
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = combinedchart.getViewPortHandler().offsetLeft();
        float barLeft = barChart.getViewPortHandler().offsetLeft();
        float lineRight = combinedchart.getViewPortHandler().offsetRight();
        float barRight = barChart.getViewPortHandler().offsetRight();
        float barBottom = barChart.getViewPortHandler().offsetBottom();
        float offsetLeft, offsetRight;
        float transLeft = 0, transRight = 0;
 /*注：setExtraLeft...函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换*/
        if (barLeft < lineLeft) {
           /* offsetLeft = Utils.convertPixelsToDp(lineLeft - barLeft);
            barChart.setExtraLeftOffset(offsetLeft);*/
            transLeft = lineLeft;
        } else {
            offsetLeft = Utils.convertPixelsToDp(barLeft-lineLeft);
            combinedchart.setExtraLeftOffset(offsetLeft);
            transLeft = barLeft;
        }
  /*注：setExtraRight...函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换*/
        if (barRight < lineRight) {
          /*  offsetRight = Utils.convertPixelsToDp(lineRight);
            barChart.setExtraRightOffset(offsetRight);*/
            transRight = lineRight;
        } else {
            offsetRight = Utils.convertPixelsToDp(barRight);
            combinedchart.setExtraRightOffset(offsetRight);
            transRight = barRight;
        }
        barChart.setViewPortOffsets(transLeft, 20, transRight, barBottom);
    }

}
