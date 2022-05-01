package com.honestmc.laryngoscopeapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.honestmc.laryngoscopeapp.AppDialog.SingleDownloadDialog;
import com.honestmc.laryngoscopeapp.AppInfo.AppInfo;
import com.honestmc.laryngoscopeapp.BaseItems.DownloadInfo;
import com.honestmc.laryngoscopeapp.BaseItems.MultiPbItemInfo;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyProgressDialog;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyToast;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Message.AppMessage;
import com.honestmc.laryngoscopeapp.Mode.PreviewLaunchMode;
import com.honestmc.laryngoscopeapp.Mode.VideoPbMode;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.Presenter.Interface.BasePresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.SdkApi.CameraAction;
import com.honestmc.laryngoscopeapp.SdkApi.FileOperation;
import com.honestmc.laryngoscopeapp.SdkApi.VideoPlayback;
import com.honestmc.laryngoscopeapp.SystemInfo.SystemInfo;
import com.honestmc.laryngoscopeapp.Tools.ConvertTools;
import com.honestmc.laryngoscopeapp.Tools.FileOpertion.FileOper;
import com.honestmc.laryngoscopeapp.Tools.FileOpertion.FileTools;
import com.honestmc.laryngoscopeapp.Tools.MediaRefresh;
import com.honestmc.laryngoscopeapp.Tools.StorageUtil;
import com.honestmc.laryngoscopeapp.View.Interface.VideoPbView;
import com.icatch.wificam.customer.ICatchWificamListener;
import com.icatch.wificam.customer.type.ICatchEvent;
import com.icatch.wificam.customer.type.ICatchEventID;
import com.icatch.wificam.customer.type.ICatchFile;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VideoPbPresenter extends BasePresenter {
    private String TAG = "VideoPbPresenter";
    private VideoPbView videoPbView;
    private Activity activity;
    private VideoPlayback videoPlayback;
    private FileOperation fileOperation;
    private CameraAction cameraAction;
    private MyCamera myCamera;
    private VideoPbMode videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
    private boolean needUpdateSeekBar = true;
    private ICatchFile curVideoFile;
    private CacheStateChangedListener cacheStateChangedListener;
    private CacheProgressListener cacheProgressListener;
    private VideoIsEndListener videoIsEndListener;
    private MediaClosedListener mediaClosedListener;
    private VideoPbHandler handler = new VideoPbHandler();
    private boolean cacheFlag = false;
    private Boolean waitForCaching = false;
    private double currentTime = -1.0;
    private int videoDuration = 0;
    private int lastSeekBarPosition;
    private int curVideoPosition;
    private ExecutorService executor;
    protected Timer downloadProgressTimer;
    private String downloadingFilename = "";
    private List<MultiPbItemInfo> fileList = GlobalInfo.getInstance().videoInfoList;
    private SingleDownloadDialog singleDownloadDialog;
    private String curFilePath = "";
    private boolean isMediaClosed = false;

    public VideoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        Intent intent = activity.getIntent();
        Bundle data = intent.getExtras();
        curVideoPosition = data.getInt("curfilePosition");
        this.curVideoFile = fileList.get(curVideoPosition).iCatchFile;
        AppLog.i(TAG, "cur video position=" + curVideoPosition + " video name=" + curVideoFile.getFileName());
        initClint();
        addEventListener();
    }

    public void updatePbSeekbar(double pts) {
        if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
            return;
        }
        currentTime = pts;
        int temp = new Double(currentTime * 100).intValue();
        handler.obtainMessage(AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR, temp, 0).sendToTarget();
    }

    public void setView(VideoPbView videoPbView) {
        this.videoPbView = videoPbView;
        initCfg();
        initView();
        play();
    }

    private void initView() {
        String fileName = curVideoFile.getFileName();
        int start = fileName.lastIndexOf("/");
        String videoName = fileName.substring(start + 1);

        videoPbView.setVideoNameTxv(videoName);
    }

    public void initClint() {
        myCamera = GlobalInfo.getInstance().getCurrentCamera();
        videoPlayback = VideoPlayback.getInstance();
        cameraAction = CameraAction.getInstance();
        fileOperation = FileOperation.getInstance();
    }

    public void addEventListener() {
        cacheStateChangedListener = new CacheStateChangedListener();
        cacheProgressListener = new CacheProgressListener();
        videoIsEndListener = new VideoIsEndListener();
        mediaClosedListener = new MediaClosedListener();
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoIsEndListener);
        cameraAction.addEventListener(ICatchEventID.ICH_EVENT_MEDIA_STREAM_CLOSED, mediaClosedListener);
    }

    public void removeEventListener() {
        if (cacheStateChangedListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED, cacheStateChangedListener);
        }
        if (cacheProgressListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS, cacheProgressListener);
        }
        if (videoIsEndListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_STREAM_PLAYING_ENDED, videoIsEndListener);
        }
        if (mediaClosedListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_MEDIA_STREAM_CLOSED, mediaClosedListener);
        }
    }

    public void play() {
        // TODO Auto-generated method stub
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            AppLog.i(TAG, "start play video");
            if (videoPlayback.startPlaybackStream(curVideoFile) == false) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.e(TAG, "failed to startPlaybackStream");
                return;
            }
            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
            startVideoPb();
            needUpdateSeekBar = true;
            AppLog.i(TAG, "seekBar.getProgress() =" + videoPbView.getSeekBarProgress());
            int tempDuration = videoPlayback.getVideoDuration();
            AppLog.i(TAG, "end getLength = " + tempDuration);
            //JIRA BUG ICOM-3594 Begin ADD by b.jiang 2016-08-16
            videoPbView.setSeekbarEnabled(true);
            //JIRA BUG ICOM-3594 End ADD by b.jiang 2016-08-16
            videoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
            videoPbView.setTimeLapsedValue("00:00");
            videoPbView.setTimeDurationValue(ConvertTools.secondsToMinuteOrHours(tempDuration / 100));
            videoPbView.setSeekBarMaxValue(tempDuration);
            videoDuration = tempDuration;// temp attemp to avoid sdk
            videoPbView.setDeleteBtnEnabled(false);
            videoPbView.setDownloadBtnEnabled(false);
            isMediaClosed = true;
            AppLog.i(TAG, "has start the GetVideoFrameThread() to get play video");
        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
            resumeVideoPb();
        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }
    }

    private void startVideoPbForSeek() {
//        stopVideoPb();
        videoPbView.stopMPreview();
        videoPlayback.stopPlaybackStream();
        AppLog.d(TAG, "startVideoPbForSeek");
        if (videoPlayback.startPlaybackStream(curVideoFile) == false) {
            MyToast.show(activity, R.string.dialog_failed);
            AppLog.e(TAG, "failed to startPlaybackStream");
            return;
        }
        videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
        startVideoPb();
        needUpdateSeekBar = true;
        //JIRA BUG ICOM-3594 Begin ADD by b.jiang 2016-08-16
        videoPbView.setSeekbarEnabled(true);
        videoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
//        videoPbView.setTimeLapsedValue("00:00");
        videoPbView.setDeleteBtnEnabled(false);
        videoPbView.setDownloadBtnEnabled(false);
        isMediaClosed = true;
    }

