package com.fengjiaxing.xiaobudian.activity;

/**
 * 登录活动的model接口
 *
 * @see com.fengjiaxing.xiaobudian.network.Login
 * */
public interface LoginModel {

    /**
     * 验证账号和密码，进行登录
     *
     * @param telephone 账号（电话号码）
     * @param encryptPassword 密码（MD5加密后）
     * */
    boolean login(String telephone, String encryptPassword);

}
