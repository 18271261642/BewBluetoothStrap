package com.example.bozhilun.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.bozhilun.android.bean.DataActivityReport;
import com.example.bozhilun.android.bean.Sleep;
import com.example.bozhilun.android.bean.SleepBean;
import com.example.bozhilun.android.bean.Sport;
import com.example.bozhilun.android.bean.SportWeekMonth;
import com.example.bozhilun.android.bean.Sporthours;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SumBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DateFormat;
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
 * 睡眠日
 */

public class DataReportStepItemSleepFragment extends BaseFragment {
    @BindView(R.id.chart_step_shuimianone) RelativeLayout shuimianjilu;
    @BindView(R.id.tabs_dtarepot_shuimianone) TabLayout tabs;

    @BindView(R.id.stepval_tv_shuimianone) TextView quantian;
    @BindView(R.id.activityval_tv_shuimianone) TextView Shengshuimian;
    @BindView(R.id.lichengval_tv_shuimianone) TextView Qianshuimian;

    @BindView(R.id.start_time) TextView Starttime;//睡眠开始时间
    @BindView(R.id.end_time) TextView EndTime;//睡眠结束时间


    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private Sleep dataActivityReport;

    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
    mydraw aaa;


    private List  mysleep;
    private int shengshui,QIANSHUI,zongshichang;
    @Override
    protected void initViews() {
        aaa=new mydraw(getActivity());
        mysleep=new ArrayList();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    MyLogUtil.i("getSleepCurveP"+jsonObject.toString());
                    String loginResult = jsonObject.getString("resultCode");
                    String  sleep= jsonObject.getString("sleep");
                    Gson gson = new Gson();
                    if ("001".equals(loginResult)&&!sleep.equals("[]")) {
                        shuimianjilu.removeView(aaa);
                        mysleep=new ArrayList();
                        JSONArray SLEEPS=new JSONArray(sleep);
                        for (int i = 0; i < SLEEPS.length(); i++) {
                            JSONObject jo = (JSONObject)SLEEPS.get(i);
                            String starttime = jo.getString("startTime");
                            String endtime = jo.getString("endTime");
                            Starttime.setText(getResources().getString(R.string.start_time)+":  "+starttime);
                            EndTime.setText(getResources().getString(R.string.end_time)+":  "+endtime);
                        }


                        dataActivityReport = gson.fromJson(result, Sleep.class);
                        try{

                            ArrayList<B15PSleepBean> sportHours = dataActivityReport.getSleep();
                            if(null!=dataActivityReport.getSleep()){
                                String sleepCurve = null;
                                if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                                    sleepCurve = sportHours.get(0).getSleepCurveP();

                                } else if (MyCommandManager.DEVICENAME.contains("B15S")) {
                                    sleepCurve = sportHours.get(0).getSleepCurveS();
                                }

                                ArrayList<SumBean> sumBeanArrayList = Common.getSleepSumList(sleepCurve);
                                for (SumBean sumBean : sumBeanArrayList) {
                                    try{
                                        JSONObject sumBeanJSONObject=new JSONObject();
                                        sumBeanJSONObject.put("sumBean",sumBean.getSum());
                                        sumBeanJSONObject.put("getType",sumBean.getType());
                                        mysleep.add(sumBeanJSONObject);
                                    }catch (Exception e){e.printStackTrace();}
                                }
                                shuimianjilu.setVisibility(View.VISIBLE);

                                aaa.invalidate();
                                shuimianjilu.invalidate();
                                shuimianjilu.addView(aaa);
                                JSONObject bbb =new JSONObject(jsonObject.getString("sleepTotal").toString());
                                shengshui= bbb.getInt("deepLen");
                                QIANSHUI= bbb.getInt("shallowLen");
                                zongshichang= bbb.getInt("sleepLen");
                                quantian.setText(String.valueOf(zongshichang/60)+getResources().getString(R.string.hour)+String.valueOf(zongshichang%60)+getResources().getString(R.string.minute));
                                Shengshuimian.setText(String.valueOf(shengshui/60)+getResources().getString(R.string.hour)+String.valueOf(shengshui%60)+getResources().getString(R.string.minute));
                                Qianshuimian.setText(String.valueOf(QIANSHUI/60)+getResources().getString(R.string.hour)+String.valueOf(QIANSHUI%60)+getResources().getString(R.string.minute));




                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {
                        shuimianjilu.setVisibility(View.INVISIBLE);
                         quantian.setText("0");
                        Shengshuimian.setText("0");
                        Qianshuimian.setText("0");

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
        tabs.setScrollPosition(mDataList.size()-1, 1F, true);
        ///移动到最后去
       new Handler().postDelayed((new Runnable() { @Override public void run() {
             tabs.scrollTo(10000,0);} }),5);
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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    sdf.format(new Date());
                    String datas;
                    if(String.valueOf(position+1).length()==1){
                        datas="0"+String.valueOf(position+1);
                    }else{
                        datas=String.valueOf(position+1);
                    }
                    mysleep.clear();
                    getData(sdf.format(new Date()).toString()+"-"+datas);

                    }});}catch(NoSuchFieldException e) {e.printStackTrace();
            }catch(IllegalAccessException e) {e.printStackTrace();}}


        getData(sdf.format(new Date()).toString());
    }







    @Override
    protected int getContentViewId() {
        return R.layout.fragment_stepsleeep_item;
    }

    private void getData(String data) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();

        map.put("date", data);
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getSleepD, mapjson);
    }



    //内部类画睡眠数据
    class  mydraw extends  View{
        public mydraw(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            try{
                JSONArray oArray=new JSONArray(mysleep.toString());
                int count=20;
                int sleeptime=0;
                for (int i = 0; i < oArray.length(); i++) {
                    JSONObject jo = (JSONObject)oArray.get(i);
                    String sumBean=jo.optString("sumBean").toString();//深睡时长sumBean
                    String getType=jo.optString("getType").toString();//睡眠类型getType
                    sleeptime+=Integer.valueOf(sumBean);
                    Paint   myPaint = new Paint();
                    Paint   myPaint2 = new Paint();
                    myPaint.setAntiAlias(true);
                    myPaint2.setAntiAlias(true);
                    //绘制条形图
                    myPaint.setColor(Color.parseColor("#511D82")); //设置画笔颜色
                    myPaint.setStyle(Paint.Style.FILL); //设置填充
                    myPaint2.setColor(Color.parseColor("#6924A9")); //设置画笔颜色
                    myPaint2.setStyle(Paint.Style.FILL); //设置填充


                    if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                        if(getType.equals("0")){
                            canvas.drawRect(new Rect(count, 10,50+count+Integer.valueOf(sumBean), 400), myPaint2);// 画浅睡
                            count=50+count+Integer.valueOf(sumBean);
                        }else {
                            canvas.drawRect(new Rect(count,50,count+Integer.valueOf(sumBean)*5,400), myPaint);//画深睡
                            count=count+Integer.valueOf(sumBean)*5;
                        }
                    }else{
                        if(getType.equals("0")){
                            canvas.drawRect(new Rect(count, 10,50+count+Integer.valueOf(sumBean), 400), myPaint2);// 画浅睡
                            count=50+count+Integer.valueOf(sumBean);
                        }else {
                            canvas.drawRect(new Rect(count,50,count+Integer.valueOf(sumBean),400), myPaint);//画深睡
                            count=count+Integer.valueOf(sumBean);
                        }
                    }



                }
                MyLogUtil.i("--sleeptime-->" + sleeptime);
                count=20;
            }catch (Exception E){E.printStackTrace();}




        }
        public mydraw(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }
    }
}
