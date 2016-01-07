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
    public static volatile Long time = System.nanoTime();
    private static ExecutorService service = Executors.newCachedThreadPool();
    public static final int instancesCount = 2;


    public static final int messagesCount = 20000;
    public static final int totalMessagesCount = instancesCount * messagesCount;
    public static volatile int totalMessageReveived = 0;
    private static final int size = 24;
    public static void main(String[] args) {

        Thread timer = new Thread(new Runnable(){

            @Override
            public void run() {
                int totalMessageReveivedTmp = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(totalMessageReveived - totalMessageReveivedTmp + " msgs/s");
                    totalMessageReveivedTmp = totalMessageReveived;
                    if ((System.nanoTime() - Main.time) / 1000000000.0 >= 20.0) {
                        System.out.println(totalMessageReveived + " msgs received");
                        System.out.println((totalMessageReveived / totalMessagesCount) * 100 + "%");
                        break;
                    }
                }
            }
        });
        timer.start();

        Thread receiverThread1 = new Thread(new Receiver());
        receiverThread1.start();
        Thread receiverThread2 = new Thread(new Receiver());
        receiverThread2.start();
        Thread receiverThread3 = new Thread(new Receiver());
        receiverThread3.start();

        String message = sender.Main.createDataSize(size);
        for(int i=0; i<instancesCount; i++){
            service.submit(new Sender(i,message));
        }

    }
}
