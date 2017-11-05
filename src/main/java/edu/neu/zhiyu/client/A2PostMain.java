package edu.neu.zhiyu.client;

import com.google.gson.Gson;
import edu.neu.zhiyu.model.RFIDLiftData;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A2PostMain {

    // small
//    private static String REMOTE_URL = "http://ec2-34-209-86-171.us-west-2.compute.amazonaws.com:8080/bsds-server/api/ski";
    // large
    private static String REMOTE_URL = "http://ec2-34-209-12-29.us-west-2.compute.amazonaws.com:8080/bsds-server/api/ski";
    private static String LOCAL_URL = "http://localhost:9234/api/ski";

    private static int THREADS = 100;

    public static void main(String[] args) throws Exception {
        List<String> lifts = new ArrayList<>(800000);
        String filePath = "src/main/resources/BSDSAssignment2Day1.csv";

        loadDataFromFile(lifts, filePath);
        System.out.println("Total Record Read: " + lifts.size());

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
        System.out.println("All Client Threads Started: " + new Date().toString());

        ex.shutdown();
        while (!ex.isTerminated()) {}

        System.out.println("All Client Threads Finished: " + new Date().toString());
        long end = System.currentTimeMillis();
        System.out.println("Total time taken: " + (end - start) + "ms. \n");

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
            if (lifts.size() >= 1000) {
                break;
            }
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

        printLatencyStats(latencies);
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
        for(Long lat : latencies) {
            sum += lat;
        }
        return sum / latencies.size();
    }

}
