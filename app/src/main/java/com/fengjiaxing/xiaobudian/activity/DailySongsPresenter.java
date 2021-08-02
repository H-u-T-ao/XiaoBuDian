package com.fengjiaxing.xiaobudian.activity;

import android.os.Message;

import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.adapter.MusicListAdapter;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.network.DailySong;
import com.fengjiaxing.xiaobudian.util.NetworkConnectionChecker;

import java.util.ArrayList;

/**
 * 每日推荐活动的presenter
 * */

public class DailySongsPresenter {

    private final DailySongsActivity activity;
    private final DailySongsModel model;

    public ArrayList<MusicInfo> dailySongsList;

    public DailySongsPresenter(DailySongsActivity activity) {
        this.activity = activity;
        this.model = new DailySong();
        dailySongsList = new ArrayList<>();
        activity.adapter = new MusicListAdapter(dailySongsList, activity);
    }

    /***
     * 获取每日推荐歌曲
     * */
    public void getDailySongs() {
        Message message = Message.obtain();
        if (!NetworkConnectionChecker.isNetworkConnected(activity)) {
            message.what = DailySongsActivity.NETWORK_ERROR;
            activity.getHandler().sendMessage(message);
            return;
        }

        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            model.get(dailySongsList);
            if (dailySongsList == null || dailySongsList.size() == 0) {
                message.what = DailySongsActivity.ERROR;
            } else {
                message.what = DailySongsActivity.INIT_LIST;
            }
            activity.getHandler().sendMessage(message);
        });

    }

}
