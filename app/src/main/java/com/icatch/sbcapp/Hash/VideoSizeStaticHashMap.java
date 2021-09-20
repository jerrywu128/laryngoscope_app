package com.icatch.sbcapp.Hash;

import com.icatch.sbcapp.Beans.ItemInfo;

import java.util.HashMap;

/**
 * Created by b.jiang on 2017/3/16.
 */

public class VideoSizeStaticHashMap {
    public static HashMap<String, ItemInfo> videoSizeMap = new HashMap<String, ItemInfo>();
    public static VideoSizeStaticHashMap videoSizeStaticHashMap;

    public static VideoSizeStaticHashMap getInstance() {
        if (videoSizeStaticHashMap == null) {
            videoSizeStaticHashMap = new VideoSizeStaticHashMap();
        }
        return videoSizeStaticHashMap;
    }

    public void initVideoSizeHashMap() {
        //BSP-1164 add 6k15
        videoSizeMap.put("5760x3240 15", new ItemInfo("5760x3240 15fps", "6K15", 0));
        videoSizeMap.put("5760x3240 12", new ItemInfo("5760x3240 12fps", "6K12", 0));
        videoSizeMap.put("5760x2880 15", new ItemInfo("5760x2880 15fps", "6K15", 0));
        videoSizeMap.put("5760x2880 12", new ItemInfo("5760x2880 12fps", "6K12", 0));

        videoSizeMap.put("3840x2160 60", new ItemInfo("3840x2160 60fps", "4K60", 0));
        videoSizeMap.put("3840x2160 50", new ItemInfo("3840x2160 50fps", "4K50", 0));
        videoSizeMap.put("3840x2160 30", new ItemInfo("3840x2160 30fps", "4K30", 0));
        videoSizeMap.put("3840x2160 25", new ItemInfo("3840x2160 25fps", "4K25", 0));
        videoSizeMap.put("3840x2160 24", new ItemInfo("3840x2160 24fps", "4K24", 0));
        videoSizeMap.put("3840x2160 15", new ItemInfo("3840x2160 15fps", "4K15", 0));
        videoSizeMap.put("3840x2160 10", new ItemInfo("3840x2160 10fps", "4K10", 0));

        videoSizeMap.put("3840x1920 15", new ItemInfo("3840x1920 15fps", "4K15", 0));
        videoSizeMap.put("3840x1920 30", new ItemInfo("3840x1920 30fps", "4K30", 0));

        videoSizeMap.put("2704x1524 60", new ItemInfo("2704x1524 60fps", "2.7K60", 0));
        videoSizeMap.put("2704x1524 50", new ItemInfo("2704x1524 50fps", "2.7K50", 0));
        videoSizeMap.put("2704x1524 30", new ItemInfo("2704x1524 30fps", "2.7K30", 0));
        videoSizeMap.put("2704x1524 25", new ItemInfo("2704x1524 25fps", "2.7K25", 0));
        videoSizeMap.put("2704x1400 25", new ItemInfo("2704x1400 25fps", "2.7K25", 0));
        videoSizeMap.put("2704x1524 24", new ItemInfo("2704x1524 24fps", "2.7K24", 0));
        videoSizeMap.put("2704x1524 15", new ItemInfo("2704x1524 15fps", "2.7K15", 0));

        videoSizeMap.put("2704x1520 60", new ItemInfo("2704x1520 60fps", "2.7K60", 0));
        videoSizeMap.put("2704x1520 50", new ItemInfo("2704x1520 50fps", "2.7K50", 0));
        videoSizeMap.put("2704x1520 30", new ItemInfo("2704x1520 30fps", "2.7K30", 0));
        videoSizeMap.put("2704x1520 25", new ItemInfo("2704x1520 25fps", "2.7K25", 0));
        videoSizeMap.put("2704x1520 24", new ItemInfo("2704x1520 24fps", "2.7K24", 0));
        videoSizeMap.put("2704x1520 15", new ItemInfo("2704x1520 15fps", "2.7K15", 0));

        //BSP-1164 begin add
        videoSizeMap.put("2720x1520 60", new ItemInfo("2720x1520 60fps", "2.7K60", 0));
        videoSizeMap.put("2720x1520 50", new ItemInfo("2720x1520 50fps", "2.7K50", 0));
        videoSizeMap.put("2720x1520 30", new ItemInfo("2720x1520 30fps", "2.7K30", 0));
        videoSizeMap.put("2720x1520 25", new ItemInfo("2720x1520 25fps", "2.7K25", 0));
        videoSizeMap.put("2720x1520 24", new ItemInfo("2720x1520 24fps", "2.7K24", 0));
        videoSizeMap.put("2720x1520 15", new ItemInfo("2720x1520 15fps", "2.7K15", 0));
        //BSP-1164 end add

        videoSizeMap.put("2560x1280 30", new ItemInfo("2560x1280 30fps", "1280P30", 0));
        videoSizeMap.put("2560x1280 60", new ItemInfo("2560x1280 60fps", "1280P60", 0));

        videoSizeMap.put("2560x1440 30", new ItemInfo("2560x1440 30fps", "1440P30", 0));

        videoSizeMap.put("1920x1440 30", new ItemInfo("1920x1440 30fps", "1440P30", 0));
        videoSizeMap.put("1920x1440 25", new ItemInfo("1920x1440 25fps", "1440P25", 0));
        videoSizeMap.put("1920x1440 24", new ItemInfo("1920x1440 24fps", "1440P24", 0));

        videoSizeMap.put("1920x1080 120", new ItemInfo("1920x1080 120fps", "FHD120", 0));//20171031 add
        videoSizeMap.put("1920x1080 60", new ItemInfo("1920x1080 60fps", "FHD60", 0));
        videoSizeMap.put("1920x1080 50", new ItemInfo("1920x1080 50fps", "FHD50", 0));
        videoSizeMap.put("1920x1080 48", new ItemInfo("1920x1080 48fps", "FHD48", 0));
        videoSizeMap.put("1920x1080 30", new ItemInfo("1920x1080 30fps", "FHD30", 0));
        videoSizeMap.put("1920x1080 25", new ItemInfo("1920x1080 25fps", "FHD25", 0));
        videoSizeMap.put("1920x1080 24", new ItemInfo("1920x1080 24fps", "FHD24", 0));

        videoSizeMap.put("1920x960 30", new ItemInfo("1920x960 30fps", "960P30", 0));
        videoSizeMap.put("1440x960 30", new ItemInfo("1440x960 30fps", "960P30", 0));

        videoSizeMap.put("1280x960 120", new ItemInfo("1280x960 120fps", "960P", 0));
        videoSizeMap.put("1280x960 60", new ItemInfo("1280x960 60fps", "960P", 0));
        videoSizeMap.put("1280x960 30", new ItemInfo("1280x960 30fps", "960P", 0));

        videoSizeMap.put("1280x720 240", new ItemInfo("1280x720 240fps", "HD240", 0));//20171031 add
        videoSizeMap.put("1280x720 120", new ItemInfo("1280x720 120fps", "HD120", 0));
        videoSizeMap.put("1280x720 60", new ItemInfo("1280x720 60fps", "HD60", 0));
        videoSizeMap.put("1280x720 50", new ItemInfo("1280x720 50fps", "HD50", 0));
        videoSizeMap.put("1280x720 30", new ItemInfo("1280x720 30fps", "HD30", 0));
        videoSizeMap.put("1280x720 25", new ItemInfo("1280x720 25fps", "HD25", 0));
        videoSizeMap.put("1280x720 24", new ItemInfo("1280x720 24fps", "HD24", 0));
        videoSizeMap.put("1280x720 15", new ItemInfo("1280x720 15fps", "HD15", 0));

        videoSizeMap.put("1280x640 15", new ItemInfo("1280x640 15fps", "640P15", 0));

        videoSizeMap.put("1152x648 120", new ItemInfo("1152x648 120fps", "640P120", 0));
        videoSizeMap.put("480x640 120", new ItemInfo("480x640 120fps", "640P120", 0));

        videoSizeMap.put("848x480 240", new ItemInfo("848x480 240fps", "WVGA", 0));
        videoSizeMap.put("640x480 240", new ItemInfo("640x480 240fps", "VGA240", 0));
        videoSizeMap.put("640x480 120", new ItemInfo("640x480 120fps", "VGA120", 0));
        videoSizeMap.put("640x480 60", new ItemInfo("640x480 60fps", "VGA60", 0));

        videoSizeMap.put("640x360 1000", new ItemInfo("640x360 1000fps", "VGA1000", 0));//20171031 add
        videoSizeMap.put("640x360 240", new ItemInfo("640x360 240fps", "VGA240", 0));
        videoSizeMap.put("640x360 120", new ItemInfo("640x360 120fps", "VGA120", 0));

        videoSizeMap.put("240x320 240", new ItemInfo("240x320 240fps", "360P240", 0));
    }
}
