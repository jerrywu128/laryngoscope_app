package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
import android.util.LruCache;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.icatch.sbcapp.Adapter.ViewPagerAdapter;
import com.icatch.sbcapp.BaseItems.FileType;
import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.BaseItems.PhotoWallPreviewType;
import com.icatch.sbcapp.ExtendComponent.MyProgressDialog;
import com.icatch.sbcapp.ExtendComponent.MyToast;
import com.icatch.sbcapp.Function.PbDownloadManager;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnStatusChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Mode.OperationMode;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SdkApi.FileOperation;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.Tools.FileDES;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Fragment.LocalPhotoFragment;
import com.icatch.sbcapp.View.Fragment.LocalVideoFragment;
import com.icatch.sbcapp.View.Fragment.MultiPbPhotoFragment;
import com.icatch.sbcapp.View.Fragment.MultiPbVideoFragment;
import com.icatch.sbcapp.View.Interface.MultiPbView;
import com.icatch.wificam.customer.type.ICatchFile;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by b.jiang on 2016/1/6.
 */
public class MultiPbPresenter extends BasePresenter {
    private static final String TAG = "MultiPbPresenter";
    private MultiPbView multiPbView;
    private Activity activity;
    private FragmentManager fragmentManager;
    private int offset = 0;// 动画图片偏移量
    MultiPbPhotoFragment multiPbPhotoFragment;
    MultiPbVideoFragment multiPbVideoFragment;
    LocalPhotoFragment localPhotoFragment;
    LocalVideoFragment localVideoFragment;
    OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    ViewPagerAdapter adapter;
    private Executor executor;
    private boolean curSelectAll = false;
    private boolean isBitmapRecycled = false;

    public MultiPbPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        this.executor = Executors.newSingleThreadExecutor();
        initLruCache();
    }

    public void setView(MultiPbView multiPbView) {
        this.multiPbView = multiPbView;
        initCfg();
    }

    public void loadViewPage() {
        initViewpager();
    }

    private void initLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 4;
        AppLog.d(TAG, "initLruCache cacheMemory=" + cacheMemory);
        GlobalInfo.getInstance().mLruCache = new LruCache<Integer, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
