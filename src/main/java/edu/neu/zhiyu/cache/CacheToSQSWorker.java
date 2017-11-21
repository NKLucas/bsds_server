package edu.neu.zhiyu.cache;

import com.google.gson.Gson;
import edu.neu.zhiyu.model.ServerLatency;
import edu.neu.zhiyu.sqsUtil.SQSPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheToSQSWorker {

    private static Logger logger = Logger.getLogger(CacheToSQSWorker.class.getName());
    private static ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
    private static long start = System.currentTimeMillis();
    private static long DELAY = 0;          // no delay
    private static long PERIOD = 5000;      // 5 seconds
    private static int LATENCIES_MERGE_THRESHOLD = 1000; // 1000 Latencies into one message to sqs.
    private static int SQS_BATCH_SIZE = 10; // Defined by amazon.

    public static void start() {
        logger.log(Level.INFO, "CacheToSQSWorker Started!");
        SQSPublisher.init();

        scheduledService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        sendCachedDataToSQS();
                    }
                }, DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    private static void sendCachedDataToSQS() {
        ServerLatencyCacher cacher = ServerLatencyCacher.getInstance();
        Gson gson = new Gson();
        if (cacher.size() > 0) {
            List<ServerLatency> dataList = cacher.getCachedData();
            List<String> latStrs = new ArrayList<>();
            try{
                for (ServerLatency latency : dataList) {
                    latStrs.add(gson.toJson(latency));
                }

                List<List<String>> splitedList = getSplitedList(latStrs);

                for (List<String> messages : splitedList) {
                    SQSPublisher.sendBatchMessage(messages);
                }

                System.out.println("YEAH!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<List<String>> getSplitedList(List<String> strs) {
        Gson gson = new Gson();
        List<List<String>> result = new ArrayList<>();
        List<String> mergedString = new ArrayList<>();
        int startIndex = 0;
        // Group every 1000 into one string.
        while (startIndex < strs.size()) {
            int endIndex = Math.min(startIndex + LATENCIES_MERGE_THRESHOLD, strs.size());
            List<String> temp = strs.subList(startIndex, endIndex);
            mergedString.add(gson.toJson(temp));
            startIndex = endIndex;
        }

        startIndex = 0;
        // Group every 10 string into one list.
        while (startIndex < mergedString.size()) {
            int endIndex = Math.min(startIndex + SQS_BATCH_SIZE, mergedString.size());
            List<String> temp = mergedString.subList(startIndex, endIndex);
            result.add(temp);
            startIndex = endIndex;
        }

        return result;
    }



}
