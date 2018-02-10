package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afa.tourism.greendao.gen.AlarmClockBeanDao;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.SportsHistoryActivity;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.AlarmClockBean;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/11.
 * 闹钟
 */

public class AlarmClockActivity extends BaseActivity {
    @BindView(R.id.tv_title) TextView tvTitle;
    //开关
    @BindView(R.id.shuimian_ceshi_alsrm) SwitchCompat shuimianCeshi;
    @BindView(R.id.alarm_switch) SwitchCompat alarmSwitch;
    @BindView(R.id.other_switch) SwitchCompat otherSwitch;
    //时间
    @BindView(R.id.getuptime_tv) TextView getuptimeTv;
    @BindView(R.id.alarmtime_tv) TextView alarmtimeTv;
    @BindView(R.id.othertime_tv) TextView othertimeTv;
    //点击设置控件
    @BindView(R.id.getup_linear) LinearLayout oneLinear;
    @BindView(R.id.alarm_linear) LinearLayout twoLinear;
    @BindView(R.id.other_linear) LinearLayout therreLinear;
    //保存
    @BindView(R.id.addalarm_btn) Button addalarmBtn;
    private String kaishitimea,jieshutimeas;//开始小时分钟
    private Calendar    clendar;
    //开关的状态
    boolean ischeckone=false;
    boolean ischecktwo=false;
    boolean ischeckthere=false;

    String Devicename,Deviceaderess;//蓝牙名字和地址
    Map<String, Object> map;

    @Override
    protected int getStatusBarColor() {return R.color.mosi;}//设置toobar颜色
    @Override
    protected void initViews() {
        map=new HashMap<>();
        clendar=Calendar.getInstance();
        EventBus.getDefault().register(this);
        tvTitle.setText(R.string.alarm_clock);
        //开关点击监听
        shuimianCeshi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){shuimianCeshi.setChecked(true);ischeckone=true;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischeckone","1");//0关 1开
                }else{shuimianCeshi.setChecked(false);ischeckone=false;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischeckone","0");//0关 1开
                }}});
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){alarmSwitch.setChecked(true);ischecktwo=true;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischecktwo","1");//0关 1开
                }else{alarmSwitch.setChecked(false);ischecktwo=false;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischecktwo","0");//0关 1开
                }}});
        otherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){otherSwitch.setChecked(true);ischeckthere=true;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischeckthere","1");//0关 1开
                }else{otherSwitch.setChecked(false);ischeckthere=false;SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"ischeckthere","0");//0关 1开
                }}});

        //查询开关状态设置的闹钟
        Seaching();
    }
