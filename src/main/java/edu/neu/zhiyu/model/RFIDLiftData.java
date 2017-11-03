/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.neu.zhiyu.model;

/**
 *
 * Simple class to wrap the data in a RFID lift pass reader record
 */
public class RFIDLiftData {
    
    private String resortID;
    private int dayNum;
    private String skierID;
    private String liftID;
    private String time;

    public RFIDLiftData(String resortID, int dayNum, String skierID, String liftID, String time) {
        this.resortID = resortID;
        this.dayNum = dayNum;
        this.skierID = skierID;
        this.liftID = liftID;
        this.time = time;
    }

    public String getResortID() {
        return resortID;
    }

    public void setResortID(String resortID) {
        this.resortID = resortID;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public String getSkierID() {
        return skierID;
    }

    public void setSkierID(String skierID) {
        this.skierID = skierID;
    }

    public String getLiftID() {
        return liftID;
    }

    public void setLiftID(String liftID) {
        this.liftID = liftID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
}
