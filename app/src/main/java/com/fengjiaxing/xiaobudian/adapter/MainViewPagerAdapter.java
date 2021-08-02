package com.fengjiaxing.xiaobudian.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fengjiaxing.xiaobudian.fragment.HomeFragment;
import com.fengjiaxing.xiaobudian.fragment.MoreFragment;

/**
 * 首页ViewPager的适配器
 * */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private final HomeFragment homeFragment;
    private final MoreFragment moreFragment;

    public MainViewPagerAdapter(@NonNull FragmentManager fm, HomeFragment homeFragment, MoreFragment moreFragment) {
        super(fm);
        this.homeFragment = homeFragment;
        this.moreFragment = moreFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) return homeFragment;
        else return moreFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "首页&发现";
        else return "更多&关于";
    }
}
