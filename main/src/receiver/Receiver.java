package receiver;

import com.rabbitmq.client.*;
import main.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receiver implements Runnable{

    private final static String QUEUE_NAME = "hello";
    static boolean isFirstReceived = false;
    static long startTime;
    static int count  = 0;
    private int no;

    public Receiver(int no){
        this.no = no;
    }

    private static void writeReport(String report){
        try {
            File file = new File("receiver_report.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(report + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                        String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body)
                        throws IOException {

                    String message = new String(body, "UTF-8");
                    Long time = System.nanoTime() - main.Main.timing.get(message);
                    //TODO wykorzystac to
                    if(!isFirstReceived){
                        isFirstReceived=true;
                        startTime = System.nanoTime();
                    }

//                    Main.totalMessageReveived++;
                    Main.increaseRcvdMsg();
                    Main.totalMessageReveivedPerRec[no]++;

//                    if(++count == Main.messagesCount*Main.senderInstancesCount) {
//                        String report = count + " messages received " +
//                                "after " + ((System.nanoTime() - Main.time) / 1000000000.0) + " s";
//                        System.out.println(report);
//                        writeReport(report);
//                    }

                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}