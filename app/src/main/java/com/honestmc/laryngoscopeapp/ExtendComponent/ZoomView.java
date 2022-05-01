package com.honestmc.laryngoscopeapp.ExtendComponent;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.icatch.wificam.customer.type.ICatchCameraProperty;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.SdkApi.CameraProperties;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhang yanhu C001012 on 2015/12/28 14:40.
 */
public class ZoomView extends RelativeLayout {
    private static final String TAG = "ZoomView";
    private final ImageButton zoomIn;
    private final ImageButton zoomOut;
    private final ZoomBar zoomBar;
    private final TextView zoomRateText;
    private static final int DISPLAY_DURATION = 5000; //ms
    private Timer timer;
    public static final int MIN_VALUE = 10;
    public static int MAX_VALUE;
    public boolean firstCreate = true;


    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View zoomView = LayoutInflater.from(context).inflate(R.layout.zoombar_view, this, true);
        zoomIn = (ImageButton) zoomView.findViewById(R.id.zoom_in);
        zoomOut = (ImageButton) zoomView.findViewById(R.id.zoom_out);
        zoomBar = (ZoomBar) zoomView.findViewById(R.id.zoomBar);
        zoomRateText = (TextView) zoomView.findViewById(R.id.zoom_rate);
        setMinValue(MIN_VALUE);
        this.post(new Runnable() {
            @Override
            public void run() {
                startDisplay();
            }
        });
    }

    public void setZoomInOnclickListener(OnClickListener onclickListener) {
        zoomIn.setOnClickListener(onclickListener);
    }

    public void setZoomOutOnclickListener(OnClickListener onclickListener) {
        zoomOut.setOnClickListener(onclickListener);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        zoomBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    private void updateZoomRateText(float zoomRate) {
        zoomRateText.setText("x " + zoomRate);
    }

    public void updateZoomBarValue(int value) {
        AppLog.i(TAG,"updateZoomBarValue value ="+value);
        zoomBar.setProgress(value);
        updateZoomRateText(value / 10.0f);
    }

    public void setMinValue(int minValue) {
        zoomBar.setMinValue(minValue);
    }

    public void setMaxValue(int maxValue) {
        MAX_VALUE = maxValue;
        zoomBar.setMax(maxValue);
    }

    public int getProgress() {
        return zoomBar.getZoomProgress();
    }

    public void startDisplay() {
        if (firstCreate == true) {
            firstCreate = false;
            return;
        }

        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_DIGITAL_ZOOM) == false) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Handler handler = getHandler();
                if(handler != null){
                    handler.post(new Runnable() {
                        public void run() {
                            setVisibility(View.GONE);
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, DISPLAY_DURATION);
        setVisibility(View.VISIBLE);
    }
}
