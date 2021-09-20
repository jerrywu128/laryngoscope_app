package com.icatch.sbcapp.View.Activity;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.icatch.sbcapp.ExtendComponent.HackyViewPager;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.Presenter.PhotoPbPresenter;
import com.icatch.sbcapp.R;
import com.icatch.sbcapp.View.Interface.PhotoPbView;

import uk.co.senab.photoview.PhotoView;

public class PhotoPbActivity extends BaseActivity implements PhotoPbView {
    private static final String TAG = PhotoPbActivity.class.getSimpleName();
    private HackyViewPager viewPager;
    private ImageButton downloadBtn;
    private ImageButton deleteBtn;
    private TextView indexInfoTxv;
    private RelativeLayout topBar;
    private LinearLayout bottomBar;
    private PhotoPbPresenter presenter;
    private ImageButton back;


    // private String photoPath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH + "Jellyfish.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pb);

        viewPager = (HackyViewPager) findViewById(R.id.viewpager);
        indexInfoTxv = (TextView) findViewById(R.id.pb_index_info);
        downloadBtn = (ImageButton) findViewById(R.id.photo_pb_download);
        deleteBtn = (ImageButton) findViewById(R.id.photo_pb_delete);
        topBar = (RelativeLayout) findViewById(R.id.pb_top_layout);
        bottomBar = (LinearLayout) findViewById(R.id.pb_bottom_layout);
        back = (ImageButton) findViewById(R.id.pb_back);

        presenter = new PhotoPbPresenter(this);
        presenter.setView(this);
        viewPager.setPageMargin(30);


//        viewPager.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float v, float v1) {
//                AppLog.d(TAG, "viewPager.setOnPhotoTapListener");
//                presenter.showBar(topBar.getVisibility() == View.VISIBLE ? true : false);
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//
//            }
//        });

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"viewPager.setOnClickListener");
                presenter.showBar();
            }
        });




        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppLog.d(TAG, "onKeyDown");
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
//                presenter.release();
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
        if(adapter != null){
            viewPager.setAdapter(adapter);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
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
    public void setIndexInfoTxv(String photoName) {
        indexInfoTxv.setText(photoName);
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

