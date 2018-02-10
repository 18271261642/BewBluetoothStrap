package com.example.bozhilun.android.B18I.b18ibean;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/26 15:10
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class TimeFormatBean {
    private int imagesTime;
    private boolean stateTime;

    public TimeFormatBean() {
    }

    public TimeFormatBean(int imagesTime, boolean stateTime) {
        this.imagesTime = imagesTime;
        this.stateTime = stateTime;
    }

    public int getImagesTime() {
        return imagesTime;
    }

    public void setImagesTime(int imagesTime) {
        this.imagesTime = imagesTime;
    }

    public boolean isStateTime() {
        return stateTime;
    }

    public void setStateTime(boolean stateTime) {
        this.stateTime = stateTime;
    }
}
