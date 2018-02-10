package com.example.bozhilun.android.h9.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.bean.HeartDataBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/27 16:34
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9DataFragment extends Fragment {
    private final String TAG = "----->>>" + this.getClass();
    @BindView(R.id.leaf_square_chart)
    ColumnChartView columnChartView;//直方图
    View b18iDataView;
    Unbinder unbinder;

    @BindView(R.id.b18i_table)
    TabLayout tableLayout;
    @BindView(R.id.b18i_viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //距离
    @BindView(R.id.distanceData)
    TextView distanceData;
    //心率
    @BindView(R.id.heartData)
    TextView heartData;
    //卡路里
    @BindView(R.id.kcalData)
    TextView kcalData;
    @BindView(R.id.h9DataSwipe)
    SwipeRefreshLayout h9DataSwipe;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    List<WatchDataDatyBean> watchDataList;
    ColumnChartData data;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    h9DataSwipe.setRefreshing(false);
                    getSportDataForServer();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iDataView = inflater.inflate(R.layout.fragment_b18i_data, container, false);
        unbinder = ButterKnife.bind(this, b18iDataView);

        toolbar.setBackgroundColor(Color.parseColor("#32c0ff"));
        //刷新
        h9DataSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                h9DataSwipe.setRefreshing(true);
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

        getHeartData(B18iUtils.getSystemDatasss());
        getSleepData(B18iUtils.getSystemDatasss());
//        setContent();//给直方图设置值
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {

            @Override
            public void onNext(String result) {
                Log.e("H9", "----获取data数据-----" + result);
                if (result != null) {
                    try {
                        JSONObject jso = new JSONObject(result);
                        if (jso.getInt("resultCode") == 001) {
                            if (jso.has("day")) {
                                Log.d(TAG, "----------------001day");
                                String daydata = jso.getString("day");
                                getWatchDataList(daydata);
                                for (WatchDataDatyBean stepNumber : watchDataList) {
                                    mValues.add(stepNumber.getStepNumber());    //步数值显示
                                    //x轴的时间
                                    String rct = stepNumber.getRtc().substring(5, stepNumber.getRtc().length());
                                    xStringList.add(rct);
                                }
                                distanceData.setText(watchDataList.get(0).getDistance());
                                kcalData.setText(watchDataList.get(0).getCalories());
                                setContent();//给直方图设置值
                            } else if (jso.has("heartRate")) {
                                Log.d(TAG, "----------------002heartRate");
                                String heartRates = jso.getString("heartRate");
                                getHeartDataList(result);
                                int heartRate = 0;
                                for (HeartDataBean.HeartRateBean hearte : heartRateList) {
                                    heartRate = hearte.getHeartRate();
                                }
                                heartData.setText(String.valueOf(heartRate));
                            } else if (jso.has("avgSleep")) {
                                Log.d(TAG, "----------------003avgSleep");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        getDatas();//获取数据
        return b18iDataView;
    }

    /**
     * 解析心率数据
     *
     * @param heartRates
     */
    List<HeartDataBean.HeartRateBean> heartRateList;

    private List<HeartDataBean.HeartRateBean> getHeartDataList(String heartRates) {
        if (heartRates == null) {
            return null;
        }
        HeartDataBean heartDataBean = new Gson().fromJson(heartRates, HeartDataBean.class);
        heartRateList = heartDataBean.getHeartRate();
        Collections.sort(heartRateList, new Comparator<HeartDataBean.HeartRateBean>() {
            @Override
            public int compare(HeartDataBean.HeartRateBean watchDataDatyBean, HeartDataBean.HeartRateBean t1) {
                return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
            }
        });
        return heartRateList;
    }

    //Y轴值
    //图表值
    private List<Integer> mValues;// = new ArrayList<>();
    //X
    List<String> xStringList = new ArrayList<>();
    List<View> views = new ArrayList<>();
    String datas[];
    int[] score;
    int position;

    /**
     * 获取数据
     */
    private void getDatas() {
        //获取运动数据
        //获取睡眠数据
        //获取心率数据
        tvTitle.setText(WatchUtils.getCurrentDate());
        //获取步数和睡眠数据接口
        getSportDataForServer();

    }

    /**
     * 返回运动数据list
     *
     * @param daydata
     */
    private List<WatchDataDatyBean> getWatchDataList(String daydata) {
        if (daydata != null) {
            watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
            }.getType());
            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                @Override
                public int compare(WatchDataDatyBean watchDataDatyBean, WatchDataDatyBean t1) {
                    return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
                }
            });
        } else {
            return null;
        }
        return watchDataList;
    }


    /**
     * 获取运动数据
     */
    private void getSportDataForServer() {
        mValues = new ArrayList<>();
        String dataStepUrl = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
            jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 6)));
            jsonObect.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("H9", "----参数---" + jsonObect.toString());
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, MyApp.getContext());
        OkHttpObservable.getInstance().getData(commonSubscriber, dataStepUrl, jsonObect.toString());

    }


    /**
     * 获取心率数据
     */
    private void getHeartData(String time) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", (String) SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
        map.put("userId", (String) SharedPreferencesUtils.readObject(getActivity(), "userId"));
        map.put("date", time);//周起始日期
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getHeartD, mapjson);
    }


    /**
     * 获取睡眠数据
     */
    private void getSleepData(String time) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", (String) SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
        map.put("userId", (String) SharedPreferencesUtils.readObject(getActivity(), "userId"));
        map.put("date", time);//周起始日期
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getSleepD, mapjson);
    }

    private void setContent() {
        //addColimnData();//直方图添加数据（死数据）

        // 使用的 score.length 列，每列1个subcolumn。
        int numSubcolumns = 1;
        final int numColumns = 7;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<>();
        //子列数据集合
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                float f = mValues.get(i);
//                if (i == position) {
//                    values.add(new SubcolumnValue(f, Color.parseColor("#FFFFFFFF")));
//                } else {
//                    values.add(new SubcolumnValue(f, Color.parseColor("#70FFFFFF")));
//                }
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
            column.setHasLabels(true);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(xStringList.get(i)));
        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        data = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis().setHasLines(false);
        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        axisY.setLineColor(Color.parseColor("#00BBBBBB"));
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisX.setTextSize(12);

        axisX.setHasTiltedLabels(false);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        //data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        data.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        data.setValueLabelTextSize(8);
