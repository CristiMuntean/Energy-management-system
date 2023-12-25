package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.services.mq.QueueConnector;
import ro.tuc.ds2020.services.mq.QueueConsumer;
import ro.tuc.ds2020.services.mq.QueueProducer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class MQService implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQService.class);

    private final MessageProcessor messageProcessor;

    private QueueConsumer consumer;
    private QueueProducer producer;
    private List<String> queueNames;

    @Autowired
    public MQService(QueueConnector connector,
                     MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;

        queueNames = connector.getQueueNames();
        this.consumer = new QueueConsumer(connector);
        this.producer = new QueueProducer(connector);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null)
            init();
    }

    public void init() {
        requestClientUsernamesAndIds();
        requestDevices();
        readMessagesFromQueues();
    }

    private void requestClientUsernamesAndIds() {
        String requestQueue = queueNames.stream()
                .filter(name -> name.toLowerCase().contains("request"))
                .filter(name -> name.toLowerCase().contains("user"))
                .findFirst()
                .orElse(null);
        if(requestQueue != null) {
            String requestMessage = """
                    {
                        "request: "getAllClientUsernamesAndIds"
                    }
                    """;
            producer.sendMessageToQueue(requestMessage, requestQueue);
        }
    }

    private void requestDevices() {
        String requestQueue = queueNames.stream()
                .filter(name -> name.toLowerCase().contains("request"))
                .filter(name -> name.toLowerCase().contains("device"))
                .findFirst()
                .orElse(null);
        if(requestQueue != null) {
            String requestMessage = """
            {
                "request": "getAllDevices"
            }
            """;
            producer.sendMessageToQueue(requestMessage, requestQueue);
        }
    }

    private void readMessagesFromQueues() {
        for(String queueName: queueNames) {
            LOGGER.info("Reading messages from queue: " + queueName);

            if(queueName.toLowerCase().contains("monitoring"))
                readMessagesFromMonitoringQueue(queueName);
            if(queueName.toLowerCase().contains("send") && queueName.toLowerCase().contains("device"))
                readMessagesFromDeviceRequestQueue(queueName);
            if(queueName.toLowerCase().contains("send") && queueName.toLowerCase().contains("user"))
                readMessagesFromUserRequestQueue(queueName);
        }
    }

    private void readMessagesFromDeviceRequestQueue(String queueName) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                String responseJson = consumer.readMessageFromQueue(queueName);

                messageProcessor.processDeviceResponse(responseJson);
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(repeatedTask, 0, 1000);
    }

    private void readMessagesFromUserRequestQueue(String queueName) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                String responseJson = consumer.readMessageFromQueue(queueName);

                messageProcessor.processUserResponse(responseJson);
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(repeatedTask, 0, 1000);
    }

    private void readMessagesFromMonitoringQueue(String queueName) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                String readingJson = consumer.readMessageFromQueue(queueName);

                messageProcessor.processReading(readingJson);
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(repeatedTask, 0, 100);
    }
}
