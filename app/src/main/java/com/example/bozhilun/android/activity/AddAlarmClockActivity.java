package com.example.bozhilun.android.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/15.
 */

public class AddAlarmClockActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.alarmname_tv)
    EditText alarmnameTv;
    @BindView(R.id.alarmtime_tv_shu)
    TextView alarmtimeTv;
    @BindView(R.id.repeat_tv)
    TextView repeatTv;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.add_alarm);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_alarmclock;
    }

    @OnClick({R.id.alarmtime_relayout, R.id.repeat_relayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alarmtime_relayout:
                break;
            case R.id.repeat_relayout:
                break;
        }
    }
}
