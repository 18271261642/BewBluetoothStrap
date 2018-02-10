package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.WenxinBandActivity;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.Jinakang;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SampleGattAttributes;
import com.example.bozhilun.android.onekeyshare.OnekeyShare;
import com.example.bozhilun.android.onekeyshare.OnekeyShareTheme;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;

import static com.example.bozhilun.android.bleutil.Customdata.byteToHex;

/**
 * Created by admin on 2016/12/14.
 * 健康测试
 */
public class HeathActivity extends BaseActivity {
    @BindView(R.id.tupianxuianzhang)
    ImageButton tupianxuanzhang;//旋转的蓝色箭头
    @BindView(R.id.jiangkang_back)
    LinearLayout xianshikongzhi_back;//返回
    @BindView(R.id.tongyongmoshi)
    TextView tongyong;//通用
    @BindView(R.id.sirenmoshianniu)
    TextView sirem;//私人
    @BindView(R.id.zuidishousuoya)
    TextView shuzhangya;//收缩压(字)
    @BindView(R.id.zuidishuzhangya)
    TextView shengsuo;//舒张压(字)
    @BindView(R.id.zuidishxinlv)
    TextView xinlvceshi;//心率(字)
    @BindView(R.id.zuidixieyang)
    TextView xieya;//血氧(字)
    @BindView(R.id.ceshi_jiankang_shousuoya)
    TextView shousuo;//收缩压
    @BindView(R.id.ceshi_jiankang_shuzhangya)
    TextView sguzhang;//舒张压
    @BindView(R.id.ceshi_jiankang_xinlv)
    TextView xinlv;//心率
    @BindView(R.id.ceshi_jiankang_xieyang)
    TextView xieyang;//血氧
    @BindView(R.id.jiankang_kaishijiance)
    TextView kaisjiance;//开始检测
    @BindView(R.id.jangkangtishiduan)
    LinearLayout Jinakang;//健康提示语
    @BindView(R.id.jiankangtishi)
    TextView tishiyu;//提示语
    @BindView(R.id.jiangkang_fengxiangsfg)
    ImageView fengxiang;//分享
    @BindView(R.id.jiankang_ceshibeijiangtu)
    ImageView ceshibackground;//测试结果对比图
    @BindView(R.id.jiankang_jiecejieguo)
    TextView jiecejieguo;//检测结果


    private byte[] WriteBytes, WriteBytespinjie;
    private boolean kaixinlv = false;
    private boolean xinlvdf = false;
    private boolean kaiqidonghua = false;
    private Handler myHandler;
    private Jinakang aaa = new Jinakang();

    private int recLen = 0;
    private Timer timer = new Timer(true);
    private JSONArray jsonArrayc = new JSONArray();
    private JSONObject jsonObjectc = new JSONObject();
    private boolean moshi = false;//检测模式


    private boolean isdianji = false;//是否可以切换


    public String mDeviceName, mDeviceAddress;//蓝牙名字和地址
    private BluetoothLeService mBluetoothLeService;
    private static boolean mConnected = false;//连接的状态
    private BluetoothGattService mnotyGattService;
    private BluetoothGattCharacteristic characteristic;
    private Calendar cendar = Calendar.getInstance();
    private JSONArray jsonArray = new JSONArray();
    private JSONObject jsonObjectxinlv = new JSONObject();

