package com.example.laughter.simpleledger.bean;

import org.litepal.crud.LitePalSupport;
import java.io.Serializable;

public class DbRecord extends LitePalSupport implements Serializable{

    private String remark;
    private float money;
    private String date;
    private int type;
    private String objectId;

    public DbRecord(){}

    public DbRecord(String remark, float money, String date, int type, String objectId){
        this.remark = remark;
        this.money = money;
        this.date = date;
        this.type = type;
        this.objectId = objectId;
    }

    public DbRecord(BmobRecord record, String objectId){
        this.remark = record.getRemark();
        this.money = record.getMoney();
        this.date = record.getDate();
        this.type = record.getType();
        this.objectId = objectId;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
