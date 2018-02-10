package com.example.bozhilun.android.b15p;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.MessageAcitivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设备界面
 * Created by Administrator on 2017/12/21.
 */

public class B15PDeviceActivity extends WatchBaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_device);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.mine_equipment));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.b15pDevieMsgNnotiLin, R.id.b15pDeviceAlarmLin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b15pDevieMsgNnotiLin: //消息提醒
                startActivity(B15PMsgAlertActivity.class);

                break;
            case R.id.b15pDeviceAlarmLin:   //闹钟


                break;
        }
    }
}
