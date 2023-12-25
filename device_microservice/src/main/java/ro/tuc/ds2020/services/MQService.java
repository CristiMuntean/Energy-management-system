package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.services.mq.DeviceOperation;
import ro.tuc.ds2020.services.mq.QueueConnector;
import ro.tuc.ds2020.services.mq.QueueConsumer;
import ro.tuc.ds2020.services.mq.QueueProducer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class MQService implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQService.class);
    private final DeviceRepository deviceRepository;

    private QueueConsumer consumer;
    private QueueProducer producer;
    private List<String> queueNames;

    @Autowired
    public MQService(QueueConnector connector, DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;

        queueNames = connector.getQueueNames();
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
                    if (readMessage.contains("getAllDevices")) {
                        List<DeviceOperation> deviceDTOList = deviceRepository
                                .getDevicesWithUserId()
                                .stream()
                                .map(DeviceBuilder::deviceWithUserIdDTO)
                                .map(el -> new DeviceOperation("insert", el.getId(), el.getMaxHourlyEnergyConsumption(),el.getUserId()))
                                .toList();
                        String responseQueue = queueNames.stream()
                                .filter(name -> name.toLowerCase().contains("send"))
                                .findFirst()
                                .orElse(null);
                        if (responseQueue != null) {
                            deviceDTOList.stream()
                                    .map(DeviceOperation::toJson)
                                    .forEach(el -> producer.sendMessageToQueue(el, responseQueue));
                        }
                    }
                }
            };
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(repeatedTask, 0, 1000);
        }
    }

    public void sendDeviceUpdate(DeviceDTO deviceDTO, String operation) {
        String sendQueue = queueNames.stream()
                .filter(name -> name.toLowerCase().contains("send"))
                .findFirst()
                .orElse(null);
        if (sendQueue != null) {
            DeviceWithUserIdDTO deviceWithUserIdDTO = deviceRepository.getDevicesWithUserId()
                            .stream().map(DeviceBuilder::deviceWithUserIdDTO)
                            .filter(el -> el.getId().equals(deviceDTO.getId()))
                                    .findFirst().orElse(null);
            producer.sendMessageToQueue(
                    new DeviceOperation(
                            operation,
                            deviceWithUserIdDTO.getId(),
                            deviceWithUserIdDTO.getMaxHourlyEnergyConsumption(),
                            deviceWithUserIdDTO.getUserId())
                            .toJson(),
                    sendQueue);
        }
    }

    public void sendDeviceDelete(DeviceDTO deviceDTO) {
        String sendQueue = queueNames.stream()
                .filter(name -> name.toLowerCase().contains("send"))
                .findFirst()
                .orElse(null);
        if (sendQueue != null) {
            DeviceWithUserIdDTO deviceWithUserIdDTO = deviceRepository.getDevicesWithUserId()
                    .stream().map(DeviceBuilder::deviceWithUserIdDTO)
                    .filter(el -> el.getId().equals(deviceDTO.getId()))
                    .findFirst().orElse(null);
            if(deviceWithUserIdDTO == null) return;
            producer.sendMessageToQueue(
                    new DeviceOperation(
                            "delete",
                            deviceWithUserIdDTO.getId(),
                            deviceWithUserIdDTO.getMaxHourlyEnergyConsumption(),
                            deviceWithUserIdDTO.getUserId())
                            .toJson(),
                    sendQueue);
        }
    }
}
