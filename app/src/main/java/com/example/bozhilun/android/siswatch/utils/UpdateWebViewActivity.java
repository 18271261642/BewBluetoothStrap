package com.example.bozhilun.android.siswatch.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/28.
 */

/**
 * 更新的页面
 */
public class UpdateWebViewActivity extends WatchBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wb = new WebView(this);
        wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wb.getSettings().setJavaScriptEnabled(true);
       // String url = "http://app.qq.com/#id=detail&appid=1105653402";
        String url = getIntent().getStringExtra("updateUrl");
        if(!WatchUtils.isEmpty(url)){
            wb.loadUrl(url);
        }else{
            wb.loadUrl("http://app.qq.com/#id=detail&appid=1105653402");   //应用宝更新地址
        }
        setContentView(wb);
    }

}
