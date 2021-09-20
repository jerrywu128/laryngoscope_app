package com.icatch.sbcapp.View.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.GlobalApp.ExitApp;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.ConnectCMPresenter;
import com.icatch.sbcapp.Presenter.LaunchPresenter;
import com.icatch.sbcapp.R;





public class ConnectCMActivity extends BaseActivity {
   // private TextView wifi_status;

    private ConnectCMPresenter presenter;
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_connect_wifi);
        //wifi_status = (TextView) findViewById(R.id.status);
        flag = true;
        Bundle bundle2=this.getIntent().getExtras();
        String wifissid = bundle2.getString("ssid");
        String wifipw = bundle2.getString("pw");
        String wifitp = bundle2.getString("tp");
        presenter = new ConnectCMPresenter(ConnectCMActivity.this);
        this.setWifi_Link(wifissid,wifipw,wifitp);
        //this.textTask();
        //MyProgressDialog.showProgressDialog(this);


    }
    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    protected void onResume() {
        super.onResume();

        ExitApp.getInstance().setCurActivity(this);
    }
    @Override
    protected void onStop() {
        flag=false;
        super.onStop();
    }



    private void setWifi_Link(String wifi_ssid,String wifi_pw,String wifi_tp){
        int delay = 0;



        if(!presenter.isWifienabled()){
            delay=2000;
        }

        presenter.connectWifiQ(this,wifi_ssid,wifi_pw);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    presenter.launchCamera();

                }
            }, 6500 + delay);

        }
    }


}
