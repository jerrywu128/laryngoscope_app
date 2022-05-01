package com.honestmc.laryngoscopeapp.Listener;

/**
 * Created by b.jiang on 2016/4/27.
 */
public interface Callback {
    void processSucceed();
    void processFailed();
    void processAbnormal();
}
