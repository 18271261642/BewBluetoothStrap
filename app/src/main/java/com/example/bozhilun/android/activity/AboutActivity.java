package com.example.bozhilun.android.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.util.URLs;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/9.
 * 关于
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.version_tv)
    TextView versionTv;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.abour);
        try {
            versionTv.setText(getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkUpdates();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }

    @OnClick(R.id.app_version_relayout)
    public void onClick() {
        checkUpdates();
    }

    public void checkUpdates(){
        UpdateManager updateManager = new UpdateManager(AboutActivity.this, URLs.HTTPs + URLs.getvision);
        updateManager.checkForUpdate(true);
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }



}
