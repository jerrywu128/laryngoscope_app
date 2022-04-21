package com.icatch.sbcapp.AppDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;

import com.icatch.sbcapp.GlobalApp.ExitApp;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraProperties;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yh.zhang C001012 on 2015/10/15:13:28.
 * Fucntion:
 */
public class AppDialog {
    private final static String tag = "AppDialog";
    private static boolean needShown = true;
    private static AlertDialog dialog;


    public static void showDialogQuit(final Context context, final int messageID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(messageID);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.i(tag, "ExitApp because of " + context.getResources().getString(messageID));
                ExitApp.getInstance().exit();
            }
        });
        builder.create().show();
    }

    public static void showDialogQuit(final Context context, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppLog.i(tag, "ExitApp because of " + message);
                ExitApp.getInstance().exit();
            }
        });
        builder.create().show();
    }

    public static void showDialogWarn(final Context context, String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showDialogWarn(final Context context, int messageID) {
        if (dialog != null) {
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(messageID);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public static void showConectFailureWarning(final Context context) {
        if (needShown == false) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(R.string.dialog_timeout);
        builder.setPositiveButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitApp.getInstance().exit();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.dialog_btn_reconnect, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                needShown = true;
            }
        });
        builder.create().show();
    }

    public static void showLowBatteryWarning(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(R.string.low_battery);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showAPPVersionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = "";
        if (packageInfo != null) {
            appVersion = packageInfo.versionName;
        }
        builder.setTitle(R.string.app_name).setMessage("App version : " + appVersion);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    public static void showLicenseAgreementDialog(final Context context,final String eulaversion) {
        if (needShown == false) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.license_agreement_layout, null);
        builder.setTitle("License agreement");
        builder.setView(contentView);
        builder.setPositiveButton(R.string.text_agree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                editor.putBoolean("agreeLicenseAgreement", true);
                editor.putString("agreeLicenseAgreementVersion", eulaversion);
                editor.commit();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.text_disagree, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitApp.getInstance().exit();
                needShown = true;
            }
        });
        builder.create().show();
    }
    public static void showSystemInfo(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersion = "";
        if (packageInfo != null) {
            appVersion = packageInfo.versionName;
        }
        builder.setTitle(R.string.app_name).setMessage(appVersion+"\n"+CameraProperties.getInstance().getCameraInfo());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
