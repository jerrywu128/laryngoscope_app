package com.icatch.sbcapp.Model;

import android.util.Log;

import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.Application.MyApplication;
import com.icatch.sbcapp.BaseItems.TimeLapseMode;
import com.icatch.sbcapp.Beans.SettingMenu;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraFixedInfo;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.CameraState;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.wificam.customer.type.ICatchCameraProperty;
import com.icatch.wificam.customer.type.ICatchMode;

import java.util.LinkedList;

public class UIDisplaySource {
    public static final int CAPTURE_SETTING_MENU = 1;
    public static final int VIDEO_SETTING_MENU = 2;
    public static final int TIMELAPSE_SETTING_MENU = 3;

    private static UIDisplaySource uiDisplayResource;
    private CameraState cameraState = CameraState.getInstance();
    private LinkedList<SettingMenu> settingMenuList;

    public static UIDisplaySource getinstance() {
        if (uiDisplayResource == null) {
            uiDisplayResource = new UIDisplaySource();
        }

        return uiDisplayResource;
    }

    public static void createInstance() {
        uiDisplayResource = new UIDisplaySource();
    }

    public LinkedList<SettingMenu> getList(int type, MyCamera currCamera) {
        switch (type) {
            case CAPTURE_SETTING_MENU:
                return getForCaptureMode(currCamera);
            case VIDEO_SETTING_MENU:
                return getForVideoMode(currCamera);
            case TIMELAPSE_SETTING_MENU:
                return getForTimelapseMode(currCamera);
            default:
                return null;
        }

    }

    public LinkedList<SettingMenu> getForCaptureMode(MyCamera currCamera) {
//        LinkedList<SettingMenu> settingMenuList = new LinkedList<SettingMenu>();
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, "",R.string.setting_title_switch));
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, "", R.string.setting_type_switch));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, "", R.string.setting_type_switch));
        }

        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, currCamera.getWhiteBalance().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, currCamera.getElectricityFrequency().getCurrentUiStringInSetting(), R.string
                    .setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_DATE_STAMP) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, currCamera.getDateStamp().getCurrentUiStringInSetting(), R.string
                    .setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, currCamera.getUpside().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_SWITCH)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_camera_switch, currCamera.getCameraSwitch().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, "", R.string.setting_type_general));
        }
        // settingMenuList.add(new SettingMenu(R.string.setting_auto_download,""));
        // settingMenuList.add(new
        // SettingMenu(R.string.setting_auto_download_size_limit,""));
        settingMenuList.add(new SettingMenu(R.string.setting_format, "", R.string.setting_type_general));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(MyApplication.getContext()), R.string
                .setting_type_general));
//         settingMenuList.add(new SettingMenu(R.string.setting_update_fw,""));

        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_IMAGE_SIZE) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_image_size, currCamera.getImageSize().getCurrentUiStringInSetting(), R.string
                    .setting_type_specific));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_CAPTURE_DELAY) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_capture_delay, currCamera.getCaptureDelay().getCurrentUiStringInPreview(), R.string
                    .setting_type_specific));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_BURST_NUMBER) == true) {
            settingMenuList.add(new SettingMenu(R.string.title_burst, currCamera.getBurst().getCurrentUiStringInSetting(), R.string.setting_type_specific));
        }

        //Begin add ICOM-4129
        if (CameraProperties.getInstance().hasFuction(PropertyId.SCREEN_SAVER)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, currCamera.getScreenSaver().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, currCamera.getAutoPowerOff().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, currCamera.getExposureCompensation()
                    .getCurrentUiStringInPreview(), R.string.setting_type_custom));
        }
        //End add ICOM-4129

