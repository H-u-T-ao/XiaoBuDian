package com.fengjiaxing.xiaobudian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.storage.shared.UserInfoManager;
import com.fengjiaxing.xiaobudian.util.MD5Tool;
import com.fengjiaxing.xiaobudian.util.SoftInputManager;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;
import com.fengjiaxing.xiaobudian.util.ToastUtil;

/**
 * @description 登录活动
 * */
public class LoginActivity extends AppCompatActivity {

    private LoginPresenter presenter;

    private EditText telephoneNumber;
    private EditText password;

    private String telephone;
    private String encryptPassword;
    private Button login;

    public static final int EMPTY = -1;
    public static final int FAIL = 0;
    public static final int SUCCESS = 1;
    public static final int NETWORK_ERROR = 999;

    public static final String TAG = "LoginActivity";

    private final Handler handler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case EMPTY:
                    scannerEmpty();
                    break;
                case FAIL:
                    loginFail();
                    break;
                case SUCCESS:
                    loginSuccess();
                    break;
                case NETWORK_ERROR:
                    networkError();
            }
        }
    };

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginPresenter(this);

        telephoneNumber = findViewById(R.id.edt_login_telephone_number);
        password = findViewById(R.id.edt_login_password);
        login = findViewById(R.id.btn_login_login);

        login.setOnClickListener(v -> {
            telephoneNumber.setEnabled(false);
            password.setEnabled(false);
            login.setEnabled(false);
            SoftInputManager.hide(this);
            ToastUtil.showToast(this, "正在登录，请稍后...");
            telephone = telephoneNumber.getText().toString();
            encryptPassword = MD5Tool.encrypt(password.getText().toString());
            presenter.login(telephone, encryptPassword);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
    }

    @Override
    protected void onDestroy() {
        this.presenter = null;
        super.onDestroy();
    }

    private void scannerEmpty() {
        telephoneNumber.setEnabled(true);
        password.setEnabled(true);
        login.setEnabled(true);
        ToastUtil.showToast(this, "输入账号或密码为空");
    }

    private void loginFail() {
        telephoneNumber.setEnabled(true);
        password.setEnabled(true);
        login.setEnabled(true);
        ToastUtil.showToast(this, "输入账号或密码有误");
    }

    private void loginSuccess() {
        // 登录成功后保存密码
        UserInfoManager.save(this, telephone, encryptPassword);
        ToastUtil.showToast(this, "登录成功");
        finish();
    }


    private void networkError() {
        Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
    }

}