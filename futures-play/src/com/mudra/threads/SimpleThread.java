package com.mudra.threads;

import java.util.concurrent.TimeUnit;

class SimpleThread extends Thread {

    private final int secs;

    SimpleThread(String name, int secs) {
        this.secs = secs;
        this.setName(name);
    }

    @Override
    public void run() {
        System.out.printf("%s : Starting Simple Thread\n", this.getName());

        try {
            TimeUnit.SECONDS.sleep(this.secs);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        System.out.printf("%s : Ending Simple Thread\n", this.getName());
    }
}