//        settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, currCamera.getScreenSaver().getCurrentUiStringInPreview(),R.string
// .setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off,currCamera.getAutoPowerOff().getCurrentUiStringInPreview() ,R.string
// .setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation,currCamera.getExposureCompensation().getCurrentUiStringInPreview()
// ,R.string.setting_type_custom));

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION, R.string.setting_type_other));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, CameraFixedInfo.getInstance().getCameraName(), R.string.setting_type_other));
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, CameraFixedInfo.getInstance().getCameraVersion(), R.string
                    .setting_type_other));
        }
        return settingMenuList;
    }

    public LinkedList<SettingMenu> getForVideoMode(MyCamera currCamera) {
//        LinkedList<SettingMenu> settingMenuList = new LinkedList<SettingMenu>();
        Log.d("1111", "currCamera ==" + currCamera);
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, "",R.string.setting_type_switch));
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, "", R.string.setting_type_switch));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, "", R.string.setting_type_switch));
        }

        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, currCamera.getWhiteBalance().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, currCamera.getElectricityFrequency().getCurrentUiStringInSetting(), R.string
                    .setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_DATE_STAMP) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_datestamp, currCamera.getDateStamp().getCurrentUiStringInSetting(), R.string
                    .setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, currCamera.getUpside().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_SWITCH)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_camera_switch, currCamera.getCameraSwitch().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, "", R.string.setting_type_general));
        }
        // settingMenuList.add(new SettingMenu(R.string.setting_auto_download,""));
        // settingMenuList.add(new
        // SettingMenu(R.string.setting_auto_download_size_limit,""));
        //settingMenuList.add(new SettingMenu(R.string.setting_format, "", R.string.setting_type_general));
        /*settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(MyApplication.getContext()), R.string
                .setting_type_general));*/
//         settingMenuList.add(new SettingMenu(R.string.setting_update_fw,""));
/*
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_VIDEO_SIZE) == true) {
            settingMenuList.add(new SettingMenu(R.string.setting_video_size, currCamera.getVideoSize().getCurrentUiStringInSetting(), R.string
                    .setting_type_specific));
        }*/
        if (CameraProperties.getInstance().hasFuction(PropertyId.SLOW_MOTION)) {
            settingMenuList.add(new SettingMenu(R.string.slowmotion, currCamera.getSlowMotion().getCurrentUiStringInSetting(), R.string.setting_type_specific));
        }

        //ICOM-4219
       /* if (CameraProperties.getInstance().hasFuction(PropertyId.SCREEN_SAVER)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, currCamera.getScreenSaver().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.POWER_ON_AUTO_RECORD)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record, "", R.string.setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, currCamera.getAutoPowerOff().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }*/
        if (CameraProperties.getInstance().hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, currCamera.getExposureCompensation()
                    .getCurrentUiStringInPreview(), R.string.setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.IMAGE_STABILIZATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_image_stabilization, "", R.string.setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.VIDEO_FILE_LENGTH)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_video_file_length, currCamera.getVideoFileLength().getCurrentUiStringInPreview(), R
                    .string.setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.FAST_MOTION_MOVIE)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_fast_motion_movie, currCamera.getFastMotionMovie().getCurrentUiStringInPreview(), R
                    .string.setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.WIND_NOISE_REDUCTION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_wind_noise_reduction, "", R.string.setting_type_custom));
        }
        //ICOM-4219

