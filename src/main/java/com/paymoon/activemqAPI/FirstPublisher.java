
package com.paymoon.activemqAPI;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class FirstPublisher {

    private static Logger LOGGER = Logger.getLogger(FirstPublisher.class);
    private static final int PUBLISH_NUMBER = 5;

    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = null;
        TopicConnection connection = null;
        TopicSession session = null;
        Topic topic = null;
        TopicPublisher publisher = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
            connection = connectionFactory.createTopicConnection();
            connection.start();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("firstTopic");
            publisher = session.createPublisher(topic);
            for (int i = 1; i <= PUBLISH_NUMBER; i++) {
                publisher.send(session.createTextMessage("test，firstTopic!"));
                System.out.println("test，firstTopic!");
            }
        } catch (Exception e) {
            LOGGER.error("向firstTopic发布消息异常", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOGGER.error("connection异常", e);
                }
            }
        }
    }
}