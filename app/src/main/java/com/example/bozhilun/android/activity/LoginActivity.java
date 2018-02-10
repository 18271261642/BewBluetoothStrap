package com.example.bozhilun.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.view.LoginWaveView;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.view.PromptDialog;
import com.example.bozhilun.android.xinlangweibo.AccessTokenKeeper;
import com.example.bozhilun.android.xinlangweibo.SinaUserInfo;
import com.example.bozhilun.android.xinlangweibo.UsersAPI;
import com.google.gson.Gson;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.twitter.Twitter;
import m.framework.utils.UIHandler;

/**
 * Created by thinkpad on 2017/3/3.
 */

public class LoginActivity extends BaseActivity implements IWXAPIEventHandler, Callback, PlatformActionListener {
    @BindView(R.id.login_visitorTv)
    TextView loginVisitorTv;
    //波浪形曲线
    @BindView(R.id.login_waveView)
    LoginWaveView loginWaveView;

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        Log.i(TAG, "onComplete执行了");
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            login(platform.getName(), platform.getDb().getUserId(), res);
        }
        Log.i(TAG, res.toString());
    }

    public void onError(Platform platform, int action, Throwable t) {
        Log.i(TAG, "onError执行了");
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        Log.i(TAG, "onCancel执行了");
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId,
                       HashMap<String, Object> userInfo) {
        Log.i(TAG, "login执行了");
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @BindView(R.id.ll_bottom_tabaa)
    LinearLayout guolei;//在国内
    @BindView(R.id.ll_bottom_tabguowai)
    LinearLayout guiwai;//在国外

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.username_input_logon)
    TextInputLayout usernameInput;
    @BindView(R.id.password_logon)
    EditText password;
    @BindView(R.id.textinput_password)
    TextInputLayout textinputPassword;
    @BindView(R.id.xinlang_iv)
    RelativeLayout weiboIv;
    @BindView(R.id.qq_iv)
    RelativeLayout qqIv;
    @BindView(R.id.weixin_iv)
    RelativeLayout weixinIv;
    private static final String TAG = "LoginActivity";
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    //qq
    private Tencent mTencent;
    String openID;//唯一标识符 1105653402
//     private final String APP_ID = "101357650";// 测试时使用，真正发布的时候要换成自己的APP_ID
    private final String APP_ID = "101357650";// 测试时使用，真正发布的时候要换成自己的APP_ID
    public static String mAppid;

    private IUiListener loginListener;
    // private CallbackManager callbackManager;
    JSONObject jsonObject;
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    //微信
    private IWXAPI api;

    private Platform platform11;

    private final static String SWB_APP_ID = "25665906";
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private Oauth2AccessToken accessToken;
    private TextView userinfo_tv;
    private SinaUserInfo userInfo;
    private IWeiboShareAPI weiboShareAPI;
    public static final String SWB_REDIRECT_URL = "http://www.sina.com";//新浪微博回调页面
    public static final String SWB_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";


    private static final int REQUEST_CODE_WRITESDCARD = 10001;

    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("ServiceCast")
    @Override
    protected void initViews() {
        loginWaveView.startMove();  //波浪线贝塞尔曲线
        ShareSDK.initSDK(LoginActivity.this);
        weiboShareAPI = WeiboShareSDK.createWeiboAPI(LoginActivity.this, SWB_APP_ID);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                //Loaddialog.getInstance().dissLoading();
                Log.e("LoainActivity", "-----loginresult---" + result);
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        BlueUser userInfo = gson.fromJson(jsonObject.getString("userInfo").toString(), BlueUser.class);
                        MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                        Common.userInfo = userInfo;
                        Common.customer_id = userInfo.getUserId();
                        //保存userid
                        SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                        SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", jsonObject.getString("userInfo").toString());
                        MobclickAgent.onProfileSignIn(Common.customer_id);
                        //缓存fragment通知更新姓名图像
                        /*Intent intent2 = new Intent();
                        //设置Intent的Action属性
                        intent2.setAction("CHEN.COM.UPDATEPERSONDATA_MINE");
                        intent2.putExtra("realname", userInfo.getRealName());
                        intent2.putExtra("headimgUrl", userInfo.getHeadimgurl());
                        //发送广播,改变姓名显示
                        sendBroadcast(intent2);*/
                        String pass = password.getText().toString();
                        String usernametxt = username.getText().toString();
                        userInfo.setPassword(Md5Util.Md532(pass));
                        // userInfo.setPhone(usernametxt);
                        //MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);
                        SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.CUSTOMER_PASSWORD, pass);
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        //startActivity(new Intent(LoginActivity.this, SelectDeviceActivity.class));
                        startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                        finish();
                    } else if (loginResult.equals("003")) {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.yonghuzhej));
                    } else if (loginResult.equals("006")) {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.miamacuo));
                    } else {
                        ToastUtil.showShort(LoginActivity.this, getString(R.string.miamacuo));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        boolean lauage = VerifyUtil.isZh(LoginActivity.this);
        if (lauage) {
            guolei.setVisibility(View.VISIBLE);
            guiwai.setVisibility(View.GONE);
            usernameInput.setHint(getResources().getString(R.string.input_name));
        } else {
            guiwai.setVisibility(View.VISIBLE);
            guolei.setVisibility(View.GONE);
            usernameInput.setHint(getResources().getString(R.string.input_email));
        }
        //请求读写SD卡的权限
        AndPermission.with(LoginActivity.this)
                .requestCode(REQUEST_CODE_WRITESDCARD)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
                .callback(permissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(LoginActivity.this, rationale).show();
                    }
                }).start();


        //判断蓝牙是否开启
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter =  bluetoothManager.getAdapter();
        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled()){
            turnOnBlue();
        }
