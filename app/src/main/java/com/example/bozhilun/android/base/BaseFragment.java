package com.example.bozhilun.android.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.util.ViewUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by SilenceDut on 2015/11/28.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected View rootView;

    protected abstract void initViews();

    protected abstract int getContentViewId();

    protected String mPageName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getContentViewId(), container, false);
        ButterKnife.bind(this, rootView);
        if (getNoStatusBar() == 0) {
            setupToolbar();
            setStatusBarColor();
        }
        initViews();
        return rootView;
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" ");
        }
    }

    private void setStatusBarColor() {
        if (getStatusBarColor() != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int tintColor = getStatusBarColor();
                ViewUtils.setTranslucentStatus(getActivity(), true);
                SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
                // enable status bar tint
                tintManager.setStatusBarTintEnabled(true);
                // enable navigation bar tint
                tintManager.setNavigationBarTintEnabled(true);
                if (tintColor != 0) {
                    tintManager.setTintColor(ContextCompat.getColor(getContext(), tintColor));
                } else {
                    tintManager.setTintColor(ContextCompat.getColor(getContext(), R.color.new_colorAccent));
                }
            }
        }
    }

    protected int getNoStatusBar() {
        return 0;
    }

    protected int getStatusBarColor() {
        return -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
