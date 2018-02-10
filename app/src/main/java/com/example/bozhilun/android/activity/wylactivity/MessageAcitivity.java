package com.example.bozhilun.android.activity.wylactivity;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.NeNotificationService;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import java.util.List;
import butterknife.BindView;


/**
 * Created by admin on 2016/9/3.
 * 消息提醒
 */
public class MessageAcitivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private static final int READ_CONTACTS_CODE = 1001;
    private static final int READ_MSG_CODE = 1002;
    private static final int READ_SETTINGS_CODE = 1003;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public final static String TAG = "MessageAcitivity";
    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    //连接状态
    private Handler handler = new Handler();

    private LinearLayout rootLayout;

    @BindView(R.id.mTosss)//微信
     SwitchCompat weixin;
    @BindView(R.id.msg_my)
    SwitchCompat MSG;//信息
    @BindView(R.id.mTogBtsssn2)
    SwitchCompat qqmsg;//qq
    @BindView(R.id.Viber)
    SwitchCompat Viber;//Viber
    @BindView(R.id.Twitter)
    SwitchCompat Twitter;//Twitter
    @BindView(R.id.Facebook)
    SwitchCompat Facebook;//Facebook
    @BindView(R.id.Whatsapp)
    SwitchCompat Whatsapp;//Whatsapp
    @BindView(R.id.Instagram)
    SwitchCompat Instagram;//Instagram
    @BindView(R.id.laidiantixing)
    SwitchCompat laidiantixing;//来电

    @Override
    protected int getContentViewId() {
        return R.layout.acitivity_message;
    }

    protected void initViews() {

        initMonitor();  //判断是否开启辅助服务

        //设置默认状态
        initCheckState();

        //查询开关状态
        chushihua();

        //申请读短信的权限
        AndPermission.with(MessageAcitivity.this)
                .requestCode(READ_SETTINGS_CODE)
                .permission(Manifest.permission.WRITE_SETTINGS)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MessageAcitivity.this,rationale).show();
                    }
                })
                .callback(permissionListener)
                .start();

    }

    //判断是否开启辅助服务
    private void initMonitor() {
        /**
         * 是否开启辅助服务
         */
        if (WatchUtils.isAccessibilitySettingsOn(MessageAcitivity.this)) {
            Intent intentr = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intentr, 0);
        }
        /**
         * 开启通知栏
         */
        if (WatchUtils.isNotificationEnabled(this)) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, 1);
        }
    }

    //设置监听
    private void initCheckState() {
        weixin.setOnCheckedChangeListener(this);
        MSG.setOnCheckedChangeListener(this);
        qqmsg.setOnCheckedChangeListener(this);
        Twitter.setOnCheckedChangeListener(this);
        Facebook.setOnCheckedChangeListener(this);
        Whatsapp.setOnCheckedChangeListener(this);
        Instagram.setOnCheckedChangeListener(this);
        Viber.setOnCheckedChangeListener(this);
        laidiantixing.setOnCheckedChangeListener(this);

    }

    //查询状态
    public void chushihua() {
            //来电提醒
            String mySharedPre = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "laidian");  //来电提醒
            if (mySharedPre != null) {
                if ("0".equals(mySharedPre)) {
                    laidiantixing.setChecked(true);
                } else {
                    laidiantixing.setChecked(false);
                }
            }
            //微信
            String mySharedPreferences = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "weixinmsg");    //微信
            if (mySharedPreferences != null) {
                Log.e("MSG","----mySharedPreferences-22--"+mySharedPreferences);
                if ("0".equals(mySharedPreferences)) {
                    weixin.setChecked(true);
                } else {
                    weixin.setChecked(false);
                }
            }
            //QQ
            String qqmsga = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "qqmsg"); //QQ
            if (qqmsga != null) {
                if ("0".equals(qqmsga)) {
                    qqmsg.setChecked(true);
                } else {
                    qqmsg.setChecked(false);
                }
            }
            //facebook
            String mySharedPref = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "facebook");    //facebook
            if (mySharedPref != null) {
                if ("0".equals(mySharedPref)) {
                    Facebook.setChecked(true);
                } else {
                    Facebook.setChecked(false);
                }
            }

            //Instagram
            String mySharedPInstagrambutton = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "Instagrambutton"); //Instagr
            if (mySharedPInstagrambutton != null) {
                if ("0".equals(mySharedPInstagrambutton)) {
                    Instagram.setChecked(true);
                } else {
                    Instagram.setChecked(false);
                }
            }

            //Twitteraa
            String mySharedPreTwitteraa = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "Twitteraa");   //twitteraa
            if (mySharedPreTwitteraa != null) {
                if ("0".equals(mySharedPreTwitteraa)) {
                    Twitter.setChecked(true);
                } else {
                    Twitter.setChecked(false);
                }
            }

            //Whatsapp
            String mySharedWhatsapp = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "Whatsapp");    //wahtsapp
            if (mySharedWhatsapp != null) {
                if ("0".equals(mySharedWhatsapp)) {
                    Whatsapp.setChecked(true);
                } else {
                    Whatsapp.setChecked(false);
                }
            }
            //
            String mySharedPreViber = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "Viber");   //viber
            if (mySharedPreViber != null) {
                if ("0".equals(mySharedPreViber)) {
                    Viber.setChecked(true);
                } else {
                    Viber.setChecked(false);
                }
            }
            //短信
            String mySharedPremsg = (String) SharedPreferencesUtils.readObject(MessageAcitivity.this, "msg");   //短信
            if (mySharedPremsg != null) {
                if (mySharedPremsg.equals("0")) {
                    MSG.setChecked(true);
                } else {
                    MSG.setChecked(false);
                }
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy()    {
        super.onDestroy();
    }


    /**
     * 开关设置
     * @param
     * @param
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.e("MSG","--11---b---"+b);
        switch (compoundButton.getId()){
            case R.id.mTosss:   //微信
                if (b) {
                    Log.e("MSG","-----b---"+b);
                    weixin.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "weixinmsg", "0");
                } else {
                    weixin.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "weixinmsg", "1");
                }
                break;
            case R.id.msg_my:   //信息
                if (b) {
                    MSG.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "msg", "0");
                    //申请读短信的权限
                    AndPermission.with(MessageAcitivity.this)
                            .requestCode(READ_MSG_CODE)
                            .permission(Manifest.permission.READ_SMS)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    AndPermission.rationaleDialog(MessageAcitivity.this,rationale).show();
                                }
                            })
                            .callback(permissionListener)
                            .start();
                } else {
                    MSG.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "msg", "1");
                }
                break;
            case R.id.mTogBtsssn2:  //QQ
                if (b) {
                    qqmsg.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "qqmsg", "0");
                } else {
                    qqmsg.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "qqmsg", "1");
                }
                break;
            case R.id.Viber:    //viber
                if (b) {
                    Viber.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Viber", "0");
                } else {
                    Viber.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Viber", "1");
                }
                break;
            case R.id.Twitter:      //Twitter
                if (b) {
                    Twitter.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Twitteraa", "0");
                } else {
                    Twitter.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Twitteraa", "1");
                }
                break;
            case R.id.Facebook:     //facebook
                if (b) {
                    Facebook.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "facebook", "0");
                } else {
                    Facebook.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "facebook", "1");
                }
                break;
            case R.id.Whatsapp: //wahtsapp
                if (b) {
                    Whatsapp.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Whatsapp", "0");
                } else {
                    Whatsapp.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Whatsapp", "1");
                }
                break;
            case R.id.Instagram:    //Instangram
                if (b) {
                    Instagram.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Instagrambutton", "0");
                } else {
                    Instagram.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "Instagrambutton", "1");
                }
                break;
            case R.id.laidiantixing:    //来电
                if (b) {
                    laidiantixing.setChecked(true);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "laidian", "0");
                    //申请读取通讯录的权限
                    AndPermission.with(MessageAcitivity.this)
                            .requestCode(READ_CONTACTS_CODE)
                            .permission(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_SETTINGS)
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                    AndPermission.rationaleDialog(MessageAcitivity.this,rationale).show();
                                }
                            })
                            .callback(permissionListener)
                            .start();

                } else {
                    laidiantixing.setChecked(false);
                    SharedPreferencesUtils.saveObject(MessageAcitivity.this, "laidian", "1");
                }
                break;
        }
    }


    /**
     * 判断数组的包数，，，
     *
     * @param current
     * @param total
     * @return
     */
    private boolean isExit(int current, int total) {
        float a = (float) total / 14;
        //超过4包就退出
        if (current > 4) {
            return false;
        }
        //不足4包的时候，当已发送完就退出
        if (current >= a + 1) {
            return false;
        }
        return true;
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

    //判断辅助功能是否开启
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + NeNotificationService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return false;
    }

    /**
     * 权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode){
                case READ_CONTACTS_CODE:    //获取读取通讯录的权限了

                    break;
                case READ_MSG_CODE: //获取读取短信的权限了

                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case READ_CONTACTS_CODE:    //没有获取读取通讯录的权限了

                    break;
                case READ_MSG_CODE: //没有获取读取短信的权限了

                    break;
            }
            if(AndPermission.hasAlwaysDeniedPermission(MessageAcitivity.this,deniedPermissions)){
                AndPermission.defaultSettingDialog(MessageAcitivity.this,READ_CONTACTS_CODE).show();
            }
        }
    };
}