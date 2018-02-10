package com.example.bozhilun.android.siswatch;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.PhoneBroadcastReceiver;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SampleGattAttributes;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.bleus.WatchBluetoothService;
import com.example.bozhilun.android.siswatch.data.WatchH8DataFragment;
import com.example.bozhilun.android.siswatch.dataserver.UploadStepsPressent;
import com.example.bozhilun.android.siswatch.dataserver.UploadStepsView;
import com.example.bozhilun.android.siswatch.mine.WatchMineFragment;
import com.example.bozhilun.android.siswatch.record.WatchRecordFragment;
import com.example.bozhilun.android.siswatch.run.WatchRunFragment;
import com.example.bozhilun.android.siswatch.utils.BlueAdapterUtils;
import com.example.bozhilun.android.siswatch.utils.PhoneStateListenerInterface;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.example.bozhilun.android.bleutil.Customdata.makeGattUpdateIntentFilter;

/**
 * Created by Administrator on 2017/7/17.
 */

/**
 * H8手表主页面
 */
public class WatchHomeActivity extends WatchBaseActivity implements PhoneStateListenerInterface,UploadStepsView{

    private static final String TAG = "WatchHomeActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private static final int H8_CONECT_SUCCESS_CODE = 777;  //连接成功
    private static final int H8_DISCONNECT_CODE = 888;  //连接失败


    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    @BindView(R.id.record_bottomsheet)
    BottomSheetLayout recordBottomsheet;

    SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
    private BluetoothAdapter bluetoothAdapter;


    private WatchBluetoothService mBluetoothService = MyApp.getInstance().getWatchBluetoothService(); //service
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    //写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;
    //读数据
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattService readMnotyGattService;

    //蓝牙名称和地址
    private String mDeviceName;
    private String mDeviceAddress;

    private List<Fragment> watchfragments;


    private boolean isrefresh = false;

    private Timer timer = new Timer();
    private TimerTask task;


