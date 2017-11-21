package edu.neu.zhiyu;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import edu.neu.zhiyu.sqsUtil.SQSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQSSubscriber {

    private static AmazonSQS sqs;
    private static String QURL;

    public static void init() {
        if (sqs == null) {
            sqs = SQSUtil.getAmazonSQSClient();
        }
        if (QURL == null) {
            QURL = sqs.listQueues().getQueueUrls().get(0);
            System.out.println("Queue URL is: " + QURL);
        }
    }

    private static void showMessages(List<Message> messages) {
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
    }

    public static List<Message> receiveMessages() {
        System.out.println("Receiving messages from: " + QURL);
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(QURL);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    public static void deleteMessages(List<Message> messages) {
        List<DeleteMessageBatchRequestEntry> msgs = new ArrayList<>();
        for (Message message : messages) {
            DeleteMessageBatchRequestEntry dmbre =
                    new DeleteMessageBatchRequestEntry(
                            message.getMessageId(),
                            message.getReceiptHandle());
            msgs.add(dmbre);
        }
        sqs.deleteMessageBatch(QURL, msgs);
    }






}
