package com.example.bozhilun.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.DataReportItemActivity;
import com.example.bozhilun.android.activity.DataReportItemHateActivity;
import com.example.bozhilun.android.activity.DataReportItemSleepActivity;
import com.example.bozhilun.android.activity.DataReportItemXieYaActivity;
import com.example.bozhilun.android.activity.DataReportItemXieYangActivity;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.B15PSleepBean;
import com.example.bozhilun.android.bean.BloodOxygenList;
import com.example.bozhilun.android.bean.BloodPressureList;
import com.example.bozhilun.android.bean.DataActivityReport;
import com.example.bozhilun.android.bean.HeartRatePointTime;
import com.example.bozhilun.android.bean.Sporthours;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SumBean;
import com.example.bozhilun.android.coverflow.RefreshableView;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by thinkpad on 2017/3/13.
 */

public class DataReportFragment extends BaseFragment {
    @BindView(R.id.ropot_MyListView)
    RefreshableView myListView;

    @BindView(R.id.dareport_step)
    CardView bushu;
    @BindView(R.id.dareport_shuimian)
    CardView shuimian;
    @BindView(R.id.dareport_xinlv)
    CardView xinlv;
    @BindView(R.id.dareport_xieya)
    CardView xieya;
    @BindView(R.id.dareport_xieyang)
    CardView xieyang;

    @BindView(R.id.sleep_huor)
    TextView mysleeptime;
    @BindView(R.id.repot_xinlv)
    TextView reportxinlv;

    @BindView(R.id.Shuimianjilu)
    RelativeLayout shuimianjilu;
    @BindView(R.id.datroport_step)
    TextView Steps;//步数
    @BindView(R.id.step_chart)
    ColumnChartView stepChart;
    @BindView(R.id.xieyang_imgright_linechart)
    ColumnChartView xieyaChart;
    @BindView(R.id.xyangqi_img_imgright_linechart)
    ColumnChartView xieyangChart;

    @BindView(R.id.startime_tv)
    TextView startimeTv;
    @BindView(R.id.endtime_tv_huodong)
    TextView endtimeTv;
    @BindView(R.id.heart_linechart)
    LineChartView heartLinechart;
    /* @BindView(R.id.sleep_chart)
     ColumnChartView sleepChart;*/
    @BindView(R.id.heartrate_startime_tv)
    TextView heartrateStartimeTv;
    @BindView(R.id.heartrate_endtime_tv)
    TextView heartrateEndtimeTv;
    private int randomint = 12;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;

    private ColumnChartData sleepColumdata;
    private LineChartData sleepData;

    private ColumnChartData stepColumdata;
    private LineChartData stepData;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 24;
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;
    private DataActivityReport dataActivityReport;
    private Paint myPaint, myPaint2;
    private List mysleep;
    Handler handler;

