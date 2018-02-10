package com.example.bozhilun.android.fragment;


import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.Sport;
import com.example.bozhilun.android.bean.Sporthours;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

import static com.example.bozhilun.android.util.Common.formatDouble;

/**
 * Created by thinkpad on 2017/3/24.
 * 步数日
 */

public class DataReportStepItemFragment extends BaseFragment {
    @BindView(R.id.chart_step)
    ColumnChartView stepChart;
    @BindView(R.id.tabs_dtarepot)
    TabLayout tabs;

    @BindView(R.id.stepval_tv)
    TextView Mysteps;
    @BindView(R.id.activityval_tv)
    TextView activityva;
    @BindView(R.id.lichengval_tv)
    TextView licheng;
    @BindView(R.id.calval_tv)
    TextView calval;


    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private Sport dataActivityReport;
    private ColumnChartData stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();

    String activityTime;

    @Override
    protected void initViews() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {

                Log.e("TT", "--------resultstep--" + result.toString());

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");

                    Gson gson = new Gson();

                    if ("001".equals(loginResult)) {
                        String stepSum = jsonObject.getString("stepSum");
                        JSONObject AAA = new JSONObject(stepSum);
                        activityTime = AAA.optString("activityTime");

                        dataActivityReport = gson.fromJson(result, Sport.class);
                        try {
                            if (null != dataActivityReport.getHours()) {
                                stepChart.setVisibility(View.VISIBLE);
                                ArrayList<Sporthours> sportHours = dataActivityReport.getHours();
                                //Collections.sort(sportHours);
                                //int numSubcolumns = 1;
                                // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values;
                                String dateNum;
                                int stepNum;
                                int step = 0;
                                Integer timeNum;
                                for (int i = 0; i < 24; ++i) {
                                    values = new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {
                                        dateNum = sportHours.get(j).getRtc();
                                        stepNum = sportHours.get(j).getStepNumber();
                                        timeNum = Integer.valueOf(dateNum.substring(11, 13));
                                        if (i == 0) {
                                            step = stepNum + step;
                                            MyLogUtil.i("-sportHours-size->" + step);


                                        }

                                        if (i == timeNum) {
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
                                    axisX.setTextSize(6);
                                    Axis axisY = new Axis().setHasLines(true);
                                    stepColumdata.setAxisXBottom(axisX);
                                } else {
                                    stepColumdata.setAxisXBottom(null);
                                    stepColumdata.setAxisYLeft(null);
                                }
                                stepChart.setZoomEnabled(false);//设置是否支持缩放
                                stepColumdata.setValueLabelTypeface(Typeface.DEFAULT);// 设置数据文字样式
                                //stepColumdata.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
                                stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                                stepColumdata.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
                                stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
                                stepChart.setColumnChartData(stepColumdata);
                                prepareDataAnimation();
                                stepChart.startDataAnimation();
                                stepChart.setInteractive(true);

                                Mysteps.setText(step + "");
                                licheng.setText(formatDouble((0.766 * step) / 1000) + "");
                                activityva.setText(activityTime);
                                calval.setText(formatDouble(((0.766 * step) / 1000) * 65.4) + "");
                            }
                        } catch (Exception E) {
                            E.printStackTrace();
                        }

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


        List<String> mDataList = Common.getDayListOfMonth(getActivity());
        for (int i = 0; i < mDataList.size(); i++) {
            tabs.addTab(tabs.newTab().setText(mDataList.get(i)));
        }
        tabs.getTabAt(mDataList.size() - 1).select();
        tabs.setScrollPosition(mDataList.size() - 1, 1F, true);
        ///移动到最后去
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                tabs.scrollTo(10000, 0);
            }
        }), 5);
        int tabCount = tabs.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            if (tab == null) {
                continue;
            }
            Class c = tab.getClass(); // 这里使用到反射，拿到Tab对象后获取Class
            try {//    Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) {
                    continue;
                }
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) view.getTag();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                        sdf.format(new Date());
                        String datas;
                        if (String.valueOf(position + 1).length() == 1) {
                            datas = "0" + String.valueOf(position + 1);
                        } else {
                            datas = String.valueOf(position + 1);
                        }
                        stepChart.postInvalidate();
                        stepChart.invalidate();
                        getData(sdf.format(new Date()).toString() + "-" + datas);

                    }
                });
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        getData(sdf.format(new Date()).toString());
    }


    private void prepareDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
              /*  value.setTarget(Float.valueOf(value.getValue()) );
                value.update(0.5f);*/
                value.setValue(Float.valueOf(value.getValue()));
            }
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_step_item;
    }

    private void getData(String data) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();

        map.put("date", data);
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        String mapjson = gson.toJson(map);
        Log.e("Test", "-------data----" + data + "--deviceCode--" + MyCommandManager.ADDRESS + "---userId--" + Common.customer_id);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getSportH, mapjson);
    }
}
