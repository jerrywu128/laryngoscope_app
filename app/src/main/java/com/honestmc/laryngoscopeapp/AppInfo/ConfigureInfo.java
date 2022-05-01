package com.honestmc.laryngoscopeapp.AppInfo;

import android.content.Context;
import android.os.Environment;

import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.icatch.wificam.customer.ICatchWificamConfig;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Log.SdkLog;
import com.honestmc.laryngoscopeapp.Tools.FileOpertion.FileOper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 14:01.
 */
public class ConfigureInfo {
    private static ConfigureInfo configureInfo;
    private static final String TAG = "ConfigureInfo";
    private final String[] cfgTopic = {
            "AppVersion=" + AppInfo.APP_VERSION,
            "SupportAutoReconnection=false",
            "SaveStreamVideo=false",
            "SaveStreamAudio=false",
            "broadcast=false",
            "SupportSetting=false",
            "SaveAppLog=true",
            "SaveSDKLog=true",
            "disconnectRetry=3",
            "enableSoftwareDecoder=false",
            "disableAudio=true",
            "youtubeLive=true",
            "DisplayDecodeTime=false"
    };

    private ConfigureInfo() {

    }

    public static ConfigureInfo getInstance() {
        if (configureInfo == null) {
            configureInfo = new ConfigureInfo();
        }
        return configureInfo;
    }

