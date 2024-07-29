package com.mudra.compfutures;

import com.mudra.futures.FuturesPlay;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * CompletableFuture started on a different Pool (mypool) altogether.
 * Its a good idea to use a different Thread pool if the tasks are
 * IO bound. The Fork Join Pool must be used only CPU intensive tasks.
 *
 */
@Slf4j
public class CompletableFutureWithThreadPool {

    // Usually the Threadpool would be created upfront
    private static final ExecutorService mypool = Executors.newCachedThreadPool();

    public static void main(String[] args) {

        try {
            examplePipelineApplyAcceptAsyncWithExecutor().join();
        } finally {
            mypool.close();
        }

    }


    private static CompletableFuture<Void> examplePipelineApplyAcceptAsyncWithExecutor() {

        // Execute a task in common pool
        // then Apply a function on another executor service thread
        // then Accept the result on another executor service thread
        return CompletableFuture.supplyAsync(() -> FuturesPlay.doTask("SomeTask", 3, false), mypool)
                .thenApplyAsync(taskResult -> {
                    log.info("Apply Thread Name : " + Thread.currentThread());
                    return taskResult.secs();
                }, mypool)
                .thenAcceptAsync(time -> {
                    log.info("Accept Thread Name : {}", Thread.currentThread());
                    log.info(String.valueOf(time));
                }, mypool);
    }


}
