package com.example.bozhilun.android.siswatch.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.H8ShareActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.record.RecordHistoryActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
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

/**
 * Created by sunjianhua on 2017/11/6.
 */

public class WatchH8DataFragment extends Fragment {

    private static final String TAG = "-------WatchH8DataFragment";

    View h8View;
    @BindView(R.id.watch_newh8dataweekChar)
    LineChart watchNewh8dataweekChar;
    Unbinder unbinder;
    List<WatchDataDatyBean> watchDataList;
    //身高
    @BindView(R.id.h8UserHeightTv)
    TextView h8UserHeightTv;
    //体重
    @BindView(R.id.h8UserWeighttTv)
    TextView h8UserWeighttTv;
    //年龄
    @BindView(R.id.h8UserAgetTv)
    TextView h8UserAgetTv;
    //步数显示
    @BindView(R.id.watch_newh8_dataStepTv)
    TextView watchNewh8DataStepTv;
    //里程
    @BindView(R.id.watch_newh8_dataDisTv)
    TextView watchNewh8DataDisTv;
    //卡路里
    @BindView(R.id.watch_newh8_dataKcalTv)
    TextView watchNewh8DataKcalTv;
    //周
    @BindView(R.id.week_newtv1)
    TextView weekNewtv1;
    //月
    @BindView(R.id.month_newtv2)
    TextView monthNewtv2;
    //年
    @BindView(R.id.year_newtv3)
    TextView yearNewtv3;
    @BindView(R.id.week_newview1)
    View weekNewview1;
    @BindView(R.id.month_newview2)
    View monthNewview2;
    @BindView(R.id.year_newview3)
    View yearNewview3;
    @BindView(R.id.watch_newh8_datadatelTv)
    TextView watchNewh8DatadatelTv;
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
    @BindView(R.id.h8UserSexTv)
    TextView h8UserSexTv;
    //x轴的textView布局
    @BindView(R.id.h8_x_lin)
    LinearLayout h8XLin;
    @BindView(R.id.xRb1)
    RadioButton xRb1;
    @BindView(R.id.xRb2)
    RadioButton xRb2;
    @BindView(R.id.xRb3)
    RadioButton xRb3;
    @BindView(R.id.xRb4)
    RadioButton xRb4;
    @BindView(R.id.xRb5)
    RadioButton xRb5;
    @BindView(R.id.xRb6)
    RadioButton xRb6;
    @BindView(R.id.xRb7)
    RadioButton xRb7;
    @BindView(R.id.h8_teset_radio_lin)
    LinearLayout h8TesetRadioLin;
    @BindView(R.id.h8xRg)
    RadioGroup h8xRg;
    @BindView(R.id.newH8XView)
    View newH8XView;

    //标题
    @BindView(R.id.h8_data_titleTv)
    TextView h8DataTitleTv;

    private String weekTag = "week";
    Map<String, Integer> sumMap;
    private List<String> dtLis;
    private List<Integer> yearValuesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        h8View = inflater.inflate(R.layout.fragment_new_h8_data, container, false);
        unbinder = ButterKnife.bind(this, h8View);

