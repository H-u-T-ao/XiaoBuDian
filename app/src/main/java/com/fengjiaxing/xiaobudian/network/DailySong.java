package com.fengjiaxing.xiaobudian.network;

import com.fengjiaxing.xiaobudian.ConstantPool;
import com.fengjiaxing.xiaobudian.activity.DailySongsModel;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.util.FormatText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 每日推荐板块的model实现类
 * */
public class DailySong implements DailySongsModel {

    /**
     * 获取每日推荐列表
     *
     * @param dailySongsList 装载搜索结果的容器
     * */
    @Override
    public void get(ArrayList<MusicInfo> dailySongsList) {

        String urlString = ConstantPool.DOMAIN + "/recommend/songs";

        String data = JSONGetter.getJSON(urlString, null);

        if (data == null) return;

        JSONArray songs = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject result = jsonObject.getJSONObject("data");
            songs = result.getJSONArray("dailySongs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (songs == null || songs.length() == 0) return;

        FormatText.formatSongsInfo(songs, dailySongsList);
    }

}
