package com.example.bozhilun.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.BloodpressureTestActivity;
import com.example.bozhilun.android.activity.DataReportItemActivity;
import com.example.bozhilun.android.activity.DataReportItemSleepActivity;
import com.example.bozhilun.android.activity.HeartRateTestActivity;
import com.example.bozhilun.android.activity.HeathActivity;
import com.example.bozhilun.android.activity.OxygenTsetActivity;
import com.example.bozhilun.android.adpter.DragAdapter;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.StepBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.coverflow.ScrollGridLayoutManager;
import com.example.bozhilun.android.helper.ItemTouchHelperCallback;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.widget.AnimTextView;
import com.example.bozhilun.android.widget.MagicProgressCircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.bozhilun.android.MyApp.getApplication;


/**
 * Created by thinkpad on 2017/3/6.
 */

public class NewRecordFragment extends BaseFragment {

    @BindView(R.id.demo_mpc)
    MagicProgressCircle demoMpc;
    @BindView(R.id.step_tv)
    AnimTextView stepTv;
    @BindView(R.id.circle_km)
    MagicProgressCircle circleKm;
    @BindView(R.id.licheng_tv)
    TextView lichengTv;
    @BindView(R.id.circle_kaluli)
    MagicProgressCircle circleKaluli;
    @BindView(R.id.kaluli_tv)
    AnimTextView kaluliTv;
    @BindView(R.id.rv_drag_layout)
    RecyclerView myRecyclerView;

    private List<HashMap<String, Object>> dataSourceList;
    private LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper helper;
    private DragAdapter dragAdapter;
    private boolean isAnimActive;

    public String mDeviceName, mDeviceAddress, userID;//蓝牙名字和地址

