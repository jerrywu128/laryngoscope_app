package com.icatch.sbcapp.Application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;

/**
 * Created by b.jiang on 2016/12/19.
 */

public class MyApplication extends LitePalApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        initBuglyCrash();
        context = getApplicationContext();
    }
    private void initBuglyCrash() {
        Log.d(TAG,"initBuglyCrash");
        CrashReport.initCrashReport(getApplicationContext(), "810492f6b0", true);
    }

    public static Context getContext() {
        return context;
    }
}
