package com.example.bozhilun.android.bean;

/**
 * Author: thikpad
 * Version: V1.0版本
 * Description: EventBus传递消息总体类
 * Date:
 */

public class EventCenter<T> {

    private int eventCode = -1;

    private T data;

    public EventCenter(int eventCode) {
        this.eventCode = eventCode;
    }

    public EventCenter(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    public int getEventCode() {
        return eventCode;
    }

    public T getData() {
        return data;
    }
}
