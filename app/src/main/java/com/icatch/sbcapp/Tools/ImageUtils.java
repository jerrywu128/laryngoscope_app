package com.icatch.sbcapp.Tools;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
public class ImageUtils {

    public static Bitmap image_ARGB8888_2_bitmap(DisplayMetrics metrics, Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int width = image.getWidth();
//        Log.d("WOW", "image w = " + width);
        int height = image.getHeight();
//        Log.d("WOW", "image h = " + height);

        int pixelStride = planes[0].getPixelStride();
//        Log.d("WOW", "pixelStride is " + pixelStride);
        int rowStride = planes[0].getRowStride();
//        Log.d("WOW", "row Stride is " + rowStride);
        int rowPadding = rowStride - pixelStride * width;
//        Log.d("WOW", "rowPadding is " + rowPadding);

        int offset = 0;
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(metrics, width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int pixel = 0;
                pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                pixel |= (buffer.get(offset + 2) & 0xff);       // B
                pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                bitmap.setPixel(j, i, pixel);
                offset += pixelStride;
            }
            offset += rowPadding;
        }
        return bitmap;
    }

    public static Bitmap image_2_bitmap(Image image, Bitmap.Config config) {

        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap bitmap;

        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride/*equals: rowStride/pixelStride */
                , height, config);
        bitmap.copyPixelsFromBuffer(buffer);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height);
//        return bitmap;
    }

    /**
     * PNG
     * @return
     */
    public static byte[] bitmap2byte(Bitmap bmp, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public static Bitmap byte2bitmap(byte[] data) {
        if (data.length != 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {
            return null;
        }
    }
}
