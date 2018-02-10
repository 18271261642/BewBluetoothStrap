package com.example.bozhilun.android.siswatch.data;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
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
 * Created by sunjianhua on 2017/11/8.
 */

public class H8DataLinchartActivity extends WatchBaseActivity {

    private static final String TAG = "H8DataLinchartActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.newH8DataWeekTv)
    TextView newH8DataWeekTv;
    @BindView(R.id.newH8DataMonthTv)
    TextView newH8DataMonthTv;
    @BindView(R.id.newH8DataYearTv)
    TextView newH8DataYearTv;
    @BindView(R.id.newH8DataStepShowTv)
    TextView newH8DataStepShowTv;
    @BindView(R.id.newH8DataStepChartView)
    ColumnChartView newH8DataStepChartView;
    @BindView(R.id.xTv11)
    TextView xTv11;
    @BindView(R.id.xTv22)
    TextView xTv22;
    @BindView(R.id.xTv33)
    TextView xTv33;
    @BindView(R.id.xTv44)
    TextView xTv44;
    @BindView(R.id.xTv55)
    TextView xTv55;
    @BindView(R.id.xTv66)
    TextView xTv66;
    @BindView(R.id.xTv77)
    TextView xTv77;


    private ColumnChartData data;   //步数的图表数据源
    //步数数值
    private List<Integer> mValues;
    private List<String> stepXList; //x轴数据
    private Map<String, Integer> stepSumMap; //用于计算年的步数
    private List<String> tempList;  //用于计算年的步数的list
    private List<WatchDataDatyBean> watchDataList;


    CommonSubscriber commonSubscriber;
    SubscriberOnNextListener subscriberOnNextListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h8_datalinchart);
        ButterKnife.bind(this);

        initViews();

        initData();


    }

    private void initData() {
        clearClickTvStyle();
        stepXList.clear();
        watchDataList.clear();
        newH8DataWeekTv.setTextColor(getResources().getColor(R.color.white));
        newH8DataWeekTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.new_colorAccent));
        getWeekData("week");
    }

    private void getWeekData(final String datatag) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        final JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(H8DataLinchartActivity.this, "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(H8DataLinchartActivity.this, "mylanmac"));

            if (datatag.equals("week")) { //周
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 7)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("month")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 30)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("year")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 364)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (result != null) {
                    JSONObject jso = null;
                    try {
                        jso = new JSONObject(result);
                        String daydata = jso.getString("day");
                        Log.e(TAG,"----daydata----"+daydata);
                        watchDataList.clear();
                        watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());

                        if (datatag.equals("year")) {
                            mValues = new ArrayList<>();
                            stepSumMap = new HashMap<>();
                            int sum = 0;
                            for (int i = 0; i < watchDataList.size(); i++) {
                                String strDate = watchDataList.get(i).getRtc().substring(0, 7);
                                if (stepSumMap.get(strDate) != null) {
                                    sum += watchDataList.get(i).getStepNumber();
                                }else{
                                    sum = watchDataList.get(i).getStepNumber();
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
                                Log.e(TAG,"----add-values----"+stepSumMap.get(tempList.get(k))+"\n"+
                                        tempList.get(k).substring(2,tempList.get(k).length()));
                                mValues.add(stepSumMap.get(tempList.get(k)));
                                stepXList.add(tempList.get(k).substring(2,tempList.get(k).length()));
                            }

                            showStepsChat(13);


                        } else {
                            mValues = new ArrayList<>();
                            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                                @Override
                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
                                    return o1.getRtc().compareTo(o2.getRtc());
                                }
                            });
                            //获取值
                            for (WatchDataDatyBean stepNumber : watchDataList) {
                                mValues.add(stepNumber.getStepNumber() );    //步数的数值显示
                                String rct = stepNumber.getRtc().substring(8, stepNumber.getRtc().length());
                                stepXList.add(rct);
                            }
                            Log.e(TAG, "----listsize--" + watchDataList.size());
                            showStepsChat(watchDataList.size());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        };

        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, H8DataLinchartActivity.this);
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonParams.toString());


    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.data));
        watchDataList = new ArrayList<>();
        stepXList = new ArrayList<>();

        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

            values = new ArrayList<>();
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
        axisX.setTextSize(10);
        axisX.setMaxLabelChars(6);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);

        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisY.setTextSize(5);
        data.setAxisYLeft(axisY);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        data.setValueLabelBackgroundColor(R.color.mpc_end_color);
        data.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        data.setValueLabelTextSize(8);
        data.setFillRatio(0.2f);    //设置柱子的宽度

        //给表填充数据，显示出来
        newH8DataStepChartView.setColumnChartData(data);
        newH8DataStepChartView.startDataAnimation(2000);
        newH8DataStepChartView.setZoomEnabled(false);  //支持缩放
        newH8DataStepChartView.setInteractive(true);  //支持与用户交互

        //item的点击事件
        newH8DataStepChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
                Log.e(TAG, "----i--" + i + "--i1--" + i1);
                if (count == 13) {    //年
                    //newH9DataStepShowTv.setText("" + stepSumMap.get(tempList.get(i)) + "");
                } else {
                    //newH9DataStepShowTv.setText("" + stepList.get(i).getStepNumber() + "");
                }

            }

            @Override
            public void onValueDeselected() {

            }
        });

    }

    @OnClick({R.id.newH8DataWeekTv, R.id.newH8DataMonthTv, R.id.newH8DataYearTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newH8DataWeekTv:  //周
                clearClickTvStyle();
                stepXList.clear();
                watchDataList.clear();
                newH8DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                newH8DataWeekTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.new_colorAccent));
                getWeekData("week");

                break;
            case R.id.newH8DataMonthTv: //月
                clearClickTvStyle();
                stepXList.clear();
                watchDataList.clear();
                newH8DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                newH8DataMonthTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.new_colorAccent));
                getWeekData("month");
                break;
            case R.id.newH8DataYearTv:  //年
                clearClickTvStyle();
                stepXList.clear();
                watchDataList.clear();
                newH8DataYearTv.setTextColor(getResources().getColor(R.color.white));
                newH8DataYearTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.new_colorAccent));
                getWeekData("year");

                break;
        }
    }

    private void clearClickTvStyle() {//.new_colorAccent
        newH8DataWeekTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.white));
        newH8DataWeekTv.setTextColor(getResources().getColor(R.color.black));
        newH8DataMonthTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.white));
        newH8DataMonthTv.setTextColor(getResources().getColor(R.color.black));
        newH8DataYearTv.setBackgroundColor(ContextCompat.getColor(H8DataLinchartActivity.this, R.color.white));
        newH8DataYearTv.setTextColor(getResources().getColor(R.color.black));

    }
}
