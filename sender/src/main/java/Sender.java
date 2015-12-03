import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by alex on 03.12.15.
 */
public class Sender extends Thread{

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    private long startTime;
    private String message;
    private int no;

    public Sender(int no, String message){
        this.no = no;
        this.message = message;
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(Main.QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startTime = System.nanoTime();
        for(int i=0;i<Main.count;i++) {
            try {
                channel.basicPublish("", Main.QUEUE_NAME, null, message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        String report = "Thread no" + no + " " + Main.count + " messages sent after " + ((System.nanoTime() - startTime)/1000000000.0) + "s";
        System.out.println(report);
        Main.writeReport(report);
    }
}
