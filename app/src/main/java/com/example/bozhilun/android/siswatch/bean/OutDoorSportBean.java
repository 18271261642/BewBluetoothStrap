package com.example.bozhilun.android.siswatch.bean;

/**
 * Created by Administrator on 2017/11/20.
 */

public class OutDoorSportBean {


    /**
     * rtc : 2017-11-17
     * image : http://www.sinaimg.cn/dy/weather/main/index14/007/icons_128_wt/w_02_08_00.png
     * temp : 28°C / 20°C
     * distance : 0.08
     * timeLen : 00:01:26
     * description : 良
     * latLons : [{"pId":315,"lon":"113.72710453612851","id":67366,"lat":"22.98726422344302"},{"pId":315,"lon":"113.72741521974469","id":67367,"lat":"22.98775993482421"},{"pId":315,"lon":"113.72736056316954","id":67368,"lat":"22.987586307862266"}]
     * calories : 5.48
     * type : 0
     * userId : 5c2b58f0681547a0801d4d4ac8465f82
     * speed : 0.09
     * pm25 : 60
     * startTime : 2017-11-17  11:22
     * id : 315
     */

    private String rtc;
    private String image;
    private String temp;
    private String distance;
    private String timeLen;
    private String description;
    private String calories;
    private int type;
    private String userId;
    private String speed;
    private String pm25;
    private String startTime;
    private int id;
    private Object latLons;

    public Object getLatLons() {
        return latLons;
    }

    public void setLatLons(Object latLons) {
        this.latLons = latLons;
    }

    //  private ArrayList<LatLonsBean> latLons;

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTimeLen() {
        return timeLen;
    }

    public void setTimeLen(String timeLen) {
        this.timeLen = timeLen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public ArrayList<LatLonsBean> getLatLons() {
//        return latLons;
//    }
//
//    public void setLatLons(ArrayList<LatLonsBean> latLons) {
//        this.latLons = latLons;
//    }

  /*  public static class LatLonsBean {
        *//**
         * pId : 315
         * lon : 113.72710453612851
         * id : 67366
         * lat : 22.98726422344302
         *//*

        private int pId;
        private String lon;
        private int id;
        private String lat;

        public int getPId() {
            return pId;
        }

        public void setPId(int pId) {
            this.pId = pId;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }*/
}
