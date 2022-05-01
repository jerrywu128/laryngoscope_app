package com.honestmc.laryngoscopeapp.Listener;

/**
 * Created by b.jiang on 2016/1/26.
 */
public interface OnSettingCompleteListener {
    //    void burstOptionComplete();
//    public void updateFWComplete();
//    public void cameraConfigurationComplete();
    void settingTimeLapseModeComplete(int previewMode);

    //    void whiteBalanceOptionComplete();
    void onOptionSettingComplete();
    void settingVideoSizeComplete();
}
