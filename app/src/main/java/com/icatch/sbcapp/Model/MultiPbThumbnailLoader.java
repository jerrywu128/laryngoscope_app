

package com.icatch.sbcapp.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.icatch.sbcapp.BaseItems.LoadImageType;
import com.icatch.sbcapp.Listener.UpdateImageViewListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.SdkApi.FileOperation;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by b.jiang on 2015/12/15.
 */
public class MultiPbThumbnailLoader {

    private static MultiPbThumbnailLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<Integer, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static final int DEAFULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.FIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    private boolean isDiskCacheEnable = true;

    private static final String TAG = "ImageLoader";

    private UpdateImageViewListener updateImageViewListener;
    private FileOperation fileOperation = FileOperation.getInstance();

    public enum Type {
        FIFO, LIFO;
    }

    private MultiPbThumbnailLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        initBackThread();

        // 获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        AppLog.d("cacheMemory", " cacheMemory=" + cacheMemory);
        mLruCache = new LruCache<Integer, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
//                return value.getRowBytes() * value.getHeight();
                AppLog.d("cacheMemory", " value.getByteCount()=" + value.getByteCount());
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                }
            }


        };

        // 创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 初始化后台轮询线程
     */
    private void initBackThread() {

        // 后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                // 其它线程中新建一个handler
                // 创建该线程的Looper对象，用于接收消息,在非主线程中是没有looper的所以在创建handler前一定要使用prepare()创建一个Looper
                Looper.prepare();
                AppLog.d("1111", "mPoolThreadHandler Looper.prepare()");
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 线程池去取出一个任务进行执行
                        AppLog.d("1111", "mPoolThreadHandler acquire()");
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                        }
                        AppLog.d("1111", "mPoolThreadHandler getTask()");
                        Runnable runnable = getTask();
                        if (runnable != null) {
                            mThreadPool.execute(runnable);

                        }

                    }
                };
                // 释放一个信号量
                mSemaphorePoolThreadHandler.release();
                AppLog.d("1111", "mPoolThreadHandler Looper.loop()");
                Looper.loop();//建立一个消息循环，该线程不会退出
            }

            ;
        };

        mPoolThread.start();
    }

    public static MultiPbThumbnailLoader getInstance() {
        if (mInstance == null) {
            synchronized (MultiPbThumbnailLoader.class) {
                if (mInstance == null) {
                    mInstance = new MultiPbThumbnailLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static MultiPbThumbnailLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (MultiPbThumbnailLoader.class) {
                if (mInstance == null) {
                    mInstance = new MultiPbThumbnailLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    public void loadImage(final int fileHandle, final LoadImageType loadImageType, final UpdateImageViewListener updateImageViewListener) {
        this.updateImageViewListener = updateImageViewListener;
//        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                public void handleMessage(Message msg) {
                    // 获取得到图片，为imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    int fileHandle = holder.fileHandle;
                    // 将path与getTag存储路径进行比较
                    //updateImageViewListener.onBitmapLoadComplete(fileHandle, bm);
                }

                ;
            };
        }

        // 根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(fileHandle);

        if (bm != null) {
            refreashBitmap(fileHandle, null, bm);
        } else {
            addTask(buildTask(fileHandle, null, loadImageType));
        }

    }

    public void clearTasksQueue() {
        AppLog.d("1111", "mTaskQueue.size() = " + mTaskQueue.size());
        if (mTaskQueue != null && mTaskQueue.size() > 0) {
            mTaskQueue.clear();
        }
    }

    /**
     * 根据传入的参数，新建一个任务
     *
     * @param path
     * @param imageView
     * @param loadImageType
     * @return
     */
    private Runnable buildTask(final int fileHandle, final ImageView imageView,
                               final LoadImageType loadImageType) {
        return new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                bm = getBitmap(fileHandle, loadImageType);
                addBitmapToLruCache(fileHandle, bm);
                refreashBitmap(fileHandle, imageView, bm);
                mSemaphoreThreadPool.release();
                AppLog.d("1111", "mPoolThreadHandler release()");
            }


        };
    }

    private Bitmap getBitmap(final int fileHandle, LoadImageType loadImageType) {

        Bitmap newBitmap = null;
//        if (loadImageType == LoadImageType.LOCAL_PHOTO) {
//            newBitmap = BitmapTools.getImageThumbnail(path);
//        } else if (loadImageType == LoadImageType.LOCAL_VIDEO) {
//            newBitmap = ThumbnailOperation.getlocalVideoWallThumbnail(path);
//        } else if (loadImageType == LoadImageType.CAMERA_VIDEO) {
//            //
//        }
        return newBitmap;
    }

    /**
     * 从任务队列取出一个方法
     *
     * @return
     */
    private Runnable getTask() {
        if (mTaskQueue.size() <= 0) {
            return null;
        }
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    /**
     * 方式二
     *
     * @param bytes
     * @return
     */
    public String bytes2hex02(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
            {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }

        return sb.toString();

    }

    private void refreashBitmap(final int fileHandle, final ImageView imageView,
                                Bitmap bm) {
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.fileHandle = fileHandle;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);

    }

    /**
     * 将图片加入LruCache
     *
     * @param fileHandle
     * @param bm
     */
    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d("test", "addBitmapToLruCache fileHandle=" + fileHandle);
                AppLog.d("test", "addBitmapToLruCache bitmap=" + bm);
                mLruCache.put(fileHandle, bm);
            }

        }
    }

    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
//         if(mPoolThreadHandler==null)wait();
        try {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e) {
        }
        AppLog.d("1111", " addTask");
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 获得缓存图片的地址
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 根据path在缓存中获取bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromLruCache(int key, int position) {
        return mLruCache.get(key);
    }

    public Bitmap getBitmapFromLruCache(int key) {
        return mLruCache.get(key);
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        int fileHandle;
    }


}
