package com.example.bozhilun.android.activity.wylactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.DiffuseView;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.StartFlick;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.Usermap;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.MySinkingView;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.view.PromptDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
 * Created by admin on 2017/3/28.
 * 室外骑行
 */
public class OutdoorCyclingActivityStar extends BaseActivity implements LocationSource, com.amap.api.location.AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener, OnMapReadyCallback {
    @BindView(R.id.qichemypm)
    LinearLayout xian1; //显示pm2.5
    @BindView(R.id.qichemypmwww)
    LinearLayout xian2;//显示天气
    /*** 计时*/
    @BindView(R.id.chronometer_time)
    Chronometer huwaijishi;
    /*** 计时*/
    @BindView(R.id.qixing_timer)
    Chronometer tv_timer;

    /*** 公里*/
    @BindView(R.id.full_kilometer)
    TextView gongli2;
    @BindView(R.id.my_gongli)
    TextView gongli;
    /*** 卡路里*/
    @BindView(R.id.xiaohao_kclal)
    TextView kaluli2;
    @BindView(R.id.qixingvelocity)
    TextView kaluli;
    /*** 速度*/
    @BindView(R.id.peisu)
    TextView speed2;
    @BindView(R.id.speed)
    TextView speed;

    /*** 年月日加时间*/
    @BindView(R.id.huwaiqixing_year)
    TextView data;
    /*** 天气图标*/
    @BindView(R.id.qichekongqiyu)
    ImageView kongqiimage;
    /*** 空气质量*/
    @BindView(R.id.qichekongqizhiliang)
    TextView kongqizhiliang;
    /*** 温度*/
    @BindView(R.id.qichewendu)
    TextView wendu;
    /*** 截屏的图片加载图片控件*/
    @BindView(R.id.qixingfugai)
    ImageView fugaiwu;
    /*** 下面的布局*/
    @BindView(R.id.huwaiqixing_huwaibuju)
    RelativeLayout qitabuju;
    /*** 上面的布局*/
    @BindView(R.id.huwaiqixing_ditut)
    RelativeLayout huwaiqixing;
    /*** 开始*/
    @BindView(R.id.huwaiqixing_changanzhantinghuwai)
    DiffuseView kaishi;
    /*** 继续*/
    @BindView(R.id.huwaiqixing_buttonjixuting)
    Button jixun;
    /*** 结束*/
    @BindView(R.id.huwaiqixing_button4jiwshuting)
    Button jieshu;
    /*** 开始按钮的布局*/
    @BindView(R.id.DiffuseView_buji)
    LinearLayout nuttonstart;
    /*** 地图控件*/
    @BindView(R.id.huwaiqixing_rent_map_pop)
    LinearLayout DITU;
    @BindView(R.id.huwaiqixingbubao_fengxiang)
    ImageView fengxiang;
    @BindView(R.id.qixingshuwaipaobustar)
    LinearLayout Back;
    @BindView(R.id.shuwaipaobuhuwai)
    TextView Title;
    @BindView(R.id.sinking)
    MySinkingView mySinkingView;//水波纹

    /*************************************************/
    private float percent = 0;

    MapFragment mapFragment;
    /*** 高德地图*/
    MapView mapView;
    /**
     * 高德地图 第一次加载的经纬度
     */
    double mlatitude;
    double mlongitude;
    /**
     * 高德地图金纬度
     */
    double latitude2;
    double longitude2;

    /**
     * 高德相关
     */
    Bitmap bm, bm2;
    // 自定义系统定位蓝点
    MyLocationStyle myLocationStyle = new MyLocationStyle();
    boolean gaodedingwei = false;//记录高德第一次定位
    boolean diyi = false;
    boolean dingweia = false;
    LatLng ll;
    LatLng oldll;
    Marker currentMarker;
    /**
     * google相关
     */
    LatLng al;
    com.google.android.gms.maps.model.LatLng jiu, jiu2;
    GoogleMap aaad;
    LocationManager lm;
    boolean dingwei = false;//记录google第一次定位
    boolean lockLongPressKey = true;

