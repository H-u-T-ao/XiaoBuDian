package com.fengjiaxing.xiaobudian.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;

import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.util.NetworkConnectionChecker;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

import static com.fengjiaxing.xiaobudian.ConstantPool.*;
import static com.fengjiaxing.xiaobudian.Service.Player.*;
import static com.fengjiaxing.xiaobudian.activity.MusicPlayerActivity.*;

/**
 * 乐播放器活动的presenter
 */
public class MusicPlayerPresenter {

    private final MusicPlayerActivity activity;

    public MusicPlayerReceiver receiver;

    public MusicPlayerPresenter(MusicPlayerActivity activity) {
        this.activity = activity;
    }

    /**
     * 如果用户尝试更新播放的歌曲（而不只是更换上一首或者下一首），则调用此方法
     *
     * @param position 要播放的歌曲在播放列表中的索引
     */
    public synchronized void changeMusic(int position) {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            Message message = Message.obtain();
            if (!NetworkConnectionChecker.isNetworkConnected(activity)) {
                message.what = NETWORK_ERROR;
                activity.getHandler().sendMessage(message);
                return;
            }
            Intent intent = new Intent(CM);
            intent.putExtra("pos", position);
            activity.sendBroadcast(intent);
        });
    }

    public synchronized void startAndPause() {
        Intent intent = new Intent(SAP);
        activity.sendBroadcast(intent);
    }

    /**
     * 更换歌曲
     *
     * @param next true代表更换下一首，false代表更换上一首
     */
    public void playerChange(boolean next) {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            if (!NetworkConnectionChecker.isNetworkConnected(activity)) {
                Message message = Message.obtain();
                message.what = NETWORK_ERROR;
                activity.getHandler().sendMessage(message);
                return;
            }
            Intent intent = new Intent(next ? STN : STP);
            activity.sendBroadcast(intent);
        });
    }

    public void changeLoop() {
        switch (Player.getInstance().getLoop()) {
            case LOOP:
                Intent intentLL = new Intent(LL);
                activity.sendBroadcast(intentLL);
                ToastUtil.showToast(activity, "列表循环");
                break;
            case LIST_LOOP:
                Intent intentRP = new Intent(RP);
                activity.sendBroadcast(intentRP);
                ToastUtil.showToast(activity, "随机播放");
                break;
            case RANDOM_PLAY:
                Intent intentL = new Intent(L);
                activity.sendBroadcast(intentL);
                ToastUtil.showToast(activity, "单曲循环");
                break;
        }
    }

    public void registerReceiver() {
        receiver = new MusicPlayerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LS);
        intentFilter.addAction(NC);
        activity.registerReceiver(receiver, intentFilter);
    }

    public void unRegisterReceiver() {
        if (receiver != null) activity.unregisterReceiver(receiver);
    }

    private class MusicPlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getAction();
            Message message = Message.obtain();
            switch (code) {
                case LS:
                    message.what = UPDATE_MUSIC;
                    break;
                case NC:
                    message.what = NO_COPYRIGHT;
                    break;
                default:
                    break;
            }
            activity.getHandler().sendMessage(message);
        }

    }

}
