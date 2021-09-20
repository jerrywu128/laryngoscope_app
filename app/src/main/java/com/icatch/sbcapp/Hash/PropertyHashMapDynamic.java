package com.icatch.sbcapp.Hash;

import com.icatch.sbcapp.Beans.ItemInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.Tools.ConvertTools;
import com.icatch.wificam.customer.ICatchWificamUtil;
import com.icatch.wificam.customer.exception.IchInvalidArgumentException;
import com.icatch.wificam.customer.type.ICatchImageSize;
import com.icatch.wificam.customer.type.ICatchVideoSize;

import java.util.HashMap;
import java.util.List;

public class PropertyHashMapDynamic {
    private final String tag = "PropertyHashMapDynamic";
    private static PropertyHashMapDynamic propertyHashMap;

    public static PropertyHashMapDynamic getInstance() {
        if (propertyHashMap == null) {
            propertyHashMap = new PropertyHashMapDynamic();
        }
        return propertyHashMap;
    }

    public HashMap<Integer, ItemInfo> getDynamicHashInt(int propertyId) {
        switch (propertyId) {
            case PropertyId.CAPTURE_DELAY:
                return getCaptureDelayMap();
            case PropertyId.AUTO_POWER_OFF:
                return getAutoPowerOffMap();
            case PropertyId.EXPOSURE_COMPENSATION:
                return getExposureCompensationMap();
            case PropertyId.VIDEO_FILE_LENGTH:
                return getVideoFileLengthMap();
            case PropertyId.FAST_MOTION_MOVIE:
                return getFastMotionMovieMap();
            case PropertyId.SCREEN_SAVER:
                return getScreenSaverMap();
            default:
                return null;
        }
    }

    private HashMap<Integer, ItemInfo> getScreenSaverMap() {
        HashMap<Integer, ItemInfo> screenSaverMap = new HashMap<Integer, ItemInfo>();
        List<Integer> screenSaverList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.SCREEN_SAVER);
        String temp;
        for (int ii = 0; ii < screenSaverList.size(); ii++) {
            int value = screenSaverList.get(ii);
            if (value == 0) {
                temp = "OFF";
            } else {
                temp = value + "S";
            }
            AppLog.d(tag, "screenSaverList ii=" + ii + " value=" + value);
            screenSaverMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return screenSaverMap;
    }

    private HashMap<Integer, ItemInfo> getAutoPowerOffMap() {
        HashMap<Integer, ItemInfo> autoPowerOffMap = new HashMap<Integer, ItemInfo>();

        List<Integer> autoPowerOffList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.AUTO_POWER_OFF);
        String temp;
        for (int ii = 0; ii < autoPowerOffList.size(); ii++) {
            int value = autoPowerOffList.get(ii);
            if (value == 0) {
                temp = "OFF";
            } else {
                temp = value + "S";
            }
            AppLog.d(tag, "autoPowerOffList ii=" + ii + " value=" + value);
            autoPowerOffMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return autoPowerOffMap;
    }

    private HashMap<Integer, ItemInfo> getExposureCompensationMap() {
        HashMap<Integer, ItemInfo> exposureCompensationMap = new HashMap<Integer, ItemInfo>();
        List<Integer> exposureCompensationList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.EXPOSURE_COMPENSATION);
//        String temp;
        for (int ii = 0; ii < exposureCompensationList.size(); ii++) {
            int value = exposureCompensationList.get(ii);
            String temp = ConvertTools.getExposureCompensation(value);

            AppLog.d(tag, "exposureCompensationList ii=" + ii + " value=" + value + " temp=" + temp);
            exposureCompensationMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return exposureCompensationMap;
    }

    private HashMap<Integer, ItemInfo> getVideoFileLengthMap() {
        HashMap<Integer, ItemInfo> videoFileLengthMap = new HashMap<Integer, ItemInfo>();
        List<Integer> videoFileLengthList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.VIDEO_FILE_LENGTH);
        String temp;
        for (int ii = 0; ii < videoFileLengthList.size(); ii++) {
            int value = videoFileLengthList.get(ii);
            if (value == 0) {
                temp = "OFF";
            } else {
                temp = value + "s";
            }
            AppLog.d(tag, "videoFileLengthList ii=" + ii + " value=" + value);
            videoFileLengthMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return videoFileLengthMap;
    }

    private HashMap<Integer, ItemInfo> getFastMotionMovieMap() {
        HashMap<Integer, ItemInfo> fastMotionMovieMap = new HashMap<Integer, ItemInfo>();
        List<Integer> fastMotionMovieList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.FAST_MOTION_MOVIE);
        String temp;
        for (int ii = 0; ii < fastMotionMovieList.size(); ii++) {
            int value = fastMotionMovieList.get(ii);
            if (value == 0) {
                temp = "OFF";
            } else {
                temp = value + "x";
            }
            AppLog.d(tag, "fastMotionMovieList ii=" + ii + " value=" + value);
            fastMotionMovieMap.put(value, new ItemInfo(temp, temp, 0));
        }
        return fastMotionMovieMap;
    }

    private HashMap<Integer, ItemInfo> getCaptureDelayMap() {
        HashMap<Integer, ItemInfo> captureDelayMap = new HashMap<Integer, ItemInfo>();
        List<Integer> delyaList = CameraProperties.getInstance().getSupportedPropertyValues(PropertyId.CAPTURE_DELAY);
        String temp;
        for (int ii = 0; ii < delyaList.size(); ii++) {
            if (delyaList.get(ii) == 0) {
                temp = "OFF";
            } else {
                temp = delyaList.get(ii) / 1000 + "S";
            }
            AppLog.d(tag, "delyaList.get(ii) ==" + delyaList.get(ii));
            captureDelayMap.put(delyaList.get(ii), new ItemInfo(temp, temp, 0));
        }
        return captureDelayMap;
    }

    public HashMap<String, ItemInfo> getImageSizeMap() {
        AppLog.i(tag, "begin initImageSizeMap");
        HashMap<String, ItemInfo> imageSizeMap = new HashMap<String, ItemInfo>();
        List<String> imageSizeList = null;
        imageSizeList = CameraProperties.getInstance().getSupportedImageSizes();
        List<Integer> convertImageSizeList = null;
        try {
            convertImageSizeList = ICatchWificamUtil.convertImageSizes(imageSizeList);
        } catch (IchInvalidArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String temp = "Undefined";
        String temp1 = "Undefined";
        for (int ii = 0; ii < imageSizeList.size(); ii++) {
            if (convertImageSizeList.get(ii) == ICatchImageSize.ICH_IMAGE_SIZE_VGA) {
                temp = "VGA" + "(" + imageSizeList.get(ii) + ")";
                imageSizeMap.put(imageSizeList.get(ii), new ItemInfo(temp, "VGA", 0));
            } else {
                temp = convertImageSizeList.get(ii) + "M" + "(" + imageSizeList.get(ii) + ")";
                temp1 = convertImageSizeList.get(ii) + "M";
                imageSizeMap.put(imageSizeList.get(ii), new ItemInfo(temp, temp1, 0));
            }
            AppLog.i(tag, "imageSize =" + temp);
        }
        AppLog.i(tag, "end initImageSizeMap imageSizeMap =" + imageSizeMap.size());
        return imageSizeMap;
    }
}
