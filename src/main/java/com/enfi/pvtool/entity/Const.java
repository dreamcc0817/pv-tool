package com.enfi.pvtool.entity;


import java.util.Date;

public class Const {
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 累积充电
     */
    private double cumulativeCharge;

    /**
     * 累积放电
     */
    private double cumulativDischarge;


    public double getCumulativeCharge() {
        return cumulativeCharge;
    }

    public void setCumulativeCharge(double cumulativeCharge) {
        this.cumulativeCharge = cumulativeCharge;
    }

    public double getCumulativDischarge() {
        return cumulativDischarge;
    }

    public void setCumulativDischarge(double cumulativDischarge) {
        this.cumulativDischarge = cumulativDischarge;
    }
}
