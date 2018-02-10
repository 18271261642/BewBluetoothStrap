package com.example.bozhilun.android.h9;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.fragment.H9MineFragment;
import com.example.bozhilun.android.h9.fragment.H9RecordFragment;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.data.NewsH9DataFragment;
import com.example.bozhilun.android.siswatch.run.WatchRunFragment;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.device.Language;
import com.sdk.bluetooth.protocol.command.device.WatchID;
import com.sdk.bluetooth.protocol.command.expands.FinishCorroctionTime;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.sdk.bluetooth.protocol.command.user.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @aboutContent: 主页，承载四个fragment的Activity
 * @author： 安
 * @crateTime: 2017/9/27 16:27
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HomeActivity extends WatchBaseActivity {
    private final String TAG = "----->>>" + this.getClass().toString();

    //监听连接状态广播ACTION
    public static final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
    public static final int CONNECT_STATE_CODE = 1001;


    boolean ANTI_LOST = false;
    boolean INCOME_CALL = false;
    boolean MISS_CALL = false;
    boolean SMS = false;
    boolean MAIL = false;
    boolean SOCIAL = false;
    boolean CALENDAR = false;
    boolean SEDENTARY = false;
    boolean QQ = false;
    boolean WECTH = false;
    boolean FACEBOOK = false;
    boolean TWTTER = false;
    boolean LIN = false;

    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    @BindView(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    @BindView(R.id.record_h18ibottomsheet)
    BottomSheetLayout recordH18ibottomsheet;
    private List<Fragment> h18iFragmentList = new ArrayList<>();
    private BluetoothAdapter defaultAdapter;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_STATE_CODE:
                    getDatas();
                    Intent intent = new Intent();
                    intent.setAction(H9CONNECT_STATE_ACTION);
                    intent.putExtra("h9constate", "conn");
                    sendBroadcast(intent);  //发送连接成功的广播
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    MyCommandManager.DEVICENAME = "W06X";
                    SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylany", "W06X");
                    AppsBluetoothManager.getInstance(H9HomeActivity.this).clearBluetoothManagerDeviceConnectListeners();
                    BluetoothConfig.setDefaultMac(H9HomeActivity.this, bluetoothDevice.getAddress());
                    break;
            }
        }
    };

    public void getDatas() {

        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
//                AppsBluetoothManager.getInstance(MyApp.getContext())
//                        .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x01, (byte) 1));//自动同步开关打开
//                subscriber.onNext("自动同步开关打开ok");
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new SwitchSetting(commandResultCallback));//读取通知开关状态
                subscriber.onNext("读取通知开关状态ok");
                setH9WatchLanguage();   //设置手表的语言
                subscriber.onNext("设置手表的语言ok");
                syncUserInfoData(); //同步用户信息
                subscriber.onNext("同步用户信息ok");
                //获取设备时间
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new DateTime(commandResultCallback));
                subscriber.onNext("获取设备时间ok");
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h38i_home);
        ButterKnife.bind(this);
        //注册连接状态的广播
        registerReceiver(h9Receiver, new IntentFilter(H9CONNECT_STATE_ACTION));
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.AppisOne) {
            //检查更新
//            UpdateManager updateManager = new UpdateManager(H9HomeActivity.this, URLs.HTTPs + URLs.getvision);
//            updateManager.checkForUpdate(false);
            MyApp.AppisOne = false;
        }

//        AppsBluetoothManager.getInstance(H9HomeActivity.this).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
        if (MyCommandManager.DEVICENAME != null) {    //已连接
            Intent intent = new Intent();
            intent.setAction(H9CONNECT_STATE_ACTION);
            intent.putExtra("h9constate", "conn");
            sendBroadcast(intent);  //发送连接成功的广播

//                Log.e(TAG, "----时间差---分-----" + aaa + "===" + bbb + "===" + ccc);
            Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
//                    AppsBluetoothManager.getInstance(MyApp.getContext())
//                            .sendCommand(new SwitchSetting(commandResultCallback, 3, (byte) 1, (byte) 0x01, (byte) 1));//自动同步开关打开
//                    subscriber.onNext("自动同步开关打开ok");
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new SwitchSetting(commandResultCallback));//读取通知开关状态
                    subscriber.onNext("读取通知开关状态ok");
                    syncUserInfoData(); //同步用户信息
                    subscriber.onNext("同步用户信息ok");
                    setH9WatchLanguage();   //设置手表的语言
                    subscriber.onNext("设置手表的语言ok");
                    //获取设备时间
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new DateTime(commandResultCallback));
                    subscriber.onNext("获取设备时间ok");
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

