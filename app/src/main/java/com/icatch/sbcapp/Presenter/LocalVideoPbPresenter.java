package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import com.icatch.sbcapp.BaseItems.LocalPbItemInfo;
import com.icatch.sbcapp.Tools.MediaRefresh;
import com.icatch.wificam.customer.ICatchWificamListener;
import com.icatch.wificam.customer.type.ICatchEvent;
import com.icatch.wificam.customer.type.ICatchEventID;
import com.icatch.sbcapp.ExtendComponent.MPreview;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Message.AppMessage;
import com.icatch.sbcapp.Mode.PreviewLaunchMode;
import com.icatch.sbcapp.Mode.VideoPbMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraAction;
import com.icatch.sbcapp.SdkApi.VideoPlayback;
import com.icatch.sbcapp.Tools.ConvertTools;
import com.icatch.sbcapp.View.Interface.LocalVideoPbView;
import com.icatch.wificam.customer.type.ICatchPreviewMode;

import java.io.File;
import java.util.List;

/**
 * Created by b.jiang on 2015/12/25.
 */
public class LocalVideoPbPresenter extends BasePresenter{
    private String TAG = "LocalVideoPbPresenter";
    private LocalVideoPbView localVideoPbView;
    private Activity activity;
    private VideoPlayback videoPlayback;
    private CameraAction cameraAction;
    //private MyCamera myCamera;
    private VideoPbMode videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
    private boolean needUpdateSeekBar = true;
    private String curLocalVideoPath;
    private CacheStateChangedListener cacheStateChangedListener;
    private CacheProgressListener cacheProgressListener;
    private VideoIsEndListener videoIsEndListener;
    private VideoPbHandler handler = new VideoPbHandler();
    private boolean cacheFlag =false;
    private Boolean waitForCaching =false;
    private double currentTime = -1.0;
    private int videoDuration = 0;
    private int lastSeekBarPosition;
    public List<LocalPbItemInfo> localVideoList = GlobalInfo.getInstance().localVideoList;
    private int curVideoIdx = 0;


    public LocalVideoPbPresenter(Activity activity,String videoPath) {
        super(activity);
        this.activity = activity;
        Bundle data = activity.getIntent().getExtras();
        curVideoIdx = data.getInt("curfilePosition");
        AppLog.d(TAG,"curVideoIdx=" + curVideoIdx);
        if(localVideoList != null){
            curLocalVideoPath = localVideoList.get(curVideoIdx).getFilePath();
        }
        initClint();
        addEventListener();
    }

    public void updatePbSeekbar(double pts){
        if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
            return;
        }
        currentTime = pts;
        int temp = new Double(currentTime * 100).intValue();
        handler.obtainMessage(AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR, temp, 0).sendToTarget();
    }

    public void setView(LocalVideoPbView localVideoPbView) {
        this.localVideoPbView = localVideoPbView;
        initCfg();
        initView();
       // play();
    }

    private void initView(){
        int start = curLocalVideoPath.lastIndexOf("/");
        String videoName = curLocalVideoPath.substring(start + 1);
        localVideoPbView.setVideoNameTxv(videoName);
    }

    public void initClint() {
        //tmyCamera = GlobalInfo.getInstance().getCurrentCamera();
        videoPlayback = VideoPlayback.getInstance();
        cameraAction = CameraAction.getInstance();
    }

    public void addEventListener(){
        cacheStateChangedListener = new CacheStateChangedListener();
        cacheProgressListener =new CacheProgressListener();
        videoIsEndListener = new VideoIsEndListener();
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoIsEndListener);
//        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_AUDIO_STREAM_PLAYING_ENDED, videoIsEndListener);
    }

    public void removeEventListener(){
        if (cacheStateChangedListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
        }
        if (cacheProgressListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
        }
        if (videoIsEndListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoIsEndListener);
        }
