package ro.tuc.ds2020.services.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class QueueConsumer {
    private static Logger LOGGER = LoggerFactory.getLogger(QueueConsumer.class);

    private QueueConnector connector;

    public QueueConsumer(QueueConnector connector) {
        this.connector = connector;
    }

    public String readMessageFromQueue(String queueName) {
        String readMessage;
        try {
            Connection connection = connector.getMqConnectionFactory()
                    .createConnection(connector.getUsername(), connector.getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);
            connection.start();
            javax.jms.Message message = consumer.receive();
            LOGGER.info("Received message:" + ((javax.jms.TextMessage) message).getText() + " from queue: " + queueName);
            readMessage = ((javax.jms.TextMessage) message).getText();
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            readMessage = "Error reading message from queue." + e.getMessage();
        }
        return readMessage;
    }
}
