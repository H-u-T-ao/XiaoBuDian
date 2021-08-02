package com.fengjiaxing.xiaobudian.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.ThreadPool;
import com.fengjiaxing.xiaobudian.activity.AboutActivity;
import com.fengjiaxing.xiaobudian.activity.LoginActivity;
import com.fengjiaxing.xiaobudian.entity.UserInfo;
import com.fengjiaxing.xiaobudian.network.Login;
import com.fengjiaxing.xiaobudian.storage.shared.UserInfoManager;
import com.fengjiaxing.xiaobudian.util.NetworkConnectionChecker;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

/**
 * 关于&更多碎片
 * */
public class MoreFragment extends Fragment {

    private ImageView pic;
    private TextView telephone;

    private static final int NETWORK_ERROR = -1;
    private static final int PLEASE_LOGIN = 0;
    private static final int LOGOUT_SUCCESS = 1;
    private static final int LOGIN_SUCCESS = 2;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    loginSuccess();
                    break;
                case PLEASE_LOGIN:
                    pleaseLogin();
                    break;
                case LOGOUT_SUCCESS:
                    logoutSuccess();
                    break;
                case NETWORK_ERROR:
                    networkError();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        pic = view.findViewById(R.id.iv_more_pic);
        telephone = view.findViewById(R.id.tv_more_telephone);

        pic.setOnClickListener(v -> {
            if (UserInfo.getInstance().isLoginSuccess()) return;
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_more_about).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_more_logout).setOnClickListener(v ->
                ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
                    Message message = Message.obtain();
                    if (!NetworkConnectionChecker.isNetworkConnected(getContext())) {
                        message.what = NETWORK_ERROR;
                    }
                    boolean success = new Login().logout(getContext());
                    if (success) {
                        message.what = LOGOUT_SUCCESS;
                    }
                    handler.sendMessage(message);
                })
        );

        return view;
    }

    @Override
    public void onResume() {
        ThreadPool.THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                if (UserInfo.getInstance().isLoginSuccess()) {
                    Message message = Message.obtain();
                    message.what = LOGIN_SUCCESS;
                    handler.sendMessage(message);
                } else if (getContext() != null && UserInfoManager.autoLogin(getContext())) {
                    Message message = Message.obtain();
                    message.what = LOGIN_SUCCESS;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        super.onResume();
    }

    private void logoutSuccess() {
        pic.setImageResource(R.drawable.ic_pic);
        telephone.setText("");
        ToastUtil.showToast(getActivity(), "退出登录成功");
    }

    private void loginSuccess() {
        pic.setImageResource(R.drawable.ic_login_status);
        telephone.setText(UserInfo.getInstance().getTelephone());
    }

    private void pleaseLogin() {
        ToastUtil.showToast(getActivity(), "请先登录");
    }

    private void networkError() {
        ToastUtil.showToast(getActivity(), "网络连接错误");
    }

}