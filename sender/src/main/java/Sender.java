import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {

    private final static String QUEUE_NAME = "hello";
    private static int count = 0;
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        for(int i=0;i<1000;i++) {
            String message = "Message number : " + ++count;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }
        //System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
