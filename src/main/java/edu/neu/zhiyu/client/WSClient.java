package edu.neu.zhiyu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class WSClient {

    private WebTarget webTarget;

    public WSClient(String url) {
        Client client = ClientBuilder.newClient();
        this.webTarget = client.target(url);
    }

    public int postText(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity,
                javax.ws.rs.core.MediaType.TEXT_PLAIN),
                Integer.class);
    }

    public String getStatus() throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }
}
