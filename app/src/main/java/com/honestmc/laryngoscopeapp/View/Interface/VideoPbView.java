package com.honestmc.laryngoscopeapp.View.Interface;

import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;

/**
 * Created by b.jiang on 2016/3/16.
 */
public interface VideoPbView {
    void setTopBarVisibility(int visibility);

    void setBottomBarVisibility(int visibility);

    void setPlayCircleImageViewVisibility(int visibility);

    void setTimeLapsedValue(String value);

    void setTimeDurationValue(String value);

    void setSeekBarProgress(int value);

    void setSeekBarMaxValue(int value);

    int getSeekBarProgress();

    void setSeekBarSecondProgress(int value);

    void setPlayBtnSrc(int resid);

    void showLoadingCircle(boolean isShow);

    void setLoadPercent(int value);

    void setVideoNameTxv(String curLocalVideoPath);

    void startMPreview(MyCamera mCamera, int previewLaunchMode);

    void stopMPreview();

    void setSeekbarEnabled(boolean enabled);

    //BSP-1257 add 20170818
    void setDeleteBtnEnabled(boolean enabled);

    void setDownloadBtnEnabled(boolean enabled);

}
