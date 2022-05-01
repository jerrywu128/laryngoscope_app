package com.honestmc.laryngoscopeapp.View.Interface;

import android.view.View;
import android.widget.AbsListView;

import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallListAdapter;

/**
 * Created by b.jiang on 2015/12/7.
 */
public interface LocalVideoWallView {
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


