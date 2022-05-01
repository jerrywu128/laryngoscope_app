package com.honestmc.laryngoscopeapp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.honestmc.laryngoscopeapp.Adapter.BlueToothListAdapter;
import com.honestmc.laryngoscopeapp.BaseItems.BluetoothAppDevice;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyProgressDialog;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyToast;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Fragment.BTPairSetupFragment;
import com.honestmc.laryngoscopeapp.View.Interface.BTPairBeginFragmentView;
import com.icatchtek.bluetooth.customer.ICatchBluetoothAdapter;
import com.icatchtek.bluetooth.customer.ICatchBluetoothManager;
import com.icatchtek.bluetooth.customer.client.ICatchBluetoothClient;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothContextInvalidException;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothDeviceBusyException;
import com.icatchtek.bluetooth.customer.exception.IchBluetoothNotSupportedException;
import com.icatchtek.bluetooth.customer.listener.ICatchBTDeviceDetectedListener;
import com.icatchtek.bluetooth.customer.listener.ICatchBroadcastReceiver;
import com.icatchtek.bluetooth.customer.listener.ICatchBroadcastReceiverID;
import com.icatchtek.bluetooth.customer.type.ICatchBluetoothDevice;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by b.jiang on 2016/4/22.
 */
public class BTPairBeginPresenter {
    private String TAG = "BTPairBeginPresenter";
    private Activity activity;
    Handler launchHandler;
    private int curListViewPointer = 0;
    private ExecutorService executor;
    private BTPairBeginFragmentView pairBeginFragmentView;

    private static final int REQUEST_BLUETOOTH_SCAN = 0;
    private static final int GET_BLUETOOTH_DEVICE = 2;
    private static final int GET_BLUETOOTH_CLIENT = 3;
    private static final int BLUETOOTH_SCAN_TIME_OUT = 4;
    private static final int CONNECT_WIFI_FAILED = 6;
    private static final int CONNECT_CAMERA_FAILED = 7;
    private static final int GET_BLUETOOTH_CLIENT_SUCCESS = 8;
    private static final int GET_BLUETOOTH_CLIENT_FAILED = 9;
    private BlueToothListAdapter blueToothListAdapter;
    private List<BluetoothAppDevice> bluetoothDeviceList;
    private ICatchBluetoothManager bluetoothManager;
    private ICatchBluetoothAdapter bluetoothAdapter;
    private BluetoothListener bluetoothListener = new BluetoothListener();
    protected ICatchBluetoothClient tempClient;
    private BluetoothLEConnectionStateReceiver connectionStateReceiver;
    private Context appContext;
    private boolean scanning = false;
    private Timer searchTimer;
    FragmentManager fragmentManager;

    public BTPairBeginPresenter(Activity activity, Context appContext, Handler launchHandler, FragmentManager fm) {
        this.activity = activity;
        this.launchHandler = launchHandler;
        this.appContext = appContext;
        this.fragmentManager = fm;

    }

    public void setView(BTPairBeginFragmentView pairBeginFragmentView) {
        this.pairBeginFragmentView = pairBeginFragmentView;
    }

    public void loadBtList() {
        getBluetoothManager();
        handler.obtainMessage(REQUEST_BLUETOOTH_SCAN).sendToTarget();
    }

    public void searchBluetooth() {
        updateBindedDeviceToUI();
        handler.obtainMessage(REQUEST_BLUETOOTH_SCAN).sendToTarget();
    }


    public void connectBT(int position) {
        position = position - 1;
        curListViewPointer = position;
        AppLog.d(TAG, "bluetoothListView OnItemClick position=" + position);
        AppLog.d(TAG, "bluetoothListView OnItemClick GlobalInfo.isBLE=" + GlobalInfo.isBLE);

        closeScan();
        if (GlobalInfo.isBLE) {
            handler.obtainMessage(GET_BLUETOOTH_CLIENT, bluetoothDeviceList.get(position).getBluetoothAddr()).sendToTarget();
        } else {
            try {
                if (bluetoothDeviceList.get(position).getBluetoothConnect() == true) {
                    handler.obtainMessage(GET_BLUETOOTH_CLIENT, bluetoothDeviceList.get(position).getBluetoothAddr()).sendToTarget();
                } else {

                    AppLog.d("tigertiger", "GET_BLUETOOTH_CLIENT  position== " + position);
                    AppLog.d("tigertiger", "GET_BLUETOOTH_CLIENT  bluetoothDeviceList.get(position).getBluetoothAddr()== "
                            + bluetoothDeviceList.get(position).getBluetoothAddr());
                    boolean retValue = bluetoothManager.createBond(bluetoothDeviceList.get(position).getBluetoothAddr());
                    AppLog.d("tigertiger", "GET_BLUETOOTH_CLIENT  retValue== " + retValue);
                    if (retValue == false) {
                        MyToast.show(activity, "create bounded failed.");
                        return;
                    }
                    MyProgressDialog.showProgressDialog(activity, "blinding...");
                }

            } catch (Exception e) {
                e.printStackTrace();
                MyToast.show(activity, "create bounded Exception.");
            }
        }
    }

