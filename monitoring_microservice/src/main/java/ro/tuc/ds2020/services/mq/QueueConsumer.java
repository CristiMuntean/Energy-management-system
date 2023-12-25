package ro.tuc.ds2020.services.mq;

import com.ibm.jms.JMSTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.tuc.ds2020.services.MQService;

import javax.jms.*;

public class QueueConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConsumer.class);
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
            Message message = consumer.receive();

            LOGGER.info("Received message:" + ((JMSTextMessage) message).getText() + " from queue: " + queueName);

            readMessage = ((JMSTextMessage) message).getText();
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
