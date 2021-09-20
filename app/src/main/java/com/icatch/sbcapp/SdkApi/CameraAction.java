/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.sbcapp.SdkApi;

import android.util.Log;

import com.icatch.wificam.customer.ICatchWificamAssist;
import com.icatch.wificam.customer.ICatchWificamControl;
import com.icatch.wificam.customer.ICatchWificamListener;
import com.icatch.wificam.customer.ICatchWificamSession;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchCaptureImageException;
import com.icatch.wificam.customer.exception.IchDeviceException;
import com.icatch.wificam.customer.exception.IchDevicePropException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchListenerExistsException;
import com.icatch.wificam.customer.exception.IchListenerNotExistsException;
import com.icatch.wificam.customer.exception.IchNotSupportedException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.exception.IchStorageFormatException;
import com.icatch.wificam.customer.exception.IchTimeOutException;
import com.icatch.wificam.customer.type.ICatchEventID;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Model.Implement.SDKSession;

public class CameraAction {
	private final String tag ="CameraAction";
	private static CameraAction instance;
	private ICatchWificamControl cameraAction;
	public ICatchWificamAssist cameraAssist;

	public static CameraAction getInstance() {
		if (instance == null) {
			instance = new CameraAction();
		}
		return instance;
	}

	private CameraAction() {

	}

	public void initCameraAction() {
		Log.d("1111", "GlobalInfo.getInstance().getCurrentCamera() =" + GlobalInfo.getInstance().getCurrentCamera());
		cameraAction = GlobalInfo.getInstance().getCurrentCamera().getcameraActionClient();
		cameraAssist = GlobalInfo.getInstance().getCurrentCamera().getCameraAssistClint();
	}

	//用于本地vidoe播放对cameraAction的初始化;
	public void initCameraAction(ICatchWificamControl myWificamControl) {
		this.cameraAction = myWificamControl;
	}

