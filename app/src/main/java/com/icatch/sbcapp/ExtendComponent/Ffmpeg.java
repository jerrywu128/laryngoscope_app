package com.icatch.sbcapp.ExtendComponent;

import static com.icatch.sbcapp.Tools.CrashHandler.TAG;

import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.Tools.StorageUtil;

import java.io.File;

public class Ffmpeg {
    private static final String TAG ="ffmpeg";

    public static void videoResize(){
        String [] tempStr = GlobalInfo.getInstance().getVideoName().split("\\.");//在檔名標示修改過
        String newVideoName = tempStr[0]+"R."+tempStr[1];
        FFmpegSession session = FFmpegKit.execute("-i "+ GlobalInfo.getInstance().getVideoName() + " -strict -2 -vf crop="+GlobalInfo.getInstance().getVideowidth()+":"
                + GlobalInfo.getInstance().getVideoheight()*0.75+":0:0 -preset veryslow "+ newVideoName );
        if (ReturnCode.isSuccess(session.getReturnCode())) {
            File file = new File(GlobalInfo.getInstance().getVideoName());
            file.delete();//刪除未裁切之視頻
            GlobalInfo.getInstance().setVideoName(newVideoName);//設定新名

            // SUCCESS

        } else if (ReturnCode.isCancel(session.getReturnCode())) {
            // CANCEL
        } else {

            // FAILURE
            Log.d(TAG, String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));
        }
    }
}
