package com.fengjiaxing.xiaobudian.network;

import android.content.Context;

import com.fengjiaxing.xiaobudian.ConstantPool;
import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.activity.LoginModel;
import com.fengjiaxing.xiaobudian.entity.UserInfo;
import com.fengjiaxing.xiaobudian.storage.shared.UserInfoManager;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 登录活动的model实现类
 */
public class Login implements LoginModel {

    /**
     * 验证账号和密码，进行登录
     *
     * <p>如果登录成功，则会将cookie字段存储在实体类
     * {@linkplain com.fengjiaxing.xiaobudian.entity.UserInfo}中
     *
     * @param telephoneNumber 账号（电话号码）
     * @param encryptedPassword 密码（MD5加密后）
     * @return 如果验证成功，返回true，否则返回false
     * @see com.fengjiaxing.xiaobudian.entity.UserInfo
     */
    public boolean login(String telephoneNumber, String encryptedPassword) {
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        List<String> cookies = null;
        try {
            URL url = new URL(ConstantPool.DOMAIN + "/login/cellphone");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(R.integer.network_request_time_out);
            connection.setConnectTimeout(R.integer.network_request_time_out);

            out = new OutputStreamWriter(connection.getOutputStream());
            out.write("phone=" + telephoneNumber);
            out.write("&md5_password=" + encryptedPassword);
            out.flush();

            Map<String, List<String>> headerFields = connection.getHeaderFields();
            cookies = headerFields.get("Set-Cookie");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (cookies == null || cookies.size() != 4) return false;

        StringBuilder cookie = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
            cookie.append(cookies.get(i));
        }
        UserInfo.getInstance().setTelephone(telephoneNumber);
        UserInfo.getInstance().setCookie(cookie.toString());
        UserInfo.getInstance().setLoginSuccess(true);
        return true;

    }

    /**
     * 退出登录，并删除实体类{@linkplain com.fengjiaxing.xiaobudian.entity.UserInfo}中
     * 的cookie信息和文件中已经保存的用户账户和密码信息
     *
     * @param context 上下文，用户在删除文件时获取文件路径
     * @return 如果正处在登陆状态，则会执行退出登录操作并返回true，否则返回false
     */
    public boolean logout(Context context) {

        if (!UserInfo.getInstance().isLoginSuccess()) return false;

        String url = ConstantPool.DOMAIN + "/logout";
        JSONGetter.getJSON(url, null);
        UserInfo.getInstance().setLoginSuccess(false);
        UserInfo.getInstance().setCookie(null);

        UserInfoManager.delete(context);

        return true;

    }

}