//        //当用户第一次打开APP时提醒打开通知的功能
//        String loginAlert = (String) SharedPreferencesUtils.getParam(LoginActivity.this,"loginalert","");
//        if(WatchUtils.isEmpty(loginAlert)){
//            notificationAlertDialog();  //提醒用户打开通知功能
//        }

    }

    //提醒用户打开通知功能
    private void notificationAlertDialog() {
        //加入提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getResources().getString(R.string.prompt));
        builder.setMessage(getResources().getString(R.string.xiaoxitixing));
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {//到辅助设置页面，收到打开提醒功能
                    SharedPreferencesUtils.setParam(LoginActivity.this,"loginalert","on");
                    dialog.dismiss();
                    Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivityForResult(intentr, 0);

                    //再引导用户打开通知
                    Intent intentrs = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intentrs, 1);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferencesUtils.setParam(LoginActivity.this,"loginalert","on");
            }
        });
        builder.create().show();

    }

    private void turnOnBlue(){
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                1111);
        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                1112);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.transparent;
    }

    @OnClick({R.id.register_btn, R.id.forget_tv, R.id.login_btn, R.id.xinlang_iv, R.id.qq_iv, R.id.weixin_iv, R.id.fecebook_longin, R.id.google_longin, R.id.twitter_longin, R.id.login_visitorTv})
    public void onClick(View view) {
        Context context = view.getContext();
        switch (view.getId()) {
            case R.id.fecebook_longin://登录fecebook
                /***** FaceBook 自定义按钮登录**/
                authorize(new Facebook(this), 0);
                break;
            case R.id.google_longin://登录google
                authorize(new GooglePlus(this), 1);
                break;
            case R.id.twitter_longin://登录twitter
                try {
                    QLogin("1");
                } catch (Exception E) {
                    E.printStackTrace();
                }
                break;
            case R.id.register_btn://注册
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.forget_tv://忘记密码
                startActivity(new Intent(LoginActivity.this, ForgetPasswardActivity.class));
                break;
            case R.id.login_btn:
                boolean lauage = VerifyUtil.isZh(LoginActivity.this);
                if (lauage) {
                    //登录时判断
                    String pass = password.getText().toString();
                    String usernametxt = username.getText().toString();
                    if (WatchUtils.isEmpty(pass) && pass.length() < 6) {
                        ToastUtil.showShort(this, "密码格式不对 ！");
                    } else if(WatchUtils.isEmpty(usernametxt)){
                        ToastUtil.showToast(this,"密码不能为空!");
                    }
                    else {
                        loginRemote();
                    }
                } else {
                    //登录时判断
                    String pass = password.getText().toString();
                    String usernametxt = username.getText().toString();
                    if (usernametxt.length() < 6) {
                        ToastUtil.showShort(this, getResources().getString(R.string.user_name_format));
                    } else if (!VerifyUtil.checkEmail(usernametxt)) {
                        ToastUtil.showShort(this, getResources().getString(R.string.mailbox_format_error));
                    } else if (pass.length() < 6) {
                        ToastUtil.showShort(this, getResources().getString(R.string.not_b_less));
                    } else {
                        loginRemote();
                    }

                }


                break;
            case R.id.xinlang_iv://f新浪登录
                authInfo = new AuthInfo(context, SWB_APP_ID, SWB_REDIRECT_URL, SWB_SCOPE);
                ssoHandler = new SsoHandler(LoginActivity.this, authInfo);
                ssoHandler.authorize(new AuthListener());


                break;
            case R.id.qq_iv://QQ登录
                ToastUtil.showToast(LoginActivity.this,"QQ登录");
                if(mTencent == null)
                    ToastUtil.showToast(LoginActivity.this,"Tencent为null");
                if(!mTencent.isSessionValid()){
                    mTencent.login(LoginActivity.this, "all", loginListener);
                }

                loginListener = new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        JSONObject jsonObject = (JSONObject) o;
                        Log.e(TAG,"-----QQ登录返回----"+o.toString());
                        try {
                            String accessToken = jsonObject.getString("access_token");
                            String expires = jsonObject.getString("expires_in");
                            openID = jsonObject.getString("openid");
                            mTencent.setAccessToken(accessToken, expires);
                            mTencent.setOpenId(openID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.i("=====", "===失败");
                    }

                    @Override
                    public void onCancel() {
                        Log.i("=====", "===取消");
                    }
                };
                break;
            case R.id.weixin_iv://微信登录
                //api注册
                api = WXAPIFactory.createWXAPI(this, "wx70148753927bd916", true);
                api.registerApp("wx70148753927bd916");
                SendAuth.Req req = new SendAuth.Req();
                //授权读取用户信息
                req.scope = "snsapi_userinfo";
                //自定义信息
                req.state = "wechat_sdk_demo_test";
                //向微信发送请求
                api.sendReq(req);
                break;
            case R.id.login_visitorTv:  //游客登录
                final PromptDialog pd = new PromptDialog(LoginActivity.this);
                pd.show();
                pd.setTitle(getResources().getString(R.string.prompt));
                pd.setContent(getResources().getString(R.string.login_alert));
                pd.setleftText(getResources().getString(R.string.cancle));
                pd.setrightText(getResources().getString(R.string.confirm));
                pd.setListener(new PromptDialog.OnPromptDialogListener() {
                    @Override
                    public void leftClick(int code) {
                        pd.dismiss();
                    }

                    @Override
                    public void rightClick(int code) {
                        pd.dismiss();

                        Gson gson = new Gson();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("phone", "bozlun888@gmail.com");
                        map.put("pwd", Md5Util.Md532("e10adc3949ba59abbe56e057f20f883e"));
                        String mapjson = gson.toJson(map);
                        Log.e("msg", "-mapjson-" + mapjson);
                        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, LoginActivity.this);
                        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.logon, mapjson);

                        SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                        SharedPreferences.Editor editor = userSettings.edit();
                        editor.putInt("id", 0);
                        editor.commit();

                    }
                });
                break;
        }
    }

    private void authorize(Platform plat, final int id) {
        if (plat == null) {
            return;
        }
        //判断指定平台是否已经完成授权
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (userId != null) {
                return;
            }
        }
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {// TODO Auto-generated method stub
                Log.i("platform", "LLLerror");
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                Log.i("platform", "LLLcomplete");
                //成功得到用户信息
                String userId = arg0.getDb().getUserId();
                String userName = arg0.getDb().getUserName();
                String token = arg0.getDb().getToken();
                String userIcon = arg0.getDb().getUserIcon();
                //有效时期
                long expiresTime = arg0.getDb().getExpiresTime();
                try {
                    JSONObject shuju = new JSONObject();
                    shuju.put("thirdId", userId);
                    shuju.put("thirdType", "5");
                    shuju.put("image", userIcon);
                    if (arg0.getDb().getUserGender().equals("f")) {
                        shuju.put("sex", "F");
                    } else {
                        shuju.put("sex", "M");
                    }
                    shuju.put("nickName", userName);
                    if (id == 0) {
                        shagchuanfuwuqi(shuju);//facebook
                    } else {
                        shangchuangogle(shuju, "2");
                    }
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        plat.SSOSetting(true); // true不使用SSO授权，false使用SSO授权
        plat.showUser(null);  //获取用户资料
    }


    /**
     * 登录到facekoob
     */
    public void shagchuanfuwuqi(JSONObject ASD) {
        //判断网络是否连接
        Boolean is = ConnectManages.isNetworkAvailable(LoginActivity.this);
        if (is == true) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, ASD,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String shuzhu = response.optString("userInfo");
                            if (response.optString("resultCode").equals("001")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(shuzhu);
                                    String userId = jsonObject.getString("userId");
                                    Gson gson = new Gson();
                                    BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                    Common.userInfo = userInfo;
                                    Common.customer_id = userId;
                                    //保存userid
                                    SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                                    SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", shuzhu);
                                    MobclickAgent.onProfileSignIn(Common.customer_id);

//                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (response.optString("resultCode").equals("003")) {
                                Toast.makeText(LoginActivity.this, R.string.yonghuzhej, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (response.optString("resultCode").equals("006")) {
                                Toast.makeText(LoginActivity.this, R.string.miamacuo, Toast.LENGTH_SHORT).show();

                            }/*else{Toast.makeText(LoginActivity.this,R.string.miamacuo,Toast.LENGTH_SHORT).show();}*/
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    return headers;
                }
            };
            requestQueue.add(jsonRequest);
        } else {
            Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新
        UpdateManager updateManager = new UpdateManager(LoginActivity.this, URLs.HTTPs + URLs.getvision);
        updateManager.checkForUpdate(false);
    }

    /**
     * 到google
     */
    public void shangchuangogle(JSONObject ASD, final String aaa) {
        //判断网络是否连接
        Boolean is = ConnectManages.isNetworkAvailable(LoginActivity.this);
        if (is == true) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, ASD,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String shuzhu = response.optString("userInfo");
                            if (response.optString("resultCode").equals("001")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(shuzhu);
                                    String userId = jsonObject.getString("userId");
                                    Gson gson = new Gson();
                                    BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                    Common.userInfo = userInfo;
                                    Common.customer_id = userId;
                                    //保存userid
                                    SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                                    SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", shuzhu);
                                    MobclickAgent.onProfileSignIn(Common.customer_id);

//                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (response.optString("resultCode").equals("003")) {
                                Toast.makeText(LoginActivity.this, R.string.yonghuzhej, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (response.optString("resultCode").equals("006")) {
                                Toast.makeText(LoginActivity.this, R.string.tianxie, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.miamacuo, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    return headers;
                }
            };
            requestQueue.add(jsonRequest);
        } else {
            Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加授权
     * Twitter
     */

    public void QLogin(final String AAA) {
        platform11 = ShareSDK.getPlatform(Twitter.NAME);
        platform11.authorize();
        platform11.showUser(null);//必须要加的要不然不行！这个才是授权的！
        TelephonyManager tm = (TelephonyManager) LoginActivity.this.getSystemService(TELEPHONY_SERVICE);
        String imi = tm.getDeviceId();
        platform11.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onError(Platform platform11, int arg1, Throwable arg2) {
//弹出失败窗口
            }

            @Override
            public void onComplete(Platform platform11, int arg1, HashMap<String, Object> arg2) {
//成功得到用户信息
                String userId = platform11.getDb().getUserId();
                String userName = platform11.getDb().getUserName();
                String token = platform11.getDb().getToken();
                String userIcon = platform11.getDb().getUserIcon();
//有效时期
                long expiresTime = platform11.getDb().getExpiresTime();
                //  SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
              /*  System.out.println("用户ID为："+userId);
                System.out.println("用户名称为："+userName);
                System.out.println("token     "+token);
                System.out.println("userIcon     "+userIcon);
                System.out.println("getUserGender "+platform11.getDb().getUserGender());*/

                if (AAA.equals("1")) {
                    //twitter+上传到服务器
                    try {
                        JSONObject shuju = new JSONObject();
                        shuju.put("thirdId", userId);
                        shuju.put("thirdType", "7");
                        shuju.put("image", userIcon);
                        shuju.put("sex", "F");
                        shuju.put("nickName", userName);
                        shangchuangogle(shuju, "1");
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
    }


    /**
     * 微信请求回调接口
     */
    public void onReq(BaseReq req) {
        Log.i(TAG, "onReq");
    }

    /**
     * 微信请求响应回调接口
     */
    public void onResp(BaseResp resp) {
        Log.i(TAG, "onResp");
        SendAuth.Resp sendAuthResp = (SendAuth.Resp) resp;// 用于分享时不要有这个，不能强转
        String code = sendAuthResp.code;
        Toast.makeText(this, "errCode = " + code, Toast.LENGTH_SHORT).show();
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
        }
        int errCode = resp.errCode;
    }


    //用户手机登录
    private void loginRemote() {
        String phone = username.getText().toString();
        String pass = password.getText().toString();
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("pwd", Md5Util.Md532(pass));
        String mapjson = gson.toJson(map);
        Log.e("msg", "-mapjson-" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, LoginActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.logon, mapjson);

        SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putInt("id", 0);
        editor.commit();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"----onActivityResult------"+requestCode + "---resultCode--"+resultCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        //  callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("==================", "===============requestCode:" + requestCode);
        Log.i("==================", "===============resultCode:" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                mTencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                mTencent.handleResultData(data, loginListener);
                UserInfo info = new UserInfo(this, mTencent.getQQToken());
                info.getUserInfo(new IUiListener() {
                                     @Override
                                     public void onComplete(Object o) {
                                         JSONObject info = (JSONObject) o;
                                         Log.e("=====", info.toString());
                                         try {
                                             String nickName = info.getString("nickname");//获取用户昵称
                                             String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                                             String qqxingbie = info.getString("gender");//性别
                                             //判断网络是否连接
                                             Boolean is = ConnectManages.isNetworkAvailable(LoginActivity.this);
                                             if (is == true) {
                                                 RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                 try {
                                                     jsonObject = new JSONObject();
                                                     jsonObject.put("thirdId", openID);
                                                     jsonObject.put("nickName", nickName);
                                                     jsonObject.put("thirdType", 4);//QQ登录
                                                     jsonObject.put("image", iconUrl);//头像地址
                                                     //性别
                                                     if (qqxingbie.equals("男")) {
                                                         jsonObject.put("sex", "M");
                                                     } else {
                                                         jsonObject.put("sex", "F");
                                                     }
                                                     //姓名
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }
                                                 JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, jsonObject,
                                                         new Response.Listener<JSONObject>() {
                                                             @Override
                                                             public void onResponse(JSONObject response) {
                                                                 String shuzhu = response.optString("userInfo").toString();
                                                                 if (response.optString("resultCode").toString().equals("001")) {
                                                                     try {
                                                                         JSONObject jsonObject = new JSONObject(shuzhu);
                                                                         String userId = jsonObject.getString("userId");
                                                                         Gson gson = new Gson();
                                                                         BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                                                         Common.userInfo = userInfo;
                                                                         Common.customer_id = userId;
                                                                         //保存userid
                                                                         SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                                                                         SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", shuzhu);
                                                                         MobclickAgent.onProfileSignIn(Common.customer_id);
                                                                         //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                         startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                                                                         SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                                                                         SharedPreferences.Editor editor = userSettings.edit();
                                                                         editor.putInt("id", 1);
                                                                         editor.commit();

                                                                         finish();
                                                                     } catch (Exception E) {
                                                                         E.printStackTrace();
                                                                     }
                                                                 }
                                                             }
                                                         }, new Response.ErrorListener() {
                                                     @Override
                                                     public void onErrorResponse(VolleyError error) {
                                                     }
                                                 }) {
                                                     @Override
                                                     public Map<String, String> getHeaders() {
                                                         HashMap<String, String> headers = new HashMap<String, String>();
                                                         headers.put("Accept", "application/json");
                                                         headers.put("Content-Type", "application/json; charset=UTF-8");
                                                         return headers;
                                                     }
                                                 };
                                                 requestQueue.add(jsonRequest);
                                             } else {
                                                 Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                                             }
                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }
                                     }

                                     @Override
                                     public void onError(UiError uiError) {

                                     }

                                     @Override
                                     public void onCancel() {
                                     }
                                 }
                );
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTencent = Tencent.createInstance(APP_ID,MyApp.getContext());
    }


    /***
     * *****
     * **微信登录
     * ******
     * ***
     * ***/
    /**********************/
    public class AuthListener implements WeiboAuthListener {
        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            Toast.makeText(LoginActivity.this, R.string.shouquanqu, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onComplete(Bundle values) {
            // TODO Auto-generated method stub
            accessToken = Oauth2AccessToken.parseAccessToken(values); // 从Bundle中解析Token
            String phoneNum = accessToken.getPhoneNum();// 从这里获取用户输入的 电话号码信息
            if (accessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, accessToken); // 保存Token
                Toast.makeText(LoginActivity.this, R.string.shouquancg, Toast.LENGTH_SHORT).show();

                SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                SharedPreferences.Editor editor = userSettings.edit();
                editor.putInt("id", 1);
                editor.commit();

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = String.valueOf(R.string.shouquanshib);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "Obtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    UsersAPI usersAPI = new UsersAPI(LoginActivity.this, SWB_APP_ID, accessToken);
                    usersAPI.show(Long.valueOf(accessToken.getUid()), new SinaRequestListener());
                }
            }).start();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            // TODO Auto-generated method stub
            Toast.makeText(LoginActivity.this, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public class SinaRequestListener implements RequestListener { //新浪微博请求接口
        @Override
        public void onComplete(String response) {
            // TODO Auto-generated method stub
            try {
                System.out.print("xinlang" + response.toString());
                JSONObject jsonObject = new JSONObject(response);
                String idStr = jsonObject.getString("idstr");// 唯一标识符(uid)
                String name = jsonObject.getString("name");// 姓名
                String avatarHd = jsonObject.getString("avatar_hd");// 头像
                String sex = jsonObject.getString("gender");// 性别
                userInfo = new SinaUserInfo();
                userInfo.setUid(idStr);
                userInfo.setName(name);
                userInfo.setAvatarHd(avatarHd);
                userInfo.setSEX(sex);
                Message message = Message.obtain();
                message.what = 1;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            // TODO Auto-generated method stub
            Toast.makeText(LoginActivity.this, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //上传到服务器
                try {
                    //判断网络是否连接
//                    Boolean is=   ConnectManages.isNetworkAvailable(LoginActivity.this);
//                    if(is==true) {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    try {
                        jsonObject = new JSONObject();
                        jsonObject.put("thirdId", userInfo.getUid());
                        jsonObject.put("thirdType", 4);//微博
                        jsonObject.put("image", userInfo.getAvatarHd());//头像地址
                        if (userInfo.getSEX().equals("m")) {
                            jsonObject.put("sex", "M");
                        } else {
                            jsonObject.put("sex", "F");
                        }//性别
                        jsonObject.put("nickName", userInfo.getName()); //姓名
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.print("yyyyyyyyyyyyyyyyyrrrrr" + response);
                                    Log.d("eeeee", response.toString());
                                    String shuzhu = response.optString("userInfo").toString();
                                    if (response.optString("resultCode").toString().equals("001")) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(shuzhu);
                                            String userId = jsonObject.getString("userId");
                                            Gson gson = new Gson();
                                            BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                            Common.userInfo = userInfo;
                                            Common.customer_id = userId;
                                            //保存userid
                                            SharedPreferencesUtils.saveObject(LoginActivity.this, "userId", userInfo.getUserId());
                                            SharedPreferencesUtils.saveObject(LoginActivity.this, "userInfo", userInfo);
                                            MobclickAgent.onProfileSignIn(Common.customer_id);
                                            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            startActivity(new Intent(LoginActivity.this, NewSearchActivity.class));
                                            SharedPreferences userSettings = getSharedPreferences("Login_id", 0);
                                            SharedPreferences.Editor editor = userSettings.edit();
                                            editor.putInt("id", 1);
                                            editor.commit();

                                            finish();
                                        } catch (Exception E) {
                                            E.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LoginActivity", "---sina--error--" + error.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Accept", "application/json");
                            headers.put("Content-Type", "application/json; charset=UTF-8");
                            return headers;
                        }
                    };
                    requestQueue.add(jsonRequest);
                    //}
//                    else{
//                        Toast.makeText(LoginActivity.this,R.string.wangluo,Toast.LENGTH_SHORT).show();}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception E) {
            E.printStackTrace();
        }
        loginWaveView.stopMove();
    }

    public long exitTime; // 储存点击退出时间

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.showToast(LoginActivity.this, "再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    // 全局推出
                   // removeAllActivity();
                    MyApp.getInstance().removeALLActivity();
                    return true;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //权限回调
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_WRITESDCARD:

                    break;

            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_WRITESDCARD:

                    break;

            }
            AndPermission.hasAlwaysDeniedPermission(LoginActivity.this, deniedPermissions);
        }
    };
}
