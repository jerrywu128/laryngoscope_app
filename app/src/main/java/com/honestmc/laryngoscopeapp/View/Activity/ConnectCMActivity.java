package com.honestmc.laryngoscopeapp.View.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.honestmc.laryngoscopeapp.GlobalApp.ExitApp;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Presenter.ConnectCMPresenter;
import com.honestmc.laryngoscopeapp.R;





public class ConnectCMActivity extends BaseActivity {
   // private TextView wifi_status;

    private ConnectCMPresenter presenter;
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_connect_wifi);
        flag = true;
        Bundle bundle2=this.getIntent().getExtras();
        String wifissid = bundle2.getString("ssid");
        String wifipw = bundle2.getString("pw");
        String wifitp = bundle2.getString("tp");
        presenter = new ConnectCMPresenter(ConnectCMActivity.this);
        this.setWifi_Link(wifissid,wifipw,wifitp);


    }
    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    protected void onResume() {
        super.onResume();
        GlobalInfo.getInstance().setCurrentApp(this);
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
