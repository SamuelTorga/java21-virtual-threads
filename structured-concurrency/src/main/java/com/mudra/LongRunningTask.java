package com.mudra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Simulates a Task which can be used for testing. It responds
 * to interrupts and cleanly terminates on interruption or failure.
 * The caller can give a name to the task, specify how much time
 * it should take to run and also specify if it should fail eventually.
 */
@Slf4j
@RequiredArgsConstructor
public class LongRunningTask implements Callable<LongRunningTask.TaskResponse> {

    private final String name;
    private final int time;
    private final String output;
    private final boolean fail;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        log.info("> Main : Started");
        LongRunningTask task = new LongRunningTask("LongTask1", 10, "json-response1", false);

        try (ExecutorService service = Executors.newFixedThreadPool(2)) {
            Future<TaskResponse> taskFuture = service.submit(task);

            // do something for 5 secs and cancel task1
            Thread.sleep(Duration.ofSeconds(5));
            taskFuture.cancel(true);
        }

        log.info("> Main : Completed");
    }

    /*
     * Body of the task which will be run on a separate Thread (mostly
     * Virtual Thread)
     */
    @Override
    public TaskResponse call() throws InterruptedException {

        print("Started");
        long start = System.currentTimeMillis();

        int numSecs = 0;
        while (numSecs++ < time) {

            if (Thread.interrupted()) {
                throwInterruptedException();
            }

            print("Working .." + numSecs);

            // process data (Code not shown) which uses CPU for 0.2 secs

            try {
                Thread.sleep(Duration.ofSeconds(1));
                // new HttpCaller(name).makeCall(1);
            } catch (InterruptedException intExp) {
                throwInterruptedException();
            }

            // process data (Code not shown) which uses CPU for 0.2 secs 
        }

        /* simulate failure of task */
        if (fail) {
            throwExceptionOnFailure();
        }

        print("Completed");
        long end = System.currentTimeMillis();
        return new TaskResponse(this.name, this.output, end - start);

    }

    private void throwExceptionOnFailure() {
        print("Failed");
        throw new RuntimeException(name + " : Failed");
    }

    private void throwInterruptedException() throws InterruptedException {

        print("Interrupted");
        throw new InterruptedException(name + " : Interrupted");
    }

    private void print(String message) {
        log.info("> {} : {}", name, message);
    }

    // Represents successful response of the Task
    public record TaskResponse(String name, String response, long timeTaken) {

    }

}
