package com.mudra.assignment;

import com.mudra.LongRunningTask;
import com.mudra.LongRunningTask.TaskResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;
import java.util.stream.Collectors;


/**
 * This class showcases the use of StructuredTaskScope.ShutdownOnFailure.
 * The scope.join() method will wait till the first "unsuccessful" thread
 * forked from the scope to terminate and then send cancellation requests
 * for the rest.
 */
public class STaskScopeOnFailureExecutor {

    public static void main(String[] args) throws Exception {

        // Create the tasks
        var dbTask = new LongRunningTask("dataTask", 3, "row1", false);
        var restTask = new LongRunningTask("restTask", 10, "json2", false);
        var extTask = new LongRunningTask("extTask", 5, "json2", false);

        // execute the sub tasks in parallel
        Map<String, TaskResponse> result
                = STaskScopeOnFailureExecutor.execute(List.of(dbTask, extTask, restTask));

        // print results
        result.forEach((k, v) -> {
            System.out.printf("%s : %s\n", k, v);
        });

        // get individual response
        TaskResponse extResponse = result.get("extTask");
        System.out.println(extResponse);
    }

    /*
     * Run the collection of tasks in parallel, terminate them by the end of the
     * method and return result of all tasks
     */
    private static Map<String, TaskResponse> execute(Collection<LongRunningTask> tasks)
            throws InterruptedException, ExecutionException {

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // start running the tasks in parallel 
            List<Subtask<TaskResponse>> subTasks
                    = tasks.stream().map(scope::fork).toList();

            // wait till all are successful OR first failed task
            scope.join();

            // throw exception if at least one task failed; otherwise
            // do nothing
            scope.throwIfFailed();

            // Handle the returned values from successful tasks
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
