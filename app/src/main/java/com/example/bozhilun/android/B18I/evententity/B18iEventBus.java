package com.example.bozhilun.android.B18I.evententity;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/8/29 10:27
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18iEventBus {
    private String name;
    private Object object;

    public B18iEventBus() {
    }

    public B18iEventBus(String name) {
        this.name = name;
    }

    public B18iEventBus(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
