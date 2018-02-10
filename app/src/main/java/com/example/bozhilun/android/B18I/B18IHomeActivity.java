package com.example.bozhilun.android.B18I;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.afa.tourism.greendao.gen.B18iHeartDatasDao;
import com.afa.tourism.greendao.gen.B18iSleepDatasDao;
import com.afa.tourism.greendao.gen.B18iStepDatasDao;
import com.afa.tourism.greendao.gen.B18iUserInforDatasDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.example.bozhilun.android.B18I.b18idata.B18iDataFragment;
import com.example.bozhilun.android.B18I.b18imine.B18iMineFragment;
import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18irecord.B18iRecordFragment;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.fragment.RunningFragment;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * Created by Administrator on 2017/8/25.
 */

/**
 * B18i的主页面
 */
public class B18IHomeActivity extends WatchBaseActivity {

    private final String TAG = "---->>>" + this.getClass().toString();

    public static final String B18ICONNECT_ACTION = "com.example.bozhilun.android.B18I.connstate";

    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    @BindView(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    @BindView(R.id.record_h18ibottomsheet)
    BottomSheetLayout recordH18ibottomsheet;
    Handler mHandler = new Handler();


    private List<Fragment> h18iFragmentList = new ArrayList<>();


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //时间或日期变化
        if ("startsynctime".equals(event.getMessage()) || "startsynctime".equals(event.getMessage())) {
            BluetoothSDK.setDeviceTime(resultCallBack, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                setconnectBluetooh();
                break;
            case "STATE_TURNING_ON":
                Toast.makeText(this, "蓝牙打开", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "蓝牙关闭", Toast.LENGTH_SHORT).show();
                BluetoothSDK.getSN(B18iResultCallBack.getB18iResultCallBack());
                defaultAdapter = null;
                break;
        }
    }

    BluetoothAdapter defaultAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h38i_home);
        ButterKnife.bind(this);
        Log.e(TAG, "-----onCreate----");
        //注册监听连接状态的广播
        registerReceiver(connReceiver,new IntentFilter(B18ICONNECT_ACTION));
        BluetoothSDK.getSwitchSetting(resultCallBack);//获取开关设置
        initViews();
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        /**
         * 定时读取手环数据
         */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "定时读取手环数据");
                if (BluetoothSDK.isConnected()) {
                    //获取运动数据
                    BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
                    //获取心率数据
                    BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
                    //获取睡眠数据
                    BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());
                } else {
                    String h38iMac = (String) SharedPreferencesUtils.readObject(B18IHomeActivity.this, "mylanyamac");
                    if (!WatchUtils.isEmpty(h38iMac)) {
                        BluetoothSDK.connectByMAC(resultCallBack, h38iMac);
                    }
                }
                // 循环调用实现定时刷新界面
                mHandler.postDelayed(this, 5 * (1000 * 60));//300000ms = 5min  600000ms = 10 min
            }
        };
        mHandler.postDelayed(runnable, 5 * (1000 * 60));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "-----onRestart----" + BluetoothSDK.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setconnectBluetooh();
    }

    /**
     * lianjie
     */
    private void setconnectBluetooh() {
        Log.e(TAG, "----onResume-----" + BluetoothSDK.isConnected());
        String h38iMac = (String) SharedPreferencesUtils.readObject(B18IHomeActivity.this, "mylanyamac");
        Log.e(TAG, "-------macs---" + h38iMac + "========");
        if (defaultAdapter != null) {
            if (defaultAdapter.enable()) {
                if (!WatchUtils.isEmpty(h38iMac)) {
                    if (!BluetoothSDK.isConnected()) {    //判断是否已经连接，未连接时连接
                        BluetoothSDK.connectByMAC(resultCallBack, h38iMac);
//                        BluetoothSDK.getSN(resultCallBack);
                    } else {
                        //发送连接成功的广播
                        Intent ins1 = new Intent();
                        ins1.setAction(B18ICONNECT_ACTION);
                        ins1.putExtra("b18iconstate","b18iconn");
                        sendBroadcast(ins1);

                        BluetoothSDK.getSN(resultCallBack);
                        SharedPreferencesUtils.saveObject(MyApp.getApplication(), "mylanya", "B18I");//保存一个标识
//                        BluetoothSDK.backToHome(resultCallBack);
                        BluetoothSDK.setDeviceTime(resultCallBack, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND));
                    }
                }
                Log.e(TAG, "-----是否连接----" + BluetoothSDK.isConnected());
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "----onPause-----" + BluetoothSDK.isConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "---onStop------" + BluetoothSDK.isConnected());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---onDestroy------" + BluetoothSDK.isConnected());
        unregisterReceiver(connReceiver);   //卸载连接状态的广播
    }

    private void initViews() {
        h18iFragmentList.add(new B18iRecordFragment()); //记录
        h18iFragmentList.add(new RunningFragment());    //跑步
        h18iFragmentList.add(new B18iDataFragment());   //数据
        h18iFragmentList.add(new B18iMineFragment());   //我的
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

                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_data:     //数据

                        h18iViewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_my:   //我的

                        h18iViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }


    //    private boolean LOST = false;
