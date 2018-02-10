package com.example.bozhilun.android.h9.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.B18iCommon;
import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.AxisValue;
import com.example.bozhilun.android.B18I.b18ibean.Line;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18ireceiver.RefreshBroadcastReceivers;
import com.example.bozhilun.android.B18I.b18isystemic.B18ISettingActivity;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.b18iview.CircleProgress;
import com.example.bozhilun.android.B18I.b18iview.LeafLineChart;
import com.example.bozhilun.android.B18I.b18iview.PieChartView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.h9monitor.UpDatasBase;
import com.example.bozhilun.android.h9.settingactivity.H9HearteDataActivity;
import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.h9.utils.CusRefreshLayout;
import com.example.bozhilun.android.h9.utils.Device_Time_Activity;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.view.BatteryView;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.sdk.bluetooth.bean.HeartData;
import com.sdk.bluetooth.bean.SleepData;
import com.sdk.bluetooth.bean.SportsData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.count.AllDataCount;
import com.sdk.bluetooth.protocol.command.count.SportSleepCount;
import com.sdk.bluetooth.protocol.command.data.DeviceDisplaySportSleep;
import com.sdk.bluetooth.protocol.command.data.GetHeartData;
import com.sdk.bluetooth.protocol.command.data.GetSleepData;
import com.sdk.bluetooth.protocol.command.data.GetSportData;
import com.sdk.bluetooth.protocol.command.data.SportSleepMode;
import com.sdk.bluetooth.protocol.command.device.BatteryPower;
import com.sdk.bluetooth.protocol.command.device.Unit;
import com.sdk.bluetooth.protocol.command.setting.AutoSleep;
import com.sdk.bluetooth.protocol.command.setting.GoalsSetting;
import com.sdk.bluetooth.utils.DateUtil;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/27 16:29
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9RecordFragment extends Fragment {

    private final String TAG = "H9RecordFragment";
    public static final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
    @BindView(R.id.previousImage)
    ImageView previousImage;
    @BindView(R.id.nextImage)
    ImageView nextImage;
    View b18iRecordView;
    Unbinder unbinder;
    @BindView(R.id.b18i_viewpager)
    ViewPager l38iViewpager;
    @BindView(R.id.text_stute)
    TextView textStute;
    @BindView(R.id.batteryLayout)
    LinearLayout batteryLayout;
    private int PAGES = 0;//页码
    @BindView(R.id.line_pontion)
    LinearLayout linePontion;
    private float GOAL = 7000;//默认目标
    private float STEP = 0;//步数
    @BindView(R.id.swipeRefresh)
    CusRefreshLayout swipeRefresh;//刷新控件
    //显示手表图标左上角
    @BindView(R.id.batteryshouhuanImg)
    ImageView shouhuanImg;
    //显示连接状态的TextView
    @BindView(R.id.battery_watch_connectStateTv)
    TextView watchConnectStateTv;
    //点击图标
    @BindView(R.id.watch_poorRel)
    LinearLayout watchPoorRel;
    //显示日期的TextView
    @BindView(R.id.battery_watch_recordtop_dateTv)
    TextView watchRecordtopDateTv;
    //分享
    @BindView(R.id.battery_watchRecordShareImg)
    ImageView watchRecordShareImg;
    //显示电量的图片
    @BindView(R.id.batteryTopView)
    BatteryView watchTopBatteryImgView;
    //显示电量
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    private boolean mReceiverTag = false;
    int kmormi; //距离显示是公制还是英制


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        regeditReceiver();
    }

    /**
     * 切换语言上下文置空处理
     */
    /**********************************************/
    private Context context;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (getView() != null) context = getActivity();
    }

    /**********************************************/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iRecordView = inflater.inflate(R.layout.fragment_b18i_record, container, false);
        unbinder = ButterKnife.bind(this, b18iRecordView);
        saveStateToArguments();
        setDatas();
        initViews();
        initStepList();
        String homeTime = (String) SharedPreferencesUtils.getParam(context, "homeTime", "");
        if (!TextUtils.isEmpty(homeTime)) {
            String timeDifference = H9TimeUtil.getTimeDifferencesec(homeTime, B18iUtils.getSystemDataStart());
            int number = Integer.valueOf(timeDifference.trim());
            int number2 = Integer.parseInt(timeDifference.trim());
            if (number >= 2 || number2 >= 2) {
                int nuber = Integer.valueOf(timeDifference.trim());
                if (!timeDifference.trim().equals("1")) {
                    getDatas();
                    SharedPreferencesUtils.setParam(context, "homeTime", B18iUtils.getSystemDataStart());
                }
            }
        } else {
            getDatas();
            SharedPreferencesUtils.setParam(getActivity(), "homeTime", B18iUtils.getSystemDataStart());
        }
        SynchronousData();//自动同步数据
//        timingDown();
        //时间长按去查看设备时间
        watchRecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getActivity(), Device_Time_Activity.class).putExtra("is18i", "H9"));
                return false;
            }
        });
        return b18iRecordView;
    }

    private boolean isHidden = true;//离开界面隐藏显示同步数据的

    @Override
    public void onStart() {
//       Log.d(TAG, "---vvv----onStart-------");
        super.onStart();
        isHidden = true;
        if (B18iCommon.ISCHECKTARGET) {
            B18iCommon.ISCHECKTARGET = false;
//            circleprogress.reset();
            //获取目标
//            getSportDatas();//获取运动数据
        }
        watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
        if (MyCommandManager.DEVICENAME != null) {
            watchConnectStateTv.setText("" + "connect" + "");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
            AnimationUtils.stopFlick(watchConnectStateTv);
            batteryLayout.setVisibility(View.VISIBLE);
        } else {
            watchConnectStateTv.setText("" + "disconn.." + "");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(watchConnectStateTv);
            batteryLayout.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "---vvv----onResume-------");
        watchRecordtopDateTv.setText(WatchUtils.getCurrentDate());
        if (MyCommandManager.DEVICENAME != null) {
            watchConnectStateTv.setText("" + "connect" + "");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
            AnimationUtils.stopFlick(watchConnectStateTv);

            if (MyApp.isOne) {
                if (isHidden) {
                    textStute.setText(getResources().getString(R.string.syncy_data));
                    textStute.setVisibility(View.VISIBLE);
                }
                getDatas();
                MyApp.isOne = false;
            }
//            //获取电池电量并显示
//            AppsBluetoothManager.getInstance(MyApp.context).sendCommand(new BatteryPower(commandResultCallback));//
//            //获取所有运动总数局，运动、心率、血压等等
//            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new AllDataCount(commandResultCallback));
//            //获取公里或者英里
//            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new Unit(commandResultCallback));
//            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new SportSleepMode(commandResultCallback));
//            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new GetSleepData(commandResultCallback, 0, 10, 10));
        } else {
//            Log.e(TAG, "----rrrrrrrrrrrrrrrrr-------断开连接-");
            if (isHidden) {
                textStute.setText(getResources().getString(R.string.disconnted));
                textStute.setVisibility(View.VISIBLE);
            }
            watchConnectStateTv.setText("" + "disconn.." + "");
            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(watchConnectStateTv);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG, "---vvv----onPause-------");
        isHidden = false;
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d(TAG, "---vvv----onStop-------");
        isHidden = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        saveStateToArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "------------------------onDestroy---------------H9RecordFragment-");
        if (mReceiverTag) {
            mReceiverTag = false;
            context.unregisterReceiver(broadReceiver);
        }
    }

    private void regeditReceiver() {
        if (!mReceiverTag) {
            IntentFilter intFilter = new IntentFilter();
            intFilter.addAction(H9CONNECT_STATE_ACTION);
            mReceiverTag = true;
            context.registerReceiver(broadReceiver, intFilter);
        }
    }


    /**
     * 定时读取手环数据
     */
    private Handler mHandler = new Handler();

    private void timingDown() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG, "自动同步");
//                Log.e(TAG, "----rrrrrrrrrrrrrrrrr---z----同步数据重-");
                if (isHidden) {
                    textStute.setText(getResources().getString(R.string.syncy_data));
                    textStute.setVisibility(View.VISIBLE);
                }
                getDatas();
                // 循环调用实现定时刷新界面
                mHandler.postDelayed(this, 10 * (1000 * 60));//300000ms = 5min  600000ms = 10 min
            }
        };
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, 10 * (1000 * 60));
    }


    private void initViews() {
        shouhuanImg.setImageResource(R.mipmap.h9);
        previousImage.setVisibility(View.GONE);
        nextImage.setVisibility(View.GONE);
        //手动刷新
        swipeRefresh.setOnRefreshListener(new RefreshListenter());
//        getDatas();//初次启动获取数据
    }

