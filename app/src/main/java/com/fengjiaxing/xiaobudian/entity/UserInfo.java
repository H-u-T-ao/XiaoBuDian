package com.fengjiaxing.xiaobudian.entity;

/**
 * 用户信息实体类，使用懒汉式单例
 * */
public class UserInfo {

    private static UserInfo userInfo = null;

    private volatile  String telephone;

    private volatile String cookie;

    private volatile boolean loginSuccess;

    private UserInfo() {
    }

    public static UserInfo getInstance() {
        if (userInfo == null) {
            synchronized (UserInfo.class) {
                if (userInfo == null) {
                    userInfo = new UserInfo();
                }
            }
        }
        return userInfo;
    }

    public String getCookie() {
        return cookie;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

}
