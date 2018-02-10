package com.example.bozhilun.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.activity.PrivateModeActivity;
import com.example.bozhilun.android.activity.SetActivity;
import com.example.bozhilun.android.activity.TargetSettingActivity;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.Sport;
import com.example.bozhilun.android.bean.Sporthours;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.view.MyImageviews;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;

import static com.example.bozhilun.android.util.Common.formatDouble;

/**
 * Created by thinkpad on 2017/3/6.
 */

public class MineFragment extends BaseFragment {

    @BindView(R.id.mine_logo_ivbbb)
    MyImageviews mineLogoIv;
    @BindView(R.id.name_tv_my)
    TextView nameTv;
    @BindView(R.id.avtor_relayout)
    RelativeLayout avtorRelayout;
    @BindView(R.id.dayaverage_tv)
    TextView dayaverageTv;
    @BindView(R.id.dayaveragestep_tv)
    TextView dayaveragestepTv;
    @BindView(R.id.standardays_tv)
    TextView standardaysTv;
    @BindView(R.id.standardaysnum_tv)
    TextView standardaysnumTv;
    @BindView(R.id.totalkilometers_tv)
    TextView totalkilometersTv;
    @BindView(R.id.stepgongli_tv)
    TextView stepgongliTv;
    @BindView(R.id.img_tag_one)
    ImageView imgTagOne;
    @BindView(R.id.mobiao_tv)
    TextView mobiaoTv;
    @BindView(R.id.img_tag_two_my)
    ImageView imgTagTwo;
    @BindView(R.id.siren_tv)
    TextView sirenTv;
    @BindView(R.id.img_tag_three_mine)
    ImageView imgTagThree;
    @BindView(R.id.steop_shezhi)
    TextView steopShezhi;

    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;
    private boolean isregister = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if ("update_data_huangcune".equals(result)) {
            Glide.get(getActivity()).clearMemory();//子线程清除缓存
            shuaxin();
        }
    }

    @Override
    public void onDestroy() {
        isregister = false;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences share = getActivity().getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        nameTv.setText(name);
    }


    @Override
    protected void initViews() {
        if (isregister == false) {
            isregister = true;
            EventBus.getDefault().register(this);
        }

        ViewUtils.inject(getActivity());
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("MineFragment","----result------"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        String shuzhu = jsonObject.optString("userInfo").toString();
                        try {
                            JSONObject jsonObjectb = new JSONObject(shuzhu);
                            String myname = jsonObjectb.getString("nickName");
                            String imguil = jsonObjectb.getString("image");
                            if (null != myname && !myname.equals("")) {
                                nameTv.setText(myname);
                            } else {
                                nameTv.setText("name");
                            }//姓名
                            //设置头像
                            BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
                            bitmapUtils.display(mineLogoIv, imguil);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("MineF","------获取步数返回----"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        String shuzhu = jsonObject.optString("myInfo").toString();
                        try {
                            JSONObject oArray = new JSONObject(shuzhu);
                            String stepNumber = oArray.getString("stepNumber");
                            String dab = oArray.getString("count");
                            String distance = oArray.getString("distance");
                            if (null != stepNumber && !stepNumber.equals("null")) {
                                dayaveragestepTv.setText(stepNumber + getResources().getString(R.string.steps));
                                Double tance = ((Double.parseDouble(stepNumber) * (3135.0 / 4200.0)) / 1000.0);
                                stepgongliTv.setText(""+distance+"");
                                standardaysnumTv.setText(dab + getResources().getString(R.string.data_report_day));
                            } else {
                                standardaysnumTv.setText("0");
                                dayaveragestepTv.setText("0");
                                stepgongliTv.setText("0");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        dayaveragestepTv.setText("0");
                        stepgongliTv.setText("0");
                        standardaysnumTv.setText("0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        shuaxin();
        shuaxin2();
      /*  Glide.with(this).load(B18iCommon.userInfo.getImage())
                .error(R.mipmap.touxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mineLogoIv);
        nameTv.setText(B18iCommon.userInfo.getNickName());*/
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @OnClick({R.id.mobiao_cardview, R.id.privatemode_cardview, R.id.set_cardview, R.id.avtor_relayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avtor_relayout:
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.mobiao_cardview:
                startActivity(new Intent(getActivity(), TargetSettingActivity.class));
                break;
            case R.id.privatemode_cardview:
                startActivity(new Intent(getActivity(), PrivateModeActivity.class));
                break;
            case R.id.set_cardview:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
        }
    }
    // HTTPs+URLs.getUserInfo

    /**
     * 刷新所以得数据（名字和头像）
     */
    public void shuaxin() {

        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getUserInfo, mapjson);
    }

    /**
     * 查看达标天数
     */
    public void shuaxin2() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        //map.put("userId", Common.customer_id);
        map.put("userId", (String) SharedPreferencesUtils.readObject(getActivity(), "userId"));
        map.put("deviceCode", MyCommandManager.ADDRESS);
        String mapjson = gson.toJson(map);
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, URLs.HTTPs + URLs.myInfo, mapjson);
    }


}