//        data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        data.setFillRatio(0.3f);    //设置柱子的宽度
        //给表填充数据，显示出来
        columnChartView.setColumnChartData(data);
        columnChartView.startDataAnimation(2000);
        columnChartView.setZoomEnabled(true);  //支持缩放
        columnChartView.setInteractive(true);  //支持与用户交互

        //item的点击事件
        columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                getHeartData(watchDataList.get(i).getRtc());
                getSleepData(watchDataList.get(i).getRtc());
                if (watchDataList != null) {
                    distanceData.setText(watchDataList.get(i).getDistance());
                    kcalData.setText(watchDataList.get(i).getCalories());
                }
            }

            @Override
            public void onValueDeselected() {

            }
        });

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(views);
        viewPager.setAdapter(myViewPagerAdapter);
        myViewPagerAdapter.notifyDataSetChanged();
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabGravity(TabLayout.MODE_FIXED);
        tableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout.postInvalidate();

        /******************************************/
        for (int i = 0; i < datas.length; i++) {
            tableLayout.getTabAt(i).setText(datas[i]);
        }
        tableLayout.setSelectedTabIndicatorHeight(0);
        tableLayout.setTabTextColors(Color.GRAY, Color.parseColor("#1bb3fd"));
        tableLayout.getTabAt(position).select();
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //获取运动数据
//                BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
                //获取睡眠数据
//                BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());
                //获取心率数据
//                BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
                for (int i = 0; i < numColumns; i++) {
                    if (tab.getPosition() == i) {
                        List<SubcolumnValue> values1 = columns.get(i).getValues();
                        values1.get(0).setColor(Color.parseColor("#FFFFFFFF"));
                    } else {
                        List<SubcolumnValue> values1 = columns.get(i).getValues();
                        values1.get(0).setColor(Color.parseColor("#70FFFFFF"));
                    }
                }
                columnChartView.postInvalidate();
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 直方图添加数据（死数据）
     */
    private void addColimnData() {


        List<String> times2 = B18iUtils.getTimes2();
//        int integer1 = Integer.valueOf(times2.get(1));
        int integer = Integer.valueOf(times2.get(2));
        Log.d(TAG, "TableLayout 时间为：" + Integer.valueOf(times2.get(1)) + "====" + integer);
        datas = new String[integer];
        score = new int[integer];
        for (int i = 0; i < integer; i++) {
            if (integer >= 10) {
                datas[i] = integer + "/" + (i + 1);
            } else {
                datas[i] = "0" + integer + "/" + (i + 1);
            }
            score[i] = 50;
        }
        position = datas.length - 1;

        /**************************/

        for (int i = 0; i < datas.length; i++) {
            xStringList.add(datas[i]);
            mValues.add(score[i]);
            views.add(new View(getContext()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private List<View> list;

        public MyViewPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }
}
