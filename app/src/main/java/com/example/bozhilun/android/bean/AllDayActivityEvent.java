package com.example.bozhilun.android.bean;

/**
 * Created by thinkpad on 2017/3/16.
 */

public class AllDayActivityEvent {
    private String message;
    private Object object;

    public AllDayActivityEvent(String message,Object object) {
        this.message = message;
        this.object = object;
    }



    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
