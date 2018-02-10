package com.example.bozhilun.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.StepBeanDao;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.activity.DeviceActivity;
import com.example.bozhilun.android.activity.SearchDeviceActivity;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.B15PSleepBean;
import com.example.bozhilun.android.bean.B15PSleepHeartRateStepBean;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.SleepCurveBean;
import com.example.bozhilun.android.bean.StepBean;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.Sleeptime;
import com.example.bozhilun.android.fragment.DataReportFragment;
import com.example.bozhilun.android.fragment.MineFragment;
import com.example.bozhilun.android.fragment.NewRecordFragment;
import com.example.bozhilun.android.fragment.RunningFragment;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.AnimationUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.bozhilun.android.util.Common.userInfo;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_ACCESS_FINE_LOCATION_CODE = 1001;

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.record_bottomsheet)
    BottomSheetLayout recordBottomsheet;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar_record_linear)
    RelativeLayout toolbarRecordLinear;
//    @BindView(R.id.duankai_img)
//    ImageView duankaiImg;

    @BindView(R.id.duankai_tv)
    TextView duankaiTv;

    @BindView(R.id.mian_battey)
    ImageView battey;

    /*   @BindView(R.id.boom_btn)
       BoomMenuButton boomMenuButton;*/
    private List<Fragment> fragments;
    private CommonSubscriber commonSubscriber;
    private B15PSleepBean b15PSleepBean;
    public String mDeviceName, mDeviceAddress, userID;//蓝牙名字和地址
    boolean isshuangc = false;
    boolean starttime = false;//定时任务


    public static boolean Batterylevel = false;//是否显示电池

    private int sexNum = 0; //性别 0-女；1-男
    private String newHeight = "170";   //身高
    private String userWeight = "60"; //体重

    private Handler mHandler;
    private Handler bHandler;
    private int MYCONNECTIONSTATE = 0;

    private boolean gotorefase = false;//刷新
    Menu myMenu;
    private int mybattay;
    Boolean istankuan = false;//有没有弹框
    MaterialDialog myMaterialDialog;

    public int getMybattay() {
        return mybattay;
    }

    public void setMybattay(int mybattay) {
        this.mybattay = mybattay;
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void initViews() {

        //查询当前的app版本号(如果有网络就查询下当前的版本号)
       /* boolean isLine= ConnectManages.isNetworkAvailable(MainActivity.this);
        if(isLine){ GetVersion();}*/
        GetVersion();
        EventBus.getDefault().register(this);
        //设置默认的目标步数
        SharedPreferencesUtils.setParam(MainActivity.this, SharedPreferencesUtils.DAILY_NUMBER_OFSTEPS_DEFAULT, 10000+getApplication().getResources().getString(R.string.steps));
        FinfdevicesNameAndadress();
        bHandler = new Handler();//十分钟刷新数据
        bHandler.post(new Runnable() {
            @Override
            public void run() {
                bHandler.postDelayed(this, 600 * 1000);//十分钟600000
                //Toast.makeText(MainActivity.this,"刷新了",Toast.LENGTH_SHORT).show();
                if (BluetoothLeService.isService && gotorefase) {
                    isshuangc = false;
                    MyCommandManager.ReadSteps(MyCommandManager.DEVICENAME);//同步步数
                }
            }
        });
        if (null == mHandler) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    MyCommandManager.DEVICENAME = mDeviceName;
                    MyCommandManager.ADDRESS = mDeviceAddress;
                    //延迟更新
                    if ("B15P".equals(mDeviceName)) {
                        EventBus.getDefault().post(new MessageEvent("update_data_service"));
                    }

                }
            };
        }
        fragments = new ArrayList<>();
        fragments.add(new NewRecordFragment());//记录
        fragments.add(new RunningFragment());//运动
        fragments.add(new DataReportFragment());//数据
        fragments.add(new MineFragment());//个人资料
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        //给ViewPager设置适配器
        viewPager.setAdapter(fragmentPagerAdapter);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                toolbarRecordLinear.setVisibility(View.GONE);
                switch (tabId) {
                    case R.id.tab_home:
                        initToolbar();
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:
                        init2Toolbar();
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_data:
                        init3Toolbar();
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_my:
                        init4Toolbar();
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });

        //检查读取位置权限
        AndPermission.with(MainActivity.this)
                .requestCode(REQUEST_ACCESS_FINE_LOCATION_CODE)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .rationale(new RationaleListener() {    //显示提示框
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this,rationale).show();
                    }
                })
                .callback(permissionListener)
                .start();


    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 有权限
            } else {
                // 再请求一次
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    private void initToolbar() {
        tvTitle.setText(R.string.recording);
        tvTitle.setClickable(false);
        tvTitle.setCompoundDrawables(null, null, null, null);
        toolbarRecordLinear.setVisibility(View.VISIBLE);
        toolbar.inflateMenu(R.menu.menu_record);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (Common.isFastClick()) {
                    showShare();
                }
                return true;
            }
        });
    }

    private void init4Toolbar() {
        if (null != myMenu) {
            myMenu.clear();
        }
        tvTitle.setCompoundDrawables(null, null, null, null);
        tvTitle.setClickable(false);
        tvTitle.setText(R.string.mine);
    }

    private void init3Toolbar() {
        if (null != myMenu) {
            myMenu.clear();
        }
        tvTitle.setClickable(false);
        tvTitle.setCompoundDrawables(null, null, null, null);
        tvTitle.setText(R.string.data);
    }

    private void init2Toolbar() {
        if (null != myMenu) {
            myMenu.clear();
        }

        tvTitle.setText(R.string.running);
        tvTitle.setClickable(true);
        Drawable drawable = getResources().getDrawable(R.mipmap.jiantou1);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tvTitle.setCompoundDrawables(null, null, drawable, null);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MainActivity.this).title(R.string.select_running_mode).items(R.array.select_running_mode).itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        //0表示户外,1表骑行
                        if (which == 0) {
                            SharedPreferencesUtils.saveObject(MainActivity.this, "type", "0");
                        } else {
                            SharedPreferencesUtils.saveObject(MainActivity.this, "type", "1");
                        }
                        return false;
                    }
                }).positiveText(R.string.select).show();
            }
        });
    }


    @Override
    protected void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(" ");
    }

    /**
     * 电池点击事件
     */
    @OnClick(R.id.toolbar_record_linear)
    public void onClick() {
        if (null == MyCommandManager.DEVICENAME) {
            startActivity(new Intent(MainActivity.this, SearchDeviceActivity.class));//收索设备
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, DeviceActivity.class));//mine_dev_image
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.myMenu = menu;
        MainActivity.this.getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showShare();
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"------onResume----");
        FinfdevicesNameAndadress();
        if (null != mDeviceAddress) {
            //蓝牙的的状态是连接的
            if (BluetoothLeService.isService) {
                AnimationUtils.stopFlick(duankaiTv);
               // duankaiImg.setImageResource(R.mipmap.lianjie);
                duankaiTv.setText("connect");
                duankaiTv.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.white));
                mHandler.removeCallbacksAndMessages(null);
                MyCommandManager.ReadSteps(MyCommandManager.DEVICENAME);//同步步数
                if (0 != getMybattay()) {
                    setbatter(getMybattay());
                }
            } else {
                Batterylevel = false;
               // duankaiImg.setImageResource(R.mipmap.duankai);
                duankaiTv.setText("disConn");
                duankaiTv.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.red));
                AnimationUtils.startFlick(duankaiTv);
                battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dianchihui));

            }
        }
    }


    /**
     * 数据处理
     *
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        Log.e(TAG, "-----main--resutlt---" + result);
        if ("back_step".equals(result)) {
            if (isshuangc == false) {
                isshuangc = true;
                //上传 步数(活动量)
                if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                    if (BluetoothLeService.isService) {
                        MyCommandManager.DailyActivity(MyCommandManager.DEVICENAME, "1", "0");
                    }
                } else {
                    if (BluetoothLeService.isService) {
                        MyCommandManager.DailyActivity(MyCommandManager.DEVICENAME, "0", "0");
                    }
                    MyLogUtil.i("getStepNumber" + "活动量");
                }
                if (!"0".equals(event.getObject().toString())) {
                    if (!TextUtils.isEmpty(MyCommandManager.ADDRESS) && !TextUtils.isEmpty(Common.customer_id)) {
                        StepBean stepBean = (StepBean) event.getObject();
                        Log.e(TAG,"------stepBean------"+stepBean.getDeviceCode()+"---step--"+stepBean.getDistance()+"--"+stepBean.getStepNumber());


                        ((NewRecordFragment) fragments.get(0)).updateUI(stepBean);
                        upSportData(stepBean);  //上传步数
                        MyLogUtil.i("getStepNumber" + stepBean.getStepNumber());
                        //存本地
                        List<StepBean> list = MyApp.getApplication().getDaoSession().getStepBeanDao().queryBuilder().
                                where(StepBeanDao.Properties.DeviceCode.eq(MyCommandManager.ADDRESS)
                                        , StepBeanDao.Properties.UserId.eq(Common.customer_id)).list();
                        if (list.size() > 0 && list != null) {
                            MyApp.getApplication().getDaoSession().getStepBeanDao().deleteAll();
                        }
                        SharedPreferencesUtils.saveObject(MainActivity.this, "ALLSteps", stepBean.getStepNumber());
                        SharedPreferencesUtils.saveObject(MainActivity.this, "ALLDistance", stepBean.getDistance());
                        SharedPreferencesUtils.saveObject(MainActivity.this, "ALLCalories", stepBean.getCalories());
                        //  MyApp.getApplication().getDaoSession().getStepBeanDao().insertOrReplace(stepBean);//活动量
                    }
                } else {
                    SharedPreferencesUtils.saveObject(MainActivity.this, "ALLSteps", "0");
                    SharedPreferencesUtils.saveObject(MainActivity.this, "ALLDistance", "0");
                    SharedPreferencesUtils.saveObject(MainActivity.this, "ALLCalories", "0");
                }
            }
        } else if ("all_day_acyivity".equals(result)) {
            if ("B15P".equals(MyCommandManager.DEVICENAME)) {
                MyCommandManager.Sleepdata(MyCommandManager.DEVICENAME);//发送睡眠请求
            } else {
                gotorefase = true;
            }
            //电池电量，步数，睡眠，心率  p10分钟  s整点  上传全天活动量
            ArrayList<B15PSleepHeartRateStepBean> b15PSleepHeartRateStepList = (ArrayList<B15PSleepHeartRateStepBean>) event.getObject();
            updateDayAcyivity(b15PSleepHeartRateStepList);
            if (!TextUtils.isEmpty(MyCommandManager.ADDRESS)) {
                MyApp.getApplication().getDaoSession().getB15PSleepHeartRateStepBeanDao().insertOrReplaceInTx(b15PSleepHeartRateStepList);
            }
        } else if ("sleep15s_data_service".equals(result)) {
            //上传 B15s睡眠  先统计在上传
            ArrayList<Sleeptime> sleeptimeList = (ArrayList<Sleeptime>) event.getObject();
            MyLogUtil.i("-sleeptimeList" + sleeptimeList.toString());
            B15PSleepBean b15PSleepBean = getB15PSleepData(sleeptimeList);
            upSleepData((b15PSleepBean));

        } else if ("sleep15p_data_curve".equals(result)) {

            gotorefase = true;
            if (!"0".equals(event.getObject().toString())) {
                SleepCurveBean sleepCurveBean = (SleepCurveBean) event.getObject();
                if (null != sleepCurveBean.getSleepCurveS() && null != b15PSleepBean) {
                    b15PSleepBean.setSleepCurveP(sleepCurveBean.getSleepCurveS());
                    MyApp.getApplication().getDaoSession().getB15PSleepBeanDao().insertOrReplace(b15PSleepBean);
                    upSleepData((b15PSleepBean));
                }
            }
        } else if ("sleep15p_data_service".equals(result)) {
            b15PSleepBean = (B15PSleepBean) event.getObject();//得到睡眠部分数据
        } else if ("readsteps".equals(result)) {
            ((NewRecordFragment) fragments.get(0)).getDataSql();//刷新步数

        } else if ("synchronizationtime".equals(result)) {
            boolean back = (boolean) event.getObject();
            if (back) {
                SharedPreferencesUtils.setParam(MainActivity.this, SharedPreferencesUtils.isLanguageTime + "_" + mDeviceAddress, true);
            }
            /**
             * 这里开始同步数据
             */
        } else if ("update_data_service".equals(result)) {
            AnimationUtils.stopFlick(duankaiTv);//清除动画
           // duankaiImg.setImageResource(R.mipmap.lianjie);
            duankaiTv.setText("connect");
            duankaiTv.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.white));
            Log.e(TAG,"-----BluetoothLeService.isService------"+BluetoothLeService.isService);
            //发送电池电量开始
            if (BluetoothLeService.isService) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //同步时间
                        if (BluetoothLeService.isService) {
                            syncUserInfo(); //同步用户信息
                            MyCommandManager.SynchronizationTime(MyCommandManager.DEVICENAME);
                        }
                        //同步语言
                        String language = MainActivity.this.getResources().getConfiguration().locale.getLanguage();
                        //中文
                        if (language.equals("zh")) {
                            MyCommandManager.LanguageSwitching(MyCommandManager.DEVICENAME, 0);
                        } else {
                            MyCommandManager.LanguageSwitching(MyCommandManager.DEVICENAME, 1);
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                MyCommandManager.Batterylevel(MyCommandManager.DEVICENAME);  //电池电量
                            }
                        }, 2000);
                    }
                }, 2000);
            }

        } else if ("all_day_batterylevel".equals(result)) {
            //设置电量百分比
            String msgv = event.getObject().toString();
            int msg = Integer.valueOf(msgv);
            setMybattay(msg);
            setbatter(getMybattay());
            MyCommandManager.ReadSteps(MyCommandManager.DEVICENAME);//同步步数
//            if (BluetoothLeService.isService) {
//                MyCommandManager.ReadSteps(MyCommandManager.DEVICENAME);//同步步数
//            }
        } else if ("connect_fail".equals(result)) {
           // duankaiImg.setImageResource(R.mipmap.duankai);
            duankaiTv.setText("disConn");
            duankaiTv.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.red));
            AnimationUtils.startFlick(duankaiTv);
            battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dianchihui));
        } else if ("update_service_sussess".equals(result)) {
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
            AnimationUtils.stopFlick(duankaiTv);//清除动画
            duankaiTv.setText("connect");
            duankaiTv.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.white));
           // duankaiImg.setImageResource(R.mipmap.lianjie);
            if (0 != getMybattay()) {//0.00030625
                setbatter(getMybattay());
            }

        } else if ("Findphone".equals(result)) {//b15p查找手机中
            if (istankuan == false) {
                istankuan = true;
                Common.VibratorandMusic(true, MainActivity.this);
                myMaterialDialog = new MaterialDialog.Builder(MainActivity.this)
                        .content(getResources().getString(R.string.turn_off_the_phone)).contentColor(Color.parseColor("#65cce7"))
                        .positiveText(getResources().getString(R.string.confirm))
                        .btnStackedGravity(GravityEnum.CENTER)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                try {
                                    istankuan = false;
                                    //关闭查找功能
                                    MyCommandManager.Findphoneoff();
                                    Common.VibratorandMusic(false, MainActivity.this);
                                    dialog.dismiss();
                                } catch (Exception E) {
                                    E.printStackTrace();
                                }
                            }
                        }).show();

            }
        }
    }


    private void setbatter(int msg) {
        if (0 <= msg && msg <= 20) {
            battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dianchione));
        } else if (20 < msg && msg <= 40) {
            battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dianchitwo));
        } else if (40 < msg && msg <= 80) {
            battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dianchithere));
        } else if (80 < msg && msg <= 100) {
            battey.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.dainchifour));
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"----mainactivity--onPause---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"----mainactivity--onStop---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG,"----mainactivity--onDestroy---");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"----mainactivity--onDestroy---");
        if (null != myMaterialDialog) {
            myMaterialDialog.dismiss();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    //b15p活动量
    private void updateDayAcyivity(ArrayList<B15PSleepHeartRateStepBean> list) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", list);
        if (!"[]".equals(list.toString())) {
            String mapjson = gson.toJson(map);

            MyLogUtil.e("--mapjsonppp-全天心率血压步数参数>" + mapjson);

            commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String result) {
                    try {
                        BluetoothLeService.alldayActivity.clear();
                        JSONObject jsonObject = new JSONObject(result);
                        String loginResult = jsonObject.getString("resultCode");
                        if ("001".equals(loginResult)) {
                            ToastUtil.showToast(MainActivity.this, "上传全天心率血压步数活动量成功");
                        } else {
                            ToastUtil.showToast(MainActivity.this, "上传全天心率血压步数活动量失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, MainActivity.this);
            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
        }

    }


    /**
     * 上传步数
     *
     * @param
     */
    private void upSportData(StepBean stepbean) {
        Gson gson = new Gson();
        String mapjson = gson.toJson(stepbean);
        MyLogUtil.e("responsesd" +"---上传步数参数"+ mapjson);
        commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        ToastUtil.showToast(MainActivity.this, "Number of steps uploaded successfully");
                    } else {
                        //  ToastUtil.showToast(MainActivity.this, "上传步数失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, MainActivity.this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, mapjson);
    }

    /**
     * 版本号
     */
    private void GetVersion() {

        JSONObject map = new JSONObject();
        try {
            map.put("version", getVersionName().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  Gson gson = new Gson();
        String mapjson = gson.toJson(map);*/
        commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e(TAG,"---upate-resut--"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    if ("001".equals(loginResult)) {
                        MyLogUtil.i("loginResult" + loginResult);
                        ToastUtil.showToast(MainActivity.this, "当前版本");
                    } else {
                        ToastUtil.showToast(MainActivity.this, loginResult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, MainActivity.this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getvision, String.valueOf(map));
    }

    /**
     * 当前的app版本号
     *
     * @return
     * @throws Exception
     */
    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 上传睡眠数据
     *
     * @param b15PSleepBean
     */
    private void upSleepData(B15PSleepBean b15PSleepBean) {
        Gson gson = new Gson();
        String mapjson = gson.toJson(b15PSleepBean);
        JSONObject jsonO = new JSONObject();
        try {
            jsonO.put("startTime",b15PSleepBean.getStartTime().toString());
            jsonO.put("endTime",b15PSleepBean.getEndTime());
            jsonO.put("count",b15PSleepBean.getCount()+"");
            jsonO.put("deepLen",b15PSleepBean.getDeepLen());
            jsonO.put("userId",b15PSleepBean.getUserId()+"");
            jsonO.put("shallowLen",b15PSleepBean.getShallowLen()+"");
            jsonO.put("deviceCode",b15PSleepBean.getDeviceCode()+"");
            jsonO.put("sleepQuality",b15PSleepBean.getSleepQuality()+"");
            jsonO.put("sleepCurveS",b15PSleepBean.getSleepCurveS()+"");
            jsonO.put("sleepLen",b15PSleepBean.getSleepLen()+"");
            jsonO.put("sleepCurveP",b15PSleepBean.getSleepCurveP()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyLogUtil.e("b15PSleepBean" +"---上传睡眠数据参数--"+ mapjson+"----"+jsonO.toString());
        commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    Log.e(TAG,"----loginResult---"+loginResult);
                    if ("001".equals(loginResult)) {
                        ToastUtil.showToast(MainActivity.this, "睡眠数据同步成功");
                    } else {
                        ToastUtil.showToast(MainActivity.this, "睡眠数据同步失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, MainActivity.this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSleep, jsonO.toString());
    }


    /**
     * b15s睡眠的   数据解析
     *
     * @param sleeptimes
     * @return
     */
    private B15PSleepBean getB15PSleepData(ArrayList<Sleeptime> sleeptimes) {
        B15PSleepBean b15PSleepBean = new B15PSleepBean();
       // b15PSleepBean.setUserId(B18iCommon.customer_id);
        b15PSleepBean.setUserId((String) SharedPreferencesUtils.readObject(MainActivity.this,"userId"));
        b15PSleepBean.setDeviceCode(mDeviceAddress);
        int totla = 0, deepLen = 0, shallowLen = 0;
        String starTime, endTime;
        Collections.sort(sleeptimes);
        List AAA = new ArrayList();
        for (int i = 0; i < sleeptimes.size(); i++) {
            Sleeptime sleeptime = sleeptimes.get(i);

            if (sleeptime.getType() == 0) {
                totla += sleeptime.getDuration();
                AAA.add(totla);
            } else {
                deepLen += sleeptime.getDuration();
                AAA.add(deepLen);
            }
            shallowLen = totla - deepLen;
            b15PSleepBean.setDeepLen(deepLen);
            b15PSleepBean.setShallowLen(shallowLen);
            b15PSleepBean.setSleepLen(deepLen + shallowLen);

        }
        starTime = sleeptimes.get(0).getStartime();
        endTime = sleeptimes.get(sleeptimes.size() - 1).getStartime();
        b15PSleepBean.setEndTime(starTime);
        b15PSleepBean.setStartTime(endTime);
        String ZONSHU = "", ZONSHU2 = "";
        for (int i = 0; i < AAA.size(); i++) {
            String JISHU = "", OUSHU = "";
            if (i % 2 == 0) {
                //i为偶数
                int DDD = (int) AAA.get(i);
                for (int j = 0; j < DDD / 5; j++) {
                    JISHU = JISHU + "0";
                }
            } else {
                //i为基数
                int EEE = (int) AAA.get(i);
                for (int j = 0; j < EEE / 5; j++) {
                    OUSHU = OUSHU + "1";
                }
            }
            ZONSHU = ZONSHU + JISHU + OUSHU;
        }
        ZONSHU2 = ZONSHU + "00000";//添加结束浅睡
        b15PSleepBean.setSleepCurveS(ZONSHU2);
        ZONSHU = "";
        MyLogUtil.i("AAAloginResult" + b15PSleepBean.toString());
        return b15PSleepBean;
    }

    /**
     * 查看蓝牙的名字和地址
     */
    public void FinfdevicesNameAndadress() {
        try {
            if (null != SharedPreferencesUtils.readObject(MainActivity.this, "mylanya")) {
                mDeviceName = (String) SharedPreferencesUtils.readObject(MainActivity.this, "mylanya");//蓝牙的名字
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(MainActivity.this, "mylanmac");//蓝牙的mac
                MyCommandManager.DEVICENAME = mDeviceName;
                MyCommandManager.ADDRESS = mDeviceAddress;
            } else {
                mDeviceName = MyCommandManager.DEVICENAME;
                mDeviceAddress = MyCommandManager.ADDRESS;
            }
            if (null != SharedPreferencesUtils.readObject(MainActivity.this, "userId")) {
                userID = (String) SharedPreferencesUtils.readObject(MainActivity.this, "userId");
                Common.customer_id = userID;
            } else {
                Common.customer_id = userID;
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    /**
     * 分享
     */
    private void showShare() {
        Date timedf = new Date();
        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String xXXXdf = formatdf.format(timedf);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
        ScreenShot.shoot(MainActivity.this, new File(filePath));
        Common.showShare(MainActivity.this, null, false, filePath);
    }

    /**
     * 权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
    @Override
    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
        switch (requestCode){
            case REQUEST_ACCESS_FINE_LOCATION_CODE:

                break;
        }
    }

    @Override
    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
        switch (requestCode){
            case REQUEST_ACCESS_FINE_LOCATION_CODE:

                break;
        }
        //用户拒绝时提醒用户再次获取
        if(AndPermission.hasAlwaysDeniedPermission(MainActivity.this,deniedPermissions)){
            AndPermission.defaultSettingDialog(MainActivity.this,REQUEST_ACCESS_FINE_LOCATION_CODE).show();
        }
    }
};

private void syncUserInfo(){
    //判断性别
    if(userInfo.getSex().equals("M")){  //男
        sexNum = 1;
    }else{
        sexNum = 0;
    }
    //身高
    String userHeight = userInfo.getHeight();
    if(userHeight.contains("cm")){
        newHeight = userHeight.substring(0,userHeight.length()-2).trim();
    }else{
        newHeight = userHeight.trim();
    }
    //体重
    String weitht = userInfo.getWeight();
    if(weitht.contains("kg")){
        userWeight = StringUtils.substringBefore(weitht,"kg").trim();
    }else{
        userWeight = weitht.trim();
    }
    Log.e("Search","------同步上参数---"+Integer.valueOf(newHeight)+Integer.valueOf(userWeight)+ WatchUtils.getAgeFromBirthTime(userInfo.getBirthday())+8000);
    Map<String,Object> userMap = new HashMap<>();
    userMap.put("lanyaneme",SharedPreferencesUtils.readObject(MainActivity.this,"mylanya"));
    userMap.put("age",WatchUtils.getAgeFromBirthTime(userInfo.getBirthday())+"");
    userMap.put("height",newHeight+"");
    userMap.put("wight",userWeight+"");
    userMap.put("systolicpressure","120"); //收缩压
    userMap.put("diastolicpressure","80");  //舒张压
    userMap.put("step",9000);
    MyCommandManager.SynchronousPersonalInformation(userMap,1,5);
}


}
