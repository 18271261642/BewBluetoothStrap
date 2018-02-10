package com.example.bozhilun.android.siswatch.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.activity.SetActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.xinlangweibo.SinaUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2017/7/17.
 */

/**
 * sis watch 我的fragmet
 */
public class WatchMineFragment extends Fragment {

    View watchMineView;
    Unbinder unbinder;
    //用户昵称
    @BindView(R.id.watch_mine_uname)
    TextView watchMineUname;
    //头像
    @BindView(R.id.watch_mine_userheadImg)
    ImageView watchMineUserheadImg;
    //总公里数
    @BindView(R.id.watch_distanceTv)
    TextView watchDistanceTv;
    //日平均步数
    @BindView(R.id.watch_mine_avageStepsTv)
    TextView watchMineAvageStepsTv;
    //达标天数
    @BindView(R.id.watch_mine_dabiaoTv)
    TextView watchMineDabiaoTv;

    private SinaUserInfo userInfo;

    ArrayList<String> daily_numberofstepsList;

    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        watchMineView = inflater.inflate(R.layout.fragment_watch_mine, null);
        unbinder = ButterKnife.bind(this, watchMineView);

        initData();

        getMyInfoData();    //获取我的总数

        initStepList();

        return watchMineView;
    }


    private void initStepList() {
        daily_numberofstepsList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            daily_numberofstepsList.add(String.valueOf(i * 1000));
        }

    }

    /**
     * 获取我的总数
     */
    private void getMyInfoData() {
        String myInfoUrl = URLs.HTTPs + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), "mylanmac"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, myInfoUrl, js.toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfoData();  //获取用户信息

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
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, url, jsonObj.toString());
    }

    private void initData() {
        SharedPreferences share = getActivity().getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        if (!WatchUtils.isEmpty(name)) {
            watchMineUname.setText(name + "");
        }
        //数据返回
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
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
                                    watchDistanceTv.setText("" + distances + "");
                                }
                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    watchMineDabiaoTv.setText("" + myInfoJsonObject.getString("count") + "");
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    watchMineAvageStepsTv.setText("" + myInfoJsonObject.getString("stepNumber") + "");
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        //我的数据返回
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("mine", "-----个人信息-----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                watchMineUname.setText("" + myInfoJsonObject.getString("nickName") + "");
                                String imgHead = myInfoJsonObject.getString("image");
                                if (!WatchUtils.isEmpty(imgHead)) {
                                    //头像
                                    Glide.with(getActivity()).load(myInfoJsonObject.getString("image"))
                                            .bitmapTransform(new CropCircleTransformation(getActivity())).placeholder(R.mipmap.icon_bozlun_default_img).into(watchMineUserheadImg);    //头像
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

/*    @OnClick({R.id.mine_tag_rel, R.id.mine_setting_rel, R.id.watch_mine_userheadImg, R.id.mine_setting_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_tag_rel: //目标设置
                //startActivity(new Intent(getActivity(), TargetSettingActivity.class));
                ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        //设置步数
//                        watchRecordTagstepTv.setText("目标步数 " + profession);
//                        recordwaveProgressBar.setMaxValue(Float.valueOf(profession));
                        SharedPreferencesUtils.setParam(getActivity(), "settagsteps", profession);
                        // recordwaveProgressBar.setValue(Float.valueOf((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")));

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(daily_numberofstepsList) //min year in loop
                        .dateChose("10000") // date chose when init popwindow
                        .build();
                dailyumberofstepsPopWin.showPopWin(getActivity());


                break;
            case R.id.mine_setting_rel: //设置
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
            case R.id.watch_mine_userheadImg:   //点击用户头像到用户详情页面
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.mine_setting_device:
                if (null == MyCommandManager.DEVICENAME) {
                    startActivity(new Intent(getActivity(), SearchDeviceActivity.class));//收索设备
                } else {
                    startActivity(new Intent(getActivity(), WatchDeviceActivity.class));//mine_dev_image
                }
                break;
        }
    }*/

    @OnClick({R.id.watchMinepersonalData, R.id.watchMineDevice, R.id.watchmineSetting, R.id.watch_mine_userheadImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_mine_userheadImg://用户头像点击
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.watchMinepersonalData:    //个人资料
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.watchMineDevice:  //我的设备
                if (null == MyCommandManager.DEVICENAME) {
                    startActivity(new Intent(getActivity(), NewSearchActivity.class));//收索设备
                    WatchUtils.disCommH8();
                    getActivity().finish();
                } else {
                    startActivity(new Intent(getActivity(), WatchDeviceActivity.class));//mine_dev_image
                }
                break;
            case R.id.watchmineSetting:  //系统设置
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
        }
    }
}
