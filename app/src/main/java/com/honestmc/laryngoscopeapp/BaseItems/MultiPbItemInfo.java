package com.honestmc.laryngoscopeapp.BaseItems;

import com.honestmc.laryngoscopeapp.Tools.ConvertTools;
import com.icatch.wificam.customer.type.ICatchFile;

/**
 * Created by b.jiang on 2016/1/23.
 */
public class MultiPbItemInfo {
    private String TAG = "MultiPbItemInfo";
    public ICatchFile iCatchFile;

    public int section;
    public boolean isItemChecked;

    public MultiPbItemInfo(ICatchFile file, int section) {
        super();
        this.iCatchFile = file;
        this.section =section;
        this.isItemChecked = false;
    }
    public MultiPbItemInfo(ICatchFile file) {
        super();
        this.iCatchFile = file;
        this.isItemChecked = false;
    }

    public void setSection(int section){
        this.section = section;
    }

    public String getFilePath(){
        return iCatchFile.getFilePath();
    }

    public int getFileHandle(){
        return iCatchFile.getFileHandle();
    }

    public String getFileDate(){
//        String time = iCatchFile.getFileDate();
//        int position = time.indexOf("T");
//        time = time.substring(0,position);
        String time = ConvertTools.getTimeByfileDate(iCatchFile.getFileDate());
        if(time == null){
            return "unknown";
        }
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String temp = format.format(new Date(time));
//        AppLog.d(TAG,"getFileDate temp=" + temp);
        return time;
    }

    public String getFileSize(){
        //JIRA ICOM-3673 Begin modify by b.jiang 20160908
//        int size = (int)iCatchFile.getFileSize();
        long size = iCatchFile.getFileSize();
        //JIRA ICOM-3673 End modify by b.jiang 20160908
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public long getFileSizeInteger(){
        //JIRA BUG ICOM-3511 modify by b.jiang 20160725
        //from return int --> return long
        long fileSize = iCatchFile.getFileSize();
        return  fileSize;
    }

    public String getFileDuration(){
        int durationSec = 0 ;
        int duration = iCatchFile.getFileDuration();
        if (duration > 0) {
            durationSec =  duration / 1000;
        }
        return  ConvertTools.secondsToMinuteOrHours(durationSec);
    }
    public String getFileName(){
        return iCatchFile.getFileName();
    }
    public String getFileDateMMSS(){
//        return iCatchFile.getFileDate();
        return dateFormatTransform(iCatchFile.getFileDate());
//        return ConvertTools.fileDateFormatConvert(iCatchFile.getFileDate());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return format.format(new Date(time));
    }

    public String  dateFormatTransform(String value){
        if(value == null){
            return "";
        }

        String date = "";
        String time = "";
        String yy = "";
        String MM = "";
        String dd = "";
        String hh = "";
        String mm = "";
        String ss = "";
        int position = value.indexOf("T");
        date = value.substring(0,position);//20161021
        time = value.substring(position +1);
        yy = date.substring(0,4);
        MM = date.substring(4,6);
        dd = date.substring(6,8);
        hh = time.substring(0,2);
        mm = time.substring(2,4);
        ss = time.substring(4,6);
        date = yy+ "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss;
        return date;
    }
}
