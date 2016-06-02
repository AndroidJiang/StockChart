package com.example.yanjiang.stockchart.mychart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * author：ajiang
 * mail：1025065158@qq.com
 * blog：http://blog.csdn.net/qqyanjiang
 */
public class MyXAxisRenderer extends XAxisRenderer {
    private final BarLineChartBase mChart;
    protected MyXAxis mXAxis;

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, MyXAxis xAxis, Transformer trans, BarLineChartBase chart) {
        super(viewPortHandler, xAxis, trans);
        mXAxis = xAxis;
        mChart = chart;
    }

    @Override
    protected void drawLabels(Canvas c, float pos, PointF anchor) {
        float[] position = new float[]{
                0f, 0f
        };


        if (mXAxis.getShowLabels() == null) {
            throw new RuntimeException("必须调用XAxis.setShowLabels()方法");
        }

        int count = mXAxis.getShowLabels().size();

        float perWidth = 0;// mViewPortHandler.contentWidth() / count;
        TextPaint tp = new TextPaint(mAxisLabelPaint);
        tp.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < count; i += 1) {
            Log.e("&&&", "&&&");
            int ix = mXAxis.getShowLabels().keyAt(i);

            position[0] = ix;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getShowLabels().valueAt(i);
                Log.e("ee",label);
/*
                if (mXAxis.getValueFormatter() != null) {
                    label = mXAxis.getValueFormatter().getFormattedValue(label);
                }*/

             /*   if (mXAxis.getTextPosition() == MarketXAxis.TextPosition_CENTER) {
                    if (perWidth == 0) {
                        perWidth = position[0] - mViewPortHandler.contentLeft();
                    }


                    c.save();

                    StaticLayout staticLayout = new StaticLayout(label, tp, (int) perWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, true);
                    c.translate(mViewPortHandler.contentLeft() + (perWidth) * i, pos - staticLayout.getHeight());
                    staticLayout.draw(c);
                    c.restore();
                } else {*/

                int labelWidth = Utils.calcTextWidth(mAxisLabelPaint, label);

                if ((labelWidth / 2 + position[0]) > mChart.getViewPortHandler().contentRight()) {
                    position[0] = mChart.getViewPortHandler().contentRight() - labelWidth / 2;
                } else if ((position[0] - labelWidth / 2) < mChart.getViewPortHandler().contentLeft()) {
                    position[0] = mChart.getViewPortHandler().contentLeft() + labelWidth / 2;

                }



                c.drawText(label, position[0],
                        pos,
                        mAxisLabelPaint);
            }

        }
    }

    /*x轴垂直线*/
    @Override
    public void renderGridLines(Canvas c) {
        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;
        float[] position = new float[]{
                0f, 0f
        };
        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());
        mGridPaint.setPathEffect(mXAxis.getGridDashPathEffect());
        Path gridLinePath = new Path();
        if (mXAxis.getShowLabels() == null) {
            throw new RuntimeException("必须调用XAxis.setShowLabels()方法");
        }
        int count = mXAxis.getShowLabels().size();

        if (!mChart.isScaleXEnabled()) {
            count -= 1;
        }
        for (int i = 0; i < count; i += 1) {
            String t = mXAxis.getShowLabels().valueAt(i);
            int ix = mXAxis.getShowLabels().keyAt(i);

            position[0] = ix;
            mTrans.pointValuesToPixel(position);
            if (position[0] >= mViewPortHandler.offsetLeft()
                    && position[0] <= mViewPortHandler.getChartWidth()) {
                gridLinePath.moveTo(position[0], mViewPortHandler.contentBottom());
                gridLinePath.lineTo(position[0], mViewPortHandler.contentTop());
                // draw a path because lines don't support dashing on lower android versions
                c.drawPath(gridLinePath, mGridPaint);
            }
            gridLinePath.reset();

        }

    }

}
