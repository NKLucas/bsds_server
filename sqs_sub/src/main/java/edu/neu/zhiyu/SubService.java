package edu.neu.zhiyu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.neu.zhiyu.dao.ServerLatencyDAO;
import com.amazonaws.services.sqs.model.Message;
import edu.neu.zhiyu.model.ServerLatency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SubService {

    private static final Logger logger = Logger.getLogger(SubService.class.getName());
    private static ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
    private static ServerLatencyDAO latencyDAO;
    private static long DELAY = 0;          // no delay
    private static long PERIOD = 2000;      // 2 seconds

    public static void main(String[] args) {
        SQSSubscriber.init();
        latencyDAO = ServerLatencyDAO.getInstance();
        scheduledService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        transportMessageFromSQSToDB();
                    }
                }, DELAY, PERIOD, TimeUnit.MILLISECONDS);

    }

    private static void transportMessageFromSQSToDB() {
        try {
            List<Message> messages = SQSSubscriber.receiveMessages();
            System.out.println("Get " + messages.size() + " messages from SQS.");
            if (messages.size() == 0) {return;}
            Gson gson = new Gson();
            List<ServerLatency> latencies = new ArrayList<>();
            for (Message message : messages) {
                String body = message.getBody();
                List<String> entries = gson.fromJson(body, new TypeToken<List<String>>() {
                }.getType());
                for (String entry : entries) {
                    ServerLatency latency = gson.fromJson(entry, ServerLatency.class);
                    latencies.add(latency);
                }
            }
            latencyDAO.batchInsert(latencies);
            SQSSubscriber.deleteMessages(messages);
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Failed to transport message from sqs to database. " + e);
        }
    }

}
