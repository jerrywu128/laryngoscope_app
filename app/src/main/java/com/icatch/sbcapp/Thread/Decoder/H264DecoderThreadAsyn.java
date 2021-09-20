package com.icatch.sbcapp.Thread.Decoder;

import android.annotation.TargetApi;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnDecodeTimeListener;
import com.icatch.sbcapp.Listener.VideoFramePtsChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.PreviewLaunchMode;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.SdkApi.PreviewStream;
import com.icatch.sbcapp.SdkApi.VideoPlayback;
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
import com.icatch.wificam.customer.exception.IchVideoStreamClosedException;
import com.icatch.wificam.customer.type.ICatchAudioFormat;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;
import com.icatch.wificam.customer.type.ICatchVideoFormat;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Created by b.jiang on 2018/12/25.
 */
public class H264DecoderThreadAsyn {
    private static final String TAG = "H264DecoderThreadAsyn";
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

    public H264DecoderThreadAsyn(MyCamera mCamera, SurfaceHolder holder, int previewLaunchMode, ICatchVideoFormat iCatchVideoFormat, VideoFramePtsChangedListener videoPbUpdateBarLitener) {
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
        timeout = 10000;
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
        stopDecder();
        audioPlayFlag = false;
    }

    double curVideoPts = 0;

    private class VideoThread extends Thread {
        private boolean done = false;
        private MediaCodec.BufferInfo info;

        VideoThread() {
            super();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            AppLog.i(TAG, "h264 run for gettting surface image");
            initDecoder();
            info = new MediaCodec.BufferInfo();
            byte[] mPixel = new byte[frameWidth * frameHeight * 4];
            final ICatchFrameBuffer frameBuffer = new ICatchFrameBuffer(frameWidth * frameHeight * 4);
            frameBuffer.setBuffer(mPixel);
            MediaFormat mOutputFormat;
            decoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    boolean retvalue = false;
                    try {
                        if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
                            retvalue = previewStreamControl.getNextVideoFrame(frameBuffer);
                        } else {
                            retvalue = videoPbControl.getNextVideoFrame(frameBuffer);
                        }
                        if (!retvalue) {
                            return;
                        }
                        ByteBuffer inputBuffer = codec.getInputBuffer(index);
                        inputBuffer.clear();
                        inputBuffer.rewind();
                        int sampleSize = frameBuffer.getFrameSize();
                        long pts = (long) (frameBuffer.getPresentationTime() * 1000 * 1000);
                        inputBuffer.put(frameBuffer.getBuffer(), 0, sampleSize);
                        decoder.queueInputBuffer(index, 0, sampleSize, pts, 0);
                    } catch (IchInvalidSessionException e){
                        e.printStackTrace();
                        stopDecder();
                    }catch (IchStreamNotRunningException e){
                        e.printStackTrace();
                        stopDecder();
                    }catch (IchVideoStreamClosedException e) {
                        e.printStackTrace();
                        stopDecder();
                    }catch (Exception e) {
                        e.printStackTrace();
                        AppLog.e(TAG,"Exception e:" + e.getMessage());
                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (!audioPlayFlag) {
                        audioPlayFlag = true;
                    }
                    try {
                        decoder.releaseOutputBuffer(index, true);
                    } catch (IllegalStateException e) {

                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                }
            });
            mOutputFormat = decoder.getOutputFormat(); // option B
            try {
                decoder.start();
            } catch (Exception e) {
            }
            AppLog.i(TAG, "stopMPreview video thread");
        }
    }


    private void stopDecder() {
        if (decoder == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppLog.i(TAG, "test11 start decoder stop");
                    decoder.stop();
                    decoder.release();
                    AppLog.i(TAG, "test11 end decoder release");
                    decoder = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    AppLog.i(TAG, "Exception:" + e.getClass().getSimpleName());
                }
            }
        }).start();
    }

    private void initDecoder() {
        /* create & config android.media.MediaFormat */
        ICatchVideoFormat videoFormat = this.videoFormat;
        AppLog.i(TAG, "create  MediaFormat videoFormat:" + videoFormat);
        AppLog.i(TAG, "create  MediaFormat videoFormat fps:" + (videoFormat == null? 0 :videoFormat.getFps()));
        int w = videoFormat.getVideoW();
        int h = videoFormat.getVideoH();
        String type = videoFormat.getMineType();
        MediaFormat format = MediaFormat.createVideoFormat(type, w, h);

        if (previewLaunchMode == PreviewLaunchMode.RT_PREVIEW_MODE) {
            format.setInteger(MediaFormat.KEY_DURATION, videoFormat.getDurationUs());
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, videoFormat.getMaxInputSize());
            format.setInteger(MediaFormat.KEY_BIT_RATE, videoFormat.getBitrate());
            format.setInteger(MediaFormat.KEY_FRAME_RATE, videoFormat.getFps());
        }
        /* create & config android.media.MediaCodec */
        String ret = videoFormat.getMineType();
        Log.i(TAG, "h264 videoFormat.getMineType()=" + ret);
        decoder = null;
        try {
            decoder = MediaCodec.createDecoderByType(ret);
            decoder.configure(format, surfaceHolder.getSurface(), null, 0);
            AppLog.d(TAG, "end set format");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AppLog.d(TAG, "initDecoder e=" + e.getMessage());
        }
    }

    private class AudioThread extends Thread {
        private boolean done = false;
        private LinkedList<ICatchFrameBuffer> audioQueue;
        private AudioTrack audioTrack;
        boolean isFirstShow = true;

        @Override
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
