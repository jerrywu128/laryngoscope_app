package com.honestmc.laryngoscopeapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.honestmc.laryngoscopeapp.AppDialog.AppDialog;
import com.honestmc.laryngoscopeapp.AppInfo.AppInfo;
import com.honestmc.laryngoscopeapp.AppInfo.AppSharedPreferences;
import com.honestmc.laryngoscopeapp.Beans.SearchedCameraInfo;
import com.honestmc.laryngoscopeapp.Beans.SelectedCameraInfo;
import com.honestmc.laryngoscopeapp.Dbl.DatabaseHelper;
import com.honestmc.laryngoscopeapp.ExtendComponent.MyProgressDialog;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Listener.WifiListener;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Message.AppMessage;
import com.honestmc.laryngoscopeapp.Mode.CameraNetworkMode;
import com.honestmc.laryngoscopeapp.Model.Implement.SDKEvent;
import com.honestmc.laryngoscopeapp.Model.Implement.SDKSession;
import com.honestmc.laryngoscopeapp.MyCamera.MyCamera;
import com.honestmc.laryngoscopeapp.Presenter.Interface.BasePresenter;
import com.honestmc.laryngoscopeapp.PropertyId.PropertyId;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.SdkApi.CameraProperties;
import com.honestmc.laryngoscopeapp.SystemInfo.MWifiManager;
import com.honestmc.laryngoscopeapp.ThumbnailGetting.ThumbnailOperation;
import com.honestmc.laryngoscopeapp.Tools.FileOpertion.MFileTools;
import com.honestmc.laryngoscopeapp.Tools.LruCacheTool;
import com.honestmc.laryngoscopeapp.Tools.StorageUtil;
import com.honestmc.laryngoscopeapp.View.Activity.LaunchActivity;
import com.honestmc.laryngoscopeapp.View.Activity.ConnectCMActivity;
import com.honestmc.laryngoscopeapp.View.Activity.PreviewActivity;
import com.honestmc.laryngoscopeapp.View.Interface.LaunchView;
import com.icatch.wificam.customer.type.ICatchEventID;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yh.zhang C001012 on 2015/11/12 14:51.
 */
public class LaunchPresenter extends BasePresenter {

    private LaunchView launchView;
    // private CameraSlotAdapter cameraSlotAdapter;
    //private ArrayList<CameraSlot> camSlotList;
    private final LaunchHandler launchHandler = new LaunchHandler();
    private static final String TAG = "LaunchPresenter";
    private Activity activity;
    private SDKEvent sdkEvent;
    private LinkedList<SelectedCameraInfo> searchCameraInfoList;
    private MyCamera currentCamera;
    private int cameraSlotPosition;
    private WifiListener wifiListener;


    public LaunchPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(LaunchView launchView) {
        this.launchView = launchView;
        initCfg();
    }

    @Override
    public void initCfg() {
//        AppLog.enableAppLog();
        GlobalInfo.getInstance().setCurrentApp(activity);
        // never sleep when run this activity
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//         do not display menu bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        CrashHandler.getInstance().init(activity);
//        ConfigureInfo.getInstance().initCfgInfo(activity.getApplicationContext());
        GlobalInfo.getInstance().startScreenListener();
        AppInfo.initAppdata(activity);
    }

//    @Override
//    public void submitAppInfo() {
//        GlobalInfo.getInstance().setCurrentApp(activity);
//    }

