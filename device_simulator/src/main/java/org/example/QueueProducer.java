package org.example;

import javax.jms.*;

public class QueueProducer {
    private QueueConnector connector;
    private static int counter = 0;
    private long timestamp;

    public QueueProducer(QueueConnector connector) {
        this.connector = connector;
        timestamp = System.currentTimeMillis();
    }

    public boolean sendMessage(Double reading, String deviceId) {
        try {
            Connection connection = connector.getConnectionFactory().createConnection(connector.getUsername(), connector.getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(connector.getQueueName());
            MessageProducer producer = session.createProducer(destination);
            String message = createReadingMessage(reading, deviceId);
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
            System.out.println("Sent message:" + textMessage.getText() + " to queue: " + connector.getQueueName());
            producer.close();
            session.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String createReadingMessage(Double reading, String deviceId) {
        long oneHourInMillis = 1000 * 60 * 60;
        long tenMinutesInMillis = 1000 * 60 * 10;
        long timestamp = this.timestamp + 2 * oneHourInMillis + tenMinutesInMillis * counter++;
        return String.format("""
                {
                  "timestamp": %d,
                  "deviceId": %s,
                  "measurementValue": %f
                }""", timestamp, deviceId, reading);
    }
}
