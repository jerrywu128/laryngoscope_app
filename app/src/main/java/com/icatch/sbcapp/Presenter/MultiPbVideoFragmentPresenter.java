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
import android.widget.Toast;

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
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.CameraProperties;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.View.Activity.VideoPbActivity;
import com.icatch.sbcapp.View.Interface.MultiPbVideoFragmentView;
import com.icatch.wificam.customer.type.ICatchFile;
import com.icatch.wificam.customer.type.ICatchFileType;
import com.icatch.wificam.customer.type.ICatchFrameBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by b.jiang on 2016/1/6.
 */
public class MultiPbVideoFragmentPresenter extends BasePresenter {

    private String TAG = "MultiPbVideoFragmentPresenter";
    private MultiPbVideoFragmentView videoView;
    private MultiPbPhotoWallListAdapter photoWallListAdapter;
    private MultiPbPhotoWallGridAdapter photoWallGridAdapter;
    private Activity activity;

//    private static int section = 1;
//    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private int width;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private boolean isSelectAll = false;
    private int topVisiblePosition = -1;
    private OperationMode operationMode = OperationMode.MODE_BROWSE;
    //    LinkedList<Asytask> asytaskList;
    private LimitQueue<Asytask> asytaskList;
    private Asytask curAsytask;
    private LruCache<Integer, Bitmap> mLruCache;
    private List<MultiPbItemInfo> videoInfoList;
    private FileOperation fileOperation = FileOperation.getInstance();
    private CameraProperties cameraProperties = CameraProperties.getInstance();
    private Handler handler;
    private int visiblePosition = 0;
    private static final int MAX_GET_FILE_NUM = 800;

    public MultiPbVideoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(4));
        this.mLruCache = GlobalInfo.getInstance().mLruCache;
        handler = new Handler();
    }

    public void setView(MultiPbVideoFragmentView multiPbVideoView) {
        this.videoView = multiPbVideoView;
        initCfg();
    }

    public List<MultiPbItemInfo> getVideoList() {
        List<MultiPbItemInfo> videoInfoList = new ArrayList<MultiPbItemInfo>();
        if (GlobalInfo.getInstance().videoInfoList != null) {
            videoInfoList = GlobalInfo.getInstance().videoInfoList;
        } else {
            if(CameraProperties.getInstance().hasFuction(PropertyId.CAMERA_PB_LIMIT_NUMBER)){
                videoInfoList = fileOperation.getFileList(ICatchFileType.ICH_TYPE_VIDEO,MAX_GET_FILE_NUM);
            }else {
                videoInfoList = fileOperation.getFileList(ICatchFileType.ICH_TYPE_VIDEO);
            }
            GlobalInfo.getInstance().videoInfoList = videoInfoList;
        }
        return videoInfoList;
    }

    public void loadVideoWall() {
        MyProgressDialog.showProgressDialog(activity, "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                videoInfoList = getVideoList();
                if (videoInfoList == null || videoInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoView.setGridViewVisibility(View.GONE);
                            videoView.setListViewVisibility(View.GONE);
                            videoView.setNoContentTxvVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoView.setNoContentTxvVisibility(View.GONE);
                            setAdaper();

                        }
                    });
                }
            }
        }).start();
    }

    public void setAdaper() {
        operationMode = OperationMode.MODE_BROWSE;
        int curWidth = 0;
        isFirstEnterThisActivity = true;
        if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            videoView.setGridViewVisibility(View.GONE);
            videoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new MultiPbPhotoWallListAdapter(activity, videoInfoList, mLruCache, FileType.FILE_VIDEO);
            videoView.setListViewAdapter(photoWallListAdapter);
            videoView.setListViewSelection(visiblePosition);
        } else {
            width = SystemInfo.getMetrics().widthPixels;
            videoView.setGridViewVisibility(View.VISIBLE);
            videoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new MultiPbPhotoWallGridAdapter(activity, videoInfoList, width, mLruCache, FileType.FILE_VIDEO, new OnAddAsytaskListener() {
                @Override
                public void addAsytask(int position) {
//                    int fileHandle = videoInfoList.get(position).getFileHandle();
//                    Asytask task = new Asytask(fileHandle);
                    Asytask task = new Asytask(videoInfoList.get(position).iCatchFile);
                    asytaskList.offer(task);
                }
            }));
            videoView.setGridViewAdapter(photoWallGridAdapter);
            videoView.setGridViewSelection(visiblePosition);
        }
    }

    public void changePreviewType() {
        if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
        } else {
            GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
        }
        loadVideoWall();
