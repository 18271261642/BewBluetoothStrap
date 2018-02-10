package com.example.bozhilun.android.h9.settingactivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.h9.bean.SportBean;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/11/3 15:44
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SharePosterActivity extends WatchBaseActivity {

    @BindView(R.id.shape_poster)
    ViewPager shapePoster;
    @BindView(R.id.poster_liner)
    LinearLayout posterLiner;
    @BindView(R.id.line_sharpe_poster)
    LinearLayout lineSharpePoster;
    @BindView(R.id.rec_image_logo)
    RelativeLayout recImageLogo;
    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.poster_name)
    TextView posterName;
    @BindView(R.id.poster_timer)
    TextView posterTimer;
    @BindView(R.id.poster_step)
    TextView posterStep;
    @BindView(R.id.poster_main)
    LinearLayout posterMain;
    @BindView(R.id.btn_shape_poster)
    Button btnShapePoster;
    private List<View> viewList;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_share_poster_layout);
        ButterKnife.bind(this);
        addShapeData();

        init();
        posterTimer.setText(WatchUtils.getCurrentDate());
    }

    boolean isok = false;

    private void init() {
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
                                isok = true;
                                posterName.setText("" + myInfoJsonObject.getString("nickName") + "");
                                String imgHead = myInfoJsonObject.getString("image");
                                if (!WatchUtils.isEmpty(imgHead)) {
                                    //头像
                                    Glide.with(SharePosterActivity.this).
                                            load(imgHead).bitmapTransform(new
                                            CropCircleTransformation(SharePosterActivity.this)).into(posterImage);    //头像
                                }
                                String userHeight = myInfoJsonObject.getString("height");
                                if (userHeight != null) {
                                    if (userHeight.contains("cm")) {
                                        String newHeight = userHeight.substring(0, userHeight.length() - 2);
                                        SharedPreferencesUtils.setParam(SharePosterActivity.this, "userheight", newHeight.trim());
                                    } else {
                                        SharedPreferencesUtils.setParam(SharePosterActivity.this, "userheight", userHeight.trim());
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
    protected void onResume() {
        super.onResume();
        getSportDataForServer();
        getUserInfoData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isok = false;
    }


    /**
     * 获取用户信息
     */
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(this, "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObj.toString());
    }


    /**
     * 获取运动数据
     */
    private void getSportDataForServer() {
        JSONObject jsonObect = new JSONObject();
        try {
            jsonObect.put("userId", SharedPreferencesUtils.readObject(SharePosterActivity.this, "userId"));
            jsonObect.put("deviceCode", SharedPreferencesUtils.readObject(this, "mylanmac"));
            jsonObect.put("startDate", WatchUtils.getCurrentDate());
            jsonObect.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
//                Log.e("999999999", "-----1111----" + result);
                if (!WatchUtils.isEmpty(result)) {

                    SportBean sportBean = new Gson().fromJson(result, SportBean.class);
                    List<SportBean.DayBean> day = sportBean.getDay();
//                    Log.e("999999999", "----33---" + day.get(0).getStepNumber() + "==" + day.get(0).getRtc());
//                    List<WatchDataDatyBean> list = new Gson().fromJson(result, new TypeToken<List<WatchDataDatyBean>>() {
//                    }.getType());
//                    Log.e("999999999", "----222---" + list.get(0).getRtc() + "--" + list.get(0).getRtc());
                    posterStep.setText(String.valueOf(day.get(0).getStepNumber()));
                    posterTimer.setText(day.get(0).getRtc());
                }
            }

        };
        CommonSubscriber cc = new CommonSubscriber(sb, this);
        OkHttpObservable.getInstance().getData(cc, URLs.HTTPs + URLs.GET_WATCH_DATA_DATA, jsonObect.toString());
    }


    int[] imageNumber = {R.mipmap.shape_poster_one,R.mipmap.shape_poster_two,R.mipmap.shape_poster_three};//shape_poster_two

    private void addShapeData() {
        viewList = new ArrayList<>();

        for (int i = 0; i < imageNumber.length; i++) {
            ImageView viewPoint = new ImageView(this);
            viewPoint.setPadding(3, 0, 3, 0);
            viewPoint.setImageDrawable(getResources().getDrawable(R.mipmap.poster_unselect));
            if (i == 0) {
                viewPoint.setImageDrawable(getResources().getDrawable(R.mipmap.poster_select));
            }
            posterLiner.addView(viewPoint);
            View view = new View(this);//shape_poster_one
            view.setBackground(getResources().getDrawable(imageNumber[i]));
//            View view = LayoutInflater.from(this).inflate(R.layout.shape_one_item, null, false);
            viewList.add(view);
        }
        shapePoster.setAdapter(new PageViewAdapter(viewList));
        shapePoster.addOnPageChangeListener(new PageLister());
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            closeLoadingDialog();
            if (msg.what == 10001) {
                doShareClick();
                mHandler.removeMessages(10001);
                lineSharpePoster.setVisibility(View.VISIBLE);
                recImageLogo.setVisibility(View.GONE);
                shapePoster.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                btnShapePoster.setEnabled(true);
                posterMain.postInvalidate();
            }
            return false;
        }
    });

    @OnClick(R.id.btn_shape_poster)
    public void onViewClicked(View view) {
        if (isok) {
            lineSharpePoster.setVisibility(View.GONE);
            recImageLogo.setVisibility(View.VISIBLE);
            shapePoster.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            btnShapePoster.setEnabled(false);
            posterMain.postInvalidate();
            showLoadingDialog(getResources().getString(R.string.dlog));
            mHandler.sendEmptyMessageDelayed(10001, 3000);
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.dlog));
            getUserInfoData();
            getSportDataForServer();
        }
    }


    //分享
    private void doShareClick() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
        ScreenShot.shoot(SharePosterActivity.this, new File(filePath));
        Common.showShare(this, null, false, filePath);
    }


    class PageViewAdapter extends PagerAdapter {
        private List<View> viewList;

        public PageViewAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    private class PageLister implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            settingPoint(position);
        }

        private void settingPoint(int position) {
            for (int j = 0; j < viewList.size(); j++) {
                ImageView childAt1 = (ImageView) posterLiner.getChildAt(j);
                childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.poster_unselect));
            }
            ImageView childAt = (ImageView) posterLiner.getChildAt(position);
            childAt.setImageDrawable(getResources().getDrawable(R.mipmap.poster_select));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
