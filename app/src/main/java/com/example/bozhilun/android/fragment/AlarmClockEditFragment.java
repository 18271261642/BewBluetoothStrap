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
package com.example.bozhilun.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.event.AlarmClock;
import com.example.bozhilun.android.alock.MyUtil;
import com.example.bozhilun.android.util.WeacConstants;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 闹钟修改fragment
 *
 * @author 咖枯
 * @version 1.0 2015/06/14
 */
public class AlarmClockEditFragment extends BaseFragment implements
        OnClickListener, OnCheckedChangeListener {

    /**
     * Log tag ：AlarmClockEditFragment
     */
    private static final String LOG_TAG = "AlarmClockEditFragment";

    /**
     * 铃声选择按钮的requestCode
     */
    private static final int REQUEST_RING_SELECT = 1;

    /**
     * 小睡按钮的requestCode
     */
    private static final int REQUEST_NAP_EDIT = 2;

    /**
     * 闹钟实例
     */
    private AlarmClock mAlarmClock;

    /**
     * 下次响铃时间提示控件
     */
    private TextView mTimePickerTv;

    /**
     * 周一按钮状态，默认未选中
     */
    private Boolean isMondayChecked = false;

    /**
     * 周二按钮状态，默认未选中
     */
    private Boolean isTuesdayChecked = false;

    /**
     * 周三按钮状态，默认未选中
     */
    private Boolean isWednesdayChecked = false;

    /**
     * 周四按钮状态，默认未选中
     */
    private Boolean isThursdayChecked = false;

    /**
     * 周五按钮状态，默认未选中
     */
    private Boolean isFridayChecked = false;

    /**
     * 周六按钮状态，默认未选中
     */
    private Boolean isSaturdayChecked = false;

    /**
     * 周日按钮状态，默认未选中
     */
    private Boolean isSundayChecked = false;

    /**
     * 保存重复描述信息String
     */
    private StringBuilder mRepeatStr;

    private StringBuilder mBlueRepeatStr;

    /**
     * 重复描述组件
     */
    private TextView mRepeatDescribe;

    /**
     * 按键值顺序存放重复描述信息
     */
    private TreeMap<Integer, String> mMap;

    /**
     * 铃声描述
     */
    private TextView mRingDescribe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmClock = getActivity().getIntent().getParcelableExtra(
                WeacConstants.ALARM_CLOCK);
        // 闹钟默认开启
        mAlarmClock.setOnOff(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_alarm_clock_new_edit,
                container, false);
        // 设置界面背景
        setBackground(view);
//        setBounce(view);
        // 初始化操作栏
        initActionBar(view);
        // 初始化时间选择
        initTimeSelect(view);
        // 初始化重复
        initRepeat(view);
        // 初始化标签
        initTag(view);


        return view;
    }

    private void setBounce(View view) {
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView1);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }




    /**
     * 设置界面背景
     *
     * @param view view
     */
    private void setBackground(View view) {
        // 闹钟修改界面
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.new_alarm_clock_llyt);
        // 设置页面背景
        MyUtil.setBackground(viewGroup, getActivity());
    }

    /**
     * 设置操作栏按钮
     *
     * @param view view
     */
    private void initActionBar(View view) {
        // 操作栏取消按钮
        ImageView cancelAction = (ImageView) view.findViewById(R.id.action_cancel);
        cancelAction.setOnClickListener(this);
        // 操作栏确定按钮
        ImageView acceptAction = (ImageView) view.findViewById(R.id.action_accept);
        acceptAction.setOnClickListener(this);
        // 操作栏标题
        TextView actionTitle = (TextView) view.findViewById(R.id.action_title);
        actionTitle.setText(getResources()
                .getString(R.string.edit_alarm_clock));
    }

    /**
     * 设置时间选择
     *
     * @param view view
     */
    private void initTimeSelect(View view) {
        // 下次响铃提示
        mTimePickerTv = (TextView) view.findViewById(R.id.time_picker_tv);
        // 计算倒计时显示
        displayCountDown();
        // 闹钟时间选择器
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        // 初始化时间选择器的小时
        //noinspection deprecation
        timePicker.setCurrentHour(mAlarmClock.getHour());
        // 初始化时间选择器的分钟
        //noinspection deprecation
        timePicker.setCurrentMinute(mAlarmClock.getMinute());

        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // 保存闹钟实例的小时
                mAlarmClock.setHour(hourOfDay);
                // 保存闹钟实例的分钟
                mAlarmClock.setMinute(minute);
                // 计算倒计时显示
                displayCountDown();
            }

        });
    }

    /**
     * 设置重复信息
     *
     * @param view view
     */
    private void initRepeat(View view) {
        // 重复描述
        mRepeatDescribe = (TextView) view.findViewById(R.id.repeat_describe);

        // 周选择按钮
        // 周一按钮
        ToggleButton monday = (ToggleButton) view.findViewById(R.id.tog_btn_monday);
        // 周二按钮
        ToggleButton tuesday = (ToggleButton) view.findViewById(R.id.tog_btn_tuesday);
        // 周三按钮
        ToggleButton wednesday = (ToggleButton) view.findViewById(R.id.tog_btn_wednesday);
        // 周四按钮
        ToggleButton thursday = (ToggleButton) view.findViewById(R.id.tog_btn_thursday);
        // 周五按钮
        ToggleButton friday = (ToggleButton) view.findViewById(R.id.tog_btn_friday);
        // 周六按钮
        ToggleButton saturday = (ToggleButton) view.findViewById(R.id.tog_btn_saturday);
        // 周日按钮
        ToggleButton sunday = (ToggleButton) view.findViewById(R.id.tog_btn_sunday);

        monday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
        sunday.setOnCheckedChangeListener(this);

        mRepeatStr = new StringBuilder();
        mBlueRepeatStr = new StringBuilder();

        mMap = new TreeMap<>();

        String weeks = mAlarmClock.getWeeks();
        // 不是单次响铃时
        if (weeks != null) {
            final String[] weeksValue = weeks.split(",");
            for (String aWeeksValue : weeksValue) {
                int week = Integer.parseInt(aWeeksValue);
                switch (week) {
                    case 1:
                        sunday.setChecked(true);
                        break;
                    case 2:
                        monday.setChecked(true);
                        break;
                    case 3:
                        tuesday.setChecked(true);
                        break;
                    case 4:
                        wednesday.setChecked(true);
                        break;
                    case 5:
                        thursday.setChecked(true);
                        break;
                    case 6:
                        friday.setChecked(true);
                        break;
                    case 7:
                        saturday.setChecked(true);
                        break;
                }

            }
        }
    }

    /**
     * 设置标签
     *
     * @param view view
     */
    private void initTag(View view) {
        // 标签描述控件
        EditText tag = (EditText) view.findViewById(R.id.tag_edit_text);
        tag.setText(mAlarmClock.getTag());
        tag.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) {
                    mAlarmClock.setTag(s.toString());
                } else {
                    mAlarmClock.setTag(getString(R.string.alarm_clock));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 当点击取消按钮
            case R.id.action_cancel:
                drawAnimation();
                break;
            // 当点击确认按钮
            case R.id.action_accept:



                if((!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                        & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked & !isSundayChecked)){

                    Toast.makeText(getActivity(),"您需选择闹钟重复次数 !",Toast.LENGTH_SHORT).show();

                }else {
                    saveDefaultAlarmTime();
                    Intent data = new Intent();
                    data.putExtra(WeacConstants.ALARM_CLOCK, mAlarmClock);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    drawAnimation();

                    MyCommandManager.NewAlock(MyCommandManager.DEVICENAME,mAlarmClock,true);//闹钟提醒
                    break;
                }

        }
    }

    private void saveDefaultAlarmTime() {
        SharedPreferences share = getActivity().getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(WeacConstants.DEFAULT_ALARM_HOUR, mAlarmClock.getHour());
        editor.putInt(WeacConstants.DEFAULT_ALARM_MINUTE, mAlarmClock.getMinute());
        editor.apply();
    }

    /**
     * 结束新建闹钟界面时开启移动退出效果动画
     */
    private void drawAnimation() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, R.anim.move_out_bottom);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        switch (buttonView.getId()) {
            // 选中周一
            case R.id.tog_btn_monday:
                if (isChecked) {
                    isMondayChecked = true;
                    mMap.put(1, getString(R.string.one_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isMondayChecked = false;
                    mMap.remove(1);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周二
            case R.id.tog_btn_tuesday:
                if (isChecked) {
                    isTuesdayChecked = true;
                    mMap.put(2, getString(R.string.two_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isTuesdayChecked = false;
                    mMap.remove(2);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周三
            case R.id.tog_btn_wednesday:
                if (isChecked) {
                    isWednesdayChecked = true;
                    mMap.put(3, getString(R.string.three_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isWednesdayChecked = false;
                    mMap.remove(3);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周四
            case R.id.tog_btn_thursday:
                if (isChecked) {
                    isThursdayChecked = true;
                    mMap.put(4, getString(R.string.four_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isThursdayChecked = false;
                    mMap.remove(4);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周五
            case R.id.tog_btn_friday:
                if (isChecked) {
                    isFridayChecked = true;
                    mMap.put(5, getString(R.string.five_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isFridayChecked = false;
                    mMap.remove(5);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周六
            case R.id.tog_btn_saturday:
                if (isChecked) {
                    isSaturdayChecked = true;
                    mMap.put(6, getString(R.string.six_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSaturdayChecked = false;
                    mMap.remove(6);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周日
            case R.id.tog_btn_sunday:
                if (isChecked) {
                    isSundayChecked = true;
                    mMap.put(7, getString(R.string.day));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSundayChecked = false;
                    mMap.remove(7);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;


        }

    }

    /**
     * 设置重复描述的内容
     */
    private void setRepeatDescribe() {
        // 全部选中
        if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getResources()
                    .getString(R.string.every_day));
            mAlarmClock.setRepeat(getString(R.string.every_day));
            // 响铃周期
            //mAlarmClock.setWeeks("2,3,4,5,6,7,1");
            mAlarmClock.setWeeks("1111111");
            // 周一到周五全部选中
        } else if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_day));
            mAlarmClock.setRepeat(getString(R.string.week_day));
            //mAlarmClock.setWeeks("2,3,4,5,6");
            mAlarmClock.setWeeks("1111100");
            // 周六、日全部选中
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_end));
            mAlarmClock.setRepeat(getString(R.string.week_end));
            //mAlarmClock.setWeeks("7,1");
            mAlarmClock.setWeeks("0000011");
            // 没有选中任何一个
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.repeat_once));
            mAlarmClock.setRepeat(getResources()
                    .getString(R.string.repeat_once));
            mAlarmClock.setWeeks("0000000");

        } else {
            mRepeatStr.setLength(0);
            mRepeatStr.append(getString(R.string.week));
            int shiliu = 0;
            mBlueRepeatStr.setLength(0);
            StringBuffer Bluejilv = new StringBuffer("0000000");
            // mMap  1 2 3 4 5 6
            Set cols = mMap.keySet();
            for (Object aCol : cols) {
                mBlueRepeatStr.append(aCol);
            }
            for (int i = 0 ; i< 8; i++) {
                // 3  4   6  7
                if(mBlueRepeatStr.indexOf("1") != -1){
                    Bluejilv.replace(0, 1, "1");
                    shiliu = 1+128;
                }
                if(mBlueRepeatStr.indexOf("2") != -1){
                    Bluejilv.replace(1, 2, "1");
                    shiliu = 1+2+128;
                }
                if(mBlueRepeatStr.indexOf("3") != -1){
                    Bluejilv.replace(2, 3, "1");
                    shiliu = 1+2+4+128;
                }
                if(mBlueRepeatStr.indexOf("4") != -1){
                    Bluejilv.replace(3, 4, "1");
                    shiliu = 1+2+4+8+128;
                }
                if(mBlueRepeatStr.indexOf("5") != -1){
                    Bluejilv.replace(4, 5, "1");
                    shiliu = 1+2+4+8+16+128;
                }
                if(mBlueRepeatStr.indexOf("6") != -1){
                    Bluejilv.replace(5, 6, "1");
                    shiliu = 1+2+4+8+16+32+128;
                }
                if(mBlueRepeatStr.indexOf("7") != -1){
                    Bluejilv.replace(6, 7, "1");
                    shiliu = 1+2+4+8+16+32+64+128;
                }
            }
            mAlarmClock.setWeeks(Bluejilv.toString());
            mAlarmClock.setShiliu(shiliu);


            Collection<String> col = mMap.values();
            for (String aCol : col) {
                mRepeatStr.append(aCol).append(getResources().getString(R.string.caesura));
            }
            // 去掉最后一个"、"
            mRepeatStr.setLength(mRepeatStr.length() - 1);
            mRepeatDescribe.setText(mRepeatStr.toString());
            mAlarmClock.setRepeat(mRepeatStr.toString());


        }

    }

    /**
     * 计算显示倒计时信息
     */
    private void displayCountDown() {
        // 取得下次响铃时间
        long nextTime = MyUtil.calculateNextTime(mAlarmClock.getHour(),
                mAlarmClock.getMinute(), mAlarmClock.getWeeks());
        // 系统时间
        long now = System.currentTimeMillis();
        // 距离下次响铃间隔毫秒数
        long ms = nextTime - now;

        // 单位秒
        int ss = 1000;
        // 单位分
        int mm = ss * 60;
        // 单位小时
        int hh = mm * 60;
        // 单位天
        int dd = hh * 24;

        // 不计算秒，故响铃间隔加一分钟
        ms += mm;
        // 剩余天数
        long remainDay = ms / dd;
        // 剩余小时
        long remainHour = (ms - remainDay * dd) / hh;
        // 剩余分钟
        long remainMinute = (ms - remainDay * dd - remainHour * hh) / mm;

        // 响铃倒计时
        String countDown;
        // 当剩余天数大于0时显示【X天X小时X分】格式
        if (remainDay > 0) {
            countDown = getString(R.string.countdown_day_hour_minute);
            mTimePickerTv.setText(String.format(countDown, remainDay,
                    remainHour, remainMinute));
            // 当剩余小时大于0时显示【X小时X分】格式
        } else if (remainHour > 0) {
            countDown = getResources()
                    .getString(R.string.countdown_hour_minute);
            mTimePickerTv.setText(String.format(countDown, remainHour,
                    remainMinute));
        } else {
            // 当剩余分钟不等于0时显示【X分钟】格式
            if (remainMinute != 0) {
                countDown = getString(R.string.countdown_minute);
                mTimePickerTv.setText(String.format(countDown, remainMinute));
                // 当剩余分钟等于0时，显示【1天0小时0分】
            } else {
                countDown = getString(R.string.countdown_day_hour_minute);
                mTimePickerTv.setText(String.format(countDown, 1, 0, 0));
            }

        }
    }
}
