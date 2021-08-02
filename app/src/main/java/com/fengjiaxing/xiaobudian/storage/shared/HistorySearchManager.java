package com.fengjiaxing.xiaobudian.storage.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.fengjiaxing.xiaobudian.network.Login;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_MULTI_PROCESS;

/**
 * 写入、读取和删除历史搜索记录的工具类
 */
public class HistorySearchManager {

    /**
     * 写入历史搜索关键词
     *
     * @param context   上下文，用于获取存储位置
     * @param searchKey 要存储的关键词
     */
    public static void save(Context context, String searchKey) {
        if (searchKey != null && !searchKey.isEmpty()) {
            SharedPreferences.Editor editor = context.getSharedPreferences("history_search", context.MODE_PRIVATE).edit();
            editor.putString(searchKey, searchKey);
            editor.apply();
        }
    }

    /**
     * 获取历史搜索关键词
     *
     * @param context 上下文，用于获取存储位置
     * @return 返回装载历史搜索关键词的容器
     */
    public static ArrayList<String> get(Context context) {
        SharedPreferences pref = context.getSharedPreferences("history_search", MODE_MULTI_PROCESS);
        Map<String, ?> keys = pref.getAll();
        ArrayList<String> keyLists = new ArrayList<>();
        if (keys != null && !keys.isEmpty()) {
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                keyLists.add((String) entry.getValue());
            }
        } else {
            keyLists.add("搜索历史为空");
        }
        return keyLists;
    }

    /**
     * 删除历史搜索关键词
     *
     * @param context 上下文，用于获取存储位置
     */
    public static void delete(Context context) {
        File file = new File("/data/data/" + context.getPackageName() + "/shared_prefs/history_search.xml");
        if (file.exists()) {
            file.delete();
        }
    }

}
