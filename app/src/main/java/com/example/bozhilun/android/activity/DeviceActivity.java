package com.example.bozhilun.android.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.CameraActivity;
import com.example.bozhilun.android.activity.wylactivity.MessageAcitivity;
import com.example.bozhilun.android.activity.wylactivity.WenxinBandActivity;
import com.example.bozhilun.android.alock.AlockActivity;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.VerifyUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/14.
 * 设备设置
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceActivity extends BaseActivity {

    private static final int OPEN_CAMERA_REQUEST_CODE = 1001;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.raisethebrightscreen_switch)
    SwitchCompat raisethebrightscreenSwitch;//抬手亮屏
    @BindView(R.id.fangdu_swicth)
    SwitchCompat fangduSwicth;//防丢
    @BindView(R.id.autoxinlv_swicth)
    SwitchCompat autoxinlvSwicth;//自动心率测量
    @BindView(R.id.mywristband_tv)
    TextView mywristbandTv;
    @BindView(R.id.search_wristband_cardview)
    CardView fingdphone;//查找手机b15p没有
    @BindView(R.id.sedentary_reminder_relayout)
    RelativeLayout JIUZUO;//久坐提醒


    private static final int TAKE_PHOTO = 1;//调用相机拍照
    private boolean isopenraisethebrightscreen, isopenfangduSwicth, isopenautoxinlvSwicth;
    private String mDeviceName, mDeviceAddress;

    /**
     * 查看蓝牙的名字和地址
     */
    public void FinfdevicesNameAndadress() {
        try {
            if (null != SharedPreferencesUtils.readObject(DeviceActivity.this, "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(DeviceActivity.this, "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(DeviceActivity.this, "mylanmac");//蓝牙的mac
                MyCommandManager.DEVICENAME = mDeviceName;
                mywristbandTv.setText(getResources().getString(R.string.my) + mDeviceName + getResources().getString(R.string.shouhuan));
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
                mywristbandTv.setText(getResources().getString(R.string.my) + "- -" + getResources().getString(R.string.shouhuan));
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    protected void initViews() {
        tvTitle.setText(getResources().getString(R.string.device));
        EventBus.getDefault().register(this);

        //请求相机权限
        AndPermission.with(this)
                .requestCode(OPEN_CAMERA_REQUEST_CODE)
                .permission(Manifest.permission.CAMERA)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(DeviceActivity.this, rationale).show();
                    }
                }).start();

        try {
            FinfdevicesNameAndadress();

            //判断设备类型
            if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                fingdphone.setVisibility(View.GONE);
            } else if ("B15S-H".equals(mDeviceName)) {
                JIUZUO.setVisibility(View.GONE);
            }

            //这里要查询开关状态
            Object isopenraisethebrightscreen = SharedPreferencesUtils.readObject(DeviceActivity.this, "isopenraisethebrightscreen");
            Object isopenfangduSwicth = SharedPreferencesUtils.readObject(DeviceActivity.this, "isopenfangduSwicth");
            Object isopenautoxinlvSwicth = SharedPreferencesUtils.readObject(DeviceActivity.this, "isopenautoxinlvSwicth");
            if (null != isopenraisethebrightscreen) {
                if ("0".equals(isopenraisethebrightscreen)) {
                    raisethebrightscreenSwitch.setChecked(true);
                } else {
                    raisethebrightscreenSwitch.setChecked(false);
                }
            }
            if (null != isopenfangduSwicth) {
                if ("0".equals(isopenfangduSwicth)) {
                    fangduSwicth.setChecked(true);
                } else {
                    fangduSwicth.setChecked(false);
                }
            }
            if (null != isopenautoxinlvSwicth) {
                if ("0".equals(isopenautoxinlvSwicth)) {
                    autoxinlvSwicth.setChecked(true);
                } else {
                    autoxinlvSwicth.setChecked(false);
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        raisethebrightscreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isopenraisethebrightscreen = true;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenraisethebrightscreen", "0");
                    if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                        MyCommandManager.Raisethebrightscreen(MyCommandManager.DEVICENAME, 0);
                    } else {
                        MyCommandManager.Raisethebrightscreen(MyCommandManager.DEVICENAME, 1);
                    }

                } else {
                    isopenraisethebrightscreen = false;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenraisethebrightscreen", "1");
                    if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                        MyCommandManager.Raisethebrightscreen(MyCommandManager.DEVICENAME, 1);
                    } else {
                        MyCommandManager.Raisethebrightscreen(MyCommandManager.DEVICENAME, 0);
                    }
                }
            }
        });
        fangduSwicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isopenfangduSwicth = true;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenfangduSwicth", "0");
                    MyCommandManager.Intelligentantilost(MyCommandManager.DEVICENAME, 0);
                } else {
                    isopenfangduSwicth = false;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenfangduSwicth", "1");
                    MyCommandManager.Intelligentantilost(MyCommandManager.DEVICENAME, 1);
                }
            }
        });
        autoxinlvSwicth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isopenautoxinlvSwicth = true;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenautoxinlvSwicth", "0");
                    if (VerifyUtil.getIsChinesEnglish(DeviceActivity.this)) {
                        MyCommandManager.Automaticheartratedetection(0, 0, 0);
                    } else {
                        MyCommandManager.Automaticheartratedetection(1, 0, 0);
                    }
                } else {
                    isopenautoxinlvSwicth = false;
                    SharedPreferencesUtils.saveObject(DeviceActivity.this, "isopenautoxinlvSwicth", "1");
                    if (VerifyUtil.getIsChinesEnglish(DeviceActivity.this)) {
                        MyCommandManager.Automaticheartratedetection(0, 0, 1);
                    } else {
                        MyCommandManager.Automaticheartratedetection(1, 0, 1);
                    }
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("automaticheartratedetection".equals(msg)) {
            boolean result = (boolean) event.getObject();
            if (!result) {
                // ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_fail));
                setDdefault(autoxinlvSwicth, autoxinlvSwicth.isChecked());
            } else {
                ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_success));
            }
        } else if ("intelligentantilost".equals(msg)) {
            boolean result = (boolean) event.getObject();
            if (!result) {
                ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_fail));
                setDdefault(fangduSwicth, fangduSwicth.isChecked());
            } else {
                ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_success));
            }
        } else if ("raisethebrightscreen".equals(msg)) {
            boolean result = (boolean) event.getObject();
            if (!result) {
                ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_fail));
                setDdefault(raisethebrightscreenSwitch, raisethebrightscreenSwitch.isChecked());
            } else {
                ToastUtil.showShort(DeviceActivity.this, getString(R.string.settings_success));
            }
        }


    }

    private void setDdefault(CompoundButton compoundButton, boolean b) {
        if (b) {
            compoundButton.setChecked(true);
        } else {
            compoundButton.setChecked(false);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_device;
    }

    @OnClick({R.id.wristband_cardview, R.id.movement_control_cardview, R.id.sedentary_reminder_relayout, R.id.alarmclock_relayout, R.id.message_notification_relayout, R.id.vis_control_relayout, R.id.shake_cardview, R.id.search_wristband_cardview, R.id.wechat_movement_cardview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wristband_cardview:
                startActivity(new Intent(DeviceActivity.this, MyStrapActivity.class));
                break;
            case R.id.movement_control_cardview:
                break;
            case R.id.sedentary_reminder_relayout:
                startActivity(new Intent(DeviceActivity.this, SedentaryeminderActivity.class));
                break;
            case R.id.alarmclock_relayout:
             /*   if("B15P".equals(MyCommandManager.DEVICENAME)){
                    startActivity(new Intent(DeviceActivity.this, AlockActivity.class));
                }else{
                    startActivity(new Intent(DeviceActivity.this, AlarmClockActivity.class));
                }*/
                startActivity(new Intent(DeviceActivity.this, AlockActivity.class));
                break;
            case R.id.message_notification_relayout:    //到消息提醒页面
                //加入提示框
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceActivity.this);
                builder.setTitle(getResources().getString(R.string.prompt));
                builder.setMessage(getResources().getString(R.string.xiaoxitixing));
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {//上传到服务器
                            dialog.dismiss();
                            Intent intentr = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivityForResult(intentr, 0);
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //查询下数据
                        // chaoxundata();
                    }
                });
                builder.create().show();
                break;
            case R.id.vis_control_relayout:
                break;
            case R.id.shake_cardview:
                try {//摇一摇拍照
                    if (BluetoothLeService.isService) {
                        MyCommandManager.Shakethecamera(MyCommandManager.DEVICENAME, 1);//开启摇一摇
                        startActivity(new Intent(DeviceActivity.this, CameraActivity.class));
                    } else {
                        Toast.makeText(DeviceActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception E) {
                    E.printStackTrace();
                }

                break;
            case R.id.search_wristband_cardview: /**查找手环  b15s b15h才有*/
                MyCommandManager.FindBracelet();
                break;
            case R.id.wechat_movement_cardview:
                //微信
                startActivity(new Intent(DeviceActivity.this, WenxinBandActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(DeviceActivity.this, MessageAcitivity.class));
    }

    /**
     * 请求权限成功和失败回调
     *
     * @param requestCode
     * @param grantPermissions
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode){
                case OPEN_CAMERA_REQUEST_CODE:

                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case OPEN_CAMERA_REQUEST_CODE:

                    break;
            }
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(DeviceActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(DeviceActivity.this, OPEN_CAMERA_REQUEST_CODE).show();}
        }
    };

}
