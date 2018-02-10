package com.example.bozhilun.android.h9.settingactivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18isystemic.HeartRateActivity;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.HeartRateTestActivity;
import com.example.bozhilun.android.bean.Sleep;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.H9HeartHistoryListActivity;
import com.example.bozhilun.android.h9.bean.HeartDataBean;
import com.example.bozhilun.android.h9.utils.H9HeathtestAdapter;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.DensityUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.view.ChartView;
import com.google.gson.Gson;
import com.sdk.bluetooth.interfaces.BlueToothHeartRateListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.other.HeartStatus;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.operate.HeartOperater;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @aboutContent: 心率测试
 * @author： 安
 * @crateTime: 2017/11/1 17:34
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HearteTestActivity extends WatchBaseActivity {
    public static final String TAG = "H9HearteTestActivity";

    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.xinlv_value)
    TextView xinlvValue;
    @BindView(R.id.xinlv_celang)
    TextView xinlvCelang;
    @BindView(R.id.notest_state)
    RelativeLayout notestState;
    @BindView(R.id.test_state)
    ChartView testState;
    @BindView(R.id.xinlv_FrameLayout)
    FrameLayout xinlvFrameLayout;
    @BindView(R.id.hate_test)
    Button hateTest;
    @BindView(R.id.celiang_xinlv)
    LinearLayout celiangXinlv;
    private String is18i;
    private Timer timer = null;
    private TimerTask task = null;
    int count = -1;
    private Handler mHandler = new Handler(new MyCallBack());
    private int currentIndex;
    private String[] testData;
    int DATA = 0;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private CommonSubscriber commonSubscriber;
    @BindView(R.id.listheartetest)
    ListView listHearteTest;
    private List<HeartDataBean.ManualBean> manual;
    private H9HeathtestAdapter heathtestAdapter;
    private boolean one = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_hearte_test_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.heartcheck));
        whichDevice();//判断是B18i还是H9
        String testDataStr = getHeartRateDataFromAssets();
        testData = testDataStr.split(",");

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (result != null) {
                    manual = getHeartDataList(result);
//                    HeartDataBean heartDataBean = new Gson().fromJson(result, HeartDataBean.class);
//                    manual = heartDataBean.getManual();
                    if (manual != null) {
                        heathtestAdapter = new H9HeathtestAdapter(H9HearteTestActivity.this, manual);
                        listHearteTest.setAdapter(heathtestAdapter);
                        heathtestAdapter.notifyDataSetChanged();
//                        Message message = new Message();
//                        message.what = 8888;
//                        handler.sendMessage(message);
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (one) {
            whichDevice();//判断是B18i还是H9
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesUtils.setParam(this, "times", B18iUtils.getSystemDatasss());
        getHeartData(B18iUtils.getSystemDatasss());
    }

    @Override
    protected void onPause() {
        super.onPause();
        one = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new HeartStatus(commandResultCallback, 0));
    }


    /**
     * 心率数据上传后再次获取
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            if (msg.what == 10086) {
                handler.removeMessages(10086);
                getHeartData(B18iUtils.getSystemDatasss());
            }
        }
    };

    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        if (is18i.equals("B18i")) {

        } else if (is18i.equals("H9")) {
            Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    subscriber.onNext("dialog提示打开ok");
                    // 心率和血压只要监听即可，不管是手动打开或者是App方法打开都有可以接收到收到
                    // 检查心率数据（测量监听）
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .setBluetoothHeartRateListener(new BlueToothHeartRateListener() {

                                @Override
                                public void onHeartRateDatas(int data) {
                                    Log.i(TAG, "心率数据---->>>>" + data);
                                    DATA = data;
                                    xinlvValue.setText(String.valueOf(DATA));
                                    showLoadingDialog(getResources().getString(R.string.dlog));
                                    testEnd();
                                    //上传至后台
                                    uploadHeartToServer(data + "");
                                    //上传至后台
                                    showLoadingDialog(getResources().getString(R.string.dlog));
                                    handler.sendEmptyMessageDelayed(10086, 5000);
                                }

                                @Override
                                public void onHeartRateDatasFinish() {
                                    Log.i(TAG, "心率数据finish---->>>>");
                                    DATA = 0;
                                    xinlvValue.setText(String.valueOf(DATA));
                                    testEnd();
                                }
                            });
                    subscriber.onNext("测量监听ok");
                    //获取心率状态
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new HeartStatus(commandResultCallback));
                    subscriber.onNext("获取心率状态ok");
                    subscriber.onCompleted();
                }
            });

            Observer<String> observer = new Observer<String>() {
                @Override
                public void onNext(String s) {
                    Log.d(TAG, "Item: " + s);
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "Completed!");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "Error!");
                }
            };
            observable.subscribe(observer);
        } else if (is18i.equals("B15P")) {

        }
    }


    /**
     * H9心率测量打开关闭监听
     */
    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof HeartStatus) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    closeLoadingDialog();
                    Log.d(TAG, "status:" + GlobalVarManager.getInstance().isHeartMeasure());
                    if (GlobalVarManager.getInstance().isHeartMeasure()) {
                        testStart();
                    } else {
                        testEnd();
                    }
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.d(TAG, "心率状态改变");
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };


    /**
     * 测量厚后的心率值上传至后台
     *
     * @param heartData
     */
    private void uploadHeartToServer(String heartData) {
        try {
            JSONObject map = new JSONObject();
            map.put("userId", SharedPreferencesUtils.readObject(H9HearteTestActivity.this, "userId"));
            map.put("deviceCode", SharedPreferencesUtils.readObject(H9HearteTestActivity.this, "mylanmac"));
            map.put("systolic", "00");
            map.put("stepNumber", "00");
            Date timedf = new Date();
            SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String xXXXdf = formatdf.format(timedf);
            map.put("date", xXXXdf);
            map.put("heartRate", heartData);
            map.put("status", "1");
            JSONArray jsonArray = new JSONArray();
            Object jsonArrayb = jsonArray.put(map);
            JSONObject mapB = new JSONObject();
            mapB.put("data", jsonArrayb);
            String mapjson = mapB.toString();
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, H9HearteTestActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private String getHeartRateDataFromAssets() {
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

    List<Integer> heaterList = new ArrayList<>();

    @OnClick({R.id.image_back, R.id.bar_mores, R.id.test_state, R.id.hate_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.bar_mores:
                setSlecteDateTime();
                break;
            case R.id.hate_test:    //测量心率
                //判断是否已连接手表
                if (MyCommandManager.DEVICENAME != null) {
                    if (is18i.equals("H9")) {
                        if (hateTest.getText().equals(getResources().getString(R.string.measure))) {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new HeartStatus(commandResultCallback, 1));
                            testStart();
                        } else {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new HeartStatus(commandResultCallback, 0));
                            testEnd();
                        }
                    } else if (is18i.equals("B15P")) {
                        if (hateTest.getText().equals(getResources().getString(R.string.measure))) {
                            MyApp.getVpOperateManager().startDetectHeart(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int i) {

                                }
                            }, new IHeartDataListener() {

                                @Override
                                public void onDataChange(HeartData heart) {
                                    String message = "heart:\n" + heart.toString();
                                    Log.d(TAG, message);
                                    HeartOperater.HeartStatus heartStatus = heart.getHeartStatus();
                                    if (heartStatus != null) {
                                        heaterList.add(heart.getData());
                                    }
                                }
                            });
                            testStart();
                        } else {
                            MyApp.getVpOperateManager().stopDetectHeart(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int i) {
                                    testEnd();
                                    int heateData = 0;
                                    for (int h = 0; h < heaterList.size(); h++) {
                                        heateData += heaterList.get(h);
                                    }
                                    DATA = heateData / heaterList.size();
                                    xinlvValue.setText(String.valueOf(DATA));
                                    heaterList.clear();

                                    //上传至后台
                                    uploadHeartToServer(DATA + "");
                                    //上传至后台
                                    showLoadingDialog(getResources().getString(R.string.dlog));
                                    handler.sendEmptyMessageDelayed(10086, 5000);
                                }
                            });
                        }


                    }

                } else {
                    ToastUtil.showToast(H9HearteTestActivity.this, "");
                }

                break;
        }
    }

    /**
     * 开始心率曲线图
     */
    private void testStart() {
        hateTest.setText(R.string.suspend);
        notestState.setVisibility(View.GONE);
        testState.setVisibility(View.VISIBLE);
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    count++;
                    Message msg = new Message();
                    msg.what = count;
                    mHandler.sendMessage(msg);
                }
            };
            timer.schedule(task, 0, 10);
        }
    }

    /**
     * 结束心率曲线图
     */
    private void testEnd() {
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
        currentIndex = 0;
        count = -1;
        testState.ClearList();
        hateTest.setText(R.string.measure);
        testState.setVisibility(View.GONE);
        notestState.setVisibility(View.VISIBLE);
    }

    /**
     * 用Handle倒计时心率曲线图
     */
    private class MyCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (3500 == msg.what) {
                testEnd();
                if (is18i.equals("B15P")) {
                    MyApp.getVpOperateManager().stopDetectHeart(new IBleWriteResponse() {
                        @Override
                        public void onResponse(int i) {
                            testEnd();
                            int heateData = 0;
                            for (int h = 0; h < heaterList.size(); h++) {
                                heateData += heaterList.get(h);
                            }
                            DATA = heateData / heaterList.size();
                            xinlvValue.setText(String.valueOf(DATA));
                            heaterList.clear();
                            //上传至后台
                            uploadHeartToServer(DATA + "");
                            //上传至后台
                            showLoadingDialog(getResources().getString(R.string.dlog));
                            handler.sendEmptyMessageDelayed(10086, 5000);
                        }
                    });
                }
            }

            if (currentIndex > (testData.length - 1)) {
                currentIndex = 0;
            }
            testState.AddPointToList(DensityUtils.dip2px(H9HearteTestActivity.this, (int) (-(Integer.valueOf(testData[currentIndex].trim()) - 2048) * 0.4f) + 120));
            currentIndex++;
            return false;
        }
    }


    /**
     * 获取心率数据
     */
    private void getHeartData(String time) {
        closeLoadingDialog();
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


    /**
     * 解析心率数据
     *
     * @param heartRates
     */
    List<HeartDataBean.ManualBean> heartRateList;

    private List<HeartDataBean.ManualBean> getHeartDataList(String heartRates) {
        if (heartRates == null) {
            return null;
        }
        HeartDataBean heartDataBean = new Gson().fromJson(heartRates, HeartDataBean.class);
        heartRateList = heartDataBean.getManual();
        if (heartRateList == null) {
            return null;
        }
        Collections.sort(heartRateList, new Comparator<HeartDataBean.ManualBean>() {
            @Override
            public int compare(HeartDataBean.ManualBean watchDataDatyBean, HeartDataBean.ManualBean t1) {
                return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
            }
        });
        return heartRateList;
    }

    /**
     * 选择日期的pop Window
     */
    private void setSlecteDateTime() {
        View view = LayoutInflater.from(H9HearteTestActivity.this).inflate(R.layout.h9_pop_date_item, null, false);
        PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
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

    /**
     * popWindow上的操作
     *
     * @param popupWindow
     * @param view
     */
    private void setPopContent(final PopupWindow popupWindow, View view) {
        view.findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView viewById = (TextView) view.findViewById(R.id.bar_titles);
        viewById.setText(getResources().getString(R.string.history_times));
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.h9_calender);
        calendarView.setEnabled(false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("----选择的日期是-----", year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                getHeartData(year + "-" + (month + 1) + "-" + dayOfMonth);
                SharedPreferencesUtils.setParam(H9HearteTestActivity.this, "times", year + "-" + (month + 1) + "-" + dayOfMonth);
                popupWindow.dismiss();
            }
        });
    }
}
