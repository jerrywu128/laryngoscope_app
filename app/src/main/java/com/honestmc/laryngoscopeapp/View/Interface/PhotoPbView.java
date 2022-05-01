package com.honestmc.laryngoscopeapp.View.Interface;

import android.graphics.Bitmap;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;

/**
 * Created by b.jiang on 2016/3/24.
 */
public interface PhotoPbView {
    void setViewPagerAdapter(PagerAdapter adapter);
    void setTopBarVisibility(int visibility);
    void setBottomBarVisibility(int visibility);
    void setIndexInfoTxv(String indexInfo);
    void setViewPagerCurrentItem(int position);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    int getViewPagerCurrentItem();
    void updateViewPagerBitmap(int position, Bitmap bitmap);
    int getTopBarVisibility();
}
