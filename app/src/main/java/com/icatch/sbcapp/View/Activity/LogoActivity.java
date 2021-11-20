package com.icatch.sbcapp.View.Activity;

import android.content.Intent;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.icatch.sbcapp.AppDialog.AppDialog;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.R;

public class LogoActivity extends AppCompatActivity {
    TextView version_info;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        logo = (ImageView)findViewById(R.id.logo);
        String Language = getResources().getConfiguration().locale.getLanguage();
        String Traditional_Language = Language+"-"+ getResources().getConfiguration().locale.getCountry();

        if(Language.equals("en")||Language.equals("th")){
            logo.setImageResource(R.drawable.en_usharemedical_logo);
        }else if (Traditional_Language.equals("zh-TW")){
            logo.setImageResource(R.drawable.zht_usharemedical_logo);
        }//setting logo

        version_info = (TextView) findViewById(R.id.version_info);
        version_info.setText(AppInfo.APP_VERSION+" "+AppInfo.APP_FIX_DATE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent mainIntent = new Intent(LogoActivity.this, LaunchActivity.class);
                LogoActivity.this.startActivity(mainIntent);
                LogoActivity.this.finish();
            }
        }, 2000);
    }
}
