package com.example.bozhilun.android.activity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.Md5Util;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

import static com.example.bozhilun.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/9.
 * 修改密码
 */

public class ModifyPasswordActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confrim_password)
    EditText confrimPassword;

    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.modify_password);
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    if ("001".equals(resultCode)) {
                        String confrimPass = confrimPassword.getText().toString();
                        userInfo.setPassword(Md5Util.Md532(confrimPass));
                        MyApp.getApplication().getDaoSession().getBlueUserDao().insertOrReplace(userInfo);
                        ToastUtil.showShort(ModifyPasswordActivity.this, getString(R.string.modify_success));
                        finish();
                    } else {
                        ToastUtil.showShort(ModifyPasswordActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String oldPass = oldPassword.getText().toString();
        String newPass = newPassword.getText().toString();
        String confrimPass = confrimPassword.getText().toString();
        if (TextUtils.isEmpty(oldPass)) {
            ToastUtil.showShort(this, getString(R.string.input_old_password));
        } else if (TextUtils.isEmpty(newPass)) {
            ToastUtil.showShort(this, getString(R.string.input_new_password));
        } else if (TextUtils.isEmpty(confrimPass)) {
            ToastUtil.showShort(this, getString(R.string.input_confirm_password));
        } else if (!newPass.equals(confrimPass)) {
            ToastUtil.showShort(this, getString(R.string.new_dif_confirm));
        } else {
            modifyPersonData(oldPass, confrimPass);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_modify_password;
    }

    private void modifyPersonData(String oldePwd, String newPwd) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put("oldePwd", Md5Util.Md532(oldePwd));
        map.put("newPwd", Md5Util.Md532(newPwd));
        String mapjson = gson.toJson(map);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, ModifyPasswordActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.xiugaipassword, mapjson);
    }

}
