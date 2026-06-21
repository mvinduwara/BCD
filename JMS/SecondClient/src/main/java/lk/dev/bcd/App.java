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

            TopicPublisher publisher = session.createPublisher(topic);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText("Hello, This is First Message");
            publisher.publish(textMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
