package com.example.bozhilun.android.B18I.b18ibean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/20 11:18
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
@Entity
public class B18iStepDatas {
    @Id
    public Long ids;
    public int id;
    public int step;
    public int distance;
    public int sporttime;
    public int calories;
    public String time;
    public long timestamp;
    @Generated(hash = 925867474)
    public B18iStepDatas(Long ids, int id, int step, int distance, int sporttime,
            int calories, String time, long timestamp) {
        this.ids = ids;
        this.id = id;
        this.step = step;
        this.distance = distance;
        this.sporttime = sporttime;
        this.calories = calories;
        this.time = time;
        this.timestamp = timestamp;
    }
    @Generated(hash = 1363363172)
    public B18iStepDatas() {
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
    public int getStep() {
        return this.step;
    }
    public void setStep(int step) {
        this.step = step;
    }
    public int getDistance() {
        return this.distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public int getSporttime() {
        return this.sporttime;
    }
    public void setSporttime(int sporttime) {
        this.sporttime = sporttime;
    }
    public int getCalories() {
        return this.calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
