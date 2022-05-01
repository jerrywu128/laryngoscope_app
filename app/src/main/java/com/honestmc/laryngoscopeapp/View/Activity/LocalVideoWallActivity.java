package com.honestmc.laryngoscopeapp.View.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.honestmc.laryngoscopeapp.Log.AppLog;
import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallGridAdapter;
import com.honestmc.laryngoscopeapp.Adapter.LocalVideoWallListAdapter;
import com.honestmc.laryngoscopeapp.GlobalApp.GlobalInfo;
import com.honestmc.laryngoscopeapp.Presenter.LocalVideoWallPresenter;
import com.honestmc.laryngoscopeapp.R;
import com.honestmc.laryngoscopeapp.View.Interface.LocalVideoWallView;

public class LocalVideoWallActivity extends BaseActivity implements LocalVideoWallView {
    private String TAG = "LocalVideoWallActivity";
    private LocalVideoWallPresenter presenter;
    //StickyGridHeadersGridView gridView;
    ListView listView;
    TextView headerView;
    FrameLayout listLayout;
    //private MenuItem menuVideoWallType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GlobalInfo.getInstance().setCurrentApp(LocalVideoWallActivity.this);
       // gridView = (StickyGridHeadersGridView) findViewById(R.id.local_video_wall_grid_view);
        listView = (ListView) findViewById(R.id.local_video_wall_list_view);
        headerView = (TextView) findViewById(R.id.photo_wall_header);
        listLayout = (FrameLayout) findViewById(R.id.local_video_wall_list_layout);

        presenter = new LocalVideoWallPresenter(LocalVideoWallActivity.this);
        presenter.setView(this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.redirectToAnotherActivity(LocalVideoWallActivity.this, LocalVideoPbActivity.class, position);
            }
        });

        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.redirectToAnotherActivity(LocalVideoWallActivity.this, LocalVideoPbActivity.class, position);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalInfo.getInstance().setCurrentApp(LocalVideoWallActivity.this);
        presenter.loadLocalVideoWall();
        presenter.submitAppInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLog.d(TAG,"onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.loadLocalVideoWall();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
//                presenter.destroyCamera();
                 presenter.clearResource();
                 presenter.removeActivity();
                 presenter.destroyCamera();
                finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_video_wall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == android.R.id.home) {
            presenter.clearResource();
            presenter.removeActivity();
            presenter.destroyCamera();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onStop() {
        super.onStop();
//        presenter.isAppBackground();
    }

}


