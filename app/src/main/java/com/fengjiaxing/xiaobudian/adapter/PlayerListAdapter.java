package com.fengjiaxing.xiaobudian.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.activity.MusicPlayerActivity;
import com.fengjiaxing.xiaobudian.activity.PlayerListActivity;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.util.FormatText;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

/**
 * 播放列表的适配器
 * */
public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {

    private final PlayerListActivity activity;

    public PlayerListAdapter(PlayerListActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListAdapter.ViewHolder holder, int position) {
        MusicInfo musicInfo = Player.getInstance().getPlayerList().get(position);
        String musicInfoText = musicInfo.getMusic() + " - " + musicInfo.getArtist();
        holder.musicInfo.setText(musicInfoText);
        holder.duration.setText(FormatText.formatTime(musicInfo.getDuration()));
        if (Player.getInstance().getPosition() == position) {
            holder.delete.setText("");
            holder.delete.setBackgroundResource(R.drawable.ic_playing);
        } else {
            holder.delete.setOnClickListener(v -> {
                ToastUtil.showToast(activity, "移除成功");
                Player.getInstance().getPlayerList().remove(position);
                PlayerListAdapter.this.notifyDataSetChanged();
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Player.getInstance().setPosition(position);
            Intent intent = new Intent(activity, MusicPlayerActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });

    }

    // 修改返回的position是真正的item的索引，而不是经过换算之后的值
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Player.getInstance().getPlayerList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView musicInfo;
        TextView duration;
        TextView delete;

        public ViewHolder(@NonNull View view) {
            super(view);
            musicInfo = view.findViewById(R.id.tv_player_list_music_info);
            duration = view.findViewById(R.id.tv_player_list_duration);
            delete = view.findViewById(R.id.tv_player_list_delete);
        }
    }

}