        initViews();
        //showTodaySportData();   //显示当天的数据
        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
        return h8View;
    }

    //显示当天的数据
    private void showTodaySportData() {
        //显示今天的步数
        String todayStep = ((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")).trim();
        String kal = ((String) SharedPreferencesUtils.getParam(getActivity(), "watchkcal", "")).trim();
        String discan = (String) SharedPreferencesUtils.getParam(getActivity(), "watchstepsdistants", "");

        if (!WatchUtils.isEmpty(discan)) {
            watchNewh8DataDisTv.setText(discan + getResources().getString(R.string.km));
        }
        if (!WatchUtils.isEmpty(kal)) {
            watchNewh8DataKcalTv.setText(StringUtils.substringBefore(kal, ".") + getResources().getString(R.string.km_cal));
        }


        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));

    }

    private void initViews() {
        yearValuesList = new ArrayList<>();
        watchDataList = new ArrayList<>();
        h8DataTitleTv.setText(getResources().getString(R.string.data));

        getLindataFromServer("week");
        h8XLin.setVisibility(View.GONE);
        newH8XView.setVisibility(View.VISIBLE);
        h8TesetRadioLin.setVisibility(View.VISIBLE);
        getUserInfoData();

    }

    //获取用户信息
    private void getUserInfoData() {
        h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
        h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");
        h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");
        h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");


        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener userSub = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + myInfoJsonObject.getString("height") + "");
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + myInfoJsonObject.getString("weight"));
                                String birtday = myInfoJsonObject.getString("birthday"); //生日
                                if (!WatchUtils.isEmpty(birtday)) {
                                    h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + WatchUtils.getAgeFromBirthTime(birtday));
                                }
                                String sex = myInfoJsonObject.getString("sex");
                                if (sex.equals("M")) {
                                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nan));
                                } else {
                                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nv));
                                }

                            } else {
                                h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");

                                h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");

                                h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
                    h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");

                    h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");

                    h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");

                }
            }

        };
        CommonSubscriber commonSubscriber2 = new CommonSubscriber(userSub, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, url, jsonObj.toString());
    }


    private void setChartStyle(LineChart lineChart, LineData lineData, int count) {

        //是否在折线图上添加边框
        lineChart.setDrawBorders(false);
        //没有数据时提示
        lineChart.setNoDataText(getResources().getString(R.string.nodata));
        lineChart.setNoDataTextDescription(getResources().getString(R.string.nodata));
        lineChart.setEnabled(false);
        lineChart.setDescription("");
        //lineChart.setDescriptionTypeface(Typeface.DEFAULT);
        //隐藏左边的坐标轴
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        //隐藏右边的坐标轴
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        if (count == 7) {   //轴隐藏x轴
            //隐藏x轴的坐标
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.getXAxis().setEnabled(false);
        } else {
            //隐藏x轴的坐标
            lineChart.getXAxis().setDrawGridLines(true);
            lineChart.getXAxis().setEnabled(true);
        }

        //x轴在下方
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴的字体大小
        lineChart.getXAxis().setTextSize(10);
        //设置x轴的颜色
        lineChart.getXAxis().setAxisLineColor(R.color.linear_border);
        lineChart.getXAxis().setAxisLineWidth(2);


        //去掉网格线
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);

        //是否绘制背景色
