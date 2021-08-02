package com.fengjiaxing.xiaobudian.pictureLoad;

import android.graphics.Bitmap;

import com.fengjiaxing.xiaobudian.util.MD5Tool;

/***
 * @description 图片加载类，实现图片的三级缓存
 * @see com.fengjiaxing.xiaobudian.pictureLoad.LocalCache
 * @see com.fengjiaxing.xiaobudian.pictureLoad.MemoryCache
 * @see com.fengjiaxing.xiaobudian.pictureLoad.PictureNetworkGetter
 * ***/
public class PictureGetter {

    /**
     * 根据位图的URL，获取图片
     *
     * @param url 要获取的图片的URL
     * @param HD true代表要获取高清图，false代表要获取标清图
     * @return 如果获取成功，则返回位图，否则返回null
     * */
    public static Bitmap get(String url, boolean HD) {
        Bitmap bitmap;
        String urlMD5;
        if (!HD) urlMD5 = MD5Tool.encrypt(url);
        else urlMD5 = MD5Tool.encrypt(url + "hd");
        bitmap = MemoryCache.MEMORY_CACHE.getBitmap(urlMD5);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = LocalCache.getBitmap(urlMD5);
        if (bitmap != null) {
            MemoryCache.MEMORY_CACHE.put(urlMD5, bitmap);
            return bitmap;
        }
        bitmap = PictureNetworkGetter.getBitmap(url, HD);
        if (bitmap != null) {
            LocalCache.put(urlMD5, bitmap);
            MemoryCache.MEMORY_CACHE.put(urlMD5, bitmap);
            return bitmap;
        }
        return null;
    }
}
