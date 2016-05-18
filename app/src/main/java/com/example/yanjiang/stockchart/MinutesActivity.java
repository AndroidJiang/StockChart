package com.example.yanjiang.stockchart;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.example.yanjiang.stockchart.bean.MData;
import com.example.yanjiang.stockchart.bean.MinutesSH;
import com.example.yanjiang.stockchart.rxutils.SchedulersCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
    LineChart lineChart;
    @Bind(R.id.bar_chart)
    BarChart barChart;
    private Subscription subscriptionMinute;
    private LineDataSet d1, d2;
    XAxis xAxis;
    YAxis axisRight;
    YAxis axisLeft;
    BarDataSet barDataSet;

    XAxis xAxisBar;
    YAxis axisLeftBar;
    YAxis axisRightBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minutes);
        ButterKnife.bind(this);
        initChart();
        setTimeLabels();
        getMinutesData();


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
        barChart.setDrawBorders(false);
      /*  barChart.setBorderWidth(1);
        barChart.setBorderColor(getResources().getColor(R.color.grayLine));*/
        barChart.setDescription("");
        Legend barChartLegend = barChart.getLegend();
        barChartLegend.setEnabled(false);
        //x轴
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(59);



        //左边y
        axisLeft = lineChart.getAxisLeft();
        axisLeft.setLabelCount(5, true);
        axisLeft.setDrawLabels(true);

        //bar x y轴
        xAxisBar = barChart.getXAxis();
        xAxisBar.setDrawLabels(false);
        xAxisBar.setDrawGridLines(false);

        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setDrawGridLines(false);


        axisRightBar = barChart.getAxisRight();
       // axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);

        //y轴样式
        this.axisLeft.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });
        //右边y
        this.axisRight = lineChart.getAxisRight();
        this.axisRight.setLabelCount(5, true);
        this.axisRight.setDrawLabels(true);
        this.axisRight.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00%");
                return mFormat.format(value);
            }
        });

        this.axisRight.setStartAtZero(false);
        this.axisRight.setDrawGridLines(false);
        this.axisRight.setDrawAxisLine(false);
        //背景线
        this.xAxis.setGridColor(getResources().getColor(R.color.grayLine));
        this.xAxis.setAxisLineColor(getResources().getColor(R.color.grayLine));
        this.axisLeft.setGridColor(getResources().getColor(R.color.grayLine));
        this.axisRight.setAxisLineColor(getResources().getColor(R.color.grayLine));

        MinutesSH minutesSH=new MinutesSH();
        Log.e("***", minutesSH.getShowTimeLabels() + "");

    }



    private void setData(MData mData) {
        if (mData.getDatas().size() == 0) {
            lineChart.setNoDataText("暂无数据");
            return;
        }
        //设置y左右两轴最大最小值
        axisLeft.setAxisMinValue(mData.getMin());
        axisLeft.setAxisMaxValue(mData.getMax());
        axisRight.setAxisMinValue(mData.getPercentMin());
        axisRight.setAxisMaxValue(mData.getPercentMax());


        axisLeftBar.setAxisMaxValue(mData.getVolmax());
        axisLeftBar.setAxisMinValue(0);//即使最小是不是0，也无碍
        axisLeftBar.setShowOnlyMinMax(true);
        axisRightBar.setAxisMaxValue(mData.getVolmax());
        axisRightBar.setAxisMinValue(0);//即使最小是不是0，也无碍
        axisRightBar.setShowOnlyMinMax(true);
        //基准线
        LimitLine ll = new LimitLine(mData.getBaseValue());
        ll.setLineWidth(1f);
        ll.setLineColor(Color.RED);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLineWidth(1);
        axisLeft.addLimitLine(ll);


        ArrayList<Entry> lineCJEntries = new ArrayList<Entry>();
        ArrayList<Entry> lineJJEntries = new ArrayList<Entry>();
        ArrayList<String> dateList = new ArrayList<String>();
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < mData.getDatas().size(); i++) {
            //避免数据重复，skip也能正常显示
            if(mData.getDatas().get(i).time.equals("13:30")){
                continue;
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
        LineData cd = new LineData(dateList, sets);
        lineChart.setData(cd);

        BarData barData=new BarData(dateList,barDataSet);
        barChart.setData(barData);
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
    private SparseArray<String> setTimeLabels(){
        SparseArray<String> times = new SparseArray<>();
        times.put(0, "09:30");
        times.put(60, "10:30");
        times.put(121, "11:30/13:00");
        times.put(182, "14:00");
        times.put(242, "15:00");
        return times;
    }
}