//        lineChart.setDrawGridBackground(true);
//        lineChart.setGridBackgroundColor(Color.CYAN); //绘制背景色
        // 触摸
        lineChart.setTouchEnabled(true);

        // 拖拽
        lineChart.setDragEnabled(false);
        // 缩放
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // 设置背景
        //lineChart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        //设置x轴，y轴的数据
        lineChart.setData(lineData);

        Legend mLegend = lineChart.getLegend();

        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色

        mLegend.setEnabled(false);

        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToastUtil.showToast(getActivity(), "触摸了");
                return false;
            }
        });
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getActivity(), "点击了");
            }
        });


        lineChart.animateX(3000);
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), H8DataLinchartActivity.class));
            }
        });

        watchNewh8dataweekChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getActivity(), "view点击了");
            }
        });

    }

    @SuppressLint("LongLogTag")
    private LineData getLindata(List<WatchDataDatyBean> wt, int count, Map<String, Integer> summap) {
        Log.e(TAG,"---------count--="+count+"---sumMap=="+summap.toString());
        LineDataSet mLineDataSet;
        //x轴数据
        ArrayList<String> x;
        if (count == 12) {    //年
            x = new ArrayList<>();
            for (int i = 0; i <= 12; i++) {
                String xD = StringUtils.substringAfter(dtLis.get(i), "-");
                // x轴显示的数据
                x.add(xD);
            }
            //0,1,2,3,4,5,6,7,8,9,10,11,12
            ArrayList<Entry> ys = new ArrayList<>();
            for (int k = 0; k <= 12; k++) {
                String stp = summap.get(dtLis.get(k)) + "";
                float stepValue = Float.valueOf(stp);
                ys.add(new Entry(stepValue, k));
            }
            //ys.add(new Entry(summap.get(dtLis.get(12)), 12));
            // y轴数据集
            mLineDataSet = new LineDataSet(ys, "");


        } else {
            x = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String xD = wt.get(i).getRtc().substring(5, wt.get(0).getRtc().length());
                // x轴显示的数据
//            x.add(xD.substring(8,xD.length()));
                x.add(xD);
            }

            ArrayList<Entry> ys = new ArrayList<>();
            for (int k = 0; k < wt.size(); k++) {
                String stp = wt.get(k).getStepNumber() + "";
                float stepValue = Float.valueOf(stp);
                ys.add(new Entry(stepValue, k));
            }
            // y轴数据集
            mLineDataSet = new LineDataSet(ys, "");
        }


        // 用y轴的集合来设置参数
        // 线宽
        mLineDataSet.setLineWidth(1.5f);
        // 显示的圆形大小
        mLineDataSet.setCircleSize(3.0f);
        // 折线的颜色
        mLineDataSet.setColor(getResources().getColor(R.color.new_colorAccent));
        // 圆球的颜色
        mLineDataSet.setCircleColor(getResources().getColor(R.color.new_colorAccent));
        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
        // Highlight的十字交叉的纵横线将不会显示，
        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(false);

        // 按击后，十字交叉线的颜色
        mLineDataSet.setHighLightColor(Color.CYAN);

        // 设置这项上显示的数据点的字体大小。
        mLineDataSet.setValueTextSize(5.0f);
        mLineDataSet.setCircleColorHole(Color.WHITE);


        mLineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return StringUtils.substringBefore(String.valueOf(v), ".");
            }

        });


        ArrayList<LineDataSet> mLineDataSets = new ArrayList<>();
        mLineDataSets.add(mLineDataSet);

        LineData mLineData = new LineData(x, mLineDataSet);

        return mLineData;


    }

    private void getLindataFromServer(final String datatag) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));

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
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onNext(String result) {
                Log.e(TAG, "-----result--11--" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jso = new JSONObject(result);
                        String daydata = jso.getString("day");
                        watchDataList.clear();
                        watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());

                        Log.e(TAG, "-----watchDataList.seze----" + watchDataList.size() + weekTag);
                        if (datatag.equals("year")) {
                            sumMap = new HashMap<>();
                            int sum = 0;
                            for (int i = 0; i < watchDataList.size(); i++) {
                                int n = 1;
                                String strDate = watchDataList.get(i).getRtc().substring(0, 7);
                                if (sumMap.get(strDate) != null) {
                                    sum += watchDataList.get(i).getStepNumber();
                                }else{
                                    sum = watchDataList.get(i).getStepNumber();
                                }
                                sumMap.put(strDate, sum);

                            }
                            dtLis = new ArrayList<>();
                            dtLis.clear();
                            Log.e(TAG,"------map---"+sumMap.toString());
                            //遍历map
                            for (Map.Entry<String, Integer> entry : sumMap.entrySet()) {
                                dtLis.add(entry.getKey().trim());
                            }
                            //升序排列
                            Collections.sort(dtLis, new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return s.compareTo(t1);
                                }
                            });


                            xTv1.setText(StringUtils.substringAfter(dtLis.get(0), "-"));
                            xTv2.setText(StringUtils.substringAfter(dtLis.get(2), "-"));
                            xTv3.setText(StringUtils.substringAfter(dtLis.get(4), "-"));
                            xTv4.setText(StringUtils.substringAfter(dtLis.get(6), "-"));
                            xTv5.setText(StringUtils.substringAfter(dtLis.get(8), "-"));
                            xTv6.setText(StringUtils.substringAfter(dtLis.get(10), "-"));
                            xTv7.setText(StringUtils.substringAfter(dtLis.get(12), "-"));
                            dateClickEnable();
                            LineData mLineData = getLindata(watchDataList, 12, sumMap);
                            setChartStyle(watchNewh8dataweekChar, mLineData, 12);   //设置样式


                        } else if (datatag.equals("week")) {
                            yearValuesList.clear();
                            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                                @Override
                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
                                    return o1.getRtc().compareTo(o2.getRtc());
                                }
                            });
                            xRb1.setText(watchDataList.get(0).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb2.setText(watchDataList.get(1).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb3.setText(watchDataList.get(2).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb4.setText(watchDataList.get(3).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb5.setText(watchDataList.get(4).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb6.setText(watchDataList.get(5).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb7.setText(watchDataList.get(6).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            //设置昨天默认选中
                            xRb7.setChecked(true);
                            showTopData(6);
                            h8xRg.setOnCheckedChangeListener(new weekTvClickListener());

                            Map<String, Integer> map = new HashMap<>();
                            LineData mLineData = getLindata(watchDataList, watchDataList.size(), map);
                            setChartStyle(watchNewh8dataweekChar, mLineData, 7);   //设置样式


                        } else { //月
                            dateClickEnable();
                            yearValuesList.clear();
                            xTv1.setText(watchDataList.get(1).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv2.setText(watchDataList.get(1 * 5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv3.setText(watchDataList.get(2 * 5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv4.setText(watchDataList.get(3 * 5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv5.setText(watchDataList.get(4 * 5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv6.setText(watchDataList.get(5 * 5).getRtc().substring(5, watchDataList.get(0).getRtc().length()));
                            xTv7.setText(watchDataList.get(6 * 5 - 1).getRtc().substring(5, watchDataList.get(0).getRtc().length()));

                            Map<String, Integer> map = new HashMap<>();
                            LineData mLineData = getLindata(watchDataList, watchDataList.size(), map);
                            setChartStyle(watchNewh8dataweekChar, mLineData, 30);   //设置样式
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        Log.e("新数据", "-----maps----" + jsonParams.toString() + "--" + url);
        CommonSubscriber commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonParams.toString());


    }

    private void dateClickEnable() {
        xTv1.setClickable(false);
        xTv2.setClickable(false);
        xTv3.setClickable(false);
        xTv4.setClickable(false);
        xTv5.setClickable(false);
        xTv6.setClickable(false);
        xTv7.setClickable(false);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.week_newtv1, R.id.month_newtv2, R.id.year_newtv3,
            R.id.newh8dataUserLi,
            R.id.h8_dataLinChartImg, R.id.h8_dataShareImg, R.id.h8_data_titleLinImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.week_newtv1:  //周
                h8XLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.VISIBLE);
                h8TesetRadioLin.setVisibility(View.VISIBLE);
                watchDataList.clear();
                weekTag = "week";
                clearViewBack();
                weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("week");

                break;
            case R.id.month_newtv2: //月
                h8XLin.setVisibility(View.GONE);
                h8TesetRadioLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.GONE);
                watchDataList.clear();
                weekTag = "month";
                clearViewBack();
                monthNewtv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                monthNewview2.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("month");
                break;
            case R.id.year_newtv3:  //年的点击
                h8XLin.setVisibility(View.GONE);
                h8TesetRadioLin.setVisibility(View.GONE);
                newH8XView.setVisibility(View.GONE);
                watchDataList.clear();
                weekTag = "year";
                clearViewBack();
                yearNewtv3.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                yearNewview3.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                getLindataFromServer("year");
                break;
            case R.id.newh8dataUserLi:  //至个人中心
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.h8_dataLinChartImg:   //至柱状图
                startActivity(new Intent(getActivity(), H8DataLinchartActivity.class));
                break;
            case R.id.h8_data_titleLinImg:  //至列表
                startActivity(new Intent(getActivity(), RecordHistoryActivity.class));
                break;
            case R.id.h8_dataShareImg:  //分享
                startActivity(new Intent(getActivity(),H8ShareActivity.class));
                break;

        }
    }

    private void clearViewBack() {
        weekNewview1.setBackground(null);
        monthNewview2.setBackground(null);
        yearNewview3.setBackground(null);
        weekNewview1.setBackgroundColor(getResources().getColor(R.color.black_c));
        monthNewview2.setBackgroundColor(getResources().getColor(R.color.black_c));
        yearNewview3.setBackgroundColor(getResources().getColor(R.color.black_c));
        weekNewtv1.setTextColor(Color.parseColor("#828282"));
        monthNewtv2.setTextColor(Color.parseColor("#828282"));
        yearNewtv3.setTextColor(Color.parseColor("#828282"));

    }


    private class weekTvClickListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.xRb1:
                    showTopData(0);
                    xRb1.toggle();
                    break;
                case R.id.xRb2:
                    showTopData(1);
                    break;
                case R.id.xRb3:
                    showTopData(2);
                    break;
                case R.id.xRb4:
                    showTopData(3);
                    break;
                case R.id.xRb5:
                    showTopData(4);
                    break;
                case R.id.xRb6:
                    showTopData(5);
                    break;
                case R.id.xRb7:
                    showTopData(6);
                    break;
            }
        }
    }

    private void showTopData(int position) {
        watchNewh8DataStepTv.setText("" + watchDataList.get(position).getStepNumber() + "");

        watchNewh8DataDisTv.setText(watchDataList.get(position).getDistance() + getResources().getString(R.string.km));
        watchNewh8DataKcalTv.setText(watchDataList.get(position).getCalories() + getResources().getString(R.string.km_cal));
        watchNewh8DatadatelTv.setText(watchDataList.get(position).getRtc());
    }

}
