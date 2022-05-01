package com.honestmc.laryngoscopeapp.PropertyId;

import com.icatch.wificam.customer.type.ICatchCameraProperty;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 11:49.
 */
public class PropertyId {

    public final static int CAPTURE_DELAY = ICatchCameraProperty.ICH_CAP_CAPTURE_DELAY;
    public final static int BURST_NUMBER = ICatchCameraProperty.ICH_CAP_BURST_NUMBER;
    public final static int WHITE_BALANCE = ICatchCameraProperty.ICH_CAP_WHITE_BALANCE;
    public final static int LIGHT_FREQUENCY = ICatchCameraProperty.ICH_CAP_LIGHT_FREQUENCY;
    public final static int UP_SIDE = 0xd614;

    public final static int SLOW_MOTION = 0xd615;
    public final static int DATE_STAMP = ICatchCameraProperty.ICH_CAP_DATE_STAMP;
    public final static int IMAGE_SIZE = ICatchCameraProperty.ICH_CAP_IMAGE_SIZE;
    public final static int VIDEO_SIZE = ICatchCameraProperty.ICH_CAP_VIDEO_SIZE;
    public final static int ESSID_NAME = 0xd834;
    public final static int ESSID_PASSWORD = 0xd835;
    public final static int CAMERA_NAME = 0xd831;
    public final static int CAMERA_PASSWORD = 0xD83D;
    public final static int TIMELAPSE_MODE = 0xEE00;
    public final static int CAPTURE_DELAY_MODE = 0xD7F0;
    public final static int NOTIFY_FW_TO_SHARE_MODE = 0xD7FB;
    public final static int VIDEO_SIZE_FLOW = 0xD7FC;
    public final static int VIDEO_RECORDING_TIME = 0xD7FD;
    public final static int CAMERA_DATE = 0x5011;
    public final static int CAMERA_ESSID = 0xD83C;
    public final static int CAMERA_PASSWORD_NEW = 0xD832;
    public final static int SERVICE_ESSID = 0xD836;
    public final static int SERVICE_PASSWORD = 0xD837;
    public final static int CAMERA_CONNECT_CHANGE = 0xD7A1;

    //新增属性 20170316
    public final static int SCREEN_SAVER = 0xD720;
    public final static int AUTO_POWER_OFF = 0xD721;
    public final static int POWER_ON_AUTO_RECORD = 0xD722;
    public final static int EXPOSURE_COMPENSATION = 0xD723;

    public final static int IMAGE_STABILIZATION = 0xD724;
    public final static int VIDEO_FILE_LENGTH = 0xD725;
    public final static int FAST_MOTION_MOVIE = 0xD726;
    public final static int WIND_NOISE_REDUCTION = 0xD727;
    // 20180815
    public final static int CAMERA_SWITCH = 0xD733;

    // JIRA ICOM-2246
    public static final int TIMELAPSE_VIDEO_SIZE_LIST_MASK = 0xD7FB;

    //JIRA BSP-1906
    public final static int CAMERA_DATE_TIMEZONE = 0xD83E;

    //pb 分段获取文件
    public final static int CAMERA_PB_LIMIT_NUMBER = 0xD83F;

    //PIMA CODE 20210908
    public final static int PIMA_DPC_WHILE_BALANCE = 0x5005;
    public final static int PIMA_DPC_EXPOSURE_METERING_MODE = 0x500B;
    public final static int PIMA_DPC_EXPOSURE_BAIS_COMPENSATION = 0x5010;
    public final static int PIMA_DPC_CONTRAST = 0x5014;
    public final static int PIMA_DPC_SHARPNESS = 0x5015;

    //CUSTOM PIMA Code
    public final static int USB_PIMA_DCP_PIV_TRIGGER = 0xD715;
    public final static int USB_PIMA_DCP_MANUFACTURE = 0xD71A;

    public final static int USB_PIMA_DCP_IQ_BRIGHTNESS = 0xD720;
    public final static int USB_PIMA_DCP_IQ_SATURATION = 0xD721;
    public final static int USB_PIMA_DCP_IQ_HUE = 0xD722;
    public final static int USB_PIMA_DCP_IQ_BLC = 0xD723;

    //System Info 20211121
    public final static int USB_PIMA_DCP_SYSTEM_INFO =  0xD80A;
    
}