private void Seaching(){

        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischeckone")){
            if("1".equals(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischeckone"))){
                shuimianCeshi.setChecked(true);}else{shuimianCeshi.setChecked(false);}
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischecktwo")){
            if("1".equals(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischecktwo"))){
                alarmSwitch.setChecked(true);}else{alarmSwitch.setChecked(false);}
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischeckthere")){
            if("1".equals(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"ischeckthere"))){
                otherSwitch.setChecked(true);}else{otherSwitch.setChecked(false);}
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongOne")){
            getuptimeTv.setText(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongOne").toString());
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongTwo")){
            alarmtimeTv.setText(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongTwo").toString());
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongThere")){
            othertimeTv.setText(SharedPreferencesUtils.readObject(AlarmClockActivity.this,"NaoZhongThere").toString());
        }
        if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"mylanya")){
            Devicename=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"mylanya").toString();
        }if(null!=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"mylanmac")){
        Deviceaderess=SharedPreferencesUtils.readObject(AlarmClockActivity.this,"mylanmac").toString();
        }


}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_alarmclock;
    }

    @OnClick({R.id.getup_linear, R.id.alarm_linear, R.id.other_linear, R.id.addalarm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getup_linear:
                setting(1);
                break;
            case R.id.alarm_linear:
                setting(2);
                break;
            case R.id.other_linear:
                setting(3);
                break;
            case R.id.addalarm_btn: //发送闹钟的指令
                if("B15P".equals(Devicename)){
                    map.put("lanyaneme",Devicename);
                    if(ischeckone==false){map.put("id",0);}else{map.put("id",1);}
                    if(ischecktwo==false){map.put("id2",0);}else{map.put("id2",1);}
                    if(ischeckthere==false){map.put("id3",0);}else{map.put("id3",1);}
                    String BeginHour=String.valueOf(getuptimeTv.getText()).toString().substring(0,2);
                    String Beginminte=String.valueOf(getuptimeTv.getText()).toString().substring(3,5);
                    String BeginHour2=String.valueOf(alarmtimeTv.getText()).toString().substring(0,2);
                    String Beginminte2=String.valueOf(alarmtimeTv.getText()).toString().substring(3,5);
                    String BeginHour3=String.valueOf(othertimeTv.getText()).toString().substring(0,2);
                    String Beginminte3=String.valueOf(othertimeTv.getText()).toString().substring(3,5);
                    map.put("BeginHour",BeginHour);
                    map.put("Beginminte",Beginminte);
                    map.put("BeginHour2",BeginHour2);
                    map.put("Beginminte2",Beginminte2);
                    map.put("BeginHour3",BeginHour3);
                    map.put("Beginminte3",Beginminte3);
                    MyCommandManager.Alarmclockb15p(map);
                    finish();
                }else{
                    int id,id2,id3;
                    if(ischeckone==false){id=0;}else{id=1;}
                    String BeginHour=String.valueOf(getuptimeTv.getText()).toString().substring(0,2);
                    String Beginminte=String.valueOf(getuptimeTv.getText()).toString().substring(3,5);
                    MyCommandManager. Alarmclockb15s(Integer.valueOf(BeginHour).byteValue(),Integer.valueOf(Beginminte).byteValue(),01,id);
                    if(ischecktwo==false){id2=0;}else{id2=1;}
                    String BeginHour2=String.valueOf(alarmtimeTv.getText()).toString().substring(0,2);
                    String Beginminte2=String.valueOf(alarmtimeTv.getText()).toString().substring(3,5);
                    MyCommandManager. Alarmclockb15s(Integer.valueOf(BeginHour2).byteValue(),Integer.valueOf(Beginminte2).byteValue(),02,id2);
                    if(ischeckthere==false){id3=0;}else{id3=1;}
                    String BeginHour3=String.valueOf(othertimeTv.getText()).toString().substring(0,2);
                    String Beginminte3=String.valueOf(othertimeTv.getText()).toString().substring(3,5);
                    MyCommandManager. Alarmclockb15s(Integer.valueOf(BeginHour3).byteValue(),Integer.valueOf(Beginminte3).byteValue(),03,id3);
                    finish();
                }

                break;
        }
    }
private void setting(final int id){
    //声明TimePicker对象（开始日期）
    TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmClockActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog,
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    kaishitimea=Integer.toString(hourOfDay);
                    jieshutimeas=Integer.toString(minute);
                    if(kaishitimea.length()==1){kaishitimea= "0"+ kaishitimea;
                    }if(jieshutimeas.length()==1){jieshutimeas="0"+ jieshutimeas;
                    }
                    if(1==id){
                        map.put("BeginHour",kaishitimea);
                        map.put("Beginminte",jieshutimeas);
                        getuptimeTv.setText(kaishitimea+":"+jieshutimeas);//设置开始时间
                        SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"NaoZhongOne",getuptimeTv.getText());//保存第一个闹钟
                    }else if(2==id){
                        map.put("BeginHour2",kaishitimea);
                        map.put("Beginminte2",jieshutimeas);
                        alarmtimeTv.setText(kaishitimea+":"+jieshutimeas);//设置开始时间
                        SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"NaoZhongTwo",alarmtimeTv.getText()); //保存第二个闹钟
                    }else{
                        map.put("BeginHour3",kaishitimea);
                        map.put("Beginminte3",jieshutimeas);
                        othertimeTv.setText(kaishitimea+":"+jieshutimeas);//设置开始时间
                        SharedPreferencesUtils.saveObject(AlarmClockActivity.this,"NaoZhongThere",othertimeTv.getText()); //保存第三个闹钟
                    }

                }}, clendar.get(Calendar.HOUR_OF_DAY),
            clendar.get(Calendar.MINUTE), false);
    timePickerDialog.setTitle(R.string.xuanzeshijian);
    timePickerDialog.setButton(TimePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.confirm), timePickerDialog);
    Window window = timePickerDialog.getWindow();
    window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
    timePickerDialog.show();
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        boolean result = (boolean) event.getObject();
        if (!result) {
           // ToastUtil.showShort(AlarmClockActivity.this, getString(R.string.settings_fail));
            return;
        }

    }







}
