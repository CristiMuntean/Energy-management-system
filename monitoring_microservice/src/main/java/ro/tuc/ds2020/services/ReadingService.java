package ro.tuc.ds2020.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.entities.Reading;
import ro.tuc.ds2020.repositories.ReadingRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.*;

@Service
public class ReadingService {

    private final ReadingRepository readingRepository;

    @Autowired
    public ReadingService(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    public String getReadingsForDay(String date, List<Long> ids) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
        long startTimestamp = startOfDay.toEpochSecond(UTC) * 1000;
        long endTimestamp = endOfDay.toEpochSecond(UTC) * 1000;

        Map<Long, Map<Integer, Double>> readings = getReadingsByHour(ids, startTimestamp, endTimestamp);

        return createResponse(readings);
    }

    private String createResponse(Map<Long, Map<Integer, Double>> readings) {
        ObjectNode mainNode = JsonNodeFactory.instance.objectNode();
        ArrayNode devicesNode = mainNode.putArray("devices");
        for(Map.Entry<Long, Map<Integer, Double>> entry : readings.entrySet()) {
            ObjectNode deviceNode = devicesNode.addObject();
            deviceNode.put("id", entry.getKey());
            ArrayNode readingsArray = deviceNode.putArray("readings");
            for(Map.Entry<Integer, Double> reading : entry.getValue().entrySet()) {
                ObjectNode readingNode = readingsArray.addObject();
                readingNode.put("hour", reading.getKey());
                readingNode.put("reading", reading.getValue());
            }
        }

        return mainNode.toString();
    }

    private Map<Long, Map<Integer, Double>> getReadingsByHour(List<Long> ids, long startTimestamp, long endTimestamp) {
        Map<Long, Map<Integer, Double>> allReadings = new HashMap<>();
        for (Long id : ids) {
            List<Reading> readings = readingRepository.getReadingsForDeviceBetweenTimestamps(id, startTimestamp, endTimestamp);
            System.out.println(readings);

            Map<Integer, List<Reading>> readingsByHour = readings.stream()
                    .collect(Collectors.groupingBy(
                            reading -> LocalDateTime.ofInstant(Instant.ofEpochMilli(
                                            reading.getTimestamp()),
                                    ZoneId.of("UTC")
                            ).getHour()
                    ));
            Map<Integer, Double> readingsForDevices = readingsByHour.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream().mapToDouble(Reading::getMeasurementValue).sum()
                    ));
            for(int i=0;i<24;i++) {
                if(!readingsForDevices.containsKey(i))
                    readingsForDevices.put(i, null);
            }

            allReadings.put(id, readingsForDevices);

        }

        return allReadings;
    }

    public Long getMaxId() {
        return readingRepository.getMaxId();
    }

    public void insert(Reading reading) {
        readingRepository.save(reading);
    }

    public Double getCurrentHourlyConsumption(Long deviceId, long timestamp) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"));
        LocalDateTime startOfHour = localDateTime.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfHour = startOfHour.plusHours(1).minusNanos(1);
        LocalDateTime startOfPrevHour = startOfHour.minusHours(1);
        LocalDateTime endOfPrevHour = endOfHour.minusHours(1);

        long startCurrHourTimestamp = startOfHour.toEpochSecond(UTC) * 1000;
        long endCurrHourTimestamp = endOfHour.toEpochSecond(UTC) * 1000;
        long startPrevHourTimestamp = startOfPrevHour.toEpochSecond(UTC) * 1000;
        long endPrevHourTimestamp = endOfPrevHour.toEpochSecond(UTC) * 1000;

        List<Reading> readingsCurrHour = readingRepository.getReadingsForDeviceBetweenTimestamps(deviceId, startCurrHourTimestamp, endCurrHourTimestamp);
        List<Reading> readingsPrevHour = readingRepository.getReadingsForDeviceBetweenTimestamps(deviceId, startPrevHourTimestamp, endPrevHourTimestamp);
        readingsPrevHour.sort(Comparator.comparing(Reading::getTimestamp).reversed());
        readingsCurrHour.sort(Comparator.comparing(Reading::getTimestamp).reversed());
        Double prevHourConsumption = readingsPrevHour.size() == 0 ? 0 :
                readingsPrevHour.get(0).getMeasurementValue();
        return readingsCurrHour.size() == 0 ? 0 :
                readingsCurrHour.get(0).getMeasurementValue() - prevHourConsumption;
    }

    public Integer getHourFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).getHour();
    }

    public Integer getDayFromTimeStamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).getDayOfMonth();
    }

    public Integer getMonthFromTimeStamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).getMonth().getValue();
    }

    public Integer getYearFromTimeStamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).getYear();
    }
}
