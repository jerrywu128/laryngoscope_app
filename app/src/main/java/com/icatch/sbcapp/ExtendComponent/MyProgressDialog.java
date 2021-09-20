package com.icatch.sbcapp.ExtendComponent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.icatch.sbcapp.R;


/**
 * Created by zhang yanhu C001012 on 2015/11/24 12:15.
 */

public class MyProgressDialog {
    private static ProgressDialog mDialog = null;

    public static void showProgressDialog(Activity activity, String text) {
        if(!isLiving(activity)){
            return;
        }
        closeProgressDialog();
        mDialog = new CustomProgressDialog(activity, R.style.CustomDialog,text);
        mDialog.show();
    }

    public static void showProgressDialog(Activity activity, int stringID) {
        if(!isLiving(activity)){
            return;
        }
        closeProgressDialog();
        mDialog = new CustomProgressDialog(activity, R.style.CustomDialog,activity.getString(stringID));
        mDialog.show();
    }

    public static void showProgressDialog(Context context, int stringID) {
        if(context == null){
            return;
        }
        if(context != null && context instanceof Activity){
            Activity activity = (Activity) context;
            if(!isLiving(activity)){
                return;
            }
        }
        closeProgressDialog();
        mDialog = new CustomProgressDialog(context, R.style.CustomDialog,context.getString(stringID));
        mDialog.show();
    }

    public static void showProgressDialog(Activity activity) {
        if(!isLiving(activity)){
            return;
        }
        closeProgressDialog();
        mDialog = new CustomProgressDialog(activity, R.style.CustomDialog,"");//R.style.Dialog
        mDialog.show();
    }

    public static void closeProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {

            } finally {
                mDialog = null;
            }
        }
    }

    private static boolean isLiving(Activity activity){
        if (activity == null || activity.isFinishing()|| activity.isDestroyed() ) {
            return false;
        }else {
            return true;
        }
    }
}