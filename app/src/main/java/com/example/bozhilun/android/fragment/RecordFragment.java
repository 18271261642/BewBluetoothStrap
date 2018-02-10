package com.example.bozhilun.android.fragment;

import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.adpter.NewFragmentAdapter;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.ToastUtil;
import com.flipboard.bottomsheet.BottomSheetLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/6.
 */

public class RecordFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.movesleep_viewpager)
    ViewPager movesleep_viewpager;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.bottomsheet_record)
    BottomSheetLayout bottomSheetLayout;
    private List<Fragment> fragments;
    private List<String> fragment_titles;

    @Override
    protected void initViews() {

        tvTitle.setText(R.string.recording);
        mPageName = "RecordFragment";
        fragments = new ArrayList<>();
        fragment_titles = new ArrayList<>();
        tabs.addTab(tabs.newTab().setText(R.string.move_ment));
        tabs.addTab(tabs.newTab().setText(R.string.sleep));
        fragment_titles.add(getString(R.string.move_ment));
        fragment_titles.add(getString(R.string.sleep));

        fragments.add(new SleepFragment());
        NewFragmentAdapter fragmentPagerAdapter = new NewFragmentAdapter(getChildFragmentManager(), fragments, fragment_titles);
        //给ViewPager设置适配器
        movesleep_viewpager.setAdapter(fragmentPagerAdapter);
        //将TabLayout和ViewPager关联起来。
        tabs.setupWithViewPager(movesleep_viewpager);
        //给TabLayout设置适配器
        tabs.setTabsFromPagerAdapter(fragmentPagerAdapter);
        toolbar.inflateMenu(R.menu.menu_record);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ToastUtil.showShort(getActivity(), "点击分享");
                return true;
            }
        });
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_record, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_record;
    }

    @OnClick({R.id.data_img, R.id.share_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.data_img:
                //startActivity(new Intent(getActivity(), DataReportActivity.class));
                break;
            case R.id.share_img:
              if(Common.isFastClick())  {
                  Date timedf=new Date();
                  SimpleDateFormat formatdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  String xXXXdf=formatdf.format(timedf);
                  String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf+".png";
                  ScreenShot.shoot(getActivity(), new File(filePath));
                  Common.showShare(getActivity(), null, false,filePath);
              }

                break;
        }
    }

}
