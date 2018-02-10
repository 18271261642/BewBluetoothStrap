package com.example.bozhilun.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.adpter.HeathtestAdapter;
import com.example.bozhilun.android.adpter.OxyygenAdapter;
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
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.widget.MagicProgressCircle;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/4/18.
 * 血氧测试
 */

public class OxygenTsetActivity extends BaseActivity {

    @BindView(R.id.circle_xieyang_test)
    MagicProgressCircle tupianxuanzhang;//原型进度条
    @BindView(R.id.xieyang_test)
    Button start;//开始测量

    @BindView(R.id.xinlv_valuexieyang)
    TextView Xieyangvlaue;//血氧显示的值
    @BindView(R.id.xieyang_ListView)
    ListViewForScrollView listview;
    @BindView(R.id.oxygen_back)
    LinearLayout back;//返回
    @BindView(R.id.oxygen_fengxiangsfg)
    ImageView sharae;//分享

    @BindView(R.id.xeyangceliang)
    TextView celiang;

    public String mDeviceName, mDeviceAddress, userID;//蓝牙名字和地址


    private Handler mHandler;
    private Handler handler;
    private Timer timer = null;
    private TimerTask task = null;
    private int count = 0;
    boolean isens = false;//是否显示的数据
    boolean istangkuang = false;//是否弹框
    private String XieYaNng;//血氧值
    private OxyygenAdapter oxyygenAdapter;
    private JSONObject jsonObject;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

    protected int getStatusBarColor() {
        return R.color.xinmosi;
    }//设置toobar颜色

    @Override
    protected int getContentViewId() {
        return R.layout.activity_oxygentest;
    }

