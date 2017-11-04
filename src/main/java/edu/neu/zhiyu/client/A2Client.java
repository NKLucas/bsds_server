package edu.neu.zhiyu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class A2Client extends Thread {

    private WebTarget webTarget;
    private List<Long> latencies = new ArrayList<>();
    private List<String> data;
    private int requestSent = 0;
    private int successReq = 0;


    public A2Client(String url, List<String> data) {
        Client client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
        this.data = data;
    }

    public Response postData (String record) throws ClientErrorException {
        WebTarget web = webTarget.path("/load");
        return web.request(APPLICATION_JSON).post(Entity.entity(record, APPLICATION_JSON));
    }

    public Response getStatus() throws ClientErrorException {
        WebTarget web = webTarget.path("/myvert/1/100");
        return web.request().get();
    }

    @Override
    public void run() {
        for (String d : data) {
            long start = System.currentTimeMillis();
            Response response = this.postData(d);
            response.close();
            requestSent += 1;
            if (requestSent % 4000 == 0) {
                System.out.println(requestSent + " reqeusts been sent!");
            }
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