//    Handler myHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg != null && msg.what == 1001) {
//                circleprogress.reset();
//                getDatas();
//                circleprogress.postInvalidate();
//                myHandler.removeMessages(1001);
//            }
//            return false;
//        }
//    });

    /**
     * 初次获取数据
     */
    public void getDatas() {
        if (MyCommandManager.DEVICENAME != null) {

            Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    AppsBluetoothManager.getInstance(MyApp.context).sendCommand(new BatteryPower(commandResultCallback));
                    subscriber.onNext("获取电量ok");
                    //获取目标
                    AppsBluetoothManager.getInstance(context)
                            .sendCommand(new GoalsSetting(new BaseCommand.CommandResultCallback() {
                                @Override
                                public void onSuccess(BaseCommand command) {
                                    Log.d(TAG, "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
                                            "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
                                            "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
                                            "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());
                                    GOAL = GlobalVarManager.getInstance().getStepGoalsValue();
                                    recordwaveProgressBar.setMaxValue(GOAL);
                                    watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + GOAL + "");
//                        circleprogress.reset();
//                        circleprogress.setMaxValue(GOAL);
//                        circleprogress.postInvalidate();
                                }

                                @Override
                                public void onFail(BaseCommand command) {
                                    Log.d(TAG, "目标设置获取失败");
                                }
                            }));
                    subscriber.onNext("获取目标ok");
                    AppsBluetoothManager.getInstance(context)
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallback));//获取当天运动汇总
                    subscriber.onNext("获取目标ok");
                    //获取心率数据
                    initLineChart();
                    initLineCharts(heartDatas);
                    subscriber.onNext("获取心率数据ok");
                    //获取睡眠数据
                    setPieChart();
                    setH9PieCharts();
                    subscriber.onNext("获取睡眠数据ok");
                    subscriber.onCompleted();
                }
            });

            Observer<String> observer = new Observer<String>() {
                @Override
                public void onNext(String s) {
                    Log.d(TAG, "Item: " + s);
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "Completed!");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "Error!");
                }
            };

            observable.subscribe(observer);
        }
//        else {
//            myHandler.sendEmptyMessageDelayed(1001, 4000);
//        }
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
                    AppsBluetoothManager.getInstance(context)
                            .sendCommand(new DeviceDisplaySportSleep(commandResultCallback));//获取当天运动汇总
                    break;
                case 1:
                    //获取心率数据
                    initLineChart();
                    initLineCharts(heartDatas);
                    break;
                case 2:
                    //获取睡眠数据
                    setPieChart();
                    //扇形图
                    setH9PieCharts();
                    break;
            }
            swipeRefresh.setRefreshing(false);
        }
    }

    /*************   --------折线图----------    *************/


    private void initLineCharts(LinkedList<HeartData> hdt) {
        Axis axisX = new Axis(getAxisValuesX());
        axisX.setShowText(true);
        axisX.setAxisLineColor(Color.parseColor("#43FFFFFF"));
        axisX.setAxisLineWidth(0.5f);
        axisX.setTextColor(Color.WHITE);
        axisX.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(true).setShowText(true);
        Axis axisY = new Axis(getAxisValuesY());
        axisY.setShowText(false);
        axisY.setTextColor(Color.WHITE);
        axisY.setAxisLineWidth(0f);
        axisY.setShowLines(false);
        axisY.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(false).setShowText(true);
        leafLineChart.setAxisX(axisX);
        leafLineChart.setAxisY(axisY);
        List<Line> lines = new ArrayList<>();
        lines.add(getFoldLineTest(hdt));
        leafLineChart.setChartData(lines);
        leafLineChart.showWithAnimation(1000);
        leafLineChart.show();
        leafLineChart.invalidate();
    }

    /**
     * X轴值
     *
     * @return
     */
    private List<AxisValue> getAxisValuesX() {
        List<AxisValue> axisValues = new ArrayList<>();

        for (int i = 1; i < 24; i++) {
            AxisValue value = new AxisValue();
            if (i % 3 != 0) {
                value.setLabel("");
            } else {
                value.setLabel(i + "");
            }
//            if (i % 3 != 0) {
//                value.setLabel("");
//            } else {
//                value.setLabel(i + "");
//            }
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
        for (int i = 0; i < 3; i++) {
            AxisValue value = new AxisValue();
            value.setLabel("  ");
//            if (i != 0) {
//                value.setLabel(String.valueOf((i) * 50));
//            } else {
//                value.setLabel(" ");
//            }
            axisValues.add(value);
        }
        return axisValues;
    }

    //心率返回集合
    LinkedList<HeartData> heartDatas;

    /**
     * 设置值
     *
     * @return
     */
    private Line getFoldLineTest(LinkedList<HeartData> heartDatas) {

        List<PointValue> pointValues = new ArrayList<>();
        List<String> timeString = new ArrayList<>();
        List<Integer> heartString = new ArrayList<>();
        String systemTimer = B18iUtils.getSystemTimer();
        String s = B18iUtils.interceptString(systemTimer, 0, 10);

        if (heartDatas != null) {
            for (int i = 0; i < heartDatas.size(); i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartDatas.get(i).time_stamp));//yyyy/MM/dd HH:mm:ss
                if (s.equals(B18iUtils.interceptString(strTimes, 0, 10))) {
//                    Log.d(TAG, heartDatas.get(i).toString());
                    if (heartDatas.get(i) != null) {
                        int avg = heartDatas.get(i).heartRate_value;
                        String sysTim = B18iUtils.interceptString(
                                B18iUtils.getStrTimes(String.valueOf(heartDatas.get(i).time_stamp).trim()), 11, 13);
                        if (!timeString.contains(sysTim)) {
                            timeString.add(sysTim);
                            heartString.add(avg);
                        }
                        Collections.sort(timeString);
                    } else {
                        if (heartString != null) {
                            heartString.clear();
                        }
                        if (timeString != null) {
                            timeString.clear();
                        }
                        for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
                            heartString.add(0);
                            timeString.add(j + "");
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
                heartString.add(0);
                timeString.add(j + "");
            }
        }
        for (int i = 0; i < timeString.size(); i++) {
            PointValue value = new PointValue();
            value.setX(Integer.valueOf(timeString.get(i)) / 23f);
            value.setY(Integer.valueOf(heartString.get(i)) / 150f);
            pointValues.add(value);
        }

        Line line = new Line(pointValues);
        line.setLineColor(Color.parseColor("#FFFFFF"))
                .setLineWidth(2f)
                .setHasPoints(true)//是否显示点
                .setPointColor(Color.WHITE)
                .setCubic(false)
                .setPointRadius(2)
                .setFill(true)
                .setFillColor(Color.parseColor("#FFFFFF"))
                .setHasLabels(true)
                .setLabelColor(Color.parseColor("#FF00FF"));//0C33B5E5
        return line;
    }

    /******************        ---------  扇形----------               **********************/
    int AWAKE = 0;//清醒
    int DEEP = 0;//深睡
    int SHALLOW = 0;//浅睡
    LinkedList<SleepData> sleepDatas;
    private boolean fanRoateAniamtionStart;
    private String timeFromMillisecondA = "0";
    private String timeFromMillisecondS = "0";
    private String timeFromMillisecondD = "0";


    public void setH9PieCharts() {
        AWAKE = 0;
        DEEP = 0;
        SHALLOW = 0;
        int AllSleep = 0;
        boolean isSleeped = false;
        boolean isIntoSleeped = false;
//        boolean isOutSleep = false;
//        boolean isOutSleepMode = false;
//        boolean isOutSleepAuto = false;
        if (sleepDatas != null) {
            //当天日期
            String soberLenTime = "08:00";
            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
            String currentDay = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取
//            String nextDay = B18iUtils.getNextNumberDay(1);//前一天时间
            Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 1);
            String nextDay = H9TimeUtil.getValidDateStr(dateBefore);
            int size = sleepDatas.size();
            for (int i = 0; i < size; i++) {
                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
                        String timeDifference = "0";
                        if (0 < i) {
                            timeDifference = H9TimeUtil.getTimeDifference
                                    (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i - 1).sleep_time_stamp * 1000))
                                            , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        } else {
                            timeDifference = "0";
                        }

                        int SLEEPWAKE = 0;//苏醒次数
                        int sleep_type = sleepDatas.get(i).sleep_type;
                        // 0：睡着// 1：浅睡// 2：醒着// 3：准备入睡// 4：退出睡眠// 16：进入睡眠模式//
                        // 17：退出睡眠模式（本次睡眠非预设睡眠）
                        // 18：退出睡眠模式（本次睡眠为预设睡眠）
                        if (sleep_type == 0) {//--------》睡着
                            Log.e(TAG, "睡着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            AllSleep += Integer.valueOf(timeDifference);
                            DEEP += Integer.valueOf(timeDifference);//睡着的分钟数
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 1) {//--------》浅睡
                            Log.e(TAG, "浅睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            AllSleep += Integer.valueOf(timeDifference);
                            SHALLOW += Integer.valueOf(timeDifference);//浅睡的分钟数
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 2) {//--------》醒着
                            Log.e(TAG, "醒着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 3) {//--------》准备入睡着
                            Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            if (!isIntoSleeped) {
                                isIntoSleeped = true;
                                isSleeped = true;
                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
                            }
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 4) {//--------》退出睡眠
                            Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            SLEEPWAKE++;
                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                            textSleepTime.setText(soberLenTime);
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 16) {//--------》进入睡眠模式
                            Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            if (!isSleeped) {
                                isSleeped = true;
                                isIntoSleeped = true;
                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
                            }
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 17) {//--------》退出睡眠模式（本次睡眠非预设睡眠）
                            Log.e(TAG, "退出睡眠模式==0=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            SLEEPWAKE++;
                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                            textSleepTime.setText(soberLenTime);
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        } else if (sleep_type == 18) {//--------》退出睡眠模式（本次睡眠为预设睡眠）
                            Log.e(TAG, "退出睡眠模式==1=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                            SLEEPWAKE++;
                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                            textSleepTime.setText(soberLenTime);
                            Log.d(TAG, "===========" + SLEEPWAKE);
                        }
                        Log.d(TAG, DEEP + "----------222--------" + SHALLOW + "==============" + AllSleep + "===" + SLEEPWAKE);
                        //---------入睡时间-----苏醒次数--------苏醒时间
//                        TextView textSleepInto, textSleepWake, textSleepTime;
                        textSleepWake.setText(String.valueOf(SLEEPWAKE));//苏醒次数
                    }
                }
            }
            Log.d(TAG, DEEP + "----------121112--------" + SHALLOW + "==============" + AllSleep);
            timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));//时长
            timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOW * 60), 3600, 2));//浅睡
            timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEP * 60), 3600, 2));//深睡
            AWAKE = AllSleep - (DEEP + SHALLOW);//清醒
            awakeSleep.setText(timeFromMillisecondA);//时长
            shallowSleep.setText(timeFromMillisecondS);
            deepSleep.setText(timeFromMillisecondD);


