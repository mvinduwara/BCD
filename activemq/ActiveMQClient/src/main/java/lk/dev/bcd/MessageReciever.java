package lk.dev.bcd;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jmx.JmxException;

public class MessageReciever {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

//        ConnectionFactory.setBrokerURL("tcp://localhost:61616");

        try {

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("news");

            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("Recived Message:" + message.getBody(String.class));
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            while (true) {}
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
