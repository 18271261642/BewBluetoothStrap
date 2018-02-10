package com.example.bozhilun.android.h9;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.bean.HeartDataBean;
import com.example.bozhilun.android.h9.utils.H9HeathtestAdapter;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/15 13:38
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HeartHistoryListActivity extends WatchBaseActivity {

    @BindView(R.id.h9HeartHistoryLV)
    ListView h9HeartHistoryLV;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.swipe_refreshList)
    SwipeRefreshLayout swipeRefreshList;
    @BindView(R.id.bar_mores)
    TextView barMores;

    private SubscriberOnNextListener subscriberOnNextListener;
    private CommonSubscriber commonSubscriber;
    List<HeartDataBean.ManualBean> manual;
    //    private List<Map<String, Object>> mapList = new ArrayList<>();
    private H9HeathtestAdapter heathtestAdapter;

    /**
     * 获取心率数据
     */
    private void getHeartData(String time) {
        if (manual != null) {
            manual.clear();
            heathtestAdapter.notifyDataSetChanged();
        }
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", (String) SharedPreferencesUtils.readObject(this, "mylanmac"));
        map.put("userId", (String) SharedPreferencesUtils.readObject(this, "userId"));
        map.put("date", time);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getHeartD, mapjson);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 8888:
                    heathtestAdapter.notifyDataSetChanged();
                    break;
                case 1001:
                    swipeRefreshList.setRefreshing(false);
//                    mapList.clear();
                    if (manual != null) {
                        manual.clear();
                        heathtestAdapter.notifyDataSetChanged();
                    }
                    String times = (String) SharedPreferencesUtils.getParam(H9HeartHistoryListActivity.this, "times", B18iUtils.getSystemDatasss());
//                    getH9HeartHistory();
                    getHeartData(times);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h9_hearthistory);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.heart_repor));
        barMores.setText("data_time");
//        getH9HeartHistory();

        swipeRefreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshList.setRefreshing(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1001;
                        handler.sendMessage(message);
                    }
                }, 3 * 1000);
            }
        });

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (result != null) {
                    HeartDataBean heartDataBean = new Gson().fromJson(result, HeartDataBean.class);
                    manual = heartDataBean.getManual();
                    if (manual != null) {
                        heathtestAdapter = new H9HeathtestAdapter(H9HeartHistoryListActivity.this, manual);
                        h9HeartHistoryLV.setAdapter(heathtestAdapter);
                        heathtestAdapter.notifyDataSetChanged();
                        Message message = new Message();
                        message.what = 8888;
                        handler.sendMessage(message);
                    }
                }
            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
//                startActivity(SearchDeviceActivity.class);
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtils.setParam(this, "times", B18iUtils.getSystemDatasss());
        getHeartData(B18iUtils.getSystemDatasss());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

//    JSONObject jsonObject;

//    private void getH9HeartHistory() {
//        Boolean is = ConnectManages.isNetworkAvailable(H9HeartHistoryListActivity.this);
//        if (is == true) {
//            RequestQueue requestQueue = Volley.newRequestQueue(H9HeartHistoryListActivity.this.getApplicationContext());
//            try {
//                //查询登录标记
//                jsonObject = new JSONObject();
//                jsonObject.put("userId", SharedPreferencesUtils.readObject(H9HeartHistoryListActivity.this, "userId"));
//                jsonObject.put("deviceCode", SharedPreferencesUtils.readObject(H9HeartHistoryListActivity.this, "mylanmac"));
//                Date timedf = new Date();
//                SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd");
//                String xXXXdf = formatdf.format(timedf);
//                jsonObject.put("date", xXXXdf);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            System.out.print("cfhj" + jsonObject.toString());
//            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, URLs.HTTPs + URLs.getHeartD, jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            MyLogUtil.i("longfeimadnag" + response.toString());
//                            String heartRate = response.optString("heartRate");
//                            String avgHeartRate = response.optString("avgHeartRate");
//                            String manual = response.optString("manual");
//                            if (response.optString("resultCode").equals("001")) {
//                                try {
//                                    if (heartRate.equals("[]")) {
//
//                                    } else {
//                                        JSONArray oArrb = new JSONArray(manual);
//                                        for (int i = 0; i < oArrb.length(); i++) {
//                                            JSONObject jo = (JSONObject) oArrb.get(i);
//                                            String date = jo.getString("rtc");    //星期几
//                                            String stepNumbera = jo.getString("heartRate");//心率时间段
//
//                                            Map<String, Object> map2 = new HashMap<String, Object>();
//                                            map2.put("title", date);
//                                            map2.put("info", stepNumbera + getResources().getString(R.string.BPM));
//                                            mapList.add(map2);
//                                        }
//                                        // 把添加了Map的List和Context传进适配器mListViewAdapter
//                                        Collections.reverse(mapList);     //实现list集合逆序排列
//                                        heathtestAdapter = new HeathtestAdapter(H9HeartHistoryListActivity.this, mapList);
//                                        h9HeartHistoryLV.setAdapter(heathtestAdapter);
//                                        heathtestAdapter.notifyDataSetChanged();
//                                        Message message = new Message();
//                                        message.what = 8888;
//                                        handler.sendMessage(message);
//
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("Accept", "application/json");
//                    headers.put("Content-Type", "application/json; charset=UTF-8");
//                    return headers;
//                }
//            };
//            requestQueue.add(jsonRequest);
//        } else {
//            Toast.makeText(H9HeartHistoryListActivity.this, R.string.wangluo, Toast.LENGTH_SHORT).show();
//        }
//    }

    @OnClick({R.id.image_back, R.id.bar_mores})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                setSlecteDateTime();
                break;
        }
    }


    private void setSlecteDateTime() {
        View view = LayoutInflater.from(H9HeartHistoryListActivity.this).inflate(R.layout.h9_pop_date_item, null, false);
        PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        //设置pop数据
        setPopContent(popupWindow, view);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹出框消失.  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));//new BitmapDrawable()
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置可以点击
        popupWindow.setTouchable(true);
        //从顶部显示
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.TOP, 0, 0);
    }

    private void setPopContent(final PopupWindow popupWindow, View view) {
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.h9_calender);
        calendarView.setEnabled(false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("----选择的日期是-----", year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                getHeartData(year + "-" + (month + 1) + "-" + dayOfMonth);
                SharedPreferencesUtils.setParam(H9HeartHistoryListActivity.this, "times", year + "-" + (month + 1) + "-" + dayOfMonth);
                popupWindow.dismiss();
            }
        });
    }
}