//        settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, currCamera.getScreenSaver().getCurrentUiStringInPreview(),R.string
// .setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_power_on_auto_record,"" ,R.string.setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off,currCamera.getAutoPowerOff().getCurrentUiStringInPreview() ,R.string
// .setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation,currCamera.getExposureCompensation().getCurrentUiStringInPreview()
// ,R.string.setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_image_stabilization,"" ,R.string.setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_video_file_length,currCamera.getVideoFileLength().getCurrentUiStringInPreview() ,R
// .string.setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_fast_motion_movie,currCamera.getFastMotionMovie().getCurrentUiStringInPreview() ,R
// .string.setting_type_custom));
//        settingMenuList.add(new SettingMenu(R.string.setting_title_wind_noise_reduction, "",R.string.setting_type_custom));

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION, R.string.setting_type_other));
        //settingMenuList.add(new SettingMenu(R.string.setting_product_name, CameraFixedInfo.getInstance().getCameraName(), R.string.setting_type_other));
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, CameraFixedInfo.getInstance().getCameraVersion(), R.string
                    .setting_type_other));
        }

        return settingMenuList;
    }

    public LinkedList<SettingMenu> getForTimelapseMode(MyCamera currCamera) {
//        LinkedList<SettingMenu> settingMenuList = new LinkedList<SettingMenu>();
        if (settingMenuList == null) {
            settingMenuList = new LinkedList<SettingMenu>();
        } else {
            settingMenuList.clear();
        }
//        settingMenuList.add(new SettingMenu(R.string.setting_audio_switch, "",R.string.setting_title_switch));
        if (cameraState.isSupportImageAutoDownload()) {
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download, "", R.string.setting_type_switch));
            settingMenuList.add(new SettingMenu(R.string.setting_auto_download_size_limit, "", R.string.setting_type_switch));
        }


        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_WHITE_BALANCE)) {
            settingMenuList.add(new SettingMenu(R.string.title_awb, currCamera.getWhiteBalance().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_LIGHT_FREQUENCY)) {
            settingMenuList.add(new SettingMenu(R.string.setting_power_supply, currCamera.getElectricityFrequency().getCurrentUiStringInSetting(), R.string
                    .setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.UP_SIDE)) {
            settingMenuList.add(new SettingMenu(R.string.upside, currCamera.getUpside().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_SWITCH)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_camera_switch, currCamera.getCameraSwitch().getCurrentUiStringInSetting(), R.string.setting_type_general));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_ESSID)) {//camera password and wifi
            settingMenuList.add(new SettingMenu(R.string.camera_wifi_configuration, "", R.string.setting_type_general));
        }
        settingMenuList.add(new SettingMenu(R.string.setting_format, "", R.string.setting_type_general));
        settingMenuList.add(new SettingMenu(R.string.setting_storage_location, StorageUtil.getCurStorageLocation(MyApplication.getContext()), R.string
                .setting_type_general));
//         settingMenuList.add(new SettingMenu(R.string.setting_update_fw,""));
        if (currCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_IMAGE_SIZE) == true) {
                settingMenuList.add(new SettingMenu(R.string.setting_image_size, currCamera.getImageSize().getCurrentUiStringInSetting(), R.string
                        .setting_type_specific));
            }
        } else if (currCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
            if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_VIDEO_SIZE) == true) {
                settingMenuList.add(new SettingMenu(R.string.setting_video_size, currCamera.getVideoSize().getCurrentUiStringInSetting(), R.string
                        .setting_type_specific));
            }
        }
        String curTimeLapseInterval;
        if (currCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            curTimeLapseInterval = currCamera.getTimeLapseStillInterval().getCurrentValue();
        } else {
            curTimeLapseInterval = currCamera.getTimeLapseVideoInterval().getCurrentValue();
        }
        if (CameraProperties.getInstance().cameraModeSupport(ICatchMode.ICH_MODE_TIMELAPSE)) {
            settingMenuList.add(new SettingMenu(R.string.title_timeLapse_mode, currCamera.getTimeLapseMode().getCurrentUiStringInSetting(), R.string
                    .setting_type_specific));
            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_interval, curTimeLapseInterval, R.string.setting_type_specific));
            settingMenuList.add(new SettingMenu(R.string.setting_time_lapse_duration, currCamera.gettimeLapseDuration().getCurrentValue(), R.string
                    .setting_type_specific));
        }

        //Begin add ICOM-4129
        if (CameraProperties.getInstance().hasFuction(PropertyId.SCREEN_SAVER)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_screen_saver, currCamera.getScreenSaver().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.AUTO_POWER_OFF)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_auto_power_off, currCamera.getAutoPowerOff().getCurrentUiStringInPreview(), R.string
                    .setting_type_custom));
        }
        if (CameraProperties.getInstance().hasFuction(PropertyId.EXPOSURE_COMPENSATION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_title_exposure_compensation, currCamera.getExposureCompensation()
                    .getCurrentUiStringInPreview(), R.string.setting_type_custom));
        }
        //End add ICOM-4129

        settingMenuList.add(new SettingMenu(R.string.setting_app_version, AppInfo.APP_VERSION, R.string.setting_type_other));
        settingMenuList.add(new SettingMenu(R.string.setting_product_name, CameraFixedInfo.getInstance().getCameraName(), R.string.setting_type_other));
        if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_FW_VERSION)) {
            settingMenuList.add(new SettingMenu(R.string.setting_firmware_version, CameraFixedInfo.getInstance().getCameraVersion(), R.string
                    .setting_type_other));
        }
        return settingMenuList;
    }

}
