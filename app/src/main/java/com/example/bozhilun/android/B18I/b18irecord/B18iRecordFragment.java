package com.example.bozhilun.android.B18I.b18irecord;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.B18iCommon;
import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.AxisValue;
import com.example.bozhilun.android.B18I.b18ibean.Line;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.b18iutils.FanItem;
import com.example.bozhilun.android.B18I.b18iutils.OnFanItemClickListener;
import com.example.bozhilun.android.B18I.b18iview.CircleProgress;
import com.example.bozhilun.android.B18I.b18iview.LeafLineChart;
import com.example.bozhilun.android.B18I.b18iview.PieChartView;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.SearchDeviceActivity;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.siswatch.WatchStrapActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import cn.appscomm.bluetooth.model.HeartRateData;
import cn.appscomm.bluetooth.model.SleepData;
import cn.appscomm.bluetooth.model.SportCacheData;

import static com.example.bozhilun.android.B18I.B18IHomeActivity.B18ICONNECT_ACTION;

/**
 * Created by Administrator on 2017/8/28.
 */

/**
 * B18I 记录页面
 */
public class B18iRecordFragment extends Fragment {
    private static final String TAG = "--B18iRecordFragment";
    View b18iRecordView;
    @BindView(R.id.watch_poorRel)
    RelativeLayout watchPoorRel;
    @BindView(R.id.watch_recordtop_dateTv)
    TextView watchRecordtopDateTv;
    @BindView(R.id.watchRecordShareImg)
    ImageView watchRecordShareImg;
    Unbinder unbinder;

