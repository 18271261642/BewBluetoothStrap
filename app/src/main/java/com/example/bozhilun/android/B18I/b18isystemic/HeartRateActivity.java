package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.setting.HeartRateAlarm;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHeartWaringDataListener;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.settings.HeartWaringSetting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @aboutContent: 心率
 * @author： 安
 * @crateTime: 2017/9/16 10:40
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class HeartRateActivity extends WatchBaseActivity {

    private final String TAG = "----->>>" + this.getClass();
    @BindView(R.id.min_heart)
    TextView minHeart;
    @BindView(R.id.max_heart)
    TextView maxHeart;
    //    @BindView(R.id.line_heart)
//    LinearLayout lineHeart;
    @BindView(R.id.switch_heart_Indicate)
    Switch switchHeartIndicate;
    @BindView(R.id.line_xl)
    LinearLayout lineXl;
    @BindView(R.id.view_xl)
    View viewXl;
    private int HEART = 5;
    private int MINHEARTE = 30;
    private int MAXHEARTE = 180;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.switch_heart)
    Switch switchHeart;
    @BindView(R.id.heart_Auto)
    TextView heartAuto;

    @BindView(R.id.stat_hearts)
    Button statHeart;
    private String is18i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_heart_rate_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.heartrooate));
        statHeart.setText(getResources().getString(R.string.heart_rate_test));

        if (switchHeart.isChecked()) {
            heartAuto.setVisibility(View.VISIBLE);
        } else {
            heartAuto.setVisibility(View.GONE);
        }
        heartAuto.setText(HEART + " min");
        minHeart.setText(String.valueOf(MINHEARTE));
        maxHeart.setText(String.valueOf(MAXHEARTE));
        addHeartDatas();
        switchHeart.setOnCheckedChangeListener(new Listenter());
        switchHeartIndicate.setOnCheckedChangeListener(new Listenter());
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

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i) {
            case "B18i":
                BluetoothSDK.getAutoHeartRateFrequency(resultCallBack);//获取自动心率频度
                BluetoothSDK.getHeartRateAlarmThreshold(resultCallBack);//获取心率预警范围
                break;
            case "H9":
                Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        lineXl.setVisibility(View.GONE);
                        viewXl.setVisibility(View.GONE);
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        //获取心率预警
                        HeartRateAlarm getHeartRateAlarm = new HeartRateAlarm(commandResultCallback);
                        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(getHeartRateAlarm);
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
                break;
            case "B15P":
                lineXl.setVisibility(View.GONE);
                viewXl.setVisibility(View.GONE);
                MyApp.getVpOperateManager().readHeartWarning(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new IHeartWaringDataListener() {
                    @Override
                    public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                        String message = "心率报警-读取:\n" + heartWaringData.toString();
                        Log.d(TAG, message);
                        switchHeartIndicate.setChecked(heartWaringData.isOpen());
                        minHeart.setText(String.valueOf(heartWaringData.getHeartLow()));
                        maxHeart.setText(String.valueOf(heartWaringData.getHeartHigh()));
//                        Logger.t(TAG).i(message);
//                        sendMsg(message, 1);
                    }
                });
                break;
        }
    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

            if (baseCommand instanceof HeartRateAlarm) {
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置
//                    Log.d(TAG, "心率预警设置成功");
                    Toast.makeText(HeartRateActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                    //获取设备的心率预警状态(暂时获取本地的)
//                    Log.d(TAG,
//                            "HighLimit：" + GlobalVarManager.getInstance().getHighHeartLimit() + " bpm \n" +
//                                    "LowLimit：" + GlobalVarManager.getInstance().getLowHeartLimit() + " bpm \n" +
//                                    "AutoHeart：" + GlobalVarManager.getInstance().getAutoHeart() + " min \n" +
//                                    "isHeartAlarm：" + GlobalVarManager.getInstance().isHeartAlarm() + "\n" +
//                                    "isAutoHeart：" + GlobalVarManager.getInstance().isAutoHeart());
                    MINHEARTE = Integer.valueOf(GlobalVarManager.getInstance().getLowHeartLimit());
                    MAXHEARTE = Integer.valueOf(GlobalVarManager.getInstance().getHighHeartLimit());
                    minHeart.setText(String.valueOf(GlobalVarManager.getInstance().getLowHeartLimit()));
                    maxHeart.setText(String.valueOf(GlobalVarManager.getInstance().getHighHeartLimit()));
                    switchHeartIndicate.setChecked(GlobalVarManager.getInstance().isAutoHeart());
                }
            }
            closeLoadingDialog();
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };

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

    ArrayList<String> heartDatas = new ArrayList<>();
    ArrayList<String> minList = new ArrayList<>();
    ArrayList<String> maxList = new ArrayList<>();
    HashMap<String, ArrayList<String>> pickList = new HashMap<>();

    public void addHeartDatas() {
        for (int i = 1; i <= 6; i++) {
            int st = i * 5;
            heartDatas.add(String.valueOf(st));
        }

        for (int i = 31; i <= 220; i++) {
            maxList.add(String.valueOf(i));
        }

        for (int i = 30; i < 220; i++) {
            minList.add(i + "");
            pickList.put(i + "", maxList);
        }

    }

    @OnClick({R.id.image_back, R.id.heart_Auto, R.id.stat_hearts, R.id.max_heart, R.id.min_heart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.heart_Auto:
                setheartSlect(false);
                break;
            case R.id.stat_hearts://测量
                startActivity(new Intent(HeartRateActivity.this,
                        H9HearteTestActivity.class).putExtra("is18i", is18i));
                break;
            case R.id.max_heart:
                setheartSlect(true);
                break;
            case R.id.min_heart:
                setheartSlect(true);
                break;
        }
    }


    private void setheartSlect(boolean isWhat) {
        if (!isWhat) {
            ProfessionPick stepsnumber = new ProfessionPick.Builder(HeartRateActivity.this, new ProfessionPick.OnProCityPickedListener() {
                @Override
                public void onProCityPickCompleted(String profession) {
                    heartAuto.setText(profession + " min");
                    if (is18i.equals("B181")) {
                        BluetoothSDK.setAutoHeartRate(resultCallBack, (int) Integer.valueOf(profession));
                    }
                }
            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                    .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                    .btnTextSize(16) // button text size
                    .viewTextSize(25) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .setProvinceList(heartDatas) //min year in loop
                    .dateChose(HEART + "") // date chose when init popwindow
                    .build();
            stepsnumber.showPopWin(HeartRateActivity.this);
        } else {
            ProvincePick starPopWin = new ProvincePick.Builder(HeartRateActivity.this, new ProvincePick.OnProCityPickedListener() {
                @Override
                public void onProCityPickCompleted(String province, String city, String dateDesc) {
//                    Log.e("------===30", province + "==220" + city + "==" + dateDesc);
                    int highbloo = Integer.valueOf(city);
                    int lowbloo = Integer.valueOf(province);
                    if (is18i.equals("B18i")) {
                        BluetoothSDK.setHeartRateAlarmThreshold(resultCallBack, 1, Integer.valueOf(province), Integer.valueOf(city));
                    } else if (is18i.equals("H9")) {
                        //H9
                        // 是否预警
                        // 预警最大值
                        // 预警最小值
                        // 是否自动检测
                        // 检测周期间隔 （自动监控 单位必须为5分钟）
                        SharedPreferencesUtils.setParam(HeartRateActivity.this, "highbloo", highbloo);
                        SharedPreferencesUtils.setParam(HeartRateActivity.this, "lowbloo", lowbloo);
                        HeartRateAlarm setHeartRateAlarm = new HeartRateAlarm(commandResultCallback, true, (byte) highbloo, (byte) lowbloo, true, (byte) 30);
//                        Log.d(TAG, "----------------iiiii---------");
                        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(setHeartRateAlarm);
                    } else if (is18i.equals("B15P")) {
                        MyApp.getVpOperateManager().settingHeartWarning(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {

                            }
                        }, new IHeartWaringDataListener() {
                            @Override
                            public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                                String message = "心率报警-打开:\n" + heartWaringData.toString();
                                Log.d(TAG, message);
                                switchHeartIndicate.setChecked(heartWaringData.isOpen());
                                minHeart.setText(String.valueOf(heartWaringData.getHeartLow()));
                                maxHeart.setText(String.valueOf(heartWaringData.getHeartHigh()));
//                                Logger.t(TAG).i(message);
//                                sendMsg(message, 1);
                            }
                        }, new HeartWaringSetting(highbloo, lowbloo, true));
                    }
                    minHeart.setText(province);
                    maxHeart.setText(city);
                    MINHEARTE = Integer.valueOf(province);
                    MAXHEARTE = Integer.valueOf(city);
                }
            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                    .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                    .btnTextSize(16) // button text size
                    .viewTextSize(25) // pick view text size
                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                    .setProvinceList(minList) //min year in loop
                    .setCityList(pickList) // max year in loop
                    .build();
            starPopWin.showPopWin(HeartRateActivity.this);
        }

    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_AUTO_HEARTRATE:
                    HEART = (int) objects[0];
                    heartAuto.setText(HEART + " min");
                    break;
                case ResultCallBack.TYPE_OPEN_HEARTRATE:
//                    Log.e("---------------", "打开");
                    break;
                case ResultCallBack.TYPE_CLOSE_HEARTRATE:
//                    Log.e("---------------", "关闭");
                    break;
                case ResultCallBack.TYPE_REAL_TIME_HEARTRATE_DATA:
//                    Log.e("------实时心率数据---------", Arrays.toString(objects));
                    break;
                case ResultCallBack.TYPE_GET_HEARTRATE_ALARM_THRESHOLD:
//                    Log.e("-------获取心率预警--------", Arrays.toString(objects));
                    if ((int) objects[0] == 1) {
                        switchHeartIndicate.setChecked(true);
                    } else {
                        switchHeartIndicate.setChecked(false);
                    }
                    MAXHEARTE = (int) objects[1];
                    MINHEARTE = (int) objects[2];
                    maxHeart.setText(String.valueOf((int) objects[1]));
                    minHeart.setText(String.valueOf((int) objects[2]));
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    /**
     * 开关按钮
     */
    private class Listenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.switch_heart:
                    if (isChecked) {
                        heartAuto.setVisibility(View.VISIBLE);
                        BluetoothSDK.setAutoHeartRate(resultCallBack, 5);
                        BluetoothSDK.openHeartRate(resultCallBack, true);//打开或关闭心率
                    } else {
                        heartAuto.setVisibility(View.GONE);
                        BluetoothSDK.setAutoHeartRate(resultCallBack, 0);
                        BluetoothSDK.openHeartRate(resultCallBack, false);//打开或关闭心率
                    }
                    break;
                case R.id.switch_heart_Indicate:
                    if (is18i.equals("B15P")) {
                        MyApp.getVpOperateManager().settingHeartWarning(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {

                            }
                        }, new IHeartWaringDataListener() {
                            @Override
                            public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                                String message = "心率报警-打开:\n" + heartWaringData.toString();
                                Log.d(TAG, message);
                                switchHeartIndicate.setChecked(heartWaringData.isOpen());
                                minHeart.setText(String.valueOf(heartWaringData.getHeartLow()));
                                maxHeart.setText(String.valueOf(heartWaringData.getHeartHigh()));
//                                Logger.t(TAG).i(message);
//                                sendMsg(message, 1);
                            }
                        }, new HeartWaringSetting(MAXHEARTE, MINHEARTE, isChecked));
                    }
                    if (isChecked) {
                        if (is18i.equals("B18i")) {
                            BluetoothSDK.setHeartRateAlarmThreshold(resultCallBack, 1, MINHEARTE, MAXHEARTE);
                        } else if (is18i.equals("H9")) {
                            // 是否预警
                            // 预警最大值
                            // 预警最小值
                            // 是否自动检测
                            // 检测周期间隔 （自动监控 单位必须为5分钟）
                            HeartRateAlarm setHeartRateAlarm = new HeartRateAlarm(commandResultCallback, isChecked, (byte) MAXHEARTE, (byte) MINHEARTE, true, (byte) 30);
                            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(setHeartRateAlarm);
                        }
//                        lineHeart.setVisibility(View.VISIBLE);
                    } else {
                        if (is18i.equals("B18i")) {
                            BluetoothSDK.setHeartRateAlarmThreshold(resultCallBack, 0, MINHEARTE, MINHEARTE);
                        } else if (is18i.equals("H9")) {
                            // 是否预警
                            // 预警最大值
                            // 预警最小值
                            // 是否自动检测
                            // 检测周期间隔 （自动监控 单位必须为5分钟）
//                            Log.i(TAG, "设置的预警范围为：" + (byte) MAXHEARTE + (byte) MINHEARTE);
                            HeartRateAlarm setHeartRateAlarm = new HeartRateAlarm(commandResultCallback, isChecked, (byte) MAXHEARTE, (byte) MINHEARTE, true, (byte) 30);
                            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(setHeartRateAlarm);
                        }
//                        lineHeart.setVisibility(View.GONE);
                    }
                    break;
            }


        }
    }
}
