package com.example.bozhilun.android.activity.wylactivity.wyl_util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/3/22.
 * 添加gps的点集合
 */

public class GPSpoints implements Serializable {
    String starttime;//开始的时间
    int type;//运动的类型
    double distance;//运动的距离
    String duration;//持续时间
    double calorie;//卡路里
    String Url;//图片地址
    String peishu;//配速
    String kongqiZhiliang;//空气质量 pm2.5
    String wendu;//温度
    List latLons;
    String Userid;

    public String getUserid(String customer_id) {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public List getMylist() {
        return latLons;
    }

    public void setMylist(List mylist) {
        latLons = mylist;
    }

    public String getPeishu() {
        return peishu;
    }

    public void setPeishu(String peishu) {
        this.peishu = peishu;
    }

    public String getKongqiZhiliang() {
        return kongqiZhiliang;
    }

    public void setKongqiZhiliang(String kongqiZhiliang) {
        this.kongqiZhiliang = kongqiZhiliang;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public GPSpoints() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    @Override
    public String toString() {
        return "GPSpoints{" +
                "starttime='" + starttime + '\'' +
                ", type=" + type +
                ", distance=" + distance +
                ", duration='" + duration + '\'' +
                ", calorie=" + calorie +
                ", Url='" + Url + '\'' +
                ", peishu='" + peishu + '\'' +
                ", kongqiZhiliang='" + kongqiZhiliang + '\'' +
                ", wendu='" + wendu + '\'' +
                ", Mylist=" + latLons +
                ", Userid='" + Userid + '\'' +
                '}';
    }
}
