
package com.example.yanjiang.stockchart.chat;

import android.util.SparseArray;

import com.github.mikephil.charting.components.XAxis;

public class MarketXAxis extends XAxis {


    public static int TextPosition_CENTER = 1;
    public static int TextPosition_LEFT = 2;

    public static int XAXISPOSITION_BOTTOM = 3;
    public static int XAXISPOSITION_MIDDLE = 4;


    private SparseArray<String> labels;

    private int textPosition = TextPosition_LEFT;

    public float xValAverageLength;
    private int[] colors;

    public SparseArray<String> getShowLabels() {
        return labels;
    }

    public void setShowLabels(SparseArray<String> labels) {
        this.labels = labels;
        calcXValAverageLength();
    }


    public int getTextPosition() {
        return textPosition;

    }

    private void calcXValAverageLength() {

        final SparseArray<String> showLabels = this.labels;
        if (showLabels.size() <= 0) {
            xValAverageLength = 1;
            return;
        }

        float sum = 1f;

        final int count = showLabels.size();
        for (int i = 0; i < count; i++) {
            sum += showLabels.valueAt(i).length();

        }

        xValAverageLength = sum / (float) count;
    }

    /**
     * @param position -->TextPosition_CENTER,TextPosition_LEFT
     */
    public void setTextPosition(int position) {
        textPosition = position;
    }


}
