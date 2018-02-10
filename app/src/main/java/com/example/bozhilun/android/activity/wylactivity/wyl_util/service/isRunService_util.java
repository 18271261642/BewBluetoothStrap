package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;


import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 判断服务是否开启
  */

public class isRunService_util
{


	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
		if (serviceList.size() == 0) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
