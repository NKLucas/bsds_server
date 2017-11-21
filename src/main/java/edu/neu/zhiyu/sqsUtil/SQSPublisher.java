package edu.neu.zhiyu.sqsUtil;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.ArrayList;
import java.util.List;

public class SQSPublisher {

    private static AmazonSQS sqs;
    private static String QURL;
    private static final String QUEUE_NAME = "server_metrics_queue";

    public static void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials("", "");
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2)
                .build();
        QURL = createQueue();
//        sqs = AmazonSQSClientBuilder.defaultClient();

//        AWSCredentials credentials = null;
//        try {
//            credentials = new ProfileCredentialsProvider("rootkey.csv").getCredentials();
//        } catch (Exception e) {
//            throw new AmazonClientException(
//                    "Cannot load the credentials from the credential profiles file. " +
//                            "Please make sure that your credentials file is at the correct " +
//                            "location (~/.aws/credentials), and is in valid format.",
//                    e);
//        }
//
//        sqs = AmazonSQSClientBuilder.standard()
//                .withRegion(Regions.US_WEST_2)
//                .build();

    }

    public static void sendBatchMessage(List<String> messages) {
        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<>();

            for (int i = 0; i < messages.size(); i++) {
                String msgID = System.currentTimeMillis() + "_" + i;
                String msg = messages.get(i);
                SendMessageBatchRequestEntry e = new SendMessageBatchRequestEntry(msgID, msg);
                entries.add(e);
            }
            SendMessageBatchRequest batchRequest = new SendMessageBatchRequest(QURL, entries);
            sqs.sendMessageBatch(batchRequest);
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    private static String createQueue() {
        return sqs.createQueue(QUEUE_NAME).getQueueUrl();
    }

}
