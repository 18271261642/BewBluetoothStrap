package com.example.bozhilun.android.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.imagepicker.PickerBuilder;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.ImageTool;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.bozhilun.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/8.
 * 个人信息
 */

public class MyPersonalActivity extends BaseActivity {

    private static final int GET_CAMERA_REQUEST_CODE = 1001;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.mine_logo_iv_personal)
    CircleImageView mineLogoIv;
    @BindView(R.id.nickname_tv)
    TextView nicknameTv;
    @BindView(R.id.sex_tv)
    TextView sexTv;
    @BindView(R.id.height_tv)
    TextView heightTv;
    @BindView(R.id.weight_tv)
    TextView weightTv;
    @BindView(R.id.birthday_tv)
    TextView birthdayTv;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;
    @BindView(R.id.personal_avatar_relayout)
    RelativeLayout personalAvatarRelayout;
    @BindView(R.id.nickname_relayout_personal)
    RelativeLayout nicknameRelayoutPersonal;
    @BindView(R.id.sex_relayout)
    RelativeLayout sexRelayout;
    @BindView(R.id.height_relayout)
    RelativeLayout heightRelayout;
    @BindView(R.id.weight_relayout)
    RelativeLayout weightRelayout;
    @BindView(R.id.birthday_relayout)
    RelativeLayout birthdayRelayout;
    private String nickName, sex, height, weight, birthday, flag;
    private DialogSubscriber dialogSubscriber;
    private boolean isSubmit;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences share = getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        nicknameTv.setText(name);

    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        tvTitle.setText(R.string.personal_info);

        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        for (int i = 120; i < 231; i++) {
            heightList.add(i + " cm");
        }
        for (int i = 20; i < 200; i++) {
            weightList.add(i + " kg");
        }

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {

                Log.e("MyPerson", "-------result--" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.print("BBBB" + jsonObject.toString());
                    String resultCode = jsonObject.getString("resultCode");
                    System.out.print("resultCode" + resultCode);
                    Log.e("MyPerson", "----resultCode--" + resultCode + "-isSubmit----" + isSubmit);
                    if ("001".equals(resultCode)) {
                        if (isSubmit) {     //正在刷新时表示正在修改提交用户信息
                            if ("sex".equals(flag)) {
                                userInfo.setSex(sex);
                                if ("M".equals(sex)) {
                                    sexTv.setText(getResources().getString(R.string.sex_nan));
                                } else if ("F".equals(sex)) {
                                    sexTv.setText(getResources().getString(R.string.sex_nv));
                                }
                            } else if ("birthday".equals(flag)) {
                                userInfo.setBirthday(birthday);
                                birthdayTv.setText(birthday);
                            } else if ("height".equals(flag)) {
                                userInfo.setHeight(height);
                                heightTv.setText(height);
                            } else if ("weight".equals(flag)) {
                                userInfo.setHeight(weight);
                                weightTv.setText(weight);
                            }
                            MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        } else {
                            String jsonObjectb = jsonObject.getString("userInfo");
                            JSONObject jsonObjectbV = new JSONObject(jsonObjectb);
                            sex = jsonObjectbV.getString("sex").toString();
                            userInfo.setSex(sex);
                            if ("M".equals(sex)) {
                                sexTv.setText(getResources().getString(R.string.sex_nan));
                            } else if ("F".equals(sex)) {
                                sexTv.setText(getResources().getString(R.string.sex_nv));
                            }
                            birthday = jsonObjectbV.getString("birthday").toString();
                            userInfo.setBirthday(birthday);
                            birthdayTv.setText(birthday);


                            height = jsonObjectbV.getString("height").toString();
                            userInfo.setHeight(height);
                            heightTv.setText(height);
                            SharedPreferencesUtils.setParam(MyPersonalActivity.this,"userheight",StringUtils.substringBefore(height, "cm"));

                            weight = jsonObjectbV.getString("weight").toString();
                            userInfo.setHeight(weight);
                            weightTv.setText(weight);

                            nickName = jsonObjectbV.getString("nickName").toString();
                            userInfo.setNickName(nickName);
                            nicknameTv.setText(nickName);
                            String imageUrl = jsonObjectbV.getString("image");
                            if (!WatchUtils.isEmpty(imageUrl)) {
                                SharedPreferencesUtils.saveObject(MyPersonalActivity.this, "Inmageuil", imageUrl);
                                userInfo.setImage(imageUrl);
                                //设置头像
//                                BitmapUtils bitmapUtils = new BitmapUtils(MyPersonalActivity.this);
//                                bitmapUtils.display(mineLogoIv, imageUrl);
                                Glide.with(MyPersonalActivity.this).load(imageUrl).bitmapTransform(new CropCircleTransformation(MyPersonalActivity.this)).into(mineLogoIv);

                                MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                                EventBus.getDefault().post(new MessageEvent("update_data_huangcune"));
                            } else {
                                EventBus.getDefault().post(new MessageEvent("update_data_huangcune"));
                            }

                            //在此处同步用户数据至手环
                            //  MyCommandManager.SynchronousPersonalInformation(); //血压
                            SharedPreferences mySharedPre = getSharedPreferences("shousuoya", Activity.MODE_PRIVATE);
                            SharedPreferences denglu = getSharedPreferences("shuzhangya", Activity.MODE_PRIVATE);
                            //收缩压
                            String shrinkBlood = (String) SharedPreferencesUtils.getParam(MyPersonalActivity.this, "shrinkBlood", "");
                            //舒张压
                            String diastolicBlood = (String) SharedPreferencesUtils.getParam(MyPersonalActivity.this, "diastolicBlood", "");
                            if (!WatchUtils.isEmpty(shrinkBlood) && !WatchUtils.isEmpty(diastolicBlood)) {

                            } else {
                                shrinkBlood = String.valueOf(120);
                                diastolicBlood = String.valueOf(80);
                            }


                            Log.e("MYYY", "----收缩压--" + shrinkBlood + "---舒张压--" + diastolicBlood + "----年龄---" + userInfo.getBirthday() + "--身高--" + userInfo.getHeight() + height
                                    + "---体重---" + userInfo.getWeight() + "---" + WatchUtils.getStepLong(Integer.valueOf(StringUtils.substringBefore(height, "cm").trim())) + "---" + StringUtils.substringBefore(height, "cm"));
                            String ageStr = userInfo.getBirthday();
                            Log.e("MYYY", "------年龄----" + WatchUtils.getAgeFromBirthTime(ageStr));

                            Map<String, Object> maps = new HashMap<>();
                            maps.put("lanyaneme", SharedPreferencesUtils.readObject(MyPersonalActivity.this, "mylanya"));
                            maps.put("age", WatchUtils.getAgeFromBirthTime(ageStr));   //年龄
                            maps.put("height", StringUtils.substringBefore(height, "cm"));
                            maps.put("wight", StringUtils.substringBefore(userInfo.getWeight(), "kg"));
                            maps.put("systolicpressure", shrinkBlood);
                            maps.put("diastolicpressure", diastolicBlood);
                            Log.e("MYYY", "-----步长---" + WatchUtils.getStepLong(Integer.valueOf(StringUtils.substringBefore(height, "cm").trim())));
                            Log.e("MYYY", "------参数---" + maps.toString() + "---" + SharedPreferencesUtils.getParam(MyPersonalActivity.this, SharedPreferencesUtils.DAILY_NUMBER_OFSTEPS_DEFAULT, ""));
                            double stepLong = WatchUtils.getStepLong(Integer.valueOf(StringUtils.substringBefore(height, "cm").trim()));


                        }

                    } else {
                        ToastUtil.showShort(MyPersonalActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        shuaxin();
    }

    /**
     * 刷新所以得数据（名字和头像）
     */
    public void shuaxin() {
        isSubmit = false;
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId"));
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getUserInfo, mapjson);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personal_info;
    }

    @OnClick({R.id.personal_avatar_relayout, R.id.nickname_relayout_personal, R.id.sex_relayout, R.id.height_relayout, R.id.weight_relayout, R.id.birthday_relayout})
    public void onClick(View view) {
        String userId = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId");
        SharedPreferences share = getSharedPreferences("Login_id", 0);
        int isoff = share.getInt("id",0);
        if (!WatchUtils.isEmpty(userId)) {
            if (userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {  //判断是否是游客身份，如果是游客身份无权限修改信息
                ToastUtil.showToast(MyPersonalActivity.this,MyPersonalActivity.this.getResources().getString(R.string.noright));
            }else{
                if(isoff == 1){
                    ToastUtil.showToast(MyPersonalActivity.this,"第三方登录无法修改用户信息");
                }else{
                    switch (view.getId()) {
                        case R.id.personal_avatar_relayout:
                            if(AndPermission.hasPermission(MyPersonalActivity.this,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                chooseImgForUserHead(); //选择图片来源
                            }else{
                                AndPermission.with(MyPersonalActivity.this)
                                        .requestCode(GET_CAMERA_REQUEST_CODE)
                                        .permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .rationale(new RationaleListener() {
                                            @Override
                                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                                AndPermission.rationaleDialog(MyPersonalActivity.this,rationale).show();
                                            }
                                        }).callback(permissionListener)
                                        .start();
                            }
                            break;
                        case R.id.nickname_relayout_personal:
                            startActivity(new Intent(MyPersonalActivity.this, ModifyNickNameActivity.class));
                            break;
                        case R.id.sex_relayout:
                            showSexDialog();
                            break;
                        case R.id.height_relayout:
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    flag = "height";
                                    height = profession.substring(0, 3);
                                    modifyPersonData(height);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(heightList) //min year in loop
                                    .dateChose("170 cm") // date chose when init popwindow
                                    .build();
                            professionPopWin.showPopWin(MyPersonalActivity.this);
                            break;
                        case R.id.weight_relayout:
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    flag = "weight";
                                    weight = profession.substring(0, 3);
                                    modifyPersonData(weight);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(weightList) //min year in loop
                                    .dateChose("60 kg") // date chose when init popwindow
                                    .build();
                            weightPopWin.showPopWin(MyPersonalActivity.this);
                            break;
                        case R.id.birthday_relayout:
                            DatePick pickerPopWin = new DatePick.Builder(MyPersonalActivity.this, new DatePick.OnDatePickedListener() {
                                @Override
                                public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                    flag = "birthday";
                                    birthday = dateDesc;
                                    modifyPersonData(dateDesc);//
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .minYear(1950) //min year in loop
                                    .maxYear(2020) // max year in loop
                                    .dateChose("2000-06-15") // date chose when init popwindow
                                    .build();
                            pickerPopWin.showPopWin(MyPersonalActivity.this);
                            break;
                    }
                }

            }
        }

    }

    //选择图片
    private void chooseImgForUserHead() {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyPersonalActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        switch (item.getItemId()) {
                            case R.id.take_camera:
                                getImage(PickerBuilder.SELECT_FROM_CAMERA);
                                break;
                            case R.id.take_Album:
                                getImage(PickerBuilder.SELECT_FROM_GALLERY);
                                break;
                            case R.id.cancle:
                                break;
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showSexDialog() {
        new MaterialDialog.Builder(MyPersonalActivity.this)
                .title(R.string.select_sex)
                .items(R.array.select_sex)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //0表示男,1表示女
                        if (which == 0) {
                            sex = "M";
                        } else {
                            sex = "F";
                        }
                        flag = "sex";
                        modifyPersonData(sex);
                        return true;
                    }
                })
                .positiveText(R.string.select)
                .show();
    }

    private void getImage(int type) {
        new PickerBuilder(MyPersonalActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        //设置头像
//                        BitmapUtils bitmapUtils = new BitmapUtils(MyPersonalActivity.this);
//                        bitmapUtils.display(mineLogoIv, String.valueOf(imageUri));
                        Glide.with(MyPersonalActivity.this).
                                load(imageUri).bitmapTransform(new CropCircleTransformation(MyPersonalActivity.this)).into(mineLogoIv);


                        uploadPic(ImageTool.getRealFilePath(MyPersonalActivity.this, imageUri));
                    }
                })
                .setImageName("headImg")
                .setImageFolderName("NewBluetoothStrap")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                    @Override
                    public void onPermissionRefused() {
                    }
                })
                .start();
    }

    //上传头像图片
    private void uploadPic(String filePath) {
        isSubmit = false;
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put("image", ImageTool.GetImageStr(filePath));
        String mapjson = gson.toJson(map);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);
    }

    //完善用户资料
    private void modifyPersonData(String val) {
        isSubmit = true;
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put(flag, val);
        String mapjson = gson.toJson(map);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        AndPermission.with(MyPersonalActivity.this)
                .requestCode(GET_CAMERA_REQUEST_CODE)
                .permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MyPersonalActivity.this,rationale).show();
                    }
                }).callback(permissionListener)
                .start();
    }

    /**
     * 申请权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode){
                case GET_CAMERA_REQUEST_CODE:

                    break;
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case GET_CAMERA_REQUEST_CODE:
                    if(AndPermission.hasAlwaysDeniedPermission(MyPersonalActivity.this,deniedPermissions)){
                        AndPermission.defaultSettingDialog(MyPersonalActivity.this)
                                .setTitle(getResources().getString(R.string.prompt))
                                .setMessage("请求打开相机权限失败,无法打开相机,是否手动打开相机权限?")
                                .setPositiveButton(getResources().getString(R.string.confirm))
                                .show();
                    }
                    break;
            }

        }
    };
}
