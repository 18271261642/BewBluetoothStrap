package com.example.bozhilun.android.siswatch.data;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import butterknife.Unbinder;
import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2017/7/17.
 */

public class WatchDatasFragment extends Fragment {

    View watchDataView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Unbinder unbinder;

    double kcal = 0.65;

    List<WatchDataDatyBean> watchDataList;
    @BindView(R.id.watchcolumnchart)
    ColumnChartView watchcolumnchart;

    //步数的主张图图表
    @BindView(R.id.watchkmchart)
    ColumnChartView watchkmchart;
    //卡路里折线图表
    @BindView(R.id.watchlineChatView)
    LineChartView watchlineChatView;
    //卡路里图表
    @BindView(R.id.watchkcalchart)
    ColumnChartView watchkcalchart;
    @BindView(R.id.watch_dataRefresh)
    SwipeRefreshLayout watchDataRefresh;
    private ColumnChartData data;   //步数的图表数据源
    private ColumnChartData kmData;  //里程的图表数据源
    private ColumnChartData kcalData;  //卡路里的图表数据源

    //步数数值
    private List<Integer> mValues ;//= new ArrayList<>();
    //步数图标x轴
    List<String> xStringList = new ArrayList<>();

    //里程图标
    //数值
    private List<Float> kmValues ;// = new ArrayList<>();

    //卡路里图表
    private List<Float> kcalValues = new ArrayList<>();
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

    private List<Float> kcalCharValues ;//= new ArrayList<>();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1008:
                    watchDataRefresh.setRefreshing(false);
                    getDataStepsData(); //获取数据统计
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        watchDataView = inflater.inflate(R.layout.fragment_watch_data, null);
        unbinder = ButterKnife.bind(this, watchDataView);

        initViews();

        getDataStepsData(); //获取数据统计

        return watchDataView;
    }

    /**
     * 获取日数据统计
     */
    private void getDataStepsData() {
        mValues = new ArrayList<>();
        kcalCharValues = new ArrayList<>();
        kmValues = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Log.e("", "---------时间---" + sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 7)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String macadd = (String) SharedPreferencesUtils.readObject(getActivity(), "mylanmac");
        //获取运动日数据
        //String dataStepUrl = URLs.HTTPs + URLs.getSportH;
        String dataStepUrl = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
            jsonObect.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 6)));
            jsonObect.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, dataStepUrl, jsonObect, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Wtdata", "--------2222---" + response.toString());
                if (response != null) {
                    try {
                        if (response.getInt("resultCode") == 001) {
                            String daydata = response.getString("day");
                            getWatchDataList(daydata);
                            //获取值
                            for (WatchDataDatyBean stepNumber : watchDataList) {
                                mValues.add(stepNumber.getStepNumber());    //步数的数值显示
                                kmValues.add(Float.valueOf(stepNumber.getDistance()));     //里程的数值显示
                                kcalValues.add(Float.valueOf(stepNumber.getCalories()));    //卡路里图表数组显示
                                kcalCharValues.add(Float.valueOf(stepNumber.getCalories()));
                            }
                            //获取时间，x轴
                            for (WatchDataDatyBean watchDataDatyBean : watchDataList) {
                                String rct = watchDataDatyBean.getRtc().substring(5, watchDataDatyBean.getRtc().length());
                                xStringList.add(rct);

                            }
                            //卡路里折线图X,y轴坐标值
                            for (int i = 0; i < watchDataList.size(); i++) {
                                String rct = watchDataList.get(i).getRtc().substring(5, watchDataList.get(i).getRtc().length());
                                mAxisValues.add(new AxisValue(i).setLabel(rct));    //X轴值
                                //y轴值
                                mPointValues.add(new PointValue(i, Float.valueOf(watchDataList.get(i).getCalories())));
                            }


                            //步数图表显示
                            showStepsChat();
                            //卡路里折线图表显示
                            // showKcalChat();
                            showKcalChatrView();
                            //里程图表显示
                            showKmChat();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    Log.e("", "-----------333-----" + error.getMessage());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 1, 1.0f));
        MyApp.getRequestQueue().add(jsonRequest);


    }

    private void showKcalChatrView() {
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = 7;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                float f = kcalCharValues.get(i);
                //values.add(new SubcolumnValue(f, getResources().getColor(R.color.chang_white)).setTarget(f));
               // float f = mValues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(true);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(xStringList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        kcalData = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisX.setTextSize(12);

        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        kcalData.setAxisXBottom(axisX);
        kcalData.setAxisYLeft(axisY);
        //  data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        kcalData.setValueLabelBackgroundColor(R.color.mpc_end_color);
        kcalData.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        kcalData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        kcalData.setValueLabelTextSize(8);
        //    data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        kcalData.setFillRatio(0.5f);    //设置柱子的宽度
        //给表填充数据，显示出来
        watchkcalchart.setColumnChartData(kcalData);
        watchkcalchart.startDataAnimation(2000);

    }

    //卡路里图表显示
    private void showKcalChat() {
        Line line = new Line(mPointValues).setColor(Color.WHITE);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注 是否显示节点数据
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false); //x轴字体显示true为显示斜体，false正常
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setName("");  //表格名称
        axisX.setTextSize(12);//设置字体大小
        axisX.setMaxLabelChars(9);  //最多几个X轴坐标
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//      data.setAxisXTop(axisX);  //x 轴在顶部

        Axis axisY = new Axis();  //Y轴
        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setName("");//y轴标注
        axisY.setTextSize(12);//设置字体大小
        axisY.setHasLines(true);    //y轴刻度线
        data.setAxisYLeft(axisY);  //Y轴设置在左边
