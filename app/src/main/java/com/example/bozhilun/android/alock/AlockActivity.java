/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.bozhilun.android.alock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.BaseActivity;
import com.example.bozhilun.android.fragment.AlarmClockFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 天气闹钟主Activity
 *
 * @author 咖枯
 * @version 1.0 2015/04/12
 */
public class AlockActivity extends BaseActivity {

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager mFm;


    /**
     * 滑动菜单视图
     */
    private ViewPager mViewPager;

    /**
     * Tab页面集合
     */
    private List<Fragment> mFragmentList;



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 禁止滑动后退
        setSwipeBackEnable(false);
//        startService(new Intent(this, DaemonService.class));
        setContentView(R.layout.alock_bewblue);
        // 设置主题壁纸
       // setThemeWallpaper();

        mFm = getSupportFragmentManager();
        // Tab选中文字颜色
        // 初始化布局元素
        initViews();
    }

    /**
     * 设置主题壁纸
     */
    private void setThemeWallpaper() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.llyt_activity_main);
        MyUtil.setBackground(vg, this);
    }


    /**
     * 获取布局元素，并设置事件
     */
    private void initViews() {
        // 设置Tab页面集合
        mFragmentList = new ArrayList<>();
        // 展示闹钟的Fragment
        AlarmClockFragment mAlarmClockFragment = new AlarmClockFragment();
        mFragmentList.add(mAlarmClockFragment);

        // 设置ViewPager
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(mFm));
        mViewPager.setCurrentItem(0);
        // 设置一边加载的page数
        mViewPager.setOffscreenPageLimit(1);

    }

    /**
     * ViewPager适配器
     */
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }


}
