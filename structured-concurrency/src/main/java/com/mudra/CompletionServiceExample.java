package com.mudra;

import com.mudra.LongRunningTask.TaskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * This example showcases a mechanism to handle parallel tasks
 * using CompletionService. This class is equivalent
 * to the usage of STaskScopeOnFailureExample (all tasks should
 * be successful)
 */
@Slf4j
public class CompletionServiceExample {

    public static void main(String[] args) throws Exception {

        // Create the tasks
        var tasks = List.of(
                new LongRunningTask("dataTask", 3, "row1", true),
                new LongRunningTask("restTask", 10, "json2", false)
        );

        List<TaskResponse> result = doParallelWork(tasks);
        log.info("Parallel Work output = {}", result);
    }

    private static List<TaskResponse> doParallelWork(Collection<LongRunningTask> tasks) throws Exception {

        try (var service = Executors.newVirtualThreadPerTaskExecutor()) {

            CompletionService<TaskResponse> ecs
                    = new ExecutorCompletionService<TaskResponse>(service);

            List<Future<TaskResponse>> taskFutures
                    = tasks.stream().map(t -> ecs.submit(t)).toList();

            try {
                for (int j = 0; j < taskFutures.size(); j++) {
                    ecs.take().get();
                }
            } catch (Exception e) {

                // request that the threads terminate. 
                // do not wait for the threads to terminate
                for (var taskFuture : taskFutures) {
                    taskFuture.cancel(true);
                }

                throw e;
            }

            // All tasks are successful at this point
            List<TaskResponse> result
                    = taskFutures.stream().map(Future::resultNow).toList();

            log.info(result.toString());
            return result;

        } // makes sure that all threads are fully terminated

    }

}
