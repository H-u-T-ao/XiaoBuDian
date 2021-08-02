package com.fengjiaxing.xiaobudian.fragment;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.Service.PlayerControlService;
import com.fengjiaxing.xiaobudian.activity.DailySongsActivity;
import com.fengjiaxing.xiaobudian.activity.MusicPlayerActivity;
import com.fengjiaxing.xiaobudian.activity.SearchActivity;
import com.fengjiaxing.xiaobudian.entity.UserInfo;
import com.fengjiaxing.xiaobudian.pictureLoad.PictureGetter;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页&发现碎片
 * */
public class HomeFragment extends Fragment {

    private ImageView pic;
    private TextView playerInfo;

    private Timer timer;

    private static final int NOTIFY_BOX = 1;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case NOTIFY_BOX:
                    notifyBox();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pic = view.findViewById(R.id.iv_home_player_pic);
        playerInfo = view.findViewById(R.id.tv_home_player_info);

        view.findViewById(R.id.btn_home_search).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_home_daily_songs).setOnClickListener(v -> {
            if (UserInfo.getInstance().isLoginSuccess()) {
                Intent intent = new Intent(getActivity(), DailySongsActivity.class);
                startActivity(intent);
            } else {
                ToastUtil.showToast(getActivity(), "请先登录");
            }
        });

        view.findViewById(R.id.btn_home_notification).setOnClickListener(v -> {
        });

        view.findViewById(R.id.rl_home_player_box).setOnClickListener(v -> {
            if (Player.getInstance().getMusicId() == null) return;
            Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
            startActivity(intent);
        });

        return view;
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

}