package com.icatch.sbcapp.View.Activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
//import android.support.v7.app.ActionBar;
//import android.support.v7.widget.Toolbar;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.internal.Constants;
import com.icatch.sbcapp.Adapter.SettingListAdapter;
import com.icatch.sbcapp.ExtendComponent.MPreview;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.ExtendComponent.ZoomView;
import com.icatch.sbcapp.Function.MediaCaptureService;
import com.icatch.sbcapp.Function.MediaRecordService;
import com.icatch.sbcapp.Function.mediaScreenRecord;
import com.icatch.sbcapp.Listener.OnDecodeTimeListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.PreviewLaunchMode;
import com.icatch.sbcapp.Mode.PreviewMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.Presenter.PreviewPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.View.Interface.PreviewView;
import com.icatch.sbcapp.Function.mediaScreenCapture;

public class PreviewActivity extends BaseActivity implements View.OnClickListener, PreviewView {

    private static final String TAG = "PreviewActivity";
    private PreviewPresenter presenter;
    private MPreview mPreview;
    private ImageView iqBtn;
    private ImageView pbBtn;
    private ImageView captureBtn;
    private ImageView wbStatus;
    private ImageView burstStatus;
    private ImageView wifiStatus;
    private ImageView batteryStatus;
    private ImageView timelapseMode;
    private ImageView slowMotion;
    private ImageView carMode;
    private ImageView close_IQ;
    private ImageView close_seek_bar;
    private ImageView close_WB_IQ;
    private ImageView close_blcToggle_bar;
    private TextView recordingTime;
    private ImageView autoDownloadImagview;
    private TextView delayCaptureText;
    private RelativeLayout delayCaptureLayout;
    private TextView curPreviewInfoTxv;
   // private ZoomView zoomView;
    private RelativeLayout setupMainMenu;
    private ListView mainMenuList;
    private MenuItem settingMenu;
    //private ActionBar actionBar;
    private TextView noSupportPreviewTxv;
    private PopupWindow pvModePopupWindow;
    private RadioButton captureRadioBtn;
    private RadioButton videoRadioBtn;
    private RadioButton timepLapseRadioBtn;
    private ImageView pvModeBtn;
    private View contentView;
    //private Toolbar toolbar;
    private TextView decodeTimeTxv;
    private LinearLayout decodeTimeLayout;
    private Handler handler;
    private TextView decodeInfo;
    private Button youtubeLiveBtn;
    private Button googleAccountBtn;
    private Button cameraSwitchBtn;
    private LinearLayout youTubeLiveLayout;
    private LinearLayout cameraSwitchLayout;
    private RelativeLayout pb_IQ;
    private RelativeLayout buttom_bar;
    private RelativeLayout quality_bar;
    private RelativeLayout WB_change_IQ;
    private RelativeLayout BLC_change_IQ;
    private Button brightness_BT, HUE_BT,saturation_BT,white_balance_BT,BLC_BT;
    private Button WB_AUTO,WB_DAYLIGHT,WB_CLOUDY,WB_TUNGSTEN,WB_FLOURESCENT_H,CHANGE_IQ_PASSWORD;
    private TextView quality_name,seekbar_value;
    private SeekBar seekBar;
    private int[] progress_save;
    private ToggleButton blc_toggle;
    private boolean BLCtoggle_status;
    private MediaProjectionManager mMediaProjectionManager;
    private static final int PHOTO_REQUEST_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 101;
    private static MediaProjection mMediaProjection;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private mediaScreenRecord mMediaScreenRecord =null;
    private Intent service;

    private static Intent photostaticIntentData,videostaticIntentData;
    private static int photostaticResultCode,videostaticResultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppLog.d(TAG, "1122 onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
/*      toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        //t actionBar.setTitle(R.string.title_preview);
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
*/
        presenter = new PreviewPresenter(PreviewActivity.this);
        presenter.setView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(
                    Context.MEDIA_PROJECTION_SERVICE);
        }


