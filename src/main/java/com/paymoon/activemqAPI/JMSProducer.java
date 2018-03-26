package com.paymoon.activemqAPI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;

public class JMSProducer {
    Logger LOGGER = Logger.getLogger(JMSProducer.class);

    // 设置连接的最大连接数
    public final static int DEFAULT_MAX_CONNECTIONS = 2;
    private int maxConnections;

    // 线程池数量
    private int threadPoolSize;
    public final static int DEFAULT_THREAD_POOL_SIZE = 4;

    // 强制使用异步返回数据的格式
    private boolean useAsyncSend;
    public final static boolean DEFAULT_USE_ASYNC_SEND_FOR_JMS = true;

    // 连接地址
    private String brokerUrl;

    private String userName;

    private String password;

    private ExecutorService threadPool;

    private PooledConnectionFactory connectionFactory;

    public JMSProducer(String brokerUrl, String userName, String password) {
        this(brokerUrl, userName, password, DEFAULT_MAX_CONNECTIONS, DEFAULT_THREAD_POOL_SIZE,
                DEFAULT_USE_ASYNC_SEND_FOR_JMS);
    }

    public JMSProducer(String brokerUrl, String userName, String password, int maxConnections, int threadPoolSize,
            boolean useAsyncSendForJMS) {
        this.useAsyncSend = useAsyncSendForJMS;
        this.brokerUrl = brokerUrl;
        this.userName = userName;
        this.password = password;
        this.maxConnections = maxConnections;
        this.threadPoolSize = threadPoolSize;
        init();
    }

    private void init() {
        // 设置JAVA线程池
        this.threadPool = Executors.newFixedThreadPool(this.threadPoolSize);
        // ActiveMQ的连接工厂
        ActiveMQConnectionFactory actualConnectionFactory = new ActiveMQConnectionFactory(this.userName, this.password,
                this.brokerUrl);
        actualConnectionFactory.setUseAsyncSend(this.useAsyncSend);

        this.connectionFactory = new PooledConnectionFactory(actualConnectionFactory);// org.apache.activemq.pool.PooledConnectionFactory
        // 父类org.apache.activemq.jms.pool.PooledConnectionFactory的setter方法
        this.connectionFactory.setMaxConnections(this.maxConnections);
    }

    public void send(final String queue, final String text) {
        // 直接使用线程池来执行具体的调用
        this.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    LOGGER.info(Thread.currentThread().getName());
                    sendMsg(queue, text);
                } catch (Exception e) {
                    LOGGER.error("发送失败", e);
                }
            }
        });
    }

    private void sendMsg(String queue, String text) throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            // 从连接池工厂中获取一个连接
            connection = this.connectionFactory.createConnection();
            LOGGER.info("connection: " + connection);
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);
            MessageProducer producer = session.createProducer(destination);
            Message message = getMessage(session, text);
            producer.send(message);
        } finally {
            closeSession(session);
            closeConnection(connection);
        }
    }

    private Message getMessage(Session session, String text) throws JMSException {
        return session.createTextMessage(text);
    }

    private void closeSession(Session session) {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}