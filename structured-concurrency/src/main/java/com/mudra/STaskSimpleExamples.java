package com.mudra;

import com.mudra.LongRunningTask.TaskResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

@Slf4j
public class STaskSimpleExamples {

    public static void main(String[] args) throws Exception {

        log.info("Main : Started");

        // Simulate interrupt to the Main Thread before Child threads complete
//        interruptMain();
//        exampleCompleteAllTasks();
//        exampleShutdownOnFailure();
        exampleShutdownOnSuccess();

        log.info("Main : Completed");

    }

    private static void exampleShutdownOnFailure()
            throws InterruptedException, ExecutionException {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var dataTask = new LongRunningTask("dataTask", 3, "row1", true);
            var restTask = new LongRunningTask("restTask", 10, "json2", false);

            // Start running the tasks in parallel 
            Subtask<TaskResponse> dataSubTask = scope.fork(dataTask);
            Subtask<TaskResponse> restSubTask = scope.fork(restTask);

            // Wait till first Child Task fails. Send cancellation to
            // all other Child Tasks
            scope.join();
            scope.throwIfFailed();

            // Handle Success Child Task Results
            log.info(String.valueOf(dataSubTask.get()));
            log.info(String.valueOf(restSubTask.get()));
        }

    }


    private static void exampleCompleteAllTasks() throws InterruptedException {

        try (var scope = new StructuredTaskScope<TaskResponse>()) {

            var expTask = new LongRunningTask("expedia-task", 3, "100$", true);
            var hotTask = new LongRunningTask("hotwire-task", 10, "110$", true);

            // Start running the tasks in parallel 
            Subtask<TaskResponse> expSubTask = scope.fork(expTask);
            Subtask<TaskResponse> hotSubTask = scope.fork(hotTask);

            // Code to simulate random exception being thrown
            // This should still terminate the child threads
            if (true) {
                Thread.sleep(Duration.ofSeconds(2));
                throw new RuntimeException("Some Exception");
            }

            // Wait for all tasks to complete (success or not)
            scope.join();

            // Handle Child Task Results (might have succeeded or failed)
            State expState = expSubTask.state();
            if (expState == State.SUCCESS)
                log.info(String.valueOf(expSubTask.get()));
            else if (expState == State.FAILED)
                log.info(String.valueOf(expSubTask.exception()));

            State hotState = hotSubTask.state();
            if (hotState == State.SUCCESS)
                log.info(String.valueOf(hotSubTask.get()));
            else if (hotState == State.FAILED)
                log.info(String.valueOf(hotSubTask.exception()));
        }

    }


    private static void exampleShutdownOnSuccess()
            throws InterruptedException, ExecutionException {

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<TaskResponse>()) {

            var wthr1Task = new LongRunningTask("Weather-1", 3, "32", false);
            var wthr2Task = new LongRunningTask("Weather-2", 10, "30", false);

            // Start running the tasks in parallel 
            Subtask<TaskResponse> subTask1 = scope.fork(wthr1Task);
            Subtask<TaskResponse> subTask2 = scope.fork(wthr2Task);

            // Wait till first Child Task Succeeds. Send Cancellation
            // to all other Child Tasks
            scope.join();

            // Handle Successful Child Task
            TaskResponse result = scope.result();
            log.info(String.valueOf(result));
        }

    }

    private static void interruptMain() {

        Thread mainThread = Thread.currentThread();
        Thread.ofPlatform().start(() -> {

            try {
                Thread.sleep(Duration.ofSeconds(2));
                mainThread.interrupt();
            } catch (InterruptedException e) {
                log.error("Interrupted Main Thread", e);
            }
        });
    }

}
