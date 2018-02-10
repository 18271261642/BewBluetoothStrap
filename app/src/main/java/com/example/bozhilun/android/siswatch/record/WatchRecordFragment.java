package com.example.bozhilun.android.siswatch.record;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.CameraActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.settingactivity.SharePosterActivity;
import com.example.bozhilun.android.siswatch.GetWatchTimeActivity;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseFragment;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/17.
 */

/**
 * 记录页面fragment
 */
public class WatchRecordFragment extends WatchBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    View recordView;
    //左边布局
    @BindView(R.id.newH8RecordTopRel)
    RelativeLayout newH8RecordTopRel;
    //显示时间tv
    @BindView(R.id.new_h8_recordtop_dateTv)
    TextView newH8RecordtopDateTv;
    //分享按钮
    @BindView(R.id.new_h8_recordShareImg)
    ImageView newH8RecordShareImg;
    //拍照按钮
    @BindView(R.id.new_h8_recordPhotoImg)
    ImageView newH8RecordPhotoImg;
    //连接状态
    @BindView(R.id.new_h8_recordwatch_connectStateTv)
    TextView newH8RecordwatchConnectStateTv;
    //波浪形进度条
    @BindView(R.id.recordwave_progress_bar)
    WaveProgress recordwaveProgressBar;
    @BindView(R.id.watch_recordTagstepTv)
    TextView watchRecordTagstepTv;
    Unbinder unbinder;
    //目标选择列表
    ArrayList<String> daily_numberofstepsList;

    //卡里路
    @BindView(R.id.watch_recordKcalTv)
    TextView watchRecordKcalTv;
    //距离
    @BindView(R.id.watch_recordMileTv)
    TextView watchRecordMileTv;
    //计算卡里路常量
    double kcalcanstanc = 65.4;  //计算卡路里常量
    @BindView(R.id.watch_record_swipe)
    SwipeRefreshLayout watchRecordSwipe;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:    //刷新步数
                    if (watchRecordSwipe != null) {
                        watchRecordSwipe.setRefreshing(false);
                        EventBus.getDefault().post(new MessageEvent("refreshsteps"));
                    }

                    break;
                case 11:
                    int steps = (int) msg.obj;  //显示步数
                    String maxvalue = (String) SharedPreferencesUtils.getParam(getActivity(), "settagsteps", "");
                    if (steps >= 0) {
                        recordwaveProgressBar.setMaxValue(Float.valueOf(maxvalue));
                        recordwaveProgressBar.setValue(steps);
                    }
                    break;

            }

        }
    };

    String connectstate;

    @BindView(R.id.watchRestateTv)
    TextView watchRestateTv;
    @BindView(R.id.watch_record_pro)
    LinearLayout watchRecordPro;
    @BindView(R.id.watch_recordTyophyImg)
    ImageView watchRecordTyophyImg;


    private boolean mReceiverTag = false;   //广播接受者标识


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        registerReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recordView = inflater.inflate(R.layout.fragment_watch_record, null);
        unbinder = ButterKnife.bind(this, recordView);

        initViews();
        initStepList();
        //设置默认
        recordwaveProgressBar.setMaxValue(Float.valueOf(10000));
        recordwaveProgressBar.setValue(0);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.icon_trophy);
