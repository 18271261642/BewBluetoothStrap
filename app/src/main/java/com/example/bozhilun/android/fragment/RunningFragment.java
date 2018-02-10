package com.example.bozhilun.android.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.OutdoorCyclingActivityStar;
import com.example.bozhilun.android.activity.wylactivity.SportsHistoryActivity;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.DiffuseView;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.StartFlick;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.PlusCloudyView;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.RainView;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.SnowView;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.markushi.ui.RevealColorView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by thinkpad on 2017/3/23.
 */

public class RunningFragment extends BaseFragment {
    @BindView(R.id.runningdistance_tv)
    TextView runningdistanceTv;
    @BindView(R.id.cumulative_number_movements_tv)
    TextView cumulativeNumberMovementsTv;
    @BindView(R.id.reveal)
    RevealColorView revealColorView;
    @BindView(R.id.star_circlebtn)
    DiffuseView starCirclebtn;

    @BindView(R.id.PlusCloudyView)
    PlusCloudyView plusCloudyView;//多云
    @BindView(R.id.SnowView)
    SnowView snowView;//下雪
    @BindView(R.id.RainView)
    RainView rainView;//下雨\
    //两片叶子
    @BindView(R.id.weather_iv2)
    ImageView weather_iv2;
    @BindView(R.id.weather_iv)
    ImageView weather_iv;
    @BindView(R.id.weather_iv3)
    ImageView weather_iv3;
    Boolean istankuan = false;//有没有弹框
    int check;//权限
    private static String getWeathers = "http://apis.berace.com.cn/watch/user/getWeathers";//天气接口
    @BindView(R.id.running_tv_title)
    TextView runningTvTitle;
    @BindView(R.id.running_toolbar)
    Toolbar runningToolbar;
    @BindView(R.id.gsp_tv)
    TextView gspTv;
    @BindView(R.id.my_ralativity)
    RelativeLayout myRalativity;
    Unbinder unbinder;

    View rootView;
   // ImageView gpsriss;

    //动态申请权限的测试方法
    public void test() {
        // 要申请的权限 数组 可以同时申请多个权限
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (Build.VERSION.SDK_INT >= 23) {
            //如果超过6.0才需要动态权限，否则不需要动态权限
            //如果同时申请多个权限，可以for循环遍历
            int check = ContextCompat.checkSelfPermission(getActivity(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                boolean gpsisrun = Common.isOPen(getActivity());
                if (gpsisrun) {
                    //写入你需要权限才能使用的方法
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    String provider = LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(provider, 1000, 1, ll);
                    locationManager.addGpsStatusListener(statusListener);
                }
            } else {
                Common.openGPS(getActivity());//强制开启好了
            }
        } else {
            boolean gpsisrun = Common.isOPen(getActivity());
            if (gpsisrun) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                String provider = LocationManager.GPS_PROVIDER;
                locationManager.requestLocationUpdates(provider, 1000, 1, ll);
                locationManager.addGpsStatusListener(statusListener);
            } else {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
            }
        }
    }

