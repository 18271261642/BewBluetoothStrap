package com.example.bozhilun.android.siswatch;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * Created by Administrator on 2017/7/27.
 */

/**
 * sisi 解除设备
 */
public class WatchStrapActivity extends WatchBaseActivity {
    private static final String TAG = "WatchStrapActivity";
    private static final int REQUEST_UNPAIR_CODE = 1001;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.watch_jiechu_relayout)
    RelativeLayout watchJiechuRelayout;

    private BluetoothAdapter bluetoothAdapter;

    String bluName,bleMac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_stap);
        ButterKnife.bind(this);
        registerReceiver(broadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
        initViews();
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();
        bluName = (String) SharedPreferencesUtils.readObject(WatchStrapActivity.this,"mylanya");
        bleMac = (String) SharedPreferencesUtils.readObject(WatchStrapActivity.this,"mylanmac");
    }

    private void initViews() {
        tvTitle.setText(R.string.my_strap);
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置H9手表的连接监听回调
        AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
    }

    @OnClick(R.id.watch_jiechu_relayout)
    public void onViewClicked() {
        if(bluName.equals("bozlun")){ //H8 手表
            new MaterialDialog.Builder(this)
                    .title(getResources().getString(R.string.prompt))
                    .content(getResources().getString(R.string.confirm_unbind_strap))
                    .positiveText(getResources().getString(R.string.confirm))
                    .negativeText(getResources().getString(R.string.cancle))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            if(WatchConstants.customBlueDevice != null){
                                Log.e(TAG,"---bozlun断开连接---");
                                BluetoothDevice bluetoothDevice = WatchConstants.customBlueDevice.getBluetoothDevice();
                                HidUtil.getInstance(WatchStrapActivity.this).disConnect(bluetoothDevice);
                            }else{
                                //根据地址获取连接的设备
                                BluetoothDevice bd = HidUtil.getInstance(WatchStrapActivity.this).getConnectedDevice(bleMac);
                                if(bd != null){
                                    HidUtil.getInstance(WatchStrapActivity.this).disConnect(bd);
                                }
                            }

                        }
                    }).show();



//            new MaterialDialog.Builder(this)
//                    .title(getResources().getString(R.string.prompt))
//                    .content(getResources().getString(R.string.cancle_pair))
//                    .positiveText(getResources().getString(R.string.confirm))
//                    .negativeText(getResources().getString(R.string.cancle))
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            Intent intent1 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
//                            startActivityForResult(intent1,REQUEST_UNPAIR_CODE);
//                        }
//                    }).show();
        }else if(bluName.equals("B18I")){//confirm_unbind_strap
            new MaterialDialog.Builder(this)
                    .title(getResources().getString(R.string.prompt))
                    .content(getResources().getString(R.string.confirm_unbind_strap))
                    .positiveText(getResources().getString(R.string.confirm))
                    .negativeText(getResources().getString(R.string.cancle))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            BluetoothSDK.disConnect(resultCallBack);
                        }
                    }).show();
        }else { //H9 手表
            new MaterialDialog.Builder(this)
                    .title(getResources().getString(R.string.prompt))
                    .content(getResources().getString(R.string.confirm_unbind_strap))
                    .positiveText(getResources().getString(R.string.confirm))
                    .negativeText(getResources().getString(R.string.cancle))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            AppsBluetoothManager.getInstance(MyApp.getContext()).doUnbindDevice(BluetoothConfig.getDefaultMac(MyApp.getContext()));
                            BluetoothConfig.setDefaultMac(MyApp.getContext(),"");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya","");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanyamac","");
                            showLoadingDialog("断开中..");
//                            startActivity(SearchDeviceActivity.class);
//                            startActivity(NewSearchActivity.class);
//                            finish();
                        }
                    }).show();
        }

    }

    //B18I手环回调
    private ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i){
                case ResultCallBack.TYPE_DISCONNECT:    //断开成功
                    startActivity(NewSearchActivity.class);
                    SharedPreferencesUtils.saveObject(MyApp.getApplication(), "mylanya", "");//清空标识
                    finish();
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("解绑","---------"+requestCode+"----resultCode---"+resultCode+"--resultok="+RESULT_OK);
        if(requestCode == REQUEST_UNPAIR_CODE){
            if(resultCode == 0){
                List<String> unpariList = new ArrayList<>();
                Set<BluetoothDevice> pairDevice = bluetoothAdapter.getBondedDevices();
                if(pairDevice.size() > 0){
                    Log.e(TAG,"----onActivityResult----list>0---");
                    for(Iterator iterator = pairDevice.iterator();iterator.hasNext();){
                        BluetoothDevice bd = (BluetoothDevice) iterator.next();
                        if(bd != null){
                            unpariList.add(bd.getAddress().trim());
                        }
                    }
                    if(unpariList.contains(SharedPreferencesUtils.readObject(MyApp.getContext(),"mylanmac"))){
                        //未取消配对
                    }else{
                        //已取消配对
                        MyApp.getWatchBluetoothService().disconnect();//断开蓝牙
                       // MyApp.getInstance().getWatchBluetoothService().disconnect();  //断开蓝牙
//                        Intent ints = new Intent(WatchStrapActivity.this, WatchBluetoothService.class);
//                        MyApp.getWatchBluetoothService().onUnbind(ints);
                        MyCommandManager.deviceDisconnState = true;
                        MyCommandManager.ADDRESS = null;
                        MyCommandManager.DEVICENAME = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya",null);
                        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanmac",null);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
//                        startActivity(SearchDeviceActivity.class);
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                }else{
                    Log.e(TAG,"----onActivityResult----list<=0---");
                    //已取消配对
                    MyApp.getWatchBluetoothService().disconnect();//断开蓝牙
                    //MyApp.getInstance().getWatchBluetoothService().disconnect();  //断开蓝牙
//                    Intent ints = new Intent(WatchStrapActivity.this, WatchBluetoothService.class);
//                    MyApp.getWatchBluetoothService().onUnbind(ints);
                    MyCommandManager.deviceDisconnState = true;
                    MyCommandManager.ADDRESS = null;
                    MyCommandManager.DEVICENAME = null;
                    SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya",null);
                    SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanmac",null);
                    SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
//                    startActivity(SearchDeviceActivity.class);
                    startActivity(NewSearchActivity.class);
                    finish();
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * H8断开连接接收广播，只断开连接，不需要解除配对
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"-------action---"+action);
            if(!WatchUtils.isEmpty(action)){
                if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
                    int connState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,BluetoothAdapter.STATE_DISCONNECTED);
                    Log.e(TAG,"---connState--"+connState);
                    if(connState == 0){ //断开连接成功
                        //已取消配对
                        MyApp.getWatchBluetoothService().disconnect();//断开蓝牙
                        MyCommandManager.deviceDisconnState = true;
                        MyCommandManager.ADDRESS = null;
                        MyCommandManager.DEVICENAME = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya",null);
                        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanmac",null);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                }
            }
        }
    };

    /**
     * H9 手表的连接监听回调
     */
    private BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            Log.e(TAG,"------h9--onConnected--");
        }

        @Override
        public void onConnectFailed() {
            Log.e(TAG,"------h9--onConnectFailed--");
            closeLoadingDialog();
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            Log.e(TAG,"------h9-onEnableToSendComand---");
        }

        @Override
        public void onConnectDeviceTimeOut() {  //连接超时
            Log.e(TAG,"------h9--onConnectDeviceTimeOut--");
        }
    };
}
