package com.fengjiaxing.xiaobudian.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.adapter.PlayerListAdapter;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;

/**
 * 播放列表活动
 * */
public class PlayerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        RecyclerView list = findViewById(R.id.rv_player_list);

        PlayerListAdapter adapter = new PlayerListAdapter(this);

        list.setHasFixedSize(true);
        list.setFocusable(false);
        list.setFocusableInTouchMode(false);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.set_left_activity_show, R.anim.set_right_activity_hide);
        super.onPause();
    }

    @Override
    protected void onStart() {
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
        super.onStart();
    }

}