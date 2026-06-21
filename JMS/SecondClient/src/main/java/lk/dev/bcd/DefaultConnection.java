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

            MessageProducer producer = session.createProducer(topic);

            for (int i = 1; i <= 10; i++) {
                TextMessage message = session.createTextMessage();
                message.setText("Hello World "+i);
                producer.send(message);
            }
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
