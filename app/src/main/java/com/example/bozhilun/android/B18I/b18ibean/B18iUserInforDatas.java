package com.example.bozhilun.android.B18I.b18ibean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/20 11:19
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
@Entity
public class B18iUserInforDatas {
    @Id
    public Long ids;
    private int sex;
    private int age;
    private int height;
    private int weight;
    @Generated(hash = 1331929108)
    public B18iUserInforDatas(Long ids, int sex, int age, int height, int weight) {
        this.ids = ids;
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }
    @Generated(hash = 1538691768)
    public B18iUserInforDatas() {
    }
    public Long getIds() {
        return this.ids;
    }
    public void setIds(Long ids) {
        this.ids = ids;
    }
    public int getSex() {
        return this.sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWeight() {
        return this.weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
