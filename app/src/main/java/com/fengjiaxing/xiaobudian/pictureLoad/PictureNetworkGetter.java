package com.fengjiaxing.xiaobudian.pictureLoad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fengjiaxing.xiaobudian.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图片加载类，从网络中获取图片的输入流并转换为bitmap位图
 * */
public class PictureNetworkGetter {

    /**
     * 网络获取位图
     *
     * @param picUrl 位图的URL，以此获取位图
     * @param HD true代表要获取高清图，false代表要获取标清图
     * @return 如果获取成功，则返回位图，否则返回null
     * */
    public static Bitmap getBitmap(String picUrl, boolean HD) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            if (HD) picUrl = picUrl + "?param=600y600";
            else picUrl = picUrl + "?param=120y120";
            URL url = new URL(picUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(R.integer.network_request_time_out);
            connection.setReadTimeout(R.integer.network_request_time_out);
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

}
