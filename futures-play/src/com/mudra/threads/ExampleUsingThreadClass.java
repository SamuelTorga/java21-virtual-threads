package com.mudra.threads;

public class ExampleUsingThreadClass {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        // start a thread from a Thread
        Thread thread = new SimpleThread("Simple1", 2);
        thread.start();

        System.out.println("Ending Main Thread ..");

    }

}