//        if (videoIsEndListener != null) {
//            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_AUDIO_STREAM_PLAYING_ENDED, videoIsEndListener);
//        }
    }

    public void play(){
        // TODO Auto-generated method stub
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            AppLog.i(TAG, "start play video");
            if (videoPlayback.startPlaybackStream(curLocalVideoPath) == false) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.e(TAG, "failed to startPlaybackStream");
                return;
            }
            needUpdateSeekBar = true;
            AppLog.i(TAG, "seekBar.getProgress() =" + localVideoPbView.getSeekBarProgress());
            int tempDuration = videoPlayback.getVideoDuration();
            AppLog.i(TAG, "end getLength = " + tempDuration);
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
            localVideoPbView.setTimeLapsedValue("00:00");
            localVideoPbView.setTimeDurationValue(ConvertTools.secondsToMinuteOrHours(tempDuration / 100));
            localVideoPbView.setSeekBarMaxValue(tempDuration);
            videoDuration = tempDuration;// temp attemp to avoid sdk
            startVideoPb();
            AppLog.i(TAG, "has start the GetVideoFrameThread() to get play video");
            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
            AppLog.i(TAG, "mode == MODE_VIDEO_PAUSE");
            if (videoPlayback.resumePlayback() == false) {
                MyToast.show(activity,R.string.dialog_failed);
                AppLog.i(TAG, "failed to resumePlayback");
                return;
            }
            needUpdateSeekBar = true;
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;

        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            AppLog.i(TAG, "begin pause the playing");
            if (videoPlayback.pausePlayback() == false) {
                MyToast.show(activity,R.string.dialog_failed);
                AppLog.i(TAG, "failed to pausePlayback");
                return;
            }
            localVideoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
            videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
            localVideoPbView.showLoadingCircle(false);
        }
    }

    public void seekBarOnStopTrackingTouch(){
        lastSeekBarPosition = localVideoPbView.getSeekBarProgress();
        localVideoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));
        if (videoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            localVideoPbView.setSeekBarProgress(lastSeekBarPosition);
            MyToast.show(activity,R.string.dialog_failed);
        }
        needUpdateSeekBar = true;
    }

    public void seekBarOnStartTrackingTouch(){
        needUpdateSeekBar = false;
        lastSeekBarPosition = localVideoPbView.getSeekBarProgress();

    }
    public void setTimeLapsedValue(int progress){
        localVideoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(progress / 100));
    }

    private void startVideoPb(){
        AppLog.i(TAG,"startVideoPb");
        cacheFlag = true;
        waitForCaching = true;
        localVideoPbView.showLoadingCircle(true);
        //tlocalVideoPbView.startMPreview(myCamera, PreviewLaunchMode.VIDEO_PB_MODE);
    }
    public void stopVideoPb(){
        if(videoPbMode == VideoPbMode.MODE_VIDEO_IDLE){
            return;
        }
        cacheFlag = false;
        localVideoPbView.showLoadingCircle(false);
        localVideoPbView.setTimeLapsedValue("00:00");
        videoPlayback.stopPlaybackStream();
        localVideoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
        localVideoPbView.setSeekBarProgress(0);
        localVideoPbView.setSeekBarSecondProgress(0);
        localVideoPbView.setTopBarVisibility(View.VISIBLE);
        localVideoPbView.setBottomBarVisibility(View.VISIBLE);
        localVideoPbView.stopMPreview();
        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
    }

    private class VideoPbHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR:
                    if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
                        return;
                    }
                    localVideoPbView.setSeekBarProgress(msg.arg1);
                    break;
                case AppMessage.EVENT_CACHE_STATE_CHANGED:
                    AppLog.i(TAG,"receive EVENT_CACHE_STATE_CHANGED");
                    AppLog.i(TAG,"EVENT_CACHE_STATE_CHANGED ---------msg.arg1 = " + msg.arg1);
                    if (cacheFlag == false) {
                        return;
                    }
                    if(videoPbMode != VideoPbMode.MODE_VIDEO_PLAY){
                        return;
                    }
                    if (msg.arg1 == 1) {
                        localVideoPbView.showLoadingCircle(true);
                        waitForCaching = true;
                    } else if (msg.arg1 == 2) {
                        localVideoPbView.showLoadingCircle(false);
                        waitForCaching = false;
                    }
                    break;
                case AppMessage.EVENT_CACHE_PROGRESS_NOTIFY:
