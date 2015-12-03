import com.rabbitmq.client.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Receiver {

    private final static String QUEUE_NAME = "hello";
    static boolean isFirstReceived = false;
    static long startTime;
    static int count  = 0;
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                if(!isFirstReceived){
                    isFirstReceived=true;
                    startTime = System.nanoTime();
                }
                if(++count % 10000 == 0) {
                    String report = count + " messages received after " + ((System.nanoTime() - startTime) / 1000000000.0) + " s";
                    System.out.println(report);
                    writeReport(report);
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
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
}