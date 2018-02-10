package com.example.bozhilun.android.B18I.b18idata;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.B18iHeartDatasDao;
import com.afa.tourism.greendao.gen.B18iSleepDatasDao;
import com.afa.tourism.greendao.gen.B18iStepDatasDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.example.bozhilun.android.B18I.b18ibean.B18iHeartDatas;
import com.example.bozhilun.android.B18I.b18ibean.B18iSleepDatas;
import com.example.bozhilun.android.B18I.b18ibean.B18iStepDatas;
import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.model.HeartRateData;
import cn.appscomm.bluetooth.model.SleepData;
import cn.appscomm.bluetooth.model.SportCacheData;
import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by Administrator on 2017/8/28.
 */

/**
 * b18i 的数据页面
 */
public class B18iDataFragment extends Fragment {
    private static final String TAG = "--B18iDataFragment";
    View b18iDataView;
    @BindView(R.id.leaf_square_chart)
    ColumnChartView columnChartView;
    @BindView(R.id.b18i_table)
    TabLayout tableLayout;
    @BindView(R.id.b18i_viewpagers)
    ViewPager viewPager;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private MyViewPagerAdapter adapter;


    /**************************************************************************************/
    @BindView(R.id.distanceData)
    TextView distanceData;
    @BindView(R.id.heartData)
    TextView heartData;
    @BindView(R.id.kcalData)
    TextView kcalData;
    @BindView(R.id.deepSleepData)
    TextView deepSleepData;
    @BindView(R.id.lightSleepData)
    TextView lightSleepData;
    @BindView(R.id.shallowSleepData)
    TextView shallowSleepData;
    //Y数值
    private List<Integer> mValues = new ArrayList<>();
    //X
    List<String> xStringList = new ArrayList<>();
    List<View> views = new ArrayList<>();
    int[] score;//= {50, 42, 90, 33, 10, 50, 42, 90, 33, 10, 50, 42, 90, 33, 10, 50, 42, 90, 33, 10, 50, 42, 90, 33, 10, 50, 42, 90, 33, 10, 22};//图表的数据点
    String da[];
    int position;

    /**************************************************************************************/


//    private List<String> monthDayList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b18iDataView = inflater.inflate(R.layout.fragment_b18i_data, container, false);
        unbinder = ButterKnife.bind(this, b18iDataView);