    @BindView(R.id.b18i_viewpager)
    ViewPager l38iViewpager;
    @BindView(R.id.line_pontion)
    LinearLayout linePontion;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.previousImage)
    ImageView previousImage;
    @BindView(R.id.nextImage)
    ImageView nextImage;
    @BindView(R.id.shouhuanImg)
    ImageView shouhuanImg;
    @BindView(R.id.watch_connectStateTv)
    TextView watchConnectStateTv;
    //显示连接状态


    private boolean fanRoateAniamtionStart;
    private int PAGES = 0;//页码
    private float GOAL = 2000;//默认目标
    private float STEP = 0;//步数
    private int DISTANCE = 0;//距离
    private int CALORIES = 0;//卡路里

    private int AUTOHEART = 0;//心率
    List<HeartRateData> heartRateDatas;//心率

    List<SleepData> sleepDatas;//睡眠
    int AWAKE = 0;//清醒
    int DEEP = 0;//深睡
    int SHALLOW = 0;//浅睡

    private boolean mReceiverTag = false;   //广播接受者标识

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    //注册监听连接状态的广播
    private void registerReceiver() {
        if (!mReceiverTag) {     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            IntentFilter mFileter = new IntentFilter();
            mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
            mFileter.addAction(B18ICONNECT_ACTION);
            MyApp.getInstance().registerReceiver(b18iReceiver, mFileter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b18iRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
        unbinder = ButterKnife.bind(this, b18iRecordView);
        initViews();
        setDatas();
        ImageClick();
        return b18iRecordView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (B18iCommon.ISCHECKTARGET) {
            B18iCommon.ISCHECKTARGET = false;
            //获取目标
            BluetoothSDK.getGoalSetting(B18iResultCallBack.getB18iResultCallBack());
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BluetoothSDK.isConnected()) {
            watchConnectStateTv.setText("connect");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            AnimationUtils.stopFlick(watchConnectStateTv);
        } else {
            watchConnectStateTv.setText("discon..");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(watchConnectStateTv);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            MyApp.getInstance().unregisterReceiver(b18iReceiver);   //注销广播
        }
    }

    private void initViews() {

        //获取目标
        BluetoothSDK.getGoalSetting(B18iResultCallBack.getB18iResultCallBack());
        //获取运动数据
        BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
        //手动刷新
        swipeRefresh.setOnRefreshListener(new RefreshListenter());
        watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
        shouhuanImg.setImageResource(R.mipmap.icon_b18i_left_show);
    }

    /**
     * （上/下）一页
     */
    private void ImageClick() {
        previousImage.setVisibility(View.GONE);
        nextImage.setVisibility(View.GONE);
        previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "-----page----" + l38iViewpager.getCurrentItem() + "");
                l38iViewpager.setCurrentItem(l38iViewpager.getCurrentItem() - 1, true);
            }
        });

        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l38iViewpager.setCurrentItem(l38iViewpager.getCurrentItem() + 1, true);
            }
        });
    }

    /**
     * envent数据返回
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "stepGoal":
                //目标步数设置成功----》获取目标更新
                BluetoothSDK.getGoalSetting(B18iResultCallBack.getB18iResultCallBack());
                break;
            case "goal":
                //目标步数获取成功
                Object[] objects = (Object[]) event.getObject();
                if (PAGES == 0) {
                    if (objects[0] != null) {
                        GOAL = Float.valueOf(objects[0].toString());
                        circleprogress.setMaxValue(GOAL);
                        circleprogress.invalidate();
                    }
                }
                break;
            case "sportData":
                //获取运动步数成功
                List<SportCacheData> sportCacheDatas = (List<SportCacheData>) event.getObject();
                sportData(sportCacheDatas);
                break;
            case "heartRate":
                //获取心率数据成功
                heartRateDatas = (List<HeartRateData>) event.getObject();
                heartRate();
                break;
            case "sleepData":
                //获取到睡眠数据
                sleepData(event);
                break;
            default:
                break;
        }
    }

    /**
     * 睡眠数据获取成功
     *
     * @param event
     */
    private void sleepData(B18iEventBus event) {
        if (PAGES == 2) {
            sleepDatas = (List<SleepData>) event.getObject();
            setPieChart();
            awakeSleep.setText(String.valueOf(AWAKE));
            shallowSleep.setText(String.valueOf(SHALLOW));
            deepSleep.setText(String.valueOf(DEEP));
            pieChartView.invalidate();
        }
    }

    /**
     * 心率获取成功
     *
     * @param
     */
    private void heartRate() {
        if (PAGES == 1) {
            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
            String s = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取

            for (int i = 0; i < heartRateDatas.size(); i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp));//时间戳转换
                String s1 = B18iUtils.interceptString(strTimes, 0, 10);
//                Log.e(TAG, "s---time----" + s + "==系统当前时间与手环每条数据时间对比==" + s1);
                if (s.equals(s1)) {
                    AUTOHEART = heartRateDatas.get(i).avg;
                }
            }

            autoHeartText.setText(String.valueOf(AUTOHEART));
            initLineChart();
            leafLineChart.invalidate();
