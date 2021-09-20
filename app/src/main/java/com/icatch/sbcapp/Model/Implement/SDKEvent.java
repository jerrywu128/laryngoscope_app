package com.icatch.sbcapp.Model.Implement;

import android.os.Handler;
import android.util.Log;

import com.icatch.wificam.customer.ICatchWificamListener;
import com.icatch.wificam.customer.type.ICatchEvent;
import com.icatch.wificam.customer.type.ICatchEventID;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.Beans.SearchedCameraInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.SdkApi.CameraAction;

/**
 * Created by zhang yanhu C001012 on 2015/11/23 18:00.
 */
public class SDKEvent {
    private static final String TAG = "SDKEvent";
    public static final int EVENT_BATTERY_ELETRIC_CHANGED = 0;
    public static final int EVENT_CAPTURE_COMPLETED = 1;
    public static final int EVENT_CAPTURE_START = 3;
    public static final int EVENT_SD_CARD_FULL = 4;
    public static final int EVENT_VIDEO_OFF = 5;
    public static final int EVENT_VIDEO_ON = 6;
    public static final int EVENT_FILE_ADDED = 7;
    public static final int EVENT_CONNECTION_FAILURE = 8;
    public static final int EVENT_TIME_LAPSE_STOP = 9;
    public static final int EVENT_SERVER_STREAM_ERROR = 10;
    public static final int EVENT_FILE_DOWNLOAD = 11;
    public static final int EVENT_VIDEO_RECORDING_TIME = 12;
    public static final int EVENT_FW_UPDATE_COMPLETED = 13;
    public static final int EVENT_FW_UPDATE_POWEROFF = 14;
    public static final int EVENT_SEARCHED_NEW_CAMERA = 15;
    public static final int EVENT_SDCARD_REMOVED = 16;
    public static final int EVENT_SDCARD_INSERT = 17;
    public static final int EVENT_FW_UPDATE_CHECK = 18;
    public static final int EVENT_FW_UPDATE_CHKSUMERR = 19;
    public static final int EVENT_FW_UPDATE_NG = 20;

    private CameraAction cameraAction = CameraAction.getInstance();
    private Handler handler;
    private SdcardStateListener sdcardStateListener;
    private BatteryStateListener batteryStateListener;
    private CaptureStartListener captureStartListener;
    private CaptureDoneListener captureDoneListener;
    private VideoOffListener videoOffListener;
    private FileAddedListener fileAddedListener;
    private VideoOnListener videoOnListener;
    private ConnectionFailureListener connectionFailureListener;
    private TimeLapseStopListener timeLapseStopListener;
    private ServerStreamErrorListener serverStreamErrorListener;
    private VideoRecordingTimeStartListener videoRecordingTimeStartListener;
    private FileDownloadListener fileDownloadListener;
    private UpdateFWCompletedListener updateFWCompletedListener;
    private UpdateFWPoweroffListener updateFWPoweroffListener;
    private NoSdcardListener noSdcardListener;
    private ScanCameraListener scanCameraListener;
    private InsertSdcardListener insertSdcardListener;
    // add
    private UpdateFWCheckListener updateFWCheckListener;
    private UpdateFWCHKSumErrListener updateFWCHKSumErrListener;
    private UpdateFWNGListener updateFWNGListener;

    public SDKEvent(Handler handler) {
        this.handler = handler;
    }

    public void addEventListener(int iCatchEventID) {
        // switch(iCatchEventID){
        if (iCatchEventID == ICatchEventID.ICH_EVENT_SDCARD_FULL) {
            sdcardStateListener = new SdcardStateListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_SDCARD_FULL, sdcardStateListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED) {
            batteryStateListener = new BatteryStateListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED, batteryStateListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CAPTURE_START) {
            captureStartListener = new CaptureStartListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_CAPTURE_START, captureStartListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE) {
            captureDoneListener = new CaptureDoneListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE, captureDoneListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_VIDEO_OFF) {
            videoOffListener = new VideoOffListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_OFF, videoOffListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FILE_ADDED) {
            fileAddedListener = new FileAddedListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FILE_ADDED, fileAddedListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_VIDEO_ON) {
            videoOnListener = new VideoOnListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_VIDEO_ON, videoOnListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_TIMELAPSE_STOP) {
            timeLapseStopListener = new TimeLapseStopListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_TIMELAPSE_STOP, timeLapseStopListener);
        }