//        setAdaper();
    }


    public void refreshPhotoWall() {
        Log.i("1122", "refreshPhotoWall GlobalInfo.photoWallPreviewType=" + GlobalInfo.photoWallPreviewType);
        videoInfoList = getVideoList();
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            videoView.setGridViewVisibility(View.GONE);
            videoView.setListViewVisibility(View.GONE);
            videoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            videoView.setNoContentTxvVisibility(View.GONE);
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
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            return;
        }
        if (firstVisibleItem != topVisiblePosition && videoInfoList.size() > 0) {
            topVisiblePosition = firstVisibleItem;
            String fileDate = videoInfoList.get(firstVisibleItem).getFileDate();
            AppLog.d(TAG, "fileDate=" + fileDate);
            videoView.setListViewHeaderText(fileDate);
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
            AppLog.d(TAG, "onScrollStateChanged firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
            if (asytaskList != null && asytaskList.size() > 0) {
//                asytaskList.poll().execute();
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
        }
    }

    public void gridViewLoadOnceThumbnails(int firstVisibleItem, int visibleItemCount) {
        AppLog.d(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount);
        if (videoInfoList == null || videoInfoList.size() <= 0) {
            return;
        }
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            if (asytaskList != null && asytaskList.size() > 0) {
//                asytaskList.poll().execute();
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
            isFirstEnterThisActivity = false;
        }
    }

    void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        AppLog.i(TAG, "add task loadBitmaps 111111 asytaskList=" + asytaskList);
        int fileHandle;
        if (asytaskList == null) {
            asytaskList = new LimitQueue<Asytask>(SystemInfo.getWindowVisibleCountMax(4));
        }
        for (int ii = firstVisibleItem; ii < firstVisibleItem + visibleItemCount; ii++) {
            if (videoInfoList != null && videoInfoList.size() > 0 && ii < videoInfoList.size()) {
//                fileHandle = videoInfoList.get(ii).getFileHandle();
//                Asytask task = new Asytask(fileHandle);
                Asytask task = new Asytask(videoInfoList.get(ii).iCatchFile);
                asytaskList.offer(task);
                AppLog.i(TAG, "add task loadBitmaps ii=" + ii);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
//            asytaskList.poll().execute();
            curAsytask = asytaskList.poll();
            curAsytask.execute();
        }
    }

    public void redirectToAnotherActivity(Context context, Class<?> cls, int position) {
        Intent intent = new Intent();
        intent.putExtra("curfilePath", videoInfoList.get(position).getFilePath());
        AppLog.i(TAG, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public void listViewEnterEditMode(int position) {
        if (operationMode == OperationMode.MODE_BROWSE) {
            operationMode = OperationMode.MODE_EDIT;
            videoView.changeMultiPbMode(operationMode);
            photoWallListAdapter.setOperationMode(operationMode);
            photoWallListAdapter.changeSelectionState(position);
            videoView.setVideoSelectNumText(photoWallListAdapter.getSelectedCount());
        }
//        else {
//            operationMode = OperationMode.MODE_BROWSE;
//            videoView.changeMultiPbMode(operationMode);
//            photoWallListAdapter.quitEditMode();
//        }
    }

    public void gridViewEnterEditMode(int position) {
        if (operationMode == OperationMode.MODE_BROWSE) {
            operationMode = OperationMode.MODE_EDIT;
            videoView.changeMultiPbMode(operationMode);
            photoWallGridAdapter.changeCheckBoxState(position, operationMode);
            videoView.setVideoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }
//        else {
//            operationMode = OperationMode.MODE_BROWSE;
//            videoView.changeMultiPbMode(operationMode);
//            photoWallGridAdapter.quitEditMode();
//        }

    }

    public void quitEditMode() {
        if (operationMode == OperationMode.MODE_EDIT) {
            operationMode = OperationMode.MODE_BROWSE;
            videoView.changeMultiPbMode(operationMode);
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(final int position) {
        AppLog.d(TAG, "listViewSelectOrCancelOnce position=" + position + " operationMode=" + operationMode);


        if (operationMode == OperationMode.MODE_BROWSE) {
//            redirectToAnotherActivity(activity, VideoPbActivity.class);
            //IC-806 begin add by b.jiang 20170421
            if (cameraProperties.supportVideoPlayback() == false) {
                Toast.makeText(activity, R.string.gallery_view_video_not_supported, Toast.LENGTH_SHORT).show();
                return;
            }
            //IC-806 End add by b.jiang 20170421
            clealAsytaskList();
            //ICOM-3428 Start ADD by b.jiang 20160713
            if (curAsytask != null) {
                boolean ret = curAsytask.cancel(true);
                AppLog.d(TAG, "curAsytask cancal ret=" + ret);
            }
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                    AppLog.d(TAG, "curAsytask Thread.sleep(500)");
                    Intent intent = new Intent();
                    intent.putExtra("curfilePosition", position);
                    intent.setClass(activity, VideoPbActivity.class);
                    activity.startActivity(intent);
//                this.cancel();
                }
            }, 500);//五百毫秒
            MyProgressDialog.showProgressDialog(activity, "Loading...");
        } else {
            photoWallListAdapter.changeSelectionState(position);
            videoView.setVideoSelectNumText(photoWallListAdapter.getSelectedCount());
        }
    }

    public void gridViewSelectOrCancelOnce(final int position) {
        AppLog.d(TAG, "gridViewSelectOrCancelOnce positon=" + position + " GlobalInfo.photoWallPreviewType=" + GlobalInfo.photoWallPreviewType);
        if (operationMode == OperationMode.MODE_BROWSE) {
            AppLog.d(TAG, "gridViewSelectOrCancelOnce operationMode=" + operationMode);
            //IC-806 begin add by b.jiang 20170421
            if (cameraProperties.supportVideoPlayback() == false) {
                Toast.makeText(activity, R.string.gallery_view_video_not_supported, Toast.LENGTH_SHORT).show();
                return;
            }
            //IC-806 End add by b.jiang 20170421

            clealAsytaskList();
            //ICOM-3428 Start ADD by b.jiang 20160713
            if (curAsytask != null) {
                boolean ret = curAsytask.cancel(true);
                AppLog.d(TAG, "curAsytask cancal ret=" + ret);
            }
            Timer timer = new Timer();//实例化Timer类
            timer.schedule(new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                    AppLog.d(TAG, "curAsytask Thread.sleep(500)");
                    Intent intent = new Intent();
                    intent.putExtra("curfilePosition", position);
                    intent.setClass(activity, VideoPbActivity.class);
                    activity.startActivity(intent);
//                this.cancel();
                }
            }, 500);//五百毫秒
            MyProgressDialog.showProgressDialog(activity, "Loading...");
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //ICOM-3428 End ADD by b.jiang 20160713

        } else {
            photoWallGridAdapter.changeCheckBoxState(position, operationMode);
            videoView.setVideoSelectNumText(photoWallGridAdapter.getSelectedCount());
        }
    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (operationMode == OperationMode.MODE_BROWSE) {
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
            videoView.setVideoSelectNumText(selectNum);
        } else {
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            videoView.setVideoSelectNumText(selectNum);
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
        ICatchFile iCatchFile;
        int fileHandle;

        public Asytask(ICatchFile iCatchFile) {
            super();
            this.iCatchFile = iCatchFile;
            this.fileHandle = iCatchFile.getFileHandle();

        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = getBitmapFromLruCache(fileHandle);

            if (bm != null) {
                return bm;
            } else {
//                ICatchFile file = new ICatchFile(fileHandle);

                ICatchFrameBuffer buffer = FileOperation.getInstance().getThumbnail(iCatchFile);
                AppLog.d(TAG, "decodeByteArray buffer=" + buffer);
                AppLog.d(TAG, "decodeByteArray fileHandle=" + fileHandle);
                if (buffer == null) {
                    AppLog.e(TAG, "buffer == null  send _LOAD_BITMAP_FAILED");
                    return null;
                }
                AppLog.d(TAG, "decodeByteArray getFrameSize=" + buffer.getFrameSize());
                int datalength = buffer.getFrameSize();
                if (datalength > 0) {
                    bm = BitmapFactory.decodeByteArray(buffer.getBuffer(), 0, datalength);
                }
                AppLog.d(TAG, "decodeByteArray bm=" + bm);
                addBitmapToLruCache(fileHandle, bm);
                return bm;
            }
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ImageView imageView;
                if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_GRID) {
                    imageView = (ImageView) videoView.gridViewFindViewWithTag(fileHandle);
                } else {
                    imageView = (ImageView) videoView.listViewFindViewWithTag(fileHandle);
                }
                //imageView = (ImageView) mGridView.getChildAt(ii).findViewById(R.id.local_photo_wall_grid_item);
                AppLog.i(TAG, "loadBitmaps filePath=" + fileHandle + " imageView=" + imageView);
                if (imageView != null && !result.isRecycled()) {
                    imageView.setImageBitmap(result);
                }
            }
            //后台任务执行完之后被调用，在ui线程执行
            if (asytaskList != null && asytaskList.size() > 0) {
                Log.i("1111", "eeeee");
//                asytaskList.poll().execute();
                curAsytask = asytaskList.poll();
                curAsytask.execute();
            }
        }
    }

    public Bitmap getBitmapFromLruCache(int fileHandle) {
        return mLruCache.get(fileHandle);
    }

    protected void addBitmapToLruCache(int fileHandle, Bitmap bm) {
        if (getBitmapFromLruCache(fileHandle) == null) {
            if (bm != null && fileHandle != 0) {
                AppLog.d("test", "addBitmapToLruCache filePath=" + fileHandle);
                AppLog.d("test", "addBitmapToLruCache bitmap=" + bm);
                mLruCache.put(fileHandle, bm);
            }
        }
    }

    public void emptyFileList() {
        if (GlobalInfo.getInstance().videoInfoList != null) {
            GlobalInfo.getInstance().videoInfoList.clear();
            GlobalInfo.getInstance().videoInfoList = null;
        }
    }

    public void clealAsytaskList() {
        AppLog.d(TAG, "clealAsytaskList");
        if (asytaskList != null && asytaskList.size() > 0) {
            AppLog.d(TAG, "clealAsytaskList size=" + asytaskList.size());
            asytaskList.clear();
//            asytaskList = null;
        }
    }
}