//            for (int i = 0; i < heartRateDatas.size(); i++) {
//                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp));//时间戳转换
//                String s1 = B18iUtils.interceptString(strTimes, 0, 10);
//                Log.e(TAG, "s---time----" + s + "==系统当前时间与手环每条数据时间对比==" + s1);
//                if (s.equals(s1)) {
//                    AUTOHEART = heartRateDatas.get(i).avg;
//                }
//            }
//            autoHeartText.setText(String.valueOf(AUTOHEART));
//            initLineChart();
//            leafLineChart.invalidate();
        }
    }

    /**
     * 步数获取成功
     */
    private void sportData(List<SportCacheData> sportCacheDatas) {
        if (PAGES == 0) {
            STEP = 0;
            DISTANCE = 0;
            CALORIES = 0;
            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
            String s = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取
            for (int i = 0; i < sportCacheDatas.size(); i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(sportCacheDatas.get(i).timestamp));//时间戳转换
                String s1 = B18iUtils.interceptString(strTimes, 0, 10);
//                Log.e(TAG, "s---time----" + s + "==系统当前时间与手环每条数据时间对比==" + s1);
                if (s.equals(s1)) {
                    STEP += sportCacheDatas.get(i).step;
                    DISTANCE += sportCacheDatas.get(i).distance;
                    CALORIES += sportCacheDatas.get(i).calories;
                }
            }
            if (STEP >= 0) {
                circleprogress.reset();
                circleprogress.setValue(STEP);
                circleprogress.invalidate();
            }
            if (DISTANCE >= 0) {
                double div = WatchUtils.div(Double.valueOf(DISTANCE), Double.valueOf(1000), 2);
                L38iDisT.setText(String.valueOf(div));
            }
            if (CALORIES >= 0) {
                L38iCalT.setText(String.valueOf(CALORIES / 1000));
            }
        }
    }


    //    View ----- 中的子控件
    private CircleProgress circleprogress;
    private LeafLineChart leafLineChart;
    private PieChartView pieChartView;
    TextView L38iCalT, L38iDisT;
    TextView autoHeartText;
    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep;

    /**
     * view pager数据
     */
    private void setDatas() {

        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.b18i_circle_progress_view, null, false);
        circleprogress = (CircleProgress) view1.findViewById(R.id.circleprogress);
        L38iCalT = (TextView) view1.findViewById(R.id.l38i_recordKcalTv);
        L38iDisT = (TextView) view1.findViewById(R.id.l38i_recordMileTv);
        L38iCalT.setText(String.valueOf(CALORIES));
        L38iDisT.setText(String.valueOf(DISTANCE));
        circleprogress.reset();
        circleprogress.setMaxValue(GOAL);
        circleprogress.setValue(STEP);
        circleprogress.setPrecision(0);
        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.b18i_leaf_linechart_view, null, false);
        leafLineChart = (LeafLineChart) view2.findViewById(R.id.leaf_chart);
        autoHeartText = (TextView) view2.findViewById(R.id.autoHeart_text);
        autoHeartText.setText(String.valueOf(AUTOHEART));
        View view3 = LayoutInflater.from(getContext()).inflate(R.layout.b18i_pie_chart_view, null, false);
        pieChartView = (PieChartView) view3.findViewById(R.id.pieChartView);
        awakeState = (TextView) view3.findViewById(R.id.awakeState);
        shallowState = (TextView) view3.findViewById(R.id.shallowState);
        deepState = (TextView) view3.findViewById(R.id.deepState);
        awakeSleep = (TextView) view3.findViewById(R.id.awake_sleep);
        shallowSleep = (TextView) view3.findViewById(R.id.shallow_sleep);
        deepSleep = (TextView) view3.findViewById(R.id.deep_sleep);
        awakeSleep.setText(String.valueOf(AWAKE));
        shallowSleep.setText(String.valueOf(SHALLOW));
        deepSleep.setText(String.valueOf(DEEP));
        awakeState.setText(getResources().getString(R.string.waking_state));//清醒状态
        shallowState.setText(getResources().getString(R.string.shallow_sleep));//浅睡眠
        deepState.setText(getResources().getString(R.string.deep_sleep));//深睡眠
        List<View> fragments = new ArrayList<>();
        fragments.add(view1);
        fragments.add(view2);
        fragments.add(view3);
        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
        l38iViewpager.setCurrentItem(3);
        setLinePontion(fragments);
        l38iViewpager.setAdapter(adapter);
        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
    }


    /**
     * 滑动小圆点
     *
     * @param fragments
     */
    private void setLinePontion(List<View> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.rous));
            imageView.setAlpha(80);
            if (i == 0) {
                imageView.setAlpha(225);
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.rous));
            }
            imageView.setMaxHeight(1);
            imageView.setMaxWidth(1);
            imageView.setMinimumHeight(1);
            imageView.setMinimumWidth(1);
            linePontion.addView(imageView);
        }
    }


    /**
     * ViewPager页面改变监听
     */
    private class PagerChangeLister implements ViewPager.OnPageChangeListener {
        List<View> fragments;

        public PagerChangeLister(List<View> fragments) {
            this.fragments = fragments;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            PAGES = position;
            PointSetting(position);
            switch (position) {
                case 0:
                    circleprogress.reset();
                    circleprogress.setMaxValue(GOAL);
                    circleprogress.setValue(STEP);
                    circleprogress.setPrecision(0);
                    break;
                case 1:
                    //获取心率数据
                    BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
                    initLineChart();
                    break;
                case 2:
                    //获取睡眠数据
                    BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());
                    setPieChart();
                    break;
            }
        }

        private void PointSetting(int position) {
            l38iViewpager.setCurrentItem(position);
            for (int j = 0; j < fragments.size(); j++) {
                ImageView childAt1 = (ImageView) linePontion.getChildAt(j);
                childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.rous));
                childAt1.setMaxHeight(1);
                childAt1.setMaxWidth(1);
                childAt1.setAlpha(80);
            }
            ImageView childAt = (ImageView) linePontion.getChildAt(position);
            childAt.setImageDrawable(getResources().getDrawable(R.mipmap.rous));
            childAt.setMaxHeight(1);
            childAt.setMaxWidth(1);
            childAt.setAlpha(225);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @OnClick({R.id.watch_poorRel, R.id.watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_poorRel:    //手环是否连接
                if (BluetoothSDK.isConnected()) { //已经连接
//                    BluetoothSDK.disConnect(resultCallBack);
//                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "mylanya", null);//清空标识
                    startActivity(new Intent(getActivity(), WatchStrapActivity.class));
                } else {  //未连接
                    Intent intent = new Intent(getActivity(), SearchDeviceActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                break;
            case R.id.watchRecordShareImg:  //分享
                doShareData();  //分享
                break;
        }
    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_DISCONNECT:
                    Log.e(TAG, "---disconect---断开连接");
                    BluetoothSDK.getSN(B18iResultCallBack.getB18iResultCallBack());
                    Intent intent = new Intent(getActivity(), SearchDeviceActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };


    /*************   --------折线图----------    *************/
    private void initLineChart() {
        Axis axisX = new Axis(getAxisValuesX());
        axisX.setAxisLineColor(Color.parseColor("#43FFFFFF"));
        axisX.setAxisLineWidth(0.5f);
        axisX.setTextColor(Color.WHITE);
        axisX.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(true).setShowText(true);
        Axis axisY = new Axis(getAxisValuesY());
        axisY.setTextColor(Color.WHITE);
        axisY.setAxisLineWidth(0f);
        axisY.setShowLines(false);
        axisY.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(false).setShowText(true);
        leafLineChart.setAxisX(axisX);
        leafLineChart.setAxisY(axisY);
        List<Line> lines = new ArrayList<>();
        lines.add(getFoldLineTest());
        leafLineChart.setChartData(lines);
        leafLineChart.showWithAnimation(1000);
        leafLineChart.show();
    }

    /**
     * X轴值
     *
     * @return
     */
    private List<AxisValue> getAxisValuesX() {
        List<AxisValue> axisValues = new ArrayList<>();

        for (int i = 0; i <= 24; i++) {
            AxisValue value = new AxisValue();
            if (i % 3 != 0) {
                value.setLabel("");
            } else {
                value.setLabel(i + "");
            }

            axisValues.add(value);
        }
//        for (int i = 0; i <= 8; i++) {
//            AxisValue value = new AxisValue();
//            value.setLabel(i * 3 + "");
//            axisValues.add(value);
//        }
//            value.setLabel(i * 3 + "");
//        for (int i = 0; i < heartRateDatas.size(); i++) {
//            long timestamp = heartRateDatas.get(i).timestamp;
//            Date date = new Date(timestamp);
//            SimpleDateFormat sd = new SimpleDateFormat("HH");
//            String format = sd.format(date);
//        }
        return axisValues;
    }

    /**
     * Y轴值
     *
     * @return
     */
    private List<AxisValue> getAxisValuesY() {
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AxisValue value = new AxisValue();
            value.setLabel(" ");
//            if (i != 0) {
//                value.setLabel(String.valueOf((i) * 50));
//            } else {
//                value.setLabel(" ");
//            }
            axisValues.add(value);
        }
        return axisValues;
    }

    /**
     * 设置值
     *
     * @return
     */
    private Line getFoldLineTest() {

        List<PointValue> pointValues = new ArrayList<>();
        List<String> timeString = new ArrayList<>();
        List<Integer> heartString = new ArrayList<>();
        String systemTimer = B18iUtils.getSystemTimer();
        String s = B18iUtils.interceptString(systemTimer, 0, 10);
        if (heartRateDatas != null) {
            for (int i = 0; i < heartRateDatas.size(); i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp));
                if (s.equals(B18iUtils.interceptString(strTimes, 0, 10))) {
                    int avg = heartRateDatas.get(i).avg;
                    if (!heartString.contains(avg)) {
                        heartString.add(avg);
                    }
                    String sysTim = B18iUtils.interceptString(
                            B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp)), 11, 13);
                    if (!timeString.contains(sysTim)) {
                        timeString.add(sysTim);
                    }
                    Collections.sort(timeString);
                }
            }
        }

        for (int i = 0; i < timeString.size(); i++) {
            PointValue value = new PointValue();
            value.setX(Integer.valueOf(timeString.get(i)) / 24f);
            value.setY(Integer.valueOf(heartString.get(i)) / 250f);
            pointValues.add(value);
        }
        Line line = new Line(pointValues);
        line.setLineColor(Color.parseColor("#FFFFFF"))
                .setLineWidth(1f)
                .setHasPoints(false)//是否显示点
                .setPointColor(Color.WHITE)
                .setCubic(true)
                .setPointRadius(2)
                .setFill(false)
                .setFillColor(Color.parseColor("#FFFFFF"))
                .setHasLabels(false)
                .setLabelColor(Color.parseColor("#0C33B5E5"));//33B5E5
        return line;