        List<String> times2 = B18iUtils.getTimes2();
        int integer1 = Integer.valueOf(times2.get(1));
        int integer = Integer.valueOf(times2.get(2));
        da = new String[integer];
        score = new int[integer];
        for (int i = 0; i < integer; i++) {
            if (integer1 >= 10) {
                da[i] = integer1 + "/" + (i + 1);
            } else {
                da[i] = "0" + integer1 + "/" + (i + 1);
            }
            score[i] = 50;
        }
        position = da.length - 1;
        getDatas();
        setContent();
        return b18iDataView;
    }

    private List<B18iStepDatas> stepDatasList;
    private List<B18iSleepDatas> sleepDatasList;
    private List<B18iHeartDatas> heartDatasList;

    private void getDatas() {

        DaoSession daoSession = MyApp.getDBManager().getDaoSession();

        B18iStepDatasDao stepDatasDao = daoSession.getB18iStepDatasDao();
        QueryBuilder<B18iStepDatas> b18iStepDatasQueryBuilder = stepDatasDao.queryBuilder();
        stepDatasList = b18iStepDatasQueryBuilder.list();

        B18iSleepDatasDao sleepDatasDao = daoSession.getB18iSleepDatasDao();
        QueryBuilder<B18iSleepDatas> b18iSleepDatasQueryBuilder = sleepDatasDao.queryBuilder();
        sleepDatasList = b18iSleepDatasQueryBuilder.list();

        B18iHeartDatasDao heartDatasDao = daoSession.getB18iHeartDatasDao();
        QueryBuilder<B18iHeartDatas> b18iHeartDatasQueryBuilder = heartDatasDao.queryBuilder();
        heartDatasList = b18iHeartDatasQueryBuilder.list();

        if (stepDatasList.size() <= 0) {
            //获取运动数据
            BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
        } else {
            sportData(null, false);
        }
        if (sleepDatasList.size() <= 0) {
            //获取睡眠数据
            BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());
        } else {
            sleepData(null, false);
        }
        if (heartDatasList.size() <= 0) {
            //获取心率数据
            BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
        } else {
            heartRate(null, false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTitle.setText(WatchUtils.getCurrentDate());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        if ("sportData".equals(event.getName())) {
            //获取运动数据成功
            sportData(event, true);
        } else if ("sleepData".equals(event.getName())) {
            //获取到睡眠数据
            sleepData(event, true);
        } else if ("heartRate".equals(event.getName())) {
            //获取心率数据成功
            heartRate(event, true);
        }
    }

    /**
     * 获取心率数据成功
     * 先取数据库中的数据在同步手环的
     *
     * @param event
     */
    int avg;

    private void heartRate(B18iEventBus event, boolean isWhat) {
        if (isWhat) {
            List<HeartRateData> heartRateDatas = (List<HeartRateData>) event.getObject();
            if (heartRateDatas != null) {
                for (HeartRateData heart : heartRateDatas) {
                    String strTimes = B18iUtils.getStrTimes(String.valueOf(heart.timestamp));//时间戳转换
                    String s1 = B18iUtils.interceptString(strTimes, 5, 10);
                    if (s1.equals(da[position])) {
                        avg = heart.avg;
                    }
                }
            }
        } else {
            for (int i = 0; i < heartDatasList.size(); i++) {
                Log.e("----heart----db中已存的数据：", heartDatasList.get(i).getIds()
                        + "==" + heartDatasList.get(i).date + "==" + heartDatasList.get(i).avg + "==" + heartDatasList.get(i).timestamp);
                String strTimes = B18iUtils.getStrTimes(String.valueOf(heartDatasList.get(i).timestamp));//时间戳转换
                String s1 = B18iUtils.interceptString(strTimes, 5, 10);
                if (s1.equals(da[position])) {
                    avg = heartDatasList.get(i).avg;
                }
            }
        }
        heartData.setText(String.valueOf(avg));
    }

    /**
     * 获取到睡眠数据
     *
     * @param event
     */
    int AWAKE = 0;
    int DEEP = 0;
    int SHALLOW = 0;

    private void sleepData(B18iEventBus event, boolean isWhat) {
        AWAKE = 0;
        DEEP = 0;
        SHALLOW = 0;
        int total = 0;
        if (isWhat) {
            List<SleepData> sleepDatas = (List<SleepData>) event.getObject();
            if (sleepDatas != null) {
                for (int i = 0; i < sleepDatas.size(); i++) {
                    String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).timeStamp));//时间戳转换
                    String s1 = B18iUtils.interceptString(strTimes, 5, 10);
                    if (s1.equals(da[position])) {
                        total += sleepDatas.get(i).total;
                        DEEP += sleepDatas.get(i).deep;//深睡眠
                        SHALLOW += sleepDatas.get(i).light;//浅睡
                    }
                }
                AWAKE = total - (DEEP + SHALLOW);//清醒
                deepSleepData.setText(String.valueOf(DEEP));
                lightSleepData.setText(String.valueOf(SHALLOW));
                shallowSleepData.setText(String.valueOf(AWAKE));
            }
        } else {
            for (int i = 0; i < sleepDatasList.size(); i++) {
                Log.e("----sleep----db中已存的数据：", sleepDatasList.get(i).id + "=="
                        + sleepDatasList.get(i).total + "==" + sleepDatasList.get(i).awake
                        + "==" + sleepDatasList.get(i).light + "==" + sleepDatasList.get(i).deep
                        + "==" + sleepDatasList.get(i).awaketime + "==" + sleepDatasList.get(i).detail
                        + "==" + sleepDatasList.get(i).date + "==" + sleepDatasList.get(i).flag
                        + "==" + sleepDatasList.get(i).type + "==" + sleepDatasList.get(i).timeStamp);
                String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatasList.get(i).timeStamp));//时间戳转换
                String s1 = B18iUtils.interceptString(strTimes, 5, 10);
                if (s1.equals(da[position])) {
                    total += sleepDatasList.get(i).total;
                    DEEP += sleepDatasList.get(i).deep;//深睡眠
                    SHALLOW += sleepDatasList.get(i).light;//浅睡
                }
            }
        }
    }

    /**
     * 获取运动数据成功
     *
     * @param event
     */
    int distance, calories;

    private void sportData(B18iEventBus event, boolean isWhat) {
        distance = 0;
        calories = 0;
        if (isWhat) {
            List<SportCacheData> sportCacheDatas = (List<SportCacheData>) event.getObject();
            if (sportCacheDatas != null) {
                for (int i = 0; i < sportCacheDatas.size(); i++) {
                    String strTimes = B18iUtils.getStrTimes(String.valueOf(sportCacheDatas.get(i).timestamp));//时间戳转换
                    String s = B18iUtils.interceptString(strTimes, 5, 10);
                    if (s.equals(da[position])) {
                        distance += sportCacheDatas.get(i).distance;
                        calories += sportCacheDatas.get(i).calories;
                    }
                }
                distanceData.setText(String.valueOf(
                        WatchUtils.div(Double.valueOf(distance), Double.valueOf(1000), 2)));
                kcalData.setText(String.valueOf(calories / 1000));
            }
        } else {
            for (int i = 0; i < stepDatasList.size(); i++) {
                Log.e("----sport获取db中已存的数据：", stepDatasList.get(i).getIds()
                        + "==" + stepDatasList.get(i).id + "=步数=" + stepDatasList.get(i).step
                        + "=距离=" + stepDatasList.get(i).distance + "==" + stepDatasList.get(i).sporttime
                        + "=卡路里=" + stepDatasList.get(i).calories + "==" + stepDatasList.get(i).time + "==" + stepDatasList.get(i).timestamp);
                String strTimes = B18iUtils.getStrTimes(String.valueOf(stepDatasList.get(i).timestamp));//时间戳转换
                String s = B18iUtils.interceptString(strTimes, 5, 10);
                if (s.equals(da[position])) {
                    distance += stepDatasList.get(i).distance;
                    calories += stepDatasList.get(i).calories;
                }
            }
            distanceData.setText(String.valueOf(
                    WatchUtils.div(Double.valueOf(distance), Double.valueOf(1000), 2)));
            kcalData.setText(String.valueOf(calories / 1000));
        }
    }


    private void setContent() {
        for (int i = 0; i < da.length; i++) {
            xStringList.add(da[i]);
            mValues.add(score[i]);
            views.add(new View(getContext()));
        }

        // 使用的 20列，每列1个subcolumn。
//        int numSubcolumns = 1;
//        final int numColumns = 20;
        int numSubcolumns = 1;
        final int numColumns = score.length;
        //定义一个圆柱对象集合
        final List<Column> columns = new ArrayList<>();
        //子列数据集合
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                float f = mValues.get(i);
                if (i == position) {
                    values.add(new SubcolumnValue(f, Color.parseColor("#FFFFFFFF")));
                } else {
                    values.add(new SubcolumnValue(f, Color.parseColor("#70FFFFFF")));
                }
            }

            //创建Column对象
            Column column = new Column(values);
            //这一步是能让圆柱标注数据显示带小数的重要一步 让我找了好久问题
            //作者回答https://github.com/lecho/hellocharts-android/issues/185
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter();
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(false);
            column.hasLabels();
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
            //给x轴坐标设置描述
