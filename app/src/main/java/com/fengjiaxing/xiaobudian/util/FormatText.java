package com.fengjiaxing.xiaobudian.util;

import android.annotation.SuppressLint;

import com.fengjiaxing.xiaobudian.entity.MusicInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 格式化字符串工具类
 */
public class FormatText {

    /**
     * 格式化时间
     *
     * @param millis 时间，以毫秒为单位
     * @return 格式化为 "mm:ss" 格式后的字符串
     */
    public static String formatTime(int millis) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date((millis)));
    }

    /**
     * 格式化歌曲内容，并创建{@linkplain com.fengjiaxing.xiaobudian.entity.MusicInfo}对象，
     * 将新建的对象放入提供的容器中
     *
     * @param songs         需要被格式化的JSON文本
     * @param musicInfoList 用于装载{@linkplain com.fengjiaxing.xiaobudian.entity.MusicInfo}
     *                      对象的容器
     */
    public static void formatSongsInfo(JSONArray songs, ArrayList<MusicInfo> musicInfoList) {

        if (songs == null || songs.length() == 0) return;

        try {

            for (int i = 0; i < songs.length(); i++) {

                JSONObject info = songs.getJSONObject(i);
                StringBuilder artist = new StringBuilder();
                JSONArray artists = info.getJSONArray("ar");

                for (int j = 0; j < artists.length(); j++) {
                    JSONObject artistInfo = artists.getJSONObject(j);
                    if (j != 0) artist.append("/");
                    artist.append(artistInfo.get("name"));
                }

                MusicInfo searchResult =
                        new MusicInfo(info.getString("name"),
                                artist.toString(),
                                info.getJSONObject("al").getString("name"),
                                Integer.toString(info.getJSONObject("privilege").getInt("id")),
                                info.getJSONObject("al").getString("picUrl"),
                                info.getInt("fee"),
                                info.getInt("dt"));

                musicInfoList.add(searchResult);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
