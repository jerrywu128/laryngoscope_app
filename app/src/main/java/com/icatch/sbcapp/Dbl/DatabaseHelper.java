package com.icatch.sbcapp.Dbl;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;

import com.icatch.sbcapp.Beans.CameraSlot;
import com.icatch.sbcapp.Beans.CameraTable;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.SystemInfo.MWifiManager;
import com.icatch.sbcapp.Tools.BitmapTools;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.jiang on 2017/3/31.
 */

public class DatabaseHelper {
    private static String TAG = "DatabaseHelper";


    public static ArrayList<CameraSlot> readCamera(Context context) {

        ArrayList<CameraSlot> camSlotList = new ArrayList<CameraSlot>();
        List<CameraTable> cameraDBList = DataSupport.findAll(CameraTable.class);
        AppLog.d(TAG, "readCamera cameraDBList=" + cameraDBList);
        if (cameraDBList != null) {
            AppLog.d(TAG, "readCamera cameraDBList size=" + cameraDBList.size());
        }
        if (cameraDBList == null || cameraDBList.size() == 0) {
            CameraTable cameraTable = new CameraTable(false, 0, null, null);
            cameraTable.save();
            CameraTable cameraTable1 = new CameraTable(false, 1, null, null);
            cameraTable1.save();
            CameraTable cameraTable2 = new CameraTable(false, 2, null, null);
            cameraTable2.save();
            cameraDBList = DataSupport.findAll(CameraTable.class);
        }
        String wifiSsid = MWifiManager.getSsid(context);
        for (int ii = 0; ii < cameraDBList.size(); ii++) {
            CameraTable cameraTable = cameraDBList.get(ii);
            boolean isReady = false;
            if (wifiSsid != null && wifiSsid.equals(cameraTable.getCameraName())) {
                isReady = true;
            }
//            AppLog.d(TAG, "readCamera cameraTable.getId();=" + cameraTable.getId());
            camSlotList.add(new CameraSlot(cameraTable.getId(), cameraTable.slotPosition, cameraTable.isOccupied, cameraTable.getCameraName(), cameraTable.getCameraPhoto(), isReady));
        }
        AppLog.d(TAG, "readCamera camSlotList=" + camSlotList);
        return camSlotList;
    }

    public static void deleteCamera(int id, int position) {
        //ICOM-4272
        AppLog.d(TAG, "deleteCamera id=" + id);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cameraName", "");
        contentValues.put("isOccupied", false);
        DataSupport.update(CameraTable.class, contentValues, id);

//        CameraTable cameraTable = findTable(position);
//        if(cameraTable != null){
////            cameraTable.setCameraName("");
////            cameraTable.isOccupied = false;
////            cameraTable.updateAll(" slotPosition= ?", String.valueOf(position));
//            cameraTable.delete();
//        }
//
//        CameraTable cameraTable2 = new CameraTable(false,position,null,null);
//        cameraTable2.save();
    }

    public static void updateCameraName(int id, String cameraName) {
        AppLog.d(TAG, "updateCameraName position=" + id);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cameraName", cameraName);
        contentValues.put("isOccupied", true);
        DataSupport.update(CameraTable.class, contentValues, id);

//        CameraTable cameraTable = findTable(position);
//        if(cameraTable != null){
//            cameraTable.setCameraName(cameraName);
//            cameraTable.isOccupied = true;
//            cameraTable.updateAll("slotPosition = ?", String.valueOf(position));
//        }
//        CameraTable cameraTable = new CameraTable();
//        cameraTable.setCameraName(cameraName);
//        cameraTable.setOccupied(true);
//        cameraTable.updateAll(" slotPosition= ?", String.valueOf(position));
    }

    public static void updateCameraPhoto(int id, Bitmap bitmap) {

        AppLog.d(TAG, "updateCameraPhoto position=" + id);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cameraPhoto", BitmapTools.bitmapToByteArray(bitmap));
        contentValues.put("isOccupied", true);
        DataSupport.update(CameraTable.class, contentValues, id);
//        CameraTable cameraTable = new CameraTable();
//        cameraTable.setCameraPhoto(BitmapTools.bitmapToByteArray(bitmap));
//        cameraTable.setOccupied(true);
//        cameraTable.updateAll(" slotPosition= ?", String.valueOf(position));

//        CameraTable cameraTable = findTable(position);
//        if(cameraTable != null){
//            byte[] temp = BitmapTools.bitmapToByteArray(bitmap);
//            if(temp != null){
//                cameraTable.setCameraPhoto(temp);
//                cameraTable.isOccupied = true;
//                cameraTable.updateAll("slotPosition = ?", String.valueOf(position));
//            }
//
//        }
    }

    public static void updateCameraPhoto(int id, byte[] value) {
        AppLog.d(TAG, "updateCameraPhoto position=" + id);
        ContentValues contentValues = new ContentValues();
        contentValues.put("cameraPhoto", value);
        contentValues.put("isOccupied", true);
        DataSupport.update(CameraTable.class, contentValues, id);
    }

    public static CameraTable findTable(int position){
        List<CameraTable> cameraDBList = DataSupport.findAll(CameraTable.class);
        if(cameraDBList == null || cameraDBList.size() <= 0 ){
            return null;
        }
        CameraTable cameraTable = null;
        for (int ii = 0; ii<cameraDBList.size();ii++){
            CameraTable tempTable = cameraDBList.get(ii);
            if(tempTable.getSlotPosition() == position){
                cameraTable =  tempTable;
                break;
            }
        }
        return cameraTable;
    }
}
