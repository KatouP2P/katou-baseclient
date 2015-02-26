package ga.nurupeaches.katou.cli;

import java.util.Scanner;

public class CLI {

    // CLUI = Command Line User Interaface
    private static ThreadGroup threads = new ThreadGroup("KatouP2P-CLUI-Thread");

    public static void initialize() {
        Thread readingThread = new Thread(threads, new InternalCLIRead(), "KatouCLUI-Read");


    }

    private static void startThreads(Thread... threads){
        for(Thread t : threads){
            t.start();
        }
    }

    private static class InternalCLIRead implements Runnable {

        @Override
        public void run(){
            Scanner scanner = new Scanner(System.in, "UTF-8");
            String line = scanner.nextLine();

        }

    }

}