package com.honestmc.laryngoscopeapp.BaseItems;

import com.icatch.wificam.customer.type.ICatchFile;

/**
 * Created by b.jiang on 2016/3/3.
 */
public class DownloadInfo {
    public ICatchFile file = null;
    public long fileSize = 0;
    public long curFileLength = 0;
    public int progress = 0;
    public boolean done = false;

    public DownloadInfo(ICatchFile file, long fileSize, long curFileLength, int progress, boolean done) {
        this.file = file;
        this.fileSize = fileSize;
        this.curFileLength = curFileLength;
        this.progress = progress;
        this.done = done;
    }
}