//            for (int i = 0; i < sleepDatas.size(); i++) {
//                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
//                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
//                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
////                Log.d(TAG, timeDay + "------------------" + timeH);
//                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
//                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
//
//
//                        for (int j = 1; j < sleepDatas.size(); j++) {
//                            String timeDifference = H9TimeUtil.getTimeDifference
//                                    (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000))
//                                            , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j).sleep_time_stamp * 1000)));
//
//                            AllSleep += Integer.valueOf(timeDifference) / sleepDatas.size();
//                            Log.d(TAG, "----------dsd---------:   " + timeDifference + "===" + sleepDatas.get(j - 1).sleep_type + "===" + sleepDatas.get(j).sleep_type);
//                            int SLEEPWAKE = 0;//苏醒次数
//                            if (sleepDatas.get(j - 1).sleep_type == 0) {//--------》睡着
//                                DEEP += Integer.valueOf(timeDifference) / sleepDatas.size();//睡着的分钟数
//                            } else if (sleepDatas.get(j - 1).sleep_type == 1) {//--------》浅睡
//                                SHALLOW += Integer.valueOf(timeDifference) / sleepDatas.size();//浅睡的分钟数
//                            } else if (sleepDatas.get(j - 1).sleep_type == 2) {//--------》醒着
//
//                            } else if (sleepDatas.get(j - 1).sleep_type == 3) {//--------》准备入睡着
//                                Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//                                if (!isSleeped) {
//                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16));
//                                    isSleeped = true;
//                                }
//                            } else if (sleepDatas.get(j - 1).sleep_type == 4) {//--------》退出睡眠
//                                Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//                                SLEEPWAKE++;
//                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
//                                textSleepTime.setText(soberLenTime);
//                            } else if (sleepDatas.get(j - 1).sleep_type == 16) {//--------》进入睡眠模式
//                                Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//                            } else if (sleepDatas.get(j - 1).sleep_type == 17
//                                    || sleepDatas.get(j - 1).sleep_type == 18) {//--------》退出睡眠模式（本次睡眠非预设睡眠）--------》退出睡眠模式（本次睡眠为预设睡眠）
//                                Log.e(TAG, "退出睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)));
//                                SLEEPWAKE++;
//                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
//                                textSleepTime.setText(soberLenTime);
//                            }
////                            if (sleepDatas.get(sleepDatas.size()).sleep_type == 18){
////                                SLEEPWAKE++;
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(j - 1).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                            }
//                            Log.e(TAG, "苏醒次数：" + SLEEPWAKE);
////                        textSleepInto, textSleepWake, textSleepTime;
//                            //---------入睡时间-----苏醒次数--------苏醒时间
//                            textSleepWake.setText(String.valueOf(SLEEPWAKE));
//                        }
//                    }
//                }
//            }
//            for (int ii = 0; ii < sleepDatas.size() - 1; ii++) {
//                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(ii).sleep_time_stamp));//时间戳转换
//                String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
//                String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
//                Log.d(TAG, timeDay + "------------------" + timeH);
//                if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
//                    if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
//                        String timeDifference = H9TimeUtil.getTimeDifference
//                                (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000))
//                                        , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//                        Log.d(TAG,"----------dsd----s-----"+timeDifference);
//                        //睡眠类型：0x00：睡着， 0x01：浅睡，
//                        // 0x02：醒着，0x03：准备入睡，
//                        // 0x10（16）： 进入睡眠模式；0x11（17）：退出睡眠模式
//                        /**
//                         // sleepData.sleep_type
//                         // 0：睡着
//                         // 1：浅睡
//                         // 2：醒着
//                         // 3：准备入睡
//                         // 4：退出睡眠
//                         // 16：进入睡眠模式
//                         // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                         // 18：退出睡眠模式（本次睡眠为预设睡眠）
//                         */
//
//                        Log.d(TAG, "---ssssssssss----" + sleepDatas.get(0).sleep_type + "===========" + sleepDatas.get(ii + 1).sleep_type);
//                        int SLEEPWAKE = 0;//苏醒次数
//                        if (sleepDatas.get(ii + 1).sleep_type == 0) {//--------》睡着
//                            DEEP += Integer.valueOf(timeDifference);//睡着的分钟数
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 1) {//--------》浅睡
//                            SHALLOW += Integer.valueOf(timeDifference);//浅睡的分钟数
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 2) {//--------》醒着
//
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 3) {//--------》准备入睡着
//                            Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//                            if (!isIntoSleeped) {
//                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16));
//                                isSleeped = true;
//                                isIntoSleeped = true;
//                            }
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 4) {//--------》退出睡眠
//                            Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//                            SLEEPWAKE++;
//                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16);
//                            textSleepTime.setText(soberLenTime);
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 16) {//--------》进入睡眠模式
//                            Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//                            if (!isSleeped) {
//                                textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16));
//                                isSleeped = true;
//                                isIntoSleeped = true;
//                            }
//                        } else if (sleepDatas.get(ii + 1).sleep_type == 17
//                                || sleepDatas.get(ii + 1).sleep_type == 18) {//--------》退出睡眠模式（本次睡眠非预设睡眠）--------》退出睡眠模式（本次睡眠为预设睡眠）
//                            Log.e(TAG, "退出睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)));
//                            SLEEPWAKE++;
//                            soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii + 1).sleep_time_stamp * 1000)).substring(11, 16);
//                            textSleepTime.setText(soberLenTime);
//                        }
//
//
////                        switch (sleepDatas.get(ii + 1).sleep_type) {
////                            case 0:
////                                DEEP += Integer.valueOf(timeDifference);
////                                break;
////                            case 1:
////                                SHALLOW += Integer.valueOf(timeDifference);
////                                break;
////                            case 3://准备入睡
////                                if (!isSleep) {
////                                    isSleep = true;
////                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16));
////                                }
////                                Log.e(TAG, "入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                break;
////                            case 4://退出睡眠
////                                SLEEPWAKE++;
////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                                break;
////                            case 17:
////                                SLEEPWAKE++;
////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                                break;
////                            case 18:
////                                SLEEPWAKE++;
////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                                break;
////                            case 2:
////                                AWAKE += Integer.valueOf(timeDifference);
////                                break;
////                        }
//
////                            if (sleepDatas.get(ii + 1).sleep_type == 0) {
////                                DEEP += Integer.valueOf(timeDifference);
////                            } else if (sleepDatas.get(ii + 1).sleep_type == 1) {
////                                SHALLOW += Integer.valueOf(timeDifference);
////                            } else if (sleepDatas.get(ii + 1).sleep_type == 3) {
////                                if (!isSleeped) {
////                                    isSleeped = true;
////                                    textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16));
////                                }
////                                Log.e(TAG, "入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                            } else if (sleepDatas.get(ii + 1).sleep_type == 4) {
////                                SLEEPWAKE++;
////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                            } else if (sleepDatas.get(ii + 1).sleep_type == 17 || sleepDatas.get(ii + 1).sleep_type == 18) {
////                                SLEEPWAKE++;
////                                Log.e(TAG, "苏醒时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)));
////                                soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(ii).sleep_time_stamp * 1000)).substring(11, 16);
////                                textSleepTime.setText(soberLenTime);
////                            }
//                        Log.e(TAG, "苏醒次数：" + SLEEPWAKE);
////                        textSleepInto, textSleepWake, textSleepTime;
//                        //---------入睡时间-----苏醒次数--------苏醒时间
//                        textSleepWake.setText(String.valueOf(SLEEPWAKE));
//
//                    }
//                }
//            }

//            timeFromMillisecondA = String.valueOf(WatchUtils.div(((DEEP + SHALLOW) * 60), 3600, 2));//时长
//            timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOW * 60), 3600, 2));//浅睡
//            timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEP * 60), 3600, 2));//深睡
//            AWAKE = 720 - (DEEP + SHALLOW);//清醒
//            timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));
////            timeFromMillisecondA = B18iUtils.getTimeFromMillisecond((long) (AWAKE * (60 * 1000)));
////            timeFromMillisecondS = B18iUtils.getTimeFromMillisecond((long) (SHALLOW * (60 * 1000)));
////            timeFromMillisecondD = B18iUtils.getTimeFromMillisecond((long) (DEEP * (60 * 1000)));
//            awakeSleep.setText(timeFromMillisecondA);//时长
//            shallowSleep.setText(timeFromMillisecondS);
//            deepSleep.setText(timeFromMillisecondD);
//            Log.d(TAG, "s---time--深睡，浅睡,清醒-  --- 分钟（全天睡眠按照12小时）-" + DEEP + "===" + SHALLOW + "===" + AWAKE);
            if (DEEP > 0 || SHALLOW > 0) {
//                if (B18iCommon.ISUPDATASLEEP) {//判断，20分钟钟只可上传一次
                String upSleepTime = (String) SharedPreferencesUtils.getParam(context, "upSleepTime", "");
//                SharedPreferences sps = MyApp.getContext().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//                String upSleepTime = sps.getString("sss", "");
                if (!TextUtils.isEmpty(upSleepTime)) {
                    String timeDifference = H9TimeUtil.getTimeDifference(upSleepTime, B18iUtils.getSystemDataStart());
                    if (!TextUtils.isEmpty(timeDifference)) {
                        int number = Integer.valueOf(timeDifference.trim());
                        int number2 = Integer.parseInt(timeDifference.trim());
//                        Log.e(TAG, "睡眠上传---------" + number + "--" + number2 + "==" + timeDifference.compareTo("5"));
                        if (number >= 5 || number2 >= 5) {

                            Log.d(TAG, "----清醒时间" + soberLenTime);
//                            Log.e(TAG, "睡眠上传-----in----" + number + "===前几天时间" + H9TimeUtil.getDateBefore(new Date(), 7) + "前一天时间" + B18iUtils.getNextDay());
                            UpDatasBase.upDataSleep(String.valueOf(DEEP), String.valueOf(SHALLOW));//上传睡眠数据
                            SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//                            SharedPreferences sp = MyApp.getContext().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("sss", B18iUtils.getSystemDataStart());
                        }
                    }
                } else {
                    UpDatasBase.upDataSleep(String.valueOf(DEEP), String.valueOf(SHALLOW));//上传睡眠数据
                    SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//                    SharedPreferences sp = MyApp.getContext().getSharedPreferences("sleepdatas", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("sss", B18iUtils.getSystemDataStart());
                }

//                    B18iCommon.ISUPDATASLEEP = false;
//                }
            }
        }

        if (AWAKE <= 0) {
            AWAKE = 720;
        }
        if (DEEP <= 0) {
            DEEP = 0;
        }
        if (SHALLOW <= 0) {
            SHALLOW = 0;
        }
        pieChartView.setFanClickAbleData(
                new double[]{DEEP, SHALLOW, AWAKE},
                new int[]{Color.parseColor("#4CFFFFFF"), Color.parseColor("#7FFFFFFF"), Color.WHITE}, 0.08);
        pieChartView.setIsFistOffSet(false);
