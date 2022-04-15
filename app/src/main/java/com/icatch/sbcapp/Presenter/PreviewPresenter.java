package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.icatch.sbcapp.Adapter.SettingListAdapter;
import com.icatch.sbcapp.AppDialog.AppDialog;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.BaseItems.CameraSwitch;
import com.icatch.sbcapp.BaseItems.SlowMotion;
import com.icatch.sbcapp.BaseItems.TimeLapseMode;
import com.icatch.sbcapp.BaseItems.Tristate;
import com.icatch.sbcapp.BaseItems.Upside;
import com.icatch.sbcapp.Beans.GoogleToken;
import com.icatch.sbcapp.Beans.SettingMenu;
import com.icatch.sbcapp.Beans.StreamInfo;
import com.icatch.sbcapp.CustomException.NullPointerException;
import com.icatch.sbcapp.DataConvert.StreamInfoConvert;
import com.icatch.sbcapp.ExtendComponent.Ffmpeg;
import com.icatch.sbcapp.ExtendComponent.MPreview;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnDecodeTimeListener;
import com.icatch.sbcapp.Listener.OnSettingCompleteListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Message.AppMessage;
import com.icatch.sbcapp.Mode.IQMode;
import com.icatch.sbcapp.Mode.PreviewMode;
import com.icatch.sbcapp.Model.Implement.SDKEvent;
import com.icatch.sbcapp.Model.UIDisplaySource;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.Oauth.CreateBroadcast;
import com.icatch.sbcapp.Oauth.GoogleAuthTool;
import com.icatch.sbcapp.Oauth.YoutubeCredential;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraAction;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.CameraState;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SdkApi.PreviewStream;
import com.icatch.sbcapp.Setting.OptionSetting;
import com.icatch.sbcapp.ThumbnailGetting.ThumbnailOperation;
import com.icatch.sbcapp.Tools.BitmapTools;
import com.icatch.sbcapp.Tools.ConvertTools;
import com.icatch.sbcapp.Tools.FileDES;
import com.icatch.sbcapp.Tools.FileOpertion.FileOper;
import com.icatch.sbcapp.Tools.FileOpertion.FileTools;
import com.icatch.sbcapp.Tools.QRCode;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.Tools.TimeTools;
import com.icatch.sbcapp.View.Activity.LoginGoogleActivity;
import com.icatch.sbcapp.View.Interface.PreviewView;
import com.icatch.wificam.customer.ICatchWificamConfig;
import com.icatch.wificam.customer.ICatchWificamPreview;
import com.icatch.wificam.customer.type.ICatchEventID;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchH264StreamParam;
import com.icatch.wificam.customer.type.ICatchMJPGStreamParam;
import com.icatch.wificam.customer.type.ICatchMode;
import com.icatch.wificam.customer.type.ICatchPreviewMode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.icatch.sbcapp.Mode.PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 14:22.
 */
public class PreviewPresenter extends BasePresenter {
    private static final String TAG = "PreviewPresenter";

    private MediaPlayer videoCaptureStartBeep;
    private MediaPlayer modeSwitchBeep;
    private MediaPlayer stillCaptureStartBeep;
    private MediaPlayer continuousCaptureBeep;
    private Activity activity;
    private PreviewView previewView;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;
    private CameraState cameraState;
    private PreviewStream previewStream;
    private FileOperation fileOperation;
    private ICatchWificamPreview cameraPreviewStreamClint;
    private MyCamera currentCamera;
    private PreviewHandler previewHandler;
    private SDKEvent sdkEvent;
    private int curMode = PreviewMode.APP_STATE_NONE_MODE;
    private int IqMode = IQMode.NONE;
    private Timer videoCaptureButtomChangeTimer;
    public boolean videoCaptureButtomChangeFlag = true;
    private Timer recordingLapseTimeTimer;
    private int lapseTime = 0;
    private List<SettingMenu> settingMenuList;
    private SettingListAdapter settingListAdapter;
    private boolean allowClickButtoms = true;
    private int currentSettingMenuMode;
    private WifiSSReceiver wifiSSReceiver;
    private Boolean supportStreaming = true;
    private long lastCilckTime = 0;
    private long lastRecodeTime;
    private Tristate ret;
    private int curCacheTime = 0;
    private boolean isYouTubeLiving = false;
    private boolean needShowSBCHint = true;
    private MediaRecorder mediaRecorder;;
    private MPreview mPreview;
    private boolean iq_isCheck_Password =false;
    public PreviewPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(PreviewView previewView) {
        this.previewView = previewView;
        initCfg();
//        initData();
    }

    public void initUI() {
        if (AppInfo.youtubeLive) {
            previewView.setYouTubeLiveLayoutVisibility(View.VISIBLE);
        } else {
            previewView.setYouTubeLiveLayoutVisibility(View.GONE);
        }
    }


    public void initData() {
        cameraProperties = CameraProperties.getInstance();
        cameraAction = CameraAction.getInstance();
        cameraState = CameraState.getInstance();
        previewStream = PreviewStream.getInstance();
        fileOperation = FileOperation.getInstance();
        currentCamera = GlobalInfo.getInstance().getCurrentCamera();
        cameraPreviewStreamClint = currentCamera.getpreviewStreamClient();

        videoCaptureStartBeep = MediaPlayer.create(activity, R.raw.camera_timer02);
        stillCaptureStartBeep = MediaPlayer.create(activity, R.raw.captureshutter02);
        continuousCaptureBeep = MediaPlayer.create(activity, R.raw.captureburst02);
        modeSwitchBeep = MediaPlayer.create(activity, R.raw.focusbeep);

        previewHandler = new PreviewHandler();
        sdkEvent = new SDKEvent(previewHandler);
        /**取得原先IQ設定值*/
        previewView.setProgressSave(0,currentCamera.getUSB_PIMA_DCP_IQ_BRIGHTNESS().getCurrentValue());
        previewView.setProgressSave(1,currentCamera.getUSB_PIMA_DCP_IQ_SATURATION().getCurrentValue());
        previewView.setProgressSave(2,currentCamera.getUSB_PIMA_DCP_IQ_HUE().getCurrentValue());



        if (cameraProperties.hasFuction(0xD7F0)) {
            cameraProperties.setCaptureDelayMode(1);
        }
//JIRA BUG delete IC-591
//        int cacheTime = cameraProperties.getPreviewCacheTime();
//        if (cacheTime < 200) {
//            cacheTime = 200;
//        }
//        ICatchWificamConfig.getInstance().setPreviewCacheParam(cacheTime, 200);
        AppLog.i(TAG, "cameraProperties.getMaxZoomRatio() =" + cameraProperties.getMaxZoomRatio());

//        GetCurrentImageSizeTask task = new GetCurrentImageSizeTask();
//		getImageSizeTimer = new Timer(true);
//		getImageSizeTimer.schedule(task, 0,5000);
        FileOper.createDirectory(StorageUtil.getDownloadPath(activity));



    }

