package com.example.bozhilun.android.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.B15PSleepBean;
import com.example.bozhilun.android.bean.BloodPressureList;
import com.example.bozhilun.android.bean.Sleep;
import com.example.bozhilun.android.bean.SleepData;
import com.example.bozhilun.android.bean.SleepList;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SumBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by thinkpad on 2017/3/24.
 * 睡眠周
 */

public class DataReportStepItemSleeptwoFragment extends BaseFragment {
    @BindView(R.id.yungdongstep_shangzhou_shuimiantwo) TextView shangzhou;
    @BindView(R.id.yungdongstep_benzhou_shuimiantwo) TextView benzhou;

    @BindView(R.id.stepval_tv_shuimiantwo) TextView quantian;
    @BindView(R.id.activityval_tv_shuimiantwo) TextView Shengshuimian;
    @BindView(R.id.lichengval_tv_shuimiantwo) TextView Qianshuimian;

    @BindView(R.id.chart_shumian_two) ColumnChartView stepChart;



    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private SleepData dataActivityReport;
    ColumnChartData     stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();

    String EndTiem;//结束时间
    private String[] weekDay = new String[] {"MON","","TUE","","WED","","THR","","FRI","","SAT","","SUN",""};
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    //年月日
    String yue,ri;
    private List  mysleep;
    private String shengshui,QIANSHUI,zongshichang;

