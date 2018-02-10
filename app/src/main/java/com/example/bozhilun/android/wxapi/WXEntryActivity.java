package com.example.bozhilun.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyl on 2017/2/6.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp instanceof SendAuth.Resp) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;

            //获取微信传回的code
            String code = newResp.code;

            Log.e("WX","------code--"+code);



/**
 * 通过这个获取信息
 * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
 * 获得opid和access_token
 *
 */
            //判断网络是否连接
            Boolean is = ConnectManages.isNetworkAvailable(WXEntryActivity.this);
            if (is == true) {
                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx70148753927bd916&secret=0da2a2a2c3e35d25460c8f6bb01cd876&code=" + code + "&grant_type=authorization_code", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("WX","-----response----"+response.toString());
                                try {
                                    JSONObject RES = new JSONObject(response.toString());
                                    String ACCESS_TOKEN = RES.getString("access_token");
                                    String OPENID = RES.getString("openid");
                                    /**
                                     * 获取用户信息
                                     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                                     */
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://api.weixin.qq.com/sns/userinfo?access_token=" + ACCESS_TOKEN + "&openid=" + OPENID, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.e("WX","------用户信息---"+response.toString());
                                                    /**
                                                     * 获取用户信息
                                                     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
                                                     */
                                                    try {
                                                        JSONObject xinxi = new JSONObject(response.toString());
                                                        JSONObject weixin = new JSONObject();
                                                        weixin.put("thirdId", xinxi.getString("openid"));
                                                        weixin.put("thirdType", "3");
                                                        weixin.put("image", xinxi.getString("headimgurl"));
                                                        if (xinxi.getString("sex").equals(1)) {
                                                            weixin.put("sex", "M");//男
                                                        } else {
                                                            weixin.put("sex", "F");//女
                                                        }
                                                        weixin.put("nickName", xinxi.getString("nickname"));
                                                        /**
                                                         * 提交到服务器
                                                         */
                                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.disanfang, weixin,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        if (response.optString("resultCode").equals("001")) {
                                                                            try {
                                                                                String shuzhu = response.optString("userInfo");
                                                                                JSONObject jsonObject = new JSONObject(shuzhu);
                                                                                String userId = jsonObject.getString("userId");
                                                                                Gson gson = new Gson();
                                                                                BlueUser userInfo = gson.fromJson(shuzhu, BlueUser.class);
                                                                                Common.userInfo = userInfo;
                                                                                Common.customer_id = userId;
                                                                                //保存userid
                                                                                SharedPreferencesUtils.saveObject(WXEntryActivity.this, "userId", userInfo.getUserId());
                                                                                MobclickAgent.onProfileSignIn(Common.customer_id);
                                                                                MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                                                                                startActivity(new Intent(WXEntryActivity.this, NewSearchActivity.class));
                                                                                finish();
                                                                            } catch (Exception E) {
                                                                                E.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
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
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WXEntryActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API
        api = WXAPIFactory.createWXAPI(this, "APP_ID");
        api.handleIntent(getIntent(), this);
    }


}
