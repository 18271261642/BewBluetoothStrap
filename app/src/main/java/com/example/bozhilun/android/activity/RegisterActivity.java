package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
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

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by thinkpad on 2017/3/4.
 * 注册页面
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.register_agreement_my)
    TextView registerAgreement;
    @BindView(R.id.username_input)
    TextInputLayout usernameInput;
    @BindView(R.id.textinput_password_regster)
    TextInputLayout textinputPassword;
    @BindView(R.id.code_et_regieg)
    EditText codeEt;
    @BindView(R.id.username_regsiter)
    EditText username;
    @BindView(R.id.password_logonregigter)
    EditText password;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.textinput_code)
    TextInputLayout textinput_code;
    private DialogSubscriber dialogSubscriber;
    private Subscriber subscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private boolean isVal;

    @Override
    protected void initViews() {
        SMSSDK.initSDK(getApplicationContext(), "169d6eaccd2ac", "753aa57cf4a85122671fcc7d4c379ac5");
        tvTitle.setText(R.string.user_regsiter);


        boolean lauage= VerifyUtil.isZh(RegisterActivity.this);
        if(lauage){
            usernameInput.setHint(getResources().getString(R.string.input_name));
        }else{
            usernameInput.setHint(getResources().getString(R.string.input_email));
            sendBtn.setVisibility(View.GONE);
            textinput_code.setVisibility(View.GONE);
        }

        codeEt.setHintTextColor(getResources().getColor(R.color.white));
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("RegisterActivity","------11---注册返回----"+result);
                //Loaddialog.getInstance().dissLoading();
                Gson gson = new Gson();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        BlueUser userInfo = gson.fromJson(jsonObject.getString("userInfo").toString(), BlueUser.class);
                        MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                        Common.userInfo = userInfo;
                        Common.customer_id = userInfo.getUserId();
                        MobclickAgent.onProfileSignIn(Common.customer_id);
                        String pass = password.getText().toString();
                        String usernametxt = username.getText().toString();
                        userInfo.setPassword(Md5Util.Md532(pass));


                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);
                        SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.CUSTOMER_PASSWORD, pass);
                        startActivity(new Intent(RegisterActivity.this, PersonDataActivity.class));
                        finish();
                    } else if ("003".equals(loginResult)) {
                        ToastUtil.showShort(RegisterActivity.this, getString(R.string.yonghuzhej));
                    } else {
                        ToastUtil.showShort(RegisterActivity.this, getString(R.string.regsiter_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //初始化底部声明
        String INSURANCE_STATEMENT = getResources().getString(R.string.register_agreement);
        SpannableString spanStatement = new SpannableString(INSURANCE_STATEMENT);
        ClickableSpan clickStatement = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                //跳转到协议页面
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        spanStatement.setSpan(clickStatement, 0, INSURANCE_STATEMENT.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStatement.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0,
                INSURANCE_STATEMENT.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        registerAgreement.setText(R.string.agree_agreement);
        registerAgreement.append(spanStatement);
        registerAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_regsiter;
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

    @OnClick({R.id.login_btn_reger, R.id.send_btn})
    public void onClick(View view) {
        final String phoneTxt = username.getText().toString();
        switch (view.getId()) {
            case R.id.send_btn:
                if(Common.isFastClick()){
//                    if (TextUtils.isEmpty(phoneTxt) | !VerifyUtil.VerificationPhone(phoneTxt)) {
//                        ToastUtil.showShort(RegisterActivity.this, getResources().getString(R.string.format_is_wrong));
//                    } else {
//                        initTime();
//                        SMSSDK.getVerificationCode("86", phoneTxt);
//                    }

                    if (WatchUtils.isEmpty(phoneTxt)) {
                        ToastUtil.showShort(RegisterActivity.this, getResources().getString(R.string.format_is_wrong));
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

                }
                break;
            case R.id.login_btn_reger:

                boolean lauages= VerifyUtil.isZh(RegisterActivity.this);
                if(lauages){
                    //中文状态为手机号
                    String code = codeEt.getText().toString();
                    String passwordTxt = password.getText().toString();
                    if (TextUtils.isEmpty(phoneTxt) | !VerifyUtil.VerificationPhone(phoneTxt)) {
                        ToastUtil.showShort(RegisterActivity.this, getResources().getString(R.string.format_is_wrong));
                    } else if (TextUtils.isEmpty(passwordTxt) | passwordTxt.length() < 6) {
                        ToastUtil.showShort(RegisterActivity.this, getResources().getString(R.string.not_b_less));
                    } else if (TextUtils.isEmpty(code) | !VerifyUtil.checkNumber(code) | code.length() < 4 | isVal) {
                        SMSSDK.submitVerificationCode("86", phoneTxt, code);
                        ToastUtil.showShort(RegisterActivity.this, getResources().getString(R.string.yonghuzdffhej));
                    } else {
                        registerRemote(phoneTxt, passwordTxt);
                    }
                }else{
                    //其它语言为邮箱
                    try {
                        JSONObject youxiang = new JSONObject();
                        if (!TextUtils.isEmpty(phoneTxt)) {
                            if (ForgetPasswardActivity.isEmail(phoneTxt)) {
                                youxiang.put("phone", phoneTxt);
                                if (!TextUtils.isEmpty(password.getText().toString())) {
                                    if (password.getText().toString().length() < 6) {
                                        Toast.makeText(this, R.string.not_b_less, Toast.LENGTH_SHORT).show();
                                    } else {
                                        youxiang.put("pwd", Md5Util.Md532(password.getText().toString())); //Md5Util.Md532(pass)
                                        youxiang.put("status", "0");
                                        youxiang.put("type", "1");

                                        Log.e("RegisterActivity","-----提交注册---"+youxiang.toString());

                                        //上传服务器
                                        Boolean is = ConnectManages.isNetworkAvailable(RegisterActivity.this);
                                        //如果当前有网络把用户名和密码提交到服务器
                                        if (is) {
                                            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.myHTTPs, youxiang,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            Log.e("RegisterActivity", "----22--注册返回---" + response.toString());
                                                            if (response != null) {
                                                                String shuzhu = response.optString("userInfo").toString();
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(shuzhu);
                                                                    String MYusrid = jsonObject.getString("userId");
                                                                    //实例化SharedPreferences对象（第一步）
                                                                    SharedPreferences mySharedPre = RegisterActivity.this.getSharedPreferences("userId", Activity.MODE_PRIVATE);
                                                                    SharedPreferences.Editor editor = mySharedPre.edit();
                                                                    //用putString的方法保存数据
                                                                    editor.putString("userId", MYusrid);
                                                                    editor.commit();
                                                                    SharedPreferencesUtils.saveObject(RegisterActivity.this, "userId", MYusrid);
                                                                    SharedPreferencesUtils.saveObject(RegisterActivity.this, "userInfo", shuzhu);
                                                                    Toast.makeText(RegisterActivity.this, R.string.tijiao, Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                if (response.optString("resultCode").equals("001")) {
                                                                    Toast.makeText(RegisterActivity.this, R.string.submit_success, Toast.LENGTH_SHORT).show();

                                                                    BlueUser userInfo = new Gson().fromJson(shuzhu, BlueUser.class);
                                                                    MyLogUtil.i("msg", "-userInfo-" + userInfo.toString());
                                                                    Common.userInfo = userInfo;
                                                                    Common.customer_id = userInfo.getUserId();
                                                                    MobclickAgent.onProfileSignIn(Common.customer_id);
                                                                    String pass = password.getText().toString();
                                                                    String usernametxt = username.getText().toString();
                                                                    userInfo.setPassword(Md5Util.Md532(pass));


                                                                    MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                                                                    SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.CUSTOMER_ID, Common.customer_id);
                                                                    SharedPreferencesUtils.setParam(RegisterActivity.this, SharedPreferencesUtils.CUSTOMER_PASSWORD, pass);
                                                                    startActivity(new Intent(RegisterActivity.this, PersonDataActivity.class));


                                                                    finish();
                                                                } else if (response.optString("resultCode").equals("003")) {
                                                                    Toast.makeText(RegisterActivity.this, R.string.yonghuzhej, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }

                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    if(error != null){
                                                        Log.e("RegisterActivity","------注册返回--error----"+error.getMessage());
                                                        Toast.makeText(RegisterActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                                                    }

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
                                            Toast.makeText(RegisterActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, R.string.not_b_less, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, R.string.mailbox_format_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, R.string.write_nickname, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }

                break;
        }
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

    private void registerRemote(String phone, String pass) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("pwd", Md5Util.Md532(pass));
        map.put("status", "0");
        map.put("type", "0");
        String mapjson = gson.toJson(map);
        Log.e("msg","-mapjson-"+mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, RegisterActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.myHTTPs, mapjson);
    }

}
