package com.icatch.sbcapp.Thread.Decoder;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

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
import com.icatch.wificam.customer.ICatchWificamPreview;
import com.icatch.wificam.customer.ICatchWificamVideoPlayback;
import com.icatch.wificam.customer.exception.IchAudioStreamClosedException;
import com.icatch.wificam.customer.exception.IchBufferTooSmallException;
import com.icatch.wificam.customer.exception.IchCameraModeException;
import com.icatch.wificam.customer.exception.IchInvalidArgumentException;
import com.icatch.wificam.customer.exception.IchInvalidSessionException;
import com.icatch.wificam.customer.exception.IchPbStreamPausedException;
import com.icatch.wificam.customer.exception.IchSocketException;
import com.icatch.wificam.customer.exception.IchStreamNotRunningException;
import com.icatch.wificam.customer.exception.IchTryAgainException;
import com.icatch.wificam.customer.type.ICatchAudioFormat;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 11:03.
 */
public class H264DecoderThread {
    private static final String TAG = "H264DecoderThread";
    private final ICatchWificamPreview previewStreamControl;
    private ICatchWificamVideoPlayback videoPbControl;
    private PreviewStream previewStream = PreviewStream.getInstance();
    private VideoPlayback videoPlayback = VideoPlayback.getInstance();
    private SurfaceHolder surfaceHolder;
    private VideoThread videoThread;
    private AudioThread audioThread;
    private boolean audioPlayFlag = false;
    private int BUFFER_LENGTH = 1280 * 720 * 4;
    //    private int timeout = 60000;// us
    private int timeout = 20000;// us
    private MediaCodec decoder;
    private int previewLaunchMode;
    private VideoFramePtsChangedListener videoPbUpdateBarLitener;
    private ICatchVideoFormat videoFormat;
    private int frameWidth;
    private int frameHeight;
    private OnDecodeTimeListener onDecodeTimeListener;
    private int fps;

    public H264DecoderThread(MyCamera mCamera, SurfaceHolder holder, int previewLaunchMode, ICatchVideoFormat iCatchVideoFormat,
                             VideoFramePtsChangedListener videoPbUpdateBarLitener) {

        this.surfaceHolder = holder;
        this.previewLaunchMode = previewLaunchMode;
        previewStreamControl = mCamera.getpreviewStreamClient();
        videoPbControl = mCamera.getVideoPlaybackClint();
        this.videoFormat = iCatchVideoFormat;
        this.videoPbUpdateBarLitener = videoPbUpdateBarLitener;
        holder.setFormat(PixelFormat.RGBA_8888);
        if (videoFormat != null) {
            frameWidth = videoFormat.getVideoW();
            frameHeight = videoFormat.getVideoH();
            fps = videoFormat.getFps();
        }
        AppLog.i(TAG, "H264DecoderThread frameWidth=" + frameWidth);
        AppLog.i(TAG, "H264DecoderThread frameHeight=" + frameHeight);
        AppLog.i(TAG, "H264DecoderThread fps=" + fps);
        timeout = 30000;
//        if (fps > 30) {
//            timeout = 30000;
//        } else {
//            timeout = 60000;
//        }
    }

    public void setOnDecodeTimeListener(OnDecodeTimeListener onDecodeTimeListener) {
        this.onDecodeTimeListener = onDecodeTimeListener;
    }


    public void start(boolean enableAudio, boolean enableVideo) {
        AppLog.i(TAG, "start");
        setFormat();
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
        audioPlayFlag = false;
    }

    long videoShowtime = 0;
    double curVideoPts = 0;

    private class VideoThread extends Thread {

        private boolean done = false;
        private MediaCodec.BufferInfo info;
        long startTime = 0;
        int frameSize = 0;

        VideoThread() {
            super();
            done = false;
        }

