package lk.dev.bcd;

import jakarta.jms.*;

import javax.naming.InitialContext;

public class App {
    public static void main(String[] args) {
        try {
            InitialContext context = new InitialContext();
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("myTopicConnectionFactory");

            TopicConnection connection = factory.createTopicConnection();   //Create Topic Connection
            connection.start();                                             // Start Connection

            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("myTopic");

                TopicSubscriber subscriber = session.createSubscriber(topic);

            Message message = subscriber.receive();
            System.out.println(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
