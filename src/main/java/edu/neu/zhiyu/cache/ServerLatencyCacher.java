package edu.neu.zhiyu.cache;

import edu.neu.zhiyu.model.ServerLatency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerLatencyCacher {
    private static ServerLatencyCacher ServerLatencyCacher;
    private List<ServerLatency> data;

    public ServerLatencyCacher() {
        data = Collections.synchronizedList(new ArrayList<ServerLatency>());
    }

    public static ServerLatencyCacher getInstance() {
        if (ServerLatencyCacher == null) {
            ServerLatencyCacher = new ServerLatencyCacher();
        }
        return ServerLatencyCacher;
    }

    public synchronized void add(ServerLatency record) {
        data.add(record);
    }

    public synchronized int size() {
        return data.size();
    }

    public synchronized List<ServerLatency> getCachedData() {
        List<ServerLatency> result = data;
        data = Collections.synchronizedList(new ArrayList<ServerLatency>());
        return result;
    }

}
