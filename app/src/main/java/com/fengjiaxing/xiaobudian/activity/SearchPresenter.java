package com.fengjiaxing.xiaobudian.activity;

import android.os.Message;

import com.fengjiaxing.xiaobudian.adapter.MusicListAdapter;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.network.Search;
import com.fengjiaxing.xiaobudian.util.NetworkConnectionChecker;

import java.util.ArrayList;

/**
 * 搜索活动的presenter
 * */
public class SearchPresenter {

    private final SearchActivity activity;
    private final SearchModel model;
    public ArrayList<MusicInfo> searchResults;

    private int page;
    private boolean requesting;

    public SearchPresenter(SearchActivity activity) {
        this.activity = activity;
        this.model = new Search();
        this.searchResults = new ArrayList<>();
        activity.adapter = new MusicListAdapter(searchResults, activity);
    }

    /**
     * 根据关键词搜索音乐
     *
     * <p>新词则重新创建一个容器去装载新的搜索结果，旧词则用原来的容器继续装载
     *
     * @param searchKey 搜索的关键词
     * @param newKey true代表搜索的关键词是新词，false代表搜索的关键词是旧词
     * */
    public void search(String searchKey, boolean newKey) {

        if (!NetworkConnectionChecker.isNetworkConnected(activity)) {
            Message message = Message.obtain();
            message.what = SearchActivity.NETWORK_ERROR;
            activity.getHandler().sendMessage(message);
        }

        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            if (!requesting) {
                Message message = Message.obtain();
                message.what = SearchActivity.LOAD;
                activity.getHandler().sendMessage(message);

                requesting = true;
                if (newKey) {
                    page = 0;
                } else {
                    page++;
                }
                model.search(searchKey, page, searchResults);

                Message msg = Message.obtain();
                msg.what = SearchActivity.NOTIFY_LIST;
                activity.getHandler().sendMessage(msg);
                requesting = false;
            }
        });
    }

}
