package com.mudra.assignment;

import com.mudra.LongRunningTask;
import com.mudra.LongRunningTask.TaskResponse;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;


/**
 * This class showcases the use of StructuredTaskScope.ShutdownOnSuccess.
 * The scope.join() method will wait till the first "successful" thread forked
 * from the scope terminates and send cancellation requests to the rest of the
 * threads
 */
public class STaskScopeOnSuccessExecutor {

    public static void main(String[] args) throws Exception {

        // Create the tasks
        var wthr1Task = new LongRunningTask("Weather-1", 3, "32F", false);
        var wthr2Task = new LongRunningTask("Weather-2", 10, "30F", true);

        // execute the sub tasks in parallel
        TaskResponse result = execute(List.of(wthr1Task, wthr2Task));

        // print results
        System.out.printf("%s : %s\n", result.name(), result);
    }

    /*
     * Run the collection of tasks in parallel, terminate them by the end of the
     * method and return first "successful" task result
     */
    private static TaskResponse execute(Collection<LongRunningTask> tasks)
            throws InterruptedException, ExecutionException {

        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<TaskResponse>()) {

            // start running the tasks in parallel 
            List<Subtask<TaskResponse>> subTasks
                    = tasks.stream().map(scope::fork).toList();

            // wait for first task to complete successfully
            scope.join();

            // get result of first task Or throw exception
            TaskResponse output = scope.result();

            // Successful result available
            return output;
        }

        // close() method on scope will ensure that all sub tasks 
        // are terminated.

    }

}