    //实现刷新RefreshListener 中方法
    public class onRefresh implements RefreshableView.RefreshListener {
        @Override
        public void onRefresh(RefreshableView view) {
            try {
                getData();
                //伪处理
                if (null != mysleep) {
                    mysleep.clear();
                }
                handler.sendEmptyMessageDelayed(1, 2000);
                myListView.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void initViews() {
        myListView.setRefreshListener(new onRefresh());
        if ("B15P".equals(MyCommandManager.DEVICENAME)) {
            xieyang.setVisibility(View.GONE);
        } else if ("B15S".equals(MyCommandManager.DEVICENAME)) {

        } else if ("B15S-H".equals(MyCommandManager.DEVICENAME)) {
            xieyang.setVisibility(View.GONE);
            xieya.setVisibility(View.GONE);
        }
        //刷新
        handler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                myListView.finishRefresh();
                handler.removeCallbacksAndMessages(null);
                //  Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
            }
        };
        //返回的数据
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("DataFragment","------数据统计四项页面----"+result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");

                    Gson gson = new Gson();
                    if (!TextUtils.isEmpty(result)) {
                        dataActivityReport = gson.fromJson(result, DataActivityReport.class);
                        //更新步数心率
                        //步数
                        try {
                            String sport = jsonObject.getString("sport");
                            Log.e("HH","----步数---"+sport);
                            if (!sport.equals("{}")) {
                                stepChart.setVisibility(View.VISIBLE);
                                getStepData();
                                stepDataAnimation();
                                stepChart.startDataAnimation();
                            } else {
                                stepChart.setVisibility(View.INVISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /**睡眠**/
                        try {
                            String sport = jsonObject.getString("sleep");
                            Log.e("HH","----睡眠---"+sport);
                            if (!"{}".equals(sport)) {
                                JSONObject sleep = new JSONObject(sport);
                                String mysleep = sleep.getString("sleep");
                                if (!mysleep.equals("[]")) {
                                    //睡眠数据
                                    getSleepData();

                                }

                                if (!"B15P".equals(MyCommandManager.DEVICENAME)) {
                                    String sleepTotal = sleep.getString("sleepTotal");
                                    if (!sleepTotal.equals("{}") && !"null".equals(sleepTotal)) {
                                        JSONObject AAA = new JSONObject(sleepTotal);
                                        String sleepLen = AAA.getString("sleepLen");
                                        mysleeptime.setText(Integer.valueOf(sleepLen) / 60 + getResources().getString(R.string.hour) + Integer.valueOf(sleepLen) % 60 + getResources().getString(R.string.minute));
                                    }
                                }

                            }

                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                        //心率
                        try {
                            Log.e("HH","------心率---"+getHeartList(dataActivityReport.getHeartRate().getHeartRate()));
                            getHeartRateData();
                            heartLinechart.startDataAnimation();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //血压
                        try {
                            String bloodPressure = jsonObject.getString("bloodPressure");
                            Log.e("HH","----血压---"+bloodPressure);
                            if (!bloodPressure.equals("{}")) {
                                xieyaChart.setVisibility(View.VISIBLE);
                                getxieyaData();
                                xieyaDataAnimation();
                                xieyaChart.startDataAnimation();
                            } else {
                                xieyaChart.setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /**血氧**/
                        try {
                            String bloodOxygen = jsonObject.getString("bloodOxygen");
                            Log.e("HH","----血氧---"+bloodOxygen);
                            if (!bloodOxygen.equals("{}")) {
                                //血氧
                                xieyangChart.setVisibility(View.VISIBLE);
                                getxieyangData();
                                xieyangDataAnimation();
                                xieyangChart.startDataAnimation();
                            } else {
                                xieyangChart.setVisibility(View.INVISIBLE);
                            }

                        } catch (Exception E) {
                            E.printStackTrace();
                        }


                        // sleepDataAnimation();
                        // sleepChart.startDataAnimation();
                    } else {
                        ToastUtil.showShort(getActivity(), getString(R.string.settings_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        };
        getData();
    }

    private String getHeartList(List<HeartRatePointTime> hetList){
        StringBuffer sg = new StringBuffer();
        for(HeartRatePointTime ht : hetList){
            sg.append(ht.getRtc());
            sg.append(ht.getHeartRate());
            sg.append("||");
        }
        return sg.toString();
    }



    @Override
    protected int getContentViewId() {
        return R.layout.activity_datarepor;
    }

    private void getHeartRateData() {
        List<Line> lines = new ArrayList<>();
        if (null != dataActivityReport.getHeartRate().getHeartRate()) {
            heartLinechart.setVisibility(View.VISIBLE);
            List<HeartRatePointTime> heartRates = dataActivityReport.getHeartRate().getHeartRate();
            HeartRatePointTime heartRatePointTime;
            String dateNum;
            int avghata = heartRates.get(0).getHeartRate();
            reportxinlv.setText(avghata + getResources().getString(R.string.BPM));
            int rateNum;
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                for (int i = 0; i < heartRates.size(); i++) {
                    heartRatePointTime = heartRates.get(i);
                    dateNum = heartRatePointTime.getRtc();
                    rateNum = heartRatePointTime.getHeartRate();

                    Integer timeNum = Integer.valueOf(dateNum.substring(11, 13));
                    if (timeNum == j) {
                        values.add(new PointValue(j + 1, rateNum));
                    }
                }
            }

            Line line = new Line(values);
            line.setColor(getResources().getColor(R.color.red_heart));
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(true);//显示节点数据
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            // line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
            stepData = new LineChartData(lines);
            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
           /* if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }*/
                stepData.setValueLabelTextSize(10);
                stepData.setAxisXBottom(axisX);
                stepData.setAxisYLeft(axisY);
            } else {
                stepData.setAxisXBottom(null);
                stepData.setAxisYLeft(null);
            }
            stepData.setBaseValue(Float.NEGATIVE_INFINITY);
            stepData.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
            stepData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
            heartLinechart.setZoomEnabled(false);
            heartLinechart.setLineChartData(stepData);
        } else {
            heartLinechart.setVisibility(View.INVISIBLE);
        }


    }

    //内部类画睡眠数据
    class mydraw extends View {
        public mydraw(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            try {
                JSONArray oArray = new JSONArray(mysleep.toString());
                int count = 20;
                int sleeptime = 0;
                for (int i = 0; i < oArray.length(); i++) {
                    JSONObject jo = (JSONObject) oArray.get(i);
                    String sumBean = jo.optString("sumBean").toString();//深睡时长sumBean
                    String getType = jo.optString("getType").toString();//睡眠类型getType
                    sleeptime += Integer.valueOf(sumBean);
                    myPaint = new Paint();
                    myPaint2 = new Paint();
                    myPaint.setAntiAlias(true);
                    myPaint2.setAntiAlias(true);
                    //绘制条形图
                    myPaint.setColor(Color.parseColor("#511D82")); //设置画笔颜色
                    myPaint.setStyle(Paint.Style.FILL); //设置填充
                    myPaint2.setColor(Color.parseColor("#6924A9")); //设置画笔颜色
                    myPaint2.setStyle(Paint.Style.FILL); //设置填充
                    if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                        if (getType.equals("0")) {
                            canvas.drawRect(new Rect(count, 10, 50 + count + Integer.valueOf(sumBean), 400), myPaint2);// 画浅睡
                            count = 50 + count + Integer.valueOf(sumBean);
                        } else {
                            canvas.drawRect(new Rect(count, 200, count + Integer.valueOf(sumBean) * 5, 400), myPaint);//画深睡
                            count = count + Integer.valueOf(sumBean) * 5;
                        }
                    } else {
                        if (getType.equals("0")) {
                            canvas.drawRect(new Rect(count, 10, 50 + count + Integer.valueOf(sumBean), 400), myPaint2);// 画浅睡
                            count = 50 + count + Integer.valueOf(sumBean);
                        } else {
                            canvas.drawRect(new Rect(count, 200, count + Integer.valueOf(sumBean), 400), myPaint);//画深睡
                            count = count + Integer.valueOf(sumBean);
                        }
                    }


                }
                mysleep.clear();
                count = 20;
            } catch (Exception E) {
                E.printStackTrace();
            }


        }

        public mydraw(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }
    }

    private void getSleepData() {
        ArrayList<B15PSleepBean> sportHours = dataActivityReport.getSleep().getSleep();
        String sleepCurve = null;
        int sleepLen;
        if ("B15P".equals(MyCommandManager.DEVICENAME)) {
            sleepCurve = sportHours.get(0).getSleepCurveP();
            sleepLen = sportHours.get(0).getSleepLen();
            try {
                mysleeptime.setText(sleepLen / 60 + getResources().getString(R.string.hour) + sleepLen % 60 + getResources().getString(R.string.minute));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (MyCommandManager.DEVICENAME.contains("B15S")) {
            sleepCurve = sportHours.get(0).getSleepCurveS();
        }

        ArrayList<SumBean> sumBeanArrayList = Common.getSleepSumList(sleepCurve);
        mysleep = new ArrayList();
        for (SumBean sumBean : sumBeanArrayList) {
            MyLogUtil.i("-sumBean->" + sumBean.getSum() + " " + sumBean.getType());
            try {
                JSONObject sumBeanJSONObject = new JSONObject();
                sumBeanJSONObject.put("sumBean", sumBean.getSum());
                sumBeanJSONObject.put("getType", sumBean.getType());
                mysleep.add(sumBeanJSONObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       /* if ("B15P".equals(MyCommandManager.DEVICENAME)) {

        }*/
        mydraw aaa = new mydraw(getActivity());
        aaa.invalidate();
        shuimianjilu.addView(aaa);


    }


    private void getStepData() {
        if (null != dataActivityReport.getSport().getHours()) {
            ArrayList<Sporthours> sportHours = dataActivityReport.getSport().getHours();
            MyLogUtil.i("-sportHours-size->" + sportHours.size());
            //Collections.sort(sportHours);
            //int numSubcolumns = 1;
            // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values;
            ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
            ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
            String dateNum;
            int stepNum;
            int step = 0;
            Integer timeNum;
            for (int i = 0; i < numberOfPoints; ++i) {
                values = new ArrayList<>();
                for (int j = 0; j < sportHours.size(); ++j) {
                    dateNum = sportHours.get(j).getRtc();
                    stepNum = sportHours.get(j).getStepNumber();
                    timeNum = Integer.valueOf(dateNum.substring(11, 13));
                    if (i == 0) {
                        step = stepNum + step;
                        MyLogUtil.i("-sportHours-size->" + step);
                        Steps.setText(step + getResources().getString(R.string.steps));
                    }
                    if (i == timeNum) {
                        values.add(new SubcolumnValue((float) stepNum, getResources().getColor(R.color.colorAccent)));
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
                Axis axisY = new Axis().setHasLines(true);
                stepColumdata.setAxisXBottom(axisX);
            } else {
                stepColumdata.setAxisXBottom(null);
                stepColumdata.setAxisYLeft(null);
            }
            stepChart.setZoomEnabled(false);//设置是否支持缩放
            stepColumdata.setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
            stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
            stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
            stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
            stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
            stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
            stepChart.setColumnChartData(stepColumdata);
        }

    }

    private void stepDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()));

            }
        }
    }

    private void getxieyaData() {
        if (null != dataActivityReport.getBloodPressure().getBloodPressure()) {
            ArrayList<BloodPressureList> sportHours = dataActivityReport.getBloodPressure().getBloodPressure();
            MyLogUtil.i("-sportHours-sizeyuyy->" + sportHours.toString());
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values, values2;
            ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
            ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
            String dateNum;
            int Systolic;
            int Diastolic;
            int step = 0;
            Integer timeNum;
            for (int i = 0; i < numberOfPoints; ++i) {
                values = new ArrayList<>();
                values2 = new ArrayList<>();
                for (int j = 0; j < sportHours.size(); ++j) {
                    dateNum = sportHours.get(j).getRtc();
                    Systolic = sportHours.get(j).getSystolic();
                    Diastolic = sportHours.get(j).getDiastolic();
                    timeNum = Integer.valueOf(dateNum.substring(11, 13));
                    axisValuesY.add(new AxisValue(Systolic).setValue(j));// 添加Y轴显示的刻度值
                    axisValuesY.add(new AxisValue(Diastolic).setValue(j));// 添加Y轴显示的刻度值
                    axisValuesX.add(new AxisValue(Systolic).setValue(Systolic));// 添加Y轴显示的刻度值
                    axisValuesX.add(new AxisValue(Diastolic).setValue(Diastolic));// 添加Y轴显示的刻度值
                    if (i == 0) {
                        // step=stepNum+step;
                        MyLogUtil.i("-sportHours-size->" + step);
                        //  Steps.setText(step+getResources().getString(R.string.steps));
                    }
                    if (i == timeNum) {
                        values.add(new SubcolumnValue((float) Systolic, getResources().getColor(R.color.colorPrograss)));
                        values2.add(new SubcolumnValue((float) Diastolic, getResources().getColor(R.color.colorAccent)));
//设置X轴的柱子所对应的属性名称

                        // axisValuesX.add(new AxisValue(j).setValue(j).setLabel(String label));// 添加X轴显示的刻度值并设置X轴显示的内容
                    }
                }


                //将每个属性的拥有的柱子，添加到Column中
                Column column = new Column(values);
                //是否显示每个柱子的Lable
                column.setHasLabels(false);
                //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
                column.setHasLabelsOnlyForSelected(true);
                //将每个属性得列全部添加到List中
                columns.add(column);
                //将每个属性的拥有的柱子，添加到Column中
                Column column2 = new Column(values2);
                //是否显示每个柱子的Lable
                column2.setHasLabels(true);
                //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
                column2.setHasLabelsOnlyForSelected(true);
                //将每个属性得列全部添加到List中
                columns.add(column2);

            }
            stepColumdata = new ColumnChartData(columns);
            if (hasAxes) {
                Axis axisX = new Axis();

                Axis axisY = new Axis().setHasLines(true);
                // stepColumdata.setAxisXBottom(axisX);
            } else {
                stepColumdata.setAxisXBottom(null);
                stepColumdata.setAxisYLeft(null);
            }
            stepColumdata.setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
            stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
            stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
            stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
            stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
            stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
            xieyaChart.setZoomEnabled(false);
            xieyaChart.setColumnChartData(stepColumdata);
        }

    }

    private void xieyaDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()));
            }
        }
    }

    private void getxieyangData() {
        ArrayList<BloodOxygenList> sportHours = dataActivityReport.getBloodOxygen().getBloodOxygen();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
        String dateNum;
        int BloodOxygen;
        int step = 0;
        Integer timeNum;
        for (int i = 0; i < numberOfPoints; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < sportHours.size(); ++j) {
                dateNum = sportHours.get(j).getRtc();
                BloodOxygen = sportHours.get(j).getBloodOxygen();
                timeNum = Integer.valueOf(dateNum.substring(11, 13));
                axisValuesY.add(new AxisValue(BloodOxygen).setValue(j));// 添加Y轴显示的刻度值
                axisValuesX.add(new AxisValue(BloodOxygen).setValue(BloodOxygen));// 添加Y轴显示的刻度值

                if (i == 0) {
                 /*   step=stepNum+step;
                    MyLogUtil.i("-sportHours-size->" + step);
                    Steps.setText(step+getResources().getString(R.string.steps));*/
                }
                if (i == timeNum) {
                    values.add(new SubcolumnValue((float) BloodOxygen, getResources().getColor(R.color.licheng_start_color)));

//设置X轴的柱子所对应的属性名称

                    // axisValuesX.add(new AxisValue(j).setValue(j).setLabel(String label));// 添加X轴显示的刻度值并设置X轴显示的内容
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

            Axis axisY = new Axis().setHasLines(false);//显示数据吗？
            stepColumdata.setAxisXBottom(axisX);
        } else {
            stepColumdata.setAxisXBottom(null);
            stepColumdata.setAxisYLeft(null);
        }
        stepColumdata.setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
        stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
        stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
        stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
        stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
        xieyangChart.setZoomEnabled(false);
        xieyangChart.setColumnChartData(stepColumdata);
    }

    private void xieyangDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()));
            }
        }
    }

    private void getData() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put("date", sdf.format(new Date()));
        map.put("deviceCode", MyCommandManager.ADDRESS);
        //map.put("userId", B18iCommon.customer_id);
        map.put("userId", (String) SharedPreferencesUtils.readObject(getActivity(), "userId"));
        Log.e("", "------device----" + MyCommandManager.ADDRESS + "-userid----" + Common.customer_id);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getData, mapjson);
    }

    @OnClick({R.id.dareport_step, R.id.dareport_shuimian, R.id.dareport_xinlv, R.id.dareport_xieya, R.id.dareport_xieyang})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dareport_step:
                startActivity(new Intent(getActivity(), DataReportItemActivity.class));
                break;
            case R.id.dareport_shuimian:
                startActivity(new Intent(getActivity(), DataReportItemSleepActivity.class));
                break;
            case R.id.dareport_xinlv:
                startActivity(new Intent(getActivity(), DataReportItemHateActivity.class));
                break;
            case R.id.dareport_xieya:
                startActivity(new Intent(getActivity(), DataReportItemXieYaActivity.class));
                break;
            case R.id.dareport_xieyang:
                startActivity(new Intent(getActivity(), DataReportItemXieYangActivity.class));
                break;
        }
    }
}
