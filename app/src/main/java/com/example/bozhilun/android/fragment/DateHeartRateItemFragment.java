package com.example.bozhilun.android.fragment;

import android.graphics.Color;
import android.support.design.widget.TabLayout;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.util.Common;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by thinkpad on 2017/3/24.
 */

public class DateHeartRateItemFragment extends BaseFragment {

    /*@BindView(R.id.bar_view)
    BarView barView;*/
    @BindView(R.id.chart_step)
    ColumnChartView chart;
    @BindView(R.id.tabs)
    TabLayout tabs;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;

    @Override
    protected void initViews() {
        List<String> mDataList = Common.getDayListOfMonth(getActivity());
        for (int i = 0; i < mDataList.size(); i++) {
            tabs.addTab(tabs.newTab().setText(mDataList.get(i)));
        }
        tabs.getTabAt(mDataList.size() - 1).select();
        tabs.setScrollPosition(mDataList.size() - 1, 1F, true);
        //randomSet(barView);
        generateDefaultData();
        prepareDataAnimation();
        chart.startDataAnimation();
    }

/*    private void randomSet(BarView barView) {
        int random = (int) (Math.random() * 20) + 6;
        ArrayList<String> test = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            test.add(i + "h");
        }
        barView.setBottomTextList(test);
        ArrayList<Integer> barDataList = new ArrayList<Integer>();
        for (int i = 0; i < random * 2; i++) {
            barDataList.add((int) (Math.random() * 100));
        }
        barView.setDataList(barDataList, 100);
    }*/

    private void generateDefaultData() {
        int numSubcolumns = 1;
        int numColumns = 24;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
//                ChartUtils.pickColor()
                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, Color.WHITE));
            }
            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        data = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setColumnChartData(data);

    }

    private void prepareDataAnimation() {
        for (Column column : data.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget((float) Math.random() * 100);
            }
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_step_item;
    }
}
