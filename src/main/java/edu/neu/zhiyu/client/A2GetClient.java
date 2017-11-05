package edu.neu.zhiyu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class A2GetClient extends Thread {

    private WebTarget webTarget;
    private List<Long> latencies = new ArrayList<>();
    private List<String> data;
    private int requestSent = 0;
    private int successReq = 0;


    public A2GetClient(String url, List<String> data) {
        Client client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
        this.data = data;
    }

    public Response getStats(String skierID, int day) throws ClientErrorException {
        String path = "/myvert/" + skierID + "/" + day;
        WebTarget web = webTarget.path(path);
        return web.request().get();
    }

    @Override
    public void run() {
        for (String d : data) {
            long start = System.currentTimeMillis();
            Response response = this.getStats(d, 1);
            response.close();
            requestSent += 1;
            if (response.getStatus() == 200) {
                successReq += 1;
            }
            long end = System.currentTimeMillis();
            latencies.add(end - start);
        }
    }

    public List<Long> getLatencies() {
        return latencies;
    }

    public int getRequestSent() {
        return requestSent;
    }

    public int getSuccessReq() {
        return successReq;
    }
}