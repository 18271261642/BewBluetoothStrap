package com.example.bozhilun.android.activity.wylactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.adapter.MylistAdspter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.calendar.CaldroidFragment;
import com.example.bozhilun.android.calendar.CaldroidListener;
import com.example.bozhilun.android.coverflow.ListViewForScrollView;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/30.
 * 运动历史记录
 */

public class SportsHistoryActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.listview_history)
    ListViewForScrollView mylistview;

    private  static String url="http://47.90.83.197:8080/watch/sport/getOutdoorSport";//查询地图数据
    JSONObject aa;
    //查询下数据
    private    List<Map<String, Object>>  list;
    int myid;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;

    private CaldroidFragment caldroidFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

    }

    @Override
    protected void initViews() {
        title.setText(getResources().getString(R.string.sports_history));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        myid=0;
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    MyLogUtil.i("response"+resultCode);
                    if ("001".equals(resultCode)) {
                        try{
                            list=new ArrayList<Map<String,Object>>();
                            String OutDoorSport=jsonObject.optString("outdoorSport");
                            JSONArray JSONObjectArray=new  JSONArray(OutDoorSport);
                            for (int i = 0; i < JSONObjectArray.length(); i++) {
                                JSONObject jo = (JSONObject) JSONObjectArray.get(i);
                                String rtc=jo.optString("rtc").toString();//开始日期
                                String image=jo.optString("image").toString();//天气头像
                                String temp=jo.optString("temp").toString();//温度
                                String distance=jo.optString("distance").toString();//总公里
                                String timeLen=jo.optString("timeLen").toString();//持续时间
                                String description=jo.optString("description").toString();//质量
                                String calories=jo.optString("calories").toString();//卡路里
                                String type=jo.optString("type").toString();//类型
                                String speed=jo.optString("speed").toString();//速度
                                String startTime=jo.optString("startTime").toString();//开始时间
                                String  latLons=jo.optString("latLons").toString();
                                Map<String, Object>    map=new HashMap<>();
                                map.put("year", rtc);//年
                                map.put("day", startTime);//日
                                map.put("zonggongli", distance+"Km");//总公里
                                if("0".equals(type)){
                                    map.put("qixing", getResources().getString(R.string.outdoor_running));//骑行或者跑步
                                    map.put("image", R.mipmap.huwaipaohuan);//跑步-骑行
                                }else{
                                    map.put("qixing",getResources().getString(R.string.outdoor_cycling) );//骑行或者跑步
                                    map.put("image", R.mipmap.qixinghuan);//跑步-骑行
                                }
                                map.put("chixugongli", distance+"Km");//持续公里数
                                map.put("chixutime", timeLen);//持续时间
                                map.put("kclal", calories+"Kcal");//卡路里
                                list.add(map);
                                Map    mapb=new HashMap();
                                mapb.put("year", rtc);//年
                                mapb.put("day", startTime);//日
                                mapb.put("zonggongli", distance+"Km");//总公里
                                if("0".equals(type)){
                                    mapb.put("qixing", getResources().getString(R.string.outdoor_running));//骑行或者跑步
                                    mapb.put("image", R.mipmap.huwaipaohuan);//跑步-骑行
                                }else{
                                    mapb.put("qixing", getResources().getString(R.string.outdoor_cycling));//骑行或者跑步
                                    mapb.put("image", R.mipmap.qixinghuan);//跑步-骑行
                                }
                                mapb.put("chixugongli", distance+"Km");//持续公里数
                                mapb.put("chixutime", timeLen);//持续时间
                                mapb.put("kclal", calories+"Kcal");//卡路里
                                mapb.put("image",image);
                                mapb.put("temp",temp);
                                mapb.put("description",description);
                                mapb.put("speed",speed);
                                //存下经纬度的值
                                myid++;
                                Gson gson = new Gson();
                                SharedPreferencesUtils.saveObject(SportsHistoryActivity.this,String.valueOf(myid),latLons);
                                SharedPreferencesUtils.saveObject(SportsHistoryActivity.this,String.valueOf(myid+"one"),gson.toJson(mapb));
                                //得到数据
                                mylistview.setVisibility(View.VISIBLE);
                                mylistview.setAdapter(new MylistAdspter(SportsHistoryActivity.this, list));
                            }


                        }catch (Exception E){E.printStackTrace();}
                    } else {
                        mylistview.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {e.printStackTrace();}}};
        if(null!=mylistview){
            mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent AA=new Intent(SportsHistoryActivity.this,MapRecordActivity.class);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    AA.putExtra("mapdata",String.valueOf( SharedPreferencesUtils.readObject(SportsHistoryActivity.this,String.valueOf(position+1))));
                    AA.putExtra("mapdata2",String.valueOf( SharedPreferencesUtils.readObject(SportsHistoryActivity.this,String.valueOf(position+1+"one"))));
                    startActivity(AA);
                }
            });
        }

        Mapdata(df.format(new Date()));
    }

    @Override
    protected int getContentViewId() {return R.layout.activity_sportshistory;}

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mylistview=(ListViewForScrollView)findViewById(R.id.listview_history);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        caldroidFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }

        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

       FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
               // Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
               // Toast.makeText(getApplicationContext(), df.format(date), Toast.LENGTH_SHORT).show();
                Mapdata(df.format(date));

            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
               // Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
               // Toast.makeText(getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                   // Toast.makeText(getApplicationContext(), "Caldroid view is created", Toast.LENGTH_SHORT).show();
                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);
    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();
        Date blueDate = cal.getTime();
        cal = Calendar.getInstance();
        Date greenDate = cal.getTime();
        if (caldroidFragment != null) {
            caldroidFragment.setBackgroundResourceForDate(R.color.blue,
                    blueDate);
            caldroidFragment.setBackgroundResourceForDate(R.color.mosi,
                    greenDate);
            caldroidFragment.setTextColorForDate(R.color.blue, blueDate);
            caldroidFragment.setTextColorForDate(R.color.blue, greenDate);
        }
    }



    /**
     * 查询经纬度数据
     */
    public  void  Mapdata(String Data){
        //判断网络是否连接
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", Common.customer_id);
            map.put("date",Data);
            String mapjson = JSON.toJSONString(map);
            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, SportsHistoryActivity.this);
            OkHttpObservable.getInstance().getData(dialogSubscriber,url, mapjson);
        }catch (Exception E){E.printStackTrace();}}




    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myid=0;
    }

}
