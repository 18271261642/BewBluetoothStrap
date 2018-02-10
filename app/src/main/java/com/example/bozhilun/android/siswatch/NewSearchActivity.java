package com.example.bozhilun.android.siswatch;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.GuideActivity;
import com.example.bozhilun.android.activity.LoginActivity;
import com.example.bozhilun.android.b15p.B15pHomeActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.adapter.CustomBlueAdapter;
import com.example.bozhilun.android.siswatch.bean.CustomBlueDevice;
import com.example.bozhilun.android.siswatch.utils.BlueAdapterUtils;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 新的搜索页面
 */
public class NewSearchActivity extends GetUserInfoActivity implements CustomBlueAdapter.OnSearchOnBindClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NewSearchActivity";
    private static final int BLUE_VISIABLE_TIME_CODE = 10 * 1000;
    private static final int REQUEST_TURNON_BLUE_CODE = 1001;   //打开蓝牙请求码
    private static final int STOP_SCANNER_DEVICE_CODE = 1002;   //停止扫描
    private static final int GET_OPENBLUE_SUCCESS_CODE = 120;   //请求打开蓝牙 ，用户确认打开后返回
    public static final int H9_REQUEST_CONNECT_CODE = 1112;
    public static final int H8_CONNECT_SUCCESS_CODE = 1113; //H8手表连接成功
    public static final int H8_CONNECT_FAILED_CDOEE = 1114; //H8手表连接失败
    private static final int H8_BLE_BANDSTATE_CODE = 12;    //H8手表蓝牙配对状态
    private static final int H8_PAIR_REQUEST_CODE = 1115;   //H8手表配对返回
    private static final String B15P_BLENAME = "B15P";  //B15P
    private static final String B18I_BLENAME = "B18I";  //B18I
    private static final String H9_BLENAME = "W06"; //H9手表名字标识  保存时后面+X
    private static final String B15PNAME = "B15P";  //B15P

    //RecycleView
    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    //Swiper
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    //跑马灯textView
    @BindView(R.id.search_alertTv)
    TextView searchAlertTv;
    @BindView(R.id.newSearchTitleLeft)
    FrameLayout newSearchTitleLeft;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;


    private List<CustomBlueDevice> customDeviceList;  //数据源集合
    private CustomBlueAdapter customBlueAdapter;    //适配器
    private BluetoothAdapter bluetoothAdapter;  //蓝牙适配器
    private List<String> repeatList;

    CustomBlueDevice customBlueDevice = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_SCANNER_DEVICE_CODE:  //停止扫描
                    swipeRefresh.setRefreshing(false);
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    break;
                case H9_REQUEST_CONNECT_CODE:  //H 9手表连接
                    closeLoadingDialog();
                    BluetoothDevice h9Blued = (BluetoothDevice) msg.obj;
                    //连接成功后移除连接监听
                    AppsBluetoothManager.getInstance(NewSearchActivity.this).clearBluetoothManagerDeviceConnectListeners();
                    SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "W06X");
                    SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", h9Blued.getAddress());
                    startActivity(new Intent(NewSearchActivity.this, H9HomeActivity.class));
                    finish();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(this);
        //注册扫描蓝牙设备的广播
        registerReceiver(broadcastReceiver, BlueAdapterUtils.getBlueAdapterUtils(NewSearchActivity.this).scanIntFilter()); //注册广播
        initViews();
        initData();
