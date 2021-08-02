package com.fengjiaxing.xiaobudian;

/**
 * 常量池
 */
public class ConstantPool {

    /**
     * 域名
     */
    public static final String DOMAIN = "http://gochiusa.top:3000";

    /**
     * 广播，开始与暂停
     */
    public static final String SAP = "START_AND_PAUSE";

    /**
     * 广播，上一首
     */
    public static final String STP = "SEEK_TO_PREVIOUS";

    /**
     * 广播，下一首
     */
    public static final String STN = "SEEK_TO_NEXT";

    /**
     * 广播，自动下一首
     */
    public static final String AN = "AUTO_NEXT";

    /**
     * 广播，更换歌曲（不只是上一首或下一首）,需要附带歌曲在播放列表中的索引
     */
    public static final String CM = "CHANGE_MUSIC";

    /**
     * 广播，更改播放模式为单曲循环
     */
    public static final String L = "LOOP";

    /**
     * 广播，更改播放模式为列表循环
     */
    public static final String LL = "LIST_LOOP";

    /**
     * 广播，更改播放模式为随机播放
     */
    public static final String RP = "RANDOM_PLAY";

    /**
     * 广播，更新播放器状态
     */
    public static final String LS = "LOAD_SUCCESS";

    /**
     * 广播，歌曲没有版权
     */
    public static final String NC = "NO_COPYRIGHT";

}
