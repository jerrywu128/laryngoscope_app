package com.honestmc.laryngoscopeapp.Presenter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.honestmc.laryngoscopeapp.Dbl.DatabaseHelper;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyProgressDialog;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Message.AppMessage;
import com.honestmc.laryngoscopeapp.Mode.CameraNetworkMode;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.Presenter.Interface.BasePresenter;
import com.honestmc.laryngoscopeapp.PropertyId.PropertyId;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.SdkApi.CameraProperties;
import com.honestmc.laryngoscopeapp.SystemInfo.MWifiManager;
import com.honestmc.laryngoscopeapp.View.Activity.LaunchActivity;
import com.honestmc.laryngoscopeapp.View.Activity.PreviewActivity;

public class ConnectCMPresenter extends BasePresenter {

    private Activity activity;
    private MyCamera currentCamera;
    private final LaunchHandler launchHandler = new LaunchHandler();
    WifiManager wifiManager;
    MWifiManager mWifi;
    IntentFilter filter;
    public ConnectCMPresenter(Activity activity){
        super(activity);
        this.activity = activity;
    }


    public void launchCamera() {
       // String wifiSsid = MWifiManager.getSsid(activity);


        new Thread(new Runnable() {
            public void run() {
                beginConnectCamera(MWifiManager.getIp(activity));
            }
        }).start();
    }





    private class LaunchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMessage.MESSAGE_CAMERA_CONNECT_FAIL:
                    MyProgressDialog.closeProgressDialog();
                    //AppDialog.showDialogWarn(activity, R.string.dialog_timeout);
                    Toast.makeText(activity, R.string.dialog_timeout, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                            Intent mainIntent = new Intent(activity, LaunchActivity.class);
                            activity.startActivity(mainIntent);
                        }
                    }, 2000);
                    break;
                case AppMessage.MESSAGE_CAMERA_CONNECT_SUCCESS:
                    MyProgressDialog.closeProgressDialog();
                    redirectToAnotherActivity(activity, PreviewActivity.class);
                    activity.finish();
                    break;
            }
        }
    }


    private void beginConnectCamera(String ip) {
        AppLog.i(TAG, "isWifiConnect() == true");

        currentCamera = new MyCamera();

        if (currentCamera.getSDKsession().prepareSession(ip) == false) {
            launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
            return;
        }
        if (currentCamera.getSDKsession().checkWifiConnection() == true) {
            GlobalInfo.getInstance().setCurrentCamera(currentCamera);
            currentCamera.initCamera();
            if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_DATE)) {

                CameraProperties.getInstance().setCameraDate();
            }
            if (CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_DATE_TIMEZONE)) {
                CameraProperties.getInstance().setCameraDateTimeZone();
            }
            currentCamera.setMyMode(CameraNetworkMode.AP);

            DatabaseHelper.updateCameraName(GlobalInfo.curSlotId, MWifiManager.getSsid(activity));

            GlobalInfo.getInstance().setSsid(MWifiManager.getSsid(activity));

            activity.finish();
            launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECT_SUCCESS).sendToTarget();
            return;
        } else {

            AppLog.i(TAG, "..........checkWifiConnection  fail");
            launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECT_FAIL).sendToTarget();
            return;
        }

    }



    public void connectWifiQ(Context context, String ssid, String password){
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        mWifi = new MWifiManager(activity,wifiManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mWifi.connectWifiQ(context,ssid,password,this);
        } else {
            filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.getApplicationContext().registerReceiver(mWifi.wifiBroadcastReceiver,filter);
            mWifi.connectWifi(ssid,password);

        }

    }

    public String getssid(){
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = info.getSSID();
        return ssid;

    }

    public boolean isWifienabled(){
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(activity.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            return false;
        }
        else
            return true;

    }



}
