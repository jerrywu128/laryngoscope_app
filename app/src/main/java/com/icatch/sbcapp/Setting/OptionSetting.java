package com.icatch.sbcapp.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.AppInfo.AppSharedPreferences;
import com.icatch.sbcapp.BaseItems.TimeLapseInterval;
import com.icatch.sbcapp.BaseItems.TimeLapseMode;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.GlobalApp.ExitApp;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnSettingCompleteListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Model.Implement.SDKEvent;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraAction;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.Tools.FileOpertion.FileTools;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.wificam.customer.type.ICatchEventID;

import java.lang.reflect.Field;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhang yanhu C001012 on 2015/12/30 16:33.
 */
public class OptionSetting {

    private final static String TAG = "OptionSetting";
    private static OptionSetting optionSetting;
    private static OnSettingCompleteListener onSettingCompleteListener;
    private static AlertDialog alertDialog;
    private final SettingHander handler = new SettingHander();
    private static SDKEvent sdkEvent;
    private Activity context;


    public static OptionSetting getInstance() {
        if (optionSetting == null) {
            optionSetting = new OptionSetting();
        }
        return optionSetting;
    }

    public void addSettingCompleteListener(OnSettingCompleteListener onSettingCompleteListener) {
        this.onSettingCompleteListener = onSettingCompleteListener;
    }

    public void showSettingDialog(int nameId, Activity context) {
        this.context = context;
        switch (nameId) {
            case R.string.setting_image_size:
                Log.d("1111", "setting_image_size");
                showImageSizeOptionDialog(context);
                break;
            case R.string.setting_video_size:
                Log.d("1111", "setting_video_size");
                showVideoSizeOptionDialog(context);
                break;
            case R.string.setting_capture_delay:
                Log.d("1111", "setting_capture_delay");
                showDelayTimeOptionDialog(context);
                break;
            case R.string.title_burst:
                showBurstOptionDialog(context);
                break;
            case R.string.title_awb:
                Log.d("1111", "showWhiteBalanceOptionDialog =");
                showWhiteBalanceOptionDialog(context);
                break;
            case R.string.setting_power_supply:
                showElectricityFrequencyOptionDialog(context);
                break;
            case R.string.setting_datestamp:
                showDateStampOptionDialog(context);
                break;
            case R.string.setting_format:
                if (CameraProperties.getInstance().isSDCardExist() == false) {
                    sdCardIsNotReadyAlert(context);
                    break;
                }
                showFormatConfirmDialog(context);
                break;
            case R.string.setting_time_lapse_interval:
                showTimeLapseIntervalDialog(context);
                break;
            case R.string.setting_time_lapse_duration:
                showTimeLapseDurationDialog(context);
                break;
            case R.string.title_timeLapse_mode:
                showTimeLapseModeDialog(context);
                break;
            case R.string.slowmotion:
                showSlowMotionDialog(context);
                break;
            case R.string.upside:
                showUpsideDialog(context);
                break;
            case R.string.setting_title_camera_switch:
                showCameraSwitchDialog(context);
                break;
            case R.string.camera_wifi_configuration:
                showCameraConfigurationDialog(context);
                break;
            case R.string.setting_update_fw:
                if (CameraProperties.getInstance().isSDCardExist() == false) {
                    sdCardIsNotReadyAlert(context);
                    break;
                }
                showUpdateFWDialog(context);
                break;
            case R.string.setting_auto_download_size_limit:
                showSetDownloadSizeLimitDialog(context);
                break;
            case R.string.setting_title_screen_saver:
                showScreenSaverDialog(context);
                break;
            case R.string.setting_title_auto_power_off:
                AppLog.d("1111", "showAutoPowerOffDialog");
                showAutoPowerOffDialog(context);
                break;
            case R.string.setting_title_exposure_compensation:
                AppLog.d("1111", "showExposureCompensationDialog");
                showExposureCompensationDialog(context);
                break;
            case R.string.setting_title_video_file_length:
                AppLog.d("1111", "showVideoFileLengthDialog");
                showVideoFileLengthDialog(context);
                break;
            case R.string.setting_title_fast_motion_movie:
                AppLog.d("1111", "showFastMotionMovieDialog");
                showFastMotionMovieDialog(context);
                break;
            case R.string.setting_storage_location:
                AppLog.d("1111", "showStorageLocationDialog");
                showStorageLocationDialog(context);
                break;
        }
    }
    public static void showCameraSwitchDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_camera_switch);
        // final MyCamera currCamera =
        // GlobalInfo.getInstance().getCurrentCamera();
        final String[] cameraSwitchUIString = currCamera.getCameraSwitch().getValueList();
        if (cameraSwitchUIString == null) {
            AppLog.e(TAG, "cameraSwitchUIString == null");
            return;
        }
        int length = cameraSwitchUIString.length;
        int curIdx = 0;

        for (int i = 0; i < length; i++) {
            if (cameraSwitchUIString[i].equals(currCamera.getCameraSwitch().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getCameraSwitch().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, cameraSwitchUIString, curIdx, listener, true);
    }

    public void showUpdateFWDialog(final Context context) {
        AppLog.i(TAG, "showUpdateFWDialog");
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.setting_updateFW_prompt);
        builder.setNegativeButton(R.string.setting_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.setting_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                if (sdkEvent == null) {
                    sdkEvent = new SDKEvent(handler);
                }
                sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED);
                sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF);
                //add 20170710 by b.jiang
                sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK);
                sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR);
                sdkEvent.addEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG);

