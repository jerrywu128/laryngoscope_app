package com.honestmc.laryngoscopeapp.BaseItems;

import com.honestmc.laryngoscopeapp.Tools.ConvertTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by b.jiang on 2015/12/18.
 */
public class LocalPbItemInfo {
    public File file;

    public int section;
    public boolean isItemChecked;

    public LocalPbItemInfo(File file, int section) {
        super();
        this.file = file;
        this.section =section;
        this.isItemChecked = false;
    }
    public LocalPbItemInfo(File file) {
        super();
        this.file = file;
        this.isItemChecked = false;
    }

    public void setSection(int section){
        this.section = section;
    }

    public String getFilePath(){
        return file.getPath();
    }

    public String getFileDate(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public String getFileSize(){
        //JIRA ICOM-3673 Begin modify by b.jiang 20160908
        long size = file.length();
        //JIRA ICOM-3673 End modify by b.jiang 20160908
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public String getFileName(){
        return file.getName();
    }
    public String getFileDateMMSS(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    //读取文件创建时间
    public void getCreateTime() {
        String filePath = file.getPath();
        String strTime = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(".txt")) {
                    strTime = line.substring(0, 17);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("创建时间    " + strTime);
    }

}