    public void checkFirstInapp(){
        //判斷是否第一次開啟app，並打開初次導覽頁面
        Boolean isFirstIn = false;
        SharedPreferences pref = activity.getSharedPreferences("myActivityName", 0);
        isFirstIn = pref.getBoolean("isFirstIn", true);
        if(isFirstIn){
            previewView.setBootPage(View.VISIBLE);
        }
        //下一次開啟則為否
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    public void initStatus() {
        int resid = ThumbnailOperation.getBatteryLevelIcon();
        if (resid > 0) {
            previewView.setBatteryIcon(resid);
            if (resid == R.drawable.ic_battery_charging_green24dp) {
                AppDialog.showLowBatteryWarning(activity);
            }
        }
        IntentFilter wifiSSFilter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        wifiSSReceiver = new WifiSSReceiver();
        activity.registerReceiver(wifiSSReceiver, wifiSSFilter);
        GlobalInfo.getInstance().startConnectCheck();
        if (AppInfo.displayDecodeTime) {
            previewView.setDecodeTimeLayoutVisibility(View.VISIBLE);
            previewView.setOnDecodeTimeListener(new OnDecodeTimeListener() {
                @Override
                public void decodeTime(final long time) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            previewView.setDecodeTimeTxv((time / 1000.0) + "ms");
                        }
                    });
                }
            });
        } else {
            previewView.setDecodeTimeLayoutVisibility(View.GONE);
            previewView.setOnDecodeTimeListener(null);
        }

    }

    public void changeCameraMode(final int previewMode, final ICatchPreviewMode ichVideoPreviewMode) {
        AppLog.i(TAG, "start changeCameraMode ichVideoPreviewMode=" + ichVideoPreviewMode);
        AppLog.i(TAG, "start changeCameraMode previewMode=" + previewMode + " isStreaming=" + currentCamera.isStreaming);
        if (currentCamera.isStreaming) {
            AppLog.d(TAG, "changeCameraMode currnet streaming has started,do not need to start again!");
            return;
        }
        ret = Tristate.FALSE;
        previewView.setmPreviewVisibility(View.GONE);
        MyProgressDialog.showProgressDialog(activity, "processing..");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //add by b.jiang 兼容旧版Firmware
                if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                        previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                        previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                        previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                    cameraProperties.getRemainImageNum();
                } else {
                    cameraProperties.getRecordingRemainTime();
                }
                //end add

                ret = startMediaStream(ichVideoPreviewMode);
                if (ret == Tristate.NORMAL) {
                    currentCamera.isStreaming = true;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                            previewView.startMPreview(currentCamera);
                            String info = "Current cache time=" + curCacheTime + "  enableSoftwareDecoder=" + GlobalInfo.enableSoftwareDecoder;
                            previewView.setDecodeInfo(info);
//                            createUIByMode(curMode);
                            AppLog.i(TAG, "startMPreview");
                            supportStreaming = true;
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                    previewHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createUIByMode(curMode);
                        }
                    }, 1000);
                } else if (ret == Tristate.ABNORMAL) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            curMode =  PreviewMode.APP_STATE_VIDEO_PREVIEW;
                            createUIByMode(curMode);
                            previewView.setmPreviewVisibility(View.GONE);
                            previewView.setSupportPreviewTxvVisibility(View.VISIBLE);
                            supportStreaming = false;
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                } else if (ret == Tristate.FALSE) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.show(activity, R.string.stream_set_error);
                            curMode =  PreviewMode.APP_STATE_VIDEO_PREVIEW;
                            createUIByMode(curMode);
                            supportStreaming = false;
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }
    public void changeCameraMode(final int previewMode) {
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            //IC-754 Begin modify 20161212 BY b.jiang
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_recording);
            }
            //IC-754 End modify 20161212 BY b.jiang
            return;
        }
        if(curMode==PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode==PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            curMode = previewMode;
            previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
            previewView.setchangeVideoBtnBackgroundResource(R.drawable.selector_radio_video);
            previewView.setchangeCameraBtnBackgroundResource(R.drawable.capture_toggle_btn_on);
        }else if(curMode == PreviewMode.APP_STATE_STILL_PREVIEW){
            curMode = previewMode;
            previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
            previewView.setchangeVideoBtnBackgroundResource(R.drawable.video_toggle_btn_on);
            previewView.setchangeCameraBtnBackgroundResource(R.drawable.selector_radio_capture);

        }
    }


    public boolean stopMediaStream() {
        if (!currentCamera.isStreaming) {
            AppLog.d(TAG, "stopMediaStream currnet Streaming has stopped,do not need to stop again!");
            return true;
        }
        boolean ret = previewStream.stopMediaStream(cameraPreviewStreamClint);
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        currentCamera.isStreaming = false;
        return ret;
    }

    public Tristate startMediaStream(ICatchPreviewMode ichVideoPreviewMode) {
        AppLog.d(TAG, "start startMediaStream");
        Tristate ret = Tristate.FALSE;
        String streamUrl = cameraProperties.getCurrentStreamInfo();
//        String streamUrl = "H264?W=1280&H=720&BR=2000000&FPS=15&";
//        String streamUrl =  "MJPG?W=720&H=540&BR=4000000";
        AppLog.d(TAG, "1123 start startMediaStream streamUrl=[" + streamUrl + "]");
        if (streamUrl == null) {
            //JIRA IC-591
            int cacheTime = cameraProperties.getPreviewCacheTime();
            if (cacheTime > 0 && cacheTime < 200) {
                cacheTime = 500;
            }
//            int cacheTime = 0;
            Boolean setCacheRet = ICatchWificamConfig.getInstance().setPreviewCacheParam(cacheTime, 200);
            ICatchWificamConfig.getInstance().allowMosaic(true);
            AppLog.d(TAG, "start startMediaStream setCacheRet=" + setCacheRet + " cacheTime=" + cacheTime);
            curCacheTime = cacheTime;
            ICatchMJPGStreamParam param = new ICatchMJPGStreamParam();
            ret = previewStream.startMediaStream(cameraPreviewStreamClint, param,
                    ichVideoPreviewMode, AppInfo.disableAudio);
            return ret;
        }

        StreamInfo streamInfo = StreamInfoConvert.convertToStreamInfoBean(streamUrl);
        GlobalInfo.curFps = streamInfo.fps;
        if (streamInfo.mediaCodecType.equals("MJPG")) {
            //JIRA IC-591
            int cacheTime = cameraProperties.getPreviewCacheTime();
            if (cacheTime > 0 && cacheTime < 200) {
                cacheTime = 200;
            }
//            int cacheTime = 0;
            ICatchWificamConfig.getInstance().setPreviewCacheParam(cacheTime, 200);
            ICatchWificamConfig.getInstance().allowMosaic(true);
            AppLog.d(TAG, "start startMediaStream MJPG cacheTime=" + cacheTime);
            curCacheTime = cacheTime;
            ICatchMJPGStreamParam param = new ICatchMJPGStreamParam(streamInfo.width, streamInfo.height, streamInfo.bitrate, 50);
            AppLog.i(TAG, "begin startMediaStream MJPG");
            ret = previewStream.startMediaStream(cameraPreviewStreamClint, param,
                    ichVideoPreviewMode, AppInfo.disableAudio);
        } else if (streamInfo.mediaCodecType.equals("H264")) {
            //JIRA IC-591
            int cacheTime = cameraProperties.getPreviewCacheTime();
            if (cacheTime > 0 && cacheTime < 200) {
                cacheTime = 500;
            }
//            int cacheTime = 0;
            ICatchWificamConfig.getInstance().setPreviewCacheParam(cacheTime, 200);
            ICatchWificamConfig.getInstance().allowMosaic(true);
            AppLog.d(TAG, "start startMediaStream start H264 startMediaStream cacheTime=" + cacheTime);
            curCacheTime = cacheTime;
//            if (GlobalInfo.enableSoftwareDecoder) {
//                streamUrl = ConvertTools.resolutionConvert(streamUrl);
//            }
//            ICatchCustomerStreamParam param = new ICatchCustomerStreamParam(554, streamUrl);

            ICatchH264StreamParam param = new ICatchH264StreamParam(streamInfo.width, streamInfo.height, streamInfo.bitrate, streamInfo.fps);
            ret = previewStream.startMediaStream(cameraPreviewStreamClint, param, ichVideoPreviewMode, AppInfo.disableAudio);
        }
        AppLog.i(TAG, "end startMediaStream ret = " + ret);
        return ret;
    }

    public void startOrStopCapture() {

        AppLog.d(TAG, "begin startOrStopCapture curMode=" + curMode);
        if (TimeTools.isFastClick()) {
            return;
        }

        this.mPreview = mPreview;
        if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
            previewView.setRecording_text(View.VISIBLE);
            startVideoCaptureButtomChangeTimer();
            if (videoCaptureStartBeep != null) {
                videoCaptureStartBeep.start();
            }
            previewView.startRecordlocalCapture();
            /*

            if (cameraProperties.isSDCardExist() == false) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            int remainTime = cameraProperties.getRecordingRemainTime();
            if (remainTime == 0) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            } else if (remainTime < 0) {
                AppDialog.showDialogWarn(activity, R.string.text_get_data_exception);
                return;
            }
            if (videoCaptureStartBeep != null) {
                videoCaptureStartBeep.start();
            }

            lastRecodeTime = System.currentTimeMillis();
            if (cameraAction.startMovieRecord()) {
                //start();
                AppLog.i(TAG, "startRecordingLapseTimeTimer(0)");
                curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                startVideoCaptureButtomChangeTimer();
                startRecordingLapseTimeTimer(0);



            }*/
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            previewView.setRecording_text(View.GONE);
            stopVideoCaptureButtomChangeTimer();
            if (videoCaptureStartBeep != null) {
                videoCaptureStartBeep.start();
            }
            previewView.stopRecordlocalCapture();
            /*
            if (videoCaptureStartBeep != null) {
                videoCaptureStartBeep.start();
            }
            if (System.currentTimeMillis() - lastRecodeTime < 2000) {
                MyToast.show(activity, "Operation Frequent!");
                return;
            }
            if (cameraAction.stopVideoCapture()) {
                //stop();
                curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                stopVideoCaptureButtomChangeTimer();
                stopRecordingLapseTimeTimer();
                String info = currentCamera.getVideoSize().getCurrentUiStringInPreview() + "/" + ConvertTools.secondsToMinuteOrHours(cameraProperties
                        .getRecordingRemainTime());
                previewView.setPreviewInfo(info);

            }*/
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {


            previewView.startPhotolocalCapture();
            startPhotoCapture();


        }
        AppLog.d(TAG, "end processing for responsing captureBtn clicking");
    }
