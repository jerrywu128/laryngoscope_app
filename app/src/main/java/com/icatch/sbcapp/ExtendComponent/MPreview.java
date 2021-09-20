package com.icatch.sbcapp.ExtendComponent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.Listener.OnDecodeTimeListener;
import com.icatch.sbcapp.Listener.VideoFramePtsChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.PreviewLaunchMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.SdkApi.PreviewStream;
import com.icatch.sbcapp.SdkApi.VideoPlayback;
import com.icatch.sbcapp.Thread.Decoder.H264DecoderThread;
import com.icatch.sbcapp.Thread.Decoder.H264DecoderThreadAsyn;
import com.icatch.sbcapp.Thread.Decoder.MjpgDecoderThread;
import com.icatch.wificam.customer.type.ICatchCodec;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

/**
 * Created by zhang yanhu C001012 on 2015/12/3 14:15.
 */
public class MPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MPreview";
    private PreviewStream previewStream = PreviewStream.getInstance();
    private VideoPlayback videoPlayback = VideoPlayback.getInstance();
    private SurfaceHolder holder;
    private boolean hasSurface = false;
    private MyCamera mCamera;
    private MjpgDecoderThread mjpgDecoderThread;
    private H264DecoderThread h264DecoderThread;
//    private H264DecoderThreadAsyn h264DecoderThread;
    private boolean needStart = false;
    private int previewLaunchMode;
    private int previewCodec;
    private int frmW = 0;
    private int frmH = 0;
    private VideoFramePtsChangedListener videoPbUpdateBarLitener = null;
    private ICatchVideoFormat videoFormat;
    OnDecodeTimeListener onDecodeTimeListener;

    public MPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        AppLog.i(TAG, "create MPreview");
        //add callback if create preview
        holder = this.getHolder();
        holder.addCallback(this);
    }

    public void setOnDecodeTimeListener(OnDecodeTimeListener onDecodeTimeListener) {
        this.onDecodeTimeListener = onDecodeTimeListener;
    }

    public boolean start(MyCamera mCamera, int previewLaunchMode) {
        AppLog.i(TAG, "start preview hasSurface =" + hasSurface + " previewLaunchMode=" + previewLaunchMode);
        this.previewLaunchMode = previewLaunchMode;
        if (previewLaunchMode == PreviewLaunchMode.VIDEO_PB_MODE) {
            // play之后还不能立马获取到frame 宽高，sdk获取到一帧才会获取到宽高;
            // 采用延时获取宽高，三次延时可以获取到;
            int times = 0;
            while (frmW == 0 || frmH == 0) {
                if (times > 20) {
                    break;
                }
                videoFormat = videoPlayback.getVideoFormat();
                if (videoFormat != null) {
                    frmW = videoFormat.getVideoW();
                    frmH = videoFormat.getVideoH();
                }
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                times++;
            }
        } else if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
            videoFormat = previewStream.getVideoFormat(mCamera.getpreviewStreamClient());
            if (videoFormat != null) {
                frmW = videoFormat.getVideoW();
                frmH = videoFormat.getVideoH();
            }
        }
        AppLog.d(TAG, "init preview, VideoW =" + frmW + " VideoH=" + frmH);
        if (frmH * frmW == 0) {
            return false;
        }
        this.mCamera = mCamera;
        if (hasSurface == false) {
            needStart = true;
            return false;
        }
        startDecoderThread(previewLaunchMode, videoFormat);
        return true;
    }

    public boolean stop() {
        AppLog.i(TAG, "stopMPreview preview");
        if (mjpgDecoderThread != null) {
            mjpgDecoderThread.stop();
            postInvalidate();
            AppLog.i(TAG, "start stopMPreview mjpgDecoderThread.isAlive() =" + mjpgDecoderThread.isAlive());
        }
        if (h264DecoderThread != null) {
            h264DecoderThread.stop();
            AppLog.i(TAG, "start stopMPreview h264DecoderThread.isAlive() =" + h264DecoderThread.isAlive());
        }
        needStart = false;
        AppLog.i(TAG, "end preview");
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AppLog.i(TAG, "surfaceCreated hasSurface =" + hasSurface);
        hasSurface = true;
        if (needStart) {
            startDecoderThread(previewLaunchMode, videoFormat);
        }
    }

    public void startDecoderThread(int previewLaunchMode, ICatchVideoFormat videoFormat) {
        AppLog.i(TAG, "start startDecoderThread");
        boolean enableAudio = true;
        if (videoFormat == null) {
            AppLog.i(TAG, "start startDecoderThread videoFormat=" + videoFormat);
            return;
        }
        previewCodec = videoFormat.getCodec();
        if (previewLaunchMode == PreviewLaunchMode.VIDEO_PB_MODE) {
            enableAudio = true;
        } else if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
            enableAudio = previewStream.supportAudio(mCamera.getpreviewStreamClient()) && (!AppInfo.disableAudio);
        }
        AppLog.i(TAG, "start startDecoderThread previewCodec=" + previewCodec + " enableAudio=" + enableAudio);
        switch (previewCodec) {
            case ICatchCodec.ICH_CODEC_RGBA_8888:
                mjpgDecoderThread = new MjpgDecoderThread(mCamera, holder, this, previewLaunchMode, videoFormat, videoPbUpdateBarLitener);
                mjpgDecoderThread.setOnDecodeTimeListener(onDecodeTimeListener);
                mjpgDecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;
            case ICatchCodec.ICH_CODEC_H264:
                h264DecoderThread = new H264DecoderThread(mCamera, holder, previewLaunchMode, videoFormat, videoPbUpdateBarLitener);
                h264DecoderThread.setOnDecodeTimeListener(onDecodeTimeListener);
                h264DecoderThread.start(enableAudio, true);
                setSurfaceViewArea();
                break;

//            case ICatchCodec.ICH_CODEC_H264:
//                h264DecoderThread = new H264DecoderThreadAsyn(mCamera, holder, previewLaunchMode, videoFormat, videoPbUpdateBarLitener);
//                h264DecoderThread.setOnDecodeTimeListener(onDecodeTimeListener);
//                h264DecoderThread.start(enableAudio, true);
//                setSurfaceViewArea();
//                break;
            default:
                return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        AppLog.i(TAG, " surfaceDestroyed hasSurface =" + hasSurface);
        hasSurface = false;
        stop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        AppLog.d(TAG, " redrawBitmap");
        AppLog.d(TAG, "start startDecoderThread.I'm coming......");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        AppLog.d(TAG, " change ....onLayout");
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                setSurfaceViewArea();
            }
        });

    }

    public void setSurfaceViewArea() {
        if (frmH == 0 || frmW == 0) {
            return;
        }
        View parentView = (View) this.getParent();
        int mWidth = parentView.getWidth();
        int mHeigth = parentView.getHeight();
        AppLog.i(TAG, "start setSurfaceViewArea frmW=" + frmW + " frmH=" + frmH + " mWidth=" + mWidth + " mHeigth=" + mHeigth);
//        if (frmH > 1080) {
//            AppLog.e(TAG, "Illegal frmW frmW!!!");
//            return;
//        }
        if (frmH <= 0 || frmW <= 0) {
            AppLog.e(TAG, "setSurfaceViewArea frmW or frmH <= 0!!!");
            holder.setFixedSize(mWidth, mWidth * 9 / 16);
            return;
        }
        if (mWidth == 0 || mHeigth == 0) {
            return;
        }

        //JIRA IC-666
        if (previewCodec == ICatchCodec.ICH_CODEC_RGBA_8888) {
            if (mjpgDecoderThread != null && previewLaunchMode == PreviewLaunchMode.VIDEO_PB_MODE) {
                mjpgDecoderThread.redrawBitmap(holder, mWidth, mHeigth);
            }
        } else if (previewCodec == ICatchCodec.ICH_CODEC_H264) {
            if (mWidth * frmH / frmW <= mHeigth) {
                holder.setFixedSize(mWidth, mWidth * frmH / frmW);
            } else {
                holder.setFixedSize(mHeigth * frmW / frmH, mHeigth);
            }
        }
        AppLog.d(TAG, "end setSurfaceViewArea");
    }

    public void addVideoFramePtsChangedListener(VideoFramePtsChangedListener videoPbUpdateBarLitener) {
        this.videoPbUpdateBarLitener = videoPbUpdateBarLitener;
    }
}

