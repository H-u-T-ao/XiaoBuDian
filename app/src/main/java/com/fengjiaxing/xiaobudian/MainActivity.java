package com.fengjiaxing.xiaobudian;

import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.fengjiaxing.xiaobudian.Service.PlayerControlService;
import com.fengjiaxing.xiaobudian.pictureLoad.LocalCache;
import com.fengjiaxing.xiaobudian.adapter.MainViewPagerAdapter;
import com.fengjiaxing.xiaobudian.fragment.HomeFragment;
import com.fengjiaxing.xiaobudian.fragment.MoreFragment;
import com.fengjiaxing.xiaobudian.util.StatusBarColorSetting;
import com.google.android.material.tabs.TabLayout;

/**
 * 主活动
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tab_main);
        ViewPager viewPager = findViewById(R.id.vp_main);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager(),
                new HomeFragment(), new MoreFragment());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_AUTO);

        Intent intent = new Intent(this, PlayerControlService.class);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalCache.createDiskLruCache(this);
        StatusBarColorSetting.setColor(this, R.color.app_gray_light, false);
    }

}