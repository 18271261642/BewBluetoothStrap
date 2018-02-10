package com.example.bozhilun.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.TimerCount;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by admin on 2017/4/21.
 * 忘记密码
 */

public class ForgetPasswardActivity extends BaseActivity {

    @BindView(R.id.username_forget) EditText username;//用户名
    @BindView(R.id.password_forget) EditText password;//密码
    @BindView(R.id.send_btn_forget) Button sendBtn;//发送按钮
    @BindView(R.id.code_et_forget)EditText yuanzhengma;//验证码
    @BindView(R.id.username_input_forget)
    TextInputLayout textInputLayoutname;
    private JSONObject jsonObject;
    private DialogSubscriber dialogSubscriber;
    private Subscriber subscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private boolean  isVal;
    private String code;//验证码
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        }else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void initViews() {
        SMSSDK.initSDK(this, "169d6eaccd2ac","753aa57cf4a85122671fcc7d4c379ac5");
        boolean lauage=VerifyUtil.isZh(ForgetPasswardActivity.this);
        if(lauage){
            textInputLayoutname.setHint(getResources().getString(R.string.input_name));
        }else{
            textInputLayoutname.setHint(getResources().getString(R.string.input_email));
            sendBtn.setTextSize(5);
        }

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                //Loaddialog.getInstance().dissLoading();
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        Toast.makeText(ForgetPasswardActivity.this,getResources().getString(R.string.change_password),Toast.LENGTH_SHORT).show();
                  /*      BlueUser userInfo = gson.fromJson(jsonObject.getString("userInfo").toString(), BlueUser.class);
                        MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                        B18iCommon.userInfo = userInfo;
                        B18iCommon.customer_id = userInfo.getUserId();
                        MobclickAgent.onProfileSignIn(B18iCommon.customer_id);
                        String pass = password.getText().toString();
                        String usernametxt = username.getText().toString();
                        userInfo.setPassword(Md5Util.Md532(pass));


                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        SharedPreferencesUtils.setParam(ForgetPasswardActivity.this, SharedPreferencesUtils.CUSTOMER_ID, B18iCommon.customer_id);
                        SharedPreferencesUtils.setParam(ForgetPasswardActivity.this, SharedPreferencesUtils.CUSTOMER_PASSWORD, pass);*/
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    @OnClick({R.id.login_btn__forget, R.id.send_btn_forget})
    public void onClick(View view) {
        final String phoneTxt = username.getText().toString();
        switch (view.getId()) {
            case R.id.send_btn_forget:
                //判断当前网络
                Boolean isbb=   ConnectManages.isNetworkAvailable(ForgetPasswardActivity.this);
                if(isbb==true) {
                    boolean lauages=VerifyUtil.isZh(ForgetPasswardActivity.this);
                    if(lauages){
                        if (TextUtils.isEmpty(phoneTxt) | !VerifyUtil.VerificationPhone(phoneTxt)) {
                            ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.format_is_wrong));
                        } else {
                            initTime();
                            SMSSDK.getVerificationCode("86", phoneTxt);
                        }
                        EventHandler eventHandler = new EventHandler() {
                            @Override
                            public void afterEvent(int event, int result, Object data) {
                                Message msg = new Message();
                                msg.arg1 = event;
                                msg.arg2 = result;
                                msg.obj = data;
                                handler.sendMessage(msg);
                            }
                        };
                        SMSSDK.registerEventHandler(eventHandler);

                    }else{
                        //邮箱
                        if(!TextUtils.isEmpty(username.getText().toString())){
                            if(isEmail(username.getText().toString())==true){
                                sendBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.sms_verification));
                                sendBtn.setTextColor(Color.parseColor("#2b2b2b"));
                                TimerCount timer = new TimerCount(60000, 1000, sendBtn);
                                timer.start();
                                //判断网络是否连接
                                Boolean is=   ConnectManages.isNetworkAvailable(ForgetPasswardActivity.this);
                                if(is==true){
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    try{
                                        jsonObject = new JSONObject();
                                        jsonObject.put("phone",username.getText().toString());

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                    System.out.print("调用次数"+jsonObject.toString());
                                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST,URLs.HTTPs+URLs.sendEmail, jsonObject,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    System.out.print("yujjgnsdmfdsb"+response.toString());
                                                    Log.d("dfg",response.toString());
                                                    if (response.optString("resultCode").equals("001")){
                                                        code=response.optString("code");
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.wangluo));
                                        }
                                    })
                                    {
                                        @Override
                                        public Map<String, String> getHeaders() {
                                            HashMap<String, String> headers = new HashMap<String, String>();
                                            headers.put("Accept", "application/json");
                                            headers.put("Content-Type", "application/json; charset=UTF-8");
                                            return headers;
                                        }
                                    };
                                    requestQueue.add(jsonRequest);
                                }else{
                                    ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.wangluo));
                                }
                            }else{
                                ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.tianxie));
                            }

                        }else{
                            ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.write_nickname));
                        }
                    }
                }else{
                    ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.wangluo));
                }




                break;
            case R.id.login_btn__forget:

                boolean lauage=VerifyUtil.isZh(ForgetPasswardActivity.this);
                if(lauage){
                    String passwordTxt = password.getText().toString();
//                    if (TextUtils.isEmpty(phoneTxt) | !VerifyUtil.VerificationPhone(phoneTxt)) {
//                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.format_is_wrong));
//                    } else if (TextUtils.isEmpty(passwordTxt) | passwordTxt.length() < 6) {
//                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.not_b_less));
//                    } else if (TextUtils.isEmpty(yuanzhengma.getText().toString()) | !VerifyUtil.checkNumber(yuanzhengma.getText().toString()) | yuanzhengma.getText().toString().length() < 4 | isVal) {
//                        SMSSDK.submitVerificationCode("86", phoneTxt, code);
//                        ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.yonghuzdffhej));
//                    } else {
//                        registerRemote(phoneTxt, passwordTxt);
//                    }

                    if(WatchUtils.isEmpty(phoneTxt)){
                        ToastUtil.showToast(ForgetPasswardActivity.this,getResources().getString(R.string.user_name_format));
                    }else if (TextUtils.isEmpty(passwordTxt) | passwordTxt.length() < 6) {
                        ToastUtil.showShort(ForgetPasswardActivity.this, getResources().getString(R.string.not_b_less));
                    } else if (TextUtils.isEmpty(yuanzhengma.getText().toString()) | !VerifyUtil.checkNumber(yuanzhengma.getText().toString()) | yuanzhengma.getText().toString().length() < 4 | isVal) {
                        SMSSDK.submitVerificationCode("86", phoneTxt, code);
                        ToastUtil.showShort(ForgetPasswardActivity.this,getResources().getString(R.string.yonghuzdffhej));
                    } else {
                        registerRemote(phoneTxt, passwordTxt);
                    }
                }
                //国外为邮箱
                else{
                    String usernametxt = username.getText().toString();
                    String passwordTxt = password.getText().toString();
                    if (TextUtils.isEmpty(usernametxt)|!VerifyUtil.checkEmail(usernametxt)) {
                        ToastUtil.showShort(this, getResources().getString(R.string.mailbox_format_error));
                    } else if (passwordTxt.length() < 6) {
                        ToastUtil.showShort(this,getResources().getString(R.string.not_b_less));
                    } else if(TextUtils.isEmpty(passwordTxt)){
                        ToastUtil.showShort(this,getResources().getString(R.string.input_password));
                    }else if(TextUtils.isEmpty(yuanzhengma.getText().toString())){
                        ToastUtil.showShort(this,getResources().getString(R.string.input_code));
                    }else if(!code.equals(yuanzhengma.getText().toString())){
                        ToastUtil.showShort(this,getResources().getString(R.string.yonghuzdffhej));
                    }else{
                        registerRemote(usernametxt, passwordTxt);
                    }
                }


                break;
        }}


    /*
 * 是否email
 */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }
    private void registerRemote(String phone, String pass) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("pwd", Md5Util.Md532(pass));
        map.put("status", "0");
        map.put("type", "0");
        String mapjson = gson.toJson(map);
        //Log.i("msg","-mapjson-"+mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ForgetPasswardActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.xiugaimima, mapjson);
        /*Intent intent = new Intent(ForgetPasswardActivity.this, MainActivity.class);
        startActivity(intent);*/
    }
    private void initTime() {
        final int countTime = 60;
        sendBtn.setText(getResources().getString(R.string.resend)+"(" + countTime + "s)");
        sendBtn.setClickable(false);
        subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                if (integer == 0) {
                    //isTime = false;
                    sendBtn.setText(getResources().getString(R.string.resend));
                    sendBtn.setClickable(true);
                } else {
                    sendBtn.setText(getResources().getString(R.string.resend)+"(" + integer + "s)");
                }
            }
        };
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
                        return countTime - increaseTime.intValue();
                    }
                })
                .take(countTime + 1)
                .subscribe(subscriber);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    isVal = true;
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), R.string.yanzhengma,
                            Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), R.string.guojia,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };


    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mymobiles){
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mymobiles)) return false;
        else return mymobiles.matches(telRegex);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_forgetpassward;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
