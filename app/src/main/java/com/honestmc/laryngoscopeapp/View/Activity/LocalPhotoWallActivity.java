package com.honestmc.laryngoscopeapp.View.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
//import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.honestmc.laryngoscopeapp.Adapter.LocalPhotoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.LocalPhotoWallListAdapter;
import com.honestmc.laryngoscopeapp.Presenter.LocalPhotoWallPresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Interface.LocalPhotoWallView;

public class LocalPhotoWallActivity extends BaseActivity implements LocalPhotoWallView {
    String TAG = "LocalPhotoWallActivity";
    //StickyGridHeadersGridView localPhotoGridView;
    ListView localPhotoListView;
    TextView localPhotoHeaderView;
    FrameLayout localPhotoWallListLayout;
    //tMenuItem menuPhotoWallType;
    private LocalPhotoWallPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_photo_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlobalInfo.getInstance().setCurrentApp(LocalPhotoWallActivity.this);
        //localPhotoGridView = (StickyGridHeadersGridView) findViewById(R.id.local_photo_wall_grid_view);
        localPhotoListView = (ListView) findViewById(R.id.local_photo_wall_list_view);
        localPhotoHeaderView = (TextView) findViewById(R.id.photo_wall_header);
        localPhotoWallListLayout = (FrameLayout) findViewById(R.id.local_photo_wall_list_layout);
        presenter = new LocalPhotoWallPresenter(this);
        presenter.setView(this);
        localPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.redirectToAnotherActivity(LocalPhotoWallActivity.this, LocalPhotoPbActivity.class, position);
            }
        });

       /* localPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MyToast.show(LocalPhotoWallActivity.this,position+"");
                presenter.redirectToAnotherActivity(LocalPhotoWallActivity.this, LocalPhotoPbActivity.class, position);
            }
        });t*/

//        localPhotoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                presenter.listViewLoadThumbnails(scrollState);
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                presenter.listViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
//            }
//        });
//
//        localPhotoGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                presenter.gridViewLoadThumbnails(scrollState);
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                presenter.gridViewLoadOnceThumbnails(firstVisibleItem, visibleItemCount);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadLocalPhotoWall();
        presenter.submitAppInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_photo_wall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.menu_photo_wall_type) {
           // menuPhotoWallType = item;
           // presenter.changePreviewType();
        }  else*/ if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