//                return value.getRowBytes() * value.getHeight();
                AppLog.d("cacheMemory", " value.getByteCount()=" + value.getByteCount());
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    AppLog.d("cacheMemory", "entryRemoved key=" + key);
                    //回收bitmap占用的内存空间
                    oldValue.recycle();
                    oldValue = null;
                }
            }
        };
    }

    public void clearCache() {
        isBitmapRecycled = true;
        GlobalInfo.getInstance().mLruCache.evictAll();
    }

    public void reset() {
        GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
        GlobalInfo.currentViewpagerPosition = 0;
    }


    private void initViewpager() {

        if (multiPbVideoFragment == null) {
            multiPbVideoFragment = new MultiPbVideoFragment();
        }
        multiPbVideoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onEnterEditMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setEditLayoutVisibiliy(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setEditLayoutVisibiliy(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = "Selected(" + SelectedNum + ")";
                multiPbView.setSelectNumText(temp);
            }


        });

        if (localPhotoFragment == null) {
            localPhotoFragment = new LocalPhotoFragment();
        }
        localPhotoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onEnterEditMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setEditLayoutVisibiliy(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setEditLayoutVisibiliy(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = "Selected(" + SelectedNum + ")";
                multiPbView.setSelectNumText(temp);
            }


        });

        if (localVideoFragment == null) {
            localVideoFragment = new LocalVideoFragment();
        }
        localVideoFragment.setOperationListener(new OnStatusChangedListener() {
            @Override
            public void onEnterEditMode(OperationMode operationMode) {
                curOperationMode = operationMode;
                if (curOperationMode == OperationMode.MODE_BROWSE) {
                    multiPbView.setViewPagerScanScroll(true);
                    multiPbView.setTabLayoutClickable(true);
                    multiPbView.setEditLayoutVisibiliy(View.GONE);
                    multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
                    curSelectAll = false;
                    AppLog.d(TAG, "multiPbVideoFragment quit EditMode");
                } else {
                    multiPbView.setViewPagerScanScroll(false);
                    multiPbView.setTabLayoutClickable(false);
                    multiPbView.setEditLayoutVisibiliy(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedItemsCountChanged(int SelectedNum) {
                String temp = "Selected(" + SelectedNum + ")";
                multiPbView.setSelectNumText(temp);
            }


        });


        FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(manager);
        adapter.addFragment(localPhotoFragment, activity.getResources().getString(R.string.title_photo));
        adapter.addFragment(localVideoFragment, activity.getResources().getString(R.string.title_video));

        multiPbView.setViewPageAdapter(adapter);
        multiPbView.setViewPageCurrentItem(GlobalInfo.currentViewpagerPosition);

    }

    public void updateViewpagerStatus(int arg0) {
        AppLog.d(TAG, "updateViewpagerStatus arg0=" + arg0);
        GlobalInfo.currentViewpagerPosition = arg0;
    }

    public void changePreviewType() {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            clealAsytaskList();
            if (GlobalInfo.photoWallPreviewType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
                GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_grid_white_24dp);
            } else {
                GlobalInfo.photoWallPreviewType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
                multiPbView.setMenuPhotoWallTypeIcon(R.drawable.ic_view_list_white_24dp);
            }
            multiPbPhotoFragment.changePreviewType();
            multiPbVideoFragment.changePreviewType();
            AppLog.d(TAG, " changePreviewType AppInfo.photoWallPreviewType");
        }
    }

    public void reback() {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            activity.finish();
        } else if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            if (GlobalInfo.currentViewpagerPosition == 0) {
                   multiPbPhotoFragment.quitEditMode();
            } else if (GlobalInfo.currentViewpagerPosition == 1) {
                   multiPbVideoFragment.quitEditMode();
            }
        }

    }


    public void selectOrCancel() {

        if (curSelectAll) {
            multiPbView.setSelectBtnIcon(R.drawable.ic_select_all_white_24dp);
            curSelectAll = false;
        } else {
            multiPbView.setSelectBtnIcon(R.drawable.ic_unselected_white_24dp);
            curSelectAll = true;
        }
        if (GlobalInfo.currentViewpagerPosition == 0) {
             multiPbPhotoFragment.selectOrCancelAll(curSelectAll);
        } else if (GlobalInfo.currentViewpagerPosition == 1) {
             multiPbVideoFragment.select(curSelectAll);
        }
    }

    public void delete() {
        List<MultiPbItemInfo> list = null;
        FileType fileType = null;
        AppLog.d(TAG, "delete AppInfo.currentViewpagerPosition=" + GlobalInfo.currentViewpagerPosition);
        if (GlobalInfo.currentViewpagerPosition == 0) {
             list = multiPbPhotoFragment.getSelectedList();
            fileType = FileType.FILE_PHOTO;
        } else if (GlobalInfo.currentViewpagerPosition == 1) {
             list = multiPbVideoFragment.getSelectedList();
            fileType = FileType.FILE_VIDEO;
        }
        if (list == null || list.size() <= 0) {
            AppLog.d(TAG, "asytaskList size=" + list.size());
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            CharSequence what = activity.getResources().getString(R.string.gallery_delete_des).replace("$1$", String.valueOf(list.size()));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage(what);
            builder.setPositiveButton(activity.getResources().getString(R.string.gallery_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final List<MultiPbItemInfo> finalList = list;
            final FileType finalFileType = fileType;
            builder.setNegativeButton(activity.getResources().getString(R.string.gallery_delete), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    quitEditMode();
                    new DeleteFileThread(finalList, finalFileType).run();
                }
            });
            builder.create().show();
        }
    }

    public void download() {
        List<MultiPbItemInfo> list = null;
        LinkedList<ICatchFile> linkedList = new LinkedList<>();
        long fileSizeTotal = 0;
        AppLog.d(TAG, "delete currentViewpagerPosition=" + GlobalInfo.currentViewpagerPosition);
        if (GlobalInfo.currentViewpagerPosition == 0) {
              list = multiPbPhotoFragment.getSelectedList();
        } else if (GlobalInfo.currentViewpagerPosition == 1) {
             list = multiPbVideoFragment.getSelectedList();
        }
        if (list == null || list.size() <= 0) {
            AppLog.d(TAG, "asytaskList size=" + list.size());
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            for (MultiPbItemInfo temp : list
                    ) {
                linkedList.add(temp.iCatchFile);
                fileSizeTotal += temp.getFileSizeInteger();
            }
            if (SystemInfo.getSDFreeSize(activity) < fileSizeTotal) {
                MyToast.show(activity, R.string.text_sd_card_memory_shortage);
            } else {
                quitEditMode();
                PbDownloadManager downloadManager = new PbDownloadManager(activity, linkedList);
                downloadManager.show();
            }
        }
    }

    class DeleteFileThread implements Runnable {
        private List<MultiPbItemInfo> fileList;
        private List<MultiPbItemInfo> deleteFailedList;
        private List<MultiPbItemInfo> deleteSucceedList;
        private Handler handler;
        private FileOperation fileOperation;
        private FileType fileType;

        public DeleteFileThread(List<MultiPbItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileOperation = FileOperation.getInstance();
            this.fileType = fileType;
        }

        @Override
        public void run() {

            AppLog.d(TAG, "DeleteThread");

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<MultiPbItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<MultiPbItemInfo>();
            } else {
                deleteSucceedList.clear();
            }
            for (MultiPbItemInfo tempFile : fileList) {
                AppLog.d(TAG, "deleteFile f.getFileHandle =" + tempFile.getFileHandle());
                if (fileOperation.deleteFile(tempFile.iCatchFile) == false) {
                    deleteFailedList.add(tempFile);
                } else {
                    deleteSucceedList.add(tempFile);
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();

                    if (fileType == FileType.FILE_PHOTO) {
                        GlobalInfo.getInstance().photoInfoList.removeAll(deleteSucceedList);
                           multiPbPhotoFragment.refreshPhotoWall();
                          multiPbPhotoFragment.quitEditMode();

                    } else if (fileType == FileType.FILE_VIDEO) {
                        GlobalInfo.getInstance().videoInfoList.removeAll(deleteSucceedList);
                           multiPbVideoFragment.refreshPhotoWall();
                           multiPbVideoFragment.quitEditMode();
                    }
                    if (deleteFailedList.isEmpty() == false) {
//                        showDeleteFailedDialog();
                    }
                }
            });
        }
    }

    public void clealAsytaskList() {
          multiPbPhotoFragment.clealAsytaskList();
          multiPbVideoFragment.clealAsytaskList();
    }

    private void quitEditMode() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (GlobalInfo.currentViewpagerPosition == 0) {
             multiPbPhotoFragment.quitEditMode();
        } else if (GlobalInfo.currentViewpagerPosition == 1) {
            multiPbVideoFragment.quitEditMode();
        }
    }

    public void clearDecodeFile(){
        String path = StorageUtil.getDownloadPath(activity);

        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File childFile = files[i];
            String childName = childFile.getName();
            if(!(childName.equals("DES")||childName.equals("ENVIDEO"))){
                childFile.delete();
            }

        }
    }

    public void encodeVideo(){
        AppLog.d(TAG, "encode video");
        String path = StorageUtil.getDownloadPath(activity);
        FileDES fileDES = null;
        try {
            fileDES = new FileDES("test.key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(path+"/ENVIDEO");
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File childFile = files[i];
            String childName = childFile.getName();
            try {
                fileDES.encrypt(path+"ENVIDEO/"+childName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
