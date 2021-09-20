/**
 * Added by zhangyanhu C01012,2014-6-27
 */
package com.icatch.sbcapp.SdkApi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.icatch.sbcapp.BaseItems.Tristate;
import com.icatch.sbcapp.Dbl.DatabaseHelper;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.test.test;
import com.icatch.wificam.customer.ICatchWificamPreview;
import com.icatch.wificam.customer.exception.IchAudioStreamClosedException;
import com.icatch.wificam.customer.exception.IchBufferTooSmallException;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchInvalidArgumentException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.exception.IchStreamNotRunningException;
import com.icatch.wificam.customer.exception.IchStreamNotSupportException;
import com.icatch.wificam.customer.exception.IchStreamPublishException;
import com.icatch.wificam.customer.exception.IchTryAgainException;
import com.icatch.wificam.customer.exception.IchVideoStreamClosedException;
import com.icatch.wificam.customer.type.ICatchAudioFormat;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;
import com.icatch.wificam.customer.type.ICatchPreviewMode;
import com.icatch.wificam.customer.type.ICatchStreamParam;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

//import junit.framework.Test;

import java.nio.ByteBuffer;

public class PreviewStream {
    private final String TAG = "PreviewStream";
    private static PreviewStream instance;

    // private ICatchWificamPreview previewStreamControl;
    //
    public static PreviewStream getInstance() {
        if (instance == null) {
            instance = new PreviewStream();
        }
        return instance;
    }