    UpdateManager updateManager;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    writedatatoble();   //写入获取步数的数据
                    break;
                case 2: //接收数据
                    String bledata = (String) msg.obj;
                    if (bledata != null) {
                        int steps = Customdata.hexStringToAlgorism(bledata);
                        Log.e(TAG, "-----steps--" + steps);
                        EventBus.getDefault().post(new MessageEvent("steprecord", String.valueOf(steps)));

                    }
                    break;
                case 3:
                    if (mConnected) {
                        Log.e(TAG, "-------执行了---");
                        characteristic.setValue(WatchConstants.watchSteps);
                        mBluetoothService.writeCharacteristic(characteristic);
                    }
                    break;
                case 111:
                    closeLoadingDialog();
                    break;
                case H8_CONECT_SUCCESS_CODE:   //蓝牙连接成功
                    mConnected = true;
                    Log.e(TAG,"-----device-name-----"+mDeviceName+mDeviceAddress);
                    MyCommandManager.deviceDisconnState = true;
                   // ToastUtil.showShort(WatchHomeActivity.this, "H8_connect_success");
                    MyCommandManager.DEVICENAME = mDeviceName;
                    MyCommandManager.ADDRESS = mDeviceAddress;
                    Intent intent1 = new Intent();
                    intent1.setAction(WatchUtils.WATCH_CONNECTED_STATE_ACTION);
                    intent1.putExtra("connectstate","conn");
                    sendBroadcast(intent1);
                    SharedPreferencesUtils.setParam(WatchHomeActivity.this,"bozlunmac",mDeviceAddress);
                    break;
                case H8_DISCONNECT_CODE:   //蓝牙连接失败
                    mConnected = false;
                    MyCommandManager.deviceDisconnState = false;
                    MyCommandManager.DEVICENAME = null;
                    MyCommandManager.ADDRESS = null;
                    Intent intent2 = new Intent();
                    intent2.setAction(WatchUtils.WATCH_CONNECTED_STATE_ACTION);
                    intent2.putExtra("connectstate","disconn");
                    sendBroadcast(intent2);
                    invalidateOptionsMenu();
                    break;
            }
        }

    };

    private UploadStepsPressent uploadStepsPressent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_home);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initViews();
        initData();
        //注册相关信息
        regeditData();

    }

    private void regeditData() {
        //注册获取闹钟的广播
        registerReceiver(broadcastReceiver, WatchUtils.regeditAlarmBraod());
        //注册接收蓝牙连接状态的广播
        registerReceiver(broadcastReceiver,WatchUtils.h8ConnectState());
        //注册监听电话状态变化的监听
        MyApp.getCustomPhoneStateListener().setPhoneStateListenerInterface(this);
        uploadStepsToServer();  //循环上传7天的数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        Log.e(TAG, "------onresume--");
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //mBluetoothService = MyApp.getWatchBluetoothService();
        if (mBluetoothService != null) {
            Log.e(TAG,"------mBluetoothService != null--------");
            if (!WatchUtils.isEmpty(mDeviceAddress)) {
                if(WatchBluetoothService.bleConnect && MyCommandManager.DEVICENAME != null){
                    Log.e(TAG,"-----已结连接上了------");
                }else{
                    final boolean result = mBluetoothService.connect(mDeviceAddress);
                    Log.e(TAG, "-----11Connect request result=" + result);
                }

            } else {
                ToastUtil.showToast(WatchHomeActivity.this, "the bluetooth address is null");
            }
        } else {
            Log.e(TAG,"------mBluetoothService == null--------");
            mBluetoothService = MyApp.getWatchBluetoothService();
        }

    }

    //初始化相关数据
    private void initData() {
        mDeviceName = (String) SharedPreferencesUtils.readObject(WatchHomeActivity.this, "mylanya");
        mDeviceAddress = (String) SharedPreferencesUtils.readObject(WatchHomeActivity.this, "mylanmac");
        Log.e(TAG, "---home---蓝牙地址--" + mDeviceName + "------" + mDeviceAddress);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(!bluetoothAdapter.enable()){
            BlueAdapterUtils.getBlueAdapterUtils(WatchHomeActivity.this).turnOnBlue(WatchHomeActivity.this,10000,1000);
        }
        //检查更新
        updateManager = new UpdateManager(WatchHomeActivity.this, URLs.HTTPs + URLs.getvision);
        updateManager.checkForUpdate(false);

    }

    //初始化相关控件
    private void initViews() {
        //设置默认的步数
        String tagsteps = (String) SharedPreferencesUtils.getParam(WatchHomeActivity.this, "settagsteps", "");
        if (WatchUtils.isEmpty(tagsteps)) {
            SharedPreferencesUtils.setParam(WatchHomeActivity.this, "settagsteps", "10000");
        }
        watchfragments = new ArrayList<>();
        watchfragments.add(new WatchRecordFragment());  //记录
        watchfragments.add(new WatchH8DataFragment());
        watchfragments.add(new WatchRunFragment());
        watchfragments.add(new WatchMineFragment());    //个人中心
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), watchfragments);
        viewPager.setAdapter(fragmentPagerAdapter);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_data:     //数据
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_set:  //开跑
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_my:   //我的
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    /**
     * 广播接收器，接收消息
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            Log.e(TAG, "----action--" + action);
            if (WatchBluetoothService.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = H8_CONECT_SUCCESS_CODE;
                        handler.sendMessage(msg);
                    }
                });

                invalidateOptionsMenu();
            } else if (WatchBluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = H8_DISCONNECT_CODE;
                        handler.sendMessage(message);
                        mConnected = false;
                    }
                });

                // clearUI();
            }
            //发现有可支持的服务
            else if (WatchBluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //写数据的服务和characteristic
                if(mBluetoothService != null){
                    mnotyGattService = mBluetoothService.getSupportedGattServices(UUID.fromString(SampleGattAttributes.BZLUN_IKP_SERVER_UUID));
                    if(mnotyGattService != null){
                        characteristic = mnotyGattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BZLUN_IKP_WRITE_UUID));
                    }
                    //读写数据characteristic
                    readMnotyGattService = mBluetoothService.getSupportedGattServices(UUID.fromString(SampleGattAttributes.BZLUN_IKP_SERVER_UUID));
                    readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString(SampleGattAttributes.BZLUN_IKP_READ_UUID));
                    Log.e(TAG,"----mnotyGattService------"+mnotyGattService+"-characteristic-"+characteristic.toString()+"-readMnotyGattService-"+readMnotyGattService.toString()+"--readCharacteristic-"+readCharacteristic.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
            //显示数据
            else if (WatchBluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {

            }
        }
    };


    private void writedatatoble() {
        //设置通知
        mBluetoothService.setCharacteristicNotification(readCharacteristic, true);

        final int charaProp = characteristic.getProperties();
        //如果该char可写
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            Log.e(TAG,"-----mNotifyCharacteristic-=null------");
            if (mNotifyCharacteristic != null) {
                Log.e(TAG,"-----mNotifyCharacteristic!=null-------");
                mBluetoothService.setCharacteristicNotification(mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据，数据将在回调函数中
            //mBluetoothLeService.readCharacteristic(characteristic);
            //byte[] ds = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x11, (byte) 0x03,(byte)0x08, (byte) 0x01, (byte) 0x01, (byte) 0x00};
            //写入获取步数
            characteristic.setValue(WatchConstants.watchSteps);
            //Log.e(TAG,"------characteristic.setValue------"+characteristic.setValue(WatchConstants.watchSteps));
            mBluetoothService.writeCharacteristic(characteristic);

        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothService.setCharacteristicNotification(characteristic, true);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        Log.e("WatchHomeActivity", "--eventbus-home---" + result);
        if (result != null) {
            if ("syncwatchtime".equals(result)) { //收到同步时间成功发返回
                characteristic.setValue(WatchConstants.setDeviceType());
                mBluetoothService.writeCharacteristic(characteristic);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        characteristic.setValue(WatchConstants.getDeviceType());
                        mBluetoothService.writeCharacteristic(characteristic);
                    }
                },3000);

            } else if ("getwatchsteps".equals(result)) {   //获取返回的步数
                //获取手表步数后发生消息至记录fragment中显示
                Map<String,Integer> blestepsMap = (Map<String, Integer>) event.getObject();
                int dodaySteps = blestepsMap.get("today");  //今天的步数

                Log.e(TAG, "-------步数----" + dodaySteps+"----"+blestepsMap.get("yestoday")+"--"+blestepsMap.get("qiantian"));
                Intent intent = new Intent();
                intent.setAction(WatchUtils.WATCH_GETWATCH_STEPS_ACTION);
                intent.putExtra("homestep","homestep");
                intent.putExtra("homesteps",String.valueOf(dodaySteps));
                sendBroadcast(intent);
                SharedPreferencesUtils.setParam(WatchHomeActivity.this, "stepsnum", String.valueOf(dodaySteps));

                syncUserStepsData(dodaySteps,blestepsMap);   //同步数据到后台

                task = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                };
                timer.schedule(task, 60 * 10000, 60 * 10000);


            } else if ("refreshsteps".equals(result)) {
                if (mConnected == true) {
                    //写入获取步数
                    characteristic.setValue(WatchConstants.watchSteps);
                    mBluetoothService.writeCharacteristic(characteristic);
                }else{
                    if (mBluetoothService != null) {
                        Log.e(TAG,"------mBluetoothService != null--------");
                        if (!WatchUtils.isEmpty(mDeviceAddress)) {
                            if(WatchBluetoothService.bleConnect){
                                Log.e(TAG,"-----已结连接上了------");
                            }else{
                                final boolean results = mBluetoothService.connect(mDeviceAddress);
                                Log.e(TAG, "-----11Connect request result=" + results);
                            }

                        } else {
                            ToastUtil.showToast(WatchHomeActivity.this, "the bluetooth address is null");
                        }
                    } else {
                        Log.e(TAG,"------mBluetoothService == null--------");
                        mBluetoothService = MyApp.getWatchBluetoothService();
                    }
                }

            } else if ("startsynctime".equals(result)) {
                if(mBluetoothService != null && characteristic != null){
                    //同步手表的时间
                    characteristic.setValue(WatchConstants.syncwatchtime());
                    mBluetoothService.writeCharacteristic(characteristic);
                }
            }
            else if ("settingfirstalarm".equals(result)) {    //获取第一个闹钟的时间
                characteristic.setValue(WatchConstants.getWatchAlarmOne);
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("settingsecondalarm".equals(result)) {   //获取第二个闹钟的时间
                characteristic.setValue(WatchConstants.getWatchAlarmSecond);
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("settingthirdalarm".equals(result)) {    //获取第三个闹钟的时间
                characteristic.setValue(WatchConstants.getWatchAlarmThird);
                mBluetoothService.writeCharacteristic(characteristic);
            }
            else if ("getjiediantime".equals(result)) { //发送获取节电时间的指令
                characteristic.setValue(WatchConstants.getjiedianTime());
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("settingjiediantime".equals(result)) {  //设置节电时间发送指令
                String tim = (String) event.getObject() + "-";
                int startHour = Integer.parseInt(tim.substring(0, 2));
                int startMinue = Integer.parseInt(tim.substring(2, 4));
                int endHour = Integer.parseInt(tim.substring(4, 6));
                int endMMinue = Integer.parseInt(tim.substring(6, 8));
                Log.e(TAG, "------jiedian---" + startHour + "--" + startMinue + "--" + endHour + "--" + endMMinue + "--111--" + Integer.valueOf(tim.substring(0, 2)) + "----" + Integer.valueOf(tim.substring(2, 4)) + "----" + Integer.valueOf(tim.substring(6, 8)));
                characteristic.setValue(WatchConstants.settingJiedianTime(startHour, startMinue, endHour, endMMinue));
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("jiangetime".equals(result)) {  //提醒间隔
                String jianget = (String) event.getObject();
                Log.e(TAG, "----jianget---" + jianget);
                Log.e(TAG, "-----jianget---" + jianget + "----" + Integer.valueOf(jianget.substring(0, jianget.length() - 1)));
                characteristic.setValue(WatchConstants.settingJiangeTime(Integer.valueOf(jianget.substring(0, jianget.length() - 1))));
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("laidianphone".equals(result)) {    //来电提醒发送指令
                //来电提醒发送指令
                characteristic.setValue(WatchConstants.phoneAlert(0));
                mBluetoothService.writeCharacteristic(characteristic);
            } else if ("appalert".equals(result)) {    //消息提醒发送指令
                characteristic.setValue(WatchConstants.appalert());
                mBluetoothService.writeCharacteristic(characteristic);
            } else if("smsappalert".equals(result)){    //短信提醒
                characteristic.setValue(WatchConstants.smsAlert());
                mBluetoothService.writeCharacteristic(characteristic);
            }
            else if ("getWatchTime".equals(result)) {    //获取手表时间
                characteristic.setValue(WatchConstants.getWatchTime());
                mBluetoothService.writeCharacteristic(characteristic);
            }

            else if("setAlarm1".equals(result)){    //设置第一个闹钟的时间
                String resultData = (String) event.getObject(); //1-14|12:59  1-127|13:30
                Log.e(TAG,"---resultData----"+resultData);
                String repeat = StringUtils.substringBefore(resultData,"-"); //是否重复
                String tempSum = StringUtils.substringBefore(resultData,"|");    //1-14
                String sum = StringUtils.substringAfter(tempSum,"-");  //sum
                String times = StringUtils.substringAfter(resultData,"|");
                String hours = StringUtils.substringBefore(times,":");
                String mines = StringUtils.substringAfter(times,":");

                Log.e(TAG,"------设置闹钟参数-----"+resultData+"-"+repeat+"-"+sum+"-"+hours+"-"+mines);
                characteristic.setValue(WatchConstants.setWatchAlarm2(1,Integer.valueOf(hours),Integer.valueOf(mines),1,Integer.valueOf(sum),Integer.valueOf(repeat)));
                mBluetoothService.writeCharacteristic(characteristic);

            }else if("setAlarm2".equals(result)){   //设置第二个闹钟的时间
                String resultData = (String) event.getObject(); //1-14|12:59  1-127|13:30
                String repeat = StringUtils.substringBefore(resultData,"-"); //是否重复
                String tempSum = StringUtils.substringBefore(resultData,"|");    //1-14
                String sum = StringUtils.substringAfter(tempSum,"-");  //sum
                String times = StringUtils.substringAfter(resultData,"|");
                String hours = StringUtils.substringBefore(times,":");
                String mines = StringUtils.substringAfter(times,":");
                Log.e(TAG,"------设置闹钟参数-----"+resultData+"-"+repeat+"-"+sum+"-"+hours+"-"+mines);
                characteristic.setValue(WatchConstants.setWatchAlarm2(2,Integer.valueOf(hours),Integer.valueOf(mines),1,Integer.valueOf(sum),Integer.valueOf(repeat)));
                mBluetoothService.writeCharacteristic(characteristic);
            }else if("setAlarm3".equals(result)){   //设置第三个闹钟的时间
                String resultData = (String) event.getObject(); //1-14|12:59  1-127|13:30
                String repeat = StringUtils.substringBefore(resultData,"-"); //是否重复
                String tempSum = StringUtils.substringBefore(resultData,"|");    //1-14
                String sum = StringUtils.substringAfter(tempSum,"-");  //sum
                String times = StringUtils.substringAfter(resultData,"|");
                String hours = StringUtils.substringBefore(times,":");
                String mines = StringUtils.substringAfter(times,":");
                Log.e(TAG,"------设置闹钟参数-----"+resultData+"-"+repeat+"-"+sum+"-"+hours+"-"+mines);
                characteristic.setValue(WatchConstants.setWatchAlarm2(3,Integer.valueOf(hours),Integer.valueOf(mines),1,Integer.valueOf(sum),Integer.valueOf(repeat)));
                mBluetoothService.writeCharacteristic(characteristic);
            }
            else if(result.equals("disphone")){ //按4点位键挂断电话
                Log.e(TAG,"----4点位键挂断电话------");
                TelephonyManager tm = (TelephonyManager) WatchHomeActivity.this
                        .getSystemService(Service.TELEPHONY_SERVICE);
                PhoneUtils.endPhone(WatchHomeActivity.this,tm);
                PhoneUtils.endCall(MyApp.getContext());
                PhoneUtils.endcall();
            }else if(result.equals("disMissCall")){ //挂断电话后停止震动
                characteristic.setValue(WatchConstants.disPhoneAlert());
                mBluetoothService.writeCharacteristic(characteristic);
            }

        }

    }

    /**
     * 同步数据到后台
     *
     * @param steps
     */
    private void syncUserStepsData(int steps,Map<String,Integer> stpMap) {
        String height = (String) SharedPreferencesUtils.getParam(WatchHomeActivity.this,"userheight","");
        int userHeight = Integer.valueOf(height);   //身高
        String tagNum = (String) SharedPreferencesUtils.getParam(WatchHomeActivity.this, "settagsteps", "");
        int targetStep = Integer.valueOf(tagNum.trim());   //目标步数
        int targetStatus;   //是否达标
        //用户ID
        String userId = (String) SharedPreferencesUtils.readObject(WatchHomeActivity.this, "userId");
        //设备地址
        String deviceCode = (String) SharedPreferencesUtils.readObject(WatchHomeActivity.this, "mylanmac");


        //当天的数据
        int todaySteps = stpMap.get("today");   //当天的步数
        //当天的路程
        double todayDisc = WatchUtils.getDistants(Integer.valueOf(todaySteps), WatchUtils.getStepLong(userHeight));
        //当天的卡路里
        double todayKcal = WatchUtils.mul(todayDisc, WatchUtils.kcalcanstanc);
        int todayStatus = todaySteps - targetStep;
        if(todayStatus >=0){
            targetStatus = 0;
        }else{
            targetStatus = 1;
        }
        //上传当天的数据
        uploadTodaySteps(userId,deviceCode,targetStatus,todaySteps,todayDisc,todayKcal, WatchUtils.getCurrentDate()); //上传当天的数据

        //昨天的数据
        int yestodaySteps = stpMap.get("yestoday");   //昨天的步数
        //昨天的路程
        double yestodayDisc = WatchUtils.getDistants(Integer.valueOf(yestodaySteps), WatchUtils.getStepLong(userHeight));
        //昨天的卡路里
        double yestodayKcal = WatchUtils.mul(yestodayDisc, WatchUtils.kcalcanstanc);
        int yestodayStatus = yestodaySteps - targetStep;
        if(yestodayStatus >=0){
            targetStatus = 0;
        }else{
            targetStatus = 1;
        }
        //上传昨天的数据
        uploadYesTodayStepsToServer(userId,deviceCode,yestodaySteps,yestodayDisc,yestodayKcal,targetStatus);


        //前天的数据
        int thirddaySteps = stpMap.get("qiantian");   //昨天的步数
        //昨天的路程
        double thirddayDisc = WatchUtils.getDistants(Integer.valueOf(thirddaySteps), WatchUtils.getStepLong(userHeight));
        //昨天的卡路里
        double thirddayKcal = WatchUtils.mul(thirddayDisc, WatchUtils.kcalcanstanc);
        int thirddayStatus = thirddaySteps - targetStep;
        if(thirddayStatus >=0){
            targetStatus = 0;
        }else{
            targetStatus = 1;
        }
        //上传前天数据
        uploadThirdStepsToServer(userId,deviceCode,thirddaySteps,thirddayDisc,thirddayKcal,targetStatus);


        //前后天数据
        int fourthdaySteps = stpMap.get("fourthDay");   //昨天的步数
        //昨天的路程
        double fourthdayDisc = WatchUtils.getDistants(Integer.valueOf(fourthdaySteps), WatchUtils.getStepLong(userHeight));
        //昨天的卡路里
        double fourthdayKcal = WatchUtils.mul(fourthdayDisc, WatchUtils.kcalcanstanc);
        int fourthStatus = fourthdaySteps - targetStep;
        if(fourthStatus >=0){
            targetStatus = 0;
        }else{
            targetStatus = 1;
        }
        uploadForuthDaysToServier(userId,deviceCode,fourthdaySteps,fourthdayDisc,fourthdayKcal,targetStatus);
    }

    //上传前后天的数据
    private void uploadForuthDaysToServier(String userId, String deviceCode, int fourthdaySteps, double fourthdayDisc, double fourthdayKcal, int targetStatus) {
        String syncUrl = URLs.HTTPs + URLs.upSportData;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("stepNums", fourthdaySteps);
            map.put("userId", userId);
            map.put("deviceCode", deviceCode);
            map.put("targetStatus", targetStatus);
            map.put("distance", "" + fourthdayDisc);
            map.put("calories", "" + fourthdayKcal);
            map.put("currentDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 2)) + "");
            uploadStepsPressent.pressentUploadData(WatchHomeActivity.this, syncUrl, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //上传前天的数据
    private void uploadThirdStepsToServer(String userId, String deviceCode, int thirddaySteps, double thirddayDisc, double thirddayKcal,int targetStatus) {
        String syncUrl = URLs.HTTPs + URLs.upSportData;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("stepNums", thirddaySteps);
            map.put("userId", userId);
            map.put("deviceCode", deviceCode);
            map.put("targetStatus", targetStatus);
            map.put("distance", "" + thirddayDisc);
            map.put("calories", "" + thirddayKcal);
            map.put("currentDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 2)) + "");
            uploadStepsPressent.pressentUploadData(WatchHomeActivity.this, syncUrl, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //上传昨天的数据
    private void uploadYesTodayStepsToServer(String userId,String deviceCode,int yestodaySteps, double yestodayDisc, double yestodayKcal, int targetStatus) {
        String syncUrl = URLs.HTTPs + URLs.upSportData;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("stepNums", yestodaySteps);
            map.put("userId", userId);
            map.put("deviceCode", deviceCode);
            map.put("targetStatus", targetStatus);
            map.put("distance", "" + yestodayDisc);
            map.put("calories", "" + yestodayKcal);
            map.put("currentDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)) + "");
            uploadStepsPressent.pressentUploadData(WatchHomeActivity.this, syncUrl, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //上传当天的数据
    private void uploadTodaySteps(String userId, String deviceCode, int targetStatus, int todaySteps, double todayDisc, double todayKcal, String currentDate) {
        String syncUrl = URLs.HTTPs + URLs.upSportData;
        Map<String,Object> map = new HashMap<>();
        map.put("stepNums",todaySteps);
        map.put("userId",userId);
        map.put("deviceCode",deviceCode);
        map.put("targetStatus",targetStatus);
        map.put("distance",""+todayDisc);
        map.put("calories",""+todayKcal);
        map.put("currentDate",currentDate);
        uploadStepsPressent.pressentUploadData(WatchHomeActivity.this,syncUrl,map);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mGattUpdateReceiver);
        MyApp.getWatchBluetoothService().disconnect();
        unregisterReceiver(broadcastReceiver);
        uploadStepsPressent.detach();
    }

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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"------receiver---"+intent.getAction()+"----"+intent.getStringExtra("setalarmbroad"));
            String action = intent.getAction();
            if(action != null){
                if(action.equals("com.example.bozhilun.android.siswatch.alarm")){
                    if(intent.getStringExtra("setalarmbroad").equals("getalarmfirst")){
                        if(characteristic != null && mBluetoothService != null){
                            characteristic.setValue(WatchConstants.getWatchAlarmOne);
                            mBluetoothService.writeCharacteristic(characteristic);
                        }

                    }
                    else if(intent.getStringExtra("setalarmbroad").equals("getalarm2")){
                        if(mBluetoothService != null && characteristic != null){
                            characteristic.setValue(WatchConstants.getWatchAlarmSecond);
                            mBluetoothService.writeCharacteristic(characteristic);
                        }
                    }
                    else if(intent.getStringExtra("setalarmbroad").equals("getalarm3")){
                        if(mBluetoothService != null && characteristic != null){
                            characteristic.setValue(WatchConstants.getWatchAlarmThird);
                            mBluetoothService.writeCharacteristic(characteristic);
                        }
                    }
                }
                if(action.equals(WatchUtils.WATCH_OPENTAKE_PHOTO_ACTION)){
                    String openTag = intent.getStringExtra("phototag");
                    Log.e(TAG,"----openTag---"+openTag);
                    if(openTag.equals("on")){
                        if(mBluetoothService != null && characteristic!= null){
                            characteristic.setValue(WatchConstants.openTakeOphot(1));
                            mBluetoothService.writeCharacteristic(characteristic);
                        }
                    }else{
                        if(mBluetoothService != null && characteristic != null){
                            characteristic.setValue(WatchConstants.openTakeOphot(0));
                            mBluetoothService.writeCharacteristic(characteristic);
                        }
                    }
                }
            }

        }
    };

    //上传7天的数据
    private void uploadStepsToServer() {
        uploadStepsPressent = new UploadStepsPressent();
        uploadStepsPressent.attach(this);
    }

    //电话的状态变化
    @Override
    public void callPhoneData(int flag) {
        Log.e(TAG,"-----电话状态变化==="+flag);
        if(!WatchHomeActivity.this.isFinishing() && mBluetoothService != null && characteristic != null){
            switch (flag){
                case 0: //挂断
                    characteristic.setValue(WatchConstants.disPhoneAlert());
                    mBluetoothService.writeCharacteristic(characteristic);
                    break;
                case 1: //来电
                    characteristic.setValue(WatchConstants.phoneAlert(0));
                    mBluetoothService.writeCharacteristic(characteristic);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void uploadResultData(Object object) {
        Log.e(TAG,"------上传数据返回=="+object.toString());
    }
}
