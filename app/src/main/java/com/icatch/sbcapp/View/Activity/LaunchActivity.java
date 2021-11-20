package com.icatch.sbcapp.View.Activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.AppBarLayout;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.widget.Toolbar;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

import com.google.android.material.appbar.AppBarLayout;
import com.google.zxing.Result;
import com.icatch.sbcapp.Adapter.CameraSlotAdapter;
import com.icatch.sbcapp.AppDialog.AppDialog;
import com.icatch.sbcapp.AppInfo.ConfigureInfo;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnFragmentInteractionListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.LaunchPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.Tools.GlideUtils;
import com.icatch.sbcapp.Tools.LruCacheTool;
import com.icatch.sbcapp.Tools.PermissionTools;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Interface.LaunchView;
import com.icatch.wificam.customer.type.ICatchEventID;

import java.io.IOException;

public class LaunchActivity extends BaseActivity implements View.OnClickListener, LaunchView, OnFragmentInteractionListener {
    private static String TAG = "LaunchActivity";
    private TextView noPhotosFound, noVideosFound, qrScannerText;
    private ImageView localVideo, localPhoto;
    private ListView camSlotListView;
    private LaunchPresenter presenter;
    private LinearLayout launchLayout;
    private FrameLayout launchSettingFrame;
    private String currentFragment;
    private ActionBar actionBar;
    private final String tag = "LaunchActivity";
    private AppBarLayout appBarLayout;
    private String TEST = "LaunchActivityTEST";
    private MenuItem menuSetIp;
    private CodeScannerView scannerView;
    private CodeScanner mCodeScanner;
    private String result_data;
    public static final int PERMISSION_REQUEST_CODE =100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        launchLayout = (LinearLayout) findViewById(R.id.launch_view);
        launchSettingFrame = (FrameLayout) findViewById(R.id.launch_setting_frame);

        scannerView =(CodeScannerView) findViewById(R.id.qrcode_scanner);
        qrScannerText=(TextView) findViewById(R.id.qrscanner_text);


