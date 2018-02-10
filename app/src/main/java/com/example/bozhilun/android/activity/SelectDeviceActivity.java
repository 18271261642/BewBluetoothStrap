package com.example.bozhilun.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.MyCoverFlowAdapter;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.bean.EventCenter;
import com.example.bozhilun.android.coverflow.CoverFlowView;
import com.example.bozhilun.android.B18I.B18ISearchActivity;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thinkpad on 2017/3/14.
 * 搜索设备
 */

public class SelectDeviceActivity extends BaseActivity {

    private static final int GPS_REQUEST_CODE = 102;


    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.coverflow) CoverFlowView coverflow;
    @BindView(R.id.seach_devicename) TextView devicename;//设备名字
    String NAME="";

    @Override
    protected void initViews(){
        if(checkGPSIsOpen()){
            initView();
        }else{
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.prompt));
            alert.setMessage(getResources().getString(R.string.gps_alert));
            alert.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, GPS_REQUEST_CODE);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.create().show();
        }
    }

    protected void initView() {
        //获取用户信息，主要获取身高
        testUserInfo();

        EventBus.getDefault().register(this);
        tvTitle.setText(R.string.search_device);    //, R.mipmap.l42a_xiaotu
        int[] imggIds = new int[]{R.mipmap.b15p,R.mipmap.icon_translation_watch, R.mipmap.b15s,R.mipmap.icon_b18i_transaction};
        final MyCoverFlowAdapter adapter = new MyCoverFlowAdapter(SelectDeviceActivity.this, imggIds);
        coverflow.setAdapter(adapter);
        coverflow  .setCoverFlowListener(new CoverFlowView.CoverFlowListener<MyCoverFlowAdapter>() {
                    @Override
                    public void imageOnTop(
                            CoverFlowView<MyCoverFlowAdapter> view,
                            int position, float left, float top, float right,
                            float bottom) {
                        switch (position){
                            case 0:
                                devicename.setText("B15P");
                                NAME="B15P";
                                break;
                            case 1: //sis watch
                                devicename.setText("bozlun");
                                NAME="bozlun";
                                break;
                            case 2:
                                devicename.setText("B15S");
                                NAME="B15S";
                                break;
                            case 3:
                                devicename.setText("B18I");
                                NAME="HR";
                                break;

                        }
                       // ToastUtil.showShort(SelectDeviceActivity.this, position + " on top!");
                    }

                    @Override
                    public void topImageClicked(
                            CoverFlowView<MyCoverFlowAdapter> view, int position) {
                        if (position == 0) {
                            ToastUtil.showShort(SelectDeviceActivity.this, position + "B15P clicked!");
                        } else if (position == 1) {
                            ToastUtil.showShort(SelectDeviceActivity.this, position + "bozlun clicked!");
//                        } else if (position == 2) {
//                            ToastUtil.showShort(SelectDeviceActivity.this, position + "L42A clicked!");
//                        }
                        }  else if(position == 2){
                            ToastUtil.showShort(SelectDeviceActivity.this,position + "B15s clicked");
                        }else if(position == 3){
                            ToastUtil.showShort(SelectDeviceActivity.this,position + "L38I clicked");
                        }
                    }
                    @Override
                    public void invalidationCompleted() {

                    }
                });

        int permissionCheck = ContextCompat.checkSelfPermission(SelectDeviceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if("-1".equals(String.valueOf(permissionCheck))){//请求权限
            ActivityCompat.requestPermissions(SelectDeviceActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
    }

    private void testUserInfo() {
        BlueUser blueUser = new BlueUser();
        if(blueUser != null){
            Log.e("SelectDeviceActivity","---user--"+blueUser.getHeight()+"----"+blueUser.getWeight()+"----"+blueUser.getDeviceCode()+"---"+blueUser.getUserId()+"---"+ Common.customer_id);
            getUserInfoData(Common.customer_id);
        }
    }

    private void getUserInfoData(String userId) {
        String url = URLs.HTTPs + URLs.getUserInfo;
        JSONObject jsonob = new JSONObject();
        try {
            jsonob.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //RequestQueue requestQ = Volley.newRequestQueue(SelectDeviceActivity.this);
        JsonRequest<JSONObject> jso = new JsonObjectRequest(Request.Method.POST, url, jsonob, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response != null){
                    Log.e("SelectDeviceActivity","====="+response.toString());
                    try {
                        if(response.getInt("resultCode") == 001){
                            JSONObject userJson = response.getJSONObject("userInfo");
                            if(userJson != null){
                                Log.e("SelectDeviceActivity","-------height--"+userJson.getString("height"));
                                String height = userJson.getString("height");
                                Log.e("SelectDeviceActivity","---heithg--"+height+"---------"+height.contains("cm")+"------"+height.endsWith("cm"));
                                if(height.contains("cm")){
                                    String newHeight = height.substring(0,height.length()-2);
                                    Log.e("SelectDeviceActivity","----newHeight---"+newHeight);
                                    SharedPreferencesUtils.setParam(SelectDeviceActivity.this,"userheight",newHeight.trim());
                                }else{
                                    SharedPreferencesUtils.setParam(SelectDeviceActivity.this,"userheight",height.trim());
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SelectDeviceActivity","---------"+error.getMessage());
            }
        });
        MyApp.getRequestQueue().add(jso);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("SelectDeviceActivity","-----re---"+requestCode + "----resultCode-"+resultCode);
        if(requestCode == GPS_REQUEST_CODE){
            initView();
        }else{
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.prompt));
            alert.setMessage(getResources().getString(R.string.gps_alert));
            alert.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, GPS_REQUEST_CODE);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.create().show();
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
            }
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_3dselect_device;
    }

    @OnClick(R.id.search_btn)
    public void onClick() {
        if("HR".equals(NAME)){
            Intent intent = new Intent(SelectDeviceActivity.this, B18ISearchActivity.class);
            intent.putExtra("NAME","HR");
            startActivity(intent);
        }else{
            Intent AAA = new Intent(SelectDeviceActivity.this, SearchDeviceActivity.class);
            if ("".equals(NAME)) {
                AAA.putExtra("NAME", "B15S");
            } else {
                AAA.putExtra("NAME", NAME);
            }
            startActivity(AAA);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFinishThread(EventCenter event) {
        int tag = event.getEventCode();
        if (tag == 0) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.transparent;
    }

    @Override
    protected void getToolbarClick() {
        super.getToolbarClick();
        removeAllActivity();    //全部退出，即退出程序
    }

    /**
     * 判断GPS是否打开
     * @return
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    public long exitTime; // 储存点击退出时间

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtil.showToast(SelectDeviceActivity.this,"再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                    return false;
                } else {
                    // 全局推出
                    removeAllActivity();
                    return true;
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
