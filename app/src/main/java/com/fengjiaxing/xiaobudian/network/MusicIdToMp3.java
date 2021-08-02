package com.fengjiaxing.xiaobudian.network;

import com.fengjiaxing.xiaobudian.ConstantPool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 根据musicId获取对应MP3文件URL的网络工具类
 * */
public class MusicIdToMp3{

    /**
     * 根据musicId获取歌曲MP3文件的URL
     *
     * @param musicId 歌曲的musicId
     * @return 歌曲的MP3文件的URL
     * */
    public static synchronized String getMp3Url(String musicId) {

        String url = ConstantPool.DOMAIN + "/song/url";
        HashMap<String, String> p = new HashMap<>();
        p.put("id", musicId);
        String data = JSONGetter.getJSON(url, p);

        String mp3Url = null;
        try {
            JSONArray jsonData = new JSONObject(data).getJSONArray("data");
            if (jsonData.length() != 0) {
                mp3Url = jsonData.getJSONObject(0).getString("url");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mp3Url;

    }

}