//        if (heartRateDatas != null) {
//            List<String> timeString = new ArrayList<>();
//            List<Integer> heartString = new ArrayList<>();
//            String systemTimer = B18iUtils.getSystemTimer();
//            String s = B18iUtils.interceptString(systemTimer, 0, 10);
//            for (int i = 0; i < heartRateDatas.size(); i++) {
//                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp));
//                if (s.equals(B18iUtils.interceptString(strTimes, 0, 10))) {
//                    int avg = heartRateDatas.get(i).avg;
//                    if (!heartString.contains(avg)) {
//                        heartString.add(avg);
//                    }
//                    String sysTim = B18iUtils.interceptString(
//                            B18iUtils.getStrTimes(String.valueOf(heartRateDatas.get(i).timestamp)), 11, 13);
//                    if (!timeString.contains(sysTim)) {
//                        timeString.add(sysTim);
//                    }
//                    Collections.sort(timeString);
//                }
//            }
//            for (int i = 0; i < timeString.size(); i++) {
//                PointValue value = new PointValue();
//                value.setX(Integer.valueOf(timeString.get(i)) / 24f);
//                value.setY(Integer.valueOf(heartString.get(i)) / 250f);
//                pointValues.add(value);
//            }
//        }
//        Line line = new Line(pointValues);
//        line.setLineColor(Color.parseColor("#FFFFFF"))
//                .setLineWidth(1f)
//                .setHasPoints(true)
//                .setPointColor(Color.WHITE)
//                .setCubic(true)
//                .setPointRadius(2)
//                .setFill(false)
//                .setFillColor(Color.parseColor("#FFFFFF"))
//                .setHasLabels(false)
//                .setLabelColor(Color.parseColor("#0C33B5E5"));//33B5E5
//        return line;
    }

    /******************        ---------  扇形----------               **********************/
    public void setPieChart() {
        AWAKE = 0;
        DEEP = 0;
        SHALLOW = 0;
        int total = 0;
        String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
        String s = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取

        //SleepData{id=0, total sleep=35, awake=35, light sleep=0, deep sleep=0,
        // detail='2017-09-26T08:51:31&BEGIN,2017-09-26T08:51:31&AWAKE,2017-09-26T09:26:53&AWAKE,
        // 2017-09-26T09:26:54&END,', date='2017-09-26', flag=-1, type=0, timeStamp=0}
        if (sleepDatas != null) {
            for (int i = 0; i < sleepDatas.size(); i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).timeStamp));//时间戳转换
                String s1 = B18iUtils.interceptString(strTimes, 0, 10);
                Log.e(TAG, "系统时间：" + s + "======== 读取睡眠数据时间：" + s1);
                Log.e(TAG, sleepDatas.get(i).total + "===" +
                        sleepDatas.get(i).awake + "===" +
                        sleepDatas.get(i).deep + "===" +
                        sleepDatas.get(i).light + "===" +
                        sleepDatas.get(i).timeStamp);
                if (s.equals(s1)) {
                    total += sleepDatas.get(i).total;
                    DEEP += sleepDatas.get(i).deep;//深睡眠
                    SHALLOW += sleepDatas.get(i).light;//浅睡
                    AWAKE += sleepDatas.get(i).awake;//清醒
                }
