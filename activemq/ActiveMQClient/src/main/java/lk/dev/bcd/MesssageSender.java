package lk.dev.bcd;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

public class MesssageSender {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

//        ConnectionFactory.setBrokerURL("tcp://localhost:61616");

        try {

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("news");

            MessageProducer producer = session.createProducer(topic);

            TextMessage textMessage = session.createTextMessage("Hello World");
            textMessage.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* * * * *");

            producer.send(textMessage);

            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
