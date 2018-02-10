package com.example.bozhilun.android.siswatch.data;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AvgHeartRate;
import com.example.bozhilun.android.bean.NewsSleepBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.H8ShareActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by sunjianhua on 2017/11/1.
 */

public class NewsH9DataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "NewsH9DataFragment";

    View newH9DataView;

    //步数统计图
    @BindView(R.id.newH9DataStepChartView)
    ColumnChartView newH9DataStepChartView;
    //心率统计图
    @BindView(R.id.newH9DataHeartChartView)
    ColumnChartView newH9DataHeartChartView;
    //睡眠统计图
    @BindView(R.id.newH9DataSleepChartView)
    ColumnChartView newH9DataSleepChartView;
    //步数显示tv
    @BindView(R.id.newH9DataStepShowTv)
    TextView newH9DataStepShowTv;
    //心率显示tv
    @BindView(R.id.newH9DataHeartShowTv)
    TextView newH9DataHeartShowTv;
    //睡眠显示tv
    @BindView(R.id.newH9DataSleepShowTv)
    TextView newH9DataSleepShowTv;

    Unbinder unbinder;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //步数的相关
    List<WatchDataDatyBean> stepList;
    @BindView(R.id.newH9DataWeekTv)
    TextView newH9DataWeekTv;
    @BindView(R.id.newH9DataMonthTv)
    TextView newH9DataMonthTv;
    @BindView(R.id.newH9DataYearTv)
    TextView newH9DataYearTv;
    @BindView(R.id.newH9DataSwipe)
    SwipeRefreshLayout newH9DataSwipe;
    @BindView(R.id.h8_data_titleTv)
    TextView h8DataTitleTv;
    @BindView(R.id.h8_data_titleLinImg)
    ImageView h8DataTitleLinImg;
    @BindView(R.id.h8_dataLinChartImg)
    ImageView h8DataLinChartImg;
    private ColumnChartData data;   //步数的图表数据源
    //步数数值
    private List<Integer> mValues;
    private List<String> stepXList; //x轴数据
    private Map<String, Integer> stepSumMap; //用于计算年的步数
    private List<String> tempList;  //用于计算年的步数的list

    //心率相关
    private List<AvgHeartRate> heartList;   //数据集合
    private ColumnChartData heartData;  //心率的图表
    private List<Integer> heartValues;  //心率数值
    private List<String> heartXList;    //心率X轴数据
    private List<String> tempHeartList; //心率中间list
    private Map<String, Integer> heartMap;   //计算心率年的map


    //睡眠相关
    private List<NewsSleepBean> newsSleepBeanList;  //数据源
    private ColumnChartData sleepColumnChartData;
    private List<Integer> sleepVlaues;  //睡眠的数值
    private List<String> sleepXList;    //睡眠X轴
    private Map<String, Integer> sumSleepMap;    //保存计算睡眠年的数据
    private List<String> tempSleepList; //

    SubscriberOnNextListener subscriberOnNextListener;
    CommonSubscriber commonSubscriber;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    if (newH9DataSwipe != null && newH9DataSwipe.isRefreshing()) {
                        newH9DataSwipe.setRefreshing(false);
                    }
                    clearClickTvStyle();
                    newH9DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                    newH9DataWeekTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        newH9DataView = inflater.inflate(R.layout.fragment_new_h9_data, container, false);
        unbinder = ButterKnife.bind(this, newH9DataView);

        initViews();

        newH9DataWeekTv.setTextColor(getResources().getColor(R.color.white));
        newH9DataWeekTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        getAllChartData(7);

        return newH9DataView;

    }

    //获取所有的数据
    private void getAllChartData(int week) {
        getStepsData(week); //获取步数
        getHeartRateData(week); //获取心率
        getSleepH9Data(week);   //获取睡眠
    }

    //获取心率
    private void getHeartRateData(final int week) {
        heartList = new ArrayList<>();
        heartXList = new ArrayList<>();
        String heartUrl = URLs.HTTPs + "/data/getHeartRateByTime";
        JSONObject heartJson = new JSONObject();
        try {
            heartJson.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            heartJson.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
            heartJson.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), week)));
            heartJson.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e(TAG, "----心率返回----" + result);
                if (result != null) {
                    try {
                        JSONObject heartJson = new JSONObject(result);
                        if (heartJson.getString("resultCode").equals("001")) {
                            String heartRate = heartJson.getString("heartRate");
                            Log.e(TAG, "----heartRate---" + heartRate);
                            if (!heartRate.equals("[]")) {
                                heartList = new Gson().fromJson(heartRate, new TypeToken<List<AvgHeartRate>>() {
                                }.getType());
                                if (week == 365) {    //年
                                    heartMap = new HashMap<>();
                                    heartMap.clear();
                                    heartValues = new ArrayList<>();
                                    heartValues.clear();
                                    int heartSum = 0;
                                    for (int i = 0; i < heartList.size(); i++) {
                                        String strDate = heartList.get(i).getRtc().substring(2, 7);
                                        if (heartMap.get(strDate) != null) {
                                            heartSum += heartList.get(i).getAvgHeartRate();
                                        }else{
                                            heartSum = heartList.get(i).getAvgHeartRate();
                                        }
                                        heartMap.put(strDate, heartSum);
                                    }
                                    tempHeartList = new ArrayList<>();
                                    tempHeartList.clear();
                                    for (Map.Entry<String, Integer> maps : heartMap.entrySet()) {
                                        tempHeartList.add(maps.getKey().trim());
                                    }
                                    //排序时间
                                    Collections.sort(tempHeartList, new Comparator<String>() {
                                        @Override
                                        public int compare(String o1, String o2) {
                                            return o1.compareTo(o2);
                                        }
                                    });
                                    heartXList.clear();
                                    for (int i = 0; i < tempHeartList.size(); i++) {
                                        heartValues.add(heartMap.get(tempHeartList.get(i)));
                                        heartXList.add(tempHeartList.get(i));
                                    }
                                    showHeartChartData(13);

                                } else {  //周或者月
                                    heartValues = new ArrayList<>();
                                    heartValues.clear();
                                    for (AvgHeartRate avgHeart : heartList) {
                                        heartValues.add(avgHeart.getAvgHeartRate()); //2017-10-10
                                        Log.e(TAG, "----xxx----" + avgHeart.getRtc().substring(5, avgHeart.getRtc().length()));
                                        heartXList.add(avgHeart.getRtc().substring(8, avgHeart.getRtc().length()));
                                    }
                                    //newH9DataHeartShowTv.setText(heartList.get(heartList.size()-1).getAvgHeartRate());
                                    showHeartChartData(heartList.size());
                                }

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, heartUrl, heartJson.toString());
    }

    //展示心率的图表
    private void showHeartChartData(int count) {
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = count;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; i++) {
            values = new ArrayList<>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; j++) {
                //为每一柱图添加颜色和数值
                float f = heartValues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(false);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(heartXList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        heartData = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        axisX.setTextSize(12);
        axisX.setMaxLabelChars(6);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        heartData.setAxisXBottom(axisX);
        //  data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        heartData.setValueLabelBackgroundColor(R.color.mpc_end_color);
        heartData.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        heartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        heartData.setValueLabelTextSize(8);
        //    data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        heartData.setFillRatio(0.15f);    //设置柱子的宽度

        //给表填充数据，显示出来
        newH9DataHeartChartView.setColumnChartData(heartData);
        newH9DataHeartChartView.startDataAnimation(2000);
        newH9DataHeartChartView.setZoomEnabled(false);  //支持缩放
        newH9DataHeartChartView.setInteractive(true);  //支持与用户交互

        //item的点击事件
        newH9DataHeartChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                newH9DataHeartShowTv.setText("" + subcolumnValue.getValue() + "");
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    //获取睡眠
    private void getSleepH9Data(final int week) {
        sleepVlaues = new ArrayList<>();
        newsSleepBeanList = new ArrayList<>();
        sleepXList = new ArrayList<>();

        String sleepUrl = URLs.HTTPs + "/sleep/getSleepByTime";
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            sleepJson.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
            sleepJson.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), week)));
            sleepJson.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e(TAG, "----睡眠返回----" + result);
                if (result != null) {
                    try {
                        JSONObject sleepJson = new JSONObject(result);
                        if (sleepJson.getString("resultCode").equals("001")) {
                            String sleepData = sleepJson.getString("sleepData");
                            if (!sleepData.equals("[]")) {
                                newsSleepBeanList = new Gson().fromJson(sleepData, new TypeToken<List<NewsSleepBean>>() {
                                }.getType());

                                if (week == 365) {    //年
                                    sumSleepMap = new HashMap<>();
                                    int sleepSum = 0;
                                    for (int i = 0; i < newsSleepBeanList.size(); i++) {
                                        String strDate = newsSleepBeanList.get(i).getRtc().substring(2, 7);
                                        if (sumSleepMap.get(strDate) != null) {
                                            sleepSum += newsSleepBeanList.get(i).getSleepLen();
                                        }else{
                                            sleepSum =  newsSleepBeanList.get(i).getSleepLen();
                                        }
                                        sumSleepMap.put(strDate, sleepSum);
                                    }
                                    tempSleepList = new ArrayList<>();
                                    tempSleepList.clear();
                                    //遍历map
                                    for (Map.Entry<String, Integer> maps : sumSleepMap.entrySet()) {
                                        tempSleepList.add(maps.getKey().trim());
                                    }
                                    //升序排列
                                    Collections.sort(tempSleepList, new Comparator<String>() {
                                        @Override
                                        public int compare(String s, String t1) {
                                            return s.compareTo(t1);
                                        }
                                    });
                                    sleepXList.clear();
                                    sleepVlaues.clear();
                                    for (int k = 0; k < tempSleepList.size(); k++) {
                                        sleepVlaues.add(sumSleepMap.get(tempSleepList.get(k)));
                                        sleepXList.add(tempSleepList.get(k));
                                    }
                                    showSleepChat(13);

                                } else {
                                    sleepXList.clear();
                                    sleepVlaues.clear();
                                    for (NewsSleepBean sleepBean : newsSleepBeanList) {
                                        sleepVlaues.add(sleepBean.getSleepLen()); //2017-11-11
                                        sleepXList.add(sleepBean.getRtc().substring(8, sleepBean.getRtc().length()));
                                    }
                                    showSleepChat(newsSleepBeanList.size());
                                }

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, sleepUrl, sleepJson.toString());


    }

    //步数返回
    private void getStepsData(final int weekTag) {
        //mValues = new ArrayList<>();    //实例化步数的数值
        stepXList = new ArrayList<>();
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
            jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), weekTag)));
            jsonObect.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stepList = new ArrayList<>();
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("工具类", "----步数----" + result);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            String daydata = jsonObject.getString("day");
                            if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
                                stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                                }.getType());
                                Log.e(TAG, "------lt----" + stepList.size());
                                if (weekTag == 365) { //年
                                    mValues = new ArrayList<>();
                                    stepSumMap = new HashMap<>();
                                    int sum = 0;
                                    for (int i = 0; i < stepList.size(); i++) {
                                        String strDate = stepList.get(i).getRtc().substring(2, 7);
                                        if (stepSumMap.get(strDate) != null) {
                                            sum += stepList.get(i).getStepNumber();
                                        }else{
                                            sum = stepList.get(i).getStepNumber();
                                        }
                                        stepSumMap.put(strDate, sum);
                                    }
                                    tempList = new ArrayList<>();
                                    tempList.clear();
                                    //遍历map
                                    for (Map.Entry<String, Integer> entry : stepSumMap.entrySet()) {
                                        tempList.add(entry.getKey().trim());
                                    }
                                    //升序排列
                                    Collections.sort(tempList, new Comparator<String>() {
                                        @Override
                                        public int compare(String s, String t1) {
                                            return s.compareTo(t1);
                                        }
                                    });
                                    for (int k = 0; k < tempList.size(); k++) {
                                        mValues.add(stepSumMap.get(tempList.get(k)));
                                        stepXList.add(tempList.get(k));
                                    }
                                    //newH9DataStepShowTv.setText(stepSumMap.get(tempList.get(tempList.size()-1)));
                                    showStepsChat(13);

                                } else {
                                    mValues = new ArrayList<>();
                                    //获取值
                                    for (WatchDataDatyBean stepNumber : stepList) {
                                        mValues.add(stepNumber.getStepNumber());    //步数的数值显示
                                        String rct = stepNumber.getRtc().substring(8, stepNumber.getRtc().length());
                                        stepXList.add(rct);
                                    }
                                    Log.e(TAG, "----listsize--" + stepList.size());
//                                    newH9DataStepShowTv.setText(stepList.get(stepList.size()-1).getStepNumber());
                                    showStepsChat(stepList.size());
                                }


                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObect.toString());

    }

    private void initViews() {
        h8DataTitleTv.setText(getResources().getString(R.string.data));
        h8DataTitleLinImg.setVisibility(View.INVISIBLE);
        h8DataLinChartImg.setVisibility(View.INVISIBLE);
        newH9DataSwipe.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //步数图表显示
    private void showStepsChat(final int count) {
        Log.e(TAG, "----count--" + count);
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = count;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; i++) {

            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; j++) {
                //为每一柱图添加颜色和数值
                float f = mValues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);

            //是否有数据标注
            column.setHasLabels(false);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(stepXList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        data = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        axisX.setTextSize(12);
        axisX.setMaxLabelChars(6);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        data.setValueLabelBackgroundColor(R.color.mpc_end_color);
        data.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        data.setValueLabelTextSize(8);
        data.setFillRatio(0.15f);    //设置柱子的宽度

        //给表填充数据，显示出来
        newH9DataStepChartView.setColumnChartData(data);
        newH9DataStepChartView.startDataAnimation(2000);
        newH9DataStepChartView.setZoomEnabled(false);  //支持缩放
        newH9DataStepChartView.setInteractive(true);  //支持与用户交互


        //item的点击事件
        newH9DataStepChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                Log.e(TAG, "----i--" + i + "--i1--" + i1);
                if (count == 13) {    //年
                    newH9DataStepShowTv.setText("" + stepSumMap.get(tempList.get(i)) + "");
                } else {
                    newH9DataStepShowTv.setText("" + stepList.get(i).getStepNumber() + "");
                }

            }

            @Override
            public void onValueDeselected() {

            }
        });

    }

    //睡眠图标显示
    private void showSleepChat(final int count) {
        Log.e(TAG, "----count--" + count);
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = count;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; i++) {

            values = new ArrayList<>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; j++) {
                //为每一柱图添加颜色和数值
                float f = sleepVlaues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);

            //是否有数据标注
            column.setHasLabels(false);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(stepXList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        sleepColumnChartData = new ColumnChartData(columns);
//        //定义x轴y轴相应参数
//        Axis axisY = new Axis().setHasLines(true);
//        //axisY.setName("出场率(%)");//轴名称
//        axisY.hasLines();//是否显示网格线
//        //Y轴颜色
//        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
//
//        data.setAxisYLeft(axisY);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        axisX.setTextSize(12);
        axisX.setMaxLabelChars(6);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        sleepColumnChartData.setAxisXBottom(axisX);
        sleepColumnChartData.setValueLabelBackgroundColor(R.color.mpc_end_color);
        sleepColumnChartData.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        sleepColumnChartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        sleepColumnChartData.setValueLabelTextSize(8);
        sleepColumnChartData.setFillRatio(0.15f);    //设置柱子的宽度

        //给表填充数据，显示出来
        newH9DataSleepChartView.setColumnChartData(sleepColumnChartData);
        newH9DataSleepChartView.startDataAnimation(2000);
        newH9DataSleepChartView.setZoomEnabled(false);  //支持缩放
        newH9DataSleepChartView.setInteractive(true);  //支持与用户交互


        //item的点击事件
        newH9DataSleepChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                Log.e(TAG, "----i--" + i + "--i1--" + i1);


            }

            @Override
            public void onValueDeselected() {

            }
        });

    }


    @OnClick({R.id.newH9DataWeekTv, R.id.newH9DataMonthTv, R.id.newH9DataYearTv,
            R.id.h8_dataShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newH9DataWeekTv:  //周的点击
                heartList.clear();
                stepList.clear();
                clearClickTvStyle();
                newH9DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                newH9DataWeekTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                getAllChartData(7);
                break;
            case R.id.newH9DataMonthTv: //月的点击
                heartList.clear();
                stepList.clear();
                clearClickTvStyle();
                newH9DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                newH9DataMonthTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                getAllChartData(30);
                break;
            case R.id.newH9DataYearTv:  //年的点击
                heartList.clear();
                stepList.clear();
                clearClickTvStyle();
                newH9DataYearTv.setTextColor(getResources().getColor(R.color.white));
                newH9DataYearTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                getAllChartData(365);
                break;
            case R.id.h8_dataShareImg:  //分享
                startActivity(new Intent(getActivity(), H8ShareActivity.class));
                break;

        }
    }

    private void clearClickTvStyle() {//.new_colorAccent
        newH9DataWeekTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        newH9DataWeekTv.setTextColor(Color.parseColor("#333333"));
        newH9DataMonthTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        newH9DataMonthTv.setTextColor(Color.parseColor("#333333"));
        newH9DataYearTv.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        newH9DataYearTv.setTextColor(Color.parseColor("#333333"));

    }


    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1001;
                handler.sendMessage(message);
            }
        }, 3 * 1000);

    }
}
