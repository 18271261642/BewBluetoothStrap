package com.example.bozhilun.android.bleutil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/3/11.
 */

public class Sleeptime implements Serializable, Comparable<Sleeptime> {
    private int type; //0睡眠 1深睡
    private String startime;//开始时间
    private int duration;//时长

    public Sleeptime(int type, String startime, int duration) {
        this.type = type;
        this.startime = startime;
        this.duration = duration;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Sleeptime{" +
                "type=" + type +
                ", startime='" + startime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public int compareTo(Sleeptime sleeptime) {
        String date = sleeptime.getStartime();
        int result = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date new_date = simpleDateFormat.parse(date);
            Date my_date = simpleDateFormat.parse(startime);
            if (new_date.getTime() < my_date.getTime()) {
                result = -1;
            } else if (new_date.getTime() == my_date.getTime()) {
                result = 0;
            } else {
                result = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}