//        recordwaveProgressBar.setBitmap(bitmap);
        return recordView;
    }

    private void initViews() {
        newH8RecordtopDateTv.setText(WatchUtils.getCurrentDate());
        //newH8RecordPhotoImg.setVisibility(View.GONE);
        watchRecordSwipe.setOnRefreshListener(this);

        newH8RecordtopDateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if("conn".equals(connectstate)){
                    startActivity(new Intent(getActivity(), GetWatchTimeActivity.class));
                }else{
                    startActivity(new Intent(getActivity(),NewSearchActivity.class));
                }
                return true;
            }
        });
        newH8RecordtopDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RecordHistoryActivity.class));
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("WR", "-----onResume----" + MyCommandManager.DEVICENAME);
        if ("conn".equals(connectstate)) {
            //  MyApp.getWatchBluetoothService().isConnected();
            newH8RecordwatchConnectStateTv.setText("" + "connect" + "");
            newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            AnimationUtils.stopFlick(newH8RecordwatchConnectStateTv);
            watchRecordPro.setVisibility(View.INVISIBLE);
        } else {
            newH8RecordwatchConnectStateTv.setText("" + "disconn.." + "");
            newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(newH8RecordwatchConnectStateTv);
            watchRecordPro.setVisibility(View.VISIBLE);
            watchRestateTv.setText(getResources().getString(R.string.bluetooth_disconnected) + "  connecting...");
        }
        if (MyCommandManager.DEVICENAME != null) {
            newH8RecordwatchConnectStateTv.setText("" + "connect" + "");
            newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            AnimationUtils.stopFlick(newH8RecordwatchConnectStateTv);
            watchRecordPro.setVisibility(View.INVISIBLE);
        } else {
            newH8RecordwatchConnectStateTv.setText("" + "disconn.." + "");
            newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            AnimationUtils.startFlick(newH8RecordwatchConnectStateTv);
            watchRecordPro.setVisibility(View.VISIBLE);
            watchRestateTv.setText(getResources().getString(R.string.bluetooth_disconnected) + "  connecting...");

        }
        //步数
        String st = (String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "");
        String userHeight = ((String) SharedPreferencesUtils.getParam(getActivity(), "userheight", "")).trim();
        //设置目标步数
        String targetSteps = (String) SharedPreferencesUtils.getParam(getActivity(), "settagsteps", "");
        if (!WatchUtils.isEmpty(targetSteps)) {
            watchRecordTagstepTv.setText(getResources().getString(R.string.settarget_steps) + targetSteps);
        }
        if (!WatchUtils.isEmpty(st)) {
            initData(st);
            if (userHeight != null) {
                int height = Integer.parseInt(userHeight);
                double stepLong = WatchUtils.getStepLong(height); //步长
                double rundistanc = WatchUtils.div(WatchUtils.mul(stepLong, Double.valueOf(st)), Double.valueOf(1000), 2); //路程
                double kcal = WatchUtils.mul(kcalcanstanc, rundistanc);
                //显示公里数和卡里数
                watchRecordMileTv.setText(StringUtils.substringBefore(String.valueOf(rundistanc), ".") + "." + StringUtils.substringAfter(String.valueOf(rundistanc), ".").substring(0, 1));
                watchRecordKcalTv.setText(StringUtils.substringBefore(String.valueOf(kcal), ".") + "");
                SharedPreferencesUtils.setParam(getActivity(), "watchstepsdistants", StringUtils.substringBefore(String.valueOf(rundistanc), ".") + "." + StringUtils.substringAfter(String.valueOf(rundistanc), ".").substring(0, 1)); //保存路程
                SharedPreferencesUtils.setParam(getActivity(), "watchkcal", StringUtils.substringBefore(String.valueOf(kcal), "."));

            }
        }

    }

    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }

    }

    private void initData(String strSteps) {
        String daily_number_ofsteps_default = (String) SharedPreferencesUtils.getParam(getActivity(), "settagsteps", "");
        if (daily_number_ofsteps_default != null && !"".equals(daily_number_ofsteps_default)) {
            recordwaveProgressBar.setMaxValue(Float.valueOf(daily_number_ofsteps_default.trim()));
            recordwaveProgressBar.setValue(Float.valueOf(strSteps));
            if(Integer.valueOf(strSteps) >= Integer.valueOf(daily_number_ofsteps_default.trim())){
                watchRecordTyophyImg.setVisibility(View.VISIBLE);
            }else{
                watchRecordTyophyImg.setVisibility(View.GONE);
            }
        } else {
            //默认设置为10000
            recordwaveProgressBar.setMaxValue(10000);
            SharedPreferencesUtils.setParam(getActivity(), "settagsteps", "10000");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    long firstTimesss;

    @OnClick({R.id.newH8RecordTopRel, R.id.new_h8_recordShareImg, R.id.watch_recordTagstepTv, R.id.new_h8_recordPhotoImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newH8RecordTopRel:    //电量
                if (null == MyCommandManager.DEVICENAME) {
                    WatchUtils.disCommH8();
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));//收索设备
                    getActivity().finish();
                } else {
                    startActivity(new Intent(getActivity(), WatchDeviceActivity.class));//我的设备
                }
                break;
            case R.id.watch_record_pro:
                if (null == MyCommandManager.DEVICENAME) {
                    WatchUtils.disCommH8();
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));//收索设备
                    getActivity().finish();
                } else {
                    startActivity(new Intent(getActivity(), WatchDeviceActivity.class));//我的设备
                }
                break;
            case R.id.new_h8_recordShareImg:  //分享
                doShareData();  //分享
                break;
            case R.id.new_h8_recordPhotoImg:    //拍照
                AndPermission.with(getActivity())
                        .requestCode(1001)
                        .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                AndPermission.rationaleDialog(getActivity(), rationale).show();
                            }
                        }).callback(permissionListener)
                        .start();

                break;
            case R.id.watch_recordTagstepTv:    //目标步数
                setTagSteps();  //设置目标步数
                break;

        }
    }

    //设置目标步数
    private void setTagSteps() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                //设置目标步数
                watchRecordTagstepTv.setText(getResources().getString(R.string.settarget_steps) + profession);
                recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
                SharedPreferencesUtils.setParam(getActivity(), "settagsteps", profession);
                String setStep = (String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "");
                recordwaveProgressBar.setValue(Float.valueOf(setStep.trim()));
                int aaa = Integer.valueOf(setStep.trim());
                if(aaa >= Integer.valueOf(profession)){
                    watchRecordTyophyImg.setVisibility(View.VISIBLE);
                }else{
                    watchRecordTyophyImg.setVisibility(View.GONE);
                }
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
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

    //分享
    private void doShareData() {
//        Date timedf = new Date();
//        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String xXXXdf = formatdf.format(timedf);
//        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
//        ScreenShot.shoot(getActivity(), new File(filePath));
//        Common.showShare(getActivity(), null, false, filePath);
        startActivity(new Intent(getActivity(), SharePosterActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        Log.e("WRRecordFragment", "-------result---" + result);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                    Message message = new Message();
                    message.what = 10;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    /**
     * 接收蓝牙连接状态的广播
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("WRRR", "--------广播-----" + action);
            if (action != null) {
                try {
                    if (action.equals(WatchUtils.WATCH_CONNECTED_STATE_ACTION)) {
                        String conState = intent.getStringExtra("connectstate");
                        if (!WatchUtils.isEmpty(conState)) {
                            Log.e("WRRR", "--------广播--222---" + conState + "-------" + getActivity().isFinishing());
                            if (!getActivity().isFinishing()) {
                                if (conState.equals("conn")) {
                                    connectstate = "conn";
                                    watchRecordPro.setVisibility(View.INVISIBLE);
                                    newH8RecordwatchConnectStateTv.setText("" + "connect" + "");
                                    newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                                    AnimationUtils.stopFlick(newH8RecordwatchConnectStateTv);
                                } else {
                                    connectstate = "notconn";
                                    watchRecordPro.setVisibility(View.VISIBLE);
                                    watchRestateTv.setText(getResources().getString(R.string.bluetooth_disconnected) + "  connecting...");
                                    newH8RecordwatchConnectStateTv.setText("" + "disconn.." + "");
                                    newH8RecordwatchConnectStateTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                                    AnimationUtils.startFlick(newH8RecordwatchConnectStateTv);
                                }

                            }
                        }
                    }
                    if (action.equals(WatchUtils.WATCH_GETWATCH_STEPS_ACTION)) {    //获取步数
                        if (intent.getStringExtra("homestep").equals("homestep")) {
                            try {
                                String stp = intent.getStringExtra("homesteps");
                                int step = Integer.valueOf(stp);
                                SharedPreferencesUtils.setParam(getActivity(), "stepsnum", String.valueOf(step));
                                String userHeight = (String) SharedPreferencesUtils.getParam(getActivity(), "userheight", "");
                                String maxvalue = (String) SharedPreferencesUtils.getParam(getActivity(), "settagsteps", "");
                                double distants = WatchUtils.getDistants(Integer.valueOf(step), WatchUtils.getStepLong(Integer.parseInt(userHeight)));
                                watchRecordMileTv.setText("" + StringUtils.substringBefore(String.valueOf(distants), ".") + "." + StringUtils.substringAfter(String.valueOf(distants), ".").substring(0, 1) + "");   //公里数=步数 / 1000公里数
                                watchRecordKcalTv.setText("" + StringUtils.substringBefore(String.valueOf(WatchUtils.mul(kcalcanstanc, distants)), ".") + ""); //卡里路 =步数 / 1000 * 0.65 是卡里
                                recordwaveProgressBar.setMaxValue(Float.valueOf(maxvalue));
                                recordwaveProgressBar.setValue(step);
                                if (Integer.valueOf(maxvalue) <= step) {
                                    watchRecordTyophyImg.setVisibility(View.VISIBLE);
                                } else {
                                    watchRecordTyophyImg.setVisibility(View.GONE);
                                }

                                SharedPreferencesUtils.setParam(getActivity(), "watchstepsdistants", StringUtils.substringBefore(String.valueOf(distants), ".") + "." + StringUtils.substringAfter(String.valueOf(distants), ".").substring(0, 1)); //保存路程
                                SharedPreferencesUtils.setParam(getActivity(), "watchkcal", String.valueOf(WatchUtils.mul(kcalcanstanc, distants)));
                                SharedPreferencesUtils.setParam(getActivity(), "stepsnum", WatchUtils.mul(kcalcanstanc, distants));
                                String h8syncdate = (String) SharedPreferencesUtils.getParam(getActivity(),"h8Sycndate","");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //代码中动态注册广播
    private void registerReceiver() {
        if (!mReceiverTag) {     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            IntentFilter mFileter = new IntentFilter();
            mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
            mFileter.addAction(WatchUtils.WATCH_CONNECTED_STATE_ACTION);
            mFileter.addAction(WatchUtils.WATCH_GETWATCH_STEPS_ACTION);
            MyApp.getInstance().registerReceiver(broadcastReceiver, mFileter);
        }
    }

    //注销广播
    private void unregisterReceiver() {
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            MyApp.getInstance().unregisterReceiver(broadcastReceiver);   //注销广播
        }

    }

    /**
     * 申请权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case 1001:
                    startActivity(new Intent(getActivity(), CameraActivity.class));
                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case 1001:

                    break;
            }
            AndPermission.hasAlwaysDeniedPermission(getActivity(), deniedPermissions);
        }
    };

}
