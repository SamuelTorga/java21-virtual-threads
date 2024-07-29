package com.mudra.threads;

import java.util.concurrent.TimeUnit;

class SimpleRunnable implements Runnable {

    @Override
    public void run() {

        System.out.println("Starting Simple Thread");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        System.out.println("Ending Simple Thread");
    }
}