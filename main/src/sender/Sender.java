package sender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by alex on 03.12.15.
 */
public class Sender implements Runnable{

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
            Map args = new HashMap();
//            args.put("x-ha-policy", "all");
            channel.queueDeclare(Main.QUEUE_NAME, false, false, false, args);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startTime = System.nanoTime();
        if(main.Main.time==null) main.Main.time = System.nanoTime();
        for(int i=0;i<main.Main.messagesCount;i++) {
            try {
                main.Main.timing.put(message,System.nanoTime());
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

//        String report = "Thread no" + no + " " + main.Main.messagesCount + " messages sent " +
//                "after " + ((System.nanoTime() - startTime)/1000000000.0) + "s";
//        System.out.println(report);
//        Main.writeReport(report);
    }
}
