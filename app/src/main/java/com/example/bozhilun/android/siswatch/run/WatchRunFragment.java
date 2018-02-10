package com.example.bozhilun.android.siswatch.run;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.MapRecordActivity;
import com.example.bozhilun.android.activity.wylactivity.OutdoorCyclingActivityStar;
import com.example.bozhilun.android.activity.wylactivity.SportsHistoryActivity;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.adapter.OutDoorSportAdapter;
import com.example.bozhilun.android.siswatch.bean.OutDoorSportBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/17.
 */

/**
 * 开跑页面
 */
public class WatchRunFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "WatchRunFragment";

    View watchRunView;
    @BindView(R.id.commentRunRecyclerView)
    RecyclerView commentRunRecyclerView;
    Unbinder unbinder;
    //总计距离
    @BindView(R.id.watch_run_totalDiscTv)
    TextView watchRunTotalDiscTv;
    //累计时长
    @BindView(R.id.watch_run_totalTimeTv)
    TextView watchRunTotalTimeTv;
    //平均配速
    @BindView(R.id.watch_run_averageSpeedTv)
    TextView watchRunAverageSpeedTv;
    //跑步次数
    @BindView(R.id.watch_run_totalNumTv)
    TextView watchRunTotalNumTv;
    @BindView(R.id.watch_runSwipe)
    SwipeRefreshLayout watchRunSwipe;
    @BindView(R.id.watch_run_sportTypeTitleTv)
    TextView watchRunSportTypeTitleTv;

    private List<OutDoorSportBean> outDoorSportBeanList;
    //临时数据集合
    private List<OutDoorSportBean> runSportList;

    private OutDoorSportAdapter outDoorSportAdapter;
    private AlertDialog.Builder builder;
    //跑步或者骑行
    private String runTags = "all";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        watchRunView = inflater.inflate(R.layout.comment_run_home_fragment, container, false);
        unbinder = ButterKnife.bind(this, watchRunView);
        initViews();

        return watchRunView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getRunMapListData(runTags);    //获取地图的历史记录
    }

    private void getRunMapListData(final String runtag) {
        String url = "http://47.90.83.197:8080/watch/sport/getOutdoorSport";
        JSONObject jsono = null;
        try {
            jsono = new JSONObject();
            jsono.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsono.put("date", WatchUtils.getCurrentDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "----maps----" + jsono.toString());
        SubscriberOnNextListener subs = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                watchRunSwipe.setRefreshing(false);
                if (result != null) {
                    Log.e(TAG, "------result----" + result);
                    analysisData(result,runtag); //解析数据
                }
            }
        };
        CommonSubscriber comms = new CommonSubscriber(subs, getActivity());
        OkHttpObservable.getInstance().getData(comms, url, jsono.toString());

    }

    //解析数据
    private void analysisData(String result,String runtag) {
        Log.e(TAG, "-----1111---" + result+"---"+runtag);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("resultCode").equals("001")) {
                String outdoorSport = jsonObject.getString("outdoorSport");
                if (outdoorSport != null && !outdoorSport.equals("[]")) {
                    outDoorSportBeanList = new Gson().fromJson(outdoorSport, new TypeToken<List<OutDoorSportBean>>() {
                    }.getType());
                    runSportList = new ArrayList<>();
                    if(runtag.equals("runsport")){  //跑步
                        runSportList.clear();
                        for(OutDoorSportBean ob : outDoorSportBeanList){
                            if(ob.getType() == 0){
                                runSportList.add(ob);
                            }
                        }
                        outDoorSportAdapter = new OutDoorSportAdapter(runSportList,getActivity());
                        commentRunRecyclerView.setAdapter(outDoorSportAdapter);
                        showSportComputData(runSportList);

                    }else if(runtag.equals("cyclesport")){  //骑行
                        runSportList.clear();
                        for(OutDoorSportBean ob : outDoorSportBeanList){
                            if(ob.getType() == 1){
                                runSportList.add(ob);
                            }
                        }
                        outDoorSportAdapter = new OutDoorSportAdapter(runSportList,getActivity());
                        commentRunRecyclerView.setAdapter(outDoorSportAdapter);
                        showSportComputData(runSportList);

                    }else{
                        runSportList.clear();
                        outDoorSportAdapter = new OutDoorSportAdapter(outDoorSportBeanList, getActivity());
                        commentRunRecyclerView.setAdapter(outDoorSportAdapter);
                        outDoorSportAdapter.notifyDataSetChanged();
                        showSportComputData(outDoorSportBeanList);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSportComputData(final List<OutDoorSportBean> runSportList) {
        //当天的跑步次数
        watchRunTotalNumTv.setText(outDoorSportAdapter.getItemCount() + "");
        int sumSecond = 0;  //总计的秒
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        BigDecimal bigDecimal = new BigDecimal("0.0");

        for (int i = 0; i < runSportList.size(); i++) {
            try {
                BigDecimal bigD = new BigDecimal(runSportList.get(i).getDistance());
                bigDecimal = bigD.add(bigDecimal);
                Log.e(TAG, "----bigDecimal---" + bigDecimal.doubleValue() + "---" + runSportList.get(i).getDistance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //总距离
            watchRunTotalDiscTv.setText(bigDecimal.doubleValue() + "");
            //获取运动时间
            String sportTime = runSportList.get(i).getTimeLen();
            try {
                Date sportDate = sdf.parse(sportTime);
                int sum = (sportDate.getHours() * 3600) + (sportDate.getMinutes() * 60) + (sportDate.getSeconds());
                sumSecond = sumSecond + sum;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "-----sumSecodn---" + sumSecond);
        //累计时长
        double sportLonTime = WatchUtils.div(sumSecond, 3600, 2);
        watchRunTotalTimeTv.setText(sportLonTime + "h");
        //计算平均配速
        watchRunAverageSpeedTv.setText("" + WatchUtils.div(bigDecimal.doubleValue(), sportLonTime, 2) + "Km/h");

        //item的点击事件
        outDoorSportAdapter.setListener(new OutDoorSportAdapter.OnOutDoorSportItemClickListener() {
            @Override
            public void doItemClick(int position) {
                Map<String, Object> mapb = new HashMap<>();
                mapb.put("year", runSportList.get(position).getRtc());//日期
                mapb.put("day", runSportList.get(position).getStartTime());//开始日期
                mapb.put("zonggongli", runSportList.get(position).getDistance() + "Km");//总公里
                if (runSportList.get(position).getType()== 0) {
                    mapb.put("qixing", getResources().getString(R.string.outdoor_running));//骑行或者跑步
                    mapb.put("image", R.mipmap.huwaipaohuan);//跑步-骑行
                } else {
                    mapb.put("qixing", getResources().getString(R.string.outdoor_cycling));//骑行或者跑步
                    mapb.put("image", R.mipmap.qixinghuan);//跑步-骑行
                }
                mapb.put("chixugongli", runSportList.get(position).getDistance() + "Km");//持续公里数
                mapb.put("chixutime", runSportList.get(position).getTimeLen());//持续时间
                mapb.put("kclal", runSportList.get(position).getCalories() + "Kcal");//卡路里
                mapb.put("image", runSportList.get(position).getImage());
                mapb.put("temp", runSportList.get(position).getTemp());
                mapb.put("description", runSportList.get(position).getDescription());
                mapb.put("speed", runSportList.get(position).getSpeed());
                Intent intent = new Intent(getActivity(), MapRecordActivity.class);
                intent.putExtra("mapdata", runSportList.get(position).getLatLons().toString());
                intent.putExtra("mapdata2", new Gson().toJson(mapb));
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        if(runTags.equals("runsport")){ //跑步
            watchRunSportTypeTitleTv.setText(R.string.outdoor_running);
        }else if(runTags.equals("cyclesport")){
            watchRunSportTypeTitleTv.setText(R.string.outdoor_cycling);
        }else{
            watchRunSportTypeTitleTv.setText(R.string.sportdata);
        }
        LinearLayoutManager linm = new LinearLayoutManager(getActivity());
        linm.setOrientation(LinearLayoutManager.VERTICAL);
        commentRunRecyclerView.setLayoutManager(linm);
        commentRunRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        outDoorSportBeanList = new ArrayList<>();
        outDoorSportAdapter = new OutDoorSportAdapter(outDoorSportBeanList, getActivity());
        commentRunRecyclerView.setAdapter(outDoorSportAdapter);
        watchRunSwipe.setOnRefreshListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        if (ConnectManages.isNetworkAvailable(getActivity())) {
            watchRunSwipe.setRefreshing(true);
            getRunMapListData(runTags);
        }else{
            watchRunSwipe.setRefreshing(false);
        }
    }

    @OnClick({R.id.watch_run_sportTypeTitleTv, R.id.watch_run_sportHistoryTitleTv, R.id.watch_runStartBtn})
    public void onViewClicked(View view) {
        String runTypeString[] = new String[]{getResources().getString(R.string.outdoor_running), getResources().getString(R.string.outdoor_cycling)};
        switch (view.getId()) {
            case R.id.watch_run_sportTypeTitleTv:   //类型标题
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.select_running_mode))
                        .setItems(runTypeString, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (i == 0) { //跑步
                                    watchRunSportTypeTitleTv.setText(R.string.outdoor_running);
                                    runTags = "runsport";
                                    getRunMapListData(runTags);
                                } else {  //骑行
                                    watchRunSportTypeTitleTv.setText(R.string.outdoor_cycling);
                                    runTags = "cyclesport";
                                    getRunMapListData(runTags);
                                }
                            }
                        }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                break;
            case R.id.watch_run_sportHistoryTitleTv:    //历史记录
                startActivity(new Intent(getActivity(), SportsHistoryActivity.class));
                break;
            case R.id.watch_runStartBtn:    //开跑
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.select_running_mode))
                        .setItems(runTypeString, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (i == 0) { //跑步
                                    SharedPreferencesUtils.saveObject(getActivity(), "type", "0");
                                    startActivity(new Intent(getActivity(), OutdoorCyclingActivityStar.class));
                                } else {  //骑行
                                    SharedPreferencesUtils.saveObject(getActivity(), "type", "1");
                                    startActivity(new Intent(getActivity(), OutdoorCyclingActivityStar.class));
                                }
                            }
                        }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                break;
        }
    }
}
