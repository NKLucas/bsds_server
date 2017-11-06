package edu.neu.zhiyu.model;

public class LiftStat {
    private String skierID;
    private int dayNum;
    private int liftCount;
    private int totalVertical;

    public LiftStat(String skierID, int dayNum, int liftCount, int totalVertical) {
        this.skierID = skierID;
        this.dayNum = dayNum;
        this.liftCount = liftCount;
        this.totalVertical = totalVertical;
    }

    public LiftStat(){}

    public String getSkierID() {
        return skierID;
    }

    public void setSkierID(String skierID) {
        this.skierID = skierID;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public int getLiftCount() {
        return liftCount;
    }

    public void setLiftCount(int liftCount) {
        this.liftCount = liftCount;
    }

    public int getTotalVertical() {
        return totalVertical;
    }

    public void setTotalVertical(int totalVertical) {
        this.totalVertical = totalVertical;
    }
}
