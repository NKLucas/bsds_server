package edu.neu.zhiyu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.concurrent.atomic.AtomicInteger;

public class WSClient extends Thread {

    private WebTarget webTarget;
    private int iterations;
    private AtomicInteger totalRequest;
    private AtomicInteger successRequest;
    private String testMessage = "OK! This is a test!";

    public WSClient(String url, int iterations, AtomicInteger totalRequest, AtomicInteger successRequest) {
        Client client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
        this.iterations = iterations;
        this.totalRequest = totalRequest;
        this.successRequest = successRequest;

    }

    public int postText(Object requestEntity) throws ClientErrorException {
        totalRequest.getAndAdd(1);
        int length = webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity,
                javax.ws.rs.core.MediaType.TEXT_PLAIN),
                Integer.class);

        if (length == testMessage.length()) {
            successRequest.getAndAdd(1);
        }
        return length;
    }

    public String getStatus() throws ClientErrorException {
        totalRequest.getAndAdd(1);
        String status = webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        if (status.equals("alive")){
            successRequest.getAndAdd(1);
        }
        return status;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            this.getStatus();
            this.postText(testMessage);
        }
    }

}
