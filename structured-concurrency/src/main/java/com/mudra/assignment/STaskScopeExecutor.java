package com.mudra.assignment;

import com.mudra.LongRunningTask;
import com.mudra.LongRunningTask.TaskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;
import java.util.stream.Collectors;

/**
 * This class showcases the use of StructuredTaskScope used in
 * default fashion. By default, the scope.join() method will
 * wait for all the threads forked from scope to terminate (
 * whether successful or not)
 */
@Slf4j
public class STaskScopeExecutor {

    public static void main(String[] args) throws InterruptedException {

        // Create the sub tasks
        var expTask = new LongRunningTask("expedia-task", 3, "100$", false);
        var hotTask = new LongRunningTask("hotwire-task", 10, "110$", false);

        // execute the sub tasks in parallel
        var result = execute(List.of(expTask, hotTask));

        // print results
        result.forEach((k, v) -> log.info("{} : {}}\n", k, v));
    }

    /*
     * Run the collection of tasks in parallel, terminate them by the end of the
     * method and return a consolidated result.
     */
    private static Map<String, TaskResponse> execute(Collection<LongRunningTask> tasks)
            throws InterruptedException {

        try (var scope = new StructuredTaskScope<TaskResponse>()) {

            // start running the tasks in parallel 
            List<Subtask<TaskResponse>> subTasks
                    = tasks.stream().map(scope::fork).toList();

            // wait for all tasks to complete (success or not)
            scope.join();

            // Handle the results returned by only "successful" tasks
            Map<String, TaskResponse> output
                    = subTasks.stream()
                    .filter(sub -> sub.state() == State.SUCCESS)
                    .map(Subtask::get)
                    .collect(Collectors.toMap(TaskResponse::name, r -> r));

            return output;
        }

        // close() method on scope will ensure that all sub tasks 
        // are terminated.

    }

}
