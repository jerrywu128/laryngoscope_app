package com.icatch.sbcapp.Log;

import android.os.Environment;

import com.icatch.wificam.customer.ICatchWificamLog;
import com.icatch.wificam.customer.type.ICatchLogLevel;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.Tools.FileOpertion.FileOper;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 17:49.
 */
public class SdkLog {
    private static SdkLog sdkLog;

    public static SdkLog getInstance(){
        if(sdkLog == null){
            sdkLog = new SdkLog();
        }
        return sdkLog;
    }
    public void enableSDKLog() {
        String path = null;
        path = Environment.getExternalStorageDirectory().toString() + AppInfo.SDK_LOG_DIRECTORY_PATH;
        Environment.getExternalStorageDirectory();
        FileOper.createDirectory(path);
        AppLog.d("sdkLog", "start enable sdklog");
        ICatchWificamLog.getInstance().setDebugMode(true);
        ICatchWificamLog.getInstance().setFileLogPath(path);
        ICatchWificamLog.getInstance().setSystemLogOutput(true);
        ICatchWificamLog.getInstance().setFileLogOutput(true);
        ICatchWificamLog.getInstance().setRtpLog(true);
        ICatchWificamLog.getInstance().setPtpLog(true);
        ICatchWificamLog.getInstance().setRtpLogLevel(ICatchLogLevel.ICH_LOG_LEVEL_INFO);
        ICatchWificamLog.getInstance().setPtpLogLevel(ICatchLogLevel.ICH_LOG_LEVEL_INFO);
        AppLog.d("sdkLog", "end enable sdklog");
    }
}