//            axisValues.add(new AxisValue(i).setLabel(xStringList.get(i)));
        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        ColumnChartData data = new ColumnChartData(columns);
        //定义x轴y轴相应参数
        Axis axisX = new Axis().setHasLines(false);
        Axis axisY = new Axis().setHasLines(true);
        //axisY.setName("出场率(%)");//轴名称
        axisY.hasLines();//是否显示网格线
        axisY.setLineColor(Color.parseColor("#00BBBBBB"));
        //Y轴颜色
        axisY.setTextColor(getResources().getColor(R.color.antiquewhite));//颜色
        axisX.setTextSize(12);

        axisX.setHasTiltedLabels(false);
        axisX.hasLines();
        //x轴颜色
        axisX.setTextColor(getResources().getColor(R.color.album_item_bg));
        axisX.setValues(axisValues);
        //把X轴Y轴数据设置到ColumnChartData 对象中
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        //data.setValueLabelBackgroundColor(R.color.dim_foreground_light_disabled);
        data.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
        data.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式
        data.setValueLabelTextSize(8);
//        data.setValueLabelsTextColor(getResources().getColor(R.color.chang_white));
        data.setFillRatio(0.3f);    //设置柱子的宽度
        //给表填充数据，显示出来
        columnChartView.setColumnChartData(data);
        columnChartView.startDataAnimation();

        //item的点击事件
        columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {

            }

            @Override
            public void onValueDeselected() {

            }
        });


        adapter = new MyViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tableLayout.setupWithViewPager(viewPager);
        tableLayout.setTabGravity(TabLayout.MODE_FIXED);
        tableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tableLayout.postInvalidate();

        /******************************************/
        for (int i = 0; i < da.length; i++) {
            tableLayout.getTabAt(i).setText(da[i]);
        }
        tableLayout.setSelectedTabIndicatorHeight(0);
        tableLayout.setTabTextColors(Color.GRAY, Color.parseColor("#1bb3fd"));
        tableLayout.getTabAt(position).select();
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //获取运动数据
                BluetoothSDK.getSportData(B18iResultCallBack.getB18iResultCallBack());
                //获取睡眠数据
                BluetoothSDK.getSleepData(B18iResultCallBack.getB18iResultCallBack());
                //获取心率数据
                BluetoothSDK.getHeartRateData(B18iResultCallBack.getB18iResultCallBack());
                for (int i = 0; i < numColumns; i++) {
                    if (tab.getPosition() == i) {
                        List<SubcolumnValue> values1 = columns.get(i).getValues();
                        values1.get(0).setColor(Color.parseColor("#FFFFFFFF"));
                    } else {
                        List<SubcolumnValue> values1 = columns.get(i).getValues();
                        values1.get(0).setColor(Color.parseColor("#70FFFFFF"));
                    }
                }
                columnChartView.postInvalidate();
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private List<View> list;

        public MyViewPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }
}
