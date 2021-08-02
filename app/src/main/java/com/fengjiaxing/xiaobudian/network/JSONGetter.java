package com.fengjiaxing.xiaobudian.network;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.entity.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取JSON文本的网络工具类
 * */
public class JSONGetter {

    /**
     * 获取JSON文本
     *
     * @param urlString url地址
     * @param p 需要以POST方法传入的参数，其中Map集合的key会被当做参数名，value会被当做参数
     * @return 如果获取成功，返回获得的JSON文本。否则返回null
     * */
    public static String getJSON(String urlString, HashMap<String, String> p) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String data = null;
        try {
            URL url = new URL(urlString + "?timestamp=" + System.currentTimeMillis());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(R.integer.network_request_time_out);
            connection.setConnectTimeout(R.integer.network_request_time_out);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            if (UserInfo.getInstance().isLoginSuccess()) {
                connection.setRequestProperty("Cookie", UserInfo.getInstance().getCookie());
            }
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

            if (p != null) {
                boolean thirst = true;
                for (Map.Entry<String, String> entry : p.entrySet()) {
                    if (thirst) {
                        out.write(entry.getKey() + "=" + entry.getValue());
                        thirst = false;
                    } else
                        out.write("&" + entry.getKey() + "=" + entry.getValue());
                }
            }

            out.flush();

            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            data = result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return data;
    }

}
