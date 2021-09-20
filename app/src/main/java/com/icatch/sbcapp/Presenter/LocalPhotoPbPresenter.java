package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
//import android.support.v4.view.ViewPager;
import android.util.LruCache;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.icatch.sbcapp.Adapter.LocalPhotoPbViewPagerAdapter;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.BaseItems.LocalPbItemInfo;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.ExtendComponent.ProgressWheel;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.Tools.BitmapTools;
import com.icatch.sbcapp.Tools.MediaRefresh;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Interface.LocalPhotoPbView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by b.jiang on 2016/9/21.
 */
public class LocalPhotoPbPresenter extends BasePresenter {
    private String TAG = "LocalPhotoPbPresenter";
    private LocalPhotoPbView photoPbView;
    private Activity activity;
    private LocalPhotoPbViewPagerAdapter viewPagerAdapter;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;

    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;

    private List<View> viewList;
    private ExecutorService executor;
    private Handler handler;
    public List<LocalPbItemInfo> localPhotoList = GlobalInfo.getInstance().localPhotoList;
    int slideDirection = DIRECTION_RIGHT;

//    long lastTime = 0;

    public LocalPhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        viewList = new LinkedList<View>();
        handler = new Handler();
    }

    public void setView(LocalPhotoPbView localPhotoPbView){
        this.photoPbView = localPhotoPbView;
        initCfg();
    }

    public void loadImage() {
        String filePath = StorageUtil.getDownloadPath(activity);
        localPhotoList = GlobalInfo.getInstance().localPhotoList;
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
        for (int ii = 0; ii < localPhotoList.size(); ii++) {
            viewList.add(ii, null);
        }
        viewPagerAdapter = new LocalPhotoPbViewPagerAdapter(activity, localPhotoList, viewList);
        viewPagerAdapter.setOnPhotoTapListener(new LocalPhotoPbViewPagerAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap() {
                showBar();
            }
        });
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
        photoPbView.setOnPageChangeListener(new MyViewPagerOnPagerChangeListener());
    }

    public void showBar() {
        boolean isShowBar = photoPbView.getTopBarVisibility() == View.VISIBLE ? true : false;
        AppLog.d(TAG, "showBar isShowBar=" + isShowBar);
        if (isShowBar) {
            photoPbView.setTopBarVisibility(View.GONE);
            photoPbView.setBottomBarVisibility(View.GONE);
        } else {
            photoPbView.setTopBarVisibility(View.VISIBLE);
            photoPbView.setBottomBarVisibility(View.VISIBLE);
        }
    }


    public void reloadBitmap() {
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
    }


    private class MyViewPagerOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    isScrolling = true;
                    tempLastItem = photoPbView.getViewPagerCurrentItem();
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    if (isScrolling == true && tempLastItem != -1 && tempLastItem != photoPbView.getViewPagerCurrentItem()) {
                        lastItem = tempLastItem;
                    }

                    curPhotoIdx = photoPbView.getViewPagerCurrentItem();
                    isScrolling = false;
                    ShowCurPageNum();
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if (isScrolling) {
                if (lastItem > arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_RIGHT;
                } else if (lastItem < arg2) {
                    // 递减，向右侧滑动
                    slideDirection = DIRECTION_LEFT;
                } else if (lastItem == arg2) {
                    slideDirection = DIRECTION_RIGHT;
                }
            }
            lastItem = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
            ShowCurPageNum();
        }
    }

    private void ShowCurPageNum() {
        int curPhoto = photoPbView.getViewPagerCurrentItem() + 1;
        String indexInfo = curPhoto + "/" + localPhotoList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
    }

    public void delete(){
        showDeleteEnsureDialog();
    }

    public void share(){
        int curPosition = photoPbView.getViewPagerCurrentItem();
        String photoPath = localPhotoList.get(curPosition).file.getPath();
        AppLog.d(TAG,"share curPosition=" + curPosition + " photoPath=" + photoPath);
      /*  Uri imageUri = Uri.fromFile(new File(photoPath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)));
        */
        File cameraPhoto = new File(photoPath);
        Intent shareIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileUri = FileProvider.getUriForFile(activity,activity.getPackageName() + ".provider", cameraPhoto);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setDataAndType( fileUri, "image/*");
        activity.startActivityForResult(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.gallery_share_to)), 1 );
    }

    public void info(){
        MyToast.show(activity, "info photo");
    }

    private void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            LocalPbItemInfo curFile = localPhotoList.get(curPhotoIdx);
            if (curFile.file.exists()) {
                curFile.file.delete();
                //ICOM-4574
                MediaRefresh.notifySystemToScan(curFile.file);
            }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
//                        localPhotoList.remove(curPhotoIdx);
                        viewList.remove(curPhotoIdx);
                        localPhotoList.remove(curPhotoIdx);
                        viewPagerAdapter.notifyDataSetChanged();
                        photoPbView.setViewPagerAdapter(viewPagerAdapter);
                        int photoNums = localPhotoList.size();
                        if (photoNums == 0) {
                            activity.finish();
                            return;
                        } else {
                            if (curPhotoIdx == photoNums) {
                                curPhotoIdx--;
                            }
                            AppLog.d(TAG, "photoNums=" + photoNums + " curPhotoIdx=" + curPhotoIdx);
                            photoPbView.setViewPagerCurrentItem(curPhotoIdx);
                            ShowCurPageNum();
                        }
                    }
                });
            AppLog.d(TAG, "end DeleteThread");
        }
    }
}
