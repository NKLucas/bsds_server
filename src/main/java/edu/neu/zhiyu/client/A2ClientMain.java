package edu.neu.zhiyu.client;

import com.google.gson.Gson;
import edu.neu.zhiyu.model.RFIDLiftData;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class A2ClientMain {
    private static String baseUrl = "http://ec2-34-209-86-171.us-west-2.compute.amazonaws.com:8080/bsds-server/api/ski";
    private static String defaultPort = "8080";
    private static String serviceName = "bsds-server";
    private static String apiPath = "/api/ski/status";
    private static String localUrl = "http://localhost:9234/api/ski";

    private static final String SEPERATER = ",";
    private static int THREADS = 100;

    public static void main(String[] args) throws Exception {
        List<String> lifts = new ArrayList<>(80000);
        List<A2Client> clients = new ArrayList<>();

        String filePath = "src/main/resources/BSDSAssignment2Day1.csv";
        Scanner scanner = new Scanner(new File(filePath));
        System.out.println(scanner.nextLine());
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
//            if (lifts.size() >= 100000) {
//                break;
//            }
        }
        System.out.println("Total Record Read: " + lifts.size());

        ExecutorService ex = Executors.newFixedThreadPool(THREADS);
        int countForEachThreads = lifts.size() / THREADS;

        System.out.println("Time Start: " + new Date().toString());
        for (int i = 0; i < THREADS; i++) {
            int startIndex = i * countForEachThreads;
            int endIndex = i == THREADS - 1 ? lifts.size() : (i + 1) * countForEachThreads;
            List<String> data = lifts.subList(startIndex, endIndex);
            System.out.println("Data size for thread i: " + i + " is: " + data.size());
            A2Client client = new A2Client(baseUrl, data);
            clients.add(client);
            ex.submit(client);
        }
        ex.shutdown();

        while (!ex.isTerminated()) {}

        System.out.println("End Start: " + new Date().toString());

        for (A2Client c : clients) {
            System.out.println(getSum(c.getLatencies()));
        }

    }

    private static long getSum(List<Long> latencies) {
        long sum = 0;
        for (long lat : latencies) {
            sum += lat;
        }
        return sum;
    }

}