//                    AppLog.i(TAG,"receive EVENT_CACHE_PROGRESS_NOTIFY msg.arg1=" + msg.arg1  + "msg.arg2="+msg.arg2);
                    if (cacheFlag == false) {
                        return;
                    }
                    if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY) {
                        break;
                    }
                    if (waitForCaching) {
                        localVideoPbView.setLoadPercent(msg.arg1);
                    }
                    localVideoPbView.setSeekBarSecondProgress(msg.arg2);
                    break;
                case AppMessage.EVENT_VIDEO_PLAY_COMPLETED:
                    AppLog.i(TAG,"receive EVENT_VIDEO_PLAY_COMPLETED");
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
                        stopVideoPb();
                        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                    }
                    break;
            }

        }
    }

    public class CacheStateChangedListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            handler.obtainMessage(AppMessage.EVENT_CACHE_STATE_CHANGED, arg0.getIntValue1(), 0).sendToTarget();
        }
    }

    public class CacheProgressListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            int temp = new Double(arg0.getDoubleValue1() * 100).intValue();
            handler.obtainMessage(AppMessage.EVENT_CACHE_PROGRESS_NOTIFY, arg0.getIntValue1(), temp).sendToTarget();
        }
    }

    /**
     *
     * Added by zhangyanhu C01012,2014-5-22
     */
    public class VideoIsEndListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(AppMessage.EVENT_VIDEO_PLAY_COMPLETED, 0, 0).sendToTarget();
        }
    }

    public void showBar(boolean isShowBar) {
        if (isShowBar) {
            if (videoPbMode != VideoPbMode.MODE_VIDEO_IDLE){
                localVideoPbView.setBottomBarVisibility(View.GONE);
                localVideoPbView.setTopBarVisibility(View.GONE);
            }
        } else {
            localVideoPbView.setBottomBarVisibility(View.VISIBLE);
            localVideoPbView.setTopBarVisibility(View.VISIBLE);
        }
    }
/*
    public void share(){
        if(videoPbMode != VideoPbMode.MODE_VIDEO_IDLE){
            AppLog.i(TAG, "begin stop the playing");
            stopVideoPb();
        }

       // String photoPath = this.curLocalVideoPath;
       // Uri fileUri = Uri.fromFile(new File(photoPath));
       // Intent shareIntent = new Intent();
       // shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
       // shareIntent.setType("video/*");
        //activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)));

        String photoPath = this.curLocalVideoPath;
        Log.d(TAG, photoPath.toString());
        File cameraPhoto = new File(photoPath);
        Intent shareIntent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".provider",
                cameraPhoto);
        shareIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setDataAndType( fileUri, "video/*");
        activity.startActivityForResult(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)), 1 );
    }*/

    public void delete() {
        if(videoPbMode != VideoPbMode.MODE_VIDEO_IDLE){
            AppLog.i(TAG, "begin stop the playing");
            stopVideoPb();
        }
        showDeleteEnsureDialog();
    }

    private void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        //JIRA BUG IC-759 20161226
//        builder.setTitle(R.string.image_delete_des);
        builder.setTitle(R.string.video_delete_des);

        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                stopVideoPb();
                localVideoList.remove(curVideoIdx);
                File file = new File(curLocalVideoPath);
                if (file.exists()) {
                    file.delete();
                }
                //ICOM-4574
                MediaRefresh.notifySystemToScan(curLocalVideoPath);
                activity.finish();
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
