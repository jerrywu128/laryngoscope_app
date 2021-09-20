package com.icatch.sbcapp.ThumbnailGetting;

import android.graphics.Bitmap;

import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Model.Implement.SDKSession;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.Tools.BitmapTools;
import com.icatch.wificam.customer.ICatchWificamPlayback;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

/**
 * Created by b.jiang on 2015/12/31.
 */
public class ThumbnailOperation {

    //ThumbnailGetting
    private static String TAG = "ThumbnailOperation";
    private static FileOperation fileOperation = FileOperation.getInstance();

    public static Bitmap getVideoThumbnailFromSdk(String videoPath) {
        if (videoPath == null) {
            return null;
        }
        AppLog.d(TAG, "start getVideoThumbnailFromSdk");
        ICatchWificamPlayback cameraPlayback = null;
        Bitmap bitmap = null;
        ICatchFrameBuffer frameBuffer = null;
        int datalength = 0;
        byte[] buffer = null;
        SDKSession sdkSession = new SDKSession();
        if (!sdkSession.prepareSession("192.168.1.1", false)) {
            AppLog.d(TAG, "getVideoThumbnailFromSdk false");
            return null;
        }
        try {
            cameraPlayback = sdkSession.getSDKSession().getPlaybackClient();
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        }
        frameBuffer = FileOperation.getInstance().getThumbnail(cameraPlayback, videoPath);
        if (frameBuffer != null) {
            buffer = frameBuffer.getBuffer();
            datalength = frameBuffer.getFrameSize();
            AppLog.d(TAG, "end getVideoThumbnailFromSdk datalength=" + datalength);
            if (datalength > 0) {
                bitmap = BitmapTools.decodeByteArrayForRGB565(buffer, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
//                bitmap = BitmapTools.decodeByteArray(buffer, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
            }
        }
        sdkSession.destroySession();
        AppLog.d(TAG, "end getVideoThumbnailFromSdk bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        AppLog.d(TAG, "start getVideoThumbnail");
        Bitmap bitmap = BitmapTools.getVideoThumbnail(videoPath, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
        if (bitmap == null) {
            bitmap = getVideoThumbnailFromSdk(videoPath);
        }
        AppLog.d(TAG, "end getVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap getLocalVideoThumbnail(String videoPath) {
        AppLog.d(TAG, "start getLocalVideoThumbnail");
        Bitmap bitmap = null;
        ICatchFrameBuffer frameBuffer = null;
        int datalength = 0;
        byte[] buffer = null;
        frameBuffer = FileOperation.getInstance().getThumbnail(videoPath);
        if (frameBuffer != null) {
            buffer = frameBuffer.getBuffer();
            datalength = frameBuffer.getFrameSize();
            if (datalength > 0) {
                //JIRA BUG ICOM-3521 Start modify by b.jiang 20160725
//                bitmap = BitmapTools.decodeByteArray(buffer, 160, 160);
                bitmap = BitmapTools.decodeByteArrayForRGB565(buffer, 160, 160);
//                bitmap = BitmapFactory.decodeByteArray(buffer, 0, datalength);
                //JIRA BUG ICOM-3521 End modify by b.jiang 20160725
            }
        }
        AppLog.d(TAG, "end getLocalVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap getlocalVideoWallThumbnail(String videoPath) {
        AppLog.d(TAG, "start getVideoThumbnail");
        Bitmap bitmap = BitmapTools.getVideoThumbnail(videoPath, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
//        Bitmap bitmap = null;
        if(bitmap == null){
            bitmap = getLocalVideoThumbnail(videoPath);
        }
        AppLog.d(TAG, "end getVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static int getBatteryLevelIcon() {
        int current = CameraProperties.getInstance().getBatteryElectric();
        AppLog.d(TAG, "current setBatteryLevelIcon= " + current);
        int resId = -1;
        if (current < 20 && current >= 0) {
            resId = R.drawable.ic_battery_alert_green_24dp;
        } else if (current == 33) {
            resId = R.drawable.ic_battery_30_green_24dp;
        } else if (current == 66) {
            resId = R.drawable.ic_battery_60_green_24dp;
        } else if (current == 100) {
            resId = R.drawable.ic_battery_full_green_24dp;
        } else if (current > 100) {
            resId = R.drawable.ic_battery_charging_full_green_24dp;
        }
        return resId;
    }
}
