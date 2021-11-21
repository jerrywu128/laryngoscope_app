/**
 * Added by zhangyanhu C01012,2014-6-23
 */
package com.icatch.sbcapp.SdkApi;

import android.util.Log;

import com.icatch.sbcapp.DataConvert.BurstConvert;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.R;
import com.icatch.wificam.customer.ICatchWificamControl;
import com.icatch.wificam.customer.ICatchWificamProperty;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchDevicePropException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchNoSDCardException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.type.ICatchCodec;
import com.icatch.wificam.customer.type.ICatchLightFrequency;
import com.icatch.wificam.customer.type.ICatchMode;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CameraProperties {
    private final String TAG = "CameraProperties";
    private List<Integer> supportedPropertieList;
    private List<ICatchMode> modeList;
    private static CameraProperties instance;
    private ICatchWificamProperty cameraConfiguration;
    private ICatchWificamControl cameraAction;

    public static CameraProperties getInstance() {
        if (instance == null) {
            instance = new CameraProperties();
        }
        return instance;
    }

    private CameraProperties() {

    }

    public void resetCameraProperties() {
        AppLog.d(TAG, "resetCameraProperties");
        supportedPropertieList = null;
        modeList = null;
        instance = null;
    }

    public void initCameraProperties() {
        cameraConfiguration = GlobalInfo.getInstance().getCurrentCamera().getCameraPropertyClint();
        cameraAction = GlobalInfo.getInstance().getCurrentCamera().getcameraActionClient();
    }

    public List<String> getSupportedImageSizes() {
        List<String> list = null;
        try {
            list = cameraConfiguration.getSupportedImageSizes();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (list != null) {
            AppLog.i(TAG, "end getSupportedImageSizes list.size =" + list.size());
            for (String temp : list
                    ) {
                AppLog.i(TAG, "end getSupportedImageSizes value=" + temp);
            }
        }
        return list;
    }

    public List<String> getSupportedVideoSizes() {
        AppLog.i(TAG, "begin getSupportedVideoSizes");
        List<String> list = null;
        try {
            list = cameraConfiguration.getSupportedVideoSizes();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "begin getSupportedVideoSizes list=" + list);

        return list;
    }

    public List<Integer> getSupportedWhiteBalances() {
        AppLog.i(TAG, "begin getSupportedWhiteBalances");
        List<Integer> list = new LinkedList<>();
        try {
            list = cameraConfiguration.getSupportedWhiteBalances();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedWhiteBalances list=" + list);
        return list;
    }

    public List<Integer> getSupportedCaptureDelays() {
        AppLog.i(TAG, "begin getSupportedCaptureDelays");
        List<Integer> list =  new LinkedList<>();
        try {
            list = cameraConfiguration.getSupportedCaptureDelays();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedCaptureDelays list=" + list);
        if (list != null) {
            for (Integer temp : list
                    ) {
                AppLog.i(TAG, "end getSupportedCaptureDelays list value=" + temp);
            }
        }

        return list;
    }

    public List<Integer> getSupportedLightFrequencys() {
        AppLog.i(TAG, "begin getSupportedLightFrequencys");
        List<Integer> list =  new LinkedList<>();;

        try {
            list = cameraConfiguration.getSupportedLightFrequencies();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // delete LIGHT_FREQUENCY_AUTO because UI don't need this option
        AppLog.i(TAG, "end getSupportedLightFrequencys list=" + list);
        if (list != null) {
            for (int ii = 0; ii < list.size(); ii++) {
                if (list.get(ii) == ICatchLightFrequency.ICH_LIGHT_FREQUENCY_AUTO) {
                    list.remove(ii);
                }
            }
        }

        return list;
    }

    public boolean setImageSize(String value) {
        AppLog.i(TAG, "begin setImageSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraConfiguration.setImageSize(value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setImageSize retVal=" + retVal);
        return retVal;
    }

    public boolean setVideoSize(String value) {
        AppLog.i(TAG, "begin setVideoSize set value =" + value);
        boolean retVal = false;

        try {
            retVal = cameraConfiguration.setVideoSize(value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setVideoSize retVal=" + retVal);
        return retVal;
    }

    public boolean setWhiteBalance(int value) {
        AppLog.i(TAG, "begin setWhiteBalanceset value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
            retVal = cameraConfiguration.setWhiteBalance(value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setWhiteBalance retVal=" + retVal);
        return retVal;
    }

    public boolean setLightFrequency(int value) {
        AppLog.i(TAG, "begin setLightFrequency set value =" + value);
        boolean retVal = false;
        if (value < 0 || value == 0xff) {
            return false;
        }
        try {
            retVal = cameraConfiguration.setLightFrequency(value);
//            retVal = cameraConfiguration.setLightFreqency(value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setLightFrequency retVal=" + retVal);
        return retVal;
    }

    public String getCurrentImageSize() {
        AppLog.i(TAG, "begin getCurrentImageSize");
        String value = "unknown";

        try {
            value = cameraConfiguration.getCurrentImageSize();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentImageSize value =" + value);
        return value;
    }

    public String getCurrentVideoSize() {
        AppLog.i(TAG, "begin getCurrentVideoSize");
        String value = "unknown";

        try {
            value = cameraConfiguration.getCurrentVideoSize();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentVideoSize value =" + value);
        return value;
    }

    public int getCurrentWhiteBalance() {
        AppLog.i(TAG, "begin getCurrentWhiteBalance");
        int value = 0xff;
        try {
            AppLog.i(TAG, "******value=   " + value);
            value = cameraConfiguration.getCurrentWhiteBalance();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentWhiteBalance retvalue =" + value);
        return value;
    }

    public int getCurrentLightFrequency() {
        AppLog.i(TAG, "begin getCurrentLightFrequency");
        int value = 0xff;
        try {
            value = cameraConfiguration.getCurrentLightFrequency();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentLightFrequency value =" + value);
        return value;
    }

    public boolean setCaptureDelay(int value) {
        AppLog.i(TAG, "begin setCaptureDelay set value =" + value);
        boolean retVal = false;

        try {
            AppLog.i(TAG, "start setCaptureDelay ");
            retVal = cameraConfiguration.setCaptureDelay(value);
            AppLog.i(TAG, "end setCaptureDelay ");
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCaptureDelay retVal =" + retVal);
        return retVal;
    }

    public int getCurrentCaptureDelay() {
        AppLog.i(TAG, "begin getCurrentCaptureDelay");
        int retVal = 0;

        try {
            retVal = cameraConfiguration.getCurrentCaptureDelay();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentCaptureDelay retVal=" + retVal);
        return retVal;
    }

    public int getCurrentDateStamp() {
        AppLog.i(TAG, "begin getCurrentDateStampType");
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentDateStamp();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "getCurrentDateStampType retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public boolean setDateStamp(int dateStamp) {
        AppLog.i(TAG, "begin setDateStampType set value = " + dateStamp);
        Boolean retValue = false;
        try {
            retValue = cameraConfiguration.setDateStamp(dateStamp);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setDateStampType retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-4-1
     */
    public List<Integer> getDateStampList() {
        AppLog.i(TAG, "begin getDateStampList");
        List<Integer> list =  new LinkedList<>();;
        try {
            list = cameraConfiguration.getSupportedDateStamps();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getDateStampList list.size ==" + list.size());
        return list;
    }

    public List<Integer> getSupportedProperties() {
        AppLog.i(TAG, "begin getSupportedProperties");
        //List<Integer> fuction = null;
        List<Integer> fuction = new ArrayList<Integer>();
        // List<Integer> temp = null;
        try {
            fuction = cameraConfiguration.getSupportedProperties();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedProperties supportedPropertieList=" + fuction);
//        for (Integer temp:supportedPropertieList
//             ) {
//            AppLog.i(TAG, "end getSupportedProperties supportedPropertieList value=" + temp);
//        }
        return fuction;
    }

    /**
     * to prase the burst number Added by zhangyanhu C01012,2014-2-10
     */
    public int getCurrentBurstNum() {
        AppLog.i(TAG, "begin getCurrentBurstNum");
        int number = 0xff;
        try {
            number = cameraConfiguration.getCurrentBurstNumber();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "getCurrentBurstNum num =" + number);
        return number;
    }

    public int getCurrentAppBurstNum() {
        AppLog.i(TAG, "begin getCurrentAppBurstNum");
        int number = 0xff;
        try {
            number = cameraConfiguration.getCurrentBurstNumber();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        number = BurstConvert.getInstance().getBurstConverFromFw(number);
        AppLog.i(TAG, "getCurrentAppBurstNum num =" + number);
        return number;
    }

    public boolean setCurrentBurst(int burstNum) {
        AppLog.i(TAG, "begin setCurrentBurst set value = " + burstNum);
        if (burstNum < 0 || burstNum == 0xff) {
            return false;
        }
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setBurstNumber(burstNum);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCurrentBurst retValue =" + retValue);
        return retValue;
    }

    public int getRemainImageNum() {
        AppLog.i(TAG, "begin getRemainImageNum");
        int num = -1;
        try {
            num = cameraAction.getFreeSpaceInImages();
            AppLog.i(TAG, "return getRemainImageNum num =" + num);
        } catch (Exception e) {
            AppLog.e(TAG, "" + e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
            GlobalInfo.getInstance().showExceptionInfoDialog(R.string.text_get_data_exception);
        }
        AppLog.i(TAG, "end getRemainImageNum num =" + num);
        return num;
    }

    public int getRecordingRemainTime() {
        AppLog.i(TAG, "begin getRecordingRemainTimeInt");
        int recordingTime = -1;

        try {
            recordingTime = cameraAction.getRemainRecordingTime();
            AppLog.i(TAG, "return recordingTime =" + recordingTime);
        } catch (Exception e) {
            AppLog.e(TAG, "" + e.getClass().getSimpleName());
            e.printStackTrace();
            GlobalInfo.getInstance().showExceptionInfoDialog(R.string.text_device_exception);
        }
        AppLog.i(TAG, "end getRecordingRemainTimeInt recordingTime =" + recordingTime);
        return recordingTime;
    }

    public boolean isSDCardExist() {
        AppLog.i(TAG, "begin isSDCardExist");
        Boolean isReady = false;
        try {
            isReady = cameraAction.isSDCardExist();
        } catch (Exception e) {
            AppLog.e(TAG, e.getClass().getSimpleName());
            // TODO Auto-generated catch block
            e.printStackTrace();
            GlobalInfo.getInstance().showExceptionInfoDialog(R.string.text_device_exception);
        }
        AppLog.i(TAG, "end isSDCardExist isReady =" + isReady);
        return isReady;

        //return GlobalInfo.getInstance().getCurrentCamera().isSdCardReady();
        // JIRA ICOM-1577 End:Modify by b.jiang C01063 2015-07-17
    }

    public int getBatteryElectric() {
        AppLog.i(TAG, "start getBatteryElectric");
        int electric = 0;
        try {
            electric = cameraAction.getCurrentBatteryLevel();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getBatteryElectric electric =" + electric);
        return electric;
    }

    public boolean supportVideoPlayback() {
        AppLog.i(TAG, "begin hasVideoPlaybackFuction");
        boolean retValue = false;
        try {
            retValue = cameraAction.supportVideoPlayback();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSDCardException e) {
            AppLog.e(TAG, "IchNoSDCardException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "hasVideoPlaybackFuction retValue =" + retValue);
        return retValue;
        // return false;
    }

    public boolean cameraModeSupport(ICatchMode mode) {
        AppLog.i(TAG, "begin cameraModeSupport  mode=" + mode);
        Boolean retValue = false;
        if (modeList == null) {
            modeList = getSupportedModes();
        }
//        modeList = getSupportedModes();
        if (modeList == null) {
            return false;
        }
        if (modeList.contains(mode)) {
            retValue = true;
        }
        AppLog.i(TAG, "end cameraModeSupport retValue =" + retValue);
        return retValue;
    }

    public String getCameraMacAddress() {
        AppLog.i(TAG, "begin getCameraMacAddress macAddress macAddress ");
        String macAddress = cameraAction.getMacAddress();
        AppLog.i(TAG, "end getCameraMacAddress macAddress =" + macAddress);
        return macAddress;
    }

    public boolean hasFuction(int fuc) {
        AppLog.i(TAG, "begin hasFuction query supportedPropertieList = " + fuc);
        if (supportedPropertieList == null) {
            supportedPropertieList = getSupportedProperties();
        }
        Boolean retValue = false;
        if (supportedPropertieList != null && supportedPropertieList.contains(fuc) == true) {
            retValue = true;
        }
        AppLog.i(TAG, "end hasFuction retValue =" + retValue);
        return retValue;
    }

    //BSP-899
    public void refreshSupportedProperties() {
        AppLog.i(TAG, "begin refreshSupportedProperties");
        if (supportedPropertieList != null) {
            supportedPropertieList.clear();
        }
        supportedPropertieList = getSupportedProperties();
        AppLog.i(TAG, "end refreshSupportedProperties");
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getsupportedDateStamps() {
        AppLog.i(TAG, "begin getsupportedDateStamps");
        List<Integer> list =  new LinkedList<>();;

        try {
            list = cameraConfiguration.getSupportedDateStamps();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getsupportedDateStamps list.size() =" + list.size());
        return list;
    }

    public List<Integer> getsupportedBurstNums() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getsupportedBurstNums");
        List<Integer> list =  new LinkedList<>();;

        try {
            list = cameraConfiguration.getSupportedBurstNumbers();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getsupportedBurstNums list.size() =" + list.size());
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-4
     */
    public List<Integer> getSupportedFrequencies() {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getSupportedFrequencies");
        List<Integer> list =  new LinkedList<>();;
        try {
            list = cameraConfiguration.getSupportedLightFrequencies();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedFrequencies list.size() =" + list.size());
        return list;
    }

    /**
     * Added by zhangyanhu C01012,2014-8-21
     *
     * @return
     */
    public List<ICatchMode> getSupportedModes() {
        AppLog.i(TAG, "begin getSupportedModes");

        List<ICatchMode> list = null;
        try {
            list = cameraAction.getSupportedModes();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedModes list=" + list);

        return list;
    }

    public List<Integer> getSupportedTimeLapseDurations() {
        AppLog.i(TAG, "begin getSupportedTimeLapseDurations");
        List<Integer> list =  new LinkedList<>();;
        // boolean retValue = false;
        try {
            list = cameraConfiguration.getSupportedTimeLapseDurations();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (list != null) {
            for (int ii = 0; ii < list.size(); ii++) {
                AppLog.i(TAG, "list.get(ii) =" + list.get(ii));
            }
        }

        AppLog.i(TAG, "end getSupportedTimeLapseDurations list=" + list);
        return list;
    }

    public List<Integer> getSupportedTimeLapseIntervals() {
        AppLog.i(TAG, "begin getSupportedTimeLapseIntervals");
        List<Integer> list =  new LinkedList<>();;
        // boolean retValue = false;
        try {
            list = cameraConfiguration.getSupportedTimeLapseIntervals();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedTimeLapseIntervals list=" + list);
        return list;
    }

    public boolean setTimeLapseDuration(int timeDuration) {
        AppLog.i(TAG, "begin setTimeLapseDuration videoDuration =" + timeDuration);
        boolean retVal = false;
        if (timeDuration < 0 || timeDuration == 0xff) {
            return false;
        }
        try {
            retVal = cameraConfiguration.setTimeLapseDuration(timeDuration);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseDuration() {
        AppLog.i(TAG, "begin getCurrentTimeLapseDuration");
        int retVal = 0xff;
        try {
            retVal = cameraConfiguration.getCurrentTimeLapseDuration();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentTimeLapseDuration retVal=" + retVal);
        return retVal;
    }

    public boolean setTimeLapseInterval(int timeInterval) {
        AppLog.i(TAG, "begin setTimeLapseInterval videoDuration =" + timeInterval);
        boolean retVal = false;
//		if (timeInterval < 0 || timeInterval == 0xff) {
//			return false;
//		}
        try {
            retVal = cameraConfiguration.setTimeLapseInterval(timeInterval);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public int getCurrentTimeLapseInterval() {
        AppLog.i(TAG, "begin getCurrentTimeLapseInterval");
        int retVal = 0xff;
        try {
            retVal = cameraConfiguration.getCurrentTimeLapseInterval();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentTimeLapseInterval retVal=" + retVal);
        return retVal;
    }

    public int getMaxZoomRatio() {
        AppLog.i(TAG, "start getMaxZoomRatio");
        int retValue = 0;/*
        try {
            retValue = cameraConfiguration.getMaxZoomRatio();

        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        AppLog.i(TAG, "end getMaxZoomRatio retValue =" + retValue);
        return retValue;
    }

    public int getCurrentZoomRatio() {
        AppLog.i(TAG, "start getCurrentZoomRatio");
        int retValue = 0;/*
        try {
            retValue = cameraConfiguration.getCurrentZoomRatio();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        AppLog.i(TAG, "end getCurrentZoomRatio retValue =" + retValue);
        return retValue;
    }

    public int getCurrentUpsideDown() {
        AppLog.i(TAG, "start getCurrentUpsideDown");
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentUpsideDown();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentUpsideDown retValue =" + retValue);
        return retValue;
    }

    public boolean setUpsideDown(int upside) {
        AppLog.i(TAG, "start setUpsideDown upside = " + upside);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setUpsideDown(upside);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setUpsideDown retValue =" + retValue);
        return retValue;
    }
    public int getCurrentCameraSwitch() {
        AppLog.i(TAG, "start getCurrentCameraSwitch propertyId = " + PropertyId.CAMERA_SWITCH);
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentPropertyValue(PropertyId.CAMERA_SWITCH);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentCameraSwitch retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraSwitch(int value) {
        AppLog.i(TAG, "start setCameraSwitch value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setPropertyValue(PropertyId.CAMERA_SWITCH, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSwitch retValue =" + retValue);
        return retValue;
    }
    public int getCurrentSlowMotion() {
        AppLog.i(TAG, "start getCurrentSlowMotion");
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentSlowMotion();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setSlowMotion(int slowMotion) {
        AppLog.i(TAG, "start setSlowMotion slowMotion = " + slowMotion);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setSlowMotion(slowMotion);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setSlowMotion retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd HHmmss");
        String tempDate = myFmt.format(date);
        tempDate = tempDate.replaceAll(" ", "T");
        tempDate = tempDate + ".0";
        AppLog.i(TAG, "start setCameraDate date = " + tempDate);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_DATE, tempDate);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraDate retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraDateTimeZone() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat myFmt = new SimpleDateFormat("Z");
        String tempZone= myFmt.format(date);

        AppLog.i(TAG, "start setCameraDateTimeZone date = " + tempZone);
        Log.i("1111", "start setCameraDateTimeZone = " + tempZone);

        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_DATE_TIMEZONE, tempZone);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraDateTimeZone retValue =" + retValue);
        return retValue;

    }

    public boolean setCameraEssidName(String ssidName) {
        AppLog.i(TAG, "start setCameraEssidName date = " + ssidName);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.ESSID_NAME, ssidName);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidName() {
        AppLog.i(TAG, "start getCameraEssidName");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.ESSID_NAME);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraEssidName retValue =" + retValue);
        return retValue;
    }

    public String getCameraEssidPassword() {
        AppLog.i(TAG, "start getCameraEssidPassword");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.ESSID_PASSWORD);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraEssidPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraEssidPassword(String ssidPassword) {
        AppLog.i(TAG, "start setStringPropertyValue date = " + ssidPassword);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.ESSID_PASSWORD, ssidPassword);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraSsid(String ssid) {
        AppLog.i(TAG, "start setCameraSsid date = " + ssid);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_ESSID, ssid);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraName(String cameraName) {
        AppLog.i(TAG, "start setStringPropertyValue cameraName = " + cameraName);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_NAME, cameraName);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCameraName() {
        AppLog.i(TAG, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraInfo(){
        AppLog.i(TAG, "start getCameraInfo");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.USB_PIMA_DCP_SYSTEM_INFO);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraInfo retValue =" + retValue);
        return retValue;
    }

    public String getCameraName(ICatchWificamProperty cameraConfiguration1) {
        AppLog.i(TAG, "start getCameraName");
        String retValue = null;
        try {
            retValue = cameraConfiguration1.getCurrentStringPropertyValue(PropertyId.CAMERA_NAME);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraName retValue =" + retValue);
        return retValue;
    }

    public String getCameraPasswordNew() {
        AppLog.i(TAG, "start getCameraPassword");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPasswordNew(String cameraNamePassword) {
        AppLog.i(TAG, "start setCameraPasswordNew cameraName = " + cameraNamePassword);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_PASSWORD_NEW, cameraNamePassword);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraPasswordNew retValue =" + retValue);
        return retValue;
    }

    public String getCameraSsid() {
        AppLog.i(TAG, "start getCameraSsid date = ");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.CAMERA_ESSID);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraSsid retValue =" + retValue);
        return retValue;
    }

    public boolean setCameraPassword(String password) {
        AppLog.i(TAG, "start setCameraPassword date = " + password);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.CAMERA_PASSWORD, password);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCameraPassword retValue =" + retValue);
        return retValue;
    }

    public String getCameraPassword() {
        AppLog.i(TAG, "start getCameraPassword date = ");
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(PropertyId.CAMERA_PASSWORD);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCameraPassword retValue =" + retValue);
        return retValue;
    }

    public boolean setCaptureDelayMode(int value) {
        AppLog.i(TAG, "start setCaptureDelayMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setPropertyValue(PropertyId.CAPTURE_DELAY_MODE, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setCaptureDelayMode retValue =" + retValue);
        return retValue;
    }

    public int getVideoRecordingTime() {
        AppLog.i(TAG, "start getRecordingTime");
        int retValue = 0;
        try {
            // JIRA ICOM-1608 Begin:Modify by b.jiang C01063 2015-07-20
            // 0xD7F0 -> PropertyId.VIDEO_RECORDING_TIME
            // retValue = cameraConfiguration.getCurrentPropertyValue(0xD7F0);
            retValue = cameraConfiguration.getCurrentPropertyValue(PropertyId.VIDEO_RECORDING_TIME);
            // JIRA ICOM-1608 End:Modify by b.jiang C01063 2015-07-20
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getRecordingTime retValue =" + retValue);
        return retValue;
    }

    public boolean setServiceEssid(String value) {
        AppLog.i(TAG, "start setServiceEssid value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.SERVICE_ESSID, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setServiceEssid retValue =" + retValue);
        return retValue;
    }

    public boolean setServicePassword(String value) {
        AppLog.i(TAG, "start setServicePassword value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(PropertyId.SERVICE_PASSWORD, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setServicePassword retValue =" + retValue);
        return retValue;
    }

    public boolean notifyFwToShareMode(int value) {
        AppLog.i(TAG, "start notifyFwToShareMode value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setPropertyValue(PropertyId.NOTIFY_FW_TO_SHARE_MODE, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end notifyFwToShareMode retValue =" + retValue);
        return retValue;
    }

    public List<Integer> getSupportedPropertyValues(int propertyId) {
        AppLog.i(TAG, "1122 begin getSupportedPropertyValues propertyId =" + propertyId);
//            test
//        if(propertyId == PropertyId.SCREEN_SAVER || propertyId == PropertyId.AUTO_POWER_OFF || propertyId == PropertyId.EXPOSURE_COMPENSATION || propertyId
// == PropertyId.VIDEO_FILE_LENGTH || propertyId== PropertyId.FAST_MOTION_MOVIE){
//            List<Integer> list2 = new LinkedList<Integer>();
//            list2.add(0);
//            list2.add(60);
//            list2.add(120);
//            return list2;
//        }
        List<Integer> list =  new LinkedList<>();;
        try {
            list = cameraConfiguration.getSupportedPropertyValues(propertyId);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getSupportedPropertyValues list.size() =" + list.size());
        return list;
    }

    public int getCurrentPropertyValue(int propertyId) {
        AppLog.i(TAG, "start getCurrentPropertyValue propertyId = " + propertyId);
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentPropertyValue(propertyId);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentPropertyValue retValue =" + retValue);
        return retValue;
    }

    public String getCurrentStringPropertyValue(int propertyId) {
        AppLog.i(TAG, "start getCurrentStringPropertyValue propertyId = " + propertyId);
        String retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStringPropertyValue(propertyId);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setPropertyValue(int propertyId, int value) {
        AppLog.i(TAG, "1122 start setPropertyValue propertyId=" + propertyId + " value=" + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setPropertyValue(propertyId, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setPropertyValue retValue =" + retValue);
        return retValue;
    }

    public boolean setStringPropertyValue(int propertyId, String value) {
        AppLog.i(TAG, "start setStringPropertyValue value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStringPropertyValue(propertyId, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStringPropertyValue retValue =" + retValue);
        return retValue;
    }

    public int getVideoSizeFlow() {
        AppLog.i(TAG, "start getVideoSizeFlow");
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentPropertyValue(PropertyId.VIDEO_SIZE_FLOW);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getVideoSizeFlow retValue =" + retValue);
        return retValue;
    }

    public boolean notifyCameraConnectChnage(int value) {
        AppLog.i(TAG, "start notifyCameraConnectChnage value = " + value);
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setPropertyValue(PropertyId.CAMERA_CONNECT_CHANGE, value);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end notifyCameraConnectChnage retValue =" + retValue);
        return retValue;
    }

    public List<ICatchVideoFormat> getResolutionList() {
        AppLog.i(TAG, "start getResolution");
        List<ICatchVideoFormat> retList = null;
        try {
            retList = cameraConfiguration.getSupportedStreamingInfos();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getResolution retList.size() =" + retList.size());
        return retList;
    }

    public String getBestResolution() {
        AppLog.i(TAG, "start getBestResolution");
        String bestResolution = null;

        List<ICatchVideoFormat> tempList = getResolutionList(cameraConfiguration);
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        Log.d("1111", "getResolutionList() tempList.size() = " + tempList.size());
        int tempWidth = 0;
        int tempHeigth = 0;

        ICatchVideoFormat temp;

        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                if (bestResolution == null) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }
        if (bestResolution != null) {
            return bestResolution;
        }
        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);
            if (temp.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                if (bestResolution == null) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                }

                if (temp.getVideoW() == 640 && temp.getVideoH() == 360) {
                    bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return bestResolution;
                } else if (temp.getVideoW() == 640 && temp.getVideoH() == 480) {
                    if (tempWidth != 640) {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = 640;
                        tempHeigth = 480;
                    }
                } else if (temp.getVideoW() == 720) {
                    if (tempWidth != 640) {
                        if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                        {
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                        {
                            if (tempWidth != 720)
                                bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                            tempWidth = 720;
                            tempHeigth = temp.getVideoH();
                        }
                    }
                } else if (temp.getVideoW() < tempWidth) {
                    if (temp.getVideoW() * 9 == temp.getVideoH() * 16)// 16:9优先
                    {
                        bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    } else if (temp.getVideoW() * 3 == temp.getVideoH() * 4)// 4:3
                    {
                        if (tempWidth != temp.getVideoW())
                            bestResolution = "MJPG?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                        tempWidth = temp.getVideoW();
                        tempHeigth = temp.getVideoH();
                    }
                }
            }
        }

        AppLog.i(TAG, "end getBestResolution");
        return bestResolution;

    }

    public List<ICatchVideoFormat> getResolutionList(ICatchWificamProperty cameraConfiguration) {
        AppLog.i(TAG, "start getResolutionList");
        List<ICatchVideoFormat> retList = null;
        try {
            retList = cameraConfiguration.getSupportedStreamingInfos();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(retList != null && retList.size() >0) {
            for (int ii = 0; ii < retList.size(); ii++) {
                AppLog.i(TAG, " getResolutionList retList.get(" + ii + ")==" + retList.get(ii));
            }
        }
        return retList;
    }

    public String getAppDefaultResolution() {
        AppLog.i(TAG, "start getAppDefaultResolution");
        String appDefaultResolution = null;

        List<ICatchVideoFormat> tempList = getResolutionList(cameraConfiguration);
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        Log.d("1111", "getResolutionList() tempList.size() = " + tempList.size());

        ICatchVideoFormat temp;

        for (int ii = 0; ii < tempList.size(); ii++) {
            temp = tempList.get(ii);

            if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                if (temp.getVideoW() == 1280 && temp.getVideoH() == 720 && temp.getBitrate() == 500000) {
                    appDefaultResolution = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
                    return appDefaultResolution;
                }
            }
        }

        AppLog.i(TAG, "end getAppDefaultResolution");
        return appDefaultResolution;

    }

    public String getFWDefaultResolution() {
        AppLog.i(TAG, "start getFWDefaultResolution");
        String resolution = null;
        ICatchVideoFormat retValue = null;
        try {
            retValue = cameraConfiguration.getCurrentStreamingInfo();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue != null) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                resolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                resolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&";
            }
        }
        AppLog.i(TAG, "end getFWDefaultResolution");
        return resolution;

    }

    public boolean setStreamingInfo(ICatchVideoFormat iCatchVideoFormat) {
        AppLog.i(TAG, "start setStreamingInfo");
        boolean retValue = false;
        try {
            retValue = cameraConfiguration.setStreamingInfo(iCatchVideoFormat);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end setStreamingInfo");
        return retValue;

    }

    public String getCurrentStreamInfo() {
        AppLog.i(TAG, "start getCurrentStreamInfo cameraConfiguration=" + cameraConfiguration);

        ICatchVideoFormat retValue = null;
        String bestResolution = null;
        try {
            retValue = cameraConfiguration.getCurrentStreamingInfo();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCurrentStreamInfo retValue = " + retValue);
        if (retValue == null) {
            return null;
        }
        if (hasFuction(0xd7ae)) {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFps() + "&";
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate() + "&FPS="
                        + retValue.getFps() + "&";
            }
        } else {
            if (retValue.getCodec() == ICatchCodec.ICH_CODEC_H264) {
                bestResolution = "H264?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            } else if (retValue.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
                bestResolution = "MJPG?" + "W=" + retValue.getVideoW() + "&H=" + retValue.getVideoH() + "&BR=" + retValue.getBitrate();
            }
        }


        AppLog.i(TAG, "end getCurrentStreamInfo bestResolution =" + bestResolution);
        return bestResolution;
    }

    public int getPreviewCacheTime() {
        AppLog.i(TAG, "start getPreviewCacheTime");
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getPreviewCacheTime();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            AppLog.e(TAG, "IchDevicePropException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getPreviewCacheTime retValue =" + retValue);
        return retValue;
    }

    public int getCameraTimeLapseVideoSizeListMask() {
        int retValue = 0;
        try {
            retValue = cameraConfiguration.getCurrentPropertyValue(PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchDevicePropException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retValue;
    }

}
