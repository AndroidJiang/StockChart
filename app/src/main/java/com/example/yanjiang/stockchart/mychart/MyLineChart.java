package com.example.yanjiang.stockchart.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.example.yanjiang.stockchart.bean.DataParse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * 作者：ajiang
 * 邮箱：1025065158
 * 博客：http://blog.csdn.net/qqyanjiang
 */
public class MyLineChart extends LineChart {
    private MyLeftMarkerView myMarkerViewLeft;
    private MyRightMarkerView myMarkerViewRight;
    private MyBottomMarkerView mMyBottomMarkerView;
    private DataParse minuteHelper;

    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        /*此两处不能重新示例*/
        mXAxis = new MyXAxis();
        mAxisLeft = new MyYAxis(YAxis.AxisDependency.LEFT);
        mXAxisRenderer = new MyXAxisRenderer(mViewPortHandler, (MyXAxis) mXAxis, mLeftAxisTransformer, this);
        mAxisRendererLeft = new MyYAxisRenderer(mViewPortHandler, (MyYAxis) mAxisLeft, mLeftAxisTransformer);

        mAxisRight = new MyYAxis(YAxis.AxisDependency.RIGHT);
        mAxisRendererRight = new MyYAxisRenderer(mViewPortHandler, (MyYAxis) mAxisRight, mRightAxisTransformer);

    }

    /*返回转型后的左右轴*/
    @Override
    public MyYAxis getAxisLeft() {
        return (MyYAxis) super.getAxisLeft();
    }

    @Override
    public MyXAxis getXAxis() {
        return (MyXAxis) super.getXAxis();
    }


    @Override
    public MyYAxis getAxisRight() {
        return (MyYAxis) super.getAxisRight();
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyRightMarkerView markerRight,MyBottomMarkerView markerBottom, DataParse minuteHelper) {
        this.myMarkerViewLeft = markerLeft;
        this.myMarkerViewRight = markerRight;
        this.mMyBottomMarkerView=markerBottom;
        this.minuteHelper = minuteHelper;
    }

    public void setHighlightValue(Highlight h) {
        if (mData == null)
            mIndicesToHighlight = null;
        else {
            mIndicesToHighlight = new Highlight[]{
                    h};
        }
        invalidate();
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (!mDrawMarkerViews || !valuesToHighlight())
            return;
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            int xIndex = mIndicesToHighlight[i].getXIndex();
            int dataSetIndex = mIndicesToHighlight[i].getDataSetIndex();
            float deltaX = mXAxis != null
                    ? mXAxis.mAxisRange
                    : ((mData == null ? 0.f : mData.getXValCount()) - 1.f);
            if (xIndex <= deltaX && xIndex <= deltaX * mAnimator.getPhaseX()) {
                Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
                // make sure entry not null
                if (e == null || e.getXIndex() != mIndicesToHighlight[i].getXIndex())
                    continue;

                float[] pos = getMarkerPosition(e, highlight);
                // check bounds
                if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                    continue;

                float yValForXIndex1 = minuteHelper.getDatas().get(mIndicesToHighlight[i].getXIndex()).cjprice;
                float yValForXIndex2 = minuteHelper.getDatas().get(mIndicesToHighlight[i].getXIndex()).per;
                String time = minuteHelper.getDatas().get(mIndicesToHighlight[i].getXIndex()).time;
                myMarkerViewLeft.setData(yValForXIndex1);
                myMarkerViewRight.setData(yValForXIndex2);
                mMyBottomMarkerView.setData(time);
                myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);
                myMarkerViewRight.refreshContent(e, mIndicesToHighlight[i]);
                mMyBottomMarkerView.refreshContent(e, mIndicesToHighlight[i]);
                /*修复bug*/
                // invalidate();
                /*重新计算大小*/
                myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(),
                        myMarkerViewLeft.getMeasuredHeight());
                myMarkerViewRight.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewRight.layout(0, 0, myMarkerViewRight.getMeasuredWidth(),
                        myMarkerViewRight.getMeasuredHeight());
                mMyBottomMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                mMyBottomMarkerView.layout(0, 0, mMyBottomMarkerView.getMeasuredWidth(),
                        mMyBottomMarkerView.getMeasuredHeight());

                mMyBottomMarkerView.draw(canvas, pos[0]-mMyBottomMarkerView.getWidth()/2, mViewPortHandler.contentBottom());
                myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft() - myMarkerViewLeft.getWidth(), pos[1] - myMarkerViewLeft.getHeight() / 2);
                myMarkerViewRight.draw(canvas, mViewPortHandler.contentRight(), pos[1] - myMarkerViewRight.getHeight() / 2);
            }
        }
    }
}
