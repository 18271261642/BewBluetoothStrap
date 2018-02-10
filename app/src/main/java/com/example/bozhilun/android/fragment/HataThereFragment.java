package com.example.bozhilun.android.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.HeartRateList;
import com.example.bozhilun.android.bean.HeartRateone;
import com.example.bozhilun.android.bean.SleepData;
import com.example.bozhilun.android.bean.SleepList;
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
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by thinkpad on 2017/3/24.
 * 心率月
 */

public class HataThereFragment extends BaseFragment {

    @BindView(R.id.tabs_dtarepot_xinlvthere) TabLayout tabs;
    @BindView(R.id.chart_shumian_xinlvthere) ColumnChartView stepChart;

    @BindView(R.id.stepval_tv_xinlvthere) TextView Mysteps;
    @BindView(R.id.activityval_tv_xinlvthere) TextView activityva;
    @BindView(R.id.lichengval_tv__xinlvthere) TextView licheng;


    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private HeartRateone dataActivityReport;
    private ColumnChartData stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    String EndTiem;//结束时间
    private String[] weekDay = new String[] {  "01","", "02","", "03","", "04","", "05","", "06","","07","","08","","09","","10","","11",
            "","12","","13","","14","","15","","16","","17","","18","",
            "19","","20","","21","","22","","23","","24","","25","","26","","27","","28","","29","","30","","31","" };
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    String Steps;
    String  QIANSHUI,shengshui,zongshichang;
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
                        dataActivityReport = gson.fromJson(result, HeartRateone.class);
                        try{
                            if(null!=dataActivityReport.getHeartRate()){
                                stepChart.setVisibility(View.VISIBLE);
                                ArrayList<HeartRateList> sportHours = dataActivityReport.getHeartRate();
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values;
                                List<SubcolumnValue> values2;
                                String dateNum;
                                int MaxHeartRate;
                                int minHeart;
                                int deep;
                                int weekCount;
                                for (int i = 0; i < 31; ++i) {
                                    values = new ArrayList<>();
                                    values2 = new ArrayList<>();

                                    for (int j = 0; j < sportHours.size(); ++j) {

                                        dateNum = sportHours.get(j).getRtc();
                                        MaxHeartRate = sportHours.get(j).getMaxHeartRate();
                                        minHeart = sportHours.get(j).getMinHeartRate();
                                        weekCount = Integer.valueOf(dateNum.substring(8, 10));

                                        if (i == weekCount-1) {
                                            values.add(new SubcolumnValue((float) MaxHeartRate, getResources().getColor(R.color.chang_white)));
                                            values.add(new SubcolumnValue((float) minHeart, getResources().getColor(R.color.chang_white)));
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
                               // stepColumdata.setFillRatio(0.3F);//设置柱形图的宽度
                                stepColumdata.setValueLabelsTextColor(Color.WHITE);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
                                stepChart.setColumnChartData(stepColumdata);
                                prepareDataAnimation();
                                stepChart.startDataAnimation();
                                stepChart.setVisibility(View.VISIBLE);
                                Steps=jsonObject.getString("max_min");
                                JSONObject avgSleepjsonObject=new JSONObject(Steps);
                                QIANSHUI=  avgSleepjsonObject.getString("maxHeartRate");
                                zongshichang=avgSleepjsonObject.getString("minHeartRate");
                                if(!"null".equals(QIANSHUI)){
                                    Mysteps.setText(QIANSHUI+"");
                                    licheng.setText(String.valueOf(zongshichang)+"");
                                    activityva.setText((Integer.valueOf(QIANSHUI)+Integer.valueOf(zongshichang))/2+"");
                                }else{
                                    Mysteps.setText("- -");
                                    activityva.setText("- -");
                                    licheng.setText("- -");
                                }


                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {
                        stepChart.setVisibility(View.GONE);
                        Mysteps.setText("- -");
                        activityva.setText("- -");
                        licheng.setText("- -");

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
        return R.layout.fragment_hatathere;
    }

    private void getData(String date) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        map.put("date",date);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getHeartRateM, mapjson);
    }
}
