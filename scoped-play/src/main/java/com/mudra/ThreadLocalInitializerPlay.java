package com.mudra;

import com.mudra.user.User;
import lombok.extern.slf4j.Slf4j;

/*
 * Thread locals can be created with an initializer
 *  - Initial value would be determined by running the supplier
 *  - Supplier is called when calling get()
 *  - After remove() if you call get() => supplier will be called
 *
 */
@Slf4j
public class ThreadLocalInitializerPlay {

    public static final ThreadLocal<User> user = ThreadLocal.withInitial(() -> new User("anonymous"));

    public static void main(String[] args) throws InterruptedException {

        print("User => " + user.get());

        // Main thread sets the user 
        user.set(new User("main"));

        print("Modified User => " + user.get());

        // Start a Child Thread for "bob"
        Thread thread = Thread.ofPlatform().start(() -> {
            Thread.currentThread().setName("bob-thread");

            print("User => " + user.get());

            user.set(new User("bob"));
            print("Modified User => " + user.get());

        });

        thread.join();

        print("User => " + user.get());

    }

    private static void print(String m) {
        log.info("[{}] {}", Thread.currentThread().getName(), m);
    }
}
