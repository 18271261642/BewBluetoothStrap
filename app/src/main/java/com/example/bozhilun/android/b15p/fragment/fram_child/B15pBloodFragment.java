package com.example.bozhilun.android.b15p.fragment.fram_child;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.B15pBaseFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import java.util.ArrayList;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/3.
 */

public class B15pBloodFragment extends B15pBaseFragment {


    @BindView(R.id.b150BloodBarChartView)
    BarChart b150BloodBarChartView;
    Unbinder unbinder;

    private Random random;

    @Override
    protected int setContentView() {
        return R.layout.fragment_b15p_blood;
    }

    @Override
    protected void lazyLoad() {
//        initBarChartView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        initBarChartView();

        return rootView;
    }

    private void initBarChartView() {
        random = new Random();
        ArrayList<String> xValueList = new ArrayList<>();
        String xfloat[] = new String[]{"0","3","6","9","12","15","18","21","24","25"};
        for(int i = 0;i<xfloat.length;i++){
            xValueList.add(xfloat[i]);
        }

        ArrayList<BarEntry> yValue = new ArrayList<>();
        for(int k = 0;k<10;k++){
            yValue.add(new BarEntry(random.nextInt(1000),k));
        }

        b150BloodBarChartView.setDrawBarShadow(false);
        b150BloodBarChartView.setScaleEnabled(false);
        XAxis xAxis = b150BloodBarChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(R.color.black);
        xAxis.setTextColor(R.color.black);
        xAxis.setAxisLineWidth(2f);
        xAxis.setValues(xValueList);


        //隐藏右边的y轴
        b150BloodBarChartView.getAxisRight().setEnabled(false);
        b150BloodBarChartView.getAxisLeft().setEnabled(false);
        b150BloodBarChartView.getLegend().setEnabled(false);
        b150BloodBarChartView.setDescription("");

        BarDataSet barDataSet1 = new BarDataSet(yValue,"y");
        barDataSet1.setColor(Color.WHITE);
        barDataSet1.setValueTextColor(R.color.white);
        barDataSet1.setBarSpacePercent(50f);
        barDataSet1.setBarShadowColor(R.color.white);

        ArrayList<IBarDataSet> ibar = new ArrayList<>();
        ibar.add(barDataSet1);

        BarData barData = new BarData(xValueList,ibar);
        barData.setValueTextColor(R.color.white);

        b150BloodBarChartView.setData(barData);

        b150BloodBarChartView.animateX(2500);
        b150BloodBarChartView.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
