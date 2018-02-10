package com.example.bozhilun.android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.imagepicker.PickerBuilder;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.ImageTool;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.widget.SwitchIconView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.bozhilun.android.R.id.man_iconview;
import static com.example.bozhilun.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/4.
 * 我的资料
 */

public class PersonDataActivity extends BaseActivity {

    private static final int GET_OPENCAMERA_CODE = 100;



    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.head_img)
    CircleImageView headImg;
    @BindView(R.id.code_et)
    EditText codeEt;
    @BindView(R.id.brithdayval_tv)
    TextView brithdayvalTv;
    @BindView(R.id.heightval_tv)
    TextView heightvalTv;
    @BindView(R.id.weightval_tv)
    TextView weightvalTv;
    @BindView(R.id.man_iconview)
    SwitchIconView manIconview;
    @BindView(R.id.women_iconview)
    SwitchIconView womenIconview;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;

    private String height, weight, sexVal, brithdayVal, nickName;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private boolean isSubmit;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.mine_data);
        manIconview.switchState();
        sexVal = "M";
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        for (int i = 120; i < 231; i++) {
            heightList.add(i + " cm");
        }
        for (int i = 20; i < 200; i++) {
            weightList.add(i + " kg");
        }


        //请求打开相机的权限
        AndPermission.with(PersonDataActivity.this)
                .requestCode(GET_OPENCAMERA_CODE)
                .permission(Manifest.permission.CAMERA)
                .callback(permissionListener)
                .rationale(new RationaleListener() {    //当用户拒绝时再次申请
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(PersonDataActivity.this,rationale).show();
                    }
                }).start();

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("PersonDataActivity","--result--"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");

                    Log.e("PersonDataActivity","------result----"+resultCode+"---"+jsonObject.getString("resultCode"));

                    if ("001".equals(resultCode)) {
                        if (isSubmit) {
                            Log.e("PersonDataActivity","----333------");
                            userInfo.setNickName(nickName);
                            userInfo.setSex(sexVal);
                            userInfo.setBirthday(brithdayVal);
                            userInfo.setHeight(height);
                            userInfo.setWeight(weight);
                            MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                           // startActivity(new Intent(PersonDataActivity.this, MainActivity.class));
                            startActivity(new Intent(PersonDataActivity.this, SearchDeviceActivity.class));
                            finish();
                        } else {
                            Log.e("PersonDataActivity","----444------");
                            String imageUrl = jsonObject.optString("url");
                            userInfo.setImage(imageUrl);
                            Glide.with(PersonDataActivity.this).load(imageUrl)
                                    .error(R.mipmap.touxiang)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .into(headImg);
                            MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        }
                    } else {
                        ToastUtil.showShort(PersonDataActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

//        //请求相机权限
//        int permissionCheck = ContextCompat.checkSelfPermission(PersonDataActivity.this, Manifest.permission.CAMERA);
//        if("-1".equals(String.valueOf(permissionCheck))){//请求权限
//            ActivityCompat.requestPermissions(PersonDataActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
//        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_persondata;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // startActivity(new Intent(PersonDataActivity.this, MainActivity.class));
        startActivity(new Intent(PersonDataActivity.this, SelectDeviceActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.head_img, R.id.selectbirthday_relayout, R.id.selectheight_relayout, R.id.selectweight_relayout, R.id.confirmcompelte_btn, man_iconview, R.id.women_iconview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_img:
                MenuSheetView menuSheetView =
                        new MenuSheetView(PersonDataActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
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
                break;
            case R.id.selectbirthday_relayout:
                DatePick pickerPopWin = new DatePick.Builder(PersonDataActivity.this, new DatePick.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        brithdayVal = dateDesc;
                        brithdayvalTv.setText(dateDesc);
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
                pickerPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.selectheight_relayout:

                ProfessionPick professionPopWin = new ProfessionPick.Builder(PersonDataActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        height = profession;
                        heightvalTv.setText(height);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(heightList) //min year in loop
                        .dateChose("170 cm") // date chose when init popwindow
                        .build();
                professionPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.selectweight_relayout:
                ProfessionPick weightPopWin = new ProfessionPick.Builder(PersonDataActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        weight = profession;
                        weightvalTv.setText(profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(weightList) //min year in loop
                        .dateChose("60 kg") // date chose when init popwindow
                        .build();
                weightPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.confirmcompelte_btn:
                //完成
                nickName = codeEt.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.write_nickname));
                } else if (TextUtils.isEmpty(brithdayVal)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_brithday));
                } else if (TextUtils.isEmpty(height)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_height));
                } else if (TextUtils.isEmpty(weight)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_weight));
                } else {
                    submitPersonData();
                }
                break;
            case man_iconview:
                sexVal = "M";
                if (!manIconview.isIconEnabled()) {
                    manIconview.switchState();
                    womenIconview.setIconEnabled(false);
                }
                break;
            case R.id.women_iconview:
                sexVal = "F";
                if (!womenIconview.isIconEnabled()) {
                    womenIconview.switchState();
                    manIconview.setIconEnabled(false);
                }
                break;
        }
    }


    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 有权限

            } else {
                // 再请求一次
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        }
    }


    private void getImage(int type) {
        new PickerBuilder(PersonDataActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        uploadPic(ImageTool.getRealFilePath(PersonDataActivity.this, imageUri));
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

    private void uploadPic(String filePath) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put("image", ImageTool.GetImageStr(filePath));
        String mapjson = gson.toJson(map);
        Log.e("PersonDataActivity","----111------"+mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, PersonDataActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);
    }

    private void submitPersonData() {
        isSubmit = true;
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
//        map.put("userId", B18iCommon.customer_id);
        map.put("userId", Common.customer_id);
        map.put("sex", sexVal);
        map.put("nickName", nickName);
        map.put("height", height);
        map.put("weight", weight);
        map.put("birthday", brithdayVal);
        String mapjson = gson.toJson(map);
        Log.e("PersonDataActivity","----222------"+mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, PersonDataActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

    /**
     * 申请权限回调
     */
    private PermissionListener permissionListener = new PermissionListener() {
        /**
         * 申请成功回调
         * @param requestCode
         * @param grantPermissions
         */
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode){
                case GET_OPENCAMERA_CODE:

                    break;
            }
        }
        //申请失败回调
        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case GET_OPENCAMERA_CODE:
                    ToastUtil.showToast(PersonDataActivity.this,"未获得打开相机权限,打开相机失败");
                    break;
            }
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(PersonDataActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(PersonDataActivity.this, GET_OPENCAMERA_CODE).show();}
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
