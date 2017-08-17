package com.guohua.sdk.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @file FragmentAdapter.java
 * @author Leo
 * @version 1
 * @detail 适配Fragment和ViewPager的适配器
 * @since 2016/12/16 16:43
 */

/**
 * 文件名：FragmentAdapter.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/16 16:43
 * 描  述：适配Fragment和ViewPager的适配器
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments; //数据源

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment) {
        this.fragments.add(fragment);
    }
}
