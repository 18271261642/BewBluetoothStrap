package com.example.bozhilun.android.siswatch;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.BaseActivity;

/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchBaseActivity extends AppCompatActivity {

    private MyApp myApp;
    private WatchBaseActivity watchBaseActivity;
    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (myApp == null) {
            myApp = (MyApp) getApplication();
        }
        watchBaseActivity = this;
        addActivity();

    }

    // 添加Activity方法
    public void addActivity() {
        myApp.addActivity(watchBaseActivity);// 调用myApplication的添加Activity方法
    }

    /**
     * 销毁所有activity
     *
     * @param
     */
    public void removeAllActivity() {
        myApp.removeALLActivity();  //调用Application的方法销毁所有Activity
    }

    /**
     * 通用的Intent跳转
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 跳转公共方法2 带参数
     *
     * @param cls
     */
    public void startActivity(Class<?> cls, String[] keys, String[] values) {
        Intent intent = new Intent(this, cls);
        int size = keys.length;
        for (int i = 0; i < size; i++) {
            intent.putExtra(keys[i], values[i]);
        }
        startActivity(intent);
    }

    /**
     * 进度条显示
     *
     * @param msg
     */
    private static int MSG_DISMISS_DIALOG = 101;

    public void showLoadingDialog(String msg) {

        if (dialog == null) {
            dialog = new Dialog(WatchBaseActivity.this, R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.setCancelable(true);
            dialog.show();
        } else {
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            dialog.setCancelable(true);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.show();
        }
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 30 * 1000);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MSG_DISMISS_DIALOG == msg.what) {
                if (null != dialog) {
                    if (dialog.isShowing()) {
                        Log.i("----", "handler get mesage");
                        dialog.dismiss();
                    }
                }
            }
        }
    };

    //关闭进度条
    public void closeLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
