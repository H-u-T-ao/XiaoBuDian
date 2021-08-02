package com.fengjiaxing.xiaobudian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.adapter.MusicListAdapter;
import com.fengjiaxing.xiaobudian.pictureLoad.PictureGetter;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页&发现板块的每日推荐活动
 * */
public class DailySongsActivity extends AppCompatActivity {

    private SwipeRefreshLayout refresh;
    private ImageView pic;
    private TextView playerInfo;

    public MusicListAdapter adapter;

    private DailySongsPresenter presenter;

    private Timer timer;

    public static final int NETWORK_ERROR = -1;
    public static final int ERROR = 0;
    public static final int INIT_LIST = 1;
    public static final int NOTIFY_BOX = 2;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case NOTIFY_BOX:
                    notifyBox();
                    break;
                case INIT_LIST:
                    initList();
                    break;
                case NETWORK_ERROR:
                    networkError();
                    break;
                case ERROR:
                    error();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_songs);

        presenter = new DailySongsPresenter(this);

        refresh = findViewById(R.id.sfl_daily_songs_refresh);
        RecyclerView list = findViewById(R.id.rv_daily_songs_list);
        pic = findViewById(R.id.iv_daily_songs_player_pic);
        playerInfo = findViewById(R.id.tv_daily_songs_player_info);

        refresh.setColorSchemeResources(R.color.app_theme_dark);
        refresh.setRefreshing(true);

        list.setHasFixedSize(true);
        list.setFocusable(false);
        list.setFocusableInTouchMode(false);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);

        findViewById(R.id.rl_daily_songs_player_box).setOnClickListener(v -> {
            if (Player.getInstance().getMusicId() == null) return;
            Intent intent = new Intent(this, MusicPlayerActivity.class);
            startActivity(intent);
        });

        presenter.getDailySongs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
    }

    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        initPlayerBox();
        super.onResume();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    // 初始化音乐盒子
    private void initPlayerBox() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Player.getInstance().getMusicId() != null) {
                    Message message = Message.obtain();
                    message.what = NOTIFY_BOX;
                    handler.sendMessage(message);
                }
            }
        }, 0, 3000L);
    }

    // 更新音乐盒子
    private void notifyBox() {
        Player musicInfo = Player.getInstance();
        pic.setImageBitmap(PictureGetter.get(musicInfo.getPicUrl(), false));
        String info = musicInfo.getMusic() + " - " + musicInfo.getArtist();
        playerInfo.setText(info);
    }

    private void initList() {
        adapter.notifyDataSetChanged();
        refresh.setEnabled(false);
    }

    private void networkError() {
        ToastUtil.showToast(this, "网络连接失败");
    }

    private void error() {
        ToastUtil.showToast(this, "未打开个性化服务");
    }

    public Handler getHandler() {
        return this.handler;
    }

}