/*
    public void start(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);


        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        mediaRecorder.setOutputFile("/DCIM/test3/"+System.currentTimeMillis()+".mp4");
        mediaRecorder.setVideoSize(mPreview.getWidth(), mPreview.getHeight());
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if(mediaRecorder!=null){
            try {
                mediaRecorder.stop();
            }catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.release();
            mediaRecorder=null;
        }

    }*/

    public void createUIByMode(int previewMode) {
        AppLog.i(TAG, "start createUIByMode previewMode=" + previewMode);
        if (cameraProperties.cameraModeSupport(ICatchMode.ICH_MODE_VIDEO)) {
            if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                    previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                previewView.setchangeVideoBtnBackgroundResource(R.drawable.video_toggle_btn_on);
                previewView.setchangeCameraBtnBackgroundResource(R.drawable.selector_radio_capture);
            }
        }
        if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                previewMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            previewView.setchangeVideoBtnBackgroundResource(R.drawable.selector_radio_video);
            previewView.setchangeCameraBtnBackgroundResource(R.drawable.capture_toggle_btn_on);
        }


        if (previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
        } else if (previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                previewMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
        }
        if (currentCamera.getCaptureDelay().needDisplayByMode(previewMode)) {
            previewView.setDelayCaptureLayoutVisibility(View.VISIBLE);
            previewView.setDelayCaptureTextTime(currentCamera.getCaptureDelay().getCurrentUiStringInPreview());
        } else {
            previewView.setDelayCaptureLayoutVisibility(View.GONE);
        }
        if (currentCamera.getCameraSwitch().needDisplayByMode(previewMode)) {
            previewView.setCameraSwitchLayoutVisibility(View.VISIBLE);
        } else {
            previewView.setCameraSwitchLayoutVisibility(View.GONE);
        }
        if (currentCamera.getImageSize().needDisplayByMode(previewMode)) {
            String info = currentCamera.getImageSize().getCurrentUiStringInPreview() + "/" + cameraProperties.getRemainImageNum();
            previewView.setPreviewInfo(info);
        }

        if (currentCamera.getVideoSize().needDisplayByMode(previewMode)) {
            String info = currentCamera.getVideoSize().getCurrentUiStringInPreview() + "/" + ConvertTools.secondsToMinuteOrHours(cameraProperties
                    .getRecordingRemainTime());
            previewView.setPreviewInfo(info);
        }

        if (currentCamera.getBurst().needDisplayByMode(previewMode)) {
            previewView.setBurstStatusVisibility(View.VISIBLE);
            try {
                previewView.setBurstStatusIcon(currentCamera.getBurst().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.setBurstStatusVisibility(View.GONE);
        }

        if (currentCamera.getWhiteBalance().needDisplayByMode(previewMode)) {
            previewView.setWbStatusVisibility(View.VISIBLE);
            try {
                previewView.setWbStatusIcon(currentCamera.getWhiteBalance().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.setWbStatusVisibility(View.GONE);
        }

        if (currentCamera.getUpside().needDisplayByMode(previewMode) &&
                cameraProperties.getCurrentUpsideDown() == Upside.UPSIDE_ON) {
            previewView.setUpsideVisibility(View.VISIBLE);
        } else {
            previewView.setUpsideVisibility(View.GONE);
        }

        if (currentCamera.getSlowMotion().needDisplayByMode(previewMode) &&
                cameraProperties.getCurrentSlowMotion() == SlowMotion.SLOW_MOTION_ON) {
            previewView.setSlowMotionVisibility(View.VISIBLE);
        } else {
            previewView.setSlowMotionVisibility(View.GONE);
        }

        if (currentCamera.getTimeLapseMode().needDisplayByMode(previewMode)) {
            previewView.settimeLapseModeVisibility(View.VISIBLE);
            try {
                previewView.settimeLapseModeIcon(currentCamera.getTimeLapseMode().getCurrentIcon());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            previewView.settimeLapseModeVisibility(View.GONE);
        }

    }

    public void startVideoCaptureButtomChangeTimer() {
        AppLog.d(TAG, "startVideoCaptureButtomChangeTimer videoCaptureButtomChangeTimer=" + videoCaptureButtomChangeTimer);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (videoCaptureButtomChangeFlag) {
                    videoCaptureButtomChangeFlag = false;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
                            }
                        }
                    });

                } else {
                    videoCaptureButtomChangeFlag = true;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_off);
                            }
                        }
                    });
                }
            }
        };

        videoCaptureButtomChangeTimer = new Timer(true);
        videoCaptureButtomChangeTimer.schedule(task, 0, 1000);
    }

    public void initPreview() {
        AppLog.i(TAG, "initPreview curMode=" + curMode);
        previewView.setMaxZoomRate(cameraProperties.getMaxZoomRatio());
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
        if (cameraState.isMovieRecording()) {
            AppLog.i(TAG, "camera is recording...");
            //JIRA ICOM-3537 Start Modify by b.jiang 20160726
//            if (changeCameraMode(PreviewMode.APP_STATE_VIDEO_CAPTURE,
//                    ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE) == Tristate.FALSE) {
//                return;
//            }
            changeCameraMode(PreviewMode.APP_STATE_VIDEO_CAPTURE, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            //JIRA ICOM-3537 End Modify by b.jiang 20160726
            //start recording buttom,need a period timer
            //curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
            //startVideoCaptureButtomChangeTimer();
            //start recording time timer
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());

        } else if (cameraState.isTimeLapseVideoOn()) {
            AppLog.i(TAG, "camera is TimeLapseVideoOn...");
            currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
//            if (changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE,
//                    ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE) == Tristate.FALSE) {
//                return;
//            }
            GlobalInfo.getInstance().getCurrentCamera().resetTimeLapseVideoSize();
            changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
            //default set
            //start recording buttom,need a period timer
            //startVideoCaptureButtomChangeTimer();
            //start recording time timer
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());

        } else if (cameraState.isTimeLapseStillOn()) {
            AppLog.i(TAG, "camera is TimeLapseStillOn...");
            currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            GlobalInfo.getInstance().getCurrentCamera().resetTimeLapseVideoSize();
            changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE, ICatchPreviewMode.ICH_TIMELAPSE_STILL_PREVIEW_MODE);
            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE;
            //startVideoCaptureButtomChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());

        } else if (curMode == PreviewMode.APP_STATE_NONE_MODE) {
            if (cameraProperties.cameraModeSupport(ICatchMode.ICH_MODE_VIDEO)) {
                previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
                changeCameraMode(PreviewMode.APP_STATE_VIDEO_PREVIEW, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            } else {
                changeCameraMode(PreviewMode.APP_STATE_STILL_PREVIEW, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            }
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW");
            previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            changeCameraMode(curMode, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            // normal state, app show preview
        } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
            currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
            previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
            changeCameraMode(curMode, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
            // normal state, app show preview
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_STILL");
            previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_TIMELAPSE_STILL_PREVIEW_MODE);
            currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            changeCameraMode(curMode, ICatchPreviewMode.ICH_TIMELAPSE_STILL_PREVIEW_MODE);
            // normal state, app show preview
        } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == ICH_STILL_PREVIEW_MODE");
            changeCameraMode(curMode, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
        }
        //do not start preview,
    }

    public void stopVideoCaptureButtomChangeTimer() {
        AppLog.d(TAG, "stopVideoCaptureButtomChangeTimer videoCaptureButtomChangeTimer=" + videoCaptureButtomChangeTimer);
        if (videoCaptureButtomChangeTimer != null) {
            videoCaptureButtomChangeTimer.cancel();
        }
        previewView.setCaptureBtnBackgroundResource(R.drawable.video_recording_btn_on);
    }

    private void startRecordingLapseTimeTimer(int startTime) {
        if (cameraProperties.hasFuction(PropertyId.VIDEO_RECORDING_TIME) == false) {
            return;
        }
        AppLog.i(TAG, "startRecordingLapseTimeTimer curMode=" + curMode);
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE
                || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppLog.i(TAG, "startRecordingLapseTimeTimer");
            if (recordingLapseTimeTimer != null) {
                recordingLapseTimeTimer.cancel();
            }

            lapseTime = startTime;
            recordingLapseTimeTimer = new Timer(true);
            previewView.setRecordingTimeVisibility(View.VISIBLE);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            previewView.setRecordingTime(ConvertTools.secondsToHours(lapseTime++));
                        }
                    });
                }
            };
            recordingLapseTimeTimer.schedule(timerTask, 0, 1000);
        }
    }

    private void stopRecordingLapseTimeTimer() {
        if (recordingLapseTimeTimer != null) {
            recordingLapseTimeTimer.cancel();
        }
        previewView.setRecordingTime("00:00:00");
        previewView.setRecordingTimeVisibility(View.GONE);
    }

    public void changePreviewMode(int previewMode) {
        AppLog.d(TAG, "changePreviewMode previewMode=" + previewMode);
        AppLog.d(TAG, "changePreviewMode curMode=" + curMode);
        long timeInterval = System.currentTimeMillis() - lastCilckTime;
        AppLog.d(TAG, "repeat click: timeInterval=" + timeInterval);
        if (System.currentTimeMillis() - lastCilckTime < 2000) {
            AppLog.d(TAG, "repeat click: timeInterval < 2000");
            return;
        } else {
            lastCilckTime = System.currentTimeMillis();
        }
        if (modeSwitchBeep != null) {
            modeSwitchBeep.start();
        }
        previewView.dismissPopupWindow();
        if (previewMode == PreviewMode.APP_STATE_VIDEO_MODE) {/*
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                    MyToast.show(activity, R.string.stream_error_capturing);
                } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                    MyToast.show(activity, R.string.stream_error_recording);
                }
                return;
            } else if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                //createUIByMode(PreviewMode.APP_STATE_VIDEO_PREVIEW);
                previewView.stopMPreview(currentCamera);
                stopMediaStream();
                GlobalInfo.getInstance().getCurrentCamera().resetVideoSize();
                previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);*/
                if(curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    changeCameraMode(PreviewMode.APP_STATE_VIDEO_PREVIEW);
                }
            //}

        } else if (previewMode == PreviewMode.APP_STATE_STILL_MODE) {
            /*if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                    MyToast.show(activity, R.string.stream_error_capturing);
                } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                    MyToast.show(activity, R.string.stream_error_recording);
                }
                return;
            } else */if (true)/*(curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                    curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW)*/ {
                //previewView.stopMPreview(currentCamera);
                //stopMediaStream();
                //previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_STILL_PREVIEW_MODE);
                if(curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW||curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                    changeCameraMode(PreviewMode.APP_STATE_STILL_PREVIEW);
                }
            }
        }
    }

    private void startPhotoCapture() {
        previewView.setCaptureBtnEnability(false);
        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn_off);

        if (stillCaptureStartBeep != null) {
            stillCaptureStartBeep.start();
         }

        curMode = PreviewMode.APP_STATE_STILL_CAPTURE;
        if (cameraProperties.isSDCardExist() == true) {
            CameraProperties.getInstance().setPropertyValue(PropertyId.USB_PIMA_DCP_PIV_TRIGGER, 1);
        }
        /*
        int remainImageNum = cameraProperties.getRemainImageNum();
        if (remainImageNum == 0) {

        } else if (remainImageNum < 0) {

        }*/



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
                curMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                previewView.setCaptureBtnEnability(true);
            }
        }, 500);



    }

    public boolean destroyCamera() {
        return currentCamera.destroyCamera();
    }

    public void unregisterWifiSSReceiver() {
        if (wifiSSReceiver != null) {
            activity.unregisterReceiver(wifiSSReceiver);
        }
    }

    public void zoomIn() {/*
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        ZoomInOut.getInstance().zoomIn();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());*/
    }

    public void zoomOut() {/*
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        ZoomInOut.getInstance().zoomOut();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());*/
    }

    public void zoomBySeekBar() {/*
        ZoomInOut.getInstance().startZoomInOutThread(this);
        ZoomInOut.getInstance().addZoomCompletedListener(new ZoomInOut.ZoomCompletedListener() {
            @Override
            public void onCompleted(final int currentZoomRate) {
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        previewView.updateZoomViewProgress(currentZoomRate);
                    }
                });
            }
        });
        MyProgressDialog.showProgressDialog(activity, null);*/
    }

    public void showZoomView() {
        /*if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE
                || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE
                || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE
                || (cameraProperties.hasFuction(ICatchCameraProperty.ICH_CAP_DATE_STAMP) == true &&
                ICatchDateStamp.ICH_DATE_STAMP_OFF != cameraProperties.getCurrentDateStamp())) {
            return;
        }*/
        //previewView.showZoomView();
    }

    public int getMaxZoomRate() {
        return previewView.getZoomViewMaxZoomRate();
    }

    public int getZoomViewProgress() {
        AppLog.d(TAG, "getZoomViewProgress value=" + previewView.getZoomViewProgress());
        return previewView.getZoomViewProgress();
    }

    public void showSettingDialog(int position) {
        if (settingMenuList != null && settingMenuList.size() > 0) {
            OptionSetting.getInstance().addSettingCompleteListener(new OnSettingCompleteListener() {
                @Override
                public void onOptionSettingComplete() {
                    settingMenuList = UIDisplaySource.getinstance().getList(currentSettingMenuMode, currentCamera);
                    settingListAdapter.notifyDataSetChanged();
                }

                @Override
                public void settingVideoSizeComplete() {
                    AppLog.d(TAG, "settingVideoSizeComplete curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                        previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
                    } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
                    }
                    cameraProperties.refreshSupportedProperties();
                }

                @Override
                public void settingTimeLapseModeComplete(int timeLapseMode) {
                    if (timeLapseMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
                        boolean ret = previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_TIMELAPSE_STILL_PREVIEW_MODE);
                        if (ret) {
                            curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
                        }
                    } else if (timeLapseMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
                        boolean ret = previewStream.changePreviewMode(cameraPreviewStreamClint, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
                        if (ret) {
                            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                        }
                    }
                }
            });

            OptionSetting.getInstance().showSettingDialog(settingMenuList.get(position).name, activity);
        }
    }

    public void showDevicePopupMenu(View view){
        AppLog.d(TAG, "showDevicePopupmenu");

        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            //IC-754 Begin modify 20161212 BY b.jiang
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_recording);
            }
            //IC-754 End modify 20161212 BY b.jiang
            return;
        }

        previewView.showPopupMenu(view);
    }

    public void showPvModePopupWindow() {
        AppLog.d(TAG, "showPvModePopupWindow curMode=" + curMode);


        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            //IC-754 Begin modify 20161212 BY b.jiang
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_recording);
            }
            //IC-754 End modify 20161212 BY b.jiang
            return;
        }
        previewView.showPopupWindow(curMode);
        if (cameraProperties.cameraModeSupport(ICatchMode.ICH_MODE_VIDEO)) {
            previewView.setVideoRadioBtnVisibility(View.VISIBLE);
        }
        if (cameraProperties.cameraModeSupport(ICatchMode.ICH_MODE_TIMELAPSE)) {
            previewView.setTimepLapseRadioBtnVisibility(View.VISIBLE);
        }
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureRadioBtnChecked(true);
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setVideoRadioBtnChecked(true);
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            previewView.setTimepLapseRadioChecked(true);
        }
    }

    public void refresh() {
        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
                initPreview();
                addEvent();
            }
        });
    }

    public void stopStream() {
        AppLog.i(TAG, "start stopStream");
        stopMediaStream();
        delEvent();
        previewView.stopMPreview(currentCamera);
        destroyCamera();
    }

    public void stopConnectCheck() {
        GlobalInfo.getInstance().stopConnectCheck();
    }

    public void resetState() {
        cameraProperties.resetCameraProperties();
    }

    public void setCameraSwitch() {
        if (!cameraProperties.hasFuction(PropertyId.CAMERA_SWITCH)) {
            MyToast.show(activity, " Do not support this function ! ");
            return;
        }

        int camera_switch_value = cameraProperties.getCurrentCameraSwitch();
        Boolean camera_switch_retval;
        if (camera_switch_value == CameraSwitch.CAMERA_FRONT) {
            camera_switch_retval = cameraProperties.setCameraSwitch(CameraSwitch.CAMERA_BACK);
            if (camera_switch_retval == false) {
                MyToast.show(activity, R.string.setting_title_camera_switch + " setting " + Integer.toString(CameraSwitch.CAMERA_BACK) + " return value = false");
            } else {
                MyToast.show(activity, R.string.setting_title_camera_switch + " setting " + Integer.toString(CameraSwitch.CAMERA_BACK) + " return value = true ");
            }
        } else {
            camera_switch_retval = cameraProperties.setCameraSwitch(CameraSwitch.CAMERA_FRONT);
            if (camera_switch_retval == false) {
                MyToast.show(activity, R.string.setting_title_camera_switch + " setting " + Integer.toString(CameraSwitch.CAMERA_FRONT) + " return value = false");
            } else {
                MyToast.show(activity, R.string.setting_title_camera_switch + " setting " + Integer.toString(CameraSwitch.CAMERA_FRONT) + " return value = true ");
            }
        }

    }

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Tristate ret = Tristate.FALSE;
            switch (msg.what) {
                case SDKEvent.EVENT_BATTERY_ELETRIC_CHANGED:
                    AppLog.i(TAG, "receive EVENT_BATTERY_ELETRIC_CHANGED power =" + msg.arg1);
                    //need to update battery eletric
                    int resid = ThumbnailOperation.getBatteryLevelIcon();
                    if (resid > 0) {
                        previewView.setBatteryIcon(resid);
                        if (resid == R.drawable.ic_battery_charging_green24dp) {
                            AppDialog.showLowBatteryWarning(activity);
                        }
                    }
                    break;
                case SDKEvent.EVENT_CONNECTION_FAILURE:
                    AppLog.i(TAG, "receive EVENT_CONNECTION_FAILURE");
//                    previewView.stopMPreview(currentCamera);
                    stopMediaStream();
                    delEvent();
//                    unregisterWifiSSReceiver();
                    previewView.stopMPreview(currentCamera);
                    destroyCamera();
                    break;

                case SDKEvent.EVENT_SD_CARD_FULL:
                    AppLog.i(TAG, "receive EVENT_SD_CARD_FULL");
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_SDCARD_FULL);  //IC-966
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_full);
                    break;
                case SDKEvent.EVENT_VIDEO_OFF://only receive if fw request to stopMPreview video recording
                    AppLog.i(TAG, "receive EVENT_VIDEO_OFF:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                            curMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        } else {
                            curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                        }
                        stopRecordingLapseTimeTimer();
                        String info = currentCamera.getVideoSize().getCurrentUiStringInPreview() + "/" + ConvertTools.secondsToMinuteOrHours(cameraProperties
                                .getRecordingRemainTime());
                        previewView.setPreviewInfo(info);
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_ON:
                    /*
                    AppLog.i(TAG, "receive EVENT_VIDEO_ON:curMode =" + curMode);
                    // video from camera when file exceeds 4g
                    if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        curMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                        startVideoCaptureButtomChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                        curMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
                        startVideoCaptureButtomChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    }*/
                    break;
                case SDKEvent.EVENT_CAPTURE_START:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_START:curMode=" + curMode);
                    if (curMode != PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        return;
                    }
                    if (continuousCaptureBeep != null) {
                        continuousCaptureBeep.start();
                    }
                    MyToast.show(activity, R.string.capture_start);
                    break;
                case SDKEvent.EVENT_CAPTURE_COMPLETED:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_COMPLETED:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
                        //ret = changeCameraMode(PreviewMode.APP_STATE_STILL_MODE, ICatchPreviewMode.ICH_STILL_PREVIEW_MODE);
                        if (!cameraProperties.hasFuction(0xd704)) {
                            ret = startMediaStream(ICatchPreviewMode.ICH_STILL_PREVIEW_MODE);
                            if (ret == Tristate.FALSE) {
                                return;
                            }
                            if (ret == Tristate.NORMAL) {
                                currentCamera.isStreaming = true;
                                previewView.startMPreview(currentCamera);
                            }
                        }

                        previewView.setCaptureBtnEnability(true);
                        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
                        String info = currentCamera.getImageSize().getCurrentUiStringInPreview() + "/" + cameraProperties.getRemainImageNum();
                        previewView.setPreviewInfo(info);
                        curMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                        return;
                    }
                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        previewView.setCaptureBtnEnability(true);
                        previewView.setCaptureBtnBackgroundResource(R.drawable.still_capture_btn);
                        String info = currentCamera.getImageSize().getCurrentUiStringInPreview() + "/" + cameraProperties.getRemainImageNum();
                        previewView.setPreviewInfo(info);
                        MyToast.show(activity, R.string.capture_completed);
                    }

                    break;
                case SDKEvent.EVENT_FILE_ADDED:
                    AppLog.i(TAG, "EVENT_FILE_ADDED");
