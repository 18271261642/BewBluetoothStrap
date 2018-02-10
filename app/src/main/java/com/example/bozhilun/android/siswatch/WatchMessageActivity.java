package com.example.bozhilun.android.siswatch;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.isRunService_util;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.utils.test.JiedianListener;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

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
 * Created by Administrator on 2017/7/22.
 */

public class WatchMessageActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final int REQD_MSG_CONTENT_CODE = 1001;  //读取短信内容权限code
    private static final int NOTI_OPEN_BACK_CODE = 1002;    //打开通知返回code
    private static final int ACCESS_OPEN_BACK_CODE = 1003;  //打开服务服务返回

    private static final String TAG = "WatchMessageActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //消息提醒间隔时间显示Tv
    @BindView(R.id.watch_message_jiangeTv)
    TextView watchMessageJiangeTv;
    //消息提醒间隔布局
    @BindView(R.id.watch_message_jiangeLin)
    LinearLayout watchMessageJiangeLin;
    //微信的开关
    @BindView(R.id.watchmWechatSwitch)
    SwitchCompat watchmWechatSwitch;
    //短信的开关
    @BindView(R.id.watchmsgSwitch)
    SwitchCompat watchmsgSwitch;
    //QQ的开关
    @BindView(R.id.watchmQQSwitch)
    SwitchCompat watchmQQSwitch;
    //Viber的开关
    @BindView(R.id.watchViberSwitch)
    SwitchCompat watchViberSwitch;
    //twitter的开关
    @BindView(R.id.watchTwitterSwitch)
    SwitchCompat watchTwitterSwitch;
    //facebook的开关
    @BindView(R.id.watchFacebookSwitch)
    SwitchCompat watchFacebookSwitch;
    //wathcapp的开关
    @BindView(R.id.watchWhatsappSwitch)
    SwitchCompat watchWhatsappSwitch;
    //instarg的开关
    @BindView(R.id.watchInstagramSwitch)
    SwitchCompat watchInstagramSwitch;

    private String starHour, starMinute;//开始时间
    private String entHour, entMinute; //结束时间string

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;

    private ArrayList<String> jiangeTimeList;

    boolean jiedian = false;

    private Intent upservice;

    private JiedianListener jiedianListener;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_message);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initViews();

        initChecked();

        initData();


    }
    //查询开关状态
    private void initChecked() {
        //消息提醒间隔设置
        String msgjiange = (String) SharedPreferencesUtils.getParam(WatchMessageActivity.this, "profession", "");
        if (!WatchUtils.isEmpty(msgjiange)) {
            watchMessageJiangeTv.setText(msgjiange);
        } else {
            watchMessageJiangeTv.setText("0s");
        }
        //微信提醒状态
        String wechatState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"weixinmsg");
        Log.e(TAG,"--------微信----"+wechatState);
        if(!WatchUtils.isEmpty(wechatState)){
            if(wechatState.equals("0")){
                watchmWechatSwitch.setChecked(true);
            }else{
                watchmWechatSwitch.setChecked(false);
            }
        }
        //短信的提醒状态
        String msgState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"msg");
        Log.e(TAG,"--------短信----"+msgState);
        if(!WatchUtils.isEmpty(msgState)){
            if(msgState.equals("0")){
                watchmsgSwitch.setChecked(true);
            }else{
                watchmsgSwitch.setChecked(false);
            }
        }
        //QQ的提醒状态
        String qqState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"qqmsg");
        Log.e(TAG,"--------qqState----"+qqState);
        if(!WatchUtils.isEmpty(qqState)){
            if(qqState.equals("0")){
                watchmQQSwitch.setChecked(true);
            }else{
                watchmQQSwitch.setChecked(false);
            }
        }
        //viber的提醒状态
        String viberState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"Viber");
        Log.e(TAG,"--------viberState----"+viberState);
        if(!WatchUtils.isEmpty(viberState)){
            if(viberState.equals("0")){
                watchViberSwitch.setChecked(true);
            }else{
                watchViberSwitch.setChecked(false);
            }
        }
        //twitter的提醒状态
        String twitterState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"Twitteraa");
        Log.e(TAG,"--------twitterState----"+twitterState);
        if(!WatchUtils.isEmpty(twitterState)){
            if(twitterState.equals("0")){
                watchTwitterSwitch.setChecked(true);
            }else{
                watchTwitterSwitch.setChecked(false);
            }
        }
        //facebook的提醒状态
        String facebookState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"facebook");
        Log.e(TAG,"--------facebookState----"+facebookState);
        if(!WatchUtils.isEmpty(facebookState)){
            if(facebookState.equals("0")){
                watchFacebookSwitch.setChecked(true);
            }else{
                watchFacebookSwitch.setChecked(false);
            }
        }//whatsapp的提醒状态
        String whatsappState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"Whatsapp");
        Log.e(TAG,"--------whatsappState----"+whatsappState);
        if(!WatchUtils.isEmpty(whatsappState)){
            if(whatsappState.equals("0")){
                watchWhatsappSwitch.setChecked(true);
            }else{
                watchWhatsappSwitch.setChecked(false);
            }
        }//intager提醒状态
        String intagerState = (String) SharedPreferencesUtils.readObject(WatchMessageActivity.this,"Instagrambutton");
        Log.e(TAG,"--------intagerState----"+intagerState);
        if(!WatchUtils.isEmpty(intagerState)){
            if(intagerState.equals("0")){
                watchInstagramSwitch.setChecked(true);
            }else{
                watchInstagramSwitch.setChecked(false);
            }
        }


    }

    private void initData() {
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
        tvTitle.setText(getResources().getString(R.string.Messagealert));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        watchmWechatSwitch.setOnCheckedChangeListener(this);
        watchmsgSwitch.setOnCheckedChangeListener(this);
        watchmQQSwitch.setOnCheckedChangeListener(this);
        watchViberSwitch.setOnCheckedChangeListener(this);
        watchTwitterSwitch.setOnCheckedChangeListener(this);
        watchFacebookSwitch.setOnCheckedChangeListener(this);
        watchWhatsappSwitch.setOnCheckedChangeListener(this);
        watchInstagramSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.watchmWechatSwitch:   //微信
                if (b) {
                    Log.e("MSG","-----b---"+b);
                    watchmWechatSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "weixinmsg", "0");
                } else {
                    watchmWechatSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "weixinmsg", "1");
                }
                break;
            case R.id.watchmsgSwitch:   //短信
                if(b){
                    //判断是否获取读取短信内容的权限
                    if(!AndPermission.hasPermission(WatchMessageActivity.this,Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS)){
                        //申请获取短信内容的权限
                        AndPermission.with(WatchMessageActivity.this)
                                .requestCode(REQD_MSG_CONTENT_CODE)
                                .permission(Manifest.permission.READ_SMS)
                                .rationale(new RationaleListener() {
                                    @Override
                                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                        AndPermission.rationaleDialog(WatchMessageActivity.this,rationale).show();
                                    }
                                }).callback(permissionListener)
                                .start();
                    }else{
                        watchmsgSwitch.setChecked(true);
                        SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "msg", "0");
                    }

                }else{
                    watchmsgSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "msg", "1");
                }

                break;
            case R.id.watchmQQSwitch:   //QQ
                if (b) {
                    watchmQQSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "qqmsg", "0");
                } else {
                    watchmQQSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "qqmsg", "1");
                }
                break;
            case R.id.watchViberSwitch: //viber
                if (b) {
                    watchViberSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Viber", "0");
                } else {
                    watchViberSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Viber", "1");
                }
                break;
            case R.id.watchTwitterSwitch:   //twitter
                if (b) {
                    watchTwitterSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Twitteraa", "0");
                } else {
                    watchTwitterSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Twitteraa", "1");
                }
                break;
            case R.id.watchFacebookSwitch:  //facebook
                if (b) {
                    watchFacebookSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "facebook", "0");
                } else {
                    watchFacebookSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "facebook", "1");
                }
                break;
            case R.id.watchWhatsappSwitch:  //wathapp
                if (b) {
                    watchWhatsappSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Whatsapp", "0");
                } else {
                    watchWhatsappSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Whatsapp", "1");
                }
                break;
            case R.id.watchInstagramSwitch: //instag
                if (b) {
                    watchInstagramSwitch.setChecked(true);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Instagrambutton", "0");
                } else {
                    watchInstagramSwitch.setChecked(false);
                    SharedPreferencesUtils.saveObject(WatchMessageActivity.this, "Instagrambutton", "1");
                }
                break;
        }
    }

    @OnClick({R.id.watch_message_jiangeLin, R.id.watch_msgOpenNitBtn, R.id.watch_msgOpenAccessBtn,R.id.watchMsgExplainTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_message_jiangeLin:  //时间间隔
                ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(WatchMessageActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置提醒间隔时间
                        watchMessageJiangeTv.setText(profession);
                        EventBus.getDefault().post(new MessageEvent("jiangetime", profession));
                        SharedPreferencesUtils.setParam(WatchMessageActivity.this, "profession", profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(jiangeTimeList) //min year in loop
                        .dateChose("0s") // date chose when init popwindow
                        .build();
                dailyumberofstepsPopWin.showPopWin(WatchMessageActivity.this);
                break;
            case R.id.watch_msgOpenNitBtn:  //打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, NOTI_OPEN_BACK_CODE);
                break;
            case R.id.watch_msgOpenAccessBtn:   //打开辅助服务功能
                Intent ints = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(ints,ACCESS_OPEN_BACK_CODE);
                break;
            case R.id.watchMsgExplainTv:    //说明
                builder = new AlertDialog.Builder(WatchMessageActivity.this)
                        .setTitle("说明")
                        .setMessage("消息不提醒"+"\n"+"请确保通知或者辅助功能已打开,请查看所需权限是否打开"+"\n"+"来电无提醒或无法挂断电话"+"\n"+"请查看所需权限是否打开,请检查手机安全软件是否阻止")
                        .setNegativeButton(getResources().getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msgResult = event.getMessage();
        Log.e("消息", "----msgResult----" + msgResult + "---" + event.getObject());
//        if (msgResult != null) {
//            if(msgResult.equals("msgJiedian")){
//                String timeData = (String) event.getObject();
//                watchJiedianStarttimeTv.setText(StringUtils.substringBefore(timeData,"-"));
//                watchJiedianEndtimeTv.setText(StringUtils.substringAfter(timeData,"-"));
//            }
//        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //更新消息服务
    private void updateServiceStatus(boolean start) {
        try {
            boolean bRunning = isRunService_util.isServiceRunning(this, "com.example.bozhilun.android.activity.wylactivity.wyl_util.service.NeNotificationService");
            if (start && !bRunning) {
                this.startService(upservice);
            } else if (!start && bRunning) {
                this.stopService(upservice);
            }
            bRunning = isRunService_util.isServiceRunning(this, "com.example.bozhilun.android.activity.wylactivity.wyl_util.service.NeNotificationService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断通知栏是否开启
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 动态申请权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                switch (requestCode){

                }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case REQD_MSG_CONTENT_CODE:

                    break;
            }
            AndPermission.hasAlwaysDeniedPermission(WatchMessageActivity.this,deniedPermissions);
        }
    };
}