    public void addGlobalLisnter(int eventId, boolean forAllSession) {
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(launchHandler);
        }
        sdkEvent.addGlobalEventListener(eventId, forAllSession);
    }
    public void launchCamera() {
        // String wifiSsid = MWifiManager.getSsid(activity);


        new Thread(new Runnable() {
            public void run() {
                beginConnectCamera(MWifiManager.getIp(activity));
            }
        }).start();
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
    public void launchCamera(final int position) {
        String wifiSsid = MWifiManager.getSsid(activity);
        final String ip = MWifiManager.getIp(activity);
        /*if (camSlotList.get(position).isWifiReady) {
            MyProgressDialog.showProgressDialog(activity, activity.getResources().getString(R.string.action_processing));
            new Thread(new Runnable() {
                public void run() {
                    beginConnectCamera(position, ip);
                }
            }).start();
        } else {
            if (camSlotList.get(position).isOccupied) {
                AppDialog.showDialogWarn(activity, "Please connect camera wifi " + camSlotList.get(position).cameraName);
                //Toast.makeText(activity, "Please connect camera wifi " + camSlotList.get(position).cameraName, Toast.LENGTH_SHORT).show();

            } else if (!isRegistered(MWifiManager.getSsid(activity))) {
                MyProgressDialog.showProgressDialog(activity, activity.getResources().getString(R.string.action_processing));
                new Thread(new Runnable() {
                    public void run() {
                        beginConnectCamera(position, ip);
                    }
                }).start();
            } else {
                AppDialog.showDialogWarn(activity, "Camera " + wifiSsid + " has been registered");
                //Toast.makeText(activity, "Camera " + wifiSsid + " has been registered", Toast.LENGTH_LONG).show();
            }
        }*/
    }

    public void removeCamera(int position) {
        AppLog.i(TAG, "remove camera position = " + position);
        //tint id = camSlotList.get(position).id;
       //t DatabaseHelper.deleteCamera(id, position);
//        CameraSlotSQLite.getInstance().deleteByPosition(position);
        loadListview();
    }

    public void loadListview() {
        //need to update isReady status
//        camSlotList = CameraSlotSQLite.getInstance().getAllCameraSlotFormDb();
        /*camSlotList = DatabaseHelper.readCamera(activity);
        if (cameraSlotAdapter != null) {
            cameraSlotAdapter.notifyDataSetInvalidated();
        }
        cameraSlotAdapter = new CameraSlotAdapter(GlobalInfo.getInstance().getAppContext(),
                camSlotList, launchHandler, SystemInfo.getMetrics().heightPixels);
        launchView.setListviewAdapter(cameraSlotAdapter);*/
    }

    public void notifyListview() {
       /* if (cameraSlotAdapter != null) {
            cameraSlotAdapter.notifyDataSetChanged();
        }*/
    }

    //meet error in android 6.0
    public void loadLocalThumbnails() {
        String path = MFileTools.getNewestPhotoFromDirectory(StorageUtil.getDownloadPath(activity));
        launchView.setLocalPhotoThumbnail(path);


        if (MFileTools.getPhotosSize(StorageUtil.getDownloadPath(activity)) > 0) {
            launchView.setNoPhotoFilesFoundVisibility(View.GONE);
            launchView.setPhotoClickable(true);
        } else {
            launchView.setNoPhotoFilesFoundVisibility(View.VISIBLE);
            launchView.setPhotoClickable(false);
        }

        path = MFileTools.getNewestVideoFromDirectory(StorageUtil.getDownloadPath(activity));
//        launchView.setLocalVideoThumbnail(path);
        //thumbnail = BitmapTools.getImageThumbnail(path);
        Bitmap thumbnail = LruCacheTool.getInstance().getBitmapFromLruCache(path);
        if (thumbnail == null) {
            thumbnail = ThumbnailOperation.getVideoThumbnail(path);
        }

        if (thumbnail == null) {
            launchView.loadDefaultLocalVideooThumbnail();
        } else {
            launchView.setLocalVideoThumbnail(thumbnail);
            LruCacheTool.getInstance().addBitmapToLruCache(path, thumbnail);
        }

        if (MFileTools.getVideosSize(StorageUtil.getDownloadPath(activity)) > 0) {
            launchView.setNoVideoFilesFoundVisibility(View.GONE);
            launchView.setVideoClickable(true);
        } else {
            launchView.setNoVideoFilesFoundVisibility(View.VISIBLE);
            launchView.setVideoClickable(false);
        }
    }

    private void startSearchTimeoutTimer() {
        Timer searchTimer = new Timer();
        TimerTask searchTask = new TimerTask() {
            @Override
            public void run() {
                launchHandler.obtainMessage(AppMessage.MESSAGE_CAMERA_SCAN_TIME_OUT).sendToTarget();
            }
        };
        searchTimer.schedule(searchTask, 5000);
    }

    private void showSearchCameraListSingleDialog() {
        if (searchCameraInfoList.isEmpty()) {
            return;
        }
        CharSequence title = "Please selectOrCancelAll camera";
        final CharSequence[] tempsearchCameraInfoList = new CharSequence[searchCameraInfoList.size()];

        for (int ii = 0; ii < tempsearchCameraInfoList.length; ii++) {
            tempsearchCameraInfoList[ii] = searchCameraInfoList.get(ii).cameraName + "\n" + searchCameraInfoList.get(ii).cameraIp
                    + "          " + CameraNetworkMode.getModeConvert(searchCameraInfoList.get(ii).cameraMode);

        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                //appStartHandler.obtainMessage(GlobalInfo.MESSAGE_CAMERA_SEARCH_SELECTED, searchCameraInfoList.get(which)).sendToTarget();
            }
        };
        showOptionDialogSingle(title, tempsearchCameraInfoList, 0, listener, true);
    }

    private void showOptionDialogSingle(CharSequence title, CharSequence[] items, int checkedItem,
                                        DialogInterface.OnClickListener listener, boolean cancelable) {
        AlertDialog optionDialog = new AlertDialog.Builder(activity).setTitle(title)
                .setSingleChoiceItems(items, checkedItem, listener).create();
        optionDialog.setCancelable(cancelable);
        optionDialog.show();
    }

    public void initMenu() {
        launchView.setMenuSetIpVisibility(AppInfo.youtubeLive);
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
                    break;
                case AppMessage.MESSAGE_DELETE_CAMERA:
                    removeCamera(msg.arg1);
                    break;
                case AppMessage.MESSAGE_CAMERA_SCAN_TIME_OUT:
                    AppLog.i(TAG, "MESSAGE_CAMERA_SCAN_TIME_OUT:count =" + searchCameraInfoList.size());
                    SDKSession.stopDeviceScan();
                    sdkEvent.delGlobalEventListener(ICatchEventID.ICATCH_EVENT_DEVICE_SCAN_ADD, false);
                    MyProgressDialog.closeProgressDialog();
                    if (searchCameraInfoList.isEmpty()) {
                        Toast.makeText(activity, R.string.alert_no_camera_found, Toast.LENGTH_LONG).
                                show();
                        break;
                    }
                    showSearchCameraListSingleDialog();
                    break;
                case SDKEvent.EVENT_SEARCHED_NEW_CAMERA:
                    SearchedCameraInfo temp = (SearchedCameraInfo) msg.obj;
                    searchCameraInfoList.addLast(new SelectedCameraInfo(temp.cameraName, temp.cameraIp, temp.cameraMode, temp.uid));
                    break;
                case AppMessage.MESSAGE_CAMERA_CONNECTING_START:
                    AppLog.i(TAG, "MESSAGE_CAMERA_CONNECTING_START");
                    launchView.fragmentPopStackOfAll();
                    launchCamera(cameraSlotPosition);
                    break;
                case AppMessage.MESSAGE_DISCONNECTED:
                    AppLog.i(TAG, "receive MESSAGE_DISCONNECTED");
                    resetWifiState();
                    notifyListview();
//                    loadListview();
                    break;
                case AppMessage.MESSAGE_CONNECTED:
                    launchHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String ssid = MWifiManager.getSsid(activity);
                            if(ssid != null){
                                setWifiState(true, ssid);
                                notifyListview();
                            }
                        }
                    },800);

