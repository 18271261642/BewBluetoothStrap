package com.example.bozhilun.android.b15p.fragment.fram_child;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.B15pHomeActivity;
import com.example.bozhilun.android.b15p.fragment.B15pRecordFragment;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.MyNewHandler;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.B15pBaseFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.util.SportUtil;


import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildSprotFragment extends B15pBaseFragment {
    private static final String TAG = "===>>ChildSprotFragment";
    @BindView(R.id.recordwave_progress_bar)
    WaveProgress recordwaveProgressBar;
    @BindView(R.id.watch_recordTagstepTv)
    TextView watchRecordTagstepTv;
    @BindView(R.id.watch_recordKcalTv)
    TextView watchRecordKcalTv;
    @BindView(R.id.watch_recordMileTv)
    TextView watchRecordMileTv;


    private boolean mReceiverTag = false;   //广播接受者标识

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "-------onCreate-------");
        regeditReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "-------onCreate------");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "------onStart-------");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "------onResume-------");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "---onPause------");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "------onStop------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "-------onDestroyView-----");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "------onDestroy------");
        if (mReceiverTag) {
            mReceiverTag = false;
            getContext().unregisterReceiver(h9Receiver);
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_watch_record_change;
    }

    @Override
    protected void lazyLoad() {
        initView();
        refresh();
    }

    private float GOAL = 7000;
    private float STEP = 0;

    private void initView() {
        String tempAimStep = (String) SharedPreferencesUtils.getParam(getContext(),"settagsteps","");
        if (!getActivity().isFinishing() && MyCommandManager.DEVICENAME != null) {
            String tepmStep = (String) SharedPreferencesUtils.getParam(getContext(), "b15pSteps", ""); //步数
            String tempDisc = (String)SharedPreferencesUtils.getParam(getContext(), "b15pDid", "");    //距离
            String tempKcal = (String)SharedPreferencesUtils.getParam(getContext(), "b15pKcl",  "");    //卡路里

            Log.e(TAG,"---initVIew--"+tempAimStep+"-="+tepmStep+"-="+tempDisc+"-="+tempKcal);

            if(!WatchUtils.isEmpty(tempAimStep) && !WatchUtils.isEmpty(tepmStep) && !WatchUtils.isEmpty(tempKcal)){
                watchRecordMileTv.setText(tempDisc);
                watchRecordKcalTv.setText(tempKcal);
                recordwaveProgressBar.setMaxValue(Float.valueOf(tempAimStep.trim()));
                recordwaveProgressBar.setValue(Float.valueOf(tepmStep.trim()));
            }

        }else{
            recordwaveProgressBar.setMaxValue(Float.valueOf(tempAimStep.trim()));
            recordwaveProgressBar.setValue(0);
        }

    }

    public void getRefreshDatas() {
        try {
            if(!getActivity().isFinishing()){
                //目标步数
                final String b15pTagSteps = (String) SharedPreferencesUtils.getParam(getContext(),"settagsteps","");
                Log.e(TAG,"-------目标步数="+b15pTagSteps);
                //获取步数数据
                MyApp.getVpOperateManager().readSportStep(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new ISportDataListener() {
                    @Override
                    public void onSportDataChange(SportData sportData) {
                        String message = "当前计步:\n" + sportData.getStep();
                        Log.e(TAG, message);
                        int step = sportData.getStep();
                        if (step >= 0) {
                            STEP = step;
                        }
                        //步数显示
                        recordwaveProgressBar.setMaxValue(Float.valueOf(b15pTagSteps.trim()));
                        recordwaveProgressBar.setValue(STEP);

                        //计算距离和卡路里
                        //身高
                        String userHeight = (String) SharedPreferencesUtils.getParam(getActivity(),"userheight","");
                        int tempUserHeight = Integer.valueOf(userHeight.trim());
                        //计算步长
                        double stepLong = SportUtil.getStepLength(tempUserHeight);
                        double newDisc = WatchUtils.getDistants(step,stepLong);
                        double newKcal = WatchUtils.getKcal(step,stepLong);
                        Log.e(TAG,"---------身高="+userHeight+"---步长="+stepLong+"---newDisc="+newDisc+"--newKcal="+newKcal);

                        //计算距离
                        double sportDisc = SportUtil.getDistance1(sportData.getStep(),tempUserHeight);
                        //计算卡路里
                        //double sportKcal = SportUtil.getKcal0(sportData.getStep(),tempUserHeight,false);
                        double sportKcal = WatchUtils.mul(sportDisc,65.4);
                        Log.e(TAG,"----计算数据="+sportDisc+"-="+sportKcal*1000);
                        watchRecordMileTv.setText(""+sportDisc+"");
                        watchRecordKcalTv.setText(""+ StringUtils.substringBefore(String.valueOf(sportKcal),".")+"");
                        SharedPreferencesUtils.setParam(getContext(), "b15pSteps", ""+step+""); //步数
                        SharedPreferencesUtils.setParam(getContext(), "b15pDid", ""+sportDisc);    //距离
                        SharedPreferencesUtils.setParam(getContext(), "b15pKcl",  StringUtils.substringBefore(String.valueOf(sportKcal),"."));    //卡路里

                        //上传当天数据到后台
                        uploadTodayStepsToServer(String.valueOf(sportDisc),String.valueOf(sportKcal),step);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        myNewHandler = null;
    }

    MyNewHandler myNewHandler;

    public void refresh() {
        myNewHandler = MyNewHandler.getInstance();
        myNewHandler.setMyMessage(new MyNewHandler.MyMessage() {
            @Override
            public void mHandler(Message msg) {
                Log.d(TAG, "=============" + "运动");
                if (msg.what == myNewHandler.getMessgeNumber()) {
                    myNewHandler.removeMessages(myNewHandler.getMessgeNumber());
                    if (B15pRecordFragment.getPAGES() == 0) {
                        Log.d(TAG, "----刷新---------" + "步数页面");
                        getRefreshDatas();
                    }
                    B15pRecordFragment.getSwipeRefresh().setRefreshing(false);
                }
            }
        });

    }

    //注册广播，防止广播重复注册
    private void regeditReceiver() {
        if (!mReceiverTag) {
            IntentFilter intFilter = new IntentFilter();
            intFilter.addAction(B15pHomeActivity.H9CONNECT_STATE_ACTION);
            mReceiverTag = true;
            getContext().registerReceiver(h9Receiver, intFilter);
        }
    }

    /**
     * 监听手表连接状态的广播
     */
    private BroadcastReceiver h9Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(B15pHomeActivity.H9CONNECT_STATE_ACTION)) {
                    if (intent.getStringExtra("h9constate") != null && intent.getStringExtra("h9constate").equals("conn")) {
                        if (!getActivity().isFinishing()) {
                            Log.e(TAG,"------广播成功");
                            getRefreshDatas();
                        }
                    }
                }
            }
        }
    };

    //上传当天数据
    private void uploadTodayStepsToServer(String disc, String kcal, int steps) {
        String syncUrl = URLs.HTTPs + URLs.upSportData;
        String aimSteps = (String) SharedPreferencesUtils.getParam(getContext(),"settagsteps","");
        int tempAimStep = Integer.valueOf(aimSteps.trim());
        int aimState ; //是否达标
        if(tempAimStep - steps >=0){    //达标
            aimState = 0;
        }else{
            aimState = 1;
        }
        JSONObject jsonOb = null;
        try {
            jsonOb = new JSONObject();
            jsonOb.put("userId",SharedPreferencesUtils.readObject(getContext(),"userId"));
            jsonOb.put("deviceCode",SharedPreferencesUtils.readObject(getContext(),"mylanmac"));
            jsonOb.put("stepNumber",steps);
            jsonOb.put("distance",disc);
            jsonOb.put("calories",kcal);
            jsonOb.put("timeLen",0);
            jsonOb.put("date",WatchUtils.getCurrentDate());
            jsonOb.put("status",aimState);
        }catch (Exception e){
            e.printStackTrace();
        }
        SubscriberOnNextListener sbus = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e(TAG,"-----result="+result);
            }

        };
        CommonSubscriber coms = new CommonSubscriber(sbus,getContext());
        OkHttpObservable.getInstance().getData(coms,syncUrl,jsonOb.toString());


    }

}
