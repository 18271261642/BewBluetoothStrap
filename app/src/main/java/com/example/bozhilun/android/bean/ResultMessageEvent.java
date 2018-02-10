package com.example.bozhilun.android.bean;

/**
 * Created by thinkpad on 2017/3/13.
 */

public class ResultMessageEvent {

    private String message;
    private Object object;

    public ResultMessageEvent(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    public ResultMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
