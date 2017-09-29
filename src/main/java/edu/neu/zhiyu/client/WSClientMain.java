package edu.neu.zhiyu.client;


import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WSClientMain {

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

        System.out.println("Client stating...");
        System.out.println("Current time: " + new Date().toString() + "\n");

        String url = base + ":" + port + "/" + serviceName + apiPath;
        ExecutorService ex = Executors.newFixedThreadPool(MAX_THREAD);

        long start = System.currentTimeMillis();

        for (int id = 0; id < MAX_THREAD; id++) {
            WSClient client = new WSClient(url, ITERATIONS, totalRequest, successRequest);
            ex.submit(client);
        }

        System.out.println("All threads are now running...");

        ex.shutdown();
        while (!ex.isTerminated()) {}
        long end = System.currentTimeMillis();

        System.out.println("All threads are now completed.");
        System.out.println("Total request send: " + totalRequest);
        System.out.println("Total successful responses: " + successRequest);
        System.out.println();
        System.out.println("Current time: " + new Date().toString());
        System.out.println("Total time taken: " + (end - start) + "ms");
    }
}