//                FileTools.copyFile(R.raw.sphost);
//                String fileName = Environment.getExternalStorageDirectory().toString() + AppInfo.UPDATEFW_FILENAME;
//                FileOperation.getInstance().uploadFile(fileName,"ddd.BRN");
//                FileOperation.getInstance().uploadFile(fileName,"ddd.BRN");

                MyProgressDialog.showProgressDialog(context, R.string.setting_update_fw);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int messageId;
                        FileTools.copyFile(R.raw.sphost);
                        String fileName = Environment.getExternalStorageDirectory().toString() + AppInfo.UPDATEFW_FILENAME;

                        //ICOM-3486 Begin And by b.jiang 20161020;
                        FileOperation.getInstance().openFileTransChannel();
                        //ICOM-3486 End And by b.jiang 20161020;
                        if (!CameraAction.getInstance().updateFW(fileName)) {
                            messageId = R.string.text_operation_success;
                            AlertDialog.Builder updateFWFailedBuilder = new AlertDialog.Builder(context);
                            updateFWFailedBuilder.setMessage(R.string.setting_updatefw_failedInfo);
                            updateFWFailedBuilder.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("1111", "update FW has failed,App quit");
                                    ExitApp.getInstance().exit();
                                }
                            });
                            alertDialog = updateFWFailedBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else {
                            //ICOM-3486 Begin And by b.jiang 20161020;
                            FileOperation.getInstance().closeFileTransChannel();
                            //ICOM-3486 End And by b.jiang 20161020;
                        }
                    }
                }).start();
            }
        });
        builder.create().show();
    }

    public static void showCameraConfigurationDialog(final Context context) {
        // TODO Auto-generated method stub
        LayoutInflater factory = LayoutInflater.from(context);
        View textEntryView = factory.inflate(R.layout.camera_name_password_set, null);
        final EditText cameraName = (EditText) textEntryView.findViewById(R.id.camera_name);
        final String name = CameraProperties.getInstance().getCameraSsid();
        cameraName.setText(name);
        final EditText cameraPassword = (EditText) textEntryView.findViewById(R.id.wifi_password);
        final String password = CameraProperties.getInstance().getCameraPassword();
        cameraPassword.setText(password);
        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setTitle(R.string.camera_wifi_configuration);
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);
        ad1.setCancelable(false);

        // BSP-656 20170601
        ad1.setNegativeButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        ad1.setPositiveButton(R.string.camera_configuration_set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                String temp1 = cameraName.getText().toString();
                if (temp1.length() > 20 || temp1.length() < 1) {
                    Toast.makeText(context, R.string.camera_name_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                String temp = cameraPassword.getText().toString();
                if (temp.length() > 10 || temp.length() < 8) {
                    Toast.makeText(context, R.string.password_limit, Toast.LENGTH_LONG).show();
                    // do not allow dialog close
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // allow dialog close
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();

                }

                AppLog.d(TAG, "cameraName=" + cameraName.getText().toString() + " cameraPassword=" + cameraPassword.getText().toString());
                if (name == null || name.equals(cameraName.getText().toString()) == false) {
                    CameraProperties.getInstance().setCameraSsid(cameraName.getText().toString());
                }
                if (password == null || password.equals(temp) == false) {
                    CameraProperties.getInstance().setCameraPassword(cameraPassword.getText().toString());
                }
            }
        });
        ad1.show();
    }

    public static void showUpsideDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.upside);
        // final MyCamera currCamera =
        // GlobalInfo.getInstance().getCurrentCamera();
        final String[] upsideUIString = currCamera.getUpside().getValueList();
        if (upsideUIString == null) {
            AppLog.e(TAG, "upsideUIString == null");
            return;
        }
        int length = upsideUIString.length;
        int curIdx = 0;

        for (int i = 0; i < length; i++) {
            if (upsideUIString[i].equals(currCamera.getUpside().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getUpside().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, upsideUIString, curIdx, listener, true);
    }


    public static void showSlowMotionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.title_slow_motion);
        final String[] slowmotionUIString = currCamera.getSlowMotion().getValueList();
        if (slowmotionUIString == null) {
            AppLog.e(TAG, "slowmotionUIString == null");
            return;
        }
        int length = slowmotionUIString.length;
        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (slowmotionUIString[i].equals(currCamera.getSlowMotion().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getSlowMotion().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, slowmotionUIString, curIdx, listener, true);
    }


    public void showTimeLapseModeDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.title_timeLapse_mode);
        final String[] timeLapseModeString = currCamera.getTimeLapseMode().getValueList();
        if (timeLapseModeString == null) {
            AppLog.e(TAG, "timeLapseModeString == null");
            return;
        }
        int length = timeLapseModeString.length;
        int curIdx = 0;

        for (int i = 0; i < length; i++) {
            if (timeLapseModeString[i].equals(currCamera.getTimeLapseMode().getCurrentUiStringInSetting())) {
                Log.d("tigertiger", "timeLapseModeString[i] =" + timeLapseModeString[i]);
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.timeLapsePreviewMode = arg1;
                arg0.dismiss();
                onSettingCompleteListener.settingTimeLapseModeComplete(arg1);
                onSettingCompleteListener.onOptionSettingComplete();
                Log.d("tigertiger", "showTimeLapseModeDialog  timeLapseMode =" + arg1);
            }
        };
        showOptionDialog(title, timeLapseModeString, curIdx, listener, true);
    }

    public static void showTimeLapseDurationDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_time_lapse_duration);
        final String[] videoTimeLapseDurationString = currCamera.gettimeLapseDuration().getValueStringList();
        if (videoTimeLapseDurationString == null) {
            AppLog.e(TAG, "videoTimeLapseDurationString == null");
            return;
        }
        int length = videoTimeLapseDurationString.length;

        int curIdx = 0;
        String temp = currCamera.gettimeLapseDuration().getCurrentValue();
        for (int i = 0; i < length; i++) {
            if (videoTimeLapseDurationString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.gettimeLapseDuration().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoTimeLapseDurationString, curIdx, listener, true);
    }

    public void showTimeLapseIntervalDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_time_lapse_interval);

        final String[] timeLapseIntervalString;
        final TimeLapseInterval timeLapseInterval;
        if (currCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_STILL) {
            timeLapseInterval = currCamera.getTimeLapseStillInterval();
        } else {
            timeLapseInterval = currCamera.getTimeLapseVideoInterval();
        }
        timeLapseIntervalString = timeLapseInterval.getValueStringList();
        if (timeLapseIntervalString == null) {
            AppLog.e(TAG, "timeLapseIntervalString == null");
            return;
        }
        int length = timeLapseIntervalString.length;

        int curIdx = 0;
        String temp = timeLapseInterval.getCurrentValue();
        for (int i = 0; i < length; i++) {
            if (timeLapseIntervalString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timeLapseInterval.setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, timeLapseIntervalString, curIdx, listener, true);
    }

    public static void showImageSizeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.stream_set_res_photo);

        final String[] imageSizeUIString = currCamera.getImageSize().getValueArrayString();
        if (imageSizeUIString == null) {
            AppLog.e(TAG, "imageSizeUIString == null");
            return;
        }
        int length = imageSizeUIString.length;
        int curIdx = 0;
        for (int ii = 0; ii < length; ii++) {
            if (imageSizeUIString[ii].equals(currCamera.getImageSize().getCurrentUiStringInSetting())) {
                curIdx = ii;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getImageSize().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, imageSizeUIString, curIdx, listener, true);
    }

    public  void showDelayTimeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.stream_set_timer);
        final String[] delayTimeUIString = currCamera.getCaptureDelay().getValueList();
        if (delayTimeUIString == null) {
            AppLog.e(TAG, "delayTimeUIString == null");
            return;
        }
        int length = delayTimeUIString.length;
        int curIdx = 0;
        String temp = currCamera.getCaptureDelay().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (delayTimeUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getCaptureDelay().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, delayTimeUIString, curIdx, listener, true);
    }

    public static void showVideoSizeOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.stream_set_res_vid);
        final String[] videoSizeUIString = currCamera.getVideoSize().getValueArrayString();
        final List<String> videoSizeValueString = currCamera.getVideoSize().getValueList();
        if (videoSizeUIString == null) {
            AppLog.e(TAG, "videoSizeUIString == null");
            return;
        }
        int length = videoSizeUIString.length;

        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (videoSizeUIString[i].equals(currCamera.getVideoSize().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                final String value = videoSizeValueString.get(arg1);
                currCamera.getVideoSize().setValue(value);
                arg0.dismiss();
                onSettingCompleteListener.settingVideoSizeComplete();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoSizeUIString, curIdx, listener, false);
    }

    public static void showFormatConfirmDialog(final Context context) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.setting_format_desc);
        builder.setNegativeButton(R.string.setting_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.setting_yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                final Handler handler = new Handler();
                MyProgressDialog.showProgressDialog(context, R.string.setting_format);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int messageId;
                        if (CameraAction.getInstance().formatStorage()) {
                            messageId = R.string.text_operation_success;
                        } else {
                            messageId = R.string.text_operation_failed;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                MyToast.show(context, messageId);
                            }
                        });
                    }
                }).start();

            }
        });
        builder.create().show();
    }


    public static void showDateStampOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_datestamp);
        final String[] dateStampUIString = currCamera.getDateStamp().getValueList();
        if (dateStampUIString == null) {
            AppLog.e(TAG, "dateStampUIString == null");
            return;
        }
        int length = dateStampUIString.length;

        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (dateStampUIString[i].equals(currCamera.getDateStamp().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getDateStamp().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, dateStampUIString, curIdx, listener, true);
    }

    public static void showElectricityFrequencyOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_power_supply);

        final String[] eleFreUIString = currCamera.getElectricityFrequency().getValueList();
        if (eleFreUIString == null) {
            AppLog.e(TAG, "eleFreUIString == null");
            return;
        }
        int length = eleFreUIString.length;

        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (eleFreUIString[i].equals(currCamera.getElectricityFrequency().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getElectricityFrequency().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, eleFreUIString, curIdx, listener, true);
    }


    public static void showWhiteBalanceOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.title_awb);
        final String[] whiteBalanceUIString = currCamera.getWhiteBalance().getValueList();
        if (whiteBalanceUIString == null) {
            AppLog.e(TAG, "whiteBalanceUIString == null");
            return;
        }
        int length = whiteBalanceUIString.length;

        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (whiteBalanceUIString[i].equals(currCamera.getWhiteBalance().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getWhiteBalance().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, whiteBalanceUIString, curIdx, listener, true);
    }

    public void showBurstOptionDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.title_burst);

        final String[] burstUIString = currCamera.getBurst().getValueList();
        if (burstUIString == null) {
            AppLog.e(TAG, "burstUIString == null");
            return;
        }
        int length = burstUIString.length;

        int curIdx = 0;
        for (int i = 0; i < length; i++) {
            if (burstUIString[i].equals(currCamera.getBurst().getCurrentUiStringInSetting())) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getBurst().setValueByPosition(arg1);
                arg0.dismiss();
                Log.d("1111", "refresh optionListAdapter!");
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, burstUIString, curIdx, listener, true);
    }

    public static void showScreenSaverDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_screen_saver);
        final String[] screenSaverUIString = currCamera.getScreenSaver().getValueList();
        if (screenSaverUIString == null) {
            AppLog.e(TAG, "screenSaverUIString == null");
            return;
        }
        int length = screenSaverUIString.length;
        int curIdx = 0;
        String temp = currCamera.getScreenSaver().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (screenSaverUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getScreenSaver().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, screenSaverUIString, curIdx, listener, true);
    }

    public static void showAutoPowerOffDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_auto_power_off);
        final String[] autoPowerOffUIString = currCamera.getAutoPowerOff().getValueList();
        if (autoPowerOffUIString == null) {
            AppLog.e(TAG, "autoPowerOffUIString == null");
            return;
        }
        int length = autoPowerOffUIString.length;
        int curIdx = 0;
        String temp = currCamera.getAutoPowerOff().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (autoPowerOffUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getAutoPowerOff().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, autoPowerOffUIString, curIdx, listener, true);
    }

    public static void showExposureCompensationDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_exposure_compensation);
        final String[] exposureCompensationUIString = currCamera.getExposureCompensation().getValueList();
        if (exposureCompensationUIString == null) {
            AppLog.e(TAG, "exposureCompensationUIString == null");
            return;
        }
        int length = exposureCompensationUIString.length;
        int curIdx = 0;
        String temp = currCamera.getExposureCompensation().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (exposureCompensationUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getExposureCompensation().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, exposureCompensationUIString, curIdx, listener, true);
    }

    public static void showVideoFileLengthDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_video_file_length);
        final String[] videoFileLengthUIString = currCamera.getVideoFileLength().getValueList();
        if (videoFileLengthUIString == null) {
            AppLog.e(TAG, "videoFileLengthUIString == null");
            return;
        }
        int length = videoFileLengthUIString.length;
        int curIdx = 0;
        String temp = currCamera.getVideoFileLength().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (videoFileLengthUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getVideoFileLength().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, videoFileLengthUIString, curIdx, listener, true);
    }

    public static void showFastMotionMovieDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_title_fast_motion_movie);
        final String[] fastMotionMovieUIString = currCamera.getFastMotionMovie().getValueList();
        if (fastMotionMovieUIString == null) {
            AppLog.e(TAG, "fastMotionMovieUIString == null");
            return;
        }
        int length = fastMotionMovieUIString.length;
        int curIdx = 0;
        String temp = currCamera.getFastMotionMovie().getCurrentUiStringInPreview();
        for (int i = 0; i < length; i++) {
            if (fastMotionMovieUIString[i].equals(temp)) {
                curIdx = i;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                currCamera.getFastMotionMovie().setValueByPosition(arg1);
                arg0.dismiss();
                onSettingCompleteListener.onOptionSettingComplete();
            }
        };
        showOptionDialog(title, fastMotionMovieUIString, curIdx, listener, true);
    }


    public static void showOptionDialog(CharSequence title, CharSequence[] items, int checkedItem,
                                        DialogInterface.OnClickListener listener,
                                        boolean cancelable) {
        AlertDialog.Builder optionDialog = new AlertDialog.Builder(GlobalInfo.getInstance().getCurrentApp());

        optionDialog.setTitle(title).setSingleChoiceItems(items, checkedItem, listener).create();
        optionDialog.show();
        optionDialog.setCancelable(cancelable);
    }

    public void sdCardIsNotReadyAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_no_sd);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private class SettingHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case SDKEvent.EVENT_FW_UPDATE_COMPLETED:
                    //ICOM-3486 Begin And by b.jiang 20161020;
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG);
                    FileOperation.getInstance().closeFileTransChannel();
                    //ICOM-3486 End And by b.jiang 20161020;
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_COMPLETED");
                    MyProgressDialog.closeProgressDialog();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setMessage(R.string.setting_updatefw_closeAppInfo);
                    builder2.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "update FW completed!");
                        }
                    });
                    alertDialog = builder2.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case SDKEvent.EVENT_FW_UPDATE_POWEROFF:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_POWEROFF");
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG);

                    AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                    builder3.setMessage(R.string.setting_updatefw_closeAppInfo);
                    builder3.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App quit");
                            ExitApp.getInstance().exit();
                        }
                    });
                    alertDialog = builder3.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case SDKEvent.EVENT_FW_UPDATE_CHECK:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_CHECK");
                    break;
                case SDKEvent.EVENT_FW_UPDATE_CHKSUMERR:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_CHKSUMERR");
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG);
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(context);
                    builder5.setMessage(R.string.setting_updatefw_chec_sum_failed);
                    builder5.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App FW updatefw chech sume failed");
                            dialog.dismiss();
                        }
                    });
                    alertDialog = builder5.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;
                case SDKEvent.EVENT_FW_UPDATE_NG:
                    AppLog.d(TAG, "receive EVENT_FW_UPDATE_NG");
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_COMPLETED);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_POWEROFF);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHECK);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_CHKSUMERR);
                    sdkEvent.delEventListener(ICatchEventID.ICH_EVENT_FW_UPDATE_NG);
                    AlertDialog.Builder builder6 = new AlertDialog.Builder(context);
                    builder6.setMessage(R.string.setting_updatefw_failed);
                    builder6.setNegativeButton(R.string.dialog_btn_exit, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("1111", "App FW updatefw failed");
                            dialog.dismiss();
                            // ExitApp.getInstance().exit();
                            // do something by yourself
                        }
                    });
                    alertDialog = builder6.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    break;

            }
        }
    }

    public static void showSetDownloadSizeLimitDialog(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = View.inflate(context, R.layout.content_download_size_dialog, null);
        final EditText resetTxv = (EditText) contentView.findViewById(R.id.download_size);
        String value = AppInfo.autoDownloadSizeLimit + "";
        resetTxv.setHint(value);
        builder.setTitle(R.string.setting_auto_download_size_limit);
        builder.setView(contentView);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.action_save)
                // 为按钮设置监听器
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (resetTxv.getText().toString().equals("")) {
                            AppLog.d("3322", "ret dddddddddd");
                        } else {
                            float sizeLimit = Float.parseFloat(resetTxv.getText().toString());
                            AppSharedPreferences.writeDataByName(context, AppSharedPreferences.OBJECT_NAME, String.valueOf(sizeLimit));
                            AppInfo.autoDownloadSizeLimit = sizeLimit;
                            onSettingCompleteListener.onOptionSettingComplete();
                        }
                    }
                });
        // 为对话框设置一个“取消”按钮
        builder.setNegativeButton(context.getResources().getString(R.string.gallery_cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消登录，不做任何事情。
                    }
                });
        //创建、并显示对话框
        builder.create().show();
    }

    public static void showStorageLocationDialog(final Context context) {
        // TODO Auto-generated method stub
        final MyCamera currCamera = GlobalInfo.getInstance().getCurrentCamera();
        CharSequence title = context.getResources().getString(R.string.setting_storage_location);
        boolean sdCardExist = StorageUtil.sdCardExist(context);
        final String[] storageLocationString;
        int curIdx = 0;
        if (sdCardExist) {
            storageLocationString = new String[2];
            storageLocationString[0] = context.getResources().getString(R.string.setting_internal_storage);
            storageLocationString[1] = context.getResources().getString(R.string.setting_sd_card_storage);
        } else {
            storageLocationString = new String[1];
            storageLocationString[0] = context.getResources().getString(R.string.setting_internal_storage);
        }
        SharedPreferences preferences = context.getSharedPreferences("appData", MODE_PRIVATE);
        String storageLocation = preferences.getString("storageLocation", "InternalStorage");
        if (storageLocation.equals("InternalStorage")) {
            curIdx = 0;
        } else {
            curIdx = 1;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == 0) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                    editor.putString("storageLocation", "InternalStorage");
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("appData", MODE_PRIVATE).edit();
                    editor.putString("storageLocation", "SdCard");
                    editor.commit();
                }
                arg0.dismiss();
                onSettingCompleteListener.settingTimeLapseModeComplete(arg1);
                onSettingCompleteListener.onOptionSettingComplete();
                AppLog.d("tigertiger", "showStorageLocationDialog  storageLocation =" + arg1);
            }
        };
        showOptionDialog(title, storageLocationString, curIdx, listener, true);
    }
}
