package com.icatch.sbcapp.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;

import com.icatch.sbcapp.Log.AppLog;

import java.io.ByteArrayOutputStream;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 19:54.
 */
public class BitmapTools {
    private static String TAG = BitmapTools.class.getSimpleName();
    public final static int THUMBNAIL_WIDTH = 180;
    public final static int THUMBNAIL_HEIGHT = 180;
    private final static int ORIGINAL_WIDTH = 1080;
    private final static int ORIGINAL_HEIGHT = 720;
    private final static long LIMITED_IMGAE_SIZE = 1024 * 1024 * 10;//byte

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageByPath(String imagePath, int width, int height) {
        AppLog.d(TAG, "Start getImageByPath imagePath=" + imagePath);
        if (imagePath == null) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        options.inSampleSize = calculateInSampleSize(options, width, height);
        ;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        AppLog.d(TAG, "End getImageByPath bitMap=" + bitmap);
//        AppLog.d( TAG, "End getImageByPath bitMap.getByteCount()=" + bitmap.getByteCount() );
        return zoomBitmap(bitmap, width, height);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
        while ((height / inSampleSize > reqHeight) || (width / inSampleSize > reqWidth)) {
            inSampleSize *= 2;
        }
        AppLog.d(TAG, "height * width /(inSampleSize * inSampleSize) *4 =" + height * width / (inSampleSize * inSampleSize) * 4);
        while (height * width / (inSampleSize * inSampleSize) * 4 > LIMITED_IMGAE_SIZE) {//out of memory.
            inSampleSize *= 2;
        }
        AppLog.d(TAG, "calculateInSampleSize return inSampleSize=" + inSampleSize);
        return inSampleSize;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height) {
        AppLog.i(TAG, "start getVideoThumbnail videoPath=" + videoPath);
        if (videoPath == null) {
            return null;
        }
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        //extractThumbnail 方法二次处理,以指定的大小提取居中的图片,获取最终我们想要的图片
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        AppLog.i(TAG, "End getVideoThumbnail bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, float width, float heigth) {
        if (bitmap == null) {
            return bitmap;
        }
        //ICOM-4179 Begin modify by b.jiang 20170221
        Bitmap tempBitmap = null;
        float zoomRate = 1.0f;

        if (bitmap.getWidth() >= width || bitmap.getHeight() >= heigth) {
        } else if (width * bitmap.getHeight() > heigth * bitmap.getWidth()) {
            zoomRate = heigth / bitmap.getHeight();
        } else if (width * bitmap.getHeight() <= heigth * bitmap.getWidth()) {
            zoomRate = width / bitmap.getWidth();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(zoomRate, zoomRate); // 长和宽放大缩小的比例
        try {
            tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            AppLog.e(TAG, "zoomBitmap OutOfMemoryError");
            tempBitmap.recycle();
        }
        bitmap.recycle();
        System.gc();
        //ICOM-4179 End modify by b.jiang 20170221
        return tempBitmap;
    }

    public static Bitmap decodeByteArrayForRGB565(byte[] data, int reqWidth, int reqHeight) {
        AppLog.d(TAG, "start decodeByteArrayForRGB565");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        AppLog.d(TAG, "end decodeByteArrayForRGB565 bitmap=" + bitmap);
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int reqWidth, int reqHeight) {
        AppLog.d(TAG, "start decodeByteArray");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        //并且制定缩放比例
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        AppLog.d(TAG, "end decodeByteArray");
        //ICOM-4179
//        return bitmap;
        return zoomBitmap(bitmap, reqWidth, reqHeight);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        AppLog.d(TAG, "bitmapToByteArray bitmap size=" + bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, output);//把bitmap100%高质量压缩 到 output对象里
        byte[] result = output.toByteArray();//转换成功了
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            AppLog.d(TAG, "bitmapToByteArray result size=" + result.length);
        }

        return result;
    }
}
