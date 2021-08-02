package com.fengjiaxing.xiaobudian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.Service.PlayerControlService;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.pictureLoad.PictureGetter;
import com.fengjiaxing.xiaobudian.util.FormatText;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.fengjiaxing.xiaobudian.Service.Player.*;

/**
 * 音乐播放器活动
 */
public class MusicPlayerActivity extends AppCompatActivity {

    private MusicPlayerPresenter presenter;

    private String picture;

    private ImageView bg;
    private ImageView pic;
    private TextView music;
    private TextView artist;
    private TextView album;
    private ImageView ivMode;
    private SeekBar progress;
    private TextView nowTime;
    private TextView totalTime;
    private ImageView control;
    private ImageView last;
    private ImageView next;

    private boolean setting;

    private Timer timer;

    public static final int NO_COPYRIGHT = -3;
    public static final int ERROR = -2;
    public static final int NETWORK_ERROR = -1;
    public static final int UPDATE_TEXT = 1;
    public static final int UPDATE_MUSIC = 2;

    public Handler getHandler() {
        return handler;
    }

    private final Handler handler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    updateProgress();
                    break;
                case UPDATE_MUSIC:
                    updateMusic();
                    break;
                case NO_COPYRIGHT:
                    noCopyright();
                    break;
                case NETWORK_ERROR:
                    networkError();
                    break;
                case ERROR:
                    playerError();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        presenter = new MusicPlayerPresenter(this);
        presenter.registerReceiver();

        bg = findViewById(R.id.iv_music_player_bg);
        pic = findViewById(R.id.iv_music_player_pic);
        music = findViewById(R.id.tv_music_player_music);
        artist = findViewById(R.id.tv_music_player_artist);
        album = findViewById(R.id.tv_music_player_album);
        ivMode = findViewById(R.id.iv_music_player_mode);
        progress = findViewById(R.id.sb_music_player_progress);
        nowTime = findViewById(R.id.tv_music_player_now_time);
        totalTime = findViewById(R.id.tv_music_player_total_time);
        control = findViewById(R.id.iv_music_player_control);
        last = findViewById(R.id.iv_music_player_last);
        next = findViewById(R.id.iv_music_player_next);

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setting = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Player.getInstance().isPlaying() && setting) {
                    Player.getInstance().seekTo(seekBar.getProgress());
                    setting = false;
                }
            }
        });

        switch (PlayerControlService.player.getLoop()) {
            case LOOP:
                ivMode.setImageResource(R.drawable.ic_loop);
                break;
            case LIST_LOOP:
                ivMode.setImageResource(R.drawable.ic_list_loop);
                break;
            case RANDOM_PLAY:
                ivMode.setImageResource(R.drawable.ic_random);
                break;
        }

        // 更改播放循环模式
        ivMode.setOnClickListener(v -> presenter.changeLoop());

        findViewById(R.id.iv_music_player_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.set_right_activity_show, R.anim.set_left_activity_hide);
        });

        control.setOnClickListener(v -> {
            control.setEnabled(false);
            presenter.startAndPause();
        });

        last.setOnClickListener(v -> playerChange(false));

        next.setOnClickListener(v -> playerChange(true));

        // 加载毛玻璃特效背景
        Glide.with(this)
                .load(R.drawable.ic_pic)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(14, 3)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bg);

    }

    @Override
    protected void onStart() {
        StatusBarColorSetting.setTransparent(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        presenter.unRegisterReceiver();
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    private void init() {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {

            presenter.changeMusic(PlayerControlService.player.getPosition());

            if (timer != null) timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                }
            }, 0, 100L);

        });

    }

    private void playerChange(boolean next) {
        this.last.setEnabled(false);
        this.next.setEnabled(false);
        presenter.playerChange(next);
    }

    // 更新播放器上需要实时更改的内容（进度条，播放时长，播放状态）
    private void updateProgress() {
        Player info = PlayerControlService.player;
        if (info.getMusic() == null) return;
        int duration = info.getCurrentPosition();
        if (duration == 0) {
            nowTime.setText("正在缓冲");
        } else if (!setting) {
            nowTime.setText(FormatText.formatTime(duration));
            progress.setProgress(duration);
        }
        if (PlayerControlService.player.isPlaying())
            control.setImageResource(R.drawable.ic_music_player_pause);
        else
            control.setImageResource(R.drawable.ic_music_player_start);
        control.setEnabled(true);
    }

    // 更新播放器上不需要实时更新的内容（图片，歌名，歌手，专辑，总时长，播放模式）
    private void updateMusic() {
        Player player = PlayerControlService.player;
        switch (player.getLoop()) {
            case LOOP:
                ivMode.setImageResource(R.drawable.ic_loop);
                break;
            case LIST_LOOP:
                ivMode.setImageResource(R.drawable.ic_list_loop);
                break;
            case RANDOM_PLAY:
                ivMode.setImageResource(R.drawable.ic_random);
                break;
        }
        if (player.getMusic() == null) return;
        if (!player.getPicUrl().equals(picture)) {
            ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
                picture = player.getPicUrl();
                String url = player.getPicUrl();
                Bitmap bitmap = PictureGetter.get(url, true);
                if (bitmap == null) return;
                runOnUiThread(() -> {
                    try {
                        pic.setImageBitmap(bitmap);
                        // 加载毛玻璃特效背景
                        Glide.with(this)
                                .load(bitmap)
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(20, 3)))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(bg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }
        music.setText(player.getMusic());
        artist.setText(player.getArtist());
        album.setText(player.getAlbum());
        progress.setMax(player.getDuration());
        totalTime.setText(FormatText.formatTime(player.getDuration()));
        this.last.setEnabled(true);
        this.next.setEnabled(true);
    }

    private void playerError() {
        ToastUtil.showToast(this, "播放器初始化错误");
    }

    private void networkError() {
        ToastUtil.showToast(this, "网络连接失败");
    }


    private void noCopyright() {
        ToastUtil.showToast(this, "抱歉，没有版权");
    }
}