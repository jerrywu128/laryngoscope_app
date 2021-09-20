package com.icatch.sbcapp.Function;

import com.icatch.sbcapp.ExtendComponent.ZoomView;
import com.icatch.sbcapp.Presenter.PreviewPresenter;
import com.icatch.sbcapp.SdkApi.CameraAction;
import com.icatch.sbcapp.SdkApi.CameraProperties;

/**
 * Created by zhang yanhu C001012 on 2015/12/29 09:42.
 */
public class ZoomInOut{

    private  int lastZoomRate = 10;
    private static ZoomInOut zoomInOut;
    private ZoomCompletedListener zoomCompletedListener;


    public static synchronized ZoomInOut  getInstance(){
        if(zoomInOut == null){
            zoomInOut = new ZoomInOut();
        }
        return  zoomInOut;
    }

    private ZoomInOut() {
    }

    public void zoomIn(){
        if(lastZoomRate <= ZoomView.MAX_VALUE) {
            CameraAction.getInstance().zoomIn();
        }
        lastZoomRate = CameraProperties.getInstance().getCurrentZoomRatio();
        //zoomCompletedListener.onCompleted(lastZoomRate);
    }
    public void zoomOut(){
        if(lastZoomRate >= ZoomView.MIN_VALUE) {
            CameraAction.getInstance().zoomOut();
        }
        lastZoomRate = CameraProperties.getInstance().getCurrentZoomRatio();
       // zoomCompletedListener.onCompleted(lastZoomRate);
    }

    public void startZoomInOutThread(final PreviewPresenter presenter){
        new Thread(new Runnable() {
            @Override
            public void run() {
                zoom(presenter);
            }
        }).start();
    }


    private void zoom(PreviewPresenter presenter) {
        int maxZoomCount = 50;
        lastZoomRate = CameraProperties.getInstance().getCurrentZoomRatio();
        int curProgress = presenter.getZoomViewProgress();
        //ICOM-3435 Start Modify by b.jiang 20160713
        if(lastZoomRate > curProgress){//缩小处理
            while (lastZoomRate > (presenter.getZoomViewProgress()) && lastZoomRate > ZoomView.MIN_VALUE && maxZoomCount-- > 0) {
                CameraAction.getInstance().zoomOut();
                lastZoomRate = CameraProperties.getInstance().getCurrentZoomRatio();
            }
        }else {
            while (lastZoomRate < (presenter.getZoomViewProgress()) && lastZoomRate < ZoomView.MAX_VALUE && maxZoomCount-- > 0) {
                CameraAction.getInstance().zoomIn();
                lastZoomRate = CameraProperties.getInstance().getCurrentZoomRatio();
            }
        }
        //ICOM-3435 End Modify by b.jiang 20160713
        zoomCompletedListener.onCompleted(lastZoomRate);
    }

    public interface ZoomCompletedListener{
        void onCompleted(int currentZoomRate);
    };

    public void addZoomCompletedListener(ZoomCompletedListener zoomCompletedListener){
        this.zoomCompletedListener = zoomCompletedListener;
    }
}
