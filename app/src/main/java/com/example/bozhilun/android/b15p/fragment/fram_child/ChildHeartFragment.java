package com.example.bozhilun.android.b15p.fragment.fram_child;


import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.AxisValue;
import com.example.bozhilun.android.B18I.b18ibean.Line;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.b18iview.LeafLineChart;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.fragment.B15pRecordFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.MyNewHandler;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.B15pBaseFragment;
import com.example.bozhilun.android.h9.h9monitor.UpDatasBase;
import com.example.bozhilun.android.h9.settingactivity.H9HearteDataActivity;
import com.example.bozhilun.android.h9.settingactivity.H9HearteTestActivity;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildHeartFragment extends B15pBaseFragment {
    private static final String TAG = "===>>ChildSleepFragment";
    @BindView(R.id.leaf_chart)
    LeafLineChart leafLineChart;

    @Override
    protected int setContentView() {
        return R.layout.b18i_leaf_linechart_view;
    }

    @Override
    protected void lazyLoad() {
        initLineCharts(halfHourRateDatas);
        getHearteDatas();
        refresh();
    }

    List<HalfHourRateData> halfHourRateDatas;

    private void getHearteDatas() {
        int today = 0;
        MyApp.getVpOperateManager().readOriginDataSingleDay(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new IOriginDataListener() {
            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {
                String message = "健康数据[5分钟]-返回:" + originData.toString();
                Log.d(TAG, message);
            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                halfHourRateDatas = originHalfHourData.getHalfHourRateDatas();
                Log.d(TAG, message);
                initLineCharts(halfHourRateDatas);
                setUpHerteDatas(halfHourRateDatas);
            }

            @Override
            public void onReadOriginProgress(float progress) {
                String message = "健康数据[5分钟]-读取进度:" + progress;
                Log.d(TAG, message);
            }

            @Override
            public void onReadOriginProgressDetail(int date, String dates, int all, int num) {

            }

            @Override
            public void onReadOriginComplete() {
                String message = "健康数据-读取结束";
                Log.d(TAG, message);
                //刷新完成----关闭
                if (B15pRecordFragment.getSwipeRefresh().isRefreshing()) {
                    B15pRecordFragment.getSwipeRefresh().setRefreshing(false);
                }
            }
        }, today, 1, 3);
    }

    /**
     * 上传心率数据
     *
     * @param halfHourRateDatas
     */
    private void setUpHerteDatas(List<HalfHourRateData> halfHourRateDatas) {
        String upHearteTime = (String) SharedPreferencesUtils.getParam(getContext(), "upHearteTime", "");
        if (!TextUtils.isEmpty(upHearteTime)) {
            String timeDifference = H9TimeUtil.getTimeDifference(upHearteTime, B18iUtils.getSystemDataStart());
            if (!TextUtils.isEmpty(timeDifference.trim())) {
                int number = Integer.valueOf(timeDifference.trim());
                int number2 = Integer.parseInt(timeDifference.trim());
                if (number >= 5 || number2 >= 5) {
                    for (HalfHourRateData heartData : halfHourRateDatas) {
                        if (heartData != null) {
                            TimeData time = heartData.time;
                            String stringTimer = getTimes(time);
                            UpDatasBase.upDataHearte(String.valueOf(heartData.rateValue), stringTimer);//上传心率
                        }
                    }
                    SharedPreferencesUtils.setParam(getContext(), "upHearteTime", B18iUtils.getSystemDataStart());
                }
            }
        } else {
            for (HalfHourRateData heartData : halfHourRateDatas) {
                if (heartData != null) {
                    TimeData time = heartData.time;
                    String stringTimer = getTimes(time);
                    UpDatasBase.upDataHearte(String.valueOf(heartData.rateValue), stringTimer);//上传心率
                }
            }
            SharedPreferencesUtils.setParam(getContext(), "upHearteTime", B18iUtils.getSystemDataStart());
        }
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        myNewHandler = null;
    }

    MyNewHandler myNewHandler;

    public void refresh() {
        myNewHandler = MyNewHandler.getInstance();
        myNewHandler.setMyMessage(new MyNewHandler.MyMessage() {
            @Override
            public void mHandler(Message msg) {
                Log.d(TAG, "=============" + "心率");
                if (msg.what == myNewHandler.getMessgeNumber()) {
                    myNewHandler.removeMessages(myNewHandler.getMessgeNumber());
                    if (B15pRecordFragment.getPAGES() == 1) {
                        Log.d(TAG, "-------刷新--------心率页面");
                        getHearteDatas();
                    }
                    B15pRecordFragment.getSwipeRefresh().setRefreshing(false);
                }
            }
        });
    }


    @OnClick({R.id.autoHeart_text, R.id.autoData_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.autoHeart_text:
                startActivity(new Intent(getContext(),
                        H9HearteTestActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("is18i", "B15P"));
                break;
            case R.id.autoData_text:
                startActivity(new Intent(getContext(),
                        H9HearteDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }


    /*************   --------折线图----------    *************/


    private void initLineCharts(List<HalfHourRateData> halfHourRateDatas) {
        Axis axisX = new Axis(getAxisValuesX());
        axisX.setShowText(true);
        axisX.setAxisLineColor(Color.parseColor("#43FFFFFF"));
        axisX.setAxisLineWidth(0.5f);
        axisX.setTextColor(Color.WHITE);
        axisX.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(true).setShowText(true);
        Axis axisY = new Axis(getAxisValuesY());
        axisY.setShowText(false);
        axisY.setTextColor(Color.WHITE);
        axisY.setAxisLineWidth(0f);
        axisY.setShowLines(false);
        axisY.setAxisColor(Color.parseColor("#FFFFFF")).setTextColor(Color.parseColor("#FFFFFF")).setHasLines(false).setShowText(true);
        leafLineChart.setAxisX(axisX);
        leafLineChart.setAxisY(axisY);
        List<Line> lines = new ArrayList<>();
        lines.add(getFoldLineTest(halfHourRateDatas));
        leafLineChart.setChartData(lines);
        leafLineChart.showWithAnimation(1000);
        leafLineChart.show();
        leafLineChart.invalidate();
    }

    /**
     * X轴值
     *
     * @return
     */
    private List<AxisValue> getAxisValuesX() {
        List<AxisValue> axisValues = new ArrayList<>();

        for (int i = 1; i < 24; i++) {
            AxisValue value = new AxisValue();
            if (i % 3 != 0) {
                value.setLabel("");
            } else {
                value.setLabel(i + "");
            }
//            if (i % 3 != 0) {
//                value.setLabel("");
//            } else {
//                value.setLabel(i + "");
//            }
            axisValues.add(value);
        }
//        for (int i = 0; i <= 8; i++) {
//            AxisValue value = new AxisValue();
//            value.setLabel(i * 3 + "");
//            axisValues.add(value);
//        }
//            value.setLabel(i * 3 + "");
//        for (int i = 0; i < heartRateDatas.size(); i++) {
//            long timestamp = heartRateDatas.get(i).timestamp;
//            Date date = new Date(timestamp);
//            SimpleDateFormat sd = new SimpleDateFormat("HH");
//            String format = sd.format(date);
//        }
        return axisValues;
    }

    /**
     * Y轴值
     *
     * @return
     */
    private List<AxisValue> getAxisValuesY() {
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AxisValue value = new AxisValue();
            value.setLabel("  ");
//            if (i != 0) {
//                value.setLabel(String.valueOf((i) * 50));
//            } else {
//                value.setLabel(" ");
//            }
            axisValues.add(value);
        }
        return axisValues;
    }

    /**
     * 设置值
     *
     * @return
     */
    private Line getFoldLineTest(List<HalfHourRateData> heartDatas) {

        List<PointValue> pointValues = new ArrayList<>();
        List<String> timeString = new ArrayList<>();
        List<Integer> heartString = new ArrayList<>();
        String systemTimer = B18iUtils.getSystemTimer();
        String s = B18iUtils.interceptString(systemTimer, 0, 10);

        if (heartDatas != null) {
            for (int i = 0; i < heartDatas.size(); i++) {
                TimeData time = heartDatas.get(i).time;
                String strTimes = getTimes(time);
//                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartDatas.get(i).time_stamp));//yyyy/MM/dd HH:mm:ss
                if (s.equals(B18iUtils.interceptString(strTimes, 0, 10))) {
//                    Log.d(TAG, heartDatas.get(i).toString());
                    if (heartDatas.get(i) != null) {
//                        int avg = heartDatas.get(i).heartRate_value;
                        int avg = heartDatas.get(i).rateValue;
                        String strTimes1 = getTimes(time);
                        String sysTim = B18iUtils.interceptString(strTimes1, 11, 13);
                        if (!timeString.contains(sysTim)) {
                            timeString.add(sysTim);
                            heartString.add(avg);
                        }
                        Collections.sort(timeString);
                    } else {
                        if (heartString != null) {
                            heartString.clear();
                        }
                        if (timeString != null) {
                            timeString.clear();
                        }
                        for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
                            heartString.add(0);
                            timeString.add(j + "");
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(systemTimer, 11, 13)); j++) {
                heartString.add(0);
                timeString.add(j + "");
            }
        }
        for (int i = 0; i < timeString.size(); i++) {
            PointValue value = new PointValue();
            value.setX(Integer.valueOf(timeString.get(i)) / 23f);
            value.setY(Integer.valueOf(heartString.get(i)) / 150f);
            pointValues.add(value);
        }

        Line line = new Line(pointValues);
        line.setLineColor(Color.parseColor("#FFFFFF"))
                .setLineWidth(2f)
                .setHasPoints(true)//是否显示点
                .setPointColor(Color.WHITE)
                .setCubic(false)
                .setPointRadius(2)
                .setFill(true)
                .setFillColor(Color.parseColor("#FFFFFF"))
                .setHasLabels(true)
                .setLabelColor(Color.parseColor("#FF00FF"));//0C33B5E5
        return line;
    }


    public String getTimes(TimeData time) {
        String year = String.valueOf(time.getYear());
        String month = String.valueOf(time.getMonth());
        String day = String.valueOf(time.getDay());
        String hour = String.valueOf(time.getHour());
        String minute = String.valueOf(time.getMinute());
        String second = String.valueOf(time.getSecond());
        if (time.getMonth() <= 9) {
            month = "0" + time.getMonth();
        }
        if (time.getDay() <= 9) {
            day = "0" + time.getDay();
        }
        if (time.getHour() <= 9) {
            hour = "0" + time.getHour();
        }
        if (time.getMinute() <= 9) {
            minute = "0" + time.getMinute();
        }
        if (time.getSecond() <= 9) {
            second = "0" + time.getSecond();
        }
        return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
    }
}