        cameraSwitchLayout = (LinearLayout) findViewById(R.id.camera_switch_layout);
        cameraSwitchBtn = (Button) findViewById(R.id.camera_switch_btn);
        decodeTimeTxv = (TextView) findViewById(R.id.decodeTimeTxv);
        decodeTimeLayout = (LinearLayout) findViewById(R.id.decode_time_layout);
        decodeInfo = (TextView) findViewById(R.id.decode_info);
        mPreview = (MPreview) findViewById(R.id.m_preview);
        mPreview.setOnClickListener(this);
        pbBtn = (ImageView) findViewById(R.id.multi_pb);
        pbBtn.setOnClickListener(this);
        captureBtn = (ImageView) findViewById(R.id.doCapture);
        captureBtn.setOnClickListener(this);
        iqBtn = (ImageView) findViewById(R.id.image_quality);
        iqBtn.setOnClickListener(this);
        close_IQ = (ImageView)findViewById(R.id.close_IQ_bar);
        close_IQ.setOnClickListener(this);
        close_seek_bar = (ImageView) findViewById(R.id.close_seekbar);
        close_seek_bar.setOnClickListener(this);
        close_WB_IQ = (ImageView) findViewById(R.id.close_WB);
        close_WB_IQ.setOnClickListener(this);
        close_blcToggle_bar = (ImageView) findViewById(R.id.close_toggle);
        close_blcToggle_bar.setOnClickListener(this);
        pb_IQ = (RelativeLayout)findViewById(R.id.pb_IQ);
        WB_change_IQ = (RelativeLayout)findViewById(R.id.WB_IQ);
        BLC_change_IQ = (RelativeLayout) findViewById(R.id.blc_bar);
        buttom_bar = (RelativeLayout) findViewById(R.id.buttomBar);
        quality_bar = (RelativeLayout) findViewById(R.id.quality_bar);
        quality_name = (TextView) findViewById(R.id.quality_name);
        seekbar_value = (TextView) findViewById(R.id.seekbar_value);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        blc_toggle = (ToggleButton) findViewById(R.id.blc_toggle);
        blc_toggle.setOnCheckedChangeListener(new ToggleListener());
        /**IQ_BT*/
        brightness_BT = (Button)findViewById(R.id.brightness_bt);
        HUE_BT = (Button)findViewById(R.id.hue_bt);
        saturation_BT = (Button)findViewById(R.id.saturation_bt);
        white_balance_BT = (Button)findViewById(R.id.white_balance_bt);
        BLC_BT = (Button)findViewById(R.id.BLC_bt);
        CHANGE_IQ_PASSWORD = (Button)findViewById(R.id.change_iq_pwd_bt);

        brightness_BT.setOnClickListener(this);
        HUE_BT.setOnClickListener(this);
        saturation_BT.setOnClickListener(this);
        white_balance_BT.setOnClickListener(this);
        BLC_BT.setOnClickListener(this);
        CHANGE_IQ_PASSWORD.setOnClickListener(this);

        progress_save = new int[3];
        progress_save[0]=128;//brightness
        progress_save[1]=128;//saturation
        progress_save[2]=0;//hue
        BLCtoggle_status=false;

        /**----------------------------*/
        /**WB_IQ*/
        WB_AUTO =(Button)findViewById(R.id.WB_AUTO);
        WB_DAYLIGHT=(Button)findViewById(R.id.WB_DAYLIGHT);
        WB_CLOUDY=(Button)findViewById(R.id.WB_CLOUDY);
        WB_TUNGSTEN=(Button)findViewById(R.id.WB_INCADESCENT);
        WB_FLOURESCENT_H=(Button)findViewById(R.id.WB_FLOURESCENT_H);

        WB_AUTO.setOnClickListener(this);
        WB_DAYLIGHT.setOnClickListener(this);
        WB_CLOUDY.setOnClickListener(this);
        WB_TUNGSTEN.setOnClickListener(this);
        WB_FLOURESCENT_H.setOnClickListener(this);



        /**----------------------------*/



