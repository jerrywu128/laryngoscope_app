package com.icatch.sbcapp.Tools;

import android.os.Handler;

import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.SystemInfo.MWifiManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by b.jiang on 2016/12/5.
 */

public class ConnectCheckTimer {
    private static final String TAG = "ConnectCheckTimer";
    private static Timer connectChecktimer;
    private static final long DELAY = 3000;
    private static final long PERIOD = 3000;
    public static final int MESSAGE_CONNECT_DISCONNECTED = 0x1601;
    private static int connectCheckNum = 0;

    public static void startCheck(final Handler handler) {
        if (connectChecktimer != null) {
            connectChecktimer.cancel();
        }
        connectChecktimer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!InetAddressUtils.isReachable(MWifiManager.getIp(GlobalInfo.getInstance().getAppContext()))) {
                    connectCheckNum++;
                    if (connectCheckNum == 5) {
                        stopCheck();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.obtainMessage(MESSAGE_CONNECT_DISCONNECTED).sendToTarget();
                            }
                        });
                        connectCheckNum = 0;
                    }
                } else {
                    connectCheckNum = 0;
                }
            }
        };
        connectChecktimer.schedule(timerTask, DELAY, PERIOD);
        AppLog.d(TAG, "startCheck connectChecktimer=" + connectChecktimer);
    }

    public static void stopCheck() {
        AppLog.d(TAG, "stopCheck connectChecktimer=" + connectChecktimer);
        if (connectChecktimer != null) {
            connectChecktimer.cancel();
        }
    }
}