    @Override
    protected void initViews() {

        try {
            if (null != SharedPreferencesUtils.readObject(getActivity(), "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(getActivity(), "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(getActivity(), "mylanmac");//蓝牙的mac
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
            }
            if (null != SharedPreferencesUtils.readObject(getActivity(), "userId")) {
                userID = (String) SharedPreferencesUtils.readObject(getActivity(), "userId");
            } else {
                userID = Common.customer_id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
//      getDataSql();

    }

    @OnClick(R.id.demo_mpc)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.demo_mpc://步数详情
                startActivity(new Intent(getActivity(), DataReportItemActivity.class));
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dataSourceList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataSql();


    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_new_record;
    }

    public void getDataSql() {
        if (!TextUtils.isEmpty(MyCommandManager.ADDRESS) && !TextUtils.isEmpty(Common.customer_id)) {
           /* List<StepBean> list = MyApp.getApplication().getDaoSession().getStepBeanDao().queryBuilder().
                    where(StepBeanDao.Properties.DeviceCode.eq(MyCommandManager.ADDRESS)
                            , StepBeanDao.Properties.UserId.eq(B18iCommon.customer_id)).list();
            if (list.size() > 0 && list != null) {
                StepBean stepBean = list.get(0);
                updateUI(stepBean);//刷新步数
            }*/
            try {
                StepBean stepBean = new StepBean();
                if (null != SharedPreferencesUtils.readObject(getActivity(), "ALLSteps") && null != SharedPreferencesUtils.readObject(getActivity(), "ALLDistance")) {
                    stepBean.setStepNumber(Integer.valueOf(SharedPreferencesUtils.readObject(getActivity(), "ALLSteps").toString()));
                    stepBean.setDistance(SharedPreferencesUtils.readObject(getActivity(), "ALLDistance").toString());
                    stepBean.setCalories(Integer.valueOf(SharedPreferencesUtils.readObject(getActivity(), "ALLCalories").toString()));

                   Log.e("NewRecordFragment","-----"+SharedPreferencesUtils.readObject(getActivity(), "ALLSteps")+"--"+SharedPreferencesUtils.readObject(getActivity(), "ALLDistance")+"--"+
                           SharedPreferencesUtils.readObject(getActivity(), "ALLCalories"));
                    updateUI(stepBean);//刷新步数
                }


            } catch (Exception E) {
                E.printStackTrace();
            }

        }
    }


    public void updateUI(StepBean stepBean) {
        Log.e("NewRecordFragment","------stepBean------"+stepBean.getDeviceCode()+"---step--"+stepBean.getDistance()+"--"+stepBean.getStepNumber()+"--"+stepBean.getCalories());
        //计算10000的卡路里和里程 ,默认的目标步数
        String daily_number_ofsteps_default = (String) SharedPreferencesUtils.getParam(getActivity(), SharedPreferencesUtils.DAILY_NUMBER_OFSTEPS_DEFAULT, "");
        Log.e("NewRecordFragment", "------daily_number_ofsteps_default------" + daily_number_ofsteps_default);
        String jieguo = daily_number_ofsteps_default.replace(getApplication().getResources().getString(R.string.steps), "").trim(); //目标步数
        Log.e("NewRecordFragment", "------NewRecordFragment---" + jieguo);
        int set_step;
        if (!TextUtils.isEmpty(jieguo)) {
            set_step = Integer.valueOf(jieguo);
        } else {
            set_step = 10000;
        }
        float daily_number_ofsteps = Float.valueOf(set_step);
        //身高
        String heithg = (String) SharedPreferencesUtils.getParam(getActivity(), "userheight", "");
        Log.e("NewRecordFragment", "------daily_number_ofsteps---" + daily_number_ofsteps + "--heithg--" + heithg);
        //路程距离，千米
        float tenThousandDistance = Float.valueOf(stepBean.getDistance(Integer.valueOf(heithg), set_step));
        Log.e("NewRecordFragment","---------tenThousandDistance----"+tenThousandDistance);
        //卡里路 千卡
        float tenThousandCalories = Float.valueOf(stepBean.getCalories("" + tenThousandDistance));
        //步数的百分比，用于显示步数的圆比例
        float stepPercentage = stepBean.getStepNumber() / daily_number_ofsteps;
        Log.e("NewRecordFragment","----步数圆百分比----"+stepPercentage+"-------"+stepBean.getStepNumber());
        //距离的百分比,用于显示距离的圆的比例
        float distancePercentage = Float.valueOf(stepBean.getDistance()) / tenThousandDistance;
        //卡里路的百分比，用于显示卡里路的圆的比例
        float caloriesPercentage = stepBean.getCalories() / tenThousandCalories;
        Log.e("NewRecordFragment","----步数圆百分比+步数----"+stepPercentage+"|"+stepBean.getStepNumber()
                +"----距离的百分比+距离---"+distancePercentage+"|"+stepBean.getDistance()+"-----卡里路的百分比+卡里路--"+caloriesPercentage+"|"+stepBean.getCalories());
        updateUiAnim(stepPercentage, distancePercentage, caloriesPercentage, stepBean);
//        }
    }

    private void updateUiAnim(float stepPercentage, float distancePercentage, float caloriesPercentage, StepBean stepBean) {
        AnimatorSet set = new AnimatorSet();
        if ("0.0".equals(stepBean.getDistance())) {
            set.playTogether(
                    ObjectAnimator.ofFloat(demoMpc, "percent", 0, stepPercentage),  //用于显示步数圆的比例
                    ObjectAnimator.ofFloat(circleKm, "percent", 0, distancePercentage),     //用于显示距离元的比例
                    ObjectAnimator.ofFloat(circleKaluli, "percent", 0, caloriesPercentage),     //用于显示卡里路的圆的比例
                    ObjectAnimator.ofInt(stepTv, "progress", 0, stepBean.getStepNumber()));     //步数的数字显示
        } else {
            set.playTogether(
                    ObjectAnimator.ofFloat(demoMpc, "percent", 0, stepPercentage),  //用于显示步数圆的比例
                    ObjectAnimator.ofFloat(circleKm, "percent", 0, distancePercentage), //用于显示距离元的比例
                    ObjectAnimator.ofFloat(circleKaluli, "percent", 0, caloriesPercentage), //用于显示卡里路的圆的比例
                    ObjectAnimator.ofInt(stepTv, "progress", 0, stepBean.getStepNumber()),  //步数
                    ObjectAnimator.ofInt(kaluliTv, "progress", 0, stepBean.getCalories())); //卡里路
            lichengTv.setText(String.valueOf(stepBean.getDistance()));  //距离
        }
        set.setDuration(600);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimActive = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimActive = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
    }

    private void init() {

        if (null != DragAdapter.bmList) {
            dragAdapter = new DragAdapter(getActivity(), DragAdapter.bmList);
        } else {
            dataSourceList = new ArrayList<HashMap<String, Object>>();
            //要判断设备类型
            if ("B15P".equals(mDeviceName)) {
                for (int i = 0; i < 4; i++) {
                    HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
                    if (i == 0) {
                        itemHashMap.put("item_image", R.mipmap.jiankangcelaing);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.jiankangceshi));
                    } else if (i == 1) {
                        itemHashMap.put("item_image", R.mipmap.xinlv0);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.heartbeats_per_minute));
                    } else if (i == 2) {
                        itemHashMap.put("item_image", R.mipmap.shuimain);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.sleep_monitoring));
                    } else if (i == 3) {
                        itemHashMap.put("item_image", R.mipmap.xueyajilu);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.blood));
                    }
                    dataSourceList.add(itemHashMap);
                }
            } else if ("B15S-H".equals(mDeviceName)) {
                if (dataSourceList.size() > 0) {
                    dataSourceList.clear();
                }
                for (int i = 0; i < 2; i++) {
                    HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
                    if (i == 0) {
                        itemHashMap.put("item_image", R.mipmap.xinlv0);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.heartbeats_per_minute));
                    } else if (i == 1) {
                        itemHashMap.put("item_image", R.mipmap.shuimain);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.sleep_monitoring));
                    }
                    dataSourceList.add(itemHashMap);
                }
            } else if ("B15S".equals(mDeviceName)) {
                for (int i = 0; i < 5; i++) {
                    HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
                    if (i == 0) {
                        itemHashMap.put("item_image", R.mipmap.jiankangcelaing);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.jiankangceshi));
                    } else if (i == 1) {
                        itemHashMap.put("item_image", R.mipmap.xinlv0);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.heartbeats_per_minute));
                    } else if (i == 2) {
                        itemHashMap.put("item_image", R.mipmap.shuimain);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.sleep_monitoring));
                    } else if (i == 3) {
                        itemHashMap.put("item_image", R.mipmap.xueyajilu);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.blood));
                    } else if (i == 4) {
                        itemHashMap.put("item_image", R.mipmap.xueyangluse);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.blood_oxygen));
                    }
                    dataSourceList.add(itemHashMap);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
                    if (i == 0) {
                        itemHashMap.put("item_image", R.mipmap.jiankangcelaing);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.jiankangceshi));
                    } else if (i == 1) {
                        itemHashMap.put("item_image", R.mipmap.xinlv0);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.heartbeats_per_minute));
                    } else if (i == 2) {
                        itemHashMap.put("item_image", R.mipmap.shuimain);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.sleep_monitoring));
                    } else if (i == 3) {
                        itemHashMap.put("item_image", R.mipmap.xueyajilu);
                        itemHashMap.put("item_text", getActivity().getResources().getString(R.string.blood));
                    }
                    dataSourceList.add(itemHashMap);
                }
            }

            dragAdapter = new DragAdapter(getActivity(), dataSourceList);
        }

        /**
         * 实例化helper，附加到RecyclerView上
         */
        //  myRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));//每行两个item6.0冲突
        ScrollGridLayoutManager recyclerViews = new ScrollGridLayoutManager(getActivity(), 2);
        recyclerViews.setScrollEnabled(false);
        myRecyclerView.setLayoutManager(recyclerViews);
        myRecyclerView.setAdapter(dragAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(dragAdapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(myRecyclerView);

        /**
         * 下面是内容展示RecyclerView，
         * 这里主要为了使ScrollView整体滑动以及item能够在ScrollView下实现match_parent
         * 因此并未绑定拖拽事件
         */
        linearLayoutManager = new LinearLayoutManager(getActivity()) {
            /**
             * 返回false禁止RecyclerView竖直滚动
             * @return
             */
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };


        dragAdapter.setOnItemClickListener(new DragAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String tag) {

                // Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
                if (tag.equals(getActivity().getResources().getString(R.string.jiankangceshi))) {//健康测量
                    startActivity(new Intent(getActivity(), HeathActivity.class));
                } else if (tag.equals(getActivity().getResources().getString(R.string.heartbeats_per_minute))) {//心率测量
                    startActivity(new Intent(getActivity(), HeartRateTestActivity.class));
                } else if (tag.equals(getActivity().getResources().getString(R.string.sleep_monitoring))) {//睡眠监测
                    startActivity(new Intent(getActivity(), DataReportItemSleepActivity.class));
                } else if (tag.equals(getActivity().getResources().getString(R.string.blood))) {//血压测量
                    startActivity(new Intent(getActivity(), BloodpressureTestActivity.class));
                } else if (tag.equals(getActivity().getResources().getString(R.string.blood_oxygen))) {//血氧测量
                    startActivity(new Intent(getActivity(), OxygenTsetActivity.class));
                }

               /*     try{
                        if()
                    }catch (Exception E){E.printStackTrace();} */


            }
        });

    }

}
