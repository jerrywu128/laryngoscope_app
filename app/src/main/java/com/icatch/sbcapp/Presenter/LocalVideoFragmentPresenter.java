package com.icatch.sbcapp.Presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.icatch.sbcapp.Adapter.LocalVideoWallListAdapter;
import com.icatch.sbcapp.BaseItems.LimitQueue;
import com.icatch.sbcapp.BaseItems.LocalPbItemInfo;
import com.icatch.sbcapp.BaseItems.PhotoWallPreviewType;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.MyCamera.MyCamera;
import com.icatch.sbcapp.Presenter.Interface.BasePresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.SystemInfo.SystemInfo;
import com.icatch.sbcapp.ThumbnailGetting.ThumbnailOperation;
import com.icatch.sbcapp.Tools.FileDES;
import com.icatch.sbcapp.Tools.FileOpertion.MFileTools;
import com.icatch.sbcapp.Tools.LruCacheTool;
import com.icatch.sbcapp.Tools.StorageUtil;
import com.icatch.sbcapp.View.Interface.LocalVideoFragmentView;
import com.icatch.sbcapp.View.Interface.LocalVideoWallView;
import com.icatch.wificam.customer.ICatchWificamAssist;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalVideoFragmentPresenter extends BasePresenter {
    private String TAG = "LocalVideoFragmentPresenter";
    private LocalVideoFragmentView localVideoFragmentView;
    private LocalVideoWallListAdapter localVideoWallListAdapter;
    //private LocalVideoWallGridAdapter localVideoWallGridAdapter;
    private PhotoWallPreviewType layoutType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
    private Activity activity;
    List<File> fileList;
    private List<LocalPbItemInfo> mGirdList;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private int width;
    private int height;
    //tprivate MyCamera myCamera;
    private int mFirstVisibleItem;
    // GridView中可见的图片的数量
    private int mVisibleItemCount;
    // 记录是否是第一次进入该界面
    private boolean isFirstEnterThisActivity = true;
    private int topVisiblePosition = -1;
    private LimitQueue<LocalVideoFragmentPresenter.Asytask> asytaskList;
    private LruCache<String, Bitmap> mLruCache = LruCacheTool.getInstance().getLruCache();
    private static int NUM_COLUMNS = 4;
    private Handler handler = new Handler();
    private static boolean isCheck_Password =false;
    public LocalVideoFragmentPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        asytaskList = new LimitQueue<LocalVideoFragmentPresenter.Asytask>(SystemInfo.getWindowVisibleCountMax(NUM_COLUMNS));
        initCamera();
    }



    public void setView(LocalVideoFragmentView localVideoFragmentView) {
        this.localVideoFragmentView = localVideoFragmentView;
        initCfg();
    }

    public boolean initCamera() {
      /*  myCamera = new MyCamera();
        if (myCamera.getSDKsession().prepareSession("192.168.1.1", false) == false) {
            AppLog.e(TAG, "prepareSession failed!");
            return false;
        }

        GlobalInfo.getInstance().setCurrentCamera(myCamera);
        AppLog.i(TAG, "Set CurrentCamera");
//        myCamera.initCamera();
        myCamera.initCameraForLocalPB();*/
        return true;
    }

    public void destroyCamera() {
        //t myCamera.destroyCamera();
    }

    private List<LocalPbItemInfo> getVideoList() {
        List<LocalPbItemInfo> tempList = new ArrayList<LocalPbItemInfo>();
        width = SystemInfo.getMetrics().widthPixels;
        String filePath = StorageUtil.getDownloadPath(activity)+"ENVIDEO/";
        //String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/thumbnails/";

        String fileDate;
        fileList = MFileTools.getVideosOrderByDate(filePath);
        //fileList =  FileTools.getFilesOrderByDate(filePath, FileType.FILE_PHOTO);
        if(fileList!=null){
            for (int ii = 0; ii < fileList.size(); ii++) {
                long time = fileList.get(ii).lastModified();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                fileDate = format.format(new Date(time));
                if (!sectionMap.containsKey(fileDate)) {
                    sectionMap.put(fileDate, section);
                    LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
                    tempList.add(mGridItem);
                    section++;
                } else {
                    LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), sectionMap.get(fileDate));
                    tempList.add(mGridItem);
                }
            }
        }
        return tempList;
    }

    public void loadLocalVideoWall() {
        if (mGirdList == null || mGirdList.size() < 0) {
            mGirdList = getVideoList();
        }
//        if (asytaskList == null) {
//            asytaskList = new LimitQueue<>(SystemInfo.getWindowVisibleCountMax(localVideoWallView.getGridViewNumColumns()));
//        }

        GlobalInfo.getInstance().localVideoList = mGirdList;
        int curWidth = 0;
        if (layoutType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            localVideoFragmentView.setListViewVisibility(View.VISIBLE);
            localVideoWallListAdapter = new LocalVideoWallListAdapter(activity, mGirdList, mLruCache);
            localVideoFragmentView.setListViewAdapter(localVideoWallListAdapter);
            localVideoFragmentView.setListViewOnScrollListener(listViewOnScrollListener);
        }
    }

    public void changePreviewType() {
        if (layoutType == PhotoWallPreviewType.PREVIEW_TYPE_LIST) {
            layoutType = PhotoWallPreviewType.PREVIEW_TYPE_GRID;
            localVideoFragmentView.setMenuPreviewTypeIcon(R.drawable.ic_view_grid_white_24dp);
        } else {
            layoutType = PhotoWallPreviewType.PREVIEW_TYPE_LIST;
            localVideoFragmentView.setMenuPreviewTypeIcon(R.drawable.ic_view_list_white_24dp);
        }
        loadLocalVideoWall();
    }

    public void redirectToAnotherActivity(final Context context, final Class<?> cls, final int position) {

        AppLog.i(TAG, "redirectToAnotherActivity position=" + position);
        clealAsytaskList();
        final String videoPath = mGirdList.get(position).getFilePath();
        //JIRA BUG ICOM-3524 Start add by b.jiang 20160725
        isFirstEnterThisActivity = true;
        //JIRA BUG ICOM-3524 End add by b.jiang 20160725
        //ICatchWificamAssist.getInstance().supportLocalPlay(videoPath)
        if (true) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("curfilePath", videoPath);
                    intent.putExtra("curfilePosition", position);
                    AppLog.i(TAG, "intent:start redirectToAnotherActivity class =" + cls.getName());
                    intent.setClass(context, cls);
                    context.startActivity(intent);
                }
            }, 500);
        } else {
            showNotSupportLocalPlayDialog(context, videoPath);
        }
    }

    private AbsListView.OnScrollListener listViewOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            AppLog.i(TAG, "onScrollStateChanged");
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                AppLog.i(TAG, "onScrollStateChanged firstVisibleItem=" + mFirstVisibleItem + " visibleItemCount=" + mVisibleItemCount);
                asytaskList.clear();
                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
            } else {
                asytaskList.clear();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            AppLog.i(TAG, "onScroll firstVisibleItem=" + firstVisibleItem);
            if (firstVisibleItem != topVisiblePosition) {
                topVisiblePosition = firstVisibleItem;
                if(!mGirdList.isEmpty()){
                    String fileDate = mGirdList.get(firstVisibleItem).getFileDate();
                    AppLog.i(TAG, "fileDate=" + fileDate);
                    localVideoFragmentView.setListViewHeaderText(fileDate);
                }
            }
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            if (isFirstEnterThisActivity && visibleItemCount > 0) {
                loadBitmaps(firstVisibleItem, visibleItemCount);
                isFirstEnterThisActivity = false;
            }
        }
    };
    private AbsListView.OnScrollListener gridViewOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            AppLog.i(TAG, "onScrollStateChanged scrollState=" + scrollState);
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                AppLog.i(TAG, "onScrollStateChanged firstVisibleItem=" + mFirstVisibleItem + " visibleItemCount=" + mVisibleItemCount);
                if (asytaskList != null && asytaskList.size() > 0) {
                    asytaskList.poll().execute();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            AppLog.i(TAG, "onScroll firstVisibleItem=" + firstVisibleItem + " visibleItemCount=" + visibleItemCount + " isFirstEnterThisActivity=" +
                    isFirstEnterThisActivity);
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            if (isFirstEnterThisActivity && visibleItemCount > 0) {
                if (asytaskList != null && asytaskList.size() > 0) {
                    asytaskList.poll().execute();
                    isFirstEnterThisActivity = false;
                }
            }
        }
    };

    void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        String imageUrl;
        for (int ii = firstVisibleItem; ii < firstVisibleItem + visibleItemCount; ii++) {
            if (ii < mGirdList.size()) {
                imageUrl = mGirdList.get(ii).getFilePath();
                LocalVideoFragmentPresenter.Asytask task = new LocalVideoFragmentPresenter.Asytask(imageUrl);
                asytaskList.offer(task);
                AppLog.i(TAG, "add task loadBitmaps ii=" + ii);
            }
        }
        if (asytaskList != null && asytaskList.size() > 0) {
            asytaskList.poll().execute();
        }
    }

    public void clearResource() {
        asytaskList.clear();
    }


    class Asytask extends AsyncTask<String, Integer, Bitmap> {

        String filePath;

        public Asytask(String path) {
            super();
            filePath = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            Bitmap bm = LruCacheTool.getInstance().getBitmapFromLruCache(filePath);
            AppLog.d(TAG, "Asytask doInBackground filePath=" + filePath + " bm=" + bm);
            if (bm != null) {
                return bm;
            } else {
                bm = ThumbnailOperation.getlocalVideoWallThumbnail(filePath);
                LruCacheTool.getInstance().addBitmapToLruCache(filePath, bm);
                return bm;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //后台任务执行完之后被调用，在ui线程执行
            if (result == null) {
                AppLog.d(TAG, "Asytask onPostExecute result is null");
                if (asytaskList != null && asytaskList.size() > 0) {
                    asytaskList.poll().execute();
                }
                return;
            }
            AppLog.d(TAG, "Asytask onPostExecute result size=" + result.getByteCount());
            ImageView imageView;
            if (layoutType == PhotoWallPreviewType.PREVIEW_TYPE_GRID) {
                imageView = (ImageView) localVideoFragmentView.gridViewFindViewWithTag(filePath);
            } else {
                imageView = (ImageView) localVideoFragmentView.listViewFindViewWithTag(filePath);
            }
            //imageView = (ImageView) mGridView.getChildAt(ii).findViewById(R.id.local_photo_wall_grid_item);
            AppLog.i(TAG, "loadBitmaps filePath=" + filePath + "result.isRecycled=" + result.isRecycled() + " imageView=" + imageView);
            //JIRA BUG ICOM-3521 Start modify by b.jiang 20160725
            if (imageView != null && !result.isRecycled()) {
                imageView.setImageBitmap(result);
            }
            //JIRA BUG ICOM-3521 End modify by b.jiang 20160725
            if (asytaskList != null && asytaskList.size() > 0) {
                asytaskList.poll().execute();
            }

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

    private void showNotSupportLocalPlayDialog(final Context context, final String videoPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.warning).setTitle("Warning").setMessage(R.string.not_support_play);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(videoPath);
                final Uri uri = Uri.fromFile(file);
                AppLog.d(TAG, "not supportLocalPlay");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/mp4");
                //t  context.startActivity(intent);
                //t dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void decodeVideo() throws Exception {

        String path = StorageUtil.getDownloadPath(activity);
        FileDES fileDES = null;
        try {
            fileDES = new FileDES(FileDES.getPbKey());
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

    public boolean check_password(){
        SharedPreferences mySharedPreferences= activity.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String password =mySharedPreferences.getString("video_password", "password");
        if(!isCheck_Password){
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View v = inflater.inflate(R.layout.iq_password_dialog, null);

        new AlertDialog.Builder(activity)
                .setTitle(R.string.please_input_password)
                .setView(v)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) (v.findViewById(R.id.password_text));
                        if (password.equals(editText.getText().toString())) {
                            isCheck_Password = true;
                            Toast.makeText(activity, R.string.check_video_password_sucess, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, R.string.iq_password_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                    }
                })
                .setNeutralButton(R.string.change_password,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //change password
                        change_video_password();
                    }
                })
                .show();
            return isCheck_Password;
        }else {
            return isCheck_Password;
        }
    }

    public void change_video_password() {
        SharedPreferences mySharedPreferences= activity.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String video_pwd =mySharedPreferences.getString("video_password", "password");
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View v2 = inflater.inflate(R.layout.iq_change_password, null);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.change_password)
                .setView(v2)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which2) {
                        EditText old_password_text = (EditText) (v2.findViewById(R.id.old_password));
                        EditText new_password_text = (EditText) (v2.findViewById(R.id.new_password));
                        if (video_pwd.equals(old_password_text.getText().toString())) {
                            if("".equals(new_password_text.getText().toString())){
                                Toast.makeText(activity, R.string.new_password_null, Toast.LENGTH_SHORT).show();
                            }else {
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.putString("video_password", new_password_text.getText().toString());
                                editor.commit();
                                isCheck_Password =false;
                                Toast.makeText(activity, R.string.change_password_sucess, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activity, R.string.iq_password_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                    }
                })
                .show();

    }



}