//        pieChartView.setOnFanClick(new OnFanItemClickListener() {
//            @Override
//            public void onFanClick(final FanItem fanItem) {
//                if (!fanRoateAniamtionStart) {
//                    float to;
//                    float centre = (fanItem.getStartAngle() * 2 + fanItem.getAngle()) / 2;
//                    if (centre >= 270) {
//                        to = 360 - centre + 90;
//                    } else {
//                        to = 90 - centre;
//                    }
//                    RotateAnimation animation = new RotateAnimation(0, to, pieChartView.getFanRectF().centerX(), pieChartView.getFanRectF().centerY());
//                    animation.setDuration(800);
//                    animation.setAnimationListener(new Animation.AnimationListener() {
//
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                            fanRoateAniamtionStart = true;
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            pieChartView.setToFirst(fanItem);
//                            pieChartView.clearAnimation();
//                            pieChartView.invalidate();
//                            fanRoateAniamtionStart = false;
////                            Toast.makeText(getContext(), "当前选中:" + fanItem.getPercent() + "%", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "----------------当前选中:" + fanItem.getPercent() + "%");
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//                    });
//                    animation.setFillAfter(true);
//                    pieChartView.startAnimation(animation);
//                }
//            }
//        });

    }


    //    View ----- 中的子控件
    private CircleProgress circleprogress;
    private LeafLineChart leafLineChart;
    private PieChartView pieChartView;
    TextView L38iCalT, L38iDisT;
    TextView autoHeartText;//心率
    TextView autoDatatext;//数据
    //-----清醒状态-------浅睡状态----深睡状态------清醒==改==》时常---浅睡----深睡
    TextView awakeState, shallowState, deepState, awakeSleep, shallowSleep, deepSleep;
    //---------入睡时间-----苏醒次数--------苏醒时间
    TextView textSleepInto, textSleepWake, textSleepTime;

    /**
     * view pager数据
     */
    private double DISTANCE = 0;//距离
    private double CALORIES = 0;//卡路里
//    private int AUTOHEART = 0;


    WaveProgress recordwaveProgressBar;
    TextView watchRecordTagstepTv;
    //目标选择列表
    ArrayList<String> daily_numberofstepsList;

    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }

    }

    private void setDatas() {

        View mView = LayoutInflater.from(context).inflate(R.layout.fragment_watch_record_change, null, false);
        recordwaveProgressBar = (WaveProgress) mView.findViewById(R.id.recordwave_progress_bar);
        L38iCalT = (TextView) mView.findViewById(R.id.watch_recordKcalTv);
        L38iDisT = (TextView) mView.findViewById(R.id.watch_recordMileTv);
//        mView.findViewById(R.id.watch_lines).setVisibility(View.GONE);
//        mView.findViewById(R.id.watch_record_swipe).setEnabled(false);
        L38iCalT.setText("" + CALORIES + "");
        L38iDisT.setText("" + DISTANCE + "");
        recordwaveProgressBar.setMaxValue(GOAL);
        recordwaveProgressBar.setValue(STEP);
        watchRecordTagstepTv = (TextView) mView.findViewById(R.id.watch_recordTagstepTv);
        String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
        watchRecordTagstepTv.setText(getResources().getString(R.string.settarget_steps) + tagGoal);
//        setTagSteps();
//        View view1 = LayoutInflater.from(context).inflate(R.layout.b18i_circle_progress_view, null, false);
//        circleprogress = (CircleProgress) view1.findViewById(R.id.circleprogress);
//        L38iCalT = (TextView) view1.findViewById(R.id.l38i_recordKcalTv);
//        L38iDisT = (TextView) view1.findViewById(R.id.l38i_recordMileTv);
//        L38iCalT.setText("" + CALORIES + "");
//        L38iDisT.setText("" + DISTANCE + "");
//        circleprogress.reset();
//        circleprogress.setMaxValue(GOAL);
//        circleprogress.setValue(STEP);
//        circleprogress.setPrecision(0);
        View view2 = LayoutInflater.from(context).inflate(R.layout.b18i_leaf_linechart_view, null, false);
        leafLineChart = (LeafLineChart) view2.findViewById(R.id.leaf_chart);
        autoHeartText = (TextView) view2.findViewById(R.id.autoHeart_text);//心率---可点击
        autoDatatext = (TextView) view2.findViewById(R.id.autoData_text);//数据---可点击
//        autoHeartText.setText(String.valueOf(AUTOHEART));
        autoHeartText.setOnClickListener(new MyViewLister());
        autoDatatext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,
                        H9HearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        View view3 = LayoutInflater.from(context).inflate(R.layout.b18i_pie_chart_view, null, false);
        pieChartView = (PieChartView) view3.findViewById(R.id.pieChartView);
        awakeState = (TextView) view3.findViewById(R.id.awakeState);
        shallowState = (TextView) view3.findViewById(R.id.shallowState);
        deepState = (TextView) view3.findViewById(R.id.deepState);
        awakeSleep = (TextView) view3.findViewById(R.id.awake_sleep);
        shallowSleep = (TextView) view3.findViewById(R.id.shallow_sleep);
        deepSleep = (TextView) view3.findViewById(R.id.deep_sleep);
        textSleepInto = (TextView) view3.findViewById(R.id.text_sleep_into);//入睡时间
        textSleepWake = (TextView) view3.findViewById(R.id.text_sleep_wake);//苏醒次数
        textSleepTime = (TextView) view3.findViewById(R.id.text_sleep_time);//苏醒时间
        awakeSleep.setText(timeFromMillisecondA);
        shallowSleep.setText(timeFromMillisecondS);
        deepSleep.setText(timeFromMillisecondD);
        awakeState.setText(getResources().getString(R.string.waking_state));//清醒状态
        shallowState.setText(getResources().getString(R.string.shallow_sleep));//浅睡眠
        deepState.setText(getResources().getString(R.string.deep_sleep));//深睡眠
        List<View> fragments = new ArrayList<>();
        fragments.add(mView);
//        fragments.add(view1);
        fragments.add(view2);
        fragments.add(view3);
        MyHomePagerAdapter adapter = new MyHomePagerAdapter(fragments);
        l38iViewpager.setCurrentItem(3);
        setLinePontion(fragments);
        l38iViewpager.setAdapter(adapter);
        l38iViewpager.addOnPageChangeListener(new PagerChangeLister(fragments));
    }


