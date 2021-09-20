package com.icatch.sbcapp.BaseItems;

import android.util.Log;

import com.icatch.wificam.customer.type.ICatchCodec;
import com.icatch.wificam.customer.type.ICatchVideoFormat;
import com.icatch.sbcapp.SdkApi.CameraProperties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StreamResolution {
	private String[] valueArrayStringUI;
	private HashMap<String, String> hashMap;
	private String[] valueArrayString;
	private String[] resolutionStrategyList;
	private ICatchVideoFormat[] resolutionVideoFormatsList;
	public static String currentResolutionCmd = null;
	public static ICatchVideoFormat currentResolutionVideoFormat = null;

	public StreamResolution() {
		initItem();
	}

	public void initItem() {
		// TODO Auto-generated method stub
		currentResolutionCmd = null;
		hashMap = new HashMap<String, String>();
		List<ICatchVideoFormat> tempList = CameraProperties.getInstance().getResolutionList();
		// "H264?W=640&H=360&Q=50&BR=3000000&"
		valueArrayStringUI = new String[tempList.size()];
		valueArrayString = new String[tempList.size()];
		ICatchVideoFormat temp;
		for (int ii = 0; ii < tempList.size(); ii++) {
			temp = tempList.get(ii);
			if (temp.getCodec() == ICatchCodec.ICH_CODEC_JPEG) {
				valueArrayStringUI[ii] = "MJPG/";
				valueArrayString[ii] = "MJPG?";
			} else if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
				valueArrayStringUI[ii] = "H264/";
				valueArrayString[ii] = "H264?";
			}
			valueArrayStringUI[ii] = valueArrayStringUI[ii] + temp.getVideoW() + " X " + temp.getVideoH() + "/";
			valueArrayString[ii] = valueArrayString[ii] + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH();
			valueArrayStringUI[ii] = valueArrayStringUI[ii] + temp.getBitrate();
			valueArrayString[ii] = valueArrayString[ii] + "&BR=" + temp.getBitrate() + "&";
			Log.d("1111","valueArrayString[ii] ="+valueArrayString[ii]);
			hashMap.put(valueArrayStringUI[ii], valueArrayString[ii]);
		}

		initStreamStrategy();
	}

	public String getCurrentValue() {
		return currentResolutionCmd;
	}

	public String getValueByPositon(int position) {
		return valueArrayString[position];
	}

	public String[] getValueList() {
		return valueArrayStringUI;
	}

	public int getCurrentPositon() {
		int position = 0;
		for (int ii = 0; ii < valueArrayString.length; ii++) {
			if (valueArrayString[ii].equals(getCurrentValue()) == true) {
				position = ii;
				break;
			}
		}
		return position;
	}

	public void initStreamStrategy() {
		List<ICatchVideoFormat> tempList = CameraProperties.getInstance().getResolutionList();
		List<ICatchVideoFormat> tempList1 = new LinkedList<ICatchVideoFormat>();
		for (ICatchVideoFormat temp : tempList) {
			if (temp.getCodec() == ICatchCodec.ICH_CODEC_H264) {
				tempList1.add(temp);
			}
		}
		resolutionStrategyList = new String[tempList1.size()];
		resolutionVideoFormatsList = new ICatchVideoFormat[tempList1.size()];
		for (int ii = 0; ii < tempList1.size(); ii++) {
			ICatchVideoFormat temp = tempList1.get(ii);
			resolutionStrategyList[ii] = "H264?" + "W=" + temp.getVideoW() + "&H=" + temp.getVideoH() + "&BR=" + temp.getBitrate() + "&";
			resolutionVideoFormatsList[ii] = temp;
		}
	}

	public String getAutoChangeStream() {
		if (currentResolutionCmd == null) {
			return null;
		}
		for (int ii = 0; ii < resolutionStrategyList.length; ii++) {
			if (currentResolutionCmd.equals(resolutionStrategyList[ii])) {
				if (ii != (resolutionStrategyList.length - 1)) {
					return resolutionStrategyList[ii + 1];
				}
			}
		}
		return null;
	}

	public String getStreamDown() {
		if (currentResolutionCmd == null) {
			return null;
		}
		for (int ii = 0; ii < resolutionStrategyList.length; ii++) {
			if (currentResolutionCmd.equals(resolutionStrategyList[ii])) {
				if (ii != (resolutionStrategyList.length - 1)) {
					return resolutionStrategyList[ii + 1];
				}
			}
		}
		return null;
	}

	public String getStreamUp() {
		if (currentResolutionCmd == null) {
			return null;
		}
		for (int ii = 0; ii < resolutionStrategyList.length; ii++) {
			if (currentResolutionCmd.equals(resolutionStrategyList[ii])) {
				if (ii != 0) {
					return resolutionStrategyList[ii - 1];
				}
			}
		}
		return null;
	}

	public ICatchVideoFormat getStreamDownVideoFormat() {
		if (currentResolutionCmd == null) {
			return null;
		}
		for (int ii = 0; ii < resolutionStrategyList.length; ii++) {
			if (currentResolutionCmd.equals(resolutionStrategyList[ii])) {
				if (ii != (resolutionStrategyList.length - 1)) {
					return resolutionVideoFormatsList[ii + 1];
				}
			}
		}
		return null;
	}

	public ICatchVideoFormat getStreamUpVideoFormat() {
		if (currentResolutionCmd == null) {
			return null;
		}
		for (int ii = 0; ii < resolutionStrategyList.length; ii++) {
			if (currentResolutionCmd.equals(resolutionStrategyList[ii])) {
				if (ii != 0) {
					return resolutionVideoFormatsList[ii - 1];
				}
			}
		}
		return null;
	}
}