//            //打开通知
//            AppsBluetoothManager.getInstance(MyApp.getContext())
//                    .sendCommand(new SwitchSetting(commandResultCallback, 4, (byte) 0x00, "11111011,11111111,00111111", (byte) 0x01));
//            AppsBluetoothManager.getInstance(MyApp.getContext())
//                    .sendCommand(new SwitchSetting(commandResultCallback));//读取通知开关状态
//            setTimes();//设置时间
            //AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new SportSleepMode(commandResultCallback));
//                    sendBroadcast(intent);  //发送连接成功的广播

        } else {  //未连接
            AppsBluetoothManager.getInstance(H9HomeActivity.this).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
            String h9Mac = (String) SharedPreferencesUtils.readObject(H9HomeActivity.this, "mylanmac");
//            Log.e("H9", "---h9mac--" + h9Mac);
            if (!WatchUtils.isEmpty(h9Mac)) {
                AppsBluetoothManager.getInstance(H9HomeActivity.this).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
                AppsBluetoothManager.getInstance(H9HomeActivity.this).connectDevice(h9Mac);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(h9Receiver);
    }

    //同步用户信息
    private void syncUserInfoData() {
        String userData = (String) SharedPreferencesUtils.readObject(H9HomeActivity.this, "saveuserinfodata");
        if (!WatchUtils.isEmpty(userData)) {
            try {
                int weight;
                JSONObject jsonO = new JSONObject(userData);
                String userSex = jsonO.getString("sex");    //性别 男 M ; 女 F
                String userAge = jsonO.getString("birthday");   //生日
                String userWeight = jsonO.getString("weight");  //体重
                String tempWeight = StringUtils.substringBefore(userWeight, "kg").trim();
                if (tempWeight.contains(".")) {
                    weight = Integer.valueOf(StringUtils.substringBefore(tempWeight, ".").trim() + "0");
                } else {
                    weight = Integer.valueOf(tempWeight + "0");
                }
                String userHeight = ((String) SharedPreferencesUtils.getParam(H9HomeActivity.this, "userheight", "")).trim();
                int sex;
                if (userSex.equals("M")) {    //男
                    sex = 0;
                } else {
                    sex = 1;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight);

                //同步用户信息
                AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new UserInfo(commandResultCallback, 5, sex, age, height, weight));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //设置手表的语言
    private void setH9WatchLanguage() {
        //根据系统语言设置手环的语言
        String language = this.getResources().getConfiguration().locale.getLanguage();
        if (!WatchUtils.isEmpty(language)) {
            byte languageTag;
            if (language.equals("zh")) {  //中文
                languageTag = (byte) 0x01;
            } else {
                languageTag = (byte) 0x00;
            }
            // 语言类型 0x00：英文   0x01：中文
            AppsBluetoothManager.getInstance(MyApp.getContext())
                    .sendCommand(new Language(commandResultCallback, languageTag));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //时间或日期变化
        if ("startsynctime".equals(event.getMessage())) {
            setDevTimes();
        }
    }

    private void setDevTimes() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String secon = String.valueOf(calendar.get(Calendar.SECOND));
        if (Integer.valueOf(month) <= 9) {
            month = "0" + month;
        } else {
            month = "" + month;
        }

        if (Integer.valueOf(day) <= 9) {
            day = "0" + day;
        } else {
            day = "" + day;
        }

        if (Integer.valueOf(hour) <= 9) {
            hour = "0" + hour;
        } else {
            hour = "" + hour;
        }

        if (Integer.valueOf(minute) <= 9) {
            minute = "0" + minute;
        } else {
            minute = "" + minute;
        }

        if (Integer.valueOf(secon) <= 9) {
            secon = "0" + secon;
        } else {
            secon = "" + secon;
        }
        String s = Integer.toHexString(year);
        byte[] bytes = H9TimeUtil.string2bytes(s);
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new FinishCorroctionTime(commandResultCallback,
                        bytes,
                        (byte) (int) Integer.valueOf(month),
                        (byte) (int) Integer.valueOf(day),
                        (byte) (int) Integer.valueOf(hour),
                        (byte) (int) Integer.valueOf(minute),
                        (byte) (int) Integer.valueOf(secon)));

//        Log.e("==============", year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + secon);
//        AppsBluetoothManager.getInstance(MyApp.getContext())
//                .sendCommand(new DateTime(commandResultCallback,
//                        7,
//                        Integer.valueOf(year),
//                        Integer.valueOf(month),
//                        Integer.valueOf(day),
//                        Integer.valueOf(hour),
//                        Integer.valueOf(minute),
//                        Integer.valueOf(secon)));//手机时间改变设置设备时间
    }

    boolean isDis = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                setconnectBluetooh();
                break;
            case "STATE_TURNING_ON":
//                Toast.makeText(this, "蓝牙打开", Toast.LENGTH_SHORT).show();
                if (defaultAdapter == null) {
                    defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Intent intent = new Intent();
                intent.setAction(H9CONNECT_STATE_ACTION);
                intent.putExtra("h9constate", "disconn");
                sendBroadcast(intent);  //发送连接失败的广播
                MyCommandManager.DEVICENAME = null;
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                isDis = true;
                defaultAdapter = null;
                break;
        }
    }

    /**
     * lianjie
     */
    private void setconnectBluetooh() {
//        startActivity(SearchDeviceActivity.class);
        startActivity(NewSearchActivity.class);
        finish();
    }


    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        h18iFragmentList.add(new H9RecordFragment()); //记录
//        h18iFragmentList.add(new NewH9RecordFragment()); //记录
//        h18iFragmentList.add(new H9DataFragment());   //数据
        h18iFragmentList.add(new NewsH9DataFragment());   //数据
        // h18iFragmentList.add(new RunningFragment());    //跑步
        h18iFragmentList.add(new WatchRunFragment());   //跑步
        h18iFragmentList.add(new H9MineFragment());   //我的
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), h18iFragmentList);
        h18iViewPager.setAdapter(fragmentPagerAdapter);
        h18iBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        h18iViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:  //开跑
                        h18iViewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_data:     //数据
                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_my:   //我的
                        h18iViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override//返回键拦截
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 处理蓝牙命令回调
     */
    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if ((baseCommand instanceof WatchID)) {//获取设备ＩＤ
                String isClearDB = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "isClearDB");
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "conntWatchID", isClearDB);
                if (isDis) {
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "disWatchID", isClearDB);//保存一个标识
                }
            }
