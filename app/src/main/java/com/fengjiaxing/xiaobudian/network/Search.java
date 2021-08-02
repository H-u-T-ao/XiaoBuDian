package com.fengjiaxing.xiaobudian.network;

import com.fengjiaxing.xiaobudian.ConstantPool;
import com.fengjiaxing.xiaobudian.activity.SearchModel;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.util.FormatText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 搜索板块的model实现类
 * */
public class Search implements SearchModel {

    /**
     * 根据关键词，页码搜索歌曲并将歌曲信息添加到传入的容器中
     *
     * @param searchKey 搜索的关键词
     * @param page 搜索的页码
     * @param searchResults 装载搜索结果的容器
     * */
    @Override
    public void search(String searchKey, int page, ArrayList<MusicInfo> searchResults) {

        int offset = page * 20;
        String urlString = ConstantPool.DOMAIN + "/cloudsearch";
        HashMap<String, String> p = new HashMap<>();
        p.put("keywords", searchKey);
        p.put("limit", "20");
        p.put("offset", Integer.toString(offset));

        String data = JSONGetter.getJSON(urlString, p);

        if(data == null) return;

        JSONArray songs = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject result = jsonObject.getJSONObject("result");
            songs = result.getJSONArray("songs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FormatText.formatSongsInfo(songs, searchResults);

    }

}
