package edu.neu.zhiyu.client;

import edu.neu.zhiyu.model.LiftStat;
import edu.neu.zhiyu.utils.ChartMaker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A2GetMain {

    private static String REMOTE_URL = "http://ec2-34-209-12-29.us-west-2.compute.amazonaws.com:8080/bsds-server/api/ski";
    private static String LOCAL_URL = "http://localhost:9234/api/ski";

    private static int THREADS = 100;
    private static final int SKIER_COUNT = 40000;

    public static void main(String[] args) throws Exception {

        List<String> skierIDs = new ArrayList<>(SKIER_COUNT);

        for (int i = 1; i <= SKIER_COUNT; i++) {
            skierIDs.add(String.valueOf(i));
        }

        List<A2GetClient> clients = new ArrayList<>();
        ExecutorService ex = Executors.newFixedThreadPool(THREADS);
        int countForEachThreads = skierIDs.size() / THREADS;

        for (int i = 0; i < THREADS; i++) {
            int startIndex = i * countForEachThreads;
            int endIndex = i == THREADS - 1 ? skierIDs.size() : (i + 1) * countForEachThreads;
            List<String> data = skierIDs.subList(startIndex, endIndex);
            A2GetClient client = new A2GetClient(REMOTE_URL, data);
            clients.add(client);
            ex.submit(client);
        }

        long start = System.currentTimeMillis();
        System.out.println("All Client Threads Started: " + new Date().toString());

        ex.shutdown();
        while (!ex.isTerminated()) {
        }

        System.out.println("All Client Threads Finished: " + new Date().toString());
        long end = System.currentTimeMillis();
        System.out.println("Total time taken: " + (end - start) + "ms. \n");

        calculateAndPrintStats(clients);
    }

    private static void calculateAndPrintStats(List<A2GetClient> clients) {
        System.out.println("Some stats: \n");
        List<Long> latencies = new ArrayList<>();
        int totalRequest = 0;
        int successRequest = 0;

        for (A2GetClient client : clients) {
            totalRequest += client.getRequestSent();
            successRequest += client.getSuccessReq();
            latencies.addAll(client.getLatencies());
        }

        System.out.println("Total requests send: " + totalRequest);
        System.out.println("Total successful responses: " + successRequest);

        printLatencyStats(latencies);
        generateChart(latencies);
    }

    private static void generateChart(List<Long> latencies) {
        String fileName = "post_latency_thread_" + THREADS;
        ChartMaker chartMaker = new ChartMaker();
        try {
            chartMaker.makeChart(latencies, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printLatencyStats(List<Long> latencies) {
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
        for (Long lat : latencies) {
            sum += lat;
        }
        return sum / latencies.size();
    }

}
