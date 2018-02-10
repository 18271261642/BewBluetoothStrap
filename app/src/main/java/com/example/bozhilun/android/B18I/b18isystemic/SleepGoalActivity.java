package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.setting.AutoSleep;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/14 17:41
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SleepGoalActivity extends WatchBaseActivity {

    private static final String TAG = "--SleepGoalActivity";
    @BindView(R.id.sleep_text)
    TextView sleepText;
    @BindView(R.id.sleep_text2)
    TextView sleepText2;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.auto_Sleep)
    Switch autoSleep;
    @BindView(R.id.manual_Sleep)
    Switch manualSleep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_sleep_goal_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.preset_sleep));
        whichDevice();//判断是B18i还是H9
        autoSleep.setOnCheckedChangeListener(new ChangeListenter());
        manualSleep.setOnCheckedChangeListener(new ChangeListenter());
        addDatas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //获取预设睡眠
        switch (is18i){
            case "B18i":
                BluetoothSDK.getAutoSleep(resultCallBack);
                break;
            case "H9":
                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new AutoSleep(commandResultCallback));//获取预设睡眠时间
                break;
            case "B15P":

                break;
        }

    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof AutoSleep) {
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new SwitchSetting(commandResultCallback));//读取通知
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    closeLoadingDialog();
//                    Log.d(TAG, "enter sleep:" + GlobalVarManager.getInstance().getEnterSleepHour() + "hour" +
//                            "\n enter sleep:" + GlobalVarManager.getInstance().getEnterSleepMin() + "min" +
//                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepHour() + "hour" +
//                            "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepMin() + "min" +
//                            "\n myremind sleep cycle:" + GlobalVarManager.getInstance().getRemindSleepCycle());

                    String mHour = "";
                    String mMin = "";
                    String mHours = "";
                    String mMins = "";
                    if (GlobalVarManager.getInstance().getEnterSleepHour() <= 9) {
                        mHour = "0" + GlobalVarManager.getInstance().getEnterSleepHour();
                    } else {
                        mHour = "" + GlobalVarManager.getInstance().getEnterSleepHour();
                    }

                    if (GlobalVarManager.getInstance().getEnterSleepMin() <= 9) {
                        mMin = "0" + GlobalVarManager.getInstance().getEnterSleepMin();
                    } else {
                        mMin = "" + GlobalVarManager.getInstance().getEnterSleepMin();
                    }

                    if (GlobalVarManager.getInstance().getQuitSleepHour() <= 9) {
                        mHours = "0" + GlobalVarManager.getInstance().getQuitSleepHour();
                    } else {
                        mHours = "" + GlobalVarManager.getInstance().getQuitSleepHour();
                    }

                    if (GlobalVarManager.getInstance().getQuitSleepMin() <= 9) {
                        mMins = "0" + GlobalVarManager.getInstance().getQuitSleepMin();
                    } else {
                        mMins = "" + GlobalVarManager.getInstance().getQuitSleepMin();
                    }
                    sleepText.setText(mHour + ":" + mMin);
                    sleepText2.setText(mHours + ":" + mMins);
