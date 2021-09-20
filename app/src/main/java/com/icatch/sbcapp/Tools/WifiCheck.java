package com.icatch.sbcapp.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.GlobalApp.ExitApp;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.WifiListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Message.AppMessage;
import com.icatch.sbcapp.Mode.CameraNetworkMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SystemInfo.MWifiManager;
import com.icatch.sbcapp.View.Activity.LaunchActivity;
import com.icatch.sbcapp.View.Activity.MultiPbActivity;
import com.icatch.sbcapp.View.Activity.PhotoPbActivity;
import com.icatch.sbcapp.View.Activity.PreviewActivity;
import com.icatch.sbcapp.View.Activity.VideoPbActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by b.jiang on 2016/4/22.
 */
public class WifiCheck {

    private String TAG = "WifiCheck";
    private static final int CONNECT_FAILED = 0x02;
    private static final int IN_BACKGROUND = 0x03;
    private static final int RECONNECT_SUCCESS = 0x04;
    private static final int RECONNECT_FAILED = 0x05;
    public static final int WIFICIPHER_NOPASS = 0x06;
    public static final int WIFICIPHER_WEP = 0x07;
    public static final int WIFICIPHER_WAP = 0x08;
    private static final int RECONNECT_CAMERA = 0x09;
    private static final int MESSAGE_CAMERA_CONNECT_FAIL = 0x0a;
    private static int RECONNECT_WAITING = 10000;
    private static int RECONNECT_CHECKING_PERIOD = 3000;
    private WifiInfo mWifiInfo;
    private WifiManager mWifiManager;
    private Activity activity;
    //    private Timer reconnectTimer;
    private Timer timeoutTimer;
    private static int RECONNECT_TIME = 15;
    protected AlertDialog reconnectDialog;
    private AlertDialog dialog;
    private Boolean isShowed = false;
    private Handler handler;
    private WifiListener wifiListener;
    private boolean enableReconnect = false;
    private static int RECONNECT_TIMEOUT = 1000 * 45;//超时45s


    public WifiCheck(Activity activity) {
        // 取得WifiManager对象
        this.activity = activity;
        mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public WifiCheck(Activity activity, Handler handler) {
        // 取得WifiManager对象
        this.handler = handler;
        this.activity = activity;
        mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public boolean connectWifi(String SSID, String Password, int Type) {
        AppLog.d(TAG, "connectWifi curTime=" + System.currentTimeMillis() + " SSID=" + SSID + " Password=" + Password + " activity=" + activity);
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiConfiguration tempConfig = isExsits(SSID);
//        if (tempConfig != null) {
//            AppLog.d(TAG, "removeNetwork SSID=" + SSID);
//            wifiManager.removeNetwork(tempConfig.networkId);
//        }
        WifiConfiguration config = CreateWifiInfo(SSID, Password, Type);
//        AppLog.d(TAG, "connectWifi start addNetwork　config=" +config);

        int netID = wifiManager.addNetwork(config);
        AppLog.d(TAG, "connectWifi start enableNetwork netID=" + netID);
        if (netID < 0) {
            return false;
        }
        boolean bRet = wifiManager.enableNetwork(netID, true);
        AppLog.d(TAG, "connectWifi end----bRet =" + bRet);
        return bRet;
    }

    //20170303 add by b.jiang
    //
    public void connectWifi(String targetSsid, String targetPsd, String enc) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi

        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        AppLog.d(TAG, "begin addNetwork conf=" + conf);
        int value = wifiManager.addNetwork(conf);
        AppLog.d(TAG, "addNetwork value=" + value);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            AppLog.d(TAG, "i.SSID=" + i.SSID + " ssid" + ssid);
            if (i.SSID != null && i.SSID.equals(ssid)) {
                AppLog.d(TAG, "start connect");
                boolean ret = false;
                ret = wifiManager.disconnect();
                AppLog.d(TAG, "i.networkId=" + i.networkId);
                if (i.networkId < 0) {
                    break;
                }
                ret = wifiManager.enableNetwork(i.networkId, true);
                AppLog.d(TAG, "enableNetwork ret=" + ret);
                ret = wifiManager.reconnect();
                AppLog.d(TAG, "reconnect ret=" + ret);
                break;
            }
        }
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        AppLog.d(TAG, "start CreateWifiInfo SSID=" + SSID);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WIFICIPHER_NOPASS) // Data.WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WIFICIPHER_WEP) // Data.WIFICIPHER_WEP
        {
            if (Password.length() != 0) {
                int length = Password.length();
                // WEP-40, WEP-104, and 256-bit WEP
                // (WEP-232?)
                if ((length == 10 || length == 26 || length == 58) && Password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = '"' + Password + '"';
                }
            }
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WIFICIPHER_WAP) // Data.WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        AppLog.d(TAG, "end CreateWifiInfo config=" + config);
        return config;
    }

    public boolean isWifiConnected(Context context, String nameFilter) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                WifiManager mWifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = mWifi.getConnectionInfo();
                AppLog.d(TAG, "isWifiConnected ssid=" + nameFilter + " getSSID()=" + wifiInfo.getSSID());
                if (wifiInfo.getIpAddress() != 0 && wifiInfo.getSSID().contains(nameFilter) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showReconnectDialog() {
        MyProgressDialog.closeProgressDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.dialog_btn_reconnect).setMessage(R.string.message_reconnect);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showReconnectDialog exit connect");
                ExitApp.getInstance().finishAllActivity();
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }

    private void showReconnectTimeoutDialog(int messageId) {
        MyProgressDialog.closeProgressDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.text_reconnect_timeout).setMessage(messageId);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ExitApp.getInstance().exit();
                AppLog.d(TAG, "showReconnectTimeoutDialog exit connect");
                ExitApp.getInstance().finishAllActivityExceptOne(LaunchActivity.class);
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }

    private void showReconnectFailedDialog(int messageId) {
        MyProgressDialog.closeProgressDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalInfo.getInstance().getAppContext());
        builder.setIcon(R.drawable.warning).setTitle(R.string.text_reconnect_failed).setMessage(messageId);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.d(TAG, "showReconnectTimeoutDialog exit connect");
                ExitApp.getInstance().finishAllActivityExceptOne(LaunchActivity.class);
