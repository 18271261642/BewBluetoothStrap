package com.example.bozhilun.android.siswatch.bean;

/**
 * Created by Administrator on 2017/9/29.
 */

public class AlarmTestBean {

    private Integer idnum;
    private String weeks;

    public Integer getIdnum() {
        return idnum;
    }

    public void setIdnum(Integer idnum) {
        this.idnum = idnum;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public AlarmTestBean(Integer idnum, String weeks) {
        this.idnum = idnum;
        this.weeks = weeks;
    }
}
