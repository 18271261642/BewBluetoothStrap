package com.example.bozhilun.android.siswatch.data;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.view.RiseNumberTextView;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;
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
import butterknife.Unbinder;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017/10/26.
 */

public class WatchDataNewsFragment extends Fragment {

    private static final String TAG = "新的数据";

    //H8拍照
    View newsWatchView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //显示步数的textView
    @BindView(R.id.watch_h8_dataStepTv)
    RiseNumberTextView watchH8DataStepTv;
    //显示路程的textView
    @BindView(R.id.watch_h8_dataDisTv)
    TextView watchH8DataDisTv;
    //显示卡里路的textView
    @BindView(R.id.watch_h8_dataKcalTv)
    TextView watchH8DataKcalTv;
    //同步数据的卡里路
    @BindView(R.id.watch_h8_syncdataTv)
    TextView watchH8SyncdataTv;
    //周的textView
    @BindView(R.id.week_tv1)
    TextView weekTv1;
    //月的TextView
    @BindView(R.id.month_tv2)
    TextView monthTv2;
    //年的textView
    @BindView(R.id.year_tv3)
    TextView yearTv3;
    //周的View
    @BindView(R.id.week_view1)
    View weekView1;
    //月的View
    @BindView(R.id.month_view2)
    View monthView2;
    //年的View
    @BindView(R.id.year_view3)
    View yearView3;
    //显示周的折线图
    @BindView(R.id.watch_newdataweekChar)
    LineChartView watchNewdataweekChar;
    Unbinder unbinder;
    @BindView(R.id.xTv1)
    TextView xTv1;
    @BindView(R.id.xTv2)
    TextView xTv2;
    @BindView(R.id.xTv3)
    TextView xTv3;
    @BindView(R.id.xTv4)
    TextView xTv4;
    @BindView(R.id.xTv5)
    TextView xTv5;
    @BindView(R.id.xTv6)
    TextView xTv6;
    @BindView(R.id.xTv7)
    TextView xTv7;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    List<WatchDataDatyBean> watchDataList;
    //周的折线图相关
    private List<PointValue> mPointValues;// = new ArrayList<>();  //数据
    private List<AxisValue> mAxisXValues;// = new ArrayList<>();   //x周显示

    private String weekTag = "week";
    List<String> dateList;
    private Map<String,Integer> map;
    private Map<String,Integer> sumMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        newsWatchView = inflater.inflate(R.layout.fragment_watch_data_layout, container, false);
        unbinder = ButterKnife.bind(this, newsWatchView);

