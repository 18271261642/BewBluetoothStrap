package com.example.bozhilun.android.siswatch;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.CameraActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.bleus.WatchBluetoothService;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchDeviceActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "WatchDeviceActivity";
    private static final int REQUEST_REQDPHONE_STATE_CODE = 1001;
    private static final int REQUEST_OPENCAMERA_CODE = 1002;
    private static final int NOTI_OPEN_BACK_CODE = 1003;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.watch_mynaozhongRel)
    RelativeLayout watchMynaozhongRel;
    @BindView(R.id.watch_mymsgRel)
    RelativeLayout watchMymsgRel;

    String mDevicename, mDeviceAddress;
    @BindView(R.id.watch_mycaozuoRel)
    RelativeLayout watchMycaozuoRel;
    @BindView(R.id.watch_takePhotoRel)
    RelativeLayout watchTakePhotoRel;
    @BindView(R.id.watchDeviceTagShowTv)
    TextView watchDeviceTagShowTv;

    //节电模式开关
    @BindView(R.id.watch_message_jiedianSwitch)
    SwitchCompat watchMessageJiedianSwitch;
    //来电提醒开关
    @BindView(R.id.watch_message_callphoneSwitch)
    SwitchCompat watchMessageCallphoneSwitch;
    //节电模式开始时间
    @BindView(R.id.watch_jiedian_starttimeTv)
    TextView watchJiedianStarttimeTv;
    //节电模式结束时间
    @BindView(R.id.watch_jiedian_endtimeTv)
    TextView watchJiedianEndtimeTv;

    private String starHour, starMinute;//开始时间
    private String entHour, entMinute; //结束时间string

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    //间隔时间
    private ArrayList<String> jiangeTimeList;


    private Intent upservice;
    ArrayList<String> daily_numberofstepsList;
    String bleMac;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_device);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //获取节电时间
        EventBus.getDefault().post(new MessageEvent("getjiediantime"));
       // registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
        registerReceiver(broadcastReceiver,new IntentFilter(WatchUtils.WACTH_DISCONNECT_BLE_ACTION));   //注册接收蓝牙服务断开连接的广播
        initViews();

        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();
        initJiedianData();
        initData();
    }

    private void initJiedianData() {
            //提醒间隔时间
            jiangeTimeList = new ArrayList<>();
            jiangeTimeList.add("0" + "s");
            jiangeTimeList.add("5" + "s");
            jiangeTimeList.add("10" + "s");
            jiangeTimeList.add("30" + "s");
            jiangeTimeList.add("60" + "s");

            minuteMapList = new HashMap<>();
            hourList = new ArrayList<>();
            minuteList = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                if (i == 0) {
                    minuteList.add("00 m");
                } else if (i < 10) {
                    minuteList.add("0" + i + " m");
                } else {
                    minuteList.add(i + " m");
                }
            }
            for (int i = 0; i < 24; i++) {
                if (i == 0) {
                    hourList.add("00 h");
                    minuteMapList.put("00 h", minuteList);
                } else if (i < 10) {
                    hourList.add("0" + i + " h");
                    minuteMapList.put("0" + i + " h", minuteList);
                } else {
                    hourList.add(i + " h");
                    minuteMapList.put(i + " h", minuteList);
                }
            }

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.device));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (null != SharedPreferencesUtils.readObject(WatchDeviceActivity.this, "mylanya")) {
            mDevicename = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this, "mylanya");
            mDeviceAddress = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this, "mylanmac");

        }

        watchMessageJiedianSwitch.setOnCheckedChangeListener(this);
        watchMessageCallphoneSwitch.setOnCheckedChangeListener(this);
    }

    private void initData() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }
        bleMac = (String) SharedPreferencesUtils.readObject(WatchDeviceActivity.this, "mylanmac");
        String tagStp = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this,"settagsteps","");
        if(!WatchUtils.isEmpty(tagStp)){
            watchDeviceTagShowTv.setText(tagStp);
        }

        //来电提醒按钮状态
        String laidianPhone = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "laidianphone", "on");
        if (laidianPhone != null) {
            if ("on".equals(laidianPhone)) {
                watchMessageCallphoneSwitch.setChecked(true);
            } else {
                watchMessageCallphoneSwitch.setChecked(false);
            }
        }
        //节电模式
        String jiedian = (String) SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", "");
        if (jiedian != null) {
            if ("on".equals(jiedian)) {
                watchMessageJiedianSwitch.setChecked(true);
            } else {
                watchMessageJiedianSwitch.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.watchUnpairRel, R.id.watch_mynaozhongRel, R.id.watch_mymsgRel,
            R.id.watch_mycaozuoRel, R.id.watch_takePhotoRel, R.id.watch_targetRel,
            R.id.watch_message_jiedianLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watchUnpairRel:   //解除绑定
                doUnpairDisconn();  //解除绑定
                break;
            case R.id.watch_mynaozhongRel:  //闹钟提醒
                startActivity(WatchAlarmActivity.class);
                break;
            case R.id.watch_mymsgRel:       //消息提醒
                Log.e(TAG, "-----辅助功能----" + WatchUtils.isNotificationEnabled(WatchDeviceActivity.this) + "---" + !WatchUtils.isAccessibilitySettingsOn(this));
                startActivity(WatchMessageActivity.class);
                break;
            case R.id.watch_takePhotoRel:   //拍照
                if(AndPermission.hasPermission(WatchDeviceActivity.this,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)){
                    startActivity(CameraActivity.class);
                }else{
                    AndPermission.with(WatchDeviceActivity.this)
                            .permission(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)
                            .requestCode(REQUEST_OPENCAMERA_CODE)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    AndPermission.rationaleDialog(WatchDeviceActivity.this,rationale).show();
                                }
                            })
                            .callback(permissin)
                            .start();
                }
                break;
            case R.id.watch_mycaozuoRel:    //操作说明
                startActivity(WatchOperationActivity.class);
                break;
            case R.id.watch_targetRel:  //目标设置
                ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(WatchDeviceActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置步数
//                        watchRecordTagstepTv.setText("目标步数 " + profession);
//                        recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
                        watchDeviceTagShowTv.setText(profession);
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "settagsteps", profession);
                        // recordwaveProgressBar.setValue(Float.valueOf((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")));

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_numberofstepsList) //min year in loop
                        .dateChose("10000") // date chose when init popwindow
                        .build();
                dailyumberofstepsPopWin.showPopWin(WatchDeviceActivity.this);
                break;
            case R.id.watch_message_jiedianLin: //节电模式
                if (SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", "") != null &&
                        "on".equals(SharedPreferencesUtils.getParam(WatchDeviceActivity.this, "jiedianstate", ""))) {
                    showJieDianStartTime(); //节电模式开始时间
                }
                break;
        }
}
    //解除绑定
    private void doUnpairDisconn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.confirm_unbind_strap))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        boolean isServiceRunn = WatchUtils.isServiceRunning(WatchBluetoothService.class.getName(),WatchDeviceActivity.this);
                        Log.e(TAG,"--------服务是否在运行-----"+isServiceRunn);
                        if(!isServiceRunn){
                            WatchUtils.disCommH8();
                            startActivity(NewSearchActivity.class);
                            finish();
                        }else{
                            showLoadingDialog("disconn...");
                            SharedPreferencesUtils.setParam(MyApp.getContext(),"bozlunmac","");
                            MyApp.getWatchBluetoothService().disconnect();//断开蓝牙
                        }

                    }
                }).show();

    }


    //首先显示开始时间
    private void showJieDianStartTime() {
        ProvincePick starPopWin = new ProvincePick.Builder(WatchDeviceActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                starHour = province.substring(0, province.length() - 2);
                starMinute = city.substring(0, city.length() - 2);
                watchJiedianStarttimeTv.setText(starHour + ":" + starMinute);
                showEndTime();  //选择开始时间后显示结束

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(WatchDeviceActivity.this);
    }

    //显示结束时间
    private void showEndTime() {
        ProvincePick starPopWin = new ProvincePick.Builder(WatchDeviceActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                entHour = province.substring(0, province.length() - 2);
                entMinute = city.substring(0, city.length() - 2);
                watchJiedianEndtimeTv.setText(entHour + ":" + entMinute);
                String aa = "3秒";
                EventBus.getDefault().post(new MessageEvent("settingjiediantime", starHour + starMinute + entHour + entMinute + "-"));
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(WatchDeviceActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "-----辅助功能--2222--" + WatchUtils.isAccessibilitySettingsOn(this));
        startActivity(new Intent(WatchDeviceActivity.this, WatchMessageActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if (result != null) {
            if(result.equals("msgJiedian")){
                String timeData = (String) event.getObject();
                watchJiedianStarttimeTv.setText(StringUtils.substringBefore(timeData,"-"));
                watchJiedianEndtimeTv.setText(StringUtils.substringAfter(timeData,"-"));
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(broadcastReceiver);

    }

    /**
     * H8断开连接接收广播，只断开连接，不需要解除配对
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "-------action---" + action);
            if (!WatchUtils.isEmpty(action)) {
                if(action.equals(WatchUtils.WACTH_DISCONNECT_BLE_ACTION)){
                    String bleState = intent.getStringExtra("bledisconn");
                    if(!WatchUtils.isEmpty(bleState) && bleState.equals("bledisconn")){ //断开连接
                        closeLoadingDialog();
                        MyCommandManager.deviceDisconnState = true;
                        WatchBluetoothService.isInitiative = true;
                        MyCommandManager.ADDRESS = null;
                        MyCommandManager.DEVICENAME = null;
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", "");
                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", null);
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                }
            }
        }
    };

    /**
     * 节点模式和来电提醒的开关点击事件
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.watch_message_jiedianSwitch:  //节电模式
                if (isChecked) {
                    SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "jiedianstate", "on");
                } else {
                    SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "jiedianstate", "off");
                }
                break;
            case R.id.watch_message_callphoneSwitch:    //来电提醒
                if (isChecked) {
                    //先判断是否有读取手机状态，读取联系人，拨打电话的权限
                    if(AndPermission.hasPermission(WatchDeviceActivity.this,Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS)){
                        watchMessageCallphoneSwitch.setChecked(true);
                        SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "on");
                    }else{
                        AndPermission.with(WatchDeviceActivity.this)
                                .requestCode(REQUEST_REQDPHONE_STATE_CODE)
                                .permission(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,
                                        Manifest.permission.WRITE_SETTINGS)
                                .rationale(new RationaleListener() {
                                    @Override
                                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                        AndPermission.rationaleDialog(WatchDeviceActivity.this,rationale).show();
                                    }
                                }).callback(permissin)
                                .start();
                    }

                } else {
                    watchMessageCallphoneSwitch.setChecked(false);
                    SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "off");
                }
                break;
        }
    }

    //权限回调
    private PermissionListener permissin = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode){
                case REQUEST_REQDPHONE_STATE_CODE:
                    watchMessageCallphoneSwitch.setChecked(true);
                    SharedPreferencesUtils.setParam(WatchDeviceActivity.this, "laidianphone", "on");
                    break;
                case REQUEST_OPENCAMERA_CODE:   //打开相机
                    Log.e(TAG,"-----请求打开相机权限成功------");
                    startActivity(CameraActivity.class);
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case REQUEST_REQDPHONE_STATE_CODE:
                    Log.e(TAG,"-----请求读取手机状态权限失败------");
                    if(AndPermission.hasAlwaysDeniedPermission(WatchDeviceActivity.this,deniedPermissions)){
//                        AndPermission.defaultSettingDialog(WatchDeviceActivity.this)
//                                .setTitle("提示")
//                                .setMessage("请求权限失败,是否打开权限？")
//                                .setPositiveButton("是")
//                                .show();
                        ActivityCompat.shouldShowRequestPermissionRationale(WatchDeviceActivity.this,Manifest.permission.CALL_PHONE);
                    }
                    break;
                case REQUEST_OPENCAMERA_CODE:
                    Log.e(TAG,"-----请求打开相机权限失败------");
                    if(AndPermission.hasAlwaysDeniedPermission(WatchDeviceActivity.this,deniedPermissions)){
                        AndPermission.defaultSettingDialog(WatchDeviceActivity.this)
                                .setTitle("提示")
                                .setMessage("请求打开相机的权限失败,无法打开相机,是否打开权限？")
                                .setPositiveButton("是")
                                .show();
                    }
                    break;
            }

        }
    };
}