//    //设置目标步数
//    private void setTagSteps() {
//        ProfessionPick stepsnumber = new ProfessionPick.Builder(getContext(), new ProfessionPick.OnProCityPickedListener() {
//            @Override
//            public void onProCityPickCompleted(String profession) {
//                //设置步数
////                Log.e("-----目标步数", profession + "");
//
//                int st = Integer.valueOf(profession) / 100;
//                // 50*100
//                AppsBluetoothManager.getInstance(MyApp.getContext())
//                        .sendCommand(new GoalsSetting(commandResultCallback, (byte) 0, st, (byte) 0));//目标步数
//
//                //设置目标步数
//                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + profession);
//                recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
////                SharedPreferencesUtils.setParam(getActivity(), "settagsteps", profession);
////                recordwaveProgressBar.setValue(Float.valueOf((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")));
//            }
//        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
//                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
//                .btnTextSize(16) // button text size
//                .viewTextSize(25) // pick view text size
//                .colorCancel(Color.parseColor("#999999")) //color of cancel button
//                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
//                .setProvinceList(daily_numberofstepsList) //min year in loop
//                .dateChose(String.valueOf(GOAL)) // date chose when init popwindow
//                .build();
//        stepsnumber.showPopWin(getActivity());
//    }

    /**
     * 滑动小圆点
     *
     * @param fragments
     */
    private void setLinePontion(List<View> fragments) {
        for (int i = 0; i < fragments.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setPadding(3, 0, 3, 0);
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
     * ViewPager页面改变监听
     */
    private class PagerChangeLister implements ViewPager.OnPageChangeListener {
        private List<View> fragments;

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
//            switch (position) {
//                case 0:
//                    circleprogress.reset();
//                    circleprogress.setMaxValue(GOAL);
//                    circleprogress.setValue(STEP);
//                    circleprogress.setPrecision(0);
//                    circleprogress.invalidate();
//                    break;
//                case 1:
//                    //获取心率数据
//                    initLineChart();
////                    AppsBluetoothManager.getInstance(MyApp.getContext())
////                            .sendCommand(new GetHeartData(commandResultCallback, 0, new Date().getTime() / 1000, (int) GlobalVarManager.getInstance().getHeartRateCount()));
//                    break;
//                case 2:
//                    //获取睡眠数据
//                    setPieChart();
//                    //扇形图
////                    setPieCharts();
////                    setH9PieCharts();
//                    break;
//            }
        }

        private void PointSetting(int position) {
            l38iViewpager.setCurrentItem(position);
            for (int j = 0; j < fragments.size(); j++) {
                ImageView childAt1 = (ImageView) linePontion.getChildAt(j);
                childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
                childAt1.setMaxHeight(1);
                childAt1.setMaxWidth(1);
//                childAt1.setAlpha(80);
            }
            ImageView childAt = (ImageView) linePontion.getChildAt(position);
            childAt.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
            childAt.setMaxHeight(1);
            childAt.setMaxWidth(1);
//            childAt.setAlpha(225);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    /**
     * 获取心率数据
     */
    private void initLineChart() {  //System.currentTimeMillis()
        AppsBluetoothManager.getInstance(context)
                .sendCommand(new AllDataCount(commandResultCallback));//获取全部数据的条数
    }

    /**
     * 获取睡眠数据
     */
    private void setPieChart() {
        //获取睡眠条数
        AppsBluetoothManager.getInstance(context)
                .sendCommand(new SportSleepCount(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
//                        Log.d(TAG, "SleepCount:" + GlobalVarManager.getInstance().getSleepCount());
                        // 获取睡眠数据详情需要传入睡眠数据条数，所以在获取睡眠数据详情之前必需先获取睡眠数据条数。
                        // 如果睡眠条数为0，则睡眠详情数必定为0。所以如果获取到睡眠条数为0，则没有必要再去获取睡眠详情数据。
//                        AppsBluetoothManager.getInstance(MyApp.getContext())
//                                .sendCommand(new GetSleepData(commandResultCallback, 0, new Date().getTime() / 1000, (int) GlobalVarManager.getInstance().getSleepCount()));
                        //获取睡眠数据
                        AppsBluetoothManager.getInstance(context)
                                .sendCommand(new GetSleepData(commandResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getSleepCount()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
//                        Log.d(TAG, "睡眠条数获取失败");
                    }
                }, 1, 0));
    }

    @OnClick({R.id.watch_poorRel, R.id.battery_watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_poorRel:    //点击是否连接
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    startActivity(new Intent(getActivity(), B18ISettingActivity.class).putExtra("is18i", "H9"));
                } else {
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                startActivity(new Intent(context, SharePosterActivity.class).putExtra("is18i", "H9"));
                break;
        }
    }

    //分享
    private void doShareClick() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
        ScreenShot.shoot(getActivity(), new File(filePath));
        Common.showShare(getActivity(), null, false, filePath);
    }


//    private static Handler myHandler;

//    private static final int MessageNumber = 1008611;

    /**
     * 自动同步数据
     */
    public void SynchronousData() {
        RefreshBroadcastReceivers.setMyCallBack(new RefreshBroadcastReceivers.MyCallBack() {
            @Override
            public void setMyCallBack(Message msg) {
                if (msg.what == RefreshBroadcastReceivers.MessageNumber) {
                    if (isHidden) {
                        textStute.setText(context.getString(R.string.syncy_data));
                        textStute.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "H9同步成功");
                    getDatas();
                    RefreshBroadcastReceivers.getMyHandler().removeMessages(RefreshBroadcastReceivers.MessageNumber);
                }
            }
        });

//        myHandler = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                if (msg.what == MessageNumber) {
//                    if (isHidden) {
//                        textStute.setText(context.getString(R.string.syncy_data));
//                        textStute.setVisibility(View.VISIBLE);
//                    }
//                    getDatas();
//                    myHandler.removeMessages(MessageNumber);
//                }
//                return false;
//            }
//        });
    }

//    /**
//     * 接受同步广播提醒
//     */
//    public static class RefreshBroadcastReceiver extends B18IBroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            super.onReceive(context, intent);
//            Log.e("-------", "同步数据广播提醒来了");
//            if (MyCommandManager.DEVICENAME != null) {
////                myHandler.sendEmptyMessage(MessageNumber);
//                RefreshBroadcastReceivers.getMyHandler().sendEmptyMessage(MessageNumber);
//            }
//        }
//    }

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
                            MyCommandManager.DEVICENAME = "W06X";
                            watchConnectStateTv.setText("" + "connect" + "");
                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.tweet_list_divider_color_lights));
                            AnimationUtils.stopFlick(watchConnectStateTv);
                            if (isHidden) {
                                textStute.setText(getResources().getString(R.string.connted));
                                textStute.setVisibility(View.GONE);
                            }
                            if (MyApp.isOne) {
                                MyApp.isOne = false;
                                textStute.setVisibility(View.VISIBLE);
                                textStute.setText(getResources().getString(R.string.syncy_data));
                                getDatas();
                            }
                        } else {
                            MyCommandManager.DEVICENAME = null;
                            batteryLayout.setVisibility(View.INVISIBLE);
                            MyApp.isOne = true;
                            watchConnectStateTv.setText("" + "disconn.." + "");
                            watchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                            AnimationUtils.startFlick(watchConnectStateTv);
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
     * 蓝牙回调
     */
    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
//            Log.e(TAG, "-----baseCommand----" + baseCommand.toString());
            if (baseCommand instanceof DeviceDisplaySportSleep) {//获取当天运动汇总
                Log.d(TAG, "步数:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
                        "\n 卡路里:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
                        "\n 距离:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
                        "\n 睡眠时间:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
                STEP = GlobalVarManager.getInstance().getDeviceDisplayStep();
                recordwaveProgressBar.setMaxValue(GOAL);
                recordwaveProgressBar.setValue(STEP);
                String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
//                circleprogress.reset();
//                circleprogress.setMaxValue(GOAL);
//                circleprogress.setValue(STEP);
//                circleprogress.invalidate();
                CALORIES = WatchUtils.div(Double.valueOf(GlobalVarManager.getInstance().getDeviceDisplayCalorie()), 1000, 2);
                DISTANCE = WatchUtils.div(Double.valueOf(GlobalVarManager.getInstance().getDeviceDisplayDistance()), 1000, 2);
//                double tempDis = DISTANCE;
//                if(kmormi == 1){    //公制
//                    L38iCalT.setText("" + tempDis + "");
//                }else{  //英制
//                    L38iCalT.setText("" + WatchUtils.kmToMi(tempDis) + "");
//                }
                L38iCalT.setText("" + CALORIES + "");
                L38iDisT.setText("" + DISTANCE + "");
//                if (B18iCommon.ISUPDATASPROT) {//判断，20分钟钟只可上传一次
//                    //上传运动数据到后台
//                    updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE);
//                    B18iCommon.ISUPDATASPROT = false;
//                }
                String upStepTime = (String) SharedPreferencesUtils.getParam(context, "upStepTime", "");
//                SharedPreferences sps = MyApp.getContext().getSharedPreferences("stepdatas", Context.MODE_PRIVATE);
//                String upStepTime = sps.getString("sss", "");
                if (!TextUtils.isEmpty(upStepTime)) {
                    String timeDifference = H9TimeUtil.getTimeDifference(upStepTime, B18iUtils.getSystemDataStart());
                    if (!TextUtils.isEmpty(timeDifference)) {
                        int number = Integer.valueOf(timeDifference.trim());
                        int number2 = Integer.parseInt(timeDifference.trim());
//                        Log.e(TAG, "步数上传---------" + number);
                        if (number >= 5 && number2 >= 5) {
//                            Log.e(TAG, "步数上传----in-----" + number);
                            //上传运动数据到后台
                            UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE);
                            SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
                        }
                    }
                } else {
                    //上传运动数据到后台
                    UpDatasBase.updateLoadSportToServer(GOAL, STEP, CALORIES, DISTANCE);
                    SharedPreferencesUtils.setParam(context, "upStepTime", B18iUtils.getSystemDataStart());
                }

                //获取目标
                AppsBluetoothManager.getInstance(context)
                        .sendCommand(new GoalsSetting(new BaseCommand.CommandResultCallback() {
                            @Override
                            public void onSuccess(BaseCommand command) {
                                Log.d(TAG, "步数目标:" + GlobalVarManager.getInstance().getStepGoalsValue() + "\n" +
                                        "卡路里目标:" + GlobalVarManager.getInstance().getCalorieGoalsValue() + "\n" +
                                        "距离目标:" + GlobalVarManager.getInstance().getDistanceGoalsValue() + "\n" +
                                        "睡眠时间目标:" + GlobalVarManager.getInstance().getSleepGoalsValue());
                                GOAL = GlobalVarManager.getInstance().getStepGoalsValue();
                                recordwaveProgressBar.setMaxValue(GOAL);
                                recordwaveProgressBar.setValue(STEP);
                                String tagGoal = StringUtils.substringBefore(GOAL + "", ".");
                                watchRecordTagstepTv.setText(context.getResources().getString(R.string.settarget_steps) + tagGoal);
//                                circleprogress.reset();
//                                circleprogress.setMaxValue(GOAL);
//                                circleprogress.setValue(STEP);
//                                circleprogress.invalidate();
                            }

                            @Override
                            public void onFail(BaseCommand command) {
                                Log.d(TAG, "目标设置获取失败");
                            }

                        }));
                //获取电池电量并显示
            } else if (baseCommand instanceof GetSleepData) {//获取睡眠数据
                if (isHidden) {
                    textStute.setVisibility(View.INVISIBLE);
                }
                if (GlobalDataManager.getInstance().getSleepDatas() == null) {
//                    Log.e(TAG, "-------睡眠数据为null");
                    setH9PieCharts();
                } else {
                    sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
                    String sleepStr = "";
                    for (SleepData sleepData : GlobalDataManager.getInstance().getSleepDatas()) {
                        sleepStr = sleepStr + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepData.sleep_time_stamp * 1000)) + " 类型:" + sleepData.sleep_type + "\n";
                    }
                    Log.d(TAG, "------睡眠数据---" + sleepStr);

                    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
//                            setH9PieCharts();
//                            subscriber.onNext("睡眠图填充完成--数据上传完成");
//                            sleepDataCrrur(1);
//                            subscriber.onNext("睡眠前一天上传完成");
//                            sleepDataCrrur(2);
//                            subscriber.onNext("睡眠前2天上传完成");
//                            sleepDataCrrur(3);
//                            subscriber.onNext("睡眠前3天上传完成");
//                            sleepDataCrrur(4);
//                            subscriber.onNext("睡眠前4天上传完成");
//                            sleepDataCrrur(5);
//                            subscriber.onNext("睡眠前5天上传完成");
                            setH9PieCharts();
                            subscriber.onNext("睡眠图填充完成--数据上传完成");
                            //int numberDay,int numberDayEnd, LinkedList<SleepData> sleepDatas
                            UpDatasBase.sleepDataCrrur(2, 1, sleepDatas);
                            subscriber.onNext("睡眠前一天上传完成");
                            UpDatasBase.sleepDataCrrur(3, 2, sleepDatas);
                            subscriber.onNext("睡眠前2天上传完成");
                            UpDatasBase.sleepDataCrrur(4, 3, sleepDatas);
                            subscriber.onNext("睡眠前3天上传完成");
                            UpDatasBase.sleepDataCrrur(5, 4, sleepDatas);
                            subscriber.onNext("睡眠前4天上传完成");
                            UpDatasBase.sleepDataCrrur(6, 5, sleepDatas);
                            subscriber.onNext("睡眠前5天上传完成");
                            subscriber.onCompleted();
                        }
                    });

                    Observer<String> observer = new Observer<String>() {
                        @Override
                        public void onNext(String s) {
                            Log.d(TAG, "Item: " + s);
                        }

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "Completed!");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "Error!");
                        }
                    };

                    observable.subscribe(observer);

                }
            }// 获取心率
            else if (baseCommand instanceof GetHeartData) {
//                Log.e(TAG, "-----心率返回----" + Arrays.toString(GlobalDataManager.getInstance().getHeartDatas().toArray()));

                heartDatas = GlobalDataManager.getInstance().getHeartDatas();
                //折线图
                initLineCharts(heartDatas);
                String upHearteTime = (String) SharedPreferencesUtils.getParam(context, "upHearteTime", "");
                if (!TextUtils.isEmpty(upHearteTime)) {
                    String timeDifference = H9TimeUtil.getTimeDifference(upHearteTime, B18iUtils.getSystemDataStart());
//                    Log.e(TAG, "心率--时间-------" + timeDifference);
                    if (!TextUtils.isEmpty(timeDifference.trim())) {
                        int number = Integer.valueOf(timeDifference.trim());
                        int number2 = Integer.parseInt(timeDifference.trim());
//                        Log.e(TAG, "心率---------" + number);
                        if (number >= 5 || number2 >= 5) {
//                            Log.e(TAG, "心率-----in----" + number);
                            for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
                                if (heartData != null) {
                                    String stringTimer = B18iUtils.interceptString(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.time_stamp * 1000)), 0, 16);
//                                    Log.d(TAG, stringTimer);
                                    UpDatasBase.upDataHearte(String.valueOf(heartData.heartRate_value), stringTimer);//上传心率
                                }
                            }
                            SharedPreferencesUtils.setParam(context, "upHearteTime", B18iUtils.getSystemDataStart());
                        }
                    }
                } else {
                    for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
                        if (heartData != null) {
                            String stringTimer = B18iUtils.interceptString(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.time_stamp * 1000)), 0, 16);
