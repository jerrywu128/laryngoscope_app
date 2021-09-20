package com.icatch.sbcapp.View.Interface;

import android.view.View;
import android.widget.AbsListView;

import com.icatch.sbcapp.Adapter.LocalPhotoWallGridAdapter;
import com.icatch.sbcapp.Adapter.LocalPhotoWallListAdapter;

/**
 * Created by b.jiang on 2015/12/24.
 */
public interface LocalPhotoWallView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(LocalPhotoWallListAdapter localPhotoWallListAdapter);
    void setGridViewAdapter(LocalPhotoWallGridAdapter localPhotoWallGridAdapter);
    void setListViewSelection(int position);
    void setGridViewSelection(int position);
    void setListViewOnScrollListener(AbsListView.OnScrollListener onScrollListener);
    void setGridViewOnScrollListener(AbsListView.OnScrollListener onScrollListener);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(String tag);
    View gridViewFindViewWithTag(String tag);
    void setMenuPhotoWallTypeIcon(int iconRes);
}