//    private boolean PHONE = false;
//    private boolean MISSPHONE = false;
//    private boolean MSG = false;
//    private boolean EMAIL = false;
//    private boolean SOCIAL = false;//社交
//    private boolean CALENDAR = false;//calendar
    private DaoSession daoSession = MyApp.getDBManager().getDaoSession();
    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_CONNECT:   //连接成功
                    //发送连接成功的广播
                    Intent ins1 = new Intent();
                    ins1.setAction(B18ICONNECT_ACTION);
                    ins1.putExtra("b18iconstate","b18iconn");
                    sendBroadcast(ins1);
                    break;
                case ResultCallBack.TYPE_GET_SWITCH_SETTING://获取开关设置
// (SwitchType)0防盗，1同步，2睡眠，3睡状，4来电，5未接来电，6短信，7社交，8邮箱，9日历，10久坐，11低功耗，12二提醒，13提高唤醒
//[true, false, false, false, true, true, true, true, true, true, false, false, false, false]
//                    LOST = (boolean) objects[0];
//                    LOST = (boolean) objects[1];
//                    LOST = (boolean) objects[2];
//                    LOST = (boolean) objects[3];
//                    PHONE = (boolean) objects[4];
//                    MISSPHONE = (boolean) objects[5];
//                    MSG = (boolean) objects[6];
//                    EMAIL = (boolean) objects[8];
//                    SOCIAL = (boolean) objects[7];
//                    CALENDAR = (boolean) objects[9];
//                    LOST = (boolean) objects[10];
//                    LOST = (boolean) objects[11];
//                    LOST = (boolean) objects[12];
//                    LOST = (boolean) objects[13];
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "ANTI_LOST", (boolean) objects[0]);//同步
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "INCOME_CALL", (boolean) objects[4]);//来电
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MISS_CALL", (boolean) objects[5]);//未接
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SMS", (boolean) objects[6]);//短信
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "MAIL", (boolean) objects[8]);//邮件
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "SOCIAL", (boolean) objects[7]);//社交
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "CALENDAR", (boolean) objects[9]);//日历
                    break;
                case ResultCallBack.TYPE_GET_SN:
                    String isClearDB = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "isClearDB");
                    String s = String.valueOf(objects[0]);
                    Log.e("------sn---", Arrays.toString(objects) + "===========" + isClearDB + "===" + s);
                    if (isClearDB == null || !s.equals(isClearDB)) {
                        B18iStepDatasDao stepDatasDao = daoSession.getB18iStepDatasDao();
                        stepDatasDao.deleteAll();
                        B18iHeartDatasDao heartDatasDao = daoSession.getB18iHeartDatasDao();
                        heartDatasDao.deleteAll();
                        B18iSleepDatasDao sleepDatasDao = daoSession.getB18iSleepDatasDao();
                        sleepDatasDao.deleteAll();
                        B18iUserInforDatasDao userInforDatasDao = daoSession.getB18iUserInforDatasDao();
                        userInforDatasDao.deleteAll();
                    }
                    break;
            }
        }

        @Override
        public void onFail(int i) { //连接失败
            ToastUtil.showToast(B18IHomeActivity.this, "connection failed");
            //发送连接失败的广播
            Intent ins2 = new Intent();
            ins2.setAction(B18ICONNECT_ACTION);
            ins2.putExtra("b18iconstate","b18idisconn");
            sendBroadcast(ins2);
        }
    };

    /**
     * 检测连接状态的广播
     */
    private BroadcastReceiver connReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
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
}
