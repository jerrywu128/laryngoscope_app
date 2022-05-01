package com.honestmc.laryngoscopeapp.View.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.honestmc.laryngoscopeapp.ExtendComponent.MPreview;
import com.honestmc.laryngoscopeapp.ExtendComponent.ProgressWheel;
import com.honestmc.laryngoscopeapp.Listener.VideoFramePtsChangedListener;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.Presenter.VideoPbPresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Interface.VideoPbView;

public class VideoPbActivity extends BaseActivity implements VideoPbView {
    private String TAG = "VideoPbActivity";
    private TextView timeLapsed;
    private TextView timeDuration;
    private SeekBar seekBar;
    private ImageButton play;
    private ImageButton back;
    private ImageButton deleteBtn;
    private ImageButton downloadBtn;
    private ImageButton stopBtn;
    private LinearLayout topBar;
    private RelativeLayout bottomBar;
    private TextView videoNameTxv;
    private MPreview videoPbView;
    private boolean isShowBar = true;
    private ProgressWheel progressWheel;
    private VideoPbPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pb);
        timeLapsed = (TextView) findViewById(R.id.video_pb_time_lapsed);
        timeDuration = (TextView) findViewById(R.id.video_pb_time_duration);
        seekBar = (SeekBar) findViewById(R.id.video_pb_seekBar);
        play = (ImageButton) findViewById(R.id.video_pb_play_btn);
        back = (ImageButton) findViewById(R.id.video_pb_back);
        stopBtn = (ImageButton) findViewById(R.id.video_pb_stop_btn);

        topBar = (LinearLayout) findViewById(R.id.video_pb_top_layout);
        bottomBar = (RelativeLayout) findViewById(R.id.video_pb_bottom_layout);
        videoPbView = (MPreview) findViewById(R.id.video_pb_view);
        videoNameTxv = (TextView) findViewById(R.id.video_pb_video_name);
        progressWheel = (ProgressWheel) findViewById(R.id.video_pb_spinner);
        deleteBtn = (ImageButton) findViewById(R.id.delete);
        downloadBtn = (ImageButton) findViewById(R.id.download);
        presenter = new VideoPbPresenter(this);
        presenter.setView(this);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        // do not display menu bar
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoPbView.addVideoFramePtsChangedListener(new VideoFramePtsChangedListener() {
            @Override
            public void onFramePtsChanged(double pts) {
                presenter.updatePbSeekbar(pts);
            }
        });
        videoPbView.setOnClickListener(new View.OnClickListener() {
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

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.stopVideoPb();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
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
    public void setPlayCircleImageViewVisibility(int visibility) {
//        play_circle_imageView.setVisibility(visibility);
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
//        AppLog.d(TAG, "showLoadingCircle isShow=" + isShow);
        if (isShow) {
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.setText("0%");
            progressWheel.startSpinning();
        } else {
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
        videoNameTxv.setText(value);
    }

    @Override
    public void startMPreview(MyCamera mCamera, int previewLaunchMode) {
        videoPbView.start(mCamera, previewLaunchMode);
    }

    @Override
    public void stopMPreview() {
        videoPbView.stop();
    }

    @Override
    public void setSeekbarEnabled(boolean enabled) {
        if(seekBar.isEnabled() != enabled){
            seekBar.setEnabled(enabled);
        }
    }

    @Override
    public void setDeleteBtnEnabled(boolean enabled) {
        deleteBtn.setEnabled(enabled);
        if (enabled) {
            deleteBtn.setImageResource(R.drawable.ic_delete_white_24dp);
        } else {
            deleteBtn.setImageResource(R.drawable.ic_delete_grey_600_24dp);
        }
    }

    @Override
    public void setDownloadBtnEnabled(boolean enabled) {
        downloadBtn.setEnabled(enabled);
        if (enabled) {
            downloadBtn.setImageResource(R.drawable.ic_file_download_white_24dp);
        } else {
            downloadBtn.setImageResource(R.drawable.ic_file_download_grey_600_24dp);
        }
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppLog.d(TAG, "onConfigurationChanged");
    }

    public void refresh() {
        presenter.refresh();
    }
}

