package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.repositories.UserRepository;
import ro.tuc.ds2020.services.mq.QueueConnector;
import ro.tuc.ds2020.services.mq.QueueConsumer;
import ro.tuc.ds2020.services.mq.QueueProducer;
import ro.tuc.ds2020.services.mq.UserOperation;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Service
public class MQService implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQService.class);
    private final UserRepository userService;
    private QueueConsumer consumer;
    private QueueProducer producer;
    private List<String> queueNames;

    @Autowired
    public MQService(QueueConnector connector, UserRepository userService) {
        this.userService = userService;
        this.queueNames = connector.getQueueNames();
        this.consumer = new QueueConsumer(connector);
        this.producer = new QueueProducer(connector);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null)
            listenForRequests();
    }

    private void listenForRequests() {
        String requestQueue = queueNames.stream()
                .filter(name -> name.toLowerCase().contains("request"))
                .findFirst()
                .orElse(null);
        if (requestQueue != null) {
            TimerTask repeatedTask = new TimerTask() {
                public void run() {
                    String readMessage = consumer.readMessageFromQueue(requestQueue);
                    LOGGER.info("Received message: " + readMessage + " from queue: " + requestQueue);
                    if (readMessage.contains("getAllClientUsernamesAndIds")) {
                        List<UserOperation> userOperationList = userService
                                .findAll()
                                .stream()
                                .map(el -> new UserOperation(el.getId(), el.getUsername(),"insert"))
                                .collect(Collectors.toList());
                        String responseQueue = queueNames.stream()
                                .filter(name -> name.toLowerCase().contains("send"))
                                .findFirst()
                                .orElse(null);
                        if (responseQueue != null) {
                            userOperationList.stream()
                                    .map(UserOperation::toJson)
                                    .forEach(el -> producer.sendMessageToQueue(el, responseQueue));
                        }
                    }
                }
            };
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(repeatedTask, 0, 1000);
        }
    }

    public void sendUserUpdate(UserDTO userDTO, String operation) {
        queueNames.stream()
                .filter(name -> name.toLowerCase().contains("send"))
                .findFirst().ifPresent(sendQueue -> producer.sendMessageToQueue(
                        new UserOperation(
                                userDTO.getId(),
                                userDTO.getUsername(),
                                operation)
                                .toJson(),
                        sendQueue));
    }
}
