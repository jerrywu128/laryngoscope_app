/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.sbcapp.SdkApi;

import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Tools.ConvertTools;
import com.icatch.sbcapp.Tools.FileOpertion.FileTools;
import com.icatch.wificam.customer.ICatchWificamPlayback;
import com.icatch.wificam.customer.exception.IchBufferTooSmallException;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchDeviceException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchNoSuchFileException;
import com.icatch.wificam.customer.exception.IchNoSuchPathException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Added by zhangyanhu C01012,2014-6-27
 */
public class FileOperation {
	private final String tag = "FileOperation";
	private static FileOperation instance;
	private ICatchWificamPlayback cameraPlayback;

	public static FileOperation getInstance() {
		if (instance == null) {
			instance = new FileOperation();
		}
		return instance;
	}

	private FileOperation() {

	}

	public void initICatchWificamPlayback() {
		cameraPlayback = GlobalInfo.getInstance().getCurrentCamera().getplaybackClient();
	}

	public boolean cancelDownload(ICatchWificamPlayback playback) {
		AppLog.i(tag, "begin cancelDownload");
		if(playback == null){
			return true;
		}
		boolean retValue = false;
		try {
			retValue = playback.cancelFileDownload();
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
		} catch (IchDeviceException e) {
			AppLog.e(tag, "IchDeviceException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end cancelDownload retValue =" + retValue);
		return retValue;
	}

	public boolean cancelDownload() {
		AppLog.i(tag, "begin cancelDownload");
		if(cameraPlayback == null){
			AppLog.i(tag, "cameraPlayback is null");
			return true;
		}
		boolean retValue = false;
		try {
			retValue = cameraPlayback.cancelFileDownload();
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
		} catch (IchDeviceException e) {
			AppLog.e(tag, "IchDeviceException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AppLog.i(tag, "end cancelDownload retValue =" + retValue);
		return retValue;
	}

	public List<MultiPbItemInfo> getFileList(ICatchFileType type) {
		AppLog.i(tag, "begin getFileList timeout 20s---");
		List<ICatchFile> list = null;
		if(cameraPlayback == null){
			AppLog.i(tag, "cameraPlayback is null");
			return null;
		}
		try {
			//Log.d("1111", "start listFiles cameraPlayback=" + cameraPlayback);
			list = cameraPlayback.listFiles(type,20);//timeout 20s
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchNoSuchPathException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchPathException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end getFileList list=" + list);

		List<MultiPbItemInfo> multiPbItemInfos = convertFiles(list);
		return multiPbItemInfos;
	}

	public boolean deleteFile(ICatchFile file) {
		AppLog.i(tag, "begin deleteFile filename =" + file.getFileName());
		boolean retValue = false;
		try {
			retValue = cameraPlayback.deleteFile(file);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end deleteFile retValue=" + retValue);
		return retValue;
	}

	public boolean downloadFileQuick(ICatchFile file, String path){
		AppLog.i(tag, "begin downloadFileQuick filename =" + file.getFileName());
		AppLog.i(tag, "begin downloadFileQuick path =" + path);
		path = FileTools.chooseUniqueFilename(path);
		boolean retValue = false;
		try {
			retValue = cameraPlayback.downloadFileQuick(file, path);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end downloadFileQuick retValue =" + retValue);
		return retValue;
	}

	public boolean downloadFile(ICatchFile file, String path) {
		AppLog.i(tag, "begin downloadFile filename =" + file.getFileName());
		AppLog.i(tag, "begin downloadFile path =" + path);
		//ICOM-4062 Begin ADD by b.jiang 20170103
		path = FileTools.chooseUniqueFilename(path);
		AppLog.i(tag, "begin downloadFile chooseUniqueFilename path =" + path);
		//ICOM-4062 Begin ADD by b.jiang 20170103
		boolean retValue = false;
		try {
			retValue = cameraPlayback.downloadFile(file, path);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end downloadFile retValue =" + retValue);
		return retValue;
	}

	/**
	 * Added by zhangyanhu C01012,2014-7-2
	 */
	public ICatchFrameBuffer downloadFile(ICatchFile curFile) {
		AppLog.i(tag, "begin downloadFile for buffer filename =" + curFile.getFileName());
		ICatchFrameBuffer buffer = null;
		try {
			buffer = cameraPlayback.downloadFile(curFile);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		} catch (IchBufferTooSmallException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchBufferTooSmallException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end downloadFile for buffer, buffer =" + buffer);
		return buffer;
	}

	/**
	 * 
	 * Added by zhangyanhu C01012,2014-10-28
	 */

	public ICatchFrameBuffer getQuickview(ICatchFile curFile) {
		AppLog.i(tag, "begin getQuickview for buffer filename =" + curFile.getFileName());
		ICatchFrameBuffer buffer = null;
		try {
			buffer = cameraPlayback.getQuickview(curFile);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		} catch (IchBufferTooSmallException e) {
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		} catch (IchNoSuchFileException e) {
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		}
		AppLog.i(tag, "end getQuickview for buffer, buffer =" + buffer);
		return buffer;
	}

	/**
	 * Added by zhangyanhu C01012,2014-7-2
	 */
	public ICatchFrameBuffer getThumbnail(ICatchFile file) {
		AppLog.i(tag, "begin getThumbnail file=" + file);
		// TODO Auto-generated method stub
		ICatchFrameBuffer frameBuffer = null;
		try {
			//Log.d("1111", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
			frameBuffer = cameraPlayback.getThumbnail(file);
			//Log.d("1111", "end cameraPlayback.getThumbnail(file)");
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		} catch (IchBufferTooSmallException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchBufferTooSmallException");
			e.printStackTrace();
		}

		AppLog.i(tag, "end getThumbnail frameBuffer=" + frameBuffer );
		return frameBuffer;
	}

	public ICatchFrameBuffer getThumbnail(String filePath) {
		AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail file=" + filePath);
		// TODO Auto-generated method stub
		ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_TYPE_VIDEO, filePath,"", 0);
		AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail cameraPlayback=" + cameraPlayback);
		ICatchFrameBuffer frameBuffer = null;
		try {
			AppLog.d("test", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + cameraPlayback);
			frameBuffer = cameraPlayback.getThumbnail(icathfile);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchDeviceException");
			e.printStackTrace();
		} catch (IchBufferTooSmallException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchBufferTooSmallException");
			e.printStackTrace();
		}
		AppLog.d("[Normal] -- FileOperation: ", "end getThumbnail frameBuffer=" + frameBuffer);
		return frameBuffer;
	}

	public ICatchFrameBuffer getThumbnail(ICatchWificamPlayback wificamPlayback,String filePath) {
		AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail");
		// TODO Auto-generated method stub
		ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_TYPE_VIDEO, filePath,"", 0);
		AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail file=" + filePath);
		AppLog.d("[Normal] -- FileOperation: ", "begin getThumbnail cameraPlayback=" + wificamPlayback);
		ICatchFrameBuffer frameBuffer = null;
		try {
			AppLog.d("test", "start cameraPlayback.getThumbnail(file) cameraPlayback=" + wificamPlayback);
			frameBuffer = wificamPlayback.getThumbnail(icathfile);
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchInvalidSessionException");
		} catch (IchNoSuchFileException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchDeviceException");
			e.printStackTrace();
		} catch (IchBufferTooSmallException e) {
			// TODO Auto-generated catch block
			AppLog.d("[Error] -- FileOperation: ", "IchBufferTooSmallException");
			e.printStackTrace();
		}
		AppLog.d("[Normal] -- FileOperation: ", "end getThumbnail frameBuffer=" + frameBuffer);
		return frameBuffer;
	}

	public boolean openFileTransChannel(){
		AppLog.i(tag, "begin openFileTransChannel");
		boolean retValue = false;
		try {
			retValue = cameraPlayback.openFileTransChannel();
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		}
		AppLog.i(tag, "end openFileTransChannel retValue=" + retValue);
		return retValue;
	}

	public boolean closeFileTransChannel(){
		AppLog.i(tag, "begin closeFileTransChannel");
		boolean retValue = false;
		try {
			retValue = cameraPlayback.closeFileTransChannel();
		} catch (IchSocketException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			// TODO Auto-generated catch block
			AppLog.e(tag, "IchInvalidSessionException");
		}
		AppLog.i(tag, "end closeFileTransChannel retValue=" + retValue);
		return retValue;
	}

	public boolean uploadFile(String localPath, String remotePath){
		AppLog.i(tag, "begin uploadFile");
		boolean retValue = false;
		try {
			retValue = cameraPlayback.uploadFile(localPath,remotePath);
		} catch (IchNoSuchFileException e) {
			AppLog.e(tag, "IchNoSuchFileException");
			e.printStackTrace();
		} catch (IchSocketException e) {
			AppLog.e(tag, "IchSocketException");
			e.printStackTrace();
		} catch (IchCameraModeException e) {
			AppLog.e(tag, "IchCameraModeException");
			e.printStackTrace();
		} catch (IchInvalidSessionException e) {
			AppLog.e(tag, "IchInvalidSessionException");
			e.printStackTrace();
		} catch (IchDeviceException e) {
			AppLog.e(tag, "IchDeviceException");
			e.printStackTrace();
		}
		AppLog.i(tag, "End uploadFile retValue=" + retValue);
		return retValue;
	}

	public int getFileCount(){
		AppLog.i(tag, "begin getFileCount");
		int retValue = -1;
		try {
			retValue = cameraPlayback.getFileCount();
		} catch (Exception e) {
			AppLog.e(tag, "Exception e:" + e.getClass().getSimpleName());
			e.printStackTrace();
		}
		AppLog.i(tag, "End getFileCount retValue=" + retValue);
		return retValue;
	}


	public List<MultiPbItemInfo> getFileList(ICatchFileType type, int maxNum) {
		AppLog.i(tag, "begin getFileList type: " + type + " maxNum：" + maxNum);
		if(cameraPlayback == null){
			AppLog.i(tag, "cameraPlayback is null");
			return null;
		}
		int startIndex = 0;
		int endIndex;
		int fileCount = -1;
		List<ICatchFile> photoList = new LinkedList<>();
		List<ICatchFile> videoList = new LinkedList<>();

		fileCount = getFileCount();
		if(fileCount <=0){
			return null;
		}
		if(fileCount  < maxNum){
			startIndex = 1 ;
			endIndex = fileCount;
		}else {
			startIndex = 1;
			endIndex = maxNum;
		}
		while (fileCount >= startIndex){
			AppLog.i(tag, "start getFileList startIndex=" + startIndex + " endIndex=" + endIndex);
			 try {
				 List<ICatchFile> templist = cameraPlayback.listFiles(ICatchFileType.ICH_TYPE_ALL,startIndex, endIndex, 20);//timeout 20s
				 if(templist != null) {
					 AppLog.i(tag, "end getFileList tempList =" + templist.size());
				 }
				 if(templist != null && templist.size() > 0){
					 for (ICatchFile file: templist
						  ) {
						 if(file != null && file.getFileType() == ICatchFileType.ICH_TYPE_VIDEO){
						 	videoList.add(file);
						 }else if(file != null && file.getFileType() == ICatchFileType.ICH_TYPE_IMAGE){
						 	photoList.add(file);
						 }
					 }
//				 	list.addAll(templist);
				 }

				 AppLog.i(tag, "end getFileList photoList size=" + photoList.size());
				 AppLog.i(tag, "end getFileList videoList size=" + videoList.size());
			} catch (Exception e) {
				AppLog.e(tag, "Exception e:" + e.getClass().getSimpleName());
				e.printStackTrace();
			}

			startIndex = endIndex + 1;
			 if(endIndex + maxNum > fileCount){
			 	endIndex =fileCount;
			 }else {
			 endIndex = endIndex + maxNum;
			 }

			AppLog.i(tag, "end getFileList startIndex=" + startIndex + " endIndex=" + endIndex);
		}

		List<MultiPbItemInfo> photoInfoList = convertFiles(photoList);
		List<MultiPbItemInfo> videoInfoList = convertFiles(videoList);
		GlobalInfo.getInstance().photoInfoList = photoInfoList;
		GlobalInfo.getInstance().videoInfoList = videoInfoList;
		if(type == ICatchFileType.ICH_TYPE_VIDEO) {
			return videoInfoList;
		}else if(type == ICatchFileType.ICH_TYPE_IMAGE){
			return photoInfoList;
		}else {
			return null;
		}
	}

	private List<MultiPbItemInfo> convertFiles(List<ICatchFile> fileList){
		List<MultiPbItemInfo> photoInfoList = new ArrayList<MultiPbItemInfo>();
		if(fileList == null || fileList.size() <= 0){
			return photoInfoList;
		}
		String fileDate;
		int section = 1;
		Map<String, Integer> sectionMap = new HashMap<String, Integer>();

		for (int ii = 0; ii < fileList.size(); ii++) {
			fileDate = ConvertTools.getTimeByfileDate(fileList.get(ii).getFileDate());
			if (fileDate == null) {
				continue;
			}
			if (!sectionMap.containsKey(fileDate)) {
				sectionMap.put(fileDate, section);
				MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
				photoInfoList.add(mGridItem);
				section++;
			} else {
				MultiPbItemInfo mGridItem = new MultiPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
				photoInfoList.add(mGridItem);
			}
		}

		return photoInfoList;
	}


}
