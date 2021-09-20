package com.icatch.sbcapp.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.icatch.sbcapp.Log.AppLog;

/**
 * Created by b.jiang on 2015/12/8.
 */
public class PermissionTools {
    private static String TAG = "PermissionTools";
    public static final int WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE = 102;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;

    public static void RequestPermissions(final Activity activity) {
        AppLog.d(TAG, "Start RequestPermissions");
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_OR_READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {

        }
        AppLog.d(TAG, "End RequestPermissions");
    }

    public static boolean CheckSelfPermission(final Activity activity){
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

}
