package com.honestmc.laryngoscopeapp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.honestmc.laryngoscopeapp.Adapter.LocalPhotoWallListAdapter;
import com.honestmc.laryngoscopeapp.BaseItems.FileType;
import com.honestmc.laryngoscopeapp.BaseItems.LocalPbItemInfo;
import com.honestmc.laryngoscopeapp.BaseItems.PhotoWallPreviewType;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Presenter.Interface.BasePresenter;
import com.honestmc.laryngoscopeapp.Tools.FileDES;
import com.honestmc.laryngoscopeapp.Tools.FileOpertion.MFileTools;
import com.honestmc.laryngoscopeapp.Tools.StorageUtil;
import com.honestmc.laryngoscopeapp.View.Interface.LocalPhotoFragmentView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalPhotoFragmentPresenter extends BasePresenter {
    String TAG = "LocalPhotoFragmentPresenter";
    String path;
    private LocalPhotoFragmentView localPhotoFragmentView;
    private LocalPhotoWallListAdapter localPhotoWallListAdapter;
    private PhotoWallPreviewType layoutType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
    private Activity activity;
    //    String[] fileList;
    private List<LocalPbItemInfo> photoList;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private int width;
    private int mFirstVisibleItem = 0;
    // GridView中可见的图片的数量
    private int mVisibleItemCount;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private int topVisiblePosition = -1;
    //    private LruCache<String, Bitmap> mLruCache;
//    private LimitQueue<Asytask> asytaskLimitQueue;
    private static int NUM_COLUMNS = 4;
    public LocalPhotoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(LocalPhotoFragmentView localPhotoFragmentView) {
        this.localPhotoFragmentView = localPhotoFragmentView;
        initCfg();
    }

    private List<LocalPbItemInfo> getPhotoList() {
        String filePath = StorageUtil.getDownloadPath(activity);
//        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/";
        String fileDate;
        int section = 1;
        List<LocalPbItemInfo> photoList = new ArrayList<LocalPbItemInfo>();
        List<File> fileList = MFileTools.getPhotosOrderByDate(filePath);
        AppLog.i(TAG, "fileList=" + fileList);
        AppLog.i(TAG, "fileList size=" + fileList.size());
        for (int ii = 0; ii < fileList.size(); ii++) {
            long time = fileList.get(ii).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), section);
                photoList.add(mGridItem);
                section++;
            } else {
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
                photoList.add(mGridItem);
            }
        }
        return photoList;
    }

    public void loadLocalPhotoWall()  {

        if (photoList == null) {
            photoList = getPhotoList();
        }

        if (photoList != null && photoList.size() > 0) {
            String fileDate = photoList.get(0).getFileDate();
            AppLog.i(TAG, "fileDate=" + fileDate);
            localPhotoFragmentView.setListViewHeaderText(fileDate);
        }
        GlobalInfo.getInstance().localPhotoList = photoList;
        localPhotoFragmentView.setListViewSelection(mFirstVisibleItem);
        int curWidth = 0;
        isFirstEnterThisActivity = true;
        if (layoutType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            localPhotoFragmentView.setListViewVisibility(View.VISIBLE);
            localPhotoWallListAdapter = new LocalPhotoWallListAdapter(activity, photoList, FileType.FILE_PHOTO);
            localPhotoFragmentView.setListViewAdapter(localPhotoWallListAdapter);
        }
    }

    public void redirectToAnotherActivity(Context context, Class<?> cls, int position) {
        AppLog.i(TAG, "redirectToAnotherActivity position=" + position);
        Intent intent = new Intent();
        isFirstEnterThisActivity = true;
//        intent.putExtra("curfilePath", photoList.get(position).getFilePath());
        intent.putExtra("curfilePosition", position);
        AppLog.i(TAG, "intent:start redirectToAnotherActivity class =" + cls.getName());
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public void decodePhoto() throws Exception {

        String path = StorageUtil.getDownloadPath(activity);
        FileDES fileDES = null;
        try {
            fileDES = new FileDES(FileDES.getPbKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(path+"/DES");
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File childFile = files[i];
            String childName = childFile.getName();
            try {
                fileDES.doDecryptFile(path+"DES/"+childName,path+childName+".jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }

}