//    public void seekBarOnStopTrackingTouch() {
//        AppLog.d(TAG, "seekBarOnStopTrackingTouch lastSeekBarPosition=" + lastSeekBarPosition + " videoDuration=" + videoDuration);
//        //stream已停止或者在最后5s seek
//        if (!isMediaClosed || (videoDuration - lastSeekBarPosition) < 500) {
//            startVideoPbForSeek();
//        }
//        lastSeekBarPosition = videoPbView.getSeekBarProgress();
//        videoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));
//
//        if (videoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//            videoPbView.setSeekBarProgress(lastSeekBarPosition);
//            MyToast.show(activity, R.string.dialog_failed);
//        }
//        needUpdateSeekBar = true;
//    }

    public void seekBarOnStopTrackingTouch() {
        AppLog.d(TAG, "seekBarOnStopTrackingTouch lastSeekBarPosition=" + lastSeekBarPosition + " videoDuration=" + videoDuration);
        lastSeekBarPosition = videoPbView.getSeekBarProgress();
        videoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (videoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
                    videoPlayback.resumePlayback();
//                    resumeVideoPb();
//                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
                            videoPbView.setPlayCircleImageViewVisibility(View.GONE);
                            videoPbView.setDeleteBtnEnabled(false);
                            videoPbView.setDownloadBtnEnabled(false);
                            videoPbView.showLoadingCircle(true);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoPbView.setSeekBarProgress(lastSeekBarPosition);
                            MyToast.show(activity, R.string.dialog_failed);
                        }
                    });

                }
                videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
                needUpdateSeekBar = true;
            }
        }).start();

    }

    public void seekBarOnStartTrackingTouch() {
        needUpdateSeekBar = false;
        lastSeekBarPosition = videoPbView.getSeekBarProgress();
        videoPlayback.pausePlayback();
        videoPbView.showLoadingCircle(false);
        videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
        AppLog.d(TAG, "seekBarOnStartTrackingTouch lastSeekBarPosition=" + lastSeekBarPosition);

    }

    public void setTimeLapsedValue(int progress) {
        if((videoDuration - progress) < 500) {
            videoPbView.setSeekbarEnabled(false);
        }
        videoPbView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(progress / 100));
    }

    private void resumeVideoPb() {
        AppLog.i(TAG, "mode == MODE_VIDEO_PAUSE");
        if (videoPlayback.resumePlayback() == false) {
            MyToast.show(activity, R.string.dialog_failed);
            AppLog.i(TAG, "failed to resumePlayback");
            return;
        }
        videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
        needUpdateSeekBar = true;
        videoPbView.setPlayBtnSrc(R.drawable.ic_pause_white_36dp);
        videoPbView.setPlayCircleImageViewVisibility(View.GONE);
        videoPbView.setDeleteBtnEnabled(false);
        videoPbView.setDownloadBtnEnabled(false);
    }

    private void pauseVideoPb() {
        AppLog.i(TAG, "begin pause the playing");
        if (videoPlayback.pausePlayback() == false) {
            MyToast.show(activity, R.string.dialog_failed);
            AppLog.i(TAG, "failed to pausePlayback");
            return;
        }
        videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
        videoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
        videoPbView.setPlayCircleImageViewVisibility(View.VISIBLE);
        videoPbView.showLoadingCircle(false);
        videoPbView.setDeleteBtnEnabled(true);
        videoPbView.setDownloadBtnEnabled(true);
    }

    private void startVideoPb() {
        AppLog.i(TAG, "startVideoPb");
        videoPbView.startMPreview(myCamera, PreviewLaunchMode.VIDEO_PB_MODE);
        cacheFlag = true;
        waitForCaching = true;
        videoPbView.setPlayCircleImageViewVisibility(View.GONE);
        videoPbView.showLoadingCircle(true);

    }

    public void stopVideoPb() {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        videoPbView.showLoadingCircle(false);
        videoPbView.setTimeLapsedValue("00:00");
        videoPlayback.stopPlaybackStream();
        videoPbView.stopMPreview();
        videoPbView.setPlayBtnSrc(R.drawable.ic_play_arrow_white_36dp);
        videoPbView.setSeekBarProgress(0);
        videoPbView.setSeekBarSecondProgress(0);
        videoPbView.setTopBarVisibility(View.VISIBLE);
        videoPbView.setBottomBarVisibility(View.VISIBLE);
        videoPbView.setPlayCircleImageViewVisibility(View.VISIBLE);
        //JIRA BUG ICOM-3594 Begin ADD by b.jiang 2016-08-16
        videoPbView.setSeekbarEnabled(false);
        //JIRA BUG ICOM-3594 End ADD by b.jiang 2016-08-16
        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
        videoPbView.setDeleteBtnEnabled(true);
        videoPbView.setDownloadBtnEnabled(true);
    }

    public void delete() {
        //ICOM-4097 ADD by b.jiang 20170112
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }
        showDeleteEnsureDialog();
    }

    public void download() {
        //ICOM-4097 ADD by b.jiang 20170112
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }
        showDownloadEnsureDialog();
    }

    public void refresh() {
        AppLog.d(TAG, "refresh");
        initClint();
        addEventListener();
        handler.post(new Runnable() {
            @Override
            public void run() {
                stopVideoPb();
            }
        });
    }

    private class VideoPbHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR:
                    if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || needUpdateSeekBar == false) {
                        return;
                    }
                    videoPbView.setSeekBarProgress(msg.arg1);
                    break;
                case AppMessage.EVENT_CACHE_STATE_CHANGED:
