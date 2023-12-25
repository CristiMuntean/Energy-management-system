package ro.tuc.ds2020.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.LiveReadingDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.WarningMessageDTO;
import ro.tuc.ds2020.entities.Reading;
import ro.tuc.ds2020.services.mq.DeviceOperation;
import ro.tuc.ds2020.services.mq.UserOperation;

@Component
public class MessageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQService.class);
    private final DeviceService deviceService;
    private final UserService userService;
    private final ReadingService readingService;
    private final WebSocketSender webSocketSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MessageProcessor(
            DeviceService deviceService,
            UserService userService,
            ReadingService readingService,
            WebSocketSender webSocketSender) {
        this.deviceService = deviceService;
        this.userService = userService;
        this.readingService = readingService;
        this.webSocketSender = webSocketSender;
    }

    public void processDeviceResponse(String responseJson) {
        try {
            DeviceOperation deviceOperation = objectMapper.readValue(responseJson, DeviceOperation.class);
            String operation = deviceOperation.getOperation();
            switch (operation) {
                case "insert", "update" -> {
                    deviceService.insertOrUpdate(
                            new DeviceDTO(deviceOperation.getDeviceId(),
                                    deviceOperation.getMaxHourlyEnergyConsumption(),
                                    deviceOperation.getUserId()));
                }
                case "delete" -> deviceService.deleteDevice(deviceOperation.getDeviceId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processUserResponse(String responseJson) {
        try {
            UserOperation userOperation  = objectMapper.readValue(responseJson, UserOperation.class);
            String operation = userOperation.getOperation();
            switch (operation) {
                case "insert", "update" -> {
                    userService.insertOrUpdate(
                            new UserDTO(userOperation.getId(),
                                    userOperation.getUsername()));
                }
                case "delete" -> userService.deleteUser(userOperation.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processReading(String readingJson) {
        Long nextid = readingService.getMaxId();
        nextid = nextid == null ? 1 : nextid + 1;

        try {
            Reading reading = objectMapper.readValue(readingJson, Reading.class);
            reading.setId(nextid);

            LOGGER.info("Reading id: " + reading.getId() + ", deviceId:" + reading.getDeviceId() + ", readingValue:" + reading.getMeasurementValue());
            if(deviceExists(reading.getDeviceId())) {
                readingService.insert(reading);
                updateMonitoringDataForDevice(reading.getDeviceId(), reading.getTimestamp());
            }
            else {
                LOGGER.info("Device " + reading.getDeviceId() + " does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deviceExists(Long deviceId) {
        return deviceService.findDeviceById(deviceId) != null;
    }

    private void updateMonitoringDataForDevice(Long deviceId, long timestamp) {
        Double currentHourlyConsumption = readingService.getCurrentHourlyConsumption(deviceId, timestamp);
        Integer maxHourlyConsumption =
                deviceService.findDeviceById(deviceId)
                        .getMaxHourlyEnergyConsumption();
        String username = userService.findUserById(deviceService.findDeviceById(deviceId).getUserId()).getUsername();
        LOGGER.info("Device " + deviceId + " current hourly consumption: " + currentHourlyConsumption);
        webSocketSender.sendMessage("/topic/reading/" + username ,
                new LiveReadingDTO(deviceId,
                        readingService.getHourFromTimestamp(timestamp),
                        readingService.getDayFromTimeStamp(timestamp),
                        readingService.getMonthFromTimeStamp(timestamp),
                        readingService.getYearFromTimeStamp(timestamp),
                        currentHourlyConsumption));
        LOGGER.info("Device " + deviceId + " max hourly consumption: " + maxHourlyConsumption);
        if(currentHourlyConsumption > maxHourlyConsumption) {
            LOGGER.warn("Device " + deviceId + " exceeded max hourly consumption");
            //send message to front-end via websocket
            webSocketSender.sendMessage("/topic/notification/" + username,
                    new WarningMessageDTO(deviceId,
                            "exceeded max hourly consumption")
            );
        }
    }
}
