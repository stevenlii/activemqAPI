package com.paymoon.activemqAPI;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class FirstSender {

	private static Logger LOGGER = Logger.getLogger(FirstSender.class);
	private static final int SEND_NUMBER = 5;

	public static void main(String[] args) {
		ActiveMQConnectionFactory connectionFactory = null;
		QueueConnection connection = null;
		QueueSession session = null;
		Queue queue = null;
		QueueSender sender = null;
		TextMessage message = null;
		try {
			connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
					ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
			connection = connectionFactory.createQueueConnection();
			connection.start();
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = session.createQueue("firstQueue");
			sender = session.createSender(queue);
			// 默认是持久化模式的
//			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			sender.setDeliveryMode(DeliveryMode.PERSISTENT);
			for (int i = 0; i < SEND_NUMBER; i++) {
				message = session.createTextMessage("你好吗!!");
				sender.send(message);
			}
		} catch (JMSException e) {
			LOGGER.error("向消息队列发送消息失败", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOGGER.error("connection关闭失败", e);
				}
			}
		}
	}
}