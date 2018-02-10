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
 */

public class TargetSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.daily_numberofstepsdefault_tv)
    TextView dailyNumberofstepsdefaultTv;
    @BindView(R.id.daily_sleepdurationdefault_tv)
    TextView dailySleepdurationdefaultTv;
    @BindView(R.id.daily_consunergy_defaultcalorie_tv)
    TextView dailyConsunergyDefaultcalorieTv;

    private ArrayList<String> daily_numberofstepsList;
    private ArrayList<String> daily_sleepdurationList;
    private ArrayList<String> daily_consunergy_calorieList;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.Targetsetting);
        daily_numberofstepsList = new ArrayList<>();
        daily_sleepdurationList = new ArrayList<>();
        daily_consunergy_calorieList = new ArrayList<>();
        String daily_number_ofsteps_default = (String) SharedPreferencesUtils.getParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_NUMBER_OFSTEPS_DEFAULT, "");
        String daily_sleep_duration_default = (String) SharedPreferencesUtils.getParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_SLEEP_DURATION_DEFAULT, "");
        String daily_consunergy_default = (String) SharedPreferencesUtils.getParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_CONSUNERGY_DEFAULT, "");
        if (!TextUtils.isEmpty(daily_number_ofsteps_default)) {
            dailyNumberofstepsdefaultTv.setText(daily_number_ofsteps_default);
        }else{
            dailyNumberofstepsdefaultTv.setText("1000"+getResources().getString(R.string.daily_numberofsteps_default));
        }
        if (!TextUtils.isEmpty(daily_sleep_duration_default)) {
            dailySleepdurationdefaultTv.setText(daily_sleep_duration_default);
        }
        if (!TextUtils.isEmpty(daily_consunergy_default)) {
            dailyConsunergyDefaultcalorieTv.setText(daily_consunergy_default);
        }
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(i * 1000 + " "+getResources().getString(R.string.steps));
        }
        for (int i = 1; i < 12; i++) {
            daily_sleepdurationList.add(i + " "+getResources().getString(R.string.hour));
        }
        for (int i = 1; i < 300; i++) {
            daily_consunergy_calorieList.add(i * 100 + " "+getResources().getString(R.string.km_cal));
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
        return R.layout.activity_targetsetting;
    }

    @OnClick({R.id.daily_numberofstepsdefault_relayout, R.id.daily_sleepdurationdefault_relayout, R.id.daily_consunergy_defaultcalorie_relayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.daily_numberofstepsdefault_relayout:
                ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(TargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置步数
                        dailyNumberofstepsdefaultTv.setText(profession);
                        SharedPreferencesUtils.setParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_NUMBER_OFSTEPS_DEFAULT, profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_numberofstepsList) //min year in loop
                        .dateChose("10000"+getResources().getString(R.string.steps)) // date chose when init popwindow
                        .build();
                dailyumberofstepsPopWin.showPopWin(TargetSettingActivity.this);
                break;
            case R.id.daily_sleepdurationdefault_relayout:
                ProfessionPick daily_sleepdurationPopWin = new ProfessionPick.Builder(TargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置睡眠
                        dailySleepdurationdefaultTv.setText(profession);
                        SharedPreferencesUtils.setParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_SLEEP_DURATION_DEFAULT, profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_sleepdurationList) //min year in loop
                        .dateChose("8 "+getResources().getString(R.string.hour)) // date chose when init popwindow
                        .build();
                daily_sleepdurationPopWin.showPopWin(TargetSettingActivity.this);
                break;
            case R.id.daily_consunergy_defaultcalorie_relayout:
                ProfessionPick daily_consunergy_caloriePopWin = new ProfessionPick.Builder(TargetSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置睡眠
                        dailyConsunergyDefaultcalorieTv.setText(profession);
                        SharedPreferencesUtils.setParam(TargetSettingActivity.this, SharedPreferencesUtils.DAILY_CONSUNERGY_DEFAULT, profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_consunergy_calorieList) //min year in loop
                        .dateChose("170 "+getResources().getString(R.string.km_cal)) // date chose when init popwindow
                        .build();
                daily_consunergy_caloriePopWin.showPopWin(TargetSettingActivity.this);
                break;
        }
    }
}
