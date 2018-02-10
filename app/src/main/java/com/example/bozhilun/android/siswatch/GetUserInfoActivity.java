package com.example.bozhilun.android.siswatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/11/1.
 */

public class GetUserInfoActivity extends WatchBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserInfoData((String) SharedPreferencesUtils.readObject(GetUserInfoActivity.this,"userId"));
    }

    //获取用户信息
    private void getUserInfoData(String customer_id) {
        String url = URLs.HTTPs + URLs.getUserInfo;
        JSONObject jsonob = new JSONObject();
        try {
            jsonob.put("userId", customer_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonRequest<JSONObject> jso = new JsonObjectRequest(Request.Method.POST, url, jsonob, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getInt("resultCode") == 001) {
                            JSONObject userJson = response.getJSONObject("userInfo");
                            if (userJson != null) {
                                //保存用户信息
                                SharedPreferencesUtils.saveObject(GetUserInfoActivity.this, "saveuserinfodata", userJson.toString());
                                String height = userJson.getString("height");
                                if (height.contains("cm")) {
                                    String newHeight = height.substring(0, height.length() - 2);
                                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, "userheight", newHeight.trim());
                                } else {
                                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, "userheight", height.trim());
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

            }
        });
        MyApp.getRequestQueue().add(jso);
    }
}