//      data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        watchlineChatView.setInteractive(false);
        watchlineChatView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        watchlineChatView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        watchlineChatView.setLineChartData(data);
        watchlineChatView.setVisibility(View.VISIBLE);

    }

    //里程图表显示
    private void showKmChat() {
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = 7;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                float f = kmValues.get(i);
//                values.add(new SubcolumnValue(f, getResources().getColor(R.color.chang_white)).setTarget(f));
//                float f = mValues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(true);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(xStringList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        kmData = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisX.setTextSize(12);

        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        kmData.setAxisXBottom(axisX);
        kmData.setAxisYLeft(axisY);
        //  data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        kmData.setValueLabelBackgroundColor(R.color.mpc_end_color);
        kmData.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        kmData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        kmData.setValueLabelTextSize(8);
        //    data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        kmData.setFillRatio(0.5f);    //设置柱子的宽度
        //给表填充数据，显示出来
        watchkmchart.setColumnChartData(kmData);
        watchkmchart.startDataAnimation(2000);

    }

    //步数图表显示
    private void showStepsChat() {
        // 使用的 8列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = 7;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; i++) {

            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; j++) {
                //为每一柱图添加颜色和数值
                float f = mValues.get(i);
                SubcolumnValue sb = new SubcolumnValue();
                sb.setTarget(f);
                values.add(sb);

            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(true);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(xStringList.get(i)));

        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        data = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisX.setTextSize(12);

        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        //  data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        data.setValueLabelBackgroundColor(R.color.mpc_end_color);
        data.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        data.setValueLabelTextSize(8);
        //    data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        data.setFillRatio(0.5f);    //设置柱子的宽度

        //给表填充数据，显示出来
        watchcolumnchart.setColumnChartData(data);
        watchcolumnchart.startDataAnimation(2000);
        watchcolumnchart.setZoomEnabled(true);  //支持缩放
        watchcolumnchart.setInteractive(true);  //支持与用户交互


        //item的点击事件
        watchcolumnchart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {


            }

            @Override
            public void onValueDeselected() {

            }
        });

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
            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                @Override
                public int compare(WatchDataDatyBean watchDataDatyBean, WatchDataDatyBean t1) {
                    return t1.getRtc().compareTo(watchDataDatyBean.getRtc());
                }
            });
        } else {
            return null;
        }
        return watchDataList;
    }

    private void initViews() {
        tvTitle.setText(WatchUtils.getCurrentDate());
        //禁止缩放
        watchcolumnchart.setZoomEnabled(false);

        LinearLayoutManager linm = new LinearLayoutManager(getActivity());
        linm.setOrientation(LinearLayoutManager.VERTICAL);

        watchDataRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1008;
                        handler.sendMessage(msg);
                    }
                },3 * 1000);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
