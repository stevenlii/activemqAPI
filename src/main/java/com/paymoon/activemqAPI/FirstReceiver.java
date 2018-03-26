package com.paymoon.activemqAPI;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class FirstReceiver {
    private static Logger LOGGER = Logger.getLogger(FirstReceiver.class);

    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = null;
        QueueConnection connection = null;
        QueueSession session = null;
        Queue queue = null;
        QueueReceiver receiver = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
            connection = connectionFactory.createQueueConnection();
            connection.start();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue("firstQueue");
            receiver = session.createReceiver(queue);
            receiver.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        TextMessage text = (TextMessage) message;
                        LOGGER.info(text.getText());
                    } catch (JMSException e) {
                        LOGGER.error("接收消息异常", e);
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("接收消息失败", e);
        }
    }
}