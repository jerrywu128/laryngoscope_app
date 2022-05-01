package com.honestmc.laryngoscopeapp.GlobalApp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import com.honestmc.laryngoscopeapp.AppDialog.AppDialog;
import com.honestmc.laryngoscopeapp.BaseItems.BluetoothAppDevice;
import com.honestmc.laryngoscopeapp.BaseItems.LocalPbItemInfo;
import com.honestmc.laryngoscopeapp.BaseItems.PhotoWallPreviewType;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.Tools.WifiCheck;
import com.honestmc.laryngoscopeapp.Tools.ConnectCheckTimer;
import com.honestmc.laryngoscopeapp.BaseItems.MultiPbItemInfo;
import com.honestmc.laryngoscopeapp.Listener.ScreenListener;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Model.Implement.SDKEvent;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.View.Activity.PreviewActivity;
import com.icatchtek.bluetooth.customer.client.ICatchBluetoothClient;

import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/13 10:55.
 */
public class GlobalInfo {
    private final String TAG = "GlobalInfo";
    private static GlobalInfo instance;
    private Activity activity;
    private MyCamera currentCamera;
    private List<MyCamera> cameraList;
    private SDKEvent sdkEvent;
    public List<MultiPbItemInfo> photoInfoList;
    public List<MultiPbItemInfo> videoInfoList;
    public List<LocalPbItemInfo> localPhotoList;
    public List<LocalPbItemInfo> localVideoList;
    public LruCache<Integer, Bitmap> mLruCache;

    private String ssid;
    private WifiCheck wifiCheck;
    ScreenListener listener;
    private Handler handler = new Handler();

    private String videoName;

    public static PhotoWallPreviewType photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
    public static int currentViewpagerPosition = 0;
    public static boolean enableSoftwareDecoder = false;
    public static boolean isBLE = false;
    public static ICatchBluetoothClient iCatchBluetoothClient;
    public static BluetoothAppDevice curBtDevice;
    public static boolean isReleaseBTClient = true;

    public static boolean needReconnect = true;
    public static double curFps = 30;
    public static int videoCacheNum = 0;
    public final static double THRESHOLD_TIME = 0.1; //s
    public static boolean isNeedGetBTClient = true;
    public static int curSlotId = 0;
    public static boolean isPrepareSession = false;
    public static int mWidth = 0;
    public static int mHeight = 0;

    public Handler getAppStartHandler() {
        return appStartHandler;
    }

    public void setAppStartHandler(Handler appStartHandler) {
        this.appStartHandler = appStartHandler;
    }

    private Handler appStartHandler;

    private GlobalInfo() {

    }

    public static GlobalInfo getInstance() {
        if (instance == null) {
            instance = new GlobalInfo();
        }
        return instance;
    }

    public void setSsid(String ssid) {
        AppLog.d(TAG, "setSsid = " + ssid);
        this.ssid = ssid;

    }

    public String getSsid() {
        return ssid;
    }

    public void setVideoName(String videoName){
        AppLog.d(TAG, "videoName = " + videoName);
        this.videoName=videoName;
    }

    public String getVideoName(){
        return videoName;
    }

    public Context getAppContext() {
        return (Context) activity;
    }

    public void setCurrentApp(Activity activity) {
        this.activity = activity;
    }

    public Activity getCurrentApp() {
        AppLog.d(TAG, "getCurrentApp activity=" + activity);
        return activity;
    }

    public void setCurrentCamera(MyCamera myCamera) {
        this.currentCamera = myCamera;
    }

    public MyCamera getCurrentCamera() {
        return currentCamera;
    }

    public List<MyCamera> getCameraList() {
        return cameraList;
    }

    public void startScreenListener() {
        listener = new ScreenListener(getCurrentApp());
        listener.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                AppLog.i(TAG, "onUserPresent");
            }

            @Override
            public void onScreenOn() {
                AppLog.i(TAG, "onScreenOn");

            }

            @Override
            public void onScreenOff() {
                AppLog.i(TAG, "onScreenOff,need to close app!");
                //ExitApp.getInstance().exitWhenScreenOff();
                ExitApp.getInstance().finishAllActivity();
            }
        });
    }

    public void endSceenListener() {
        if (listener != null) {
            listener.unregisterListener();
        }
    }

    private Handler globalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDKEvent.EVENT_CONNECTION_FAILURE:
                    //need to show dialog
                    AppLog.i(TAG, "receive EVENT_CONNECTION_FAILURE AppInfo.needReconnect=" + needReconnect);
                    //JIRA ICOM-3669 begin modify by b.jiang 20160914
                    GlobalInfo.isPrepareSession = false;
                    if (needReconnect) {
                        needReconnect = false;
                        wifiCheck = new WifiCheck(activity);
                        wifiCheck.showAutoReconnectProgressDialog(activity);
                    }
                    //JIRA ICOM-3669 end modify by b.jiang 20160914
//                    ExitApp.getInstance().finishAllActivity();
                    break;
                case SDKEvent.EVENT_SDCARD_REMOVED:
                    AppLog.i(TAG, "receive EVENT_SDCARD_REMOVED");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_removed);
                    break;

                case ConnectCheckTimer.MESSAGE_CONNECT_DISCONNECTED:
                    AppLog.i(TAG, "receive ConnectCheckTimer.MESSAGE_CONNECT_DISCONNECTED needReconnect=" + needReconnect);
                    GlobalInfo.isPrepareSession = false;
                    if (needReconnect) {
                        needReconnect = false;
                        wifiCheck = new WifiCheck(activity);
                        wifiCheck.showAutoReconnectProgressDialog(activity);
                    }
                    if (activity instanceof PreviewActivity) {
                        AppLog.d(TAG, "stopStream");
                        ((PreviewActivity) activity).stopStream();
                    }
                    break;
            }
        }
    };

    public void addEventListener(int eventId, boolean forAllSession) {
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(globalHandler);
        }
        sdkEvent.addGlobalEventListener(eventId, forAllSession);
    }

    public void delEventListener(int eventId, boolean forAllSession) {
        if (sdkEvent != null) {
            sdkEvent.delGlobalEventListener(eventId, forAllSession);
        }
    }

    public void startConnectCheck() {
        ConnectCheckTimer.startCheck(globalHandler);
    }

    public void stopConnectCheck() {
        ConnectCheckTimer.stopCheck();
    }

    public void showExceptionInfoDialog(final int messageID) {
        if (activity != null) {
            AppLog.d(TAG, "showExceptionInfoToast");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppDialog.showDialogWarn(activity, messageID);
                }
            });
        }
    }

    public void showExceptionInfoDialog(final String message) {
        if (activity != null) {
            AppLog.d(TAG, "showExceptionInfoToast");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppDialog.showDialogWarn(activity, message);
                }
            });
        }
    }

    public void setVideosize(int width,int height){
        mHeight = height;
        mWidth = width;
    }


    public int getVideoheight(){
        return mHeight;
    }


    public int getVideowidth(){
        return mWidth;
    }

}
