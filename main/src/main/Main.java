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
    public static final int senderInstancesCount = 2;
    public static final int receiverInstancesCount = 8;

    public static final int messagesCount = 60000;
    public static final int totalMessagesCount = senderInstancesCount * messagesCount;
    public static volatile int totalMessageReveived = 0;
    public static volatile int[] totalMessageReveivedPerRec;
    private static final int size = 24;
    public static void main(String[] args) {

        totalMessageReveivedPerRec = new int[8];

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
                    if ((System.nanoTime() - Main.time) / 1000000000.0 >= 30.0) {
                        System.out.println(totalMessageReveived + " msgs received");
                        System.out.println((totalMessageReveived / totalMessagesCount) * 100 + "%");
                        for(int i=0; i< receiverInstancesCount; i++){
                            System.out.println("Rec no." + i + ": " + totalMessageReveivedPerRec[i] + " msgs received");
                        }
                        break;
                    }
                }
            }
        });
        timer.start();

        for(int i=0; i< receiverInstancesCount; i++){
            service.submit(new Receiver(i));
        }

        String message = sender.Main.createDataSize(size);
        for(int i=0; i< senderInstancesCount; i++){
            service.submit(new Sender(i,message));
        }

    }

    public static synchronized void increaseRcvdMsg(){
        totalMessageReveived++;
    }
}
