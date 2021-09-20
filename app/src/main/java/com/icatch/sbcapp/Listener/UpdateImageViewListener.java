package com.icatch.sbcapp.Listener;

import android.graphics.Bitmap;

/**
 * Created by b.jiang on 2016/1/6.
 */
public interface UpdateImageViewListener {
    void onBitmapLoadComplete(String tag, Bitmap bitmap);
}
