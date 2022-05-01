package com.honestmc.laryngoscopeapp.View.Interface;

import android.graphics.Bitmap;
import android.view.View;

import com.honestmc.laryngoscopeapp.Adapter.MultiPbPhotoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.MultiPbPhotoWallListAdapter;
import com.honestmc.laryngoscopeapp.Mode.OperationMode;

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
