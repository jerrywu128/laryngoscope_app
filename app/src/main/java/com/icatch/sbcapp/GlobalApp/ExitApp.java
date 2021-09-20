package com.icatch.sbcapp.GlobalApp;

import android.app.Activity;

import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SdkApi.PreviewStream;
import com.icatch.sbcapp.SdkApi.VideoPlayback;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:30.
 */
public class ExitApp {
    private final String TAG = "ExitApp";
    private LinkedList<Activity> activityList = new LinkedList<Activity>();
    private static ExitApp instance;
    private Activity activity;

    public static ExitApp getInstance() {
        if (instance == null) {
            instance = new ExitApp();
        }
        return instance;
    }

    public Activity getCurActivity() {
        return activity;
    }

    public void setCurActivity(Activity curActivity) {
        this.activity = curActivity;
    }

    public void addActivity(Activity activity) {
        if (activityList.contains(activity) == false) {
            activityList.addFirst(activity);
            AppLog.d(TAG, "addActivity activity=" + activity.getClass().getSimpleName());
            AppLog.d(TAG, "addActivity activityList size=" + activityList.size());
        }
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
        AppLog.d(TAG, "removeActivity activity=" + activity.getClass().getSimpleName());
        AppLog.d(TAG, "removeActivity activityList size=" + activityList.size());
    }

    private void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityList.remove(activity);
            activity = null;
        }
    }

    public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
        List<MyCamera> cameraList = GlobalInfo.getInstance().getCameraList();
        if (cameraList != null && cameraList.isEmpty() == false) {
            PreviewStream previewStream = PreviewStream.getInstance();
            FileOperation fileOperation = FileOperation.getInstance();
            VideoPlayback videoPlayback = VideoPlayback.getInstance();
            for (MyCamera camera : cameraList) {
                if (camera.getSDKsession().isSessionOK() == true) {
                    fileOperation.cancelDownload(camera.getplaybackClient());
                    previewStream.stopMediaStream(camera.getpreviewStreamClient());
                    videoPlayback.stopPlaybackStream(camera.getVideoPlaybackClint());
                    camera.destroyCamera();
                }
            }
        }
        if (activityList == null || activityList.size() < 0) {
            return;
        }
        int size = activityList.size();
        for (int ii = 0; ii < size; ii++) {
            Activity activity = getCurActivity();
            if (!activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public void exit() {
        AppLog.i(TAG, "start exit activity activityList size=" + activityList.size());
        if (activityList != null && activityList.isEmpty() == false) {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }
        AppLog.i(TAG, "end exit System.exit");
        System.exit(0);
    }

    public void exitWhenScreenOff() {
        List<MyCamera> cameraList = GlobalInfo.getInstance().getCameraList();
        if (cameraList != null && cameraList.isEmpty() == false) {
            PreviewStream previewStream = PreviewStream.getInstance();
            FileOperation fileOperation = FileOperation.getInstance();
            VideoPlayback videoPlayback = VideoPlayback.getInstance();
            for (MyCamera camera : cameraList) {
                if (camera.getSDKsession().isSessionOK() == true) {
                    fileOperation.cancelDownload(camera.getplaybackClient());
                    previewStream.stopMediaStream(camera.getpreviewStreamClient());
                    videoPlayback.stopPlaybackStream(camera.getVideoPlaybackClint());
                    camera.destroyCamera();
                }
            }
        }

        AppLog.i(TAG, "start finsh activity");
        if (activityList != null && activityList.isEmpty() == false) {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }
        //System.exit(0);
    }

    public void finishAllActivity() {
        List<MyCamera> cameraList = GlobalInfo.getInstance().getCameraList();
        if (cameraList != null && cameraList.isEmpty() == false) {
            PreviewStream previewStream = PreviewStream.getInstance();
            FileOperation fileOperation = FileOperation.getInstance();
            VideoPlayback videoPlayback = VideoPlayback.getInstance();
            for (MyCamera camera : cameraList) {
                if (camera.getSDKsession().isSessionOK() == true) {
                    fileOperation.cancelDownload(camera.getplaybackClient());
                    previewStream.stopMediaStream(camera.getpreviewStreamClient());
                    videoPlayback.stopPlaybackStream(camera.getVideoPlaybackClint());
                    camera.destroyCamera();
                }
            }
        }

        AppLog.i(TAG, "start finsh activity");
        if (activityList != null && activityList.isEmpty() == false) {
            for (Activity activity : activityList) {
                AppLog.i(TAG, "finsh activity=" + activity);
                activity.finish();
            }
            activityList.clear();
        }
    }
}