//                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
//                        lapseTime = 0;
//                    }
                    break;

                case SDKEvent.EVENT_TIME_LAPSE_STOP:
                    AppLog.i(TAG, "receive EVENT_TIME_LAPSE_STOP:curMode=" + curMode);
                    if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        //BSP-1419 收到 Event 時，就表示FW 已經自己停止了，APP 不需要再去執行 stopTimeLapse
//                        if (cameraAction.stopTimeLapse()) {
                        stopVideoCaptureButtomChangeTimer();
                        stopRecordingLapseTimeTimer();
//                        String info = currentCamera.getImageSize().getCurrentUiStringInPreview() + "/" + cameraProperties.getRemainImageNum();
////                        previewView.setPreviewInfo(info);
                        //Fixed BSP-2852 20180823 b.jiang
                        String info = currentCamera.getVideoSize().getCurrentUiStringInPreview() + "/" + ConvertTools.secondsToMinuteOrHours(cameraProperties
                                .getRecordingRemainTime());
                        previewView.setPreviewInfo(info);
                        curMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                    } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        //BSP-1419 收到 Event 時，就表示FW 已經自己停止了，APP 不需要再去執行 stopTimeLapse
//                        if (cameraAction.stopTimeLapse()) {
                        stopRecordingLapseTimeTimer();
                        String info = currentCamera.getImageSize().getCurrentUiStringInPreview() + "/" + cameraProperties.getRemainImageNum();
                        previewView.setPreviewInfo(info);
                        curMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
