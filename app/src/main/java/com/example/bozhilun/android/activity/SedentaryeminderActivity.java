package com.example.bozhilun.android.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/11.
 */

public class SedentaryeminderActivity extends BaseActivity {
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.shuimian_ceshi) SwitchCompat shuimianCeshi;
    @BindView(R.id.startime_tv_sedenye) TextView startimeTv;
    @BindView(R.id.endtime_tv) TextView endtimeTv;
    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    private String starHour, starMinute, endHour, endMinute;
    private int tag;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.sedentary_reminder);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        startimeTv.setText(formatter.format(date));
        endtimeTv.setText(formatter.format(date));
        //初始化24小时分钟
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        minuteMapList = new HashMap<>();
        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                minuteList.add("00 m");
            } else if (i < 10) {
                minuteList.add("0" + i + " m");
            } else {
                minuteList.add(i + " m");
            }
        }
        for (int i = 0; i < 24; i++) {
            if (i == 0) {
                hourList.add("00 h");
                minuteMapList.put("00 h", minuteList);
            } else if (i < 10) {
                hourList.add("0" + i + " h");
                minuteMapList.put("0" + i + " h", minuteList);
            } else {
                hourList.add(i + " h");
                minuteMapList.put(i + " h", minuteList);
            }
        }
        shuimianCeshi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (TextUtils.isEmpty(starHour) | TextUtils.isEmpty(starMinute)) {
                    ToastUtil.showShort(SedentaryeminderActivity.this, getString(R.string.select_star_time));
                    compoundButton.setChecked(false);
                } else if (TextUtils.isEmpty(endHour) | TextUtils.isEmpty(endMinute)) {
                    ToastUtil.showShort(SedentaryeminderActivity.this, getString(R.string.select_end_time));
                    compoundButton.setChecked(false);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("lanyaneme", MyCommandManager.DEVICENAME);
                    map.put("BeginTime", starHour);
                    map.put("Beginminte", starMinute);
                    map.put("EndTime", endHour);
                    map.put("Endminte", endMinute);
                    map.put("TimeInterval", "30");
                     if (!b) {
                         //保存开始时间
                         SharedPreferencesUtils.saveObject(SedentaryeminderActivity.this,"SedentaryeminderActivity",0);//0关 1开
                         tag = 1;
                         map.put("id", 0);
                     } else {
                         //保存开始时间
                         SharedPreferencesUtils.saveObject(SedentaryeminderActivity.this,"SedentaryeminderActivity",1);//0关 1开
                         tag = 0;
                         map.put("id", 1);
                     }

                    MyCommandManager.Sedentaryreminder(map);
                }
            }
        });

        //查看开关设置和保存的时间
        Settinging();
    }
private  void  Settinging(){
    if(null!=SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"Starttime")){
        startimeTv.setText(SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"Starttime").toString());
        starHour=String.valueOf(startimeTv.getText()).toString().substring(0,2);
        starMinute=String.valueOf(startimeTv.getText()).toString().substring(3,5);
    }else{startimeTv.setText("08:00");}
    if(null!=SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"EndTime")){
        endtimeTv.setText(SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"EndTime").toString());
        endHour=String.valueOf(endtimeTv.getText()).toString().substring(0,2);
        endMinute=String.valueOf(endtimeTv.getText()).toString().substring(3,5);
    }else{endtimeTv.setText("08:00");}
    //开关
    if(null!=SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"SedentaryeminderActivity")){
        if(1==(int)SharedPreferencesUtils.readObject(SedentaryeminderActivity.this,"SedentaryeminderActivity")){
            shuimianCeshi.setChecked(true);}else{shuimianCeshi.setChecked(false);}
    }
}
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if ("sedentaryreminder".equals(event.getMessage())) {
            boolean result = (boolean) event.getObject();
            if (result) {
                if (tag == 0) {
                    shuimianCeshi.setChecked(true);
                } else {
                    shuimianCeshi.setChecked(false);
                }
            } else {
                if (shuimianCeshi.isChecked()) {
                    shuimianCeshi.setChecked(true);
                } else {
                    shuimianCeshi.setChecked(false);
                }
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_sedentaryeminder;
    }

    @OnClick({R.id.start_time_relayout, R.id.end_time_relayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_time_relayout:
                ProvincePick starPopWin = new ProvincePick.Builder(SedentaryeminderActivity.this, new ProvincePick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String province, String city, String dateDesc) {
                        starHour = province.substring(0, province.length() - 2);
                        starMinute = city.substring(0, city.length() - 2);
                        startimeTv.setText(starHour + ":" + starMinute);
                        //保存开始时间
                        SharedPreferencesUtils.saveObject(SedentaryeminderActivity.this,"Starttime",startimeTv.getText());//保存第一个闹钟

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(hourList) //min year in loop
                        .setCityList(minuteMapList) // max year in loop
                        .build();
                starPopWin.showPopWin(SedentaryeminderActivity.this);
                break;
            case R.id.end_time_relayout:
                ProvincePick endPopWin = new ProvincePick.Builder(SedentaryeminderActivity.this, new ProvincePick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String province, String city, String dateDesc) {
                        endHour = province.substring(0, province.length() - 2);
                        endMinute = city.substring(0, city.length() - 2);
                        endtimeTv.setText(endHour + ":" + endMinute);
                        SharedPreferencesUtils.saveObject(SedentaryeminderActivity.this,"EndTime",endtimeTv.getText());//保存结束提醒的时间
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(hourList) //min year in loop
                        .setCityList(minuteMapList) // max year in loop
                        .build();
                endPopWin.showPopWin(SedentaryeminderActivity.this);
                break;
        }
    }
}
