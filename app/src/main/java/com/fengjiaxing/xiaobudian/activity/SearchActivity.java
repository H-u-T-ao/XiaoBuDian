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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.pictureLoad.PictureGetter;
import com.fengjiaxing.xiaobudian.adapter.MusicListAdapter;
import com.fengjiaxing.xiaobudian.storage.shared.HistorySearchManager;
import com.fengjiaxing.xiaobudian.util.SoftInputManager;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;
import com.fengjiaxing.xiaobudian.util.ToastUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 搜索活动
 * */
public class SearchActivity extends AppCompatActivity {

    private SearchPresenter presenter;
    public MusicListAdapter adapter;

    private EditText scanner;
    private TextView historyText;
    private Chip delete;
    private ChipGroup history;
    private SwipeRefreshLayout refresh;
    private ImageView pic;
    private TextView playerInfo;
    private RelativeLayout loading;

    private Timer timer;

    public static final int NETWORK_ERROR = -1;
    public static final int NOTIFY_LIST = 1;
    public static final int NOTIFY_BOX = 2;
    public static final int LOAD = 3;
    public static final int INIT_CHIP = 4;
    public static final int DELETE_CHIP = 5;

    private ArrayList<Chip> chips;
    private ArrayList<Chip> lastChips;

    private String searchKey;

    private final Handler handler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case NOTIFY_LIST:
                    notifyList();
                    break;
                case NOTIFY_BOX:
                    notifyBox();
                    break;
                case INIT_CHIP:
                    initChip();
                    break;
                case DELETE_CHIP:
                    deleteChip();
                    break;
                case LOAD:
                    load();
                    break;
                case NETWORK_ERROR:
                    networkError();
                    break;
            }
        }

    };

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.presenter = new SearchPresenter(this);

        scanner = findViewById(R.id.edt_search_scanner);
        historyText = findViewById(R.id.tv_search_history_text);
        delete = findViewById(R.id.chip_search_delete_history);
        history = findViewById(R.id.cg_search_history);
        refresh = findViewById(R.id.srl_search_refresh);
        RecyclerView list = findViewById(R.id.rv_search_result);
        pic = findViewById(R.id.iv_search_player_pic);
        playerInfo = findViewById(R.id.tv_search_player_info);
        loading = findViewById(R.id.rl_search_loading);

        buildHistoryChip();

        delete.setOnClickListener(v -> ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            HistorySearchManager.delete(this);
            buildHistoryChip();
        }));

        refresh.setColorSchemeResources(R.color.app_theme_dark);

        list.setHasFixedSize(true);
        list.setFocusable(false);
        list.setFocusableInTouchMode(false);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);

        scanner.setOnClickListener(v -> {
            refresh.setVisibility(View.GONE);
            historyText.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            history.setVisibility(View.VISIBLE);
            buildHistoryChip();
        });

        scanner.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                refresh(scanner.getText().toString());
            }
            return false;
        });

        refresh.setOnRefreshListener(() -> {
            if (presenter.searchResults.isEmpty()) return;
            refresh(scanner.getText().toString());
        });

        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // 滑动到列表底部查询更多数据并显示
                int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (adapter.getItemCount() != 0 && lastCompletelyVisibleItemPosition == linearLayoutManager.getItemCount() - 1) {
                    presenter.search(searchKey, false);
                }

            }
        });

        findViewById(R.id.rl_search_player_box).setOnClickListener(v -> {
            if (Player.getInstance().getMusicId() == null) return;
            Intent intent = new Intent(SearchActivity.this, MusicPlayerActivity.class);
            startActivity(intent);
        });

    }


    // 搭建历史搜索chip
    private void buildHistoryChip() {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            ArrayList<String> keys = HistorySearchManager.get(this);
            if (chips != null) {
                lastChips = new ArrayList<>();
                lastChips.addAll(chips);
                Message msg = Message.obtain();
                msg.what = DELETE_CHIP;
                handler.sendMessage(msg);
            }
            chips = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                Chip chip = new Chip(this);
                chip.setText(keys.get(i));
                chips.add(chip);
                if (keys.get(i).equals("搜索历史为空") && i == 0) continue;
                chip.setOnClickListener(v -> {
                    refresh(chip.getText().toString());
                    scanner.setText(chip.getText().toString());
                });
            }
            Message message = Message.obtain();
            message.what = INIT_CHIP;
            handler.sendMessage(message);
        });
    }

    private void initChip() {
        for (int i = 0; i < chips.size(); i++) {
            history.addView(chips.get(i));
        }
    }

    private void deleteChip() {
        for (int i = 0; i < lastChips.size(); i++) {
            lastChips.get(i).setVisibility(View.GONE);
        }
    }

    /**
     * 搜索全新的关键词
     *
     * @param searchKey 要搜索的新关键词
     * */
    private void refresh(String searchKey) {
        historyText.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        history.setVisibility(View.GONE);
        scanner.setEnabled(false);
        refresh.setEnabled(false);
        refresh.setRefreshing(true);
        this.searchKey = searchKey;
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> HistorySearchManager.save(this, searchKey));
        presenter.searchResults.clear();
        adapter.notifyDataSetChanged();
        SoftInputManager.hide(SearchActivity.this);
        presenter.search(searchKey, true);
    }

    private void notifyList() {
        adapter.notifyDataSetChanged();
        refresh.setVisibility(View.VISIBLE);
        scanner.setEnabled(true);
        refresh.setEnabled(true);
        refresh.setRefreshing(false);
        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
    }

    @Override
    protected void onDestroy() {
        this.presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        initPlayerBox();
        super.onResume();
    }

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

    private void notifyBox() {
        Player musicInfo = Player.getInstance();
        pic.setImageBitmap(PictureGetter.get(musicInfo.getPicUrl(), false));
        String info = musicInfo.getMusic() + " - " + musicInfo.getArtist();
        playerInfo.setText(info);
    }

    private void load() {
        loading.setVisibility(View.VISIBLE);
    }

    private void networkError() {
        ToastUtil.showToast(this, "网络连接失败");
    }

}