//                            Log.d(TAG, stringTimer);
                            UpDatasBase.upDataHearte(String.valueOf(heartData.heartRate_value), stringTimer);//上传心率
                        }
                    }
                    SharedPreferencesUtils.setParam(context, "upHearteTime", B18iUtils.getSystemDataStart());
                }

            }//电量返回
            else if (baseCommand instanceof BatteryPower) {
                int battery = GlobalVarManager.getInstance().getBatteryPower();
                setBatteryPowerShow(battery);   //显示电量
                //获取公里或者英里
                AppsBluetoothManager.getInstance(context).sendCommand(new Unit(commandResultCallback));
            } else if (baseCommand instanceof AllDataCount) {   //所有条数
                Log.e("H9", "---所有条数---" + "SportCount:" + GlobalVarManager.getInstance().getSportCount()
                        + "\n SleepCount:" + GlobalVarManager.getInstance().getSleepCount()
                        + "\n HeartRateCount:" + GlobalVarManager.getInstance().getHeartRateCount()
                        + "\n BloodCount:" + GlobalVarManager.getInstance().getBloodCount());
                if (GlobalVarManager.getInstance().getSportCount() > 0) {
                    AppsBluetoothManager.getInstance(context)
                            .sendCommand(new GetSportData(commandResultCallback, (int) GlobalVarManager.getInstance().getSportCount()));
                }
                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
                    AppsBluetoothManager.getInstance(context)
                            .sendCommand(new GetHeartData(commandResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getHeartRateCount()));
                }
            } else if (baseCommand instanceof Unit) {  //英制还是公里
                kmormi = GlobalVarManager.getInstance().getUnit();
            } else if (baseCommand instanceof AutoSleep) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    Log.e("H9", "--------enter sleep:" + GlobalVarManager.getInstance().getEnterSleepHour() + "hour" +
                            "\n enter sleep:" + GlobalVarManager.getInstance().getEnterSleepMin() + "min" +
                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepHour() + "hour" +
                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepMin() + "min" +
                            "\n myremind sleep cycle:" + GlobalVarManager.getInstance().getRemindSleepCycle());
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.e("H9", "----success---");
                }
            } else if (baseCommand instanceof GetSportData) {//详细运动数据
//                int step = 0;
//                int calorie = 0;
//                int distance = 0;
                if (GlobalDataManager.getInstance().getSportsDatas() != null) {
                    final LinkedList<SportsData> sportsDatas = GlobalDataManager.getInstance().getSportsDatas();
                    Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
//                            upSportDatasCrrur(sportsDatas, 1);
//                            subscriber.onNext("运动前一天上传完成");
//                            upSportDatasCrrur(sportsDatas, 2);
//                            subscriber.onNext("运动前2天上传完成");
//                            upSportDatasCrrur(sportsDatas, 3);
//                            subscriber.onNext("运动前3天上传完成");
//                            upSportDatasCrrur(sportsDatas, 4);
//                            subscriber.onNext("运动前4天上传完成");
//                            upSportDatasCrrur(sportsDatas, 5);
//                            subscriber.onNext("运动前5天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 1, GOAL);
                            subscriber.onNext("运动前一天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 2, GOAL);
                            subscriber.onNext("运动前2天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 3, GOAL);
                            subscriber.onNext("运动前3天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 4, GOAL);
                            subscriber.onNext("运动前4天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 5, GOAL);
                            subscriber.onNext("运动前5天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 6, GOAL);
                            subscriber.onNext("运动前6天上传完成");
                            UpDatasBase.upSportDatasCrrur(sportsDatas, 7, GOAL);
                            subscriber.onNext("运动前7天上传完成");
                            subscriber.onCompleted();
                        }
                    });

                    Observer<String> observer = new Observer<String>() {
                        @Override
                        public void onNext(String s) {
                            Log.d(TAG, "Item: " + s);
                        }

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "Completed!");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "Error!");
                        }
                    };

                    observable.subscribe(observer);

                }
            } else if (baseCommand instanceof SportSleepMode) {
                if (GlobalVarManager.getInstance().getSportSleepMode() == 0) {
                    Log.d(TAG, "sport model");
                } else {
                    Log.d(TAG, "sleep model");
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            Log.e(TAG, "-----onFail---------获取失败");
        }
    };


