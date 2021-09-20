package com.icatch.sbcapp.View.Interface;

import com.icatch.sbcapp.MyCamera.MyCamera;

/**
 * Created by b.jiang on 2015/12/24.
 */
public interface LocalVideoPbView {
    void setTopBarVisibility(int visibility);
    void setBottomBarVisibility(int visibility);
    void setTimeLapsedValue(String value);
    void setTimeDurationValue(String  value);
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
}
