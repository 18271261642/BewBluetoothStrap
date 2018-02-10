package com.example.bozhilun.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
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
import com.example.bozhilun.android.util.DensityUtils;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.util.VerifyUtil;
import com.example.bozhilun.android.view.ChartView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class HeartRateTestActivity extends BaseActivity {
    @BindView(R.id.hate_test) Button tupianxuanzhang;//测试键
    @BindView(R.id.notest_state)RelativeLayout  testvalue;//测试结果显示
    @BindView(R.id.test_state)ChartView testladding;//测试中
    @BindView(R.id.xinlv_value)TextView xinlvvalue;//心率值
    @BindView(R.id.daojishi)   Button  daojishi;//倒计时
    @BindView(R.id.xinlvtest_ListView)ListViewForScrollView listview;
    @BindView(R.id.heart_back) LinearLayout heart_back;
    @BindView(R.id.heart_fengxiangsfg)ImageView test_fengxiangsfg;
    @BindView(R.id.xinlv_celang)TextView cesezhi;

    private Timer timer=null;
    private TimerTask task=null;
    int count=-1;
    public String mDeviceName,mDeviceAddress,userID;//蓝牙名字和地址
    private Handler mHandler;
    private Handler handler;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private JSONObject jsonObject;
    com.example.bozhilun.android.adpter.HeathtestAdapter HeathtestAdapter;
    private List<Map<String, Object>> mList=new ArrayList<Map<String, Object>>();

    private int[] test = new int[]{
        2071, 2086, 2099, 2109, 2117, 2124, 2130, 2134, 2136, 2138, 2139, 2141, 2143, 2145,
            2147, 2148, 2148, 2148, 2147,2145,2141, 2134, 2125, 2116, 2107, 2098, 2088, 2078, 2067, 2058, 2049, 1997, 1991, 1997, 2058, 2168, 2221, 2144, 2003, 1937, 1937, 1954,
            1970, 1972, 1972, 1972, 1984, 2005, 2019, 2020, 2020, 2019, 2019, 2021,2025, 2028, 2032, 2036, 2041, 2048, 2057, 2066, 2074, 2081, 2089, 2097, 2105, 2113, 2122, 2132, 2143, 2154,
            2164, 2174, 2184, 2190, 2190, 2181, 2166, 2146, 2125, 2103, 2082, 2062, 2046, 2036, 2030, 2027, 2025, 2024, 2022, 2019, 2017, 2014, 2012, 2012, 2014, 2016, 2019, 2021, 2026, 2031,
            2037, 2041, 2044, 2045, 2047, 2048, 2048, 2047, 2046, 2044, 2043, 2041, 2040, 2039, 2038, 2037, 2037, 2037, 2038, 2039, 2041, 2042, 2043,2044, 2046, 2047, 2048, 2048, 2047, 2047,
            2047, 2047, 2047, 2048, 2050, 2052, 2055, 2058, 2060, 2062, 2062, 2059,2056, 2052, 2049, 2046, 2044,2041, 2042, 2044, 2046, 2047, 2034, 2023, 2023, 2045, 2120, 2231, 2273, 2273
    };
    private int currentIndex;

    private String[] testData;

    private int heartRateTime = 30;
    @Override
    protected void initViews() {
        String testDataStr = getHeartRateDataFromAssets();
        testData = testDataStr.split(",");
        boolean  is= VerifyUtil.isZh(HeartRateTestActivity.this);
        if(!is){
            cesezhi.setTextSize(10);

            tupianxuanzhang.setTextSize(12);
        }


        EventBus.getDefault().register(this);
        Seaechdevice();//查找蓝牙的名字和地址userid
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    MyLogUtil.i("responseBBB"+resultCode);
                    if ("001".equals(resultCode)) {
                        Toast.makeText(HeartRateTestActivity.this,getResources().getString(R.string.data_upload),Toast.LENGTH_SHORT).show();
                        //查询下数据
                        chaoxundata();
                    } else {
                        Toast.makeText(HeartRateTestActivity.this,getResources().getString(R.string.data_upload_fail),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {e.printStackTrace();}}};

        if(null==handler){handler = new Handler() {@Override
        public void handleMessage(Message msg)  {
            switch (msg.what){
                case  8888:
                    listview.invalidate();
                    if(HeathtestAdapter!=null){
                        HeathtestAdapter.notifyDataSetChanged();
                    }
                   // mList.clear();
                    break;
            }



        }
        };}
        if(null==mHandler){mHandler = new Handler() {@Override
                public void handleMessage(Message msg)  {
                    if((msg.what % 50) == 0) {
                        daojishi.setText((heartRateTime--) + "");
                    }
                    //
                    if(1500 == msg.what){
                        end();
                        if("B15P".equals(mDeviceName)){
                            //结束指令
                            MyCommandManager. OnekeyMeasurementXin(mDeviceName,0);
                        }else{
                            //结束指令
                            MyCommandManager. OnekeyMeasurementXin(mDeviceName,1);
                        }
                        if(Integer.valueOf(xinlvvalue.getText().toString())>50){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HeartRateTestActivity.this);
                            builder.setTitle(getResources().getString(R.string.prompt));
                            builder.setMessage(getResources().getString(R.string.measurement_record));
                            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {//上传到服务器
                                        mList.clear();
                                        Mapdata();dialog.dismiss();} catch (Exception E) {E.printStackTrace();}}});
                            builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {dialog.dismiss();
                                    //查询下数据
                                    mList.clear();
                                    chaoxundata();}
                            });
                            builder.create().show();
                        }else{ Toast.makeText(HeartRateTestActivity.this,getResources().getString(R.string.try_again),Toast.LENGTH_SHORT).show();}
                    }
                    if(currentIndex > (testData.length - 1)){
                        currentIndex = 0;
                    }
