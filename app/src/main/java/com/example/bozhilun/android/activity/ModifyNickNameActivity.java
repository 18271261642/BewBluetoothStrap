package com.example.bozhilun.android.activity;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

import static com.example.bozhilun.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/8.
 * 修改昵称
 */

public class ModifyNickNameActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.code_et_nickname)
    EditText codeEt;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private  boolean isregister=false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isregister=false;
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void initViews() {


        tvTitle.setText(R.string.modify_nickname);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if ("001".equals(resultCode)) {
                        String nickName = codeEt.getText().toString();
                        userInfo.setNickName(nickName);

                        SharedPreferences shares = getSharedPreferences("nickName", 0);
                        SharedPreferences.Editor editors = shares.edit();
                        editors.putString("name",nickName);
                        editors.commit();

                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        ToastUtil.showShort(ModifyNickNameActivity.this, getString(R.string.modify_success));
                        finish();
                        Glide.get(ModifyNickNameActivity.this).clearMemory();//子线程清除缓存
                    } else {
                        ToastUtil.showShort(ModifyNickNameActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_modify_nickname;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String nickName = codeEt.getText().toString();
        if (!TextUtils.isEmpty(nickName)) {
            modifyPersonData(nickName);
        } else {
            ToastUtil.showShort(this, getString(R.string.write_nickname));
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyPersonData(String val) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put("nickName", val);
        String mapjson = gson.toJson(map);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ModifyNickNameActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

}
