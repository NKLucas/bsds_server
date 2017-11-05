package edu.neu.zhiyu.model;

public class LiftDataStat {
    private String id;
    private String skierID;
    private int dayNum;
    private int liftCount;
    private int totalVertical;

    public LiftDataStat(String id, String skierID, int dayNum, int liftCount, int totalVertical) {
        this.id = id;
        this.skierID = skierID;
        this.dayNum = dayNum;
        this.liftCount = liftCount;
        this.totalVertical = totalVertical;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
