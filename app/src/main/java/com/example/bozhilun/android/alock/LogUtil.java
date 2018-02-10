/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.bozhilun.android.alock;

import android.util.Log;

import com.example.bozhilun.android.BuildConfig;


/**
 * Log输出
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class LogUtil {

/*    private static final int VERBOSE = 1;

    private static final int DEBUG = 2;

    private static final int INFO = 3;

    private static final int WARN = 4;

    private static final int ERROR = 5;

    private static final int NOTHING = 6;

    // TODO release >>> NOTHING
    private static final int LEVEL = NOTHING;*/

    public static void v(String tag, String msg) {
       /* if (BuildConfig.LOG_DEBUG) {
            Log.v(tag, msg);
        }*/
    }

    public static void d(String tag, String msg) {
      /*  if (BuildConfig.LOG_DEBUG) {
            Log.d(tag, msg);
        }*/
    }

    public static void i(String tag, String msg) {
       /* if (BuildConfig.LOG_DEBUG) {
            Log.i(tag, msg);
        }*/
    }

    public static void w(String tag, String msg) {
     /*   if (BuildConfig.LOG_DEBUG) {
            Log.w(tag, msg);
        }*/
    }

    public static void e(String tag, String msg) {
      /*  if (BuildConfig.LOG_DEBUG) {
            Log.e(tag, msg);
        }*/
    }

}
