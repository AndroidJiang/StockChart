package com.example.yanjiang.stockchart;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.example.yanjiang.stockchart.api.ConstantTest;
import com.example.yanjiang.stockchart.bean.MData;
import com.example.yanjiang.stockchart.bean.MinutesData;
import com.example.yanjiang.stockchart.bean.MinutesSH;
import com.example.yanjiang.stockchart.mychart.MyBarChart;
import com.example.yanjiang.stockchart.mychart.MyLineChart;
import com.example.yanjiang.stockchart.mychart.MyXAxis;
import com.example.yanjiang.stockchart.mychart.MyYAxis;
import com.example.yanjiang.stockchart.rxutils.ChengJiaoLiangFormatter;
import com.example.yanjiang.stockchart.rxutils.MyUtils;
import com.example.yanjiang.stockchart.rxutils.SchedulersCompat;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;

public class MinutesActivity extends BaseActivity {
    @Bind(R.id.line_chart)
    MyLineChart lineChart;
    @Bind(R.id.bar_chart)
    MyBarChart barChart;
    private Subscription subscriptionMinute;
    private LineDataSet d1, d2;
    MyXAxis xAxisLine;
    MyYAxis axisRightLine;
    MyYAxis axisLeftLine;
    BarDataSet barDataSet;

    MyXAxis xAxisBar;
    MyYAxis axisLeftBar;
    MyYAxis axisRightBar;
    SparseArray<String> stringSparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minutes);
        ButterKnife.bind(this);
        initChart();

        stringSparseArray = setTimeLabels();

        /*网络数据*/
        //getMinutesData();
        /*离线测试数据*/
        getOffLineData();
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                barChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {

            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                lineChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void initChart() {
        lineChart.setScaleEnabled(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(1);
        lineChart.setBorderColor(getResources().getColor(R.color.grayLine));
        lineChart.setDescription("");
        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(false);

        barChart.setScaleEnabled(false);
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(1);
        barChart.setBorderColor(getResources().getColor(R.color.grayLine));
        barChart.setDescription("");
        Legend barChartLegend = barChart.getLegend();
        barChartLegend.setEnabled(false);
        //x轴
        xAxisLine = lineChart.getXAxis();
        xAxisLine.setDrawLabels(true);
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxisLine.setLabelsToSkip(59);


        //左边y
        axisLeftLine = lineChart.getAxisLeft();
        /*折线图y轴左没有basevalue，调用系统的*/
        axisLeftLine.setLabelCount(5, true);
        axisLeftLine.setDrawLabels(true);


        //右边y
        axisRightLine = lineChart.getAxisRight();
        axisRightLine.setLabelCount(2, true);
        axisRightLine.setDrawLabels(true);
        axisRightLine.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00%");
                return mFormat.format(value);
            }
        });

        axisRightLine.setStartAtZero(false);
        axisRightLine.setDrawGridLines(false);
        axisRightLine.setDrawAxisLine(false);
        //背景线
        xAxisLine.setGridColor(getResources().getColor(R.color.grayLine));
        xAxisLine.setAxisLineColor(getResources().getColor(R.color.grayLine));
        axisLeftLine.setGridColor(getResources().getColor(R.color.grayLine));
        axisRightLine.setAxisLineColor(getResources().getColor(R.color.grayLine));


        //bar x y轴
        xAxisBar = barChart.getXAxis();
        xAxisBar.setDrawLabels(false);
        xAxisBar.setDrawGridLines(false);
        // xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);

        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setAxisMinValue(0);
        axisLeftBar.setDrawGridLines(false);


        axisRightBar = barChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);

        //y轴样式
        this.axisLeftLine.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });
        MinutesSH minutesSH = new MinutesSH();
        Log.e("***", minutesSH.getShowTimeLabels() + "");

    }


    private void setData(MData mData) {
        setShowLabels(stringSparseArray);
        Log.e("###", mData.getDatas().size() + "ee");
        if (mData.getDatas().size() == 0) {
            lineChart.setNoDataText("暂无数据");
            return;
        }
        //设置y左右两轴最大最小值
        axisLeftLine.setAxisMinValue(mData.getMin());
        axisLeftLine.setAxisMaxValue(mData.getMax());
        axisRightLine.setAxisMinValue(mData.getPercentMin());
        axisRightLine.setAxisMaxValue(mData.getPercentMax());


        axisLeftBar.setAxisMaxValue(mData.getVolmax());
        /*单位*/
        String unit = MyUtils.getVolUnit(mData.getVolmax());
        int u = 1;
        if (unit.equals("万手")) {
            u = 4;
        } else if (unit.equals("亿手")) {
            u = 8;
        }
        /*次方*/
        axisLeftBar.setValueFormatter(new ChengJiaoLiangFormatter((int) Math.pow(10, u)));
        axisLeftBar.setShowMaxAndUnit(unit);
        //axisLeftBar.setAxisMinValue(0);//即使最小是不是0，也无碍
        //axisLeftBar.setShowOnlyMinMax(true);
        axisRightBar.setAxisMaxValue(mData.getVolmax());
        //   axisRightBar.setAxisMinValue(mData.getVolmin);//即使最小是不是0，也无碍
        //axisRightBar.setShowOnlyMinMax(true);

        //基准线
        LimitLine ll = new LimitLine(0);
        ll.setLineWidth(1f);
        ll.setLineColor(Color.RED);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLineWidth(1);
        axisRightLine.addLimitLine(ll);
        axisRightLine.setBaseValue(0);

        ArrayList<Entry> lineCJEntries = new ArrayList<Entry>();
        ArrayList<Entry> lineJJEntries = new ArrayList<Entry>();
        ArrayList<String> dateList = new ArrayList<String>();
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();
        Log.e("##", xVals.size() + "");
        for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
           /* //避免数据重复，skip也能正常显示
            if (mData.getDatas().get(i).time.equals("13:30")) {
                continue;
            }*/
            MinutesData t = mData.getDatas().get(j);

            if (t == null) {
                lineCJEntries.add(new Entry(Float.NaN, i, t));
                lineJJEntries.add(new Entry(Float.NaN, i));
                barEntries.add(new BarEntry(Float.NaN, i, t));
                continue;
            }
            if (!TextUtils.isEmpty(stringSparseArray.get(i)) &&
                    stringSparseArray.get(i).contains("/")) {
                i++;
            }
            lineCJEntries.add(new Entry(mData.getDatas().get(i).chengjiaojia, i));
            lineJJEntries.add(new Entry(mData.getDatas().get(i).junjia, i));
            barEntries.add(new BarEntry(mData.getDatas().get(i).chengjiaoliang, i));
            dateList.add(mData.getDatas().get(i).time);
        }
        d1 = new LineDataSet(lineCJEntries, "成交价");
        d2 = new LineDataSet(lineJJEntries, "均价");
        barDataSet = new BarDataSet(barEntries, "成交量");

        d1.setCircleRadius(0);
        d2.setCircleRadius(0);
        d1.setColor(Color.BLUE);
        d2.setColor(Color.RED);
        d1.setHighLightColor(Color.BLACK);
        d2.setHighlightEnabled(false);
        d1.setDrawFilled(true);


        barDataSet.setBarSpacePercent(0); //bar空隙
        barDataSet.setHighLightColor(Color.BLACK);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(true);
        //谁为基准
        d1.setAxisDependency(YAxis.AxisDependency.LEFT);
        // d2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        sets.add(d2);
        /*注老版本LineData参数可以为空，最新版本会报错，修改进入ChartData加入if判断*/
        LineData cd = new LineData(getMinutesCount(), sets);
        lineChart.setData(cd);

        BarData barData = new BarData(getMinutesCount(), barDataSet);
        barChart.setData(barData);


        setOffset();
        //setOff();
        // setYAxisOffset(2);
        lineChart.invalidate();//刷新图
        barChart.invalidate();


    }

    private void getMinutesData() {
        String code = "sz002081";
        subscriptionMinute = clientApi.getMinutes(code)
                .compose(SchedulersCompat.<ResponseBody>applyIoSchedulers())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("更新失败" + e.toString());
                    }

                    @Override
                    public void onNext(ResponseBody minutes) {
                        MData mData = new MData();
                        JSONObject object = null;
                        try {
                            object = new JSONObject(minutes.string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mData.parseData(object);
                        setData(mData);

                    }
                });

        mCompositeSubscription.add(subscriptionMinute);
    }

    private void getOffLineData() {
           /*方便测试，加入假数据*/
        MData mData = new MData();
        JSONObject object = null;
        try {
            object = new JSONObject(ConstantTest.JSON_TEST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mData.parseData(object);
        setData(mData);
    }

    private SparseArray<String> setTimeLabels() {
        SparseArray<String> times = new SparseArray<>();
        times.put(0, "09:30");
        times.put(60, "10:30");
        times.put(121, "11:30/13:00");
        times.put(182, "14:00");
        times.put(242, "15:00");
        return times;
    }

    public void setOff() {
        float offset = Utils.convertDpToPixel(5);

        lineChart.setExtraOffsets(0, 0, 0, 0);
       // barChart.setExtraOffsets(offset, 0, offset, offset);
    }

    public void setYAxisOffset(float offset) {
        axisLeftLine.setXOffset(offset);
        axisRightLine.setXOffset(offset);
        axisLeftBar.setXOffset(offset);
    }

    /*设置量表对齐*/
    private void setOffset() {
        float lineLeft = lineChart.getViewPortHandler().offsetLeft();
        float barLeft = barChart.getViewPortHandler().offsetLeft();
        float lineRight = lineChart.getViewPortHandler().offsetRight();
        float barRight = barChart.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
        if (barLeft < lineLeft) {
            offsetLeft = lineLeft - barLeft;
            barChart.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = barLeft - lineLeft;
            lineChart.setExtraLeftOffset(offsetLeft);
        }

        if (barRight < lineRight) {
            offsetRight = lineRight - barRight;
            barChart.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = barRight - lineRight;
            lineChart.setExtraRightOffset(offsetRight);
        }

    }

    public void setShowLabels(SparseArray<String> labels) {
        xAxisLine.setXLabels(labels);
        xAxisBar.setXLabels(labels);
    }

    public String[] getMinutesCount() {
        return new String[243];
    }
}
