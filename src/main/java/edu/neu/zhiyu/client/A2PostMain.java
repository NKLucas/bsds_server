package edu.neu.zhiyu.client;

import com.google.gson.Gson;
import edu.neu.zhiyu.model.RFIDLiftData;
import edu.neu.zhiyu.utils.ChartMaker;
import edu.neu.zhiyu.utils.LatencyStat;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A2PostMain {

    private static String REMOTE_URL = "http://Test-Load-Balancer-1542817281.us-west-2.elb.amazonaws.com:8080/bsds-server/api/ski";
    private static String LOCAL_URL = "http://localhost:9234/api/ski";

    private static int THREADS = 100;

    public static void main(String[] args) throws Exception {
        List<String> lifts = new ArrayList<>();
        String filePath = "src/main/resources/BSDSAssignment2Day999.csv";

        loadDataFromFile(lifts, filePath);
        System.out.println("Total Record Read: " + lifts.size());
        System.out.println("Request URL: " + REMOTE_URL);
        System.out.println("Threads Number: " + THREADS + '\n');

        List<A2PostClient> clients = new ArrayList<>();
        ExecutorService ex = Executors.newFixedThreadPool(THREADS);
        int countForEachThreads = lifts.size() / THREADS;

        for (int i = 0; i < THREADS; i++) {
            int startIndex = i * countForEachThreads;
            int endIndex = i == THREADS - 1 ? lifts.size() : (i + 1) * countForEachThreads;
            List<String> data = lifts.subList(startIndex, endIndex);
            A2PostClient client = new A2PostClient(REMOTE_URL, data);
            clients.add(client);
            ex.submit(client);
        }

        long start = System.currentTimeMillis();
        System.out.println("All " + THREADS + " Client Threads Started: " + new Date().toString());

        ex.shutdown();
        while (!ex.isTerminated()) {}

        System.out.println("All Client Threads Finished: " + new Date().toString());
        long end = System.currentTimeMillis();
        System.out.println("Total time taken: " + (end - start) + "ms.");
        System.out.println("Throughput of this test: " + (lifts.size() / ((end - start) / 1000)) + "\n");

        calculateAndPrintStats(clients);
    }

    private static void loadDataFromFile(List<String> lifts, String filePath) throws Exception {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.nextLine();
        Gson gson = new Gson();

        while (scanner.hasNext()) {
            String record  = scanner.nextLine();
            String[] details = record.split(",");
            if (details.length != 5) {
                System.out.println("Invalid Record Found!");
                continue;
            }
            String restoreID = details[0];
            int day = Integer.parseInt(details[1]);
            String skierID = details[2];
            String liftID = details[3];
            String time = details[4];
            RFIDLiftData liftData = new RFIDLiftData(restoreID, day, skierID, liftID, time);
            lifts.add(gson.toJson(liftData));
//            if (lifts.size() >= 1000) {
//                break;
//            }
        }
    }

    private static void calculateAndPrintStats(List<A2PostClient> clients) {
        System.out.println("Some stats: \n");
        List<Long> latencies = new ArrayList<>();
        int totalRequest = 0;
        int successRequest = 0;

        for (A2PostClient client : clients) {
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
        String fileName = "post_latency_thread_" + THREADS + "_load_balancer";
        ChartMaker chartMaker = new ChartMaker();
        try {
            chartMaker.makeChart(latencies, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