    @Override
    protected void initViews() {
        boolean is = VerifyUtil.isZh(OxygenTsetActivity.this);
        if (!is) {
            celiang.setTextSize(10);
            start.setTextSize(12);
        }


        EventBus.getDefault().register(OxygenTsetActivity.this);
        if (null == mHandler) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {

                    MyLogUtil.i("msggetData" + (float) msg.what / 50);
                    // Toast.makeText(OxygenTsetActivity.this,String.valueOf(Integer.valueOf(msg.what/100)),Toast.LENGTH_SHORT).show();

                    tupianxuanzhang.setPercent((float) msg.what / 50);
                    if (1.0 == (float) msg.what / 50) {
                        MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 0);
                        End();
                        isens = true;
                    }
                }
            };
        }
        if (null == handler) {
            handler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    switch (msg.what) {
                        case 8888:
                            listview.invalidate();
                            if (oxyygenAdapter != null) {
                                oxyygenAdapter.notifyDataSetChanged();
                            }
                            break;
                    }


                }
            };
        }
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    MyLogUtil.i("responseBBB" + resultCode);
                    if ("001".equals(resultCode)) {
                        Toast.makeText(OxygenTsetActivity.this, getResources().getString(R.string.data_upload), Toast.LENGTH_SHORT).show();
                        //查询下数据
                        chaoxundata();
                    } else {
                        Toast.makeText(OxygenTsetActivity.this, getResources().getString(R.string.data_upload_fail), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Seaechdevice();
        chaoxundata();
    }


//要查看蓝牙的服务状态

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("XieYng".equals(msg)) {
            try {
                String jeson = event.getObject().toString();
                JSONObject obj = new JSONObject(jeson);
                XieYaNng = String.valueOf(obj.getString("oxygen"));
                String shrinkBlood = obj.getString("systolicpressure"); //收缩血压的值
                String diastolicBlood = obj.getString("diastolicpressure"); //舒张压
                SharedPreferencesUtils.setParam(OxygenTsetActivity.this,"shrinkBlood",shrinkBlood); //保存收缩压
                SharedPreferencesUtils.setParam(OxygenTsetActivity.this,"diastolicBlood",diastolicBlood); //保存舒张压


                if (isens) {
                    Xieyangvlaue.setText(XieYaNng);
                    if (istangkuang == false) {
                        istangkuang = true;
                        if ("0".equals(Xieyangvlaue.getText().toString())) {
                            Toast.makeText(OxygenTsetActivity.this, getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OxygenTsetActivity.this);
                            builder.setTitle(getResources().getString(R.string.prompt));
                            builder.setMessage(getResources().getString(R.string.measurement_record));
                            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {//上传到服务器
                                        isens = false;
                                        mList.clear();
                                        Mapdata(XieYaNng);
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
                                    mList.clear();
                                    //查询下数据
                                    // chaoxundata();
                                }
                            });
                            builder.create().show();
                        }
                    }
                }
            } catch (Exception E) {
                E.printStackTrace();
            }


        }
    }

    @OnClick({R.id.xieyang_test, R.id.oxygen_back, R.id.oxygen_fengxiangsfg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oxygen_back:
                finish();
                break;
            case R.id.oxygen_fengxiangsfg:
                Date timedf = new Date();
                SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String xXXXdf = formatdf.format(timedf);
                String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                ScreenShot.shoot(OxygenTsetActivity.this, new File(filePath));
                Common.showShare(this, null, false, filePath);
                break;
            case R.id.xieyang_test:
                if (start.getText().equals(OxygenTsetActivity.this.getResources().getString(R.string.measure))) {
                    start.setText(R.string.suspend);
                    if (BluetoothLeService.isService) {
                        MyCommandManager.OnekeyMeasurementxxXieya(mDeviceName, 1); /*** b15s不分模式*/
                    } else {
                        Toast.makeText(OxygenTsetActivity.this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                    }
                    if (null == timer) {
                        timer = new Timer(true);
                    }
                    if (null == task) {
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                count++;
                                Message msg = new Message();
                                msg.what = count;
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


    //查询数据
    public void chaoxundata() {
        Boolean is = ConnectManages.isNetworkAvailable(OxygenTsetActivity.this);
        if (is == true) {
            RequestQueue requestQueue = Volley.newRequestQueue(OxygenTsetActivity.this.getApplicationContext());
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
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.getBloodOxygenD, jsonObject,
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
                                            String stepNumbera = jo.getString("bloodOxygen");//血氧时间段

                                            Map<String, Object> map2 = new HashMap<String, Object>();
                                            map2.put("data", date);
                                            map2.put("baifenbi", stepNumbera + "%");
                                            mList.add(map2);
                                        }
                                        // 把添加了Map的List和Context传进适配器mListViewAdapter
                                        listview.setAdapter(oxyygenAdapter = new OxyygenAdapter(OxygenTsetActivity.this, mList));
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
            Toast.makeText(OxygenTsetActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
        }
    }

    public void Mapdata(String xieyang) {
        //判断网络是否连接
        try {
            JSONObject map = new JSONObject();
            map.put("userId", userID);
            map.put("deviceCode", mDeviceAddress);
            map.put("systolic", "00");
            map.put("stepNumber", "00");
            map.put("bloodOxygen", xieyang);
            Date timedf = new Date();
            SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String xXXXdf = formatdf.format(timedf);
            map.put("date", xXXXdf);
            map.put("status", "1");
            JSONArray jsonArray = new JSONArray();
            Object jsonArrayb = jsonArray.put(map);
            JSONObject mapB = new JSONObject();
            mapB.put("data", jsonArrayb);
            String mapjson = mapB.toString();
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, OxygenTsetActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private void End() {
        //测试结束了
        mHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        count = 0;
        start.setText(R.string.measure);
        tupianxuanzhang.setPercent(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //测试结束了
        mHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        if (null != mHandler) {
            mHandler = null;
        }
        if (null != handler) {
            handler = null;
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


    private void Seaechdevice() {
        try {
            if (null != SharedPreferencesUtils.readObject(OxygenTsetActivity.this, "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(OxygenTsetActivity.this, "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(OxygenTsetActivity.this, "mylanmac");//蓝牙的mac
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
            }
            if (null != SharedPreferencesUtils.readObject(OxygenTsetActivity.this, "userId")) {
                userID = (String) SharedPreferencesUtils.readObject(OxygenTsetActivity.this, "userId");
            } else {
                userID = Common.customer_id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