        @Override
        public void run() {
            AppLog.i(TAG, "h264 run for gettting surface image");

            info = new MediaCodec.BufferInfo();
//            byte[] mPixel = new byte[BUFFER_LENGTH];
            byte[] mPixel = new byte[frameWidth * frameHeight * 4];
            ICatchFrameBuffer frameBuffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
            frameBuffer.setBuffer(mPixel);
            int inIndex = -1;
            int sampleSize = 0;
            long pts = 0;
            boolean retvalue = true;
            boolean isFirst = true;
            long lastTime = System.currentTimeMillis();
            long testTime = System.currentTimeMillis();
            long currentTime;
            boolean isSaveBitmapToDb = false;
            while (!done) {
                retvalue = false;
                curVideoPts = -1;

                try {
//                    AppLog.d(TAG, "end time=" + (System.currentTimeMillis() - endTime));
//                    endTime = System.currentTimeMillis();
                    if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                        retvalue = previewStreamControl.getNextVideoFrame(frameBuffer);
                    } else {
                        retvalue = videoPbControl.getNextVideoFrame(frameBuffer);
                    }
                    if (!retvalue) {
                        continue;
                    }
                } catch (IchTryAgainException ex) {
                    ex.printStackTrace();
                    retvalue = false;
//                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    continue;
                } catch (Exception ex) {
                    AppLog.e(TAG, "getNextVideoFrame " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                    retvalue = false;
                    break;
                }
                if (frameBuffer.getFrameSize() <= 0 || frameBuffer == null) {
                    retvalue = false;
                    continue;
                }
                if (!retvalue) {
                    continue;
                }
                try {
                    inIndex = decoder.dequeueInputBuffer(10000);
//                inIndex = decoder.dequeueInputBuffer(timeout);
                    curVideoPts = frameBuffer.getPresentationTime();
//                AppLog.i(TAG, " dequeueInputBuffer inIndex=" + inIndex);
                    frameSize++;
                    if (isFirst) {
                        isFirst = false;
                        startTime = System.currentTimeMillis();
                        AppLog.i(TAG, "get first Frame");
                    }
                    if (inIndex >= 0) {
                        sampleSize = frameBuffer.getFrameSize();
                        pts = (long) (frameBuffer.getPresentationTime() * 1000 * 1000); // (seconds
                        ByteBuffer buffer = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            buffer = decoder.getInputBuffer(inIndex);
                        }else {
                            buffer = decoder.getInputBuffers()[inIndex];
                        }
//
                        buffer.clear();
                        buffer.rewind();
                        buffer.put(frameBuffer.getBuffer(), 0, sampleSize);
                        decoder.queueInputBuffer(inIndex, 0, sampleSize, pts, 0);
                    }
                    int outBufId = decoder.dequeueOutputBuffer(info, timeout);
                    if (outBufId >= 0) {
                        //AppLog.d( TAG, "do decoder and display....." );
                        decoder.releaseOutputBuffer(outBufId, true);
                        if (!audioPlayFlag) {
                            audioPlayFlag = true;
                            GlobalInfo.videoCacheNum = frameSize;
                            videoShowtime = System.currentTimeMillis();
                            AppLog.d(TAG, "ok show image!.....................startTime= " + (System.currentTimeMillis() - startTime) + " frameSize=" + frameSize
                                    + " curVideoPts=" + curVideoPts);
                        }

                        if (!isSaveBitmapToDb) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        previewStream.savePvThumbnail(previewStreamControl,frameWidth,frameHeight);
                                    }
                                }).start();
                                isSaveBitmapToDb = true;
                        }
                        if (previewLaunchMode == PreviewLaunchMode.VIDEO_PB_MODE && videoPbUpdateBarLitener != null) {
                            videoPbUpdateBarLitener.onFramePtsChanged(frameBuffer.getPresentationTime());
                        }
                    }else if (outBufId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){

                    }
                    if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE && onDecodeTimeListener != null && frameBuffer != null) {
                        if (System.currentTimeMillis() - lastTime > 500) {
                            lastTime = System.currentTimeMillis();
                            long decodeTime = frameBuffer.getDecodeTime();
                            onDecodeTimeListener.decodeTime(decodeTime);
                        }
                    }
                }catch (IllegalStateException e){
                    AppLog.e(TAG, "decoder IllegalStateException");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        decoder.reset();
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    setFormat();
                }

            }
            try {
                AppLog.i(TAG, "test11 start decoder stop");
                decoder.stop();
                decoder.release();
                AppLog.i(TAG, "test11 end decoder release");
            }catch (Exception e){
                e.printStackTrace();
                AppLog.i(TAG, "Exception:" + e.getClass().getSimpleName());
            }

            AppLog.i(TAG, "stopMPreview video thread");
        }

        public void requestExitAndWait() {
            // 把这个线程标记为完成，并合并到主程序线程
            AppLog.e(TAG, "H264Decoder requestExitAndWait isAlive=" + this.isAlive());
            done = true;
            if (this.isAlive()) {
                try {
                    join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            AppLog.e(TAG, "end  H264Decoder requestExitAndWait");
        }
    }

    private void setFormat() {
        /* create & config android.media.MediaFormat */
        ICatchVideoFormat videoFormat = this.videoFormat;
        AppLog.i(TAG, "create  MediaFormat videoFormat:" +videoFormat);
        AppLog.i(TAG, "create  MediaFormat videoFormat fps:" +videoFormat.getFps());
        int w = videoFormat.getVideoW();
        int h = videoFormat.getVideoH();
        String type = videoFormat.getMineType();
        MediaFormat format = MediaFormat.createVideoFormat(type, w, h);

        if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
//            format.setByteBuffer("csd-0", ByteBuffer.wrap(videoFormat.getCsd_0(), 0, videoFormat.getCsd_0_size()));
//            format.setByteBuffer("csd-1", ByteBuffer.wrap(videoFormat.getCsd_1(), 0, videoFormat.getCsd_0_size()));
            format.setInteger(MediaFormat.KEY_DURATION, videoFormat.getDurationUs());
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, videoFormat.getMaxInputSize());
            format.setInteger(MediaFormat.KEY_BIT_RATE, videoFormat.getBitrate());
            format.setInteger(MediaFormat.KEY_FRAME_RATE, videoFormat.getFps());
//            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        }

		/* create & config android.media.MediaCodec */
        String ret = videoFormat.getMineType();
        Log.i(TAG, "h264 videoFormat.getMineType()=" + ret);
        decoder = null;
        try {
            decoder = MediaCodec.createDecoderByType(ret);
            decoder.configure(format, surfaceHolder.getSurface(), null, 0);
//            Thread.sleep(200);
            decoder.start();
            AppLog.d(TAG, "end set format");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AppLog.d(TAG, "setFormat e=" + e.getMessage());
        }
    }

    private class AudioThread extends Thread {

        private boolean done = false;
        private LinkedList<ICatchFrameBuffer> audioQueue;

        private AudioTrack audioTrack;
        boolean isFirstShow = true;


        public void run() {
            AppLog.i(TAG, "start running AudioThread previewLaunchMode=" + previewLaunchMode);
            ICatchFrameBuffer temp = null;
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

            AppLog.i(TAG, "start running AudioThread audioFormat=" + audioFormat);
            int bufferSize = AudioTrack.getMinBufferSize(audioFormat.getFrequency(),
                    audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_LEFT,
                    audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioFormat.getFrequency(),
                    audioFormat.getNChannels() == 2 ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_LEFT,
                    audioFormat.getSampleBits() == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT, bufferSize,
                    AudioTrack.MODE_STREAM);

            AppLog.i(TAG, "start running AudioThread audioFormat.getFrequency()=" + audioFormat.getFrequency());
            AppLog.i(TAG, "start running AudioThread audioFormat.getNChannels()=" + audioFormat.getNChannels());
            AppLog.i(TAG, "start running AudioThread audioFormat.getSampleBits()=" + audioFormat.getSampleBits());

            audioTrack.play();
            audioQueue = new LinkedList<ICatchFrameBuffer>();
            boolean ret = false;

            ICatchFrameBuffer tempBuffer = new ICatchFrameBuffer(1024 * 50);
            byte[] testaudioBuffer = new byte[1024 * 50];
            tempBuffer.setBuffer(testaudioBuffer);
            while (!done) {
                ICatchFrameBuffer icatchBuffer = new ICatchFrameBuffer(1024 * 50);
                byte[] audioBuffer = new byte[1024 * 50];
                icatchBuffer.setBuffer(audioBuffer);
                ret = false;
                try {
                    if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                        ret = previewStreamControl.getNextAudioFrame(icatchBuffer);
                    } else {
                        ret = videoPbControl.getNextAudioFrame(icatchBuffer);
                    }
                } catch (IchSocketException e) {
                    AppLog.e(TAG, " getNextAudioFrame IchSocketException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchBufferTooSmallException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchBufferTooSmallException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchCameraModeException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchCameraModeException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchInvalidSessionException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchInvalidSessionException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchTryAgainException e) {
                    // TODO Auto-generated catch block
//                    AppLog.e(TAG, "getNextAudioFrame IchTryAgainException");
                    e.printStackTrace();
                } catch (IchStreamNotRunningException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchStreamNotRunningException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchInvalidArgumentException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchInvalidArgumentException");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                } catch (IchAudioStreamClosedException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchAudioStreamClosedException");
                    AppLog.e(TAG, "audioQueue size=" + audioQueue.size());
                    // TODO Auto-generated catch block
                    //播放完缓存的数据 ICOM-4250
                    if (audioQueue != null && audioQueue.size() > 0) {
                        while (audioQueue.size() > 0) {
                            temp = audioQueue.poll();
                            if (temp != null) {
                                audioTrack.write(temp.getBuffer(), 0, temp.getFrameSize());
                            }
                        }
                    }
                    e.printStackTrace();
                    return;

                } catch (IchPbStreamPausedException e) {
                    AppLog.e(TAG, "getNextAudioFrame IchPbStreamPausedException");
                    e.printStackTrace();
                    return;
                }
                if (false == ret) {
//                    AppLog.e(TAG, "failed to getNextAudioFrame!");
                    continue;
                } else {
                    audioQueue.offer(icatchBuffer);
//                    test.saveAduio(icatchBuffer,icatchBuffer.getFrameSize());
                }
                if (audioPlayFlag) {

                    temp = audioQueue.poll();
                    if (temp == null) {
                        continue;
                    }
                    double tempPts = curVideoPts;
                    double delayTime = 0;
                    if (isFirstShow) {
                        delayTime = (1 / GlobalInfo.curFps) * GlobalInfo.videoCacheNum;
                        AppLog.d(TAG, "delayTime=" + delayTime + " AppInfo.videoCacheNum=" + GlobalInfo.videoCacheNum + " AppInfo.curFps=" + GlobalInfo.curFps);
                        isFirstShow = false;
                    }
                    if (curVideoPts == -1) {
//                        AppLog.d(TAG,"tempPts == -1");
                    } else {
                        if (temp.getPresentationTime() - (tempPts - delayTime) > GlobalInfo.THRESHOLD_TIME) {
                            audioQueue.addFirst(temp);
                            AppLog.d(TAG, "audioQueue.addFirst(temp);");
                            continue;
                        }
                        if (temp.getPresentationTime() - (tempPts - delayTime) < -GlobalInfo.THRESHOLD_TIME && audioQueue.size() > 0) {
                            //JIRA BUG ICOM-3618 Begin modefy by b.jiang 20160825
                            while (temp.getPresentationTime() - (tempPts - delayTime) < 0 && audioQueue.size() > 0) {
                                temp = audioQueue.poll();
                                if (temp != null) {
                                    AppLog.d(TAG, "audioQueue.poll()----tempPts=" + tempPts + " curVideoPts=" + curVideoPts + " curPts=" + temp
                                            .getPresentationTime() + " audioQueue size=" + audioQueue.size());
                                }
                            }
                            //JIRA BUG ICOM-3618 end modefy by b.jiang 20160825
                        }
                    }
                    if (temp != null) {
                        audioTrack.write(temp.getBuffer(), 0, temp.getFrameSize());
                    }
                }
            }
            audioTrack.stop();
            audioTrack.release();
            AppLog.i(TAG, "stopMPreview audio thread");
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
}