        if (iCatchEventID == ICatchEventID.ICH_EVENT_SERVER_STREAM_ERROR) {
            serverStreamErrorListener = new ServerStreamErrorListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_SERVER_STREAM_ERROR, serverStreamErrorListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FILE_DOWNLOAD) {
            fileDownloadListener = new FileDownloadListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FILE_DOWNLOAD, fileDownloadListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED) {
            updateFWCompletedListener = new UpdateFWCompletedListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED, updateFWCompletedListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF) {
            updateFWPoweroffListener = new UpdateFWPoweroffListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF, updateFWPoweroffListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_SDCARD_REMOVED) {
            noSdcardListener = new NoSdcardListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, noSdcardListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED) {
            connectionFailureListener = new ConnectionFailureListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK) {
            updateFWCheckListener = new UpdateFWCheckListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK, updateFWCheckListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR) {
            updateFWCHKSumErrListener = new UpdateFWCHKSumErrListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR, updateFWCHKSumErrListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_NG) {
            updateFWNGListener = new UpdateFWNGListener();
            cameraAction.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG, updateFWNGListener);
        }
    }

    public void addGlobalEventListener(int iCatchEventID, Boolean forAllSession) {
        AppLog.i(TAG, "Start addGlobalEventListener  iCatchEventID=" + iCatchEventID);
        switch (iCatchEventID) {
            case ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD:
                scanCameraListener = new ScanCameraListener();
                CameraAction.addGlobalEventListener(ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, scanCameraListener, forAllSession);
                break;
            case ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED:
                connectionFailureListener = new ConnectionFailureListener();
                CameraAction.addGlobalEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener, forAllSession);
                break;
            case ICatchEventID.ICH_EVENT_SDCARD_REMOVED:
                noSdcardListener = new NoSdcardListener();
                CameraAction.addGlobalEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, noSdcardListener, forAllSession);
                break;
        }
        AppLog.i(TAG, "End addGlobalEventListener");
    }

    public void delGlobalEventListener(int iCatchEventID, Boolean forAllSession) {
        AppLog.i(TAG, "Start delGlobalEventListener iCatchEventID=" + iCatchEventID);
        switch (iCatchEventID) {
            case ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD:
                if (scanCameraListener != null) {
                    CameraAction.delGlobalEventListener(ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, scanCameraListener, forAllSession);
                }
                break;
            case ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED:
                if (connectionFailureListener != null) {
                    CameraAction.delGlobalEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener, forAllSession);
                }
                break;
            case ICatchEventID.ICH_EVENT_SDCARD_REMOVED:
                if (noSdcardListener != null) {
                    CameraAction.delGlobalEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, noSdcardListener, forAllSession);
                }
                break;
        }
        AppLog.i(TAG, "End delGlobalEventListener");
    }

    public void addCustomizeEvent(int eventID) {
        switch (eventID) {
            case 0x5001:
                videoRecordingTimeStartListener = new VideoRecordingTimeStartListener();
                cameraAction.addCustomEventListener(eventID, videoRecordingTimeStartListener);
                break;
            case 0x3701:
                insertSdcardListener = new InsertSdcardListener();
                cameraAction.addCustomEventListener(eventID, insertSdcardListener);
                break;
        }
    }

    public void delCustomizeEventListener(int eventID) {
        switch (eventID) {
            case 0x5001:
                if (videoRecordingTimeStartListener != null) {
                    cameraAction.delCustomEventListener(eventID, videoRecordingTimeStartListener);
                }
                break;
            case 0x3701:
                if (insertSdcardListener != null) {
                    cameraAction.delCustomEventListener(eventID, insertSdcardListener);
                }
                break;
        }

    }

    public void delEventListener(int iCatchEventID) {
        // switch(iCatchEventID){
        if (iCatchEventID == ICatchEventID.ICH_EVENT_SDCARD_FULL && sdcardStateListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_SDCARD_FULL, sdcardStateListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED && batteryStateListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_BATTERY_LEVEL_CHANGED, batteryStateListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE && captureDoneListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_CAPTURE_COMPLETE, captureDoneListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CAPTURE_START && captureStartListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_CAPTURE_START, captureStartListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_VIDEO_OFF && videoOffListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_OFF, videoOffListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FILE_ADDED && fileAddedListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FILE_ADDED, fileAddedListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_VIDEO_ON && videoOnListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_VIDEO_ON, videoOnListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_TIMELAPSE_STOP && timeLapseStopListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_TIMELAPSE_STOP, timeLapseStopListener);
        }

        if (iCatchEventID == ICatchEventID.ICH_EVENT_SERVER_STREAM_ERROR && serverStreamErrorListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_SERVER_STREAM_ERROR, serverStreamErrorListener);
        }

        if (iCatchEventID == ICatchEventID.ICH_EVENT_FILE_DOWNLOAD && fileDownloadListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FILE_DOWNLOAD, fileDownloadListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED && updateFWCompletedListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED, updateFWCompletedListener);
        }

        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF && updateFWPoweroffListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF, updateFWPoweroffListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_SDCARD_REMOVED && noSdcardListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, noSdcardListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED && connectionFailureListener != null) {
            Log.d("1111", "connectionFailureListener != null");
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_CONNECTION_DISCONNECTED, connectionFailureListener);
        }
        // add
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK && updateFWCheckListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK, updateFWCheckListener);
        }

        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR && updateFWCHKSumErrListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR, updateFWCHKSumErrListener);
        }
        if (iCatchEventID == ICatchEventID.ICH_EVENT_FW_UPDATE_NG && updateFWNGListener != null) {
            cameraAction.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG, updateFWNGListener);
        }

    }

    public class SdcardStateListener implements ICatchWificamListener {

        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(EVENT_SD_CARD_FULL).sendToTarget();
            AppLog.i(TAG, "event: EVENT_SD_CARD_FULL");
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-7
     */
    public class BatteryStateListener implements ICatchWificamListener {

        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            handler.obtainMessage(EVENT_BATTERY_ELETRIC_CHANGED).sendToTarget();
        }
    }

    public class CaptureDoneListener implements ICatchWificamListener {

        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:capture done");
            handler.obtainMessage(EVENT_CAPTURE_COMPLETED).sendToTarget();
        }
    }

    public class CaptureStartListener implements ICatchWificamListener {

        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:capture start");
            handler.obtainMessage(EVENT_CAPTURE_START).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-10
     */
    public class VideoOffListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:videooff");
            handler.obtainMessage(EVENT_VIDEO_OFF).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-3-10
     */
    public class VideoOnListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:videoON");
            handler.obtainMessage(EVENT_VIDEO_ON).sendToTarget();
        }
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public class FileAddedListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:FileAddedListener arg0=" + arg0);
            if (arg0 != null) {
                AppLog.i(TAG, "--------------receive arg0.getIntValue1()=" + arg0.getIntValue1());
                AppLog.i(TAG, "--------------receive arg0.getIntValue2()=" + arg0.getIntValue2());
                AppLog.i(TAG, "--------------receive arg0.getIntValue3()=" + arg0.getIntValue3());
            }

            arg0.getIntValue1();

            handler.obtainMessage(EVENT_FILE_ADDED).sendToTarget();
        }
    }

    public class ConnectionFailureListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:ConnectionFailureListener");
            handler.obtainMessage(EVENT_CONNECTION_FAILURE).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class TimeLapseStopListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:TimeLapseStopListener");
            handler.obtainMessage(EVENT_TIME_LAPSE_STOP).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class ServerStreamErrorListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:ServerStreamErrorListener");
            handler.obtainMessage(EVENT_SERVER_STREAM_ERROR).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class FileDownloadListener implements ICatchWificamListener {

        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive event:FileDownloadListener");
            Log.d("1111", "receive event:FileDownloadListener");
            handler.obtainMessage(EVENT_FILE_DOWNLOAD, arg0.getFileValue1()).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class VideoRecordingTimeStartListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive VideoRecordingTimeStartListener");
            handler.obtainMessage(EVENT_VIDEO_RECORDING_TIME).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWCompletedListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCompletedListener");
            handler.obtainMessage(EVENT_FW_UPDATE_COMPLETED).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWPoweroffListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWPoweroffListener");
            handler.obtainMessage(EVENT_FW_UPDATE_POWEROFF).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class NoSdcardListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive NoSdcardListener");
            AppInfo.isSdCardExist = false;
            handler.obtainMessage(EVENT_SDCARD_REMOVED).sendToTarget();
            AppLog.i(TAG, "receive NoSdcardListener GlobalInfo.isSdCard = " + AppInfo.isSdCardExist);
        }
    }

    public class ScanCameraListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "Send..........EVENT_SEARCHED_NEW_CAMERA");
            Log.d("1111", "get a uid arg0.getgetStringValue3() ==" + arg0.getStringValue3());
