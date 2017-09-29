package edu.neu.zhiyu.client;


public class WSClientMain {
    public static void main(String[] argv) {
        // Please, do not remove this line from file template, here invocation of web service will be inserted
        WSClient client = new WSClient("http://localhost:9234/api/status");
        String status = client.getStatus();
        System.out.println("getStatus got response: " + status);

        String message = "OK! This is a test!";
        int count = client.postText(message);
        System.out.println("postText got response: " + count);
    }
}