//            else if (baseCommand instanceof GetSportData) {
//                int step = 0;
//                int calorie = 0;
//                int distance = 0;
//                if (GlobalDataManager.getInstance().getSportsDatas() == null) {
//                    Log.d(TAG, "---------null");
//                } else {
//                    for (SportsData sportsData : GlobalDataManager.getInstance().getSportsDatas()) {
//                        step += sportsData.sport_steps;
//                        calorie += sportsData.sport_cal;
//                        distance += sportsData.sport_energy;
//                    }
//                    Log.d(TAG, "步数:" + step + "step" +
//                            "\n 卡路里:" + calorie + "cal" +
//                            "\n 距离:" + distance + "m");
//                }
//            } else if (baseCommand instanceof GetSleepData) {//获取睡眠数据
//                if (GlobalDataManager.getInstance().getSleepDatas() != null) {
//                    // sleepData.sleep_type
//                    // 0：睡着
//                    // 1：浅睡
//                    // 2：醒着
//                    // 3：准备入睡
//                    // 4：退出睡眠
//                    // 16：进入睡眠模式
//                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
//                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
//                    String sleepStr = "";
//                    for (SleepData sleepData : GlobalDataManager.getInstance().getSleepDatas()) {
//                        sleepStr = sleepStr + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepData.sleep_time_stamp * 1000)) + " 类型:" + sleepData.sleep_type + "\n";
//                    }
//                    Log.d(TAG, "sleep data is :" + sleepStr);
//                } else {
//                    Log.d(TAG, "sleep data is null");
//                }
//            } else if (baseCommand instanceof GetHeartData) {//获取心率数据
//                String heartDatas = "";
//                for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
//                    heartDatas += "value:" + heartData.heartRate_value + "---time:" + DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.time_stamp * 1000)) + "\n";
//                }
//                Log.d(TAG, heartDatas);
//            } else

            if (baseCommand instanceof SwitchSetting) {//获取通知开关
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

                    ANTI_LOST = GlobalVarManager.getInstance().isAntiLostSwitch();
                    INCOME_CALL = GlobalVarManager.getInstance().isIncomePhoneSwitch();
                    MISS_CALL = GlobalVarManager.getInstance().isMissPhoneSwitch();
                    SMS = GlobalVarManager.getInstance().isSmsSwitch();
                    MAIL = GlobalVarManager.getInstance().isMailSwitch();
                    SOCIAL = GlobalVarManager.getInstance().isSocialSwitch();
                    CALENDAR = GlobalVarManager.getInstance().isCalendarSwitch();
                    SEDENTARY = GlobalVarManager.getInstance().isSedentarySwitch();
                    QQ = GlobalVarManager.getInstance().isQqSwitch();
                    WECTH = GlobalVarManager.getInstance().isWechatSwitch();
                    FACEBOOK = GlobalVarManager.getInstance().isFacebookSwitch();
                    TWTTER = GlobalVarManager.getInstance().isTwitterSwitch();
                    LIN = GlobalVarManager.getInstance().isLineSwitch();


                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "ANTI_LOST", ANTI_LOST);//同步
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "INCOME_CALL", INCOME_CALL);//来电
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MISS_CALL", MISS_CALL);//未接
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SMS", SMS);//短信
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MAIL", MAIL);//邮件
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SOCIAL", SOCIAL);//社交
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "CALENDAR", CALENDAR);//日历
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SEDENTARY", SEDENTARY);//久坐提醒

                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "QQ", QQ);
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "WECTH", WECTH);
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "FACEBOOK", FACEBOOK);
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "TWTTER", TWTTER);
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "LIN", LIN);
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.d(TAG, "成功");
                }
            } else if (baseCommand instanceof DateTime) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    Log.e(TAG, "----设备时间-----" + GlobalVarManager.getInstance().getDeviceDateTime()
                            + "\n----本地时间-----" + B18iUtils.getSystemDataStart());
                    String deviceDateTime = GlobalVarManager.getInstance().getDeviceDateTime();
                    String[] splitTime = deviceDateTime.split("\\s+");
                    String s1 = splitTime[0];
                    String[] split = s1.split("-");
                    String deviceTimes = split[0].trim();
                    for (int i = 1; i < split.length; i++) {
                        String s = split[i].trim();
                        int integer = Integer.valueOf(s);
                        Log.d(TAG, "------时间值:" + split[i].trim());
                        if (integer > 9) {
                            deviceTimes += "-" + integer;
                        } else if (integer <= 9 && integer >= 0) {
                            deviceTimes += "-0" + integer;
                        }
                    }
                    Log.e(TAG, "----设备时间2-----" + deviceTimes
                            + "\n----本地时间-2----" + B18iUtils.getSystemDataStart());
                    //此处设置时间是根据手表的日期设置，判断是否属于当前的日期   如：2017-12-04
                    if (!deviceTimes.equals(B18iUtils.getSystemDataStart().substring(0, 10))) {
                        setDevTimes();
                    }
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {

                }
            } else if (baseCommand instanceof UserInfo) {  //同步用户信息

            }
