package lk.dev.bcd;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakartha.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "activeMQTopic"),
        @ActivationConfigProperty(propertyName = "destination",propertyValue = "activeMQTopic"),
        @ActivationConfigProperty(propertyName = "resourceAdapter   ",propertyValue = "activemq-rar-6.2.6")
})
public class MessageRecieverFromActiveMQ implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("Message Recieved:" + message.getBody(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
