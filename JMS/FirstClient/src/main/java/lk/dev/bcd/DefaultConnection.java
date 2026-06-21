package lk.dev.bcd;

import jakarta.jms.*;

import javax.naming.InitialContext;

public class DefaultConnection {
    public static void main(String[] args) {
        try {

            InitialContext context = new InitialContext();
            ConnectionFactory factory =
                    (ConnectionFactory) context.lookup("jms/__defaultConnectionFactory");

            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = (Topic) context.lookup("myTopic");

            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(message -> {
                try {
                    System.out.println(message.getBody(String.class));
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });

            while (true){}
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
