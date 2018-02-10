package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bozhilun.android.activity.wylactivity.MyShouhuanXitongShenJiActivity;


/**
 * Created by admin on 2016/12/1.
 */
public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent intent = new Intent(this, MyShouhuanXitongShenJiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(getIntent().getExtras()); // copy all extras
            startActivity(intent);
        }
        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish();
    }
}