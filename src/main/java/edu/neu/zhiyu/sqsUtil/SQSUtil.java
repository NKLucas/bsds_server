package edu.neu.zhiyu.sqsUtil;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SQSUtil {
    private static AmazonSQS sqs;

    public static AmazonSQS getAmazonSQSClient() {
        if (sqs != null) {
            return sqs;
        }

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

        BasicAWSCredentials credentials = new BasicAWSCredentials("", "");
        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2)
                .build();
        return sqs;
    }

    public static String createQueue(AmazonSQS sqs, String queueName) {
        return sqs.createQueue(queueName).getQueueUrl();
    }

}
