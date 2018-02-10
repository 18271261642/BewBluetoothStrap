package com.example.bozhilun.android.b15p.fragment.fram_child_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/12/8 14:40
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B15pFragmentPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragmentList;

    public B15pFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
