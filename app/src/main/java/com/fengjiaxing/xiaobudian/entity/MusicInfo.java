package com.fengjiaxing.xiaobudian.entity;

/**
 * 音乐信息实体类
 */
public class MusicInfo {

    private final String music;
    private final String artist;
    private final String album;
    private final String musicId;
    private final String picUrl;
    private final int fee;
    private final int duration;

    public MusicInfo(String music, String artist, String album, String musicId, String picUrl, int fee, int duration) {
        this.music = music;
        this.artist = artist;
        this.album = album;
        this.musicId = musicId;
        this.picUrl = picUrl;
        this.fee = fee;
        this.duration = duration;
    }

    public String getMusic() {
        return music;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getFee() {
        return fee;
    }

    public String getMusicId() {
        return musicId;
    }

}
