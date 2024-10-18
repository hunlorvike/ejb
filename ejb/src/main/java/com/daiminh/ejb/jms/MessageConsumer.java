package com.daiminh.ejb.jms;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

@MessageDriven(
        name = "MessageConsumer",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ProductQueue")
        }
)
public class MessageConsumer implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {

                String userName = mapMessage.getString("UserName");
                String time = mapMessage.getString("Time");
                String description = mapMessage.getString("Description");

                System.out.println("Received message: UserName=" + userName + ", Time=" + time + ", Description=" + description);
            } catch (JMSException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to send JMS message", e);
            }
        }
    }
}
