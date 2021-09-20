package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
//import android.support.v4.view.ViewPager;
import android.util.LruCache;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.icatch.sbcapp.Adapter.PhotoPbViewPagerAdapter;
import com.icatch.sbcapp.AppInfo.AppInfo;
import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.ExtendComponent.ProgressWheel;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.Tools.BitmapTools;
import com.icatch.sbcapp.Tools.FileOpertion.FileOper;
import com.icatch.sbcapp.Tools.MediaRefresh;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Interface.PhotoPbView;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by b.jiang on 2016/4/12.
 */
public class PhotoPbPresenter extends BasePresenter {
    private String TAG = "PhotoPbPresenter";
    private PhotoPbView photoPbView;
    private Activity activity;
    private List<MultiPbItemInfo> fileList = GlobalInfo.getInstance().photoInfoList;
    private FileOperation fileOperation = FileOperation.getInstance();
    private PhotoPbViewPagerAdapter viewPagerAdapter;
    private Handler handler;
    private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;

    private ExecutorService executor;
    private Future<Object> future;

    private int slideDirection = DIRECTION_RIGHT;

    private final static int DIRECTION_RIGHT = 0x1;
    private final static int DIRECTION_LEFT = 0x2;
    private final static int DIRECTION_UNKNOWN = 0x4;

    public String downloadingFilename;
    public long downloadProcess;
    public long downloadingFilesize;
    LinkedList<Asytask> asytaskList;
    Asytask curAsytask;
    private LruCache<Integer, Bitmap> mLruCache;
    private List<View> viewList;
//    long lastTime = 0;

