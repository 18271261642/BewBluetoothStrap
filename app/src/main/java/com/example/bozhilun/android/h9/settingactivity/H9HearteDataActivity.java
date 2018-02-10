package com.example.bozhilun.android.h9.settingactivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.AxisValue;
import com.example.bozhilun.android.B18I.b18ibean.Line;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.b18iview.LeafLineChart;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.bean.HeartDataBean;
import com.example.bozhilun.android.h9.utils.H9HearteDataAdapter;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 心率数据
 * @author： 安
 * @crateTime: 2017/10/31 16:50
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9HearteDataActivity extends WatchBaseActivity {


    @BindView(R.id.leaf_chart)
    LeafLineChart leafLineChart;
    @BindView(R.id.heartedata_list)
    ListView heartedataList;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    List<HeartDataBean.HeartRateBean> heartDataList;
    H9HearteDataAdapter dataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_hearte_data_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.heart_rate));
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("------返回--", result);
                if (result != null) {
                    HeartDataBean heartDataBean = new Gson().fromJson(result, HeartDataBean.class);
                    heartDataList = heartDataBean.getHeartRate();
//                    heartDataList = getHeartDataList(result);
                    if (heartDataList != null) {
                        dataAdapter = new H9HearteDataAdapter(H9HearteDataActivity.this, heartDataList);
//                    for (int i = 0; i < heartDataList.size(); i++) {
//                        HeartDataBean.HeartRateBean heartRateBean = heartDataList.get(i);
//                        Log.d("-----数据-ssss---", heartRateBean.getRtc() + "===" + heartRateBean.getHeartRate());
//                    }
                        heartedataList.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                    }
                }
                initLineCharts();
                leafLineChart.postInvalidate();
            }
        };
    }


    /**
     * 获取心率数据
     */
    private void getHeartData(String time) {
        if (heartDataList != null) {
            heartDataList.clear();
            dataAdapter.notifyDataSetChanged();
        }
        initLineCharts();
        leafLineChart.postInvalidate();
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", (String) SharedPreferencesUtils.readObject(this, "mylanmac"));
        map.put("userId", (String) SharedPreferencesUtils.readObject(this, "userId"));
        map.put("date", time);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getHeartD, mapjson);//getHeartD, mapjson);
    }


//    /**
//     * 解析心率数据
//     *
//     * @param heartRates
//     */
//    List<HeartDataBean.HeartRateBean> heartRateList;
//
//    private List<HeartDataBean.HeartRateBean> getHeartDataList(String heartRates) {
//        if (heartRates == null) {
//            return null;
//        }
//        HeartDataBean heartDataBean = new Gson().fromJson(heartRates, HeartDataBean.class);
//        heartRateList = heartDataBean.getHeartRate();
//        Collections.sort(heartRateList, new Comparator<HeartDataBean.HeartRateBean>() {
//            @Override
//            public int compare(HeartDataBean.HeartRateBean watchDataDatyBean, HeartDataBean.HeartRateBean t1) {
//                return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
//            }
//        });
//        return heartRateList;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        getHeartData(B18iUtils.getSystemDatasss());
    }

    /*************   --------折线图----------    *************/

    private void initLineCharts() {
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
        lines.add(getFoldLineTest());
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
    private Line getFoldLineTest() {

        List<PointValue> pointValues = new ArrayList<>();
        List<String> timeString = new ArrayList<>();
        List<Integer> heartString = new ArrayList<>();

        if (heartDataList != null) {
            for (int i = 0; i < heartDataList.size(); i++) {
                if (heartDataList.get(i) != null) {
                    int avg = heartDataList.get(i).getHeartRate();
                    String sysTim = B18iUtils.interceptString(heartDataList.get(i).getRtc(), 11, 13);
                    if (!timeString.contains(sysTim)) {
                        timeString.add(sysTim);
                        heartString.add(avg);
                    }
                    //                Collections.sort(timeString);
                } else {
                    if (heartString != null) {
                        heartString.clear();
                    }
                    if (timeString != null) {
                        timeString.clear();
                    }
                    for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(B18iUtils.getSystemTimer(), 11, 13)); j++) {
                        heartString.add(0);
                        timeString.add(j + "");
                    }
                }


            }
        } else {
            for (int j = 0; j < (int) Integer.valueOf(B18iUtils.interceptString(B18iUtils.getSystemTimer(), 11, 13)); j++) {
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


    private void setSlecteDateTime() {
        View view = LayoutInflater.from(H9HearteDataActivity.this).inflate(R.layout.h9_pop_date_item, null, false);
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
                popupWindow.dismiss();
            }
        });
    }

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
}