    @OnClick({R.id.yungdongstep_benzhou_shuimiantwo, R.id.yungdongstep_shangzhou_shuimiantwo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yungdongstep_benzhou_shuimiantwo:
                shangzhou.setTextColor(Color.parseColor("#575757"));
                benzhou.setTextColor(Color.parseColor("#000000"));
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                getData(EndTiem,sdf.format(new Date()).toString());
                break;
            case R.id.yungdongstep_shangzhou_shuimiantwo:
                benzhou .setTextColor(Color.parseColor("#575757"));
                shangzhou.setTextColor(Color.parseColor("#000000"));
                try {
                   getData(String.valueOf(Common.getStatetime(12)) ,String.valueOf(Common.getStatetime(6)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }




    @Override
    protected void initViews() {

        /**
         * X 轴的显示
         */
        for (int i = 0; i < weekDay.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(weekDay[i]));

        }

        mysleep=new ArrayList();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    Gson gson = new Gson();
                    if ("001".equals(loginResult)) {

                        dataActivityReport = gson.fromJson(result, SleepData.class);
                        try{
                           ArrayList<SleepList> sportHours = dataActivityReport.getSleepData();
                            if(null!=dataActivityReport.getSleepData()){
                                stepChart.setVisibility(View.VISIBLE);
                                MyLogUtil.i("-sportHours-sizeyuyy->" + sportHours.toString());
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values,values2;
                                ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
                                ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
                                String dateNum;
                                int Systolic;
                                int  Diastolic;
                                int step=0;
                                Integer timeNum;
                                for (int i = 0; i < 7; ++i) {
                                    values = new ArrayList<>();
                                    values2= new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {
                                        dateNum = sportHours.get(j).getRtc();
                                        Systolic = sportHours.get(j).getDeepSleep();
                                        Diastolic = sportHours.get(j).getShallowSleep();
                                        timeNum = sportHours.get(j).getWeekCount();
                                        axisValuesY.add(new AxisValue(Systolic).setValue(Systolic));// 添加Y轴显示的刻度值
                                        axisValuesY.add(new AxisValue(Diastolic).setValue(Diastolic));// 添加Y轴显示的刻度值
                                        axisValuesX.add(new AxisValue(Systolic).setValue(Systolic));// 添加Y轴显示的刻度值
                                        axisValuesX.add(new AxisValue(Diastolic).setValue(Diastolic));// 添加Y轴显示的刻度值
                                        if (i == timeNum-1) {
                                            values.add(new SubcolumnValue((float) Systolic, getResources().getColor(R.color.SECHAb)));
                                            values.add(new SubcolumnValue((float) Diastolic, getResources().getColor(R.color.SECHAa)));
                                        }
                                    }



                                    //将每个属性的拥有的柱子，添加到Column中
                                    Column column = new Column(values);
                                    //是否显示每个柱子的Lable
                                    column.setHasLabels(true);
                                    //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
                                    column.setHasLabelsOnlyForSelected(false);
                                    //将每个属性得列全部添加到List中
                                    columns.add(column);
                                    //将每个属性的拥有的柱子，添加到Column中
                                    Column column2 = new Column(values2);
                                    //是否显示每个柱子的Lable
                                    column2.setHasLabels(true);
                                    //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
                                    column2.setHasLabelsOnlyForSelected(false);
                                    //将每个属性得列全部添加到List中
                                    columns.add(column2);

                                }
                                   stepColumdata = new ColumnChartData(columns);
                                if (hasAxes) {
                                    Axis axisX = new Axis();
                                    axisX.setTextSize(10);
                                    axisX.setValues(mAxisXValues);
                                    Axis axisY = new Axis().setHasLines(true);
                                    stepColumdata.setAxisXBottom(axisX);
                                } else {
                                    stepColumdata.setAxisXBottom(null);
                                    stepColumdata.setAxisYLeft(null);
                                }
                                stepChart.setZoomEnabled(false);//设置是否支持缩放
                                stepColumdata .setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
                                stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
                                stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
                                stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                                stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
                                stepColumdata.setFillRatio(0.4F);//设置柱形图的宽度
                                stepChart .setZoomEnabled(false);
                                stepChart.setColumnChartData(stepColumdata);
                                xieyaDataAnimation();
                                stepChart.startDataAnimation();

                                String avgSleep = jsonObject.getString("avgSleep");
                                JSONObject avgSleepjsonObject = new JSONObject(avgSleep);
                                QIANSHUI=  avgSleepjsonObject.getString("avgSleepLen");
                                 shengshui=   avgSleepjsonObject.getString("avgDeepSleep");
                                zongshichang=avgSleepjsonObject.getString("avgShallowSleep");
                                if(!"null".equals(String.valueOf(QIANSHUI))){
                                    quantian.setText(String.valueOf(Integer.valueOf(QIANSHUI)/60)+getResources().getString(R.string.hour)+String.valueOf(Integer.valueOf(QIANSHUI)%60)+getResources().getString(R.string.minute));
                                    Shengshuimian.setText(String.valueOf(Integer.valueOf(shengshui)/60)+getResources().getString(R.string.hour)+String.valueOf(Integer.valueOf(shengshui)%60)+getResources().getString(R.string.minute));
                                    Qianshuimian.setText(String.valueOf(Integer.valueOf(zongshichang)/60)+getResources().getString(R.string.hour)+String.valueOf(Integer.valueOf(zongshichang)%60)+getResources().getString(R.string.minute));
                                }else{
                                    quantian.setText("- -");
                                    Shengshuimian.setText("- -");
                                    Qianshuimian.setText("- -");
                                }


                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {
                        stepChart.setColumnChartData(null);
                         quantian.setText("- -");
                        Shengshuimian.setText("- -");
                        Qianshuimian.setText("- -");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if(String.valueOf(calendar.get(Calendar.MONTH)+1).length()<=1){
            yue=  "0"+String.valueOf(calendar.get(Calendar.MONTH)+1);
        }else{
            yue=String.valueOf(calendar.get(Calendar.MONTH)+1);
        }
        if(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)).length()<=1){
            ri= "0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }else{
            ri=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }
        EndTiem=String.valueOf(calendar.get(Calendar.YEAR))+"-"+yue+"-"+String.valueOf(Integer.parseInt(ri)-6);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        getData(EndTiem,sdf.format(new Date()).toString());


    }



    private void xieyaDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()) );
            }
        }
    }





    @Override
    protected int getContentViewId() {
        return R.layout.fragment_stepsleeeptwo_item;
    }

    private void getData(String starttime,String endtime) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        map.put("startDate",starttime);
        map.put("endDate",endtime);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getSleepW, mapjson);
    }



}
