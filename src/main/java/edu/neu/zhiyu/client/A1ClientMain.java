package edu.neu.zhiyu.client;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class A1ClientMain {

    private static String baseUrl = "http://ec2-34-209-86-171.us-west-2.compute.amazonaws.com";
    private static String defaultPort = "8080";
    private static String serviceName = "bsds-server";
    private static String apiPath = "/api/status";
    private static String localUrl = "http://localhost:9234/api/status";

    public static void main(String[] argv) {
        String base = baseUrl;
        String port = defaultPort;
        int MAX_THREAD = 10;
        int ITERATIONS = 100;
        AtomicInteger totalRequest = new AtomicInteger(0);
        AtomicInteger successRequest = new AtomicInteger(0);

        List<A1Client> clients = new ArrayList<>();
        List<Long> latencies = new ArrayList<>();

        if (argv.length == 4) {
            MAX_THREAD = Integer.valueOf(argv[0]);
            ITERATIONS = Integer.valueOf(argv[1]);
            base = argv[2];
            port = argv[3];
        }

        System.out.println("Server Address: " + base);
        System.out.println("Connect Port: " + port);
        System.out.println("API Path: " + apiPath);
        System.out.println("Max Threads: " + MAX_THREAD);
        System.out.println("Iterations: " + ITERATIONS + "\n");

        System.out.println("Client starting...");
        System.out.println("Current time: " + new Date().toString() + "\n");

        String url = base + ":" + port + "/" + serviceName + apiPath;
        ExecutorService ex = Executors.newFixedThreadPool(MAX_THREAD);

        long start = System.currentTimeMillis();

        for (int id = 0; id < MAX_THREAD; id++) {
            A1Client client = new A1Client(url, ITERATIONS, totalRequest, successRequest);
            clients.add(client);
            ex.submit(client);
        }

        System.out.println("All threads are now running...");

        ex.shutdown();
        while (!ex.isTerminated()) {}
        long end = System.currentTimeMillis();

        System.out.println("All threads are now completed.");
        System.out.println("Total requests send: " + totalRequest);
        System.out.println("Total successful responses: " + successRequest);
        System.out.println();
        System.out.println("Current time: " + new Date().toString());
        System.out.println("Total time taken: " + (end - start) + "ms. \n");

        System.out.println("Some stats: \n");

        for (A1Client client : clients) {
            for (Long lat : client.getLatencies()) {
                latencies.add(lat);
            }
        }

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