//                    testladding.AddPointToList((int) (Math.random() * 400));

            testladding.AddPointToList(DensityUtils.dip2px(HeartRateTestActivity.this, (int) (-(Integer.valueOf(testData[currentIndex].trim()) - 2048)*0.4f) + 120));
            currentIndex++;
 }};
 }
 }

    public  void  Mapdata(){
        //判断网络是否连接
        try{
            JSONObject map = new JSONObject();
            map.put("userId",userID);
            map.put("deviceCode",mDeviceAddress);
            map.put("systolic","00");
            map.put("stepNumber", "00");
            Date timedf=new Date();
            SimpleDateFormat formatdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String xXXXdf=formatdf.format(timedf);
            map.put("date",xXXXdf);
            map.put("heartRate",xinlvvalue.getText().toString());
            map.put("status","1");
            JSONArray jsonArray=new JSONArray();
            Object jsonArrayb=jsonArray.put(map);
            JSONObject mapB = new JSONObject();
            mapB.put("data",jsonArrayb);
            String mapjson = mapB.toString();
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, HeartRateTestActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs+ URLs.upHeart, mapjson);
        }catch (Exception E){E.printStackTrace();}}

//要查看蓝牙的服务状态

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("HeartRate".equals(msg)) {
        String   HeartRate=(String)event.getObject();
            MyLogUtil.i("HeartRate"+HeartRate);
            xinlvvalue.setText(HeartRate);}

        }

    private void Seaechdevice(){
        try {
            if(null!= SharedPreferencesUtils.readObject(HeartRateTestActivity.this,"mylanya")){
                mDeviceName= (String) SharedPreferencesUtils.readObject(HeartRateTestActivity.this,"mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(HeartRateTestActivity.this,"mylanmac");//蓝牙的mac
            }else{
                mDeviceName= MyCommandManager.DEVICENAME;
                mDeviceAddress= MyCommandManager.ADDRESS;
            }
                if(null!= SharedPreferencesUtils.readObject(HeartRateTestActivity.this,"userId")){
                    userID= (String) SharedPreferencesUtils.readObject(HeartRateTestActivity.this,"userId");
                }else{
                    userID= Common.customer_id;
                }
            chaoxundata();//查询数据
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    protected int getStatusBarColor() {return R.color.backgoundhtest;}//设置toobar颜色
    @Override
    protected int getContentViewId() {return R.layout.activity_heartrate;}
    @OnClick({R.id.hate_test, R.id.heart_back, R.id.heart_fengxiangsfg})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heart_fengxiangsfg:
                Date timedf=new Date();
                SimpleDateFormat formatdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String xXXXdf=formatdf.format(timedf);
                String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf+".png";
                ScreenShot.shoot(HeartRateTestActivity.this, new File(filePath));
                Common.showShare(this, null, false,filePath);
                break;
            case R.id.heart_back:
                finish();
                 break;
            case R.id.hate_test:
                if(tupianxuanzhang.getText().equals(HeartRateTestActivity.this.getResources().getString(R.string.measure))){
                 try{
                     //发指令
                     if("B15P".equals(mDeviceName)){


                         if(BluetoothLeService.isService){
                             MyCommandManager. OnekeyMeasurementXin(mDeviceName,1);
                         }else{
                             Toast.makeText(HeartRateTestActivity.this,getResources().getString(R.string.bluetooth_disconnected),Toast.LENGTH_SHORT).show();
                         }
                     }else{
                         //开始测心率(b15s)
                         if(BluetoothLeService.isService){ MyCommandManager. OnekeyMeasurementXin(mDeviceName,0);}else{
                             Toast.makeText(HeartRateTestActivity.this,getResources().getString(R.string.bluetooth_disconnected),Toast.LENGTH_SHORT).show();
                         }

                     }
                     tupianxuanzhang.setText(R.string.suspend);
                     testvalue.setVisibility(View.GONE);
                     testladding.setVisibility(View.VISIBLE);
                     daojishi.setVisibility(View.VISIBLE);
                     if(null==timer){timer = new Timer();}
                     if(null==task){
                         task = new TimerTask() {@Override
                         public void run() {
                             count++;
                             Message msg = new Message();
                             msg.what=count;
                             mHandler.sendMessage(msg);}};timer.schedule(task,0,20);
                     }
                 }catch (Exception E){E.printStackTrace();}
                }else{
                    end();
                }
                break;

        }}
private void end(){
    //测试结束了
    mHandler.removeCallbacksAndMessages(null);
    if(null!=timer){timer.cancel();timer=null;}
    if(null!=task){task.cancel();task=null;}
    currentIndex = 0;
    count=-1;
    heartRateTime = 30;
    daojishi.setText(heartRateTime + "");
    testladding. ClearList();
    tupianxuanzhang.setText(R.string.measure);
    testladding .setVisibility(View.GONE);
    testvalue.setVisibility(View.VISIBLE);
    daojishi.setVisibility(View.GONE);
}

    //查询数据
    public  void chaoxundata(){
        Boolean is= ConnectManages.isNetworkAvailable(HeartRateTestActivity.this);
        if(is==true) {
            RequestQueue requestQueue = Volley.newRequestQueue(HeartRateTestActivity.this.getApplicationContext());
            try{
                //查询登录标记
                jsonObject=new JSONObject();
                jsonObject.put("userId",userID);
                jsonObject.put("deviceCode",mDeviceAddress);
                Date timedf=new Date();
                SimpleDateFormat formatdf=new SimpleDateFormat("yyyy-MM-dd");
                String xXXXdf=formatdf.format(timedf);
                jsonObject.put("date",xXXXdf);
            }catch (JSONException e){e.printStackTrace();}
            System.out.print("cfhj"+jsonObject.toString());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs+ URLs.getHeartD, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            MyLogUtil.i("longfeimadnag"+response.toString());
                            String heartRate=response.optString("heartRate");
                            String avgHeartRate=response.optString("avgHeartRate");
                            String	manual=response.optString("manual");
                            if (response.optString("resultCode").equals("001")){
                                try {
                                    if(heartRate.equals("[]")){

                                    }else{
                                        JSONArray oArrb=new JSONArray(manual);
                                        for (int i = 0; i < oArrb.length(); i++) {
                                            JSONObject jo = (JSONObject) oArrb.get(i);
                                            String   date=jo.getString("rtc");	//星期几
                                            String   stepNumbera=jo.getString("heartRate");//心率时间段

                                            Map<String, Object> map2 = new HashMap<String, Object>();
                                            map2.put("title", date);
                                            map2.put("info", stepNumbera+getResources().getString(R.string.BPM));
                                            mList.add(map2);
                                        }
                                        // 把添加了Map的List和Context传进适配器mListViewAdapter
                                        listview.setAdapter(HeathtestAdapter=new HeathtestAdapter(HeartRateTestActivity.this, mList));
                                        Message message = new Message();message.what = 8888;handler.sendMessage(message);

                                    }}catch (Exception e){e.printStackTrace();}}}}, new Response.ErrorListener() {@Override
                public void onErrorResponse(VolleyError error) {}}) {@Override
                public Map<String, String> getHeaders() {HashMap<String, String> headers = new HashMap<String, String>();headers.put("Accept", "application/json");headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;}};requestQueue.add(jsonRequest);
        }else{
            Toast.makeText(HeartRateTestActivity.this, R.string.wangluo,Toast.LENGTH_SHORT).show();
        }
    }

    private String getHeartRateDataFromAssets(){
        String data = null;
        try {
    //Return an AssetManager instance for your application's package
            InputStream is = getAssets().open("data.txt");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            data = new String(buffer, "GB2312");

        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try{if(null!=mHandler){ mHandler.removeCallbacksAndMessages(null);}if(null!=timer){timer.cancel();}
            if(null!=handler){ handler.removeCallbacksAndMessages(null);}
        }catch (Exception E){E.printStackTrace();}
    }
}