//                        }
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_RECORDING_TIME:
                    AppLog.i(TAG, "receive EVENT_VIDEO_RECORDING_TIME");
                    startRecordingLapseTimeTimer(0);
                    break;
                case SDKEvent.EVENT_FILE_DOWNLOAD:
                    AppLog.i(TAG, "receive EVENT_FILE_DOWNLOAD");
                    AppLog.d(TAG, "receive EVENT_FILE_DOWNLOAD  msg.arg1 =" + msg.arg1);
                    if (AppInfo.autoDownloadAllow == false) {
                        AppLog.d(TAG, "GlobalInfo.autoDownload == false");
                        return;
                    }
                    final String path = StorageUtil.getDownloadPath(activity);
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        path = Environment.getExternalStorageDirectory().toString() + AppInfo.AUTO_DOWNLOAD_PATH;
//                    } else {
//                        return;
//                    }
                    File directory = new File(path);

                    if (FileTools.getFileSize(directory) / 1024 >= AppInfo.autoDownloadSizeLimit * 1024 * 1024) {
                        AppLog.d(TAG, "can not download because size limit");
                        return;
                    }
                    final ICatchFile file = (ICatchFile) msg.obj;
                    FileOper.createDirectory(path);
                    new Thread() {
                        @Override
                        public void run() {
                            AppLog.d(TAG, "receive downloadFile file =" + file);
                            AppLog.d(TAG, "receive downloadFile path =" + path);
                            boolean retvalue = fileOperation.downloadFile(file, path + file.getFileName());
                            if (retvalue == true) {
                                previewHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String path1 = path + file.getFileName();
                                        Bitmap bitmap = BitmapTools.getImageByPath(path1, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
                                        previewView.setAutoDownloadBitmap(bitmap);
                                    }
                                });
                            }
                            AppLog.d(TAG, "receive downloadFile retvalue =" + retvalue);
                        }
                    }.start();
                    break;
                case AppMessage.SETTING_OPTION_AUTO_DOWNLOAD:
                    AppLog.d(TAG, "receive SETTING_OPTION_AUTO_DOWNLOAD");
                    Boolean switcher = (Boolean) msg.obj;
                    if (switcher == true) {
                        // AutoDownLoad
                        AppInfo.autoDownloadAllow = true;
                        previewView.setAutoDownloadVisibility(View.VISIBLE);
                    } else {
                        AppInfo.autoDownloadAllow = false;
                        previewView.setAutoDownloadVisibility(View.GONE);
                    }
                    break;
                case SDKEvent.EVENT_SDCARD_INSERT:
                    AppLog.i(TAG, "receive EVENT_SDCARD_INSERT");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_inserted);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void addEvent() {
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_SDCARD_FULL);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_OFF);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_ON);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_CAPTURE_START);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FILE_ADDED);
//        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED);
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_TIMELAPSE_STOP);
        sdkEvent.addCustomizeEvent(0x5001);// video recording event
        sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FILE_DOWNLOAD);
        sdkEvent.addCustomizeEvent(0x3701);// Insert SD card event
    }

    public void delEvent() {
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_SDCARD_FULL);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_CAPTURE_START);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_OFF);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FILE_ADDED);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_ON);
//        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_TIMELAPSE_STOP);
        sdkEvent.delCustomizeEventListener(0x5001);
        sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FILE_DOWNLOAD);
        sdkEvent.delCustomizeEventListener(0x3701);// Insert SD card event
    }

    public void stopPreview() {
        previewView.stopMPreview(currentCamera);
    }

    public void loadSettingMenuList() {
        AppLog.i(TAG, "setupBtn is clicked:allowClickButtoms=" + allowClickButtoms);

        if (allowClickButtoms == false) {
            return;
        }
        allowClickButtoms = false;
        /*if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_recording, Toast.LENGTH_SHORT);
            return;
        } else if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_capturing, Toast.LENGTH_SHORT);
            return;
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_recording, Toast.LENGTH_SHORT);
            return;
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppToast.show(activity, R.string.stream_error_capturing, Toast.LENGTH_SHORT);
            return;
        } else
        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW ) {
            currentSettingMenuMode = UIDisplaySource.CAPTURE_SETTING_MENU;
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            currentSettingMenuMode = UIDisplaySource.VIDEO_SETTING_MENU;
        } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW || curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            currentSettingMenuMode = UIDisplaySource.TIMELAPSE_SETTING_MENU;
        }
        if (settingMenuList != null) {
            settingMenuList.clear();
        }
        previewView.setSetupMainMenuVisibility(View.VISIBLE);
        previewView.setSettingBtnVisible(false);
        previewView.setBackBtnVisibility(true);
        //t previewView.setActionBarTitle(R.string.title_setting);
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopPreview();
                        stopMediaStream();
                        settingMenuList = UIDisplaySource.getinstance().getList(currentSettingMenuMode, currentCamera);
                        settingListAdapter = new SettingListAdapter(activity, settingMenuList, previewHandler, new SettingListAdapter.OnItemClickListener
                                () {
                            @Override
                            public void onItemClick(int position) {
                                showSettingDialog(position);
                            }
                        });
                        previewView.setSettingMenuListAdapter(settingListAdapter);
                        MyProgressDialog.closeProgressDialog();
                    }
                });
            }
        }).start();
        allowClickButtoms = true;*/
    }

    @Override
    public void finishActivity() {
        if (previewView.getSetupMainMenuVisibility() == View.VISIBLE) {
            AppLog.i(TAG, "onKeyDown curMode==" + curMode);
            previewView.setSetupMainMenuVisibility(View.GONE);
            previewView.setSettingBtnVisible(true);
            previewView.setBackBtnVisibility(false);
            //t previewView.setActionBarTitle(R.string.title_preview);
            if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_VIDEO_PREVIEW");
                changeCameraMode(curMode, ICatchPreviewMode.ICH_VIDEO_PREVIEW_MODE);
            } else if (curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
                currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
                changeCameraMode(curMode, ICatchPreviewMode.ICH_TIMELAPSE_VIDEO_PREVIEW_MODE);
            } else if (curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
                AppLog.i(TAG, "onKeyDown curMode == APP_STATE_TIMELAPSE_PREVIEW_STILL");
                currentCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
                changeCameraMode(curMode, ICatchPreviewMode.ICH_TIMELAPSE_STILL_PREVIEW_MODE);
            } else {
                changeCameraMode(curMode, ICatchPreviewMode.ICH_STILL_PREVIEW_MODE);
//                if (supportStreaming) {
//                    previewView.startMPreview(currentCamera);
//                }
//                createUIByMode(curMode);
            }
           /* AppLog.i(TAG, "finishActivity start showDialogWarn: Only for iCatch SBC");
            AppDialog.showDialogWarn(activity, R.string.text_preview_hint_info);*/
        } else {
            super.finishActivity();
            //System.exit(0);
        }
    }

    @Override
    public void redirectToAnotherActivity(final Context context, final Class<?> cls) {
        AppLog.i(TAG, "pbBtn is clicked curMode=" + curMode);
        if (allowClickButtoms == false) {
            AppLog.i(TAG, "do not allow to response button clicking");
            return;
        }
        allowClickButtoms = false;
        if (cameraProperties.isSDCardExist() == false) {
            AppDialog.showDialogWarn(activity, R.string.dialog_card_lose);
            allowClickButtoms = true;
            return;
        }
        AppLog.i(TAG, "curMode =" + curMode);

        if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            if (supportStreaming) {
                if (stopMediaStream() == false) {
                    AppLog.i("[Error] -- Main: ", "failed to stopMediaStream");
                    allowClickButtoms = true;
                    return;
                }
            }
            stopPreview();
            delEvent();
            allowClickButtoms = true;
            needShowSBCHint = true;

            //BSP-1209
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            previewHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    Intent intent = new Intent();
                    AppLog.i(TAG, "intent:start PbMainActivity.class");
                    intent.setClass(context, cls);
                    context.startActivity(intent);
                    AppLog.i(TAG, "intent:end start PbMainActivity.class");
                }
            }, 500);
