package com.icatch.sbcapp.View.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.icatch.sbcapp.Adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.sbcapp.Adapter.MultiPbPhotoWallListAdapter;
import com.icatch.sbcapp.Mode.OperationMode;

/**
 * Created by b.jiang on 2016/1/5.
 */
public interface MultiPbPhotoFragmentView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(MultiPbPhotoWallListAdapter photoWallListAdapter);
    void setGridViewAdapter(MultiPbPhotoWallGridAdapter PhotoWallGridAdapter);
    void setListViewSelection(int position);
    void setGridViewSelection(int position);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(int tag);
    View gridViewFindViewWithTag(int tag);
    void updateGridViewBitmaps(String tag, Bitmap bitmap);
    void notifyChangeMultiPbMode(OperationMode operationMode);
    void setPhotoSelectNumText(int selectNum);
    void setNoContentTxvVisibility(int visibility);

}
