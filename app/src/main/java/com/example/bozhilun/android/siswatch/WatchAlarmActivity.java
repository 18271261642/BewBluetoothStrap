package com.example.bozhilun.android.siswatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.bean.AlarmTestBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/19.
 */

/**
 * H8闹钟显示页面
 */
public class WatchAlarmActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int WATCH_EDIT_REQUEST_CODE = 1001;
    //下拉刷新重新获取闹钟
    private static final int REFRESH_GET_ALARMDATA_CODE = 1333;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //第一个闹钟时间
    @BindView(R.id.watch_alarm_oneTv)
    TextView watchAlarmOneTv;
    //第二个闹钟时间
    @BindView(R.id.watch_alarm_thirdTv)
    TextView watchAlarmThirdTv;
    //第三个闹钟时间
    @BindView(R.id.watch_alarm_twoTv)
    TextView watchAlarmTwoTv;
    //第一个闹钟开个按钮
    @BindView(R.id.watch_alarm_one_switch)
    SwitchCompat watchAlarmOneSwitch;
    //第二个闹钟开个按钮
    @BindView(R.id.watch_alarm_two_switch)
    SwitchCompat watchAlarmTwoSwitch;
    //第三个闹钟开关按钮
    @BindView(R.id.watch_alarm_third_switch)
    SwitchCompat watchAlarmThirdSwitch;
    //第一个闹钟的重复显示
    @BindView(R.id.firstalarmRepeatTv)
    TextView firstalarmRepeatTv;
    //第一个闹钟的周显示
    @BindView(R.id.firstalarmWeekShowTv)
    TextView firstalarmWeekShowTv;
    //第二个闹钟的重复显示
    @BindView(R.id.secondalarmRepeatTv)
    TextView secondalarmRepeatTv;
    //第二个闹钟的周显示
    @BindView(R.id.secondalarmWeekShowTv)
    TextView secondalarmWeekShowTv;
    //第三个闹钟的重复显示
    @BindView(R.id.thirdalarmRepeatTv)
    TextView thirdalarmRepeatTv;
    //第三个闹钟的周显示
    @BindView(R.id.thirdalarmWeekShowTv)
    TextView thirdalarmWeekShowTv;

    Calendar calendar;

    @BindView(R.id.watch_alarm_oneLin)
    LinearLayout watchAlarmOneLin;
    @BindView(R.id.watch_alarm_twoLin)
    LinearLayout watchAlarmTwoLin;
    @BindView(R.id.watch_alarm_thirdLin)
    LinearLayout watchAlarmThirdLin;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_GET_ALARMDATA_CODE:    //下拉刷新
                    alarmSwipeRefresh.setRefreshing(false);
                    //WatchAlarmDialogTv.setText("Loading...");
                    linSetOnClick();
                    getAllAlarmData();  //获取闹钟
                    break;
            }

        }
    };
    @BindView(R.id.watch_test_showView)
    TextView watchTestShowView;

    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};
    Map<String, String> weekMaps = new HashMap<>();
    String alarmrepeat; //重复

    int week1repeat;
    int week2repeat;
    int week3repeat;
    @BindView(R.id.alarmSwipeRefresh)
    SwipeRefreshLayout alarmSwipeRefresh;
    @BindView(R.id.WatchAlarmDialogTv)
    TextView WatchAlarmDialogTv;

    private List<AlarmTestBean> alarmTestBeen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_alarm);
        Log.e("闹钟显示页面", "-----onCreate-----");
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        registerReceiver(broadcastReceiver, WatchUtils.regeditAlarmBraod());
        calendar = Calendar.getInstance();
        initViews();

        Log.e("闹钟", "------device--" + MyCommandManager.DEVICENAME);

        if (MyCommandManager.DEVICENAME == null) {
            ToastUtil.showToast(WatchAlarmActivity.this, getResources().getString(R.string.bluetooth_disconnected));
        }
        getAlarmSatae(); //获取开关的状态
        //获取闹钟
        getAllAlarmData();
    }


    //获取开关的状态
    private void getAlarmSatae() {
        //第一个开关
        String firstAlarmState = (String) SharedPreferencesUtils.getParam(WatchAlarmActivity.this, "firstalarmstate", "");
        if (firstAlarmState != null) {
            if ("on".equals(firstAlarmState)) {
                watchAlarmOneSwitch.setChecked(true);
            } else {
                watchAlarmOneSwitch.setChecked(false);
            }
        }
        //第二个开关
        String secondAlarmState = (String) SharedPreferencesUtils.getParam(WatchAlarmActivity.this, "secondalarmstate", "");
        if (secondAlarmState != null) {
            if ("on".equals(secondAlarmState)) {
                watchAlarmTwoSwitch.setChecked(true);
            } else {
                watchAlarmTwoSwitch.setChecked(false);
            }
        }
        //第三个开关
        String thirdAlarmState = (String) SharedPreferencesUtils.getParam(WatchAlarmActivity.this, "thirdalarmstate", "");
        if (thirdAlarmState != null) {
            if ("on".equals(thirdAlarmState)) {
                watchAlarmThirdSwitch.setChecked(true);
            } else {
                watchAlarmThirdSwitch.setChecked(false);
            }
        }
    }

    //获取闹钟
    private void getAllAlarmData() {
        if (MyCommandManager.DEVICENAME != null) {
            WatchAlarmDialogTv.setText("Loading...");
            Intent intent = new Intent();
            intent.setAction("com.example.bozhilun.android.siswatch.alarm");
            intent.putExtra("setalarmbroad", "getalarmfirst");
            sendBroadcast(intent);
        }
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.alarmclock));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        watchAlarmOneSwitch.setOnCheckedChangeListener(this);
        watchAlarmTwoSwitch.setOnCheckedChangeListener(this);
        watchAlarmThirdSwitch.setOnCheckedChangeListener(this);

        LinearLayoutManager linm = new LinearLayoutManager(WatchAlarmActivity.this);
        linm.setOrientation(LinearLayoutManager.VERTICAL);
        //下拉刷新
        alarmSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = REFRESH_GET_ALARMDATA_CODE;
                        handler.sendMessage(msg);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.watch_alarm_one_switch:   //第一个闹钟
                if (b) { //打开状态
                    //保存状态
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "firstalarmstate", "on");
                } else {
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "firstalarmstate", "off");
                    //1-255|20:1 EventBus.getDefault().post(new MessageEvent("firstalarm",0 + "-"+week1repeat + "|"+watchAlarmOneTv.getText().toString().trim()));
                    EventBus.getDefault().post(new MessageEvent("setAlarm1",0 + "-"+week1repeat + "|"+watchAlarmOneTv.getText().toString().trim()));
                }
                break;
            case R.id.watch_alarm_two_switch:   //第二个闹钟
                if (b) {
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "secondalarmstate", "on");
                } else {
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "secondalarmstate", "off");
                    //EventBus.getDefault().post(new MessageEvent("secondalarm", watchAlarmTwoTv.getText().toString().trim() + 0));
                    EventBus.getDefault().post(new MessageEvent("setAlarm2", 0 + "-"+week2repeat + "|"+watchAlarmTwoTv.getText().toString().trim()));
                }
                break;
            case R.id.watch_alarm_third_switch: //第三个闹钟
                if (b) {
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "thirdalarmstate", "on");
                } else {
                    SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "thirdalarmstate", "off");
                    //EventBus.getDefault().post(new MessageEvent("thirdalarm", watchAlarmThirdTv.getText().toString().trim() + 0));
                    EventBus.getDefault().post(new MessageEvent("setAlarm3", 0 + "-" + week3repeat+"|"+watchAlarmThirdTv.getText().toString().trim()));
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if (result != null) {
            Log.e("WatchAlarmActivity", "-----nazhong----" + event.getObject());
            if ("getalarmtimesuccessfirst".equals(result)) {
                String alarkData = (String) event.getObject();
                Log.e("WatchAlarmActivity", "-----------第一个闹钟-----" + alarkData);
                watchAlarmOneTv.setText(StringUtils.substringBefore(alarkData, "-"));
                // EventBus.getDefault().post(new MessageEvent("settingsecondalarm"));
                String repeat = StringUtils.substringAfter(alarkData, "-");
                this.week1repeat = Integer.valueOf(repeat);
                Log.e("", "--------" + repeat);
                vertRepeatData(repeat, 1);
                Intent intent = new Intent();
                intent.setAction("com.example.bozhilun.android.siswatch.alarm");
                intent.putExtra("setalarmbroad", "getalarm2");
                sendBroadcast(intent);
            } else if ("getalarmtimesuccesssecond".equals(result)) {
                String alarkData = (String) event.getObject();
                Log.e("WatchAlarmActivity", "-----------第2个闹钟-----" + event.getObject());
                watchAlarmTwoTv.setText(StringUtils.substringBefore(alarkData, "-"));
                // EventBus.getDefault().post(new MessageEvent("settingthirdalarm"));
                Intent intent = new Intent();
                intent.setAction("com.example.bozhilun.android.siswatch.alarm");
                intent.putExtra("setalarmbroad", "getalarm3");
                sendBroadcast(intent);
                String repeat = StringUtils.substringAfter(alarkData, "-");
                this.week2repeat = Integer.valueOf(repeat);
                vertRepeatData(repeat, 2);
            } else if ("getalarmtimesuccessthird".equals(result)) {
                WatchAlarmDialogTv.setText("");
                linSetClick();
                Log.e("WatchAlarmActivity", "-----------第3个闹钟-----" + event.getObject());
                String alarkData = (String) event.getObject();
                watchAlarmThirdTv.setText(StringUtils.substringBefore(alarkData, "-"));
                String repeat = StringUtils.substringAfter(alarkData, "-");
                this.week3repeat = Integer.valueOf(repeat);
                vertRepeatData(repeat, 3);
            }
        }
    }

    //判断是否重复和周
    private void vertRepeatData(String repeat, int num) {
        alarmTestBeen = new ArrayList<>();
        Collections.sort(alarmTestBeen, new Comparator<AlarmTestBean>() {
            @Override
            public int compare(AlarmTestBean demobean, AlarmTestBean t1) {

                return demobean.getIdnum().compareTo(t1.getIdnum());
            }
        });
        int weekRepeat = Integer.valueOf(repeat);
        Log.e("闹钟", "---weekRepeat--" + weekRepeat);
        Log.e("闹钟", "--周--111=" + weekMaps.toString());
        if (weekRepeat > 128) {   //大于128说明是重复
            int newWeekRepeat = weekRepeat - 128;
            alarmrepeat = getResources().getString(R.string.repeat);
            if ((newWeekRepeat & weekArray[0]) == 1) {   //周日
                weekMaps.put("week1", getResources().getString(R.string.sunday));
                alarmTestBeen.add(new AlarmTestBean(1, getResources().getString(R.string.sunday)));
            }
            if ((newWeekRepeat & weekArray[1]) == 2) { //周一
                weekMaps.put("week2", getResources().getString(R.string.monday));
                alarmTestBeen.add(new AlarmTestBean(2, getResources().getString(R.string.monday)));
            }
            if ((newWeekRepeat & weekArray[2]) == 4) { //周二
                weekMaps.put("week3", getResources().getString(R.string.tuesday));
                alarmTestBeen.add(new AlarmTestBean(4, getResources().getString(R.string.tuesday)));
            }
            if ((newWeekRepeat & weekArray[3]) == 8) {  //周三
                weekMaps.put("week4", getResources().getString(R.string.wednesday));
                alarmTestBeen.add(new AlarmTestBean(8, getResources().getString(R.string.wednesday)));
            }
            if ((newWeekRepeat & weekArray[4]) == 16) {  //周四
                weekMaps.put("week5", getResources().getString(R.string.thursday));
                alarmTestBeen.add(new AlarmTestBean(16, getResources().getString(R.string.thursday)));
            }
            if ((newWeekRepeat & weekArray[5]) == 32) {  //周五
                weekMaps.put("week6", getResources().getString(R.string.friday));
                alarmTestBeen.add(new AlarmTestBean(32, getResources().getString(R.string.friday)));
            }
            if ((newWeekRepeat & weekArray[6]) == 64) {  //周六
                weekMaps.put("week7", getResources().getString(R.string.saturday));
                alarmTestBeen.add(new AlarmTestBean(64, getResources().getString(R.string.saturday)));
            }
        } else {    //不重复
            alarmrepeat = "";
            if ((weekRepeat & weekArray[0]) == 1) {   //周日
                weekMaps.put("week1", getResources().getString(R.string.sunday));
                alarmTestBeen.add(new AlarmTestBean(1, getResources().getString(R.string.sunday)));
            }
            if ((weekRepeat & weekArray[1]) == 2) { //周一
                weekMaps.put("week2", getResources().getString(R.string.monday));
                alarmTestBeen.add(new AlarmTestBean(2, getResources().getString(R.string.monday)));
            }
            if ((weekRepeat & weekArray[2]) == 4) { //周二
                weekMaps.put("week3", getResources().getString(R.string.tuesday));
                alarmTestBeen.add(new AlarmTestBean(4, getResources().getString(R.string.tuesday)));
            }
            if ((weekRepeat & weekArray[3]) == 8) {  //周三
                weekMaps.put("week4", getResources().getString(R.string.wednesday));
                alarmTestBeen.add(new AlarmTestBean(8, getResources().getString(R.string.wednesday)));
            }
            if ((weekRepeat & weekArray[4]) == 16) {  //周四
                weekMaps.put("week5", getResources().getString(R.string.thursday));
                alarmTestBeen.add(new AlarmTestBean(16, getResources().getString(R.string.thursday)));
            }
            if ((weekRepeat & weekArray[5]) == 32) {  //周五
                weekMaps.put("week6", getResources().getString(R.string.friday));
                alarmTestBeen.add(new AlarmTestBean(32, getResources().getString(R.string.friday)));
            }
            if ((weekRepeat & weekArray[6]) == 64) {  //周六
                weekMaps.put("week7", getResources().getString(R.string.saturday));
                alarmTestBeen.add(new AlarmTestBean(64, getResources().getString(R.string.saturday)));
            }

        }

        if (num == 1) {   //第一个闹钟
            if (weekRepeat == 255) {  //每天重复
                firstalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                firstalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    firstalarmWeekShowTv.setText(getResources().getString(R.string.every_time));
                } else {
                    firstalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                firstalarmRepeatTv.setText(alarmrepeat + "");

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        } else if (num == 2) { //第二个闹钟
            if (weekRepeat == 255) {  //每天重复
                secondalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                secondalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    secondalarmWeekShowTv.setText(getResources().getString(R.string.every_time));   //每天
                } else {
                    secondalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                secondalarmRepeatTv.setText(alarmrepeat + "");
                //secondalarmWeekShowTv.setText(getWeekMap(weekMaps));

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        } else {  //第三个闹钟
            if (weekRepeat == 255) {  //每天重复
                thirdalarmRepeatTv.setText(getResources().getString(R.string.repeat));  //重复
                thirdalarmWeekShowTv.setText(getResources().getString(R.string.every_time));    //每天
            } else {
                if (weekRepeat == 127) {
                    thirdalarmWeekShowTv.setText(getResources().getString(R.string.every_time));
                } else {
                    thirdalarmWeekShowTv.setText(getListData(alarmTestBeen));
                }
                thirdalarmRepeatTv.setText(alarmrepeat + "");
                //thirdalarmWeekShowTv.setText(getWeekMap(weekMaps));

            }
            weekMaps.clear();
            alarmTestBeen.clear();
        }

    }

    private String getListData(List<AlarmTestBean> alarmTestBeen) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < alarmTestBeen.size(); i++) {
            sb.append(alarmTestBeen.get(i).getWeeks());
            sb.append(",");
        }
        if (sb.toString().length() > 1) {
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return null;
        }

    }

    //遍历map中的key
    private String getWeekMap(Map<String, String> weekMaps) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, String>> setmaps = weekMaps.entrySet();
        for (Map.Entry<String, String> ve : setmaps) {
            sb.append(ve.getValue());
            sb.append(",");
        }
        if (sb.toString().length() > 0) {
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(broadcastReceiver);
    }

    @OnClick({R.id.watch_test_showView, R.id.watch_alarm_oneLin, R.id.watch_alarm_twoLin, R.id.watch_alarm_thirdLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_alarm_oneLin:
                Intent intent = new Intent(WatchAlarmActivity.this, WatchEditAlarmActivity.class);
                intent.putExtra("alarmTag", "alarm1");
                intent.putExtra("times", watchAlarmOneTv.getText().toString().trim());
                intent.putExtra("wekrepeat", alarmrepeat);
                intent.putExtra("weekrepeat", week1repeat);
                startActivityForResult(intent, WATCH_EDIT_REQUEST_CODE);
                watchAlarmOneSwitch.setChecked(true);
                SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "firstalarmstate", "on");
                break;
            case R.id.watch_alarm_twoLin:
                Intent intents = new Intent(WatchAlarmActivity.this, WatchEditAlarmActivity.class);
                intents.putExtra("alarmTag", "alarm2");
                intents.putExtra("times", watchAlarmTwoTv.getText().toString().trim());
                intents.putExtra("wekrepeat", alarmrepeat);
                intents.putExtra("weekrepeat", week2repeat);
                startActivityForResult(intents, WATCH_EDIT_REQUEST_CODE);
                watchAlarmTwoSwitch.setChecked(true);
                SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "secondalarmstate", "on");
                break;
            case R.id.watch_alarm_thirdLin:
                Intent intent3 = new Intent(WatchAlarmActivity.this, WatchEditAlarmActivity.class);
                intent3.putExtra("alarmTag", "alarm3");
                intent3.putExtra("times", watchAlarmThirdTv.getText().toString().trim());
                intent3.putExtra("wekrepeat", alarmrepeat);
                intent3.putExtra("weekrepeat", week3repeat);
                startActivityForResult(intent3, WATCH_EDIT_REQUEST_CODE);
                watchAlarmThirdSwitch.setChecked(true);
                SharedPreferencesUtils.setParam(WatchAlarmActivity.this, "thirdalarmstate", "on");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("闹钟显示页面", "-----onActivityResult-----");
        Log.e("MM", "-------request---" + requestCode + "--" + resultCode + "---" + RESULT_OK);
        if (requestCode == WATCH_EDIT_REQUEST_CODE && resultCode == WATCH_EDIT_REQUEST_CODE) {
            Log.e("闹钟", "----tag--" + data.getStringExtra("tag"));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = REFRESH_GET_ALARMDATA_CODE;
                    handler.sendMessage(msg);
                }
            }, 1000);

        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private void linSetOnClick(){
        watchAlarmOneLin.setClickable(false);
        watchAlarmTwoLin.setClickable(false);
        watchAlarmThirdLin.setClickable(false);

    }

    private void linSetClick(){
        watchAlarmOneLin.setClickable(true);
        watchAlarmTwoLin.setClickable(true);
        watchAlarmThirdLin.setClickable(true);
    }

}