//                    AppLog.i(TAG, "receive EVENT_CACHE_STATE_CHANGED ---------msg.arg1 = " + msg.arg1);
                    if (cacheFlag == false) {
                        return;
                    }
                    if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY) {
                        return;
                    }
                    if (msg.arg1 == 1) {
                        videoPbView.showLoadingCircle(true);
                        waitForCaching = true;
                    } else if (msg.arg1 == 2) {
                        videoPbView.showLoadingCircle(false);
                        waitForCaching = false;
                    }
                    break;
                case AppMessage.EVENT_CACHE_PROGRESS_NOTIFY:
//                    AppLog.i(TAG, "receive EVENT_CACHE_PROGRESS_NOTIFY msg.arg1=" + msg.arg1 + "waitForCaching=" + waitForCaching);
                    if (cacheFlag == false) {
                        return;
                    }
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE || videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                        return;
                    }
                    if (waitForCaching) {
                        videoPbView.setLoadPercent(msg.arg1);
                    }
                    videoPbView.setSeekBarSecondProgress(msg.arg2);
                    break;
                case AppMessage.EVENT_VIDEO_PLAY_COMPLETED:
                    AppLog.i(TAG, "receive EVENT_VIDEO_PLAY_COMPLETED");
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY || videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                        cacheFlag = false;
                        stopVideoPb();
                        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                    }
                    break;
                case AppMessage.MESSAGE_CANCEL_VIDEO_DOWNLOAD:
                    AppLog.d(TAG, "receive CANCEL_VIDEO_DOWNLOAD_SUCCESS");
                    if (singleDownloadDialog != null) {
                        singleDownloadDialog.dismissDownloadDialog();
                    }
                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    if (fileOperation.cancelDownload() == false) {
                        MyToast.show(activity, R.string.dialog_cancel_downloading_failed);
                        break;
                    }
                    try {
                        Thread.currentThread().sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String filePath = StorageUtil.getDownloadPath(activity) + curVideoFile.getFileName();
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                    MyToast.show(activity, R.string.dialog_cancel_downloading_succeeded);
                    break;

                case AppMessage.MESSAGE_STREAM_CLOSED:
                    AppLog.d(TAG, "receive MESSAGE_STREAM_CLOSED");
                    videoPbView.setSeekbarEnabled(false);
                    isMediaClosed = false;
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
     * Added by zhangyanhu C01012,2014-5-22
     */
    public class VideoIsEndListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(AppMessage.EVENT_VIDEO_PLAY_COMPLETED, 0, 0).sendToTarget();
        }
    }

    public class MediaClosedListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(AppMessage.MESSAGE_STREAM_CLOSED, 0, 0).sendToTarget();
        }
    }

    public void showBar(boolean isShowBar) {
        if (isShowBar) {
            if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
                videoPbView.setBottomBarVisibility(View.GONE);
                videoPbView.setTopBarVisibility(View.GONE);
                videoPbView.setPlayCircleImageViewVisibility(View.GONE);
            }
        } else {
            videoPbView.setBottomBarVisibility(View.VISIBLE);
            videoPbView.setTopBarVisibility(View.VISIBLE);
            if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY) {
                videoPbView.setPlayCircleImageViewVisibility(View.VISIBLE);
            }
        }

    }

    private class DownloadThread implements Runnable {
        private String TAG = "DownloadThread";
        long fileSize;
        String fileType;
        private ICatchFile curVideoFile;
        private String path;

        public DownloadThread(ICatchFile iCatchFile, String path) {
            this.curVideoFile = iCatchFile;
            this.path = path;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;
            fileSize = curVideoFile.getFileSize();
            String fileName = curVideoFile.getFileName();
            String downloadingFilename = path + fileName;
            boolean temp = fileOperation.downloadFile(curVideoFile, downloadingFilename);
            if (temp == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (singleDownloadDialog != null) {
                            singleDownloadDialog.dismissDownloadDialog();
                        }
                        if (downloadProgressTimer != null) {
                            downloadProgressTimer.cancel();
                        }
                        MyToast.show(activity, "Download failed");
                    }
                });
                AppInfo.isDownloading = false;
                return;
            }
            if (fileName.endsWith(".mov") || fileName.endsWith(".MOV")) {
                fileType = "video/mov";
            } else {
                fileType = "video/mp4";
            }
            MediaRefresh.addMediaToDB(activity, downloadingFilename, fileType);
            AppLog.d(TAG, "end downloadFile temp =" + temp);
            AppInfo.isDownloading = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (singleDownloadDialog != null) {
                        singleDownloadDialog.dismissDownloadDialog();
                    }
                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    MyToast.show(activity, "Downloaded to " + path);
                }
            });
