package com.icatch.sbcapp.Function;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Tools.FileDES;
import com.icatch.sbcapp.Tools.FileOpertion.FileOper;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Activity.LaunchActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class mediaScreenRecord {
    private static final String TAG = "mediaScreenRecord";
    private static MediaProjection mMediaProjection;
    boolean isScreenCaptureStarted;
    mediaScreenCapture.OnImageCaptureScreenListener listener;
    private int mDensity;
    private Display mDisplay;
    private int mWidth;
    private int mHeight;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private Handler mHandler;
    private String path;
    private Context mContext;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMediaMuxer;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private boolean muxerStarted = false;
    private int videoTrackIndex = -1;
    private Surface surface;
    private VirtualDisplay virtualDisplay;
    private int dpi = 1;
    private String fileName,fileNameTemp;

    public mediaScreenRecord (Context context, MediaProjection mediaProjection) {
        mMediaProjection = mediaProjection;
        mContext = context;

        isScreenCaptureStarted = false;

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();


        path = StorageUtil.getDownloadPath(context);

        FileOper.createDirectory(path);
        FileOper.createDirectory(path+"/ENVIDEO");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public mediaScreenRecord startProjection() {

        Date currentDate = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
        fileNameTemp = path +"ENVIDEO/"+ date.format(
                currentDate);
        fileName = fileNameTemp + ".mp4";
        GlobalInfo.getInstance().setVideoName(fileName);
        WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = window.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        // get phone size

        mDisplay.getRealMetrics(metrics);
        mDensity = metrics.densityDpi;
        Log.d(TAG, "metrics.widthPixels is " + metrics.widthPixels);
        Log.d(TAG, "metrics.heightPixels is " + metrics.heightPixels);
        mWidth = metrics.widthPixels;//size.x;
        mHeight = metrics.heightPixels;//size.y;

        new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        prepareEncoder();
                        mMediaMuxer = new MediaMuxer(fileName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    virtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                            mWidth, mHeight, dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                            surface, null, null);

                    recordVirtualDisplay();

                } finally {
                    release();
                }
            }
        }.start();
        return this;
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 6000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);


        mMediaCodec = MediaCodec.createEncoderByType("video/avc");

        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        surface = mMediaCodec.createInputSurface();

        mMediaCodec.start();


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void recordVirtualDisplay() {


        while (!mQuit.get()) {
            int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();
            } else if (index >= 0) {
                encodeToVideoTrack(index);
                mMediaCodec.releaseOutputBuffer(index, false);
            }
        }
        MediaScannerConnection
                .scanFile(mContext, new String[] { fileName }, null, null); //更新相冊
    }

    private void resetOutputFormat() {
        MediaFormat newFormat = mMediaCodec.getOutputFormat();
        videoTrackIndex = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
        muxerStarted = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mMediaCodec.getOutputBuffer(index);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            bufferInfo.size = 0;
        }
        if (bufferInfo.size == 0) {
            encodedData = null;
        }
        if (encodedData != null) {

            encodedData.position(bufferInfo.offset);
            encodedData.limit(bufferInfo.offset + bufferInfo.size);
            mMediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopRecorder() {
        mQuit.set(true);

        mHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (mMediaProjection != null) {
                    mMediaProjection.stop();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void release() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }
}
