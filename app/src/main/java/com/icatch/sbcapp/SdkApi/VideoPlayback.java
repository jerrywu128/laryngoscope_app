/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.sbcapp.SdkApi;

import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.wificam.customer.ICatchWificamVideoPlayback;
import com.icatch.wificam.customer.exception.IchAudioStreamClosedException;
import com.icatch.wificam.customer.exception.IchBufferTooSmallException;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchInvalidArgumentException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchNoSuchFileException;
import com.icatch.wificam.customer.exception.IchPauseFailedException;
import com.icatch.wificam.customer.exception.IchPbStreamPausedException;
import com.icatch.wificam.customer.exception.IchResumeFailedException;
import com.icatch.wificam.customer.exception.IchSeekFailedException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.exception.IchStreamNotRunningException;
import com.icatch.wificam.customer.exception.IchTryAgainException;
import com.icatch.wificam.customer.exception.IchVideoStreamClosedException;
import com.icatch.wificam.customer.type.ICatchAudioFormat;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

public class VideoPlayback {
    private final String tag = "VideoPlayback";
    private static VideoPlayback instance;
    private ICatchWificamVideoPlayback videoPlayback;

    public static VideoPlayback getInstance() {
        if (instance == null) {
            instance = new VideoPlayback();
        }
        return instance;
    }

    private VideoPlayback() {

    }

    public void initVideoPlayback() {
        videoPlayback = GlobalInfo.getInstance().getCurrentCamera().getVideoPlaybackClint();
    }

    //用于本地video播放时对vidoePlayback的初始化;
    public void initVideoPlayback(ICatchWificamVideoPlayback videoPlayback) {
        this.videoPlayback = videoPlayback;
    }

    public boolean stopPlaybackStream() {
        AppLog.i(tag, "start stopPlaybackStream ");
        String sdkApi = "ICatchWificamVideoPlayback.stop() ";
        if (videoPlayback == null) {
            return true;
        }
        boolean retValue = false;
        try {
            retValue = videoPlayback.stop();
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "stopPlaybackStream =" + retValue);
        return retValue;
    }

    public boolean stopPlaybackStream(ICatchWificamVideoPlayback videoPlayback) {
        AppLog.i(tag, "start stopPlaybackStream ");
        String sdkApi = "ICatchWificamVideoPlayback.stop() ";
        if (videoPlayback == null) {
            return true;
        }
        boolean retValue = false;
        try {
            retValue = videoPlayback.stop();
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "stopPlaybackStream =" + retValue);
        return retValue;
    }

