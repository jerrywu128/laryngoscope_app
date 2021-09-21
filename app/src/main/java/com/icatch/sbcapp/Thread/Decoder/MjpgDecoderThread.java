package com.icatch.sbcapp.Thread.Decoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.SurfaceHolder;

import com.icatch.sbcapp.Dbl.DatabaseHelper;
import com.icatch.sbcapp.ExtendComponent.MPreview;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnDecodeTimeListener;
import com.icatch.sbcapp.Listener.VideoFramePtsChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.PreviewLaunchMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.SdkApi.PreviewStream;
import com.icatch.sbcapp.SdkApi.VideoPlayback;
import com.icatch.sbcapp.Tools.ScaleTool;
import com.icatch.sbcapp.test.test;
import com.icatch.wificam.customer.ICatchWificamPreview;
import com.icatch.wificam.customer.ICatchWificamVideoPlayback;
import com.icatch.wificam.customer.exception.IchTryAgainException;
import com.icatch.wificam.customer.type.ICatchAudioFormat;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

import java.nio.ByteBuffer;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 11:03.
 */
public class MjpgDecoderThread {
    private static final String TAG = "MjpgDecoderThread";
    private final ICatchWificamPreview previewStreamControl;
    private ICatchWificamVideoPlayback videoPbControl;
    private final MPreview mPreview;
    private Bitmap videoFrameBitmap;
    private int frameWidth;
    private int frameHeight;
    private PreviewStream previewStream = PreviewStream.getInstance();
    private VideoPlayback videoPlayback = VideoPlayback.getInstance();
    private SurfaceHolder surfaceHolder;
    private AudioThread audioThread;
    private VideoThread videoThread;
    private int previewLaunchMode;
    private VideoFramePtsChangedListener videoPbUpdateBarLitener = null;
    private Rect drawFrameRect;
    private ICatchVideoFormat videoFormat;
    private OnDecodeTimeListener onDecodeTimeListener;

    public void setOnDecodeTimeListener(OnDecodeTimeListener onDecodeTimeListener) {
        this.onDecodeTimeListener = onDecodeTimeListener;
    }

    public MjpgDecoderThread(MyCamera mCamera, SurfaceHolder holder, MPreview mPreview, int previewLaunchMode, ICatchVideoFormat iCatchVideoFormat,
                             VideoFramePtsChangedListener videoPbUpdateBarLitener) {
        this.surfaceHolder = holder;
        this.mPreview = mPreview;
        this.previewLaunchMode = previewLaunchMode;
        previewStreamControl = mCamera.getpreviewStreamClient();
        videoPbControl = mCamera.getVideoPlaybackClint();
        this.videoPbUpdateBarLitener = videoPbUpdateBarLitener;
        this.videoFormat = iCatchVideoFormat;
        if (videoFormat != null) {
            frameWidth = videoFormat.getVideoW();
            frameHeight = videoFormat.getVideoH();
        }
        holder.setFormat(PixelFormat.RGBA_8888);
    }

    public void start(boolean enableAudio, boolean enableVideo) {
        AppLog.i(TAG, "start");
        if (enableAudio) {
            audioThread = new AudioThread();
            audioThread.start();
        }
        if (enableVideo) {
            videoThread = new VideoThread();
            videoThread.start();
        }
    }

    public boolean isAlive() {
        if (videoThread != null && videoThread.isAlive() == true) {
            return true;
        }
        if (audioThread != null && audioThread.isAlive() == true) {
            return true;
        }
        return false;
    }

    public void stop() {
        if (audioThread != null) {
            audioThread.requestExitAndWait();
        }
        if (videoThread != null) {
            videoThread.requestExitAndWait();
        }
    }

    private class VideoThread extends Thread {
        private boolean done;
        private ByteBuffer bmpBuf;
        private byte[] pixelBuf;

        VideoThread() {
            super();
            done = false;
            pixelBuf = new byte[frameWidth * frameHeight * 4];
            bmpBuf = ByteBuffer.wrap(pixelBuf);
            // Trigger onDraw with those initialize parameters
            videoFrameBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
            drawFrameRect = new Rect(0, 0, frameWidth, frameHeight);
        }

