package com.mudra.threads;

import java.util.concurrent.TimeUnit;

public class ExampleLambda {
    
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting Main Thread ..");

        Thread.ofPlatform().start(() -> {
            
            System.out.println("Starting Simple Thread");

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }

            System.out.println("Ending Simple Thread");
        });
        
        System.out.println("Ending Main Thread ..");

    }

}