    public boolean startPlaybackStream(ICatchFile file) {
        boolean retValue = false;
        AppLog.i(tag, "begin startPlaybackStream file=" + file);
        String sdkApi = "ICatchWificamVideoPlayback.play() ";
        try {
            retValue = videoPlayback.play(file);
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSuchFileException e) {
            AppLog.e(tag, sdkApi + "IchNoSuchFileException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "-----------end startPlaybackStream retValue =" + retValue);
        return retValue;
    }

    public boolean startPlaybackStream(String fileName) {
        String sdkApi = "ICatchWificamVideoPlayback.play() ";
        boolean retValue = false;
        ICatchFile icathfile = new ICatchFile(33, ICatchFileType.ICH_TYPE_VIDEO, fileName, "", 0);
        AppLog.d(tag, "begin startPlaybackStream file=" + fileName);
        try {
            retValue = videoPlayback.play(icathfile, false, false);
//			retValue = videoPlayback.play(icathfile);
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchNoSuchFileException e) {
            AppLog.e(tag, sdkApi + "IchNoSuchFileException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "-----------end startPlaybackStream retValue =" + retValue);
        return retValue;
    }

    public boolean pausePlayback() {
        String sdkApi = "ICatchWificamVideoPlayback.pause() ";
        AppLog.i(tag, "begin pausePlayback");
        boolean retValue = false;
        try {
            retValue = videoPlayback.pause();
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchPauseFailedException e) {
            AppLog.e(tag, sdkApi + "IchPauseFailedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end pausePlayback =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-3-20
     */
    public boolean resumePlayback() {
        String sdkApi = "ICatchWificamVideoPlayback.resume() ";
        AppLog.i(tag, "begin resumePlayback");
        boolean retValue = false;
        try {
            retValue = videoPlayback.resume();
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchResumeFailedException e) {
            AppLog.e(tag, sdkApi + "IchResumeFailedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i("VideoPlayback", "end resumePlayback retValue=" + retValue);
        return retValue;
    }

    public int getVideoDuration() {
        String sdkApi = "ICatchWificamVideoPlayback.getLength() ";
        AppLog.i(tag, "begin getVideoDuration");
        double temp = 0;
        try {
            temp = videoPlayback.getLength();
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end getVideoDuration temp =" + temp);
        AppLog.i(tag, "end getVideoDuration length =" + new Double(temp * 100).intValue());
        return (new Double(temp * 100).intValue());
    }

    public boolean videoSeek(double position) {
        String sdkApi = "ICatchWificamVideoPlayback.seek() ";
        AppLog.i(tag, "begin videoSeek position = " + position);
        boolean retValue = false;
        try {
            retValue = videoPlayback.seek(position);
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchSeekFailedException e) {
            AppLog.e(tag, sdkApi + "IchSeekFailedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end videoSeek retValue=" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public boolean getNextVideoFrame(ICatchFrameBuffer buffer) {
        String sdkApi = "ICatchWificamVideoPlayback.getNextVideoFrame() ";
        AppLog.i(tag, "begin getNextVideoFrame");
        boolean retValue = false;
        try {
            retValue = videoPlayback.getNextVideoFrame(buffer);
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchBufferTooSmallException");
            e.printStackTrace();
        } catch (IchTryAgainException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchTryAgainException");
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchInvalidArgumentException");
            e.printStackTrace();
        } catch (IchVideoStreamClosedException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchVideoStreamClosedException");
            e.printStackTrace();
        } catch (IchPbStreamPausedException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchPbStreamPausedException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end getNextVideoFrame  retValue= " + retValue);
        return retValue;
    }

    public boolean getNextAudioFrame(ICatchFrameBuffer buffer) {
        String sdkApi = "ICatchWificamVideoPlayback.getNextAudioFrame() ";
        AppLog.i(tag, "begin getNextAudioFrame");
        boolean retValue = false;
        try {
            retValue = videoPlayback.getNextAudioFrame(buffer);
        } catch (IchSocketException e) {
            AppLog.e(tag, sdkApi + "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, sdkApi + "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, sdkApi + "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(tag, sdkApi + "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchBufferTooSmallException");
            e.printStackTrace();
        } catch (IchTryAgainException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchTryAgainException");
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, sdkApi + "IchInvalidArgumentException");
            e.printStackTrace();
        } catch (IchAudioStreamClosedException e) {
            AppLog.e(tag, sdkApi + "IchAudioStreamClosedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchPbStreamPausedException e) {
            AppLog.e(tag, sdkApi + "IchPbStreamPausedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(tag, "end getNextAudioFrame  retValue= " + retValue);
        return retValue;
    }

    public boolean containsAudioStream() {
        AppLog.i(tag, "begin containsAudioStream");
        boolean retValue = false;
        try {
            retValue = videoPlayback.containsAudioStream();
        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end containsAudioStream  retValue= " + retValue);
        return retValue;
    }

    public ICatchAudioFormat getAudioFormat() {
        AppLog.i(tag, "begin getAudioFormat");
        ICatchAudioFormat retValue = null;
        try {
            retValue = videoPlayback.getAudioFormat();

        } catch (IchSocketException e) {
            AppLog.e(tag, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(tag, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(tag, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(tag, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(tag, "end getAudioFormat  retValue= " + retValue);
        return retValue;
    }

    public ICatchVideoFormat getVideoFormat() {
        AppLog.i(tag, "begin getVideoFormat");
        ICatchVideoFormat retValue = null;
        try {
            retValue = videoPlayback.getVideoFormat();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AppLog.i(tag, "end getVideoFormat  retValue= " + retValue);
        return retValue;
    }

}
