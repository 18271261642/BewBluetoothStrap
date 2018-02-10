package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
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
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.settings.LongSeatSetting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import cn.appscomm.bluetooth.protocol.SwitchType;
import me.imid.swipebacklayout.lib.BuildConfig;

/**
 * @aboutContent: 久坐提醒
 * @author： 安
 * @crateTime: 2017/9/8 10:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class SedentaryReminder extends WatchBaseActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.switch_reminder)
    Switch switchReminder;
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.sedentary_text)
    TextView sedentaryText;
    private static final String TAG = "SedentaryReminder";
    private String is18i;
    int starHour = 8;
    int starMin = 00;
    int endHour = 20;
    int endMin = 00;
    int allTime = 60;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_sedentary_reminder_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.Sedentaryreminder));
        addDatas();
        switchReminder.setOnCheckedChangeListener(new ChangeListenter());
        sedentaryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSendtaryDatas();
            }
        });
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


    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        Log.d(TAG, "为：" + is18i);
        switch (is18i) {
            case "B18i":
                sedentaryText.setVisibility(View.VISIBLE);
                BluetoothSDK.getInactivityAlert(resultCallBack);
                break;
            case "H9":
                sedentaryText.setVisibility(View.GONE);
                boolean a = (boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SEDENTARY");//获取久坐提醒
                switchReminder.setChecked(a);
                break;
            case "B15P":
                showLoadingDialog(getResources().getString(R.string.dlog));
                sedentaryText.setVisibility(View.VISIBLE);
                MyApp.getVpOperateManager().readLongSeat(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {
                        //状态返回
                        closeLoadingDialog();
                    }
                }, new ILongSeatDataListener() {
                    @Override
                    public void onLongSeatDataChange(LongSeatData longSeatData) {
                        String message = "设置久坐-读取:\n" + longSeatData.toString();
                        Log.d(TAG, message);
                        switchReminder.setChecked(longSeatData.isOpen());
                        starHour = longSeatData.getStartHour();
                        starMin = longSeatData.getStartMinute();
                        endHour = longSeatData.getEndHour();
                        endMin = longSeatData.getEndMinute();
                        allTime = longSeatData.getThreshold();
                        sedentaryText.setText((double) (Math.round(allTime) / 60.0) + " h");
                    }
                });

                break;
        }
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    private class ChangeListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.SEDENTARY, isChecked);
            if (isChecked) {

                switch (is18i) {
                    case "B18i":
                        sedentaryText.setVisibility(View.VISIBLE);
                        BluetoothSDK.setInactivityAlert(resultCallBack, 1, 127, allTime, 8, 0, 20, 0, 500);//不活动提醒开
                        break;
                    case "H9":
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        //H9
                        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0A, (byte) 1));
                        SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SEDENTARY", true);
                        break;
                    case "B15P":
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        MyApp.getVpOperateManager().settingLongSeat(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {
                                //状态返回
                                closeLoadingDialog();
                            }
                        }, new LongSeatSetting(starHour, starMin, endHour, endMin, allTime, true), new ILongSeatDataListener() {
                            @Override
                            public void onLongSeatDataChange(LongSeatData longSeatData) {
                                String message = "设置久坐-打开:\n" + longSeatData.toString();
                                Log.d(TAG, message);
                                if (longSeatData.getStatus().equals("OPEN_SUCCESS")) {
                                    switchReminder.setChecked(longSeatData.isOpen());
                                    starHour = longSeatData.getStartHour();
                                    starMin = longSeatData.getStartMinute();
                                    endHour = longSeatData.getEndHour();
                                    endMin = longSeatData.getEndMinute();
                                    allTime = longSeatData.getThreshold();
                                }
                            }
                        });
                        break;
                }

            } else {
                switch (is18i) {
                    case "B18i":
                        sedentaryText.setVisibility(View.GONE);
                        BluetoothSDK.setInactivityAlert(resultCallBack, 0, 127, allTime, 8, 0, 20, 0, 500);//不活动提醒关
                        break;
                    case "H9":
                        //H9
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0A, (byte) 0));
                        SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SEDENTARY", false);
                        break;
                    case "B15P":
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        MyApp.getVpOperateManager().settingLongSeat(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {
                                //状态返回
                                closeLoadingDialog();
                            }
                        }, new LongSeatSetting(10, 35, 11, 45, 60, false), new ILongSeatDataListener() {
                            @Override
                            public void onLongSeatDataChange(LongSeatData longSeatData) {
                                String message = "设置久坐-关闭:\n" + longSeatData.toString();
                                Log.d(TAG, message);
                                if (longSeatData.getStatus().equals("CLOSE_SUCCESS")) {
                                    switchReminder.setChecked(longSeatData.isOpen());
                                    starHour = longSeatData.getStartHour();
                                    starMin = longSeatData.getStartMinute();
                                    endHour = longSeatData.getEndHour();
                                    endMin = longSeatData.getEndMinute();
                                    allTime = longSeatData.getThreshold();
                                }
                            }
                        });
                        break;
                }

            }
        }
    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof SwitchSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//读取
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

                    Log.d(TAG, "isAntiLostSwitch:" + GlobalVarManager.getInstance().isAntiLostSwitch()
                            + "\n isAutoSyncSwitch:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
                            + "\n isSleepSwitch:" + GlobalVarManager.getInstance().isSleepSwitch()
                            + "\n isSleepStateSwitch:" + GlobalVarManager.getInstance().isSleepStateSwitch()
                            + "\n isIncomePhoneSwitch:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
                            + "\n isMissPhoneSwitch:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
                            + "\n isSmsSwitch:" + GlobalVarManager.getInstance().isSmsSwitch()
                            + "\n isSocialSwitch:" + GlobalVarManager.getInstance().isSocialSwitch()
                            + "\n isMailSwitch:" + GlobalVarManager.getInstance().isMailSwitch()
                            + "\n isCalendarSwitch:" + GlobalVarManager.getInstance().isCalendarSwitch()
                            + "\n isSedentarySwitch:" + GlobalVarManager.getInstance().isSedentarySwitch()
                            + "\n isLowPowerSwitch:" + GlobalVarManager.getInstance().isLowPowerSwitch()
                            + "\n isSecondRemindSwitch:" + GlobalVarManager.getInstance().isSecondRemindSwitch()

                            + "\n isSportHRSwitch:" + GlobalVarManager.getInstance().isSportHRSwitch()
                            + "\n isFacebookSwitch:" + GlobalVarManager.getInstance().isFacebookSwitch()
                            + "\n isTwitterSwitch:" + GlobalVarManager.getInstance().isTwitterSwitch()
                            + "\n isInstagamSwitch:" + GlobalVarManager.getInstance().isInstagamSwitch()
                            + "\n isQqSwitch:" + GlobalVarManager.getInstance().isQqSwitch()
                            + "\n isWechatSwitch:" + GlobalVarManager.getInstance().isWechatSwitch()
                            + "\n isWhatsappSwitch:" + GlobalVarManager.getInstance().isWhatsappSwitch()
                            + "\n isLineSwitch:" + GlobalVarManager.getInstance().isLineSwitch());
                    switchReminder.setChecked(GlobalVarManager.getInstance().isSedentarySwitch());
                    closeLoadingDialog();
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置
                    Log.d(TAG, "设置成功");
                    closeLoadingDialog();
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_SET_INACTIVITY_ALERT:
                    Log.e("--------------", "久坐提醒设置成功，间隔时间" + Arrays.toString(objects));
                    break;
                case ResultCallBack.TYPE_GET_INACTIVITY_ALERT:
                    int switchs = (int) objects[0];
                    if (switchs == 0) {
                        switchReminder.setChecked(false);
                        sedentaryText.setVisibility(View.GONE);
                    } else {
                        switchReminder.setChecked(true);
                        sedentaryText.setVisibility(View.VISIBLE);
                    }
                    allTime = (int) objects[2];
                    Log.e("----提醒间隔时间---", allTime + "" + Arrays.toString(objects));
                    sedentaryText.setText((double) (Math.round(allTime) / 60.0) + " h");
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    ArrayList<String> sendtaryData;

    private void addDatas() {
        sendtaryData = new ArrayList<>();
        sendtaryData.clear();
        for (int i = 1; i <= 24; i++) {
            double s = (Math.round(i * 30) / 60.0);// 这样为保持2位.
            sendtaryData.add(String.valueOf(s));
        }
    }

    private void setSendtaryDatas() {

        ProfessionPick stepsnumber = new ProfessionPick.Builder(SedentaryReminder.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                if (TextUtils.isEmpty(profession) || profession == null || profession.equals("")) {
                    profession = "0.5";
                }
                if (profession.contains("h")) {
                    profession = profession.substring(0, profession.length() - 1);
                }
                Log.d(TAG, "-0---选中的是:" + profession);
                //设置预设睡眠
                sedentaryText.setText(profession + " h");
                allTime = (int) (Double.valueOf(profession) * 60);
                Log.d(TAG, "选中的是:" + profession + "--值是：" + allTime);
                switch (is18i) {
                    case "B18i":
                        BluetoothSDK.setInactivityAlert(resultCallBack, 1, 127, allTime, 8, 0, 20, 0, 500);//活动提醒
                        break;
                    case "H9":

                        break;
                    case "B15P":
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        MyApp.getVpOperateManager().settingLongSeat(new IBleWriteResponse() {
                            @Override
                            public void onResponse(int i) {
                                //状态返回
                                closeLoadingDialog();
                            }
                        }, new LongSeatSetting(starHour, starMin, endHour, endMin, allTime, true), new ILongSeatDataListener() {
                            @Override
                            public void onLongSeatDataChange(LongSeatData longSeatData) {
                                String message = "设置久坐-打开:\n" + longSeatData.toString();
                                Log.d(TAG, message);
                                switchReminder.setChecked(longSeatData.isOpen());
                                starHour = longSeatData.getStartHour();
                                starMin = longSeatData.getStartMinute();
                                endHour = longSeatData.getEndHour();
                                endMin = longSeatData.getEndMinute();
                                allTime = longSeatData.getThreshold();
                                sedentaryText.setText((double) (Math.round(allTime) / 60.0) + " h");
                            }
                        });
                        break;
                }

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sendtaryData) //min year in loop
                .dateChose(String.valueOf((allTime / 60.0)) + "h") // date chose when init popwindow
                .build();
        stepsnumber.showPopWin(SedentaryReminder.this);
    }
}