//
//        MyApp.setH9ConnListener(new MyApp.OnH9ConnListener() {
//            @Override
//            public void h9connect(BluetoothDevice bluetoothDevice) {
//                Log.e(TAG, "------h9--11-");
//            }
//
//            @Override
//            public void h9connectFailed() {
//                Log.e(TAG, "------h9--o222-");
//            }
//
//            @Override
//            public void h9onEnableToSendComand(BluetoothDevice bluetoothDevice) {
//                Log.e(TAG, "------h9--333--");
//                Message message = new Message();
//                message.what = H9_REQUEST_CONNECT_CODE;
//                message.obj = bluetoothDevice;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void h9contimeout() {
//                Log.e(TAG, "------h9--444--");
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        String mylanya = (String) SharedPreferencesUtils.readObject(NewSearchActivity.this, "mylanya");
        String mylanmac = (String) SharedPreferencesUtils.readObject(NewSearchActivity.this, "mylanmac");
        String defaultMac = BluetoothConfig.getDefaultMac(NewSearchActivity.this);
        if (!TextUtils.isEmpty(mylanya) || !TextUtils.isEmpty(mylanmac) || !TextUtils.isEmpty(defaultMac)) {
            AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(mylanmac);
            AppsBluetoothManager.getInstance(NewSearchActivity.this).clearBluetoothManagerDeviceConnectListeners();
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "");
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", "");
            BluetoothConfig.setDefaultMac(NewSearchActivity.this, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HidUtil.getInstance(MyApp.getContext());
        //设置H9手表的连接监听回调
        AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
    }

    private void initData() {

        Log.d("-ccc-SDK中的--mac---", BluetoothConfig.getDefaultMac(MyApp.getContext()));
        String blmac = (String) SharedPreferencesUtils.readObject(NewSearchActivity.this, "mylanmac");
        if (!WatchUtils.isEmpty(blmac) && blmac.equals("W06X")) {
            AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(blmac);
        }

        repeatList = new ArrayList<>();
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {  //未打开蓝牙
                BlueAdapterUtils.getBlueAdapterUtils(NewSearchActivity.this).turnOnBlue(NewSearchActivity.this,
                        BLUE_VISIABLE_TIME_CODE, REQUEST_TURNON_BLUE_CODE);
            } else {
                scanBlueDevice(true);   //扫描设备
            }
        } else {
            //不支持蓝牙
            ToastUtil.showToast(NewSearchActivity.this, getResources().getString(R.string.bluetooth_not_supported));
            return;
        }
    }

    //扫描和停止扫描设备
    private void scanBlueDevice(boolean b) {
        if (b) {
            //扫描设备，默认扫描时间为10秒
            swipeRefresh.setRefreshing(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
            getPhonePairDevice();   //获取手机配对的设备
            bluetoothAdapter.startLeScan(leScanCallback);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = STOP_SCANNER_DEVICE_CODE;
                    handler.sendMessage(message);
                }
            }, BLUE_VISIABLE_TIME_CODE);
        } else {
            swipeRefresh.setRefreshing(false);
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    //获取配对的蓝牙设备
    private void getPhonePairDevice() {
        //获取已配对设备
        Object[] lstDevice = bluetoothAdapter.getBondedDevices().toArray();
        for (int i = 0; i < lstDevice.length; i++) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) lstDevice[i];
            if (bluetoothDevice != null) {
                if (bluetoothDevice.getName() != null && bluetoothDevice.getName().length() > 6 && bluetoothDevice.getName().substring(0, 2).equals("H8")) {
                    repeatList.add(bluetoothDevice.getAddress());
                    String pairDevice = "已配对|" + bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress();
                    Log.d(TAG, "----已配对设备--" + pairDevice + "----" + bluetoothDevice.getUuids());
                    customDeviceList.add(new CustomBlueDevice(bluetoothDevice, "", 0));
                    customBlueAdapter.notifyDataSetChanged();
                }
            }

        }

    }

    /**
     * 蓝牙扫描回调
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            // Log.e(TAG, "------扫描返回----" + bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress() + "--" + Arrays.toString(scanRecord));
            String bleName = bluetoothDevice.getName();
            String bleMac = bluetoothDevice.getAddress(); //bozlun
            if (!WatchUtils.isEmpty(bleName) && bleName.length() >= 4) {//bleName.equals(B15P_BLENAME) || || bleName.substring(0,4).equals(B18I_BLENAME)
                if ((scanRecord[7] == 80 && scanRecord[8] == 80)
                        || bleName.substring(0, 3).equals(H9_BLENAME) || bleName.substring(0,2).equals("H9") ) {
                    if (!repeatList.contains(bleMac)) {
                        if (customDeviceList.size() <= 30) {
                            repeatList.add(bleMac);
                            customDeviceList.add(new CustomBlueDevice(bluetoothDevice, Math.abs(rssi) + "", ((scanRecord[7] + scanRecord[8]))));
                            Comparator comparator = new Comparator<CustomBlueDevice>() {
                                @Override
                                public int compare(CustomBlueDevice o1, CustomBlueDevice o2) {
                                    return o1.getRssi().compareTo(o2.getRssi());
                                }
                            };
                            Collections.sort(customDeviceList, comparator);
                            customBlueAdapter.notifyDataSetChanged();
                        } else {
                            scanBlueDevice(false);
                        }
                    }

                }
            }
        }
    };

    private void initViews() {
        HidUtil.instance = null;
        //跑马灯效果
        searchAlertTv.setSelected(true);
        newSearchTitleTv.setText(getResources().getString(R.string.search_device));
        //设置RecyclerView相关
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecycler.setLayoutManager(linearLayoutManager);
        searchRecycler.addItemDecoration(new DividerItemDecoration(NewSearchActivity.this, DividerItemDecoration.VERTICAL));
        customDeviceList = new ArrayList<>();
        customBlueAdapter = new CustomBlueAdapter(customDeviceList, NewSearchActivity.this);
        searchRecycler.setAdapter(customBlueAdapter);
        customBlueAdapter.setOnBindClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "---requestCode---" + requestCode + "---resultCode---" + resultCode);
        if (requestCode == REQUEST_TURNON_BLUE_CODE) {    //打开蓝牙返回
            if (resultCode == GET_OPENBLUE_SUCCESS_CODE) {  //打开蓝牙返回
                scanBlueDevice(true);
            } else {  //点击取消 0    //取消后强制打开
                bluetoothAdapter.enable();
                scanBlueDevice(true);
            }
        }
        //H8手表配对返回
        if (requestCode == H8_PAIR_REQUEST_CODE) {
            //已经配对和连接了就直接连接
            boolean h8Connect = MyApp.getWatchBluetoothService().connect(customBlueDevice.getBluetoothDevice().getAddress());
            Log.e(TAG, "-----H8手表连接返回-----" + h8Connect);
            if (h8Connect) {
                closeLoadingDialog();
                WatchConstants.customBlueDevice = customBlueDevice;
                WatchConstants.H8ConnectState = true;
                SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "bozlun");
                SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", customBlueDevice.getBluetoothDevice().getAddress());
                startActivity(WatchHomeActivity.class);
                finish();
            } else {
                closeLoadingDialog();
                WatchConstants.H8ConnectState = false;
                ToastUtil.showToast(NewSearchActivity.this, "----连接失败-");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    //绑定按钮的点击事件
    @Override
    public void doBindOperator(int position) {
        if (!bluetoothAdapter.isDiscovering()) {
            scanBlueDevice(false);
        }
        customBlueDevice = customDeviceList.get(position);
        String bleName = customBlueDevice.getBluetoothDevice().getName();
        Log.e(TAG, "-----1111----" + bleName);
        if (customBlueDevice != null) {
            if (customBlueDevice.getCompanyId() == 160 ||
                    customBlueDevice.getBluetoothDevice().getName().substring(0, 2).equals("H8")) { //H8手表
                Log.e(TAG, "-----222----" + bleName);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.prompt)
                        .setMessage("是否配对?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //连接H8手表
                                connectH8Watch(customBlueDevice);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                    }
                }).create().show();
            } else if (bleName.substring(0, 3).equals(H9_BLENAME) || bleName.substring(0,2).equals("H9")) {    //H9手表
                showLoadingDialog("connection...");
                //AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
                AppsBluetoothManager.getInstance(MyApp.getContext()).connectDevice(customBlueDevice.getBluetoothDevice().getAddress());
            } else if (bleName.equals(B15P_BLENAME)) { //B15P
                //连接B15P手环
               // connectB15pDevice(customBlueDevice);
            } else if (bleName.substring(0, 3).equals(B18I_BLENAME)) {  //B18I

            }
        }

    }

    //连接H8手表
    private void connectH8Watch(CustomBlueDevice customBlueDevice) {
        if (customBlueDevice != null && customBlueDevice.getBluetoothDevice() != null) {
            //先判断是否绑定,绑定即配对
            if (customBlueDevice.getBluetoothDevice().getBondState() == H8_BLE_BANDSTATE_CODE) {
                //判断是否配对连接
                if (HidUtil.getInstance(MyApp.getContext()).isConnected(customBlueDevice.getBluetoothDevice())) {
                    //已经配对和连接了就直接连接
                    boolean h8Connect = MyApp.getWatchBluetoothService().connect(customBlueDevice.getBluetoothDevice().getAddress());
                    Log.e(TAG, "-----H8手表连接返回-----" + h8Connect);
                    if (h8Connect) {
                        //H8同步时间标识
                        SharedPreferencesUtils.setParam(NewSearchActivity.this,"h8Sycndate","");
                        closeLoadingDialog();
                        WatchConstants.customBlueDevice = customBlueDevice;
                        WatchConstants.H8ConnectState = true;
                        SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "bozlun");
                        SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", customBlueDevice.getBluetoothDevice().getAddress());
                        startActivity(WatchHomeActivity.class);
                        finish();
                    } else {
                        closeLoadingDialog();
                        WatchConstants.H8ConnectState = false;
                        ToastUtil.showToast(NewSearchActivity.this, "连接失败 请重新连接");
                    }
                } else {  //已配对，但未连接
                    //showH8PairAlert();
                    showLoadingDialog("连接中...");
                    HidUtil.getInstance(MyApp.getContext()).connect(customBlueDevice.getBluetoothDevice());
                }
            } else {  //没有配对
                //showH8PairAlert();
                showLoadingDialog("配对中...");
                HidUtil.getInstance(MyApp.getContext()).pair(customBlueDevice.bluetoothDevice);
            }
        }
    }


    //H8手表配对提示
    private void showH8PairAlert() {
        new MaterialDialog.Builder(NewSearchActivity.this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.setting_pair))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent1 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivityForResult(intent1, H8_PAIR_REQUEST_CODE);
                    }
                }).show();
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        customDeviceList.clear();
        repeatList.clear();
        customBlueAdapter.notifyDataSetChanged();
        scanBlueDevice(true);
    }

    //广播接收者接收H8手表配对连接的状态
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "--------action-------" + action);
            if (!WatchUtils.isEmpty(action)) {
                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {   //绑定状态的广播，配对
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e(TAG, "------配对状态返回----" + bondState);
                    switch (bondState) {
                        case BluetoothDevice.BOND_BONDED:   //已绑定 12
                            Log.e(TAG, "-----111-----");
                            if(customBlueDevice != null){
                                if (bd != null && bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
                                    Log.e(TAG, "-----22-----");
                                    showLoadingDialog("connect...");
                                    HidUtil.getInstance(MyApp.getContext()).connect(customBlueDevice.getBluetoothDevice());
                                }
                            }
                            break;
                        case BluetoothDevice.BOND_BONDING:  //绑定中   11
                            if(customBlueDevice != null){
                                if (bd != null && bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
                                    Log.e(TAG, "-----22-----");
                                    showLoadingDialog("配对中...");
                                }
                            }
                            break;
                        case BluetoothDevice.BOND_NONE: //绑定失败  10
                            if (customBlueDevice != null && customBlueDevice.getBluetoothDevice().getName() != null) {
                                if (bd != null && bd.getName().equals(customBlueDevice.getBluetoothDevice().getName())) {
                                    closeLoadingDialog();
                                    ToastUtil.showToast(NewSearchActivity.this, "绑定失败,请重新绑定");
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) { //连接状态的改变
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                    int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.STATE_DISCONNECTED);
                    BluetoothDevice conBle = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //0-1;0-2 3;0
                    Log.e(TAG, "-----连接状态----" + state + "--" + connState + "----conBle--" + conBle.getName());
                    if (connState == 2 && customBlueDevice != null && conBle.getName().equals(customBlueDevice.getBluetoothDevice().getName())) { //连接成功
                        closeLoadingDialog();
                        if (customBlueDevice != null) {
                            SharedPreferencesUtils.setParam(NewSearchActivity.this,"h8Sycndate","");
                            ToastUtil.showToast(NewSearchActivity.this, "conn success");
                            WatchConstants.customBlueDevice = customBlueDevice;
                            WatchConstants.H8ConnectState = true;
                            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "bozlun");
                            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", customBlueDevice.getBluetoothDevice().getAddress());
                            startActivity(WatchHomeActivity.class);
                            finish();
                        }

                    } else if (connState == 0) {   //断开连接成功

                    }
                }
            }
        }
    };

    /**
     * 连接B15P手环
     *
     * @param customBlueDevice
     */
    private void connectB15pDevice(final CustomBlueDevice customBlueDevice) {
        final String blename = customBlueDevice.getBluetoothDevice().getName();
        final String blemac = customBlueDevice.getBluetoothDevice().getAddress();
        showLoadingDialog("connect...");
        MyApp.getVpOperateManager().connectDevice(blemac, new IConnectResponse() {
            @Override
            public void connectState(int state, BleGattProfile bleGattProfile, boolean b) {

            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int code) {
                closeLoadingDialog();
                ToastUtil.showToast(NewSearchActivity.this, "connect success");
                if (code == Code.REQUEST_SUCCESS) {
                    SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", blename);
                    SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", blemac);
                    MyCommandManager.DEVICENAME = "B15P";
                    startActivity(B15pHomeActivity.class);
                    finish();
                }
            }
        });
    }


    /**
     * H9 手表的连接监听回调
     */
    public BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            Log.e(TAG, "------h9--onConnected--");
        }

        @Override
        public void onConnectFailed() {
            Log.e(TAG, "------h9--onConnected--");
            closeLoadingDialog();
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            Log.e(TAG, "------h9--onEnableToSendComand--");
            Message message = new Message();
            message.what = H9_REQUEST_CONNECT_CODE;
            message.obj = bluetoothDevice;
            handler.sendMessage(message);
        }

        @Override
        public void onConnectDeviceTimeOut() {  //连接超时
            Log.e(TAG, "------h9--onConnected--");
            BluetoothConfig.setDefaultMac(NewSearchActivity.this, "");
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanya", "");
            SharedPreferencesUtils.saveObject(NewSearchActivity.this, "mylanmac", "");
            closeLoadingDialog();

        }
    };

    //返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            startActivity(LoginActivity.class);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:   //返回
                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.newSearchRightImg1: //帮助
                startActivity(SearchExplainActivity.class);
                break;
        }
    }

}