    public void initCfgInfo(Context context) {
        AppLog.d(TAG, "readCfgInfo............");
        String directoryPath = context.getExternalCacheDir() + AppInfo.PROPERTY_CFG_DIRECTORY_PATH;
        String fileName = AppInfo.PROPERTY_CFG_FILE_NAME;
        String info = "";
        for (int ii = 0; ii < cfgTopic.length; ii++) {
            info = info + cfgTopic[ii] + "\n";
        }
        File file = new File(directoryPath + fileName);
        if (file.exists() == false) {
            FileOper.createFile(directoryPath, fileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(directoryPath + fileName);
                out.write(info.getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //如果配置文件存在，判断是否需要替换旧版的配置文件;
        else {
            CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
            String cfgVersion = null;
            try {
                cfgVersion = cfgInfo.getProperty("AppVersion");
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (cfgVersion != null) {
                AppLog.d(TAG, "cfgVersion..........=" + cfgVersion);
                if (!cfgVersion.equals(AppInfo.APP_VERSION)) {
                    writeCfgInfo(directoryPath + fileName, info);
                }
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            } else {
                writeCfgInfo(directoryPath + fileName, info);
                AppLog.d(TAG, "cfgVersion=" + cfgVersion + " appVersion=" + AppInfo.APP_VERSION);
            }
        }

        CfgProperty cfgInfo = new CfgProperty(directoryPath + fileName);
        String AutoReconnection = null;
        String saveStreamVideo = null;
        String saveStreamAudio = null;
        String broadcast = null;
        String supportSetting = null;
        String saveAppLog = null;
        String saveSDKLog = null;
        String disconnectRetry = null;
        String enableSoftwareDecoder = null;
        String disableAudio = null;
        String youtubeLive = null;
        String displayDecodeTime = null;
        try {
            AutoReconnection = cfgInfo.getProperty("SupportAutoReconnection");
            saveStreamVideo = cfgInfo.getProperty("SaveStreamVideo");
            saveStreamAudio = cfgInfo.getProperty("SaveStreamAudio");
            broadcast = cfgInfo.getProperty("broadcast");
            supportSetting = cfgInfo.getProperty("SupportSetting");
            saveAppLog = cfgInfo.getProperty("SaveAppLog");
            saveSDKLog = cfgInfo.getProperty("SaveSDKLog");
            disconnectRetry = cfgInfo.getProperty("disconnectRetry");
            enableSoftwareDecoder = cfgInfo.getProperty("enableSoftwareDecoder");
            disableAudio = cfgInfo.getProperty("disableAudio");
            youtubeLive = cfgInfo.getProperty("youtubeLive");
            displayDecodeTime = cfgInfo.getProperty("DisplayDecodeTime");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String streamOutputPath = Environment.getExternalStorageDirectory().toString() + AppInfo.STREAM_OUTPUT_DIRECTORY_PATH;
        FileOper.createDirectory(streamOutputPath);

        if (saveAppLog != null) {
            if (saveAppLog.equals("true")) {
                AppLog.enableAppLog();
            }
            AppLog.i(TAG, "GlobalInfo.saveAppLog..........=" + saveAppLog);
        }

        if (saveSDKLog != null) {
            if (saveSDKLog.equals("true")) {
                AppInfo.saveSDKLog = true;
                SdkLog.getInstance().enableSDKLog();
            }
            AppLog.i(TAG, "GlobalInfo.saveSDKLog..........=" + AppInfo.saveSDKLog);
        }

        if (AutoReconnection != null) {
            if (AutoReconnection.equals("true")) {
                AppInfo.isSupportAutoReconnection = true;
            } else {
                AppInfo.isSupportAutoReconnection = false;
            }
            AppLog.i(TAG, " end isSupportAutoReconnection = " + AppInfo.isSupportAutoReconnection);
        }

        if (saveStreamVideo != null) {
            if (saveStreamVideo.equals("true")) {
                AppLog.d(TAG, "saveStreamVideo..........=" + true);
                ICatchWificamConfig.getInstance().enableDumpMediaStream(true, streamOutputPath);
                // save video
            }
        }
        if (saveStreamAudio != null) {
            if (saveStreamAudio.equals("true")) {
                AppLog.d(TAG, "enableDumpMediaStream..........=" + false);
                ICatchWificamConfig.getInstance().enableDumpMediaStream(false, streamOutputPath);
                // save audio
            }
        }
        if (saveStreamAudio != null) {
            if (saveStreamAudio.equals("true")) {
                AppLog.d(TAG, "enableDumpMediaStream..........=" + false);
                ICatchWificamConfig.getInstance().enableDumpMediaStream(false, streamOutputPath);
                // save audio
            }
        }
        if (broadcast != null) {
            AppLog.d(TAG, "broadcast..........=" + broadcast);
            if (broadcast.equals("true")) {
                AppLog.d(TAG, "broadcast..........=" + broadcast);
                AppInfo.isSupportBroadcast = true;
            }
            AppLog.d(TAG, "GlobalInfo.isSupportBroadcast..........=" + AppInfo.isSupportBroadcast);
        }
        if (supportSetting != null) {
            AppLog.i(TAG, "supportSetting..........=" + supportSetting);
            if (supportSetting.equals("true")) {
                AppLog.i(TAG, "supportSetting..........=" + supportSetting);
                AppInfo.isSupportSetting = true;
            }
            AppLog.i(TAG, "GlobalInfo.isSupportSetting..........=" + AppInfo.isSupportSetting);
        }

        AppLog.d(TAG, "disconnectRetry=" + disconnectRetry);
        if (disconnectRetry != null) {
            int retryCount = Integer.parseInt(disconnectRetry);
            //ICatchWificamConfig.getInstance().setConnectionCheckParam(retryCount, 15);
            AppLog.d(TAG, "retryCount=" + retryCount);
        }

        AppLog.i(TAG, "enableSoftwareDecoder="+enableSoftwareDecoder);
        if (enableSoftwareDecoder != null) {
            if (enableSoftwareDecoder.equals("true")) {
                ICatchWificamConfig.getInstance().enableSoftwareDecoder(true);
                AppLog.d("1111", "open SoftwareDecoder");
                GlobalInfo.enableSoftwareDecoder = true;
            } else {
                ICatchWificamConfig.getInstance().enableSoftwareDecoder(false);
                GlobalInfo.enableSoftwareDecoder = false;
            }
        }
        AppLog.i(TAG, "disableAudio=" + disableAudio);
        if (disableAudio != null) {
            if (disableAudio.equals("true")) {
                AppInfo.disableAudio = true;
            } else {
                AppInfo.disableAudio = false;
            }
            AppLog.i(TAG, "AppInfo.disableAudio..........=" + AppInfo.disableAudio);
        }

        AppLog.i(TAG, "youtubeLive=" + youtubeLive);
        if (youtubeLive != null) {
            if(youtubeLive.equals("true")){
                AppInfo.youtubeLive = true;
            }else{
                AppInfo.youtubeLive = false;
            }
            AppLog.i(TAG, "AppInfo.youtubeLive..........=" + AppInfo.youtubeLive);
        }
        if (displayDecodeTime != null) {
            if(displayDecodeTime.equals("true")){
                AppInfo.displayDecodeTime = true;
            }else{
                AppInfo.displayDecodeTime = false;
            }
            AppLog.i(TAG, "AppInfo.displayDecodeTime..........=" + AppInfo.displayDecodeTime);
        }


    }

    private void writeCfgInfo(String path, String cfgInfo) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(cfgInfo.getBytes(), 0, cfgInfo.getBytes().length);
            out.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            AppLog.i(TAG, "end writeCfgInfo :IOException ");
            e1.printStackTrace();
        }
    }
}
