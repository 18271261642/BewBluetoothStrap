package com.example.bozhilun.android.activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.B18IHomeActivity;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.HomeAdapter;
import com.example.bozhilun.android.b15p.B15pHomeActivity;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchHomeActivity;
import com.example.bozhilun.android.siswatch.utils.SharedPreferenceUtil;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 滑动页
 */
public class GuideActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;//启动时就开启来电监听

    private static final int READ_CONTACTS_CODE = 1001;
    private static final int READ_MSG_CODE = 1002;

    @Override
    protected void initViews() {
        //SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya","W06X");
        String isFirst = (String) SharedPreferencesUtils.getParam(GuideActivity.this, "msgfirst", "");
        if (WatchUtils.isEmpty(isFirst)) {
            SharedPreferencesUtils.saveObject(GuideActivity.this, "weixinmsg", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "msg", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "qqmsg", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "Viber", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "Twitteraa", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "facebook", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "Whatsapp", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "Instagrambutton", "1");
            SharedPreferencesUtils.saveObject(GuideActivity.this, "laidian", "0");
            SharedPreferencesUtils.setParam(MyApp.getApplication(), "laidianphone", "off");

            //H8同步时间标识
            SharedPreferencesUtils.setParam(GuideActivity.this,"h8Sycndate","");
        }
        //初始化H8消息提醒功能
        SharedPreferencesUtils.setParam(GuideActivity.this, "msgfirst", "isfirst");

        //申请读取通讯录的权限
        AndPermission.with(GuideActivity.this)
                .requestCode(READ_CONTACTS_CODE)
                .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.WRITE_SETTINGS)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // AndPermission.rationaleDialog(GuideActivity.this,rationale).show();
                    }
                })
                .callback(permissionListener)
                .start();


        String isFirstData = (String) SharedPreferencesUtils.getParam(GuideActivity.this, "isFirst", "");
        if (!WatchUtils.isEmpty(isFirstData) && "on".equals(isFirstData)) {   //不是第一次进入
            try {
                if (null != SharedPreferencesUtils.readObject(GuideActivity.this, "userInfo")) {
                    String userInfo = SharedPreferencesUtils.readObject(GuideActivity.this, "userInfo").toString();
                    System.out.print("userInfo" + userInfo);
                    Gson gson = new Gson();
                    BlueUser userInfos = gson.fromJson(userInfo, BlueUser.class);
                    Common.userInfo = userInfos;
                    Common.customer_id = userInfos.getUserId();
                    // MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfos);

                }
                //判断有没有登录
                if (null != SharedPreferencesUtils.readObject(GuideActivity.this, "userId")) {
                    Log.e("GuideActivity", "--------蓝牙---" + SharedPreferencesUtils.readObject(GuideActivity.this, "mylanya"));
                    String btooth = (String) SharedPreferencesUtils.readObject(GuideActivity.this, "mylanya");
                    if (!WatchUtils.isEmpty(btooth)) {
                        if ("bozlun".equals(btooth)) {    //H8 手表
                            startActivity(new Intent(GuideActivity.this, WatchHomeActivity.class));
                        } else if ("B18I".equals(btooth)) {   //B18I手表
                            startActivity(new Intent(GuideActivity.this, B18IHomeActivity.class));
                        } else if ("W06X".equals(btooth)) {   // H9 手表
                            startActivity(new Intent(GuideActivity.this, H9HomeActivity.class));
                        } else if ("B15P".equals(btooth)) {    //B15P手环
                            startActivity(new Intent(GuideActivity.this, B15pHomeActivity.class));
                        }
// else if("B15S".equals(btooth)){    //B15S手环
//                            startActivity(new Intent(GuideActivity.this, MainActivity.class));
//                        }else if("B15S-H".equals(btooth)){  //B15S-H手环
//                            startActivity(new Intent(GuideActivity.this, MainActivity.class));
//                        }
                        else {
                            startActivity(new Intent(GuideActivity.this, NewSearchActivity.class));
                        }
                    } else {
                        startActivity(new Intent(GuideActivity.this, NewSearchActivity.class));
                    }
                    finish();
                } else {
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<View> pageList = createPageList();
        HomeAdapter adapter = new HomeAdapter();
        adapter.setData(pageList);
        viewPager.setAdapter(adapter);


    }

    @NonNull
    private List<View> createPageList() {
        List<View> pageList = new ArrayList<>();
        pageList.add(createPageView(R.mipmap.yindao_01, 0));
        pageList.add(createPageView(R.mipmap.yindao_02, 1));
        pageList.add(createPageView(R.mipmap.yindao_03, 2));
        return pageList;
    }

    @NonNull
    private View createPageView(int drawable, int index) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.banner_guide, null);
        RelativeLayout activity_guide = (RelativeLayout) contentView.findViewById(R.id.activity_guide);
        TextView txt_one = (TextView) contentView.findViewById(R.id.guide_txt_1);
        TextView txt_two = (TextView) contentView.findViewById(R.id.guide_txt_2);
        String[] txt_banner_1 = this.getResources().getStringArray(R.array.banner_txt_one);
        String[] txt_banner_2 = this.getResources().getStringArray(R.array.banner_txt_two);
        txt_one.setText(txt_banner_1[index]);
        //txt_one.setLetterSpacing(5);
        txt_two.setText(txt_banner_2[index]);
        //txt_two.setLetterSpacing(5);
        if (index == 2) {
            Button go_home = (Button) contentView.findViewById(R.id.go_home);
            go_home.setVisibility(View.VISIBLE);
            go_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferencesUtils.setParam(GuideActivity.this, "isFirst", "on");
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
        activity_guide.setBackgroundResource(drawable);
        return contentView;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.transparent;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_guide;
    }

    @OnClick(R.id.viewPager)
    public void onClick() {
        SharedPreferencesUtils.setParam(GuideActivity.this, "isFirst", "on");
        startActivity(new Intent(GuideActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * 权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case READ_CONTACTS_CODE:    //获取读取通讯录的权限了

                    break;
                case READ_MSG_CODE: //获取读取短信的权限了

                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case READ_CONTACTS_CODE:    //没有获取读取通讯录的权限了

                    break;
                case READ_MSG_CODE: //没有获取读取短信的权限了

                    break;
            }
        }
    };
}