//                    if (GlobalVarManager.getInstance().getEnterSleepMin() <= 9) {
//                        sleepText.setText(GlobalVarManager.getInstance().getEnterSleepHour() + ":0" + GlobalVarManager.getInstance().getEnterSleepMin());
//                    } else {
//                        sleepText.setText(GlobalVarManager.getInstance().getEnterSleepHour() + ":" + GlobalVarManager.getInstance().getEnterSleepMin());
//                    }
//
//                    if (GlobalVarManager.getInstance().getQuitSleepMin() <= 9) {
//                        sleepText2.setText(GlobalVarManager.getInstance().getQuitSleepHour() + ":0" + GlobalVarManager.getInstance().getQuitSleepMin());
//                    } else {
//                        sleepText2.setText(GlobalVarManager.getInstance().getQuitSleepHour() + ":" + GlobalVarManager.getInstance().getQuitSleepMin());
//                    }
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
//                    Log.d(TAG, "预设睡眠设置成功");
//                    Toast.makeText(SleepGoalActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
                    closeLoadingDialog();
                    finish();
                }
            } else if (baseCommand instanceof SwitchSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    // 防丢开关
                    // 自动同步开关
                    // 睡眠开关
                    // 自动睡眠监测开关
                    // 来电提醒开关
                    // 未接来电提醒开关
                    // 短信提醒开关
                    // 社交提醒开关
                    // 邮件提醒开关
                    // 日历开关
                    // 久坐提醒开关
                    // 超低功耗功能开关
                    // 二次提醒开关

                    // 运动心率模式开关
                    // FACEBOOK开关
                    // TWITTER开关
                    // INSTAGRAM开关
                    // QQ开关
                    // WECHAT开关
                    // WHATSAPP开关
                    // LINE开关

//                    Log.d(TAG, "isAntiLostSwitch:" + GlobalVarManager.getInstance().isAntiLostSwitch()
//                            + "\n isAutoSyncSwitch:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
//                            + "\n isSleepSwitch:" + GlobalVarManager.getInstance().isSleepSwitch()
//                            + "\n isSleepStateSwitch:" + GlobalVarManager.getInstance().isSleepStateSwitch()
//                            + "\n isIncomePhoneSwitch:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
//                            + "\n isMissPhoneSwitch:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
//                            + "\n isSmsSwitch:" + GlobalVarManager.getInstance().isSmsSwitch()
//                            + "\n isSocialSwitch:" + GlobalVarManager.getInstance().isSocialSwitch()
//                            + "\n isMailSwitch:" + GlobalVarManager.getInstance().isMailSwitch()
//                            + "\n isCalendarSwitch:" + GlobalVarManager.getInstance().isCalendarSwitch()
//                            + "\n isSedentarySwitch:" + GlobalVarManager.getInstance().isSedentarySwitch()
//                            + "\n isLowPowerSwitch:" + GlobalVarManager.getInstance().isLowPowerSwitch()
//                            + "\n isSecondRemindSwitch:" + GlobalVarManager.getInstance().isSecondRemindSwitch()
//                            + "\n isSportHRSwitch:" + GlobalVarManager.getInstance().isSportHRSwitch()
//                            + "\n isFacebookSwitch:" + GlobalVarManager.getInstance().isFacebookSwitch()
//                            + "\n isTwitterSwitch:" + GlobalVarManager.getInstance().isTwitterSwitch()
//                            + "\n isInstagamSwitch:" + GlobalVarManager.getInstance().isInstagamSwitch()
//                            + "\n isQqSwitch:" + GlobalVarManager.getInstance().isQqSwitch()
//                            + "\n isWechatSwitch:" + GlobalVarManager.getInstance().isWechatSwitch()
//                            + "\n isWhatsappSwitch:" + GlobalVarManager.getInstance().isWhatsappSwitch()
//                            + "\n isLineSwitch:" + GlobalVarManager.getInstance().isLineSwitch());
                    autoSleep.setChecked(GlobalVarManager.getInstance().isSleepStateSwitch());
                    manualSleep.setChecked(GlobalVarManager.getInstance().isSleepSwitch());
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
//                    Log.d(TAG, "自动睡眠监测设置成功");
//                    Toast.makeText(SleepGoalActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };

    String a, b, c, d;
    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_AUTO_SLEEP:
                    //int[5]{enterSleepHour, enterSleepMin, quitSleepHour, quitSleepMin, remindSleepCycle}
//                    Log.e("-------slepp--", Arrays.toString(objects) + "===" + objects[0].toString());
                    int enterSleepHour = (int) objects[0];
                    int enterSleepMin = (int) objects[1];
                    int quitSleepHour = (int) objects[2];
                    int quitSleepMin = (int) objects[3];
                    if (String.valueOf(enterSleepHour).length() == 1) {
                        a = "0" + objects[0];
                    } else {
                        a = (String) String.valueOf(objects[0]);
                    }
                    if (String.valueOf(enterSleepMin).length() == 1) {
                        b = "0" + objects[1];
                    } else {
                        b = (String) String.valueOf(objects[1]);
                    }
                    if (String.valueOf(quitSleepHour).length() == 1) {
                        c = "0" + objects[2];
                    } else {
                        c = (String) String.valueOf(objects[2]);
                    }
                    if (String.valueOf(quitSleepMin).length() == 1) {
                        d = "0" + objects[3];
                    } else {
                        d = (String) String.valueOf(objects[3]);
                    }
                    sleepText.setText(a + ":" + b);
                    sleepText2.setText(c + ":" + d);
                    break;
                case ResultCallBack.TYPE_SET_AUTO_SLEEP:
                    Log.e(TAG, "设置成功");
                    finish();
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    @OnClick({R.id.image_back, R.id.sleep_text, R.id.sleep_text2, R.id.image_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.sleep_text:
                setSleepDatas(true);
                break;
            case R.id.sleep_text2:
                setSleepDatas(false);
                break;
            case R.id.image_right:
                String eH = (String) SharedPreferencesUtils.getParam(SleepGoalActivity.this, "eH", "");
                String eM = (String) SharedPreferencesUtils.getParam(SleepGoalActivity.this, "eM", "");
                String qH = (String) SharedPreferencesUtils.getParam(SleepGoalActivity.this, "qH", "");
                String qM = (String) SharedPreferencesUtils.getParam(SleepGoalActivity.this, "qM", "");

                if (TextUtils.isEmpty(eH)
                        && TextUtils.isEmpty(eM)
                        && TextUtils.isEmpty(qH)
                        && TextUtils.isEmpty(qM)) {
                    finish();
                    return;
                }
                if (TextUtils.isEmpty(a)) {
                    a = "00";
                }
                if (TextUtils.isEmpty(b)) {
                    b = "00";
                }
                if (TextUtils.isEmpty(c)) {
                    c = "00";
                }
                if (TextUtils.isEmpty(d)) {
                    d = "00";
                }
                if (TextUtils.isEmpty(eH)) {
                    eH = a;
                }
                if (TextUtils.isEmpty(eM)) {
                    eM = b;
                }
                if (TextUtils.isEmpty(qH)) {
                    qH = c;
                }
                if (TextUtils.isEmpty(qM)) {
                    qM = d;
                }
                //设置预设睡眠
                if (is18i.equals("B18i")) {
                    BluetoothSDK.setAutoSleep(resultCallBack, Integer.valueOf(eH), Integer.valueOf(eM),
                            Integer.valueOf(qH), Integer.valueOf(qM), 127);
                } else {
                    int eh = Integer.valueOf(eH);
                    int em = Integer.valueOf(eM);
                    int qh = Integer.valueOf(qH);
                    int qm = Integer.valueOf(qM);
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    Log.d(TAG, "预设睡眠时间为 ：" + eh + "-" + em + " -- " + qh + "-" + qm);
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new AutoSleep(commandResultCallback,
                                    (byte) eh,
                                    (byte) em,
                                    (byte) qh,
                                    (byte) qm, (byte) 7F));
                }
                break;
        }
    }

    ArrayList<String> startHourList;
    ArrayList<String> startMinuteList;
    HashMap<String, ArrayList<String>> startMinuteMapList;

    private void addDatas() {
        startHourList = new ArrayList<>();
        startMinuteList = new ArrayList<>();
        startMinuteMapList = new HashMap<>();

        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                startMinuteList.add("00 m");
            } else if (i < 10) {
                startMinuteList.add("0" + i + " m");
            } else {
                startMinuteList.add(i + " m");
            }
        }
        for (int i = 0; i < 24; i++) {
            if (i == 0) {
                startHourList.add("00 h");
                startMinuteMapList.put("00 h", startMinuteList);
            } else if (i < 10) {
                startHourList.add("0" + i + " h");
                startMinuteMapList.put("0" + i + " h", startMinuteList);
            } else {
                startHourList.add(i + " h");
                startMinuteMapList.put(i + " h", startMinuteList);
            }
        }
    }

    private void setSleepDatas(final boolean isWhat) {
        ProvincePick starPopWin = new ProvincePick.Builder(SleepGoalActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (isWhat) {
                    SharedPreferencesUtils.setParam(SleepGoalActivity.this, "eH", province.substring(0, province.length() - 2));
                    SharedPreferencesUtils.setParam(SleepGoalActivity.this, "eM", city.substring(0, city.length() - 2));
                    sleepText.setText(province.substring(0, province.length() - 2) + ":" + city.substring(0, city.length() - 2));
                } else {
                    SharedPreferencesUtils.setParam(SleepGoalActivity.this, "qH", province.substring(0, province.length() - 2));
                    SharedPreferencesUtils.setParam(SleepGoalActivity.this, "qM", city.substring(0, city.length() - 2));
                    sleepText2.setText(province.substring(0, province.length() - 2) + ":" + city.substring(0, city.length() - 2));
                }
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(startHourList) //min year in loop
                .setCityList(startMinuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(SleepGoalActivity.this);
    }

    private class ChangeListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.auto_Sleep:
                    if (isChecked) {
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x03, (byte) 1));
                    } else {
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x03, (byte) 0));
                    }
                    break;
                case R.id.manual_Sleep:
                    if (isChecked) {
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x02, (byte) 1));
                    } else {
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x02, (byte) 0));
                    }
                    break;
            }
        }
    }
}
