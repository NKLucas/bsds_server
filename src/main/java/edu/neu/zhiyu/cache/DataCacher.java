package edu.neu.zhiyu.cache;

import edu.neu.zhiyu.model.RFIDLiftData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataCacher {
    private static DataCacher dataCacher;
    private List<RFIDLiftData> data;

    public DataCacher() {
        data = Collections.synchronizedList(new ArrayList<RFIDLiftData>());
    }

    public static DataCacher getInstance() {
        if (dataCacher == null) {
            dataCacher = new DataCacher();
        }
        return dataCacher;
    }

    public synchronized void add(RFIDLiftData record) {
        data.add(record);
    }

    public synchronized int size() {
        return data.size();
    }

    public synchronized List<RFIDLiftData> getCachedData() {
        List<RFIDLiftData> result = new ArrayList<>(data);
        return result;
    }

    public synchronized void cleanCache() {
        data.clear();
    }

}
