package edu.neu.zhiyu.client;


public class WSClientMain {

    private static String baseUrl = "http://ec2-34-209-86-171.us-west-2.compute.amazonaws.com:8080/bsds-server/api/";

    public static void main(String[] argv) {
        // Please, do not remove this line from file template, here invocation of web service will be inserted
        String url = baseUrl + "status";
        WSClient client = new WSClient(url);
        String status = client.getStatus();
        System.out.println("getStatus got response: " + status);

        String message = "OK! This is a test!";
        int count = client.postText(message);
        System.out.println("postText got response: " + count);
    }
}
