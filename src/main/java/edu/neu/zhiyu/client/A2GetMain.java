package edu.neu.zhiyu.client;

import edu.neu.zhiyu.utils.ChartMaker;
import edu.neu.zhiyu.utils.LatencyStat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A2GetMain {

    private static String REMOTE_URL = "http://ec2-35-165-154-92.us-west-2.compute.amazonaws.com:8080/bsds-server/api/ski";
    private static String LOCAL_URL = "http://localhost:9234/api/ski";

    private static int THREADS = 100;
    private static final int SKIER_COUNT = 40000;

    public static void main(String[] args) throws Exception {

        List<String> skierIDs = new ArrayList<>(SKIER_COUNT);

        for (int i = 1; i <= SKIER_COUNT; i++) {
            skierIDs.add(String.valueOf(i));
        }

        System.out.println("Request URL: " + REMOTE_URL);
        System.out.println("Threads Number: " + THREADS + '\n');

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
        System.out.println("All " + THREADS + " Client Threads Started: " + new Date().toString());

        ex.shutdown();
        while (!ex.isTerminated()) {
        }

        System.out.println("All Client Threads Finished: " + new Date().toString());
        long end = System.currentTimeMillis();
        System.out.println("Total time taken: " + (end - start) + "ms.");
        System.out.println("Throughput of this test: " + (SKIER_COUNT / ((end - start) / 1000)) + "\n");

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

        LatencyStat.printLatencyStats(latencies);
        generateChart(latencies);
    }

    private static void generateChart(List<Long> latencies) {
        String fileName = "get_latency_thread_" + THREADS + "_single_server";
        ChartMaker chartMaker = new ChartMaker();
        try {
            chartMaker.makeChart(latencies, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
