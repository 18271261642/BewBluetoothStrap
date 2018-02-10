package com.example.bozhilun.android.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.Sport;
import com.example.bozhilun.android.bean.SportWeekMonth;
import com.example.bozhilun.android.bean.Sporthours;
import com.example.bozhilun.android.bean.StepWeekMonth;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

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

import static com.example.bozhilun.android.util.Common.formatDouble;

/**
 * Created by thinkpad on 2017/3/24.
 * 步数周
 */

public class DataReportStepWeekFragment extends BaseFragment {
    @BindView(R.id.chart_step_week) ColumnChartView stepChart;

    @BindView(R.id.stepval_tv_week) TextView Mysteps;
    @BindView(R.id.activityval_tv_week) TextView activityva;
    @BindView(R.id.lichengval_tv_week) TextView licheng;
    @BindView(R.id.calval_tv_week) TextView calval;

    @BindView(R.id.yungdongstep_benzhou) TextView BENzhoou;
    @BindView(R.id.yungdongstep_shangzhou) TextView Shangzhou;

    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private SportWeekMonth dataActivityReport;
    private ColumnChartData stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    String EndTiem;//结束时间
    private String[] weekDay = new String[] {  "MON", "TUE", "WED", "THR", "FRI", "SAT","SUN" };
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    int activityTime;
    int Steps;
    //年月日
    String yue,ri;
    @Override
    protected void initViews() {
        /**
         * X 轴的显示
         */
        for (int i = 0; i < weekDay.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(weekDay[i]));

        }
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");

                    Gson gson = new Gson();
                    if ("001".equals(loginResult)) {
                         activityTime = jsonObject.getInt("avgActivity");
                       Steps=jsonObject.getInt("avgSport");
                        dataActivityReport = gson.fromJson(result, SportWeekMonth.class);
                        try{
                            if(null!=dataActivityReport.getDay()){
                                stepChart.setVisibility(View.VISIBLE);
                                ArrayList<StepWeekMonth> sportHours = dataActivityReport.getDay();
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values;
                                String dateNum;
                                int stepNum;
                                String weekCount;
                                for (int i = 0; i < 7; ++i) {
                                    values = new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {
                                        dateNum = sportHours.get(j).getDate();
                                        stepNum = sportHours.get(j).getStepNumber();
                                        weekCount=sportHours.get(j).getWeekCount();
                                        if (i == Integer.valueOf(weekCount)-1) {
                                            values.add(new SubcolumnValue((float) stepNum, getResources().getColor(R.color.chang_white)));
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

                                }
                                stepColumdata = new ColumnChartData(columns);
                                if (hasAxes) {
                                    Axis axisX = new Axis();
                                    axisX.setTextSize(14);
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
                                stepColumdata.setFillRatio(0.4F);//设置柱形图的宽度
                                stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
                                stepChart.setColumnChartData(stepColumdata);
                                prepareDataAnimation();
                                stepChart.startDataAnimation();

                                Mysteps.setText(Steps+"");
                                licheng.setText(formatDouble((0.766*Steps)/1000)+"");
                                activityva.setText(activityTime+"");
                                calval.setText(formatDouble(((0.766*Steps)/1000)*65.4)+"");


                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {
                        stepChart.setColumnChartData(null);
                        Mysteps.setText("0");
                        licheng.setText("0");
                        activityva.setText("0");
                        calval.setText("0");
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




    private void prepareDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setValue(Float.valueOf(value.getValue()) );
            }
        }
    }


    @OnClick({R.id.yungdongstep_benzhou, R.id.yungdongstep_shangzhou})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yungdongstep_benzhou:
                Shangzhou.setTextColor(Color.parseColor("#575757"));
                BENzhoou.setTextColor(Color.parseColor("#000000"));
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                getData(EndTiem,sdf.format(new Date()).toString());
                break;
            case R.id.yungdongstep_shangzhou:
                BENzhoou .setTextColor(Color.parseColor("#575757"));
                Shangzhou.setTextColor(Color.parseColor("#000000"));
                try {
                    getData(String.valueOf(Common.getStatetime(12)) ,String.valueOf(Common.getStatetime(6)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_step_week_item;
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
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getSportWeek, mapjson);
    }
}
