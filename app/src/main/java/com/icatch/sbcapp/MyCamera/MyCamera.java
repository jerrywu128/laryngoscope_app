package com.icatch.sbcapp.MyCamera;

import android.util.Log;

import com.icatch.sbcapp.BaseItems.PropertyTypeInteger;
import com.icatch.sbcapp.BaseItems.PropertyTypeString;
import com.icatch.sbcapp.BaseItems.StreamResolution;
import com.icatch.sbcapp.BaseItems.TimeLapseDuration;
import com.icatch.sbcapp.BaseItems.TimeLapseInterval;
import com.icatch.sbcapp.BaseItems.TimeLapseMode;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Hash.PropertyHashMapStatic;
import com.icatch.sbcapp.Hash.VideoSizeStaticHashMap;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Model.Implement.SDKSession;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.SdkApi.CameraAction;
import com.icatch.sbcapp.SdkApi.CameraFixedInfo;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.CameraState;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SdkApi.VideoPlayback;
import com.icatch.wificam.customer.ICatchWificamAssist;
import com.icatch.wificam.customer.ICatchWificamControl;
import com.icatch.wificam.customer.ICatchWificamInfo;
import com.icatch.wificam.customer.ICatchWificamPlayback;
import com.icatch.wificam.customer.ICatchWificamPreview;
import com.icatch.wificam.customer.ICatchWificamProperty;
import com.icatch.wificam.customer.ICatchWificamState;
import com.icatch.wificam.customer.ICatchWificamVideoPlayback;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.type.ICatchCameraProperty;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:43.
 */
public class MyCamera {

    private final String tag = "MyCamera";
    private boolean isSdCardReady = false;
    private SDKSession mSDKSession;

    private ICatchWificamPlayback photoPlayback;
    private ICatchWificamControl cameraAction;
    private ICatchWificamVideoPlayback videoPlayback;
    private ICatchWificamPreview previewStream;
    private ICatchWificamInfo cameraInfo;
    private ICatchWificamProperty cameraProperty;
    private ICatchWificamState cameraState;
    private ICatchWificamAssist cameraAssist;


    private PropertyTypeInteger whiteBalance;
    private PropertyTypeInteger burst;
    private PropertyTypeInteger electricityFrequency;
    private PropertyTypeInteger dateStamp;
    private PropertyTypeInteger slowMotion;
    private PropertyTypeInteger upside;
    private PropertyTypeInteger captureDelay;
    private PropertyTypeString videoSize;
    private PropertyTypeString imageSize;
    private PropertyTypeInteger cameraSwitch;

    private PropertyTypeInteger screenSaver;
    private PropertyTypeInteger autoPowerOff;
    private PropertyTypeInteger exposureCompensation;
    private PropertyTypeInteger videoFileLength;
    private PropertyTypeInteger fastMotionMovie;

    private StreamResolution streamResolution;
    private TimeLapseInterval timeLapseVideoInterval;
    private TimeLapseInterval timeLapseStillInterval;
    private TimeLapseDuration timeLapseDuration;
    private PropertyTypeInteger timeLapseMode;


    private PropertyTypeInteger USB_PIMA_DCP_IQ_BRIGHTNESS;
    private PropertyTypeInteger USB_PIMA_DCP_IQ_SATURATION;
    private PropertyTypeInteger USB_PIMA_DCP_IQ_HUE;
    private PropertyTypeInteger USB_PIMA_DCP_IQ_BLC;

    private PropertyTypeString systemInfo;


    public int timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
    public String cameraName;

    public String ipAddress;
    public int mode;
    public String inputPassword;
    public String uid;
    public boolean needInputPassword = true;
    public boolean isStreaming = false;
    public boolean isConnecting = false;


    public MyCamera(String ipAddress, String uid, String username, String password) {
        mSDKSession = new SDKSession(ipAddress, uid, username, password);
    }

    public MyCamera() {
        mSDKSession = new SDKSession();
    }

    public MyCamera(String cameraName) {
        mSDKSession = new SDKSession();
        this.cameraName = cameraName;
    }

    public MyCamera(String ipAddress, int mode, String uid) {
        mSDKSession = new SDKSession();
        this.ipAddress = ipAddress;
        this.mode = mode;
        this.uid = uid;
    }

