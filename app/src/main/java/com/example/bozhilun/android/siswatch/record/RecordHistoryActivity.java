package com.example.bozhilun.android.siswatch.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sunjianhua on 2017/11/6.
 */

/**
 * 点击时间显示记录
 */
public class RecordHistoryActivity extends WatchBaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recordHistoryRecyclerView)
    RecyclerView recordHistoryRecyclerView;
    @BindView(R.id.startDateTv)
    TextView startDateTv;

    private RecordDataAdapter recordDataAdapter;
    private List<WatchDataDatyBean> watchDataDatyBeanList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_history);
        ButterKnife.bind(this);

        initViews();

        getRecordData();


    }

    private void getRecordData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(RecordHistoryActivity.this, "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(RecordHistoryActivity.this, "mylanmac"));
            //开始时间
            jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 30)));
            //结束时间
            jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SubscriberOnNextListener sub = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                if (s != null) {
                    JSONObject jso = null;
                    try {
                        jso = new JSONObject(s);
                        String daydata = jso.getString("day");
                        if (!daydata.equals("[]")) {
                            watchDataDatyBeanList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                            }.getType());
                            Collections.sort(watchDataDatyBeanList, new Comparator<WatchDataDatyBean>() {
                                @Override
                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
                                    return o2.getRtc().compareTo(o1.getRtc());
                                }
                            });
                            recordDataAdapter = new RecordDataAdapter(watchDataDatyBeanList, RecordHistoryActivity.this);
                            recordHistoryRecyclerView.setAdapter(recordDataAdapter);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        CommonSubscriber coms = new CommonSubscriber(sub, RecordHistoryActivity.this);
        OkHttpObservable.getInstance().getData(coms, url, jsonParams.toString());


    }

    private void initViews() {
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd");
        tvTitle.setText(getResources().getString(R.string.history_record));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            startDateTv.setText(sdfs.format(WatchUtils.getDateBefore(sdfs.parse(WatchUtils.getCurrentDate()), 30))
                    +" -- "+sdfs.format(WatchUtils.getDateBefore(sdfs.parse(WatchUtils.getCurrentDate()), 1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LinearLayoutManager linm = new LinearLayoutManager(this);
        linm.setOrientation(LinearLayoutManager.VERTICAL);
        recordHistoryRecyclerView.setLayoutManager(linm);
        recordHistoryRecyclerView.addItemDecoration(new DividerItemDecoration(RecordHistoryActivity.this, DividerItemDecoration.VERTICAL));
        watchDataDatyBeanList = new ArrayList<>();
        recordDataAdapter = new RecordDataAdapter(watchDataDatyBeanList, RecordHistoryActivity.this);
        recordHistoryRecyclerView.setAdapter(recordDataAdapter);


    }
}