        wbStatus = (ImageView) findViewById(R.id.wb_status);
        burstStatus = (ImageView) findViewById(R.id.burst_status);
        wifiStatus = (ImageView) findViewById(R.id.wifi_status);
        batteryStatus = (ImageView) findViewById(R.id.battery_status);
        timelapseMode = (ImageView) findViewById(R.id.timelapse_mode);
        slowMotion = (ImageView) findViewById(R.id.slow_motion);
        carMode = (ImageView) findViewById(R.id.car_mode);
        recordingTime = (TextView) findViewById(R.id.recording_time);
        autoDownloadImagview = (ImageView) findViewById(R.id.auto_download_imageview);
        delayCaptureText = (TextView) findViewById(R.id.delay_capture_text);
        delayCaptureLayout = (RelativeLayout) findViewById(R.id.delay_capture_layout);
        curPreviewInfoTxv = (TextView) findViewById(R.id.preview_info_txv);
        setupMainMenu = (RelativeLayout) findViewById(R.id.setupMainMenu);
        mainMenuList = (ListView) findViewById(R.id.setup_menu_listView);
        noSupportPreviewTxv = (TextView) findViewById(R.id.not_support_preview_txv);
        pvModeBtn = (ImageView) findViewById(R.id.pv_mode);

        contentView = LayoutInflater.from(PreviewActivity.this).inflate(R.layout.camer_mode_switch_layout, null);
        pvModePopupWindow = new PopupWindow(contentView,
                GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT, true);
        //ICOM-4096 begin add by b.jiang 20170112
        pvModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
        pvModePopupWindow.setFocusable(true);
        pvModePopupWindow.setOutsideTouchable(true);
        //ICOM-4096 end add by b.jiang 20170112
        captureRadioBtn = (RadioButton) contentView.findViewById(R.id.capture_radio);
        videoRadioBtn = (RadioButton) contentView.findViewById(R.id.video_radio);
        timepLapseRadioBtn = (RadioButton) contentView.findViewById(R.id.timeLapse_radio);
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                AppLog.d(TAG, "contentView onKey");
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        AppLog.d("AppStart", "contentView back");
                        if (pvModePopupWindow != null && pvModePopupWindow.isShowing()) {
                            AppLog.d("AppStart", "dismiss pvModePopupWindow");
                            pvModePopupWindow.dismiss();
                        }
                        break;
                }
                return true;
            }
        });







        cameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setCameraSwitch();
            }
        });
/*
        googleAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.gotoGoogleAccountManagement();
            }
        });

        youtubeLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startOrStopYouTubeLive();
            }
        });
*/




     /*test   zoomView = (ZoomView) findViewById(R.id.zoom_view);
        zoomView.setZoomInOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.zoomIn();
            }
        });

        zoomView.setZoomOutOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.zoomOut();
            }
        });

        zoomView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.zoomBySeekBar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.zoomBySeekBar();
            }
        });
*/
        pvModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showPvModePopupWindow();
            }
        });

        captureRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_STILL_MODE);
            }
        });

        videoRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_VIDEO_MODE);
            }
        });
        timepLapseRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePreviewMode(PreviewMode.APP_STATE_TIMELAPSE_MODE);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int temp=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_value.setText("" + progress + "");
                temp = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                AppLog.d(TAG, "start Seek");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppLog.d(TAG, "stop Seek");
                presenter.setIQvalue(temp);
            }
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9999);

        }



    }



    @Override
    protected void onStart() {
        super.onStart();
        presenter.initUI();
    }

    @Override
    protected void onResume() {
        AppLog.d(TAG, "1122 onResume");
        super.onResume();
        presenter.initData();
        presenter.submitAppInfo();
        presenter.initPreview();
        presenter.initStatus();
        presenter.addEvent();
        handler = new Handler();
    }

    @Override
    protected void onStop() {
        AppLog.d(TAG, "1122 onStop");
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:

                AppLog.d("AppStart", "back");
                if (pvModePopupWindow != null && pvModePopupWindow.isShowing()) {
                    AppLog.d("AppStart", "dismiss pvModePopupWindow");
                    pvModePopupWindow.dismiss();
                } else {
                    presenter.finishActivity();

                }
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "1122 onDestroy");
        super.onDestroy();
        presenter.removeActivity();
        presenter.stopPreview();
        presenter.delEvent();
        presenter.stopMediaStream();
        presenter.destroyCamera();
        presenter.unregisterWifiSSReceiver();
        presenter.stopConnectCheck();
        //IC-758 Begin ADD by b.jiang 20161227
        presenter.resetState();
        //IC-758 End ADD by b.jiang 20161227
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent caservice = new Intent(this, MediaCaptureService.class);
            stopService(caservice);
            Intent reservice = new Intent(this, MediaRecordService.class);
            stopService(reservice);
        }
    }