	public boolean triggerCapturePhoto() {
		AppLog.i(tag, "begin triggerCapturePhoto");
		boolean ret = false;
		try {
			ret = cameraAction.triggerCapturePhoto();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCaptureImageException e) {
			AppLog.e(tag, "IchCaptureImageException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end triggerCapturePhoto ret = " + ret);
		return ret;
	}

	public boolean capturePhoto() {
		AppLog.i(tag, "begin doStillCapture");
		boolean ret = false;
		try {
			ret = cameraAction.capturePhoto();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCaptureImageException e) {
			AppLog.e(tag, "IchCaptureImageException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end doStillCapture ret = " + ret);
		return ret;
	}

	public boolean startMovieRecord() {
		AppLog.i(tag, "begin startVideoCapture");
		boolean ret = false;

		try {
			ret = cameraAction.startMovieRecord();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end startVideoCapture ret =" + ret);
		return ret;
	}

	public boolean startTimeLapse() {
		AppLog.i(tag, "begin startTimeLapse");
		boolean ret = false;

		try {
			ret = cameraAction.startTimeLapse();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end startTimeLapse ret =" + ret);
		return ret;
	}

	public boolean stopTimeLapse() {
		AppLog.i(tag, "begin stopMovieRecordTimeLapse");
		boolean ret = false;

		try {
			ret = cameraAction.stopTimeLapse();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end stopMovieRecordTimeLapse ret =" + ret);
		return ret;
	}

	public boolean stopVideoCapture() {
		AppLog.i(tag, "begin stopVideoCapture");
		boolean ret = false;

		try {
			ret = cameraAction.stopMovieRecord();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end stopVideoCapture ret =" + ret);
		return ret;
	}

	public boolean formatStorage() {
		AppLog.i(tag, "begin formatSD");
		boolean retVal = false;

		try {
			retVal = cameraAction.formatStorage();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchStorageFormatException e) {
			AppLog.e(tag, "IchStorageFormatException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "begin formatSD retVal =" + retVal);
		return retVal;
	}

	public boolean sleepCamera() {
		AppLog.i(tag, "begin sleepCamera");
		boolean retValue = false;
		try {
			try {
				retValue = cameraAction.toStandbyMode();
			} catch (IchDeviceException e) {
				// TODO Auto-generated catch block
				AppLog.e(tag, "IchDeviceException");
				e.printStackTrace();
			} catch (IchInvalidSessionException e) {
				AppLog.e(tag, "IchInvalidSessionException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end sleepCamera retValue =" + retValue);
		return retValue;
	}

	public boolean addCustomEventListener(int eventID, ICatchWificamListener listener) {
		AppLog.i(tag, "begin addEventListener eventID=" + eventID);
		boolean retValue = false;
		try {
			retValue = cameraAction.addCustomEventListener(eventID, listener);
		} catch (IchListenerExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AppLog.i(tag, "end addEventListener retValue = " + retValue);
		return retValue;
	}

	public boolean delCustomEventListener(int eventID, ICatchWificamListener listener) {
		AppLog.i(tag, "begin delEventListener eventID=" + eventID);
		boolean retValue = false;
		try {
			retValue = cameraAction.delCustomEventListener(eventID, listener);
		} catch (IchListenerNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end delEventListener retValue = " + retValue);
		return retValue;
	}
	
	public boolean addEventListener(int eventID,ICatchWificamListener listener){
		AppLog.i(tag, "begin addEventListener eventID=" + eventID);

		boolean retValue = false;
		try {

			retValue = cameraAction.addEventListener(eventID, listener);

		} catch (IchListenerExistsException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchListenerExistsException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end addEventListener retValue = " + retValue);
		return retValue;
	}

	public boolean delEventListener(int eventID, ICatchWificamListener listener) {
		AppLog.i(tag, "begin delEventListener eventID=" + eventID);
		boolean retValue = false;
		try {
			retValue = cameraAction.delEventListener(eventID, listener);
		} catch (IchListenerNotExistsException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchListenerExistsException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end delEventListener retValue = " + retValue);
		return retValue;
	}

	public static boolean addScanEventListener(ICatchWificamListener listener) {
		if(listener == null){
			return false;
		}
		boolean retValue = false;
		try {
			retValue = ICatchWificamSession.addEventListener(ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, listener,false);
		} catch (IchListenerExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}

	public static boolean delScanEventListener(ICatchWificamListener listener) {
		if(listener == null){
			return true;
		}
		boolean retValue = false;
		try {
			retValue = ICatchWificamSession.delEventListener(ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, listener,false);
		} catch (IchListenerNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}

	/**
	 * Added by zhangyanhu C01012,2014-7-2
	 */
	public String getCameraMacAddress() {
		// TODO Auto-generated method stub
		String macAddress = "";
		macAddress = cameraAction.getMacAddress();
		return macAddress;
	}

	public boolean zoomIn() {
		AppLog.i(tag, "begin zoomIn");
		boolean retValue = false;
		try {
			retValue = cameraAction.zoomIn();
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchStorageFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end zoomIn retValue = " + retValue);
		return retValue;
	}

	public boolean zoomOut() {
		AppLog.i(tag, "begin zoomOut");
		boolean retValue = false;
		try {
			retValue = cameraAction.zoomOut();
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchStorageFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end zoomOut retValue = " + retValue);
		return retValue;
	}

	public boolean updateFW(String fileName) {
		boolean ret = false;
		AppLog.i(tag, "begin update FW");
		SDKSession mSDKSession = GlobalInfo.getInstance().getCurrentCamera().getSDKsession();
		try {
			ret = cameraAssist.updateFw(mSDKSession.getSDKSession(), fileName);
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchDevicePropException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDevicePropException");
			e.printStackTrace();
		} catch (IchTimeOutException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchTimeOutException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		} catch (IchNotSupportedException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNotSupportedException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end updateFW ret=" +ret);
		return ret;
	}

	public static boolean addGlobalEventListener(int iCatchEventID,ICatchWificamListener listener,Boolean forAllSession) {
		boolean retValue = false;
		try {
			retValue = ICatchWificamSession.addEventListener(iCatchEventID, listener,forAllSession);
		} catch (IchListenerExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}

	public static boolean delGlobalEventListener(int iCatchEventID,ICatchWificamListener listener,Boolean forAllSession) {
		boolean retValue = false;
		try {
			retValue = ICatchWificamSession.delEventListener(iCatchEventID, listener,forAllSession);
		} catch (IchListenerNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}

	public boolean previewMove(int xshift, int yshfit) {
		AppLog.i(tag, "begin previewMove");
		boolean ret = false;
		ret = cameraAction.pan(xshift, yshfit);
		AppLog.i(tag, "end previewMove ret = " + ret);
		return ret;
		//return true;
	}
	
	public boolean resetPreviewMove() {
		AppLog.i(tag, "begin resetPreviewMove");
		boolean ret = false;
		ret = cameraAction.panReset();
		AppLog.i(tag, "end resetPreviewMove ret = " + ret);
		return ret;
		//return true;
	}
}