        @Override
        public void run() {
            AppLog.i(TAG, "start running video thread");
            ICatchFrameBuffer buffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
            buffer.setBuffer(pixelBuf);
            boolean temp = false;
            boolean isSaveBitmapToDb = false;
            boolean isFirstFrame = true;
            boolean isStartGet = true;
            long lastTime = System.currentTimeMillis();
            long testTime;
            while (!done) {
                temp = false;
                try {
                    if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                        temp = previewStreamControl.getNextVideoFrame(buffer);
                    } else {
                        temp = videoPbControl.getNextVideoFrame(buffer);
                    }
                } catch (IchTryAgainException e) {
//                    AppLog.e(TAG, "IchTryAgainException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    return;
                }
                if (temp == false) {
//                    AppLog.e(TAG,"getNextVideoFrame failed\n");
                    continue;
                }
                if (buffer == null || buffer.getFrameSize() == 0) {
                    AppLog.e(TAG, "getNextVideoFrame buffer == null\n");
                    continue;
                }

                bmpBuf.rewind();
//                test.savefile(buffer,buffer.getFrameSize());
                if (videoFrameBitmap == null) {
                    continue;
                }
                if (isFirstFrame) {
                    isFirstFrame = false;
                    AppLog.i(TAG, "get first Frame");
                }
                videoFrameBitmap.copyPixelsFromBuffer(bmpBuf);

                if (!isSaveBitmapToDb) {
                    if (videoFrameBitmap != null && previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseHelper.updateCameraPhoto(GlobalInfo.curSlotId, videoFrameBitmap);
                            }
                        }).start();
                        isSaveBitmapToDb = true;
                    }
                }

                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                int w = mPreview.getWidth();
                int h = mPreview.getHeight();
                drawFrameRect = ScaleTool.getScaledPosition(frameWidth, frameHeight, w, h);
                canvas.drawBitmap(videoFrameBitmap, null, drawFrameRect, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
                if (onDecodeTimeListener != null && buffer != null) {
                    if (System.currentTimeMillis() - lastTime > 500) {
                        lastTime = System.currentTimeMillis();
                        long decodeTime = buffer.getDecodeTime();
                        onDecodeTimeListener.decodeTime(decodeTime);
                    }
                }
                if (previewLaunchMode == PreviewLaunchMode.VIDEO_PB_MODE && videoPbUpdateBarLitener != null) {
                    videoPbUpdateBarLitener.onFramePtsChanged(buffer.getPresentationTime());
                }
            }
            AppLog.i(TAG, "stopMPreview video thread");
        }



        public void requestExitAndWait() {
            // 把这个线程标记为完成，并合并到主程序线程
            done = true;
            try {
                join();
            } catch (InterruptedException ex) {
            }
        }
    }



    public void redrawBitmap() {
        AppLog.i(TAG, "redrawBitmap");
        if (videoFrameBitmap != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            int w = mPreview.getWidth();
            int h = mPreview.getHeight();
            AppLog.i(TAG, "redrawBitmap mPreview.getWidth()=" + mPreview.getWidth());
            AppLog.i(TAG, "redrawBitmap mPreview.getHeight()=" + mPreview.getHeight());
            Rect drawFrameRect = ScaleTool.getScaledPosition(frameWidth, frameHeight, w, h);
            canvas.drawBitmap(videoFrameBitmap, null, drawFrameRect, null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private class AudioThread extends Thread {
        private boolean done = false;
        private AudioTrack audioTrack;

        public void run() {
            AppLog.i(TAG, "Run AudioThread");
            ICatchAudioFormat audioFormat;
            if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                audioFormat = previewStream.getAudioFormat(previewStreamControl);
            } else {
                audioFormat = videoPlayback.getAudioFormat();
            }

            if (audioFormat == null) {
                AppLog.e(TAG, "Run AudioThread audioFormat is null!");
                return;
            }
            int bufferSize = AudioTrack.getMinBufferSize(audioFormat.getFrequency(), audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO
                    : AudioFormat.CHANNEL_IN_LEFT, audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioFormat.getFrequency(), audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO
                    : AudioFormat.CHANNEL_IN_LEFT, audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT,
                    bufferSize, AudioTrack.MODE_STREAM);

            audioTrack.play();
            AppLog.i(TAG, "Run AudioThread 3");
            byte[] audioBuffer = new byte[1024 * 50];
            ICatchFrameBuffer icatchBuffer = new ICatchFrameBuffer(1024 * 50);
            icatchBuffer.setBuffer(audioBuffer);
            boolean temp = false;
            while (!done) {
                temp = false;
                try {
                    if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                        temp = previewStreamControl.getNextAudioFrame(icatchBuffer);
                    } else {
                        temp = videoPbControl.getNextAudioFrame(icatchBuffer);
                    }
                } catch (IchTryAgainException e) {
                    //AppLog.e(TAG, "getNextAudioFrame IchTryAgainException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    return;
                }
                if (false == temp) {
                    continue;
                }
                audioTrack.write(icatchBuffer.getBuffer(), 0, icatchBuffer.getFrameSize());
            }
            audioTrack.stop();
            audioTrack.release();
            AppLog.i(TAG, "stopMPreview audio thread");

        }

        public void requestExitAndWait() {
            done = true;
            try {
                join();
            } catch (InterruptedException ex) {
            }
        }
    }

    public void redrawBitmap(SurfaceHolder holder, int w, int h) {
        SurfaceHolder surfaceHolder = holder;
        AppLog.d(TAG, "redrawBitmap w=" + w + " h=" + h);
        AppLog.d(TAG, "redrawBitmap frameWidth=" + frameWidth + " frameHeight=" + frameHeight);
        AppLog.d(TAG, "redrawBitmap videoFrameBitmap=" + videoFrameBitmap);
        if (videoFrameBitmap != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                drawFrameRect = ScaleTool.getScaledPosition(frameWidth, frameHeight, w, h);
                canvas.drawBitmap(videoFrameBitmap, null, drawFrameRect, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
