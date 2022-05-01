package com.honestmc.laryngoscopeapp.View.Interface;

import android.view.View;
import android.widget.AbsListView;

import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallListAdapter;

public interface LocalVideoFragmentView {
    void setListViewVisibility(int visibility);
    void setGridViewVisibility(int visibility);
    void setListViewAdapter(LocalVideoWallListAdapter localVideoWallListAdapter);
    void setGridViewAdapter(LocalVideoWallGridAdapter localVideoWallGridAdapter);
    void setListViewOnScrollListener(AbsListView.OnScrollListener onScrollListener);
    void setGridViewOnScrollListener(AbsListView.OnScrollListener onScrollListener);
    void setListViewHeaderText(String headerText);
    View listViewFindViewWithTag(String tag);
    View gridViewFindViewWithTag(String tag);
    void setMenuPreviewTypeIcon(int iconRes);
    int getGridViewNumColumns();
}
