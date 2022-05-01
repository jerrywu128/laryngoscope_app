package com.honestmc.laryngoscopeapp.Adapter;

import android.content.Context;
//import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.honestmc.laryngoscopeapp.BaseItems.LocalPbItemInfo;
import com.honestmc.laryngoscopeapp.ExtendComponent.ProgressWheel;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.Tools.GlideUtils;

import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by b.jiang on 2016/9/21.
 */
public class LocalPhotoPbViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PhotoPbViewPagerAdapter";
    private List<LocalPbItemInfo> filesList;
    private Context context;
//    LruCache<String, Bitmap> mLruCache;
    private List<View> viewList;
    private OnPhotoTapListener onPhotoTapListener;
    LinkedList<View> mCaches = new LinkedList<View>();

    public LocalPhotoPbViewPagerAdapter(Context context, List<LocalPbItemInfo> filesList, List<View> viewList) {
        this.filesList = filesList;
        this.context = context;
        this.viewList = viewList;
//        this.mLruCache = mLruCache;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RelativeLayout view = (RelativeLayout) object;
        if(view == null){
            return;
        }
        PhotoView imageView = (PhotoView) view.findViewById(R.id.photo);
        if (imageView == null)
            return;
        GlideUtils.clear(imageView);
        if (position < filesList.size()) {
            container.removeView((View)object);
        }
        //ICOM-4179 Begin add by b.jiang 20170221
        mCaches.addLast((View)object);
        //ICOM-4179 End add by b.jiang 20170221
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        View v = View.inflate(context, R.layout.pb_photo_item, null);
        View v = null;
        //ICOM-4179 Begin add by b.jiang 20170221
        if(mCaches.size() < 3){
            AppLog.d(TAG, "instantiateItem View.inflate");
            v = View.inflate(context, R.layout.pb_photo_item, null);
        }else {
            AppLog.d(TAG, "instantiateItem removeFirst mCaches.size=" + mCaches.size());
            v= mCaches.removeFirst();
        }
        //ICOM-4179 End add by b.jiang 20170221
//        v = View.inflate(context, R.layout.pb_photo_item, null);
        String filePath = filesList.get(position).file.getPath();
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photo);
        ProgressWheel progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        if(photoView != null){
            GlideUtils.loadImageViewSkipCacheSize(context,filePath,720,400,photoView);
//            if(bitmap != null){
//                photoView.setImageBitmap(bitmap);
//            }
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    if(onPhotoTapListener != null){
                        onPhotoTapListener.onPhotoTap();
                    }
                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
        }
        //ICOM-4179 Begin add by b.jiang 20170221
//        photoView.setTag(filePath);
        //ICOM-4179 End add by b.jiang 20170221
        viewList.set(position,v);
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

    public interface OnPhotoTapListener{
        void onPhotoTap();
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener){
        this.onPhotoTapListener = onPhotoTapListener;
    }
}
