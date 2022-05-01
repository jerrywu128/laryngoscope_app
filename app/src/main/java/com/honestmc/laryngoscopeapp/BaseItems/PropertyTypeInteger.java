package com.honestmc.laryngoscopeapp.BaseItems;

import android.content.Context;
import android.content.res.Resources;

import com.honestmc.laryngoscopeapp.Beans.ItemInfo;
import com.honestmc.laryngoscopeapp.CustomException.NullPointerException;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Hash.PropertyHashMapDynamic;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Mode.PreviewMode;
import com.honestmc.laryngoscopeapp.PropertyId.PropertyId;
import com.honestmc.laryngoscopeapp.SdkApi.CameraProperties;
import com.icatch.wificam.customer.type.ICatchCameraProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyTypeInteger {

    private static final String TAG = "PropertyTypeInteger";
    private HashMap<Integer, ItemInfo> hashMap;
    private int propertyId;
    private String[] valueListString;
    private List<Integer> valueListInt;
    private Context context;
    private Resources res;

    public PropertyTypeInteger(HashMap<Integer, ItemInfo> hashMap, int propertyId, Context context) {
        this.hashMap = hashMap;
        this.propertyId = propertyId;
        this.context = context;
        initItem();
    }

    public PropertyTypeInteger(int propertyId, Context context) {
        this.propertyId = propertyId;
        this.context = context;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            hashMap = PropertyHashMapDynamic.getInstance().getDynamicHashInt(propertyId);
        }
        res = context.getResources();

        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                valueListInt = CameraProperties.getInstance().getSupportedWhiteBalances();
                break;
            case PropertyId.CAPTURE_DELAY:
                valueListInt = CameraProperties.getInstance().getSupportedCaptureDelays();
                break;
            case PropertyId.BURST_NUMBER:

                valueListInt = CameraProperties.getInstance().getsupportedBurstNums();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                valueListInt = CameraProperties.getInstance().getSupportedLightFrequencys();
                break;
            case PropertyId.DATE_STAMP:
                valueListInt = CameraProperties.getInstance().getsupportedDateStamps();
                break;
            case PropertyId.UP_SIDE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(Upside.UPSIDE_OFF);
                valueListInt.add(Upside.UPSIDE_ON);
                break;
            case PropertyId.CAMERA_SWITCH:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(CameraSwitch.CAMERA_FRONT);
                valueListInt.add(CameraSwitch.CAMERA_BACK);
                break;

            case PropertyId.SLOW_MOTION:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(SlowMotion.SLOW_MOTION_OFF);
                valueListInt.add(SlowMotion.SLOW_MOTION_ON);
                break;
            case PropertyId.TIMELAPSE_MODE:
                valueListInt = new ArrayList<Integer>();
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_STILL);
                valueListInt.add(TimeLapseMode.TIME_LAPSE_MODE_VIDEO);
                break;
            default:
                valueListInt = CameraProperties.getInstance().getSupportedPropertyValues(propertyId);
                break;
        }
        valueListString = new String[valueListInt.size()];
        if (valueListInt != null && valueListInt.size() > 0) {
            for (int ii = 0; ii < valueListInt.size(); ii++) {
                if (hashMap.get(valueListInt.get(ii)) == null) {
                    continue;
                }

                String uiStringInSettingString = hashMap.get(valueListInt.get(ii)).uiStringInSettingString;
                if (uiStringInSettingString != null) {
                    valueListString[ii] = uiStringInSettingString;
                    AppLog.d(TAG, "propertyId=" + propertyId + " uiStringInSettingString=" + uiStringInSettingString);
//                if (propertyId == ICatchCameraProperty.ICH_CAP_CAPTURE_DELAY) {
//                    valueListString[ii] = hashMap.get(valueListInt.get(ii)).uiStringInSettingString;
                } else {
                    valueListString[ii] = res.getString(hashMap.get(valueListInt.get(ii)).uiStringInSetting);
                }
            }
        }
    }

    public int getCurrentValue() {
        // TODO Auto-generated method stub
        int retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = CameraProperties.getInstance().getCurrentWhiteBalance();
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = CameraProperties.getInstance().getCurrentCaptureDelay();
                break;
            case PropertyId.BURST_NUMBER:
                retValue = CameraProperties.getInstance().getCurrentBurstNum();
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = CameraProperties.getInstance().getCurrentLightFrequency();
                break;
            case PropertyId.DATE_STAMP:
                retValue = CameraProperties.getInstance().getCurrentDateStamp();
                break;
            case PropertyId.UP_SIDE:
                retValue = CameraProperties.getInstance().getCurrentUpsideDown();
                break;
            case PropertyId.CAMERA_SWITCH:
                retValue = CameraProperties.getInstance().getCurrentCameraSwitch();
                break;
            case PropertyId.SLOW_MOTION:
                retValue = CameraProperties.getInstance().getCurrentSlowMotion();
                break;
            case PropertyId.TIMELAPSE_MODE:
                retValue = GlobalInfo.getInstance().getCurrentCamera().timeLapsePreviewMode;
                break;
            default:
                retValue = CameraProperties.getInstance().getCurrentPropertyValue(propertyId);
                break;
        }
        AppLog.d(TAG, "getCurrentValue retValue=" + retValue);
        return retValue;
    }

    public String getCurrentUiStringInSetting() {
        // TODO Auto-generated method stub
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        AppLog.d(TAG, "propertyId=" + propertyId + " itemInfo=" + itemInfo);
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = res.getString(itemInfo.uiStringInSetting);
        }
        return ret;
    }

    public String getCurrentUiStringInPreview() {
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInPreview;
        }
        // TODO Auto-generated method stub
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListString[position];
    }

    public int getCurrentIcon() throws NullPointerException {
        // TODO Auto-generated method stub
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        AppLog.d(TAG, "itemInfo=" + itemInfo);
        if (itemInfo == null) {
            throw new NullPointerException(TAG, "getCurrentIcon itemInfo is null", "");
        }
        return itemInfo.iconID;
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(int value) {
        // TODO Auto-generated method stub
        boolean retValue;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = CameraProperties.getInstance().setWhiteBalance(value);
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = CameraProperties.getInstance().setCaptureDelay(value);
                break;
            case PropertyId.BURST_NUMBER:
                retValue = CameraProperties.getInstance().setCurrentBurst(value);
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = CameraProperties.getInstance().setLightFrequency(value);
                break;
            case PropertyId.DATE_STAMP:
                retValue = CameraProperties.getInstance().setDateStamp(value);
                break;
            default:
                retValue = CameraProperties.getInstance().setPropertyValue(propertyId, value);
                break;
        }
        return retValue;
    }

    public Boolean setValueByPosition(int position) {
        // TODO Auto-generated method stub

        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                retValue = CameraProperties.getInstance().setWhiteBalance(valueListInt.get(position));
                break;
            case PropertyId.CAPTURE_DELAY:
                retValue = CameraProperties.getInstance().setCaptureDelay(valueListInt.get(position));
                break;
            case PropertyId.BURST_NUMBER:
                retValue = CameraProperties.getInstance().setCurrentBurst(valueListInt.get(position));
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = CameraProperties.getInstance().setLightFrequency(valueListInt.get(position));
                break;
            case PropertyId.DATE_STAMP:
                retValue = CameraProperties.getInstance().setDateStamp(valueListInt.get(position));
                break;
            case PropertyId.UP_SIDE:
                retValue = CameraProperties.getInstance().setUpsideDown(valueListInt.get(position));
                break;
            case PropertyId.CAMERA_SWITCH:
                retValue = CameraProperties.getInstance().setCameraSwitch(valueListInt.get(position));
                break;
            case PropertyId.SLOW_MOTION:
                retValue = CameraProperties.getInstance().setSlowMotion(valueListInt.get(position));
                break;
            default:
                retValue = CameraProperties.getInstance().setPropertyValue(propertyId, valueListInt.get(position));
                break;
        }
        return retValue;
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.WHITE_BALANCE:
                //retValue = CameraProperties.getInstance().setWhiteBalance(valueListInt.get(position));
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_WHITE_BALANCE)) {
                    retValue = true;
                    break;
                }
                retValue = true;
                break;
            case PropertyId.CAPTURE_DELAY:
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_IMAGE_SIZE) &&
                        CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_CAPTURE_DELAY) && //IC-564
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.BURST_NUMBER:
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_BURST_NUMBER) &&
                        previewMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
                    retValue = true;
                    break;
                }
                break;
            case PropertyId.LIGHT_FREQUENCY:
                retValue = true;
                break;
            case PropertyId.DATE_STAMP:
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_DATE_STAMP)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW || previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.UP_SIDE:
                if (CameraProperties.getInstance().hasFuction(PropertyId.UP_SIDE)) {
                    return true;
                }
                break;
            case PropertyId.CAMERA_SWITCH:
                if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_SWITCH)) {
                    if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = false;
                        break;
                    } else {
                        retValue = true;
                    }
                }
                break;
            case PropertyId.SLOW_MOTION:
                if (CameraProperties.getInstance().hasFuction(PropertyId.SLOW_MOTION) &&
                        (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                                previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE)) {
                    retValue = true;
                    break;
                }
                break;

            case PropertyId.TIMELAPSE_MODE:
                AppLog.i(TAG, "TIMELAPSE_MODE has this fucntion! =" + CameraProperties.getInstance().hasFuction(PropertyId.TIMELAPSE_MODE));
                if (CameraProperties.getInstance().hasFuction(PropertyId.TIMELAPSE_MODE)) {
                    AppLog.i(TAG, "TIMELAPSE_MODE has this fucntion!");
                    if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                    break;
                }
                break;
            default:
                break;
        }
        return retValue;
    }

}