    /*********其他*/
    Usermap usermap = new Usermap();
    Handler mandler, bmandler;
    Timer timerb;
    com.example.bozhilun.android.activity.wylactivity.wyl_util.GPSutils GPSutils;

    com.alibaba.fastjson.JSONObject Mgps;

    double cijiao;//比较数据
    double myDistance;//GPS记录运动的距离
    int miss = 0;//计时器
    /**
     * 定位
     */
    LocationManagerProxy mAMapLocationManager;
    AMap aMap;
    OnLocationChangedListener mListener;//定位监听
    List Mymap = new ArrayList();
    private static String uil = "http://apis.berace.com.cn/watch/sport/upOutdoorSport";//上传轨迹
    private static String getWeathers = "http://apis.berace.com.cn/watch/user/getWeathers";//天气接口
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    int Exercisetimes;//记录运动的次数
    Thread myhread;
    //初始化陀螺仪传感器，注册回调函数
    private SensorManager mSM;
    private Sensor mSensor;

    protected void initViews() {
      /*  mSM = (SensorManager) getSystemService(SENSOR_SERVICE);mSensor = mSM.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSM.registerListener((SensorEventListener) OutdoorCyclingActivityStar.this, mSensor, SensorManager.SENSOR_DELAY_UI);//注册回调函数*/
        percent = 0.00f;
        mySinkingView.setPercent(percent);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    MyLogUtil.i("response" + resultCode);
                    if ("001".equals(resultCode)) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Mgps = new com.alibaba.fastjson.JSONObject();
        GPSutils = new com.example.bozhilun.android.activity.wylactivity.wyl_util.GPSutils();
        xian1.getBackground().setAlpha(100);
        xian2.getBackground().setAlpha(100);
        //  qitabuju.getBackground().setAlpha(100);
        /**查询当前的运动类型*/
        try {
            if (null != SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "type")) {
                if ("0".equals(SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "type"))) {
                    Mgps.put("type", 0);
                    Title.setText(getResources().getString(R.string.outdoor_running));
                } else {
                    Mgps.put("type", 1);
                    Title.setText(getResources().getString(R.string.outdoor_cycling));
                }
            } else {
                Mgps.put("type", 0);
                Title.setText(getResources().getString(R.string.outdoor_running));
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        /**计时器开始*/
        tv_timer.start();
        huwaijishi.start();
        tv_timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                miss++;
                tv_timer.setText(GPSutils.FormatMiss(miss));
                try {//得到时间
                    Mgps.put("timeLen", GPSutils.FormatMiss(miss));
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });
        huwaijishi.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                huwaijishi.setText(GPSutils.FormatMiss(miss));
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        data.setText(format.format(Calendar.getInstance().getTime()));//设置当前的日期和时间
        try {
            //得到开始时间
            Mgps.put("startTime", format.format(Calendar.getInstance().getTime()));
        } catch (Exception E) {
            E.printStackTrace();
        }

