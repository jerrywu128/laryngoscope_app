package com.honestmc.laryngoscopeapp.View.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.honestmc.laryngoscopeapp.Adapter.LocalPhotoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.LocalPhotoWallListAdapter;
import com.honestmc.laryngoscopeapp.Listener.OnStatusChangedListener;
import com.honestmc.laryngoscopeapp.Presenter.LocalPhotoFragmentPresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Activity.LocalPhotoPbActivity;
import com.honestmc.laryngoscopeapp.View.Interface.LocalPhotoFragmentView;

public class LocalPhotoFragment extends Fragment implements LocalPhotoFragmentView {
    String TAG = "LocalPhotoFragment";

    ListView localPhotoListView;
    TextView localPhotoHeaderView;
    FrameLayout localPhotoWallListLayout;
    private LocalPhotoFragmentPresenter presenter;
    private OnStatusChangedListener modeChangedListener;

    public LocalPhotoFragment(){
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_local_photo_wall, container, false);
        //localPhotoGridView = (StickyGridHeadersGridView) findViewById(R.id.local_photo_wall_grid_view);
        localPhotoListView = (ListView) view.findViewById(R.id.local_photo_wall_list_view);
        localPhotoHeaderView = (TextView) view.findViewById(R.id.photo_wall_header);
        localPhotoWallListLayout = (FrameLayout) view.findViewById(R.id.local_photo_wall_list_layout);
        presenter = new LocalPhotoFragmentPresenter(getActivity());
        presenter.setView(this);
        try {
            presenter.decodePhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        localPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.redirectToAnotherActivity(getActivity(), LocalPhotoPbActivity.class, position);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.loadLocalPhotoWall();
        presenter.submitAppInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.isAppBackground();
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener){
        this.modeChangedListener = modeChangedListener;
    }


    @Override
    public void setListViewVisibility(int visibility) {
        localPhotoWallListLayout.setVisibility(visibility);
    }

    @Override
    public void setGridViewVisibility(int visibility) {
        //  localPhotoGridView.setVisibility(visibility);
    }

    @Override
    public void setListViewAdapter(LocalPhotoWallListAdapter localPhotoWallListAdapter) {
        localPhotoListView.setAdapter(localPhotoWallListAdapter);
    }

    @Override
    public void setGridViewAdapter(LocalPhotoWallGridAdapter localLocalPhotoWallGridAdapter) {
        // localPhotoGridView.setAdapter(localLocalPhotoWallGridAdapter);
    }

    @Override
    public void setListViewSelection(int position) {
        localPhotoListView.setSelection(position);
    }

    @Override
    public void setGridViewSelection(int position) {
        // localPhotoGridView.setSelection(position);
    }

    @Override
    public void setListViewOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        localPhotoListView.setOnScrollListener(onScrollListener);
    }

    @Override
    public void setGridViewOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        // localPhotoGridView.setOnScrollListener(onScrollListener);
    }

    @Override
    public void setListViewHeaderText(String headerText) {
        localPhotoHeaderView.setText(headerText);
    }

    @Override
    public View listViewFindViewWithTag(String tag) {
        return localPhotoListView.findViewWithTag(tag);
    }

    @Override
    public View gridViewFindViewWithTag(String tag) {
        // return localPhotoGridView.findViewWithTag(tag);
        return null;
    }

    @Override
    public void setMenuPhotoWallTypeIcon(int id) {
        // menuPhotoWallType.setIcon(id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.loadLocalPhotoWall();
    }

}
