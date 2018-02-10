package com.example.bozhilun.android.siswatch.utils.test;

/**
 * Created by Administrator on 2017/10/9.
 */

public class JiedianListener {

    private JiedianTest jiedianTest;

   public void userJiedianListener(String data){
       jiedianTest.getJiedianData(data);
   }

    public void setJiedianTest(JiedianTest jiedianTest) {
        this.jiedianTest = jiedianTest;
    }

    public JiedianListener() {

    }
}