//            //BSP-1209

            //ICOM-3812
//            Intent intent = new Intent();
//            AppLog.i(TAG, "intent:start PbMainActivity.class");
//            intent.setClass(context, cls);
//            context.startActivity(intent);
//            AppLog.i(TAG, "intent:end start PbMainActivity.class");
            return;
        } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_recording);
        } else if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            MyToast.show(activity, R.string.stream_error_capturing);
        }
        allowClickButtoms = true;
        AppLog.i(TAG, "end processing for responsing pbBtn clicking");
    }

    private class WifiSSReceiver extends BroadcastReceiver {
        private WifiManager wifi;

        public WifiSSReceiver() {
            super();

            wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            changeWifiStatusIcon();
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            changeWifiStatusIcon();
        }

        private void changeWifiStatusIcon() {
            WifiInfo info = wifi.getConnectionInfo();
            String ssid = info.getSSID().replaceAll("\"", "");
            if (info.getBSSID() != null && ssid != null && ssid.equals(GlobalInfo.getInstance().getSsid())) {
                int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);

                AppLog.d(TAG, "change Wifi Status：" + strength);
                switch (strength) {
                    case 0:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_0_bar_green_24dp);
                        break;
                    case 1:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_1_bar_green_24dp);
                        break;
                    case 2:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_2_bar_green_24dp);
                        break;
                    case 3:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_3_bar_green_24dp);
                        break;
                    case 4:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_4_bar_green_24dp);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    public void startOrStopYouTubeLive() {
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            MyToast.show(activity, R.string.capturing_cannot_live);
            return;
        }
        if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            MyToast.show(activity, R.string.recording_cannot_live);
            return;
        }
        if (!isYouTubeLiving) {
            final String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
            final String fileName = AppInfo.FILE_GOOGLE_TOKEN;
            final GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
            AppLog.d(TAG, "refreshAccessToken googleToken=" + googleToken);

//            final GoogleToken googleToken = null;
            if (googleToken != null && googleToken.getRefreshToken() != null && googleToken.getRefreshToken() != "") {
                final String refreshToken = googleToken.getRefreshToken();
                MyToast.show(activity, "readSerializable RefreshToken=" + refreshToken);
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accessToken = null;
                        try {
                            accessToken = GoogleAuthTool.refreshAccessToken(activity, refreshToken);
                        } catch (IOException e) {
                            previewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, "refreshAccessToken IOException");
                                }
                            });
                            e.printStackTrace();
                        }
                        if (accessToken != null) {
                            AppLog.d(TAG, "refreshAccessToken accessToken=" + accessToken);
                            googleToken.setCurrentAccessToken(accessToken);
                            FileTools.saveSerializable(directoryPath + fileName, googleToken);
                            previewHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, "start live");
                                    startYoutubeLive();
                                }
                            }, 1000);
                        } else {
                            previewHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    MyToast.show(activity, "Failed to get accessToken , Please enter the google account click disconnect and re-login!");
                                }
                            });
                        }
                    }
                }).start();

            } else {
                MyToast.show(activity, "You are not logged in, please login to google account!");
            }

        } else {
            AppLog.d(TAG, "stop push publish...");
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            stopYoutubeLive();
        }
    }

    public void startYoutubeLive() {
        String directoryPath = activity.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String fileName = AppInfo.FILE_GOOGLE_TOKEN;
        GoogleToken googleToken = (GoogleToken) FileTools.readSerializable(directoryPath + fileName);
        String accessToken = googleToken.getAccessToken();
        String refreshToken = googleToken.getRefreshToken();
//        String accessToken  = AppInfo.accessToken;
//        String refreshToken = AppInfo.refreshToken;
        final GoogleClientSecrets clientSecrets = YoutubeCredential.readClientSecrets(activity);
        AppLog.d(TAG, "readSerializable accessToken=" + accessToken);
        AppLog.d(TAG, "readSerializable refreshToken=" + refreshToken);
        if (accessToken == null) {
            MyToast.show(activity, "Failed to Youtube live,OAuth2AccessToken is null!");
            return;
        }
        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            }
        });
        final Credential credential;
        try {
            credential = YoutubeCredential.authorize(clientSecrets, accessToken, refreshToken);
        } catch (IOException e) {
            AppLog.d(TAG, "authorize IOException");
            e.printStackTrace();
            return;
        }
        AppLog.d(TAG, "success credential=" + credential);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String pushUrl = CreateBroadcast.createLive(activity, credential);
                AppLog.d(TAG, "push url..." + pushUrl);
                if (pushUrl == null) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to Youtube live,pushUrl is null!");
                        }
                    });
                    return;
                }
                final boolean ret = previewStream.startPublishStreaming(cameraPreviewStreamClint, pushUrl);
                if (ret == false) {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to start publish streaming!");
                        }
                    });
                    return;
                }
                final String shareUrl = CreateBroadcast.startLive();
                AppLog.d(TAG, "shareUrl =" + shareUrl);
                if (shareUrl == null) {
                    previewStream.stopPublishStreaming(cameraPreviewStreamClint);
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                            MyToast.show(activity, "Failed to YouTube live,shareUrl is null!");
                        }
                    });
                    return;
                } else {
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            isYouTubeLiving = true;
                            previewView.setYouTubeBtnTxv(R.string.end_youtube_live);
                            showSharedUrlDialog(activity, shareUrl);
                        }
                    });
                }
            }
        }).start();
    }

    private void stopYoutubeLive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean ret = previewStream.stopPublishStreaming(cameraPreviewStreamClint);
                try {
                    CreateBroadcast.stopLive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        if (ret == false) {
                            MyToast.show(activity, "Failed to stop living publish!");
                        } else {
                            MyToast.show(activity, "Succed to stop living publish!");
                        }
                        isYouTubeLiving = false;
                        previewView.setYouTubeBtnTxv(R.string.start_youtube_live);
                    }
                });
            }
        }).start();
    }

    public void gotoGoogleAccountManagement() {
        if (isYouTubeLiving) {
            MyToast.show(activity, R.string.stop_live_hint);
        } else {
            if (curMode == PreviewMode.APP_STATE_STILL_PREVIEW || curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || curMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW
                    || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
                stopPreview();
                if (supportStreaming) {
                    if (stopMediaStream() == false) {
                        AppLog.i("[Error] -- Main: ", "failed to stopMediaStream");
                        return;
                    }
                }

                delEvent();
                Intent intent = new Intent();
                intent.setClass(activity, LoginGoogleActivity.class);
                activity.startActivity(intent);
                AppLog.i(TAG, "intent:end start PbMainActivity.class");
                return;
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_recording);
            } else if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE || curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
            }
        }
    }

    public void showSharedUrlDialog(final Context context, final String shareUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.live_shared_url, null);
        final EditText resetTxv = (EditText) view.findViewById(R.id.shared_url);
        final ImageView qrcodeImage = (ImageView) view.findViewById(R.id.shared_url_qrcode);
        Bitmap bitmap = QRCode.createQRCodeWithLogo(shareUrl, QRCode.WIDTH, BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_wificamera));
        qrcodeImage.setImageBitmap(bitmap);

        resetTxv.setText(shareUrl);
        builder.setTitle("Success, share url is:");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(view);
        builder.setCancelable(false);
        builder.create().show();
    }



    public void change_IQ_password() {
        SharedPreferences mySharedPreferences= activity.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String IQ_pwd =mySharedPreferences.getString("IQ_password", "password");
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View v2 = inflater.inflate(R.layout.iq_change_password, null);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.change_password)
                .setView(v2)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which2) {
                        EditText old_password_text = (EditText) (v2.findViewById(R.id.old_password));
                        EditText new_password_text = (EditText) (v2.findViewById(R.id.new_password));
                        if (IQ_pwd.equals(old_password_text.getText().toString())) {
                            if("".equals(new_password_text.getText().toString())){
                                Toast.makeText(activity, R.string.new_password_null, Toast.LENGTH_SHORT).show();
                            }else {
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.putString("IQ_password", new_password_text.getText().toString());
                                editor.commit();
                                iq_isCheck_Password =false;
                                Toast.makeText(activity, R.string.change_password_sucess, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, R.string.iq_password_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                    }
                })
                .show();

    }

    public void startIQlayout(RelativeLayout pb_IQ,RelativeLayout buttom_bar){
        if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
            if (curMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_capturing);
            } else if (curMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                MyToast.show(activity, R.string.stream_error_recording);
            }
            return;
        }

        SharedPreferences mySharedPreferences= activity.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String IQ_pwd =mySharedPreferences.getString("IQ_password", "password");

        if(!iq_isCheck_Password) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            final View v = inflater.inflate(R.layout.iq_password_dialog, null);


            new AlertDialog.Builder(activity)
                    .setTitle(R.string.please_input_password)
                    .setView(v)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) (v.findViewById(R.id.password_text));
                            if (IQ_pwd.equals(editText.getText().toString())) {
                                pb_IQ.setVisibility(View.VISIBLE);
                                buttom_bar.setVisibility((View.GONE));
                                iq_isCheck_Password =true;
                            }else{
                                Toast.makeText(activity, R.string.iq_password_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //cancel
                        }
                    })
                    .setNeutralButton(R.string.change_password,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //change password
                            change_IQ_password();
                        }
                    })
                    .show();
        }else{
            pb_IQ.setVisibility(View.VISIBLE);
            buttom_bar.setVisibility((View.GONE));
        }
    }

    public void getIQstatus(){
        previewView.setProgressSave(0,currentCamera.getUSB_PIMA_DCP_IQ_BRIGHTNESS().getCurrentValue());
        previewView.setProgressSave(1,currentCamera.getUSB_PIMA_DCP_IQ_SATURATION().getCurrentValue());
        previewView.setProgressSave(2,currentCamera.getUSB_PIMA_DCP_IQ_HUE().getCurrentValue());
    }

    public void stopIQlayout(RelativeLayout pb_IQ,RelativeLayout buttom_bar,RelativeLayout quality_bar,RelativeLayout WB_change_IQ){
        pb_IQ.setVisibility(View.GONE);
        quality_bar.setVisibility(View.GONE);
        WB_change_IQ.setVisibility(View.GONE);
        buttom_bar.setVisibility(View.VISIBLE);
    }

    public void openSeekbar(RelativeLayout pb_IQ, RelativeLayout quality_bar, int v, TextView name){
        pb_IQ.setVisibility(View.GONE);
        quality_bar.setVisibility(View.VISIBLE);
        name.setText("none");
        switch (v) {
            case R.id.brightness_bt:
                name.setText(R.string.brightness);
                IqMode = IQMode.BRIGHTNESS;
                break;
            case R.id.hue_bt:
                name.setText(R.string.hue);
                IqMode = IQMode.HUE;
                break;
            case R.id.saturation_bt:
                name.setText(R.string.saturation);
                IqMode = IQMode.SATURATION;
                break;
            default:
                break;
        }


    }

    public void setIQvalue(int value){
        if(IqMode == IQMode.BRIGHTNESS){
            previewView.setProgressSave(0,value);
            currentCamera.getUSB_PIMA_DCP_IQ_BRIGHTNESS().setValue(value);
        }else if (IqMode == IQMode.SATURATION){
            previewView.setProgressSave(1,value);
            currentCamera.getUSB_PIMA_DCP_IQ_SATURATION().setValue(value);
        }else if (IqMode == IQMode.HUE){
            previewView.setProgressSave(2,value);
            currentCamera.getUSB_PIMA_DCP_IQ_HUE().setValue(value);

        }
    }

    public void resetIQ(){
        currentCamera.getUSB_PIMA_DCP_IQ_BRIGHTNESS().setValue(128);
        currentCamera.getUSB_PIMA_DCP_IQ_SATURATION().setValue(128);
        currentCamera.getUSB_PIMA_DCP_IQ_HUE().setValue(0);
        getIQstatus();
    }

    public void openWB_IQ(RelativeLayout pb_IQ, RelativeLayout WB_IQ){
        pb_IQ.setVisibility(View.GONE);
        WB_IQ.setVisibility(View.VISIBLE);
    }



    public void videoResize(){
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Ffmpeg.videoResize();
                MyProgressDialog.closeProgressDialog();
            }
        }, 300);
    }

    public void encodeVideo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String path = StorageUtil.getDownloadPath(activity);
                FileDES fileDES = null;
                try {
                    fileDES = new FileDES(FileDES.getPbKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileDES.encrypt(GlobalInfo.getInstance().getVideoName());
            }
        }, 500);
    }



}