//    /**
//     * 整理number天睡眠数据（准备上传）
//     *
//     * @param numberDay
//     */
//    public void sleepDataCrrur(int numberDay) {
//        int AWAKEDATAS = 0;
//        int DEEPDATAS = 0;
//        int SHALLOWDATAS = 0;
//
//        int AllSleep = 0;
//        boolean isSleeped = false;
//        boolean isIntoSleeped = false;
//        if (sleepDatas != null) {
//            //当天日期
//            String soberLenTime = "08:00";
//            String systemTimer = B18iUtils.getSystemTimer();//获取系统时间 2017/08/30 10:21:32
//            String currentDay = B18iUtils.interceptString(systemTimer, 0, 10);//字符串截取
//            String nextDay = B18iUtils.getNextNumberDay(numberDay);//前一天时间
//            setSleepDatas(currentDay, nextDay, AllSleep, isSleeped, isIntoSleeped, SHALLOWDATAS, DEEPDATAS, AWAKEDATAS, soberLenTime);
//        }
//    }
//
//    /**
//     * 上传前number天睡眠数据
//     * @param currentDay
//     * @param nextDay
//     * @param AllSleep
//     * @param isSleeped
//     * @param isIntoSleeped
//     * @param SHALLOWDATAS
//     * @param DEEPDATAS
//     * @param AWAKEDATAS
//     * @param soberLenTime
//     */
//    private void setSleepDatas(String currentDay, String nextDay, int AllSleep, boolean isSleeped,
//                               boolean isIntoSleeped, int SHALLOWDATAS, int DEEPDATAS, int AWAKEDATAS, String soberLenTime) {
//        int size = sleepDatas.size();
//        for (int i = 0; i < size; i++) {
//            String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
//            String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
//            String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
//            if (currentDay.equals(timeDay) || nextDay.equals(timeDay)) {
//                if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
//                    String timeDifference = "0";
//                    if (0 < i) {
//                        timeDifference = H9TimeUtil.getTimeDifference
//                                (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i - 1).sleep_time_stamp * 1000))
//                                        , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                    } else {
//                        timeDifference = "0";
//                    }
//
//                    int SLEEPWAKE = 0;//苏醒次数
//                    int sleep_type = sleepDatas.get(i).sleep_type;
//                    // 0：睡着// 1：浅睡// 2：醒着// 3：准备入睡// 4：退出睡眠// 16：进入睡眠模式//
//                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
//                    if (sleep_type == 0) {//--------》睡着
//                        Log.e(TAG, "睡着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        AllSleep += Integer.valueOf(timeDifference);
//                        DEEPDATAS += Integer.valueOf(timeDifference);//睡着的分钟数
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 1) {//--------》浅睡
//                        Log.e(TAG, "浅睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        AllSleep += Integer.valueOf(timeDifference);
//                        SHALLOWDATAS += Integer.valueOf(timeDifference);//浅睡的分钟数
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 2) {//--------》醒着
//                        Log.e(TAG, "醒着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 3) {//--------》准备入睡着
//                        Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        if (!isIntoSleeped) {
//                            isIntoSleeped = true;
//                            isSleeped = true;
////                            textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
//                        }
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 4) {//--------》退出睡眠
//                        Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        SLEEPWAKE++;
//                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                        textSleepTime.setText(soberLenTime);
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 16) {//--------》进入睡眠模式
//                        Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        if (!isSleeped) {
//                            isSleeped = true;
//                            isIntoSleeped = true;
////                            textSleepInto.setText(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16));
//                        }
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 17) {//--------》退出睡眠模式（本次睡眠非预设睡眠）
//                        Log.e(TAG, "退出睡眠模式==0=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        SLEEPWAKE++;
//                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                        textSleepTime.setText(soberLenTime);
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    } else if (sleep_type == 18) {//--------》退出睡眠模式（本次睡眠为预设睡眠）
//                        Log.e(TAG, "退出睡眠模式==1=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
//                        SLEEPWAKE++;
//                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
////                        textSleepTime.setText(soberLenTime);
//                        Log.d(TAG, "===========" + SLEEPWAKE);
//                    }
//                    Log.d(TAG, DEEPDATAS + "----------222--------" + SHALLOWDATAS + "==============" + AllSleep + "===" + SLEEPWAKE);
//                    //---------入睡时间-----苏醒次数--------苏醒时间
////                        TextView textSleepInto, textSleepWake, textSleepTime;
////                    textSleepWake.setText(String.valueOf(SLEEPWAKE));//苏醒次数
//                }
//            }
//        }
//        Log.d(TAG, DEEPDATAS + "----------121112--------" + SHALLOWDATAS + "==============" + AllSleep);
////        timeFromMillisecondA = String.valueOf(WatchUtils.div((AllSleep * 60), 3600, 2));//时长
////        timeFromMillisecondS = String.valueOf(WatchUtils.div((SHALLOWDATAS * 60), 3600, 2));//浅睡
////        timeFromMillisecondD = String.valueOf(WatchUtils.div((DEEPDATAS * 60), 3600, 2));//深睡
//        AWAKEDATAS = AllSleep - (DEEPDATAS + SHALLOWDATAS);//清醒
//        Log.d(TAG, "睡眠----清醒" + AWAKEDATAS);
////        awakeSleep.setText(timeFromMillisecondA);//时长
////        shallowSleep.setText(timeFromMillisecondS);
////        deepSleep.setText(timeFromMillisecondD);
//
//        if (DEEPDATAS > 0 || SHALLOWDATAS > 0) {
////                if (B18iCommon.ISUPDATASLEEP) {//判断，20分钟钟只可上传一次
//            String upSleepTime = (String) SharedPreferencesUtils.getParam(context, "upSleepTime", "");
//            if (!TextUtils.isEmpty(upSleepTime)) {
//                String timeDifference = H9TimeUtil.getTimeDifference(upSleepTime, B18iUtils.getSystemDataStart());
//                if (!TextUtils.isEmpty(timeDifference)) {
//                    int number = Integer.valueOf(timeDifference.trim());
//                    int number2 = Integer.parseInt(timeDifference.trim());
////                        Log.e(TAG, "睡眠上传---------" + number + "--" + number2 + "==" + timeDifference.compareTo("5"));
//                    if (number >= 5 || number2 >= 5) {
//
//                        Log.d(TAG, "----清醒时间" + soberLenTime);
////                            Log.e(TAG, "睡眠上传-----in----" + number + "===前几天时间" + H9TimeUtil.getDateBefore(new Date(), 7) + "前一天时间" + B18iUtils.getNextDay());
//                        upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS));//上传睡眠数据
//                        SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//                    }
//                }
//            } else {
//                upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS));//上传睡眠数据
//                SharedPreferencesUtils.setParam(context, "upSleepTime", B18iUtils.getSystemDataStart());
//            }
//        }
//    }

//    /**
//     * 心率数据上传
//     */
//    private void upDataHearte(String heartData, String stringTimer) {
//        try {
//            JSONObject map = new JSONObject();
//            map.put("userId", SharedPreferencesUtils.readObject(context, "userId"));
//            map.put("deviceCode", SharedPreferencesUtils.readObject(context, "mylanmac"));
//            map.put("systolic", "00");
//            map.put("stepNumber", "00");
//            map.put("date", stringTimer);
//            map.put("heartRate", heartData);
//            map.put("status", "0");
//            JSONObject mapB = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//            Object jsonArrayb = jsonArray.put(map);
//            mapB.put("data", jsonArrayb);
//            String mapjson = mapB.toString();
//            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
//                @Override
//                public void onNext(String s) {
//                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--心率数据上传--------" + s);
//                }
//            };
//            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, getActivity());
//            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
//        } catch (Exception E) {
//            E.printStackTrace();
//        }
//    }

//    /**
//     * 上传睡眠数据
//     *
//     * @param deepSleep
//     * @param shallowSleep
//     */
//    private void upDataSleep(String deepSleep, String shallowSleep) {
//        Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠上传--------" + shallowSleep + "==" + deepSleep);
//        try {
//            JSONObject map = new JSONObject();
//            String userId = (String) SharedPreferencesUtils.readObject(context, "userId");
//            String mylanmac = (String) SharedPreferencesUtils.readObject(context, "mylanmac");
//            Log.d(TAG, "==设备名称与MAC==" + userId + "==" + mylanmac);
//            map.put("userId", (String) SharedPreferencesUtils.readObject(context, "userId"));
//            Log.d(TAG, B18iUtils.getNextNumberDays(1) + "===" + B18iUtils.getNextNumberDays(0));
//            map.put("startTime", B18iUtils.getNextNumberDays(1));//
//            map.put("endTime", B18iUtils.getNextNumberDays(0));
//            map.put("count", "10");
//            map.put("deepLen", deepSleep);
//            map.put("shallowLen", shallowSleep);
//            map.put("deviceCode", (String) SharedPreferencesUtils.readObject(context, "mylanmac"));
//            map.put("sleepQuality", "6");
//            map.put("sleepLen", "4");
//            map.put("sleepCurveP", "5");
//            map.put("sleepCurveS", "8");
////            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, context);
//
//            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
//                @Override
//                public void onNext(String s) {
//                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠数据上传--------" + s);
//                }
//            };
//            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, getActivity());
//            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSleep, map.toString());
//        } catch (Exception E) {
//            E.printStackTrace();
//        }
//    }

//    /**
//     * 前几天运动分配 （准备上传）
//     *
//     * @param sportsDatas
//     * @param numberDay
//     */
//    public void upSportDatasCrrur(LinkedList<SportsData> sportsDatas, int numberDay) {
//        int step = 0;
//        int calorie = 0;
//        String nextDay = B18iUtils.getNextNumberDay(numberDay);//前numberDay天时间
//        for (SportsData sportsData : sportsDatas) {
//            SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
//            int i = Integer.parseInt(String.valueOf(sportsData.getSport_time_stamp()));
//            String times = sdr.format(new Date(i * 1000L));
//            if (nextDay.equals(times)) {
//                step += sportsData.sport_steps;
//                calorie += sportsData.sport_cal;
//            }
//        }
//        Log.d("-------------TEMT", "Step:" + step + "step" + "Calorie:" + calorie + "cal");
//        int dis = 0;
//        String sex = "M";
//        String hight = "175";
//        if ("M".equals(sex)) {
//            dis = (int) (Integer.valueOf(hight) * 0.415 * step);
//        } else {
//            dis = (int) (Integer.valueOf(hight) * 0.413 * step);
//        }
//        updateLoadSportToServer2(GOAL, step, calorie, dis, nextDay);
//    }
//
//    /**
//     * 前几天运动上传
//     *
//     * @param goal
//     * @param step
//     * @param calories
//     * @param distance
//     * @param nextDay
//     */
//    public void updateLoadSportToServer2(float goal, float step, double calories, double distance, String nextDay) {
//        int state = 1;  //步数是否达标
//        if (goal - state >= 0) {  //达标
//            state = 0;
//        } else {
//            state = 1;
//        }
//        JSONObject stepJons = new JSONObject();
//        try {
//            stepJons.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId")); //用户ID
//            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac")); //mac地址
//            stepJons.put("stepNumber", step);   //步数
//            stepJons.put("distance", distance);  //路程
//            stepJons.put("calories", calories);  //卡里路
//            stepJons.put("timeLen", "0");    //时长
//            stepJons.put("date", nextDay);   //data_time
//            stepJons.put("status", state);     //是否达标
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
//
//            @Override
//            public void onNext(String result) {
//                Log.e("H9", "---前几天步数数据返回--" + result);
//            }
//        }, MyApp.getContext());
//        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
//    }

//    /**
//     * @param goal     目标步数
//     * @param step     手表步数
//     * @param calories 手表卡路里
//     * @param distance //手表公里
//     */
//    private void updateLoadSportToServer(float goal, float step, double calories, double distance) {
//        Log.e(TAG, "--aaaaaaaaaaaaaaaa---步数上传--------" + goal + "==" + step + "==" + calories + "==" + distance);
//        int state = 1;  //步数是否达标
//        if (goal - state >= 0) {  //达标
//            state = 0;
//        } else {
//            state = 1;
//        }
//        JSONObject stepJons = new JSONObject();
//        try {
//            stepJons.put("userId", SharedPreferencesUtils.readObject(context, "userId")); //用户ID
//            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(context, "mylanmac")); //mac地址
//            stepJons.put("stepNumber", step);   //步数
//            stepJons.put("distance", distance);  //路程
//            stepJons.put("calories", calories);  //卡里路
//            stepJons.put("timeLen", "0");    //时长
//            stepJons.put("date", WatchUtils.getCurrentDate());   //data_time
//            stepJons.put("status", state);     //是否达标
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.e(TAG, "-----steJson-----" + stepJons.toString() + "--" + System.currentTimeMillis() / 1000 + "---" + new Date().getTime() / 1000);
//        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
//
//            @Override
//            public void onNext(String result) {
//                Log.e("H9", "---上次步数数据返回--" + result);
//            }
//        }, context);
//        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
//    }

    //显示电量
    private void setBatteryPowerShow(int battery) {
        Log.e(TAG,"----------battery="+battery);
        try {
            watchTopBatteryImgView.setColor(R.color.black);
            watchTopBatteryImgView.setPower(battery);
            batteryPowerTv.setText(battery + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MyViewLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(getActivity(), HeartRateActivity.class).putExtra("is18i", "H9");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            startActivity(new Intent(context,
                    H9HearteTestActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "H9"));
        }
    }
}