        initData();
        watchDataList = new ArrayList<>();
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String jsonObject) {
                Log.e(TAG, "----result---" + jsonObject.toString());
                if (!WatchUtils.isEmpty(jsonObject.toString())) {
                    try {
                        JSONObject jso = new JSONObject(jsonObject);
                        if (jso.getString("resultCode").equals("001")) {
                            String daydata = jso.getString("day");
                            watchDataList.clear();
                            getWatchDataList(daydata);
                            if(!weekTag.equals("year")){
                                for (int i = 0; i < watchDataList.size(); i++) {
                                    //对应value值
                                    mPointValues.add(new PointValue(i, watchDataList.get(i).getStepNumber()));
                                    //x轴值
                                    //mAxisXValues.add(new AxisValue(i).setLabel(watchDataList.get(i).getRtc().substring(5, watchDataList.get(i).getRtc().length())));
                                }
                                showWeekData(); //显示折线图
                            }

                            if(!WatchUtils.isEmpty(weekTag)){
                                if(weekTag.equals("week")){
                                    xTv1.setText(watchDataList.get(0).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv2.setText(watchDataList.get(1).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv3.setText(watchDataList.get(2).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv4.setText(watchDataList.get(3).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv5.setText(watchDataList.get(4).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv6.setText(watchDataList.get(5).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                    xTv7.setText(watchDataList.get(6).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                                }else if(weekTag.equals("month")){
                                    xTv1.setText(watchDataList.get(1).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv2.setText(watchDataList.get(1*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv3.setText(watchDataList.get(2*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv4.setText(watchDataList.get(3*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv5.setText(watchDataList.get(4*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv6.setText(watchDataList.get(5*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                    xTv7.setText(watchDataList.get(6*5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                }else if(weekTag.equals("year")){   //2017-10-27
                                    sumMap = new HashMap<>();
                                    int sum = 0;
                                    for(int i = 0;i<watchDataList.size();i++){
                                        int n = 1;
                                        String strDate = watchDataList.get(i).getRtc().substring(0,7);
                                        if(sumMap.get(strDate) != null){
                                            sum +=watchDataList.get(i).getStepNumber();
                                        }
                                        sumMap.put(strDate,sum);
                                    }
                                    Log.e(TAG,"----mapss------"+sumMap.toString());
                                    dateList = new ArrayList<>();
                                    dateList.clear();
                                    //遍历map
                                    for(Map.Entry<String,Integer> entry : sumMap.entrySet()){
                                        dateList.add(entry.getKey().trim());
                                    }
                                    //升序排列
                                    Collections.sort(dateList, new Comparator<String>() {
                                        @Override
                                        public int compare(String s, String t1) {
                                            return s.compareTo(t1);
                                        }
                                    });
                                    for(int k = 0;k<dateList.size();k++){
                                        mPointValues.add(new PointValue(k, sumMap.get(dateList.get(k))));
                                        //mAxisXValues.add(new AxisValue(k).setLabel(watchDataList.get(i).getRtc().substring(5, watchDataList.get(i).getRtc().length())));
                                    }
                                    showWeekData(); //显示折线图
                                    xTv1.setText(StringUtils.substringAfter(dateList.get(0),"-"));
                                    xTv2.setText(StringUtils.substringAfter(dateList.get(2),"-"));
                                    xTv3.setText(StringUtils.substringAfter(dateList.get(4),"-"));
                                    xTv4.setText(StringUtils.substringAfter(dateList.get(6),"-"));
                                    xTv5.setText(StringUtils.substringAfter(dateList.get(8),"-"));
                                    xTv6.setText(StringUtils.substringAfter(dateList.get(10),"-"));
                                    xTv7.setText(StringUtils.substringAfter(dateList.get(12),"-"));

                                }
                            }else{
                                xTv1.setText(watchDataList.get(0).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv2.setText(watchDataList.get(1).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv3.setText(watchDataList.get(2).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv4.setText(watchDataList.get(3).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv5.setText(watchDataList.get(4).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv6.setText(watchDataList.get(5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                                xTv7.setText(watchDataList.get(6).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            }


                        } else {
                            ToastUtil.showToast(getActivity(), jso.getString("msg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        };
        //默认获取周的数据
        getSportWatchData("week");
        return newsWatchView;
    }

    private void initData() {
        tvTitle.setText("" + getResources().getString(R.string.data) + "");
        clearViewBack();
        weekTv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.newwatchdatatvcolor));
        weekView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
        mPointValues = new ArrayList<>();
        mAxisXValues = new ArrayList<>();
        setStepsTvShow();
    }

    private void setStepsTvShow() {
        SharedPreferencesUtils.getParam(getActivity(), "watchstepsdistants", ""); //保存路程
        String kal = ((String) SharedPreferencesUtils.getParam(getActivity(), "watchkcal", "")).trim();
        SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "");
        String stp = ((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")).trim();
        if(!WatchUtils.isEmpty(stp)){
            watchH8DataStepTv.setInteger(0,Integer.valueOf(stp));
        }else{
            watchH8DataStepTv.setInteger(0,0);
        }
        watchH8DataStepTv.setDuration(1000);
        if(!watchH8DataStepTv.isRunning()){
            watchH8DataStepTv.start();
        }
        watchH8DataDisTv.setText(SharedPreferencesUtils.getParam(getActivity(), "watchstepsdistants", "") + getResources().getString(R.string.km));
        watchH8DataKcalTv.setText(StringUtils.substringBefore(kal,".") + getResources().getString(R.string.km_cal));
    }


    /**
     * 获取数据
     *
     * @param daydata
     */
    private List<WatchDataDatyBean> getWatchDataList(String daydata) {
        if (daydata != null) {
            watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
            }.getType());
            //排序，降序
//            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
//                @Override
//                public int compare(WatchDataDatyBean watchDataDatyBean, WatchDataDatyBean t1) {
//                    return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
//                }
//            });
        } else {
            return null;
        }
        return watchDataList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.watch_h8_syncdataTv, R.id.week_tv1, R.id.month_tv2, R.id.year_tv3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_h8_syncdataTv:  //同步数据
                watchDataList.clear();
                mPointValues.clear();
                mAxisXValues.clear();
                setStepsTvShow();
                getSportWatchData(weekTag);
                break;
            case R.id.week_tv1: //周点击
                weekTag = "week";
                clearViewBack();
                watchDataList.clear();
                mPointValues.clear();
                mAxisXValues.clear();
                weekTv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                weekView1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getSportWatchData("week");
                break;
            case R.id.month_tv2:    //月点击
                weekTag = "month";
                clearViewBack();
                watchDataList.clear();
                mPointValues.clear();
                mAxisXValues.clear();
                monthTv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                monthView2.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getSportWatchData("month");
                break;
            case R.id.year_tv3:     //年点击
                weekTag = "year";
                clearViewBack();
                watchDataList.clear();
                mPointValues.clear();
                mAxisXValues.clear();
                yearTv3.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                yearView3.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getSportWatchData("year");     //显示年的折线图
                break;
        }
    }

    private void clearViewBack() {
        weekView1.setBackground(null);
        monthView2.setBackground(null);
        yearView3.setBackground(null);
        weekTv1.setTextColor(Color.parseColor("#828282"));
        monthTv2.setTextColor(Color.parseColor("#828282"));
        yearTv3.setTextColor(Color.parseColor("#828282"));

    }

    //获取数据
    private void getSportWatchData(String datatag) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));

            if (datatag.equals("week")) { //周
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 6)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            } else if (datatag.equals("month")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 29)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            } else if (datatag.equals("year")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 365)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("新数据", "-----maps----" + jsonParams.toString() + "--" + url);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonParams.toString());

    }

    //显示周的折线图
    private void showWeekData() {
        Line line = new Line(mPointValues).setColor(getResources().getColor(R.color.new_colorAccent));
        List<Line> linesList = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);   //设置折线图的点的形状 CIRCLE-圆形；SQUARE-方形;DIAMOND-菱形
        line.setCubic(false);    //曲线是否平滑
        line.setFilled(false);  //是否填充曲线的面积
        line.setHasLabels(false);    //曲线是否添加点的标注
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setStrokeWidth(1); //线的宽度
        linesList.add(line);
        LineChartData data = new LineChartData();
        data.setLines(linesList);

//        //坐标轴
//        Axis axisX = new Axis(); //X轴
//        axisX.setLineColor(getResources().getColor(R.color.black_c));
//        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
//        axisX.setTextColor(Color.BLACK);  //设置字体颜色
//        //axisX.setName("date");  //表格名称
//        axisX.setTextSize(17);//设置字体大小
//        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
//        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
//        axisX.setHasSeparationLine(false);
//        data.setAxisXBottom(axisX); //x 轴在底部
//        //data.setAxisXTop(axisX);  //x 轴在顶部
//        axisX.setHasLines(false); //x 轴分割线
//        axisX.setInside(false);  //x轴显示在里或者内
//        axisX.setTypeface(Typeface.DEFAULT);


        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
//        Axis axisY = new Axis();  //Y轴
//        axisY.setName("");//y轴标注
//        axisY.setTextSize(10);//设置字体大小
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        watchNewdataweekChar.setInteractive(true);
        watchNewdataweekChar.setZoomType(ZoomType.HORIZONTAL);
        watchNewdataweekChar.setMaxZoom((float) 2);//最大方法比例
        watchNewdataweekChar.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        watchNewdataweekChar.setLineChartData(data);
        watchNewdataweekChar.startDataAnimation(2000);
        watchNewdataweekChar.setVisibility(View.VISIBLE);

        //点击事件
        watchNewdataweekChar.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
                Log.e(TAG,"-----i1----"+i1+"---"+pointValue.getY());
                String stes = StringUtils.substringBefore(String.valueOf(pointValue.getY()).trim(),".");
                watchH8DataStepTv.setInteger(1,Integer.valueOf(stes));
                watchH8DataStepTv.setDuration(1000);
                if(!watchH8DataStepTv.isRunning()){
                    watchH8DataStepTv.start();
                }
                watchH8DataDisTv.setText(watchDataList.get(i1).getDistance() + getResources().getString(R.string.km));
                watchH8DataKcalTv.setText(watchDataList.get(i1).getCalories() + getResources().getString(R.string.km_cal));
            }

            @Override
            public void onValueDeselected() {

            }
        });
        watchNewdataweekChar.setValueSelectionEnabled(true);


    }

}
