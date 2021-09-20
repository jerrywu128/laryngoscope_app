package com.icatch.sbcapp.Presenter.Interface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.GlobalApp.ExitApp;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchNoSuchPathException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhang yanhu C001012 on 2015/11/19 15:43.
 */
public abstract  class BasePresenter {
    private final String tag = "BasePresenter";
    protected Activity activity;
    public BasePresenter(Activity activity) {
        this.activity = activity;
    }
    public void initCfg(){
        GlobalInfo.getInstance().setCurrentApp(activity);
        // never sleep when run this activity
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setCurrentApp(){
        GlobalInfo.getInstance().setCurrentApp(activity);
    }


    public void redirectToAnotherActivity(Context context,Class<?> cls){
        Intent intent = new Intent();
        AppLog.i(tag, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }
    
    public void finishActivity() {
        activity.finish();
    }

    public void isAppBackground() {
        if(AppInfo.isAppSentToBackground(activity)) {
            AppLog.d(tag,"is background activity=" + activity);
            ExitApp.getInstance().exit();
        }else {
            AppLog.d(tag,"not is background activity=" + activity);
        }
    }

    public void submitAppInfo() {
        GlobalInfo.getInstance().setCurrentApp(activity);
//        ExitApp.getInstance().addActivity(activity);
    }

    public void removeActivity(){
//        if(activity != null){
//            ExitApp.getInstance().removeActivity(activity);
//        }
    }

    public void showOptionIcon(Menu menu) {
        //display icon
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
    }
}
