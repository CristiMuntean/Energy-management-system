package org.example;

import java.util.Timer;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java Main <appConfigName> <sensorFileName>");
            System.exit(1);
        }
        System.out.println(args[0]);
        JsonReader jsonReader = new JsonReader();
        String deviceId = jsonReader.readDeviceId(args[0]);
        System.out.println("Device id: " + deviceId);

        CsvReaderTask csvReaderTask = new CsvReaderTask(deviceId, args[1]);
        Timer timer = new Timer(true);
//        timer.scheduleAtFixedRate(csvReaderTask, 0, 60000 * 10);
        timer.scheduleAtFixedRate(csvReaderTask, 0, 3000);
        synchronized (csvReaderTask) {
            try {
                csvReaderTask.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