    public PhotoPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        viewList = new LinkedList<View>();
        initLruCache();
        slideDirection = DIRECTION_UNKNOWN;
    }

    public void setView(PhotoPbView photoPbView) {
        this.photoPbView = photoPbView;
        initCfg();
    }

    private void initLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        AppLog.d(TAG, "initLruCache maxMemory=" + maxMemory);
        AppLog.d(TAG, "initLruCache cacheMemory=" + cacheMemory);
        mLruCache = new LruCache<Integer, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                AppLog.d(TAG, "cacheMemory value.getByteCount()=" + value.getByteCount());
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    AppLog.d(TAG, "cacheMemory entryRemoved key=" + key);
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                    System.gc();
                }
            }
        };
    }

    public Bitmap getBitmapFromLruCache(int fileHandle) {
        AppLog.d(TAG, "getBitmapFromLruCache filePath=" + fileHandle);
        return mLruCache.get(fileHandle);
    }

    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (bm.getByteCount() > mLruCache.maxSize()) {
            AppLog.d(TAG, "addBitmapToLruCache greater than mLruCache size filePath=" + fileHandle);
            return;
        }
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d(TAG, "addBitmapToLruCache filePath=" + fileHandle);
                mLruCache.put(fileHandle, bm);
            }
        }
    }

    public void loadImage() {
        Bundle data = activity.getIntent().getExtras();
        curPhotoIdx = data.getInt("curfilePosition");
        for (int ii = 0; ii < fileList.size(); ii++) {
            viewList.add(ii, null);
        }
        viewPagerAdapter = new PhotoPbViewPagerAdapter(activity, fileList, viewList, mLruCache);
        viewPagerAdapter.setOnPhotoTapListener(new PhotoPbViewPagerAdapter.OnPhotoTapListener() {
            @Override
            public void onPhotoTap() {
                showBar();
            }
        });
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
        loadBitmaps(curPhotoIdx);
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

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void download() {
        showDownloadEnsureDialog();
    }

    void loadBitmaps(int curPhotoIdx) {
        AppLog.i(TAG, "add task loadBitmaps curPhotoIdx=" + curPhotoIdx);
        if (curPhotoIdx < 0) {
            return;
        }
        if (curAsytask != null && !curAsytask.isCancelled()) {
            AppLog.i(TAG, "add task curAsytask cancel curAsytask position" + curAsytask.position);
            curAsytask.cancel(true);
        }
        if (asytaskList == null) {
            asytaskList = new LinkedList<Asytask>();
        } else {
            asytaskList.clear();
        }
        if (fileList == null || fileList.size() < 0) {
            AppLog.e(TAG, "fileList is null or size < 0");
            return;
        }
        if (curPhotoIdx == 0) {
            Asytask task1 = new Asytask(fileList.get(curPhotoIdx).iCatchFile, curPhotoIdx);
            asytaskList.add(task1);
            if (fileList.size() > 1) {
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx + 1).iCatchFile, curPhotoIdx + 1);
                asytaskList.add(task2);
            }

        } else if (curPhotoIdx == fileList.size() - 1) {
            Asytask task1 = new Asytask(fileList.get(curPhotoIdx).iCatchFile, curPhotoIdx);
            Asytask task2 = new Asytask(fileList.get(curPhotoIdx - 1).iCatchFile, curPhotoIdx - 1);
            asytaskList.add(task1);
            asytaskList.add(task2);
        } else {
            AppLog.d(TAG, "loadBitmaps slideDirection=" + slideDirection);
            if (slideDirection == DIRECTION_RIGHT) {
                Asytask task1 = new Asytask(fileList.get(curPhotoIdx).iCatchFile, curPhotoIdx);
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx - 1).iCatchFile, curPhotoIdx - 1);
                Asytask task3 = new Asytask(fileList.get(curPhotoIdx + 1).iCatchFile, curPhotoIdx + 1);
                asytaskList.add(task1);
                asytaskList.add(task2);
                asytaskList.add(task3);
            } else {
                Asytask task1 = new Asytask(fileList.get(curPhotoIdx).iCatchFile, curPhotoIdx);
                Asytask task2 = new Asytask(fileList.get(curPhotoIdx + 1).iCatchFile, curPhotoIdx + 1);
                Asytask task3 = new Asytask(fileList.get(curPhotoIdx - 1).iCatchFile, curPhotoIdx - 1);
                asytaskList.add(task1);
                asytaskList.add(task2);
                asytaskList.add(task3);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            curAsytask = asytaskList.removeFirst();
            curAsytask.execute();
//            asytaskList.removeFirst().execute();
        }
    }

    public void reloadBitmap() {
        photoPbView.setViewPagerAdapter(viewPagerAdapter);
        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        ShowCurPageNum();
        loadBitmaps(curPhotoIdx);
    }


    class Asytask extends AsyncTask<String, Integer, Bitmap> {

        ICatchFile iCatchFile;
        int fileHandle;
        int position;
        ICatchFrameBuffer buffer;
        boolean isZoom = false;

        public Asytask(ICatchFile iCatchFile, int position) {
            super();
            this.iCatchFile = iCatchFile;
            this.fileHandle = iCatchFile.getFileHandle();
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(fileHandle);
            AppLog.d(TAG, "getBitmapFromLruCache bm=" + bm);
            if (bm != null) {
                return bm;
            } else {
                buffer = fileOperation.getQuickview(iCatchFile);
                //JIRA BUG ICOM-3339 begin add by b.jiang 2016-06-08
                if (buffer == null || buffer.getFrameSize() <= 0) {
                    AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED 01");
                    buffer = fileOperation.downloadFile(iCatchFile);
                    isZoom = true;
                }
                //JIRA BUG ICOM-3339 end add by b.jiang 2016-06-08
                if (buffer == null || buffer.getFrameSize() <= 0) {
                    AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED 02");
                    return null;
                }
//                bm = BitmapFactory.decodeByteArray(buffer.getBuffer(), 0, buffer.getFrameSize());
                //JIRA BUG ICOM-3339 Start Modify by b.jiang 2016-06-08
                bm = BitmapTools.decodeByteArray(buffer.getBuffer(), 1080, 720);
                //JIRA BUG ICOM-3339 End Modify by b.jiang 2016-06-08
//                bm = bitmapDecode.decodeSampledBitmapFromByteArray(buffer.getBuffer(), 0, buffer.getFrameSize(),
//                        activity.getResources().getDisplayMetrics().widthPixels, activity.getResources().getDisplayMetrics().heightPixels);
                AppLog.d(TAG, "position=" + position + " decodeByteArray bm=" + bm);
                if (bm == null) {
                    return null;
                }
                AppLog.d(TAG, "11 position=" + position + "filePath=" + fileHandle + " buffer size=" + buffer.getFrameSize() + " bm size=" + bm.getByteCount());
                addBitmapToLruCache(fileHandle, bm);
                return bm;
            }
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Bitmap result) {
            //后台任务执行完之后被调用，在ui线程执行
            if (result != null) {
                View view = viewList.get(position);
                if (view != null) {
                    PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
                    ProgressWheel progressBar = (ProgressWheel) view.findViewById(R.id.progress_wheel);
                    //ICOM-4179 Begin modify by b.jiang 20170221
                    int tag = (int) photoView.getTag();
                    AppLog.d(TAG, "onPostExecute position=" + position + " fileHandle=" + fileHandle + " tag=" + tag + " size=" + result.getByteCount() + " result.isRecycled()=" + result.isRecycled() + " photoView=" + photoView);
                    if (photoView != null && !result.isRecycled() & tag == fileHandle) {
                        //ICOM-4179 Begin modify by b.jiang 20170221
                        photoView.setImageBitmap(result);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            if (asytaskList != null && asytaskList.size() > 0) {
                curAsytask = asytaskList.removeFirst();
                curAsytask.execute();
            }
        }
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
                    loadBitmaps(photoPbView.getViewPagerCurrentItem());
                    ShowCurPageNum();
//                    startLoadBitmapThread(photoPbView.getViewPagerCurrentItem());
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


    private class DownloadThread implements Runnable {
        private String TAG = "DownloadThread";
        private int curIdx = photoPbView.getViewPagerCurrentItem();

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;
            final String path = StorageUtil.getDownloadPath(activity);

//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH;
//            } else {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyProgressDialog.closeProgressDialog();
//                        MyToast.show(activity, "Download failed");
//                    }
//                });
//                return;
//            }
            String fileName = fileList.get(curIdx).getFileName();
            AppLog.d(TAG, "------------fileName =" + fileName);
            FileOper.createDirectory(path);
            downloadingFilename = path + fileName;
            downloadingFilesize = fileList.get(curIdx).iCatchFile.getFileSize();
            File tempFile = new File(downloadingFilename);
            //ICOM-4062 Delete ADD by b.jiang 20170103
//            if (tempFile.exists()) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        MyProgressDialog.closeProgressDialog();
//                        MyToast.show(activity, "Downloaded to" + AppInfo.DOWNLOAD_PATH);
//                    }
//                });
//            } else {
            boolean temp = fileOperation.downloadFile(fileList.get(curIdx).iCatchFile, downloadingFilename);
            if (temp == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, "Download failed");
                    }
                });
                AppInfo.isDownloading = false;
                return;
            }
            MediaRefresh.scanFileAsync(activity, downloadingFilename);
            AppLog.d(TAG, "end downloadFile temp =" + temp);
            AppInfo.isDownloading = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    MyToast.show(activity, "Downloaded to " + path);
                }
            });
