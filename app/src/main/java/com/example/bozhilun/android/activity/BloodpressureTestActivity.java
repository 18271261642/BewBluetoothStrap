package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.OutdoorCyclingActivityStar;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.ArcProgress;
import com.example.bozhilun.android.adpter.BloodpresuretestAdapter;
import com.example.bozhilun.android.adpter.HeathtestAdapter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.coverflow.ListViewForScrollView;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.view.BaseView;
import com.example.bozhilun.android.view.ChartView;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/14.
 * 心率测试界面
 */

public class BloodpressureTestActivity extends BaseActivity {
    @BindView(R.id.hate_testxieya)
    Button tupianxuanzhang;//测试键
    @BindView(R.id.notest_statexieya)
    RelativeLayout testvalue;//测试结果显示
    @BindView(R.id.xinlv_valuexieya)
    TextView xinlvvalue;//血压值
    @BindView(R.id.xinlvtest_ListViewxieya)
    ListViewForScrollView listview;
    @BindView(R.id.heart_backxieya)
    LinearLayout heart_back;
    @BindView(R.id.heart_fengxiangsfgxieya)
    ImageView test_fengxiangsfg;
    @BindView(R.id.tongyongmoshi_xieya)
    TextView tongyong;//通用按钮
    @BindView(R.id.sirenmoshianniu_xieya)
    TextView siren;//私人按钮
    @BindView(R.id.myprogress_axieya)
    ArcProgress xieyaArcProgress;
    @BindView(R.id.xieyaceliangsss)
    TextView jieguo;


    public String mDeviceName, mDeviceAddress, userID;//蓝牙名字和地址

    boolean ismymoshi = false;//模式选择
    private Timer timer = null;
    private TimerTask task = null;
    int count = 2;
    private Handler mHandler;
    private Handler handler;
    boolean isens = false;
    boolean istangkuang = false;//是否弹框
    private List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    JSONObject jsonObject;
    String XieYa;

    public String getXieYa() {
        return XieYa;
    }

    public void setXieYa(String xieYa) {
        XieYa = xieYa;
    }

    BloodpresuretestAdapter bloodpresuretestAdapter;

    @Override
    protected int getStatusBarColor() {
        return R.color.mosi;
    }//设置toobar颜色

    @Override
    protected void initViews() {
        boolean is = VerifyUtil.isZh(BloodpressureTestActivity.this);
        if (!is) {
            jieguo.setTextSize(10);
            tupianxuanzhang.setTextSize(12);
            tongyong.setTextSize(10);
            siren.setTextSize(10);
        }

        EventBus.getDefault().register(this);
        Seaechdevice();//查找蓝牙的名字和地址userid
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    MyLogUtil.i("responseBBB" + resultCode);
                    if ("001".equals(resultCode)) {
                        Toast.makeText(BloodpressureTestActivity.this, R.string.data_upload, Toast.LENGTH_SHORT).show();
                        //查询下数据
                        chaoxundata();
                    } else {
                        Toast.makeText(BloodpressureTestActivity.this, R.string.data_upload_fail, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        if (null == handler) {
            handler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                        case 8888:
                            listview.invalidate();
                            if (bloodpresuretestAdapter != null) {
                                bloodpresuretestAdapter.notifyDataSetChanged();
                            }
                            // mList.clear();
                            break;
                    }


                }
            };
        }

        if (null == mHandler) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    xieyaArcProgress.setProgress(msg.what);
                    if ("50".equals(String.valueOf(msg.what))) {
                        if ("B15P".equals(mDeviceName)) {
                            if (BluetoothLeService.isService) {
                                MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 1);
                            } else {
                                Toast.makeText(BloodpressureTestActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if ("60".equals(String.valueOf(msg.what))) {
                        if ("B15P".equals(mDeviceName)) {
                        } else {
                            if (BluetoothLeService.isService) {
                                MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 0);
                            } else {
                                Toast.makeText(BloodpressureTestActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    if ("100".equals(String.valueOf(msg.what))) {

                        End();
                        if (null != getXieYa() && !"".equals(getXieYa())) {
                            xinlvvalue.setText(XieYa);
                            if (istangkuang == false) {
                                istangkuang = true;

                                AlertDialog.Builder builder = new AlertDialog.Builder(BloodpressureTestActivity.this);
                                builder.setTitle(getResources().getString(R.string.prompt));
                                builder.setMessage(getResources().getString(R.string.measurement_record));
                                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {//上传到服务器
                                            // mList.clear();
                                            String gaoya = XieYa.split("/")[0];
                                            String diya = XieYa.split("/")[1];
                                            mList.clear();
                                            Mapdata(gaoya.trim(), diya.trim());
                                            dialog.dismiss();
                                            istangkuang = false;
                                        } catch (Exception E) {
                                            E.printStackTrace();
                                        }
                                    }
                                });
                                builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        istangkuang = false;
                                        //查询下数据
                                        mList.clear();
                                        chaoxundata();
                                    }
                                });
                                builder.create().show();
                            }
                        }


                    }
                }
            };
        }

        chaoxundata();

        //私人模式测试血压
        siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ismymoshi = true;
                siren.setTextColor(getResources().getColor(R.color.mosi));
                tongyong.setTextColor(getResources().getColor(R.color.backgounds));
                siren.setBackground(getResources().getDrawable(R.drawable.jiankang));
                tongyong.setBackground(getResources().getDrawable(R.drawable.jiankanga));
            }
        });

    }


