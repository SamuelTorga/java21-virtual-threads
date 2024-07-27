package com.mudra;

import com.mudra.user.User;
import com.mudra.user.UserHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple example with a Single Thread. Demonstrates use of Thread Local
 * as an implicit parameter in whole method stack
 */
@Slf4j
public class ThreadLocalSimplePlay {

    public static ThreadLocal<User> user = new ThreadLocal<>();

    public static void main(String[] args) {
        
        print("User => " + user.get());

        // Main thread sets the user 
        user.set(new User("anonymous"));
        print("User => " + user.get());
        
        handleUser();
    }

    private static void handleUser() {
        
        UserHandler handler = new UserHandler();
        handler.handle();
    }

    public static void print(String m) {
        log.info("[{}] {}", Thread.currentThread().getName(), m);
    }

}