//            }
            AppLog.d(TAG, "end DownloadThread");
        }
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            Boolean retValue = false;
            retValue = fileOperation.deleteFile(curVideoFile);
            if (retValue == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.dialog_delete_failed_single);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        GlobalInfo.getInstance().videoInfoList.remove(curVideoPosition);
                        activity.finish();
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    public void showDownloadEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_downloading_single);
        long videoFileSize = 0;
        videoFileSize = fileList.get(curVideoPosition).getFileSizeInteger() / 1024 / 1024;
        AppLog.d(TAG, "video FileSize=" + videoFileSize);
        long minute = videoFileSize / 60;
        long seconds = videoFileSize % 60;

        CharSequence what = activity.getResources().getString(R.string.gallery_download_with_vid_msg).replace("$1$", "1")
                .replace("$3$", String.valueOf(seconds)).replace("$2$", String.valueOf(minute));
        builder.setMessage(what);
        builder.setNegativeButton(R.string.gallery_download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppLog.d(TAG, "showProgressDialog");
//                MyProgressDialog.showProgressDialog(activity,R.string.dialog_downloading_single);
                //ICOM-4097
//                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
//                    stopVideoPb();
//                }
                if (SystemInfo.getSDFreeSize(activity) < curVideoFile.getFileSize()) {
                    dialog.dismiss();
                    MyToast.show(activity, R.string.text_sd_card_memory_shortage);
                } else {
                    singleDownloadDialog = new SingleDownloadDialog(activity, curVideoFile);
                    singleDownloadDialog.setBackBtnOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handler.obtainMessage(AppMessage.MESSAGE_CANCEL_VIDEO_DOWNLOAD, 0, 0).sendToTarget();
                        }
                    });
                    singleDownloadDialog.showDownloadDialog();
                    final String path = StorageUtil.getDownloadPath(activity);
                    String fileName = curVideoFile.getFileName();
                    FileOper.createDirectory(path);
                    downloadingFilename = path + fileName;
                    executor = Executors.newSingleThreadExecutor();
                    executor.submit(new DownloadThread(curVideoFile, path), null);
                    downloadProgressTimer = new Timer();
                    downloadProgressTimer.schedule(new DownloadProcessTask(FileTools.chooseUniqueFilename(downloadingFilename)), 0, 1000);
                }
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //ICOM-4097
//                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
//                    resumeVideoPb();
//                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(activity.getResources().getString(R.string.gallery_delete_des).replace("$1$", "1"));
        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                //ICOM-4097
                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                    stopVideoPb();
                }
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                //ICOM-4097
//                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
//                    resumeVideoPb();
//                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    class DownloadProcessTask extends TimerTask {
        int downloadProgress = 0;
        long fileSize;
        long curFileLength;
        private String curFilePath;

        public DownloadProcessTask(String curFilePath) {
            this.curFilePath = curFilePath;
        }

        @Override
        public void run() {
            String path;
//            path = StorageUtil.getDownloadPath(activity);
            File file = new File(curFilePath);
            fileSize = curVideoFile.getFileSize();
            curFileLength = file.length();
            if (file != null) {
                if (curFileLength == fileSize) {
                    downloadProgress = 100;
                } else {
                    downloadProgress = (int) (file.length() * 100 / fileSize);
                }
            } else {
                downloadProgress = 0;
            }
            final DownloadInfo downloadInfo = new DownloadInfo(curVideoFile, fileSize, curFileLength, downloadProgress, false);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (singleDownloadDialog != null) {
                        singleDownloadDialog.updateDownloadStatus(downloadInfo);
                    }
                    AppLog.d(TAG, "update Process downloadProgress=" + downloadProgress);
                }
            });
            AppLog.d(TAG, "end DownloadProcessTask");
        }
    }
}

