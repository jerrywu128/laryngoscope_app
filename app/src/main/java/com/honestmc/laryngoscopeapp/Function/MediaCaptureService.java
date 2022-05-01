package com.honestmc.laryngoscopeapp.Function;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Activity.PreviewActivity;

import java.util.Objects;

public class MediaCaptureService extends Service {
    private static final String TAG = "mediaCaptureService";
    private static MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private static Intent staticIntentData;
    private static int staticResultCode;
    private void createNotificationChannel() {

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, PreviewActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_icatchtek_alpha_test)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        startForeground(110, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        int mResultCode = intent.getIntExtra("code", -1);
        Intent mResultData = intent.getParcelableExtra("data");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(staticResultCode == 0 && staticIntentData == null) {
                mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, Objects.requireNonNull(mResultData));
                staticIntentData = Objects.requireNonNull(mResultData);
                staticResultCode = mResultCode;
            }else{
                mMediaProjection = mMediaProjectionManager.getMediaProjection(staticResultCode, staticIntentData);
            }
            new mediaScreenCapture(this, mMediaProjection).startProjection();
        }

        Log.e(TAG, "mMediaProjection created: " + mMediaProjection);

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
