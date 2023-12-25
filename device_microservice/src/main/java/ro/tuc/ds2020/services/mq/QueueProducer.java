package ro.tuc.ds2020.services.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class QueueProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueProducer.class);
    private QueueConnector connector;

    public QueueProducer(QueueConnector connector) {
        this.connector = connector;
    }

    public boolean sendMessageToQueue(String message, String queueName) {
        try {
            Connection connection = connector.getMqConnectionFactory()
                    .createConnection(connector.getUsername(), connector.getPassword());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
            LOGGER.info("Sent message:" + textMessage.getText() + " to queue: " + queueName);
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
