package com.example.bozhilun.android.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.dd.CircularProgressButton;
import com.example.bozhilun.android.B18I.B18IHomeActivity;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.BlueServiceAdpter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.EventCenter;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.RssiBluetoothDevice;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.db.AlarmClockOperate;
import com.example.bozhilun.android.event.AlarmClock;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.WatchHomeActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.sdk.bluetooth.config.BluetoothConfig;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;



/**
 * Created by thinkpad on 2017/3/13.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SearchDeviceActivity extends BaseActivity {

    @BindView(R.id.search_recycler)
    RecyclerView searchRecycler;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private ArrayList<RssiBluetoothDevice> mLeDevices;
    private BlueServiceAdpter blueServiceAdpter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapterThread bluetoothAdapterThread;

    private static final int REQUEST_CODE_BLUETOOTH_ON = 505;
    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;
    private CircularProgressButton button;
    private ValueAnimator widthAnimation;

    List Myadvice;
    String Devicename;

    BluetoothDevice bluetoothDevice;
    private String b18iMac ;

    //所有的设备
    private String[] checkedDevices = new String[]{"B15P","B15S","bozlun"};
    private int sexNum = 0; //性别 0-女；1-男
    private String newHeight = "170";   //身高
    private String userWeight = "60"; //体重

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:  //H9 手表的连接绑定
                    closeLoadingDialog();
                    BluetoothDevice h9Blued = (BluetoothDevice) msg.obj;
                    //连接成功后移除连接监听
                    AppsBluetoothManager.getInstance(SearchDeviceActivity.this).clearBluetoothManagerDeviceConnectListeners();
                    BluetoothConfig.setDefaultMac(SearchDeviceActivity.this,h9Blued.getAddress());
                    startActivity(new Intent(SearchDeviceActivity.this,H9HomeActivity.class));
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya","W06X");
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanmac",h9Blued.getAddress());
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void initViews() {

        getUserInfoData(Common.customer_id);

        //MyApp.getInstance().getWatchBluetoothService().disconnect();
        mScanning=false;
        //取出值
        final Intent intent = getIntent();
        Devicename= intent.getStringExtra("NAME");
        EventBus.getDefault().register(this);

        mLeDevices = new ArrayList<>();
        Myadvice= new ArrayList<>();
        blueServiceAdpter = new BlueServiceAdpter(mLeDevices, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecycler.setLayoutManager(llm);
        searchRecycler.setAdapter(blueServiceAdpter);
        mHandler = new Handler();
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        if (!mBluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
        } else {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this,getResources().getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            } else {
                if (!BluetoothLeService.isService) {
                    if (!mScanning) {
                        swipeRefresh.setRefreshing(true);
                        scanLeDevice(true);

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(mLeDevices.size()==0) {

                                    swipeRefresh.setRefreshing(false);
                                    scanLeDevice(false);
                                    Toast.makeText(SearchDeviceActivity.this,getResources().getString(R.string.notdevice), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 10000);

                    }
                }
            }
            blueServiceAdpter.setOnItemClickListener(new BlueServiceAdpter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    bluetoothDevice = mLeDevices.get(position).getBluetoothDevice();
                    //保存蓝牙名字
                    //保存蓝牙名字
//                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya",bluetoothDevice.getName());
//                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanmac",bluetoothDevice.getAddress());
                    //Bozlun  H8手表
                    if(bluetoothDevice.getName().equals("bozlun")){
                        Log.e("SearchDeviceActivity","-----state---"+bluetoothDevice.getBondState());
                        SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya",bluetoothDevice.getName());
                        SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanmac",bluetoothDevice.getAddress());

                      new MaterialDialog.Builder(SearchDeviceActivity.this)
                              .title(getResources().getString(R.string.prompt))
                              .content(getResources().getString(R.string.setting_pair))
                              .positiveText(getResources().getString(R.string.confirm))
                              .negativeText(getResources().getString(R.string.cancle))
                              .onPositive(new MaterialDialog.SingleButtonCallback() {
                                  @Override
                                  public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                      Intent intent1 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                      startActivityForResult(intent1,11);
                                  }
                              }).show();

                        return;

                    }//Bozlun B18I手表
                    else if("B18I".equals(bluetoothDevice.getName().substring(0,4))){   //B18i手环
                        showLoadingDialog("Connection...");
                        b18iMac = bluetoothDevice.getAddress().trim();
                        BluetoothSDK.connectByMAC(resultCallBack,b18iMac);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                closeLoadingDialog();
                                if(BluetoothSDK.isConnected() == false){
                                    ToastUtil.showToast(SearchDeviceActivity.this,"disconnect please connect to again");
                                }

                            }
                        }, 10 * 1000);
                        return;
                    }
                    //H9 手表
                    else if("W06X".equals(bluetoothDevice.getName().substring(0,4))){
                        showLoadingDialog("Connection...");
                        AppsBluetoothManager.getInstance(MyApp.getContext()).connectDevice(bluetoothDevice.getAddress());
                        return;
                    }
                    //B15B手环
                    button = (CircularProgressButton) v;
                    simulateSuccessProgress(button);
                    EventBus.getDefault().post(new ServiceMessageEvent("Bingdingshouhuan"));
                    if("B15P".equals(MyCommandManager.DEVICENAME)){
                        DataSupport.deleteAll(AlarmClock.class);
                        AlarmClock one1 = new AlarmClock();
                        AlarmClock one2 = new AlarmClock();
                        AlarmClock one3 = new AlarmClock();

                        AlarmClockOperate.getInstance().saveAlarmClock(one1);
                        AlarmClockOperate.getInstance().saveAlarmClock(one2);
                        AlarmClockOperate.getInstance().saveAlarmClock(one3);


                    }else { //B15S手环
                        SharedPreferences userSettings = getSharedPreferences("alock_id", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putInt("id",0);
                        editor.commit();

                        DataSupport.deleteAll(AlarmClock.class);
                    }



                }
            });
            swipeRefresh.setOnRefreshListener(  new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    scanLeDevice(false);
                    if (bluetoothAdapterThread != null) {
                        mHandler.removeCallbacks(bluetoothAdapterThread);
                    }
                    mLeDevices.clear();
                    Myadvice.clear();
                    blueServiceAdpter.updateView(mLeDevices);
                    if (!BluetoothLeService.isService) {
                        scanLeDevice(true);
                    } else {
                        swipeRefresh.setRefreshing(false);
                    }

                    if(swipeRefresh.isRefreshing()){
                        swipeRefresh.setEnabled(false);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                swipeRefresh.setEnabled(true);

                            }
                        }, 2000);
                    }

                   mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mLeDevices.size()==0){

                                swipeRefresh.setRefreshing(false);
                                scanLeDevice(false);
                              /*  new AlertDialog.Builder(new ContextThemeWrapper(SearchDeviceActivity.this, R.style.AlertDialog))
                                        .setTitle("用户提醒").setMessage("您身边没有蓝牙设备 !")
                                        .setNegativeButton("确定", null).show();*/
                                Toast.makeText(SearchDeviceActivity.this,getResources().getString(R.string.notdevice), Toast.LENGTH_SHORT).show();
                               // finish();
                            }
                        }
                    }, 10000);

                }
            });
        }
    }

    //获取用户信息
    private void getUserInfoData(String customer_id) {
            String url = URLs.HTTPs + URLs.getUserInfo;
            JSONObject jsonob = new JSONObject();
            try {
                jsonob.put("userId",customer_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest<JSONObject> jso = new JsonObjectRequest(Request.Method.POST, url, jsonob, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response != null){
                        Log.e("SearchDeviceActivity","====="+response.toString());
                        try {
                            if(response.getInt("resultCode") == 001){
                                JSONObject userJson = response.getJSONObject("userInfo");
                                if(userJson != null){
                                    //保存用户信息
                                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"saveuserinfodata",userJson.toString());
                                    Log.e("SearchDeviceActivity","-------height--"+userJson.getString("height"));
                                    String height = userJson.getString("height");
                                    Log.e("SearchDeviceActivity","---heithg--"+height+"---------"+height.contains("cm")+"------"+height.endsWith("cm"));
                                    if(height.contains("cm")){
                                        String newHeight = height.substring(0,height.length()-2);
                                        Log.e("SearchDeviceActivity","----newHeight---"+newHeight);
                                        SharedPreferencesUtils.setParam(SearchDeviceActivity.this,"userheight",newHeight.trim());
                                    }else{
                                        SharedPreferencesUtils.setParam(SearchDeviceActivity.this,"userheight",height.trim());
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SearchDeviceActivity","---------"+error.getMessage());
                }
            });
            MyApp.getRequestQueue().add(jso);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        Log.e("SearchDeviceActivity","---result----"+result);
        if ("connect_success".equals(result)) {
            if (button != null) {
                widthAnimation.end();
                button.setProgress(100);
            }
            EventBus.getDefault().post("update_ui_name");
            //初始化mac name
            BluetoothGatt gatt = (BluetoothGatt) event.getObject();
            BluetoothDevice mBluetoothDevice = gatt.getDevice();
            MyCommandManager.CONNECTIONSTATE = 2;
            MyCommandManager.DEVICENAME = mBluetoothDevice.getName();
            MyCommandManager.ADDRESS = mBluetoothDevice.getAddress();
            SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya",mBluetoothDevice.getName());
            SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanmac",mBluetoothDevice.getAddress());
            MyCommandManager.initUUID(MyCommandManager.DEVICENAME);
           /* SharedPreferencesUtils.setParam(SearchDeviceActivity.this, SharedPreferencesUtils.DEVICENAME, MyCommandManager.DEVICENAME);
            SharedPreferencesUtils.setParam(SearchDeviceActivity.this, SharedPreferencesUtils.DEVICEADDRESS, MyCommandManager.ADDRESS);*/
            scanLeDevice(false);

        } else if ("connect_fail".equals(result)) {
            if (button != null) {
                widthAnimation.end();
                button.setProgress(0);
            }
        } else if ("update_ui".equals(result)) {
           RssiBluetoothDevice device = (RssiBluetoothDevice) event.getObject();
            if (!mLeDevices.contains(device)) {
                if (mLeDevices.size() < 10) {
                    mLeDevices.add(device);
                    Collections.sort(mLeDevices);
                    swipeRefresh.setRefreshing(false);
                    blueServiceAdpter.updateView(mLeDevices);
                }else{
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }
        }

//        else if("STATE_CONNECTED".equals(result)){
//            ToastUtil.showShort(SearchDeviceActivity.this,"连接成功");
//            Intent intent1 = new Intent(SearchDeviceActivity.this, WatchHomeActivity.class);
//            intent1.putExtra(WatchHomeActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
//            intent1.putExtra(WatchHomeActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
//            startActivity(intent1);
//            SearchDeviceActivity.this.finish();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                break;
            case "STATE_TURNING_ON":
//                Toast.makeText(this, "蓝牙打开", Toast.LENGTH_SHORT).show();
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                mLeDevices.clear();
                Myadvice.clear();
                blueServiceAdpter.notifyDataSetChanged();
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void simulateSuccessProgress(final CircularProgressButton button) {
        button.setIndeterminateProgressMode(true);
        widthAnimation = ValueAnimator.ofInt(20, 90);
        widthAnimation.setRepeatCount(ValueAnimator.INFINITE);
        widthAnimation.setDuration(SCAN_PERIOD);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //刷新ui
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EventBus.getDefault().post(new EventCenter(0));
                            // bleConnect. UNRegistrationService(SearchDeviceActivity.this);
                            startActivity(new Intent(SearchDeviceActivity.this, MainActivity.class));
                            finish();
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                        // EventBus.getDefault().post(new MessageEvent("update_data_service"));
                    }
                }, 2000);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    private void turnOnBluetooth() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                BLUETOOTH_DISCOVERABLE_DURATION);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                REQUEST_CODE_BLUETOOTH_ON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON) {
            scanLeDevice(true);
            switch (resultCode) {
                // 点击确认按钮
                case REQUEST_CODE_BLUETOOTH_ON:
                    if (!mScanning) {
                        scanLeDevice(true);
                    }
                    break;
                case Activity.RESULT_CANCELED:

                    break;
            }
        }
        if(requestCode == 11){
            blueServiceAdpter.notifyDataSetChanged();
            Log.e("SearchDeviceActivity","-----bluetoothDevice.getBondState()-------"+bluetoothDevice.getBondState());
                if (bluetoothDevice.getBondState() == 12) {
                    Intent intent1 = new Intent(SearchDeviceActivity.this, WatchHomeActivity.class);
                    intent1.putExtra(WatchHomeActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                    intent1.putExtra(WatchHomeActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya",bluetoothDevice.getName());
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanmac",bluetoothDevice.getAddress());
                    startActivity(intent1);
                    SearchDeviceActivity.this.finish();
                } else {
                    ToastUtil.showToast(this, "no pair");
                }

        }
    }

    @Override
    protected void onDestroy() {
        scanLeDevice(false);
        if (bluetoothAdapterThread != null) {
            mHandler.removeCallbacks(bluetoothAdapterThread);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            swipeRefresh.setRefreshing(true);
            bluetoothAdapterThread = new BluetoothAdapterThread(mLeScanCallback, mScanning);
            mHandler.postDelayed(bluetoothAdapterThread, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            swipeRefresh.setRefreshing(false);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.e("Search","-------"+device.getName()+"---"+device.getAddress());
                    if(!WatchUtils.isEmpty(device.getName())){  // || "HR".equals(device.getName().substring(0,2))
                        if(Arrays.asList(checkedDevices).contains(device.getName()) || "B18I".equals(device.getName().substring(0,4)) || "W06X".equals(device.getName().substring(0,4))){
                            //if (Devicename.equals(device.getName())) {//忽略设备
                            if (Myadvice.size() == 0) {
                                Myadvice.add(device.getAddress());
                                final RssiBluetoothDevice rssiBluetoothDevice = new RssiBluetoothDevice(device, Math.abs(rssi));
                                EventBus.getDefault().post(new MessageEvent("update_ui", rssiBluetoothDevice));
                            } else {
                                if (!Myadvice.toString().contains(device.getAddress())) {
                                    if(Myadvice.size()<=50){
                                        Myadvice.add(device.getAddress());
                                        final RssiBluetoothDevice rssiBluetoothDevice = new RssiBluetoothDevice(device, Math.abs(rssi));
                                        EventBus.getDefault().post(new MessageEvent("update_ui", rssiBluetoothDevice));
                                    }

                                }
                            }
                        }
                    }
                }
            };


    private class BluetoothAdapterThread implements Runnable {
        private BluetoothAdapter.LeScanCallback LeScanCallback;
        private boolean scanning;

        public BluetoothAdapterThread(BluetoothAdapter.LeScanCallback LeScanCallback, boolean scanning) {
            this.LeScanCallback = LeScanCallback;
            this.scanning = scanning;
        }

        @Override
        public void run() {
            scanning = false;
            mBluetoothAdapter.stopLeScan(LeScanCallback);
        }}

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search_device;
    }

    @Override
    protected void getToolbarClick() {
        super.getToolbarClick();
        // 全局推出
        removeAllActivity();
    }

    public long exitTime; // 储存点击退出时间

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.showToast(SearchDeviceActivity.this,"再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    // 全局推出
                    removeAllActivity();
                    return true;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //B18i手环连接回调
    private ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i){
                case ResultCallBack.TYPE_CONNECT:   //连接成功
                    closeLoadingDialog();
                    ToastUtil.showToast(SearchDeviceActivity.this,"connect successfull");
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanya","B18I");
                    SharedPreferencesUtils.saveObject(SearchDeviceActivity.this,"mylanyamac",b18iMac);
                    Intent intents = new Intent(SearchDeviceActivity.this,B18IHomeActivity.class);
                    intents.putExtra("b18imac",b18iMac);
                    startActivity(intents);
                    SearchDeviceActivity.this.finish();

                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //设置H9手表的连接监听回调
        AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
    }

    /**
     * H9 手表的连接监听回调
     */
    private BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {

        }

        @Override
        public void onConnectFailed() {
            closeLoadingDialog();
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            Message message = new Message();
            message.what  = 1001;
            message.obj = bluetoothDevice;
            handler.sendMessage(message);
        }

        @Override
        public void onConnectDeviceTimeOut() {  //连接超时
            closeLoadingDialog();
        }
    };
}
