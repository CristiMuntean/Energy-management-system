package org.example;

import com.ibm.mq.jms.MQConnectionFactory;

import javax.jms.JMSException;

public class QueueConnector {
    private final String host;
    private final int port;
    private final String queueManager;
    private final String queueName;
    private final String username;
    private final String password;

    private MQConnectionFactory connectionFactory;

    public QueueConnector() throws JMSException {
        this.host = "localhost";
        this.port = 1414;
        this.queueManager = "Monitoring_Queue_Manager";
        this.queueName = "Device_Monitoring_Queue";
        this.username = "app";
        this.password = "passw0rd";
        connectionFactory = new MQConnectionFactory();
        configureConnectionFactory();
    }

    private void configureConnectionFactory() throws JMSException {
        connectionFactory.setHostName(host);
        connectionFactory.setPort(port);
        connectionFactory.setQueueManager(queueManager);
        connectionFactory.setChannel("DEV.APP.SVRCONN");
        connectionFactory.setTransportType(1);
    }

    public MQConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getQueueName() {
        return queueName;
    }
}
