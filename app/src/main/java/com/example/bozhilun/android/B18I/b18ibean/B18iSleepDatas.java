package com.example.bozhilun.android.B18I.b18ibean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/20 11:15
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
@Entity
public class B18iSleepDatas {
    @Id
    public Long ids;
    public int id;
    public int total;
    public int awake;
    public int light;
    public int deep;
    public int awaketime;
    public String detail;
    public String date;
    public int flag;
    public int type;
    public long timeStamp;
    @Generated(hash = 502308494)
    public B18iSleepDatas(Long ids, int id, int total, int awake, int light,
            int deep, int awaketime, String detail, String date, int flag, int type,
            long timeStamp) {
        this.ids = ids;
        this.id = id;
        this.total = total;
        this.awake = awake;
        this.light = light;
        this.deep = deep;
        this.awaketime = awaketime;
        this.detail = detail;
        this.date = date;
        this.flag = flag;
        this.type = type;
        this.timeStamp = timeStamp;
    }
    @Generated(hash = 1321942958)
    public B18iSleepDatas() {
    }
    public Long getIds() {
        return this.ids;
    }
    public void setIds(Long ids) {
        this.ids = ids;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTotal() {
        return this.total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public int getAwake() {
        return this.awake;
    }
    public void setAwake(int awake) {
        this.awake = awake;
    }
    public int getLight() {
        return this.light;
    }
    public void setLight(int light) {
        this.light = light;
    }
    public int getDeep() {
        return this.deep;
    }
    public void setDeep(int deep) {
        this.deep = deep;
    }
    public int getAwaketime() {
        return this.awaketime;
    }
    public void setAwaketime(int awaketime) {
        this.awaketime = awaketime;
    }
    public String getDetail() {
        return this.detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getFlag() {
        return this.flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