//                AWAKE = total - (DEEP + SHALLOW);//清醒
                Log.e(TAG, "s---time--深睡，浅睡，清醒--" + DEEP + "===" + SHALLOW + "==" + AWAKE);
            }
        }
        pieChartView.setFanClickAbleData(
                new double[]{DEEP, SHALLOW, AWAKE},
                new int[]{Color.parseColor("#4CFFFFFF"), Color.parseColor("#7FFFFFFF"), Color.WHITE}, 0.08);
        pieChartView.setIsFistOffSet(false);
        pieChartView.setOnFanClick(new OnFanItemClickListener() {
            @Override
            public void onFanClick(final FanItem fanItem) {
                if (!fanRoateAniamtionStart) {
                    float to;
                    float centre = (fanItem.getStartAngle() * 2 + fanItem.getAngle()) / 2;
                    if (centre >= 270) {
                        to = 360 - centre + 90;
                    } else {
                        to = 90 - centre;
                    }
                    RotateAnimation animation = new RotateAnimation(0, to, pieChartView.getFanRectF().centerX(), pieChartView.getFanRectF().centerY());
                    animation.setDuration(800);
                    animation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            fanRoateAniamtionStart = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            pieChartView.setToFirst(fanItem);
                            pieChartView.clearAnimation();
                            pieChartView.invalidate();
                            fanRoateAniamtionStart = false;
//                            Toast.makeText(getContext(), "当前选中:" + fanItem.getPercent() + "%", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "----------------当前选中:" + fanItem.getPercent() + "%");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    animation.setFillAfter(true);
                    pieChartView.startAnimation(animation);
                }
            }
        });
    }

    /**
     * 内部Adapter
     */
    public class MyHomePagerAdapter extends PagerAdapter {
        List<View> stringList;

        public MyHomePagerAdapter(List<View> stringList) {
            this.stringList = stringList;
        }

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(stringList.get(position));
            return stringList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(stringList.get(position));
        }
    }

    /**
     * 手动刷新
     */
    private class RefreshListenter implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            switch (PAGES) {
                case 0:
                    //获取运动数据
                    BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
                    //获取目标
                    BluetoothSDK.getGoalSetting(B18iResultCallBack.getB18iResultCallBack());
                    break;
                case 1:
                    //获取心率数据
                    BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
                    initLineChart();
                    break;
                case 2:
                    BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());//获取睡眠数据
                    setPieChart();
                    break;
            }
            swipeRefresh.setRefreshing(false);
        }
    }

    //接收连接状态的广播
    private BroadcastReceiver b18iReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(B18ICONNECT_ACTION)) {
                try {
                    String conStateData = intent.getStringExtra("b18iconstate");
                    if (!WatchUtils.isEmpty(conStateData)) {
                        if (conStateData.equals("b18iconn")) { //已连接
                            watchConnectStateTv.setText("" + "connect" + "");
                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                            AnimationUtils.stopFlick(watchConnectStateTv);
                        } else {
                            watchConnectStateTv.setText("" + "disconn.." + "");
                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                            AnimationUtils.startFlick(watchConnectStateTv);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    //分享
    private void doShareData() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
        ScreenShot.shoot(getActivity(), new File(filePath));
        com.example.bozhilun.android.util.Common.showShare(getActivity(), null, false, filePath);
    }
}
