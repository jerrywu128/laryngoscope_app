package com.honestmc.laryngoscopeapp.View.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.Presenter.LocalVideoPbPresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Interface.LocalVideoPbView;

public class LocalVideoPbActivity extends BaseActivity implements LocalVideoPbView {
    private String TAG = "LocalVideoPbActivity";
    private ImageButton back;
    private ImageButton delete;
    private RelativeLayout topBar;
    private TextView localVideoNameTxv;
    //private MPreview localPbView;
//    private TextView pbLoadPercent;
    private boolean isShowBar =true;
    private LocalVideoPbPresenter presenter;
    private String videoPath;
    private VideoView videoView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pb_local_video);
        back = (ImageButton) findViewById(R.id.local_pb_back);
        delete = (ImageButton) findViewById(R.id.deleteBtn);

        topBar = (RelativeLayout) findViewById(R.id.local_pb_top_layout);
        //tlocalPbView = (MPreview) findViewById(R.id.local_pb_view);
        localVideoNameTxv = (TextView)findViewById(R.id.local_pb_video_name);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        videoPath = data.getString("curfilePath");
        AppLog.i(TAG, "photoPath=" + videoPath);
        presenter = new LocalVideoPbPresenter(this,videoPath);
        presenter.setView(this);

        videoView = (VideoView) findViewById(R.id.locail_videoview);

        final MediaController mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(videoView);


        videoView.setMediaController(mediacontroller);
        videoView.setVideoPath(videoPath);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               // Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();

            }
        });
        videoView.start();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*localPbView.addVideoFramePtsChangedListener(new VideoFramePtsChangedListener() {
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
        });*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.stopVideoPb();
                presenter.removeEventListener();
                finish();
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

    }

    @Override
    public void setTimeLapsedValue(String value) {

    }

    @Override
    public void setTimeDurationValue(String value) {

    }

    @Override
    public void setSeekBarProgress(int value) {

    }

    @Override
    public void setSeekBarMaxValue(int value) {

    }

    @Override
    public int getSeekBarProgress() {
       return 0;
    }

    @Override
    public void setSeekBarSecondProgress(int value) {

    }


    @Override
    public void setPlayBtnSrc(int resid) {

    }

    @Override
    public void showLoadingCircle(boolean isShow) {

    }

    @Override
    public void setLoadPercent(int value) {

    }

    @Override
    public void setVideoNameTxv(String value) {
        localVideoNameTxv.setText(value);
    }

    @Override
    public void startMPreview(MyCamera mCamera, int previewLaunchMode) {
       //t localPbView.setVisibility(View.GONE);
       //t localPbView.setVisibility(View.VISIBLE);
        //tlocalPbView.start(mCamera,previewLaunchMode);
    }

    @Override
    public void stopMPreview() {
     //t   localPbView.stop();
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
