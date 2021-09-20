package com.icatch.sbcapp.Listener;

import com.icatch.sbcapp.Mode.OperationMode;

/**
 * Created by b.jiang on 2016/1/19.
 */
public interface OnStatusChangedListener {
    public void onEnterEditMode(OperationMode curOperationMode);
    public void onSelectedItemsCountChanged(int SelectedNum);
}
