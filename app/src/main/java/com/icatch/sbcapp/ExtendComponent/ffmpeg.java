package com.icatch.sbcapp.ExtendComponent;

public class ffmpeg {
    static {
        System.loadLibrary("avcodec");
        System.loadLibrary("avdevice");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
    }
    public static native void run();
}
