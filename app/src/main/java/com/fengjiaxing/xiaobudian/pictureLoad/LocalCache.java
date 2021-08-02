package com.fengjiaxing.xiaobudian.pictureLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片加载类，在外存写入或读取图片缓存文件时使用
 * */
public class LocalCache {

    public static DiskLruCache diskLruCache;

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 50;

    public static void createDiskLruCache(Context context) {
        File diskLruCacheFile = getDiskCacheDir(context);
        if (!diskLruCacheFile.exists()) {
            diskLruCacheFile.mkdirs();
        }
        if (getUsableSpace(diskLruCacheFile) > DISK_CACHE_SIZE) {
            try {
                diskLruCache = DiskLruCache.open(diskLruCacheFile, 1, 1, DISK_CACHE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将bitmap位图存储在外存中
     *
     * @param urlMD5 位图的URL（MD5加密后），将作为这个位图的键值
     * @param bitmap 要存储的位图
     * */
    public static void put(String urlMD5, Bitmap bitmap) {
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(urlMD5);
            if (editor != null) {
                OutputStream out = editor.newOutputStream(0);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    editor.commit();
                    System.out.println("存入外存成功");
                } else {
                    editor.abort();
                }
            }
            diskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从外存中读取输入流并转化为位图
     *
     * @param urlMD5 位图的URL（MD5加密后），以此搜索对应文件
     * @return 如果搜索成功，则返回位图，否则返回null
     * */
    public static Bitmap getBitmap(String urlMD5) {
        Bitmap bitmap = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(urlMD5);
            if (snapshot != null) {
                bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static File getDiskCacheDir(Context context) {

        boolean externalStorageAvailable = Environment
                .getExternalStorageDirectory().toString().equals(Environment.MEDIA_MOUNTED);
        //磁盘缓存的绝对路径
        final String cachePath;
        //当SD卡存在的情况下
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + "DiskLruCache");
    }

    private static long getUsableSpace(File path) {
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

}
