package com.fengjiaxing.xiaobudian.Service;

import android.media.MediaPlayer;

import com.fengjiaxing.xiaobudian.entity.MusicInfo;
import com.fengjiaxing.xiaobudian.network.MusicIdToMp3;

import java.util.ArrayList;

/**
 * 继承MediaPlayer的拓展类
 */
public class Player extends MediaPlayer {

    private static Player player;

    // 请求加载新歌曲后返回的请求状态码
    /**
     * 如果发起请求时，已经处在请求状态下，返回此请求码
     */
    public static final int LOAD_NOW = 0;

    /**
     * 如果请求时发现歌曲不存在，返回此请求码
     */
    public static final int MP3_URL_NULL = -1;

    /**
     * 如果请求成功，返回此请求码
     */
    public static final int LOAD_SUCCESS = 1;

    // 播放模式
    /**
     * 标志播放模式为单曲循环
     */
    public static final int LOOP = 10;

    /**
     * 标志播放模式为列表循环
     */
    public static final int LIST_LOOP = 11;

    /**
     * 标志播放模式为随机播放
     */
    public static final int RANDOM_PLAY = 12;

    /**
     * 正在播放歌曲的歌曲名
     */
    private String music;

    /**
     * 正在播放歌曲的艺术家，如果有多个，用 '/' 隔开
     */
    private String artist;

    /**
     * 正在播放歌曲的专辑名
     */
    private String album;

    /**
     * 正在播放歌曲的musicId
     */
    private String musicId;

    /**
     * 正在播放歌曲的专辑图的URL地址
     */
    private String picUrl;

    /**
     * 尚未使用的变量，应该是代表歌曲的价格
     */
    private int fee;

    /**
     * 正在播放歌曲的总时长
     */
    private int duration;

    /**
     * 记录将要切换的歌曲在播放列表中的索引
     */
    private volatile int position;

    /**
     * 默认播放模式为列表循环
     */
    private int loop = LIST_LOOP;

    /**
     * 记录是否正在请求
     */
    private static boolean loading;

    /**
     * 播放列表
     */
    private final ArrayList<MusicInfo> playerList = new ArrayList<>();

    private Player() {
        super();
    }

    /**
     * 获取全局唯一Player对象
     */
    public static Player getInstance() {
        if (player == null) {
            synchronized (Player.class) {
                if (player == null) {
                    player = new Player();
                }
            }
        }
        return player;
    }

    /**
     * 因为网络异步加载时，父类{@linkplain android.media.MediaPlayer}
     * 的getDuration()方法无效，因此重写该方法，改为返回从歌曲信息中获取到的歌曲总时长
     *
     * @return 歌曲的总时长，单位为毫秒
     */
    @Override
    public int getDuration() {
        return this.duration;
    }

    /**
     * 设置播放模式
     *
     * @param looping 播放模式，可选参数为
     *                10（单曲循环，LOOP），
     *                11（列表循环，LIST_LOOP），
     *                12（随机播放，RANDOM_PLAY），
     */
    public synchronized void setLooping(int looping) {
        if (LOOP <= looping && looping <= RANDOM_PLAY) {
            this.loop = looping;
            super.setLooping(looping == LOOP);
        }
    }

    /**
     * 切换歌曲成功以后，调用此方法更新正在播放的歌曲的信息
     *
     * @param music    歌曲名
     * @param artist   艺术家
     * @param album    专辑名
     * @param musicId  歌曲的musicId
     * @param picUrl   歌曲专辑图的URL
     * @param fee      未知参数
     * @param duration 歌曲的总时长
     */
    private synchronized void updateMusicInfo(String music, String artist, String album, String musicId, String picUrl, int fee, int duration) {
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

    public String getPicUrl() {
        return picUrl;
    }

    public String getMusicId() {
        return musicId;
    }

    public int getFee() {
        return fee;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<MusicInfo> getPlayerList() {
        return playerList;
    }

    public int getLoop() {
        return loop;
    }

    /**
     * 更换播放歌曲（通用，不只是上一首或下一首）
     *
     * @param pos 要更换的歌曲在播放列表中的索引
     * @return 返回请求码
     */
    public int changeMusic(int pos) {
        int code = LOAD_NOW;
        if (!loading) {
            loading = true;
            MusicInfo musicInfo = playerList.get(pos);
            if (musicInfo.getMusicId().equals(getMusicId())) {
                code = LOAD_SUCCESS;
                position = pos;
            } else {
                if (player.isPlaying()) startAndPause();
                String mp3Url = MusicIdToMp3.getMp3Url(musicInfo.getMusicId());
                player.updateMusicInfo(
                        musicInfo.getMusic(),
                        musicInfo.getArtist(),
                        musicInfo.getAlbum(),
                        musicInfo.getMusicId(),
                        musicInfo.getPicUrl(),
                        musicInfo.getFee(),
                        musicInfo.getDuration());
                position = pos;
                if (mp3Url == null || mp3Url.equals("null")) {
                    code = MP3_URL_NULL;
                } else {
                    try {
                        player.reset();
                        player.setDataSource(mp3Url);
                        player.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    code = LOAD_SUCCESS;
                }
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            loading = false;
        }
        return code;
    }

    /**
     * 更换下一首歌曲
     *
     * <p>如果正在播放的歌曲已经是播放列表的最后一首歌曲，
     * 那么更换下一首歌曲为播放列表的第一首歌曲
     *
     * @return 返回请求码
     */
    public int seekToNext() {
        int pos = position;
        if (pos == (playerList.size() - 1) || playerList.size() == 1) {
            pos = -1;
        }
        return changeMusic(pos + 1);
    }

    /**
     * 更换上一首歌曲
     *
     * <p>如果正在播放的歌曲已经是播放列表的第一首歌曲，
     * 那么更换上一首歌曲为播放列表的最后一首歌曲
     *
     * @return 返回请求码
     */
    public int seekToPrevious() {
        int pos = position;
        if (pos == 0 || playerList.size() == 1) {
            pos = playerList.size();
        }
        return changeMusic(pos - 1);
    }

    /**
     * 开始或暂停歌曲
     *
     * <p>如果正在播放，则暂停,反之则开始
     */
    public synchronized void startAndPause() {
        if (!player.isPlaying()) {
            player.start();
        } else if (player.isPlaying()) {
            player.pause();
        }
    }
}
