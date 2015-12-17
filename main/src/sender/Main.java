package sender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public final static String QUEUE_NAME = "hello";
    public static int count = 10000;
    private static final int threads = 4;
    private static ArrayList<Sender> threadList;

    public static void main(String[] args) {

        String message = createDataSize(1024);
        threadList = new ArrayList<Sender>();

        for(int i=0; i<threads; i++){
            Sender sender = new Sender(i, message);
            threadList.add(sender);
        }

//        for(Sender sender : threadList){
//            sender.start();
//        }
    }

    public static String createDataSize(int msgSize) {
        msgSize = msgSize/2;
        msgSize = msgSize * 1024;
        StringBuilder sb = new StringBuilder(msgSize);
        for (int i=0; i<msgSize; i++) {
            sb.append('a');
        }
        return sb.toString();
    }

    public static void writeReport(String report){
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
