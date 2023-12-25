package ro.tuc.ds2020.services.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.List;

@Component
public class QueueConnector {
    @Value("${mq.host}")
    private String host;

    @Value("${mq.port}")
    private int port;

    @Value("${mq.queueManager}")
    private String queueManager;

    @Value("${mq.queueNames}")
    private List<String> queueNames;

    @Value("${mq.username}")
    private String username;

    @Value("${mq.password}")
    private String password;

    private MQConnectionFactory mqConnectionFactory;

    private QueueConnector() {
    }

    @PostConstruct
    public void init() throws JMSException {
        mqConnectionFactory = new MQConnectionFactory();
        configureConnectionFactory();
    }

    private void configureConnectionFactory() throws JMSException {
        mqConnectionFactory.setHostName(host);
        mqConnectionFactory.setPort(port);
        mqConnectionFactory.setQueueManager(queueManager);
        mqConnectionFactory.setChannel("DEV.APP.SVRCONN");
        mqConnectionFactory.setTransportType(1);
    }

    public MQConnectionFactory getMqConnectionFactory() {
        return mqConnectionFactory;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getQueueNames() {
        return queueNames;
    }

    public String getPassword() {
        return password;
    }
}
