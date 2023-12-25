package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class CsvReaderTask extends TimerTask {
    private final String deviceId;
    private String[] allData;
    private int currentIndex = 0;
    private Double previousValue = 0.;
    private QueueProducer producer;
    public CsvReaderTask(String deviceId, String sensorFileName) {
        this.deviceId = deviceId;
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(sensorFileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: sensor.csv");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                data.append(line + " ");
            }
            reader.close();
            this.allData = data.toString().split(" ");
            QueueConnector connector = new QueueConnector();
            producer = new QueueProducer(connector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Double newValue = Double.parseDouble(allData[currentIndex++]);
//            Double reading = newValue - previousValue;
            Double reading = newValue;

            System.out.println("Reading: " + reading);
            producer.sendMessage(reading, deviceId);

            previousValue = newValue;
            if(currentIndex == allData.length) {
                System.out.println("No more lines to read.\n");

                this.cancel();
                synchronized (this) {
                    this.notify();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
