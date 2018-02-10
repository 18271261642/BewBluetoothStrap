package com.example.bozhilun.android.util;

/**
 * Created by wang on 2016/8/18.
 * 设置验证码计时
 */
import android.os.CountDownTimer;
import android.widget.Button;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;


public class TimerCount extends CountDownTimer {
    private Button bnt;

    public TimerCount(long millisInFuture, long countDownInterval, Button bnt) {
        super(millisInFuture, countDownInterval);
        this.bnt = bnt;
    }

    public TimerCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
       // bnt.setClickable(true);
        bnt.setTextSize(10);
        bnt.setText(R.string.send_code);
        bnt.setPadding(0,0,0,0);
        bnt.setClickable(true);
        bnt.setBackgroundDrawable(MyApp.getApplication().getResources().getDrawable(R.drawable.blue_border_btn_selector));
    }

    @Override
    public void onTick(long arg0) {
        // TODO Auto-generated method stub
        bnt.setClickable(false);
        bnt.setText(arg0 / 1000 + "S");
        bnt.setTextSize(10);
        bnt.setPadding(0,0,0,0);
    }
}