    /*    noPhotosFound = (TextView) findViewById(R.id.no_local_photos);
        noVideosFound = (TextView) findViewById(R.id.no_local_videos);

        localVideo = (ImageView) findViewById(R.id.local_video);
        localVideo.setOnClickListener(this);

        localPhoto = (ImageView) findViewById(R.id.local_photo);
        localPhoto.setOnClickListener(this);
*/
        presenter = new LaunchPresenter(LaunchActivity.this);
        presenter.setView(this);
        GlobalInfo.getInstance().addEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, false);//        presenter.addGlobalLisnter(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, false);

        result_data = new String("");

      /*  camSlotListView = (ListView) findViewById(R.id.cam_slot_listview);
        camSlotListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.removeCamera(position);
                return false;
            }
        });

        camSlotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fm = getSupportFragmentManager();
                getSupportFragmentManager();
                presenter.launchCamera(position, fm);
            }
        });*/
        LruCacheTool.getInstance().initLruCache();
        presenter.submitAppInfo();
        presenter.checkFirstInapp();
        if(presenter.checkMobiledata()){
            AppDialog.showDialogWarn(this, R.string.dialog_mobiledata);
        }

       /* if (Build.VERSION.SDK_INT < 23 || PermissionTools.CheckSelfPermission(this)) {
            ConfigureInfo.getInstance().initCfgInfo(this.getApplicationContext());
             presenter.showLicenseAgreementDialog();
        } else {
            PermissionTools.RequestPermissions(LaunchActivity.this);
        }*/

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(result.getText().contains("WIFI:")&&result.getText().contains("P:")&&result.getText().contains("S:")) {
                            if (SDK_INT >= Build.VERSION_CODES.Q) {
                                if(!presenter.isWifienabled()) {
                                    Intent panelIntent = new
                                            Intent(Settings.Panel.ACTION_WIFI);

                                    startActivityForResult(panelIntent, 545);
                                    result_data = result.getText();
                                    //OnactivityResult
                                    /*
                                    if(presenter.isWifienabled())
                                        presenter.setWifiwithQrcode(LaunchActivity.this, result.getText());
                                    else{
                                        Toast.makeText(LaunchActivity.this, R.string.qrcode_tp_error, Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mCodeScanner.startPreview();
                                            }
                                        }, 2000);
                                    }
                                    */
                                }
                                else{
                                    presenter.setWifiwithQrcode(LaunchActivity.this, result.getText());
                                }
                            }else {
                                presenter.setWifiwithQrcode(LaunchActivity.this, result.getText());
                            }
                        }else{
                            Toast.makeText(LaunchActivity.this, R.string.qrcode_tp_error, Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCodeScanner.startPreview();
                                }
                            }, 2000);

                        }

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mCodeScanner.startPreview();
            }
        });

        qrScannerText.setText(R.string.scan_text);

        if(!checkPermission()) {
            requestPermission();
        }
        verifyStoragePermissions(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 10 );

        }

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
    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(LaunchActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(LaunchActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==545) {
            if(!presenter.isWifienabled()) {
                Toast.makeText(LaunchActivity.this, R.string.check_wifi_isenabled, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.startPreview();
                    }
                }, 2000);
            }
            else{
                presenter.setWifiwithQrcode(LaunchActivity.this, result_data);
            }
        }

        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        AppLog.d(tag, "onStart");
    }

    @Override
    protected void onResume() {

        AppLog.i(tag, "Start onResume");
        super.onResume();

        //t presenter.registerReceiver();
        //t presenter.loadListview();
        if (SDK_INT < 23 || PermissionTools.CheckSelfPermission(this)) {
               presenter.loadLocalThumbnails();
        }
        GlobalInfo.getInstance().setCurrentApp(LaunchActivity.this);
        AppLog.i(tag, "End onResume");
        StorageUtil.sdCardExist(LaunchActivity.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        else
            mCodeScanner.startPreview();

    }


    @Override
    protected void onPause() {
        super.onPause();
        //t presenter.unregisterWifiReceiver();
        mCodeScanner.releaseResources();

    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLog.d(tag, "onStop");

//        presenter.isAppBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                AppLog.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                AppLog.d("AppStart", "back");
                finish();
                removeFragment();
                break;
            case KeyEvent.KEYCODE_MENU:
                AppLog.d("AppStart", "KEYCODE_MENU");
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        AppLog.d(tag, "onDestroy");
        super.onDestroy();
        System.exit(0);
        GlobalInfo.getInstance().delEventListener(ICatchEventID.ICH_EVENT_SDCARD_REMOVED, false);
//        GlobalInfo.getInstance().endSceenListener();
        LruCacheTool.getInstance().clearCache();
        presenter.removeActivity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        AppLog.d(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
        //tmenuSetIp = menu.findItem(R.id.action_set_ip);
        presenter.initMenu();
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        AppLog.i(tag, "id =" + id);
        AppLog.i(tag, "R.id.home =" + R.id.home);
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            //return true;
//            presenter.startSearchCamera();
//        } else
        if (id == android.R.id.home) {
//            finish();
            removeFragment();
            return true;
        } else if (id == R.id.action_set_ip) {
            presenter.showSettingIpDialog(LaunchActivity.this);
        } else if (id == R.id.action_about) {
            AppDialog.showAPPVersionDialog(LaunchActivity.this);
        } else if (id == R.id.action_license) {
            Intent mainIntent = new Intent(LaunchActivity.this, LicenseAgreementActivity.class);
            LaunchActivity.this.startActivity(mainIntent);
        } else if (id == R.id.action_help) {
            Intent mainIntent = new Intent(LaunchActivity.this, LaunchHelpActivity.class);
            LaunchActivity.this.startActivity(mainIntent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {
        AppLog.i(tag, "click info:::v.getId() =" + v.getId());
        //AppLog.i(tag, "click info:::R.id.local_photo =" + R.id.local_photo);
        //AppLog.i(tag, "click info:::R.id.local_video =" + R.id.local_video);
       /* switch (v.getId()) {
            case R.id.local_photo:
                AppLog.i(tag, "click the local photo");
               //t presenter.redirectToAnotherActivity(LaunchActivity.this, LocalPhotoWallActivity.class);
                //presenter.redirectToAnotherActivity(LaunchActivity.this, MultiPbActivity.class);

                break;
            case R.id.local_video:
              //t  presenter.redirectToAnotherActivity(LaunchActivity.this, LocalVideoWallActivity.class);
                break;
            default:
                break;
        }*/
    }

    @Override
    public void setLocalPhotoThumbnail(String filePath) {
      //t  GlideUtils.loadImageViewLodingSize(this, filePath, 500, 500, localPhoto, R.drawable.local_default_thumbnail, R.drawable.local_default_thumbnail);
    }

    @Override
    public void setLocalVideoThumbnail(Bitmap bitmap) {
//        GlideUtils.loadImageViewLodingSize(this,filePath,500,500,localVideo,R.drawable.local_default_thumbnail,R.drawable.local_default_thumbnail);
      //t  localVideo.setImageBitmap(bitmap);
    }

    @Override
    public void loadDefaultLocalPhotoThumbnail() {
        //ICOM-3906 20161104
      //t  localPhoto.setImageResource(R.drawable.local_default_thumbnail);
//        localPhoto.setBackgroundResource(R.drawable.local_default_thumbnail);
    }

    @Override
    public void loadDefaultLocalVideooThumbnail() {
      //t  localVideo.setImageResource(R.drawable.local_default_thumbnail);
//        localVideo.setBackgroundResource(R.drawable.local_default_thumbnail);
    }

    @Override
    public void setNoPhotoFilesFoundVisibility(int visibility) {
       //t noPhotosFound.setVisibility(visibility);
    }

    @Override
    public void setNoVideoFilesFoundVisibility(int visibility) {
      //t  noVideosFound.setVisibility(visibility);
    }

    @Override
    public void setPhotoClickable(boolean clickable) {
      //t  localPhoto.setEnabled(clickable);
    }

    @Override
    public void setVideoClickable(boolean clickable) {
      //t  localVideo.setEnabled(clickable);
    }

    @Override
    public void setListviewAdapter(CameraSlotAdapter cameraSlotAdapter) {
      //t  camSlotListView.setAdapter(cameraSlotAdapter);
    }

    @Override
    public void setBackBtnVisibility(boolean visibility) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(visibility);
        }
    }

    @Override
    public void setNavigationTitle(int resId) {
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    @Override
    public void setNavigationTitle(String res) {
        if (actionBar != null) {
            actionBar.setTitle(res);
        }
    }

    @Override
    public void setLaunchLayoutVisibility(int visibility) {
        launchLayout.setVisibility(visibility);
        appBarLayout.setVisibility(visibility);
    }

    @Override
    public void setLaunchSettingFrameVisibility(int visibility) {
        launchSettingFrame.setVisibility(visibility);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionTools.WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE:
                AppLog.i(tag, "permissions.length = " + permissions.length);
                AppLog.i(tag, "grantResults.length = " + grantResults.length);
                boolean retValue = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Request write storage ", Toast.LENGTH_SHORT).show();
                        retValue = true;
                    } else {
                        retValue = false;
                    }
                }
                if (retValue) {
                     presenter.loadLocalThumbnails();
                     presenter.showLicenseAgreementDialog();
                    ConfigureInfo.getInstance().initCfgInfo(this.getApplicationContext());
                } else {
                    AppDialog.showDialogQuit(this, R.string.permission_is_denied_info);
//                    Toast.makeText(this, "Request write storage failed!", Toast.LENGTH_SHORT).show();
                }

                break;
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void submitFragmentInfo(String fragment, int resId) {
        currentFragment = fragment;
//        setNavigationTitle(resId);
    }

    @Override
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                //setNavigationTitle(R.string.app_name);
                launchSettingFrame.setVisibility(View.GONE);
                launchLayout.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                setBackBtnVisibility(false);
            }
            getSupportFragmentManager().popBackStack();
        }
    }

    // 将所有的fragment 出栈;

    @Override
    public void fragmentPopStackOfAll() {
        int fragmentBackStackNum = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < fragmentBackStackNum; i++) {
            getSupportFragmentManager().popBackStack();
        }

        setBackBtnVisibility(false);
       // setNavigationTitle(R.string.app_name);
        launchSettingFrame.setVisibility(View.GONE);
        launchLayout.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMenuSetIpVisibility(boolean visible) {
        AppLog.i(TAG, "setMenuSetIpVisibility visible=" + visible + " menuSetIp=" + menuSetIp);
        if (menuSetIp != null) {
            menuSetIp.setVisible(visible);
        }
    }

    public void startScreenListener() {
        GlobalInfo.getInstance().startScreenListener();
    }


    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(LaunchActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


}
