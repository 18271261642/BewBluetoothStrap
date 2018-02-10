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
public class B18iHeartDatas {
    @Id
    public Long ids;
    public int id;
    public int avg;
    public String date;
    public long timestamp;
    @Generated(hash = 1537377172)
    public B18iHeartDatas(Long ids, int id, int avg, String date, long timestamp) {
        this.ids = ids;
        this.id = id;
        this.avg = avg;
        this.date = date;
        this.timestamp = timestamp;
    }
    @Generated(hash = 440995061)
    public B18iHeartDatas() {
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
    public int getAvg() {
        return this.avg;
    }
    public void setAvg(int avg) {
        this.avg = avg;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
