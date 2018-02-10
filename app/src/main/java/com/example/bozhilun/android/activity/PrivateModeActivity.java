package com.example.bozhilun.android.activity;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/10.
 * 私人模式
 */

public class PrivateModeActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.minimumsystolic_bloodpressure_tv)
    TextView minimumsystolicBloodpressureTv;
    @BindView(R.id.maximumdiastolic_bloodpressure_tv)
    TextView maximumdiastolicBloodpressureTv;
    @BindView(R.id.bloodpressure_msg_tv)
    TextView bloodpressureMsgTv;

    private ArrayList<String> minimumsystolicBloodpressureList;
    private ArrayList<String> maximumdiastolicBloodpressureList;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.private_mode);
        minimumsystolicBloodpressureList = new ArrayList<>();
        maximumdiastolicBloodpressureList = new ArrayList<>();
        for (int i = 81; i <= 210; i++) {
            maximumdiastolicBloodpressureList.add("" + i);
        }
        for (int i = 40; i <= 120; i++) {
            minimumsystolicBloodpressureList.add("" + i);
        }
        String minimumsystolicBloodpressure = (String) SharedPreferencesUtils.getParam(PrivateModeActivity.this, SharedPreferencesUtils.MINI_MUMSYSTOLIC_BLOODPRESSURE, "");
        String maximumdiastolicBloodpressure = (String) SharedPreferencesUtils.getParam(PrivateModeActivity.this, SharedPreferencesUtils.MAX_IMUMDIASTOLIC_BLOODPRESSURE, "");
        if (!TextUtils.isEmpty(minimumsystolicBloodpressure)) {
            minimumsystolicBloodpressureTv.setText(minimumsystolicBloodpressure);
        }
        if (!TextUtils.isEmpty(maximumdiastolicBloodpressure)) {
            maximumdiastolicBloodpressureTv.setText(maximumdiastolicBloodpressure);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_private_mode;
    }

    @OnClick({R.id.daily_numberofstepsdefault_relayout, R.id.daily_sleepdurationdefault_relayout, R.id.save_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.daily_numberofstepsdefault_relayout:
                ProfessionPick minimumsystolicBloodpressurePopWin = new ProfessionPick.Builder(PrivateModeActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置最低收缩压
                        minimumsystolicBloodpressureTv.setText(profession);
                        SharedPreferencesUtils.setParam(PrivateModeActivity.this, SharedPreferencesUtils.MINI_MUMSYSTOLIC_BLOODPRESSURE, profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(minimumsystolicBloodpressureList) //min year in loop
                        .dateChose("10000 "+getResources().getString(R.string.steps)) // date chose when init popwindow
                        .build();
                minimumsystolicBloodpressurePopWin.showPopWin(PrivateModeActivity.this);
                break;
            case R.id.daily_sleepdurationdefault_relayout:
                ProfessionPick maximumdiastolicBloodpressurePopWin = new ProfessionPick.Builder(PrivateModeActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置最低收缩压
                        maximumdiastolicBloodpressureTv.setText(profession);
                        SharedPreferencesUtils.setParam(PrivateModeActivity.this, SharedPreferencesUtils.MAX_IMUMDIASTOLIC_BLOODPRESSURE, profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size shujuku javescript
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(maximumdiastolicBloodpressureList) //min year in loop
                        .dateChose(getResources().getString(R.string.daily_numberofsteps_default)) // date chose when init popwindow
                        .build();
                maximumdiastolicBloodpressurePopWin.showPopWin(PrivateModeActivity.this);
                break;
            case R.id.save_btn:
                break;
        }
    }
}
