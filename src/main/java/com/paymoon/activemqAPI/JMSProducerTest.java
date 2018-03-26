package com.paymoon.activemqAPI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.log4j.Logger;

public class JMSProducerTest {

    private static Logger LOGGER = Logger.getLogger(JMSProducerTest.class);

    public static void main(String[] args) {
        String brokerURL = "tcp://localhost:61616";
        JMSProducer producer = new JMSProducer(brokerURL, "admin", "admin");
        for (int i = 0; i < 5; i++) {
            LOGGER.info(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now()));
            producer.send("firstQueue", UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }
}