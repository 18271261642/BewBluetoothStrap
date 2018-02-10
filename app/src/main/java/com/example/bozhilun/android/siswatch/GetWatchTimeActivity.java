package com.example.bozhilun.android.siswatch;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.utils.test.TimeInterface;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/26.
 */

public class GetWatchTimeActivity extends WatchBaseActivity implements TimeInterface {

    private static final String TAG = "GetWatchTimeActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.showAnalysisTimeTv)
    TextView showAnalysisTimeTv;
    @BindView(R.id.showSysTimeTv)
    TextView showSysTimeTv;
    private int count = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @BindView(R.id.getwahtchTimeProgressBar)
    ProgressBar getwahtchTimeProgressBar;

    private ProgressThread pdThread;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    showSysTimeTv.setText(sdf.format(new Date(System.currentTimeMillis())));
                    break;
                case 1002:
                    Log.e(TAG,"-22---count="+count);
                    //getwahtchTimeProgressBar.setProgress(count);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_watchtime);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initViews();

        initData();

    }

    private void initData() {
//        pdThread = new ProgressThread();
//        pdThread.start();
        new TimeThread().start();   //获取当前系统时间
        EventBus.getDefault().post(new MessageEvent("getWatchTime"));

    }

    @Override
    public void getWatchTime(Object o) {
        Log.e(TAG, "------jiekou------" + Arrays.toString((byte[]) o));
    }

    @OnClick(R.id.getWatchTimeTv)
    public void onViewClicked() {
        EventBus.getDefault().post(new MessageEvent("laidianphone"));
    }

    class ProgressThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (count <1000){
                try {
                    Thread.sleep(500);
                    count = doWork();
                    handler.sendEmptyMessage(1002);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private int doWork() {
        return count + 300;
    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = 1001;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.time));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //getwahtchTimeProgressBar.setMax(1000);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if (!WatchUtils.isEmpty(result)) {
            Log.e(TAG, "-----result-----" + result);
            if (result.equals("rebackWatchTime")) {
                //getwahtchTimeProgressBar.setProgress(1000);
                byte[] watchTimeData = (byte[]) event.getObject();
                //showWatchTimeTv.setText(Customdata.bytes2HexString(watchTimeData) + "-" + Arrays.toString(watchTimeData));
                showAnalysisTimeTv.setText(String.valueOf(20) + watchTimeData[6] + "-" + watchTimeData[7] + "-" + watchTimeData[8] + " " + watchTimeData[9] + ":" + watchTimeData[10] + ":" + watchTimeData[11]);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
