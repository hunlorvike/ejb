package com.daiminh.ejb.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.MapMessage;
import jakarta.jms.Queue;

@Stateless
public class MessageProducer {

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/ProductQueue")
    private Queue productQueue;

    public void sendMessage(String userName, String description) {
        MapMessage mapMessage = context.createMapMessage();
        try {
            mapMessage.setString("UserName", userName);
            mapMessage.setString("Time", String.valueOf(System.currentTimeMillis()));
            mapMessage.setString("Description", description);

            context.createProducer().send(productQueue, mapMessage);
            System.out.println("Sent message: " + mapMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