        /*** google地图控件*/
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.qixinggoogle_map);

        huwaiqixing.getBackground().setAlpha(100);
        kaishi.start();
        kaishi.setDiffuseWidth(10);//设置扩散圆宽度
        kaishi.setColor(Color.parseColor("#65cce7"));
        kaishi.Typeface(getResources().getString(R.string.long_stop));
        kaishi.setmCoreRadius(120); //设置中心圆半径
        StartFlick.startFlick(kaishi);
        kaishi.setOnTouchListener(buttonListener);
        mandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (DITU.getHeight() != 0) {
                        usermap.setHeight(DITU.getHeight());
                        timerb.cancel();
                        mandler.removeCallbacksAndMessages(null);
                    }
                }
            }
        };
        timerb = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                mandler.sendMessage(message);
            }
        };
        timerb.schedule(task, 10, 1000);
        bmandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    //计时暂停
                    huwaijishi.stop();
                    tv_timer.stop();
                    nuttonstart.setVisibility(View.GONE);//开始按钮消失
                    jixun.setVisibility(View.VISIBLE);//继续
                    jieshu.setVisibility(View.VISIBLE);//结束
                    //启动动画
                    StartFlick.startFlickzou(jixun);//从左往右
                    StartFlick.startFlickyou(jieshu);//从右往左
                    mySinkingView.setVisibility(View.GONE);
                } else {
                    lockLongPressKey = false;
                    mySinkingView.setVisibility(View.GONE);
                }
            }
        };
        chaxuntianqi();//查询天气
    }


    private View.OnTouchListener buttonListener = new View.OnTouchListener() {

        public boolean onTouch(View arg0, MotionEvent event) {
            // TODO Auto-generated method stub
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) { // 按下
                lockLongPressKey = true;
                mySinkingView.setVisibility(View.VISIBLE);
                myhread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        percent = 0;
                        while (percent <= 1 && lockLongPressKey) {
                            mySinkingView.setPercent(percent);
                            percent += 0.01f;
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (percent >= 1.0) {
                            Message message = new Message();
                            message.what = 1;
                            bmandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = 2;
                            bmandler.sendMessage(message);
                        }
                        percent = 0.00f;
                        mySinkingView.setPercent(percent);
                    }
                });
                myhread.start();
                return true;

            } else if (action == MotionEvent.ACTION_UP) { // 松开
                if (percent < 1.0) {
                    Message message = new Message();
                    message.what = 2;
                    bmandler.sendMessage(message);
                }
                return true;
            }
            return true;
        }
    };


    @Override
    protected int getContentViewId() {
        return R.layout.activity_outdoor_cycling_star;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = (MapView) findViewById(R.id.qixing_map);
        try {

            //判断下语言吧
            Boolean zhon = getResources().getConfiguration().locale.getCountry().equals("CN");
            if (zhon == true) {
                init();
            }
            else {
                mapView.setVisibility(View.GONE);
                mapFragment.getMapAsync(this);//Google地图初始化
            }
            mapView.onCreate(savedInstanceState);// 必须要写
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


/**********************************************************************************************************************************************************************************************************************************/
    /**
     * 高德地图
     *
     * @param
     */
    protected void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    } /**初始化AMap对象*/
    /*** 设置地图样式*/
    protected void setUpMap() {
        // 自定义定位蓝点图标+
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.BLUE);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);//取消控制+ -
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(30));

    }

    Handler handler = new Handler() {
        @Override                  //这个方法是从父类/接口 继承过来的，需要重写一次
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);        // 此处可以更新UI
            try {
                LocationManager loctionManager;//声明LocationManager对象
                String contextService = OutdoorCyclingActivityStar.this.LOCATION_SERVICE; //通过系统服务，取得LocationManager对象
                loctionManager = (LocationManager) getSystemService(contextService);
                Criteria criteria = new Criteria();   //使用标准集合，让系统自动选择可用的最佳位置提供器，提供位置
                criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
                criteria.setAltitudeRequired(false);//不要求海拔
                criteria.setBearingRequired(false);//要求方位
                criteria.setCostAllowed(true);//允许有花费
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);//低功耗
                String provider = loctionManager.getBestProvider(criteria, true); //从可用的位置提供器中，匹配以上标准的最佳提供器
                //获得最后一次变化的位置
                Location location = loctionManager.getLastKnownLocation(provider);
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    GPSutils.setJINDU(lat);
                    GPSutils.setWEIDU(lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        // TODO Auto-generated method stub
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getAMapException().getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);
                // Toast.makeText(this,String.valueOf(amapLocation.getBearing()),Toast.LENGTH_SHORT).show();
                // System.out.print("uuuu"+ amapLocation.getBearing());
                ll = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //保存第一次定位的经纬度
                if (String.valueOf(mlatitude).equals("0.0")) {
                    mlatitude = amapLocation.getLatitude();
                    mlongitude = amapLocation.getLongitude();
                    oldll = new LatLng(mlatitude, mlongitude);//空的金纬度
                    hua(1, mlatitude, mlongitude);//画起点
                    try {
                        com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                        beanList.put("lat", mlatitude);
                        beanList.put("lon", mlongitude);
                        Mymap.add(beanList);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
                if (!gaodedingwei) {
                    //画第一段
                    aMap.addPolyline((new PolylineOptions()).add(oldll, ll).color(Color.parseColor("#00aeef")).width(30));
                    gaodedingwei = true;
                    //计算第一段距离
                    myDistance += GPSutils.getDistance(oldll.latitude, oldll.longitude, ll.latitude, ll.longitude);
                } else {
                    //计算其他的距离
                    myDistance += GPSutils.getDistance(oldll.latitude, oldll.longitude, ll.latitude, ll.longitude);
                    //保留两位小数
                    DecimalFormat decimalFormat = new DecimalFormat("######0.00");
                    /**设置公里*/
                    gongli.setText(String.valueOf(decimalFormat.format(myDistance)));
                    gongli2.setText(String.valueOf(decimalFormat.format(myDistance)));
                    /**设置卡路里*///卡路里=路程*65.4
                    kaluli.setText(String.valueOf(decimalFormat.format(myDistance * 65.4)) + "Kcal");
                    kaluli2.setText(String.valueOf(decimalFormat.format(myDistance * 65.4)) + "Kcal");
                    latitude2 = amapLocation.getLatitude();
                    longitude2 = amapLocation.getLongitude();
                    try {
                        com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                        beanList.put("lat", latitude2);
                        beanList.put("lon", longitude2);
                        Mymap.add(beanList);
                        //得到卡路里
                        Mgps.put("calories", Double.valueOf(decimalFormat.format(myDistance * 65.4)));
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    ll = new LatLng(latitude2, longitude2);
                    // aMap.addPolyline((new PolylineOptions()).add(oldll,ll).color(Color.parseColor("#00aeef")).width(20).setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.huodongjiaoyin)));
                    aMap.addPolyline((new PolylineOptions()).add(oldll, ll).color(Color.parseColor("#00aeef")).width(20).geodesic(true));
                    oldll = ll;
                    /**设置速度*/
                    try {
                        SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
                        Date dates = dff.parse(huwaijishi.getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.setTime(dates);
                        speed.setText(String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(myDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                        speed2.setText(String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(myDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)) + "KM/H");
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }
        }
    }


/**
 * 这里是 Google地图
 */
    /*******************************************************************************************************************************************************************************************************************************/
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        aaad = googleMap;
        lm = (LocationManager) OutdoorCyclingActivityStar.this.getSystemService(Context.LOCATION_SERVICE);
        // 返回所有已知的位置提供者的名称列表，包括未获准访问或调用活动目前已停用的。
        //List<String> lp = lm.getAllProviders();
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        //设置位置服务免费
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //设置水平位置精度
        //getBestProvider 只有允许访问调用活动的位置供应商将被返回
        String providerName = lm.getBestProvider(criteria, true);
        if (providerName != null) {
            Location location = lm.getLastKnownLocation(providerName);
            if (location != null) {
                //获取维度信息
                double latitude = location.getLatitude();
                //获取经度信息
                double longitude = location.getLongitude();
                com.google.android.gms.maps.model.LatLng sydney = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
                googleMap.setMyLocationEnabled(false);
                googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(sydney, 16));
                if (dingwei == false) {
                    //第一次定位
                    tianjia(googleMap, latitude, longitude, 0);
                    jiu = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
                    //保存Google第一次定位的坐标点
                    try {
                        com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                        beanList.put("lat", latitude);
                        beanList.put("lon", longitude);
                        Mymap.add(beanList);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    dingwei = true;
                }

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListenerb);
            }
        }
    }

    AMapLocationListener locationListenerb = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
        }

        public void onLocationChanged(Location location) { //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            if (location != null) {
                if (!dingweia) {
                    dingweia = true;
                    //画第一段
                    com.google.android.gms.maps.model.LatLng duan = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                    aaad.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().add(jiu, duan).width(20).color(Color.parseColor("#00aeef")).geodesic(true));
                    myDistance += GPSutils.getDistance(jiu.latitude, jiu.longitude, location.getLatitude(), location.getLongitude());
                } else {
                    if (location.getLatitude() != cijiao) {
                        cijiao = location.getLatitude();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);// 发送消息
                        // tianjia(aaad, location.getLatitude(), location.getLongitude(), 2);
                        jiu2 = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                        aaad.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().add(jiu, jiu2).width(20).color(Color.parseColor("#00aeef")).geodesic(true));
                        jiu = jiu2;
                        try {
                            com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
                            beanList.put("lat", location.getLatitude());
                            beanList.put("lon", location.getLongitude());
                            Mymap.add(beanList);
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                        jiu = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                        //计算其他的距离
                        myDistance += GPSutils.getDistance(jiu2.latitude, jiu2.longitude, jiu.latitude, jiu.longitude);
                        //保留两位小数
                        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
                        /**设置公里*/
                        gongli.setText(String.valueOf(decimalFormat.format(myDistance)));
                        gongli2.setText(String.valueOf(decimalFormat.format(myDistance)));
                        /**设置卡路里*///卡路里=路程*65.4
                        kaluli.setText(String.valueOf(decimalFormat.format(myDistance * 65.4)) + "Kcal");
                        kaluli2.setText(String.valueOf(decimalFormat.format(myDistance * 65.4)) + "Kcal");
                        try {//得到卡路里
                            if (0.0 != myDistance) {
                                Mgps.put("calories", Double.valueOf(decimalFormat.format(myDistance * 65.4)));
                            } else {
                                Mgps.put("calories", "0.0");
                            }
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                        /**设置速度*/
                        try {
                            SimpleDateFormat dfa = new SimpleDateFormat("HH:mm:ss");
                            Date dates = dfa.parse(huwaijishi.getText().toString());
                            Calendar c = Calendar.getInstance();
                            c.setTime(dates);
                            speed.setText(String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(myDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                            speed2.setText(String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(myDistance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)) + "KM/H");
                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    }
                }
            }
        }

        public void onProviderDisabled(String provider) {// Provider被disable时触发此函数，比如GPS被关闭
        }

        public void onProviderEnabled(String provider) {//  Provider被enable时触发此函数，比如GPS被打开
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {// Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        }
    };

    /**
     * 添加起点和终点
     *
     * @param map
     * @param a   精度
     * @param b   纬度
     * @param id  如果id为0添加终点
     */
    void tianjia(GoogleMap map, double a, double b, int id) {
        if (id == 0) {
            com.google.android.gms.maps.model.MarkerOptions markerOption2 = new com.google.android.gms.maps.model.MarkerOptions();
            markerOption2.position(new com.google.android.gms.maps.model.LatLng(a, b));
            markerOption2.draggable(true);
            markerOption2.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.mipmap.qidian));
            map.addMarker(markerOption2);
        } else if (id == 2) {
            /**
             * 划线
             */
            com.google.android.gms.maps.model.PolylineOptions rectOptions = new com.google.android.gms.maps.model.PolylineOptions();
            rectOptions.add(new com.google.android.gms.maps.model.LatLng(a, b));
            com.google.android.gms.maps.model.LatLng xin = new com.google.android.gms.maps.model.LatLng(a, b);
            if (diyi == false) {
                jiu = xin;
                jiu2 = jiu;
                diyi = true;
            } else {
                map.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().add(jiu2, xin).width(20).color(Color.parseColor("#00aeef")).geodesic(true));
                jiu2 = xin;
            }
        } else {
            com.google.android.gms.maps.model.MarkerOptions markerOption2 = new com.google.android.gms.maps.model.MarkerOptions();
            markerOption2.position(new com.google.android.gms.maps.model.LatLng(a, b));
            markerOption2.draggable(true);
            markerOption2.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.mipmap.zhongdian));
            map.addMarker(markerOption2);
        }
    }


    /**
     * huwaiqixingbubao_fengxiang 分享截屏
     * huwaiqixing_huwaibuju 开始加载
     * huwaiqixing_ditut 下面的布局
     */
    @OnClick({R.id.huwaiqixing_huwaibuju, R.id.huwaiqixing_ditut, R.id.huwaiqixing_buttonjixuting, R.id.huwaiqixing_button4jiwshuting, R.id.huwaiqixingbubao_fengxiang, R.id.qixingshuwaipaobustar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qixingshuwaipaobustar:
                final PromptDialog promptDialog = new PromptDialog(OutdoorCyclingActivityStar.this);
                promptDialog.show();
                promptDialog.setTitle(getResources().getString(R.string.prompt));
                promptDialog.setContent("是否退出？");
                promptDialog.setrightText(getResources().getString(R.string.cancle));
                promptDialog.setleftText(getResources().getString(R.string.confirm));
                promptDialog.setListener(new PromptDialog.OnPromptDialogListener() {
                    @Override
                    public void leftClick(int code) {
                        promptDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void rightClick(int code) {
                        promptDialog.dismiss();
                    }
                });
                break;
            case R.id.huwaiqixingbubao_fengxiang:
                if (Common.isFastClick()) {
                    Boolean zhon = getResources().getConfiguration().locale.getCountry().equals("CN");
                    if (zhon == true) {
                        aMap.getMapScreenShot(new Gaode()); /**高德地图截屏*/
                    } else {
                        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                            Bitmap bitmap;

                            public void onSnapshotReady(Bitmap snapshot) {
                                bitmap = snapshot;
                                try {
                                    FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "SDSDSS.png");
                                    boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    try {
                                        out.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (ifSuccess) {
                                        fugaiwu.setVisibility(View.VISIBLE);
                                        fugaiwu.setImageBitmap(bitmap);
                                        Date timedf = new Date();
                                        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String xXXXdf = formatdf.format(timedf);
                                        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                                        ScreenShot.shoot(OutdoorCyclingActivityStar.this, new File(filePath));
                                        Common.showShare(OutdoorCyclingActivityStar.this, null, false, filePath);
                                    } else {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        aaad.snapshot(callback);
                    }
                }
                break;
            case R.id.huwaiqixing_huwaibuju:
                qitabuju.setVisibility(View.GONE);
                huwaiqixing.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                DITU.setLayoutParams(params);
                StartFlick.startFlickbutton(huwaiqixing);
                break;
            case R.id.huwaiqixing_ditut:
                huwaiqixing.setVisibility(View.GONE);
                qitabuju.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams paramsa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, usermap.getHeight());
                DITU.setLayoutParams(paramsa);
                break;
            //继续
            case R.id.huwaiqixing_buttonjixuting:
                jieshu.setVisibility(View.GONE);
                jixun.setVisibility(View.GONE);
                nuttonstart.setVisibility(View.VISIBLE);
                huwaijishi.start();
                tv_timer.start();
                break;
            //结束
            case R.id.huwaiqixing_button4jiwshuting:


                try {
                    Boolean zhong = getResources().getConfiguration().locale.getCountry().equals("CN");
                    if (zhong == true) {
                        hua(0, latitude2, longitude2);//高德添加终点
                    } else {

                        if (null != jiu2) {
                            tianjia(aaad, jiu2.latitude, jiu2.longitude, 3);//google添加终点
                        } else {
                            ToastUtil.showShort(OutdoorCyclingActivityStar.this, getResources().getString(R.string.notsportdata));
                        }
                    }
                    /**停止计时器*/
                    huwaijishi.stop();
                    tv_timer.stop();
                    qitabuju.setVisibility(View.GONE);
                    huwaiqixing.setVisibility(View.VISIBLE);
                    try {
                        //取得配速
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Mgps.put("rtc", df.format(new Date()));
                        Mgps.put("speed", Double.valueOf(speed.getText().toString()));
                        //取得公里
                        Mgps.put("distance", Double.valueOf(Double.parseDouble(gongli.getText().toString())));
                        Mgps.put("userId", Common.customer_id);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                    ViewGroup.LayoutParams paramsb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                    DITU.setLayoutParams(paramsb);
                    deactivate();//停止定位
                    AlertDialog.Builder builder = new AlertDialog.Builder(OutdoorCyclingActivityStar.this);
                    builder.setMessage(getResources().getString(R.string.save_record));
                    builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Exercisetimes++;
                            try {
                                if (null != SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "Exercisetimes")) {
                                    Exercisetimes = Integer.valueOf(String.valueOf(SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "Exercisetimes"))) + Exercisetimes;
                                }//保存运动的次数
                                SharedPreferencesUtils.saveObject(OutdoorCyclingActivityStar.this, "Exercisetimes", Exercisetimes);
                                if (null != SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "Movingdistance")) {
                                    //保存运动的距离
                                    SharedPreferencesUtils.saveObject(OutdoorCyclingActivityStar.this, "Movingdistance", Double.parseDouble(gongli.getText().toString()) +
                                            Double.valueOf(String.valueOf(SharedPreferencesUtils.readObject(OutdoorCyclingActivityStar.this, "Movingdistance"))));
                                } else {
                                    SharedPreferencesUtils.saveObject(OutdoorCyclingActivityStar.this, "Movingdistance", Double.parseDouble(gongli.getText().toString()));
                                }
                                //上传到服务器
                                Mapdata();
                                dialog.dismiss();
                            } catch (Exception E) {
                                E.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 上传经纬度数据
     */
    public void Mapdata() {
        //判断网络是否连接
        if(!ConnectManages.isNetworkAvailable(OutdoorCyclingActivityStar.this)){
                ToastUtil.showToast(OutdoorCyclingActivityStar.this,"当前无网络连接!");
        }else{
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("outdoorSports", Mgps);
                System.out.print("Mgps" + Mgps.toString());
                map.put("latLons", Mymap);
                String mapjson = JSON.toJSONString(map);

                MyLogUtil.i("latLons" + mapjson);
                dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, OutdoorCyclingActivityStar.this);
                OkHttpObservable.getInstance().getData(dialogSubscriber, uil, mapjson);
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
    }


    /**
     * 重新开始定位
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != handler) {
                handler.removeCallbacksAndMessages(null);
            }
            if (null != mandler) {
                mandler.removeCallbacksAndMessages(null);
            }
            if (null != bmandler) {
                bmandler.removeCallbacksAndMessages(null);
            }
            if (null != myhread) {
                myhread.stop();
                myhread.destroy();
            }
            mapView.onDestroy();

            deactivate();//停止定位
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
        if (lm != null) {
            lm.removeUpdates(this);
            if (locationListenerb != null) {
                lm.removeUpdates(locationListenerb);
                locationListenerb = null;
            }

        }
        lm = null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    //画高德地图的起始点
    public void hua(int id, double jin, double wei) {
        com.amap.api.maps.model.MarkerOptions mo = new com.amap.api.maps.model.MarkerOptions();
        mo.position(new LatLng(jin, wei));
        mo.draggable(true);
        // mo.icon(BitmapDescriptorFactory.defaultMarker());
        if (id == 1) {
            bm = BitmapFactory.decodeResource(getResources(), R.mipmap.qidian);
            mo.icon(BitmapDescriptorFactory.fromBitmap(bm));
            aMap.addMarker(mo);
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.mipmap.zhongdian);
            mo.icon(BitmapDescriptorFactory.fromBitmap(bm));
            aMap.addMarker(mo);
        }
    }


    /**
     * 点击地图其他地方时，隐藏InfoWindow,和popWindow弹出框
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.hideInfoWindow();//隐藏InfoWindow框
        }
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 高德地图截屏
     */
    public class Gaode implements AMap.OnMapScreenShotListener {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            try {
                // 保存在SD卡根目录下，图片为png格式。
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "eees.png");
                boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ifSuccess) {

                    fugaiwu.setVisibility(View.VISIBLE);
                    fugaiwu.setImageBitmap(bitmap);
                    Date timedf = new Date();
                    SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String xXXXdf = formatdf.format(timedf);
                    String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                    ScreenShot.shoot(OutdoorCyclingActivityStar.this, new File(filePath));
                    Common.showShare(OutdoorCyclingActivityStar.this, null, false, filePath);
                } else {
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {
        }
    }


    /**
     * 查询天气
     */
    private void chaxuntianqi() {
        //判断网络是否连接
        Boolean is = ConnectManages.isNetworkAvailable(OutdoorCyclingActivityStar.this);
        if (is == true) {
            //RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET, getWeathers, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //  System.out.print("response"+response);
                            try {
                                String weathera = response.optString("now");
                                JSONObject AAA = new JSONObject(weathera);
                                String description = AAA.getString("description").toString();//空气质量
                                String imgurl = AAA.getString("img_url").toString();//图片地址
                                String pm25 = AAA.getString("pm25").toString();//pm25
                                String temp_c = AAA.getString("temp_c").toString();//temp_c
                                try {
                                    if ("null" != imgurl) {
                                        Mgps.put("image", imgurl);//图片地址
                                        BitmapUtils bitmapUtils = new BitmapUtils(OutdoorCyclingActivityStar.this);
                                        BitmapDisplayConfig config = new BitmapDisplayConfig();
                                        // 设置图片的分辨率
                                        BitmapSize size = new BitmapSize(500, 500);
                                        config.setBitmapMaxSize(size);
                                        bitmapUtils.display(kongqiimage, imgurl);
                                    } else {
                                        Mgps.put("image", "http://www.sinaimg.cn//dy//weather//main//index14//007//icons_128_wt//w_03_30_00.png");//图片地址
                                        BitmapUtils bitmapUtils = new BitmapUtils(OutdoorCyclingActivityStar.this);
                                        BitmapDisplayConfig config = new BitmapDisplayConfig();
                                        // 设置图片的分辨率
                                        BitmapSize size = new BitmapSize(500, 500);
                                        config.setBitmapMaxSize(size);
                                        bitmapUtils.display(kongqiimage, "http://www.sinaimg.cn//dy//weather//main//index14//007//icons_128_wt//w_03_30_00.png");
                                    }
                                    if ("null" != description) {
                                        Mgps.put("description", description);//空气质量
                                        if (description.equals("优")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.good));
                                        } else if (description.equals("良")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.good));
                                        } else if (description.equals("轻度污染")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.mild_pollution));
                                        } else if (description.equals("中度污染")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.moderate_pollution));
                                        } else if (description.equals("重度污染")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.heavy_pollution));
                                        } else if (description.equals("严重污染")) {
                                            kongqizhiliang.setText(getResources().getString(R.string.serious_pollution));
                                        }
                                    } else {
                                        Mgps.put("description", "N/A");//空气质量
                                    }
                                    if ("null" != AAA.getString("temp_c").toString()) {
                                        Mgps.put("temp", AAA.getString("temp_c").toString());//温度\
                                    } else {
                                        Mgps.put("temp", "N/A");//温度\
                                    }
                                    if ("null" != pm25) {
                                        Mgps.put("pm25", AAA.getString("pm25").toString());//pm25
                                    } else {
                                        Mgps.put("pm25", "N/A");//pm25
                                    }
                                    wendu.setTextSize(6);
                                    if ("null" != temp_c) {
                                        wendu.setText(temp_c);
                                    } else {
                                        wendu.setText("N/A");
                                    }


                                } catch (Exception E) {
                                    E.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
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
            MyApp.getRequestQueue().add(jsonRequest);
            //requestQueue.add(jsonRequest);
        } else {
        }
    }
}