//            if (arg0.getIntValue1() == 1) {// ap mode
//                return;
//            }
            handler.obtainMessage(EVENT_SEARCHED_NEW_CAMERA,
                    new SearchedCameraInfo(arg0.getStringValue2(), arg0.getStringValue1(), arg0.getIntValue1(), arg0.getStringValue3()))
                    .sendToTarget();
        }
    }

    public class InsertSdcardListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive InsertSdcardListener");
            AppInfo.isSdCardExist = true;
            handler.obtainMessage(EVENT_SDCARD_INSERT).sendToTarget();
            AppLog.i(TAG, "receive InsertSdcardListener GlobalInfo.isSdCard = " + AppInfo.isSdCardExist);
        }
    }

    // add
    public class UpdateFWCheckListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCheckListener");
            handler.obtainMessage(EVENT_FW_UPDATE_CHECK).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWCHKSumErrListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWCHKSumErrListener");
            handler.obtainMessage(EVENT_FW_UPDATE_CHKSUMERR).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }

    public class UpdateFWNGListener implements ICatchWificamListener {
        @Override
        public void eventNotify(ICatchEvent arg0) {
            // TODO Auto-generated method stub
            AppLog.i(TAG, "--------------receive UpdateFWNGListener");
            handler.obtainMessage(EVENT_FW_UPDATE_NG).sendToTarget();
            // sendOkMsg(EVENT_FILE_ADDED);
        }
    }
}
