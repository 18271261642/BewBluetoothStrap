package com.example.bozhilun.android.b15p;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b15p.fragment.B15pRecordFragment;
import com.example.bozhilun.android.b15p.fragment.B15pMineFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.data.NewsH9DataFragment;
import com.example.bozhilun.android.siswatch.run.WatchRunFragment;
import com.example.bozhilun.android.siswatch.utils.SharedPreferenceUtil;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IABluetoothStateListener;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.util.SportUtil;
import com.veepoo.protocol.util.VPLogger;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class B15pHomeActivity extends WatchBaseActivity {

    private static final String TAG = "B15pHomeActivity";
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

    boolean isDis = false;
    //监听连接状态广播ACTION
    public static final String H9CONNECT_STATE_ACTION = "com.example.bozhilun.android.h9.connstate";
    public static final String H9CONNECT_DISCONN_STATE_ACTION = "com.example.bozhilun.android.h9.disconnect"; //断开连接的广播
    public static final int CONNECT_STATE_CODE = 1001;  //已连接
    public static final int DISCONNECT_STATE_CODE = 1002;   //断开连接
    private static final int AUTO_CONNECT_CODE = 1003;  //自动连接
    private static final int STOP_SCAN_DEVICE = 1004;   //停止扫描

    private String bleName,bleMac;
    //自动连接
    private boolean autoConnect ;
    //蓝牙是否打开
    private boolean isOpen = false;
    private boolean isAuto = false;
    private int count = 0;
    private List<String> scanList = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_STATE_CODE:    //连接成功
                    //getDatas();
                    Intent intent = new Intent();
                    intent.setAction(H9CONNECT_STATE_ACTION);
                    intent.putExtra("h9constate", "conn");
                    sendBroadcast(intent);  //发送连接成功的广播
                    MyCommandManager.DEVICENAME = "B15P";
                    //syncUserInfoData(); //同步用户信息
                    break;
                case DISCONNECT_STATE_CODE: //连接断开
                    Intent intents = new Intent();
                    intents.setAction(H9CONNECT_STATE_ACTION);
                    intents.putExtra("h9constate", "disconn");
                    sendBroadcast(intents);  //发送连接成功的广播
                    MyCommandManager.DEVICENAME = null;
                    break;
                case AUTO_CONNECT_CODE: //自动连接
                    VPLogger.e("--autoConnect="+autoConnect);
                    if(!autoConnect){
                        handler.removeCallbacks(runnable);
                    }else{
                        Log.e(TAG,"----111--=");
                        connectBleDevice(bleMac);
                    }
                    break;
                case STOP_SCAN_DEVICE:  //停止扫描
                    MyApp.getVpOperateManager().stopScanDevice();
                    connectBleDevice(bleMac);
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,5000);
            Log.e(TAG,"-----循环了---="+count);
            handler.sendEmptyMessage(AUTO_CONNECT_CODE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h38i_home);
        ButterKnife.bind(this);
        initViews();
        getBleMac();    //获取保存的蓝牙名称和地址

        //注册连接状态的广播
        registerReceiver(h9Receiver, new IntentFilter(H9CONNECT_STATE_ACTION));
        registerBluetoothStateListener();
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if(defaultAdapter != null && !BluetoothUtils.isBluetoothEnabled()){ //蓝牙未打开
            BluetoothUtils.openBluetooth();
        }

    }

    //获取保存的蓝牙名称和地址
    private void getBleMac() {
        bleName = (String) SharedPreferencesUtils.readObject(B15pHomeActivity.this,"mylanya");
        bleMac = (String) SharedPreferencesUtils.readObject(B15pHomeActivity.this,"mylanmac");
    }

    @Override
    protected void onStart() {
        super.onStart();
        VPLogger.setDebug(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBleMac();
        //监听蓝牙打开或关闭状态
        //在进入时判断蓝牙是否打开或者关闭
        MyApp.getVpOperateManager().registerBluetoothStateListener(mBluetoothStateListener);
        //监听连接状态
        MyApp.getVpOperateManager().registerConnectStatusListener(bleMac,connstateListener);
        if(MyCommandManager.DEVICENAME != null){    //已经连接成功了
            VPLogger.e("-------搜索进来--已经连接");
            String longTime = (String) SharedPreferenceUtil.get(B15pHomeActivity.this,"saveLongTime","");

            if(!WatchUtils.isEmpty(longTime+"") && WatchUtils.getNowTime()-Long.valueOf(longTime) >=300 * 1000){
                Log.e(TAG,"----longTime----="+longTime+"--="+System.currentTimeMillis()+"----间隔时间="+(WatchUtils.getNowTime()-Long.valueOf(longTime)));
                handler.sendEmptyMessage(CONNECT_STATE_CODE);
            }else{
                handler.sendEmptyMessage(CONNECT_STATE_CODE);
            }

        }else{
            //连接
            connectBleDevice(bleMac);
        }
    }

    //连接
    private boolean connectBleDevice(String bleMac) {
        count++;
        MyApp.getVpOperateManager().connectDevice(bleMac, new IConnectResponse() {
            @Override
            public void connectState(int state, BleGattProfile bleGattProfile, boolean b) {
                Log.e(TAG,"连接state="+state);
                if(state == Code.REQUEST_SUCCESS){  //已经连接成功了，未设置通知，此时不可数据交互
                    autoConnect = false;
                    if(isAuto){
                        handler.removeCallbacks(runnable);
                    }

                }else{
                    autoConnect = true;
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int code) {
                Log.e(TAG,"连接设置通知code="+code);
                if(code == Code.REQUEST_SUCCESS){   //设置监听成功，可以交互数据
                    autoConnect = false;
                    handler.sendEmptyMessage(CONNECT_STATE_CODE);
                }
            }
        });
        return autoConnect;
    }

    //监听连接状态
    private IABleConnectStatusListener connstateListener = new IABleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int stateCode) {
            VPLogger.e("----监听状态code="+stateCode);
            if(stateCode == Constants.STATUS_CONNECTED){    //连接成功
                VPLogger.e("-----监听连接成功---");
            }else{
                handler.sendEmptyMessage(DISCONNECT_STATE_CODE);
                if(!WatchUtils.isEmpty(bleMac)){    //非正常断开
                    VPLogger.e("----非正常断开--");
                    autoConnect = true;
                    if(BluetoothUtils.isBluetoothEnabled() && !isOpen)
                        bleAutoConn();
                }
            }
        }
    };

    //自动连接
    private void bleAutoConn() {
        isAuto = true;
        handler.postDelayed(runnable,3000);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(h9Receiver);
    }

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;

    private void getDatas() {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                getNitifStute();
                subscriber.onNext("读取通知开关状态ok");
                syncUserInfoData(); //同步用户信息
                subscriber.onNext("同步用户信息ok");
                setH9WatchLanguage();   //设置手表的语言
                subscriber.onNext("设置手表的语言ok");
                //获取设备时间
//                    subscriber.onNext("获取设备时间ok");
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

    private void getNitifStute() {
        MyApp.getVpOperateManager().readSocialMsg(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                String message = " 社交信息提醒-读取:\n" + socailMsgData.toString();
                Log.d(TAG, message);
                EFunctionStatus facebook = socailMsgData.getFacebook();
                EFunctionStatus flickr = socailMsgData.getFlickr();
                EFunctionStatus gmail = socailMsgData.getGmail();
                EFunctionStatus instagram = socailMsgData.getInstagram();
                EFunctionStatus line = socailMsgData.getLine();
                EFunctionStatus msg = socailMsgData.getMsg();
                EFunctionStatus other = socailMsgData.getOther();
                EFunctionStatus phone = socailMsgData.getPhone();
                EFunctionStatus qq = socailMsgData.getQq();
                EFunctionStatus sina = socailMsgData.getSina();
                EFunctionStatus skype = socailMsgData.getSkype();
                EFunctionStatus snapchat = socailMsgData.getSnapchat();
                EFunctionStatus twitter = socailMsgData.getTwitter();
                EFunctionStatus wechat = socailMsgData.getWechat();
                EFunctionStatus whats = socailMsgData.getWhats();
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "ANTI_LOST", ANTI_LOST);//同步
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "INCOME_CALL", phone);//来电
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MISS_CALL", MISS_CALL);//未接
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SMS", msg);//短信
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MAIL", MAIL);//邮件
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SOCIAL", SOCIAL);//社交
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "CALENDAR", CALENDAR);//日历
//                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SEDENTARY", SEDENTARY);//久坐提醒

                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "QQ", qq);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "WECTH", wechat);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "FACEBOOK", facebook);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "TWTTER", twitter);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "LIN", line);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "WHATS", whats);
                SharedPreferencesUtils.saveObject(MyApp.getApplication(), "INSTAGRM", instagram);
