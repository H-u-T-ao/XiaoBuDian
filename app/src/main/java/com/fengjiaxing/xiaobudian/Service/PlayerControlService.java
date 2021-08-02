package com.fengjiaxing.xiaobudian.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.fengjiaxing.xiaobudian.MainActivity;
import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.activity.MusicPlayerActivity;
import com.fengjiaxing.xiaobudian.fragment.HomeFragment;

import java.util.Random;

import static com.fengjiaxing.xiaobudian.ConstantPool.*;
import static com.fengjiaxing.xiaobudian.Service.Player.*;

/**
 * MediaPlayer的控制以及通知栏音乐盒子的前台服务
 */
public class PlayerControlService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final Player player = Player.getInstance();

    private NotificationManager manager;
    private Notification notification;

    private ControlReceiver receiver;

    public PlayerControlService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        initNotification();
        setListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unRegisterReceiver();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initNotification() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("playerBox", "playerBox"
                    , NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(notificationChannel);
        }

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_music_box);

        Intent intentSTP = new Intent(STP);
        PendingIntent PIntentSTP =
                PendingIntent.getBroadcast(this, 0, intentSTP, 0);
        views.setOnClickPendingIntent(R.id.iv_notification_last, PIntentSTP);

        Intent intentSAP = new Intent(SAP);
        PendingIntent PIntentSAP =
                PendingIntent.getBroadcast(this, 0, intentSAP, 0);
        views.setOnClickPendingIntent(R.id.iv_notification_control, PIntentSAP);

        Intent intentSTN = new Intent(STN);
        PendingIntent PIntentSTN =
                PendingIntent.getBroadcast(this, 0, intentSTN, 0);
        views.setOnClickPendingIntent(R.id.iv_notification_next, PIntentSTN);

        Intent intentWhole = new Intent(this, MainActivity.class);
        PendingIntent pendingIntentWhole =
                PendingIntent.getActivity(this, 0, intentWhole, 0);

        notification = new NotificationCompat.Builder(this, "playerBox")
                .setContentTitle("小布点playerBox")
                .setContentText("小布点playerBox")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntentWhole)
                .build();

        notification.contentView = views;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        startForeground(1, notification);

        updateNotification();
    }

    // 更新通知
    public void updateNotification() {
        Player player = Player.getInstance();
        if (player.getMusic() != null) {
            if (player.isPlaying())
                notification.contentView.setTextViewText(R.id.iv_notification_control, "暂停");
            else
                notification.contentView.setTextViewText(R.id.iv_notification_control, "开始");
            notification.contentView.setTextViewText(R.id.tv_notification_music, player.getMusic());
            notification.contentView.setTextViewText(R.id.tv_notification_artist, player.getArtist());
            manager.notify(1, notification);
        }
    }

    // 设置歌曲播放完毕、准备完毕和截获到错误信息的监听
    private void setListener() {
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
    }

    // 播放完毕的监听（单曲循环不会触发）
    @Override
    public void onCompletion(MediaPlayer mp) {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            if (player.getLoop() == LIST_LOOP) {
                int code = player.seekToNext();
                while (code != LOAD_SUCCESS) {
                    code = player.seekToNext();
                }
            } else if (player.getLoop() == RANDOM_PLAY) {
                int random = Math.abs(new Random().nextInt() % (player.getPlayerList().size()));
                int code = player.changeMusic(random);
                while (code != LOAD_SUCCESS) {
                    code = player.seekToNext();
                }
            }
            // 自动切歌完成以后发送自动切歌成功的本地广播
            Intent intent = new Intent(AN);
            sendBroadcast(intent);
        });
    }

    // 准备完成的监听
    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }

    // 截获到错误信息的监听
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    // 注册广播
    private void registerReceiver() {
        receiver = new ControlReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SAP);
        intentFilter.addAction(STP);
        intentFilter.addAction(STN);
        intentFilter.addAction(AN);
        intentFilter.addAction(CM);
        intentFilter.addAction(L);
        intentFilter.addAction(LL);
        intentFilter.addAction(RP);
        registerReceiver(receiver, intentFilter);
    }

    // 取消注册广播
    private void unRegisterReceiver() {
        if (receiver != null) unregisterReceiver(receiver);
    }

    /**
     * 广播内部类，用于接收控制MediaPlayer的广播，并肩负更新通知栏音乐盒子的重任
     */
    private class ControlReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Player.getInstance().getPlayerList().isEmpty()) return;
            ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
                int requestCode = 1;
                String code = intent.getAction();
                // 如果广播内容是 AN(AUTO_NEXT) ，说明是自动切歌
                // 此时不用执行任何操作，直接发出歌曲更新的广播即可
                switch (code) {
                    case SAP:
                        player.startAndPause();
                        break;
                    case STP:
                        requestCode = player.seekToPrevious();
                        break;
                    case STN:
                        requestCode = player.seekToNext();
                        break;
                    case CM:
                        int pos = intent.getIntExtra("pos", -1);
                        if (pos != -1) requestCode = player.changeMusic(pos);
                        break;
                    case L:
                        player.setLooping(LOOP);
                        break;
                    case LL:
                        player.setLooping(LIST_LOOP);
                        break;
                    case RP:
                        player.setLooping(RANDOM_PLAY);
                        break;
                    default:
                        break;
                }
                // 更新通知
                if (notification != null) updateNotification();
                // 发送歌曲加载成功通知
                if (requestCode == 1) {
                    Intent returnIntent = new Intent(LS);
                    sendBroadcast(returnIntent);
                } else if (requestCode == -1) {
                    Intent returnIntent = new Intent(NC);
                    sendBroadcast(returnIntent);
                }
            });
        }
    }


}