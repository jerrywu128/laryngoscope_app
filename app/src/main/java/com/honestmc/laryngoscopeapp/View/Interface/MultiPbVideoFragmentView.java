package com.honestmc.laryngoscopeapp.View.Interface;

import android.view.View;

import com.honestmc.laryngoscopeapp.Adapter.MultiPbPhotoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.MultiPbPhotoWallListAdapter;
import com.honestmc.laryngoscopeapp.Mode.OperationMode;

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
