package com.example.bozhilun.android.base;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.ViewUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thinkpad on 2016/10/20.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    protected abstract void initViews();
    protected abstract int getContentViewId();

    private MyApp myApp;
    private BaseActivity baseActivity;

    private Dialog dialog; //进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(getContentViewId());
        setStatusBarColor();
        ButterKnife.bind(this);
       setupToolbar();
        initViews();
        if(myApp == null){
            myApp = (MyApp) getApplication();
        }
        baseActivity = this;
        addActivity();
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(" ");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getToolbarClick();
                }
            });
        }
    }

    public void setStatusBarColor() {
        if (getStatusBarColor() != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int tintColor = getStatusBarColor();
                ViewUtils.setTranslucentStatus(this, true);
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                // enable status bar tint
                tintManager.setStatusBarTintEnabled(true);
                // enable navigation bar tint
                tintManager.setNavigationBarTintEnabled(true);
                if (tintColor != 0) {
                    tintManager.setTintColor(ContextCompat.getColor(this, tintColor));
                } else {
                    tintManager.setTintColor(ContextCompat.getColor(this, R.color.new_colorAccent));
                }
            }
        }
    }

    protected void getToolbarClick() {
        finish();
    }

    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 添加Activity方法
    public void addActivity() {
        myApp.addActivity(baseActivity);// 调用myApplication的添加Activity方法
    }

    /**
     * 销毁所有activity
     * @param msg
     */
    public void removeAllActivity(){
        myApp.removeALLActivity();  //调用Application的方法销毁所有Activity
    }

    /**
     * 进度条显示
     * @param msg
     */
    public void showLoadingDialog(String msg){

        if(dialog == null){
            dialog = new Dialog(BaseActivity.this,R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            TextView tv = (TextView) dialog.getWindow(). findViewById(R.id.progress_tv);
            tv.setText(msg+"");
            dialog.setCancelable(true);
            dialog.show();
        }else {
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            dialog.setCancelable(true);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg+"");
            dialog.show();
        }

    }

    //关闭进度条
    public void closeLoadingDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }
}
