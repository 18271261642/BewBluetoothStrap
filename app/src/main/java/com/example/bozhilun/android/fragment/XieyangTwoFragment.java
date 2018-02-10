package com.example.bozhilun.android.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.BloodOxygen;
import com.example.bozhilun.android.bean.BloodOxygenList;
import com.example.bozhilun.android.bean.BloodPressure;
import com.example.bozhilun.android.bean.BloodPressureList;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
 * 血氧周
 */

public class XieyangTwoFragment extends BaseFragment {
    @BindView(R.id.chart_step_shuimia_xieyangtwo) ColumnChartView shuimianjilu;
    @BindView(R.id.yungdongstep_shangzhou_xieyangtwo) TextView shangzhou;
    @BindView(R.id.yungdongstep_benzhou_xieyangtwo) TextView benzhou;

    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private BloodOxygen dataActivityReport;
    ColumnChartData  stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    private String[] weekDay = new String[] { "MON","" ,"TUE","" , "WED","" , "THR","" , "FRI", "" ,"SAT","" ,"SUN" ,"" };
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    //年月日
    String yue,ri;
    String EndTiem;//结束时间
    @Override
    protected void initViews() {

        /**
         * X 轴的显示
         */
        for (int i = 0; i < weekDay.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(weekDay[i]));
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    Gson gson = new Gson();
                    if ("001".equals(loginResult)) {
                        dataActivityReport = gson.fromJson(result, BloodOxygen.class);
                        try{
                            ArrayList<BloodOxygenList> sportHours = dataActivityReport.getBloodOxygen();
                            if(null!=dataActivityReport.getBloodOxygen()){
                                shuimianjilu.setVisibility(View.VISIBLE);
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values;
                                List<SubcolumnValue> values2;
                                ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
                                ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
                                int MaxHeartRate;
                                int minHeart;
                                int weekCount;

                           for (int i = 0; i < 7; ++i) {
                                    values = new ArrayList<>();
                                    values2= new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {

                                        MaxHeartRate = sportHours.get(j).getMaxBloodOxygen();
                                        minHeart= sportHours.get(j).getMinBloodOxygen();
                                        weekCount = sportHours.get(j).getWeekCount();
                                        axisValuesY.add(new AxisValue(MaxHeartRate).setValue(j));// 添加Y轴显示的刻度值
                                        axisValuesY.add(new AxisValue(minHeart).setValue(j));// 添加Y轴显示的刻度值
                                        axisValuesX.add(new AxisValue(MaxHeartRate).setValue(MaxHeartRate));// 添加Y轴显示的刻度值
                                        axisValuesX.add(new AxisValue(minHeart).setValue(minHeart));// 添加Y轴显示的刻度值

                                        if (i == weekCount-1) {
                                            values.add(new SubcolumnValue((float) MaxHeartRate, getResources().getColor(R.color.chang_white)));
                                            values2.add(new SubcolumnValue((float) minHeart, getResources().getColor(R.color.chang_white)));
                                    //设置X轴的柱子所对应的属性名称

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
                                    axisX.setTextSize(12);
                                    axisX.setValues(mAxisXValues);
                                    Axis axisY = new Axis().setHasLines(true);
                                    stepColumdata.setAxisXBottom(axisX);
                                } else {
                                    stepColumdata.setAxisXBottom(null);
                                    stepColumdata.setAxisYLeft(null);
                                }
                                shuimianjilu.setZoomEnabled(false);//设置是否支持缩放
                                stepColumdata .setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
                                stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
                                stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
                                stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                                stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(10);// 设置数据文字大小
                                stepColumdata.setFillRatio(0.7F);//设置柱形图的宽度
                                shuimianjilu.setColumnChartData(stepColumdata);
                                prepareDataAnimation();
                                shuimianjilu.startDataAnimation();

                                shuimianjilu.setVisibility(View.VISIBLE);


                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {
                        shuimianjilu.setVisibility(View.INVISIBLE);

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


    @OnClick({R.id.yungdongstep_benzhou_xieyangtwo, R.id.yungdongstep_shangzhou_xieyangtwo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yungdongstep_benzhou_xieyangtwo:
                shangzhou.setTextColor(Color.parseColor("#575757"));
                benzhou.setTextColor(Color.parseColor("#000000"));
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                getData(EndTiem,sdf.format(new Date()).toString());
                break;
            case R.id.yungdongstep_shangzhou_xieyangtwo:
                benzhou .setTextColor(Color.parseColor("#575757"));
                shangzhou.setTextColor(Color.parseColor("#000000"));
                try {
                    getData(String.valueOf(Common.getStatetime(12)) ,String.valueOf(Common.getStatetime(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    private void prepareDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()) );
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_xieyangtwo;
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
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getBloodOxygenW, mapjson);
    }



}
