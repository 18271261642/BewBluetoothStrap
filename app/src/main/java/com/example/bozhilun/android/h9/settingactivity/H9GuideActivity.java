package com.example.bozhilun.android.h9.settingactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 校针说明
 * @author： 安
 * @crateTime: 2017/10/20 11:47
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9GuideActivity extends WatchBaseActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_guide_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.guide));
    }

    @OnClick(R.id.image_back)
    public void onViewClicked() {
        finish();
    }
}