    @Override
    protected void initViews() {
       // gpsriss = (ImageView) rootView.findViewById(R.id.gps_riss);
        String blueName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanya");
        if (!WatchUtils.isEmpty(blueName)) {
            if (blueName.equals("B18I") || blueName.equals("W06X")) {
                runningToolbar.setVisibility(View.VISIBLE);
                runningTvTitle.setText(R.string.running);
                runningTvTitle.setClickable(true);
                Drawable drawable = getResources().getDrawable(R.mipmap.jiantou1);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                runningTvTitle.setCompoundDrawables(null, null, drawable, null);
                runningTvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(getActivity()).title(R.string.select_running_mode).items(R.array.select_running_mode).itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                //0表示户外,1表骑行
                                if (which == 0) {
                                    SharedPreferencesUtils.saveObject(getActivity(), "type", "0");
                                } else {
                                    SharedPreferencesUtils.saveObject(getActivity(), "type", "1");
                                }
                                return false;
                            }
                        }).positiveText(R.string.select).show();
                    }
                });
            }else{
                runningToolbar.setVisibility(View.GONE);
            }
        }

        boolean isopenGPS = Common.openGPS(getActivity());//查看gps开关，没有开就强制开启
        if (isopenGPS) {
            test();
        }
        starCirclebtn.start();
        starCirclebtn.setDiffuseWidth(10);//设置扩散圆宽度
        starCirclebtn.setColor(Color.parseColor("#1979ca"));
        starCirclebtn.Typeface(getResources().getString(R.string.star));
        starCirclebtn.setmCoreRadius(200);
        StartFlick.startFlick(starCirclebtn);
        //显示当前的运动次数和运动距离
        try {
            if (null != SharedPreferencesUtils.readObject(getActivity(), "Exercisetimes")) {
                cumulativeNumberMovementsTv.setText(getResources().getString(R.string.leiji) + SharedPreferencesUtils.readObject(getActivity(), "Exercisetimes").toString() + getResources().getString(R.string.cishu) + " >");
            } else {
                cumulativeNumberMovementsTv.setText(getResources().getString(R.string.cumulative_number_of_movements));
            }
            if (null != SharedPreferencesUtils.readObject(getActivity(), "Movingdistance")) {
                DecimalFormat df = new DecimalFormat("###.00");
                Double Distans = Double.parseDouble(SharedPreferencesUtils.readObject(getActivity(), "Movingdistance").toString());
                runningdistanceTv.setText(df.format(Distans));

                if (String.valueOf(df.format(Distans)).length() < 3) {
                    runningdistanceTv.setText("0" + df.format(Distans));
                } else if (String.valueOf(df.format(Distans)).length() == 3) {
                    runningdistanceTv.setText("0" + df.format(Distans));
                } else {
                    runningdistanceTv.setText(df.format(Distans));
                }
            } else {
                runningdistanceTv.setText("0.00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        plusCloudyView.start();
        chaxuntianqi();//查询当前的天气和空气质量

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(plusCloudyView != null){
            plusCloudyView.stop();
        }

        Log.e("", "---onDestroy-----runn----");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_running;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
            if (null != Context.LOCATION_SERVICE) {
                if (null != Context.LOCATION_SERVICE) {

                    LocationManager locationManager = (LocationManager) MyApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
                    GpsStatus status = locationManager.getGpsStatus(null); //取当前状态
                    String satelliteInfo = updateGpsStatus(event, status);
                    //卫星个数必须大于7个菜可以定位
                    if (!satelliteInfo.equals("")) {
                        if (Integer.valueOf(satelliteInfo) <= 7) {
                           // gpsriss.setImageResource(R.mipmap.gpsone);
                        } else if (12 > Integer.valueOf(satelliteInfo) && Integer.valueOf(satelliteInfo) > 7) {
                           // gpsriss.setImageResource(R.mipmap.gpstwo);
                        } else {
                           // gpsriss.setImageResource(R.mipmap.gpsthere);
                        }
                    }

                }
            }
        }
    };

    public String updateGpsStatus(int event, GpsStatus status) {
        StringBuilder sb2 = new StringBuilder("");
        if (status == null) {
            sb2.append(0);//没有卫星
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            int count = 0;
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                numSatelliteList.add(s);
                count++;
            }
            sb2.append(numSatelliteList.size());
        }
        return sb2.toString();
    }


    @OnClick({R.id.star_circlebtn, R.id.runningdistance_tv, R.id.cumulative_number_movements_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.star_circlebtn:
                //跳转到详情
                startActivity(new Intent(getActivity(), OutdoorCyclingActivityStar.class));
                break;
            case R.id.runningdistance_tv:
                startActivity(new Intent(getActivity(), SportsHistoryActivity.class));
                break;
            case R.id.cumulative_number_movements_tv:
                startActivity(new Intent(getActivity(), SportsHistoryActivity.class));
                break;
        }


    }


    private int getColor(View view) {
        return Color.parseColor((String) view.getTag());
    }

    //定位监听
    LocationListener ll = new LocationListener() {
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
    };


    /**
     * 查询天气
     */
    private void chaxuntianqi() {
        //判断网络是否连接
        Boolean is = ConnectManages.isNetworkAvailable(getActivity());
        if (is == true) {
            //RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET, getWeathers, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("开跑", "-----天气返回--" + response.toString());
                            if (response != null) {

                                analysisData(response); //解析返回的天气信息
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("开跑", "-----天气error-----" + error.getMessage());
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
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1f));
            MyApp.getRequestQueue().add(jsonRequest);
            // requestQueue.add(jsonRequest);
        } else {
        }
    }

    //解析返回的天气信息
    private void analysisData(JSONObject response) {
        System.out.print("tianqi" + response);
        try {
            String weathera = response.optString("now");
            if (!WatchUtils.isEmpty(weathera) && weathera.length() > 2) {
                JSONObject AAA = new JSONObject(weathera);
                String weatherName = AAA.getString("weatherName").toString();//天气
                String pm25 = AAA.getString("description").toString();//空气质量
                MyLogUtil.i("weatherName" + weatherName + "--" + pm25);
                if ("null" != pm25) {
                    if (pm25.equals("优")) {
                        weather_iv2.setImageResource(R.mipmap.kongqizhilaing1);
                        weather_iv.setImageResource(R.mipmap.kongqizhilaing1);
                        weather_iv3.setImageResource(R.mipmap.kongqizhilaing1);
                    } else if (pm25.equals("良")) {
                        weather_iv2.setImageResource(R.mipmap.kongqizhilaing1);
                        weather_iv3.setImageResource(R.mipmap.kongqizhilaing1);
                    } else if (pm25.equals("重度污染")) {
                        weather_iv2.setImageResource(R.mipmap.kongqizhilaing2);
                        weather_iv.setImageResource(R.mipmap.kongqizhilaing2);
                    } else if (pm25.equals("严重污染")) {
                        weather_iv2.setImageResource(R.mipmap.kongqizhilaing2);
                        weather_iv.setImageResource(R.mipmap.kongqizhilaing2);
                        weather_iv3.setImageResource(R.mipmap.kongqizhilaing2);
                    }
                }
                if ("null" != weatherName) {
                    if (weatherName.contains("云") || weatherName.contains("阴")) {
                        plusCloudyView.setVisibility(View.VISIBLE);
                    } else if (weatherName.contains("雨")) {
                        rainView.setVisibility(View.VISIBLE);
                    } else if (weatherName.contains("雪")) {
                        snowView.setVisibility(View.VISIBLE);
                    } else {
                        plusCloudyView.setVisibility(View.GONE);
                        rainView.setVisibility(View.GONE);
                        snowView.setVisibility(View.GONE);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