/*
    @Override
    protected boolean onPrepareOptionsPanel(View view,Menu menu){
        return  super.onPrepareOptionsPanel(view,menu);
    }
*/
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up ImageButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AppLog.d(TAG, "id == android.R.id.home");
            if (pvModePopupWindow != null && pvModePopupWindow.isShowing()) {
                pvModePopupWindow.dismiss();
            } else {
                presenter.finishActivity();
            }
           //presenter.finishActivity();
        } /*else if (id == R.id.action_setting) {
            presenter.stopIQlayout(pb_IQ,buttom_bar,quality_bar,WB_change_IQ);
            settingMenu = item;
            presenter.loadSettingMenuList();
        }*/
        return super.onOptionsItemSelected(item);
    }

    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static class Utils {
        // 两次点击按钮之间的点击间隔不能少于3000毫秒
        private static final int MIN_CLICK_DELAY_TIME = 3000;
        private static long lastClickTime;

        public static boolean isFastClick() {
            boolean flag = false;
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                flag = true;
            }
            lastClickTime = curClickTime;
            return flag;
        }
    }

    private class ToggleListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonview, boolean ischecked) {
            if (ischecked) {
                presenter.openBLC();
                BLCtoggle_status=true;
            } else {
                presenter.closeBLC();
                BLCtoggle_status=false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        AppLog.i(TAG, "click the v.getId() =" + v.getId());
        switch (v.getId()) {
            case R.id.image_quality:
                AppLog.i(TAG, "click the image_quality");
                presenter.startIQlayout(pb_IQ,buttom_bar);
                break;
            case R.id.multi_pb:
                AppLog.i(TAG, "click the multi_pb");
                if(Utils.isFastClick()) { //防止重複點擊造成崩潰
                    presenter.redirectToAnotherActivity(PreviewActivity.this, MultiPbActivity.class);
                      //presenter.redirectToAnotherActivity(PreviewActivity.this,LocalPhotoWallActivity.class);
                    //presenter.redirectToAnotherActivity(PreviewActivity.this,LocalVideoWallActivity.class);
                }
                break;
            case R.id.doCapture:
                AppLog.i(TAG, "click the doCapture");
                presenter.startOrStopCapture();
                break;
            case R.id.m_preview:
                AppLog.i(TAG, "click the m_preview");
                presenter.showZoomView();
                break;
            case R.id.close_IQ_bar:
                AppLog.i(TAG, "click the close_IQ_bar");
                pb_IQ.setVisibility(View.GONE);
                buttom_bar.setVisibility((View.VISIBLE));
                break;
            case R.id.close_seekbar:
                AppLog.i(TAG, "click the close_seekbar");
                quality_bar.setVisibility(View.GONE);
                pb_IQ.setVisibility(View.VISIBLE);
                break;
            case R.id.close_toggle:
                AppLog.i(TAG, "click the close_toggle");
                BLC_change_IQ.setVisibility(View.GONE);
                pb_IQ.setVisibility(View.VISIBLE);
                break;
            case R.id.close_WB:
                AppLog.i(TAG, "click the close_WB");
                WB_change_IQ.setVisibility(View.GONE);
                pb_IQ.setVisibility(View.VISIBLE);
                break;
            case R.id.change_iq_pwd_bt:
                presenter.change_IQ_password();
                break;
            /**IQ_BT_OnClick*/
            case R.id.brightness_bt:
                seekBar.setMax(255);
                seekBar.setProgress(progress_save[0]);
                AppLog.i(TAG, "click the brightness_bt");
                presenter.openSeekbar(pb_IQ,quality_bar,v.getId(),quality_name);
                break;
            case R.id.hue_bt:
                seekBar.setMax(360);
                seekBar.setProgress(progress_save[2]);
                AppLog.i(TAG, "click the hue_bt");
                presenter.openSeekbar(pb_IQ,quality_bar,v.getId(),quality_name);
                break;
            case R.id.saturation_bt:
                seekBar.setMax(255);
                seekBar.setProgress(progress_save[1]);
                AppLog.i(TAG, "click the saturation_bt");
                presenter.openSeekbar(pb_IQ,quality_bar,v.getId(),quality_name);
                break;
            case R.id.white_balance_bt:
                AppLog.i(TAG, "click the white_balance_bt");
                presenter.openWB_IQ(pb_IQ,WB_change_IQ);
                break;
            case R.id.BLC_bt:
                AppLog.i(TAG, "click the BLC_bt");
                blc_toggle.setChecked(BLCtoggle_status);
                presenter.openBLC_IQ(pb_IQ,BLC_change_IQ);
                break;
            /**WB_Change*/
            case R.id.WB_AUTO:
                CameraProperties.getInstance().setWhiteBalance(1);
                setWbStatusIcon(R.drawable.awb_auto);
                MyToast.show(this, R.string.wb_auto);
                break;
            case R.id.WB_DAYLIGHT:
                CameraProperties.getInstance().setWhiteBalance(2);
                setWbStatusIcon(R.drawable.awb_daylight);
                MyToast.show(this, R.string.wb_daylight);
                break;
            case R.id.WB_CLOUDY:
                CameraProperties.getInstance().setWhiteBalance(3);
                setWbStatusIcon(R.drawable.awb_cloudy);
                MyToast.show(this, R.string.wb_cloudy);
                break;
            case R.id.WB_INCADESCENT:
                CameraProperties.getInstance().setWhiteBalance(5);
                setWbStatusIcon(R.drawable.awb_incadescent);
                MyToast.show(this, R.string.wb_incandescent);
                break;
            case R.id.WB_FLOURESCENT_H:
                CameraProperties.getInstance().setWhiteBalance(4);
                setWbStatusIcon(R.drawable.awb_fluoresecent);
                MyToast.show(this, R.string.wb_fluorescent);
                break;
            default:
                break;
        }
    }

    @Override
    public void startPhotolocalCapture(){

        if (SDK_INT >= Build.VERSION_CODES.Q) {
            if(photostaticIntentData == null) {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), PHOTO_REQUEST_CODE);
            }else{
                service.putExtra("code", photostaticResultCode);
                service.putExtra("data", photostaticIntentData);
                startForegroundService(service);// 启动前台服务
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), PHOTO_REQUEST_CODE);
        }
    }

    @Override
    public void startRecordlocalCapture(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(videostaticIntentData == null) {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), VIDEO_REQUEST_CODE);
            }else{
                Intent intent = new Intent(this, MediaRecordService.class);
                intent.putExtra("code",videostaticResultCode);
                intent.putExtra("data",videostaticIntentData);
                startForegroundService(intent); // 启动前台服务
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), VIDEO_REQUEST_CODE);

        }
    }

    @Override
    public void stopRecordlocalCapture(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            stopService(new Intent(this, MediaRecordService.class));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mMediaScreenRecord!=null){
                mMediaScreenRecord.stopRecorder();
            }else{
                MyToast.show(this, "null");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //會執行initPreview
        Log.d(TAG, "on result : requestCode = " + requestCode + " resultCode = " + resultCode);
        if (RESULT_OK == resultCode) {

            if(PHOTO_REQUEST_CODE == requestCode){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if(photostaticResultCode == 0 && photostaticIntentData == null) {
                        photostaticIntentData = data;
                        photostaticResultCode = resultCode;
                    }
                        Log.d(TAG, "Start capturing...");
                        service = new Intent(this, MediaCaptureService.class);
                        service.putExtra("code", resultCode);
                        service.putExtra("data", data);
                        startForegroundService(service);// 启动前台服务
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

                    if (mMediaProjection != null) {

                        Log.d(TAG, "Start capturing...");
                        new mediaScreenCapture(this, mMediaProjection).startProjection();
                    }
                }
            }else if(VIDEO_REQUEST_CODE == requestCode){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if(videostaticResultCode == 0 && videostaticIntentData == null) {
                        videostaticIntentData = data;
                        videostaticResultCode = resultCode;
                    }
                    Log.d(TAG, "Start recording...");
                    Intent intent = new Intent(this, MediaRecordService.class);
                    intent.putExtra("code",resultCode);
                    intent.putExtra("data",data);
                    startForegroundService(intent); // 启动前台服务
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                    Log.d(TAG, "Start recording...");
                    if (mMediaProjection != null) {
                        mMediaScreenRecord =  new mediaScreenRecord(this, mMediaProjection).startProjection();
                    }
                }
            }

        } else{
            MyToast.show(this, "取消拍攝");

        }
    }


    @Override
    public void setToggleStatus(boolean checked){
        BLCtoggle_status=checked;
    }

    @Override
    public void setProgressSave(int IQ,int value){
        switch (IQ){
            case 0:
                progress_save[0]=value;
                break;
            case 1:
                progress_save[1]=value;
                break;
            case 2:
                progress_save[2]=value;
                break;
            default:
                break;
        }
    }

    @Override
    public void setmPreviewVisibility(int visibility) {
        mPreview.setVisibility(visibility);
    }

    @Override
    public void setWbStatusVisibility(int visibility) {
        wbStatus.setVisibility(visibility);
    }

    @Override
    public void setBurstStatusVisibility(int visibility) {
        burstStatus.setVisibility(visibility);
    }

    @Override
    public void setWifiStatusVisibility(int visibility) {
        wifiStatus.setVisibility(visibility);
    }

    @Override
    public void setWifiIcon(int drawableId) {
        wifiStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void setBatteryStatusVisibility(int visibility) {
        batteryStatus.setVisibility(visibility);
    }

    @Override
    public void setBatteryIcon(int drawableId) {
        batteryStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void settimeLapseModeVisibility(int visibility) {
        timelapseMode.setVisibility(visibility);
    }

    @Override
    public void settimeLapseModeIcon(int drawableId) {
        timelapseMode.setBackgroundResource(drawableId);
    }

    @Override
    public void setSlowMotionVisibility(int visibility) {
        slowMotion.setVisibility(visibility);
    }

    @Override
    public void setCarModeVisibility(int visibility) {
        carMode.setVisibility(visibility);
    }

    @Override
    public void setRecordingTimeVisibility(int visibility) {
        recordingTime.setVisibility(visibility);
    }

    @Override
    public void setAutoDownloadVisibility(int visibility) {
        autoDownloadImagview.setVisibility(visibility);
    }

    @Override
    public void setCaptureBtnBackgroundResource(int id) {
        captureBtn.setImageResource(id);
    }

    @Override
    public void setRecordingTime(String laspeTime) {
        recordingTime.setText(laspeTime);
    }

    @Override
    public void setDelayCaptureLayoutVisibility(int visibility) {
        delayCaptureLayout.setVisibility(visibility);
    }

    @Override
    public void setDelayCaptureTextTime(String delayCaptureTime) {
        delayCaptureText.setText(delayCaptureTime);
    }

    @Override
    public void setBurstStatusIcon(int drawableId) {
        burstStatus.setBackgroundResource(drawableId);
    }

    @Override
    public void setWbStatusIcon(int drawableId) {
        wbStatus.setBackgroundResource(drawableId);
        wbStatus.setVisibility(View.GONE);
    }

    @Override
    public void setUpsideVisibility(int visibility) {
        carMode.setVisibility(visibility);
    }

    @Override
    public void startMPreview(MyCamera myCamera) {
        AppLog.d(TAG, "startMPreview");
        if (noSupportPreviewTxv.getVisibility() == View.VISIBLE) {
            noSupportPreviewTxv.setVisibility(View.GONE);
        }
        mPreview.setVisibility(View.VISIBLE);
        mPreview.start(myCamera, PreviewLaunchMode.RT_PREVIEW_MODE);
    }

    @Override
    public void stopMPreview(MyCamera myCamera) {
        mPreview.stop();
    }

    @Override
    public void setCaptureBtnEnability(boolean enablity) {
        captureBtn.setEnabled(enablity);
    }

    @Override
    public void setPreviewInfo(String info) {
        AppLog.i(TAG, "setPreviewInfo info=" + info);
        curPreviewInfoTxv.setText(info);
    }

    @Override
    public void setDecodeInfo(String info) {
//        decodeInfo.setText(info);
    }

    @Override
    public void setYouTubeLiveLayoutVisibility(int visibility) {
       // youTubeLiveLayout.setVisibility(visibility);
    }
    @Override
    public void setCameraSwitchLayoutVisibility(int visibility) {
        cameraSwitchLayout.setVisibility(visibility);
    }
    @Override
    public void setYouTubeBtnTxv(int resId) {
       // youtubeLiveBtn.setText(resId);
    }

    @Override
    public void setOnDecodeTimeListener(OnDecodeTimeListener onDecodeTimeListener) {
        mPreview.setOnDecodeTimeListener(onDecodeTimeListener);
    }

    @Override
    public void setDecodeTimeLayoutVisibility(int visibility) {
        decodeTimeLayout.setVisibility(visibility);
    }

    @Override
    public void setDecodeTimeTxv(String value) {
        decodeTimeTxv.setText(value);
    }

    @Override
    public void showZoomView() {
       //t zoomView.startDisplay();
    }

    @Override
    public void setMaxZoomRate(final int maxZoomRate) {
        //t zoomView.setMaxValue(maxZoomRate);
    }

    @Override
    public int getZoomViewProgress() {
        //treturn zoomView.getProgress();
        return 0;
    }

    @Override
    public int getZoomViewMaxZoomRate() {
        return ZoomView.MAX_VALUE;
    }

    @Override
    public void updateZoomViewProgress(int currentZoomRatio) {
      //  zoomView.updateZoomBarValue(currentZoomRatio);
    }

    @Override
    public int getSetupMainMenuVisibility() {
        return setupMainMenu.getVisibility();
    }

    @Override
    public void setSetupMainMenuVisibility(int visibility) {
        setupMainMenu.setVisibility(visibility);
    }

    @Override
    public void setAutoDownloadBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            autoDownloadImagview.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setActionBarTitle(int resId) {
       // actionBar.setTitle(resId);
    }

    @Override
    public void setSettingBtnVisible(boolean isVisible) {
        settingMenu.setVisible(isVisible);
    }

    @Override
    public void setBackBtnVisibility(boolean isVisible) {
        //actionBar.setDisplayHomeAsUpEnabled(isVisible);
    }

    @Override
    public void setSettingMenuListAdapter(SettingListAdapter settingListAdapter) {
        mainMenuList.setAdapter(settingListAdapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissPopupWindow();
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }

    @Override
    public void setSupportPreviewTxvVisibility(int visibility) {
        noSupportPreviewTxv.setVisibility(visibility);
    }

    @Override
    public void setPvModeBtnBackgroundResource(int drawableId) {
        pvModeBtn.setImageResource(drawableId);
    }

    @Override
    public void setTimepLapseRadioBtnVisibility(int visibility) {
        timepLapseRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setCaptureRadioBtnVisibility(int visibility) {
        captureRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setVideoRadioBtnVisibility(int visibility) {
        videoRadioBtn.setVisibility(visibility);
    }

    @Override
    public void setTimepLapseRadioChecked(boolean checked) {
        timepLapseRadioBtn.setChecked(checked);
    }

    @Override
    public void setCaptureRadioBtnChecked(boolean checked) {
        captureRadioBtn.setChecked(checked);
    }

    @Override
    public void setVideoRadioBtnChecked(boolean checked) {
        videoRadioBtn.setChecked(checked);
    }

    @Override
    public void showPopupWindow(int curMode) {
        if (pvModePopupWindow != null) {
            int height = SystemInfo.getMetrics().heightPixels;
            AppLog.d(TAG, "showPopupWindow height = " + height);
            AppLog.d(TAG, "showPopupWindow pvModeBtn.getWidth() = " + pvModeBtn.getWidth());
            AppLog.d(TAG, "showPopupWindow pvModeBtn.getHeight() = " + pvModeBtn.getHeight());
            AppLog.d(TAG, "showPopupWindow contentView.getHeight() = " + contentView.getHeight());
            //JIRA BUG IC-587 Start modify by b.jiang 20160719
            int contentViewH = contentView.getHeight();
            if (contentViewH == 0) {
                contentViewH = pvModeBtn.getHeight() * 5;
            }
//            pvModePopupWindow.showAsDropDown(pvModeBtn, -pvModeBtn.getWidth(), -pvModeBtn.getHeight()-contentViewH);
            pvModePopupWindow.showAsDropDown(pvModeBtn, 0, -pvModeBtn.getHeight() - contentViewH);
            //JIRA BUG IC-587 End modify by b.jiang 20160719
        }
    }

    @Override
    public void dismissPopupWindow() {
        if (pvModePopupWindow != null) {
            if (pvModePopupWindow.isShowing()) {
                pvModePopupWindow.dismiss();
            }
        }
    }

    //JIRA ICOM-3669 begin add by b.jiang 20160914
    public void refresh() {
        presenter.refresh();
    }
    //JIRA ICOM-3669 begin add by b.jiang 20160914

    public void stopStream() {
        presenter.stopStream();
    }


}