//                    loadListview();
                    break;
            }
        }
    }



    void setWifiState(boolean isWifiReady, String ssid) {
        if (ssid == null || ssid.equals("")) {
            return;
        }
        /*for (CameraSlot temp : camSlotList) {
            if (temp.cameraName != null && temp.cameraName.equals(ssid)) {
                temp.isWifiReady = isWifiReady;
                break;
            }
        }*/
    }

    void resetWifiState() {
       /* for (CameraSlot temp : camSlotList) {
            temp.isWifiReady = false;
        }*/
    }

    public void showSettingIpDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText ipName = new EditText(context);
        String ip = AppSharedPreferences.readIp(activity);
        if (ip != null) {
            ipName.setText(ip);
        }
        builder.setTitle("Please Input ip:");
        builder.setNegativeButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String temp1 = ipName.getText().toString();
                if (temp1.length() > 20) {
                    Toast.makeText(context, R.string.camera_name_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                AppSharedPreferences.writeDataByName(activity, AppSharedPreferences.OBJECT_NAME_INPUT_IP, temp1);
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, temp1, Toast.LENGTH_LONG).show();
            }
        });
        builder.setView(ipName);
        builder.setCancelable(false);
        builder.create().show();
    }

    public void showLicenseAgreementDialog() {
        SharedPreferences preferences = activity.getSharedPreferences("appData", MODE_PRIVATE);
        boolean isAgreeLicenseAgreement = preferences.getBoolean("agreeLicenseAgreement", false);
        AppLog.d(TAG, "showLicenseAgreementDialog isAgreeLicenseAgreement=" + isAgreeLicenseAgreement);
        String AgreeLicenseAgreementVersion = preferences.getString("agreeLicenseAgreementVersion", "");
        AppLog.d(TAG, "showLicenseAgreementDialog Version =" + AgreeLicenseAgreementVersion);

        if ((!isAgreeLicenseAgreement) || (!AppInfo.EULA_VERSION.equalsIgnoreCase(AgreeLicenseAgreementVersion))) {
            AppDialog.showLicenseAgreementDialog(activity, AppInfo.EULA_VERSION);
        }

    }

    public void setWifiwithQrcode(LaunchActivity LA,String qrdata){

        Intent mainIntent = new Intent(LA, ConnectCMActivity.class);
        Bundle bundle = new Bundle();

        String password;
        String passwordTemp = qrdata.substring(qrdata
                .indexOf("P:"));
        password = passwordTemp.substring(2,
                passwordTemp.indexOf(";"));

        String ssid;
        String ssidTemp = qrdata.substring(qrdata
                .indexOf("S:"));
        ssid = ssidTemp.substring(2,
                ssidTemp.indexOf(";"));

        String type;
        String typeTemp = qrdata.substring(qrdata
                .indexOf("T:"));
        type = typeTemp.substring(2,
                typeTemp.indexOf(";"));


        bundle.putString("ssid",ssid);
        bundle.putString("pw",password);
        bundle.putString("tp",type);
        mainIntent.putExtras(bundle);

        LA.startActivity(mainIntent);
    }

    public boolean isWifienabled(){
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            return true;
        }
        else
            return false;
    }


    public boolean checkMobiledata(){
        TelephonyManager telMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);



        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);

        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//CHECK SIMCARD
            int simmain=telMgr.getSimState(0);
            int simsecond=telMgr.getSimState(1);
            if(simmain==TelephonyManager.SIM_STATE_ABSENT&&simsecond==TelephonyManager.SIM_STATE_ABSENT){
                mobileDataEnabled=false;
            }
        }
        else{
            int simstate=telMgr.getSimState();
            if(simstate==TelephonyManager.SIM_STATE_ABSENT){
                mobileDataEnabled=false;
            }
        }

        return mobileDataEnabled;
    }

    public void checkFirstInapp(){
        //???????????????????????????app????????????IQ????????????
        Boolean isFirstIn = false;
        SharedPreferences pref = activity.getSharedPreferences("myActivityName", 0);
        isFirstIn = pref.getBoolean("isFirstIn", true);
        if(isFirstIn){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("IQ_password", "password");
            editor.commit();
        }
        //????????????????????????(???preview????????????????????????????????????)
        //SharedPreferences.Editor editor = pref.edit();
        //editor.putBoolean("isFirstIn", false);
        //editor.commit();
    }

}