//            else if (baseCommand instanceof DeviceDisplaySportSleep) {//获取运动数据
//                Log.d(TAG, "Step:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
//                        "\n Calorie:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
//                        "\n Distance:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
//                        "\n Sleep time:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
//            } else if (baseCommand instanceof DateTime) {//时间设置和获取
//                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
//                    Log.d(TAG, "获取设备时间为：" + GlobalVarManager.getInstance().getDeviceDateTime());
//                }
//                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
//                    Log.d(TAG, "设置时间成功");
//                }
//            }else

        }

        @Override
        public void onFail(BaseCommand baseCommand) {


        }
    };


    /**
     * H9 手表的连接监听
     */
    private BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            Log.d(TAG, "链接成功");
        }

        @Override
        public void onConnectFailed() {
            Log.e(TAG, "-------onConnectFailed------");
            Intent intent = new Intent();
            intent.setAction(H9CONNECT_STATE_ACTION);
            intent.putExtra("h9constate", "disconn");
            sendBroadcast(intent);  //发送连接失败的广播
            MyCommandManager.DEVICENAME = null;
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) { //绑定成功
            Log.e(TAG, "-------onEnableToSendComand------");
            Message message = new Message();
            message.what = CONNECT_STATE_CODE;
            message.obj = bluetoothDevice;
            handler.sendMessage(message);
        }

        @Override
        public void onConnectDeviceTimeOut() {
            Log.e(TAG, "-------onConnectDeviceTimeOut------");
            Intent intent = new Intent();
            intent.setAction(H9CONNECT_STATE_ACTION);
            intent.putExtra("h9constate", "disconn");
            sendBroadcast(intent);  //发送连接失败的广播
            MyCommandManager.DEVICENAME = null;
            //连接超时重新连接
            AppsBluetoothManager.getInstance(H9HomeActivity.this).connectDevice((String) SharedPreferencesUtils.readObject(H9HomeActivity.this, "mylanmac"));
        }
    };

    /**
     * 监听手表连接状态的广播
     */
    private BroadcastReceiver h9Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

}
