package edu.neu.zhiyu.model;

public class ServerLatency {
    private HttpRequestType requestType;
    private long latency;
    private int errorCount;

    public ServerLatency(){
    }

    public ServerLatency(HttpRequestType requestType, long latency, int errorCount) {
        this.requestType = requestType;
        this.latency = latency;
        this.errorCount = errorCount;
    }

    public ServerLatency(HttpRequestType requestType, long latency) {
        this.requestType = requestType;
        this.latency = latency;
        this.errorCount = 0;
    }

    public HttpRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(HttpRequestType requestType) {
        this.requestType = requestType;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
