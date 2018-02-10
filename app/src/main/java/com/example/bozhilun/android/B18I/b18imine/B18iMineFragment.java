package com.example.bozhilun.android.B18I.b18imine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.B18iStepDatasDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.bumptech.glide.Glide;
import com.example.bozhilun.android.B18I.b18ibean.B18iStepDatas;
import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18isystemic.B18IAppSettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.B18IRankingListActivity;
import com.example.bozhilun.android.B18I.b18isystemic.B18ISettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.B18ITargetSettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.FindFriendActivity;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.model.SportCacheData;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 我的界面
 * Created by Administrator on 2017/8/25.
 */

public class B18iMineFragment extends Fragment {
    private final String TAG = "----->>>" + this.getClass();
    View b18iMineView;
    //头像显示ImageView
    @BindView(R.id.userImageHead)
    ImageView userImageHead;
    //用户名称显示TextView
    @BindView(R.id.userName)
    TextView userName;
    //总公里数显示TextView
    @BindView(R.id.totalKilometers)
    TextView totalKilometers;
    //日均步数显示TextView
    @BindView(R.id.equalStepNumber)
    TextView equalStepNumber;
    //达标天数显示TextView
    @BindView(R.id.standardDay)
    TextView standardDay;
    Unbinder unbinder;

    private int DISTANCE = 0;
    private int STEP = 0;

    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b18iMineView = inflater.inflate(R.layout.fragment_b18i_mine, container, false);
        unbinder = ButterKnife.bind(this, b18iMineView);

        initData();

        //获取用户信息
        getUserInfoData();
        //获取数据展示
        getUserSportData();
        return b18iMineView;
    }

    //获取显示的数据
    private void getUserSportData() {
        String myInfoUrl = URLs.HTTPs + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, myInfoUrl, js.toString());
    }

    private void initData() {
        //我的数据返回
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {//{"userInfo":{"birthday":"1994-12-27","image":"http://47.90.83.197/image/2017/07/31/1501490388771.jpg","nickName":"孙建华","sex":"M","weight":"60 kg","userId":"5c2b58f0681547a0801d4d4ac8465f82","phone":"15916947377","height":"175 cm"},"resultCode":"001"}
                Log.e("mine", "-----个人信息-----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                userName.setText("" + myInfoJsonObject.getString("nickName") + "");
                                String imgHead = myInfoJsonObject.getString("image");
                                if (!WatchUtils.isEmpty(imgHead)) {
                                    //头像
                                    Glide.with(getActivity()).load(imgHead).bitmapTransform(new CropCircleTransformation(getActivity())).into(userImageHead);    //头像
                                }
                                String userHeight = myInfoJsonObject.getString("height");
                                if (userHeight != null) {
                                    if (userHeight.contains("cm")) {
                                        String newHeight = userHeight.substring(0, userHeight.length() - 2);
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", newHeight.trim());
                                    } else {
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", userHeight.trim());
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        //显示数据返回
        //数据返回
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) { //{"myInfo":{"distance":48.3,"count":2,"stepNumber":1582},"resultCode":"001"}
                Log.e("mine", "------result----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("resultCode") == 001) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("myInfo");
                            if (myInfoJsonObject != null) {
                                String distances = myInfoJsonObject.getString("distance");
                                Log.e("WTM", "-------distances----" + distances);
                                if (!WatchUtils.isEmpty(distances)) {
                                    //总公里数
                                    totalKilometers.setText("" + distances + "");
                                }
                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    standardDay.setText("" + myInfoJsonObject.getString("count") + "");
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    equalStepNumber.setText("" + myInfoJsonObject.getString("stepNumber") + "");
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    //获取用户信息
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObj.toString());
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onB18iEventBus(B18iEventBus event) {
//        switch (event.getName()) {
//            case "sportData":
//                List<SportCacheData> sportCacheDatas = (List<SportCacheData>) event.getObject();
//                List<String> timeList = new ArrayList<>();
//                for (SportCacheData sport : sportCacheDatas) {
//                    String strTimes = B18iUtils.getStrTimes(String.valueOf(sport.timestamp));//时间戳转换
//                    String s1 = B18iUtils.interceptString(strTimes, 0, 10);
//                    DISTANCE += sport.distance;
//                    STEP += sport.step;
//                    if (!timeList.contains(s1)) {
//                        timeList.add(s1);
//                    }
//                }
//                DISTANCE = DISTANCE / 1000;
//                STEP = STEP / timeList.size();
////                totalKilometers.setText(String.valueOf(DISTANCE));//总距离
////                equalStepNumber.setText(String.valueOf(STEP));//平均步数
//                break;
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.privatemode_cardview, R.id.targetSetting,
            R.id.personalData, R.id.smartAlert, R.id.findFriends, R.id.mineSetting, R.id.userImageHead})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.userImageHead:    //点击头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.privatemode_cardview://排行榜
                startActivity(new Intent(getActivity(), B18IRankingListActivity.class).putExtra("is18i", "B18i"));
                break;
            case R.id.targetSetting://目标设置
                startActivity(new Intent(getActivity(), B18ITargetSettingActivity.class).putExtra("is18i", "B18i"));
                break;
            case R.id.personalData://个人资料
                //  startActivity(new Intent(getActivity(), MinePersonDataActivity.class).putExtra("is18i", "B18i"));
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.smartAlert://功能设置
                startActivity(new Intent(getActivity(), B18ISettingActivity.class).putExtra("is18i", "B18i"));
                break;
            case R.id.findFriends://查找朋友
                startActivity(new Intent(getActivity(), FindFriendActivity.class).putExtra("is18i", "B18i"));
                break;
            case R.id.mineSetting://设置
                startActivity(new Intent(getActivity(), B18IAppSettingActivity.class).putExtra("is18i", "B18i"));
                break;
        }
    }
}
