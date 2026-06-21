package lk.dev.bcd;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "myTopic")
})
public class MessageReciever implements MessageListener {
    @PostConstruct
    public void init() {
        System.out.println("MessageReciever init............");
    }

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("MessageReciever onMessage.......... : " + message.getBody(String.class));
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
