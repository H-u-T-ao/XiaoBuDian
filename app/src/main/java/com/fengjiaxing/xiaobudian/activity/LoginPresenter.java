package com.fengjiaxing.xiaobudian.activity;

import android.os.Message;
import android.util.Log;

import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.network.Login;
import com.fengjiaxing.xiaobudian.util.NetworkConnectionChecker;
import com.fengjiaxing.xiaobudian.util.SoftInputManager;

/**
 * 登录活动的presenter
 * */
public class LoginPresenter {

    private final LoginActivity activity;
    private final LoginModel model;

    private boolean requesting;

    public LoginPresenter(LoginActivity activity) {
        this.activity = activity;
        this.model = new Login();
    }

    /**
     * 验证账号和密码，进行登录
     *
     * @param telephoneNumber 账号（电话号码）
     * @param encryptPassword 密码（MD5加密后）
     * */
    public void login(String telephoneNumber, String encryptPassword) {

        Message message = Message.obtain();

        if (telephoneNumber.isEmpty() || encryptPassword.isEmpty()) {

            Log.d(LoginActivity.TAG, "login empty");

            message.what = LoginActivity.EMPTY;
            activity.getHandler().sendMessage(message);
        } else {
            if (NetworkConnectionChecker.isNetworkConnected(activity)) {

                if (!requesting) {
                    requesting = true;

                    ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
                        boolean success = model.login(telephoneNumber, encryptPassword);
                        if (success) {
                            message.what = LoginActivity.SUCCESS;
                            SoftInputManager.hide(activity);
                        } else {
                            message.what = LoginActivity.FAIL;
                        }
                        activity.getHandler().sendMessage(message);
                    });
                }
            } else {
                message.what = LoginActivity.NETWORK_ERROR;
                activity.getHandler().sendMessage(message);
            }
        }
        requesting = false;
    }

}
