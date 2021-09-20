package com.icatch.sbcapp.BaseItems;

import android.content.Context;
import android.util.Log;

import com.icatch.sbcapp.Beans.ItemInfo;
import com.icatch.sbcapp.Hash.PropertyHashMapDynamic;
import com.icatch.sbcapp.Hash.VideoSizeStaticHashMap;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.PreviewMode;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.wificam.customer.type.ICatchCameraProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PropertyTypeString {
    private String TAG = PropertyTypeString.class.getSimpleName();
    private int propertyId;
    private List<String> valueListString;
    private List<String> valueListStringUI;
    private HashMap<String, ItemInfo> hashMap;
    private String[] valueArrayString;

    public PropertyTypeString(int propertyId, Context context) {
        this.propertyId = propertyId;
        initItem();
    }

    public void initItem() {
        // TODO Auto-generated method stub
        if (hashMap == null) {
            if (propertyId == PropertyId.VIDEO_SIZE || propertyId == PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) {
                hashMap = VideoSizeStaticHashMap.videoSizeMap;
                Log.i("2333", "get VideoSizeStaticHashMap");
            } else if (propertyId == PropertyId.IMAGE_SIZE) {
                hashMap = PropertyHashMapDynamic.getInstance().getImageSizeMap();
            }
        }
        if (hashMap == null) {
            return;
        }
        if (propertyId == PropertyId.IMAGE_SIZE) {
            valueListString = CameraProperties.getInstance().getSupportedImageSizes();
        }
        if (propertyId == PropertyId.VIDEO_SIZE) {
            valueListString = CameraProperties.getInstance().getSupportedVideoSizes();
        }
        //JIRA ICOM-2246 Begin Add by b.jiang 2015-12-04
        if (propertyId == PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) {
            valueListString = CameraProperties.getInstance().getSupportedVideoSizes();
            AppLog.d(TAG, " before valueListString.size = " + valueListString.size());
            Log.d("TigerTiger", "before valueListString.size = " + valueListString.size());
            // Check support property : TIMELAPSE_VIDEO_SIZE_LIST_MASK
            int intTimeLapseVideoSizeListMask = 0;
            if (CameraProperties.getInstance().hasFuction(PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) == true) {
                intTimeLapseVideoSizeListMask = CameraProperties.getInstance().getCameraTimeLapseVideoSizeListMask();
                AppLog.d(TAG, " getCameraTimeLapseVideoSizeListMask = " + intTimeLapseVideoSizeListMask);
                for (int ii = 0; ii < valueListString.size(); ii++) {
                    if (checkWithTimeLapseMask(intTimeLapseVideoSizeListMask, ii) == false) {
                        valueListString.remove(ii);
                        ii--;
                        intTimeLapseVideoSizeListMask = intTimeLapseVideoSizeListMask >> 1;
                    }
                }
            }
        }

        //JIRA ICOM-2246 End Add by b.jiang 2015-12-04
        for (int ii = 0; ii < valueListString.size(); ii++) {
            if (hashMap.containsKey(valueListString.get(ii)) == false) {
                valueListString.remove(ii);
                ii--;
            }
        }
        valueListStringUI = new LinkedList<String>();
        valueArrayString = new String[valueListString.size()];
        if (valueListString != null) {
            for (int ii = 0; ii < valueListString.size(); ii++) {
                valueListStringUI.add(ii, hashMap.get(valueListString.get(ii)).uiStringInSettingString);
                valueArrayString[ii] = hashMap.get(valueListString.get(ii)).uiStringInSettingString;
            }
        }
    }

    //JIRA ICOM-2246 Begin Add by b.jiang 2015-12-04
    private Boolean checkWithTimeLapseMask(int TimeLapseMaskValue, int index) {
        int intShiftValue = 0x1;
        int intMatchValue = 0;
        AppLog.d(TAG, "start ceckWithTimeLapseMask");
        AppLog.d(TAG, "TimeLapseMaskValue = " + TimeLapseMaskValue);
        AppLog.d(TAG, "index = " + index);
        if (index > 0) {
            intShiftValue = intShiftValue << index;
        }
        AppLog.d(TAG, "intShiftValue = " + intShiftValue);
        intMatchValue = TimeLapseMaskValue & intShiftValue;
        AppLog.d(TAG, "intMatchValue = " + intMatchValue);
        if (intMatchValue > 0) {
            AppLog.d(TAG, "end ceckWithTimeLapseMask = true ");
            return true;
        } else {
            AppLog.d(TAG, "end ceckWithTimeLapseMask = faluse ");
            return false;
        }
    }

    public String getCurrentValue() {
        // TODO Auto-generated method stub
        String curValue = "";
        if (propertyId == PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) {
            curValue = CameraProperties.getInstance().getCurrentStringPropertyValue(PropertyId.VIDEO_SIZE);
        } else {
            curValue = CameraProperties.getInstance().getCurrentStringPropertyValue(propertyId);
        }
        return curValue;
    }

    public String getCurrentUiStringInSetting() {
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInSettingString;
        }
        return ret;
    }

    public String getCurrentUiStringInPreview() {
        // TODO Auto-generated method stub
        ItemInfo itemInfo = hashMap.get(getCurrentValue());
        String ret = null;
        if (itemInfo == null) {
            ret = "Unknown";
        } else {
            ret = itemInfo.uiStringInPreview;
        }
        return ret;
    }

    public String getCurrentUiStringInSetting(int position) {
        // TODO Auto-generated method stub
        return valueListString.get(position);
    }

    public List<String> getValueList() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public List<String> getValueListUI() {
        // TODO Auto-generated method stub
        return valueListString;
    }

    public Boolean setValue(String value) {
        //JIRA ICOM-2246 Begin Add by b.jiang 2015-12-04
        if (propertyId == PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) {
            return CameraProperties.getInstance().setStringPropertyValue(PropertyId.VIDEO_SIZE, value);
        } else {
            return CameraProperties.getInstance().setStringPropertyValue(propertyId, value);
        }
        //return CameraProperties.getInstance().setStringPropertyValue( propertyId, value);
        //JIRA ICOM-2246 End Add by b.jiang 2015-12-04
    }

    public boolean setValueByPosition(int position) {
        if (propertyId == PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK) {
            return CameraProperties.getInstance().setStringPropertyValue(PropertyId.VIDEO_SIZE,
                    valueListString.get(position));
        } else {
            return CameraProperties.getInstance().setStringPropertyValue(propertyId,
                    valueListString.get(position));
        }
        //return CameraProperties.getInstance().setStringPropertyValue( propertyId,valueListString.get(position));
        //JIRA ICOM-2246 Begin Add by b.jiang 2015-12-04
    }

    public String[] getValueArrayString() {
        return valueArrayString;
    }

    public Boolean needDisplayByMode(int previewMode) {
        boolean retValue = false;
        switch (propertyId) {
            case PropertyId.IMAGE_SIZE:
                //retValue = CameraProperties.getInstance().setWhiteBalance(valueListInt.get(position));
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_IMAGE_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            case PropertyId.TIMELAPSE_VIDEO_SIZE_LIST_MASK:
            case PropertyId.VIDEO_SIZE:
                if (CameraProperties.getInstance().hasFuction(ICatchCameraProperty.ICH_CAP_VIDEO_SIZE)) {
                    if (previewMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                            previewMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        retValue = true;
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return retValue;
    }
}
