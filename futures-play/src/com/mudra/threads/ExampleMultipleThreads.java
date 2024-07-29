package com.mudra.threads;

public class ExampleMultipleThreads {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        // start a thread from a Thread
        Thread thread1 = new SimpleThread("Simple1", 2);
        thread1.start();

        // start another thread from a Thread
        Thread thread2 = new SimpleThread("Simple2", 3);
        thread2.start();

        // Wait for both threads to complete before proceeding
        thread1.join();
        thread2.join();
        
        System.out.println("Ending Main Thread ..");

    }

}

