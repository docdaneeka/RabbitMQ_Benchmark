package main;
import receiver.Receiver;
import sender.Sender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adam on 2015-12-17.
 */
public class Main {

    public static Map<String, Long> timing = new ConcurrentHashMap<>();
    public static Long time = null;
    private static ExecutorService service = Executors.newCachedThreadPool();
    public static final int instancesCount = 2;


    public static final int messagesCount = 10000;
    private static final int size = 24;
    public static void main(String[] args) {

        Thread receiverThread = new Thread(new Receiver());
        receiverThread.start();
        String message = sender.Main.createDataSize(size);
        for(int i=0; i<instancesCount; i++){
            service.submit(new Sender(i,message));
        }
    }
}
