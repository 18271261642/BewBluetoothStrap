package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent: 高级设置
 * @author： 安
 * @crateTime: 2017/9/6 08:50
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class AdvancedSettingsActivity extends WatchBaseActivity {

    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.advanced_sedentary)
    LinearLayout advancedSedentary;
    @BindView(R.id.advanced_sleep)
    LinearLayout advancedSleep;
    @BindView(R.id.xiantiao)
    View xiantiao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_advanced_settings_layout);
        ButterKnife.bind(this);
        whichDevice();//判断是B18i还是H9
        barTitles.setText(getResources().getString(R.string.advanced_setting));
        imageBack.setOnClickListener(new Onclick());
        advancedSedentary.setOnClickListener(new Onclick());
        advancedSleep.setOnClickListener(new Onclick());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i) {
            case "B18i":
                advancedSedentary.setVisibility(View.VISIBLE);
                xiantiao.setVisibility(View.VISIBLE);
                break;
            case "H9":
                advancedSedentary.setVisibility(View.GONE);
                xiantiao.setVisibility(View.GONE);
                break;
            case "B15P":
                advancedSedentary.setVisibility(View.VISIBLE);
                xiantiao.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class Onclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_back:
                    finish();
                    break;
                case R.id.advanced_sedentary:
                    startActivity(new Intent(AdvancedSettingsActivity.this, SedentaryReminder.class).putExtra("is18i", is18i));
                    break;
                case R.id.advanced_sleep:
                    startActivity(new Intent(AdvancedSettingsActivity.this, SleepGoalActivity.class).putExtra("is18i", is18i));
                    break;
            }
        }
    }
}
