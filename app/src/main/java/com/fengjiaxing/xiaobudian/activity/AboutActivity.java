package com.fengjiaxing.xiaobudian.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fengjiaxing.xiaobudian.R;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;

/**
 * 关于活动
 * */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
    }
}