    public Boolean initCamera() {
        boolean retValue = false;
        AppLog.i(tag, "Start initCamera");
        try {
            photoPlayback = mSDKSession.getSDKSession().getPlaybackClient();
            cameraAction = mSDKSession.getSDKSession().getControlClient();
            previewStream = mSDKSession.getSDKSession().getPreviewClient();
            videoPlayback = mSDKSession.getSDKSession().getVideoPlaybackClient();
            cameraProperty = mSDKSession.getSDKSession().getPropertyClient();
            cameraInfo = mSDKSession.getSDKSession().getInfoClient();
            cameraState = mSDKSession.getSDKSession().getStateClient();
            cameraAssist = ICatchWificamAssist.getInstance();
            retValue = true;
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        CameraAction.getInstance().initCameraAction();
        CameraFixedInfo.getInstance().initCameraFixedInfo();
        CameraProperties.getInstance().initCameraProperties();
        CameraState.getInstance().initCameraState();
        FileOperation.getInstance().initICatchWificamPlayback();
        VideoPlayback.getInstance().initVideoPlayback();
        PropertyHashMapStatic.getInstance().initPropertyHashMap();
        VideoSizeStaticHashMap.getInstance().initVideoSizeHashMap();
        initProperty();
        return retValue;
    }

    public Boolean initCameraForLocalPB() {
        boolean retValue = false;
        AppLog.i(tag, "Start initCamera");
        try {
            photoPlayback = mSDKSession.getSDKSession().getPlaybackClient();
            cameraAction = mSDKSession.getSDKSession().getControlClient();
            previewStream = mSDKSession.getSDKSession().getPreviewClient();
            videoPlayback = mSDKSession.getSDKSession().getVideoPlaybackClient();
            cameraProperty = mSDKSession.getSDKSession().getPropertyClient();
            cameraInfo = mSDKSession.getSDKSession().getInfoClient();
            cameraState = mSDKSession.getSDKSession().getStateClient();
            cameraAssist = ICatchWificamAssist.getInstance();
            retValue = true;
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        CameraAction.getInstance().initCameraAction();
        CameraFixedInfo.getInstance().initCameraFixedInfo();
        CameraProperties.getInstance().initCameraProperties();
        CameraState.getInstance().initCameraState();
        FileOperation.getInstance().initICatchWificamPlayback();
        VideoPlayback.getInstance().initVideoPlayback();
        PropertyHashMapStatic.getInstance().initPropertyHashMap();
        VideoSizeStaticHashMap.getInstance().initVideoSizeHashMap();
        return retValue;
    }

    public Boolean initCameraByClint() {
        boolean retValue = false;
        AppLog.i(tag, "Start initCamera");
        try {
            photoPlayback = mSDKSession.getSDKSession().getPlaybackClient();
            cameraAction = mSDKSession.getSDKSession().getControlClient();
            previewStream = mSDKSession.getSDKSession().getPreviewClient();
            videoPlayback = mSDKSession.getSDKSession().getVideoPlaybackClient();
            cameraProperty = mSDKSession.getSDKSession().getPropertyClient();
            cameraInfo = mSDKSession.getSDKSession().getInfoClient();
            cameraState = mSDKSession.getSDKSession().getStateClient();
            cameraAssist = ICatchWificamAssist.getInstance();
            retValue = true;
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    private void initProperty() {
        // TODO Auto-generated method stub
        AppLog.i(tag, "Start initProperty");
        whiteBalance = new PropertyTypeInteger(PropertyHashMapStatic.whiteBalanceMap, PropertyId.WHITE_BALANCE, GlobalInfo.getInstance().getAppContext());
        burst = new PropertyTypeInteger(PropertyHashMapStatic.burstMap, ICatchCameraProperty.ICH_CAP_BURST_NUMBER, GlobalInfo.getInstance().getAppContext());
        dateStamp = new PropertyTypeInteger(PropertyHashMapStatic.dateStampMap, PropertyId.DATE_STAMP, GlobalInfo.getInstance().getAppContext());
        slowMotion = new PropertyTypeInteger(PropertyHashMapStatic.slowMotionMap, PropertyId.SLOW_MOTION, GlobalInfo.getInstance().getAppContext());
        upside = new PropertyTypeInteger(PropertyHashMapStatic.upsideMap, PropertyId.UP_SIDE, GlobalInfo.getInstance().getAppContext());
        electricityFrequency = new PropertyTypeInteger(PropertyHashMapStatic.electricityFrequencyMap, PropertyId.LIGHT_FREQUENCY, GlobalInfo.getInstance()
                .getAppContext());

        cameraSwitch = new PropertyTypeInteger(PropertyHashMapStatic.cameraSwitchMap, PropertyId.CAMERA_SWITCH, GlobalInfo.getInstance().getAppContext());

        captureDelay = new PropertyTypeInteger(PropertyId.CAPTURE_DELAY, GlobalInfo.getInstance().getAppContext());
        videoSize = new PropertyTypeString(PropertyId.VIDEO_SIZE, GlobalInfo.getInstance().getAppContext());
        imageSize = new PropertyTypeString(PropertyId.IMAGE_SIZE, GlobalInfo.getInstance().getAppContext());

        screenSaver = new PropertyTypeInteger(PropertyId.SCREEN_SAVER, GlobalInfo.getInstance().getAppContext());
        autoPowerOff = new PropertyTypeInteger(PropertyId.AUTO_POWER_OFF, GlobalInfo.getInstance().getAppContext());
        exposureCompensation = new PropertyTypeInteger(PropertyId.EXPOSURE_COMPENSATION, GlobalInfo.getInstance().getAppContext());
        videoFileLength = new PropertyTypeInteger(PropertyId.VIDEO_FILE_LENGTH, GlobalInfo.getInstance().getAppContext());
        fastMotionMovie = new PropertyTypeInteger(PropertyId.FAST_MOTION_MOVIE, GlobalInfo.getInstance().getAppContext());

        streamResolution = new StreamResolution();
        timeLapseVideoInterval = new TimeLapseInterval();
        timeLapseStillInterval = new TimeLapseInterval();
        timeLapseDuration = new TimeLapseDuration();
        timeLapseMode = new PropertyTypeInteger(PropertyHashMapStatic.timeLapseMode, PropertyId.TIMELAPSE_MODE, GlobalInfo.getInstance().getAppContext());

        //20210913IQ
        USB_PIMA_DCP_IQ_BRIGHTNESS = new PropertyTypeInteger(PropertyId.USB_PIMA_DCP_IQ_BRIGHTNESS, GlobalInfo.getInstance().getAppContext());
        USB_PIMA_DCP_IQ_SATURATION = new PropertyTypeInteger(PropertyId.USB_PIMA_DCP_IQ_SATURATION, GlobalInfo.getInstance().getAppContext());
        USB_PIMA_DCP_IQ_BLC = new PropertyTypeInteger(PropertyId.USB_PIMA_DCP_IQ_BLC, GlobalInfo.getInstance().getAppContext());
        USB_PIMA_DCP_IQ_HUE = new PropertyTypeInteger(PropertyId.USB_PIMA_DCP_IQ_HUE, GlobalInfo.getInstance().getAppContext());

        //20211121
        systemInfo = new PropertyTypeString(PropertyId.USB_PIMA_DCP_SYSTEM_INFO, GlobalInfo.getInstance().getAppContext());


        AppLog.i(tag, "End initProperty");
    }

    public void setMyMode(int mode) {
        this.mode = mode;
    }

    public int getMyMode() {
        return mode;
    }

    public Boolean destroyCamera() {
        return mSDKSession.destroySession();
    }

    public SDKSession getSDKsession() {
        return mSDKSession;
    }

    public ICatchWificamPlayback getplaybackClient() {
        return photoPlayback;
    }

    public ICatchWificamControl getcameraActionClient() {
        return cameraAction;
    }

    public ICatchWificamVideoPlayback getVideoPlaybackClint() {
        return videoPlayback;
    }

    public ICatchWificamPreview getpreviewStreamClient() {
        return previewStream;
    }

    public ICatchWificamInfo getCameraInfoClint() {
        return cameraInfo;
    }

    public ICatchWificamProperty getCameraPropertyClint() {
        return cameraProperty;
    }

    public ICatchWificamState getCameraStateClint() {
        return cameraState;
    }

    public ICatchWificamAssist getCameraAssistClint() {
        return cameraAssist;
    }

    public PropertyTypeInteger getWhiteBalance() {
        return whiteBalance;
    }

    public PropertyTypeInteger getBurst() {
        return burst;
    }

    public PropertyTypeInteger getDateStamp() {
        return dateStamp;
    }

    public PropertyTypeInteger getCaptureDelay() {
        return captureDelay;
    }

    public PropertyTypeInteger getScreenSaver() {
        return screenSaver;
    }

    public PropertyTypeInteger getAutoPowerOff() {
        return autoPowerOff;
    }

    public PropertyTypeInteger getExposureCompensation() {
        return exposureCompensation;
    }

    public PropertyTypeInteger getVideoFileLength() {
        return videoFileLength;
    }

    public PropertyTypeInteger getFastMotionMovie() {
        return fastMotionMovie;
    }

    public PropertyTypeInteger getSlowMotion() {
        return slowMotion;
    }

    public PropertyTypeInteger getUpside() { return upside;  }

    public PropertyTypeInteger getCameraSwitch() { return cameraSwitch;  }

    public PropertyTypeString getVideoSize() {
        return videoSize;
    }

    public PropertyTypeString getImageSize() {
        return imageSize;
    }

    public PropertyTypeInteger getElectricityFrequency() {
        return electricityFrequency;
    }

    public StreamResolution getStreamResolution() {
        return streamResolution;
    }

    public TimeLapseInterval getTimeLapseVideoInterval() {
        return timeLapseVideoInterval;
    }

    public TimeLapseInterval getTimeLapseStillInterval() {
        return timeLapseStillInterval;
    }

    public TimeLapseDuration gettimeLapseDuration() {
        return timeLapseDuration;
    }

    public PropertyTypeInteger getTimeLapseMode() {
        return timeLapseMode;
    }

    public PropertyTypeInteger getUSB_PIMA_DCP_IQ_BRIGHTNESS(){return USB_PIMA_DCP_IQ_BRIGHTNESS;}

    public PropertyTypeInteger getUSB_PIMA_DCP_IQ_SATURATION(){return USB_PIMA_DCP_IQ_SATURATION;}

    public PropertyTypeInteger getUSB_PIMA_DCP_IQ_HUE(){return USB_PIMA_DCP_IQ_HUE;}

    public PropertyTypeInteger getUSB_PIMA_DCP_IQ_BLC(){return USB_PIMA_DCP_IQ_BLC;}

    //public PropertyTypeInteger getUSB_PIMA_DCP_PIV_TRIGGER(){return USB_PIMA_DCP_PIV_TRIGGER;}

    public boolean isSdCardReady() {
        return isSdCardReady;
    }

    public void setSdCardReady(boolean isSdReady) {
        isSdCardReady = isSdReady;
    }


    //JIRA ICOM-2246 Begin Add by b.jiang 2015-12-04
    /*public PropertyTypeString getTimeLapseVideoSize(){
		return timeLapseVideoSize;
	}*/
    public void resetVideoSize() {
        videoSize = new PropertyTypeString(PropertyId.VIDEO_SIZE, GlobalInfo.getInstance().getAppContext());
        List<String> videoSizeList = videoSize.getValueListUI();
        for (int i = 0; i < videoSizeList.size(); i++) {
            Log.d("TigerTiger", "resetVideoSize - videoSizeList[" + i + "] = " + videoSizeList.get(i));
        }
    }

    public void resetTimeLapseVideoSize() {
        Log.d("TigerTiger", "start resetTimeLapseVideoSize ");
        videoSize = new PropertyTypeString(PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK, GlobalInfo.getInstance().getAppContext());
        List<String> videoSizeList = videoSize.getValueListUI();
        for (int i = 0; i < videoSizeList.size(); i++) {
            Log.d("TigerTiger", "resetTimeLapseVideoSize - timeLapseVideoSizeList[" + i + "] = " + videoSizeList.get(i));
        }
    }
    //JIRA ICOM-2246 End Add by b.jiang 2015-12-04

}
