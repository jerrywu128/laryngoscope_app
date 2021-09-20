package com.icatch.sbcapp.Function;

import android.os.Handler;

import com.icatch.sbcapp.BaseItems.MultiPbItemInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.SdkApi.FileOperation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by b.jiang on 2016/2/24.
 */
public class DeleteFileThread implements Runnable {
    private String TAG="DeleteFileThread";
    private OnDeleteCompleteListener onDeleteCompleteListener;
    private List<MultiPbItemInfo> fileList;
    private List<MultiPbItemInfo> deleteFailedList;
    private List<MultiPbItemInfo> deletesucceedList;
    private Handler handler;
    private FileOperation fileOperation;

    public DeleteFileThread(OnDeleteCompleteListener listener,List<MultiPbItemInfo> fileList){
        this.fileList = fileList;
        this.onDeleteCompleteListener = listener;
        this.handler = new Handler();
        this.fileOperation = FileOperation.getInstance();
    }

    @Override
    public void run() {

        AppLog.d(TAG, "DeleteThread");

        if (deleteFailedList == null) {
            deleteFailedList = new LinkedList<MultiPbItemInfo>();
        } else {
            deleteFailedList.clear();
        }
        for (MultiPbItemInfo tempFile : fileList) {
            AppLog.d(TAG, "deleteFile f.getFileHandle =" + tempFile.getFileHandle());
            if (fileOperation.deleteFile(tempFile.iCatchFile) == false) {
                deleteFailedList.add(tempFile);
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                onDeleteCompleteListener.onDeleteComplete(deleteFailedList);
            }
        });

    }

    public interface OnDeleteCompleteListener {
        void onDeleteComplete(List<MultiPbItemInfo> deleteFailedList);
    }
}
