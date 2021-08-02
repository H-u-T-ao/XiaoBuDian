package com.fengjiaxing.xiaobudian.activity;

import com.fengjiaxing.xiaobudian.entity.MusicInfo;

import java.util.ArrayList;

/**
 * 搜索活动的model接口
 *
 * @see com.fengjiaxing.xiaobudian.network.Search
 * */
public interface SearchModel {

    /**
     * 根据关键词，页码搜索歌曲并将歌曲信息添加到传入的容器中
     *
     * @param searchKey 搜索的关键词
     * @param page 搜索的页码
     * @param oldSearchResults 装载搜索结果的容器
     * */
    void search(String searchKey, int page, ArrayList<MusicInfo> oldSearchResults);

}
