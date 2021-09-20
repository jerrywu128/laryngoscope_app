package com.icatch.sbcapp.Tools;

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
import com.icatch.sbcapp.ThumbnailGetting.ThumbnailOperation;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by b.jiang on 2015/12/15.
 */
public class ImageLoader {

    private static ImageLoader mInstance;

    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
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

    public enum Type
    {
        FIFO, LIFO;
    }

    private ImageLoader(int threadCount, Type type)
    {
        init(threadCount, type);
    }

    /**
     * 初始化
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type)
    {
        initBackThread();

        // 获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        AppLog.d("cacheMemory", " cacheMemory=" + cacheMemory);
        mLruCache = new LruCache<String, Bitmap>(cacheMemory)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
//                return value.getRowBytes() * value.getHeight();
                AppLog.d("cacheMemory", " value.getByteCount()=" + value.getByteCount());
                return value.getByteCount();
            }
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if(oldValue != null){
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
    private void initBackThread()
    {

        // 后台轮询线程
        mPoolThread = new Thread()
        {
            @Override
            public void run()
            {
                // 其它线程中新建一个handler
                // 创建该线程的Looper对象，用于接收消息,在非主线程中是没有looper的所以在创建handler前一定要使用prepare()创建一个Looper
                Looper.prepare();
                AppLog.d(TAG, "mPoolThreadHandler Looper.prepare()");
                mPoolThreadHandler = new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        // 线程池去取出一个任务进行执行
                        AppLog.d(TAG,"mPoolThreadHandler acquire()");
                        try
                        {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e)
                        {
                        }
                        AppLog.d(TAG,"mPoolThreadHandler getTask()");
                        Runnable runnable = getTask();
                        if (runnable != null){
                            mThreadPool.execute(runnable);

                        }

                    }
                };
                // 释放一个信号量
                mSemaphorePoolThreadHandler.release();
                AppLog.d(TAG, "mPoolThreadHandler Looper.loop()");
                Looper.loop();//建立一个消息循环，该线程不会退出
            };
        };
        mPoolThread.start();
    }


    public static ImageLoader getInstance()
    {
        if (mInstance == null)
        {
            synchronized (ImageLoader.class)
            {
                if (mInstance == null)
                {
                    mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount, Type type)
    {
        if (mInstance == null)
        {
            synchronized (ImageLoader.class)
            {
                if (mInstance == null)
                {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }


    public void loadImage(final String path, final ImageView imageView,
                          final LoadImageType loadImageType)
    {
//        imageView.setTag(path);
        if (mUIHandler == null)
        {
            mUIHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    // 获取得到图片，为imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    String path = holder.path;
                    AppLog.i(TAG,"handleMessage bm=" +bm + " path=" + path);
                    // 将path与getTag存储路径进行比较
                    if (imageview.getTag().toString().equals(path) )
                    {
                        AppLog.i(TAG,"handleMessage setImageBitmap");
                        imageview.setImageBitmap(bm);
                    }
                };
            };
        }

        // 根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        AppLog.i(TAG,"loadImage bm=" +bm);

        if (bm != null)
        {
            refreashBitmap(path, imageView, bm);
        } else
        {
            addTask(buildTask(path, imageView, loadImageType));
        }

    }

    public void loadImage(final String path,final LoadImageType loadImageType,final UpdateImageViewListener updateImageViewListener)
    {
        this.updateImageViewListener = updateImageViewListener;
//        imageView.setTag(path);
        if (mUIHandler == null)
        {
            mUIHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    // 获取得到图片，为imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageview = holder.imageView;
                    String path = holder.path;
                    // 将path与getTag存储路径进行比较
                    updateImageViewListener.onBitmapLoadComplete(path, bm);
                };
            };
        }

        // 根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(path);

        if (bm != null)
        {
            refreashBitmap(path, null, bm);
        } else
        {
            addTask(buildTask(path, null, loadImageType));
        }

    }

    public void clearTasksQueue(){
        AppLog.d(TAG,"mTaskQueue.size() = " + mTaskQueue.size());
        if (mTaskQueue != null && mTaskQueue.size() > 0){
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
    private Runnable buildTask(final String path, final ImageView imageView,
                               final LoadImageType loadImageType)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bm = null;
                bm = getBitmap(path, loadImageType);
                addBitmapToLruCache(path, bm);
                refreashBitmap(path, imageView, bm);
                mSemaphoreThreadPool.release();
                AppLog.d(TAG, "mPoolThreadHandler release()");
            }


        };
    }

    private Bitmap getBitmap(final String path, LoadImageType loadImageType)
    {

        Bitmap newBitmap =null;
        if(loadImageType == LoadImageType.LOCAL_PHOTO){
            newBitmap = BitmapTools.getImageByPath(path, BitmapTools.THUMBNAIL_WIDTH, BitmapTools.THUMBNAIL_HEIGHT);
        }else if(loadImageType == LoadImageType.LOCAL_VIDEO){
            newBitmap = ThumbnailOperation.getlocalVideoWallThumbnail(path);
        }else if(loadImageType == LoadImageType.CAMERA_VIDEO){
            //
        }
        AppLog.i(TAG,"getBitmap path=" +path);
        return newBitmap;
    }

    /**
     * 从任务队列取出一个方法
     *
     * @return
     */
    private Runnable getTask()
    {
        if (mTaskQueue.size() <= 0){
            return null;
        }
        if (mType == Type.FIFO)
        {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO)
        {
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
    public String bytes2hex02(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes)
        {
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

    private void refreashBitmap(final String path, final ImageView imageView,
                                Bitmap bm)
    {
        AppLog.i(TAG,"refreashBitmap path=" +path);
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageView;
        message.obj = holder;
        mUIHandler.sendMessage(message);

    }

    /**
     * 将图片加入LruCache
     *
     * @param path
     * @param bm
     */
    protected void addBitmapToLruCache(String path, Bitmap bm)
    {
        if (getBitmapFromLruCache(path) == null)
        {
            if (bm != null && path != null){
                AppLog.d("test","addBitmapToLruCache path=" + path);
                AppLog.d("test","addBitmapToLruCache bitmap=" + bm);
                mLruCache.put(path, bm);
            }

        }
    }

    private synchronized void addTask(Runnable runnable)
    {
        mTaskQueue.add(runnable);

//         if(mPoolThreadHandler==null)wait();
        try
        {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e)
        {
        }
        AppLog.d(TAG, " addTask mTaskQueue size=" + mTaskQueue.size() + " mPoolThreadHandler=" + mPoolThreadHandler);
        mPoolThreadHandler.sendEmptyMessage(0x1122);
        Message message = Message.obtain();
//        mPoolThreadHandler.sendMessage(message);
    }

    /**
     * 获得缓存图片的地址
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName)
    {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()))
        {
            cachePath = context.getExternalCacheDir().getPath();
        } else
        {
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
    public Bitmap getBitmapFromLruCache(String key,int position)
    {
        return mLruCache.get(key);
    }

    public Bitmap getBitmapFromLruCache(String key)
    {
        return mLruCache.get(key);
    }

    private class ImgBeanHolder
    {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    public void clearCache(){
        mLruCache.evictAll();
        System.gc();
    }



}
