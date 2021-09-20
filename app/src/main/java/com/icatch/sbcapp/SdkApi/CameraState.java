/**
 * Added by zhangyanhu C01012,2014-7-2
 */
package com.icatch.sbcapp.SdkApi;

import com.icatch.wificam.customer.ICatchWificamState;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;

/**
 * Added by zhangyanhu C01012,2014-7-2
 */
public class CameraState {
	private final String tag = "CameraState";
	private static CameraState instance;
	private ICatchWificamState cameraState;

	public static CameraState getInstance() {
		if (instance == null) {
			instance = new CameraState();
		}
		return instance;
	}

	private CameraState() {

	}

	public void initCameraState() {
		cameraState = GlobalInfo.getInstance().getCurrentCamera().getCameraStateClint();
	}

	public boolean isMovieRecording() {
		AppLog.i(tag, "begin isMovieRecording");
		boolean retValue = false;
		try {
			retValue = cameraState.isMovieRecording();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end isMovieRecording retValue=" + retValue);
		return retValue;
	}

	public boolean isTimeLapseVideoOn() {
		AppLog.i(tag, "begin isTimeLapseVideoOn");
		boolean retValue = false;
		try {
			retValue = cameraState.isTimeLapseVideoOn();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end isTimeLapseVideoOn retValue=" + retValue);
		return retValue;
	}

	public boolean isTimeLapseStillOn() {
		AppLog.i(tag, "begin isTimeLapseStillOn");
		boolean retValue = false;
		try {
			retValue = cameraState.isTimeLapseStillOn();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end isTimeLapseStillOn retValue=" + retValue);
		return retValue;
	}

	public boolean isSupportImageAutoDownload() {
		AppLog.i(tag, "begin isSupportImageAutoDownload");
		boolean retValue = false;
		try {
			retValue = cameraState.supportImageAutoDownload();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end isSupportImageAutoDownload = " + retValue);
		return retValue;
	}

	public boolean isStreaming() {
		AppLog.i(tag, "begin isStreaming");
		boolean retValue = false;
		try {
			retValue = cameraState.isStreaming();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}

		AppLog.i(tag, "end isStreaming retValue=" + retValue);
		return retValue;
	}
}
