package com.example.bozhilun.android.activity;

import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.adpter.NewFragmentAdapter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.fragment.DataReportStepItemFragment;
import com.example.bozhilun.android.fragment.DataReportStepItemSleepFragment;
import com.example.bozhilun.android.fragment.DataReportStepItemSleeptwoFragment;
import com.example.bozhilun.android.fragment.DataReportStepMoothFragment;
import com.example.bozhilun.android.fragment.DataReportStepMoothSleepFragment;
import com.example.bozhilun.android.fragment.DataReportStepWeekFragment;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.widget.NavigationTabStrip;
import com.flipboard.bottomsheet.BottomSheetLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by thinkpad on 2017/3/24.
 * 睡眠主页
 */

public class DataReportItemSleepActivity extends BaseActivity {
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.stepitem_viewpager) ViewPager stepitemViewpager;
    @BindView(R.id.nts_top) NavigationTabStrip ntsTop;


    private List<Fragment> fragments;
    private List<String> fragment_titles;
    @BindView(R.id.record_bottomsheet_my)
    BottomSheetLayout recordBottomsheet;
    @Override
    protected int getStatusBarColor() {return R.color.colorPrimary;}//设置toobar颜色
    @Override
    protected void initViews() {
        tvTitle.setText(getResources().getString(R.string.sleep_repor));
        fragments = new ArrayList<>();
        fragment_titles = new ArrayList<>();
        fragment_titles.add(getString(R.string.data_report_day));
        fragment_titles.add(getString(R.string.data_report_week));
        fragment_titles.add(getString(R.string.data_report_month));
        fragments.add(new DataReportStepItemSleepFragment());
        fragments.add(new DataReportStepItemSleeptwoFragment());
        fragments.add(new DataReportStepMoothSleepFragment());
        NewFragmentAdapter fragmentPagerAdapter = new NewFragmentAdapter(getSupportFragmentManager(), fragments, fragment_titles);
        //给ViewPager设置适配器
        stepitemViewpager.setAdapter(fragmentPagerAdapter);
        //将TabLayout和ViewPager关联起来。
        ntsTop.setViewPager(stepitemViewpager, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record, menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(Common.isFastClick())  {
                Date timedf=new Date();
                SimpleDateFormat formatdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String xXXXdf=formatdf.format(timedf);
                String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf+".png";
                ScreenShot.shoot(DataReportItemSleepActivity.this, new File(filePath));
                Common.showShare(DataReportItemSleepActivity.this, null, false,filePath);}
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_datareport_sleep_item;
    }
}