//            }
            AppLog.d(TAG, "end DownloadThread");
        }
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
            ICatchFile curFile = fileList.get(curPhotoIdx).iCatchFile;
            Boolean retValue = false;
            retValue = fileOperation.deleteFile(curFile);
            if (retValue == false) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.dialog_delete_failed_single);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        fileList.remove(curPhotoIdx);
                        viewList.remove(curPhotoIdx);
                        viewPagerAdapter.notifyDataSetChanged();
                        photoPbView.setViewPagerAdapter(viewPagerAdapter);
                        int photoNums = fileList.size();
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
                            loadBitmaps(curPhotoIdx);
                        }
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    private void ShowCurPageNum() {
        int curPhoto = photoPbView.getViewPagerCurrentItem() + 1;
        String indexInfo = curPhoto + "/" + fileList.size();
        photoPbView.setIndexInfoTxv(indexInfo);
    }

    public void showDownloadEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_downloading_single);
        long videoFileSize = 0;
        videoFileSize = fileList.get(curPhotoIdx).getFileSizeInteger() / 1024 / 1024;
        long minute = videoFileSize / 60;
        long seconds = videoFileSize % 60;
        CharSequence what = activity.getResources().getString(R.string.gallery_download_with_vid_msg).replace("$1$", "1")
                .replace("$3$", String.valueOf(seconds)).replace("$2$", String.valueOf(minute));
        builder.setMessage(what);
        builder.setNegativeButton(R.string.gallery_download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                AppLog.d(TAG, "showProgressDialog");
                downloadProcess = 0;
                if (SystemInfo.getSDFreeSize(activity) < fileList.get(curPhotoIdx).getFileSizeInteger()) {
                    dialog.dismiss();
                    MyToast.show(activity, R.string.text_sd_card_memory_shortage);
                } else {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_downloading_single);
                    executor = Executors.newSingleThreadExecutor();
                    future = executor.submit(new DownloadThread(), null);
                }

            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDeleteEnsureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(R.string.image_delete_des);
        builder.setNegativeButton(R.string.gallery_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                asytaskList.clear();
                executor = Executors.newSingleThreadExecutor();
                future = executor.submit(new DeleteThread(), null);
            }
        });
        builder.setPositiveButton(R.string.gallery_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // 这里添加点击确定后的逻辑
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

