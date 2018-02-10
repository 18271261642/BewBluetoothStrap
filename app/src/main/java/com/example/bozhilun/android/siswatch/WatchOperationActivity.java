package com.example.bozhilun.android.siswatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/5.
 */

/**
 * 操作说明
 */
public class WatchOperationActivity extends WatchBaseActivity {

    @BindView(R.id.watch_operationWebView)
    WebView watchOperationWebView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    WebSettings webSettings;

    String url = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_operation);
        ButterKnife.bind(this);

        initViews();
        Locale locales = getResources().getConfiguration().locale;
        Log.e("YUYAN","--------aaa--"+locales.getCountry());

        String locale = Locale.getDefault().getLanguage();
        Log.e("YUYAN","---------locale--"+locale);
        if(!WatchUtils.isEmpty(locale)){
            if("zh".equals(locale)){    //中文
                if("TW".equals(locales.getCountry())){  //中文繁体
                    url = "file:///android_asset/watch_operation_tw.html";
                }else{  //中文简体
                    url = "file:///android_asset/watch_operation_zh.html";
                }

            }else if("en".equals(locale)){  //英文
                url = "file:///android_asset/watch_operation_en.html";
            }else if("fr".equals(locale)){  //法文
                url = "file:///android_asset/watch_operation_fr.html";
            }else if("it".equals(locale)){      //意大利语
                url = "file:///android_asset/watch_operation_it.html";
            }else if("ja".equals(locale)){      //日语
                url = "file:///android_asset/watch_operation_jp.html";
            }else if("ko".equals(locale)){  //韩语
                url = "file:///android_asset/watch_operation_ko.html";
            }else if("ru".equals(locale)){    //    俄语
                url = "file:///android_asset/watch_operation_ru.html";
            }else if("es ".equals(locale)){     //西班牙语
                url = "file:///android_asset/watch_operation_es.html";
            }else if("de".equals(locale)){      //德语
                url = "file:///android_asset/watch_operation_de.html";
            }
            else{  //英文
                url = "file:///android_asset/watch_operation_en.html";
            }
        }
        initWebViews();

        watchOperationWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return true;
            }
        });
        watchOperationWebView.loadUrl(url);

    }

    private void initWebViews() {
        webSettings = watchOperationWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);

    }

    private void initViews() {
        tvTitle.setText(getResources().getText(R.string.operation));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