    public boolean stopMediaStream(ICatchWificamPreview previewStreamControl) {
        AppLog.i(TAG, "begin stopMediaStream");
        boolean retValue = false;
        try {
            retValue = previewStreamControl.stop();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end stopMediaStream =" + retValue);
        return retValue;
    }

    public boolean supportAudio(ICatchWificamPreview previewStreamControl) {
        AppLog.i(TAG, "begin supportAudio");
        boolean retValue = false;
        try {
            retValue = previewStreamControl.containsAudioStream();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end supportAudio retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public boolean getNextVideoFrame(ICatchFrameBuffer buffer, ICatchWificamPreview previewStreamControl) {
        // AppLog.i(TAG,
        // "begin getNextVideoFrame");
        boolean retValue = false;
        // Log.d("tigertiger","previewStream = "+previewStream);
        try {
            retValue = previewStreamControl.getNextVideoFrame(buffer);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // need to close preview get next video frame
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            AppLog.e(TAG, "IchBufferTooSmallException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchTryAgainException e) {
            AppLog.e(TAG, "IchTryAgainException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            AppLog.e(TAG, "IchInvalidArgumentException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchVideoStreamClosedException e) {
            AppLog.e(TAG, "IchVideoStreamClosedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // AppLog.i(TAG,
        // "end getNextVideoFrame retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public boolean getNextAudioFrame(ICatchWificamPreview previewStreamControl, ICatchFrameBuffer icatchBuffer) {
        // AppLog.i(TAG,
        // "begin getNextAudioFrame");
        boolean retValue = false;
        try {
            retValue = previewStreamControl.getNextAudioFrame(icatchBuffer);
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchBufferTooSmallException e) {
            AppLog.e(TAG, "IchBufferTooSmallException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchTryAgainException e) {
            AppLog.e(TAG, "IchTryAgainException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            AppLog.e(TAG, "IchInvalidArgumentException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchAudioStreamClosedException e) {
            AppLog.e(TAG, "IchAudioStreamClosedException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // AppLog.i(TAG,
        // "end getNextAudioFrame retValue =" + retValue);
        return retValue;
    }

    /**
     * Added by zhangyanhu C01012,2014-7-2
     */
    public Tristate startMediaStream(ICatchWificamPreview previewStreamControl, ICatchStreamParam param,
                                     ICatchPreviewMode previewMode, boolean disableAudio) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin startMediaStream param=" + param + " disableAudio=" + disableAudio + " previewMode=" + previewMode);
        boolean temp = false;
        Tristate retValue = Tristate.FALSE;
        try {
            temp = previewStreamControl.start(param, previewMode, disableAudio);
            if (temp) {
                retValue = Tristate.NORMAL;
            } else {
                retValue = Tristate.FALSE;
            }
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            AppLog.e(TAG, "IchInvalidArgumentException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AppLog.e(TAG, "IchStreamNotSupportException");
            retValue = Tristate.ABNORMAL;
        }
        AppLog.i(TAG, "end startMediaStream retValue =" + retValue);
        return retValue;
    }

    //ICOM-2956 Start
    public boolean changePreviewMode(ICatchWificamPreview previewStreamControl, ICatchPreviewMode previewMode) {
        AppLog.i(TAG, "begin changePreviewMode previewMode=" + previewMode);
        boolean retValue = false;
        try {
            retValue = previewStreamControl.changePreviewMode(previewMode);
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            AppLog.e(TAG, "IchStreamNotSupportException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end changePreviewMode ret=" + retValue);
        return retValue;
    }
    //ICOM-2956 End

    public int getVideoWidth(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getVideoWidth");
        int retValue = 0;

        try {
            retValue = previewStreamControl.getVideoFormat().getVideoW();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getVideoWidth retValue =" + retValue);
        return retValue;
    }

    public int getVideoHeigth(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getVideoHeigth");
        int retValue = 0;

        try {
            retValue = previewStreamControl.getVideoFormat().getVideoH();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getVideoHeigth retValue =" + retValue);
        return retValue;
    }

    public ICatchVideoFormat getVideoFormat(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getVideoFormat");
        ICatchVideoFormat videoFormat = null;

        try {
            videoFormat = previewStreamControl.getVideoFormat();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getVideoFormat videoFormat =" + videoFormat);
        return videoFormat;
    }

    public int getCodec(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getCodec previewStreamControl =" + previewStreamControl);
        int retValue = 0;

        try {
            retValue = previewStreamControl.getVideoFormat().getCodec();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getCodec retValue =" + retValue);
        return retValue;
    }

    public int getBitrate(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getBitrate");
        int retValue = 0;

        try {
            retValue = previewStreamControl.getVideoFormat().getBitrate();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getBitrate retValue =" + retValue);
        return retValue;
    }

    public ICatchAudioFormat getAudioFormat(ICatchWificamPreview previewStreamControl) {
        // TODO Auto-generated method stub
        AppLog.i(TAG, "begin getAudioFormat");
        ICatchAudioFormat retValue = null;

        try {
            retValue = previewStreamControl.getAudioFormat();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            AppLog.e(TAG, "IchCameraModeException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            AppLog.e(TAG, "IchInvalidSessionException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IchStreamNotRunningException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        }
        AppLog.i(TAG, "end getAudioFormat retValue =" + retValue);
        return retValue;
    }

    public boolean enableAudio(ICatchWificamPreview previewStreamControl) {
        AppLog.d(TAG, "start enableAudio");
        boolean value = false;
        try {
            value = previewStreamControl.enableAudio();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotSupportException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "end enableAudio value = " + value);
        return value;
    }
    //end by b.jiang 20160108

    //start add by b.jiang 20160108
    public boolean disableAudio(ICatchWificamPreview previewStreamControl) {
        AppLog.d(TAG, "start disableAudio");
        boolean value = false;
        try {
            value = previewStreamControl.disableAudio();
        } catch (IchSocketException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchCameraModeException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchCameraModeException");
            e.printStackTrace();
        } catch (IchInvalidSessionException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchInvalidSessionException");
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            // TODO Auto-generated catch block
            AppLog.e(TAG, "IchStreamNotSupportException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "end disableAudio value = " + value);
        return value;
    }

    public boolean startPublishStreaming(ICatchWificamPreview previewStreamControl, String rtmpUrl) {
        AppLog.d(TAG, "start startPublishStreaming ");
        boolean ret = false;
        try {
            ret = previewStreamControl.startPublishStreaming(rtmpUrl);
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            AppLog.e(TAG, "IchStreamNotSupportException");
            e.printStackTrace();
        } catch (IchSocketException e) {
            AppLog.e(TAG, "IchSocketException");
            e.printStackTrace();
        } catch (IchStreamPublishException e) {
            AppLog.e(TAG, "IchStreamPublishException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End startPublishStreaming ret=" + ret);
        return ret;
    }

    public boolean stopPublishStreaming(ICatchWificamPreview previewStreamControl) {
        AppLog.d(TAG, "start stopPublishStreaming ");
        boolean ret = false;
        try {
            ret = previewStreamControl.stopPublishStreaming();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            AppLog.e(TAG, "IchInvalidArgumentException");
            e.printStackTrace();
        } catch (IchStreamPublishException e) {
            AppLog.e(TAG, "IchStreamPublishException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End stopPublishStreaming ret=" + ret);
        return ret;
    }

    public boolean isStreamSupportPublish(ICatchWificamPreview previewStreamControl) {
        AppLog.d(TAG, "start isStreamSupportPublish ");
        boolean ret = false;
        try {
            ret = previewStreamControl.isStreamSupportPublish();
        } catch (IchStreamNotRunningException e) {
            AppLog.e(TAG, "IchStreamNotRunningException");
            e.printStackTrace();
        } catch (IchStreamNotSupportException e) {
            AppLog.e(TAG, "IchStreamNotSupportException");
            e.printStackTrace();
        }
        AppLog.d(TAG, "End isStreamSupportPublish=" + ret);
        return ret;
    }

    public boolean savePvThumbnail(ICatchWificamPreview previewStreamControl,int frameWidth, int frameHeight) {
        AppLog.d(TAG, "start isStreamSupportPublish ");
        boolean ret = false;
        byte[] mPixel = new byte[frameWidth * frameHeight * 4];
        ICatchFrameBuffer frameBuffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
        frameBuffer.setBuffer(mPixel);
        try {
            ret = previewStreamControl.getThumbnail(frameBuffer);
        } catch (IchInvalidSessionException e) {
            e.printStackTrace();
        } catch (IchInvalidArgumentException e) {
            e.printStackTrace();
        }
//        test.saveImage11(frameBuffer,frameBuffer.getFrameSize());
        Bitmap bitmap = BitmapFactory.decodeByteArray(mPixel, 0, mPixel.length);
        DatabaseHelper.updateCameraPhoto(GlobalInfo.curSlotId, bitmap);
        AppLog.d(TAG, "End isStreamSupportPublish=" + ret);
        return ret;
    }



}
