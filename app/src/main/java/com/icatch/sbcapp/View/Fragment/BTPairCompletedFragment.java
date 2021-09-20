package com.icatch.sbcapp.View.Fragment;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.icatch.sbcapp.AppDialog.AppDialog;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnFragmentInteractionListener;
import com.icatch.sbcapp.Listener.WifiListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Message.AppMessage;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.Tools.WifiCheck;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothDeviceBusyException;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothTimeoutException;
import com.icatchtek.bluetooth.customer.type.ICatchWifiEncType;
import com.icatchtek.bluetooth.customer.type.ICatchWifiInformation;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BTPairCompletedFragment extends Fragment {

    private String TAG = "BTPairCompletedFragment";
    private OnFragmentInteractionListener mListener;
    private Handler appStartHandler;
    private View myView;
    private TextView txvPairCompleted;

    private ICatchWifiInformation iCatchWifiAPInformation;
    private WifiCheck wifiCheck;
    private ExecutorService executor;
    String ssid = "";
    String password;
    private static final int ENABLE_WIFI_FAILED = 14;
    private static final int CONNECT_WIFI_FAILED = 16;
    private static final int CONNECT_CAMERA_FAILED = 17;
    private int CONNECT_WIFI_TIMEOUT = 1000 * 60;
    private int CONNECT_PERIOD = 1000 * 15;
    private ImageButton backBtn;
    private Timer connectWifiTimer;
    private Timer connectTimeoutTimer;
    private WifiListener wifiListener;
    private boolean isNotifyedConnectCamera = false;


    public BTPairCompletedFragment() {
        // Required empty public constructor
        this.appStartHandler = GlobalInfo.getInstance().getAppStartHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_btpair_completed, container, false);
        txvPairCompleted = (TextView) myView.findViewById(R.id.done_txv);
        txvPairCompleted.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyProgressDialog.showProgressDialog(getActivity(), "Connecting..");
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new ConnectWifiThread(), null);

//                enableWifi();
            }
        });

        backBtn = (ImageButton) myView.findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.removeFragment();
                }
            }
        });

        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        wifiCheck = new WifiCheck(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        if (mListener != null) {
            mListener.submitFragmentInfo(BTPairCompletedFragment.class.getSimpleName(), R.string.title_fragment_btpair_completed);
        }
        super.onResume();
    }

    class ConnectWifiThread implements Runnable {
        @Override
        public void run() {
            if (enableWifi()) {
                if (iCatchWifiAPInformation == null) {
                    try {
                        AppLog.d(TAG, "------start getWifiInformation");
                        iCatchWifiAPInformation = GlobalInfo.iCatchBluetoothClient.getSystemControl().getWifiInformation();
                        AppLog.d(TAG, "------end getWifiInformation iCatchWifiAPInformation=" + iCatchWifiAPInformation);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IOException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "connent IOException!", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    } catch (IchBluetoothTimeoutException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IchBluetoothTimeoutException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "connent timeout!", Toast.LENGTH_LONG).show();
                            }

                        });
                        return;
                    } catch (IchBluetoothDeviceBusyException e) {
                        // TODO Auto-generated catch block
                        AppLog.d(TAG, "getWifiInformation IchBluetoothDeviceBusyException");
                        e.printStackTrace();
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                Toast.makeText(getActivity(), "Device busy!", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    if (iCatchWifiAPInformation == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                MyProgressDialog.closeProgressDialog();
                                AppDialog.showDialogWarn(getActivity(), "get Wifi information is null!");
//                                Toast.makeText(getActivity(), "get Wifi information is null!", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                }

                ssid = iCatchWifiAPInformation.getWifiSSID();
                password = iCatchWifiAPInformation.getWifiPassword();
                ICatchWifiEncType encType = iCatchWifiAPInformation.getWifiEncType();
                AppLog.d(TAG, "connectWifi encType=[" + encType + "]");

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "ssid=[" + ssid + "],pwd=[" + password + "]", Toast.LENGTH_LONG).show();
                    }
                });
                AppLog.d(TAG, "connectWifi ssid=[" + ssid + "]");
                AppLog.d(TAG, "connectWifi password=[" + password + "]");
                //ICOM-4499 beign add by b.jiang 20171120
                wifiCheck.closeWifi();
                //ICOM-4499 end add by b.jiang 20171120
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                wifiCheck.openWifi();
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个200毫秒检测……
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                    }
                }
                //JIRA ICOM-3786 begin modify by b.jiang 20170308
                connectWifiTimer = new Timer();
                connectWifiTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        wifiCheck.connectWifi(ssid, password, "WPA");
