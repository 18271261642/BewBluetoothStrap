package com.example.bozhilun.android.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;

/**
 * Created by thinkpad on 2016/6/22.
 */
public class OkHttpUtils {

    private static OkHttpClient client = new OkHttpClient();

  /*  public static void getLogin(String jsonParam) {
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
        RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
        Request request = new Request.Builder()
                .url(RequestURL.LoginUrl + jsonParam)
                //.addHeader("Caller", "YuehoooApiSDK")
                // .post(formBody)
                .build();
        //MyLogUtil.i("msg", "url-" + RequestURL.LoginUrl + jsonParam);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MyLogUtil.i("msg", "--IOException-" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseUrl = response.body().string();
                MyLogUtil.i("msg", "-responseUrl-" + responseUrl);
                Gson gson = new Gson();
                //ResponseBody responseBody = gson.fromJson(responseUrl, ResponseBody.class);
            }
        });
    }*/

           /*String requestMessage="";
                String sign= (Md5Util.MD5(RequestURL.Key + mapjson)).toLowerCase();
                try {
                    requestMessage = URLEncoder.encode(mapjson, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                HashMap<String,String> mapParam=new HashMap<>();

                mapParam.put("action","appLogin");
                mapParam.put("sign",sign);
                mapParam.put("requestMessage",requestMessage);

                Log.i("msg","-sign-"+sign);
                Log.i("msg","-requestMessage-"+requestMessage);
                //HttpMethods.getInstance().getLogin(commonSubscriber,mapParam);*/

    public static final boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
