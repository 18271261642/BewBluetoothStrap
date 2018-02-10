package com.example.bozhilun.android.activity.wylactivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/1/6.
 */
public class WenxinBandActivity extends BaseActivity{
    @BindView(R.id.tv_title)
    TextView  weixin;
    @BindView(R.id.xinxin_bangdin)
     Button bangding;//绑定
    @BindView(R.id.jieruweixin)
    TextView jieruweixin;
    public String mDeviceName,mDeviceAddress,mac;//蓝牙名字和地址
    private RequestQueue requestQueue;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wenxin;
    }

    protected void initViews() {
        weixin.setText(getResources().getString(R.string.weixinpaizhao));
        bangding .getBackground().setAlpha(170);
        try{
            //这里要判断是否有绑定过的设备
            //说明绑定的设备是同一个设备
            try {//取得蓝牙的名字和mac
                if(null!= SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanya")){
                    mDeviceName= (String) SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanya");//蓝牙的名字
                    mac = (String) SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanmac");//蓝牙的mac
                    MyCommandManager.DEVICENAME= mDeviceName;
                }else{mDeviceName= MyCommandManager.DEVICENAME;mac=MyCommandManager.ADDRESS;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            if(null!=mac){
                String MYBANGDING=     (String) SharedPreferencesUtils.readObject(WenxinBandActivity.this,mac+"bnagding");
                if(null!=MYBANGDING){
                    bangding.setText(getResources().getString(R.string.bangdinged));
                    bangding.setEnabled(false);
                }else{
                    bangding.setText(getResources().getString(R.string.bangding));
                    bangding.setEnabled(true);
                }
            }else{
                bangding.setText(getResources().getString(R.string.bangding));
                bangding.setEnabled(true);
            }
        }catch (Exception E){
            E.printStackTrace();
        }
    }



    @OnClick({R.id.xinxin_bangdin})
    public void onClick(View v) {
        switch (v.getId()) {
            //绑定
            case R.id.xinxin_bangdin:
                Boolean is= ConnectManages.isNetworkAvailable(WenxinBandActivity.this);
                if(is==true) {//取得蓝牙地址
                    try {//取得蓝牙的名字和mac
                        if(null!= SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanya")){
                            mDeviceName= (String) SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanya");//蓝牙的名字
                            mac = (String) SharedPreferencesUtils.readObject(WenxinBandActivity.this,"mylanmac");//蓝牙的mac
                            MyCommandManager.DEVICENAME= mDeviceName;
                        }else{mDeviceName= MyCommandManager.DEVICENAME;mac=MyCommandManager.ADDRESS;
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    RequestQueue requestQueue = Volley.newRequestQueue(WenxinBandActivity.this);
                    JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET,"http://wx.berace.com.cn/wx/web/bind?mac="+mac, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {System.out.print("yyyyyyyyyyyyyyyyy"+response);
                                    try{if(response.optString("resultCode").equals("001")){
                                        Toast.makeText(WenxinBandActivity.this, getResources().getString(R.string.Binding_success),Toast.LENGTH_SHORT).show();
                                        //保存好绑定过的蓝牙mac
                                        SharedPreferencesUtils.saveObject(WenxinBandActivity.this,mac+"bnagding",mac);
                                            //保存好绑定过的蓝牙mac
                                            bangding.setText(getResources().getString(R.string.bangdinged));
                                            bangding.setEnabled(false);
                                            //保存
                                        }else{  Toast.makeText(WenxinBandActivity.this,getResources().getString(R.string.Bind_failed),Toast.LENGTH_SHORT).show();}
                                    }catch (Exception e){e.printStackTrace();}
                                }}, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {}}) {
                        @Override
                        public Map<String, String> getHeaders() {HashMap<String, String> headers = new HashMap<String, String>();headers.put("Accept", "application/json");headers.put("Content-Type", "application/json; charset=UTF-8");
                            return headers;
                        }};requestQueue.add(jsonRequest);
                }else{
                    Toast.makeText(this,getResources().getString(R.string.wangluo),Toast.LENGTH_SHORT).show();
                }
                break;
        }}

}