//                ExitApp.getInstance().finishAllActivity();
            }
        });
        reconnectDialog = builder.create();
        reconnectDialog.setCancelable(false);
        reconnectDialog.show();
    }

    public void showAutoReconnectProgressDialog(Activity activity) {
        if (dialog != null) {
            dialog.dismiss();
        }
//        closeWifi();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        openWifi();
        MyProgressDialog.showProgressDialog(activity, R.string.text_reconnection);
        startTimeoutTimer();
        startWifiListener();
    }

    private final Handler wifiCheckHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECONNECT_CAMERA:
                    showReconnectDialog();
                    break;
                case MESSAGE_CAMERA_CONNECT_FAIL:
                    MyProgressDialog.closeProgressDialog();
                    showReconnectFailedDialog(R.string.message_reconnect_failed);
                    break;
                case AppMessage.MESSAGE_CONNECTED:
                    WifiInfo wifiInfo = (WifiInfo) msg.obj;
                    String ssid = wifiInfo.getSSID().replaceAll("\"", "");
                    AppLog.d(TAG, "receive AppMessage.MESSAGE_CONNECTED ssid=" + ssid);
                    if (ssid != null && !ssid.equals("0x") && !ssid.equals("") && !ssid.equals("<unknown ssid>") && !ssid.equals(GlobalInfo.getInstance()
                            .getSsid())) {
                        stopTimeoutTimer();
                        if (reconnectDialog != null) {
                            reconnectDialog.dismiss();
                        }
                        endWifiListener();
                        showReconnectFailedDialog(R.string.message_connected_other_wifi);
                        GlobalInfo.needReconnect = true;
                    } else if (ssid != null && ssid.equals(GlobalInfo.getInstance().getSsid())) {
                        enableReconnect = true;
                        endWifiListener();
                        if (reconnectDialog != null) {
                            reconnectDialog.dismiss();
                        }
                        if (GlobalInfo.isPrepareSession) {
                            return;
                        }
                        wifiCheckHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                            }
                        });
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                reconnectCam();
                            }
                        };
                        Timer tempTimer = new Timer(true);
                        tempTimer.schedule(task, 1000);
                    }
                    break;
            }
        }
    };

    private void reconnectCam() {
        AppLog.d(TAG, "start reconnectCam");
        if (GlobalInfo.isPrepareSession) {
            wifiCheckHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                }
            });
            return;
        }
        GlobalInfo.isPrepareSession = true;
        GlobalInfo.needReconnect = true;
        MyCamera myCamera = new MyCamera();
        if (myCamera.getSDKsession().prepareSession(MWifiManager.getIp(activity)) == false) {
            wifiCheckHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                }
            });
            stopTimeoutTimer();
            wifiCheckHandler.obtainMessage(MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
            return;
        }

        stopTimeoutTimer();
        if (myCamera.getSDKsession().checkWifiConnection() == true) {
            GlobalInfo.getInstance().setCurrentCamera(myCamera);
            myCamera.initCamera();
            if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_DATE)) {
                CameraProperties.getInstance().setCameraDate();
            }
            myCamera.setMyMode(CameraNetworkMode.AP);
            Activity activity = GlobalInfo.getInstance().getCurrentApp();
            AppLog.d(TAG, "reconnectCam curActivity=" + activity.getClass().getSimpleName());
            GlobalInfo.getInstance().startConnectCheck();
            wifiCheckHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                }
            });
            if (activity instanceof PreviewActivity) {
                AppLog.d(TAG, "reconnectCam 1");
                ((PreviewActivity) activity).refresh();
            } else if (activity instanceof MultiPbActivity) {
                AppLog.d(TAG, "reconnectCam 2");
            } else if (activity instanceof VideoPbActivity) {
                AppLog.d(TAG, "reconnectCam 3");
                ((VideoPbActivity) activity).refresh();
            } else if (activity instanceof PhotoPbActivity) {
                AppLog.d(TAG, "reconnectCam 4");
            } else {
                AppLog.d(TAG, "reconnectCam 5");
            }
        } else {
            wifiCheckHandler.obtainMessage(MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
        }
    }

    private void startWifiListener() {
        AppLog.d(TAG, "startWifiListener");
        if (wifiListener == null) {
            wifiListener = new WifiListener(activity.getApplicationContext(), wifiCheckHandler);
        }
        wifiListener.registerReceiver();
    }

    private void endWifiListener() {
        AppLog.d(TAG, "endWifiListener");
        if (wifiListener != null) {
            wifiListener.unregisterReceiver();
            wifiListener = null;
        }
    }

    private void startTimeoutTimer() {
        AppLog.d(TAG, "startTimeoutTimer");
        if (timeoutTimer == null) {
            timeoutTimer = new Timer(true);
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AppLog.d(TAG, "reconnect timeout!");
                endWifiListener();
                wifiCheckHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showReconnectTimeoutDialog(R.string.message_reconnect_failed);
                    }
                });
            }
        };
        timeoutTimer.schedule(task, RECONNECT_TIMEOUT);
    }

    private void stopTimeoutTimer() {
        AppLog.d(TAG, "stopTimeoutTimer");
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
    }
}