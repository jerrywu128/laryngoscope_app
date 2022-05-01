package com.honestmc.laryngoscopeapp.Listener;

import com.honestmc.laryngoscopeapp.Mode.OperationMode;

/**
 * Created by b.jiang on 2016/1/19.
 */
public interface OnStatusChangedListener {
    public void onEnterEditMode(OperationMode curOperationMode);
    public void onSelectedItemsCountChanged(int SelectedNum);
}
