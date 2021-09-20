package com.icatch.sbcapp.ExtendComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by zhang yanhu C001012 on 2015/12/28 11:06.
 */
public class ZoomBar extends SeekBar {
    private static int minValue = 0;

    public ZoomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;

    }

    public synchronized int getZoomProgress() {
        // TODO Auto-generated method stub
        return super.getProgress() + minValue;
    }

    @Override
    public synchronized void setMax(int max) {
        // TODO Auto-generated method stub
        super.setMax(max - minValue);
    }

    @Override
    public synchronized void setProgress(int progress) {
        // TODO Auto-generated method stub
        super.setProgress(progress - minValue);
    }
}
