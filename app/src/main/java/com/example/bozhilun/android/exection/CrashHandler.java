package com.example.bozhilun.android.exection;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.umeng.analytics.MobclickAgent;

public class CrashHandler implements UncaughtExceptionHandler {

    /**
     * Debug Log Tag
     */
    public static final String TAG = "CrashHandler";
    /**
     * 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能
     */
    public static final boolean DEBUG = true;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }
            // 全局推出
            MyApp.getInstance().removeALLActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final StringBuilder messageError = new StringBuilder();
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            messageError.append(sw.toString());
        } catch (Exception e2) {
            System.out.println("bad getErrorInfoFromException");
        }
        final String message = ex.getMessage();// hrowable 的详细消息字符串
        final String message_type = ex.getLocalizedMessage();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = null;
        try {
            cn = am.getRunningTasks(1).get(0).topActivity;
        } catch (SecurityException e) {
            cn = null;
        }
        final String msg_class = cn == null ? "" : cn.getClassName() + "";
        // 收集设备信息
        messageError.append(collectCrashDeviceInfo(mContext));
        Log.e("CrashHandler","----------------" + messageError.toString());
        // 使用Toast来显示异常信息
        new Thread() {

            @Override
            public void run() {
                // 方案1 Toast 显示需要出现在一个线程的消息队列中
                Looper.prepare();
                // 执行
                postReport(msg_class, message_type, message, messageError.toString());
                showToast();
                Looper.loop();
            }
        }.start();
        return true;
    }

    /**
     * 自定义弹出toast
     *
     * @param
     */
    public void showToast() {
        Toast toast = new Toast(mContext);
        TextView textView = new TextView(mContext);
        textView.setText("Sorry,the program error!");
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(15, 15, 15, 15);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    public String collectCrashDeviceInfo(Context ctx) {
        StringBuffer tagString = new StringBuffer("");
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                tagString.append("app版本号:");
                tagString.append(pi.versionName == null ? "not set" : pi.versionName);
                tagString.append("app版本:" + pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 具体信息请参考后面的截图
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                tagString.append(field.getName() + ":" + field.get(null));
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(null));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
        return tagString.toString();
    }


    /**
     * 使用HTTP Post 发送错误报告到服务器 这里不再赘述
     *
     * @param msg_class    调用类
     * @param source_type  e.toString 调用此对象 getLocalizedMessage() 方法的结果
     * @paramgetLocalizedMessage 返回 null，则只返回类名称。
     * @param message      此 throwable 的详细消息字符串。
     * @param messageError throwable 相关的堆栈跟踪
     */
    private void postReport(String msg_class, String source_type, String message, String messageError) {
        // 在上传的时候还可以将该app的version，该手机的机型等信息一并发送的服务器,
        // Android的兼容性众所周知，所以可能错误不是每个手机都会报错，还是有针对性的去debug比较好
        HashMap<String, String> mapJson = new HashMap<String, String>();
        mapJson.put("racefitpro","racefitproapp");
        mapJson.put("errorClass", msg_class);// 类名
        mapJson.put("method", message);// 方法名
        mapJson.put("createtime", new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ").format(new Date(System.currentTimeMillis())));// 产生时间
        mapJson.put("loglevel", "E");// 日志级别
        mapJson.put("logmsg", messageError);// 日志信息
        mapJson.put("source_type", source_type);// 错误类型
        mapJson.put("version", WatchUtils.getVersionCode(mContext)+"");//版本号
        sendLog(mapToJson(mapJson));
        Log.e("CrashHandler","------------maptojson-"+mapToJson(mapJson));
    }

    //异常日志上传后台请求
    private void sendLog(final String jsonMap) {
        Log.e("CrashHandler","-----错误信息收集----" + jsonMap.toString());
        MobclickAgent.reportError(MyApp.getContext(),jsonMap);
        String url = "http://dgxw.gqwap.com/logMobile/exceptionLog";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("CrashHandler","---日志上传成功返回---"+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CrashHandler","---日志上传失败返回---"+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> maps = new HashMap<>();
                maps.put("log",jsonMap);
                maps.put("deviceCode",WatchUtils.getPhoneInfo(mContext)+"");
                return maps;
            }
        };
        requestQueue.add(stringRequest);

    }

    /**
     * map转换json.
     * <br>详细说明
     *
     * @param map 集合
     * @return String json字符串
     * @throws
     * @author slj
     */
    public static String mapToJson(Map<String, String> map) {
        Set<String> keys = map.keySet();
        String key = "";
        String value = "";
        StringBuffer jsonBuffer = new StringBuffer();
        jsonBuffer.append("{");
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            key = (String) it.next();
            value = map.get(key);
            jsonBuffer.append(key + ":" + "\"" + value + "\"");
            if (it.hasNext()) {
                jsonBuffer.append(",");
            }
        }
        jsonBuffer.append("}");
        return jsonBuffer.toString();
    }

}
