package com.example.bozhilun.android.fragment;

import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseFragment;
import butterknife.BindView;


/**
 * Created by thinkpad on 2017/3/8.
 */

public class SleepFragment extends BaseFragment {

    @BindView(R.id.calories_val_tv)
    TextView caloriesValTv;
    @BindView(R.id.mileage_val_tv)
    TextView mileageValTv;
    @BindView(R.id.time_val_tv)
    TextView timeValTv;

    @Override
    protected void initViews() {
       /* ArrayList<ClockPieHelper> clockPieHelperArrayList = new ArrayList<ClockPieHelper>();
//        int startHour, int startMin, int endHour, int endMin
        clockPieHelperArrayList.add(new ClockPieHelper(0,10,5,30));
        clockPieHelperArrayList.add(new ClockPieHelper(7,20,8,30));
        pieView.setDate(clockPieHelperArrayList);*/
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_sleep;
    }


}
