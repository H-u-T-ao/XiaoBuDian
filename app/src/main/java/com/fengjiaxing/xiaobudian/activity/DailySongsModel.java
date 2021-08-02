package com.fengjiaxing.xiaobudian.activity;

import com.fengjiaxing.xiaobudian.entity.MusicInfo;

import java.util.ArrayList;

/**
 * 每日推荐活动的model接口
 *
 * @see com.fengjiaxing.xiaobudian.network.DailySong
 * */
public interface DailySongsModel {

    /**
     * 获取每日推荐列表
     *
     * @param dailySongsList 装载每日推荐音乐列表的容器
     * */
    void get(ArrayList<MusicInfo> dailySongsList);

}
