package com.honestmc.laryngoscopeapp.View.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.honestmc.laryngoscopeapp.Adapter.SettingListAdapter;
import com.honestmc.laryngoscopeapp.Listener.OnDecodeTimeListener;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 15:09.
 */
public interface PreviewView {

    void setRecording_text(int visibility);

    void setmPreviewVisibility(int visibility);

    void setWbStatusVisibility(int visibility);

    void setBurstStatusVisibility(int visibility);

    void setWifiStatusVisibility(int visibility);

    void setWifiIcon(int drawableId);

    void setBatteryStatusVisibility(int visibility);

    void setBatteryIcon(int drawableId);

    void settimeLapseModeVisibility(int visibility);

    void settimeLapseModeIcon(int drawableId);

    void setSlowMotionVisibility(int visibility);

    void setCarModeVisibility(int visibility);

    void setRecordingTimeVisibility(int visibility);

    void setAutoDownloadVisibility(int visibility);

    void setCaptureBtnBackgroundResource(int id);

    void setRecordingTime(String laspeTime);

    void setDelayCaptureLayoutVisibility(int visibility);

    void setDelayCaptureTextTime(String delayCaptureTime);

    void setBurstStatusIcon(int drawableId);

    void setWbStatusIcon(int drawableId);

    void setUpsideVisibility(int visibility);

    void startMPreview(MyCamera myCamera);

    void stopMPreview(MyCamera myCamera);

    void setCaptureBtnEnability(boolean enablity);

    void showZoomView();

    void setMaxZoomRate(int maxZoomRate);

    int getZoomViewProgress();

    int getZoomViewMaxZoomRate();

    void updateZoomViewProgress(int currentZoomRatio);

    void setSettingMenuListAdapter(SettingListAdapter settingListAdapter);

    int getSetupMainMenuVisibility();

    void setSetupMainMenuVisibility(int visibility);

    void setAutoDownloadBitmap(Bitmap bitmap);

    void setActionBarTitle(int resId);

    void setSettingBtnVisible(boolean isVisible);

    void setBackBtnVisibility(boolean isVisible);

    void setSupportPreviewTxvVisibility(int visibility);

    void setchangeVideoBtnBackgroundResource(int drawableId);

    void setchangeCameraBtnBackgroundResource(int drawableId);

    void setBootPage(int status);

    void showPopupWindow(int curMode);

    void showPopupMenu(View view);

    void setTimepLapseRadioBtnVisibility(int visibility);

    void setCaptureRadioBtnVisibility(int visibility);

    void startPhotolocalCapture();

    void startRecordlocalCapture();

    void stopRecordlocalCapture();

    void setProgressSave(int IQ,int value);


    void setVideoRadioBtnVisibility(int visibility);

    void setTimepLapseRadioChecked(boolean checked);

    void setCaptureRadioBtnChecked(boolean checked);

    void setVideoRadioBtnChecked(boolean checked);

    void dismissPopupWindow();

    void setPreviewInfo(String info);

    void setDecodeInfo(String info);

    void setYouTubeLiveLayoutVisibility(int visibility);

    void setYouTubeBtnTxv(int resId);

    void setOnDecodeTimeListener(OnDecodeTimeListener onDecodeTimeListener);

    void setDecodeTimeLayoutVisibility(int visibility);

    void setDecodeTimeTxv(String value);

    void setCameraSwitchLayoutVisibility(int visibility);
}
