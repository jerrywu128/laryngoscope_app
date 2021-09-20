package com.icatch.sbcapp.View.Activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.icatch.sbcapp.ExtendComponent.HackyViewPager;
import com.icatch.sbcapp.GlobalApp.GlobalInfo;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.LocalPhotoPbPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.View.Interface.LocalPhotoPbView;

import uk.co.senab.photoview.PhotoView;

public class LocalPhotoPbActivity extends BaseActivity implements LocalPhotoPbView {
    private static final String TAG = LocalPhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private ImageButton shareBtn;
    private ImageButton deleteBtn;
    private TextView indexInfoTxv;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private LocalPhotoPbPresenter presenter;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_photo_pb);

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        shareBtn = (ImageButton) findViewById(R.id.photo_pb_share);
        deleteBtn = (ImageButton) findViewById(R.id.photo_pb_delete);
        topBar = (RelativeLayout) findViewById(R.id.pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.pb_bottom_layout);
        back = (ImageButton) findViewById(R.id.pb_back);

        presenter = new LocalPhotoPbPresenter(this);
        presenter.setView(this);
        viewPager.setPageMargin(30);

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"viewPager.setOnClickListener");
                presenter.showBar();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadImage();
        presenter.submitAppInfo();
//        presenter.test();
    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if(adapter != null){
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.reloadBitmap();
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setIndexInfoTxv(String indexInfo) {
        indexInfoTxv.setText(indexInfo);
    }

    @Override
    public void setViewPagerCurrentItem(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPager.addOnPageChangeListener(listener);
    }

    @Override
    public int getViewPagerCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void updateViewPagerBitmap(int position, Bitmap bitmap) {
        View view = viewPager.getChildAt(position);
        AppLog.d(TAG,"updateViewPagerBitmap position=" + position + " view=" + view);
        if(view == null){
            return;
        }
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
        if(photoView != null){
            photoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getTopBarVisibility() {
        return topBar.getVisibility();
    }
}
