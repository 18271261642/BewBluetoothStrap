package com.example.bozhilun.android.B18I.b18isystemic;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import cn.appscomm.bluetooth.protocol.SwitchType;

/**
 * @aboutContent: 通知界面
 * @author： 安
 * @crateTime: 2017/9/5 13:51
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18IIntelligentReminderActivity extends WatchBaseActivity {

    private final String TAG = "----->>>" + this.getClass().toString();
    private static final int READ_PHONE_CONTENT_CODE = 1001;
    private static final int GET_CALENDAR_CODE = 1002;

    @BindView(R.id.switch_Lose)
    Switch switchLose;
    @BindView(R.id.switch_phone)
    Switch switchPhone;
    @BindView(R.id.switch_messge)
    Switch switchMessge;
    @BindView(R.id.switch_missPhone)
    Switch switchMissPhone;
    @BindView(R.id.switch_email)
    Switch switchEmail;
    @BindView(R.id.switch_social)
    Switch switchCocial;


    @BindView(R.id.switch_QQ)
    Switch switchQQ;
    @BindView(R.id.switch_Wechat)
    Switch switchWechat;
    @BindView(R.id.switch_Facebook)
    Switch switchFacebook;
    @BindView(R.id.switch_Twittter)
    Switch switchTwittter;
    @BindView(R.id.switch_Linkedin)
    Switch switchLinkedin;
    @BindView(R.id.liner_social)
    LinearLayout linerSocial;


    @BindView(R.id.switch_calendar)
    Switch switchCalendar;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.watch_msgOpenNitBtn)
    RelativeLayout watchMsgOpenNitBtn;
    @BindView(R.id.watch_msgOpenAccessBtn)
    RelativeLayout watchMsgOpenAccessBtn;
    private boolean LOST = false;
    private boolean PHONE = false;
    private boolean MISSPHONE = false;
    private boolean MSG = false;
    private boolean EMAIL = false;
    private boolean SOCIAL = false;//社交

    private boolean QQ = false;
    private boolean WECTH = false;
    private boolean FACEBOOK = false;
    private boolean TWTTER = false;
    private boolean LIN = false;

    private boolean CALENDAR = false;//calendar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_intelligent_reminder_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.notice_str));
        linerSocial.setVisibility(View.GONE);
        whichDevice();//判断是B18i还是H9
        switchLose.setOnCheckedChangeListener(new SwitchChangeListenter());//防丢失
        switchPhone.setOnCheckedChangeListener(new SwitchChangeListenter());//来电
        switchMissPhone.setOnCheckedChangeListener(new SwitchChangeListenter());//未接来电
        switchMessge.setOnCheckedChangeListener(new SwitchChangeListenter());//短信
        switchEmail.setOnCheckedChangeListener(new SwitchChangeListenter());//邮箱

        switchCocial.setOnCheckedChangeListener(new SwitchChangeListenter());//社交

        switchQQ.setOnCheckedChangeListener(new SwitchChangeListenter());//qq
        switchWechat.setOnCheckedChangeListener(new SwitchChangeListenter());//微信
        switchFacebook.setOnCheckedChangeListener(new SwitchChangeListenter());//facebook
        switchTwittter.setOnCheckedChangeListener(new SwitchChangeListenter());//推特
        switchLinkedin.setOnCheckedChangeListener(new SwitchChangeListenter());//领英

        switchCalendar.setOnCheckedChangeListener(new SwitchChangeListenter());//日历
        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        //whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i){
            case "B18i":
                BluetoothSDK.getSwitchSetting(resultCallBack);//获取开关设置
                break;
            case "H9":
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new SwitchSetting(commandResultCallback));
                showLoadingDialog(getResources().getString(R.string.dlog));
                //打开通知
//            AppsBluetoothManager.getInstance(MyApp.getContext())
//                    .sendCommand(new SwitchSetting(commandResultCallback, 4, (byte) 0x00, "11111011,11111111,00111111", (byte) 0x01));
                break;
            case "B15P":

                break;
        }
    }

    private byte myByte = 0;

    @OnClick({R.id.watch_msgOpenNitBtn, R.id.watch_msgOpenAccessBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_msgOpenNitBtn:  //手动打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 101);
                break;
            case R.id.watch_msgOpenAccessBtn:   //手动打开辅助开关
                Intent ints = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(ints,102);
                break;
        }
    }

    private class SwitchChangeListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
                myByte = 1;
            } else {
                myByte = 0;
            }
            switch (buttonView.getId()) {
                case R.id.switch_Lose:
                    Log.d(TAG, isChecked + "");
                    if (is18i.equals("B18i")) {
                        switchLose.setChecked(isChecked);
                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.ANTI_LOST, isChecked);
                    } else {
                        //H9设置
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0, myByte));//防止丢失
                    }
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "ANTI_LOST", isChecked);
                    break;
                case R.id.switch_phone:
                    if (is18i.equals("B18i")) {
                        switchPhone.setChecked(isChecked);
                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.INCOME_CALL, isChecked);
                    } else {
                        //H9设置
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 4, myByte));//来电
                    }
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "INCOME_CALL", isChecked);
                    break;
                case R.id.switch_missPhone: //来电提醒
//                    if(AndPermission.hasPermission(B18IIntelligentReminderActivity.this,Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE)){
//                        //已获取权限
//
//                    }else{
//                        //未获取权限
//
//                    }

                    AndPermission.with(B18IIntelligentReminderActivity.this)
                            .requestCode(READ_PHONE_CONTENT_CODE)
                            .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    AndPermission.rationaleDialog(B18IIntelligentReminderActivity.this, rationale).show();
                                }
                            })
                            .callback(new PermissionListener() {
                                @Override
                                public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                    if (is18i.equals("B18i")) {
                                        switchMissPhone.setChecked(isChecked);
                                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.MISS_CALL, isChecked);
                                    } else {
                                        //H9设置
                                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 0x01, (byte) 0x05, myByte));//未接来电
                                    }
                                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MISS_CALL", isChecked);
                                }

                                @Override
                                public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                    //AndPermission.defaultSettingDialog(B18IIntelligentReminderActivity.this, READ_PHONE_CONTENT_CODE).show();
                                    if(AndPermission.hasAlwaysDeniedPermission(B18IIntelligentReminderActivity.this,deniedPermissions)){
                                        AndPermission.defaultSettingDialog(B18IIntelligentReminderActivity.this)
                                                .setTitle("提示")
                                                .setMessage("请求权限失败,是否手动打开？")
                                                .setPositiveButton("是")
                                                .show();
                                    }

                                }
                            }).start();
                    break;
                case R.id.switch_messge:
                    if (is18i.equals("B18i")) {
                        switchMessge.setChecked(isChecked);
                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.SMS, isChecked);
                    } else {
                        //H9设置
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 6, myByte));//短信
                    }

                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SMS", isChecked);
                    break;
                case R.id.switch_email:
                    if (is18i.equals("B18i")) {
                        switchEmail.setChecked(isChecked);
                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.MAIL, isChecked);
                    } else {
                        //H9设置
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 8, myByte));//邮件
                    }
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MAIL", isChecked);
                    break;
                case R.id.switch_social:
                    if (is18i.equals("B18i")) {
                        switchCocial.setChecked(isChecked);
                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.SOCIAL, isChecked);
                    } else {
                        //H9设置
                        showLoadingDialog(getResources().getString(R.string.dlog));
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 7, myByte));//社交
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 10, myByte));//INSTAGRAM开关

                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 13, myByte));//WHATSAPP开关
//                        AppsBluetoothManager.getInstance(MyApp.getContext())
//                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 15, myByte));//SKYPE开关
                        if (isChecked) {
                            linerSocial.setVisibility(View.VISIBLE);
                        } else {
                            linerSocial.setVisibility(View.GONE);
                        }
                    }
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SOCIAL", isChecked);
                    break;
                case R.id.switch_calendar:
                    AndPermission.with(B18IIntelligentReminderActivity.this)
                            .requestCode(GET_CALENDAR_CODE)
                            .permission(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    AndPermission.rationaleDialog(B18IIntelligentReminderActivity.this, rationale).show();
                                }
                            })
                            .callback(new PermissionListener() {
                                @Override
                                public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                    if (is18i.equals("B18i")) {
                                        switchCalendar.setChecked(isChecked);
                                        BluetoothSDK.setSwitchSetting(resultCallBack, SwitchType.CALENDAR, isChecked);
                                    } else {
                                        //H9设置
                                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                                .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 9, myByte));//日历
                                        showLoadingDialog(getResources().getString(R.string.dlog));
                                    }
                                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "CALENDAR", isChecked);
                                }

                                @Override
                                public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                    AndPermission.defaultSettingDialog(B18IIntelligentReminderActivity.this, GET_CALENDAR_CODE).show();
                                }
                            }).start();
                    break;
                case R.id.switch_QQ:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x11, myByte));//qq
                    break;
                case R.id.switch_Wechat:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x12, myByte));//WECHAT开关
                    break;
                case R.id.switch_Facebook:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0E, myByte));//FACEBOOK开关
                    break;
                case R.id.switch_Twittter:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x0F, myByte));//TWITTER开关
                    break;
                case R.id.switch_Linkedin:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x14, myByte));//LINE开关
                    break;
            }
        }
    }


    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_SWITCH_SETTING://获取开关设置
// (SwitchType)0防盗，1同步，2睡眠，3睡状，4来电，5未接来电，6短信，7社交，8邮箱，9日历，10久坐，11低功耗，12二提醒，13提高唤醒
//[true, false, false, false, true, true, true, true, true, true, false, false, false, false]
                    LOST = (boolean) objects[0];
                    PHONE = (boolean) objects[4];
                    MISSPHONE = (boolean) objects[5];
                    MSG = (boolean) objects[6];
                    EMAIL = (boolean) objects[8];
                    SOCIAL = (boolean) objects[7];
                    CALENDAR = (boolean) objects[9];
                    switchLose.setChecked(LOST);
                    switchPhone.setChecked(PHONE);
                    switchMissPhone.setChecked(MISSPHONE);
                    switchMessge.setChecked(MSG);
                    switchEmail.setChecked(EMAIL);
                    switchCocial.setChecked(SOCIAL);
                    switchCalendar.setChecked(CALENDAR);
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    //H9
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
                    LOST = (boolean) GlobalVarManager.getInstance().isAntiLostSwitch();
                    PHONE = (boolean) GlobalVarManager.getInstance().isIncomePhoneSwitch();
                    MISSPHONE = (boolean) GlobalVarManager.getInstance().isMissPhoneSwitch();
                    MSG = (boolean) GlobalVarManager.getInstance().isSmsSwitch();
                    EMAIL = (boolean) GlobalVarManager.getInstance().isMailSwitch();
                    SOCIAL = (boolean) GlobalVarManager.getInstance().isSocialSwitch();

                    QQ = (boolean) GlobalVarManager.getInstance().isQqSwitch();
                    WECTH = (boolean) GlobalVarManager.getInstance().isWechatSwitch();
                    FACEBOOK = (boolean) GlobalVarManager.getInstance().isFacebookSwitch();
                    TWTTER = (boolean) GlobalVarManager.getInstance().isTwitterSwitch();
                    LIN = (boolean) GlobalVarManager.getInstance().isLineSwitch();

                    CALENDAR = (boolean) GlobalVarManager.getInstance().isCalendarSwitch();
                    switchLose.setChecked(LOST);
                    switchPhone.setChecked(PHONE);
                    switchMissPhone.setChecked(MISSPHONE);
                    switchMessge.setChecked(MSG);
                    switchEmail.setChecked(EMAIL);
                    switchCocial.setChecked(SOCIAL);
                    if (SOCIAL) {
                        linerSocial.setVisibility(View.VISIBLE);
                    } else {
                        linerSocial.setVisibility(View.GONE);
                    }
                    switchCalendar.setChecked(CALENDAR);

                    switchQQ.setChecked(QQ);
                    switchWechat.setChecked(WECTH);
                    switchFacebook.setChecked(FACEBOOK);
                    switchTwittter.setChecked(TWTTER);
                    switchLinkedin.setChecked(LIN);
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
}
