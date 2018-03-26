package com.paymoon.activemqAPI;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class FirstSubscriber {
    private static Logger LOGGER = Logger.getLogger(FirstSubscriber.class);

    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = null;
        TopicConnection connection = null;
        TopicSession session = null;
        Topic topic = null;
        TopicSubscriber subscriber = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
            connection = connectionFactory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("firstTopic");
            subscriber = session.createSubscriber(topic);
            subscriber.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        Thread.sleep(1000);
                        if (message instanceof TextMessage) {
                            TextMessage text = (TextMessage) message;
                            LOGGER.info(text.getJMSMessageID() + ": " + text.getText());
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error("线程休眠异常", e);
                    } catch (JMSException e) {
                        LOGGER.error("firstSubscriber消費消息异常", e);
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("firstSubscriber异常", e);
        }
    }
}