    //要查看蓝牙的服务状态

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("connect_success".equals(msg)) {
            mConnected = true;
        } else if ("connect_fail".equals(msg)) {
            mConnected = false;
        } else if (BluetoothLeService.HeartRate.equals(msg)) {
            //保存心率的值
            aaa.setXinlvzhi(Integer.parseInt(event.getObject().toString()));
        }//血压(通用)
        else if (BluetoothLeService.XieYa.equals(msg)) {
            String jeson = event.getObject().toString();
            try {
                JSONObject obj = new JSONObject(jeson);
                aaa.setJiangkanggaoya(Integer.valueOf(obj.getString("systolicpressure")));
                aaa.setJiangkangdiya(Integer.valueOf(obj.getString("diastolicpressure")));
                int gaoya = Integer.valueOf(obj.getString("systolicpressure"));
                if (gaoya <= 90) {
                    aaa.setXieya(10f);
                } else if (gaoya >= 90 && gaoya <= 120) {
                    aaa.setXieya(90f);
                } else if (gaoya >= 120 && gaoya <= 140) {
                    aaa.setXieya(180f);
                } else if (gaoya >= 140 && gaoya <= 160) {
                    aaa.setXieya(200f);
                } else if (gaoya >= 160 && gaoya <= 180) {
                    aaa.setXieya(220f);
                }
            } catch (Exception E) {
                E.printStackTrace();
            }

        } else if ("updatamsg".equals(msg)) {
            //血压
            if (BluetoothLeService.isService) {
                MyCommandManager.OnekeyMeasurementxxXieya(MyCommandManager.DEVICENAME, 1);
            } else {
                Toast.makeText(HeathActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
            }
            //自动刷新UI
            timer.schedule(new HeathActivity.ListByDayTimerTask(), 1000, 1000);    // timeTask
        } //血压(私人)
        else if (BluetoothLeService.OnekeyMeasurementsiren.equals(msg)) {
            String jeson = event.getObject().toString();
            try {
                JSONObject obj = new JSONObject(jeson);
                aaa.setJiangkanggaoya(Integer.valueOf(obj.getString("systolicpressure")));
                aaa.setJiangkangdiya(Integer.valueOf(obj.getString("diastolicpressure")));
                int gaoya = Integer.valueOf(obj.getString("systolicpressure"));
                if (gaoya <= 90) {
                    aaa.setXieya(10f);
                } else if (gaoya >= 90 && gaoya <= 120) {
                    aaa.setXieya(90f);
                } else if (gaoya >= 120 && gaoya <= 140) {
                    aaa.setXieya(180f);
                } else if (gaoya >= 140 && gaoya <= 160) {
                    aaa.setXieya(200f);
                } else if (gaoya >= 160 && gaoya <= 180) {
                    aaa.setXieya(220f);
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
            //b15s的结果
        } else if (BluetoothLeService.XieYng.equals(msg)) {
            try {

                String jeson = event.getObject().toString();
                JSONObject obj = new JSONObject(jeson);
                aaa.setJiangkanggaoya(Integer.valueOf(obj.getString("systolicpressure")));
                aaa.setJiangkangdiya(Integer.valueOf(obj.getString("diastolicpressure")));

                /**                 4d:心率 61:血氧 78:高血压 4f.低血压
                 * ab 00 07 ff 32 80 4d 61 78 4f
                 *
                 */
                //保存心率的值
                aaa.setXinlvzhi(Integer.valueOf(obj.getString("heartrate")));

                if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 90) {
                    aaa.setXieya(10f);
                } else if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) >= 90 && Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 120) {
                    aaa.setXieya(90f);
                } else if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) >= 120 && Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 140) {
                    aaa.setXieya(180f);
                } else if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) >= 140 && Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 160) {
                    aaa.setXieya(200f);
                } else if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) >= 160 && Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 180) {
                    aaa.setXieya(220f);
                }
                //血氧的值
                aaa.setXieyang(Integer.valueOf(obj.getString("oxygen")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    protected void initViews() {
        ShareSDK.initSDK(HeathActivity.this);
        EventBus.getDefault().register(this);
        chushuihua();
        myHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    try {
                        kaisjiance.setText(recLen + "%");
                        if (recLen == 60) {
                            if ("B15P".equals(mDeviceName)) {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        //蓝牙必须是连接好的
                                        try {
                                            //开始测心率
                                            if (BluetoothLeService.isService) {
                                                MyCommandManager.OnekeyMeasurementXin(mDeviceName, 1);
                                            } else {
                                                Toast.makeText(HeathActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        kaiqidonghua = true;
                                    }
                                }, 4000);
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        //蓝牙必须是连接好的
                                        try {
                                            //开始测心率(b15s)
                                            if (BluetoothLeService.isService) {
                                                MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 0);
                                            } else {
                                                Toast.makeText(HeathActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        kaiqidonghua = true;

                                    }
                                }, 4000);
                            }

                        }
                        if (recLen >= 100) {
                            SharedPreferences jianceguo = HeathActivity.this.getSharedPreferences("jiance", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editora = jianceguo.edit();
                            //用putString的方法保存数据
                            editora.putString("jiance", "1");
                            editora.commit();
                            kaisjiance.setEnabled(false);
                            kaisjiance.setText(R.string.jiancewangchen);
                            isdianji = false;
                            final Animation rotateAnimation = new RotateAnimation(0f, aaa.getXieya(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setDuration(2000);              //持续时间
                            rotateAnimation.setFillAfter(true);//保存位置
                            tupianxuanzhang.setAnimation(rotateAnimation);     //设置动画
                            rotateAnimation.startNow();
                            tupianxuanzhang.startAnimation(rotateAnimation);
                            timer.cancel();
                            shousuo.setText(aaa.getJiangkanggaoya() + "");
                            sguzhang.setText(aaa.getJiangkangdiya() + "");
                            //心率
                            xinlv.setText(aaa.getXinlvzhi() + "");
                            //血氧
                            xieyang.setText(aaa.getXieyang() + "");


                            if (Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())) <= 30 || Integer.parseInt(String.valueOf(aaa.getJiangkangdiya())) <= 30) {
                                return;
                            } else {
                                shaungshaunb(Integer.parseInt(String.valueOf(aaa.getJiangkanggaoya())), Integer.parseInt(String.valueOf(aaa.getJiangkangdiya())));//上传血压
                            }
                            if (Integer.parseInt(String.valueOf(aaa.getXinlvzhi())) <= 20) {
                                return;
                            } else {
                                if (!String.valueOf(aaa.getXinlvzhi()).equals("3")) {
                                    xilv(String.valueOf(aaa.getXinlvzhi()));//上传心率
                                }
                            }

                            //保存下心率的值
                            SharedPreferences mySharedPre = HeathActivity.this.getSharedPreferences("xinlvpunjunzhima", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editorc = mySharedPre.edit();
                            //用putString的方法保存数据
                            editorc.putString("xinlvpunjunzhima", String.valueOf(aaa.getXinlvzhi()));
                            editorc.commit();
                            Jinakang.setVisibility(View.VISIBLE);
                            Log.d("caonima", String.valueOf(aaa.getXieya()));
                            if (aaa.getXieya() == 10f) {
                                tishiyu.setTextColor(Color.parseColor("#2CC7CB"));
                                tishiyu.setText(R.string.niedriger);
                            } else if (aaa.getXieya() == 90f) {
                                tishiyu.setTextColor(Color.parseColor("#93D153"));
                                tishiyu.setText(R.string.lixiangxieya);
                            } else if (aaa.getXieya() == 180f) {
                                tishiyu.setTextColor(Color.parseColor("#F5C65E"));
                                tishiyu.setText(R.string.xieyagaoxian);
                            } else if (aaa.getXieya() == 200f) {
                                tishiyu.setTextColor(Color.parseColor("#EE8F55"));
                                tishiyu.setText(R.string.zhongdugaoya);
                            } else if (aaa.getXieya() == 220f) {
                                tishiyu.setTextColor(Color.parseColor("#ED5A6A"));
                                tishiyu.setText(R.string.zhongdugaoxuieya);//
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        sirem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isdianji) {
                    ToastUtil.showToast(HeathActivity.this, getResources().getString(R.string.zhengzaiceshi));
                } else {
                    moshi = true;
                    sirem.setTextColor(Color.parseColor("#ffffff"));
                    tongyong.setTextColor(Color.parseColor("#959595"));
                    sirem.setBackground(getResources().getDrawable(R.drawable.heath_jiankang));
                    tongyong.setBackground(getResources().getDrawable(R.drawable.heath_jiankanga));
                }
            }
        });

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_heath;
    }


    public void shaungshaunb(int arr, int brr) {
        //上传xinya
        try {
            JSONObject mapa = new JSONObject();
            mapa.put("userId", Common.customer_id);
            mapa.put("deviceCode", mDeviceAddress);
            mapa.put("status", "1");
            SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            mapa.put("date", dateForm.format(new Date()));
            mapa.put("systolic", String.valueOf(arr));
            mapa.put("diastolic", String.valueOf(brr));
            mapa.put("stepNumber", "0");
            mapa.put("heartRate", "0");
            mapa.put("bloodOxygen", aaa.getXieyang());
            jsonArrayc.put(mapa);
            jsonObjectc.put("data", jsonArrayc);
            //如果当前有网络把用户名和密码提交到服务器
            Boolean wangluo = ConnectManages.isNetworkAvailable(HeathActivity.this);
            if (wangluo == true) {
                RequestQueue requestQueue = Volley.newRequestQueue(HeathActivity.this.getApplicationContext());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.upHeart, jsonObjectc,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.print("baba" + response);
                                if (response.optString("resultCode").equals("001")) {
                                    System.out.print("成功成功成功成功成功成功成功成功成功"); //跳转到数据展示的界面
                                } else {
                                    Log.d("dfdsfd", "shibai");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("eeeeeeeeeeeeeoooooooooo" + error.toString());
                        Log.d("TTTTTTTTTTTT", String.valueOf(error));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void xilv(String aaa) {
        try {
            //获取网络状态
            Boolean wangluo = ConnectManages.isNetworkAvailable(HeathActivity.this);
            //如果当前有网络把用户名和密码提交到服务器
            if (wangluo == true) {
                JSONObject map = new JSONObject();
                //查询登录标记
                try {
                    map.put("userId", Common.customer_id);
                    //设备地址
                    map.put("deviceCode", mDeviceAddress);
                    map.put("systolic", "00");
                    map.put("stepNumber", "00");

                    Date timedf = new Date();
                    SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String xXXXdf = formatdf.format(timedf);
                    map.put("date", xXXXdf);
                    map.put("heartRate", aaa);
                    map.put("status", "1");
                    // System.out.print("map"+map.toString());
                    jsonObjectxinlv.put("data", jsonArray.put(map));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestQueue requestQueue = Volley.newRequestQueue(HeathActivity.this.getApplicationContext());
                Log.d("jsonObjectww", jsonObjectxinlv.toString());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.upHeart, jsonObjectxinlv,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // System.out.print("yuyuyu" + response);
                                Log.d("ssssssssssssssssssss", response.toString());
                                if (response.optString("resultCode").equals("001")) {

                                } else {
                                    Log.d("dfdsfd", "shibai");
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  System.out.print("eeeeeeeeeeeeeoooooooooo" + error.toString());
                        Log.d("TTTTTTTTTTTT", String.valueOf(error));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != myHandler) {
            myHandler.removeCallbacksAndMessages(null);
        }
        EventBus.getDefault().unregister(this);
        timer.cancel();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @OnClick({R.id.jiangkang_fengxiangsfg, R.id.jiangkang_back, R.id.tongyongmoshi,  R.id.jiankang_kaishijiance})
    public void onClick(View v) {
        switch (v.getId()) {
            //分享
            case R.id.jiangkang_fengxiangsfg:
                if (Common.isFastClick()) {
                    String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + "jiankangfenxiang.png";
                    ScreenShot.shoot(HeathActivity.this, new File(filePath));
                    showShare(this, null, false, filePath);
                }

                break;
            case R.id.jiangkang_back:
                finish();
                break;
            case R.id.tongyongmoshi: //通用模式
                if (isdianji) {
                    ToastUtil.showToast(HeathActivity.this, getResources().getString(R.string.zhengzaiceshi));
                } else {
                    moshi = false;
                    tongyong.setTextColor(Color.parseColor("#ffffff"));
                    sirem.setTextColor(Color.parseColor("#959595"));
                    tongyong.setBackground(getResources().getDrawable(R.drawable.heath_jiankang));
                    sirem.setBackground(getResources().getDrawable(R.drawable.heath_jiankanga));
                }
                break;
//            case R.id.sirenmoshianniu:   //私人模式
//                ToastUtil.showToast(HeathActivity.this,"私人模式");
//                if (isdianji) {
//                    ToastUtil.showToast(HeathActivity.this, getResources().getString(R.string.zhengzaiceshi));
//                } else {
//                    moshi = true;
//                    sirem.setTextColor(Color.parseColor("#ffffff"));
//                    tongyong.setTextColor(Color.parseColor("#959595"));
//                    sirem.setBackground(getResources().getDrawable(R.drawable.heath_jiankang));
//                    tongyong.setBackground(getResources().getDrawable(R.drawable.heath_jiankanga));
//                }
//                break;
            //开始检测
            case R.id.jiankang_kaishijiance:
                isdianji = true;
                kaisjiance.setEnabled(false);
                try {
                    //判断是b15p还是b15s
                    if ("B15P".equals(mDeviceName)) {
                        //b15p
                        /**
                         * 通用模式下的血压
                         */
                        if (moshi == false) {
                            if (BluetoothLeService.isService) {
                                MyCommandManager.OnekeyMeasurementxxTongyong(mDeviceName, 0);
                                timer.schedule(new ListByDayTimerTask(), 1000, 1000);    // timeTask
                            } else {
                                Toast.makeText(HeathActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                            }
                            /**
                             * 私人模式下的血压
                             */
                        } else {
                            try {
                                //血压
                                SharedPreferences mySharedPre = HeathActivity.this.getSharedPreferences("shousuoya", Activity.MODE_PRIVATE);
                                SharedPreferences denglu = HeathActivity.this.getSharedPreferences("shuzhangya", Activity.MODE_PRIVATE);
                                if (String.valueOf(mySharedPre.getString("shousuoya", "")).length() > 1 && String.valueOf(denglu.getString("shuzhangya", "")).length() > 1) {
                                    //取默认值
                                    MyCommandManager.OnekeyMeasurememoren(mDeviceName, 2);
                                } else {
                                    //取默认值
                                    MyCommandManager.OnekeyMeasurememoren(mDeviceName, 1);
                                }
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                            //自动刷新UI
                            timer.schedule(new ListByDayTimerTask(), 1000, 1000);    // timeTask
                        }
                    } else {
                        try {
                            /*** b15s不分模式*/
                            MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 1);
                            //自动刷新UI
                            timer.schedule(new ListByDayTimerTask(), 1000, 1000);    // timeTask
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

        }
    }

    /**
     * 启动分享
     *
     * @param context
     * @param platformToShare
     * @param showContentEdit
     * @param uil
     */
    public static void showShare(Context context, String platformToShare, boolean showContentEdit, String uil) {
        OnekeyShare oks = new OnekeyShare();//mayihuabei jingdong 600 1200 400
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        oks.setDialogMode();
        oks.disableSSOWhenAuthorize();
        oks.setImagePath(uil);  //分享sdcard目录下的图片
        oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
        oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
        oks.show(context);
    }

    class ListByDayTimerTask extends TimerTask {
        @Override
        public void run() {
            recLen++;
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    }


    public void chushuihua() {
        try {
            if (null != SharedPreferencesUtils.readObject(HeathActivity.this, "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(HeathActivity.this, "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(HeathActivity.this, "mylanmac");//蓝牙的mac
                if (!"B15P".equals(mDeviceName)) {
                    sirem.setClickable(false);
                }
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SettingLanguge();//判断是什么语言
    }

    public void SettingLanguge() {
        String language = HeathActivity.this.getResources().getConfiguration().locale.getLanguage();
        //中文
        if (language.equals("zh")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.zhongwen));
            //英语
        } else if (language.equals("en")) {
            kaisjiance.setTextSize(12);
            jiecejieguo.setTextSize(12);
            tongyong.setTextSize(12);
            sirem.setTextSize(12);
            shuzhangya.setTextSize(8);
            shengsuo.setTextSize(8);
            xinlvceshi.setTextSize(8);
            xieya.setTextSize(8);
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.yinyu));
        }//德文
        else if (language.equals("de")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.deyu));
        }//西班牙
        else if (language.equals("es")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.xiyu));
        }//法文
        else if (language.equals("fr")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.fayu));
        }//意大利
        else if (language.equals("it")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.yidali));
        }//日文
        else if (language.equals("ja")) {
            sirem.setTextSize(10);
            tongyong.setTextSize(10);
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.riyu));
        }//韩文
        else if (language.equals("ko")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.hanyu));
        }//葡萄牙
        else if (language.equals("pt")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.puyu));
        }//俄文
        else if (language.equals("ru")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.eyu));
        }//越南文
        else if (language.equals("vi")) {
            ceshibackground.setBackground(getResources().getDrawable(R.mipmap.yuelanyu));
        }
    }

}