package com.icatch.sbcapp.View.Interface;

import android.graphics.Bitmap;

import com.icatch.sbcapp.Adapter.CameraSlotAdapter;

/**
 * Created by yh.zhang C001012 on 2015/10/20:14:44.
 * Fucntion:
 */
public interface LaunchView {
    void setLocalPhotoThumbnail(String filePath);

    void setLocalVideoThumbnail(Bitmap bitmap);

    void loadDefaultLocalPhotoThumbnail();

    void loadDefaultLocalVideooThumbnail();

    void setNoPhotoFilesFoundVisibility(int visibility);

    void setNoVideoFilesFoundVisibility(int visibility);

    void setPhotoClickable(boolean clickable);

    void setVideoClickable(boolean clickable);

    void setListviewAdapter(CameraSlotAdapter cameraSlotAdapter);

    void setBackBtnVisibility(boolean visibility);

    void setNavigationTitle(int resId);

    void setNavigationTitle(String res);

    void setLaunchLayoutVisibility(int visibility);

    void setLaunchSettingFrameVisibility(int visibility);

    void fragmentPopStackOfAll();

    void setMenuSetIpVisibility(boolean visibility);
}
