package com.fengjiaxing.xiaobudian.pictureLoad;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * 图片加载类，在内存写入或读取图片缓存文件时使用
 */
public class MemoryCache extends LruCache<String, Bitmap> {

    public static final MemoryCache MEMORY_CACHE = new MemoryCache();

    private final LinkedHashMap<String, SoftReference<Bitmap>> softCacheMap;

    public MemoryCache() {
        super((int) (Runtime.getRuntime().maxMemory() / 8));
        softCacheMap = new LinkedHashMap<>();
    }

    /**
     * 从内存中读取输入流并转化为位图
     *
     * @param urlMD5 位图的URL（MD5加密后），以此搜索对应文件
     * @return 如果搜索成功，则返回位图，否则返回null
     */
    public Bitmap getBitmap(String urlMD5) {
        Bitmap bitmap = get(urlMD5);
        if (bitmap != null) return bitmap;
        else {
            SoftReference<Bitmap> softReference = softCacheMap.get(urlMD5);
            if (softReference != null) bitmap = softReference.get();
            if (bitmap != null) {
                put(urlMD5, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 位图从强引用容器中被移除后，将其放入软引用中
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        if (oldValue != null) {
            SoftReference<Bitmap> softReference = new SoftReference<>(oldValue);
            softCacheMap.put(key, softReference);
        }
    }

}
