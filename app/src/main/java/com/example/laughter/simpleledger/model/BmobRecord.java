package com.example.laughter.simpleledger.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class BmobRecord extends BmobObject {

    private String remark;
    private float money;
    private String date;
    private int type;
    private BmobUser user;

    public BmobRecord(){}

    public BmobRecord(String remark, float money, String date, int type){
        this.remark = remark;
        this.money = money;
        this.date = date;
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }
}
