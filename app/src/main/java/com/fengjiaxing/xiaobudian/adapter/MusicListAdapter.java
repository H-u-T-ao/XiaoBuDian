package com.fengjiaxing.xiaobudian.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.Service.Player;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.activity.MusicPlayerActivity;
import com.fengjiaxing.xiaobudian.pictureLoad.PictureGetter;
import com.fengjiaxing.xiaobudian.entity.MusicInfo;

import java.util.ArrayList;

/**
 * 音乐信息列表的适配器
 * */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private final ArrayList<MusicInfo> infoLists;
    private final Activity activity;

    public MusicListAdapter(ArrayList<MusicInfo> infoLists, Activity activity) {
        this.infoLists = infoLists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListAdapter.ViewHolder holder, int position) {
        // 在末尾显示多一个item，这个item设置为完全的白色，因为最后一条item会被音乐盒子遮挡
        if (position == infoLists.size()) {
            holder.pic.setBackgroundResource(R.color.app_white);
            holder.music.setText("");
            holder.artist.setText("");
            holder.album.setText("");
            return;
        }
        MusicInfo musicInfo = infoLists.get(position);

        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                activity.runOnUiThread(() -> holder.pic.setImageResource(R.drawable.ic_pic));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap bitmap = PictureGetter.get(musicInfo.getPicUrl(), false);
            if (bitmap == null) return;
            try {
                activity.runOnUiThread(() -> holder.pic.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.music.setText(musicInfo.getMusic());
        holder.artist.setText(musicInfo.getArtist());
        holder.album.setText(musicInfo.getAlbum());

        holder.itemView.setOnClickListener(v -> {
            if(position == infoLists.size()) return;
            Player.getInstance().getPlayerList().clear();
            Player.getInstance().getPlayerList().addAll(infoLists);
            Player.getInstance().setPosition(position);
            Intent intent = new Intent(activity, MusicPlayerActivity.class);
            activity.startActivity(intent);
        });

    }

    // 修改返回的position是真正的item的索引，而不是经过换算之后的值
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // 在末尾显示多一个item，因为最后一条item会被音乐盒子遮挡
    @Override
    public int getItemCount() {
        return infoLists.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView music;
        TextView artist;
        TextView album;

        public ViewHolder(@NonNull View view) {
            super(view);
            pic = view.findViewById(R.id.iv_search_list_pic);
            music = view.findViewById(R.id.tv_search_list_music);
            artist = view.findViewById(R.id.tv_search_list_artist);
            album = view.findViewById(R.id.tv_search_list_album);
        }
    }

}
