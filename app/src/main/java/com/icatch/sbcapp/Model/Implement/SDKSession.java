package com.icatch.sbcapp.Model.Implement;

import android.util.Log;

import com.icatch.wificam.customer.ICatchWificamConfig;
import com.icatch.wificam.customer.ICatchWificamSession;
import com.icatch.wificam.customer.exception.IchInvalidPasswdException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchPtpInitFailedException;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Model.Interface.ISDKSession;

public class SDKSession implements ISDKSession {
    private static int scanflag;
    private final static String tag = "SDKSession";
    private ICatchWificamSession session;
    private String ipAddress;
    private String uid;
    private String username;
    private String password;
    private boolean sessionPrepared = false;

    public SDKSession(String ipAddress, String uid, String username, String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.uid = uid;
    }

    public SDKSession() {
    }

    @Override
    public boolean prepareSession() {
        // TODO Auto-generated constructor stub
        ICatchWificamConfig.getInstance().enablePTPIP();
        sessionPrepared = true;
        session = new ICatchWificamSession();
        boolean retValue = false;
        try {
            retValue = session.prepareSession("192.168.1.1", "anonymous", "anonymous@icatchtek.com");
        } catch (IchInvalidPasswdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchPtpInitFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue == false) {
            AppLog.e(tag, "failed to prepareSession");
            sessionPrepared = false;
            Log.v("1111", "SDKSession,prepareSession fail!");
        }
        AppLog.d(tag, "end prepareSession ret=" + sessionPrepared);
        return sessionPrepared;
    }

    //本地播放初始化
    @Override
    public boolean prepareSession(String ip,boolean enablePTPIP) {
        // TODO Auto-generated constructor stub
        if(enablePTPIP){
            ICatchWificamConfig.getInstance().enablePTPIP();
        }else{
            ICatchWificamConfig.getInstance().disablePTPIP();
        }
        sessionPrepared = true;
        session = new ICatchWificamSession();
        boolean retValue = false;
        try {
            retValue = session.prepareSession("192.168.1.1", "anonymous", "anonymous@icatchtek.com");
        } catch (IchInvalidPasswdException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (IchPtpInitFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (retValue == false) {
            AppLog.e(tag, "failed to prepareSession");
            sessionPrepared = false;
            Log.v("1111", "SDKSession,prepareSession fail!");
        }
        AppLog.d(tag, "end init local prepareSession ret=" + sessionPrepared);
        return sessionPrepared;
    }

    @Override
    public boolean isSessionOK() {
        return sessionPrepared;
    }

    @Override
    public ICatchWificamSession getSDKSession() {
        return session;
    }

    @Override
    public boolean checkWifiConnection() {
        AppLog.i(tag, "Start checkWifiConnection");
        boolean retValue = false;
        try {
            retValue = session.checkConnection();
        } catch (IchInvalidSessionException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "End checkWifiConnection,retValue=" + retValue);
        return retValue;
    }

    @Override
    public boolean destroySession() {
        AppLog.i(tag, "Start destroySession");
        Boolean retValue = false;
        try {
            retValue = session.destroySession();
            AppLog.i(tag, "End  destroySession,retValue=" + retValue);
            AppLog.i(tag, "start disableTutk");
            AppLog.i(tag, "End disableTutk,retValue=" + retValue);
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retValue;
    }

    public static boolean startDeviceScan() {
        AppLog.i(tag, "Start startDeviceScan");

        boolean tempStartDeviceScanValue = ICatchWificamSession.startDeviceScan();

        AppLog.i(tag, "End startDeviceScan,tempStartDeviceScanValue=" + tempStartDeviceScanValue);
        if (tempStartDeviceScanValue) {
            scanflag = 1;
        }
        return tempStartDeviceScanValue;
    }

    @Override
    public boolean prepareSession(String ip) {
        // TODO Auto-generated constructor stub
        AppLog.d(tag, "start to enablePTPIP");
        ICatchWificamConfig.getInstance().enablePTPIP();
        sessionPrepared = true;
        session = new ICatchWificamSession();
        boolean retValue = false;
        AppLog.d(tag, "-----start to prepareSession!");
        try {
            retValue = session.prepareSession(ip, "anonymous", "anonymous@icatchtek.com");
        } catch (IchInvalidPasswdException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "prepareSession IchInvalidPasswdException");
            e.printStackTrace();
        } catch (IchPtpInitFailedException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "prepareSession IchPtpInitFailedException");
            e.printStackTrace();
        }
        if (retValue == false) {
            AppLog.e(tag, "failed to prepareSession");
            sessionPrepared = false;
        }
        AppLog.d(tag, "prepareSession =" + sessionPrepared);
        return sessionPrepared;
    }

    public static void stopDeviceScan() {
        AppLog.i(tag, "Start stopDeviceScan");
        boolean tempStopDeviceScanValue = false;
        if (scanflag == 1) {
            tempStopDeviceScanValue = ICatchWificamSession.stopDeviceScan();
        } else {
            tempStopDeviceScanValue = true;
        }
        scanflag = 0;
        AppLog.i(tag, "End stopDeviceScan,tempStopDeviceScanValue=" + tempStopDeviceScanValue);
    }
}
