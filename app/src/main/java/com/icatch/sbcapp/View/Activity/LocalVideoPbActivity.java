package com.icatch.sbcapp.View.Activity;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.icatch.sbcapp.ExtendComponent.MPreview;
import com.icatch.sbcapp.ExtendComponent.ProgressWheel;
import com.icatch.sbcapp.Listener.VideoFramePtsChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.Presenter.LocalVideoPbPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.View.Interface.LocalVideoPbView;

public class LocalVideoPbActivity extends BaseActivity implements LocalVideoPbView {
    private String TAG = "LocalVideoPbActivity";
    private TextView timeLapsed;
    private TextView timeDuration;
    private SeekBar seekBar;
    private ImageButton play;
    private ImageButton back;
    private ImageButton share;
    private ImageButton delete;
    private RelativeLayout topBar;
    private RelativeLayout bottomBar;
    private TextView localVideoNameTxv;
    private MPreview localPbView;
//    private TextView pbLoadPercent;
    private boolean isShowBar =true;
    private ProgressWheel progressWheel;
    private LocalVideoPbPresenter presenter;
    private String videoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb_local_video);
        timeLapsed = (TextView) findViewById(R.id.local_pb_time_lapsed);
        timeDuration = (TextView) findViewById(R.id.local_pb_time_duration);
        seekBar = (SeekBar) findViewById(R.id.local_pb_seekBar);
        play = (ImageButton) findViewById(R.id.local_pb_play_btn);
        back = (ImageButton) findViewById(R.id.local_pb_back);
        share = (ImageButton) findViewById(R.id.shareBtn);
        delete = (ImageButton) findViewById(R.id.deleteBtn);

        topBar = (RelativeLayout) findViewById(R.id.local_pb_top_layout);
        bottomBar = (RelativeLayout) findViewById(R.id.local_pb_bottom_layout);
        localPbView = (MPreview) findViewById(R.id.local_pb_view);
        localVideoNameTxv = (TextView)findViewById(R.id.local_pb_video_name);
        progressWheel = (ProgressWheel) findViewById(R.id.local_pb_spinner);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        videoPath = data.getString("curfilePath");
        AppLog.i(TAG, "photoPath=" + videoPath);
        presenter = new LocalVideoPbPresenter(this,videoPath);
        presenter.setView(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        localPbView.addVideoFramePtsChangedListener(new VideoFramePtsChangedListener() {
            @Override
            public void onFramePtsChanged(double pts) {
                presenter.updatePbSeekbar(pts);
            }
        });
        localPbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenter.showBar(topBar.getVisibility() == View.VISIBLE ? true : false);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.stopVideoPb();
                presenter.removeEventListener();
                finish();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.setTimeLapsedValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStartTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStopTrackingTouch();
            }
        });

        //JIRA BUG ICOM-3698 begin ADD by b.jiang 20160920
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        //JIRA BUG ICOM-3698 end ADD by b.jiang 20160920
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setTimeLapsedValue(String value) {
        timeLapsed.setText(value);
    }

    @Override
    public void setTimeDurationValue(String value) {
        timeDuration.setText(value);
    }

    @Override
    public void setSeekBarProgress(int value) {
        seekBar.setProgress(value);
    }

    @Override
    public void setSeekBarMaxValue(int value) {
        seekBar.setMax(value);
    }

    @Override
    public int getSeekBarProgress() {
        return seekBar.getProgress();
    }

    @Override
    public void setSeekBarSecondProgress(int value) {
        seekBar.setSecondaryProgress(value);
    }


    @Override
    public void setPlayBtnSrc(int resid) {
        play.setImageResource(resid);
    }

    @Override
    public void showLoadingCircle(boolean isShow) {
        if(isShow){
//            MyProgressDialog.showProgressDialog(this,"Loading...");

            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.setText("0%");
            progressWheel.startSpinning();
        }else {

//            MyProgressDialog.closeProgressDialog();
            progressWheel.stopSpinning();
            progressWheel.setVisibility(View.GONE);

        }

    }

    @Override
    public void setLoadPercent(int value) {
        String temp = value + "%";
        progressWheel.setText(temp);
    }

    @Override
    public void setVideoNameTxv(String value) {
        localVideoNameTxv.setText(value);
    }

    @Override
    public void startMPreview(MyCamera mCamera, int previewLaunchMode) {
        localPbView.setVisibility(View.GONE);
        localPbView.setVisibility(View.VISIBLE);
        localPbView.start(mCamera,previewLaunchMode);
    }

    @Override
    public void stopMPreview() {
        localPbView.stop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                presenter.stopVideoPb();
                presenter.removeEventListener();
//                presenter.destroyCamera();
                finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }
}
