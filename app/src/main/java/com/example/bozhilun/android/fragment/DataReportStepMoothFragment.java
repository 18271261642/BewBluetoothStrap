package com.example.bozhilun.android.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.SportWeekMonth;
import com.example.bozhilun.android.bean.StepWeekMonth;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
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
 * 步数月
 */

public class DataReportStepMoothFragment extends BaseFragment {

    @BindView(R.id.tabs_dtarepot_mooth) TabLayout tabs;
    @BindView(R.id.chart_step_moonth) ColumnChartView stepChart;

    @BindView(R.id.stepval_tv_mooth) TextView Mysteps;
    @BindView(R.id.activityval_tv_mooth) TextView activityva;
    @BindView(R.id.lichengval_tv_mooth) TextView licheng;
    @BindView(R.id.calval_tv_mooth) TextView calval;

    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private SportWeekMonth dataActivityReport;
    private ColumnChartData stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    String EndTiem;//结束时间
    private String[] weekDay = new String[] {  "01", "02", "03", "04", "05", "06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31" };
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    int activityTime;
    int Steps;
    @Override
    protected void initViews() {

        int datayue=calendar.get(Calendar.MONTH)+1;

        for (int i = 1; i < datayue-1; i++) {
            tabs.addTab(tabs.newTab().setText(i+getResources().getString(R.string.data_report_month)));
        }
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.shangyue)));
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.benyue)));
        ///移动到最后去
        new Handler().postDelayed((new Runnable() { @Override public void run() {
            tabs.scrollTo(10000,0);} }),5);
        tabs.setScrollPosition(datayue, 1F, true);
        int tabCount = tabs.getTabCount();
        for(int i = 0 ; i < tabCount; i++){
            TabLayout.Tab tab = tabs.getTabAt(i);
            if(tab == null){continue;}
            Class c = tab.getClass(); // 这里使用到反射，拿到Tab对象后获取Class
            try{//    Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if(field ==null) {continue;}
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if(view ==null) {continue;}
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {@Override
                public void onClick(View v) {
                    int position = (int)view.getTag();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

                    String datas;
                    if(String.valueOf(position+1).length()==1){
                        datas="0"+String.valueOf(position+1);
                    }else{
                        datas=String.valueOf(position+1);
                    }
                    getData(sdf.format(new Date())+"-"+datas+"");
                    stepChart.postInvalidate();
                    stepChart.invalidate();
                }});}catch(NoSuchFieldException e) {e.printStackTrace();
            }catch(IllegalAccessException e) {e.printStackTrace();}}


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
                                int weekCount;
                                for (int i = 0; i < 31; ++i) {
                                    values = new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {
                                        dateNum = sportHours.get(j).getDate();
                                        stepNum = sportHours.get(j).getStepNumber();

                                        weekCount = Integer.valueOf(dateNum.substring(8, 10));

                                        if (i == weekCount-1) {
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
                                    axisX.setTextSize(8);
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
                                stepColumdata.setFillRatio(0.3F);//设置柱形图的宽度
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
        //年月日
        String yue,ri;
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
        sdf = new SimpleDateFormat("yyyy-MM");
        getData(sdf.format(new Date()).toString());
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
        return R.layout.fragment_step_month_item;
    }

    private void getData(String date) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        map.put("date",date);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getSportMonth, mapjson);
    }
}