    public void unregister() {
        bluetoothManager.unregisterBroadcastReceiver(userBroadcastReceiver);
        bluetoothManager.unregisterBroadcastReceiver(connectionStateReceiver);
    }

    private void closeScan() {
        if (scanning == true) {
            scanning = false;
            bluetoothAdapter.stopDiscovery();
        }
    }

    private void startScan() {
        AppLog.d(TAG, "startScan  isBLE=" + GlobalInfo.isBLE);
        if (GlobalInfo.isBLE) {
            pairBeginFragmentView.setListHeader(R.string.text_ble_devices);
        } else {
            pairBeginFragmentView.setListHeader(R.string.text_classic_bluetooth_devices);
        }
        if (scanning == false && bluetoothAdapter != null) {

            scanning = true;
            if (bluetoothDeviceList == null) {
                bluetoothDeviceList = new LinkedList<BluetoothAppDevice>();
            }

            if (blueToothListAdapter != null) {
                blueToothListAdapter.notifyDataSetChanged();
            }
            try {
                bluetoothAdapter.startDiscovery(bluetoothListener, GlobalInfo.isBLE);
            } catch (IchBluetoothDeviceBusyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void updateBindedDeviceToUI() {
        List<ICatchBluetoothDevice> devices = bluetoothManager.getBondedDevices();
        if (bluetoothDeviceList == null) {
            bluetoothDeviceList = new LinkedList<BluetoothAppDevice>();
        }
        if (bluetoothDeviceList.size() > 0) {
            bluetoothDeviceList.clear();
        }
        for (ICatchBluetoothDevice tempDevice : devices) {
            AppLog.d(TAG, "BT Name=" + tempDevice.getName());
            //以字符串名过滤 bt list
//			if(tempDevice.getName().startsWith(AppInfo.BT_LIST_FILTER_WITH_STARTSTRING)){
//				bluetoothDeviceList.add(new BluetoothAppDevice(tempDevice.getName(), tempDevice.getAddress(), true));
//
//			}
            bluetoothDeviceList.add(new BluetoothAppDevice(tempDevice.getName(), tempDevice.getAddress(), true, false));

        }
        if (blueToothListAdapter == null) {
            blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
        } else {
            blueToothListAdapter.notifyDataSetInvalidated();
            blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
        }
        pairBeginFragmentView.setBTListViewAdapter(blueToothListAdapter);
    }

    private void getBluetoothManager() {
        try {
            bluetoothManager = ICatchBluetoothManager.getBluetoothManager(appContext);
        } catch (IchBluetoothNotSupportedException e) {
            e.printStackTrace();

        } catch (IchBluetoothContextInvalidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.d(TAG, "End getBluetoothManager() bluetoothManager=" + bluetoothManager);
        bluetoothAdapter = bluetoothManager.getBluetoothAdapter();

        if (!bluetoothManager.isBluetoothEnabled()) {
            bluetoothManager.enableBluetooth();
        }
        while (!bluetoothManager.isBluetoothEnabled()) {
            // finish();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        AppLog.d(TAG, "End sleep()");

        // register
        List<String> intentFilter = new LinkedList<String>();
        intentFilter.add(ICatchBroadcastReceiverID.BT_ACTION_BOND_STATE_CHANGED);
        bluetoothManager.registerBroadcastReceiver(userBroadcastReceiver, intentFilter);
        connectionStateReceiver = new BluetoothLEConnectionStateReceiver(handler);

        List<String> intentFilter1 = new LinkedList<String>();
        intentFilter1.add(ICatchBroadcastReceiverID.BT_LE_GATT_ACTION_CONNECTION_STATE_CHANGED);
        bluetoothManager.registerBroadcastReceiver(connectionStateReceiver, intentFilter1);
        if (!GlobalInfo.isBLE) {
            updateBindedDeviceToUI();
        }
        AppLog.d(TAG, "End registerBroadcastReceiver()");
    }


    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_BLUETOOTH_SCAN:
                    AppLog.d(TAG, "REQUEST_BLUETOOTH_SCAN");
                    startScan();
                    startSearchTimeoutTimer();
                    MyProgressDialog.showProgressDialog(activity, "Search...");
                    break;
                case BLUETOOTH_SCAN_TIME_OUT:
                    closeScan();
                    MyProgressDialog.closeProgressDialog();
//                    AppDialog.showDialogWarn(activity, "Don't find new bluetooth device!");
                    break;
                case GET_BLUETOOTH_DEVICE:
                    MyProgressDialog.closeProgressDialog();
                    //start 移除已绑定但没有开启的蓝牙设备;
                    LinkedList<BluetoothAppDevice> tempDeviceList = new LinkedList<BluetoothAppDevice>();
                    for (BluetoothAppDevice temp : bluetoothDeviceList) {
                        if (temp.getBluetoothExist()) {
                            tempDeviceList.add(temp);
                        }
                    }
                    bluetoothDeviceList = tempDeviceList;
//				bluetoothDeviceList.addAll(tempDeviceList);
                    //end 移除已绑定但没有开启的蓝牙设备;
                    if (blueToothListAdapter == null) {
                        blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
                    } else {
                        blueToothListAdapter.notifyDataSetInvalidated();
                        blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
                    }
                    pairBeginFragmentView.setBTListViewAdapter(blueToothListAdapter);
                    break;
                case GET_BLUETOOTH_CLIENT:
                    AppLog.d(TAG, "GET_BLUETOOTH_CLIENT curListViewPointer=" + curListViewPointer);
                    // showProgressDiag("Blind successly！Open camera's wifi...");
                    MyProgressDialog.showProgressDialog(activity, "Connecting...");
                    //ICOM-3446 20161009
                    if (!GlobalInfo.isNeedGetBTClient) {
                        AppLog.d(TAG, "isNeedGetBTClient =" + GlobalInfo.isNeedGetBTClient);
                        break;
                    }
                    GlobalInfo.isNeedGetBTClient = false;
                    String mac = (String) msg.obj;
                    executor = Executors.newSingleThreadExecutor();
                    executor.submit(new GetBtClientThread(handler, mac), null);
                    break;
                case CONNECT_WIFI_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "failed to connect wifi.");
                    break;

                case CONNECT_CAMERA_FAILED:
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "failed to connect camera.");
                    break;

                case GET_BLUETOOTH_CLIENT_SUCCESS:
                    AppLog.d(TAG, "Receive GET_BLUETOOTH_CLIENT_SUCCESS");
                    MyProgressDialog.closeProgressDialog();
                    if (!GlobalInfo.isBLE) {
                        BTPairSetupFragment fragment = new BTPairSetupFragment();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.launch_setting_frame, fragment);
                        ft.addToBackStack("BTPairSetupFragment");
                        ft.commit();
                    }
                    break;

                case GET_BLUETOOTH_CLIENT_FAILED:
                    AppLog.d(TAG, "Receive GET_BLUETOOTH_CLIENT_FAILED");
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "connect failed,please tryagain!");
                    break;

                case ICatchBroadcastReceiverID.BT_LE_GATT_SERVICES_DISCOVERED: {
                    AppLog.d(TAG, "Receive BT_LE_GATT_SERVICES_DISCOVERED");
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "ble service discovered.");
                    break;
                }
                case ICatchBroadcastReceiverID.BT_LE_GATT_NO_SERVICES_DISCOVERED: {
                    AppLog.d(TAG, "Receive BT_LE_GATT_NO_SERVICES_DISCOVERED");
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "ble no service discovered.");
                    break;
                }
                case ICatchBroadcastReceiverID.BT_LE_GATT_CONNECTED: {
                    AppLog.d(TAG, "Receive BT_LE_GATT_CONNECTED");
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "ble device connected.");

                    // 绑定蓝牙成功后跳转到wifi设置界面;
                    if (GlobalInfo.isBLE) {
                        BTPairSetupFragment fragment = new BTPairSetupFragment();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.launch_setting_frame, fragment);
                        ft.addToBackStack("BTPairSetupFragment");
                        ft.commit();
                    }
                    break;
                }
                case ICatchBroadcastReceiverID.BT_LE_GATT_DISCONNECTED: {
                    AppLog.d(TAG, "Receive BT_LE_GATT_DISCONNECTED");
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "fatal error, ble device not connected, please tryagain.");
                    break;
                }
                default:
            }
        }
    };

    private class BluetoothListener implements ICatchBTDeviceDetectedListener {

        @Override
        public void deviceDetected(ICatchBluetoothDevice arg0) {
            // TODO Auto-generated method stubd
            AppLog.d(TAG, "get new device name =" + arg0.getName());
            boolean isExistSameDevice = false;
            if (bluetoothDeviceList == null) {
                bluetoothDeviceList = new LinkedList<BluetoothAppDevice>();
            }
            for (BluetoothAppDevice temp : bluetoothDeviceList) {
                if ((arg0.getAddress()).equals(temp.getBluetoothAddr())) {
                    isExistSameDevice = true;
                    temp.setBluetoothExist(true);
                    break;
                }
            }
            if (isExistSameDevice == false) {
                bluetoothDeviceList.add(new BluetoothAppDevice(arg0.getName(), arg0.getAddress(), arg0.isBonded(), true));
                handler.obtainMessage(GET_BLUETOOTH_DEVICE).sendToTarget();
            }
        }
    }

    private void startSearchTimeoutTimer() {
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        searchTimer = new Timer();
        TimerTask searchTask = new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(BLUETOOTH_SCAN_TIME_OUT).sendToTarget();
            }
        };
        searchTimer.schedule(searchTask, 4000);
    }

    private ICatchBroadcastReceiver userBroadcastReceiver = new ICatchBroadcastReceiver() {

        @Override
        public void onReceive(Intent intent) {

            String action = intent.getAction();

            if (action.equals(ICatchBroadcastReceiverID.BT_ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(ICatchBroadcastReceiverID.BT_BOND_STATE, ICatchBroadcastReceiverID.BT_BOND_STATE_NONE);
                String mac = intent.getStringExtra(ICatchBroadcastReceiverID.BT_ADAPTER_ADDRESS);
                AppLog.d(TAG, "blue tooth bondState =" + bondState);
                AppLog.d(TAG, "blue tooth mac =" + mac);
                applyUIBondState(bondState, mac);
            }
        }
    };

    private void applyUIBondState(int bondState, String mac) {
        switch (bondState) {
            case ICatchBroadcastReceiverID.BT_BOND_STATE_NONE:
                AppLog.d(TAG, "applyUIBondState BT_BOND_STATE_NONE");
                MyProgressDialog.closeProgressDialog();
                MyToast.show(activity, "failed to bounded.");
                break;
            case ICatchBroadcastReceiverID.BT_BOND_STATE_BONDING:

                break;
            case ICatchBroadcastReceiverID.BT_BOND_STATE_BONDED:
                AppLog.d(TAG, "applyUIBondState BT_BOND_STATE_BONDED");
                MyProgressDialog.closeProgressDialog();
                MyToast.show(activity, "Bounded is ok");
                for (BluetoothAppDevice temp : bluetoothDeviceList) {
                    if (mac.equals(temp.getBluetoothAddr())) {
                        temp.setBluetoothConnect(true);
                        break;
                    }
                }
                if (blueToothListAdapter == null) {
                    blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
                } else {
                    blueToothListAdapter.notifyDataSetInvalidated();
                    blueToothListAdapter = new BlueToothListAdapter(activity, bluetoothDeviceList, GlobalInfo.isBLE);
                }
                pairBeginFragmentView.setBTListViewAdapter(blueToothListAdapter);
                handler.obtainMessage(GET_BLUETOOTH_CLIENT, mac).sendToTarget();
                break;
        }
    }


    public class BluetoothLEConnectionStateReceiver implements ICatchBroadcastReceiver {
        private Handler handler = null;

        public BluetoothLEConnectionStateReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(Intent intent) {
            String action = intent.getAction();
            if (action.equals(ICatchBroadcastReceiverID.BT_LE_GATT_ACTION_CONNECTION_STATE_CHANGED)) {
                int connectionState = intent.getIntExtra(ICatchBroadcastReceiverID.BT_LE_GATT_CONNECTION_STATE, ICatchBroadcastReceiverID
                        .BT_LE_GATT_DISCONNECTED);
                Message message = new Message();
                message.what = connectionState;
                handler.sendMessage(message);
                return;
            }
        }
    }

    class GetBtClientThread implements Runnable {
        Handler handler = null;
        String mac;

        GetBtClientThread(Handler handler, String mac) {
            this.handler = handler;
            this.mac = mac;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "start GetBtClientThread curListViewPointer=" + curListViewPointer);
            tempClient = null;
            try {
                tempClient = bluetoothManager.getBluetoothClient(appContext, mac, GlobalInfo.isBLE);
                AppLog.d(TAG, "end getBluetoothClient tempClient=" + tempClient);
//				bluetoothManager.getBluetoothClient(context, macAddr, isBLE);
            } catch (IOException e) {
                AppLog.d(TAG, "getBluetoothClient IOException--");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //ICOM-3446 Modify by b.jiang 20160926
            if (tempClient == null) {
                AppLog.d(TAG, "getBluetoothClient is null");
                GlobalInfo.isNeedGetBTClient = true;
                handler.obtainMessage(GET_BLUETOOTH_CLIENT_FAILED, null).sendToTarget();
                return;
            }
            GlobalInfo.iCatchBluetoothClient = tempClient;
            GlobalInfo.curBtDevice = bluetoothDeviceList.get(curListViewPointer);
            handler.obtainMessage(GET_BLUETOOTH_CLIENT_SUCCESS, null).sendToTarget();
        }
    }
}
