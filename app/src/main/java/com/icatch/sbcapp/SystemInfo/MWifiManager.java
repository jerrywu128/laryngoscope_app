package com.icatch.sbcapp.SystemInfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.client.result.WifiParsedResult;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.AppInfo.AppSharedPreferences;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.ConnectCMPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.Tools.HotSpot;
import com.icatch.sbcapp.View.Activity.LaunchActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/24 17:57.
 */
public class MWifiManager {
    private static String TAG = "MWifiManager";
    public WifiBroadcastReceiver wifiBroadcastReceiver = new WifiBroadcastReceiver();
    private Activity activity;
    private WifiManager wifiManager;


    public MWifiManager(Activity activity) {

        this.activity = activity;
    }

    public MWifiManager(Activity activity, WifiManager wifiManager) {

        this.activity = activity;
        this.wifiManager = wifiManager;
    }

    public static String getSsid(Context context) {
        if (HotSpot.isApEnabled(context)) {
            String ssid = HotSpot.getWifiApSSID(context);
            return ssid;
        }
        if (!isWifiConnected(context)) {
            AppLog.i(TAG, "getSsid wifi not connect!");
            return null;
        }
        WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifi.getConnectionInfo();
        if (wifiInfo == null || wifiInfo.getSSID() == null) {
            AppLog.i(TAG, "getSsid wifiInfo is null");
            return null;
        }
        String ssid = wifiInfo.getSSID();
        if (ssid.contains("0x") || ssid.contains("<unknown ssid>")) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo2 = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            AppLog.i(TAG, "getSsid wifiInfo2:" + wifiInfo2);
            if (wifiInfo2 == null || wifiInfo2.getExtraInfo() == null) {
                return null;
            } else {
                String wifiName = wifiInfo2.getExtraInfo();
                return wifiName.replaceAll("\"", "");
            }

        } else {
            AppLog.i(TAG, "getSsid getSSID:" + wifiInfo.getSSID());
            return wifiInfo.getSSID().replaceAll("\"", "");
        }
    }

    private void openWifi() {
        //WifiManager mWifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);


        }

        int count = 0;
        while (!wifiManager.isWifiEnabled()) {
            if (count >= 10) {
                Log.i(TAG, "Took too long to enable wi-fi, quitting");
            }
            Log.i(TAG, "Still waiting for wi-fi to enable...");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ie) {
                // continue
            }
            count++;
        }

    }

    public static String getIp(Context context) {
        String ip = "192.168.1.1";
        if (HotSpot.isApEnabled(context)) {
            String value = HotSpot.getFirstConnectedHotIP();
            if (value != null) {
                ip = value;
            }
        }
//        AppLog.d(TAG,"getIp ip=" + ip);
        return ip;
    }

    /**
     ** 判断WIFI网络是否可用
     ** @param context
     ** @return
     *      
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
            return true;
        }

        return false;
    }


    public void connectWifiQ(Context context, String ssid, String password,ConnectCMPresenter connectCm) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

        try {

            ConnectivityManager.NetworkCallback mNetwork;
            WifiNetworkSpecifier specifier = null;

                specifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(ssid)
                        .setWpa2Passphrase(password)
                        .build();

            NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
                            .setNetworkSpecifier(specifier)
                            .build();

            @SuppressLint("ServiceCast")
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);


            mNetwork = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    //String netSSID = GlobalInfo.getInstance().getSsid();
                    assert connectivityManager != null;
                    /**將手機網路綁定到指定Wifi*/

                    connectivityManager.bindProcessToNetwork(network);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectCm.launchCamera();
                            }
                            /**android10以上連接至相機*/
                        }, 7000);

                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();

                    activity.finish();
                    Intent mainIntent = new Intent(activity, LaunchActivity.class);
                    activity.startActivity(mainIntent);
                }

            };


            connectivityManager.requestNetwork(request, mNetwork);
        }catch (SecurityException e){
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        }
    }


    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.i(TAG, "Wifi關閉中");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.i(TAG, "關閉Wifi中");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.i(TAG, "Wifi使用中");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.i(TAG, "開啟Wifi中");
                        break;
                }
            } else if ((WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                assert info != null;
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                    // Toast.makeText(context, "Wifi已斷線", Toast.LENGTH_SHORT).show();
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {

                    // Toast.makeText(context, "Wifi已連接", Toast.LENGTH_SHORT).show();
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {
                    Log.i(TAG, "Wifi連線中");
                }
            }
        }
    }

    public void connectWifi(String tagSsid, String tagPassword) {

        openWifi();


        String ssid = tagSsid;
        String password = tagPassword;
        WifiConfiguration conf = new WifiConfiguration();


        conf.allowedProtocols.clear();
        conf.allowedAuthAlgorithms.clear();
        conf.allowedGroupCiphers.clear();
        conf.allowedKeyManagement.clear();
        conf.allowedPairwiseCiphers.clear();
        conf.SSID = "\"".concat(ssid).concat("\"");
        ;
        //conf.preSharedKey = password;
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);


        conf.preSharedKey = "\"".concat(password).concat("\"");

/*
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE}, 9999);
            return;
        }

        Iterable<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                String existingSSID = existingConfig.SSID;
                if (existingSSID != null && existingSSID.equals(ssid)) {
                    wifiManager.removeNetwork(existingConfig.networkId);
                    wifiManager.saveConfiguration();
                }
            }
        }*/
        Integer foundNetworkID = findNetworkInExistingConfig(wifiManager, conf.SSID);
        if (foundNetworkID != null) {
            Log.i(TAG, "Removing old configuration for network " + conf.SSID);
            wifiManager.removeNetwork(foundNetworkID);
            wifiManager.saveConfiguration();
        }


        int netid = wifiManager.addNetwork(conf);
        if (netid >= 0) {
            // Try to disable the current network and start a new one.
            if (wifiManager.enableNetwork(netid, true)) {
                Log.i(TAG, "Associating to network " + conf.SSID);
                wifiManager.saveConfiguration();
            } else {
                Log.w(TAG, "Failed to enable network " + conf.SSID);
            }
        } else {
            Log.w(TAG, "Unable to add network " + conf.SSID);
        }


        System.out.println("ssid" + conf.SSID + "pw" + conf.preSharedKey + "netid" + netid);


    }


    private  Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE}, 9999);

            return null;
        }
        Iterable<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                String existingSSID = existingConfig.SSID;
                if (existingSSID != null && existingSSID.equals(ssid)) {
                    return existingConfig.networkId;
                }
            }
        }
        return null;
    }


}
