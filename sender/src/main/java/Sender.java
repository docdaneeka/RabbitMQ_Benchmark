import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {

    private final static String QUEUE_NAME = "hello";
    static long startTime;
    private static int count = 0;
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        count = 100000;
        String message = createDataSize(24);
        startTime = System.nanoTime();
        for(int i=0;i<count;i++) {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }
        String report = count + " messages sent after " + ((System.nanoTime() - startTime)/1000000000.0) + "s";
        System.out.println(report);
        writeReport(report);

        channel.close();
        connection.close();
    }

    private static String createDataSize(int msgSize) {
        msgSize = msgSize/2;
        msgSize = msgSize * 1024;
        StringBuilder sb = new StringBuilder(msgSize);
        for (int i=0; i<msgSize; i++) {
            sb.append('a');
        }
        return sb.toString();
    }

    private static void writeReport(String report){
        try {
            File file = new File("sender_report.txt");

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
