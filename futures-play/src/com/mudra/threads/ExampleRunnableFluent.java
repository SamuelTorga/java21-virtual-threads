package com.mudra.threads;

public class ExampleRunnableFluent {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        // start a daemon thread using Fluent API
        Runnable r = new SimpleRunnable();
        Thread thread = Thread.ofPlatform().name("Simple").daemon(true).start(r);
        
        thread.join();

        System.out.println("Ending Main Thread ..");

    }

}

