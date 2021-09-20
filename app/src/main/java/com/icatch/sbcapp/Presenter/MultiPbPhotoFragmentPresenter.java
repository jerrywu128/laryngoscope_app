package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.icatch.sbcapp.Adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.sbcapp.Adapter.MultiPbPhotoWallListAdapter;
import com.icatch.sbcapp.BaseItems.FileType;
import com.icatch.sbcapp.BaseItems.LimitQueue;
import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.BaseItems.PhotoWallPreviewType;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnAddAsytaskListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.OperationMode;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.PropertyId.PropertyId;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.Tools.ConvertTools;
import com.icatch.sbcapp.View.Activity.PhotoPbActivity;
import com.icatch.sbcapp.View.Interface.MultiPbPhotoFragmentView;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by b.jiang on 2016/1/5.
 */
public class MultiPbPhotoFragmentPresenter extends BasePresenter {

    private String TAG = "MultiPbPhotoFragmentPresenter";
    private MultiPbPhotoFragmentView multiPbPhotoView;
    private MultiPbPhotoWallListAdapter photoWallListAdapter;
    private MultiPbPhotoWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    //    String[] fileList;
    private int width;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private boolean isSelectAll = false;
    private int topVisiblePosition = -1;
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    //    LinkedList<Asytask> asytaskList;
    private LimitQueue<Asytask> asytaskList;
    private LruCache<Integer, Bitmap> mLruCache;
    private List<MultiPbItemInfo> pbItemInfoList;
    private FileOperation fileOperation = FileOperation.getInstance();
    private Handler handler;
    private int visiblePosition = 0;
    private int fileCount = -1;
    private int startIndex = 0;
    private int endIndex = 0;
    private static final int MAX_GET_FILE_NUM = 800;

