package com.mudra.threads;

import java.util.concurrent.TimeUnit;

public class ExampleMethodReference {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        Thread thr = new Thread(ExampleMethodReference::doSomething);
        thr.start();
        
        System.out.println("Ending Main Thread ..");

    }
    
    public static void doSomething() {

        System.out.println("Starting Simple Thread");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        System.out.println("Ending Simple Thread");
    }


}