//要查看蓝牙的服务状态

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("XieYa".equals(msg)) {
            try {
                String jeson = event.getObject().toString();
                JSONObject obj = new JSONObject(jeson);
                XieYa = String.valueOf(obj.getString("systolicpressure")) + "/" + String.valueOf(obj.getString("diastolicpressure"));
                setXieYa(XieYa);
            } catch (Exception E) {
                E.printStackTrace();
            }
        } else if (BluetoothLeService.XieYng.equals(msg)) {
            try {
                String jeson = event.getObject().toString();
                JSONObject obj = new JSONObject(jeson);
                XieYa = String.valueOf(obj.getString("systolicpressure")) + "/" + String.valueOf(obj.getString("diastolicpressure"));
                setXieYa(XieYa);
            } catch (Exception E) {
                E.printStackTrace();
            }
        }

    }

    private void Seaechdevice() {
        try {
            if (null != SharedPreferencesUtils.readObject(BloodpressureTestActivity.this, "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(BloodpressureTestActivity.this, "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(BloodpressureTestActivity.this, "mylanmac");//蓝牙的mac
                if (!"B15P".equals(mDeviceName)) {
                    siren.setClickable(false);
                }
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
            }
            if (null != SharedPreferencesUtils.readObject(BloodpressureTestActivity.this, "userId")) {
                userID = (String) SharedPreferencesUtils.readObject(BloodpressureTestActivity.this, "userId");
            } else {
                userID = Common.customer_id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bloodpresuretest;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.tongyongmoshi_xieya,
            R.id.hate_testxieya, R.id.heart_backxieya, R.id.heart_fengxiangsfgxieya})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tongyongmoshi_xieya://通用
                ToastUtil.showToast(BloodpressureTestActivity.this,"通用");
                ismymoshi = false;
                tongyong.setTextColor(getResources().getColor(R.color.mosi));
                siren.setTextColor(getResources().getColor(R.color.backgounds));
                tongyong.setBackground(getResources().getDrawable(R.drawable.jiankang));
                siren.setBackground(getResources().getDrawable(R.drawable.jiankanga));
                break;
//            case R.id.sirenmoshianniu_xieya:
//                ToastUtil.showToast(BloodpressureTestActivity.this,"私人");
//                ismymoshi = true;
//                siren.setTextColor(getResources().getColor(R.color.mosi));
//                tongyong.setTextColor(getResources().getColor(R.color.backgounds));
//                siren.setBackground(getResources().getDrawable(R.drawable.jiankang));
//                tongyong.setBackground(getResources().getDrawable(R.drawable.jiankanga));
//                break;
            case R.id.heart_fengxiangsfgxieya://分享
                if (Common.isFastClick()) {
                    Date timedf = new Date();
                    SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String xXXXdf = formatdf.format(timedf);
                    String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                    ScreenShot.shoot(BloodpressureTestActivity.this, new File(filePath));
                    Common.showShare(this, null, false, filePath);
                }


                break;
            case R.id.heart_backxieya:
                finish();
                break;
            case R.id.hate_testxieya:
                if (tupianxuanzhang.getText().equals(BloodpressureTestActivity.this.getResources().getString(R.string.measure))) {
                    tupianxuanzhang.setText(R.string.suspend);

                    try {
                        //判断是b15p还是b15s
                        if ("B15P".equals(mDeviceName)) {
                            if (ismymoshi == false) {
                                MyCommandManager.OnekeyMeasurementxxTongyong(mDeviceName, 0); /*** 通用模式下的血压*/
                                /*** 私人模式下的血压*/
                            } else {
                                try {
                                    //血压
                                    SharedPreferences mySharedPre = BloodpressureTestActivity.this.getSharedPreferences("shousuoya", Activity.MODE_PRIVATE);
                                    SharedPreferences denglu = BloodpressureTestActivity.this.getSharedPreferences("shuzhangya", Activity.MODE_PRIVATE);
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
                                // timer.schedule(new HeathActivity.ListByDayTimerTask(), 1000, 1000);    // timeTask
                            }
                        } else {
                            try {
                                /*** b15s不分模式*/
                                MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 1);
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (null == timer) {
                        timer = new Timer(true);
                    }
                    if (null == task) {
                        task = new TimerTask() {
                            int mycount = 0;

                            @Override
                            public void run() {
                                if ("B15P".equals(mDeviceName)) {
                                    count++;
                                    mycount = count + 2;
                                } else {
                                    count++;
                                    mycount = count + 2;
                                }
                                Message msg = new Message();
                                msg.what = mycount;
                                mHandler.sendMessage(msg);
                            }
                        };
                        timer.schedule(task, 1, 1000);
                    }
                } else {
                    End();
                }

                break;

        }
    }


    private void End() {
        //测试结束了
        mHandler.removeCallbacksAndMessages(null);
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        count = 2;
        tupianxuanzhang.setText(R.string.measure);
        xieyaArcProgress.setProgress(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //测试结束了
        mHandler.removeCallbacksAndMessages(null);
        if (null != mHandler) {
            mHandler = null;
        }
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        count = 2;
    }

    //查询数据
    public void chaoxundata() {
        Boolean is = ConnectManages.isNetworkAvailable(BloodpressureTestActivity.this);
        if (is == true) {
            RequestQueue requestQueue = Volley.newRequestQueue(BloodpressureTestActivity.this.getApplicationContext());
            try {
                //查询登录标记
                jsonObject = new JSONObject();
                jsonObject.put("userId", userID);
                jsonObject.put("deviceCode", mDeviceAddress);
                Date timedf = new Date();
                SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd");
                String xXXXdf = formatdf.format(timedf);
                jsonObject.put("date", xXXXdf);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.print("cfhj" + jsonObject.toString());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.getBloodPressureD, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            MyLogUtil.i("longfeimadnag" + response.toString());
                            String manual = response.optString("manual");
                            if (response.optString("resultCode").equals("001")) {
                                try {
                                    if (manual.equals("[]")) {

                                    } else {
                                        JSONArray oArrb = new JSONArray(manual);
                                        for (int i = 0; i < oArrb.length(); i++) {
                                            JSONObject jo = (JSONObject) oArrb.get(i);
                                            String date = jo.getString("rtc");    //星期几
                                            String systolic = jo.getString("systolic");
                                            String diastolic = jo.getString("diastolic");

                                            Map<String, Object> map2 = new HashMap<String, Object>();
                                            map2.put("data", date);
                                            map2.put("mmhg", systolic + "/" + diastolic + " mmhg");
                                            mList.add(map2);
                                        }
                                        // 把添加了Map的List和Context传进适配器mListViewAdapter
                                        listview.setAdapter(bloodpresuretestAdapter = new BloodpresuretestAdapter(BloodpressureTestActivity.this, mList));
                                        Message message = new Message();
                                        message.what = 8888;
                                        handler.sendMessage(message);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
            Toast.makeText(BloodpressureTestActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
        }
    }

    public void Mapdata(String a, String b) {
        //判断网络是否连接
        try {
            JSONObject map = new JSONObject();
            map.put("userId", userID);
            map.put("deviceCode", mDeviceAddress);
            map.put("systolic", a);
            map.put("diastolic", b);
            map.put("stepNumber", "00");
            Date timedf = new Date();
            SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String xXXXdf = formatdf.format(timedf);
            map.put("date", xXXXdf);
            map.put("heartRate", "00");
            map.put("status", "1");
            JSONArray jsonArray = new JSONArray();
            Object jsonArrayb = jsonArray.put(map);
            JSONObject mapB = new JSONObject();
            mapB.put("data", jsonArrayb);
            String mapjson = mapB.toString();
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, BloodpressureTestActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


}
