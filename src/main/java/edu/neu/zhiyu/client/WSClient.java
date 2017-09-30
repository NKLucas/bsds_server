package edu.neu.zhiyu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WSClient extends Thread {

    private WebTarget webTarget;
    private int iterations;
    private AtomicInteger totalRequest;
    private AtomicInteger successRequest;
    private String testMessage = "OK! This is a test!";
    private List<Long> latencies = new ArrayList<>();


    public WSClient(String url, int iterations, AtomicInteger totalRequest, AtomicInteger successRequest) {
        Client client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
        this.iterations = iterations;
        this.totalRequest = totalRequest;
        this.successRequest = successRequest;

    }

    public int postText(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity,
                javax.ws.rs.core.MediaType.TEXT_PLAIN),
                Integer.class);
    }

    public String getStatus() throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            String status = this.getStatus();
            totalRequest.getAndAdd(1);
            if (status.equals("alive")){
                successRequest.getAndAdd(1);
            }
            long end = System.currentTimeMillis();
            latencies.add(end - start);

            start = System.currentTimeMillis();
            int length = this.postText(testMessage);
            totalRequest.getAndAdd(1);
            if (length == testMessage.length()) {
                successRequest.getAndAdd(1);
            }
            end = System.currentTimeMillis();
            latencies.add(end - start);
        }
    }

    public List<Long> getLatencies() {
        return latencies;
    }
}
