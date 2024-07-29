package com.mudra.threads;

public class ExampleRunnableSimple {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        // start a thread from Runnable
        Runnable runnable = new SimpleRunnable();
        Thread thread = new Thread(runnable);
        thread.setName("Simple");
        thread.start();

        thread.join();

        System.out.println("Ending Main Thread ..");

    }

}

