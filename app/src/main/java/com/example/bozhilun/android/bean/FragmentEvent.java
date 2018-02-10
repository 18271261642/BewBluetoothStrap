package com.example.bozhilun.android.bean;

/**
 * Created by thinkpad on 2017/3/17.
 */

public class FragmentEvent {

    private String message;
    private Object object;

    public FragmentEvent(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    public FragmentEvent(String message) {
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