//                Logger.t(TAG).i(message);
//                sendMsg(message, 1);
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

    //同步用户信息
    private void syncUserInfoData() {
        SharedPreferenceUtil.put(B15pHomeActivity.this,"saveLongTime",System.currentTimeMillis()+"");
        String userData = (String) SharedPreferencesUtils.readObject(B15pHomeActivity.this, "saveuserinfodata");
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
                String userHeight = ((String) SharedPreferencesUtils.getParam(B15pHomeActivity.this, "userheight", "")).trim();
                ESex sex;
                if (userSex.equals("M")) {    //男
                    sex = ESex.MAN;
                } else {
                    sex = ESex.WOMEN;
                }
                int age = WatchUtils.getAgeFromBirthTime(userAge);  //年龄
                int height = Integer.valueOf(userHeight);


                MyApp.getVpOperateManager().settingDeviceLanguage(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new ILanguageDataListener() {
                    @Override
                    public void onLanguageDataChange(LanguageData languageData) {
                        Log.e(TAG,"----language--"+languageData.getLanguage());
                    }
                }, ELanguage.CHINA);

                //同步用户信息
//                AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(new UserInfo(commandResultCallback, 5, sex, age, height, weight));

                //根据性别、身高、体重算出来的目标值
//                int aimSportCount = SportUtil.getAimSportCount(sex, weight, height);
//                SharedPreferencesUtils.setParam(this, "b15pTag", aimSportCount);
//                int aimDistance = (int) SportUtil.getAimDistance(sex, weight, height);
//                SharedPreferencesUtils.setParam(this, "b15pDid", aimDistance);
//                int aimKcalNew = (int) SportUtil.getAimKcal(sex, weight, height, true);
//                SharedPreferencesUtils.setParam(this, "b15pKcl", aimKcalNew);
                String aimSportCount = (String) SharedPreferencesUtils.getParam(B15pHomeActivity.this,"settagsteps","");

                MyApp.getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new IPersonInfoDataListener() {
                    @Override
                    public void OnPersoninfoDataChange(EOprateStauts EOprateStauts) {
                        String message = "同步个人信息:\n" + EOprateStauts.toString();
                        Log.d(TAG, message);
//                        Logger.t(TAG).i(message);
//                        sendMsg(message, 1);
                    }
                }, new PersonInfoData(sex, height, weight, age, Integer.valueOf(aimSportCount.trim())));

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
            if (language.equals("zh")) {  //中文
                // 语言类型 0x00：英文   0x01：中文
                MyApp.getVpOperateManager().settingDeviceLanguage(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new ILanguageDataListener() {
                    @Override
                    public void onLanguageDataChange(LanguageData languageData) {
                        String message = "设置语言(中文):\n" + languageData.toString();
                        Log.d(TAG, message);
//                    Logger.t(TAG).i(message);
//                    sendMsg(message, 1);
                    }
                }, ELanguage.CHINA);
            } else {
                // 语言类型 0x00：英文   0x01：中文
                MyApp.getVpOperateManager().settingDeviceLanguage(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new ILanguageDataListener() {
                    @Override
                    public void onLanguageDataChange(LanguageData languageData) {
                        String message = "设置语言(中文):\n" + languageData.toString();
                        Log.d(TAG, message);
//                    Logger.t(TAG).i(message);
//                    sendMsg(message, 1);
                    }
                }, ELanguage.ENGLISH);
            }

        }
    }

    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        //设置目标步数
        String b15pTagSteps = (String) SharedPreferencesUtils.getParam(B15pHomeActivity.this,"settagsteps","");
        if(WatchUtils.isEmpty(b15pTagSteps)){
            SharedPreferencesUtils.setParam(B15pHomeActivity.this,"settagsteps","10000");
        }
        h18iFragmentList.add(new B15pRecordFragment()); //记录
        h18iFragmentList.add(new NewsH9DataFragment());   //数据
        h18iFragmentList.add(new WatchRunFragment());   //跑步
        h18iFragmentList.add(new B15pMineFragment());   //我的
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentAdapter(this.getSupportFragmentManager(), h18iFragmentList);
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

    /**
     * 监听手表连接状态的广播
     */
    private BroadcastReceiver h9Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    /**
     * 蓝牙打开or关闭状态
     */
    private void registerBluetoothStateListener() {
        MyApp.getVpOperateManager().registerBluetoothStateListener(mBluetoothStateListener);
    }

    /**
     * 监听蓝牙与设备间的回调状态
     */
    private final IABluetoothStateListener mBluetoothStateListener = new IABluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.d(TAG, "open==================" + openOrClosed);
            isOpen = openOrClosed;
            if(openOrClosed){
                if(!WatchUtils.isEmpty(bleMac)){
                    MyApp.getVpOperateManager().startScanDevice(searchResponse);
                }

            }
        }
    };

    //扫描回调
    private SearchResponse searchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {

        }

        @Override
        public void onDeviceFounded(final SearchResult searchResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(searchResult.getAddress() != null){
                        scanList.add(searchResult.getAddress());
                        if(scanList.contains(bleMac)){
                            handler.sendEmptyMessage(STOP_SCAN_DEVICE);
                        }
                    }
                }
            });
        }

        @Override
        public void onSearchStopped() {

        }

        @Override
        public void onSearchCanceled() {

        }
    };
}
