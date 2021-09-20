package com.icatch.sbcapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
//import android.support.v4.view.PagerAdapter;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.ExtendComponent.ProgressWheel;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.R;

import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by b.jiang on 2016/4/12.
 */
public class PhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<MultiPbItemInfo> filesList;
    private Context context;
    LruCache<Integer, Bitmap> mLruCache;
    private List<View> viewList;
    private OnPhotoTapListener onPhotoTapListener;
    LinkedList<View> mCaches = new LinkedList<View>();

    public PhotoPbViewPagerAdapter(Context context, List<MultiPbItemInfo> filesList, List<View> viewList, LruCache<Integer, Bitmap> mLruCache) {
        this.filesList = filesList;
        this.context = context;
        this.viewList = viewList;
        this.mLruCache = mLruCache;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        if (position < filesList.size()) {
//            container.removeView((View)object);
//        }
        //ICOM-4179 Begin add by b.jiang 20170221
        View view = (View) object;
        //移除页面
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
        photoView.setImageBitmap(null);
        container.removeView(view);
        mCaches.addLast((View) object);
        //ICOM-4179 End add by b.jiang 20170221
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = null;
        //ICOM-4179 Begin add by b.jiang 20170221
        if (mCaches.size() < 3) {
            v = View.inflate(context, R.layout.pb_photo_item, null);
        } else {
            v = mCaches.removeFirst();
        }
        //ICOM-4179 End add by b.jiang 20170221
        int fileHandle = filesList.get(position).getFileHandle();
        Bitmap bitmap = mLruCache.get(fileHandle);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
        ProgressWheel progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        if (photoView != null) {
            photoView.setImageBitmap(bitmap);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    if (onPhotoTapListener != null) {
                        onPhotoTapListener.onPhotoTap();
                    }
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
        }
        //ICOM-4179 Begin add by b.jiang 20170221
        photoView.setTag(fileHandle);
        //ICOM-4179 End add by b.jiang 20170221
        viewList.set(position, v);
        container.addView(v, 0);
        return v;
    }

    @Override
    public int getCount() {
        return filesList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public interface OnPhotoTapListener {
        void onPhotoTap();
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }

    public void releaseImageViewResouce(PhotoView photoView) {
        if (photoView == null)
            return;
        Drawable drawable = photoView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            AppLog.d(TAG, "releaseImageViewResouce 1");
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            AppLog.d(TAG, "releaseImageViewResouce bitmap=" + bitmap);
            AppLog.d(TAG, "releaseImageViewResouce bitmap.isRecycled()=" + bitmap.isRecycled());
            if (bitmap != null && !bitmap.isRecycled()) {
                AppLog.d(TAG, "releaseImageViewResouce recycle");
                bitmap.recycle();
                bitmap = null;
            }
        }
        System.gc();
    }

}