//                        wifiCheck.connectWifi(ssid, password, WifiCheck.WIFICIPHER_WAP);
                    }
                }, 1000, CONNECT_PERIOD);
                connectTimeoutTimer = new Timer();
                connectTimeoutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (connectWifiTimer != null) {
                            connectWifiTimer.cancel();
                            handler.obtainMessage(CONNECT_WIFI_FAILED).sendToTarget();
                        }
                    }
                }, CONNECT_WIFI_TIMEOUT);//一分钟后执行
                wifiListener = new WifiListener(getActivity().getApplicationContext(), handler);
                wifiListener.registerReceiver();
                //JIRA ICOM-3786 End modify by b.jiang 20170308
            } else {
                handler.obtainMessage(ENABLE_WIFI_FAILED).sendToTarget();
            }
        }
    }

    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENABLE_WIFI_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    AppDialog.showDialogWarn(getActivity(), "enable wifi failed.");
//                    Toast.makeText(getActivity(), "enable wifi failed.", Toast.LENGTH_LONG).show();
                    break;
                case CONNECT_WIFI_FAILED:
                    if (wifiListener != null) {
                        wifiListener.unregisterReceiver();
                        wifiListener = null;
                    }
                    MyProgressDialog.closeProgressDialog();
                    Toast.makeText(getActivity(), "failed to connect wifi.", Toast.LENGTH_LONG).show();
                    break;

                case CONNECT_CAMERA_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    Toast.makeText(getActivity(), "failed to connect camera.", Toast.LENGTH_LONG).show();
                    break;
                case AppMessage.MESSAGE_CONNECTED:
                    WifiInfo wifiInfo1 = (WifiInfo) msg.obj;
                    int networkId = wifiInfo1.getNetworkId();
                    String tempSsid = wifiInfo1.getSSID().replaceAll("\"", "");
                    AppLog.i(TAG, "receive MESSAGE_CONNECTED networkId=" + networkId + " tempSsid=" + tempSsid + " ssid=" + ssid + " " +
                            "isNotifyedConnectCamera=" + isNotifyedConnectCamera);
                    if (tempSsid != null && tempSsid.equals(ssid)) {
                        if (connectWifiTimer != null) {
                            connectWifiTimer.cancel();
                            connectWifiTimer = null;
                        }
                        if (connectTimeoutTimer != null) {
                            connectTimeoutTimer.cancel();
                            connectTimeoutTimer = null;
                        }
                        if (wifiListener != null) {
                            wifiListener.unregisterReceiver();
                            wifiListener = null;
                        }
//                        MyProgressDialog.closeProgressDialog();
                        if (GlobalInfo.iCatchBluetoothClient != null) {
                            try {
                                AppLog.d(TAG, "iCatchBluetoothClient.release()");
                                GlobalInfo.iCatchBluetoothClient.release();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                AppLog.d(TAG, "iCatchBluetoothClient.release() IOException");
                                e.printStackTrace();
                            }
                        }
                        if (!isNotifyedConnectCamera) {
//                            MyProgressDialog.showProgressDialog(getActivity(),R.string.action_processing);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MyProgressDialog.closeProgressDialog();
                                    appStartHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECTING_START).sendToTarget();
                                }
                            }, 6000);
                            isNotifyedConnectCamera = true;
//                            Timer timer = new Timer();
//                            timer.schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    MyProgressDialog.closeProgressDialog();
//                                    appStartHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_CONNECTING_START).sendToTarget();
//                                }
//                            }, 2000);
//                            isNotifyedConnectCamera = true;
                        }
                    } else {
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (networkId > 0) {
                            boolean ret = wifiManager.disableNetwork(networkId);
                            AppLog.d(TAG, "start disableNetwork networkId=" + networkId + " ret=" + ret);
                        }
                        wifiManager.disconnect();
                    }

                    break;
            }
        }
    };

    private boolean enableWifi() {
        Boolean retValue = false;
        try {
            AppLog.d(TAG, "start enableWifi curTime=" + System.currentTimeMillis());
            retValue = GlobalInfo.iCatchBluetoothClient.getSystemControl().enableWifi();
            AppLog.d(TAG, "end  enableWifi curTime=" + System.currentTimeMillis() + " retValue=");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            AppLog.d(TAG, "enableWifi() IOException");
            retValue = false;
            e.printStackTrace();
        } catch (IchBluetoothTimeoutException e) {
            AppLog.d(TAG, "enableWifi() IchBluetoothTimeoutException curTime=" + System.currentTimeMillis());
            // TODO Auto-generated catch block
            retValue = false;
            e.printStackTrace();
        } catch (IchBluetoothDeviceBusyException e) {
            // TODO Auto-generated catch block
            AppLog.d(TAG, "enableWifi() IchBluetoothDeviceBusyException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "enableWifi ret=" + retValue);
        return retValue;
    }
}
