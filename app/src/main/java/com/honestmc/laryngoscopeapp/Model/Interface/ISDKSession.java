package com.honestmc.laryngoscopeapp.Model.Interface;

import com.icatch.wificam.customer.ICatchWificamSession;

/**
 * Created by zhang yanhu C001012 on 2015/11/19 17:55.
 */
public interface ISDKSession {
    public boolean prepareSession();

    public boolean prepareSession(String ip);

    public boolean prepareSession(String ip,boolean enablePTPIP);

    public boolean isSessionOK();

    public ICatchWificamSession getSDKSession();

    public boolean checkWifiConnection();

    public boolean destroySession();
}
