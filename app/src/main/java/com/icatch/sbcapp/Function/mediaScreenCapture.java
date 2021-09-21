package com.icatch.sbcapp.Function;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.icatch.sbcapp.Tools.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class mediaScreenCapture  {
    private static final String TAG = "mediaScreenCapture";
    private static MediaProjection mMediaProjection;
    boolean isScreenCaptureStarted;
    OnImageCaptureScreenListener listener;
    private int mDensity;
    private Display mDisplay;
    private int mWidth;
    private int mHeight;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private Handler mHandler;
    private String STORE_DIR;
    private Context mContext;
    private MediaProjectionManager mMediaProjectionManager;

    public mediaScreenCapture(Context context, MediaProjection mediaProjection, String savePath) {
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


        if (TextUtils.isEmpty(savePath)) {
            File externalFilesDir = mContext.getExternalFilesDir("/");
            Log.d(TAG, "externalFilesDir:" + externalFilesDir.getAbsolutePath());
            if (externalFilesDir != null) {
                STORE_DIR = externalFilesDir.getAbsolutePath() + "/testapp";
            } else {
                Toast.makeText(mContext, "No save path assigned!", Toast.LENGTH_SHORT);
            }
        } else {
            STORE_DIR = savePath;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public mediaScreenCapture startProjection() {
        if (mMediaProjection != null) {
            File storeDir = new File(STORE_DIR);
            if (!storeDir.exists()) {
                boolean success = storeDir.mkdirs();
                if (!success) {
                    Log.d(TAG, "mkdir " + storeDir + "  failed");
                    return this;
                } else {
                    Log.d(TAG, "mkdir " + storeDir + "  success");
                }
            } else {
                Log.d(TAG, " " + storeDir + "  exist");
            }

        } else {
            Log.d(TAG, "get mediaprojection failed");
        }

        try {
            Thread.sleep(500);
            isScreenCaptureStarted = true;
        } catch (InterruptedException e) {

        }

        WindowManager window = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = window.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        // use getMetrics is 2030, use getRealMetrics is 2160, the diff is NavigationBar's height
        mDisplay.getRealMetrics(metrics);
        mDensity = metrics.densityDpi;
        Log.d(TAG, "metrics.widthPixels is " + metrics.widthPixels);
        Log.d(TAG, "metrics.heightPixels is " + metrics.heightPixels);
        mWidth = metrics.widthPixels;//size.x;
        mHeight = metrics.heightPixels;//size.y;

        //start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight,0x1, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                "ScreenShot",
                mWidth,
                mHeight,
                mDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader.getSurface(),
                null,
                mHandler);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {

                if (isScreenCaptureStarted) {

                    Image image = null;
                    FileOutputStream fos = null;
                    Bitmap mBitmap = null;

                    try {
                        image = reader.acquireLatestImage();
                        if (image != null) {

                            int width = image.getWidth();
                            int height = image.getHeight();
                            final Image.Plane[] planes = image.getPlanes();
                            final ByteBuffer buffer = planes[0].getBuffer();
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * width;
                            mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                            mBitmap.copyPixelsFromBuffer(buffer);
                            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);

                            Date currentDate = new Date();
                            SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmmss");
                            String fileName = STORE_DIR  + date.format(
                                    currentDate) + ".jpg";
                            fos = new FileOutputStream(fileName);
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            Log.d(TAG, "End now!!!!!!  Screenshot saved in " + fileName);
                            Toast.makeText(mContext, "Screenshot saved in " + fileName,
                                    Toast.LENGTH_LONG);
                            stopProjection();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (null != fos) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (null != mBitmap) {
                            mBitmap.recycle();
                        }
                        if (null != image) {
                            image.close(); // close it when used and
                        }
                    }
                }
            }
        }, mHandler);
        mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
        return this;
    }

    public mediaScreenCapture stopProjection() {
        isScreenCaptureStarted = false;
        Log.d(TAG, "Screen captured");
        mHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (mMediaProjection != null) {
                    mMediaProjection.stop();
                }
            }
        });
        return this;
    }

    public mediaScreenCapture setListener(OnImageCaptureScreenListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnImageCaptureScreenListener {
        public void imageCaptured(byte[] image);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) {
                        mVirtualDisplay.release();
                    }
                    if (mImageReader != null) {
                        mImageReader.setOnImageAvailableListener(null, null);
                    }
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }



}