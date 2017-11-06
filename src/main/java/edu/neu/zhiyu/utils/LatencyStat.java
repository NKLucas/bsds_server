package edu.neu.zhiyu.utils;

import java.util.Collections;
import java.util.List;

public class LatencyStat {

    public static void printLatencyStats(List<Long> latencies) {
        Collections.sort(latencies);

        int size = latencies.size();
        System.out.println("Total latencies collected: " + size);

        long leastLat = latencies.get(0);
        long mostLat = latencies.get(size - 1);
        long meanLat = getMeanLat(latencies);
        long medianLat = latencies.get(latencies.size() / 2);
        long lat95 = latencies.get((int) (latencies.size() * 0.95));
        long lat99 = latencies.get((int) (latencies.size() * 0.99));
        System.out.println("Least latency: " + leastLat + "ms");
        System.out.println("Most latency: " + mostLat + "ms");
        System.out.println("Mean latency: " + meanLat + "ms");
        System.out.println("Median latency: " + medianLat + "ms");
        System.out.println("95th percentile latency: " + lat95 + "ms");
        System.out.println("99th percentile latency: " + lat99 + "ms");
    }

    private static long getMeanLat(List<Long> latencies) {
        long sum = 0;
        for(Long lat : latencies) {
            sum += lat;
        }
        return sum / latencies.size();
    }
}
