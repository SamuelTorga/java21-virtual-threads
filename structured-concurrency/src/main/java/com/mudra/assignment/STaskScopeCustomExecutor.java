package com.mudra.assignment;

import com.mudra.LongRunningTask;
import com.mudra.LongRunningTask.TaskResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;


/**
 * This class showcases the use of Custom implementation of StructuredTaskScope.
 * The scope.join() method will wait till the first two "successful" thread forked
 * from the scope terminates and send cancellation requests to the rest of the
 * threads
 */
@Slf4j
public class STaskScopeCustomExecutor {

    public static void main(String[] args) throws Exception {

        // Create the tasks
        var w1Task = new LongRunningTask("Weather-1", 3, "30", true);
        var w2Task = new LongRunningTask("Weather-2", 4, "32", false);
        var w3Task = new LongRunningTask("Weather-3", 5, "34", false);
        var w4Task = new LongRunningTask("Weather-4", 6, "34", true);
        var w5Task = new LongRunningTask("Weather-5", 9, "30", true);

        TaskResponse result
                = execute(List.of(w1Task, w2Task, w3Task, w4Task, w5Task));
        log.info("Parallel Work output = {}", result);
    }

    /*
     * Run the collection of tasks in parallel, terminate them by the end of the
     * method and return Average of first two "successful" task results
     */
    private static TaskResponse execute(Collection<LongRunningTask> tasks) throws Exception {

        try (var scope = new AverageWeatherTaskScope()) {

            // start running the tasks in parallel 
            List<Subtask<TaskResponse>> subTasks
                    = tasks.stream().map(scope::fork).toList();

            // wait for first 2 tasks to complete
            scope.join();

            // get Average response
            TaskResponse output = scope.response();

            // Successful result available
            log.info("Output = {}", output);
            return output;
        }

        // close() method on scope will ensure that all sub tasks 
        // are terminated.

    }

}

class AverageWeatherTaskScope extends StructuredTaskScope<TaskResponse> {

    private final List<Subtask<? extends TaskResponse>> responses
            = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void handleComplete(Subtask<? extends TaskResponse> subtask) {
        if (subtask.state() == Subtask.State.SUCCESS) {
            add(subtask);
        }
    }

    private synchronized void add(Subtask<? extends TaskResponse> subtask) {

        int numOfResponses = 0;

        synchronized (responses) {
            responses.add(subtask);
            numOfResponses = responses.size();
        }

        if (numOfResponses == 2) {
            this.shutdown();
        }

    }

    @Override
    public AverageWeatherTaskScope join() throws InterruptedException {
        super.join();
        return this;
    }

    public TaskResponse response() {

        super.ensureOwnerAndJoined();

        if (responses.size() != 2) {
            throw new RuntimeException("At least two subtasks must be successful");
        }

        TaskResponse r1 = responses.get(0).get();
        TaskResponse r2 = responses.get(1).get();

        long avgTime = (r1.timeTaken() + r2.timeTaken()) / 2;
        Integer temp1 = Integer.valueOf(r1.response());
        Integer temp2 = Integer.valueOf(r2.response());
        return new TaskResponse("Weather", "" + (temp1 + temp2) / 2, avgTime);
    }

}
