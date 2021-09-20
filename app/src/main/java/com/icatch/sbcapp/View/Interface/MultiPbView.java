package com.icatch.sbcapp.View.Interface;

//import android.support.v4.app.FragmentPagerAdapter;

import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by b.jiang on 2016/1/6.
 */
public interface MultiPbView {
    void setViewPageAdapter(FragmentPagerAdapter adapter);
    void setViewPageCurrentItem(int item);
    void setMenuPhotoWallTypeIcon(int iconRes);
    void setViewPagerScanScroll(boolean isCanScroll);
    void setSelectNumText(String text);
    void setSelectBtnVisibility(int visibility);
    void setSelectBtnIcon(int icon);
    void setSelectNumTextVisibility(int visibility);
    void setTabLayoutClickable(boolean value);
    void setEditLayoutVisibiliy(int visibiliy);
}
