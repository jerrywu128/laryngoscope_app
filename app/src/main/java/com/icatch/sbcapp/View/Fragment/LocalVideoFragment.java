package com.icatch.sbcapp.View.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.icatch.sbcapp.Adapter.LocalVideoWallGridAdapter;
import com.icatch.sbcapp.Adapter.LocalVideoWallListAdapter;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Listener.OnStatusChangedListener;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.LocalPhotoFragmentPresenter;
import com.icatch.sbcapp.Presenter.LocalVideoFragmentPresenter;
import com.icatch.sbcapp.Presenter.LocalVideoWallPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.View.Activity.LocalPhotoPbActivity;
import com.icatch.sbcapp.View.Activity.LocalVideoPbActivity;
import com.icatch.sbcapp.View.Activity.LocalVideoWallActivity;
import com.icatch.sbcapp.View.Interface.LocalPhotoFragmentView;
import com.icatch.sbcapp.View.Interface.LocalVideoFragmentView;

public class LocalVideoFragment extends Fragment implements LocalVideoFragmentView {
    String TAG = "LocalVideoFragment";
    private LocalVideoFragmentPresenter presenter;
    //StickyGridHeadersGridView gridView;
    ListView listView;
    TextView headerView;
    FrameLayout listLayout;
    //private MenuItem menuVideoWallType;
    private OnStatusChangedListener modeChangedListener;
    public LocalVideoFragment(){
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_local_video_wall, container, false);
        listView = (ListView) view.findViewById(R.id.local_video_wall_list_view);
        headerView = (TextView) view.findViewById(R.id.photo_wall_header);
        listLayout = (FrameLayout) view.findViewById(R.id.local_video_wall_list_layout);

        presenter = new LocalVideoFragmentPresenter(getActivity());
        presenter.setView(this);

        try {
            presenter.decodeVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!presenter.check_password()){
                    //check password
                }else {
                    presenter.redirectToAnotherActivity(getActivity(), LocalVideoPbActivity.class, position);
                }
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.loadLocalVideoWall();
        presenter.submitAppInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.d(TAG,"onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.loadLocalVideoWall();
    }



    @Override
    public void setListViewVisibility(int visibility) {
        listLayout.setVisibility(visibility);
    }

    @Override
    public void setGridViewVisibility(int visibility) {
        // gridView.setVisibility(visibility);
    }

    @Override
    public void setListViewAdapter(LocalVideoWallListAdapter localVideoWallListAdapter) {
        listView.setAdapter(localVideoWallListAdapter);
    }

    @Override
    public void setGridViewAdapter(LocalVideoWallGridAdapter localVideoWallGridAdapter) {
        // gridView.setAdapter(localVideoWallGridAdapter);
    }

    @Override
    public void setListViewOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        listView.setOnScrollListener(onScrollListener);
    }

    @Override
    public void setGridViewOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        // gridView.setOnScrollListener(onScrollListener);
    }

    @Override
    public void setListViewHeaderText(String headerText) {
        headerView.setText(headerText);
    }

    @Override
    public View listViewFindViewWithTag(String tag) {
        return listView.findViewWithTag(tag);
    }

    @Override
    public View gridViewFindViewWithTag(String tag) {
        // return gridView.findViewWithTag(tag);
        return null;
    }

    @Override
    public void setMenuPreviewTypeIcon(int iconRes) {
        //  menuVideoWallType.setIcon(iconRes);
    }

    @Override
    public int getGridViewNumColumns() {
       /* int num = gridView.getNumColumns();
        AppLog.d(TAG,"getGridViewNumColumns num=" + num);*/
        return 0;
    }

    @Override
    public void onStop() {
        super.onStop();
//        presenter.isAppBackground();
    }

    public void setOperationListener(OnStatusChangedListener modeChangedListener){
        this.modeChangedListener = modeChangedListener;
    }



}