    public MultiPbPhotoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(4));
        mLruCache = GlobalInfo.getInstance().mLruCache;
        handler = new Handler();
    }

    public void setView(MultiPbPhotoFragmentView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    public List<MultiPbItemInfo> getPhotoInfoList() {
        String fileDate;
        List<MultiPbItemInfo> photoInfoList = new ArrayList<MultiPbItemInfo>();
        List<ICatchFile> fileList = null;

        if (GlobalInfo.getInstance().photoInfoList != null) {
            photoInfoList = GlobalInfo.getInstance().photoInfoList;
        } else {
            if(CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_PB_LIMIT_NUMBER)){
                photoInfoList = fileOperation.getFileList(ICatchFileType.ICH_TYPE_IMAGE,MAX_GET_FILE_NUM);
            }else {
                photoInfoList = fileOperation.getFileList(ICatchFileType.ICH_TYPE_IMAGE);
            }
            AppLog.i(TAG, "fileList" + photoInfoList);
            if(photoInfoList != null){
                AppLog.i(TAG, "fileList size" + photoInfoList.size());
            }
            GlobalInfo.getInstance().photoInfoList = photoInfoList;
        }
        return photoInfoList;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbItemInfoList = getPhotoInfoList();
                if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            multiPbPhotoView.setGridViewVisibility(View.GONE);
                            multiPbPhotoView.setListViewVisibility(View.GONE);
                            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                            setAdaper();

                        }
                    });
                }
            }
        }).start();
    }

    public void setAdaper() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList != null && pbItemInfoList.size() > 0) {
            String fileDate = pbItemInfoList.get(0).getFileDate();
            AppLog.d(TAG, "fileDate=" + fileDate);
            multiPbPhotoView.setListViewHeaderText(fileDate);
        }
        int curWidth = 0;
        isFirstEnterThisActivity = true;
        if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new MultiPbPhotoWallListAdapter(activity, pbItemInfoList, mLruCache, FileType.FILE_PHOTO);
            multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
            if(visiblePosition >= 0){
                multiPbPhotoView.setListViewSelection(visiblePosition);
            }
        } else {
            width = SystemInfo.getMetrics().widthPixels;
            multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new MultiPbPhotoWallGridAdapter(activity, pbItemInfoList, width, mLruCache, FileType.FILE_PHOTO, new OnAddAsytaskListener
                    () {
                @Override
                public void addAsytask(int position) {
                    int fileHandle = pbItemInfoList.get(position).getFileHandle();
                    Asytask task = new Asytask(fileHandle);
                    asytaskList.offer(task);
                }
            }));
            multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
            if(visiblePosition >= 0){
                multiPbPhotoView.setGridViewSelection(visiblePosition);
            }
        }
    }

    public void refreshPhotoWall() {
        Log.i("1122", "refreshPhotoWall layoutType=" + GlobalInfo.photoWallPreviewType);
        pbItemInfoList = getPhotoInfoList();
        if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
            multiPbPhotoView.setGridViewVisibility(View.GONE);
            multiPbPhotoView.setListViewVisibility(View.GONE);
            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
//            setAdaper();
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                if (photoWallListAdapter != null) {
                    photoWallListAdapter.notifyDataSetChanged();
                }
            } else {
                if (photoWallGridAdapter != null) {
                    photoWallGridAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void changePreviewType() {
        if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
        } else {
            GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
        }
        loadPhotoWall();
//        setAdaper();
    }


    public void listViewLoadThumbnails(int scrollState, int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScrollStateChanged");
        visiblePosition = firstVisibleItem;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            AppLog.d(TAG, "onScrollStateChanged firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
            asytaskList.clear();
            loadBitmaps(firstVisibleItem, visibleItemCount);
        } else {
            asytaskList.clear();
        }
    }

    public void listViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem);
        if (firstVisibleItem != topVisiblePosition) {
            topVisiblePosition = firstVisibleItem;
            if (pbItemInfoList != null && pbItemInfoList.size() > 0) {
                String fileDate = pbItemInfoList.get(firstVisibleItem).getFileDate();
                AppLog.d(TAG, "fileDate=" + fileDate);
                multiPbPhotoView.setListViewHeaderText(fileDate);
            }
        }
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnterThisActivity = false;
        }
    }


    public void gridViewLoadThumbnails(int scrollState, int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScrollStateChanged scrollState=" + scrollState);
        visiblePosition = firstVisibleItem;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (asytaskList != null && asytaskList.size() > 0) {
                asytaskList.poll().execute();
            }
        }
    }

    public void gridViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
        AppLog.d(TAG, "onScroll isFirstEnterThisActivity=" + isFirstEnterThisActivity);
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            if (asytaskList != null && asytaskList.size() > 0) {
                asytaskList.poll().execute();
            }
            isFirstEnterThisActivity = false;
        }
    }

    void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        AppLog.i(TAG, "add task loadBitmaps firstVisibleItem=" + firstVisibleItem + " visibleItemCount" + visibleItemCount);
        int fileHandle;
        if (asytaskList == null) {
            asytaskList = new LimitQueue<>(SystemInfo.getWindowVisibleCountMax(4));
        }
        for (int ii = firstVisibleItem; ii < firstVisibleItem + visibleItemCount; ii++) {
            if (pbItemInfoList != null && pbItemInfoList.size() > 0 && ii < pbItemInfoList.size()) {
                fileHandle = pbItemInfoList.get(ii).getFileHandle();
                Asytask task = new Asytask(fileHandle);
                asytaskList.offer(task);
                AppLog.i(TAG, "add task loadBitmaps ii=" + ii);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.poll().execute();
        }
    }

    public void redirectToAnotherActivity(Context context, Class<?> cls, int position) {
        Intent intent = new Intent();
        intent.putExtra("curfilePath", pbItemInfoList.get(position).getFilePath());
        AppLog.i(TAG, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public void listViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallListAdapter.setOperationMode(curOperationMode);
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void gridViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void quitEditMode() {
        if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + GlobalInfo.photoWallPreviewType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
            clealAsytaskList();
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.setClass(activity, PhotoPbActivity.class);
            activity.startActivity(intent);
        } else {
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
        }

    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + GlobalInfo.photoWallPreviewType);
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
            Intent intent = new Intent();
            intent.putExtra("curfilePosition", position);
            intent.setClass(activity, PhotoPbActivity.class);
            activity.startActivity(intent);
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        } else {
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }

    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.selectAllItems();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.selectAllItems();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        } else {
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        }
    }

    public List<MultiPbItemInfo> getSelectedList() {
        if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }

    class Asytask extends AsyncTask<String, Integer, Bitmap> {

        int fileHandle;

        public Asytask(int handle) {
            super();
            fileHandle = handle;

        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(fileHandle);
            AppLog.d(TAG, "getBitmapFromLruCache filePath=" + fileHandle + " bm=" + bm);
            if (bm != null) {
                return bm;
            } else {
//                bm = ThumbnailOperation.getPhotoThumbnail(filePath);
//                addBitmapToLruCache(filePath, bm);
                ICatchFile file = new ICatchFile(fileHandle);
                ICatchFrameBuffer buffer = FileOperation.getInstance().getThumbnail(file);
                AppLog.d(TAG, "decodeByteArray buffer=" + buffer);
                AppLog.d(TAG, "decodeByteArray filePath=" + fileHandle);
                if (buffer == null) {
                    AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED");
                    return null;
                }

                int datalength = buffer.getFrameSize();
                if (datalength > 0) {
                    bm = BitmapFactory.decodeByteArray(buffer.getBuffer(), 0, datalength);
                }
                if (bm != null) {
                    addBitmapToLruCache(fileHandle, bm);
                }
                return bm;
            }
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ImageView imageView;
                if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_GRID) {
                    imageView = (ImageView) multiPbPhotoView.gridViewFindViewWithTag(fileHandle);
                } else {
                    imageView = (ImageView) multiPbPhotoView.listViewFindViewWithTag(fileHandle);
                }
                //imageView = (ImageView) mGridView.getChildAt(ii).findViewById(R.id.local_photo_wall_grid_item);
                AppLog.i(TAG, "loadBitmaps imageView=" + imageView);
                if (imageView != null && !result.isRecycled()) {
                    imageView.setImageBitmap(result);
                }
            }
            //后台任务执行完之后被调用，在ui线程执行
            if (asytaskList != null && asytaskList.size() > 0) {
                Log.i("1111", "eeeee");
                asytaskList.poll().execute();
            }
        }
    }

    public Bitmap getBitmapFromLruCache(int fileHandle) {

        return mLruCache.get(fileHandle);
    }

    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d(TAG, "addBitmapToLruCache filePath=" + fileHandle);
                mLruCache.put(fileHandle, bm);
            }

        }
    }

    public void emptyFileList() {
        if (GlobalInfo.getInstance().photoInfoList != null) {
            GlobalInfo.getInstance().photoInfoList.clear();
            GlobalInfo.getInstance().photoInfoList = null;
        }
    }

    public void clealAsytaskList() {
        AppLog.d(TAG, "clealAsytaskList");
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.clear();
//            asytaskList = null;
        }
    }

}