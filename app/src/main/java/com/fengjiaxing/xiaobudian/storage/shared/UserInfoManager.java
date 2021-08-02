package com.fengjiaxing.xiaobudian.storage.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.fengjiaxing.xiaobudian.network.Login;

import java.io.File;

import static android.content.Context.MODE_MULTI_PROCESS;

/**
 * 写入、读取和删除用户账号（电话号码）和密码（MD5加密后）的工具类
 * */
public class UserInfoManager {

    /**
     * 写入用户信息
     *
     * @param context 上下文，用于获取存储位置
     * @param u 用户的账号（电话号码）
     * @param p 用户的密码（MD5加密后）
     * */
    public static void save(Context context, String u, String p) {
        if (u != null && p != null) {
            SharedPreferences.Editor editor =
                    context.getSharedPreferences("user_password", Context.MODE_PRIVATE).edit();
            editor.putString("u", u);
            editor.putString("p", p);
            editor.apply();
        }
    }

    /**
     * 根据已写入信息进行自动登录
     *
     * @param context 上下文，用于获取存储位置
     * @return 如果存在已写入信息并登陆成功，则返回true，否则返回false
     * */
    public static boolean autoLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences("user_password", MODE_MULTI_PROCESS);
        String telephoneNumber = pref.getString("u", "");
        String encryptPassword = pref.getString("p", "");
        if (telephoneNumber != null && encryptPassword != null
                && !telephoneNumber.isEmpty() && !encryptPassword.isEmpty())
            return new Login().login(telephoneNumber, encryptPassword);
        return false;
    }

    /**
     * 删除用户信息
     *
     * @param context 上下文，用于获取存储位置
     * */
    public static void delete(Context context){
        File file = new File("/data/data/" + context.getPackageName() + "/shared_prefs/user_password.xml");
        if (file.exists()) {
            file.delete();
        }
    }

}
