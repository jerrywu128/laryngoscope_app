/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.sbcapp.SdkApi;

import com.icatch.wificam.customer.ICatchWificamInfo;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class CameraFixedInfo {
	private final String tag = "CameraFixedInfo";
	private static CameraFixedInfo instance;
	private ICatchWificamInfo cameraFixedInfo;

	public static CameraFixedInfo getInstance() {
		if (instance == null) {
			instance = new CameraFixedInfo();
		}
		return instance;
	}

	private CameraFixedInfo() {

	}

	public void initCameraFixedInfo() {
		cameraFixedInfo = GlobalInfo.getInstance().getCurrentCamera().getCameraInfoClint();
	}

	public String getCameraName() {
		AppLog.i(tag, "begin getCameraName");
		String name = "";
		try {
			name = cameraFixedInfo.getCameraProductName();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end getCameraName name =" + name);
		return name;
	}

	public String getCameraVersion() {
		AppLog.i(tag, "begin getCameraVersion");
		String version = "";
		try {
			version = cameraFixedInfo.getCameraFWVersion();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end getCameraVersion version =" + version);
		return version;
	}
}
