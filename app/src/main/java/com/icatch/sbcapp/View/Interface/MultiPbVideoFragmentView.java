package com.icatch.sbcapp.View.Interface;

import android.view.View;

import com.icatch.sbcapp.Adapter.MultiPbPhotoWallGridAdapter;
import com.icatch.sbcapp.Adapter.MultiPbPhotoWallListAdapter;
import com.icatch.sbcapp.Mode.OperationMode;

/**
 * Created by b.jiang on 2016/1/6.
 */
public interface MultiPbVideoFragmentView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(MultiPbPhotoWallListAdapter multiPbPhotoWallListAdapter);
    void setGridViewAdapter(MultiPbPhotoWallGridAdapter multiPbPhotoWallGridAdapter);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(int tag);
    View gridViewFindViewWithTag(int tag);
    void setVideoSelectNumText(int selectNum);
    void changeMultiPbMode(OperationMode operationMode);
    void setListViewSelection(int position);
    void setGridViewSelection(int position);
    void setNoContentTxvVisibility(int visibility);
}
