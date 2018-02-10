package com.example.bozhilun.android.b15p.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18ireceiver.RefreshBroadcastReceivers;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.fragment.fram_child.B15pBloodFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child.ChildHeartFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child.ChildSleepFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child.ChildSprotFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.B15pFragmentPagerAdapter;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.RefreshListenter;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.h9.utils.CusRefreshLayout;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.view.BatteryView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.model.datas.BatteryData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class B15pRecordFragment extends Fragment {

    private static final String TAG = "B15pRecordFragment";
    static CusRefreshLayout swipeRefresh;
    View b15pRecordView;
    private static int PAGES = 0;//当前页面

    @BindView(R.id.text_stute)
    TextView textStute;
    @BindView(R.id.b18i_viewpager)
    ViewPager b18iViewpager;
    @BindView(R.id.nextImage)
    ImageView nextImage;
    @BindView(R.id.line_pontion)
    LinearLayout linePontion;
    @BindView(R.id.b18irecordFm)
    LinearLayout b18irecordFm;
    Unbinder unbinder;
    //连接状态显示，左上角
    @BindView(R.id.battery_watch_connectStateTv)
    TextView batteryWatchConnectStateTv;
    //电池图标
    @BindView(R.id.batteryTopView)
    BatteryView batteryTopView;
    //显示电量
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    //日期
    @BindView(R.id.battery_watch_recordtop_dateTv)
    TextView batteryWatchRecordtopDateTv;
    //分享
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView batteryWatchRecordShareImg;
    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;

    private List<Fragment> fragmentList;
    private boolean mReceiverTag = false;
    public static final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
    boolean isHidden = false;

    public B15pRecordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regeditReceiver();  //注册广播
    }

    //注册广播
    private void regeditReceiver() {
        if (!mReceiverTag) {
            IntentFilter intFilter = new IntentFilter();
            intFilter.addAction(H9CONNECT_STATE_ACTION);
            mReceiverTag = true;
            getContext().registerReceiver(broadReceiver, intFilter);
        }
    }

    public static CusRefreshLayout getSwipeRefresh() {
        return swipeRefresh;
    }

    public static int getPAGES() {
        return PAGES;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b15pRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
        unbinder = ButterKnife.bind(this, b15pRecordView);

        initViews();

        initAddDatas();
        initPagerOnclick();

        return b15pRecordView;
    }

    private void initViews() {
        batteryWatchRecordtopDateTv.setText(""+WatchUtils.getCurrentDate());
        swipeRefresh = (CusRefreshLayout) b15pRecordView.findViewById(R.id.swipeRefresh);
    }

    @Override
    public void onStart() {
        super.onStart();
        isHidden = true;
        Log.d(TAG, "重新加载页面");
        if (MyCommandManager.DEVICENAME != null) {
            getDatas();
            SynchronousData();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null) {
            batteryLayout.setVisibility(View.VISIBLE);
            batteryWatchConnectStateTv.setText("" + "connect" + "");
            batteryWatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
            AnimationUtils.stopFlick(batteryWatchConnectStateTv);
        } else {
            batteryLayout.setVisibility(View.INVISIBLE);
            batteryWatchConnectStateTv.setText("" + "disconn.." + "");
            batteryWatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(batteryWatchConnectStateTv);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isHidden = false;
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            getContext().unregisterReceiver(broadReceiver);
        }
    }

    /**
     * 添加页面
     */
    private void initAddDatas() {
        fragmentList = new ArrayList<>();
        fragmentList.clear();
        fragmentList.add(new ChildSprotFragment());
        fragmentList.add(new ChildHeartFragment());
        fragmentList.add(new ChildSleepFragment());
        fragmentList.add(new B15pBloodFragment());
        FragmentManager supportFragmentManager = getChildFragmentManager();
        B15pFragmentPagerAdapter b15pFragmentPagerAdapter = new B15pFragmentPagerAdapter(supportFragmentManager, fragmentList);
        b18iViewpager.setAdapter(b15pFragmentPagerAdapter);
        b18iViewpager.setOffscreenPageLimit(3);
        initPiont();
        //手动刷新
        swipeRefresh.setOnRefreshListener(new RefreshListenter());
    }

    /**
     * 初始化点
     */
    private void initPiont() {
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(4, 0, 4, 0);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
            if (i == 0) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
            }
            imageView.setMaxHeight(1);
            imageView.setMaxWidth(1);
            imageView.setMinimumHeight(1);
            imageView.setMinimumWidth(1);
            linePontion.addView(imageView);
        }
    }

    /**
     * 页面改变监听
     */
    private void initPagerOnclick() {
        b18iViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PAGES = position;
                for (int j = 0; j < fragmentList.size(); j++) {
                    ImageView childAt1 = (ImageView) linePontion.getChildAt(j);
                    childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
                    childAt1.setMaxHeight(1);
                    childAt1.setMaxWidth(1);
                }
                ImageView childAt = (ImageView) linePontion.getChildAt(position);
                childAt.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
                childAt.setMaxHeight(1);
                childAt.setMaxWidth(1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 显示电量
     *
     * @param battery
     */
    private void setBatteryPowerShow(int battery) {
        if (!getActivity().isFinishing()) {
            try {
                batteryTopView.setColor(R.color.black);
                batteryTopView.setPower(battery);
                batteryPowerTv.setText(battery + "%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //广播接收者，接收连接状态广播信息
    private BroadcastReceiver broadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                try {
                    String h9Redata = intent.getStringExtra("h9constate");
                    if (!WatchUtils.isEmpty(h9Redata)) {
                        if (h9Redata.equals("conn") && !getActivity().isFinishing()) {    //已链接
                            batteryLayout.setVisibility(View.VISIBLE);
                            MyCommandManager.DEVICENAME = "B15P";
                            batteryWatchConnectStateTv.setText("" + "connect" + "");
                            batteryWatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                            AnimationUtils.stopFlick(batteryWatchConnectStateTv);
                            if (isHidden) {
                                textStute.setText(getResources().getString(R.string.connted));
                                textStute.setVisibility(View.INVISIBLE);
                            }
                            if (MyApp.isOne) {
                                MyApp.isOne = false;
                                textStute.setVisibility(View.VISIBLE);
                                textStute.setText(getResources().getString(R.string.syncy_data));
                                getDatas();
                                Intent intents = new Intent();
                                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intents.setAction("com.example.bozhilun.android.RefreshBroad");
                                getContext().sendBroadcast(intents);
                            }
                        } else {
                            batteryLayout.setVisibility(View.INVISIBLE);
                            MyCommandManager.DEVICENAME = null;
                            MyApp.isOne = true;
                            batteryWatchConnectStateTv.setText("" + "disconn.." + "");
                            batteryWatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                            AnimationUtils.startFlick(batteryWatchConnectStateTv);
                            if (isHidden) {
                                textStute.setText(getResources().getString(R.string.disconnted));
                                textStute.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 获取数据
     */
    private void getDatas() {
        initBraceletDatas();
    }

    /**
     * 获取电池
     */
    private void initBraceletDatas() {
        if(MyCommandManager.DEVICENAME != null){
            MyApp.getVpOperateManager().readBattery(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {

                }
            }, new IBatteryDataListener() {
                @Override
                public void onDataChange(BatteryData batteryData) {
                    String message = "电池等级:\n" + batteryData.getBatteryLevel() + "\n" + "电量:" + batteryData.getBatteryLevel() * 25 + "%";
                    Log.d(TAG, message);
                    if (isHidden) {
                        textStute.setVisibility(View.INVISIBLE);
                    }
                    setBatteryPowerShow(batteryData.getBatteryLevel() * 25);
                }
            });
        }
    }


    /**
     * 自动同步数据
     */
    public void SynchronousData() {

        RefreshBroadcastReceivers.setMyCallBack(new RefreshBroadcastReceivers.MyCallBack() {
            @Override
            public void setMyCallBack(Message msg) {
                if (msg.what == RefreshBroadcastReceivers.MessageNumber) {
                    if (isHidden) {
                        textStute.setText(getContext().getString(R.string.syncy_data));
                        textStute.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "B15p---电池，链接状态---同步成功");
                    getDatas();
                    RefreshBroadcastReceivers.getMyHandler().removeMessages(RefreshBroadcastReceivers.MessageNumber);
                }
            }
        });
    }

    @OnClick({R.id.watch_poorRel, R.id.battery_watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_poorRel:    //电池

                break;
            case R.id.battery_watchRecordShareImg:  //分享
                startActivity(new Intent(getActivity(), SharePosterActivity.class));
                break;
        }
    }
}
